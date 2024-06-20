package lu.itrust.boot.configuration;

import javax.sql.DataSource;

import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class for Flyway database migration.
 * This class provides bean definitions for Flyway configuration and migration.
 */
@Configuration
@Profile({ "p-auth-all", "p-auth-std-ad", "p-auth-std-ldap", "p-auth-std", "p-auth-ldap", "p-auth-ad" })
public class FlywayConfig {

    /**
     * Configuration class for Flyway database migration.
     * This class provides various configuration options for Flyway migration.
     * It allows you to set the data source, baseline version, table name, and other migration settings.
     * Use this class to customize the behavior of Flyway during database migration.
     */
    @Bean
    @ConditionalOnMissingBean
    org.flywaydb.core.api.configuration.Configuration trickflywayConfig(DataSource dataSource,
            MigrationVersion baselineVersion) {
        var flywayConfig = new ClassicConfiguration();
        flywayConfig.setDataSource(dataSource);
        flywayConfig.setBaselineVersion(baselineVersion);
        flywayConfig.setTable("SchemaVersion");
        flywayConfig.setBaselineOnMigrate(false);
        flywayConfig.setMixed(false);
        flywayConfig.setGroup(false);
        flywayConfig.setLocationsAsStrings("classpath:lu/itrust/business/ts/database/migration",
                "classpath:/migration");
        return flywayConfig;
    }

    /**
        * Returns a MigrationVersion object representing the baseline version for database migrations.
        * The baseline version is determined by the value of the "app.settings.database.version" property.
        *
        * @param version the value of the "app.settings.database.version" property
        * @return a MigrationVersion object representing the baseline version
        */
    @Bean
    @ConditionalOnMissingBean
    MigrationVersion baselineVersion(@Value("${app.settings.database.version}") String version) {
        return MigrationVersion.fromVersion(version);
    }

    /**
     * This class is the main entry point for Flyway database migrations.
     * It provides methods to configure and execute database migrations.
     */
    @Bean(initMethod = "migrate")
    @ConditionalOnMissingBean
    public org.flywaydb.core.Flyway flyway(org.flywaydb.core.api.configuration.Configuration trickflywayConfig) {
        return new org.flywaydb.core.Flyway(trickflywayConfig);
    }
}
