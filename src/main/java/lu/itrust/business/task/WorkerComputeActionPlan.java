/**
 * 
 */
package lu.itrust.business.task;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.actionplan.ActionPlanComputation;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.component.helper.AsyncCallback;
import lu.itrust.business.dao.DAOActionPlan;
import lu.itrust.business.dao.DAOActionPlanSummary;
import lu.itrust.business.dao.DAOActionPlanType;
import lu.itrust.business.dao.DAOAnalysis;
import lu.itrust.business.dao.hbm.DAOActionPlanHBM;
import lu.itrust.business.dao.hbm.DAOActionPlanSummaryHBM;
import lu.itrust.business.dao.hbm.DAOActionPlanTypeHBM;
import lu.itrust.business.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.WorkersPoolManager;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @author eomar
 * 
 */
public class WorkerComputeActionPlan implements Worker {

	private long id = System.nanoTime();

	private Exception error;

	private boolean working = false;

	private boolean canceled = false;

	private boolean reloadSection = false;

	private WorkersPoolManager poolManager;

	private DAOActionPlanSummary daoActionPlanSummary;

	private DAOActionPlanType daoActionPlanType;

	private DAOActionPlan daoActionPlan;

	private DAOAnalysis daoAnalysis;

	private ServiceTaskFeedback serviceTaskFeedback;

	private SessionFactory sessionFactory;

	private int idAnalysis;

	private List<AnalysisNorm> norms = null;

	private Boolean uncertainty = false;

	/**
	 * @param daoActionPlanSummary
	 * @param daoActionPlanType
	 * @param daoActionPlan
	 * @param daoAnalysis
	 * @param serviceTaskFeedback
	 * @param idAnalysis
	 */
	public WorkerComputeActionPlan(SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback, int idAnalysis, List<AnalysisNorm> norms, Boolean uncertainty,
			Boolean reloadSection) {
		this.sessionFactory = sessionFactory;
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.idAnalysis = idAnalysis;
		this.norms = norms;
		this.uncertainty = uncertainty;
		this.reloadSection = reloadSection;
	}

	/**
	 * initialiseDAO: <br>
	 * Description
	 * 
	 * @param session
	 */
	private void initialiseDAO(Session session) {
		daoActionPlan = new DAOActionPlanHBM(session);
		daoActionPlanSummary = new DAOActionPlanSummaryHBM(session);
		daoActionPlanType = new DAOActionPlanTypeHBM(session);
		daoAnalysis = new DAOAnalysisHBM(session);
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
	public WorkerComputeActionPlan(WorkersPoolManager poolManager, SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback, int idAnalysis, List<AnalysisNorm> norms,
			Boolean uncertainty, Boolean reloadSection) {
		this.sessionFactory = sessionFactory;
		this.poolManager = poolManager;
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.idAnalysis = idAnalysis;
		this.norms = norms;
		this.uncertainty = uncertainty;
		this.reloadSection = reloadSection;
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

			System.out.println("Delete previous action plan and summary...");

			deleteActionPlan(analysis);
			ActionPlanComputation computation = new ActionPlanComputation(daoActionPlanType, daoAnalysis, serviceTaskFeedback, id, analysis, this.norms, this.uncertainty);
			if (computation.calculateActionPlans() == null) {
				session.getTransaction().commit();
				MessageHandler messageHandler = new MessageHandler("info.info.action_plan.done", "Computing Action Plans Complete!", 100);
				if (reloadSection)
					messageHandler.setAsyncCallback(new AsyncCallback("reloadSection(\"section_actionplans\")", null));
				serviceTaskFeedback.send(id, messageHandler);
				System.out.println("Computing Action Plans Complete!");
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
				serviceTaskFeedback.send(id, new MessageHandler("error.analysis.compute.actionPlan", "Action Plan computation was failed", e));
				e.printStackTrace();
				if (session != null && session.getTransaction().isInitiator())
					session.getTransaction().rollback();
			} catch (Exception e1) {
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
		// for (int i = 0; i < analysis.getUsedPhases().size(); i++)
		// Hibernate.initialize(analysis.getAPhase(i));
		Hibernate.initialize(analysis.getAnalysisNorms());
		Hibernate.initialize(this.norms);
		for (int i = 0; i < this.norms.size(); i++)
			Hibernate.initialize(this.norms.get(i).getNorm());
		// Hibernate.initialize(analysis.getActionPlans());
		// Hibernate.initialize(analysis.getSummaries());

		// Hibernate.initialize(analysis.getRiskRegisters());
	}

	/**
	 * deleteActionPlan: <br>
	 * Description
	 * 
	 * @param analysis
	 * @throws Exception
	 */
	private void deleteActionPlan(Analysis analysis) throws Exception {

		serviceTaskFeedback.send(id, new MessageHandler("info.analysis.delete.actionPlan", "Action Plan summary is deleting", null));

		while (!analysis.getSummaries().isEmpty())
			daoActionPlanSummary.delete(analysis.getSummaries().remove(analysis.getSummaries().size() - 1));

		serviceTaskFeedback.send(id, new MessageHandler("info.analysis.delete.actionPlan", "Action Plan is deleting", null));

		while (!analysis.getActionPlans().isEmpty())
			daoActionPlan.delete(analysis.getActionPlans().remove(analysis.getActionPlans().size() - 1));
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
