package lu.itrust.business.TS.database.migration;

import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

public class V2_5_1__Add_Missing_ILR_Parameter extends TrickServiceDataBaseMigration {

    private static final String INSERT_INTO_SIMPLE_PARAMETER = "INSERT INTO `SimpleParameter`(`dtDescription`, `dtValue`, `fiParameterType`, `fiAnalysis`) VALUES (?,?,?,%d)";

    private static final String SELECT_ID_ANALYSIS_FROM_ANALYSIS_WHERE_DT_DATA_TRUE = "Select `idAnalysis` From Analysis WHERE `dtData` = 1";

    private static final String SELECT_ID_PARAMETER_TYPE_FROM_PARAMETER_TYPE_WHERE_DT_NAME_SINGLE = "SELECT `idParameterType` FROM `ParameterType` WHERE `dtName` = 'SINGLE'";

    private static String INSERT_ILR_SOA_SCALE = "INSERT INTO `IlrSoaScaleParameter`(`dtValue`, `dtColor`, `dtDescription`, `fiAnalysis`) VALUES (?,?,?,%d)";

    private final List<Integer> analyses = new LinkedList<>();

    private Integer singleParameterId = 1;

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        loadAnalysis(jdbcTemplate);
        addMissingParameters(jdbcTemplate);
    }

    private void addMissingParameters(JdbcTemplate jdbcTemplate) {
        final Object[][] ilrSoaScales = {
                { 20, "#fa7070", "Not achieved" },
                { 40, "#ecac35", "Rudimentary achieved" },
                { 60, "#cdfa34", "Partially achieved" },
                { 80, "#7bd645", "Largely achieved" },
                { 100, "#0ccb11", "Fully achieved" }
        };

        final Object[] singleParameter = { "ilr_rrf_threshold", 5, singleParameterId };

        for (Integer idAnalysis : analyses) {
            for (Object[] data : ilrSoaScales)
                jdbcTemplate.update(String.format(INSERT_ILR_SOA_SCALE, idAnalysis), data);
            jdbcTemplate.update(String.format(INSERT_INTO_SIMPLE_PARAMETER, idAnalysis), singleParameter);
        }
    }

    private void loadAnalysis(JdbcTemplate template) {
        template.query(SELECT_ID_ANALYSIS_FROM_ANALYSIS_WHERE_DT_DATA_TRUE, row -> {
            analyses.add(row.getInt("idAnalysis"));
        });

        template.query(
                SELECT_ID_PARAMETER_TYPE_FROM_PARAMETER_TYPE_WHERE_DT_NAME_SINGLE,
                r -> {
                    this.singleParameterId = r.getInt("idParameterType");
                });
    }
}