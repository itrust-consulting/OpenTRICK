/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import lu.itrust.business.TS.component.Duplicator;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOIDS;
import lu.itrust.business.TS.database.dao.hbm.DAOIDSHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.messagehandler.TaskName;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.history.History;

/**
 * @author eomar
 *
 */
public class WorkerCreateAnalysisVersion implements Worker {

	private String id = String.valueOf(System.nanoTime());

	private Date started = null;

	private Date finished = null;

	private Exception error;

	private boolean working = false;

	private boolean canceled = false;

	private WorkersPoolManager poolManager;

	private SessionFactory sessionFactory;

	private DAOIDS daoIDS;

	private ServiceTaskFeedback serviceTaskFeedback;

	private int idAnalysis;

	private History history;

	private String userName;

	/**
	 * @param idAnalysis
	 * @param history
	 * @param userName
	 * @param serviceTaskFeedback
	 * @param sessionFactory
	 * @param poolManager
	 */
	public WorkerCreateAnalysisVersion(int idAnalysis, History history, String userName, ServiceTaskFeedback serviceTaskFeedback, SessionFactory sessionFactory,
			WorkersPoolManager poolManager) {
		this.idAnalysis = idAnalysis;
		this.history = history;
		this.userName = userName;
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.sessionFactory = sessionFactory;
		this.poolManager = poolManager;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Session session = null;
		try {

			synchronized (this) {
				if (poolManager != null && !poolManager.exist(getId()))
					if (!poolManager.add(this))
						return;
				if (canceled || working)
					return;
				working = true;
				started = new Timestamp(System.currentTimeMillis());
			}

			session = sessionFactory.openSession();

			daoIDS = new DAOIDSHBM(session);

			Duplicator duplicator = new Duplicator(session);

			session.getTransaction().begin();

			Analysis analysis = duplicator.getDaoAnalysis().get(idAnalysis);

			if (analysis == null)
				serviceTaskFeedback.send(id, new MessageHandler("error.analysis.not_exist", "Analysis not found", 0));
			else {

				Analysis copy = duplicator.duplicateAnalysis(analysis, null, serviceTaskFeedback, id, 5, 95);

				serviceTaskFeedback.send(id, new MessageHandler("info.analysis.update.setting", "Update analysis settings", 95));

				copy.setBasedOnAnalysis(analysis);

				copy.addAHistory(history);

				copy.setVersion(history.getVersion());

				copy.setLabel(analysis.getLabel());

				copy.setCreationDate(new Timestamp(System.currentTimeMillis()));

				copy.setProfile(false);

				copy.setDefaultProfile(false);
				
				copy.setArchived(false);

				UserAnalysisRight userAnalysisRight = copy.getRightsforUserString(userName);

				copy.setOwner(userAnalysisRight.getUser());

				userAnalysisRight.setRight(AnalysisRight.ALL);

				serviceTaskFeedback.send(id, new MessageHandler("info.saving.analysis", "Saving analysis", 96));

				duplicator.getDaoAnalysis().saveOrUpdate(copy);

				serviceTaskFeedback.send(id, new MessageHandler("info.commit.transcation", "Commit transaction", 98));

				daoIDS.getByAnalysis(analysis).forEach(ids -> {
					ids.getSubscribers().add(copy);
					daoIDS.saveOrUpdate(ids);
				});

				session.getTransaction().commit();

				MessageHandler handler = new MessageHandler("success.saving.analysis", "Analysis has been successfully saved", 100);
				
				handler.setAsyncCallbacks(new AsyncCallback("reloadSection", "section_analysis",null,null,true));//addTop
				
				serviceTaskFeedback.send(id, handler);
				/**
				 * Log
				 */
				TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.create.analysis.version",
						String.format("Analysis: %s, version: %s, new version: (%s)", analysis.getIdentifier(), analysis.getVersion(), copy.getVersion()), userName,
						LogAction.CREATE, analysis.getIdentifier(), analysis.getVersion(), copy.getVersion());
			}

		} catch (InterruptedException e) {
			serviceTaskFeedback.send(id, new MessageHandler("info.task.interrupted", "Task has been interrupted", 0));
			rollback(session);
		} catch (TrickException e) {
			serviceTaskFeedback.send(id, new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), error = e));
			rollback(session);
			TrickLogManager.Persist(e);
		} catch (Exception e) {
			rollback(session);
			serviceTaskFeedback.send(id, new MessageHandler("error.analysis.duplicate", "An unknown error occurred while copying analysis", 0));
			TrickLogManager.Persist(e);
		} finally {
			try {
				if (session != null && session.isOpen())
					session.close();
			} catch (HibernateException e) {
				TrickLogManager.Persist(e);
			}
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						working = false;
						finished = new Timestamp(System.currentTimeMillis());
					}
				}
			}
		}
	}

	protected void rollback(Session session) {
		try {
			if (session != null && session.isOpen() && session.getTransaction().getStatus().canRollback())
				session.getTransaction().rollback();
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#isWorking()
	 */
	@Override
	public boolean isWorking() {
		return working;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#isCanceled()
	 */
	@Override
	public boolean isCanceled() {
		return canceled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#getError()
	 */
	@Override
	public Exception getError() {
		return error;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.asynchronousWorkers.Worker#setId(java.lang.Long)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.asynchronousWorkers.Worker#setPoolManager(lu.itrust
	 * .business.TS.database.service.WorkersPoolManager)
	 */
	@Override
	public void setPoolManager(WorkersPoolManager poolManager) {
		this.poolManager = poolManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#start()
	 */
	@Override
	public void start() {
		run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#cancel()
	 */
	@Override
	public void cancel() {
		try {
			if (isWorking() && !isCanceled()) {
				synchronized (this) {
					if (isWorking() && !isCanceled()) {
						canceled = true;
						Thread.currentThread().interrupt();
					}
				}
			}
		} catch (Exception e) {
			TrickLogManager.Persist(error = e);
		} finally {
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						working = false;
						finished = new Timestamp(System.currentTimeMillis());
					}
				}
			}
		}
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
		return TaskName.CREATE_ANALYSIS_VERSION;
	}

}
