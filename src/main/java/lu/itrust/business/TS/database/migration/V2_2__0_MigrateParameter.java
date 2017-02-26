/**
 * 
 */
package lu.itrust.business.TS.database.migration;

import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.migration.MigrationInfoProvider;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author eomar
 *
 */
public class V2_2__0_MigrateParameter implements SpringJdbcMigration, MigrationInfoProvider {

	/**
	 * 
	 */
	public V2_2__0_MigrateParameter() {
	}

	/* (non-Javadoc)
	 * @see org.flywaydb.core.api.migration.spring.SpringJdbcMigration#migrate(org.springframework.jdbc.core.JdbcTemplate)
	 */
	@Override
	public void migrate(JdbcTemplate arg0) throws Exception {
		System.out.println("Parameters");
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public MigrationVersion getVersion() {
		return MigrationVersion.fromVersion("2.2");
	}

}
