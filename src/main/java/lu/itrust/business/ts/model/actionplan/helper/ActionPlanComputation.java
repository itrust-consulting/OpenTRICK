package lu.itrust.business.ts.model.actionplan.helper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.naming.directory.InvalidAttributesException;

import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.dao.DAOActionPlanType;
import lu.itrust.business.ts.database.service.ServiceTaskFeedback;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.model.actionplan.ActionPlanAsset;
import lu.itrust.business.ts.model.actionplan.ActionPlanEntry;
import lu.itrust.business.ts.model.actionplan.ActionPlanMode;
import lu.itrust.business.ts.model.actionplan.ActionPlanType;
import lu.itrust.business.ts.model.actionplan.summary.computation.ISummaryComputation;
import lu.itrust.business.ts.model.actionplan.summary.computation.impl.SummaryComputationQualitative;
import lu.itrust.business.ts.model.actionplan.summary.computation.impl.SummaryComputationQuantitative;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.cssf.RiskProfile;
import lu.itrust.business.ts.model.general.Phase;
import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.parameter.impl.MaturityParameter;
import lu.itrust.business.ts.model.rrf.RRF;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.AssetStandard;
import lu.itrust.business.ts.model.standard.MaturityStandard;
import lu.itrust.business.ts.model.standard.NormalStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.ts.model.standard.measure.impl.MaturityMeasure;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureProperties;

/**
 * ActionPlanComputation: <br>
 * This class is used to calculate the action plan for an Analysis. This class
 * is also used to generate the TMAList (Threat - Measure - Asset Triples). This
 * class will initialize the Lists of ActionPlan Entries inside the Analysis
 * class (The final Action Plans) as well as the Summary for each Action Plans.
 * After the Action Plans are calculated, this class will save the results to
 * the MySQL Database.
 * 
 * @author itrust consulting s.à.rl. 
 * @version 0.1
 * @since 9 janv. 2013
 */
public class ActionPlanComputation {

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	private DAOActionPlanType serviceActionPlanType;

	private ServiceTaskFeedback serviceTaskFeedback;

	/** task id */
	private String idTask;

	/** Analysis Object */
	private Analysis analysis;

	private Locale locale;

	/** List of standards to compute */
	private List<AnalysisStandard> standards;

	/** uncertainty computation flag */
	private boolean uncertainty;

	/** maturity computation computation flag */
	private boolean maturitycomputation;

	private boolean normalcomputation;

	private MessageSource messageSource;

	private List<Phase> phases = new ArrayList<>();

	private NumberFormat numberFormat = NumberFormat.getInstance(Locale.FRANCE);

	private ValueFactory factory;

	/***********************************************************************************************
	 * Constructor
	 **********************************************************************************************/

	/**
	 * Constructor: This creates an object and takes as parameter an loaded
	 * Analysis.
	 * 
	 * @param analysis The Analysis Object
	 */
	public ActionPlanComputation(Analysis analysis) {
		this.analysis = analysis;
	}

	/**
	 * Constructor
	 */
	public ActionPlanComputation() {
	}

	/**
	 * ActionPlanComputation: constructor that takes the service actionplantype and
	 * serviceAnalysis ( to get all nessesary data for computation) and the anaylsis
	 * object itself as parameters.
	 * 
	 * @param serviceActionPlanType
	 * @param sericeAnalysis
	 * @param analysis
	 */
	public ActionPlanComputation(DAOActionPlanType serviceActionPlanType, Analysis analysis) {
		this.serviceActionPlanType = serviceActionPlanType;
		this.analysis = analysis;
	}

	/**
	 * ActionPlanComputation: constructor that takes the service actionplantype and
	 * serviceAnalysis ( to get all nessesary data for computation) , standards to
	 * compute, uncertainty flag, the analysis and the task parameters for
	 * asynchronous actionplan computation.
	 * 
	 * Inside this constructor, the standards will be determined to compute as well
	 * as the uncertainty and maturity computation flag.
	 * 
	 * @param serviceActionPlanType
	 * @param serviceTaskFeedback
	 * @param idTask
	 * @param analysis
	 * @param standards
	 * @param uncertainty
	 * @param messageSource
	 */
	public ActionPlanComputation(DAOActionPlanType serviceActionPlanType, ServiceTaskFeedback serviceTaskFeedback,
			String idTask, Analysis analysis,
			List<AnalysisStandard> standards, boolean uncertainty, MessageSource messageSource) {

		// initialise variables
		this.serviceActionPlanType = serviceActionPlanType;
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.idTask = idTask;
		this.analysis = analysis;
		this.messageSource = messageSource;
		// check if standards to compute is empty -> YES: take all standards;
		// NO: use
		// only the standards given
		if (analysis.isQuantitative()) {

			if (standards == null || standards.isEmpty())
				this.standards = this.analysis.findAllAnalysisStandard();
			else
				this.standards = standards;

			if (this.standards.stream().anyMatch(analysisStandard -> analysisStandard instanceof MaturityStandard
					&& analysisStandard.getStandard().isComputable()) && this.standards.stream()
							.noneMatch(checkStandard -> checkStandard.getStandard().is(Constant.STANDARD_27002))) {

				AnalysisStandard analysisStandard = analysis.getAnalysisStandards().get(Constant.STANDARD_27002);
				if (analysisStandard != null)
					this.standards.add(analysisStandard);

			}
			this.uncertainty = uncertainty;
		}
	}

	/***********************************************************************************************
	 * Methods
	 **********************************************************************************************/

	/**
	 * calculateActionPlans: <br>
	 * This method calculates all Action Plans and all Summaries and stores the
	 * Results into the Database.
	 * 
	 * This method is parted into 3 areas:<br>
	 * <br>
	 * <ul>
	 * <li>Action Plan Computation Normal and Uncertainty and Phase</li>
	 * <li>Summary Computation</li>
	 * <li>Action Plan Storage Into the Database</li>
	 * </ul>
	 * Action Plans:
	 * <ul>
	 * <li>Normal</li>
	 * <li>Optimistic</li>
	 * <li>Pessimistic</li>
	 * <li>Phase Normal</li>
	 * <li>Phase Optimistic</li>
	 * <li>Phase Pessimistic</li>
	 * </ul>
	 * 
	 * @return True: on success; False on failure
	 */
	public MessageHandler calculateActionPlans() {

		try {
			if (locale == null)
				locale = new Locale(this.analysis.getLanguage().getAlpha2());
			// initialise task feedback progress in percentage to return to the
			// user
			int progress = uncertainty ? 10 : 20;
			// check if uncertainty to adopt the progress factor
			numberFormat.setMaximumFractionDigits(2);
			// send feedback
			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.action_plan.computing", "Computing Action Plans", progress));

			System.out.println("Computing Action Plans...");

			factory = new ValueFactory(this.analysis.getExpressionParameters());

			// preImplementedMeasures = new MaintenanceRecurrentInvestment();

			// Reset previously computed action plans
			// This is needed to assure that the action plan list is actually
			// empty
			analysis.setActionPlans(new ArrayList<>(0));

			if (analysis.isQuantitative())
				progress = quantitativeActionPlan();
			if (analysis.isQualitative())
				progress = qualitativeActionPlan();

			// send feedback

			return null;
		} catch (TrickException e) {
			TrickLogManager.persist(e);
			MessageHandler messageHandler = new MessageHandler(e);
			serviceTaskFeedback.send(idTask, messageHandler);
			return messageHandler;
		} catch (Exception e) {
			System.out.println("Action Plan saving failed! ");
			MessageHandler messageHandler = new MessageHandler(e.getMessage(), "Action Plan saving failed", e);
			serviceTaskFeedback.send(idTask, messageHandler);
			TrickLogManager.persist(e);
			// return messagehandler with errors
			return messageHandler;
		}
	}

