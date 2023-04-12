package lu.itrust.business.ts.database.migration;

import java.sql.ResultSet;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import lu.itrust.business.ts.exception.TrickException;

public class V2_5_4__Remove_AnalysisStandard_foreign_key_from_Measure extends TrickServiceDataBaseMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        var dataSource = jdbcTemplate.getDataSource();
        Assert.notNull(dataSource, "Datasource not be null");
        var connection = dataSource.getConnection();
        Assert.notNull(connection, "Connection cannot be null");
        ResultSet foreignKeys = connection.getMetaData().getImportedKeys(getSchema(), null,
                "Measure");
        while (foreignKeys.next()) {
            if (foreignKeys.getString("PKTABLE_NAME").equals("AnalysisStandard")
                    && foreignKeys.getString("FKCOLUMN_NAME").equals("fiAnalysisStandard")) {
                deleteForeignKeys(jdbcTemplate, "Measure", foreignKeys.getString("FK_NAME"));
            }
        }
        jdbcTemplate.update("ALTER TABLE `Measure` DROP `fiAnalysisStandard`");
    }

}
