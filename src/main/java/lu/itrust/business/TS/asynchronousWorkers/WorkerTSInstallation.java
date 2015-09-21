/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.io.IOException;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOTrickService;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOTrickServiceHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.TrickService;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @author eomar
 *
 */
public class WorkerTSInstallation extends WorkerAnalysisImport {

	private String currentVersion;

	/**
	 * 
	 */
	public WorkerTSInstallation() {
		setCanDeleteFile(false);
	}

	@Override
	public void run() {
		setAsyncCallback(new AsyncCallback("window.location.assign", "context+'/Admin'"));
		setMessageHandler(new MessageHandler("success.ts.install", "Installation successfull", null, 99));
		super.run();
	}

	public WorkerTSInstallation(String version, SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback, String filename, int customerId, String ownerUsername)
			throws IOException {
		super(sessionFactory, serviceTaskFeedback, filename, customerId, ownerUsername);
		setCurrentVersion(version);
		setCanDeleteFile(false);
	}

	@Override
	protected synchronized void OnStarted() throws Exception {
		Session session = null;
		try {
			super.OnStarted();
			getImportAnalysis().getServiceTaskFeedback().send(getId(), new MessageHandler("info.delete.default.profile", "Removing the default profile", null, 1));
			session = getSessionFactory().openSession();
			DAOAnalysis daoAnalysis = new DAOAnalysisHBM(session);
			Analysis analysis = daoAnalysis.getDefaultProfile();
			if (analysis != null) {
				session.beginTransaction();
				daoAnalysis.delete(analysis);
				session.getTransaction().commit();
			}
			analysis = new Analysis();
			analysis.setProfile(true);
			analysis.setDefaultProfile(true);
			analysis.setLabel("SME: Small and Medium Entreprises (Default Profile from installer)");
			getImportAnalysis().setAnalysis(analysis);
		} catch (Exception e) {
			if (session != null && session.getTransaction().isInitiator())
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
			getImportAnalysis().getServiceTaskFeedback().send(getId(), new MessageHandler("info.install.update.version", "Update install version", null, 98));
			String username = getImportAnalysis().getServiceTaskFeedback().findUsernameById(this.getId());
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogType.ANALYSIS, "log.system.install", String.format("System: TRCIK Service, version: %s", trickService.getVersion()), username,
					LogAction.INSTALL, trickService.getVersion());
		} catch (Exception e) {
			if (session != null && session.getTransaction().isInitiator())
				session.getTransaction().rollback();
			e.printStackTrace();
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

}
