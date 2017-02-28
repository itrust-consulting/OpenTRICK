/**
 * 
 */
package lu.itrust.business.TS.database.migration;

import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.migration.helper.AssessmentMapper;
import lu.itrust.business.TS.database.migration.helper.ExtendedParameterMapper;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.helper.Bounds;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.TS.model.parameter.value.AbstractValue;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.parameter.value.impl.LevelValue;
import lu.itrust.business.TS.model.parameter.value.impl.RealValue;
import lu.itrust.business.TS.model.scale.ScaleType;

/**
 * @author eomar
 *
 */
public class V2_2_1__MigrateData implements SpringJdbcMigration {

	private static final String QUERY_ASSESSMENT_SQL = "SELECT `idAssessment`, `dtALE`, `dtALEO`, `dtALEP`,`dtImpactFin`, `dtImpactLeg`, `dtImpactOp`, `dtImpactReal`, `dtImpactRep`, `dtLikelihood`, `dtLikelihoodReal`, `dtUncertainty` FROM `Assessment` WHERE `fiAnalysis` = ?";

	private Map<String, ScaleType> scaleTypes = Collections.emptyMap();

	private Map<Integer, AnalysisType> analyses = new LinkedHashMap<>();

	/**
	 * 
	 */
	public V2_2_1__MigrateData() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.flywaydb.core.api.migration.spring.SpringJdbcMigration#migrate(org.
	 * springframework.jdbc.core.JdbcTemplate)
	 */
	@Override
	public void migrate(JdbcTemplate arg0) throws Exception {
		loadScaleTypes(arg0);
		loadAnalysis(arg0);
		getAnalyses().forEach((id, type) -> migrateExtended(arg0, id, type));
	}

	private void loadAnalysis(JdbcTemplate template) {
		setAnalyses(new LinkedHashMap<>());
		template.query("Select `idAnalysis`, `dtType` as `type` From Analysis", (row) -> {
			getAnalyses().put(row.getInt("idAnalysis"), AnalysisType.valueOf(row.getString("type")));
		});
	}

	private void loadScaleTypes(JdbcTemplate template) {
		setScaleTypes(template
				.query("Select `id`, `dtName` as `name`, `dtAcronym` as `acronym` From ScaleType where `dtName` in (?,?,?,?,?)",
						new Object[] { "IMPACT", "FINANCIAL", "LEGAL", "OPERATIONAL", "REPUTATIONAL" }, new BeanPropertyRowMapper<>(ScaleType.class))
				.stream().collect(Collectors.toMap(ScaleType::getName, Function.identity())));
	}

	private void migrateExtended(JdbcTemplate template, Integer idAnalysis, AnalysisType analysisType) {
		Map<Integer, ImpactParameter> impactParameters = extendParameterLoader(template, idAnalysis, Constant.PARAMETERTYPE_TYPE_IMPACT)
				.collect(Collectors.toMap(ExtendedParameterMapper::getId, parameter -> createImpactParameter(parameter)));
		Map<Integer, LikelihoodParameter> likelihoodParameters = extendParameterLoader(template, idAnalysis, Constant.PARAMETERTYPE_TYPE_PROPABILITY)
				.collect(Collectors.toMap(ExtendedParameterMapper::getId, parameter -> createLikelihoodParameter(parameter)));
		Map<String, Map<String, IBoundedParameter>> paramters = new LinkedHashMap<>(likelihoodParameters.size() * (analysisType == AnalysisType.QUALITATIVE ? 2 : 5));
		Map<String, IBoundedParameter> likelihoods = new LinkedHashMap<>(likelihoodParameters.size());
		likelihoodParameters.values().forEach(likelihoodParameter -> {
			likelihoods.put(likelihoodParameter.getAcronym(), likelihoodParameter);
			likelihoodParameter.setAcronym("p" + likelihoodParameter.getLevel());
		});
		paramters.put(Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD, likelihoods);
		saveLikelihoodParameter(template, idAnalysis, likelihoodParameters);
		if (analysisType == AnalysisType.QUANTITATIVE) {
			Map<String, IBoundedParameter> impacts = new LinkedHashMap<>(impactParameters.size());
			impactParameters.values().forEach(impact -> {
				impacts.put(impact.getAcronym(), impact);
				impact.setAcronym(impact.getType().getAcronym() + impact.getLevel());
			});
			paramters.put(Constant.PARAMETER_CATEGORY_IMPACT, impacts);
			saveImpactParameter(template, idAnalysis, impacts.values());
			updateAssessments(template, idAnalysis, paramters);
		} else {
			scaleTypes.remove(Constant.PARAMETER_CATEGORY_IMPACT);
			impactParameters.values().forEach(impact -> {
				scaleTypes.values().forEach(scaleType -> {
					Map<String, IBoundedParameter> impacts = paramters.get(scaleType.getName());
					if (impacts == null)
						paramters.put(scaleType.getName(), impacts = new LinkedHashMap<>(impactParameters.size()));
					ImpactParameter impactParameter = impact.duplicate();
					impactParameter.setId(0);
					impactParameter.setAcronym(scaleType.getAcronym() + impactParameter.getLevel());
					impactParameter.setType(scaleType);
					impacts.put(impact.getAcronym(), impactParameter);
				});
			});
			scaleTypes.forEach((name, scaleType) -> saveImpactParameter(template, idAnalysis, paramters.get(name).values()));
			updateAssessmentAndRiskProfiles(template, idAnalysis, paramters);

		}
	}

	private void updateAssessmentAndRiskProfiles(JdbcTemplate template, Integer idAnalysis, Map<String, Map<String, IBoundedParameter>> paramters) {
		ValueFactory valueFactory = new ValueFactory();
		paramters.values().forEach(collection -> valueFactory.add(collection.values()));
		List<AssessmentMapper> assessmentMappers = loadAssessment(idAnalysis, template);
		Map<String, IBoundedParameter> likelihoods = paramters.get(Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD),
				financials = paramters.get(Constant.DEFAULT_IMPACT_TYPE_NAMES[0]), legals = paramters.get(Constant.DEFAULT_IMPACT_TYPE_NAMES[1]),
				operationals = paramters.get(Constant.DEFAULT_IMPACT_TYPE_NAMES[2]), reputationals = paramters.get(Constant.DEFAULT_IMPACT_TYPE_NAMES[3]);
		
		for (AssessmentMapper assessmentMapper : assessmentMappers) {
			
			IValue likelihood = findValue(assessmentMapper.getLikelihood(), likelihoods, Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME).

		}

	}

	private void updateAssessments(JdbcTemplate template, Integer idAnalysis, Map<String, Map<String, IBoundedParameter>> paramters) {
		ValueFactory valueFactory = new ValueFactory();
		paramters.values().forEach(collection -> valueFactory.add(collection.values()));
		List<AssessmentMapper> assessmentMappers = loadAssessment(idAnalysis, template);
		Map<String, IBoundedParameter> likelihoods = paramters.get(Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD), impacts = paramters.get(Constant.PARAMETER_CATEGORY_IMPACT);
		for (AssessmentMapper assessmentMapper : assessmentMappers) {
			IBoundedParameter likelihood = likelihoods.get(assessmentMapper.getLikelihood()), impact = impacts.get(assessmentMapper.getFinancial());
			IValue likelihoodValue = likelihood == null ? valueFactory.findProb(assessmentMapper.getLikelihood()) : new RealValue(likelihood.getValue(), likelihood),
					impactValue = impact == null ? valueFactory.findValue(assessmentMapper.getFinancial(), Constant.PARAMETER_CATEGORY_IMPACT)
							: new RealValue(impact.getValue(), impact);
			if (likelihoodValue == null)
				likelihoodValue = valueFactory.findProb(0.0);
			if (impactValue == null)
				impactValue = valueFactory.findValue(0.0, Constant.PARAMETER_CATEGORY_IMPACT);
			assessmentMapper.setAle(likelihoodValue.getReal() * impactValue.getReal());
			assessmentMapper.setAlep(assessmentMapper.getAle() * assessmentMapper.getUncertainty());
			assessmentMapper.setAleo(assessmentMapper.getAle() / assessmentMapper.getUncertainty());
			assessmentMapper.setRealImpact(impactValue.getReal());
			assessmentMapper.setLikelihoodReal(likelihoodValue.getReal());
			updateAssessment(template, assessmentMapper, idAnalysis);
			saveImpactValue(template, assessmentMapper.getId(), impactValue);
		}
		deleteAllRiskProfileFromAnalysis(template, idAnalysis);
	}

	private void deleteAllRiskProfileFromAnalysis(JdbcTemplate template, Integer idAnalysis) {
		template.update("DELETE FROM `RiskProfile` WHERE `fiAnalysis` = ?", idAnalysis);
	}

	private void saveImpactValue(JdbcTemplate template, int idAssessment, IValue value) {
		SimpleJdbcInsert simpleJdbcInsert = null;
		Map<String, Object> parameters = new HashMap<>();
		String type = null;
		if (value instanceof LevelValue) {
			simpleJdbcInsert = new SimpleJdbcInsert(template.getDataSource()).withTableName("LevelValue").usingGeneratedKeyColumns("idLevelValue");
			parameters.put("dtLevel", value.getLevel());
			type = "LEVEL";
		} else if (value instanceof RealValue) {
			simpleJdbcInsert = new SimpleJdbcInsert(template.getDataSource()).withTableName("RealValue").usingGeneratedKeyColumns("idRealValue");
			parameters.put("dtValue", value.getReal());
			type = "REAL";
		} else {
			simpleJdbcInsert = new SimpleJdbcInsert(template.getDataSource()).withTableName("Value").usingGeneratedKeyColumns("idValue");
			type = "VALUE";
		}
		parameters.put("dtParameterType", Constant.PARAMETER_CATEGORY_IMPACT);
		parameters.put("fiParameter", value.getParameter().getId());
		((AbstractValue) value).setId(simpleJdbcInsert.executeAndReturnKey(parameters).intValue());
		template.update("INSERT INTO `AssessmentImpacts`(`fiAssessment`, `dtValueType`, `fiValue`) VALUES (?, ? ,?)", idAssessment, type, ((AbstractValue) value).getId());
	}

	private void updateAssessment(JdbcTemplate template, AssessmentMapper assessmentMapper, Integer idAnalysis) {
		template.update("UPDATE `Assessment` SET `dtALE`= ? ,`dtALEO`= ?,`dtALEP`= ? ,`dtLikelihoodReal` = ?, `dtLikelihood` = ?, `dtImpactReal` = ? WHERE `idAssessment` = ?",
				assessmentMapper.getAle(), assessmentMapper.getAleo(), assessmentMapper.getAlep(), assessmentMapper.getLikelihoodReal(), assessmentMapper.getLikelihood(),
				assessmentMapper.getRealImpact(), assessmentMapper.getId());
	}

	private List<AssessmentMapper> loadAssessment(Integer idAnalysis, JdbcTemplate template) {
		List<AssessmentMapper> assessmentMappers = new LinkedList<>();
		template.query(V2_2_1__MigrateData.QUERY_ASSESSMENT_SQL, new Object[] { idAnalysis }, (row) -> {
			AssessmentMapper mapper = new AssessmentMapper();
			mapper.setId(row.getInt("idAssessment"));
			mapper.setAle(row.getDouble("dtALE"));
			mapper.setAleo(row.getDouble("dtALEO"));
			mapper.setAlep(row.getDouble("dtALEP"));
			mapper.setFinancial(row.getString("dtImpactFin"));
			mapper.setLegal(row.getString("dtImpactLeg"));
			mapper.setOperational(row.getString("dtImpactOp"));
			mapper.setReputational(row.getString("dtImpactRep"));
			mapper.setRealImpact(row.getDouble("dtImpactReal"));
			mapper.setLikelihood(row.getString("dtLikelihood"));
			mapper.setLikelihoodReal(row.getDouble("dtLikelihoodReal"));
			mapper.setUncertainty(row.getDouble("dtUncertainty"));
			assessmentMappers.add(mapper);
		});
		return assessmentMappers;
	}

	private void saveImpactParameter(JdbcTemplate template, Integer idAnalysis, Collection<IBoundedParameter> impactParameters) {
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(template.getDataSource()).withTableName("ImpactParameter").usingGeneratedKeyColumns("idImpactParameter");
		impactParameters.stream().filter(parameter -> parameter instanceof ImpactParameter).forEach(parameter -> {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("dtDescription", parameter.getDescription() == null ? "" : parameter.getDescription());
			parameters.put("dtValue", parameter.getValue());
			parameters.put("dtAcronym", parameter.getAcronym());
			parameters.put("dtFrom", parameter.getBounds().getFrom());
			parameters.put("dtTo", parameter.getBounds().getTo());
			parameters.put("dtLabel", parameter.getLabel() == null ? "" : parameter.getLabel());
			parameters.put("dtLevel", parameter.getLevel());
			parameters.put("fiParameterType", ((ImpactParameter) parameter).getType().getId());
			parameters.put("fiAnalysis", idAnalysis);
			((ImpactParameter) parameter).setId(simpleJdbcInsert.executeAndReturnKey(parameters).intValue());
		});
	}

	private void saveLikelihoodParameter(JdbcTemplate template, Integer idAnalysis, Map<Integer, LikelihoodParameter> likelihoodParameters) {
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(template.getDataSource()).withTableName("LikelihoodParameter").usingGeneratedKeyColumns("idLikelihoodParameter");
		likelihoodParameters.forEach((id, parameter) -> {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("dtDescription", parameter.getDescription());
			parameters.put("dtValue", parameter.getValue());
			parameters.put("dtAcronym", parameter.getAcronym());
			parameters.put("dtFrom", parameter.getBounds().getFrom());
			parameters.put("dtTo", parameter.getBounds().getTo());
			parameters.put("dtLabel", parameter.getLabel() == null ? "" : parameter.getLabel());
			parameters.put("dtLevel", parameter.getLevel());
			parameters.put("fiAnalysis", idAnalysis);
			parameter.setId(simpleJdbcInsert.executeAndReturnKey(parameters).intValue());
		});
	}

	private Stream<ExtendedParameterMapper> extendParameterLoader(JdbcTemplate template, Integer idAnalysis, int type) {
		return template
				.query("Select Parameter.`idParameter` as `id`, Parameter.`dtLabel` as `description` , Parameter.`dtValue` as `value`, Parameter.`fiParameterType` as `idType`, ExtendedParameter.`dtAcronym` as `acronym`, ExtendedParameter.`dtFrom` as `from`, ExtendedParameter.`dtTo` as `to`, ExtendedParameter.`dtLevel` as `level`, Parameter.`fiAnalysis` as `idAnalysis` from ExtendedParameter inner join Parameter on ExtendedParameter.`idExtendedParameter` = Parameter.`idParameter` where Parameter.`fiParameterType` = ? and Parameter.`fiAnalysis` = ?  order by Parameter.`fiAnalysis`, Parameter.`fiParameterType`, ExtendedParameter.`dtLevel`;",
						new Object[] { type, idAnalysis }, new BeanPropertyRowMapper<>(ExtendedParameterMapper.class))
				.stream();
	}

	private LikelihoodParameter createLikelihoodParameter(ExtendedParameterMapper parameter) {
		return new LikelihoodParameter(parameter.getLevel(), parameter.getAcronym(), parameter.getValue(), new Bounds(parameter.getFrom(), parameter.getTo()));
	}

	private ImpactParameter createImpactParameter(ExtendedParameterMapper parameter) {
		return new ImpactParameter(scaleTypes.get(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME), parameter.getAcronym(), parameter.getLevel(), parameter.getValue(),
				parameter.getDescription(), new Bounds(parameter.getFrom(), parameter.getTo()));
	}

	/**
	 * @return the scaleTypes
	 */
	public Map<String, ScaleType> getScaleTypes() {
		return scaleTypes;
	}

	/**
	 * @param scaleTypes
	 *            the scaleTypes to set
	 */
	public void setScaleTypes(Map<String, ScaleType> scaleTypes) {
		this.scaleTypes = scaleTypes;
	}

	/**
	 * @return the analyses
	 */
	public Map<Integer, AnalysisType> getAnalyses() {
		return analyses;
	}

	/**
	 * @param analyses
	 *            the analyses to set
	 */
	public void setAnalyses(Map<Integer, AnalysisType> analyses) {
		this.analyses = analyses;
	}

}
