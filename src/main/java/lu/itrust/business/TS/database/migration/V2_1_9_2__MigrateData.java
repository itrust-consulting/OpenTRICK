/**
 * 
 */
package lu.itrust.business.TS.database.migration;

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

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.migration.helper.AssessmentMapper;
import lu.itrust.business.TS.database.migration.helper.ExtendedParameterMapper;
import lu.itrust.business.TS.database.migration.helper.RiskProfileMapper;
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
public class V2_1_9_2__MigrateData extends TrickServiceDataBaseMigration {

	private final String QUERY_ASSESSMENT_SQL = "SELECT `idAssessment`, `dtALE`, `dtALEO`, `dtALEP`,`dtImpactFin`, `dtImpactLeg`, `dtImpactOp`, `dtImpactReal`, `dtImpactRep`, `dtLikelihood`, `dtLikelihoodReal`, `dtUncertainty` FROM `Assessment` WHERE `fiAnalysis` = ?";

	private final String QUERY_INSERT_RISK_PROFILE_EXP_IMPACT = "INSERT INTO `RiskProfileExpImpacts`(`fiRiskProfile`, `fiExpImpact`) VALUES (?,?)";

	private final String QUERY_INSERT_RISK_PROFILE_RAW_IMPACT = "INSERT INTO `RiskProfileRawImpacts`(`fiRiskProfile`, `fiRawImpact`) VALUES (?,?)";

	private Map<Integer, AnalysisType> analyses = new LinkedHashMap<>();

	private List<ScaleType> cssfScaleTypes = Collections.emptyList();

	private Map<String, ScaleType> scaleTypes = Collections.emptyMap();

	/**
	 * 
	 */
	public V2_1_9_2__MigrateData() {
	}

	/**
	 * @return the analyses
	 */
	public Map<Integer, AnalysisType> getAnalyses() {
		return analyses;
	}

	/**
	 * @return the cssfScaleTypes
	 */
	public List<ScaleType> getCssfScaleTypes() {
		return cssfScaleTypes;
	}

	/**
	 * @return the scaleTypes
	 */
	public Map<String, ScaleType> getScaleTypes() {
		return scaleTypes;
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
		getAnalyses().entrySet().parallelStream()
				.forEach((entry) -> migrateExtended(arg0, entry.getKey(), entry.getValue()));
	}

	/**
	 * @param analyses
	 *                 the analyses to set
	 */
	public void setAnalyses(Map<Integer, AnalysisType> analyses) {
		this.analyses = analyses;
	}

	/**
	 * @param cssfScaleTypes
	 *                       the cssfScaleTypes to set
	 */
	public void setCssfScaleTypes(List<ScaleType> cssfScaleTypes) {
		this.cssfScaleTypes = cssfScaleTypes;
	}

	/**
	 * @param scaleTypes
	 *                   the scaleTypes to set
	 */
	public void setScaleTypes(Map<String, ScaleType> scaleTypes) {
		this.scaleTypes = scaleTypes;
	}

	private void addRiskProfileValue(JdbcTemplate template, String sql, Integer id, IBoundedParameter parameter) {
		template.update(sql, id, parameter.getId());
	}

	private ImpactParameter createImpactParameter(ExtendedParameterMapper parameter) {
		return new ImpactParameter(scaleTypes.get(Constant.PARAMETER_TYPE_IMPACT_NAME), parameter.getAcronym(),
				parameter.getLevel(), parameter.getValue(),
				parameter.getDescription(), new Bounds(parameter.getFrom(), parameter.getTo()));
	}

	private LikelihoodParameter createLikelihoodParameter(ExtendedParameterMapper parameter) {
		return new LikelihoodParameter(parameter.getLevel(), parameter.getAcronym(), parameter.getValue(),
				new Bounds(parameter.getFrom(), parameter.getTo()));
	}

	private void deleteAllRiskProfileFromAnalysis(JdbcTemplate template, Integer idAnalysis) {
		template.update("DELETE FROM `RiskProfile` WHERE `fiAnalysis` = ?", idAnalysis);
	}

