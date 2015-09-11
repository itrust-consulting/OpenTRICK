/**
 * 
 */
package lu.itrust.TS.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import lu.itrust.business.TS.database.service.ServiceTrickService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.context.web.WebDelegatingSmartContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author eomar
 *
 */
@WebAppConfiguration
@ContextConfiguration(loader = WebDelegatingSmartContextLoader.class, value = "classpath:spring/application-config.xml")
@TestExecutionListeners(listeners = { ServletTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
@Test
@ActiveProfiles({ "Dev", "Test", "Debug" })
public abstract class SpringTestConfiguration extends AbstractTestNGSpringContextTests {

	protected static final String LANGUAGE = "en";

	protected static final String EMAIL = "eom_forum@itrust.lu";

	protected static final String USERNAME = "admin";

	protected static final String PASSWORD = "test.TS_65";

	protected static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";

	@Autowired
	protected WebApplicationContext webApplicationContext;

	@Autowired
	protected FilterChainProxy springSecurityFilterChain;

	/**
	 * Services
	 */
	@Autowired
	protected ServiceTrickService serviceTrickService;

	protected MockMvc mockMvc;

	@BeforeMethod
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity(springSecurityFilterChain)).build();
	}

}
