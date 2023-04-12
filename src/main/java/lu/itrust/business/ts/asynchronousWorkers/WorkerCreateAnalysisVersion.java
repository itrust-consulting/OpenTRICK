/**
 * 
 */
package lu.itrust.business.ts.asynchronousWorkers;

import java.sql.Timestamp;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.ts.component.Duplicator;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.dao.DAOIDS;
import lu.itrust.business.ts.database.dao.hbm.DAOIDSHBM;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.history.History;

/**
 * @author eomar
 *
 */
public class WorkerCreateAnalysisVersion extends WorkerImpl{

	private DAOIDS daoIDS;

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
	public WorkerCreateAnalysisVersion(int idAnalysis, History history, String userName) {
		this.idAnalysis = idAnalysis;
		this.history = history;
		this.userName = userName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.asynchronousWorkers.Worker#isMatch(java.lang.String
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

			daoIDS = new DAOIDSHBM(session);

			Duplicator duplicator = new Duplicator(session);

			session.getTransaction().begin();

			Analysis analysis = duplicator.getDaoAnalysis().get(idAnalysis);

			if (analysis == null)
				getServiceTaskFeedback().send(getId(), new MessageHandler("error.analysis.not_exist", "Analysis not found", 0));
			else {

				Analysis copy = duplicator.duplicateAnalysis(analysis, null, getServiceTaskFeedback(), getId(), 5, 95);

				getServiceTaskFeedback().send(getId(), new MessageHandler("info.analysis.update.setting", "Update analysis settings", 95));

				copy.setBasedOnAnalysis(analysis);

				copy.addAHistory(history);

				copy.setVersion(history.getVersion());

				copy.setLabel(analysis.getLabel());

				copy.setCreationDate(new Timestamp(System.currentTimeMillis()));

				copy.setProfile(false);

				copy.setDefaultProfile(false);
				
				copy.setArchived(false);

				UserAnalysisRight userAnalysisRight = copy.findRightsforUserString(userName);

				copy.setOwner(userAnalysisRight.getUser());

				userAnalysisRight.setRight(AnalysisRight.ALL);

				getServiceTaskFeedback().send(getId(), new MessageHandler("info.saving.analysis", "Saving analysis", 96));

				duplicator.getDaoAnalysis().saveOrUpdate(copy);

				getServiceTaskFeedback().send(getId(), new MessageHandler("info.commit.transcation", "Commit transaction", 98));

				daoIDS.getByAnalysis(analysis).forEach(ids -> {
					ids.getSubscribers().add(copy);
					daoIDS.saveOrUpdate(ids);
				});

				session.getTransaction().commit();

				MessageHandler handler = new MessageHandler("success.saving.analysis", "Analysis has been successfully saved", 100);
				
				handler.setAsyncCallbacks(new AsyncCallback("reloadSection", "section_analysis","",false,true));//addTop
				
				getServiceTaskFeedback().send(getId(), handler);
				/**
				 * Log
				 */
				TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.create.analysis.version",
						String.format("Analysis: %s, version: %s, new version: (%s)", analysis.getIdentifier(), analysis.getVersion(), copy.getVersion()), userName,
						LogAction.CREATE, analysis.getIdentifier(), analysis.getVersion(), copy.getVersion());
			}

		} catch (InterruptedException e) {
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.task.interrupted", "Task has been interrupted", 0));
			rollback(session);
		} catch (TrickException e) {
			setError(e);
			getServiceTaskFeedback().send(getId(), new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
			rollback(session);
		} catch (Exception e) {
			setError(e);
			rollback(session);
			getServiceTaskFeedback().send(getId(), new MessageHandler("error.analysis.duplicate", "An unknown error occurred while copying analysis", 0));
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
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
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
	 * @see lu.itrust.business.ts.asynchronousWorkers.Worker#start()
	 */
	@Override
	public void start() {
		run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.asynchronousWorkers.Worker#cancel()
	 */
	@Override
	public void cancel() {
		try {
			if (isWorking() && !isCanceled()) {
				synchronized (this) {
					if (isWorking() && !isCanceled()) {
						if(getCurrent() == null)
							Thread.currentThread().interrupt();
						else getCurrent().interrupt();
						setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
			setError(e);
		} finally {
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
					}
				}
			}
		}
	}


}
