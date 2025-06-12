/**
 * 
 */
package lu.itrust.business.ts.asynchronousWorkers;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.ts.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOUserAnalysisRightHBM;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.messagehandler.TaskName;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.helper.AnalysisComparator;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;

/**
 * @author eomar
 *
 */
/**
 * This class represents a worker that restores analysis rights.
 * It extends the `WorkerImpl` class and implements the `Runnable` interface.
 * 
 * The worker is responsible for restoring analysis rights by loading analysis data,
 * sorting it, and updating the rights for each analysis.
 * 
 * The worker can be started by calling the `start()` method, which will execute the `run()` method.
 * It can be canceled by calling the `cancel()` method.
 * 
 * The worker requires a username to be initialized.
 * 
 * This class provides methods for initializing the DAOs, loading analysis data,
 * restoring rights, and handling transactions.
 * 
 * The worker also implements the `isMatch()` method to check if it matches a given expression.
 * 
 * The class provides getter and setter methods for the DAOAnalysis and DAOUserAnalysisRight objects.
 * 
 * The class also overrides the `getName()` method to return the task name as `RESET_ANALYSIS_RIGHT`.
 */
public class WorkerRestoreAnalyisRight extends WorkerImpl {
	
	private String username;

	private DAOAnalysis daoAnalysis;

	private DAOUserAnalysisRight daoUserAnalysisRight;

	private Comparator<Analysis> comparator = new AnalysisComparator();

	/**
	 * @param username
	 * @param poolManager
	 * @param sessionFactory
	 * @param serviceTaskFeedback
	 */
	public WorkerRestoreAnalyisRight(String username) {
		this.username = username;
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

			MessageHandler messageHandler = new MessageHandler("info.initiliase.dao", "Intialise database connectors", 0);
			getServiceTaskFeedback().send(getId(), messageHandler);
			initialiseDAO(session = getSessionFactory().openSession());
			session.beginTransaction();
			Long totalAnalysis = daoAnalysis.countNotProfileDistinctIdentifier();
			int size = 30, totalPage = (int) Math.ceil(totalAnalysis / 30.0);
			messageHandler.update("info.restore.analysis.right", "Restore analysis rights", 1);
			double multi = totalPage / 75.0;
			for (int page = 1; page <= totalPage; page++) {
				loadAnalysis(page, size);
				messageHandler.setProgress((int) Math.floor(page * multi) + 1);
			}
			messageHandler.update("info.commit.transcation", "Commit transaction", 76);
			session.getTransaction().commit();
			messageHandler.update("sucess.restore.analysis.right", "Analysis rights", 100);
		} catch (Exception e) {
			setError(e);
			getServiceTaskFeedback().send(getId(), new MessageHandler("error.unknown.occurred", "An unknown error occurred", e));
			if (session != null && session.getTransaction().getStatus().canRollback())
				session.getTransaction().rollback();
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (HibernateException e) {
				TrickLogManager.persist(e);
			}
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
					}
				}
			}
			getWorkersPoolManager().remove(this);
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
			boolean match = values.length == expressions.length && values.length == 1;
			for (int i = 0; i < expressions.length && match; i++) {
				switch (expressions[i]) {
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

	private void loadAnalysis(int page, int size) throws Exception {
		for (String identifier : daoAnalysis.getNotProfileIdentifiers(page, size)) {
			List<Analysis> analyses = daoAnalysis.getAllByIdentifier(identifier);
			Collections.sort(analyses, comparator.reversed());
			restoreRights(analyses);
		}
	}

	/**
	 * Restores the rights for a list of analyses.
	 *
	 * @param analyses the list of analyses to restore rights for
	 * @throws Exception if an error occurs during the restoration process
	 */
	private void restoreRights(List<Analysis> analyses) throws Exception {
		Analysis previous = null;
		for (Analysis analysis : analyses) {
			if (previous != null) {
				previous.getUserRights().forEach(analysisRight -> {
					UserAnalysisRight userAnalysisRight = analysis.findRightsforUser(analysisRight.getUser());
					if (userAnalysisRight != null && userAnalysisRight.getRight() != analysisRight.getRight()) {
						AnalysisRight old = userAnalysisRight.getRight();
						userAnalysisRight.setRight(analysisRight.getRight());
						/**
						 * Log
						 */
						TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.restore.analysis.right",
								String.format("Analysis: %s, version: %s, old: %s, new: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(), old.toLower(),
										userAnalysisRight.rightToLower(), analysisRight.getUser().getLogin()),
								username, LogAction.RESTORE_ACCESS_RIGHT, analysis.getIdentifier(), analysis.getVersion(), old.name(), userAnalysisRight.getRight().name(),
								analysisRight.getUser().getLogin());
					}
				});
				daoAnalysis.saveOrUpdate(analysis);
			}

			previous = analysis;
		}
	}

	/**
	 * Initializes the DAO objects used by the WorkerRestoreAnalyisRight class.
	 * 
	 * @param session the Hibernate session object used for database operations
	 */
	private void initialiseDAO(Session session) {
		setDaoAnalysis(new DAOAnalysisHBM(session));
		setDaoUserAnalysisRight(new DAOUserAnalysisRightHBM(session));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.asynchronousWorkers.Worker#start()
	 */
	@Override
	public synchronized void start() {
		this.run();
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

	/**
	 * @return the daoAnalysis
	 */
	protected DAOAnalysis getDaoAnalysis() {
		return daoAnalysis;
	}

	/**
	 * @param daoAnalysis the daoAnalysis to set
	 */
	protected void setDaoAnalysis(DAOAnalysis daoAnalysis) {
		this.daoAnalysis = daoAnalysis;
	}

	/**
	 * @return the daoUserAnalysisRight
	 */
	protected DAOUserAnalysisRight getDaoUserAnalysisRight() {
		return daoUserAnalysisRight;
	}

	/**
	 * @param daoUserAnalysisRight the daoUserAnalysisRight to set
	 */
	protected void setDaoUserAnalysisRight(DAOUserAnalysisRight daoUserAnalysisRight) {
		this.daoUserAnalysisRight = daoUserAnalysisRight;
	}

	/**
	 * Represents the name of a task.
	 */
	@Override
	public TaskName getName() {
		return TaskName.RESET_ANALYSIS_RIGHT;
	}

}
