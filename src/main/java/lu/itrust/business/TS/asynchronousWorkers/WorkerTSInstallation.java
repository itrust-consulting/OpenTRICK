/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.io.File;
import java.io.IOException;

import lu.itrust.business.TS.data.TrickService;
import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.general.Customer;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOTrickService;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOTrickServiceHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.importation.ImportAnalysis;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.usermanagement.User;

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
		super();
		setCanDeleteFile(false);
	}

	@Override
	public void run() {
		setAsyncCallback(new AsyncCallback("window.location.assign(context+\"/Admin\")", null));
		setMessageHandler(new MessageHandler("success.ts.install", "Installation successfull", null, 99));
		super.run();
	}

	/**
	 * @param currentVersion
	 * @param importAnalysis
	 * @param fileName
	 */
	public WorkerTSInstallation(String currentVersion, ImportAnalysis importAnalysis, String fileName) {
		super(importAnalysis, fileName);
		setCurrentVersion(currentVersion);
		setCanDeleteFile(false);
	}

	/**
	 * @param importAnalysis
	 * @param importFile
	 * @param customer
	 * @throws IOException
	 */
	public WorkerTSInstallation(ImportAnalysis importAnalysis, File importFile, Customer customer) throws IOException {
		super(importAnalysis, importFile, customer);
		setCanDeleteFile(false);
	}

	/**
	 * @param sessionFactory
	 * @param serviceTaskFeedback
	 * @param importFile
	 * @param customer
	 * @param owner
	 * @throws IOException
	 */
	public WorkerTSInstallation(SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback, File importFile, Customer customer, User owner) throws IOException {
		super(sessionFactory, serviceTaskFeedback, importFile, customer, owner);
		setCanDeleteFile(false);
	}

	@Override
	protected synchronized void OnStarted() throws Exception {
		Session session = null;
		try {
			super.OnStarted();
			getImportAnalysis().getServiceTaskFeedback().send(getId(), new MessageHandler("info.delete.default.profile", "Removing the default profile", null, 1));
			session = getImportAnalysis().getSessionFactory().openSession();
			DAOAnalysis daoAnalysis = new DAOAnalysisHBM(session);
			Analysis analysis = daoAnalysis.getDefaultProfile();
			if (analysis != null) {
				session.beginTransaction();
				daoAnalysis.delete(analysis);
				session.getTransaction().commit();
			}
		} catch (Exception e) {
			if (session != null && session.getTransaction().isInitiator())
				session.getTransaction().rollback();
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}

	@Override
	protected void OnSuccess() {
		Session session = null;
		try {
			session = getImportAnalysis().getSessionFactory().openSession();
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
