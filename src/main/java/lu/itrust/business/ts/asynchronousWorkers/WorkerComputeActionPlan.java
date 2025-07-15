/**
 * 
 */
package lu.itrust.business.ts.asynchronousWorkers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.ts.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.dao.DAOActionPlan;
import lu.itrust.business.ts.database.dao.DAOActionPlanSummary;
import lu.itrust.business.ts.database.dao.DAOActionPlanType;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAORiskRegister;
import lu.itrust.business.ts.database.dao.impl.DAOActionPlanImpl;
import lu.itrust.business.ts.database.dao.impl.DAOActionPlanSummaryImpl;
import lu.itrust.business.ts.database.dao.impl.DAOActionPlanTypeImpl;
import lu.itrust.business.ts.database.dao.impl.DAOAnalysisImpl;
import lu.itrust.business.ts.database.dao.impl.DAOAssessmentImpl;
import lu.itrust.business.ts.database.dao.impl.DAOAssetImpl;
import lu.itrust.business.ts.database.dao.impl.DAORiskProfileImpl;
import lu.itrust.business.ts.database.dao.impl.DAORiskRegisterImpl;
import lu.itrust.business.ts.database.dao.impl.DAOScenarioImpl;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.messagehandler.TaskName;
import lu.itrust.business.ts.model.actionplan.helper.ActionPlanComputation;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.cssf.RiskRegisterItem;
import lu.itrust.business.ts.model.cssf.helper.CSSFFilter;
import lu.itrust.business.ts.model.cssf.helper.RiskSheetComputation;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.measure.AbstractNormalMeasure;


/**
 * WorkerComputeActionPlan is a class that represents a worker responsible for computing the action plan for a given analysis.
 * It extends the WorkerImpl class and implements the Runnable interface.
 * 
 * The worker performs the following tasks:
 * - Loads the analysis from the database
 * - Deletes the previous action plan and summary
 * - Updates the assessment and risk profile
 * - Computes the action plans using the ActionPlanComputation class
 * - Saves the computed action plans and the analysis
 * - Sends feedback messages to the service task feedback
 * 
 * The worker can be canceled and can be matched with specific expressions and values.
 * 
 * Usage:
 * WorkerComputeActionPlan worker = new WorkerComputeActionPlan(idAnalysis, standards, uncertainty, reloadSection);
 * worker.start();
 * 
 * Note: This class assumes the existence of other classes and dependencies such as AssessmentAndRiskProfileManager,
 * DAOActionPlan, DAOActionPlanSummary, DAOActionPlanType, DAOAnalysis, DAORiskRegister, and others.
 */
public class WorkerComputeActionPlan extends WorkerImpl {

	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	private DAOActionPlan daoActionPlan;

	private DAOActionPlanSummary daoActionPlanSummary;

	private DAOActionPlanType daoActionPlanType;

	private DAOAnalysis daoAnalysis;

	private DAORiskRegister daoRiskRegister;

	private int idAnalysis;

	private Map<String, RiskRegisterItem> oldRiskRegisters;

	private boolean reloadSection = false;

	private List<Integer> standards = null;

	private Boolean uncertainty = false;

	/**
	 * Constructor: <br>
	 * 
	 * @param poolManager
	 * @param sessionFactory
	 * @param serviceTaskFeedback
	 * @param idAnalysis
	 * @param standards
	 * @param uncertainty
	 */
	public WorkerComputeActionPlan(int idAnalysis, List<Integer> standards, Boolean uncertainty,
			Boolean reloadSection) {
		this.idAnalysis = idAnalysis;
		this.standards = standards;
		this.uncertainty = uncertainty;
		this.reloadSection = reloadSection;
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
						if (getCurrent() == null)
							Thread.currentThread().interrupt();
						else
							getCurrent().interrupt();
						setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
			TrickLogManager.persist(e);
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
				setName(TaskName.COMPUTE_ACTION_PLAN);
				setCurrent(Thread.currentThread());
			}

			session = getSessionFactory().openSession();
			initialiseDAO(session);

