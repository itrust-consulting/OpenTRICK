package lu.itrust.ts.controller;

import static lu.itrust.ts.helper.TestConstant.SIMPLE_ANALYSIS_NAME;
import static lu.itrust.ts.helper.TestConstant.SIMPLE_ANALYSIS_V0_0_1_ID;
import static lu.itrust.ts.helper.TestConstant.SIMPLE_ANALYSIS_VERSION;
import static lu.itrust.ts.helper.TestSharingData.getInteger;
import static lu.itrust.ts.helper.TestSharingData.put;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.Assert.isNull;
import static org.springframework.util.Assert.notNull;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.ts.asynchronousWorkers.Worker;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceAnalysis;
import lu.itrust.business.ts.database.service.ServiceAssessment;
import lu.itrust.business.ts.database.service.ServiceAsset;
import lu.itrust.business.ts.database.service.ServiceAssetType;
import lu.itrust.business.ts.database.service.ServiceCustomer;
import lu.itrust.business.ts.database.service.ServiceLanguage;
import lu.itrust.business.ts.database.service.ServiceScenario;
import lu.itrust.business.ts.database.service.ServiceTaskFeedback;
import lu.itrust.business.ts.database.service.WorkersPoolManager;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.asset.AssetType;
import lu.itrust.business.ts.model.general.AssetTypeValue;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.general.OpenMode;
import lu.itrust.business.ts.model.scenario.Scenario;

/**
 * @author eomar
 *
 */

@Test(groups = "CreateAnalysis", dependsOnGroups = "Installation")
public class TS_03_CreateAnAnlysis extends SpringTestConfiguration {

	private static final String APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8 = "application/x-www-form-urlencoded;charset=UTF-8";

	private static final String ASSESSMENT_TRICK_SERVICE_SCENARIO_TEST = "Assessment-Trick service-Scenario test";

	private static final String SCENARIO_SCENARIO_TEST = "Scenario-Scenario test";

	private static final String ASSET_TRICK_SERVICE = "Asset-Trick service";

	public static String SCENARIO_ASSET_TYPE_VALUE = "";

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

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ServiceAssessment serviceAssessment;

	public static String TASK_ID = null;

	public static int LANGUAGE_ID = 0;

	public static int CUSTOMER_ID = 0;

	public static int ANALYSIS_ID = 0;

	public static int DEFAULT_PROFILE = 0;

	@Test
	@Transactional(readOnly = true)
	public void test_00_loadData() throws Exception {
		Language language = serviceLanguage.getByAlpha3("FRA");
		notNull(language, "French language cannot be found");
		LANGUAGE_ID = language.getId();
		Customer customer = serviceCustomer.getFromContactPerson("me");
		notNull(customer, "'me' customer cannot be found");
		CUSTOMER_ID = customer.getId();
		DEFAULT_PROFILE = serviceAnalysis.findDefaultProfileIdByAnalysisTypeAndLanguage(AnalysisType.HYBRID, language);
	}