	/**
	 * Computes the qualitative action plan based on the risk profiles and measures in the analysis.
	 * This method generates an action plan from the risk profile and assigns positions and orders to the action plan entries.
	 * It also creates a summary for the normal phase action plan summary.
	 *
	 * @return The progress percentage of the action plan computation.
	 * @throws Exception If there is an error during the computation.
	 */
	private int qualitativeActionPlan() throws Exception {
		int position[] = { 0 };
		// get actionplantype by given mode
		ActionPlanType actionPlanType = serviceActionPlanType.get(ActionPlanMode.APQ.getValue());
		// check if the actionplantype exists and add it to database if not
		if (actionPlanType == null)
			serviceActionPlanType.saveOrUpdate(actionPlanType = new ActionPlanType(ActionPlanMode.APQ));

		serviceTaskFeedback.send(idTask,
				new MessageHandler("info.info.action_plan.generation", "Generation action plan from risk profile", 10));

		Map<Integer, AnalysisStandard> tmpAnalysisStandards = new LinkedHashMap<>();

		Map<String, ActionPlanEntry> actionPlanEntries = new LinkedHashMap<>();
		for (RiskProfile riskProfile : analysis.getRiskProfiles()) {
			for (Measure measure : riskProfile.getMeasures()) {
				if (measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
						|| measure.getStatus().equals(Constant.MEASURE_STATUS_EXCLUDE))
					continue;
				ActionPlanEntry entry = actionPlanEntries.get(measure.getKey());
				if (entry == null) {
					AnalysisStandard persisteStandard = analysis.getAnalysisStandards()
							.get(measure.getMeasureDescription().getStandard().getName());
					if (persisteStandard == null)
						continue;
					AnalysisStandard analysisStandard = tmpAnalysisStandards.get(persisteStandard.getId());
					if (analysisStandard == null) {
						if (persisteStandard instanceof AssetStandard)
							tmpAnalysisStandards.put(persisteStandard.getId(), analysisStandard = new AssetStandard(
									persisteStandard.getStandard()));
						else if (persisteStandard instanceof NormalStandard)
							tmpAnalysisStandards.put(persisteStandard.getId(), analysisStandard = new NormalStandard(
									persisteStandard.getStandard()));
						else
							continue;
						analysisStandard.setId(persisteStandard.getId());
					}
					analysisStandard.getMeasures().add(measure);
					if (measure.getImplementationRateValue(factory) >= 100)
						continue;
					actionPlanEntries.put(measure.getKey(), entry = new ActionPlanEntry(measure, actionPlanType, 0));
				}
				entry.setRiskCount(entry.getRiskCount() + 1);
			}
		}

		serviceTaskFeedback.send(idTask,
				new MessageHandler("info.info.action_plan.generation", "Generation action plan from risk profile", 60));

		actionPlanEntries.values().stream().sorted(qualitativeComparator()).forEach(actionPlan -> {
			actionPlan.setPosition(position[0]++);
			actionPlan.setOrder((actionPlan.getPosition() + 1) + "");
			analysis.getActionPlans().add(actionPlan);
		});

		// send feedback
		serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.create_summary.normal_phase",
				"Create summary for normal phase action plan summary", 70));

		new SummaryComputationQualitative(analysis, new LinkedList<>(tmpAnalysisStandards.values()), factory)
				.compute(ActionPlanMode.APQ);

		return 95;
	}

	private Comparator<? super ActionPlanEntry> qualitativeComparator() {
		return (a1, a2) -> {
			int comp = Integer.compare(a1.getMeasure().getPhase().getNumber(), a2.getMeasure().getPhase().getNumber());
			if (comp == 0) {
				comp = Integer.compare(a2.getRiskCount(), a1.getRiskCount());
				if (comp == 0) {
					comp = NaturalOrderComparator.compareTo(
							a1.getMeasure().getMeasureDescription().getStandard().getName(),
							a2.getMeasure().getMeasureDescription().getStandard().getName());
					if (comp == 0)
						comp = NaturalOrderComparator.compareTo(a1.getMeasure().getMeasureDescription().getReference(),
								a2.getMeasure().getMeasureDescription().getReference());

				}
			}
			return comp;
		};
	}

	/**
	 * Calculates the quantitative action plan.
	 *
	 * @return The progress of the action plan computation.
	 * @throws Exception if an error occurs during the computation.
	 */
	protected int quantitativeActionPlan() throws Exception {

		ISummaryComputation summaryComputation = new SummaryComputationQuantitative(analysis, factory, standards);
		setPhases(summaryComputation.getPhases());
		// ***************************************************************
		// * compute Action Plan - normal mode - Phase //
		// ***************************************************************
		System.out.println("compute Action Plan - normal mode - Phase");

		int progress = uncertainty ? 20 : 40;

		// send feedback
		serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.phase.normal_mode",
				"Compute Action Plan - normal mode - Phase", progress));

		if (normalcomputation) {
			computeActionPlan(ActionPlanMode.APN);
			if (uncertainty) {
				computeActionPlan(ActionPlanMode.APP);
				computeActionPlan(ActionPlanMode.APO);
			}
		}

		// compute
		computePhaseActionPlan(ActionPlanMode.APPN);

		// ****************************************************************
		// * compute Action Plan - optimistic mode - Phase
		// ****************************************************************

		// check if uncertainty to adopt the progress factor and computation
		// (if not, optimisitc
		// and pessimistic will not be computed)
		if (uncertainty) {
			progress = 30;

			System.out.println("compute Action Plan - optimistic mode - Phase");

			// send feedback
			serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.phase.optimistic_mode",
					"Compute Action Plan - optimistic mode - Phase", progress));

			// compute
			computePhaseActionPlan(ActionPlanMode.APPO);

			// ****************************************************************
			// * compute Action Plan - pessimistic mode - Phase
			// ****************************************************************

			// update progress
			progress += 10;

			System.out.println("compute Action Plan - pessimistic mode - Phase");

			// send feedback
			serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.phase.pessimistic_mode",
					"Compute Action Plan -  pessimistic mode - Phase", progress));

			// compute
			computePhaseActionPlan(ActionPlanMode.APPP);
		}
		// *********************************************************************
		// * set positions relative to normal action plan for all action
		// plans
		// *********************************************************************

		System.out.println("Calculating positions...");

		// update progress
		if (uncertainty)
			progress = 50;
		else
			progress = 60;

		// send feedback
		serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.determinepositions",
				"Compute Action Plan -  computing positions", progress));

		// compute
		determinePositions();

		// ****************************************************************
		// * create summary for normal phase action plan summary //
		// ****************************************************************

		System.out.println("compute Summary of Action Plan - normal mode - Phase");

		// update progress
		if (uncertainty)
			progress = 60;
		else
			progress = 80;

		// send feedback
		serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.create_summary.normal_phase",
				"Create summary for normal phase action plan summary", progress));

		if (normalcomputation) {
			summaryComputation.compute(ActionPlanMode.APN);
			if (uncertainty) {
				summaryComputation.compute(ActionPlanMode.APP);
				summaryComputation.compute(ActionPlanMode.APO);
			}
		}

		// compute
		summaryComputation.compute(ActionPlanMode.APPN);

		// check if uncertainty for optimisitc and pessimistic compputations
		if (uncertainty) {

			// update progress
			progress = 70;

			System.out.println("compute Summary of Action Plan - optimistic mode - Phase");

			// ****************************************************************
			// * create summary for optimistic phase action plan summary
			// ****************************************************************

			// send feedback
			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.info.action_plan.create_summary.optimistic_phase",
							"Create summary for optimistic phase action plan summary", progress));

			// compute
			summaryComputation.compute(ActionPlanMode.APPO);

			// update progress
			progress += 10;

			System.out.println("compute Summary of Action Plan - pessimistic mode - Phase");

			// ****************************************************************
			// * create summary for pessimistic phase action plan summary
			// ****************************************************************
			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.info.action_plan.create_summary.pessimistic_phase",
							"Create summary for pessimistic phase action plan summary", progress));

			// compute
			summaryComputation.compute(ActionPlanMode.APPP);

		}

		// ****************************************************************
		// * Store action plans into database
		// ****************************************************************

		System.out.println("Saving Action Plans...");

		// update progress
		if (uncertainty)
			progress = 90;
		else
			progress = 95;

		return progress;
	}

	/**
	 * determinePositions: <br>
	 * Calculates the Position of each Action Plan Entry refered to the Normal
	 * Action Plan Calculation
	 * 
	 * @throws TrickException
	 */
	private void determinePositions() throws TrickException {

		// ****************************************************************
		// initialise variable
		// ****************************************************************
		String position = "";
		List<ActionPlanEntry> actionPlan = this.analysis.findActionPlan(ActionPlanMode.APN);
		List<ActionPlanEntry> actionPlanO = this.analysis.findActionPlan(ActionPlanMode.APO);
		List<ActionPlanEntry> actionPlanP = this.analysis.findActionPlan(ActionPlanMode.APP);
		List<ActionPlanEntry> phaseActionPlan = this.analysis.findActionPlan(ActionPlanMode.APPN);
		List<ActionPlanEntry> phaseActionPlanO = this.analysis.findActionPlan(ActionPlanMode.APPO);
		List<ActionPlanEntry> phaseActionPlanP = this.analysis.findActionPlan(ActionPlanMode.APPP);

		// ****************************************************************
		// * APN - Action Plan Normal
		// ****************************************************************

		// parse all entries of the action plan
		for (int i = 0; i < actionPlan.size(); i++) {

			// set correct position
			actionPlan.get(i).setOrder(String.valueOf(i + 1));
		}

		// ****************************************************************
		// * APPN - Action Plan Phase Normal
		// ****************************************************************

		// parse all entries of the action plan
		for (int i = 0; i < phaseActionPlan.size(); i++) {

			// set correct position
			phaseActionPlan.get(i).setOrder(String.valueOf(i + 1));
		}

		// ****************************************************************
		// * APO - Action Plan Optimistic
		// ****************************************************************

		// parse all entries of the Optimistic action plan
		for (int i = 0; i < actionPlanO.size(); i++) {

			// parse all entries of the normal action plan
			for (int j = 0; j < actionPlan.size(); j++) {

				// check if the entry matches the one from the normal action
				// plan -> YES
				if (actionPlan.get(j).getMeasure().equals(actionPlanO.get(i).getMeasure())) {

					// check if the value is more than 0 -> YES
					if (Integer.valueOf(actionPlan.get(j).getPosition()) - (i + 1) > 0) {

						// set position with + sign
						position = "+" + (Integer.valueOf(actionPlan.get(j).getPosition()) - (i + 1));
					} else {

						// check if the value is more than 0 -> NO

						// check if the value is less than 0 -> YES
						if (Integer.valueOf(actionPlan.get(j).getPosition()) - (i + 1) < 0) {

							// set position
							position = String.valueOf(Integer.valueOf(actionPlan.get(j).getPosition()) - (i + 1));
						} else {

							// check if the value is less than 0 -> NO
							// make an even sign
							position = "=";
						}
					}

					// add the position ot the action plan
					actionPlanO.get(i).setOrder(position);
				}
			}
		}

		// ****************************************************************
		// * APP - Action Plan Pessimistic
		// ****************************************************************

		// parse all entries of the pessimistic action plan
		for (int i = 0; i < actionPlanP.size(); i++) {

			// parse all entries of the normal action plan
			for (int j = 0; j < actionPlan.size(); j++) {

				// check if the entry matches the one from the normal action
				// plan -> YES
				if (actionPlan.get(j).getMeasure().equals(actionPlanP.get(i).getMeasure())) {

					// check if the value is more than 0 -> YES
					if (Integer.valueOf(actionPlan.get(j).getPosition()) - (i + 1) > 0) {

						// add + sign to position
						position = "+" + (Integer.valueOf(actionPlan.get(j).getPosition()) - (i + 1));
					} else {

						// check if the value is more than 0 -> NO

						// check if the value is less than 0 -> YES
						if (Integer.valueOf(actionPlan.get(j).getPosition()) - (i + 1) < 0) {

							// set position
							position = String.valueOf(Integer.valueOf(actionPlan.get(j).getPosition()) - (i + 1));
						} else {

							// check if the value is less than 0 -> NO: make an
							// even sign
							position = "=";
						}
					}

					// add the position ot the action plan
					actionPlanP.get(i).setOrder(position);
				}
			}
		}

		// ****************************************************************
		// * APPO - Action Plan Phase Optimistic
		// ****************************************************************

		// parse all entries of the optimistic phase action plan
		for (int i = 0; i < phaseActionPlanO.size(); i++) {

			// parse all entries of the normal phase action plan
			for (int j = 0; j < phaseActionPlan.size(); j++) {

				// check if the entry matches the one from the normal action
				// plan -> YES
				if (phaseActionPlan.get(j).getMeasure().equals(phaseActionPlanO.get(i).getMeasure())) {

					// check if the value is more than 0 -> YES
					if (Integer.valueOf(phaseActionPlan.get(j).getPosition()) - (i + 1) > 0) {

						// add + sign to position
						position = "+"
								+ (Integer.valueOf(phaseActionPlan.get(j).getPosition()) - (i + 1));
					} else {

						// check if the value is more than 0 -> NO

						// check if the value is less than 0 -> YES
						if (Integer.valueOf(phaseActionPlan.get(j).getPosition()) - (i + 1) < 0) {

							// set position
							position = String.valueOf(Integer.valueOf(phaseActionPlan.get(j).getPosition()) - (i + 1));
						} else {

							// check if the value is less than 0 -> NO: make an
							// even sign
							position = "=";
						}
					}

					// add the position ot the action plan
					phaseActionPlanO.get(i).setOrder(position);
				}
			}
		}

		// ****************************************************************
		// * APPP - Action Plan Phase Pessimistic
		// ****************************************************************

		// parse all entries of the pessimistic phase action plan
		for (int i = 0; i < phaseActionPlanP.size(); i++) {

			// parse all entries of the normal phase action plan
			for (int j = 0; j < phaseActionPlan.size(); j++) {

				// check if the entry matches the one from the normal action
				// plan -> YES
				if (phaseActionPlan.get(j).getMeasure().equals(phaseActionPlanP.get(i).getMeasure())) {

					// check if the value is more than 0 -> YES
					if (Integer.valueOf(phaseActionPlan.get(j).getPosition()) - (i + 1) > 0) {

						// add + sign to position
						position = "+"
								+ (Integer.valueOf(phaseActionPlan.get(j).getPosition()) - (i + 1));
					} else {

						// check if the value is more than 0 -> NO

						// check if the value is less than 0 -> YES
						if (Integer.valueOf(phaseActionPlan.get(j).getPosition()) - (i + 1) < 0) {

							// set postion
							position = String.valueOf(Integer.valueOf(phaseActionPlan.get(j).getPosition()) - (i + 1));
						} else {

							// check if the value is less than 0 -> NO: make an
							// even sign
							position = "=";
						}
					}

					// add the position ot the action plan
					phaseActionPlanP.get(i).setOrder(position);
				}
			}
		}
	}

	/**
	 * computeActionPlan: <br>
	 * Generates a List of TMA (Threat Measure Asset) and Generates a Temporary
	 * Action Plan for each Measure used Inside TMA.
	 * 
	 * @param mode       The Mode to Compute the Action Plan : Normal, Optimistic or
	 *                   Pessimistic
	 * @param actionPlan The Action Plan where the Final Values are Stored
	 * @throws Exception
	 */
	private void computeActionPlan(ActionPlanMode mode) throws TrickException {

		// ****************************************************************
		// * variables initialisation
		// ****************************************************************
		ActionPlanEntry actionPlanEntry = null;
		MaturityMeasure maturityMeasure = null;
		AbstractNormalMeasure normalMeasure = null;
		List<Measure> usedMeasures = new ArrayList<>();
		List<ActionPlanEntry> actionPlan = this.analysis.getActionPlans();
		ActionPlanType actionPlanType = serviceActionPlanType.get(mode.getValue());

		int index = 1;

		// check if actionplantype exists, when not add to database
		if (actionPlanType == null) {
			actionPlanType = new ActionPlanType(mode);
			serviceActionPlanType.save(actionPlanType);
		}

		// ****************************************************************
		// * generate TMA list for normal computation
		// ****************************************************************
		List<TMA> tmas = generateTMAs(this.analysis, factory, usedMeasures, mode, 0, false, this.maturitycomputation,
				this.standards);

		// ****************************************************************
		// * parse all measures (to create complete action plan) until no more
		// measures are in the
		// list of measures
		// ****************************************************************
		while (!usedMeasures.isEmpty()) {

			// ****************************************************************
			// * calculate temporary Action Plan
			// ****************************************************************
			List<ActionPlanEntry> tmpActionPlan = generateTemporaryActionPlan(usedMeasures, actionPlanType, tmas);

			// ****************************************************************
			// * take biggest ROSI or ROSMI from temporary action plan and add
			// it to final action
			// plan and remove measure from usefulmeasures list
			// ****************************************************************

			actionPlanEntry = tmpActionPlan.parallelStream().max((e1, e2) -> Double.compare(e1.getROI(), e2.getROI()))
					.orElse(null);

			// check if first action plan entry is not null -> YES
			if (actionPlanEntry != null) {

				// ****************************************************************
				// * at this point actionPlanEntry is the object with the
				// biggest ROSI
				// ****************************************************************

				actionPlanEntry.setPosition(index++);

				// ****************************************************************
				// * update ALE values for next action plan run
				// ****************************************************************

				// initialise variables
				maturityMeasure = null;
				normalMeasure = null;

				// check if it is a maturity measure -> YES
				if (actionPlanEntry.getMeasure().getMeasureDescription().getStandard().is(Constant.STANDARD_MATURITY)) {

					// retrieve matrurity masure
					maturityMeasure = (MaturityMeasure) actionPlanEntry.getMeasure();

					// ****************************************************************
					// * update values for next run
					// ****************************************************************
					adaptValuesForMaturityMeasure(tmas, maturityMeasure);
				} else {

					// check if it is a maturity measure -> NO

					// retrieve measure
					normalMeasure = (AbstractNormalMeasure) actionPlanEntry.getMeasure();

					// ****************************************************************
					// * update values for next run
					// ****************************************************************
					adaptValuesForNormalMeasure(tmas, normalMeasure);

				}

				// ****************************************************************
				// * add measure to final action plan
				// ****************************************************************
				actionPlan.add(actionPlanEntry);

				// ****************************************************************
				// * remove measure from useful measures
				// ****************************************************************
				if (normalMeasure != null) {

					// remove standard measure
					usedMeasures.remove(normalMeasure);
				} else {
					if (maturityMeasure != null) {

						// remove maturity measure
						usedMeasures.remove(maturityMeasure);
					}
				}
			}
		}

		// clear TMAList after all action plan computation
		tmas.clear();
	}

	/**
	 * clone: <br>
	 * makes a clone of a given TMAList (in) and outputs a identic copy (out)
	 * 
	 * @param out
	 * @param in
	 * @throws CloneNotSupportedException
	 */
	public static void clone(List<TMA> out, List<TMA> in) {
		in.forEach(tma -> out.add(tma.clone()));
	}

	/**
	 * computePhaseActionPlan: <br>
	 * Computes the Action Plan Phase By Phase.
	 * 
	 * @param mode The Mode to Compute: Normal, Optimistic or Pessimistic
	 * @throws CloneNotSupportedException
	 * @throws TrickException
	 * @throws InvalidAttributesException
	 * @throws Exception
	 */
	private void computePhaseActionPlan(ActionPlanMode mode)
			throws TrickException {

		// ****************************************************************
		// * variables initialisation
		// ****************************************************************

		Measure measure = null;
		List<TMA> tmas = new ArrayList<>();
		List<Measure> usedMeasures = new ArrayList<>();

		int index = 1;

		// get actionplans of anaylsis
		List<ActionPlanEntry> phaseActionPlan = this.analysis.getActionPlans();

		// get actionplantype by given mode
		ActionPlanType actionPlanType = serviceActionPlanType.get(mode.getValue());

		// check if the actionplantype exists and add it to database if not
		if (actionPlanType == null)
			serviceActionPlanType.saveOrUpdate(actionPlanType = new ActionPlanType(mode));

		// ****************************************************************
		// * parse all phases where measures are in
		// ****************************************************************

		// parse all phases
		for (Phase phase : phases) {

			// ****************************************************************
			// * check if TMAList is empty -> YES: for the first time, the
			// TMAList is empty, so
			// do nothing
			// ****************************************************************
			if (tmas.isEmpty())
				tmas = generateTMAs(this.analysis, factory, usedMeasures, mode, phase.getNumber(), false,
						maturitycomputation, standards);
			else {
				// ****************************************************************
				// * TMAList was not empty, so take ALE values from current to
				// continue calculations
				// on previous values
				// ****************************************************************

				// ****************************************************************
				// * take a copy of the TMAList values
				// ****************************************************************

				// clone TMAList for ALE values

				@SuppressWarnings("unchecked")
				List<TMA> tmpTMAList = (List<TMA>) ((ArrayList<TMA>) tmas).clone();

				// ****************************************************************
				// * generate the TMAList
				// ****************************************************************
				tmas = generateTMAs(this.analysis, factory, usedMeasures, mode, phase.getNumber(), false,
						this.maturitycomputation, this.standards);

				// ****************************************************************
				// * update the created TMAList with previous values (ALE
				// values)
				// ****************************************************************

				if (tmas.isEmpty())
					tmas = tmpTMAList;

				// ****************************************************************
				// * for each TMAList entry, parse temporary TMAList to find
				// assessments that
				// are the same to change the ALE values
				// parse TMAList to edit the ALE values by assessment
				// if the assessment corresponds to the current TMAList
				// ****************************************************************
				tmas.parallelStream()
						.forEach(tma -> tmpTMAList.stream()
								.filter(tmpTMA -> tma.getAssessment().equals(tmpTMA.getAssessment())).parallel()
								.forEach(tmpTMA -> {
									// ****************************************************************
									// * edit the ALE value
									// ****************************************************************
									tma.setALE(tmpTMA.getALE());

									// ****************************************************************
									// * recalculate the delta ALE
									// ****************************************************************
									tma.calculateDeltaALE(factory);

									// ****************************************************************
									// * if 27002 standard, recalculate deltaALE
									// maturity if
									// maturity
									// computation -> YES
									// ****************************************************************
									if (tma.getStandard().is(Constant.STANDARD_27002) && maturitycomputation) {

										// ****************************************************************
										// * recalculate delta ALE Maturity
										// ****************************************************************
										tma.calculateDeltaALEMaturity(factory);
									}

								}));
			}

			// ****************************************************************
			// * generate action plan for this phase
			// ****************************************************************

			// parse all measures

			while (!usedMeasures.isEmpty()) {

				// ****************************************************************
				// * calculate temporary Action Plan
				// ****************************************************************
				List<ActionPlanEntry> tmpactionPlans = generateTemporaryActionPlan(usedMeasures, actionPlanType,
						tmas);

				// ****************************************************************
				// * determine biggest ROSI or ROSMI from temporary action plan
				// and add it to final
				// action plan remove measure from usefulmeasures list adapt
				// values for the next run
				// ****************************************************************

				// ****************************************************************
				// * start with the first element to check if it is the
				// biggest rosi
				// ****************************************************************
				ActionPlanEntry actionPlanEntry = tmpactionPlans.parallelStream()
						.max(selectActionPlanComparator()).orElse(null);

				// ****************************************************************
				// * parse the action plan to find the biggest ROSI
				// ****************************************************************

				if (actionPlanEntry != null) {
					// ****************************************************************
					// * at this time actionPlanEntry has the biggest ROSI
					// ****************************************************************

					setSOARisk(actionPlanEntry, tmas);

					// set index of entry

					actionPlanEntry.setPosition(index);
					index++;

					// ****************************************************************
					// * update TMAList ALE values for next run
					// ****************************************************************

					measure = actionPlanEntry.getMeasure();

					// check if the biggest rosi/rosmi entry is a maturity
					// measure -> YES

					if (measure instanceof MaturityMeasure) {

						// ****************************************************************
						// * change values for the next run
						// ****************************************************************
						adaptValuesForMaturityMeasure(tmas, (MaturityMeasure) measure);
					} else {

						// ****************************************************************
						// * change values for the next run
						// ****************************************************************
						adaptValuesForNormalMeasure(tmas, measure);

					}

					// ****************************************************************
					// * add measure to the final action plan
					// ****************************************************************
					phaseActionPlan.add(actionPlanEntry);

					// ****************************************************************
					// * remove measure from useful measures (either it is
					// maturity or standard)
					// ****************************************************************
					if (measure != null) {

						// remove standard measure
						usedMeasures.remove(measure);
					}
				}
			}
		}

		// ****************************************************************
		// * clear TMAList after all action plan computation
		// ****************************************************************
		tmas.clear();
	}

	private Comparator<? super ActionPlanEntry> selectActionPlanComparator() {
		return (e1, e2) -> {
			final int result = Integer.compare(Measure.statusLevel(e1.getMeasure().getStatus()),
					Measure.statusLevel(e2.getMeasure().getStatus()));
			return result == 0 ? Double.compare(e1.getROI(), e2.getROI()) : result;
		};
	}

	/**
	 * Sets the SOA (State of the Art) risk for the given ActionPlanEntry and list of TMAs (Threat Model Assessments).
	 * 
	 * @param entry The ActionPlanEntry for which to set the SOA risk.
	 * @param tmas The list of TMAs to search for the highest delta ALE (Annual Loss Expectancy).
	 * @throws TrickException If an error occurs during the computation.
	 */
	private void setSOARisk(ActionPlanEntry entry, List<TMA> tmas) throws TrickException {
		
		if (!analysis.getAnalysisStandards().get(entry.getMeasure().getMeasureDescription().getStandard().getName())
				.isSoaEnabled() || entry.getMeasure() instanceof MaturityMeasure)
			return;

		final TMA tma = tmas.parallelStream().filter(e -> e.getMeasure().equals(entry.getMeasure()))
				.max((e1, e2) -> Double.compare(e1.getDeltaALE(), e2.getDeltaALE()))
				.orElse(null);
		if (tma != null) {
			String soarisk = messageSource.getMessage("label.soa.asset", null, "Asset:", locale) + " "
					+ tma.getAssessment().getAsset().getName() + "\n"
					+ messageSource.getMessage("label.soa.scenario", null, "Scenario:", locale) + " "
					+ tma.getAssessment().getScenario().getName() + "\n"
					+ messageSource.getMessage("label.delta.ale", null, "Delta ALE:", locale) + " "
					+ numberFormat.format(tma.getDeltaALE() * .001) + " k€";

			final MeasureProperties measureProperties = entry.getMeasure() instanceof AbstractNormalMeasure
					? ((AbstractNormalMeasure) entry.getMeasure()).getMeasurePropertyList()
					: null;
			if (measureProperties != null) {
				measureProperties.setSoaRisk(soarisk);
				if (!StringUtils.hasText(measureProperties.getSoaComment()))
					measureProperties.setSoaComment(soarisk);
			}
		}

	}

	/***********************************************************************************************
	 * Temporary Action Plan - BEGIN
	 **********************************************************************************************/

	/**
	 * generateTemporaryActionPlan: <br>
	 * Generates the Temporary Action Plan based on the "TMAList" values and the
	 * usedMeasures List. Where usedMeasures is the List of Measures to add to the
	 * Action Plan.
	 * 
	 * @return The Temporary Action Plan Entries
	 * 
	 * @throws InvalidAttributesException
	 * @throws TrickException
	 * @throws CloneNotSupportedException
	 */
	private List<ActionPlanEntry> generateTemporaryActionPlan(List<Measure> usedMeasures, ActionPlanType actionPlanType,
			List<TMA> tmas)
			throws TrickException {

		// ****************************************************************
		// * variables initialisation
		// ****************************************************************
		List<ActionPlanEntry> tmpActionPlan = new ArrayList<>();

		// ****************************************************************
		// * generate normal action plan entries
		// ****************************************************************
		generateNormalActionPlanEntries(tmpActionPlan, actionPlanType, usedMeasures, tmas);

		// ****************************************************************
		// * generate maturtiy action plan entries
		// ****************************************************************
		generateMaturtiyChapterActionPlanEntries(tmpActionPlan, tmas);

		// ****************************************************************
		// * return the temporary action plan
		// ****************************************************************
		return tmpActionPlan;
	}

	/**
	 * generateNormalActionPlanEntries: <br>
	 * This method is used Inside "generateTemporaryActionPlan" to Calculate the
	 * Action Plan with Calculations only for the AnalysisStandard Measures,
	 * Maturity Measures are added but no Calculation is done for Maturity.
	 * Calculations for Maturity Entries are done in the Method
	 * "generateMaturtiyChapterActionPlanEntries".
	 * 
	 * @param tmpActionPlan The Temporary Action Plan with all usable Entries
	 * @throws InvalidAttributesException
	 * @throws TrickException
	 * @throws CloneNotSupportedException
	 */
	private void generateNormalActionPlanEntries(List<ActionPlanEntry> tmpActionPlan, ActionPlanType actionPlanType,
			List<Measure> usedMeasures, List<TMA> tmas)
			throws TrickException {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		double deltaALE = 0;
		double totalALE = 0;
		Measure measure = null;
		ActionPlanEntry actionPlanEntry = null;
		double ALE = 0;

		// ****************************************************************
		// * parse usedmeasures and generate action plan entries
		// ****************************************************************

		// parse measures
		for (int i = 0; i < usedMeasures.size(); i++) {

			// ****************************************************************
			// * create array of assets that are selected (for "per asset ALE")
			// ****************************************************************

			// ****************************************************************
			// * calculate values for action plan entry and add entry to
			// temporary action plan
			// ****************************************************************

			// ****************************************************************
			// * check if the measure is not a maturity measure -> NO
			// ****************************************************************
			if (!(usedMeasures.get(i) instanceof MaturityMeasure)) {

				List<ActionPlanAsset> tmpAssets = createSelectedAssetsList();
				Map<Integer, ActionPlanAsset> actionPlanAssetMapper = tmpAssets.parallelStream()
						.collect(Collectors.toMap(actionAsset -> actionAsset.getAsset().getId(), Function.identity()));

				// ****************************************************************
				// * calculate action plan entry ALE and delta ALE from TMAList
				// ****************************************************************

				// reinitialise variables
				deltaALE = 0;

				totalALE = 0;

				// temporary store the measure
				measure = usedMeasures.get(i);

				// parse TMAList
				for (int j = 0; j < tmas.size(); j++) {

					// ****************************************************************
					// * check if the measure is the current measure -> YES
					// ****************************************************************
					if (tmas.get(j).getMeasure().equals(measure)) {

						// ****************************************************************
						// * take ALE to calculate the sum of ALE (total ALE)
						// ****************************************************************
						totalALE += tmas.get(j).getALE();

						// ****************************************************************
						// * calculate ALE by asset for this action plan entry
						// ****************************************************************
						ActionPlanAsset actionPlanAsset = actionPlanAssetMapper
								.get(tmas.get(j).getAssessment().getAsset().getId());

						if (actionPlanAsset != null) {

							// ****************************************************************
							// * Calculate new ALE for this asset
							// ****************************************************************

							// store current value
							ALE = actionPlanAsset.getCurrentALE();

							// add this ALE
							ALE += tmas.get(j).getALE();

							// calculate minus deltaALE
							ALE -= tmas.get(j).getDeltaALE();

							// ****************************************************************
							// * update the object's ALE value
							// ****************************************************************
							actionPlanAsset.setCurrentALE(ALE);
						}
						// ****************************************************************
						// * take deltaALE to calculate the sum of deltaALE
						// ****************************************************************
						if (measure.getMeasureDescription().getStandard().isComputable())
							deltaALE += tmas.get(j).getDeltaALE();
					}
				}

				// ****************************************************************
				// * generate action plan entry object + calculate ROI + update
				// totalALE given with
				// delta ALE (for next calculation)
				// ****************************************************************
				actionPlanEntry = new ActionPlanEntry(measure, actionPlanType, tmpAssets, totalALE, deltaALE);

				// ****************************************************************
				// * add ActionPlanEntry to list of temporary action plan
				// ****************************************************************
				tmpActionPlan.add(actionPlanEntry);
			} else {

				// ****************************************************************
				// * check if the measure is not a maturity measure -> YES
				// ****************************************************************

				// store current measure as maturtiy measure
				measure = usedMeasures.get(i);

				// ****************************************************************
				// * generate object with delta ALE to 0
				// ****************************************************************
				actionPlanEntry = new ActionPlanEntry(measure, actionPlanType, 0);

				// ****************************************************************
				// * add object to temporary action plan
				// ****************************************************************
				tmpActionPlan.add(actionPlanEntry);
			}
		}
	}

	/**
	 * generateMaturtiyChapterActionPlanEntries: <br>
	 * Generate Action Plan Entries for the Maturity Chapters Inside an Action Plan.
	 * 
	 * @param tmpActionPlan The Action Plan to Add Maturity Chapters
	 * @param usedMeasures  Measures to use on the actionplan
	 * @param tmas          The list of TMA
	 * @throws TrickException
	 */
	private void generateMaturtiyChapterActionPlanEntries(List<ActionPlanEntry> tmpActionPlan, List<TMA> tmas)
			throws TrickException {

		// ****************************************************************
		// * inistialise variables
		// ****************************************************************
		double deltaALE = 0;
		ActionPlanEntry actionPlanEntry = null;
		MaturityMeasure maturityMeasure = null;
		String maturityChapter = "";
		int thisLevel = 0;
		double totalCost = 0;
		double totalChapter = 0;
		AbstractNormalMeasure tmpMeasure = null;
		double ale = 0;
		double deltaALEMat = 0;
		double numberMeasures = 0;

		// ****************************************************************
		// * check action plan on untreated maturtiy entries to calculate
		// ****************************************************************

		// parse temporary action plan
		for (int apmc = 0; apmc < tmpActionPlan.size(); apmc++) {

			// check it is a maturity measure -> YES
			if (tmpActionPlan.get(apmc).getMeasure().getMeasureDescription().getReference()
					.startsWith(Constant.MATURITY_REFERENCE)) {

				// ****************************************************************
				// * create array of assets that are selected (for
				// "per asset ALE")
				// ****************************************************************
				List<ActionPlanAsset> tmpAssets = createSelectedAssetsList();
				Map<Integer, ActionPlanAsset> actionPlanAssetMapper = new HashMap<>();
				Map<Integer, Double> tmpDeltaALEMat = new HashMap<>();

				// ****************************************************************
				// * create a vector for maturity deltaALE and initialise it
				// ****************************************************************

				// initialise deltaALEMat values to 0
				tmpAssets.parallelStream().forEach(tmpAsset -> {
					tmpDeltaALEMat.put(tmpAsset.getAsset().getId(), (double) 0);
					actionPlanAssetMapper.put(tmpAsset.getAsset().getId(), tmpAsset);
				});

				// ****************************************************************
				// * store action plan entry object
				// ****************************************************************
				actionPlanEntry = tmpActionPlan.get(apmc);

				// ****************************************************************
				// * determine cost of maturity chapter using the SML
				// ****************************************************************

				// ****************************************************************
				// * determine chapter
				// ****************************************************************
				maturityChapter = tmpActionPlan.get(apmc).getMeasure().getMeasureDescription().getReference();
				maturityChapter = maturityChapter.substring(2, maturityChapter.length());

				// retrieve maturity measure
				maturityMeasure = (MaturityMeasure) tmpActionPlan.get(apmc).getMeasure();

				// ****************************************************************
				// * determine SML
				// ****************************************************************
				thisLevel = maturityMeasure.getReachedLevel();

				// ****************************************************************
				// * determine cost
				// ****************************************************************

				// retrieve cost to get to the next SML (level numbers: 0-4)
				switch (thisLevel) {
					case 0:
						totalCost = maturityMeasure.getSML1Cost();
						break;
					case 1:
						totalCost = maturityMeasure.getSML2Cost();
						break;
					case 2:
						totalCost = maturityMeasure.getSML3Cost();
						break;
					case 3:
						totalCost = maturityMeasure.getSML4Cost();
						break;
					case 4:
						totalCost = maturityMeasure.getSML5Cost();
						break;
					default:
						totalCost = 0;
						break;
				}

				// initialise ALE for the chapter and the deltaALE
				deltaALE = 0;
				totalChapter = 0;

				Map<String, Boolean> measureCounter = new HashMap<>();

				// ****************************************************************
				// * parse TMAList to calculate totalALE and deltaALE and
				// deltaALE Maturity for the
				// action plan entry
				// ****************************************************************

				// parse TMAList entries
				for (int napmc = 0; napmc < tmas.size(); napmc++) {

					// temporary store measure
					tmpMeasure = (AbstractNormalMeasure) tmas.get(napmc).getMeasure();

					// ****************************************************************
					// * parse TMAList for AnalysisStandard 27002 measures and
					// inside this chapter
					// ****************************************************************
					if ((tmas.get(napmc).getStandard().is(Constant.STANDARD_27002))
							&& (tmpMeasure.getMeasureDescription().getReference().startsWith(maturityChapter))) {

						// ****************************************************************
						// * add measure to a list if it does not yet exist,
						// else do not add the
						// measure (measure can only be there once)
						// ****************************************************************

						if (!measureCounter.containsKey(tmpMeasure.getKey()))
							measureCounter.put(tmpMeasure.getKey(), true);

						// ****************************************************************
						// * calculate totalALE
						// ****************************************************************
						totalChapter = totalChapter + tmas.get(napmc).getALE();

						// ****************************************************************
						// * update asset ALE values and delta ALE maturity
						// values
						// ****************************************************************

						ActionPlanAsset actionPlanAsset = actionPlanAssetMapper
								.get(tmas.get(napmc).getAssessment().getAsset().getId());

						// ****************************************************************
						// * take previous value and add current ALE and
						// rewrite previous value
						// (of tmpAssets)
						// ****************************************************************
						if (actionPlanAsset != null) {

							// ****************************************************************
							// * update ALE of asset
							// ****************************************************************
							// store current value
							ale = actionPlanAsset.getCurrentALE();

							// add this ALE
							ale = ale + tmas.get(napmc).getALE();

							// update the object's ALE value
							actionPlanAsset.setCurrentALE(ale);

							// ****************************************************************
							// * update delta ALE Maturity
							// ****************************************************************

							// take deltaALE for this deltaALEMat
							deltaALEMat = tmpDeltaALEMat.get(actionPlanAsset.getAsset().getId());

							// calculate addition of deltaALEMat
							deltaALEMat = deltaALEMat + tmas.get(napmc).getDeltaALEMat();

							// rewrite current deltaALEMat with newest value
							tmpDeltaALEMat.put(actionPlanAsset.getAsset().getId(), deltaALEMat);
						}
						// ****************************************************************
						// * calculate deltaALE
						// ****************************************************************
						deltaALE = deltaALE + tmas.get(napmc).getDeltaALEMat();
					}
				}

				// ****************************************************************
				// * update current action plan entry values
				// ****************************************************************

				// store deltaALE in the ActionPlan entry for this maturity
				// measure
				actionPlanEntry.setDeltaALE(deltaALE);

				// take number of measures effected by this maturity chapter, to
				// divide with the ALE
				numberMeasures = measureCounter.size();

				// store totalALE in the ActionPlan entry for this maturity
				// measure divide to the
				// number of measures to have the correct value
				actionPlanEntry.setTotalALE(totalChapter / numberMeasures);

				// ****************************************************************
				// * parse assets to divide ALE with number of measures then
				// calculate minus
				// deltaALEMat
				// ****************************************************************
				for (int asc = 0; asc < tmpAssets.size(); asc++) {

					// take current ALE
					ale = tmpAssets.get(asc).getCurrentALE();

					// divide with number of measures
					ale = ale / numberMeasures;

					// calculate minus deltaALEMat
					ale = ale - tmpDeltaALEMat.get(asc);

					// rewrite this asset ALE value
					tmpAssets.get(asc).setCurrentALE(ale);
				}

				// add assets with current ALE to the entry
				actionPlanEntry.setActionPlanAssets(tmpAssets);

				// ****************************************************************
				// * calculate ROSMI with the given cost to reach the next SML
				// ****************************************************************
				actionPlanEntry.setCost(totalCost);

			}
		}

	}

	/**
	 * createSelectedAssetsList: <br>
	 * Create a fresh List of Assets which are only selected. This is used to set
	 * the current ALE by Asset to the Action Plan Assets.
	 * 
	 * @return The Copy of the List of Assets
	 * 
	 * @throws TrickException
	 */
	private List<ActionPlanAsset> createSelectedAssetsList()
			throws TrickException {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************

		List<ActionPlanAsset> tmpAssets = new ArrayList<>();

		// ****************************************************************
		// * take each asset and make a copy into another list
		// ****************************************************************

		// parse assets
		/*
		 * for (int asc = 0; asc < this.analysis.getAssets().size(); asc++) {
		 * 
		 * // selected asset -> YES if (this.analysis.getAnAsset(asc).isSelected() &&
		 * !tmpAssets.contains(this.analysis.getAnAsset(asc))) {
		 * 
		 * // **************************************************************** // *
		 * create new asset object //
		 * ****************************************************************
		 * 
		 * Asset tmpAsset = new Asset();
		 * tmpAsset.setComment(this.analysis.getAnAsset(asc).getComment());
		 * tmpAsset.setId(this.analysis.getAnAsset(asc).getId());
		 * tmpAsset.setName(this.analysis.getAnAsset(asc).getName());
		 * tmpAsset.setSelected(this.analysis.getAnAsset(asc).isSelected());
		 * tmpAsset.setAssetType(new
		 * AssetType(this.analysis.getAnAsset(asc).getAssetType().getType()));
		 * tmpAsset.setValue(this.analysis.getAnAsset(asc).getValue());
		 * 
		 * // **************************************************************** // * add
		 * asset to the list //
		 * ****************************************************************
		 * tmpAssets.add(new ActionPlanAsset(null, tmpAsset, 0)); } }
		 */

		// ****************************************************************
		// * add asset to the list
		// ****************************************************************
		for (Asset asset : this.analysis.getAssets()) {
			if (asset.isSelected())
				tmpAssets.add(new ActionPlanAsset(null, asset.clone(), 0));
		}

		// ****************************************************************
		// * return copy of assets
		// ****************************************************************
		return tmpAssets;
	}

	/***********************************************************************************************
	 * Temporary Action Plan - END
	 **********************************************************************************************/

	/***********************************************************************************************
	 * change values between 2 Action Plan Entries (ALE) - BEGIN
	 **********************************************************************************************/

	/**
	 * adaptValuesForNormalMeasure: <br>
	 * Adapt ALE for the Next Run of the Action Plan Calculation when a
	 * NormalMeasure was taken.
	 * 
	 * @param actionPlanEntry The Action Plan Entry(used to store ALE values of the
	 *                        Assets)
	 * @param normalMeasure   The taken AnalysisStandard Measure
	 * @throws TrickException
	 */
	private void adaptValuesForNormalMeasure(List<TMA> tmas, Measure measure)
			throws TrickException {

		// ****************************************************************
		// * variable initialisation
		// ****************************************************************
		double deltaALE = 0;
		TMA tmpTMA = null;

		if (!measure.getMeasureDescription().getStandard().isComputable())
			return;

		// ****************************************************************
		// * parse TMAList to update ALE values
		// ****************************************************************
		for (int i = 0; i < tmas.size(); i++) {

			// temporary store TMA entry
			tmpTMA = tmas.get(i);

			// check if the TMA entry has the given measure -> YES
			if (tmpTMA.getMeasure().equals(measure)) {

				// take the deltaALE for this measure
				deltaALE = tmpTMA.getDeltaALE();

				// ****************************************************************
				// * edit all ALE for the same assessment
				// ****************************************************************

				// reparse TMAList
				for (int j = 0; j < tmas.size(); j++) {

					// check if assessment is the same -> YES
					if ((tmas.get(j).getAssessment().equals(tmpTMA.getAssessment()))) {

						// ****************************************************************
						// * edit the ALE value of the TMAList element
						// ****************************************************************

						tmas.get(j).setALE(tmas.get(j).getALE() - deltaALE);

						// ****************************************************************
						// * recompute the DeltaALE
						// ****************************************************************
						tmas.get(j).calculateDeltaALE(factory);

						// if the measure is from 27002 -> YES
						if (tmas.get(j).getStandard().is(Constant.STANDARD_27002)) {

							// ****************************************************************
							// * calculate deltaALEMaturity
							// ****************************************************************
							tmas.get(j).calculateDeltaALEMaturity(factory);
						}
					}
				}
			}
		}
	}

	/**
	 * adaptValuesForMaturityMeasure: <br>
	 * Adapt ALE for the Next Run of the Action Plan Calculation when a
	 * MaturityMeasure was taken.
	 * 
	 * @param tmas
	 * @param actionPlanEntry
	 * @param maturityMeasure
	 * @throws TrickException
	 */
	private void adaptValuesForMaturityMeasure(List<TMA> tmas,
			MaturityMeasure maturityMeasure) throws TrickException {

		// ****************************************************************
		// * variable initialisation
		// ****************************************************************
		double deltaALE;
		TMA tmpTMA = null;
		String chapter = "";

		// ****************************************************************
		// * determine the maturity chapter
		// ****************************************************************
		chapter = maturityMeasure.getMeasureDescription().getReference().substring(2,
				maturityMeasure.getMeasureDescription().getReference().length());

		// ****************************************************************
		// * parse assessments of analysis to update ALE values
		// ****************************************************************

		// parse assessments
		for (Assessment assessment : this.analysis.getAssessments()) {

			// check if asset and scenario is selected for calculation and ALE >
			// 0 -> YES
			if (assessment.isSelected() && assessment.getALE() > 0) {

				// ****************************************************************
				// * calculate total deltaALE
				// ****************************************************************

				// initialise delta ALE
				deltaALE = 0;

				// parse all elements of the TMAList and sum deltaALE
				for (int i = 0; i < tmas.size(); i++) {

					// temporary store the TMA element
					tmpTMA = tmas.get(i);

					// parse each element where the measure is the one that was
					// taken, and when
					// assessment couple is the same
					if ((tmpTMA.getMeasure().getMeasureDescription().getReference().startsWith(chapter))
							&& (tmpTMA.getStandard().is(Constant.STANDARD_27002))
							&& (tmpTMA.getAssessment().getId() == assessment.getId())) {

						// ****************************************************************
						// * store the deltaALEMaturity
						// ****************************************************************
						deltaALE += tmpTMA.getDeltaALEMat();
					}
				}

				// ****************************************************************
				// * update ALE value for each assessment
				// ****************************************************************

				// parse TMAList elements
				for (int j = 0; j < tmas.size(); j++) {

					// find all assessments that are the same as for the current
					if ((tmas.get(j).getAssessment().getId() == assessment.getId())) {

						// ****************************************************************
						// * edit the ALE value
						// ****************************************************************
						tmas.get(j).setALE(tmas.get(j).getALE() - deltaALE);

						// ****************************************************************
						// * recompute the deltaALE
						// ****************************************************************
						tmas.get(j).calculateDeltaALE(factory);

						// if it is a 27002 standard ->YES
						if (tmas.get(j).getStandard().is(Constant.STANDARD_27002) && maturitycomputation) {

							// ****************************************************************
							// * calculate the deltaALEMaturity
							// ****************************************************************
							tmas.get(j).calculateDeltaALEMaturity(factory);
						}
					}
				}
			}
		}
	}

	/***********************************************************************************************
	 * change values between 2 Action Plan Entries (ALE) - END
	 **********************************************************************************************/

	/***********************************************************************************************
	 * TMAList - BEGIN
	 **********************************************************************************************/

	/**
	 * generateTMAList: <br>
	 * Generates a list of Measure-Assessment-Threat and Calculates for this Triple
	 * the deltaALE and if it is a Measure of the AnalysisStandard 27002 the
	 * deltaALE Maturity. <br>
	 * The SimpleParameter usedMeasures will have a list of Measures that are to be
	 * used for the Action Plan Calculation. The Method returns the List of TMA
	 * Entries and inside the parameter usedMeasures the Measures.
	 * 
	 * @param factory
	 * 
	 * @param usedMeasures        List to store the Measures used for Action Plan
	 *                            Calculation (will be filled inside)
	 * @param mode                Defines if the Mode is Normal, Optimistic or
	 *                            Pessimistic
	 * @param phase               Defines if the Phase Calculation is Enabled and
	 *                            what Phase to take into account
	 * @param isCssf              Flag determinating if TMAList is for CSSF
	 *                            computation or not
	 * @param maturitycomputation flag determinating if maturity is computed or not
	 * @param standards           List of AnalysisStandards to be used in the
	 *                            actionplan (to generate only TMA entries for the
	 *                            given standards)
	 * @throws TrickException
	 */
	public static List<TMA> generateTMAs(Analysis analysis, ValueFactory factory, List<Measure> usedMeasures,
			ActionPlanMode mode, int phase, boolean isCssf,
			boolean maturitycomputation, List<AnalysisStandard> standards) throws TrickException {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<TMA> tmas = new ArrayList<>();

		// ****************************************************************
		// * clear List
		// ****************************************************************
		if (usedMeasures != null)
			usedMeasures.clear();

		// ****************************************************************
		// * generate TMAListEntries
		// ****************************************************************

		// ****************************************************************
		// * parse all MeasureStandard measures to generate TMA entries
		// ****************************************************************

		// parse all standards
		for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards().values()) {

			if (!standards.contains(analysisStandard))
				continue;

			// ****************************************************************
			// * check if not Maturity standard -> NO
			// ****************************************************************
			if (analysisStandard instanceof NormalStandard) {

				// store standard as it's real type
				NormalStandard normalStandard = (NormalStandard) analysisStandard;

				// ****************************************************************
				// * parse all measures of the current standard
				// ****************************************************************
				for (AbstractNormalMeasure normalMeasure : normalStandard.getExendedMeasures()) {

					// ****************************************************************
					// * check conditions to add TMAListEntries to TMAList
					// ****************************************************************

					// ****************************************************************
					// * check if measure is applicable, mandatory and
					// implementation rate is not
					// 100% -> YES
					// ****************************************************************
					if (!(normalMeasure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
							|| normalMeasure.getStatus().equals(Constant.MEASURE_STATUS_EXCLUDE))
							&& normalMeasure
									.getImplementationRateValue(factory) < Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE
							&& normalMeasure.getMeasureDescription().isComputable()
							&& normalMeasure.getCost() >= 0) {

						// ****************************************************************
						// * when phase computation, phase is bigger than 0,
						// take these values that
						// equals the phase number -> YES
						// ****************************************************************
						if (((phase > 0) && (normalMeasure.getPhase().getNumber() == phase)) || (phase == 0)) {

							// ****************************************************************
							// * generate TMA entry -> useful measure
							// ****************************************************************
							generateTMAEntry(analysis, factory, tmas, usedMeasures, mode,
									normalStandard.getStandard(), normalMeasure, true, maturitycomputation, standards);
						} else {

							// ****************************************************************
							// * when phase computation, phase is bigger than 0,
							// take these values
							// that equals the phase number -> NO
							// ****************************************************************

							// ****************************************************************
							// * check if standard 27002 measure and if maturity
							// computation for
							// Maturity calculation
							// ****************************************************************

							if (!isCssf && normalStandard.getStandard().is(Constant.STANDARD_27002)
									&& maturitycomputation) {

								// ****************************************************************
								// * generate TMA entry -> not a useful measure
								// ****************************************************************
								generateTMAEntry(analysis, factory, tmas, usedMeasures, mode,
										normalStandard.getStandard(), normalMeasure, false, maturitycomputation,
										standards);
							}
						}
					} else {

						// ****************************************************************
						// * check if measure is applicable, mandatory and
						// implementation rate is
						// not 100% -> NO
						// ****************************************************************

						// ****************************************************************
						// * check the same except take measures where
						// implementation rate is not
						// relevant AND check if standard 27002 measure for
						// Maturity
						// calculation
						// ****************************************************************
						if (!isCssf && maturitycomputation
								&& !(normalMeasure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
										|| normalMeasure.getStatus().equals(Constant.MEASURE_STATUS_EXCLUDE))
								&& normalMeasure.getMeasureDescription().isComputable()
								&& normalMeasure.getCost() >= 0
								&& normalStandard.getStandard().is(Constant.STANDARD_27002)) {
							// ****************************************************************
							// * generate TMA entry -> not a useful measure
							// ****************************************************************
							generateTMAEntry(analysis, factory, tmas, usedMeasures, mode,
									normalStandard.getStandard(), normalMeasure, false, maturitycomputation, standards);
						}
					}
				}
			} else if (analysisStandard instanceof AssetStandard) {

				AssetStandard assetStandard = null;

				// store standard as it's real type
				assetStandard = (AssetStandard) analysisStandard;

				// ****************************************************************
				// * parse all measures of the current standard
				// ****************************************************************
				for (AssetMeasure assetMeasure : assetStandard.getExendedMeasures()) {

					// ****************************************************************
					// * check conditions to add TMAListEntries to TMAList
					// ****************************************************************

					// ****************************************************************
					// * check if measure is applicable, mandatory and
					// implementation rate is not
					// 100% -> YES
					// ****************************************************************
					if (!(assetMeasure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
							|| assetMeasure.getStatus().equals(Constant.MEASURE_STATUS_EXCLUDE))
							&& assetMeasure.getCost() >= 0 && assetMeasure
									.getImplementationRateValue(factory) < Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE
							&& assetMeasure.getMeasureDescription().isComputable()
							&& (phase > 0 && assetMeasure.getPhase().getNumber() == phase || phase == 0)) {

						// ****************************************************************
						// * when phase computation, phase is bigger than 0,
						// take these values that
						// equals the phase number -> YES
						// ****************************************************************

						// ****************************************************************
						// * generate TMA entry -> useful measure
						// ****************************************************************
						generateTMAEntry(analysis, factory, tmas, usedMeasures, mode,
								assetStandard.getStandard(), assetMeasure, true, maturitycomputation, standards);

					}
				}
			}
		}

		// ****************************************************************
		// * add maturity chapters to list of useful measures
		// ****************************************************************

		if (!isCssf && usedMeasures != null && maturitycomputation)
			addMaturityChaptersToUsedMeasures(analysis, factory, usedMeasures, phase, standards);
		// return TMAList
		return tmas;

	}

	/**
	 * generateTMAEntry: <br>
	 * This method generates for a given Measure TMA (Threat Measure Assessment)
	 * entries in the List "TMAList". This method adds this measure to the list of
	 * usedMEasures given as parameter.
	 * 
	 * @param factory
	 * 
	 * @param tmas           The List to insert the current TMA Entry
	 * @param usedMeasures   The List of Measures to add the current Measure (from
	 *                       TMA Entry) to be used
	 * @param mode           Defines which Type of Action Plan is Calculated (to
	 *                       take the correct ALE value)
	 * @param normalStandard The AnalysisStandard of the Measure (only
	 *                       NormalStandard)
	 * @param normalMeasure  The Measure of the AnalysisStandard (NormalMeasure)
	 * @param usefulMeasure  Flag to determine is this measure needs to be added to
	 *                       the usedMeasures (a valid Measure)
	 * @throws TrickException
	 */
	private static void generateTMAEntry(Analysis analysis, ValueFactory factory, List<TMA> tmas,
			List<Measure> usedMeasures, ActionPlanMode mode, Standard standard,
			Measure measure, boolean usefulMeasure, boolean maturitycomputation, List<AnalysisStandard> standards)
			throws TrickException {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		TMA tmpTMA = null;
		MaturityStandard maturityStandard = null;
		String tmpReference = "";
		int matLevel = 0;
		double rrf = 0;
		double cMaxEff = -1;
		double nMaxEff = -1;
		boolean insertMeasure = usefulMeasure && !(usedMeasures == null || standards == null);
		IParameter parameterMaxRRF = analysis.getSimpleParameters().stream()
				.filter(parameter -> parameter.isMatch(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME,
						Constant.PARAMETER_MAX_RRF))
				.findAny().orElse(null);

		Map<String, Boolean> measureMapper = insertMeasure
				? usedMeasures.parallelStream().collect(Collectors.toMap(Measure::getKey, m -> true))
				: Collections.emptyMap();

		// ****************************************************************
		// * parse assesments to generate TMA entries
		// ****************************************************************

		if (usefulMeasure || maturitycomputation) {

			// parse each assessment
			for (Assessment tmpAssessment : analysis.getAssessments()) {

				// check if threat (scenario) and asset are selected for the
				// computation AND ALE > 0
				// -> YES
				if (tmpAssessment.isUsable()) {

					// ****************************************************************
					// * calculate RRF
					// ****************************************************************
					rrf = RRF.calculateRRF(tmpAssessment, parameterMaxRRF, measure);

					// ****************************************************************
					// * create TMA object and initialise with assessment and
					// measure and RRF
					// ****************************************************************
					tmpTMA = new TMA(mode, tmpAssessment, measure, rrf);

					// ****************************************************************
					// * calculate deltaALE for this TMA
					// ****************************************************************

					if (measure instanceof AssetMeasure) {
						if (((AssetMeasure) measure).getMeasureAssetValueByAsset(tmpAssessment.getAsset()) != null)
							tmpTMA.calculateDeltaALE(factory);
						else
							tmpTMA.setDeltaALE(0);
					} else
						tmpTMA.calculateDeltaALE(factory);

					// ****************************************************************
					// * check if measure needs to taken into account for action
					// plan calculation.
					// TMA entries need to be generated for 27002 because of
					// maturity. Special case
					// ****************************************************************

					// measure needs to be taken into account? -> YES
					if (insertMeasure && !measureMapper.containsKey(measure.getKey()))
						measureMapper.put(measure.getKey(), usedMeasures.add(measure));

					// ****************************************************************
					// * check if measure is from 27002 standard (for maturity)
					// ****************************************************************
					if (standard.is(Constant.STANDARD_27002)) {

						// ****************************************************************
						// * retrieve reached SML
						// ****************************************************************

						// ****************************************************************
						// * extract useful reference data from reference (the
						// chapter part)
						// ****************************************************************

						// store reference
						tmpReference = measure.getMeasureDescription().getReference();
						int index = tmpReference.indexOf(".");
						if (index != -1) {
							// create chapter reference to check on maturity
							tmpReference = tmpReference.substring(0, index);
						}

						// ****************************************************************
						// * Parse standards to find maturity standard to
						// retrieve SML
						// from this chapter
						// (which is inside tmpReference)
						// ****************************************************************

						// parse all standards
						for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards().values()) {

							// check if standard is maturity -> YES
							if (analysisStandard instanceof MaturityStandard) {

								// store maturity standard object
								maturityStandard = (MaturityStandard) analysisStandard;

								// ****************************************************************
								// * parse measures of maturity to find the
								// correct chapter (level
								// 1) with the reference extracted from 27002
								// standard above
								// ****************************************************************

								// parse measures of maturity standard
								for (int tmc = 0; tmc < maturityStandard.getMeasures().size(); tmc++) {

									// check if the measure reference matches
									// the extracted
									// reference -> YES
									if (maturityStandard.getMeasure(tmc).getMeasureDescription().getReference()
											.equals(Constant.MATURITY_REFERENCE + tmpReference)) {

										// *************************************************************
										// * store maturity level (SML) of this
										// chapter
										// *************************************************************
										matLevel = maturityStandard.getMeasure(tmc).getReachedLevel();

										// leave the loop, only this case is
										// needed
										break;
									}
								}

								// leave the loop, only the Maturity standard is
								// needed
								break;
							}
						}

						// ****************************************************************
						// * check if SML < 5 to be used to - retrieve
						// "current max effency" and
						// "next max effency" parameter - calculate delta ALE
						// for maturity
						// ****************************************************************

						// check if maturitylevel is less than 5 -> YES
						if (matLevel < 5) {

							// ****************************************************************
							// * retrieve "current" and "next max effency" from
							// parameter list
							// ****************************************************************

							// parse params
							for (MaturityParameter parameter : analysis.getMaturityParameters()) {

								// check if it is current maxeffency -> YES
								if (parameter.getDescription().equals("SML" + matLevel)) {

									// ****************************************************************
									// * store current max effency value
									// ****************************************************************
									cMaxEff = parameter.getValue();

									// check if both parameters were found ->
									// YES
									if ((cMaxEff > -1) && (nMaxEff > -1)) {

										// leave loop
										break;
									}
								} else {

									// check if it is current maxeffency -> NO

									// check if it is next maxeffency -> YES
									if (parameter.getDescription().equals("SML" + (matLevel + 1))) {

										// *************************************************************
										// * store next max effency value
										// *************************************************************
										nMaxEff = parameter.getValue().doubleValue();

										// check if both parameters were found
										// -> YES
										if ((cMaxEff > -1) && (nMaxEff > -1)) {

											// leave loop
											break;
										}
									}
								}
							}

							// ****************************************************************
							// * store current and next max effency values in
							// TMA entry
							// ****************************************************************
							tmpTMA.setcMaxEff(cMaxEff);
							tmpTMA.setnMaxEff(nMaxEff);

							// ****************************************************************
							// * calculate delta ALE for the Maturity
							// ****************************************************************
							tmpTMA.calculateDeltaALEMaturity(factory);
						}
					}

					// ****************************************************************
					// * add TMA object in the list of TMA's to calculate the
					// Action Plan
					// ****************************************************************
					tmas.add(tmpTMA);
				}
			}
		}
	}

	/**
	 * addMaturityChaptersToUsedMeasures: <br>
	 * Parse Maturity Measure and Add only Chapters of Maturity to "usedmeasures"
	 * parameter. This is used to identify the Maturity Measures to Add to the
	 * Action Plan. If SimpleParameter "phase" is not 0 then add Maturity Chapters
	 * for the given Phase.
	 * 
	 * @param analysis     analysis object
	 * @param factory
	 * @param usedMeasures list of measures to use on the action plan
	 * @param phase        current phase number
	 * @param standards    list of standards to implement on the actionplan
	 */
	private static void addMaturityChaptersToUsedMeasures(Analysis analysis, ValueFactory factory,
			List<Measure> usedMeasures, int phase, List<AnalysisStandard> standards) {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		MaturityStandard maturityStandard = null;

		// ****************************************************************
		// * parse all chapters of maturity (4-15)
		// ****************************************************************

		// ****************************************************************
		// * parse standards to find maturity
		// ****************************************************************

		// parse standards
		for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards().values()) {

			if (!standards.contains(analysisStandard))
				continue;

			// check if standard is maturity standard -> YES
			if (analysisStandard instanceof MaturityStandard) {

				// temporary store maturity standard
				maturityStandard = (MaturityStandard) analysisStandard;

				// leave loop
				break;
			}
		}

		if (maturityStandard != null) {

			// ****************************************************************
			// * parse all measures of maturity standard
			// ****************************************************************
			for (MaturityMeasure measure : maturityStandard.getExendedMeasures()) {
				// check reference if level 1 chapter that is currently parsed
				// and if reached SML <
				// 5
				if (!measure.getMeasureDescription().isComputable() && measure.getReachedLevel() < 5
						&& ((phase > 0 && measure.getPhase().getNumber() == phase) || (phase == 0))) {
					// add Maturity Chapter as nessesary
					addAMaturtiyChapterToUsedMeasures(analysis, factory, usedMeasures, maturityStandard, measure);
				}
			}
		}
	}

	/**
	 * addAMaturtiyChapterToUsedMeasures: <br>
	 * Checks if a Maturity Chapter has a total cost > 0 and if for this chapter,
	 * there is at least 1 measure of 27002 applicable for this chapter. When both
	 * costrains are met, the measure will be added to the list "usedMeasures" given
	 * as parameter.
	 * 
	 * @param factory
	 * 
	 * @param usedMeasures     The List of Measure to add the valid Maturity Chapter
	 *                         to
	 * @param maturityStandard The Maturity AnalysisStandard Object
	 * @param measure          The Measure which represents the Maturity Chapter
	 */
	private static void addAMaturtiyChapterToUsedMeasures(Analysis analysis, ValueFactory factory,
			List<Measure> usedMeasures, MaturityStandard maturityStandard,
			MaturityMeasure measure) {

		// extract chapter number from level 1 measure
		String chapterValue = measure.getMeasureDescription().getReference().substring(2,
				measure.getMeasureDescription().getReference().length());

		// check if measure has to be added -> YES
		if ((isMaturityChapterTotalCostBiggerThanZero(maturityStandard, measure, factory))
				&& (hasUsable27002MeasuresInMaturityChapter(analysis, chapterValue))) {

			// add measure to list of used measures
			usedMeasures.add(measure);
		}
	}

	/**
	 * isMaturityChapterTotalCostBiggerThanZero: <br>
	 * Checks if the Total Cost of a Maturity Chapter is bigger than 0 euros.
	 * 
	 * @param maturityStandard The Maturity AnalysisStandard Object
	 * @param chapter          The Maturity Chapter Measure Object (Level 1 Measure)
	 * @return True if the Cost is > 0; False if Cost is 0
	 */
	private static final boolean isMaturityChapterTotalCostBiggerThanZero(MaturityStandard maturityStandard,
			MaturityMeasure chapter, ValueFactory factory) {

		// initialise measure cost
		double totalCost = 0;

		// extract chapter number from level 1 measure
		String chapterValue = chapter.getMeasureDescription().getReference().substring(2,
				chapter.getMeasureDescription().getReference().length());

		// parse measure of maturity standard
		for (Measure measure : maturityStandard.getMeasures()) {

			// *********************************************************
			// * perform checks to take only cost of usable measures
			// *********************************************************

			// check if reference starts with
			// "M.<currentChapter>.<currentSML+1>." and if applicable
			// and implementation rate is less than 100%
			if ((!measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
					|| measure.getStatus().equals(Constant.MEASURE_STATUS_EXCLUDE))
					&& measure.getMeasureDescription().getReference()
							.startsWith(Constant.MATURITY_REFERENCE + chapterValue + "."
									+ (chapter.getReachedLevel() + 1) + ".")
					&& measure
							.getImplementationRateValue(factory) < Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE) {

				// *****************************************************
				// * useful measure was found: add the cost to the total cost of
				// measure
				// *****************************************************
				totalCost += measure.getCost();
			}
		}

		// check if cost is larger than 0 euros -> YES
		return totalCost > 0;
	}

	/**
	 * hasUsable27002MeasuresInMaturityChapter: <br>
	 * Checks if a given Maturity Chapter has usable Measures in the appropriate
	 * chapter in the 27002 AnalysisStandard.
	 * 
	 * @param chapter The Maturity Chapter to check
	 * @return True if there is at least 1 Measure inside the AnalysisStandard 27002
	 *         Chapter that is applicable;False if there are no Measures in the
	 *         27002 AnalysisStandard
	 */
	private static boolean hasUsable27002MeasuresInMaturityChapter(Analysis analysis, String chapter) {
		// initialise variables
		final NormalStandard normalStandard = (NormalStandard) analysis.getAnalysisStandards().values().stream()
				.filter(analysisStandard -> (analysisStandard instanceof NormalStandard)
						&& analysisStandard.getStandard().is(Constant.STANDARD_27002))
				.findAny().orElse(null);
		return normalStandard != null && normalStandard.getMeasures().stream()
				.anyMatch(measure -> measure.getMeasureDescription().getReference().startsWith(chapter + ".")
						&& !(measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
								|| measure.getStatus().equals(Constant.MEASURE_STATUS_EXCLUDE))
						&& measure.getMeasureDescription().isComputable());
	}

	/**
	 * extractMainChapter: <br>
	 * extract the main chapter
	 * 
	 * @param chapter
	 * @return
	 */
	public static String extractMainChapter(String chapter) {
		if ((chapter.toUpperCase().startsWith("A.")) || (chapter.toUpperCase().startsWith("M."))) {
			String[] chapters = chapter.split("[.]", 3);
			return chapters[0] + "." + chapters[1];
		}
		return chapter.split(Constant.REGEX_SPLIT_REFERENCE, 2)[0];
	}

	/**
	 * getAnalysis: <br>
	 * Description
	 * 
	 * @return
	 */
	public Analysis getAnalysis() {
		return analysis;
	}

	/**
	 * getIdTask: <br>
	 * Description
	 * 
	 * @return
	 */
	public String getIdTask() {
		return idTask;
	}

	/**
	 * setIdTask: <br>
	 * Description
	 * 
	 * @param idTask
	 */
	public void setIdTask(String idTask) {
		this.idTask = idTask;
	}

	/**
	 * setServiceTaskFeedback: <br>
	 * Description
	 * 
	 * @param serviceTaskFeedback2
	 */
	public void setServiceTaskFeedback(ServiceTaskFeedback serviceTaskFeedback2) {
		this.serviceTaskFeedback = serviceTaskFeedback2;

	}

	/**
	 * getStandards: <br>
	 * Returns the standards field value.
	 * 
	 * @return The value of the standards field
	 */
	public List<AnalysisStandard> getStandards() {
		return standards;
	}

	/**
	 * setStandards: <br>
	 * Sets the Field "standards" with a value.
	 * 
	 * @param standards The Value to set the standards field
	 */
	public void setStandards(List<AnalysisStandard> standards) {
		this.standards = standards;
	}

	/**
	 * isUncertainty: <br>
	 * Returns the uncertainty field value.
	 * 
	 * @return The value of the uncertainty field
	 */
	public boolean isUncertainty() {
		return uncertainty;
	}

	/**
	 * setUncertainty: <br>
	 * Sets the Field "uncertainty" with a value.
	 * 
	 * @param uncertainty The Value to set the uncertainty field
	 */
	public void setUncertainty(boolean uncertainty) {
		this.uncertainty = uncertainty;
	}

	/**
	 * @return the phases
	 */
	public List<Phase> getPhases() {
		return phases;
	}

	/**
	 * @param phases the phases to set
	 */
	public void setPhases(List<Phase> phases) {
		this.phases = phases;
	}

	/***********************************************************************************************
	 * Action Plan Summary - END
	 **********************************************************************************************/
}
