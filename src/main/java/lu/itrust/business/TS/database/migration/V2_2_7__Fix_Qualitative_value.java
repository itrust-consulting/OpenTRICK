package lu.itrust.business.TS.database.migration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import lu.itrust.business.TS.constants.Constant;

public class V2_2_7__Fix_Qualitative_value extends TrickServiceDataBaseMigration {

	private final String LOAD_QUANTITATIVE_IMPACT_ID = "SELECT `ScaleType`.`idScaleType` FROM `ScaleType` where `ScaleType`.`dtName` = 'IMPACT';";

	private final String LOAD_VALUE_TO_MIGRATE = "SELECT `ImpactParameter`.`idImpactParameter` , `RealValue`.`idRealValue` FROM `ImpactParameter` INNER JOIN `RealValue` ON ( `ImpactParameter`.`idImpactParameter` = `RealValue`.`fiParameter` ) WHERE `ImpactParameter`.`fiParameterType` <> ?;";

	private final String UPDATE_VALUE = "UPDATE `AssessmentImpacts` SET `dtValueType` = 'VALUE', `fiValue` = ?  WHERE `AssessmentImpacts`.`dtValueType` = 'REAL' AND `AssessmentImpacts`.`fiValue` = ?;";

	private final String CLEAN_UP = "DELETE FROM `RealValue` where `RealValue`.`idRealValue`  = ?;";

	@Override
	public void migrate(JdbcTemplate template) throws Exception {
		template.query(LOAD_QUANTITATIVE_IMPACT_ID, (res) -> {
			template.query(LOAD_VALUE_TO_MIGRATE, newArgPreparedStatementSetter(new Object[] { res.getLong("idScaleType") }), (rs) -> {
				long idRealValue = rs.getLong("idRealValue"), idImpact = rs.getLong("idImpactParameter");
				Map<String, Object> parameters = new HashMap<>();
				SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(template.getDataSource()).withTableName("Value").usingGeneratedKeyColumns("idValue");
				parameters.put("fiParameter", idImpact);
				parameters.put("dtParameterType", Constant.PARAMETER_CATEGORY_IMPACT);
				template.update(UPDATE_VALUE, simpleJdbcInsert.executeAndReturnKey(parameters).intValue(), idRealValue);
				template.update(CLEAN_UP, idRealValue);
			});
		});
	}

}
