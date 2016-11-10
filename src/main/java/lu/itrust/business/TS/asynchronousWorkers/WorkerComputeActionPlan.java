/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.MessageSource;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOActionPlan;
import lu.itrust.business.TS.database.dao.DAOActionPlanSummary;
import lu.itrust.business.TS.database.dao.DAOActionPlanType;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.hbm.DAOActionPlanHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOActionPlanSummaryHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOActionPlanTypeHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOAssessmentHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOAssetHBM;
import lu.itrust.business.TS.database.dao.hbm.DAORiskProfileHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOScenarioHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.actionplan.helper.ActionPlanComputation;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;

/**
 * @author eomar
 * 
 */
public class WorkerComputeActionPlan extends WorkerImpl {

	private boolean reloadSection = false;

	private DAOActionPlanSummary daoActionPlanSummary;

	private DAOActionPlanType daoActionPlanType;

	private DAOActionPlan daoActionPlan;

	private DAOAnalysis daoAnalysis;

	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	private ServiceTaskFeedback serviceTaskFeedback;

	private int idAnalysis;

	private List<Integer> standards = null;

	private Boolean uncertainty = false;

	private MessageSource messageSource;

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
		assessmentAndRiskProfileManager = new AssessmentAndRiskProfileManager();
		assessmentAndRiskProfileManager.setDaoAnalysis(daoAnalysis);
		assessmentAndRiskProfileManager.setDaoAsset(new DAOAssetHBM(session));
		assessmentAndRiskProfileManager.setDaoScenario(new DAOScenarioHBM(session));
		assessmentAndRiskProfileManager.setDaoAssessment(new DAOAssessmentHBM(session));
		assessmentAndRiskProfileManager.setDaoRiskProfile(new DAORiskProfileHBM(session));

	}

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
	public WorkerComputeActionPlan(WorkersPoolManager poolManager, SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback, int idAnalysis, List<Integer> standards,
			Boolean uncertainty, Boolean reloadSection, MessageSource messageSource) {
		super(poolManager, sessionFactory);
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.idAnalysis = idAnalysis;
		this.standards = standards;
		this.uncertainty = uncertainty;
		this.reloadSection = reloadSection;
		this.messageSource = messageSource;
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
				if (getPoolManager() != null && !getPoolManager().exist(getId()))
					if (!getPoolManager().add(this))
						return;
				if (isCanceled() || isWorking())
					return;
				setWorking(true);
				setStarted(new Timestamp(System.currentTimeMillis()));
			}

			session = getSessionFactory().openSession();
			initialiseDAO(session);

			System.out.println("Loading Analysis...");

			serviceTaskFeedback.send(getId(), new MessageHandler("info.load.analysis", "Analysis is loading", null));
			Analysis analysis = this.daoAnalysis.get(idAnalysis);
			if (analysis == null) {
				serviceTaskFeedback.send(getId(), new MessageHandler("error.analysis.not_found", "Analysis cannot be found", null));
				return;
			}
			session.beginTransaction();

			List<AnalysisStandard> analysisStandards = new ArrayList<AnalysisStandard>();

			initAnalysis(analysis, analysisStandards);

			System.out.println("Delete previous action plan and summary...");

			deleteActionPlan(analysis);

			assessmentAndRiskProfileManager.updateAssessment(analysis, null);

			ActionPlanComputation computation = new ActionPlanComputation(daoActionPlanType, daoAnalysis, serviceTaskFeedback, getId(), analysis, analysisStandards, this.uncertainty,
					this.messageSource);
			if (computation.calculateActionPlans() == null) {
				session.getTransaction().commit();
				MessageHandler messageHandler = new MessageHandler("info.info.action_plan.done", "Computing Action Plans Complete!", 100);
				if (reloadSection)
					messageHandler.setAsyncCallback(new AsyncCallback("reloadSection(['section_actionplans','section_summary','section_soa'])"));
				serviceTaskFeedback.send(getId(), messageHandler);
				System.out.println("Computing Action Plans Complete!");
			} else
				session.getTransaction().rollback();
		} catch (InterruptedException e) {
			try {
				setCanceled(true);
				if (session != null && session.getTransaction().getStatus().canRollback())
					session.getTransaction().rollback();
			} catch (HibernateException e1) {
				TrickLogManager.Persist(e1);
			}
		} catch (TrickException e) {
			try {
				serviceTaskFeedback.send(getId(), new MessageHandler(e.getCode(), e.getParameters(), e.getCode(), e));
				TrickLogManager.Persist(e);
				if (session != null && session.getTransaction().getStatus().canRollback())
					session.getTransaction().rollback();
			} catch (Exception e1) {
				TrickLogManager.Persist(e);
			}
		} catch (Exception e) {
			try {
				serviceTaskFeedback.send(getId(), new MessageHandler("error.analysis.compute.actionPlan", "Action Plan computation was failed", e));
				TrickLogManager.Persist(e);
				if (session != null && session.getTransaction().getStatus().canRollback())
					session.getTransaction().rollback();
			} catch (Exception e1) {
				TrickLogManager.Persist(e);
			}
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

	/**
	 * initAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param analysisStandards
	 */
	private void initAnalysis(Analysis analysis, List<AnalysisStandard> analysisStandards) {
		Hibernate.initialize(analysis);
		Hibernate.initialize(analysis.getLanguage());
		Hibernate.initialize(analysis.getHistories());
		Hibernate.initialize(analysis.getAssets());
		Hibernate.initialize(analysis.getScenarios());
		Hibernate.initialize(analysis.getAssessments());
		Hibernate.initialize(analysis.getItemInformations());
		Hibernate.initialize(analysis.getRiskInformations());
		Hibernate.initialize(analysis.getParameters());
		Hibernate.initialize(analysis.getPhases());
		Hibernate.initialize(analysis.getAnalysisStandards());

		for (Integer id : this.standards) {
			for (AnalysisStandard aStandard : analysis.getAnalysisStandards()) {
				if (aStandard.getId() == id)
					analysisStandards.add(aStandard);
			}
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

		serviceTaskFeedback.send(getId(), new MessageHandler("info.analysis.delete.action_plan.summary", "Action Plan summary is deleting", null));

		while (!analysis.getSummaries().isEmpty())
			daoActionPlanSummary.delete(analysis.getSummaries().remove(analysis.getSummaries().size() - 1));

		serviceTaskFeedback.send(getId(), new MessageHandler("info.analysis.delete.action_plan", "Action Plan is deleting", null));

		while (!analysis.getActionPlans().isEmpty())
			daoActionPlan.delete(analysis.getActionPlans().remove(analysis.getActionPlans().size() - 1));

		serviceTaskFeedback.send(getId(), new MessageHandler("info.analysis.clear.soa", "Erasing of SOA", null));

		analysis.getAnalysisStandards().stream().filter(standard -> standard.getStandard().getLabel().equals(Constant.STANDARD_27002)).map(standard -> standard.getMeasures())
				.findFirst().ifPresent(measures -> measures.forEach(measure -> ((NormalMeasure) measure).getMeasurePropertyList().setSoaRisk("")));
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
						setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
			TrickLogManager.Persist(e);
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
