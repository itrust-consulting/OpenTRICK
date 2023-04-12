/**
 * 
 */
package lu.itrust.business.ts.asynchronousWorkers;

import java.io.File;
import java.sql.Timestamp;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.util.FileCopyUtils;

import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOReportTemplate;
import lu.itrust.business.ts.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOReportTemplateHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOWordReportHBM;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.exportation.word.ExportReport;
import lu.itrust.business.ts.helper.Task;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.messagehandler.TaskName;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.ExportFileName;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.document.impl.ReportTemplate;
import lu.itrust.business.ts.model.general.document.impl.WordReport;
import lu.itrust.business.ts.model.general.helper.Utils;
import lu.itrust.business.ts.usermanagement.User;

/**
 * @author eomar
 *
 */
public class WorkerExportWordReport extends WorkerImpl {

	/**
	 *
	 */

	private int idAnalysis;

	private long idTemplate;

	private String username;

	private ExportReport exportReport;

	/**
	 * @param idAnalysis
	 * @param templateId
	 * @param username
	 * @param serviceTaskFeedback
	 * @param sessionFactory
	 * @param string
	 * @param serviceTaskFeedback
	 * @param messageSource
	 * @param workersPoolManager2
	 * @param wordExporter
	 * @param workersPoolManager
	 */
	public WorkerExportWordReport(int idAnalysis, Long templateId, String username, ExportReport exportReport) {
		this.username = username;
		this.idAnalysis = idAnalysis;
		this.idTemplate = templateId;
		this.exportReport = exportReport;
	}

	@Override
	public void run() {
		Session session = null;
		try {
			synchronized (this) {
				if (getWorkersPoolManager() != null && !getWorkersPoolManager().exist(getId()))
					if (!getWorkersPoolManager().add(this))
						return;
				if (isCanceled() || isWorking())
					return;
				setWorking(true);
				setStarted(new Timestamp(System.currentTimeMillis()));
				setCurrent(Thread.currentThread());
			}

			session = getSessionFactory().openSession();
			DAOAnalysis daoAnalysis = new DAOAnalysisHBM(session);
			Analysis analysis = daoAnalysis.get(idAnalysis);
			if (analysis == null)
				throw new TrickException("error.analysis.not_exist", "Analysis not found");
			else if (analysis.isProfile())
				throw new TrickException("error.analysis.is_profile", "Profile cannot be exported as report");
			else if (!analysis.hasData())
				throw new TrickException("error.analysis.no_data", "Empty analysis cannot be exported");
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.export.report.prepare.document",
					"Please wait while preparing word document", 0));
			final DAOReportTemplate daoReportTemplate = new DAOReportTemplateHBM(session);
			final ReportTemplate reportTemplate = exportReport.getFile() != null ? null
					: daoReportTemplate.findByIdAndCustomerOrDefault(idTemplate, analysis.getCustomer().getId());
			if (exportReport.getFile() == null) {
				if (reportTemplate == null)
					throw new TrickException("error.report.template.not.found", "Report template cannot be found");
				else if (reportTemplate.getData() == null)
					throw new TrickException("error.report.template.no.data", "Report template has been corrupted");
			}
			exportReport.export(reportTemplate, new Task(getId(), 1, 98), analysis, getServiceTaskFeedback());
			saveWordDocument(session);
		} catch (TrickException e) {
			setError(e);
			getServiceTaskFeedback().send(getId(),
					new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
		} catch (Exception e) {
			setError(e);
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("error.unknown.occurred", "An unknown error occurred", e));
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
					setWorking(false);
					setFinished(new Timestamp(System.currentTimeMillis()));
				}
			}
		}
		exportReport.close();
	}

	private void saveWordDocument(Session session) throws Exception {
		try {
			final User user = new DAOUserHBM(session).get(username);
			final Analysis analysis = exportReport.getAnalysis();
			final File file = exportReport.getFile();

			final String filename = String.format(Constant.ITR_FILE_NAMING_WIHT_CTRL,
			Utils.cleanUpFileName(analysis.findSetting(ExportFileName.REPORT)),
					Utils.cleanUpFileName(analysis.getCustomer().getOrganisation()),
					Utils.cleanUpFileName(analysis.getLabel()), "Report", analysis.getVersion(),
					"docx",System.nanoTime());

			WordReport report = WordReport.BuildReport(analysis.getIdentifier(), analysis.getLabel(),
					analysis.getVersion(), user,
					filename, file.length(),
					FileCopyUtils.copyToByteArray(file));
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.saving.word.report", "Saving word report", 99));
			session.getTransaction().begin();
			new DAOWordReportHBM(session).saveOrUpdate(report);
			session.getTransaction().commit();
			MessageHandler messageHandler = new MessageHandler("success.save.word.report",
					"Report has been successfully saved", 100);
			messageHandler.setAsyncCallbacks(new AsyncCallback("download", "Report", report.getId()));
			getServiceTaskFeedback().send(getId(), messageHandler);
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogType.ANALYSIS, "log.analysis.export.word",
					String.format("Analyis: %s, version: %s, type: report", analysis.getIdentifier(),
							analysis.getVersion()),
					username, LogAction.EXPORT, analysis.getIdentifier(),
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
						setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
			setError(e);
		} finally {
			cleanUp();
		}
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
	public TaskName getName() {
		return TaskName.EXPORT_ANALYSIS_REPORT;
	}

	public long getIdTemplate() {
		return idTemplate;
	}

	public void setIdTemplate(long idTemplate) {
		this.idTemplate = idTemplate;
	}

	public ExportReport getExportReport() {
		return exportReport;
	}

	public void setExportReport(ExportReport exportReport) {
		this.exportReport = exportReport;
	}
}
