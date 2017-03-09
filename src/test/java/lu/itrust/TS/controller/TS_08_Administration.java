/**
 * 
 */
package lu.itrust.TS.controller;

import static lu.itrust.TS.helper.TestConstant.SIMPLE_ANALYSIS_V0_0_1_ID;
import static lu.itrust.TS.helper.TestSharingData.getInteger;
import static lu.itrust.TS.helper.TestSharingData.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
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

import lu.itrust.business.TS.database.dao.hbm.DAOCustomerHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.TSSettingName;

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
				.perform(post("/Admin/User/Save").with(csrf()).accept(APPLICATION_JSON_CHARSET_UTF_8).with(httpBasic(USERNAME, PASSWORD))
						.content(String.format(
								"{ \"id\":\"-1\", \"connexionType\": \"0\" ,\"login\": \"%s\",\"ROLE_USER\":\"on\", \"password\": \"%s\",\"repeatPassword\": \"%s\",\"firstName\": \"%s\",\"lastName\": \"%s\",\"email\": \"%s\",\"locale\": \"%s\"}",
								"user", "user.Pass-20", "user.Pass-20", "First name", "Last name", "email@itrust.lu", "fr")))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(content().string("{}"));
		Session session = null;
		try {
			session = sessionFactory.openSession();
			put(USER_TO_DELETE_ID, new DAOUserHBM(session).get("user").getId());
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
	}

	@Test
	public void test_01_AddCustomer() throws Exception {
		this.mockMvc
				.perform(post("/KnowledgeBase/Customer/Save").with(httpBasic(USERNAME, PASSWORD)).with(csrf()).accept(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format(
								"{\"id\":\"-1\", \"organisation\":\"%s\", \"contactPerson\":\"%s\", \"phoneNumber\":\"%s\", \"email\":\"%s\", \"address\":\"%s\", \"city\":\"%s\", \"ZIPCode\":\"%s\", \"country\":\"%s\"}",
								CUSTOMER_NAME, CUSTOMER_NAME, CUSTOMER_PHONE, CUSTOMER_EMAIL, CUSTOMER_NAME, CUSTOMER_NAME, CUSTOMER_CITY, CUSTOMER_NAME))
						.with(httpBasic(USERNAME, PASSWORD)))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andExpect(content().string("{}"));
		Session session = null;
		try {
			session = sessionFactory.openSession();
			put(CUSTOMER_TO_DELETE_ID, new DAOCustomerHBM(session).getFromContactPerson(CUSTOMER_NAME).getId());
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
	}

	@Test(dependsOnMethods = "test_00_addUser")
	public void test_02_AddCustomerUser() throws Exception {
		this.mockMvc
				.perform(post(String.format("/KnowledgeBase/Customer/%d/Manage-access/Update", getInteger(CUSTOMER_TO_DELETE_ID))).with(httpBasic(USERNAME, PASSWORD)).with(csrf())
						.contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"%d\" : \"true\", \"%d\" : \"true\"}", getInteger(USER_TO_DELETE_ID), getInteger(USER_TO_DELETE_ID)))
						.with(httpBasic(USERNAME, PASSWORD)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_01_AddCustomer")
	@Transactional(readOnly = true)
	public void test_03_SwitchCustomer() throws Exception {
		this.mockMvc
				.perform(get(String.format("/Admin/Analysis/%d/Switch/Customer", getInteger(CUSTOMER_TO_DELETE_ID))).with(httpBasic(USERNAME, PASSWORD)).with(csrf())
						.accept(APPLICATION_JSON_CHARSET_UTF_8).with(httpBasic(USERNAME, PASSWORD)))
				.andExpect(status().isOk()).andExpect(view().name("admin/analysis/switch-customer"));
	}

	@Test(dependsOnMethods = "test_03_SwitchCustomer")
	@Transactional(readOnly = true)
	public void test_04_SwitchCustomer() throws Exception {
		this.mockMvc
				.perform(post(String.format("/Admin/Analysis/%d/Switch/Customer/%d", getInteger(SIMPLE_ANALYSIS_V0_0_1_ID), getInteger(CUSTOMER_TO_DELETE_ID)))
						.contentType(APPLICATION_JSON_CHARSET_UTF_8).with(httpBasic(USERNAME, PASSWORD)).with(csrf()).with(httpBasic(USERNAME, PASSWORD)))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_00_addUser")
	@Transactional(readOnly = true)
	public void test_05_SwitchOwner() throws Exception {
		this.mockMvc
				.perform(get(String.format("/Admin/Analysis/%d/Switch/Owner", getInteger(CUSTOMER_TO_DELETE_ID))).with(httpBasic(USERNAME, PASSWORD)).with(csrf())
						.accept(APPLICATION_JSON_CHARSET_UTF_8).with(httpBasic(USERNAME, PASSWORD)))
				.andExpect(status().isOk()).andExpect(view().name("admin/analysis/switch-owner"));
	}

	@Test(dependsOnMethods = "test_05_SwitchOwner")
	@Transactional(readOnly = true)
	public void test_06_SwitchOwner() throws Exception {
		this.mockMvc
				.perform(post(String.format("/Admin/Analysis/%d/Switch/Owner/%d", getInteger(SIMPLE_ANALYSIS_V0_0_1_ID), getInteger(USER_TO_DELETE_ID)))
						.contentType(APPLICATION_JSON_CHARSET_UTF_8).with(httpBasic(USERNAME, PASSWORD)).with(csrf()).with(httpBasic(USERNAME, PASSWORD)))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = { "test_02_AddCustomerUser", "test_04_SwitchCustomer" })
	public void test_07_DeleteCustomer() throws Exception {
		this.mockMvc
				.perform(post("/KnowledgeBase/Customer/Delete/" + getInteger(CUSTOMER_TO_DELETE_ID)).with(httpBasic(USERNAME, PASSWORD)).with(csrf())
						.accept(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_00_addUser")
	public void test_08_ManageAnalysisAccessView() throws Exception {
		this.mockMvc.perform(get(String.format("/Admin/Analysis/%d/ManageAccess", getInteger(SIMPLE_ANALYSIS_V0_0_1_ID))).with(httpBasic(USERNAME, PASSWORD)).with(csrf())
				.with(httpBasic(USERNAME, PASSWORD))).andExpect(status().isOk()).andExpect(view().name("analyses/all/forms/rights"));
	}

	@Test(dependsOnMethods = "test_08_ManageAnalysisAccessView")
	public void test_09_ManageAnalysisAccessSave() throws Exception {
		this.mockMvc
				.perform(post("/Admin/Analysis/ManageAccess/Update").content(String.format("{\"analysisId\":\"%d\",\"userRights\":{\"%d\":{\"oldRight\":\"ALL\"}}}",
						getInteger(SIMPLE_ANALYSIS_V0_0_1_ID), getInteger(USER_TO_DELETE_ID))).contentType(APPLICATION_JSON_CHARSET_UTF_8).with(httpBasic(USERNAME, PASSWORD))
						.with(csrf()))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(jsonPath("$.success").exists());
	}

	@Test
	public void test_10_Home() throws Exception {
		this.mockMvc
				.perform(post(String.format("/Admin", getInteger(SIMPLE_ANALYSIS_V0_0_1_ID))).contentType(APPLICATION_JSON_CHARSET_UTF_8).with(httpBasic(USERNAME, PASSWORD))
						.with(csrf()).with(httpBasic(USERNAME, PASSWORD)))
				.andExpect(status().isOk()).andExpect(view().name("admin/administration"))
				.andExpect(model().attributeExists("tsSettings", "logFilter", "logLevels", "logTypes", "actions", "authors"));
	}

	@Test
	public void test_11_UpdateTSSettings() throws Exception {
		this.mockMvc
				.perform(post("/Admin/TSSetting/Update").with(httpBasic(USERNAME, PASSWORD)).with(csrf()).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"name\" : \"%s\", \"value\" : \"false\"}", TSSettingName.SETTING_ALLOWED_SIGNUP)).with(httpBasic(USERNAME, PASSWORD)))
				.andExpect(status().isOk()).andExpect(content().string("true"));
		this.mockMvc
				.perform(post("/Admin/TSSetting/Update").with(httpBasic(USERNAME, PASSWORD)).with(csrf()).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"name\" : \"%s\", \"value\" : \"false\"}", TSSettingName.SETTING_ALLOWED_SIGNUP)).with(httpBasic(USERNAME, PASSWORD)))
				.andExpect(status().isOk()).andExpect(content().string("true"));
	}

	@Test(dependsOnMethods = { "test_02_AddCustomerUser", "test_06_SwitchOwner", "test_09_ManageAnalysisAccessSave" })
	public void test_11_deleteUser() throws Exception {
		this.mockMvc
				.perform(post("/Admin/User/Delete").with(csrf()).contentType(APPLICATION_JSON_CHARSET_UTF_8).with(httpBasic(USERNAME, PASSWORD))
						.content(String.format("{\"idUser\":%d, \"switchOwners\": {}, \"deleteAnalysis\":[] }", getInteger(USER_TO_DELETE_ID))))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_11_deleteUser")
	public void test_12_UpdateLogsSettings() throws Exception {
		this.mockMvc
				.perform(post("/Admin/Log/Filter/Update").with(httpBasic(USERNAME, PASSWORD)).with(csrf()).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"type\" : \"%s\", \"level\" : \"%s\", \"action\" : \"%s\", \"author\" : \"%s\",\"direction\" : \"%s\", \"size\" : %d}",
								LogType.ADMINISTRATION, LogLevel.WARNING, LogAction.DELETE, USERNAME, "desc", 200))
						.with(httpBasic(USERNAME, PASSWORD)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_12_UpdateLogsSettings")
	public void test_12_LoadLogsSection() throws Exception {
		this.mockMvc
				.perform(get("/Admin/Log/Section").contentType(APPLICATION_JSON_CHARSET_UTF_8).with(httpBasic(USERNAME, PASSWORD)).with(csrf()).with(httpBasic(USERNAME, PASSWORD)))
				.andExpect(status().isOk()).andExpect(view().name("admin/log/section")).andExpect(model().attributeExists("trickLogs"));
	}
}
