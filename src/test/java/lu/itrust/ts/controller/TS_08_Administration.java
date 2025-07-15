/**
 * 
 */
package lu.itrust.ts.controller;

import static lu.itrust.ts.helper.TestConstant.SIMPLE_ANALYSIS_V0_0_1_ID;
import static lu.itrust.ts.helper.TestSharingData.getInteger;
import static lu.itrust.ts.helper.TestSharingData.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import lu.itrust.business.ts.database.dao.impl.DAOCustomerImpl;
import lu.itrust.business.ts.database.dao.impl.DAOUserImpl;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.TSSettingName;

/**
 * @author eomar
 *
 */
@Test(dependsOnGroups = "CreateAnalysis", groups = "Administration")
public class TS_08_Administration extends SpringTestConfiguration {

	private static final String CUSTOMER_TO_DELETE_ID = "customer-to-delete-id";

	private static final String CUSTOMER_CITY = "0moi554";

	private static final String CUSTOMER_EMAIL = "moi@moi.fr";

	private static final String CUSTOMER_PHONE = "9657458125";

	private static final String CUSTOMER_NAME = "moi";

	@Autowired
	private SessionFactory sessionFactory;

	private static final String USER_TO_DELETE_ID = "user-to-delete-id";

	@Test
	public void test_00_addUser() throws Exception {
		this.mockMvc
				.perform(post("/Admin/User/Save").with(csrf()).accept(APPLICATION_JSON_CHARSET_UTF_8).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
						.content(String.format(
								"{ \"id\":\"0\", \"connexionType\": \"0\" ,\"login\": \"%s\",\"ROLE_USER\":\"on\", \"password\": \"%s\",\"repeatPassword\": \"%s\",\"firstName\": \"%s\",\"lastName\": \"%s\",\"email\": \"%s\",\"locale\": \"%s\", \"using2FA\": \"%s\"}",
								"user", "user.Pass-20", "user.Pass-20", "First name", "Last name", "email@itrust.lu", "fr", false)))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(jsonPath("$.success").exists());
		Session session = null;
		try {
			session = sessionFactory.openSession();
			put(USER_TO_DELETE_ID, new DAOUserImpl(session).get("user").getId());
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
	}

	@Test
	public void test_01_AddCustomer() throws Exception {
		this.mockMvc
				.perform(post("/KnowledgeBase/Customer/Save").with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).with(csrf()).contentType(APPLICATION_JSON_CHARSET_UTF_8).accept(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format(
								"{\"id\":\"0\", \"organisation\":\"%s\", \"contactPerson\":\"%s\", \"phoneNumber\":\"%s\", \"email\":\"%s\", \"address\":\"%s\", \"city\":\"%s\", \"zipCode\":\"%s\", \"country\":\"%s\"}",
								CUSTOMER_NAME, CUSTOMER_NAME, CUSTOMER_PHONE, CUSTOMER_EMAIL, CUSTOMER_NAME, CUSTOMER_NAME, CUSTOMER_CITY, CUSTOMER_NAME))
						.with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andExpect(content().string("{}"));
		Session session = null;
		try {
			session = sessionFactory.openSession();
			put(CUSTOMER_TO_DELETE_ID, new DAOCustomerImpl(session).getFromContactPerson(CUSTOMER_NAME).getId());
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
	}

	@Test(dependsOnMethods = "test_00_addUser")
	public void test_02_AddCustomerUser() throws Exception {
		this.mockMvc
				.perform(post(String.format("/Admin/Customer/%d/Manage-access/Update", getInteger(CUSTOMER_TO_DELETE_ID))).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).with(csrf())
						.contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"%d\" : \"true\", \"%d\" : \"true\"}", getInteger(USER_TO_DELETE_ID), getInteger(USER_TO_DELETE_ID)))
						.with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_01_AddCustomer")
	@Transactional(readOnly = true)
	public void test_03_SwitchCustomer() throws Exception {
		this.mockMvc
				.perform(get(String.format("/Admin/Analysis/%d/Switch/Customer", getInteger(CUSTOMER_TO_DELETE_ID))).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).with(csrf())
						.accept(APPLICATION_JSON_CHARSET_UTF_8).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")))
				.andExpect(status().isOk()).andExpect(view().name("jsp/admin/analysis/switch-customer"));
	}

	@Test(dependsOnMethods = "test_03_SwitchCustomer")
	@Transactional(readOnly = true)
	public void test_04_SwitchCustomer() throws Exception {
		this.mockMvc
				.perform(post(String.format("/Admin/Analysis/%d/Switch/Customer/%d", getInteger(SIMPLE_ANALYSIS_V0_0_1_ID), getInteger(CUSTOMER_TO_DELETE_ID)))
						.contentType(APPLICATION_JSON_CHARSET_UTF_8).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_00_addUser")
	@Transactional(readOnly = true)
	public void test_05_SwitchOwner() throws Exception {
		this.mockMvc
				.perform(get(String.format("/Admin/Analysis/%d/Switch/Owner", getInteger(SIMPLE_ANALYSIS_V0_0_1_ID))).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).with(csrf())
						.accept(APPLICATION_JSON_CHARSET_UTF_8).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")))
				.andExpect(status().isOk()).andExpect(view().name("jsp/admin/analysis/switch-owner"));
	}

	@Test(dependsOnMethods = "test_05_SwitchOwner")
	@Transactional(readOnly = true)
	public void test_06_SwitchOwner() throws Exception {
		this.mockMvc
				.perform(post(String.format("/Admin/Analysis/%d/Switch/Owner/%d", getInteger(SIMPLE_ANALYSIS_V0_0_1_ID), getInteger(USER_TO_DELETE_ID)))
						.contentType(APPLICATION_JSON_CHARSET_UTF_8).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = { "test_02_AddCustomerUser", "test_04_SwitchCustomer" })
	public void test_07_DeleteCustomer() throws Exception {
		this.mockMvc
				.perform(post("/Admin/Customer/" + getInteger(CUSTOMER_TO_DELETE_ID)+"/Delete").with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).with(csrf())
						.accept(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_00_addUser")
	public void test_08_ManageAnalysisAccessView() throws Exception {
		this.mockMvc.perform(get(String.format("/Admin/Analysis/%d/ManageAccess", getInteger(SIMPLE_ANALYSIS_V0_0_1_ID))).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).with(csrf())
				.with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))).andExpect(status().isOk()).andExpect(view().name("jsp/analyses/all/forms/rights"));
	}

	@Test(dependsOnMethods = "test_08_ManageAnalysisAccessView")
	public void test_09_ManageAnalysisAccessSave() throws Exception {
		this.mockMvc
				.perform(post("/Admin/Analysis/ManageAccess/Update").content(String.format("{\"analysisId\":\"%d\",\"userRights\":{\"%d\":{\"oldRight\":\"ALL\"}}}",
						getInteger(SIMPLE_ANALYSIS_V0_0_1_ID), getInteger(USER_TO_DELETE_ID))).contentType(APPLICATION_JSON_CHARSET_UTF_8).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
						.with(csrf()))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(jsonPath("$.success").exists());
	}

	@Test
	public void test_10_Home() throws Exception {
		this.mockMvc
				.perform(post(String.format("/Admin", getInteger(SIMPLE_ANALYSIS_V0_0_1_ID))).contentType(APPLICATION_JSON_CHARSET_UTF_8).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
						.with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")))
				.andExpect(status().isOk()).andExpect(view().name("jsp/admin/administration"))
				.andExpect(model().attributeExists("tsSettings", "logFilter", "logLevels", "logTypes", "actions", "authors"));
	}

	@Test
	public void test_11_UpdateTSSettings() throws Exception {
		this.mockMvc
				.perform(post("/Admin/TSSetting/Update").with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).with(csrf()).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"name\" : \"%s\", \"value\" : \"false\"}", TSSettingName.SETTING_ALLOWED_SIGNUP)).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")))
				.andExpect(status().isOk()).andExpect(content().string("true"));
		this.mockMvc
				.perform(post("/Admin/TSSetting/Update").with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).with(csrf()).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"name\" : \"%s\", \"value\" : \"false\"}", TSSettingName.SETTING_ALLOWED_SIGNUP)).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")))
				.andExpect(status().isOk()).andExpect(content().string("true"));
	}

