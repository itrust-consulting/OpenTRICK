package lu.itrust.ts.controller;

import static lu.itrust.ts.controller.TS_05_ImportExport.ANALYSIS_KEY;
import static lu.itrust.ts.helper.TestSharingData.getInteger;
import static lu.itrust.ts.helper.TestSharingData.put;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.Assert.isNull;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import lu.itrust.business.ts.asynchronousWorkers.Worker;
import lu.itrust.business.ts.asynchronousWorkers.WorkerExportAnalysis;
import lu.itrust.business.ts.asynchronousWorkers.WorkerExportWordReport;
import lu.itrust.business.ts.database.dao.impl.DAOUserImpl;
import lu.itrust.business.ts.database.service.ServiceTaskFeedback;
import lu.itrust.business.ts.database.service.WorkersPoolManager;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplate;
import lu.itrust.business.ts.model.general.helper.FilterControl;
import lu.itrust.business.ts.usermanagement.RoleType;
import lu.itrust.business.ts.usermanagement.User;

/**
 * @author eomar
 *
 */
@Test(groups = "Profile", dependsOnGroups = "ImportExport")
public class TS_07_Profile extends SpringTestConfiguration {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Value("${app.settings.test.validation.action.plan.analysis.identifier}")
	private String identifier;

	@Value("${app.settings.test.validation.action.plan.analysis.version}")
	private String version;

