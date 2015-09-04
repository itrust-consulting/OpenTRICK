package lu.itrust.TS.application;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.Assert.*;
import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.model.TrickService;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TS_01_Test extends SpringTestConfiguration {
	
	@Autowired
	protected WorkersPoolManager workersPoolManager;
	
	@Autowired
	private ServiceCustomer serviceCustomer;
	
	@Autowired
	private ServiceLanguage serviceLanguage;
	
	@Autowired
	private ServiceAnalysis serviceAnalysis;
	
	@Test
	public void test_00_Install() throws Exception {
		installTaskId = new ObjectMapper()
				.readTree(
						this.mockMvc.perform(get("/Install").with(httpBasic(USERNAME, PASSWORD))).andDo(print()).andExpect(status().isOk())
								.andExpect(jsonPath("$.idTask").exists()).andReturn().getResponse().getContentAsString()).findValue("idTask").asText(null);
	}

	@Test(timeout = 30000)
	public synchronized void test_01_WaitForWorker() throws InterruptedException {
		Worker worker = workersPoolManager.get(installTaskId);
		notNull(worker, "Installation worker cannot be found");
		while (worker.isWorking())
			wait(1000);
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
	}
	
	@Test
	@Transactional(readOnly = true)
	public void test_06_CheckDefaultLangauge() throws Exception {
		Language language = serviceLanguage.get(0);
		notNull(language, "Language with id '0' cannot be found");
	}
	
	@Test
	@Transactional(readOnly = true)
	public void test_07_CheckAnalysisProfile() throws Exception{
		Analysis analysis = serviceAnalysis.getDefaultProfile();
		notNull(analysis, "Default Analysis profile cannot be found");
	}

}
