/**
 * 
 */
package lu.itrust.business.TS.database.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author eomar
 *
 */
public abstract class TrickServiceDataBaseMigration extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        migrate((JdbcTemplate) context);
    }

    public abstract void migrate(JdbcTemplate jdbcTemplate) throws Exception;
}