	@Test(timeOut = 120000, dependsOnMethods = "test_00_loadData")
	public void test_01_CreateSimpleAnalysis() throws Exception {
		this.mockMvc.perform(post("/Analysis/Build/Save").with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).contentType(APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8)
				.param("author", "Admin Admin").param("name", SIMPLE_ANALYSIS_NAME).param("version", SIMPLE_ANALYSIS_VERSION).param("comment", "comment")
				.param("customer", String.valueOf(CUSTOMER_ID)).param("language", String.valueOf(LANGUAGE_ID)).param("profile", String.valueOf(DEFAULT_PROFILE))
				.param("type", AnalysisType.QUANTITATIVE.name())).andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());

	}

	@Test(dependsOnMethods = "test_01_CreateSimpleAnalysis")
	@Transactional(readOnly = true)
	public void test_02_SelectAnalysis() throws Exception {
		Analysis analysis = serviceAnalysis.getByCustomerAndLabelAndVersion(CUSTOMER_ID, SIMPLE_ANALYSIS_NAME, SIMPLE_ANALYSIS_VERSION);
		notNull(analysis, "Analysis cannot be found");
		put(SIMPLE_ANALYSIS_V0_0_1_ID, analysis.getId());
		ANALYSIS_ID = analysis.getId();
		this.mockMvc.perform(get(String.format("/Analysis/%d/Select", ANALYSIS_ID)).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).accept(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(forwardedUrl("/WEB-INF/views/jsp/analyses/single/home.jsp"));
	}

	@Test
	public void test_03_DisplayAll() throws Exception {
		this.mockMvc.perform(get(String.format("/Analysis")).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).accept(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isFound())
				.andExpect(redirectedUrl("/Analysis/All"));
	}

	@Test(dependsOnMethods = "test_01_CreateSimpleAnalysis")
	@Transactional(readOnly = true)
	public void test_04_CreateCustom() throws Exception {
		Analysis analysis = serviceAnalysis.get(ANALYSIS_ID);
		notNull(analysis, "Analysis cannot be found");
	}

	@Test(timeOut = 120000, dependsOnMethods = "test_04_CreateCustom")
	public synchronized void test_05_CreateVersion() throws Exception {
		TASK_ID = new ObjectMapper().readTree(this.mockMvc
				.perform(post(String.format("/Analysis/Duplicate/%d", ANALYSIS_ID)).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).accept(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"author\":\"%s\", \"version\":\"%s\", \"comment\":\"%s\"}", "Admin Admin", "0.0.2", "comment")))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).findValue("analysis_task_id").asText("");
		wait(100);
		Worker worker = workersPoolManager.get(TASK_ID);
		notNull(worker, "Worker cannot be found");
		while (worker.isWorking())
			wait(100);
		serviceTaskFeedback.unregisterTask(USERNAME, TASK_ID);
		isNull(worker.getError(), "Error should be null");
	}

	@Test(dependsOnMethods = "test_05_CreateVersion")
	@Transactional(readOnly = true)
	public void test_06_SelectAnalysis_Version_0_0_2() {
		Analysis analysis = serviceAnalysis.getByCustomerAndLabelAndVersion(CUSTOMER_ID, SIMPLE_ANALYSIS_NAME, "0.0.2");
		notNull(analysis, "Analysis cannot be found");
		ANALYSIS_ID = analysis.getId();
	}

	@Test(dependsOnMethods = "test_06_SelectAnalysis_Version_0_0_2")
	public void test_07_AddAsset() throws UnsupportedEncodingException, Exception {
		this.mockMvc.perform(post("/Analysis/Asset/Save").with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).accept(APPLICATION_JSON_CHARSET_UTF_8)
				.sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID).sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT)
				.content(String.format(
						"{\"id\":\"0\", \"name\":\"%s\" ,\"assetType\": {\"id\": \"%d\" }, \"value\": \"%s\", \"selected\":\"%s\", \"comment\":\"%s\", \"hiddenComment\":\"%s\", \"relatedName\": \"%s\"}",
						"Trick service", 1, "687,688", false, "comment", "hiddenComment","relatedName")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").exists());
	}

	@Test(dependsOnMethods = "test_07_AddAsset")
	@Transactional(readOnly = false)
	public void test_08_LoadAsset() throws Exception {
		Asset asset = serviceAsset.getByNameAndAnlysisId("Trick service", ANALYSIS_ID);
		notNull(asset, "Asset 'Trick service' cannot be found");
		put(ASSET_TRICK_SERVICE, asset.getId());
		assertEquals("Bad Asset name", asset.getName(), "Trick service");
		assertEquals("Bad Asset value", asset.getValue(), 687.688 * 1000, 1E-10);
		assertEquals("Bad Asset comment", asset.getComment(), "comment");
		assertEquals("Bad Asset hidden comment", asset.getHiddenComment(), "hiddenComment");
		assertEquals("Bad Asset related name", asset.getRelatedName(), "relatedName");
		AssetType assetType = serviceAssetType.get(1);
		notNull(assetType, "Asset type cannot be found");
		assertEquals("Bad Asset asset-type", asset.getAssetType(), assetType);
	}

	@Test(dependsOnMethods = "test_06_SelectAnalysis_Version_0_0_2")
	@Transactional(readOnly = true)
	public void test_09_GenerateScenrioAssetTypeValue() throws Exception {
		List<AssetType> assetTypes = serviceAssetType.getAll();
		for (AssetType assetType : assetTypes)
			SCENARIO_ASSET_TYPE_VALUE += (SCENARIO_ASSET_TYPE_VALUE.isEmpty() ? "" : ",") + String.format("%d", assetType.getId());
	}

	@Test(dependsOnMethods = "test_06_SelectAnalysis_Version_0_0_2")
	public void test_10_AddScenario() throws Exception {
		this.mockMvc.perform(post("/Analysis/Scenario/Save").with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).accept(APPLICATION_JSON_CHARSET_UTF_8)
				.sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID).sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT)
				.content(String.format(
						"{\"id\":\"0\", \"name\":\"%s\", \"scenarioType\": {\"id\": %d},\"selected\":\"%s\", \"description\":\"%s\", \"assetTypeValues\" : [%s] ,\"assetValues\" : [], \"assetLinked\" : false, \"preventive\": 1.0, \"detective\": 0 , \"limitative\": 0, \"corrective\": 0, \"intentional\": 1, \"accidental\": 0, \"environmental\": 0, \"internalThreat\": 0, \"externalThreat\": 0}",
						"Scenario test", 1, false, "Test scenario", SCENARIO_ASSET_TYPE_VALUE)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").exists());
	}

	@Test(dependsOnMethods = "test_06_SelectAnalysis_Version_0_0_2")
	@Transactional(readOnly = true)
	public void test_10_CheckScenario() {
		Scenario scenario = serviceScenario.getByNameAndAnalysisId("Scenario test", ANALYSIS_ID);
		notNull(scenario, "Scenario 'Scenario test' cannot be found");
		put(SCENARIO_SCENARIO_TEST, scenario.getId());
	}

	@Test(dependsOnMethods = "test_10_CheckScenario")
	public void test_10_UpdateScenarioCharacteristic() throws Exception {
		Integer idScenario = getInteger(SCENARIO_SCENARIO_TEST);
		notNull(idScenario, "Scenario id cannot be null");
		this.mockMvc
				.perform(post("/Analysis/EditField/Scenario/" + idScenario).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID)
						.sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": %s}", idScenario, "intentional", "Integer", 1)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Scenario/" + idScenario).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT)
						.sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": %d}", idScenario, "accidental", "Integer", 1)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Scenario/" + idScenario).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT)
						.sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": %d}", idScenario, "environmental", "Integer", 1)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Scenario/" + idScenario).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT)
						.sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": %d}", idScenario, "internalThreat", "Integer", 1)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Scenario/" + idScenario).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT)
						.sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": %d}", idScenario, "externalThreat", "Integer", 1)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Scenario/" + idScenario).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT)
						.sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": %f}", idScenario, "preventive", "Double", 0.25)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Scenario/" + idScenario).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT)
						.sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": %f}", idScenario, "detective", "Double", 0.25)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Scenario/" + idScenario).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT)
						.sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": %f}", idScenario, "limitative", "Double", 0.25)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Scenario/" + idScenario).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT)
						.sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": %f}", idScenario, "corrective", "Double", 0.25)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());

	}

	@Test(dependsOnMethods = "test_10_UpdateScenarioCharacteristic")
	@Transactional(readOnly = true)
	public void test_11_LoadScenario() throws Exception {
		Scenario scenario = serviceScenario.get(getInteger(SCENARIO_SCENARIO_TEST));
		notNull(scenario, "Scenario cannot be found");
		assertEquals("Bad scenario name", scenario.getName(), "Scenario test");
		assertEquals("Bad scenario description", scenario.getDescription(), "Test scenario");
		assertFalse("Scenario is selected", scenario.isSelected());
		assertEquals("Bad scenario accidental", 1, scenario.getAccidental());
		assertEquals("Bad scenario environmental", 1, scenario.getEnvironmental());
		assertEquals("Bad scenario intentional", 1, scenario.getIntentional());
		assertEquals("Bad scenario externalThreat", 1, scenario.getExternalThreat());
		assertEquals("Bad scenario internalThreat", 1, scenario.getInternalThreat());

		serviceAssetType.getAll().forEach(assetType -> {
			AssetTypeValue assetTypeValue = scenario.findByAssetType(assetType);
			notNull(assetTypeValue, String.format("Scanrio Asset type value for '%s' cannot be found", assetType.getName()));
			assertEquals(String.format("Scanrio Asset type value for '%s' cannot be found", assetType.getName()), 1, assetTypeValue.getValue());
		});

	}

	@Test(dependsOnMethods = { "test_11_LoadScenario", "test_10_AddScenario" })
	@Transactional(readOnly = true)
	public void test_12_CheckAssessment() throws Exception {
		Asset asset = serviceAsset.get(getInteger(ASSET_TRICK_SERVICE));
		Scenario scenario = serviceScenario.get(getInteger(SCENARIO_SCENARIO_TEST));
		Assessment assessment = serviceAssessment.getByAssetAndScenario(asset, scenario);
		notNull(assessment, "Assessment for asset :'Trick service' and Scenario: 'Scenario test' cannot be found");
		put(ASSESSMENT_TRICK_SERVICE_SCENARIO_TEST, assessment.getId());
		assertFalse("Assessment should not be selected", assessment.isSelected());

	}

	@Test(dependsOnMethods = "test_12_CheckAssessment")
	public void test_13_SelectAssetAndScenario() throws Exception {
		this.mockMvc
				.perform(get("/Analysis/Asset/Select/" + getInteger(ASSET_TRICK_SERVICE)).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).accept(APPLICATION_JSON_CHARSET_UTF_8)
						.sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT).sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());

		this.mockMvc
				.perform(get("/Analysis/Scenario/Select/" + getInteger(SCENARIO_SCENARIO_TEST)).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
						.accept(APPLICATION_JSON_CHARSET_UTF_8).sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT).sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_13_SelectAssetAndScenario")
	public void test_14_UpdateAssessment() throws Exception {
		Integer idAssessment = getInteger(ASSESSMENT_TRICK_SERVICE_SCENARIO_TEST);
		notNull(idAssessment, "Assessment id cannot be null");
		this.mockMvc
				.perform(post("/Analysis/EditField/Assessment/" + idAssessment).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT)
						.sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": \"%s\"}", idAssessment, "IMPACT", "String", "i9")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
		this.mockMvc
				.perform(post("/Analysis/EditField/Assessment/" + idAssessment).with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT)
						.sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": \"%s\"}", idAssessment, "likelihood", "String", "p9")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());

	}

	@Test(dependsOnMethods = "test_14_UpdateAssessment")
	@Transactional(readOnly = true)
	public void test_15_CheckAssessment() throws Exception {
		Assessment assessment = serviceAssessment.get(getInteger(ASSESSMENT_TRICK_SERVICE_SCENARIO_TEST));
		notNull(assessment, "Assessment cannot be found");
		assertTrue("Assessment should be selected", assessment.isSelected());
		assessment = serviceAssessment.get(assessment.getId());
		notNull(assessment.getLikelihood(), "Likelihood should not be null");
		assertEquals("Impact should be i9", "i9", assessment.getImpactAcronym("IMPACT"));
		assertEquals("Likelihood should be p9", "p9", assessment.getLikelihood().getRaw());
	}

}
