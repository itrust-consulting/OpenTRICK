package lu.itrust.business.TS.database.migration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class V2_2_5__Fix_Risk_Profile_data extends TrickServiceDataBaseMigration {

	private final String EXP_REQUEST = "Select RiskProfile.fiAnalysis as idAnalysis, RiskProfile.idRiskProfile as idRiskProfile, ImpactParameter.idImpactParameter as idImpactParameter, ImpactParameter.dtAcronym as acronym From RiskProfile , RiskProfileExpImpacts , ImpactParameter where RiskProfileExpImpacts.fiRiskProfile = RiskProfile.idRiskProfile and RiskProfileExpImpacts.fiExpImpact = ImpactParameter.idImpactParameter and RiskProfile.fiAnalysis <> ImpactParameter.fiAnalysis;";
	private final String RAW_REQUEST = "Select RiskProfile.fiAnalysis as idAnalysis, RiskProfile.idRiskProfile as idRiskProfile, ImpactParameter.idImpactParameter as idImpactParameter, ImpactParameter.dtAcronym as acronym From RiskProfile , RiskProfileRawImpacts , ImpactParameter where RiskProfileRawImpacts.fiRiskProfile = RiskProfile.idRiskProfile and RiskProfileRawImpacts.fiRawImpact = ImpactParameter.idImpactParameter and RiskProfile.fiAnalysis <> ImpactParameter.fiAnalysis;";
	private final String PARAMETER_REQUEST = "SELECT `idImpactParameter` FROM `ImpactParameter` WHERE `fiAnalysis` = ? and `dtAcronym` = ?;";
	private final String UPDATE_EXP_REQUEST = "UPDATE `RiskProfileExpImpacts` SET `fiExpImpact`= ? WHERE `fiRiskProfile` = ? and `fiExpImpact` = ?;";
	private final String UPDATE_RAW_REQUEST = "UPDATE `RiskProfileRawImpacts` SET `fiRawImpact`= ? WHERE `fiRiskProfile` = ? and `fiRawImpact` = ?;";

	private static String KEY_FORMAT = "%s__--__%d";

	@Override
	public void migrate(JdbcTemplate template) throws Exception {
		Map<String, Integer> parametersMappers = new HashMap<>();
		template.query(EXP_REQUEST, loadData(template, parametersMappers, UPDATE_EXP_REQUEST));
		template.query(RAW_REQUEST, loadData(template, parametersMappers, UPDATE_RAW_REQUEST));
		parametersMappers.clear();
	}

	private RowCallbackHandler loadData(JdbcTemplate template, Map<String, Integer> parametersMappers, String updateRequest) {
		return (row) -> {
			Integer idAnalysis = row.getInt("idAnalysis"), idOldImpact = row.getInt("idImpactParameter"), idRiskProfile = row.getInt("idRiskProfile");
			String acronym = row.getString("acronym"), key = String.format(KEY_FORMAT, acronym, idAnalysis);
			if (!parametersMappers.containsKey(key)) {
				template.query(PARAMETER_REQUEST,  newArgPreparedStatementSetter(new Object[] { idAnalysis, acronym }), (row2) -> {
					parametersMappers.put(key, row2.getInt("idImpactParameter"));
				});
			}
			template.update(updateRequest, parametersMappers.get(key), idRiskProfile, idOldImpact);
		};
	}

}