			System.out.println("Loading Analysis...");

			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.load.analysis", "Analysis is loading", null));
			Analysis analysis = this.daoAnalysis.get(idAnalysis);
			if (analysis == null) {
				getServiceTaskFeedback().send(getId(),
						new MessageHandler("error.analysis.not_found", "Analysis cannot be found", null));
				return;
			}
			session.beginTransaction();

			List<AnalysisStandard> analysisStandards = new ArrayList<>();

			initAnalysis(analysis, analysisStandards);

			System.out.println("Delete previous action plan and summary...");

			deleteActionPlan(analysis);

			assessmentAndRiskProfileManager.updateAssessment(analysis, null);

			ActionPlanComputation computation = new ActionPlanComputation(daoActionPlanType, getServiceTaskFeedback(),
					getId(), analysis, analysisStandards, this.uncertainty,
					this.getMessageSource());
			if (computation.calculateActionPlans() == null) {
				MessageHandler messageHandler = null;
				if (analysis.isHybrid()) {
					saveRiskRegister(analysis);
					if ((messageHandler = computeRiskRegister(analysis)) == null)
						updateRiskRegister(analysis.getRiskRegisters());
					else
						throw new TrickException(messageHandler.getCode(), messageHandler.getMessage(),
								messageHandler.getException(), messageHandler.getParameters());
				}
				getServiceTaskFeedback().send(getId(),
						new MessageHandler("info.info.action_plan.saved", "Saving Action Plans", 95));
				daoAnalysis.saveOrUpdate(analysis);
				session.getTransaction().commit();
				messageHandler = new MessageHandler("info.info.action_plan.done", "Computing Action Plans Complete!",
						100);
				if (reloadSection)
					messageHandler.setAsyncCallbacks(communsCallback(analysis.isHybrid()));
				getServiceTaskFeedback().send(getId(), messageHandler);
				System.out.println("Computing Action Plans Complete!");
			} else
				session.getTransaction().rollback();
		} catch (InterruptedException e) {
			try {
				setCanceled(true);
				if (session != null && session.getTransaction().getStatus().canRollback())
					session.getTransaction().rollback();
			} catch (HibernateException e1) {
				TrickLogManager.persist(e1);
			}
		} catch (TrickException e) {
			try {
				getServiceTaskFeedback().send(getId(),
						new MessageHandler(e.getCode(), e.getParameters(), e.getCode(), e));
				TrickLogManager.persist(e);
				if (session != null && session.getTransaction().getStatus().canRollback())
					session.getTransaction().rollback();
			} catch (Exception e1) {
				TrickLogManager.persist(e);
			}
		} catch (Exception e) {
			try {
				getServiceTaskFeedback().send(getId(), new MessageHandler("error.analysis.compute.actionPlan",
						"Action Plan computation was failed", e));
				TrickLogManager.persist(e);
				if (session != null && session.getTransaction().getStatus().canRollback())
					session.getTransaction().rollback();
			} catch (Exception e1) {
				TrickLogManager.persist(e);
			}
		} finally {
			try {
				if (session != null && session.isOpen())
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

		}
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

	/**
	 * Returns an array of AsyncCallback objects for common operations.
	 * 
	 * @param isHybrid a boolean indicating whether the operation is hybrid or not
	 * @return an array of AsyncCallback objects
	 */
	private AsyncCallback[] communsCallback(boolean isHybrid) {
		AsyncCallback[] callbacks = new AsyncCallback[isHybrid ? 5 : 4];
		callbacks[0] = new AsyncCallback("reloadSection", "section_actionplans");
		callbacks[1] = new AsyncCallback("reloadSection", "section_summary");
		callbacks[2] = new AsyncCallback("reloadSection", "section_soa");
		callbacks[3] = new AsyncCallback("reloadSection", "section_chart");
		if (isHybrid)
			callbacks[4] = new AsyncCallback("riskEstimationUpdate", true);
		return callbacks;
	}

	/**
	 * Represents a message handler that processes messages in the system.
	 */
	private MessageHandler computeRiskRegister(Analysis analysis) {
		return new RiskSheetComputation(analysis).computeRiskRegister(new CSSFFilter(-1, -1, -1, 0, 0));
	}

	/**
	 * deleteActionPlan: <br>
	 * Description
	 * 
	 * @param analysis
	 * @throws Exception
	 */
	private void deleteActionPlan(Analysis analysis) throws Exception {

		getServiceTaskFeedback().send(getId(), new MessageHandler("info.analysis.delete.action_plan.summary",
				"Action Plan summary is deleting", null));

		while (!analysis.getSummaries().isEmpty())
			daoActionPlanSummary.delete(analysis.getSummaries().remove(0));

		getServiceTaskFeedback().send(getId(),
				new MessageHandler("info.analysis.delete.action_plan", "Action Plan is deleting", null));

		while (!analysis.getActionPlans().isEmpty())
			daoActionPlan.delete(analysis.getActionPlans().remove(0));

		getServiceTaskFeedback().send(getId(), new MessageHandler("info.analysis.clear.soa", "Erasing of SOA", null));

		analysis.getAnalysisStandards().values().stream().filter(AnalysisStandard::isSoaEnabled)
				.flatMap(analysisStandard -> analysisStandard.getMeasures().stream())
				.filter(e -> e instanceof AbstractNormalMeasure
						&& !e.getStatus().equals(Constant.MEASURE_STATUS_EXCLUDE))
				.map(AbstractNormalMeasure.class::cast)
				.forEach(measure -> measure.getMeasurePropertyList().setSoaRisk(""));
	}

	/**
	 * initAnalysis: <br>
	 * order measure by phase + initialise collection.
	 * 
	 * @param analysis
	 * @param analysisStandards
	 */
	private void initAnalysis(Analysis analysis, List<AnalysisStandard> analysisStandards) {
		for (Integer id : this.standards) {
			for (AnalysisStandard aStandard : analysis.getAnalysisStandards().values()) {
				if (aStandard.getId() == id)
					analysisStandards.add(aStandard);
			}
		}
	}

	
	/**
	 * Initializes the Data Access Objects (DAOs) used by the WorkerComputeActionPlan class.
	 * 
	 * @param session the session object used for database operations
	 */
	private void initialiseDAO(Session session) {
		daoActionPlan = new DAOActionPlanImpl(session);
		daoActionPlanSummary = new DAOActionPlanSummaryImpl(session);
		daoActionPlanType = new DAOActionPlanTypeImpl(session);
		daoAnalysis = new DAOAnalysisImpl(session);
		daoRiskRegister = new DAORiskRegisterImpl(session);
		assessmentAndRiskProfileManager = new AssessmentAndRiskProfileManager();
		assessmentAndRiskProfileManager.setDaoAnalysis(daoAnalysis);
		assessmentAndRiskProfileManager.setDaoAsset(new DAOAssetImpl(session));
		assessmentAndRiskProfileManager.setDaoScenario(new DAOScenarioImpl(session));
		assessmentAndRiskProfileManager.setDaoAssessment(new DAOAssessmentImpl(session));
		assessmentAndRiskProfileManager.setDaoRiskProfile(new DAORiskProfileImpl(session));

	}

	/**
	 * Saves the risk register of the given analysis.
	 * 
	 * @param analysis the analysis object containing the risk registers
	 */
	private void saveRiskRegister(Analysis analysis) {
		oldRiskRegisters = analysis.getRiskRegisters().stream()
				.collect(Collectors.toMap(RiskRegisterItem::getKey, Function.identity()));
		analysis.getRiskRegisters().clear();
	}

	/**
	 * Updates the risk register with the given list of register items.
	 * 
	 * @param registerItems the list of register items to update the risk register with
	 */
	private void updateRiskRegister(List<RiskRegisterItem> registerItems) {
		if (oldRiskRegisters == null || oldRiskRegisters.isEmpty())
			return;
		for (int i = 0; i < registerItems.size(); i++) {
			RiskRegisterItem registerItem = registerItems.get(i),
					oldRegisterItem = oldRiskRegisters.remove(registerItem.getKey());
			if (oldRegisterItem == null)
				continue;
			registerItems.set(i, oldRegisterItem.merge(registerItem));
		}

		oldRiskRegisters.values().stream().forEach(riskRegisterItem -> daoRiskRegister.delete(riskRegisterItem));

	}
}
