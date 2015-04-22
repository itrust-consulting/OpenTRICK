/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.io.File;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOWordReportHBM;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.ExportAnalysisReport;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.WordReport;
import lu.itrust.business.TS.usermanagement.User;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.util.FileCopyUtils;

/**
 * @author eomar
 *
 */
public class WorkerExportWordReport implements Worker {

	private String id = String.valueOf(System.nanoTime());

	private int idAnalysis;

	private String username;

	private Exception error;

	private boolean canceled;

	private boolean working;

	private WorkersPoolManager workersPoolManager;

	private SessionFactory sessionFactory;

	private ExportAnalysisReport exportAnalysisReport;

	/**
	 * @param idAnalysis
	 * @param username
	 * @param serviceTaskFeedback
	 * @param sessionFactory
	 * @param exportAnalysisReport
	 * @param workersPoolManager
	 */
	public WorkerExportWordReport(int idAnalysis, String username, SessionFactory sessionFactory, ExportAnalysisReport exportAnalysisReport, WorkersPoolManager workersPoolManager) {
		this.idAnalysis = idAnalysis;
		this.username = username;
		this.sessionFactory = sessionFactory;
		this.exportAnalysisReport = exportAnalysisReport;
		this.workersPoolManager = workersPoolManager;
	}

	@Override
	public void run() {
		Session session = null;
		try {
			synchronized (this) {
				if (workersPoolManager != null && !workersPoolManager.exist(getId()))
					if (!workersPoolManager.add(this))
						return;
				if (canceled || working)
					return;
				working = true;
			}
			session = sessionFactory.openSession();
			DAOAnalysis daoAnalysis = new DAOAnalysisHBM(session);
			Analysis analysis = daoAnalysis.get(idAnalysis);
			if (analysis == null)
				throw new TrickException("error.analysis.not_exist", "Analysis not found");
			else if (analysis.isProfile())
				throw new TrickException("error.analysis.is_profile", "Profile cannot be exported as report");
			else if (!analysis.hasData())
				throw new TrickException("error.analysis.no_data", "Empty analysis cannot be exported");
			exportAnalysisReport.setMaxProgress(98);
			exportAnalysisReport.setIdTask(id);
			exportAnalysisReport.exportToWordDocument(analysis, true);
			saveWordDocument(session);
		} catch (TrickException e) {
			exportAnalysisReport.getServiceTaskFeedback().send(id, new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
			e.printStackTrace();
		} catch (Exception e) {
			exportAnalysisReport.getServiceTaskFeedback().send(id, new MessageHandler("error.unknown.occurred", "An unknown error occurred", (String) null, e));
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (HibernateException e) {
				e.printStackTrace();
			}
			if (exportAnalysisReport.getWorkFile() != null && exportAnalysisReport.getWorkFile().exists())
				exportAnalysisReport.getWorkFile().delete();
			synchronized (this) {
				working = false;
			}
			if (workersPoolManager != null)
				workersPoolManager.remove(getId());
		}
	}

	private void saveWordDocument(Session session) throws Exception {
		File file = exportAnalysisReport.getWorkFile();
		User user = new DAOUserHBM(session).get(username);
		Analysis analysis = exportAnalysisReport.getAnalysis();
		WordReport report = new WordReport(analysis.getIdentifier(), analysis.getLabel(), analysis.getVersion(), user, file.getName(), file.length(),
				FileCopyUtils.copyToByteArray(file));
		try {
			exportAnalysisReport.getServiceTaskFeedback().send(id, new MessageHandler("info.saving.word.report", "Saving word report", null, 99));
			session.getTransaction().begin();
			new DAOWordReportHBM(session).saveOrUpdate(report);
			session.getTransaction().commit();
			MessageHandler messageHandler = new MessageHandler("success.save.word.report", "Report has been successfully saved", null, 100);
			messageHandler.setAsyncCallback(new AsyncCallback("downloadWordReport(\"" + report.getId() + "\")", null));
			exportAnalysisReport.getServiceTaskFeedback().send(id, messageHandler);
			String username = exportAnalysisReport.getServiceTaskFeedback().findUsernameById(this.getId());
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogType.ANALYSIS, "log.analysis.export.word", String.format("Analyis: %s, version: %s, type: report", analysis.getIdentifier(), analysis.getVersion()),
					username, LogAction.EXPORT, analysis.getIdentifier(), analysis.getVersion());
		} catch (Exception e) {
			try {
				if (session.getTransaction().isInitiator())
					session.getTransaction().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			throw e;
		}
	}

	@Override
	public boolean isWorking() {
		return working;
	}

	@Override
	public boolean isCanceled() {
		return canceled;
	}

	@Override
	public Exception getError() {
		return error;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void setPoolManager(WorkersPoolManager poolManager) {
		this.workersPoolManager = poolManager;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public synchronized void start() {
		run();
	}

	@Override
	public void cancel() {
		try {
			synchronized (this) {
				if (working) {
					Thread.currentThread().interrupt();
					canceled = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = e;
		} finally {
			synchronized (this) {
				working = false;
				File file = exportAnalysisReport.getWorkFile();
				if (file != null && file.exists())
					file.delete();
			}
			if (workersPoolManager != null)
				workersPoolManager.remove(getId());
		}
	}

	public int getIdAnalysis() {
		return idAnalysis;
	}

	public void setIdAnalysis(int idAnalysis) {
		this.idAnalysis = idAnalysis;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
