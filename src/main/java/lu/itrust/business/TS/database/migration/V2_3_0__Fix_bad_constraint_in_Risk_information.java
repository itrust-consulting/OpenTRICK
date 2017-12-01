package lu.itrust.business.TS.database.migration;

import java.sql.ResultSet;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_3_0__Fix_bad_constraint_in_Risk_information implements SpringJdbcMigration {

	@Override
	public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
		ResultSet foreignKeys = jdbcTemplate.getDataSource().getConnection().getMetaData().getImportedKeys(null, null, "RiskInformation");
		while (foreignKeys.next())
			jdbcTemplate.update(String.format("ALTER TABLE `%s` DROP FOREIGN KEY `%s`", "RiskInformation", foreignKeys.getString("FK_NAME")));

		ResultSet uniqueKeys = jdbcTemplate.getDataSource().getConnection().getMetaData().getIndexInfo(null, null, "RiskInformation", true, true);
		while (uniqueKeys.next()) {
			String index = uniqueKeys.getString("INDEX_NAME");
			if (index == null || index.equals("PRIMARY"))
				continue;
			jdbcTemplate.update(String.format("ALTER TABLE `%s` DROP INDEX `%s`", "RiskInformation", index));
			break;
		}
		
		jdbcTemplate.update("ALTER TABLE `RiskInformation` ADD UNIQUE KEY `UKitqk9tos4lt3ugbbfjb6503k5` (`fiAnalysis`,`dtLabel`,`dtChapter`,`dtCategory`);");

		while (foreignKeys.previous())
			jdbcTemplate.update(String.format("ALTER TABLE `%s` ADD CONSTRAINT `%s` FOREIGN KEY (`%s`) REFERENCES `%s` (`%s`)", foreignKeys.getString("FKTABLE_NAME"),
					foreignKeys.getString("FK_NAME"), foreignKeys.getString("FKCOLUMN_NAME"), foreignKeys.getString("PKTABLE_NAME"), foreignKeys.getString("PKCOLUMN_NAME")));

	}

}
