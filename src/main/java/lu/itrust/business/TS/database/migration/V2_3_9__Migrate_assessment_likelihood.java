/**
 * 
 */
package lu.itrust.business.TS.database.migration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.model.parameter.helper.Bounds;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.DynamicParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.TS.model.parameter.value.AbstractValue;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.parameter.value.impl.LevelValue;
import lu.itrust.business.TS.model.parameter.value.impl.RealValue;
import lu.itrust.business.TS.model.parameter.value.impl.Value;

/**
 * @author eomar
 *
 */
public class V2_3_9__Migrate_assessment_likelihood extends TrickServiceDataBaseMigration {

	private List<Integer> analyses = new LinkedList<>();

	/**
	 * 
	 */
	public V2_3_9__Migrate_assessment_likelihood() {
	}

	@Override
	public void migrate(JdbcTemplate template) throws Exception {
		loadAnalysis(template);
		final int count[] = { 0 };
		analyses.stream().forEach(i -> {
			updateAssessment(template, i);
			System.out.println(
					String.format("Analysis migration: %d / %d, current id: %d", ++count[0], analyses.size(), i));
		});
		template.update("ALTER TABLE `Assessment` DROP `dtLikelihood`");
	}

	private void updateAssessment(JdbcTemplate template, Integer analysisId) {
		final Map<Integer, String> assessment = new HashMap<>();
		template.query("Select `idAssessment`, `dtLikelihood` From Assessment where fiAnalysis = ?",
				newArgPreparedStatementSetter(new Object[] { analysisId }), (row) -> {
					assessment.put(row.getInt("idAssessment"), row.getString("dtLikelihood"));
				});
		if (assessment.isEmpty())
			return;
		final ValueFactory factory = new ValueFactory(loadLikelihoodParameters(template, analysisId));
		factory.add(loadDynamicParameters(template, analysisId));
		assessment.forEach((id, likelihood) -> saveValue(template, id, factory.findProb(likelihood)));

	}

	private void saveValue(JdbcTemplate template, Integer assessmentId, IValue value) {
		if (value == null)
			return;
		final String type;
		final SimpleJdbcInsert simpleJdbcInsert;
		final Map<String, Object> parameters = new HashMap<>();
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
		} else if (value instanceof Value) {
			simpleJdbcInsert = new SimpleJdbcInsert(template.getDataSource()).withTableName("Value")
					.usingGeneratedKeyColumns("idValue");
			type = "VALUE";
		} else {
			simpleJdbcInsert = new SimpleJdbcInsert(template.getDataSource()).withTableName("FormulaValue")
					.usingGeneratedKeyColumns("idFormulaValue");
			parameters.put("dtLevel", value.getLevel());
			parameters.put("dtValue", value.getReal());
			parameters.put("dtFormula", value.getVariable());
			type = "FORMULA";
		}

		if (value instanceof AbstractValue) {
			parameters.put("dtParameterType", Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD);
			parameters.put("fiParameter", ((AbstractValue) value).getParameter().getId());
		}

		final int valueId = simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
		template.update("UPDATE `Assessment` SET `dtLikelihoodType` = ? ,`fiLikelihood`= ? WHERE `idAssessment` = ?",
				type, valueId, assessmentId);
	}

	private List<DynamicParameter> loadDynamicParameters(JdbcTemplate template, Integer analysisId) {
		return template.query(
				"Select `dtAcronym`,`dtDescription`, `dtValue` From DynamicParameter where fiAnalysis = ?",
				newArgPreparedStatementSetter(new Object[] { analysisId }),
				(row, cout) -> new DynamicParameter(row.getString("dtAcronym"), row.getString("dtDescription"),
						row.getDouble("dtValue")));
	}

	private List<LikelihoodParameter> loadLikelihoodParameters(JdbcTemplate template, Integer analysisId) {
		return template.query(
				"Select `idLikelihoodParameter`, `dtAcronym`, `dtValue` ,`dtFrom`,`dtTo`,`dtLevel` From LikelihoodParameter where fiAnalysis = ? order by dtLevel, dtAcronym, dtValue",
				newArgPreparedStatementSetter(new Object[] { analysisId }),
				(row, cout) -> new LikelihoodParameter(row.getInt("idLikelihoodParameter"), row.getInt("dtLevel"),
						row.getString("dtAcronym"),
						row.getDouble("dtValue"), new Bounds(row.getDouble("dtFrom"), row.getDouble("dtTo"))));
	}

	private void loadAnalysis(JdbcTemplate template) {
		template.query("Select `idAnalysis` From Analysis", (row) -> {
			analyses.add(row.getInt("idAnalysis"));
		});
	}

}
