package lu.itrust.business.ts.database.migration;

import java.sql.ResultSet;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

public class V2_3_0__Fix_bad_constraint_in_Risk_information extends TrickServiceDataBaseMigration {

	@Override
	public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
		var dataSource = jdbcTemplate.getDataSource();
		Assert.notNull(dataSource, "Datasource not be null");
		var connection = dataSource.getConnection();
		Assert.notNull(connection, "Connection cannot be null");
		ResultSet foreignKeys = connection.getMetaData().getImportedKeys(getSchema(), null,
				"RiskInformation");
		while (foreignKeys.next())
			deleteForeignKeys(jdbcTemplate, "RiskInformation", foreignKeys.getString("FK_NAME"));

		ResultSet uniqueKeys = connection.getMetaData().getIndexInfo(getSchema(), null,
				"RiskInformation", true, true);
		while (uniqueKeys.next()) {
			String index = uniqueKeys.getString("INDEX_NAME");
			if (index == null || index.equals("PRIMARY"))
				continue;
			deleteIndex(jdbcTemplate, "RiskInformation", index);
		}

		jdbcTemplate.update(
				"ALTER TABLE `RiskInformation` ADD UNIQUE KEY `UKitqk9tos4lt3ugbbfjb6503k5` (`fiAnalysis`,`dtLabel`,`dtChapter`,`dtCategory`);");

		while (foreignKeys.previous())
			jdbcTemplate.update(
					String.format("ALTER TABLE `%s` ADD CONSTRAINT `%s` FOREIGN KEY (`%s`) REFERENCES `%s` (`%s`)",
							foreignKeys.getString("FKTABLE_NAME"),
							foreignKeys.getString("FK_NAME"), foreignKeys.getString("FKCOLUMN_NAME"),
							foreignKeys.getString("PKTABLE_NAME"), foreignKeys.getString("PKCOLUMN_NAME")));

	}

}
