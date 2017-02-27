/**
 * 
 */
package lu.itrust.business.TS.database.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.migration.helper.ExtendedParameterMapper;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.helper.Bounds;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.TS.model.scale.ScaleType;

/**
 * @author eomar
 *
 */
public class V2_2_1__MigrateParameter implements SpringJdbcMigration {

	private Map<String, ScaleType> scaleTypes = Collections.emptyMap();

	private Map<Integer, AnalysisType> analyses = new LinkedHashMap<>();

	/**
	 * 
	 */
	public V2_2_1__MigrateParameter() {
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
		setScaleTypes(template.query("Select `id`, `dtName` as `name`, `dtAcronym` as `acronym` From ScaleType", new BeanPropertyRowMapper<>(ScaleType.class)).stream()
				.collect(Collectors.toMap(ScaleType::getName, Function.identity())));
	}

	private void migrateExtended(JdbcTemplate template, Integer idAnalysis, AnalysisType analysisType) {

		Map<Integer, ImpactParameter> impactParameters = extendParameterLoader(template, idAnalysis, Constant.PARAMETERTYPE_TYPE_IMPACT)
				.collect(Collectors.toMap(ExtendedParameterMapper::getId, parameter -> createImpactParameter(parameter)));

		Map<Integer, LikelihoodParameter> likelihoodParameters = extendParameterLoader(template, idAnalysis, Constant.PARAMETERTYPE_TYPE_PROPABILITY)
				.collect(Collectors.toMap(ExtendedParameterMapper::getId, parameter -> createLikelihoodParameter(parameter)));

		saveLikelihoodParameter(template, idAnalysis, likelihoodParameters);

		if (analysisType == AnalysisType.QUALITATIVE) {

		} else {
			saveImpactParameter(template, idAnalysis, impactParameters);
		}
	}

	private void saveImpactParameter(JdbcTemplate template, Integer idAnalysis, Map<Integer, ImpactParameter> impactParameters) {
	}

	private void saveLikelihoodParameter(JdbcTemplate template, Integer idAnalysis, Map<Integer, LikelihoodParameter> likelihoodParameters) {
		// final String sql = "INSERT INTO
		// `LikelihoodParameter`(`dtDescription`, `dtValue`, `dtAcronym`,
		// `dtFrom`, `dtTo`, `dtLabel`, `dtLevel`, `fiAnalysis`) VALUES
		// (?,?,?,?,?,?,?,?,?)";
		/*
		 * MapSqlParameterSource parameters = new MapSqlParameterSource();
		 * KeyHolder holder = new GeneratedKeyHolder(); template.update(new
		 * PreparedStatementCreator() {
		 * 
		 * @Override public PreparedStatement createPreparedStatement(Connection
		 * con) throws SQLException { PreparedStatement statement =
		 * con.prepareStatement(sql); statement.setString(0, x); return null; }
		 * }, holder);
		 */

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
			System.out.println(parameter.getId());
		});

	}

	private Stream<ExtendedParameterMapper> extendParameterLoader(JdbcTemplate template, Integer idAnalysis, int type) {
		return template
				.query("Select Parameter.`idParameter` as `id`, Parameter.`dtLabel` as `label` , Parameter.`dtValue` as `value`, Parameter.`fiParameterType` as `idType`, ExtendedParameter.`dtAcronym` as `acronym`, ExtendedParameter.`dtFrom` as `from`, ExtendedParameter.`dtTo` as `to`, ExtendedParameter.`dtLevel` as `level`, Parameter.`fiAnalysis` as `idAnalysis` from ExtendedParameter inner join Parameter on ExtendedParameter.`idExtendedParameter` = Parameter.`idParameter` where Parameter.`fiParameterType` = ? and Parameter.`fiAnalysis` = ?  order by Parameter.`fiAnalysis`, Parameter.`fiParameterType`, ExtendedParameter.`dtLevel`;",
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
