/**
 * 
 */
package lu.itrust.business.ts.asynchronousWorkers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOTrickService;
import lu.itrust.business.ts.database.dao.impl.DAOAnalysisImpl;
import lu.itrust.business.ts.database.dao.impl.DAOTrickServiceImpl;
import lu.itrust.business.ts.database.service.ServiceStorage;
import lu.itrust.business.ts.database.service.ServiceTaskFeedback;
import lu.itrust.business.ts.database.service.WorkersPoolManager;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.messagehandler.TaskName;
import lu.itrust.business.ts.model.TrickService;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.usermanagement.User;


/**
 * This class represents a worker for installing the Trick Service application asynchronously.
 * It extends the WorkerAnalysisImport class and provides additional functionality for installation.
 */
public class WorkerTSInstallation extends WorkerAnalysisImport {

	private String currentVersion;

	private AnalysisType analysisType;

	@Override
	public void run() {
		setAsyncCallback(new AsyncCallback("gotToPage", "/Admin"));
		setMessageHandler(new MessageHandler("success.ts.install", "Installation successfull", 100));
		super.run();
	}

	public WorkerTSInstallation(String version, WorkersPoolManager workersPoolManager, SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback,
			ServiceStorage serviceStorage, List<String> fileNames, int customerId, String ownerUsername) throws IOException {
		super(fileNames, customerId, ownerUsername);
		setCurrentVersion(version);
		setCanDeleteFile(false);
	}

	/**
	 * This method is called when the worker is started. It performs the installation of the application.
	 * It removes the default profiles from the database.
	 *
	 * @throws Exception if an error occurs during the installation process.
	 */
	@Override
	protected synchronized void OnStarted() throws Exception {
		Session session = null;
		try {
			super.OnStarted();
			setName(TaskName.INSTALL_APPLICATION);
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.delete.default.profile", "Removing the default profiles", 1));
			session = getSessionFactory().openSession();
			final DAOAnalysis daoAnalysis = new DAOAnalysisImpl(session);
			final List<Analysis> analyses = daoAnalysis.getDefaultProfiles();
			if (!analyses.isEmpty()) {
				session.beginTransaction();
				analyses.forEach(analysis -> daoAnalysis.delete(analysis));
				session.getTransaction().commit();
			}
		} catch (Exception e) {
			if (session != null && session.getTransaction().getStatus().canRollback())
				session.getTransaction().rollback();
			throw e;
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.asynchronousWorkers.WorkerAnalysisImport#process(
	 * java.lang.String, org.hibernate.Session,
	 * lu.itrust.business.ts.usermanagement.User,
	 * lu.itrust.business.ts.model.general.Customer)
	 */
	@Override
	protected void process(int index, String fileName, Session session, User user, Customer customer) throws ClassNotFoundException, SQLException, Exception {
		final Analysis analysis = new Analysis();
		analysis.setProfile(true);
		analysis.setDefaultProfile(true);
		getImportAnalysis().setAnalysis(analysis);
		super.process(index, fileName, session, user, customer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.asynchronousWorkers.Worker#isMatch(java.lang.String ,
	 * java.lang.Object)
	 */
	@Override
	public boolean isMatch(String express, Object... values) {
		try {
			String[] expressions = express.split("\\+");
			boolean match = values.length == expressions.length && values.length == 2;
			for (int i = 0; i < expressions.length && match; i++) {
				switch (expressions[i]) {
				case "currentVersion":
					match &= values[i].equals(currentVersion);
					break;
				case "class":
					match &= values[i].equals(getClass());
					break;
				default:
					match = false;
					break;
				}
			}
			return match;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * This method is called when the operation is successful.
	 * It updates the version of the TrickService and saves it to the database.
	 * It also sends a message to the service task feedback with the updated version information.
	 * Finally, it logs the installation action in the TrickLogManager.
	 */
	@Override
	protected void OnSuccess() {
		Session session = null;
		try {
			session = getSessionFactory().openSession();
			session.beginTransaction();
			final DAOTrickService daoTrickService = new DAOTrickServiceImpl(session);
			TrickService trickService = daoTrickService.getStatus();
			if (trickService == null)
				trickService = new TrickService(currentVersion, true);
			else
				trickService.setVersion(currentVersion);
			daoTrickService.saveOrUpdate(trickService);
			session.getTransaction().commit();
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.install.update.version", "Update install version", 98));
			String username = getServiceTaskFeedback().findUsernameById(this.getId());
			/**
			 * Log
			 */
			TrickLogManager.persist(LogType.ANALYSIS, "log.system.install", String.format("System: TRCIK Service, version: %s", trickService.getVersion()), username,
					LogAction.INSTALL, trickService.getVersion());
		} catch (Exception e) {
			TrickLogManager.persist(e);
			if (session != null && session.getTransaction().getStatus().canRollback())
				session.getTransaction().rollback();
		} finally {
			super.OnSuccess();
			if (session != null)
				session.close();

		}
	}

	/**
	 * Returns the current version as a string.
	 *
	 * @return the current version as a string
	 */
	public String getCurrentVersion() {
		return currentVersion;
	}

	/**
	 * Sets the current version of the worker.
	 *
	 * @param currentVersion the current version to set
	 */
	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	/**
	 * @return the analysisType
	 */
	public AnalysisType getAnalysisType() {
		return analysisType;
	}

	/**
	 * @param analysisType the analysisType to set
	 */
	public void setAnalysisType(AnalysisType analysisType) {
		this.analysisType = analysisType;
	}

}
