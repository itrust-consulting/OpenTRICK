/**
 * 
 */
package lu.itrust.business.ts.database.migration;

import java.sql.ResultSet;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author eomar
 *
 */
public class V2_1_9__PrepareMigration extends TrickServiceDataBaseMigration {

	/**
	 * 
	 */
	public V2_1_9__PrepareMigration() {
	}

	@Override
	public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
		ResultSet foreignKeys = jdbcTemplate.getDataSource().getConnection().getMetaData().getExportedKeys(getSchema(),
				null,
				"Parameter");
		while (foreignKeys.next())
			deleteForeignKeys(jdbcTemplate, foreignKeys.getString("FKTABLE_NAME"), foreignKeys.getString("FK_NAME"));

		foreignKeys = jdbcTemplate.getDataSource().getConnection().getMetaData().getExportedKeys(getSchema(), null,
				"ParameterType");
		while (foreignKeys.next())
			deleteForeignKeys(jdbcTemplate, foreignKeys.getString("FKTABLE_NAME"), foreignKeys.getString("FK_NAME"));

		foreignKeys = jdbcTemplate.getDataSource().getConnection().getMetaData().getExportedKeys(getSchema(), null,
				"ExtendedParameter");
		while (foreignKeys.next())
			deleteForeignKeys(jdbcTemplate, foreignKeys.getString("FKTABLE_NAME"), foreignKeys.getString("FK_NAME"));
	}

}
