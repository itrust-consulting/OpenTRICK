/**
 * 
 */
package lu.itrust.TS.application;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import lu.itrust.business.TS.database.service.ServiceTrickService;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.context.web.WebDelegatingSmartContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author eomar
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(loader = WebDelegatingSmartContextLoader.class, value = "classpath:spring/application-config.xml")
@TestExecutionListeners(listeners = { ServletTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
@ActiveProfiles({ "Dev", "Test", "Debug" })
public abstract class SpringTestConfiguration {

	protected static final String LANGUAGE = "en";

	protected static final String EMAIL = "eom_forum@itrust.lu";

	protected static final String USERNAME = "admin";

	protected static final String PASSWORD = "test.TS_65";

	@Autowired
	protected WebApplicationContext webApplicationContext;

	@Autowired
	protected FilterChainProxy springSecurityFilterChain;
	
	/**
	 * Services
	 */
	@Autowired
	protected ServiceTrickService serviceTrickService;
	
	/**
	 * Properties
	 */
	@Value("${app.settings.version}")
	protected String version;

	protected MockMvc mockMvc;
	
	protected static String installTaskId = null;

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity(springSecurityFilterChain)).build();
	}
	
}
