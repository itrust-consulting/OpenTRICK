package lu.itrust.TS.controller;

import static lu.itrust.TS.helper.TestSharingData.put;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.Assert.isNull;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.model.TrickService;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;

@Test(groups = "Installation", dependsOnGroups = "firstAccount")
public class TS_02_InstallApplication extends SpringTestConfiguration {

	public static final String PROFILE_CUSTOMER = "profile-customer";

	public static final String ME_CUSTOMER = "me-customer";

	private static final String FRA_FRENCH = "French";

	private static final String FRA_FRANÇAIS = "Français";

	private static final String FRA_ALPHA_3 = "FRA";

	private static final String CUSTOMER_OTHER_FIELDS = "me";

	private static final String CUSTOMER_EMAIL = "me@me.me";

	private static String INSTALL_TASK_ID;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	/**
	 * Properties
	 */
	@Value("${app.settings.version}")
	private String version;

	@Test
	public void test_00_Install() throws Exception {
		INSTALL_TASK_ID = new ObjectMapper().readTree(this.mockMvc.perform(post("/Install").with(httpBasic(USERNAME, PASSWORD)).with(csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.idTask").exists()).andReturn().getResponse().getContentAsString()).findValue("idTask").asText(null);
	}

	@Test(timeOut = 120000)
	public synchronized void test_01_WaitForWorker() throws InterruptedException {
		Worker worker = workersPoolManager.get(INSTALL_TASK_ID);
		notNull(worker, "Installation worker cannot be found");
		while (worker.isWorking())
			wait(100);
		serviceTaskFeedback.unregisterTask(USERNAME, INSTALL_TASK_ID);
		isNull(worker.getError(), "Installation failed");
	}

	@Test
	@Transactional(readOnly = true)
	public void test_04_CheckForInstallVersion() throws Exception {
		TrickService trickService = serviceTrickService.getStatus();
		notNull(trickService, "TRICK Service installation cannot be found");
		assertEquals("Versions do not match", version, trickService.getVersion());
	}

	@Test
	@Transactional(readOnly = true)
	public void test_05_CheckCustomerProfile() throws Exception {
		Customer customer = serviceCustomer.getProfile();
		notNull(customer, "Profile customer cannot be found");
		put(PROFILE_CUSTOMER, customer.getId());
	}

	@Test
	@Transactional(readOnly = true)
	public void test_06_CheckDefaultLangauge() throws Exception {
		Language language = serviceLanguage.get(1);
		notNull(language, "Language with id '1' cannot be found");
	}

	@Test
	@Transactional(readOnly = true)
	public void test_07_CheckAnalysisProfile() throws Exception {
		List<Analysis> analyses = serviceAnalysis.getDefaultProfiles();
		notNull(analyses, "Default Analysis profile cannot be found");
		analyses.forEach(analysis-> {
			isTrue(analysis.isProfile(), "Analysis should be a profile");
			isTrue(analysis.isDefaultProfile(), "Analysis should be default profile");
		});
	
	}

	@Test
	public void test_08_CreateCustomer() throws UnsupportedEncodingException, Exception {
		this.mockMvc
				.perform(post("/KnowledgeBase/Customer/Save").with(httpBasic(USERNAME, PASSWORD)).with(csrf()).accept(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format(
								"{\"id\":\"-1\", \"organisation\":\"%s\", \"contactPerson\":\"%s\", \"phoneNumber\":\"%s\", \"email\":\"%s\", \"address\":\"%s\", \"city\":\"%s\", \"ZIPCode\":\"%s\", \"country\":\"%s\"}",
								CUSTOMER_OTHER_FIELDS, CUSTOMER_OTHER_FIELDS, CUSTOMER_OTHER_FIELDS, CUSTOMER_EMAIL, CUSTOMER_OTHER_FIELDS, CUSTOMER_OTHER_FIELDS,
								CUSTOMER_OTHER_FIELDS, CUSTOMER_OTHER_FIELDS))
						.with(httpBasic(USERNAME, PASSWORD)))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andExpect(content().string("{}"));
	}

	@Test
	@Transactional(readOnly = true)
	public void test_09_LoadCustomer() throws Exception {
		Customer customer = serviceCustomer.getFromContactPerson(CUSTOMER_OTHER_FIELDS);
		notNull(customer, "Customer cannot be found");
		put(ME_CUSTOMER, customer.getId());
	}

	@Test
	public void test_10_AddFRLanguage() throws Exception {
		this.mockMvc
				.perform(post("/KnowledgeBase/Language/Save").with(httpBasic(USERNAME, PASSWORD)).with(csrf()).accept(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":\"-1\", \"alpha3\":\"%s\", \"name\":\"%s\",\"altName\":\"%s\"}", FRA_ALPHA_3, FRA_FRANÇAIS, FRA_FRENCH))
						.with(httpBasic(USERNAME, PASSWORD)))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andExpect(content().string("{}"));
	}

	@Test
	@Transactional(readOnly = true)
	public void test_11_LoadLanguage() throws Exception {
		Language language = serviceLanguage.getByAlpha3(FRA_ALPHA_3);
		notNull(language, "Language with alpha3: FRA cannot be found");
	}
}
