/**
 * 
 */
package lu.itrust.TS.application;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
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
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author eomar
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(loader = WebDelegatingSmartContextLoader.class, value = "classpath:spring/application-config.xml")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TestExecutionListeners(listeners = { ServletTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
@ActiveProfiles({ "Dev", "Test", "Debug" })
public class ConfigClassApplicationContextTests {

	private static final String LANGUAGE = "en";

	private static final String EMAIL = "omar@itrust.lu";

	private static final String USERNAME = "admin";

	private static final String PASSWORD = "test.TS_65";

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private MockMvc mockMvc;
	
	private static String installTaskId = null;

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity(springSecurityFilterChain)).build();
	}

	@Test
	public void test_00_CreateAdminAccount() throws Exception {
		this.mockMvc
				.perform(
						post("/DoRegister")
								.with(csrf())
								.accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
								.content(
										String.format(
												"{\"login\": \"%s\", \"password\": \"%s\",\"repeatPassword\": \"%s\",\"firstName\": \"%s\",\"lastName\": \"%s\",\"email\": \"%s\",\"locale\": \"%s\"}",
												USERNAME, PASSWORD, PASSWORD, USERNAME, USERNAME, EMAIL, LANGUAGE))).andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(content().string("{}"));
	}

	@Test
	public void test_01_Authenticate() throws Exception {
		this.mockMvc.perform(formLogin().loginProcessingUrl("/signin").user(USERNAME).password(PASSWORD)).andExpect(status().isFound()).andExpect(authenticated())
				.andExpect(redirectedUrl("/"));
	}

	@Test
	public void test_02_Install() throws Exception {
		installTaskId  =  new ObjectMapper().readTree(this.mockMvc.perform(get("/Install").with(httpBasic(USERNAME, PASSWORD))).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.idTask").exists()).andReturn().getResponse().getContentAsString()).findValue("idTask").asText(null);
	}
	
	@Test(timeout=30000)
	public void test_03_Intall() {
		Assert.notNull(installTaskId, "Task ID cannot be null");
	}
}
