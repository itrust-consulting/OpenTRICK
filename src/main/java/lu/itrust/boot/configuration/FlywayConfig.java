package lu.itrust.boot.configuration;

import javax.sql.DataSource;

import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({ "p-auth-all", "p-auth-std-ad", "p-auth-std-ldap", "p-auth-std", "p-auth-ldap", "p-auth-ad" })
public class FlywayConfig {

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

    @Bean
    @ConditionalOnMissingBean
    MigrationVersion baselineVersion(@Value("${app.settings.database.version}") String version) {
        return MigrationVersion.fromVersion(version);
    }

    @Bean(initMethod = "migrate")
    @ConditionalOnMissingBean
    public org.flywaydb.core.Flyway flyway(org.flywaydb.core.api.configuration.Configuration trickflywayConfig) {
        return new org.flywaydb.core.Flyway(trickflywayConfig);
    }
}
