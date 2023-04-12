package lu.itrust.ts.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;

import lu.itrust.boot.Application;
import lu.itrust.business.ts.database.service.ServiceTrickService;
import lu.itrust.ts.boot.configuration.TestSessionFactoryConfig;

/**
 * @author eomar
 *
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = { "classpath:application.properties", "classpath:deployment.properties",
		"classpath:deployment-ldap.properties" })
@Import({ TestSessionFactoryConfig.class })
@TestExecutionListeners(listeners = { ServletTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
@ActiveProfiles({ "Dev", "Test", "Debug" })
public abstract class SpringTestConfiguration extends AbstractTestNGSpringContextTests {

	protected static final String LANGUAGE = "en";

	protected static final String EMAIL = "test@itrust.lu";

	protected static final String USERNAME = "admin";

	protected static final String PASSWORD = "test.TS_2022";

	protected static final String ADMIN_USER_ID = "ADMIN_USER_ID";

	protected static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";

	@Autowired
	protected WebApplicationContext webApplicationContext;

	/**
	 * Services
	 */
	@Autowired
	protected ServiceTrickService serviceTrickService;

	@Autowired
	protected MockMvc mockMvc;

	/*@BeforeMethod(groups = "setup")
	public void setUp() throws Exception {
		/*this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
				.apply(springSecurity()).build();
	}*/

}
