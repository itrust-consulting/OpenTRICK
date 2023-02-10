/**
 * 
 */
package lu.itrust.TS.controller;

import static lu.itrust.TS.controller.TS_03_CreateAnAnlysis.ANALYSIS_ID;
import static lu.itrust.TS.helper.TestSharingData.getInteger;
import static lu.itrust.TS.helper.TestSharingData.getString;
import static lu.itrust.TS.helper.TestSharingData.getStringValue;
import static lu.itrust.TS.helper.TestSharingData.put;
import static lu.itrust.business.TS.constants.Constant.SELECTED_ANALYSIS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.util.Assert.isNull;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.TS.helper.TestConstant;
import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerImportStandard;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.hbm.DAOLanguageHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOStandardHBM;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceStandard;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.standard.Standard;

/**
 * @author eomar
 *
 */
@Test(groups = "KnowledgeBase", dependsOnGroups = "CreateAnalysis")
public class TS_06_KnowledgeBase extends SpringTestConfiguration {

	private static final String APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8 = "application/x-www-form-urlencoded;charset=UTF-8";

	private static final String STANDARD_FOR_TEST = "Standard for test";

	private static final String TEST_STANDARD = "Test standard";

	private static final String TEST_STANDARD_ID = "Test-standard-id";

	private static final String TEST_ANALYSIS_FROM_TEST_PROFILE = "test analysis from test profile";

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

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private ResourceLoader resourceLoader;

	@Value("${app.settings.test.standard.template.path}")
	private String template;

	@Value("${app.settings.test.knownledge.base.standard.import}")
	private String importStandard;

	@Value("${app.settings.version}${app.settings.version.revision}")
	private String appVersion;

	@Test
	public void test_00_Show27001Standard() throws Exception {
		this.mockMvc
				.perform(get("/KnowledgeBase/Standard/1/Language/1/Measures").with(csrf())
						.with(httpBasic(USERNAME, PASSWORD)))
				.andExpect(status().isOk())
				.andExpect(view().name("knowledgebase/standards/measure/section"))
				.andExpect(model().attributeExists("selectedLanguage", "languages", "standard", "measureDescriptions"));
	}

	@Test
	public void test_00_CreateLanguageAndCustomer() throws Exception {
		this.mockMvc.perform(post("/KnowledgeBase/Customer/Save").with(httpBasic(USERNAME, PASSWORD)).with(csrf())
				.contentType(APPLICATION_JSON_CHARSET_UTF_8)
				.accept(APPLICATION_JSON_CHARSET_UTF_8)
				.content(String.format(
						"{\"id\":\"-1\", \"organisation\":\"%s\", \"contactPerson\":\"%s\", \"phoneNumber\":\"%s\", \"email\":\"%s\", \"address\":\"%s\", \"city\":\"%s\", \"zipCode\":\"%s\", \"country\":\"%s\"}",
						CUSTOMER_NAME, CUSTOMER_NAME, CUSTOMER_NAME, CUSTOMER_EMAIL, CUSTOMER_NAME, CUSTOMER_NAME,
						CUSTOMER_NAME, CUSTOMER_NAME)))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk())
				.andExpect(content().string("{}"));

