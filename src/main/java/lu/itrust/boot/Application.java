/**
 * 
 */
package lu.itrust.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author eomar
 *
 */
@ComponentScan({"lu.itrust.boot.configuration","lu.itrust.business"})
@SpringBootApplication(exclude = { FlywayAutoConfiguration.class, DataSourceAutoConfiguration.class,
		FreeMarkerAutoConfiguration.class, SecurityAutoConfiguration.class, TransactionAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class })
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
