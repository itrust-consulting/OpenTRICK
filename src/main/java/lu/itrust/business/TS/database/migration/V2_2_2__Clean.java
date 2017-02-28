/**
 * 
 */
package lu.itrust.business.TS.database.migration;

import java.sql.ResultSet;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author eomar
 *
 */
public class V2_2_2__Clean implements SpringJdbcMigration {

	/**
	 * 
	 */
	public V2_2_2__Clean() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.flywaydb.core.api.migration.spring.SpringJdbcMigration#migrate(org.
	 * springframework.jdbc.core.JdbcTemplate)
	 */
	@Override
	public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
		ResultSet foreignKeys = jdbcTemplate.getDataSource().getConnection().getMetaData().getImportedKeys(null, null, "RiskProfile");
		while (foreignKeys.next()) {
			String pkTableName = foreignKeys.getString("PKTABLE_NAME"), fkName = foreignKeys.getString("FK_NAME");
			if (pkTableName.equalsIgnoreCase("extendedparameter"))
				jdbcTemplate.update(String.format("ALTER TABLE `RiskProfile` DROP FOREIGN KEY `%s`", fkName));
		}

		jdbcTemplate.update(
				"ALTER TABLE `RiskProfile` DROP COLUMN `fiExpImpactFin`, DROP COLUMN `fiExpImpactLeg`, DROP COLUMN `fiExpImpactOp`, DROP COLUMN `fiExpImpactRep`, DROP COLUMN `fiRawImpactFin`, DROP COLUMN `fiRawImpactLeg`, DROP COLUMN `fiRawImpactOp`, DROP COLUMN `fiRawImpactRep`;");

		jdbcTemplate.update("DROP TABLE `ExtendedParameter`;");

		foreignKeys = jdbcTemplate.getDataSource().getConnection().getMetaData().getExportedKeys(null, null, "Parameter");
		while (foreignKeys.next())
			jdbcTemplate.update(String.format("ALTER TABLE `%s` DROP FOREIGN KEY `%s`", foreignKeys.getString("FKTABLE_NAME"), foreignKeys.getString("FK_NAME")));

		jdbcTemplate.update("DROP TABLE `Parameter`;");
	}

}
