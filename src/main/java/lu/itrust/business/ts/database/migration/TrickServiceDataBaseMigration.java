/**
 * 
 */
package lu.itrust.business.ts.database.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.lang.Nullable;

import lu.itrust.business.ts.component.TrickLogManager;

/**
 * @author eomar
 *
 */
public abstract class TrickServiceDataBaseMigration extends BaseJavaMigration {

    private String schema;

    @Override
    public void migrate(Context context) throws Exception {
        setSchema(context.getConnection().getCatalog());
        migrate(new JdbcTemplate(context.getConfiguration().getDataSource()));
    }

    public abstract void migrate(JdbcTemplate jdbcTemplate) throws Exception;

    public String getSchema() {
        return schema;
    }

    protected void setSchema(String schema) {
        this.schema = schema;
    }

    protected int deleteForeignKeys(JdbcTemplate jdbcTemplate, String tableName, String key) {
        try {
            return jdbcTemplate.update(String.format("ALTER TABLE `%s` DROP FOREIGN KEY `%s`", tableName, key));
        } catch (DataAccessException e) {
            TrickLogManager.persist(e);
            return 1;
        }
    }

    protected int deleteIndex(JdbcTemplate jdbcTemplate, String tableName, String key) {
        try {
            return jdbcTemplate.update(String.format("ALTER TABLE `%s` DROP INDEX `%s`", tableName, key));
        } catch (DataAccessException e) {
            TrickLogManager.persist(e);
            return 1;
        }
    }

    protected PreparedStatementSetter newArgPreparedStatementSetter(@Nullable Object[] args) {
        return new ArgumentPreparedStatementSetter(args);
    }
}
