/**
 * 
 */
package lu.itrust.TS.controller;

import static lu.itrust.TS.helper.TestSharingData.*;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.util.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static lu.itrust.TS.controller.TS_03_CreateAnAnlysis.*;

import java.util.List;
import java.util.stream.Collectors;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceStandard;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.standard.Standard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author eomar
 *
 */
@Test(groups = "KnowledgeBase", dependsOnGroups = "Installation")
public class TS_06_KnowledgeBase extends SpringTestConfiguration {

	private static final String ANALYSIS_PROFILE_ID = "analysis-profile-test-profile-id";

	private static final String PROFILE_STANDARD_LIST = "profile-standard-list";

	private static final String CUSTOMER_MEME_ID = "customer_meme_id";

	private static final String LANGUAGE_DEU_ID = "language_deu_id";

	private static final String LANGUAGE_DEU_ALT_NAME = "Allemand";

	private static final String LANGUAGE_DEU_NAME = "Deutsch";

	private static final String LANGUAGE_DEU_ALPHA_3 = "DEU";

	private static final String CUSTOMER_EMAIL = "meme@me.me";

	private static final String CUSTOMER_NAME = "meme";

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceStandard serviceStandard;
	
	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Test
	public void test_00_Show27001Standard() throws Exception {
		this.mockMvc.perform(get("/KnowledgeBase/Standard/1/Language/1/Measures").with(csrf()).with(httpBasic(USERNAME, PASSWORD))).andExpect(status().isOk())
				.andExpect(view().name("knowledgebase/standards/measure/measures"))
				.andExpect(model().attributeExists("selectedLanguage", "languages", "standard", "measureDescriptions"));
	}

