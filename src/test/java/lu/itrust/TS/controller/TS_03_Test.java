/**
 * 
 */
package lu.itrust.TS.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.Assert.isNull;
import static org.springframework.util.Assert.notNull;
import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author eomar
 *
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TS_03_Test extends SpringTestConfiguration {

	private static final String SIMPLE_ANALYSIS_VERSION = "0.0.1";

	private static final String SIMPLE_ANALYSIS_NAME = "simple-analysis";

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	protected static String TASK_ID = null;

	protected static int LANGUAGE_ID = -1;

	protected static int CUSTOMER_ID = -1;

	protected static int ANALYSIS_ID = -1;

	@Test
	@Transactional(readOnly = true)
	public void test_00_loadData() throws Exception {
		Language language = serviceLanguage.getByAlpha3("FRA");
		notNull(language, "French language cannot be found");
		LANGUAGE_ID = language.getId();
		Customer customer = serviceCustomer.getFromContactPerson("me");
		notNull(customer, "'me' customer cannot be found");
		CUSTOMER_ID = customer.getId();
	}

	@Test
	public void test_01_CreateSimpleAnalysis() throws Exception {
		this.mockMvc
				.perform(
						post("/Analysis/Build/Save").with(csrf()).with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8).param("author", "Admin Admin")
								.param("name", SIMPLE_ANALYSIS_NAME).param("version", SIMPLE_ANALYSIS_VERSION).param("comment", "comment")
								.param("customer", String.valueOf(CUSTOMER_ID)).param("language", String.valueOf(LANGUAGE_ID))).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").exists());

	}

	@Test
	@Transactional(readOnly = true)
	public void test_02_SelectAnalysis() throws Exception {
		Analysis analysis = serviceAnalysis.getByCustomerAndLabelAndVersion(CUSTOMER_ID, SIMPLE_ANALYSIS_NAME, SIMPLE_ANALYSIS_VERSION);
		notNull(analysis, "Analysis cannot be found");
		ANALYSIS_ID = analysis.getId();
		this.mockMvc.perform(get(String.format("/Analysis/%d/Select", ANALYSIS_ID)).with(csrf()).with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(forwardedUrl("analyses/single/home"));
	}

	@Test
	public void test_03_DisplayAll() throws Exception {
		this.mockMvc.perform(get(String.format("/Analysis")).with(csrf()).with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isFound())
				.andExpect(redirectedUrl("/Analysis/All"));
	}

	@Test
	@Transactional(readOnly = true)
	public void test_04_CreateCustom() throws Exception {
		Analysis analysis = serviceAnalysis.get(ANALYSIS_ID);
		notNull(analysis, "Analysis cannot be found");
	}

	@Test(timeout = 30000)
	public synchronized void test_05_CreateVersion() throws Exception {
		TASK_ID = new ObjectMapper()
				.readTree(
						this.mockMvc
								.perform(
										post(String.format("/Analysis/Duplicate/%d", ANALYSIS_ID)).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
												.accept(APPLICATION_JSON_CHARSET_UTF_8)
												.content(String.format("{\"author\":\"%s\", \"version\":\"%s\", \"comment\":\"%s\"}", "Admin Admin", "0.0.2", "comment")))
								.andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).findValue("analysis_task_id").asText("");

		Worker worker = workersPoolManager.get(TASK_ID);

		notNull(worker, "Worker cannot be found");

		while (worker.isWorking())
			wait(100);
		
		isNull(worker.getError(), "Error should be null");
		
		serviceTaskFeedback.unregisterTask(USERNAME, TASK_ID);
	}

}
