package lu.itrust.TS.application;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.Assert.isNull;
import static org.springframework.util.Assert.notNull;
import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.model.TrickService;

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

}