	@Test
	public void test_00_CreateLanguageAndCustomer() throws Exception {
		this.mockMvc
				.perform(
						post("/KnowledgeBase/Customer/Save")
								.with(httpBasic(USERNAME, PASSWORD))
								.with(csrf())
								.accept(APPLICATION_JSON_CHARSET_UTF_8)
								.content(
										String.format(
												"{\"id\":\"-1\", \"organisation\":\"%s\", \"contactPerson\":\"%s\", \"phoneNumber\":\"%s\", \"email\":\"%s\", \"address\":\"%s\", \"city\":\"%s\", \"ZIPCode\":\"%s\", \"country\":\"%s\"}",
												CUSTOMER_NAME, CUSTOMER_NAME, CUSTOMER_NAME, CUSTOMER_EMAIL, CUSTOMER_NAME, CUSTOMER_NAME, CUSTOMER_NAME, CUSTOMER_NAME)))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andExpect(content().string("{}"));

		this.mockMvc
				.perform(
						post("/KnowledgeBase/Language/Save")
								.with(httpBasic(USERNAME, PASSWORD))
								.with(csrf())
								.accept(APPLICATION_JSON_CHARSET_UTF_8)
								.content(
										String.format("{\"id\":\"-1\", \"alpha3\":\"%s\", \"name\":\"%s\",\"altName\":\"%s\"}", LANGUAGE_DEU_ALPHA_3, LANGUAGE_DEU_NAME,
												LANGUAGE_DEU_ALT_NAME))).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk())
				.andExpect(content().string("{}"));
	}

	@Test(dependsOnMethods = "test_00_CreateLanguageAndCustomer")
	@Transactional(readOnly = true)
	public void test_00_LoadCustomerIdAndLanguageId() throws Exception {
		Customer customer = serviceCustomer.getFromContactPerson(CUSTOMER_NAME);
		Language language = serviceLanguage.getByAlpha3(LANGUAGE_DEU_ALPHA_3);
		notNull(customer, "Customer 'meme' cannot be found");
		notNull(language, "Language 'deu' cannot be found");
		put(LANGUAGE_DEU_ID, language.getId());
		put(CUSTOMER_MEME_ID, customer.getId());
	}

	@Test(dependsOnMethods = "test_00_LoadCustomerIdAndLanguageId")
	public void test_01_EditCustomer() throws Exception {
		Integer idCustomer = getInteger(CUSTOMER_MEME_ID);
		notNull(idCustomer, "Customer id cannot be found");
		this.mockMvc
				.perform(
						post("/KnowledgeBase/Customer/Save")
								.with(httpBasic(USERNAME, PASSWORD))
								.with(csrf())
								.accept(APPLICATION_JSON_CHARSET_UTF_8)
								.content(
										String.format(
												"{\"id\":%d, \"organisation\":\"%s\", \"contactPerson\":\"%s\", \"phoneNumber\":\"%s\", \"email\":\"%s\", \"address\":\"%s\", \"city\":\"%s\", \"ZIPCode\":\"%s\", \"country\":\"%s\"}",
												idCustomer, "meme compagny", CUSTOMER_NAME, CUSTOMER_NAME, CUSTOMER_EMAIL, CUSTOMER_NAME, CUSTOMER_NAME, CUSTOMER_NAME,
												CUSTOMER_NAME))).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk())
				.andExpect(content().string("{}"));
	}

	@Test(dependsOnMethods = "test_00_LoadCustomerIdAndLanguageId")
	public void test_02_EditLanguage() throws Exception {
		Integer idLanguage = getInteger(LANGUAGE_DEU_ID);
		notNull(idLanguage, "Language id cannot be found");
		this.mockMvc
				.perform(
						post("/KnowledgeBase/Language/Save")
								.with(httpBasic(USERNAME, PASSWORD))
								.with(csrf())
								.accept(APPLICATION_JSON_CHARSET_UTF_8)
								.content(
										String.format("{\"id\":%d, \"alpha3\":\"%s\", \"name\":\"%s\",\"altName\":\"%s\"}", idLanguage, LANGUAGE_DEU_ALPHA_3, LANGUAGE_DEU_NAME,
												"German"))).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk())
				.andExpect(content().string("{}"));
	}

	@Test(dependsOnMethods = { "test_01_EditCustomer", "test_02_EditLanguage" })
	@Transactional(readOnly = true)
	public void test_05_CheckUpdateCustomerAndLanguage() throws Exception {
		Customer customer = serviceCustomer.get(getInteger(CUSTOMER_MEME_ID));
		Language language = serviceLanguage.get(getInteger(LANGUAGE_DEU_ID));
		notNull(customer, "Customer cannot be found");
		notNull(language, "Language cannot be found");
		assertEquals("Customer has not been updated", "meme compagny", customer.getOrganisation());
		assertEquals("Language has not been updated", "German", language.getAltName());
	}

	@Test(dependsOnGroups = "CreateAnalysis")
	@Transactional(readOnly=true)
	public void test_05_LoadAnalysisStandard() throws Exception {
		String standards = "";
		for (Standard standard : serviceStandard.getAllFromAnalysis(ANALYSIS_ID))
			standards = String.format("%s%s\"standard_%d\":\"true\"", standards, (standards.isEmpty() ? "" : ","), standard.getId());
		assertFalse("Standard not be empty", standards.isEmpty());
		put(PROFILE_STANDARD_LIST, standards);
	}

	@Test(dependsOnMethods = "test_05_LoadAnalysisStandard",timeOut=120000)
	public synchronized void test_03_CreateProfile() throws Exception {
		String standards = getString(PROFILE_STANDARD_LIST);
		notNull(standards, "Standards cannot be found");
		JsonNode node = new ObjectMapper().readTree(this.mockMvc
				.perform(
						post("/AnalysisProfile/Save").with(httpBasic(USERNAME, PASSWORD)).with(csrf()).accept(APPLICATION_JSON_CHARSET_UTF_8)
								.content(String.format("{\"id\":%d, %s, \"description\":\"%s\"}",ANALYSIS_ID, standards, "test profile")))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andExpect(jsonPath("$.taskid").exists()).andReturn().getResponse()
				.getContentAsString());
		String idTask = node.get("taskid").asText(null);
		notNull(idTask, "Task id cannot be found");
		Worker worker = workersPoolManager.get(idTask);
		notNull(worker, "Worker cannot be found");
		while(worker.isWorking())
			wait(100);
		isNull(worker.getError(), "An unknown error occurred while create analysis profile");
	}
	
	@Test(dependsOnMethods="test_03_CreateProfile")
	@Transactional(readOnly=true)
	public void test_03_LoadData() {
		Analysis analysis = serviceAnalysis.getProfileByName("test profile");
		notNull(analysis, "Analysis profile cannot be found");
		isTrue(analysis.isProfile(), "Selected analysis is not a profile");
		put(ANALYSIS_PROFILE_ID, analysis.getId());
	}

	@Test(dependsOnMethods = { "test_05_CheckUpdateCustomerAndLanguage", "test_03_LoadData" })
	public void test_04_CreateAnalysisUsedCustomerLanguageAndProfile() throws Exception {
		Integer idProfile = getInteger(ANALYSIS_PROFILE_ID);
		notNull(idProfile, "Profile analysis cannot be found");
		this.mockMvc
		.perform(
				post("/Analysis/Build/Save").with(csrf()).with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8).param("author", "Admin Admin")
						.param("name", "test analysis from test profile").param("version", SIMPLE_ANALYSIS_VERSION).param("comment", "comment")
						.param("customer", getStringValue(CUSTOMER_MEME_ID)).param("language", getStringValue(LANGUAGE_DEU_ID))).andExpect(status().isOk())
		.andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_04_CreateAnalysisUsedCustomerLanguageAndProfile")
	public void test_05_DeleteUsedCustomerAndLanguageAndProfile() {
		throw new IllegalArgumentException("Not implemented test");
	}

	@Test(dependsOnMethods = "test_05_DeleteUsedCustomerAndLanguageAndProfile")
	public void test_06_DeleteAnalysisUsedLanguageAndCustomer() {
		throw new IllegalArgumentException("Not implemented test");
	}

	@Test(dependsOnMethods = "test_06_DeleteAnalysisUsedLanguageAndCustomer")
	public void test_07_DeleteCustomerAndLanguage() {
		throw new IllegalArgumentException("Not implemented test");
	}

	@Test
	public void test_08_CreateStandard() {
		throw new IllegalArgumentException("Not implemented test");
	}

	@Test(dependsOnMethods = "test_08_CreateStandard")
	public void test_09_CreateMeasure() {
		throw new IllegalArgumentException("Not implemented test");
	}

	@Test(dependsOnGroups = "CreateAnalysis", dependsOnMethods = "test_09_CreateMeasure")
	public void test_10_CreateAnalysisUseStandard() {
		throw new IllegalArgumentException("Not implemented test");
	}

	@Test(dependsOnMethods = "test_08_CreateStandard")
	public void test_10_EditStandard() {
		throw new IllegalArgumentException("Not implemented test");
	}

	@Test(dependsOnMethods = "test_10_CreateAnalysisUseStandard")
	public void test_10_DeleteUsedStandard() {
		throw new IllegalArgumentException("Not implemented test");
	}

	@Test(dependsOnMethods = "test_10_DeleteUsedStandard")
	public void test_11_DeleteAnalysisAndStandard() {
		throw new IllegalArgumentException("Not implemented test");
	}

	@Test
	public void test_12_DownloadStandardTemplate() {
		throw new IllegalArgumentException("Not implemented test");
	}

	@Test
	public void test_13_ImportStandard() {
		throw new IllegalArgumentException("Not implemented test");
	}

	@Test
	public void test_14_ExportStandard() {
		throw new IllegalArgumentException("Not implemented test");
	}
}
