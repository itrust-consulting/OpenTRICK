/**
 * 
 */
package lu.itrust.business.TS.database.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author eomar
 *
 */
public class V2_2__Clean implements SpringJdbcMigration {

	/**
	 * 
	 */
	public V2_2__Clean() {
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
		jdbcTemplate.execute(
				"ALTER TABLE `RiskProfile` DROP COLUMN `fiExpImpactFin`, DROP COLUMN `fiExpImpactLeg`, DROP COLUMN `fiExpImpactOp`, DROP COLUMN `fiExpImpactRep`, DROP COLUMN `fiRawImpactFin`, DROP COLUMN `fiRawImpactLeg`, DROP COLUMN `fiRawImpactOp`, DROP COLUMN `fiRawImpactRep`;");
		
		jdbcTemplate.execute("ALTER TABLE `Assessment` DROP COLUMN `dtImpactFin`, DROP COLUMN `dtImpactLeg`, DROP COLUMN `dtImpactOp`, DROP COLUMN `dtImpactRep`;");

		jdbcTemplate.execute("DROP TABLE IF EXISTS `ExtendedParameter`;");
		
		jdbcTemplate.execute("DROP TABLE IF EXISTS `Parameter`;");
		
		jdbcTemplate.execute("ALTER TABLE `Analysis` DROP `dtCssf`;");
	}

}