	private Stream<ExtendedParameterMapper> extendParameterLoader(JdbcTemplate template, Integer idAnalysis, int type) {
		return template
				.query("Select Parameter.`idParameter` as `id`, Parameter.`dtLabel` as `description` , Parameter.`dtValue` as `value`, Parameter.`fiParameterType` as `idType`, ExtendedParameter.`dtAcronym` as `acronym`, ExtendedParameter.`dtFrom` as `from`, ExtendedParameter.`dtTo` as `to`, ExtendedParameter.`dtLevel` as `level`, Parameter.`fiAnalysis` as `idAnalysis` from ExtendedParameter inner join Parameter on ExtendedParameter.`idExtendedParameter` = Parameter.`idParameter` where Parameter.`fiParameterType` = ? and Parameter.`fiAnalysis` = ?  order by Parameter.`fiAnalysis`, Parameter.`fiParameterType`, ExtendedParameter.`dtLevel`;",
						newArgPreparedStatementSetter(new Object[] { type, idAnalysis }),
						new BeanPropertyRowMapper<>(ExtendedParameterMapper.class))
				.stream();
	}

	private IBoundedParameter findParameter(IBoundedParameter parameter, ValueFactory valueFactory, String type) {
		return (IBoundedParameter) valueFactory.findParameter(parameter.getLevel(), type);
	}

	private IValue findValue(String raw, Map<String, IBoundedParameter> parameters, ValueFactory valueFactory,
			String type) {
		IBoundedParameter parameter = parameters.get(raw);
		if (parameter != null)
			return new LevelValue(parameter.getLevel(), parameter);
		IValue value = valueFactory.findValue(raw, type);
		if (value == null)
			value = valueFactory.findValue(0, type);
		return value;
	}

	private void loadAnalysis(JdbcTemplate template) {
		setAnalyses(new LinkedHashMap<>());
		template.query("Select `idAnalysis`, `dtType` as `type` From Analysis", (row) -> {
			getAnalyses().put(row.getInt("idAnalysis"), AnalysisType.valueOf(row.getString("type")));
		});
	}

