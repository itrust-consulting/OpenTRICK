/**
 * 
 */
package lu.itrust.business.task;

import java.io.File;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.actionplan.ActionPlanComputation;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.service.ServiceActionPlan;
import lu.itrust.business.service.ServiceActionPlanSummary;
import lu.itrust.business.service.ServiceActionPlanType;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.WorkersPoolManager;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author eomar
 * 
 */
public class WorkerComputeActionPlan implements Worker {

	private long id = System.nanoTime();

	private Exception error;

	private boolean working = false;

	private boolean canceled = false;

	private WorkersPoolManager poolManager;

	private ServiceActionPlanSummary serviceActionPlanSummary;

	private ServiceActionPlanType serviceActionPlanType;

	private ServiceActionPlan serviceActionPlan;

	private ServiceAnalysis serviceAnalysis;

	private ServiceTaskFeedback serviceTaskFeedback;

	private int idAnalysis;

	/**
	 * @param serviceActionPlanSummary
	 * @param serviceActionPlanType
	 * @param serviceActionPlan
	 * @param serviceAnalysis
	 * @param serviceTaskFeedback
	 * @param idAnalysis
	 */
	public WorkerComputeActionPlan(ServiceActionPlanSummary serviceActionPlanSummary, ServiceActionPlanType serviceActionPlanType, ServiceActionPlan serviceActionPlan,
			ServiceAnalysis serviceAnalysis, ServiceTaskFeedback serviceTaskFeedback, int idAnalysis) {
		this.serviceActionPlanSummary = serviceActionPlanSummary;
		this.serviceActionPlanType = serviceActionPlanType;
		this.serviceActionPlan = serviceActionPlan;
		this.serviceAnalysis = serviceAnalysis;
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.idAnalysis = idAnalysis;
	}

	/**
	 * @param poolManager
	 * @param serviceActionPlanSummary
	 * @param serviceActionPlanType
	 * @param serviceActionPlan
	 * @param serviceAnalysis
	 * @param serviceTaskFeedback
	 * @param idAnalysis
	 */
	public WorkerComputeActionPlan(WorkersPoolManager poolManager, ServiceActionPlanSummary serviceActionPlanSummary, ServiceActionPlanType serviceActionPlanType,
			ServiceActionPlan serviceActionPlan, ServiceAnalysis serviceAnalysis, ServiceTaskFeedback serviceTaskFeedback, int idAnalysis) {
		this.poolManager = poolManager;
		this.serviceActionPlanSummary = serviceActionPlanSummary;
		this.serviceActionPlanType = serviceActionPlanType;
		this.serviceActionPlan = serviceActionPlan;
		this.serviceAnalysis = serviceAnalysis;
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.idAnalysis = idAnalysis;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Transactional
	@Override
	public void run() {
		try {
			synchronized (this) {
				if (poolManager != null && !poolManager.exist(getId()))
					if (!poolManager.add(this))
						return;
				if (canceled || working)
					return;
				working = true;
			}
			serviceTaskFeedback.send(id, new MessageHandler("info.load.analysis", "Analysis is loading", null));
			Analysis analysis = this.serviceAnalysis.get(idAnalysis);
			if (analysis == null) {
				serviceTaskFeedback.send(id, new MessageHandler("error.analysis.not_found", "Analysis cannot be found", null));
				return;
			}
			deleteActionPlan(analysis);
			ActionPlanComputation computation = new ActionPlanComputation(serviceActionPlanType, serviceAnalysis, serviceTaskFeedback, id, analysis);
			computation.calculateActionPlans();
		} catch (InterruptedException e) {
			canceled = true;
		} catch (Exception e) {
			serviceTaskFeedback.send(id, new MessageHandler("error.analysis.compute.actionPlan", "Action Plan computation was failed", e));
		} finally {
			synchronized (this) {
				working = false;
			}
			if (poolManager != null)
				poolManager.remove(getId());
		}
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
			serviceActionPlanSummary.remove(analysis.getSummaries().remove(analysis.getSummaries().size() - 1));

		serviceTaskFeedback.send(id, new MessageHandler("info.analysis.delete.actionPlan", "Action Plan is deleting", null));

		while (!analysis.getActionPlans().isEmpty())
			serviceActionPlan.delete(analysis.getActionPlans().remove(analysis.getActionPlans().size() - 1));
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
