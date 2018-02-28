/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.util.FileCopyUtils;

import lu.itrust.business.TS.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOReportTemplate;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOReportTemplateHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOWordReportHBM;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.word.ExportReport;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.messagehandler.TaskName;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.document.impl.ReportTemplate;
import lu.itrust.business.TS.model.general.document.impl.WordReport;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
public class WorkerExportWordReport implements Worker {

	private String id = String.valueOf(System.nanoTime());

	private Date started = null;

	private Date finished = null;

	private int idAnalysis;

	private long idTemplate;

	private String username;

	private Exception error;

	private boolean canceled;

	private boolean working;

	private WorkersPoolManager workersPoolManager;

	private SessionFactory sessionFactory;

	private ExportReport wordExporter;

	private Thread current;

	/**
	 * @param idAnalysis
	 * @param templateId
	 * @param username
	 * @param serviceTaskFeedback
	 * @param sessionFactory
	 * @param wordExporter
	 * @param workersPoolManager
	 */
	public WorkerExportWordReport(int idAnalysis, Long templateId, String username, SessionFactory sessionFactory, ExportReport wordExporter,
			WorkersPoolManager workersPoolManager) {
		this.idAnalysis = idAnalysis;
		this.idTemplate = templateId;
		this.username = username;
		this.sessionFactory = sessionFactory;
		this.wordExporter = wordExporter;
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
				started = new Timestamp(System.currentTimeMillis());
				setCurrent(Thread.currentThread());
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

			DAOReportTemplate daoReportTemplate = new DAOReportTemplateHBM(session);
			ReportTemplate reportTemplate = wordExporter.isRefurbished() ? null : daoReportTemplate.findByIdAndCustomerOrDefault(idTemplate, analysis.getCustomer().getId());
			if (!wordExporter.isRefurbished()) {
				if (reportTemplate == null)
					throw new TrickException("error.report.template.not.found", "Report template cannot be found");
				else if (reportTemplate.getFile() == null)
					throw new TrickException("error.report.template.no.data", "Report template has been corrupted");
			}
			wordExporter.setMaxProgress(98);
			wordExporter.setIdTask(id);
			wordExporter.exportToWordDocument(analysis, reportTemplate);
			saveWordDocument(session);
		} catch (TrickException e) {
			wordExporter.getServiceTaskFeedback().send(id, new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), this.error = e));
			TrickLogManager.Persist(e);
		} catch (Exception e) {
			wordExporter.getServiceTaskFeedback().send(id, new MessageHandler("error.unknown.occurred", "An unknown error occurred", this.error = e));
			TrickLogManager.Persist(e);
		} finally {
			try {
				if (session != null && session.isOpen())
					session.close();
			} catch (HibernateException e) {
				TrickLogManager.Persist(e);
			} finally {
				cleanUp();
			}
		}
	}

	private void cleanUp() {
		if (isWorking()) {
			synchronized (this) {
				if (isWorking()) {
					working = false;
					finished = new Timestamp(System.currentTimeMillis());
				}
			}
		}

		wordExporter.close();

		File workFile = wordExporter.getWorkFile();

		if (workFile != null && workFile.exists()) {
			if (!workFile.delete())
				workFile.deleteOnExit();
		}
	}

	private void saveWordDocument(Session session) throws Exception {
		try {
			User user = new DAOUserHBM(session).get(username);
			Analysis analysis = wordExporter.getAnalysis();
			File file = wordExporter.getWorkFile();
			WordReport report = WordReport.BuildReport(analysis.getIdentifier(), analysis.getLabel(), analysis.getVersion(), user, file.getName(), file.length(),
					FileCopyUtils.copyToByteArray(file));
			wordExporter.getServiceTaskFeedback().send(id, new MessageHandler("info.saving.word.report", "Saving word report", 99));
			session.getTransaction().begin();
			new DAOWordReportHBM(session).saveOrUpdate(report);
			session.getTransaction().commit();
			MessageHandler messageHandler = new MessageHandler("success.save.word.report", "Report has been successfully saved", 100);
			messageHandler.setAsyncCallbacks(new AsyncCallback("download", "Report", report.getId()));
			wordExporter.getServiceTaskFeedback().send(id, messageHandler);
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogType.ANALYSIS, "log.analysis.export.word",
					String.format("Analyis: %s, version: %s, type: report", analysis.getIdentifier(), analysis.getVersion()), username, LogAction.EXPORT, analysis.getIdentifier(),
					analysis.getVersion());
		} catch (Exception e) {
			try {
				if (session.getTransaction().getStatus().canRollback())
					session.getTransaction().rollback();
			} catch (Exception e1) {
				TrickLogManager.Persist(e1);
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
			if (isWorking() && !isCanceled()) {
				synchronized (this) {
					if (isWorking() && !isCanceled()) {
						if (getCurrent() == null)
							Thread.currentThread().interrupt();
						else
							getCurrent().interrupt();
						canceled = true;
					}
				}
			}
		} catch (Exception e) {
			TrickLogManager.Persist(error = e);
		} finally {
			cleanUp();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.asynchronousWorkers.Worker#isMatch(java.lang.String ,
	 * java.lang.Object)
	 */
	@Override
	public boolean isMatch(String express, Object... values) {
		try {
			String[] expressions = express.split("\\+");
			boolean match = values.length == expressions.length && values.length == 2;
			for (int i = 0; i < expressions.length && match; i++) {
				switch (expressions[i]) {
				case "analysis.id":
					match &= values[i].equals(idAnalysis);
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

	@Override
	public Date getStarted() {
		return started;
	}

	@Override
	public Date getFinished() {
		return finished;
	}

	@Override
	public TaskName getName() {
		return TaskName.EXPORT_ANALYSIS_REPORT;
	}

	@Override
	public Thread getCurrent() {
		return current;
	}

	private void setCurrent(Thread current) {
		this.current = current;
	}

	public long getIdTemplate() {
		return idTemplate;
	}

	public void setIdTemplate(long idTemplate) {
		this.idTemplate = idTemplate;
	}

}
