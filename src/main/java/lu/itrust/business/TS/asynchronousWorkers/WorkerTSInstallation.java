/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOTrickService;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOTrickServiceHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.messagehandler.TaskName;
import lu.itrust.business.TS.model.TrickService;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
public class WorkerTSInstallation extends WorkerAnalysisImport {

	private String currentVersion;

	private AnalysisType analysisType;

	@Override
	public void run() {
		setAsyncCallback(new AsyncCallback("window.location.assign(context+'/Admin')"));
		setMessageHandler(new MessageHandler("success.ts.install", "Installation successfull", 99));
		super.run();
	}

	public WorkerTSInstallation(String version, WorkersPoolManager workersPoolManager, SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback,
			List<String> fileNames, int customerId, String ownerUsername) throws IOException {
		super(workersPoolManager, sessionFactory, serviceTaskFeedback, fileNames, customerId, ownerUsername);
		setCurrentVersion(version);
		setCanDeleteFile(false);
	}

	@Override
	protected synchronized void OnStarted() throws Exception {
		Session session = null;
		try {
			super.OnStarted();
			setName(TaskName.INSTALL_APPLICATION);
			getImportAnalysis().getServiceTaskFeedback().send(getId(), new MessageHandler("info.delete.default.profile", "Removing the default profiles", 1));
			session = getSessionFactory().openSession();
			DAOAnalysis daoAnalysis = new DAOAnalysisHBM(session);
			List<Analysis> analyses = daoAnalysis.getDefaultProfiles();
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
	 * @see
	 * lu.itrust.business.TS.asynchronousWorkers.WorkerAnalysisImport#process(
	 * java.lang.String, org.hibernate.Session,
	 * lu.itrust.business.TS.usermanagement.User,
	 * lu.itrust.business.TS.model.general.Customer)
	 */
	@Override
	protected void process(int index, String fileName, Session session, User user, Customer customer) throws ClassNotFoundException, SQLException, Exception {
		Analysis analysis = new Analysis();
		analysis.setProfile(true);
		analysis.setDefaultProfile(true);
		getImportAnalysis().setAnalysis(analysis);
		super.process(index, fileName, session, user, customer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.asynchronousWorkers.Worker#isMatch(java.lang.String
	 * , java.lang.Object)
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

	@Override
	protected void OnSuccess() {
		Session session = null;
		try {
			session = getSessionFactory().openSession();
			session.beginTransaction();
			DAOTrickService daoTrickService = new DAOTrickServiceHBM(session);
			TrickService trickService = daoTrickService.getStatus();
			if (trickService == null)
				trickService = new TrickService(currentVersion, true);
			else
				trickService.setVersion(currentVersion);
			daoTrickService.saveOrUpdate(trickService);
			session.getTransaction().commit();
			getImportAnalysis().getServiceTaskFeedback().send(getId(), new MessageHandler("info.install.update.version", "Update install version", 98));
			String username = getImportAnalysis().getServiceTaskFeedback().findUsernameById(this.getId());
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogType.ANALYSIS, "log.system.install", String.format("System: TRCIK Service, version: %s", trickService.getVersion()), username,
					LogAction.INSTALL, trickService.getVersion());
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			if (session != null && session.getTransaction().getStatus().canRollback())
				session.getTransaction().rollback();
		} finally {
			super.OnSuccess();
			if (session != null)
				session.close();

		}
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

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
	 * @param analysisType
	 *            the analysisType to set
	 */
	public void setAnalysisType(AnalysisType analysisType) {
		this.analysisType = analysisType;
	}

}
