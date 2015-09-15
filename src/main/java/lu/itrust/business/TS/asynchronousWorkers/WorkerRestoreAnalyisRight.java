/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOUserAnalysisRightHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.AnalysisComparator;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @author eomar
 *
 */
public class WorkerRestoreAnalyisRight implements Worker {

	private String id = String.valueOf(System.nanoTime());

	private Exception error;

	private boolean working = false;

	private boolean canceled = false;

	private WorkersPoolManager poolManager;

	private SessionFactory sessionFactory;

	private DAOAnalysis daoAnalysis;

	private DAOUserAnalysisRight daoUserAnalysisRight;

	private Comparator<Analysis> comparator = new AnalysisComparator();

	private ServiceTaskFeedback serviceTaskFeedback;

	private String username;

	/**
	 * @param username
	 * @param poolManager
	 * @param sessionFactory
	 * @param serviceTaskFeedback
	 */
	public WorkerRestoreAnalyisRight(String username, WorkersPoolManager poolManager, SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback) {
		this.username = username;
		this.poolManager = poolManager;
		this.sessionFactory = sessionFactory;
		this.serviceTaskFeedback = serviceTaskFeedback;
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
			}

			MessageHandler messageHandler = new MessageHandler("info.initiliase.dao", "Intialise database connectors", null, 0);
			serviceTaskFeedback.send(getId(), messageHandler);
			initialiseDAO(session = sessionFactory.openSession());
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
			if (session != null && session.getTransaction().isInitiator())
				session.getTransaction().rollback();
			serviceTaskFeedback.send(getId(), new MessageHandler("error.unknown.occurred", "An unknown error occurred", null, e));
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (HibernateException e) {
				e.printStackTrace();
			}
			synchronized (this) {
				working = false;
			}
			if (poolManager != null)
				poolManager.remove(getId());

		}

	}
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#isMatch(java.lang.String, java.lang.Object)
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

	private void restoreRights(List<Analysis> analyses) throws Exception {
		Analysis previous = null;
		for (Analysis analysis : analyses) {
			if (previous != null) {
				previous.getUserRights().forEach(
						analysisRight -> {
							UserAnalysisRight userAnalysisRight = analysis.getRightsforUser(analysisRight.getUser());
							if (userAnalysisRight != null && userAnalysisRight.getRight() != analysisRight.getRight()) {
								AnalysisRight old = userAnalysisRight.getRight();
								userAnalysisRight.setRight(analysisRight.getRight());
								/**
								 * Log
								 */
								TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.restore.analysis.right", String.format(
										"Analysis: %s, version: %s, old: %s, new: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(), old.toLower(),
										userAnalysisRight.rightToLower(), analysisRight.getUser().getLogin()), username, LogAction.RESTORE_ACCESS_RIGHT, analysis.getIdentifier(),
										analysis.getVersion(), old.name(), userAnalysisRight.getRight().name(), analysisRight.getUser().getLogin());
							}
						});
				daoAnalysis.saveOrUpdate(analysis);
			}

			previous = analysis;
		}
	}

	private void initialiseDAO(Session session) {
		setDaoAnalysis(new DAOAnalysisHBM(session));
		setDaoUserAnalysisRight(new DAOUserAnalysisRightHBM(session));
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
	 * lu.itrust.business.TS.asynchronousWorkers.Worker#setId(java.lang.String)
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
	public synchronized void start() {
		this.run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#cancel()
	 */
	@Override
	public void cancel() {
		try {
			if (working) {
				synchronized (this) {
					if (working) {
						Thread.currentThread().interrupt();
						canceled = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = e;
		} finally {
			synchronized (this) {
				working = false;
			}
			if (poolManager != null)
				poolManager.remove(getId());
		}
	}

	/**
	 * @return the daoAnalysis
	 */
	protected DAOAnalysis getDaoAnalysis() {
		return daoAnalysis;
	}

	/**
	 * @param daoAnalysis
	 *            the daoAnalysis to set
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
	 * @param daoUserAnalysisRight
	 *            the daoUserAnalysisRight to set
	 */
	protected void setDaoUserAnalysisRight(DAOUserAnalysisRight daoUserAnalysisRight) {
		this.daoUserAnalysisRight = daoUserAnalysisRight;
	}

}
