package lu.itrust.business.TS.asynchronousWorkers;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAORiskRegister;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAORiskRegisterHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.cssf.RiskRegisterComputation;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;

/**
 * WorkerComputeRiskRegister.java: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since Feb 17, 2014
 */
public class WorkerComputeRiskRegister implements Worker {

	private String id = String.valueOf(System.nanoTime());

	private Date started = null;

	private Date finished = null;

	private Exception error;

	private boolean working = false;

	private boolean canceled = false;

	private boolean reloadSection = false;

	private WorkersPoolManager poolManager;

	private DAORiskRegister daoRiskRegister;

	private DAOAnalysis daoAnalysis;

	private ServiceTaskFeedback serviceTaskFeedback;

	private SessionFactory sessionFactory;

	/**
	 * Key: asset.id_scenerio.id<br>
	 * Value: [0] owner, [1] strategy
	 */
	private Map<String, RiskRegisterItem> ownerBackup;

	private int idAnalysis;

	/**
	 * Constructor: <br>
	 * 
	 * @param sessionFactory
	 * @param serviceTaskFeedback
	 * @param idAnalysis
	 * @param reloadSection
	 */
	public WorkerComputeRiskRegister(SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback, int idAnalysis, Boolean reloadSection) {
		this.sessionFactory = sessionFactory;
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.idAnalysis = idAnalysis;
		this.reloadSection = reloadSection;
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param poolManager
	 * @param sessionFactory
	 * @param serviceTaskFeedback
	 * @param idAnalysis
	 * @param reloadSection
	 */
	public WorkerComputeRiskRegister(WorkersPoolManager poolManager, SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback, int idAnalysis,
			Boolean reloadSection) {
		this.sessionFactory = sessionFactory;
		this.poolManager = poolManager;
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.idAnalysis = idAnalysis;
		this.reloadSection = reloadSection;
	}

	/**
	 * initialiseDAO: <br>
	 * Description
	 * 
	 * @param session
	 */
	private void initialiseDAO(Session session) {
		daoRiskRegister = new DAORiskRegisterHBM(session);
		daoAnalysis = new DAOAnalysisHBM(session);
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
			initialiseDAO(session);
			System.out.println("Loading Analysis...");
			String lang = this.daoAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2();
			serviceTaskFeedback.send(id, new MessageHandler("info.load.analysis", "Analysis is loading", 1));
			Analysis analysis = this.daoAnalysis.get(idAnalysis);
			if (analysis == null) {
				serviceTaskFeedback.send(id, new MessageHandler("error.analysis.not_found", "Analysis cannot be found", null));
				return;
			}
			session.beginTransaction();
			System.out.println("Saving user changes...");
			backup(analysis, session, lang);
			RiskRegisterComputation computation = new RiskRegisterComputation(analysis);
			serviceTaskFeedback.send(id, new MessageHandler("info.risk_register.compute", "Computing Risk Register", 20));
			if (computation.computeRiskRegister() == null) {
				restoreOwner(analysis, lang);
				serviceTaskFeedback.send(id, new MessageHandler("info.risk_register.saving", "Saving Risk Register to database", 72));
				daoAnalysis.saveOrUpdate(analysis);
				serviceTaskFeedback.send(id, new MessageHandler("info.commit.transcation", "Commit transaction", 80));
				session.getTransaction().commit();
				MessageHandler messageHandler = new MessageHandler("info.info.risk_register.done", "Computing Risk Register Complete!", 100);
				if (reloadSection)
					messageHandler.setAsyncCallback(new AsyncCallback("reloadSection", "section_riskregister"));
				serviceTaskFeedback.send(id, messageHandler);
				System.out.println("Computing Risk Register Complete!");
			} else
				session.getTransaction().rollback();
		} catch (InterruptedException e) {
			try {
				canceled = true;
				if (session != null && session.getTransaction().getStatus().canRollback())
					session.getTransaction().rollback();
			} catch (HibernateException e1) {
				TrickLogManager.Persist(e1);
			}
		} catch (TrickException e) {
			try {
				serviceTaskFeedback.send(id, new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
				TrickLogManager.Persist(e);
				if (session != null && session.getTransaction().getStatus().canRollback())
					session.getTransaction().rollback();
			} catch (HibernateException e1) {
				TrickLogManager.Persist(e1);
			}
		} catch (Exception e) {
			try {

				try {
					serviceTaskFeedback.send(id, new MessageHandler("error.analysis.compute.riskregister", "Risk register computation failed: " + e.getMessage(), e));
				} catch (Exception e1) {
					serviceTaskFeedback.send(id, new MessageHandler("error.analysis.compute.riskregister", "Risk register computation failed: " + e.getMessage(), e));
				}
				TrickLogManager.Persist(e);
				if (session != null && session.getTransaction().getStatus().canRollback())
					session.getTransaction().rollback();
			} catch (HibernateException e1) {
				TrickLogManager.Persist(e1);
			}
		} finally {
			try {
				if (session != null && session.isOpen())
					session.close();
			} catch (HibernateException e) {
				TrickLogManager.Persist(e);
			} catch (Exception e) {
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

	private void restoreOwner(Analysis analysis, String lang) throws Exception {
		if (ownerBackup == null)
			return;
		serviceTaskFeedback.send(id, new MessageHandler("info.risk_register.restore", "Restoring user's changes", 70));
		for (int i = 0; i < analysis.getRiskRegisters().size(); i++) {
			RiskRegisterItem riskRegister = analysis.getRiskRegisters().get(i);
			RiskRegisterItem riskRegisterItem = ownerBackup.remove(String.format("%d_%d", riskRegister.getAsset().getId(), riskRegister.getScenario().getId()));
			if (riskRegisterItem != null)
				analysis.getRiskRegisters().set(i, riskRegisterItem.merge(riskRegister));
		}
		serviceTaskFeedback.send(id, new MessageHandler("info.risk_register.delete", "Deleting previous Risk Register", 70));
		for (RiskRegisterItem riskRegisterItem : ownerBackup.values())
			daoRiskRegister.delete(riskRegisterItem);
	}

	/**
	 * deleteRiskRegister: <br>
	 * Description
	 * 
	 * @param analysis
	 * @throws Exception
	 */
	private void backup(Analysis analysis, Session session, String lang) throws Exception {
		if (!analysis.getRiskRegisters().isEmpty()) {
			serviceTaskFeedback.send(id, new MessageHandler("info.risk_register.backup", "Backup of user changes", 10));
			ownerBackup = new LinkedHashMap<String, RiskRegisterItem>(analysis.getRiskRegisters().size());
			analysis.getRiskRegisters()
					.forEach(riskRegister -> ownerBackup.put(String.format("%d_%d", riskRegister.getAsset().getId(), riskRegister.getScenario().getId()), riskRegister));
		}
		analysis.getRiskRegisters().clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.task.Worker#isWorking()
	 */
	@Override
	public boolean isWorking() {
		return working;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.task.Worker#isCanceled()
	 */
	@Override
	public boolean isCanceled() {
		return this.canceled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.task.Worker#getError()
	 */
	@Override
	public Exception getError() {
		return error;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.task.Worker#setId(java.lang.Long)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.task.Worker#setPoolManager(lu.itrust.business.service
	 * .WorkersPoolManager)
	 */
	@Override
	public void setPoolManager(WorkersPoolManager poolManager) {
		this.poolManager = poolManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.task.Worker#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.task.Worker#start()
	 */
	@Override
	public synchronized void start() {
		run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.task.Worker#cancel()
	 */
	@Override
	public void cancel() {
		try {
			if (isWorking() && !isCanceled()) {
				synchronized (this) {
					if (isWorking() && !isCanceled()) {
						Thread.currentThread().interrupt();
						canceled = true;
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

}