	@Test(dependsOnMethods = { "test_02_AddCustomerUser", "test_06_SwitchOwner", "test_09_ManageAnalysisAccessSave" })
	public void test_11_deleteUser() throws Exception {
		this.mockMvc
				.perform(post("/Admin/User/Delete").with(csrf()).contentType(APPLICATION_JSON_CHARSET_UTF_8).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
						.content(String.format("{\"idUser\":%d, \"switchOwners\": {}, \"deleteAnalysis\":[] }", getInteger(USER_TO_DELETE_ID))))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_11_deleteUser")
	public void test_12_UpdateLogsSettings() throws Exception {
		this.mockMvc
				.perform(post("/Admin/Log/Filter/Update").with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).with(csrf()).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"type\" : \"%s\", \"level\" : \"%s\", \"action\" : \"%s\", \"author\" : \"%s\",\"direction\" : \"%s\", \"size\" : %d}",
								LogType.ADMINISTRATION, LogLevel.WARNING, LogAction.DELETE, USERNAME, "desc", 200))
						.with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_12_UpdateLogsSettings")
	public void test_12_LoadLogsSection() throws Exception {
		this.mockMvc
				.perform(get("/Admin/Log/Section").contentType(APPLICATION_JSON_CHARSET_UTF_8).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")))
				.andExpect(status().isOk()).andExpect(view().name("jsp/admin/log/section")).andExpect(model().attributeExists("trickLogs"));
	}
}
