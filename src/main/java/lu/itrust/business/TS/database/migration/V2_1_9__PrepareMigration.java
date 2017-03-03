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
public class V2_1_9__PrepareMigration implements SpringJdbcMigration {

	/**
	 * 
	 */
	public V2_1_9__PrepareMigration() {
	}

	@Override
	public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
		ResultSet foreignKeys = jdbcTemplate.getDataSource().getConnection().getMetaData().getExportedKeys(null, null, "Parameter");
		while (foreignKeys.next())
			jdbcTemplate.update(String.format("ALTER TABLE `%s` DROP FOREIGN KEY `%s`", foreignKeys.getString("FKTABLE_NAME"), foreignKeys.getString("FK_NAME")));
		
		foreignKeys = jdbcTemplate.getDataSource().getConnection().getMetaData().getExportedKeys(null, null, "ParameterType");
		while (foreignKeys.next())
			jdbcTemplate.update(String.format("ALTER TABLE `%s` DROP FOREIGN KEY `%s`", foreignKeys.getString("FKTABLE_NAME"), foreignKeys.getString("FK_NAME")));
		
		foreignKeys = jdbcTemplate.getDataSource().getConnection().getMetaData().getExportedKeys(null, null, "ExtendedParameter");
		while (foreignKeys.next())
			jdbcTemplate.update(String.format("ALTER TABLE `%s` DROP FOREIGN KEY `%s`", foreignKeys.getString("FKTABLE_NAME"), foreignKeys.getString("FK_NAME")));
	}

}