	private List<AssessmentMapper> loadAssessment(Integer idAnalysis, JdbcTemplate template) {
		List<AssessmentMapper> assessmentMappers = new LinkedList<>();
		template.query(QUERY_ASSESSMENT_SQL, newArgPreparedStatementSetter(new Object[] { idAnalysis }), (row) -> {
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

	private void loadScaleTypes(JdbcTemplate template) {
		setScaleTypes(template
				.query("Select `id`, `dtName` as `name`, `dtAcronym` as `acronym` From ScaleType where `dtName` in (?,?,?,?,?)",
						newArgPreparedStatementSetter(
								new Object[] { "IMPACT", "FINANCIAL", "LEGAL", "OPERATIONAL", "REPUTATIONAL" }),
						new BeanPropertyRowMapper<>(ScaleType.class))
				.stream().collect(Collectors.toMap(ScaleType::getName, Function.identity())));
		this.setCssfScaleTypes(getScaleTypes().values().stream()
				.filter(type -> !type.getName().equalsIgnoreCase(Constant.PARAMETER_CATEGORY_IMPACT))
				.collect(Collectors.toList()));
	}

	private void migrateExtended(JdbcTemplate template, Integer idAnalysis, AnalysisType analysisType) {
		Map<Integer, ImpactParameter> impactParameters = extendParameterLoader(template, idAnalysis,
				Constant.PARAMETERTYPE_TYPE_IMPACT)
				.collect(Collectors.toMap(ExtendedParameterMapper::getId,
						parameter -> createImpactParameter(parameter)));
		Map<Integer, LikelihoodParameter> likelihoodParameters = extendParameterLoader(template, idAnalysis,
				Constant.PARAMETERTYPE_TYPE_PROPABILITY)
				.collect(Collectors.toMap(ExtendedParameterMapper::getId,
						parameter -> createLikelihoodParameter(parameter)));

		if (likelihoodParameters.isEmpty() && impactParameters.isEmpty()) {
			System.err.println("Analysis does not have parameters : " + idAnalysis);
			return;
		}

		System.out.println(String.format("Migrating analysis (%d), type : %s", idAnalysis, analysisType));

		Map<String, Map<String, IBoundedParameter>> paramters = new LinkedHashMap<>(
				likelihoodParameters.size() * (analysisType == AnalysisType.QUALITATIVE ? 2 : 5));
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
			updateQuantitativeAssessments(template, idAnalysis, paramters);
		} else {
			impactParameters.values().forEach(impact -> {
				cssfScaleTypes.forEach(scaleType -> {
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

			ValueFactory valueFactory = new ValueFactory(likelihoods.values());

			cssfScaleTypes.forEach(scaleType -> {
				Collection<IBoundedParameter> collection = paramters.get(scaleType.getName()).values();
				saveImpactParameter(template, idAnalysis, collection);
				valueFactory.add(collection);
			});

			updateQualitativeAssessments(template, idAnalysis, paramters, valueFactory);
			updateRiskProfiles(template, idAnalysis, impactParameters, likelihoodParameters, valueFactory);
		}
	}

	private void saveImpactParameter(JdbcTemplate template, Integer idAnalysis,
			Collection<IBoundedParameter> impactParameters) {
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(template.getDataSource())
				.withTableName("ImpactParameter").usingGeneratedKeyColumns("idImpactParameter");
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

	private void saveImpactValue(JdbcTemplate template, int idAssessment, IValue value) {
		String type = null;
		SimpleJdbcInsert simpleJdbcInsert = null;
		Map<String, Object> parameters = new HashMap<>();
		if (value instanceof LevelValue) {
			simpleJdbcInsert = new SimpleJdbcInsert(template.getDataSource()).withTableName("LevelValue")
					.usingGeneratedKeyColumns("idLevelValue");
			parameters.put("dtLevel", value.getLevel());
			type = "LEVEL";
		} else if (value instanceof RealValue) {
			simpleJdbcInsert = new SimpleJdbcInsert(template.getDataSource()).withTableName("RealValue")
					.usingGeneratedKeyColumns("idRealValue");
			parameters.put("dtValue", value.getReal());
			type = "REAL";
		} else {
			simpleJdbcInsert = new SimpleJdbcInsert(template.getDataSource()).withTableName("Value")
					.usingGeneratedKeyColumns("idValue");
			type = "VALUE";
		}
		parameters.put("dtParameterType", Constant.PARAMETER_CATEGORY_IMPACT);
		parameters.put("fiParameter", ((AbstractValue) value).getParameter().getId());
		((AbstractValue) value).setId(simpleJdbcInsert.executeAndReturnKey(parameters).intValue());
		template.update("INSERT INTO `AssessmentImpacts`(`fiAssessment`, `dtValueType`, `fiValue`) VALUES (?, ? ,?)",
				idAssessment, type, ((AbstractValue) value).getId());
	}

	private void saveLikelihoodParameter(JdbcTemplate template, Integer idAnalysis,
			Map<Integer, LikelihoodParameter> likelihoodParameters) {
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(template.getDataSource())
				.withTableName("LikelihoodParameter").usingGeneratedKeyColumns("idLikelihoodParameter");
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

	private void updateAssessment(JdbcTemplate template, AssessmentMapper assessmentMapper) {
		template.update(
				"UPDATE `Assessment` SET `dtALE`= ? ,`dtALEO`= ?,`dtALEP`= ? ,`dtLikelihoodReal` = ?, `dtLikelihood` = ?, `dtImpactReal` = ? WHERE `idAssessment` = ?",
				assessmentMapper.getAle(), assessmentMapper.getAleo(), assessmentMapper.getAlep(),
				assessmentMapper.getLikelihoodReal(), assessmentMapper.getLikelihood(),
				assessmentMapper.getRealImpact(), assessmentMapper.getId());
	}

	private void updateQualitativeAssessments(JdbcTemplate template, Integer idAnalysis,
			Map<String, Map<String, IBoundedParameter>> paramters, ValueFactory valueFactory) {
		List<AssessmentMapper> assessmentMappers = loadAssessment(idAnalysis, template);
		Map<String, IBoundedParameter> likelihoods = paramters.get(Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD),
				financials = paramters.get(Constant.DEFAULT_IMPACT_TYPE_NAMES[0]),
				legals = paramters.get(Constant.DEFAULT_IMPACT_TYPE_NAMES[1]),
				operationals = paramters.get(Constant.DEFAULT_IMPACT_TYPE_NAMES[2]),
				reputationals = paramters.get(Constant.DEFAULT_IMPACT_TYPE_NAMES[3]);
		for (AssessmentMapper assessmentMapper : assessmentMappers) {
			IValue likelihood = findValue(assessmentMapper.getLikelihood(), likelihoods, valueFactory,
					Constant.PARAMETER_TYPE_PROPABILITY_NAME),
					financial = findValue(assessmentMapper.getFinancial(), financials, valueFactory,
							Constant.DEFAULT_IMPACT_TYPE_NAMES[0]),
					legal = findValue(assessmentMapper.getLegal(), legals, valueFactory,
							Constant.DEFAULT_IMPACT_TYPE_NAMES[1]),
					operational = findValue(assessmentMapper.getOperational(), operationals, valueFactory,
							Constant.DEFAULT_IMPACT_TYPE_NAMES[2]),
					reputational = findValue(assessmentMapper.getReputational(), reputationals, valueFactory,
							Constant.DEFAULT_IMPACT_TYPE_NAMES[3]);
			assessmentMapper.setLikelihood(likelihood.getVariable());
			assessmentMapper.setRealImpact(valueFactory.findImpactValue(financial, legal, operational, reputational));
			assessmentMapper.setAle(assessmentMapper.getRealImpact() * assessmentMapper.getLikelihoodReal());
			assessmentMapper.setAlep(assessmentMapper.getAle() * assessmentMapper.getUncertainty());
			assessmentMapper.setAleo(assessmentMapper.getAle() / assessmentMapper.getUncertainty());
			saveImpactValue(template, assessmentMapper.getId(), financial);
			saveImpactValue(template, assessmentMapper.getId(), legal);
			saveImpactValue(template, assessmentMapper.getId(), operational);
			saveImpactValue(template, assessmentMapper.getId(), reputational);
			updateAssessment(template, assessmentMapper);
		}
	}

	private void updateQuantitativeAssessments(JdbcTemplate template, Integer idAnalysis,
			Map<String, Map<String, IBoundedParameter>> paramters) {
		ValueFactory valueFactory = new ValueFactory();
		paramters.values().forEach(collection -> valueFactory.add(collection.values()));
		List<AssessmentMapper> assessmentMappers = loadAssessment(idAnalysis, template);
		Map<String, IBoundedParameter> likelihoods = paramters.get(Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD),
				impacts = paramters.get(Constant.PARAMETER_CATEGORY_IMPACT);
		for (AssessmentMapper assessmentMapper : assessmentMappers) {
			IBoundedParameter likelihood = likelihoods.get(assessmentMapper.getLikelihood()),
					impact = impacts.get(assessmentMapper.getFinancial());
			IValue likelihoodValue = likelihood == null ? valueFactory.findProb(assessmentMapper.getLikelihood())
					: new RealValue(likelihood.getValue(), likelihood),
					impactValue = impact == null
							? valueFactory.findValue(assessmentMapper.getFinancial(),
									Constant.PARAMETER_CATEGORY_IMPACT)
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
			updateAssessment(template, assessmentMapper);
			saveImpactValue(template, assessmentMapper.getId(), impactValue);
		}
		deleteAllRiskProfileFromAnalysis(template, idAnalysis);
	}

	private void updateRiskProfiles(JdbcTemplate template, Integer idAnalysis,
			Map<Integer, ImpactParameter> impactParameters,
			Map<Integer, LikelihoodParameter> likelihoodParameters, ValueFactory valueFactory) {
		List<RiskProfileMapper> profileMappers = new LinkedList<>();
		template.query(
				"SELECT `idRiskProfile`, `fiExpImpactFin`, `fiExpImpactLeg`, `fiExpImpactOp`, `fiExpImpactRep`, `fiExpProbability`, `fiRawImpactFin`, `fiRawImpactLeg`, `fiRawImpactOp`, `fiRawImpactRep`, `fiRawProbability` FROM `RiskProfile` WHERE `fiAnalysis` = ?",
				newArgPreparedStatementSetter(new Object[] { idAnalysis }), (row) -> {
					profileMappers.add(new RiskProfileMapper(row.getInt("idRiskProfile"), row.getInt("fiExpImpactFin"),
							row.getInt("fiExpImpactLeg"), row.getInt("fiExpImpactOp"),
							row.getInt("fiExpImpactRep"), row.getInt("fiExpProbability"), row.getInt("fiRawImpactFin"),
							row.getInt("fiRawImpactLeg"), row.getInt("fiRawImpactOp"),
							row.getInt("fiRawImpactRep"), row.getInt("fiRawProbability")));
				});

		for (RiskProfileMapper riskProfileMapper : profileMappers) {

			if (riskProfileMapper.getExpFinancial() > 0)
				addRiskProfileValue(template, QUERY_INSERT_RISK_PROFILE_EXP_IMPACT, riskProfileMapper.getId(),
						findParameter(impactParameters.get(riskProfileMapper.getExpFinancial()), valueFactory,
								Constant.DEFAULT_IMPACT_TYPE_NAMES[0]));

			if (riskProfileMapper.getExpLegal() > 0)
				addRiskProfileValue(template, QUERY_INSERT_RISK_PROFILE_EXP_IMPACT, riskProfileMapper.getId(),
						findParameter(impactParameters.get(riskProfileMapper.getExpLegal()), valueFactory,
								Constant.DEFAULT_IMPACT_TYPE_NAMES[1]));

			if (riskProfileMapper.getExpOperational() > 0)
				addRiskProfileValue(template, QUERY_INSERT_RISK_PROFILE_EXP_IMPACT, riskProfileMapper.getId(),
						findParameter(impactParameters.get(riskProfileMapper.getExpOperational()), valueFactory,
								Constant.DEFAULT_IMPACT_TYPE_NAMES[2]));

			if (riskProfileMapper.getExpReputational() > 0)
				addRiskProfileValue(template, QUERY_INSERT_RISK_PROFILE_EXP_IMPACT, riskProfileMapper.getId(),
						findParameter(impactParameters.get(riskProfileMapper.getExpReputational()), valueFactory,
								Constant.DEFAULT_IMPACT_TYPE_NAMES[3]));

			if (riskProfileMapper.getExpLikelihood() > 0)
				riskProfileMapper.setExpLikelihood(
						findParameter(likelihoodParameters.get(riskProfileMapper.getExpLikelihood()), valueFactory,
								Constant.PARAMETER_TYPE_PROPABILITY_NAME).getId());
			else
				riskProfileMapper.setExpLikelihood(null);

			if (riskProfileMapper.getRawFinancial() > 0)
				addRiskProfileValue(template, QUERY_INSERT_RISK_PROFILE_RAW_IMPACT, riskProfileMapper.getId(),
						findParameter(impactParameters.get(riskProfileMapper.getRawFinancial()), valueFactory,
								Constant.DEFAULT_IMPACT_TYPE_NAMES[0]));

			if (riskProfileMapper.getRawLegal() > 0)
				addRiskProfileValue(template, QUERY_INSERT_RISK_PROFILE_RAW_IMPACT, riskProfileMapper.getId(),
						findParameter(impactParameters.get(riskProfileMapper.getRawLegal()), valueFactory,
								Constant.DEFAULT_IMPACT_TYPE_NAMES[1]));

			if (riskProfileMapper.getRawOperational() > 0)
				addRiskProfileValue(template, QUERY_INSERT_RISK_PROFILE_RAW_IMPACT, riskProfileMapper.getId(),
						findParameter(impactParameters.get(riskProfileMapper.getRawOperational()), valueFactory,
								Constant.DEFAULT_IMPACT_TYPE_NAMES[2]));

			if (riskProfileMapper.getRawReputational() > 0)
				addRiskProfileValue(template, QUERY_INSERT_RISK_PROFILE_RAW_IMPACT, riskProfileMapper.getId(),
						findParameter(impactParameters.get(riskProfileMapper.getRawReputational()), valueFactory,
								Constant.DEFAULT_IMPACT_TYPE_NAMES[3]));

			if (riskProfileMapper.getRawLikelihood() > 0)
				riskProfileMapper.setRawLikelihood(
						findParameter(likelihoodParameters.get(riskProfileMapper.getRawLikelihood()), valueFactory,
								Constant.PARAMETER_TYPE_PROPABILITY_NAME).getId());
			else
				riskProfileMapper.setRawLikelihood(null);

			template.update(
					"UPDATE `RiskProfile` SET `fiExpProbability` = ?, `fiRawProbability` = ? WHERE `idRiskProfile` = ?",
					riskProfileMapper.getExpLikelihood(),
					riskProfileMapper.getRawLikelihood(), riskProfileMapper.getId());
		}

	}

}
