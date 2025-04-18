package lu.itrust.ts.controller;

import static lu.itrust.ts.helper.TestSharingData.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.Assert.notNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import lu.itrust.business.ts.database.service.ServiceUser;
import lu.itrust.business.ts.usermanagement.User;


@Test(groups = "firstAccount")
public class TS_01_CreateAdminAccount extends SpringTestConfiguration {

	@Autowired
	private ServiceUser serviceUser;

	@Test(groups = "firstAccount")
	public void test_00_CreateAdminAccount() throws Exception {
		this.mockMvc
				.perform(post("/DoRegister").with(csrf()).accept(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format(
								"{\"login\": \"%s\", \"password\": \"%s\",\"repeatPassword\": \"%s\",\"firstName\": \"%s\",\"lastName\": \"%s\",\"email\": \"%s\",\"locale\": \"%s\"}",
								USERNAME, PASSWORD, PASSWORD, USERNAME, USERNAME, EMAIL, LANGUAGE)))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(content().string("{}"));
	}

	@Test(dependsOnMethods = "test_00_CreateAdminAccount")
	public void test_01_Authenticate() throws Exception {
		this.mockMvc.perform(formLogin().loginProcessingUrl("/Signin").user(USERNAME).password(PASSWORD)).andExpect(status().isFound()).andExpect(authenticated())
				.andExpect(redirectedUrl("/Home"));
	}

	@Test(dependsOnMethods = "test_01_Authenticate")
	@Transactional(readOnly = true)
	public void test_02_LoadUser() throws Exception {
		User adminUser = serviceUser.get(USERNAME);
		notNull(adminUser, "No user has been created");
		put(ADMIN_USER_ID, adminUser.getId());
	}
}
