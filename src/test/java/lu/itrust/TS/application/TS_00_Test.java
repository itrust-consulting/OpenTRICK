package lu.itrust.TS.application;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.MediaType;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TS_00_Test extends SpringTestConfiguration {

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

}