		this.mockMvc
				.perform(post("/KnowledgeBase/Language/Save").with(httpBasic(USERNAME, PASSWORD)).with(csrf())
						.accept(APPLICATION_JSON_CHARSET_UTF_8).content(
								String.format("{\"id\":\"-1\", \"alpha3\":\"%s\", \"name\":\"%s\",\"altName\":\"%s\"}",
										LANGUAGE_DEU_ALPHA_3, LANGUAGE_DEU_NAME, LANGUAGE_DEU_ALT_NAME)))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk())
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
		this.mockMvc.perform(post("/KnowledgeBase/Customer/Save").with(httpBasic(USERNAME, PASSWORD)).with(csrf())
				.contentType(APPLICATION_JSON_CHARSET_UTF_8)
				.accept(APPLICATION_JSON_CHARSET_UTF_8)
				.content(String.format(
						"{\"id\":%d, \"organisation\":\"%s\", \"contactPerson\":\"%s\", \"phoneNumber\":\"%s\", \"email\":\"%s\", \"address\":\"%s\", \"city\":\"%s\", \"zipCode\":\"%s\", \"country\":\"%s\"}",
						idCustomer, "meme compagny", CUSTOMER_NAME, CUSTOMER_NAME, CUSTOMER_EMAIL, CUSTOMER_NAME,
						CUSTOMER_NAME, CUSTOMER_NAME, CUSTOMER_NAME)))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk())
				.andExpect(content().string("{}"));
	}

	@Test(dependsOnMethods = "test_00_LoadCustomerIdAndLanguageId")
	public void test_02_EditLanguage() throws Exception {
		Integer idLanguage = getInteger(LANGUAGE_DEU_ID);
		notNull(idLanguage, "Language id cannot be found");
		this.mockMvc
				.perform(post("/KnowledgeBase/Language/Save").with(httpBasic(USERNAME, PASSWORD)).with(csrf())
						.accept(APPLICATION_JSON_CHARSET_UTF_8).content(
								String.format("{\"id\":%d, \"alpha3\":\"%s\", \"name\":\"%s\",\"altName\":\"%s\"}",
										idLanguage, LANGUAGE_DEU_ALPHA_3, LANGUAGE_DEU_NAME, "German")))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk())
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
	@Transactional(readOnly = true)
	public void test_05_LoadAnalysisStandard() throws Exception {
		String standards = "";
		for (Standard standard : serviceStandard.getAllFromAnalysis(ANALYSIS_ID))
			standards += (standards.isEmpty() ? "" : ",") + String.format("\"%d\":\"true\"", standard.getId());
		assertFalse("Standard not be empty", standards.isEmpty());
		put(PROFILE_STANDARD_LIST, standards);
	}

	@Test(dependsOnMethods = "test_05_LoadAnalysisStandard", timeOut = 120000)
	public synchronized void test_03_CreateProfile() throws Exception {
		String standards = getString(PROFILE_STANDARD_LIST);
		notNull(standards, "Standards cannot be found");
		JsonNode node = new ObjectMapper().readTree(this.mockMvc
				.perform(post(String.format("/AnalysisProfile/Analysis/%d/Save", ANALYSIS_ID))
						.with(httpBasic(USERNAME, PASSWORD)).with(csrf())
						.contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{%s, \"description\":\"%s\"}", standards, "test profile")))
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk())
				.andExpect(jsonPath("$.taskid").exists()).andReturn().getResponse()
				.getContentAsString());
		String idTask = node.get("taskid").asText(null);
		notNull(idTask, "Task id cannot be found");

		Worker worker = null;

		for (int i = 0; i < 30; i++) {
			worker = workersPoolManager.get(idTask);
			if (worker == null)
				wait(1000);
		}

		notNull(worker, "Worker cannot be found");
		while (worker.isWorking())
			wait(100);
		serviceTaskFeedback.unregisterTask(USERNAME, worker.getId());
		isNull(worker.getError(), "An unknown error occurred while create analysis profile");
	}

	@Test(dependsOnMethods = "test_03_CreateProfile")
	@Transactional(readOnly = true)
	public void test_03_LoadData() {
		Analysis analysis = serviceAnalysis.getProfileByName("test profile");
		notNull(analysis, "Analysis profile cannot be found");
		isTrue(analysis.isProfile(), "Selected analysis is not a profile");
		put(ANALYSIS_PROFILE_ID, analysis.getId());
	}

	@Test(dependsOnMethods = "test_03_LoadData")
	public void test_03_AddAssetToProfile() throws Exception {
		this.mockMvc.perform(post("/Analysis/Asset/Save").with(csrf()).with(httpBasic(USERNAME, PASSWORD))
				.accept(APPLICATION_JSON_CHARSET_UTF_8)
				.sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT)
				.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_PROFILE_ID))
				.content(String.format(
						"{\"id\":\"-1\", \"name\":\"%s\" ,\"assetType\": {\"id\": \"%d\" }, \"value\": \"%s\", \"selected\":\"%s\", \"comment\":\"%s\", \"hiddenComment\":\"%s\"}",
						"Trick service", 1, "687,688", false, "comment", "hiddenComment")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors.asset").exists());

	}

	@Test(dependsOnMethods = { "test_05_CheckUpdateCustomerAndLanguage", "test_03_LoadData", "test_03_LoadData" })
	public void test_04_CreateAnalysisUsedCustomerLanguageAndProfile() throws Exception {
		Integer idProfile = getInteger(ANALYSIS_PROFILE_ID);
		notNull(idProfile, "Profile analysis cannot be found");
		this.mockMvc
				.perform(post("/Analysis/Build/Save").with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.contentType(APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8)
						.param("author", "Admin Admin").param("name", TEST_ANALYSIS_FROM_TEST_PROFILE)
						.param("version", TestConstant.SIMPLE_ANALYSIS_VERSION)
						.param("comment", "comment").param("customer", getStringValue(CUSTOMER_MEME_ID))
						.param("language", getStringValue(LANGUAGE_DEU_ID))
						.param("type", AnalysisType.QUANTITATIVE.name()).param("profile", idProfile + ""))

				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_04_CreateAnalysisUsedCustomerLanguageAndProfile")
	@Transactional(readOnly = true)
	public void test_04_LoadAnalysis() {
		Analysis analysis = serviceAnalysis.getByCustomerAndLabelAndVersion(getInteger(CUSTOMER_MEME_ID),
				TEST_ANALYSIS_FROM_TEST_PROFILE, TestConstant.SIMPLE_ANALYSIS_VERSION);
		notNull(analysis, "Analysis cannot be found");
		put(TEST_ANALYSIS_FROM_TEST_PROFILE, analysis.getId());
	}

	@Test(dependsOnMethods = "test_04_CreateAnalysisUsedCustomerLanguageAndProfile")
	public void test_05_DeleteUsedCustomerAndLanguageAndProfile() throws Exception {
		Integer idLanguage = getInteger(LANGUAGE_DEU_ID);
		Integer idCustomer = getInteger(CUSTOMER_MEME_ID);
		Integer idProfile = getInteger(ANALYSIS_PROFILE_ID);
		notNull(idLanguage, "Language id cannot be found");
		notNull(idCustomer, "Customer id cannot be found");
		notNull(idProfile, "Customer id cannot be found");
		this.mockMvc
				.perform(post("/KnowledgeBase/Language/Delete/" + idLanguage).with(csrf())
						.with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());

		this.mockMvc
				.perform(post("/KnowledgeBase/Customer/" + idCustomer + "/Delete").with(csrf())
						.with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());

		this.mockMvc
				.perform(post("/AnalysisProfile/Delete/" + idProfile).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.accept(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());

	}

	@Test
	public void test_06_CreateStandard() throws Exception {
		this.mockMvc.perform(post("/KnowledgeBase/Standard/Save").with(csrf()).with(httpBasic(USERNAME, PASSWORD))
				.accept(APPLICATION_JSON_CHARSET_UTF_8)
				.content(String.format(
						"{\"id\":\"-1\", \"label\":\"%s\",\"name\": \"%s\",\"description\": \"%s\", \"type\": \"%s\", \"version\":\"%d\", \"computable\": \"%s\"}",
						TEST_STANDARD, TEST_STANDARD, "test standard description", "NORMAL", 2015, "on")))
				.andExpect(status().isOk()).andExpect(content().string("{}"));
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Standard standard = new DAOStandardHBM(session).getStandardByLabelAndVersion(TEST_STANDARD, 2015);
			notNull(standard, "Standard cannot be found");
			put(TEST_STANDARD_ID, standard.getId());
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}

	}

	@Test(dependsOnMethods = "test_06_CreateStandard")
	public void test_07_CreateMeasure() throws Exception {
		Integer idStandard = getInteger(TEST_STANDARD_ID);
		notNull(idStandard, "Standard id cannot be found");
		Session session = null;
		try {
			session = sessionFactory.openSession();
			List<Language> languages = new DAOLanguageHBM(session).getAll();
			String domainAndDescription = "";
			for (Language language : languages) {
				domainAndDescription = String.format(
						"%s,\"domain_%d\":\"Domain %s\", \"description_%d\": \"Description %s\"", domainAndDescription,
						language.getId(),
						language.getName(), language.getId(), language.getName());
			}

			this.mockMvc
					.perform(post(String.format("/KnowledgeBase/Standard/%d/Measures/Save", idStandard,
							getInteger(LANGUAGE_DEU_ID))).with(csrf())
							.with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8)
							.content(String.format(
									"{\"id\":\"-1\",  \"reference\": \"%s\", \"level\":%d, \"computable\": \"%s\" %s}",
									"1", 1, "", domainAndDescription)))
					.andExpect(status().isOk()).andExpect(content().string("{}"));

			this.mockMvc
					.perform(post(String.format("/KnowledgeBase/Standard/%d/Measures/Save", idStandard,
							getInteger(LANGUAGE_DEU_ID))).with(csrf())
							.with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8)
							.content(String.format(
									"{\"id\":\"-1\",  \"reference\": \"%s\", \"level\":%d, \"computable\": \"%s\" %s}",
									"1.1", 2, "", domainAndDescription)))
					.andExpect(status().isOk()).andExpect(content().string("{}"));

			this.mockMvc
					.perform(
							post(String.format("/KnowledgeBase/Standard/%d/Measures/Save", idStandard,
									getInteger(LANGUAGE_DEU_ID))).with(csrf())
									.with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8)
									.content(String
											.format("{\"id\":\"-1\",  \"reference\": \"%s\", \"level\":%d, \"computable\": \"%s\" %s}",
													"1.1.1", 3, "on", domainAndDescription)))
					.andExpect(status().isOk()).andExpect(content().string("{}"));

		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
	}

	@Test(dependsOnMethods = { "test_07_CreateMeasure", "test_04_LoadAnalysis" })
	public void test_08_CreateAnalysisUseStandard() throws Exception {
		this.mockMvc
				.perform(post("/Analysis/Standard/Add/" + getInteger(TEST_STANDARD_ID))
						.sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT)
						.sessionAttr(SELECTED_ANALYSIS, getInteger(TEST_ANALYSIS_FROM_TEST_PROFILE)).with(csrf())
						.with(httpBasic(USERNAME, PASSWORD))
						.accept(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_08_CreateAnalysisUseStandard")
	public void test_09_EditStandard() throws Exception {
		this.mockMvc
				.perform(post("/KnowledgeBase/Standard/Save").with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.accept(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format(
								"{\"id\":%d, \"label\":\"%s\",\"name\": \"%s\", \"description\": \"%s\", \"type\": \"%s\", \"version\":\"%d\", \"computable\": \"%s\"}",
								getInteger(TEST_STANDARD_ID), TEST_STANDARD, TEST_STANDARD, "test standard description",
								"ASSET", 2016, "on")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.type").exists());

		this.mockMvc
				.perform(post("/KnowledgeBase/Standard/Save").with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.accept(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format(
								"{\"id\":%d, \"label\":\"%s\",\"name\":\"%s\", \"description\": \"%s\", \"type\": \"%s\", \"version\":\"%d\", \"computable\": \"%s\"}",
								getInteger(TEST_STANDARD_ID), TEST_STANDARD, TEST_STANDARD, "test standard description",
								"MATURITY", 2016, "on")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.type").exists());

		this.mockMvc
				.perform(post("/KnowledgeBase/Standard/Save").with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.accept(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format(
								"{\"id\":%d, \"label\":\"%s\",\"name\":\"%s\", \"description\": \"%s\", \"type\": \"%s\", \"version\":\"%d\", \"computable\": \"%s\"}",
								getInteger(TEST_STANDARD_ID), TEST_STANDARD, TEST_STANDARD, "test standard description",
								"NORMAL", 2016, "on")))
				.andExpect(status().isOk()).andExpect(content().string("{}"));
	}

	@Test(dependsOnMethods = { "test_05_DeleteUsedCustomerAndLanguageAndProfile", "test_09_EditStandard" })
	public void test_10_DeleteAnalysisAndStandardUsedLanguageAndCustomer() throws Exception {
		Integer idAnalysis = getInteger(TEST_ANALYSIS_FROM_TEST_PROFILE);
		notNull(idAnalysis, "Analysis id cannot be found");
		this.mockMvc
				.perform(post("/Analysis/Delete/" + idAnalysis).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.accept(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_10_DeleteAnalysisAndStandardUsedLanguageAndCustomer")
	public void test_10_DeleteStandard() throws Exception {
		Integer idStandard = getInteger(TEST_STANDARD_ID);
		notNull(idStandard, "Standard id cannot be found");
		this.mockMvc
				.perform(post("/KnowledgeBase/Standard/Delete/" + idStandard).with(csrf())
						.with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_10_DeleteStandard")
	public void test_11_DeleteCustomerAndLanguage() throws Exception {
		Integer idLanguage = getInteger(LANGUAGE_DEU_ID);
		Integer idCustomer = getInteger(CUSTOMER_MEME_ID);
		notNull(idLanguage, "Language id cannot be found");
		notNull(idCustomer, "Customer id cannot be found");
		this.mockMvc
				.perform(post("/KnowledgeBase/Language/Delete/" + idLanguage).with(csrf())
						.with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());

		this.mockMvc
				.perform(post("/KnowledgeBase/Customer/" + idCustomer + "/Delete").with(csrf())
						.with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test
	public void test_12_DownloadStandardTemplate() throws Exception {
		Resource templateResource = resourceLoader.getResource(template);
		MvcResult result = this.mockMvc
				.perform(get("/KnowledgeBase/Standard/Template").with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andReturn();
		assertEquals("attachment; filename=\"R5xx_STA_TSE_KB-Template-MeasureCollection_v" + appVersion + ".xlsx\"",
				result.getResponse().getHeaderValue("Content-Disposition"));
		assertEquals(templateResource.contentLength(), result.getResponse().getContentLengthLong());
		assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				result.getResponse().getContentType());
	}

	@Test(dependsOnMethods = "test_11_DeleteCustomerAndLanguage", timeOut = 120000)
	public synchronized void test_13_ImportStandard() throws Exception {
		Resource resource = resourceLoader.getResource(importStandard);
		isTrue(resource.exists(), "Resource cannot be found");
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", resource.getInputStream());
		this.mockMvc
				.perform(multipart("/KnowledgeBase/Standard/Import").file(mockMultipartFile).with(csrf())
						.with(httpBasic(USERNAME, PASSWORD)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").exists());
		Worker worker = null;
		for (int i = 0; i < 3000; i++) {
			List<String> tasks = serviceTaskFeedback.tasks(USERNAME);
			notEmpty(tasks, "No background task found");
			for (String workerId : tasks) {
				Worker worker2 = workersPoolManager.get(workerId);
				if (worker2 instanceof WorkerImportStandard) {
					worker = worker2;
					break;
				}
			}
			if (worker == null)
				wait(10);
			else
				break;
		}
		notNull(worker, "Import standard worker cannot be found");
		while (worker.isWorking())
			wait(100);
		serviceTaskFeedback.unregisterTask(USERNAME, worker.getId());
		isNull(worker.getError(), "An error occured while import standard");
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Standard standard = new DAOStandardHBM(session).getStandardByLabelAndVersion(STANDARD_FOR_TEST, 2015);
			notNull(standard, "Standard cannot be found");
			put(STANDARD_FOR_TEST, standard.getId());
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
	}

	@Test(dependsOnMethods = "test_13_ImportStandard")
	public void test_14_ExportStandard() throws Exception {
		MvcResult result = this.mockMvc.perform(
				get("/KnowledgeBase/Standard/Export/" + getInteger(STANDARD_FOR_TEST)).with(csrf())
						.with(httpBasic(USERNAME, PASSWORD)).contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andReturn();
		assertEquals(
				String.format("attachment; filename=\"%s\"",
						"R5xx_STA_TSE_KB-Standardfortest-MeasureCollection_v2015.xlsx"),
				result.getResponse().getHeaderValue("Content-Disposition"));
		assertEquals(FilenameUtils.getExtension(importStandard), result.getResponse().getContentType());
	}
}
