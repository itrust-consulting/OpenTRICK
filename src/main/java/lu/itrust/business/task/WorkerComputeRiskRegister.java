package lu.itrust.business.task;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.cssf.RiskRegisterComputation;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.component.helper.AsyncCallback;
import lu.itrust.business.dao.DAOAnalysis;
import lu.itrust.business.dao.DAORiskRegister;
import lu.itrust.business.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.dao.hbm.DAORiskRegisterHBM;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.WorkersPoolManager;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/** 
 * WorkerComputeRiskRegister.java: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.à.rl.
 * @version 
 * @since Feb 17, 2014
 */
public class WorkerComputeRiskRegister implements Worker {

	private long id = System.nanoTime();

	private Exception error;

	private boolean working = false;

	private boolean canceled = false;

	private boolean reloadSection = false;

	private WorkersPoolManager poolManager;

	private DAORiskRegister daoRiskRegister;

	private DAOAnalysis daoAnalysis;

	private ServiceTaskFeedback serviceTaskFeedback;

	private SessionFactory sessionFactory;

	private int idAnalysis;

	/**
	 * Constructor: <br>
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
	 * @param norms
	 * @param uncertainty
	 */
	public WorkerComputeRiskRegister(WorkersPoolManager poolManager, SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback, int idAnalysis, Boolean reloadSection) {
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
			}
			session = sessionFactory.openSession();
			initialiseDAO(session);

			System.out.println("Loading Analysis...");

			serviceTaskFeedback.send(id, new MessageHandler("info.load.analysis", "Analysis is loading", null));
			Analysis analysis = this.daoAnalysis.get(idAnalysis);
			if (analysis == null) {
				serviceTaskFeedback.send(id, new MessageHandler("error.analysis.not_found", "Analysis cannot be found", null));
				return;
			}
			session.beginTransaction();
			initAnalysis(analysis);

			System.out.println("Delete previous risk register...");

			deleteRiskRegister(analysis);
			RiskRegisterComputation computation = new RiskRegisterComputation(analysis);
			if (computation.computeRiskRegister() == null) {
				session.getTransaction().commit();
				MessageHandler messageHandler = new MessageHandler("info.info.risk_register.done", "Computing Risk Register Complete!", 100);
				if (reloadSection)
					messageHandler.setAsyncCallback(new AsyncCallback("reloadSection(\"section_riskregister\")", null));
				serviceTaskFeedback.send(id, messageHandler);
				System.out.println("Computing Risk Register Complete!");
			} else
				session.getTransaction().rollback();
		} catch (InterruptedException e) {
			try {
				canceled = true;
				if (session != null && session.getTransaction().isInitiator())
					session.getTransaction().rollback();
			} catch (HibernateException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			try {
				serviceTaskFeedback.send(id, new MessageHandler("error.analysis.compute.riskregister", "Risk register computation failed: "+e.getMessage(), e));
				e.printStackTrace();
				if (session != null && session.getTransaction().isInitiator())
					session.getTransaction().rollback();
			} catch (HibernateException e1) {
				e1.printStackTrace();
			}
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

	/**
	 * initAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 */
	private void initAnalysis(Analysis analysis) {
		Hibernate.initialize(analysis);
		Hibernate.initialize(analysis.getLanguage());
		Hibernate.initialize(analysis.getHistories());
		Hibernate.initialize(analysis.getAssets());
		Hibernate.initialize(analysis.getScenarios());
		Hibernate.initialize(analysis.getAssessments());
		Hibernate.initialize(analysis.getItemInformations());
		Hibernate.initialize(analysis.getRiskInformations());
		Hibernate.initialize(analysis.getParameters());
		Hibernate.initialize(analysis.getUsedPhases());
		Hibernate.initialize(analysis.getAnalysisNorms());
	}

	/**
	 * deleteRiskRegister: <br>
	 * Description
	 * 
	 * @param analysis
	 * @throws Exception
	 */
	private void deleteRiskRegister(Analysis analysis) throws Exception {

		serviceTaskFeedback.send(id, new MessageHandler("info.analysis.delete.riskregister", "Risk Register is deleting", 50));

		while (!analysis.getRiskRegisters().isEmpty())
			daoRiskRegister.delete(analysis.getRiskRegisters().remove(analysis.getRiskRegisters().size() - 1));

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
	public void setId(Long id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.task.Worker#setPoolManager(lu.itrust.business.service
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
	public Long getId() {
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
			}
			if (poolManager != null)
				poolManager.remove(getId());
		}
	}

}