	@Test(timeOut = 120000)
	public synchronized void test_GenerateSqlite() throws Exception {
		Integer idAnalysis = getInteger(ANALYSIS_KEY);
		notNull(idAnalysis, "Analysis cannot be found");
		this.mockMvc
				.perform(get("/Analysis/Data-manager/Sqlite/Export-process").param("idAnalysis", idAnalysis + "")
						.with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
						.contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
		Worker worker = null;
		for (int i = 0; i < 30; i++) {
			List<String> tasks = serviceTaskFeedback.tasks(USERNAME);
			notEmpty(tasks, "No background task found");
			for (String workerId : tasks) {
				Worker worker2 = workersPoolManager.get(workerId);
				if (worker2 != null && worker2.isMatch("class+analysis.id", WorkerExportAnalysis.class, idAnalysis)) {
					worker = worker2;
					break;
				}
			}
			if (worker == null)
				wait(1000);
			else
				break;
		}

		notNull(worker, "Export analysis worker cannot be found");
		while (worker.isWorking())
			wait(100);

		try {
			isNull(worker.getError(), "An error occured while export analysis");

			MessageHandler messageHandler = serviceTaskFeedback.recieveById(worker.getId());

			notNull(messageHandler, "Last message cannot be found");

			this.mockMvc
					.perform(get("/Task/Status/" + worker.getId()).with(csrf())
							.with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
							.contentType(APPLICATION_JSON_CHARSET_UTF_8))
					.andExpect(status().isOk()).andExpect(jsonPath("$.asyncCallbacks[0].args[0]").exists());

			serviceTaskFeedback.unregisterTask(USERNAME, worker.getId());

			assertFalse("Task should be not existed", serviceTaskFeedback.hasTask(USERNAME, worker.getId()));

			notNull(messageHandler.getAsyncCallbacks(), "AsyncCallback should not be null");

			notEmpty(messageHandler.getAsyncCallbacks()[0].getArgs(), "AsyncCallback args should not be empty");

			put("key_sql_export_delete",
					Integer.parseInt(messageHandler.getAsyncCallbacks()[0].getArgs().get(1).toString()));
		} finally {
			serviceTaskFeedback.unregisterTask(USERNAME, worker.getId());
		}
	}

	@Transactional
	@SuppressWarnings("unchecked")
	@Test(timeOut = 120000)
	public synchronized void test_GenerateReport() throws Exception {
		Integer idAnalysis = getInteger(ANALYSIS_KEY);
		notNull(idAnalysis, "Analysis cannot be found");

		List<TrickTemplate> templates = (List<TrickTemplate>) this.mockMvc
				.perform(
						get("/Analysis/Data-manager/Report/Export-form/" + idAnalysis).with(csrf())
								.with(user(USERNAME).password(PASSWORD)).contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andReturn().getModelAndView().getModel().get("templates");

		notEmpty(templates, "No template can be found");

		Long idTemplate = templates.stream().filter(p -> p.getAnalysisType() == AnalysisType.QUANTITATIVE)
				.map(TrickTemplate::getId).findAny().orElse(-1L);

		assertTrue("Template cannot be found", idTemplate > 0);

		this.mockMvc
				.perform(post("/Analysis/Data-manager/Report/Export-process").with(csrf())
						.with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
						.contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.param("analysis", idAnalysis + "").param("type", "QUANTITATIVE")
						.param("template", idTemplate + ""))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());

		Worker worker = null;
		for (int i = 0; i < 30; i++) {
			List<String> tasks = serviceTaskFeedback.tasks(USERNAME);
			notEmpty(tasks, "No background task found");
			for (String workerId : tasks) {
				Worker worker2 = workersPoolManager.get(workerId);
				if (worker2 != null && worker2.isMatch("class+analysis.id", WorkerExportWordReport.class, idAnalysis)) {
					worker = worker2;
					break;
				}
			}
			if (worker == null)
				wait(1000);
			else
				break;
		}
		notNull(worker, "Export word report worker cannot be found");
		while (worker.isWorking())
			wait(100);

		try {
			isNull(worker.getError(), "An error occured while export word report");
			MessageHandler messageHandler = serviceTaskFeedback.recieveById(worker.getId());
			notNull(messageHandler, "Last message cannot be found");
			this.mockMvc
					.perform(get("/Task/Status/" + worker.getId()).with(csrf())
							.with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
							.contentType(APPLICATION_JSON_CHARSET_UTF_8))
					.andExpect(status().isOk()).andExpect(jsonPath("$.asyncCallbacks[0].args[1]").exists());
			serviceTaskFeedback.unregisterTask(USERNAME, worker.getId());
			assertFalse("Task should be not existed", serviceTaskFeedback.hasTask(USERNAME, worker.getId()));
			notNull(messageHandler.getAsyncCallbacks(), "AsyncCallback should not be null");
			notEmpty(messageHandler.getAsyncCallbacks()[0].getArgs(), "AsyncCallback args should not be empty");
			put("key_word_export_delete",
					Integer.parseInt(messageHandler.getAsyncCallbacks()[0].getArgs().get(1).toString()));
		} finally {
			serviceTaskFeedback.unregisterTask(USERNAME, worker.getId());
		}
	}

	@Test
	public void test_00_UpdateProfile() throws Exception {
		this.mockMvc.perform(post("/Account/Update").with(csrf())
				.with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN")).accept(APPLICATION_JSON_CHARSET_UTF_8)
				.content(String.format(
						"{\"currentPassword\" : \"%s\",\"password\": \"%s\",\"repeatPassword\": \"%s\",\"firstName\": \"%s\",\"lastName\": \"%s\",\"email\": \"%s\",\"locale\": \"%s\"}",
						PASSWORD, PASSWORD, PASSWORD, USERNAME, USERNAME, EMAIL, LANGUAGE)))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(content().string("{}"));

	}

	@Test
	public void test_01_UpdateUsername() throws Exception {
		this.mockMvc
				.perform(post("/Account/Update").with(csrf())
						.with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
						.accept(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format(
								"{\"currentPassword\" : \"%s\", \"login\": \"%s\",\"firstName\": \"%s\",\"lastName\": \"%s\",\"email\": \"%s\",\"locale\": \"%s\"}",
								PASSWORD, "lolmdr", USERNAME, USERNAME, EMAIL, LANGUAGE)))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(content().string("{}"));
		Session session = sessionFactory.openSession();
		try {
			assertFalse(new DAOUserImpl(session = sessionFactory.openSession()).existByUsername("lolmdr"));
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
	}

	@Test(expectedExceptions = AssertionError.class)
	public void test_01_UpdateConnexionType() throws Exception {
		this.mockMvc
				.perform(post("/Account/Update").with(csrf())
						.with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
						.accept(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format(
								"{\"currentPassword\" : \"%s\", \"connexionType\": %d,\"firstName\": \"%s\",\"lastName\": \"%s\",\"email\": \"%s\",\"locale\": \"%s\"}",
								PASSWORD,
								User.LADP_CONNEXION, USERNAME, USERNAME, EMAIL, LANGUAGE)))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(content().string("{}"));
		Session session = null;
		try {
			session = sessionFactory.openSession();
			User user = new DAOUserImpl(session = sessionFactory.openSession()).get(USERNAME);
			assertEquals(user.getConnexionType(), User.LADP_CONNEXION);
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
	}

	@Test
	public void test_01_UpdateRole() throws Exception {
		this.mockMvc
				.perform(post("/Account/Update").with(csrf())
						.with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
						.accept(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format(
								"{\"currentPassword\" : \"%s\",\"roles\": [\"%s\",\"%s\"],\"firstName\": \"%s\",\"lastName\": \"%s\",\"email\": \"%s\",\"locale\": \"%s\"}",
								PASSWORD, RoleType.ROLE_SUPERVISOR, RoleType.ROLE_CONSULTANT, USERNAME, USERNAME, EMAIL,
								LANGUAGE)))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(content().string("{}"));

		Session session = null;
		try {
			session = sessionFactory.openSession();
			User user = new DAOUserImpl(session = sessionFactory.openSession()).get(USERNAME);
			assertFalse(user.hasRole(RoleType.ROLE_SUPERVISOR));
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
	}

	@Test
	public void test_02_UpdateSQLITEFilter() throws Exception {
		MvcResult result = this.mockMvc
				.perform(post("/Account/Control/Sqlite/Update").with(csrf())
						.with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
						.accept(APPLICATION_JSON_CHARSET_UTF_8)
						.contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format(
								"{\"sort\" : \"%s\",\"filter\": \"%s\",\"direction\": \"%s\",\"size\": %d}",
								"identifier", identifier, "desc", 30)))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(jsonPath("$.success").exists()).andReturn();
		FilterControl control = (FilterControl) result.getRequest().getSession().getAttribute("sqliteControl");
		assertNotNull(control);
		assertEquals("identifier", control.getSort());
		assertEquals("desc", control.getDirection());
		assertEquals(identifier, control.getFilter());
		assertEquals(30, control.getSize());
	}

	@Test
	public void test_03_UpdateReportFilter() throws Exception {
		MvcResult result = this.mockMvc
				.perform(post("/Account/Control/Report/Update").with(csrf())
						.with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
						.accept(APPLICATION_JSON_CHARSET_UTF_8)
						.contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format(
								"{\"sort\" : \"%s\",\"filter\": \"%s\",\"direction\": \"%s\",\"size\": %d}", "version",
								version, "asc", 50)))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(jsonPath("$.success").exists()).andReturn();
		FilterControl control = (FilterControl) result.getRequest().getSession().getAttribute("reportControl");
		assertNotNull(control);
		assertEquals("version", control.getSort());
		assertEquals("asc", control.getDirection());
		assertEquals(version, control.getFilter());
		assertEquals(50, control.getSize());
	}

	@Test(dependsOnMethods = "test_GenerateReport")
	public void test_04_DeleteSQLITE() throws Exception {
		this.mockMvc
				.perform(post(String.format("/Account/Report/%d/Delete", getInteger("key_word_export_delete")))
						.with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
						.contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(dependsOnMethods = "test_GenerateSqlite")
	public void test_04_DeleteReport() throws Exception {
		this.mockMvc
				.perform(post(String.format("/Account/Sqlite/%d/Delete", getInteger("key_sql_export_delete")))
						.with(csrf()).with(user(USERNAME).password(PASSWORD).roles("USER", "ADMIN"))
						.contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

}
