package lu.itrust.business.TS.data.actionplan.helper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.naming.directory.InvalidAttributesException;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.data.actionplan.ActionPlanAsset;
import lu.itrust.business.TS.data.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.data.actionplan.ActionPlanMode;
import lu.itrust.business.TS.data.actionplan.ActionPlanType;
import lu.itrust.business.TS.data.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.data.actionplan.summary.helper.Maintenance;
import lu.itrust.business.TS.data.actionplan.summary.helper.SummaryStandardHelper;
import lu.itrust.business.TS.data.actionplan.summary.helper.SummaryValues;
import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.assessment.Assessment;
import lu.itrust.business.TS.data.assessment.helper.AssessmentManager;
import lu.itrust.business.TS.data.asset.Asset;
import lu.itrust.business.TS.data.asset.AssetType;
import lu.itrust.business.TS.data.general.Phase;
import lu.itrust.business.TS.data.parameter.Parameter;
import lu.itrust.business.TS.data.rrf.RRF;
import lu.itrust.business.TS.data.standard.AnalysisStandard;
import lu.itrust.business.TS.data.standard.AssetStandard;
import lu.itrust.business.TS.data.standard.MaturityStandard;
import lu.itrust.business.TS.data.standard.NormalStandard;
import lu.itrust.business.TS.data.standard.Standard;
import lu.itrust.business.TS.data.standard.measure.AssetMeasure;
import lu.itrust.business.TS.data.standard.measure.MaturityMeasure;
import lu.itrust.business.TS.data.standard.measure.Measure;
import lu.itrust.business.TS.data.standard.measure.NormalMeasure;
import lu.itrust.business.TS.database.dao.DAOActionPlanType;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.hbm.DAOHibernate;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * ActionPlanComputation: <br>
 * This class is used to calculate the action plan for an Analysis. This class
 * is also used to generate the TMAList (Threat - Measure - Asset Triples). This
 * class will initialize the Lists of ActionPlan Entries inside the Analysis
 * class (The final Action Plans) as well as the Summary for each Action Plans.
 * After the Action Plans are calculated, this class will save the results to
 * the MySQL Database.
 * 
 * @author itrust consulting s.à.rl. : SME
 * @version 0.1
 * @since 9 janv. 2013
 */
public class ActionPlanComputation {

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	private DAOActionPlanType serviceActionPlanType;

	private DAOAnalysis sericeAnalysis;

	private ServiceTaskFeedback serviceTaskFeedback;

	/** task id */
	private String idTask;

	/** Analysis Object */
	private Analysis analysis = null;

	/** List of standards to compute */
	private List<AnalysisStandard> standards = null;

	/** uncertainty computation flag */
	private boolean uncertainty = false;

	/** maturity computation computation flag */
	private boolean maturitycomputation = false;

	private boolean normalcomputation = false;

	@Autowired
	private MessageSource messageSource;

	/***********************************************************************************************
	 * Constructor
	 **********************************************************************************************/

	/**
	 * Constructor: This creates an object and takes as parameter an loaded
	 * Analysis.
	 * 
	 * @param analysis
	 *            The Analysis Object
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
	 * ActionPlanComputation: constructor that takes the service actionplantype
	 * and serviceAnalysis ( to get all nessesary data for computation) and the
	 * anaylsis object itself as parameters.
	 * 
	 * @param serviceActionPlanType
	 * @param sericeAnalysis
	 * @param analysis
	 */
	public ActionPlanComputation(DAOActionPlanType serviceActionPlanType, DAOAnalysis sericeAnalysis, Analysis analysis) {
		this.serviceActionPlanType = serviceActionPlanType;
		this.sericeAnalysis = sericeAnalysis;
		this.analysis = analysis;
	}

	/**
	 * ActionPlanComputation: constructor that takes the service actionplantype
	 * and serviceAnalysis ( to get all nessesary data for computation) ,
	 * standards to compute, uncertainty flag, the analysis and the task
	 * parameters for asynchronous actionplan computation.
	 * 
	 * Inside this constructor, the standards will be determined to compute as
	 * well as the uncertainty and maturity computation flag.
	 * 
	 * @param serviceActionPlanType
	 * @param sericeAnalysis
	 * @param serviceTaskFeedback
	 * @param idTask
	 * @param analysis
	 * @param standards
	 * @param uncertainty
	 */
	public ActionPlanComputation(DAOActionPlanType serviceActionPlanType, DAOAnalysis sericeAnalysis, ServiceTaskFeedback serviceTaskFeedback, String idTask, Analysis analysis,
			List<AnalysisStandard> standards, boolean uncertainty, MessageSource messageSource) {

		// initialise variables
		this.serviceActionPlanType = serviceActionPlanType;
		this.sericeAnalysis = sericeAnalysis;
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.idTask = idTask;
		this.analysis = analysis;
		this.messageSource = messageSource;
		AnalysisStandard tmp27002standard = null;

		// determine standard 27002
		for (AnalysisStandard analysisStandard : this.analysis.getAnalysisStandards()) {

			if (analysisStandard.getStandard().getLabel().equals(Constant.STANDARD_27002) && analysisStandard.getStandard().isComputable()) {
				tmp27002standard = analysisStandard;
				break;
			}
		}

		// check if standards to compute is empty -> YES: take all standards;
		// NO: use
		// only the standards given
		if (standards == null || standards.isEmpty()) {

			List<AnalysisStandard> tmpStandards = new ArrayList<AnalysisStandard>();

			for (AnalysisStandard analysisStandard : this.analysis.getAnalysisStandards()) {
				if (analysisStandard.getStandard().isComputable())
					tmpStandards.add(analysisStandard);
			}

			this.standards = tmpStandards;
		} else {
			this.standards = standards;
		}

		// check if maturity standard is to compute -> check if 27002 is
		// selected,
		// if no: select 27002
		for (AnalysisStandard analysisStandard : this.standards) {

			if (analysisStandard.getStandard().getLabel().equals(Constant.STANDARD_MATURITY) && analysisStandard.getStandard().isComputable()) {

				this.maturitycomputation = true;

				boolean found = false;

				for (AnalysisStandard checkStandard : this.standards) {
					if (checkStandard.getStandard().getLabel().equals(Constant.STANDARD_27002)) {
						found = true;
						break;
					}
				}

				if (!found) {
					this.standards.add(tmp27002standard);
				}
				break;
			}

		}
		this.uncertainty = uncertainty;
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

		// initialise task feedback progress in percentage to return to the user
		int progress = 10;

		String language = this.analysis.getLanguage().getAlpha2();

		// check if uncertainty to adopt the progress factor
		if (!uncertainty)
			progress = 20;

		// send feedback
		serviceTaskFeedback.send(idTask, new MessageHandler("info.action_plan.computing", "Computing Action Plans", language, progress));

		System.out.println("Computing Action Plans...");

		try {

			AssessmentManager asm = new AssessmentManager();

			// update ALE of asset objects
			asm.UpdateAssessment(this.analysis);

			// ***************************************************************
			// * compute Action Plan - normal mode - Phase //
			// ***************************************************************
			System.out.println("compute Action Plan - normal mode - Phase");

			// check if uncertainty to adopt the progress factor
			if (uncertainty)
				progress = 20;
			else
				progress = 40;

			// send feedback
			serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.phase.normal_mode", "Compute Action Plan - normal mode - Phase", language, progress));

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
				serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.phase.optimistic_mode", "Compute Action Plan - optimistic mode - Phase", language,
						progress));

				// compute
				computePhaseActionPlan(ActionPlanMode.APPO);

				// ****************************************************************
				// * compute Action Plan - pessimistic mode - Phase
				// ****************************************************************

				// update progress
				progress += 10;

				System.out.println("compute Action Plan - pessimistic mode - Phase");

				// send feedback
				serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.phase.pessimistic_mode", "Compute Action Plan -  pessimistic mode - Phase", language,
						progress));

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
			serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.determinepositions", "Compute Action Plan -  computing positions", language, progress));

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
			serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.create_summary.normal_phase", "Create summary for normal phase action plan summary",
					language, progress));

			if (normalcomputation) {
				computeSummary(ActionPlanMode.APN);
				if (uncertainty) {
					computeSummary(ActionPlanMode.APP);
					computeSummary(ActionPlanMode.APO);
				}
			}

			// compute
			computeSummary(ActionPlanMode.APPN);

			// check if uncertainty for optimisitc and pessimistic compputations
			if (uncertainty) {

				// update progress
				progress = 70;

				System.out.println("compute Summary of Action Plan - optimistic mode - Phase");

				// ****************************************************************
				// * create summary for optimistic phase action plan summary
				// ****************************************************************

				// send feedback
				serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.create_summary.optimistic_phase",
						"Create summary for optimistic phase action plan summary", language, progress));

				// compute
				computeSummary(ActionPlanMode.APPO);

				// update progress
				progress += 10;

				System.out.println("compute Summary of Action Plan - pessimistic mode - Phase");

				// ****************************************************************
				// * create summary for pessimistic phase action plan summary
				// ****************************************************************
				serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.create_summary.pessimistic_phase",
						"Create summary for pessimistic phase action plan summary", language, progress));

				// compute
				computeSummary(ActionPlanMode.APPP);

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

			// send feedback
			serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.saved", "Saving Action Plans", language, progress));

			// save to database
			sericeAnalysis.saveOrUpdate(analysis);

			// return null: no errors
			return null;
		} catch (TrickException e) {
			e.printStackTrace();
			MessageHandler messageHandler = new MessageHandler(e);
			serviceTaskFeedback.send(idTask, messageHandler);
			return messageHandler;
		} catch (Exception e) {
			System.out.println("Action Plan saving failed! ");
			MessageHandler messageHandler = new MessageHandler(e.getMessage(), "Action Plan saving failed", language, e);
			serviceTaskFeedback.send(idTask, messageHandler);
			e.printStackTrace();
			// return messagehandler with errors
			return messageHandler;
		}
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
		List<ActionPlanEntry> actionPlan = this.analysis.getActionPlan(ActionPlanMode.APN);
		List<ActionPlanEntry> actionPlanO = this.analysis.getActionPlan(ActionPlanMode.APO);
		List<ActionPlanEntry> actionPlanP = this.analysis.getActionPlan(ActionPlanMode.APP);
		List<ActionPlanEntry> phaseActionPlan = this.analysis.getActionPlan(ActionPlanMode.APPN);
		List<ActionPlanEntry> phaseActionPlanO = this.analysis.getActionPlan(ActionPlanMode.APPO);
		List<ActionPlanEntry> phaseActionPlanP = this.analysis.getActionPlan(ActionPlanMode.APPP);

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
						position = "+" + String.valueOf(Integer.valueOf(actionPlan.get(j).getPosition()) - (i + 1));
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
						position = "+" + String.valueOf(Integer.valueOf(actionPlan.get(j).getPosition()) - (i + 1));
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
						position = "+" + String.valueOf(Integer.valueOf(phaseActionPlan.get(j).getPosition()) - (i + 1));
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
						position = "+" + String.valueOf(Integer.valueOf(phaseActionPlan.get(j).getPosition()) - (i + 1));
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
	 * @param mode
	 *            The Mode to Compute the Action Plan : Normal, Optimistic or
	 *            Pessimistic
	 * @param actionPlan
	 *            The Action Plan where the Final Values are Stored
	 * @throws Exception
	 */
	private void computeActionPlan(ActionPlanMode mode) throws Exception {

		// ****************************************************************
		// * variables initialisation
		// ****************************************************************
		ActionPlanEntry actionPlanEntry = null;
		MaturityMeasure maturityMeasure = null;
		NormalMeasure normalMeasure = null;
		List<TMA> TMAList = null;
		List<Measure> usedMeasures = new ArrayList<Measure>();
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
		TMAList = generateTMAList(this.analysis, usedMeasures, mode, 0, false, this.maturitycomputation, this.standards);

		// ****************************************************************
		// * parse all measures (to create complete action plan) until no more
		// measures are in the
		// list of measures
		// ****************************************************************
		while (usedMeasures.size() > 0) {

			// ****************************************************************
			// * calculate temporary Action Plan
			// ****************************************************************
			List<ActionPlanEntry> tmpActionPlan = generateTemporaryActionPlan(usedMeasures, actionPlanType, TMAList);

			// ****************************************************************
			// * take biggest ROSI or ROSMI from temporary action plan and add
			// it to final action
			// plan and remove measure from usefulmeasures list
			// ****************************************************************

			// check if first action plan entry is not null -> YES
			if (tmpActionPlan.get(0) != null) {

				// ****************************************************************
				// * initialise first element to be biggest rosi
				// ****************************************************************

				// first element has the biggest ROSI/ROSMI to start
				actionPlanEntry = tmpActionPlan.get(0);

				// ****************************************************************
				// * parse all elements of action plan and determine real
				// biggest rosi
				// ****************************************************************

				// parse action plan
				for (int i = 0; i < tmpActionPlan.size(); i++) {

					// check if current element's ROSI > supposed -> YES
					if (actionPlanEntry.getROI() < tmpActionPlan.get(i).getROI()) {

						// replace entry with current element
						actionPlanEntry = tmpActionPlan.get(i);
					}
				}

				// ****************************************************************
				// * at this point actionPlanEntry is the object with the
				// biggest ROSI
				// ****************************************************************

				actionPlanEntry.setPosition(index);
				index++;

				// ****************************************************************
				// * update ALE values for next action plan run
				// ****************************************************************

				// initialise variables
				maturityMeasure = null;
				normalMeasure = null;

				// check if it is a maturity measure -> YES
				if (actionPlanEntry.getMeasure().getAnalysisStandard().getStandard().getLabel().equals(Constant.STANDARD_MATURITY)) {

					// retrieve matrurity masure
					maturityMeasure = (MaturityMeasure) actionPlanEntry.getMeasure();

					// ****************************************************************
					// * update values for next run
					// ****************************************************************
					adaptValuesForMaturityMeasure(TMAList, actionPlanEntry, maturityMeasure);
				} else {

					// check if it is a maturity measure -> NO

					// retrieve measure
					normalMeasure = (NormalMeasure) actionPlanEntry.getMeasure();

					// ****************************************************************
					// * update values for next run
					// ****************************************************************
					adaptValuesForNormalMeasure(TMAList, actionPlanEntry, normalMeasure);

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
		TMAList.clear();
	}

	/**
	 * clone: <br>
	 * makes a clone of a given TMAList (in) and outputs a identic copy (out)
	 * 
	 * @param out
	 * @param in
	 * @throws CloneNotSupportedException
	 */
	public static void clone(List<TMA> out, List<TMA> in) throws CloneNotSupportedException {
		for (int i = 0; i < in.size(); i++)
			out.add((TMA) in.get(i).clone());
	}

	/**
	 * computePhaseActionPlan: <br>
	 * Computes the Action Plan Phase By Phase.
	 * 
	 * @param mode
	 *            The Mode to Compute: Normal, Optimistic or Pessimistic
	 * @throws Exception
	 * */
	private void computePhaseActionPlan(ActionPlanMode mode) throws Exception {

		// ****************************************************************
		// * variables initialisation
		// ****************************************************************
		ActionPlanEntry actionPlanEntry = null;
		Measure measure = null;
		List<TMA> TMAList = new ArrayList<TMA>();
		List<Measure> usedMeasures = new ArrayList<Measure>();

		int index = 1;

		// get actionplans of anaylsis
		List<ActionPlanEntry> phaseActionPlan = this.analysis.getActionPlans();

		// get actionplantype by given mode
		ActionPlanType actionPlanType = serviceActionPlanType.get(mode.getValue());

		// check if the actionplantype exists and add it to database if not
		if (actionPlanType == null) {
			actionPlanType = new ActionPlanType(mode);
			serviceActionPlanType.save(actionPlanType);
		}

		// ****************************************************************
		// * parse all phases where measures are in
		// ****************************************************************

		// parse all phases
		for (int phase = 0; phase < this.analysis.getPhases().size(); phase++) {

			// ****************************************************************
			// * check if TMAList is empty -> NO: after first time TMAList is
			// not empty, so ALE
			// values need to be reused
			// ****************************************************************
			if (TMAList.size() > 0) {

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
				List<TMA> tmpTMAList = (List<TMA>) ((ArrayList<TMA>) TMAList).clone();

				// ****************************************************************
				// * generate the TMAList
				// ****************************************************************
				TMAList = generateTMAList(this.analysis, usedMeasures, mode, this.analysis.getAPhase(phase).getNumber(), false, this.maturitycomputation, this.standards);

				// ****************************************************************
				// * update the created TMAList with previous values (ALE
				// values)
				// ****************************************************************

				// parse TMAList to edit the ALE values by assessment
				for (int i = 0; i < TMAList.size(); i++) {

					// ****************************************************************
					// * for each TMAList entry, parse temporary TMAList to find
					// assessments that
					// are the same to change the ALE values
					// ****************************************************************
					for (int j = 0; j < tmpTMAList.size(); j++) {

						// if the assessment corresponds to the current TMAList
						// -> YES
						if ((TMAList.get(i).getAssessment().getScenario().getName().equals(tmpTMAList.get(j).getAssessment().getScenario().getName()))
								&& (TMAList.get(i).getAssessment().getAsset().getName().equals(tmpTMAList.get(j).getAssessment().getAsset().getName()))) {

							// ****************************************************************
							// * edit the ALE value
							// ****************************************************************
							TMAList.get(i).setALE(tmpTMAList.get(j).getALE());

							// ****************************************************************
							// * recalculate the delta ALE
							// ****************************************************************
							TMAList.get(i).calculateDeltaALE();

							// ****************************************************************
							// * if 27002 standard, recalculate deltaALE
							// maturity if
							// maturity
							// computation -> YES
							// ****************************************************************
							if (TMAList.get(i).getStandard().getLabel().equals(Constant.STANDARD_27002) && maturitycomputation) {

								// ****************************************************************
								// * recalculate delta ALE Maturity
								// ****************************************************************
								TMAList.get(i).calculateDeltaALEMaturity();
							}
						}
					}
				}

				Map<String, Double> deltamaturities = new LinkedHashMap<String, Double>();

				for (TMA tma : TMAList) {

					if (tma.getStandard().getLabel().equals("27002")) {
						if (deltamaturities.get(tma.getMeasure().getMeasureDescription().getReference()) == null) {
							deltamaturities.put(tma.getMeasure().getMeasureDescription().getReference(), tma.getDeltaALEMat());
						} else {
							Double val = deltamaturities.get(tma.getMeasure().getMeasureDescription().getReference());
							val += tma.getDeltaALEMat();
							deltamaturities.put(tma.getMeasure().getMeasureDescription().getReference(), val);
						}
					}
				}

			} else {

				// ****************************************************************
				// * check if TMAList is empty -> YES: for the first time, the
				// TMAList is empty, so
				// do nothing
				// ****************************************************************
				TMAList = generateTMAList(this.analysis, usedMeasures, mode, this.analysis.getAPhase(phase).getNumber(), false, maturitycomputation, standards);

				Map<String, Double> deltamaturities = new LinkedHashMap<String, Double>();

				for (TMA tma : TMAList) {

					if (tma.getStandard().getLabel().equals("27002")) {

						if (deltamaturities.get(tma.getMeasure().getMeasureDescription().getReference()) == null) {
							deltamaturities.put(tma.getMeasure().getMeasureDescription().getReference(), tma.getDeltaALEMat());
						} else {
							Double val = deltamaturities.get(tma.getMeasure().getMeasureDescription().getReference());
							val += tma.getDeltaALEMat();
							deltamaturities.put(tma.getMeasure().getMeasureDescription().getReference(), val);
						}
					}
				}
			}

			// ****************************************************************
			// * generate action plan for this phase
			// ****************************************************************

			// parse all measures

			while (usedMeasures.size() > 0) {

				// ****************************************************************
				// * calculate temporary Action Plan
				// ****************************************************************
				List<ActionPlanEntry> tmpactionPlan = generateTemporaryActionPlan(usedMeasures, actionPlanType, TMAList);

				// ****************************************************************
				// * determine biggest ROSI or ROSMI from temporary action plan
				// and add it to final
				// action plan remove measure from usefulmeasures list adapt
				// values for the next run
				// ****************************************************************

				// check if first element is not null
				if (tmpactionPlan.get(0) != null) {

					// ****************************************************************
					// * start with the first element to check if it is the
					// biggest rosi
					// ****************************************************************
					actionPlanEntry = tmpactionPlan.get(0);

					// ****************************************************************
					// * parse the action plan to find the biggest ROSI
					// ****************************************************************

					// parse action plan
					for (int i = 0; i < tmpactionPlan.size(); i++) {

						// check if current element ROSI > supposed element
						if (actionPlanEntry.getROI() < tmpactionPlan.get(i).getROI()) {

							// replace element with current element
							actionPlanEntry = tmpactionPlan.get(i);
						}
					}

					// ****************************************************************
					// * at this time actionPlanEntry has the biggest ROSI
					// ****************************************************************

					setSOARisk(actionPlanEntry, TMAList);

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
						adaptValuesForMaturityMeasure(TMAList, actionPlanEntry, (MaturityMeasure) measure);
					} else {

						// ****************************************************************
						// * change values for the next run
						// ****************************************************************
						adaptValuesForNormalMeasure(TMAList, actionPlanEntry, measure);

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
		TMAList.clear();
	}

	private void setSOARisk(ActionPlanEntry entry, List<TMA> TMAList) throws TrickException {

		double report = 0;

		double tmpreport = 0;

		Assessment asm = null;

		report = -1;
		asm = null;

		if (!entry.getMeasure().getAnalysisStandard().getStandard().getLabel().equals(Constant.STANDARD_27002))
			return;

		for (int i = 0; i < TMAList.size(); i++) {

			if (!entry.getMeasure().equals(TMAList.get(i).getMeasure()))
				continue;

			Assessment tmpAssessment = null;

			for (int k = 0; k < this.analysis.getAssessments().size(); k++) {
				if (TMAList.get(i).getAssessment().equals(this.analysis.getAnAssessment(k))) {
					tmpAssessment = this.analysis.getAnAssessment(k);
					break;
				}
			}

			if (tmpAssessment != null) {

				tmpreport = TMAList.get(i).getDeltaALE() / tmpAssessment.getALE() * 100;

				if (tmpreport > report) {
					report = tmpreport;
					asm = tmpAssessment;
				}
			}
		}

		String language = this.analysis.getLanguage().getAlpha2();

		Locale locale = new Locale(language);

		String soarisk = messageSource.getMessage("label.soa.asset", null, "Asset:", locale) + " " + asm.getAsset().getName() + " \n ";
		soarisk += messageSource.getMessage("label.soa.scenario", null, "Scenario:", locale) + " " + asm.getScenario().getName() + " \n ";

		double val = report;

		NumberFormat nf = new DecimalFormat();
		nf.setMaximumFractionDigits(2);

		try {
			val = nf.parse(nf.format(val)).doubleValue();
		} catch (ParseException e) {
			e.printStackTrace();
			throw new TrickException("error.number.format", e.getMessage());
		}

		soarisk += messageSource.getMessage("label.soa.rate", null, "Rate:", locale) + " " + String.valueOf(val);

		((NormalMeasure) entry.getMeasure()).getMeasurePropertyList().setSoaRisk(soarisk);
		// serviceMeasure.saveOrUpdate(entry.getMeasure());
	}

	/***********************************************************************************************
	 * Temporary Action Plan - BEGIN
	 **********************************************************************************************/

	/**
	 * generateTemporaryActionPlan: <br>
	 * Generates the Temporary Action Plan based on the "TMAList" values and the
	 * usedMeasures List. Where usedMeasures is the List of Measures to add to
	 * the Action Plan.
	 * 
	 * @return The Temporary Action Plan Entries
	 * 
	 * @throws InvalidAttributesException
	 * @throws TrickException
	 */
	private List<ActionPlanEntry> generateTemporaryActionPlan(List<Measure> usedMeasures, ActionPlanType actionPlanType, List<TMA> TMAList) throws InvalidAttributesException,
			TrickException {

		// ****************************************************************
		// * variables initialisation
		// ****************************************************************
		List<ActionPlanEntry> tmpActionPlan = new ArrayList<ActionPlanEntry>();

		// ****************************************************************
		// * generate normal action plan entries
		// ****************************************************************
		generateNormalActionPlanEntries(tmpActionPlan, actionPlanType, usedMeasures, TMAList);

		// ****************************************************************
		// * generate maturtiy action plan entries
		// ****************************************************************
		generateMaturtiyChapterActionPlanEntries(tmpActionPlan, usedMeasures, TMAList);

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
	 * @param tmpActionPlan
	 *            The Temporary Action Plan with all usable Entries
	 * @throws InvalidAttributesException
	 * @throws TrickException
	 */
	private void generateNormalActionPlanEntries(List<ActionPlanEntry> tmpActionPlan, ActionPlanType actionPlanType, List<Measure> usedMeasures, List<TMA> TMAList)
			throws InvalidAttributesException, TrickException {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		double deltaALE = 0;
		double totalALE = 0;
		Measure measure = null;
		ActionPlanEntry actionPlanEntry = null;
		List<ActionPlanAsset> tmpAssets = null;
		double ALE = 0;

		// ****************************************************************
		// * parse usedmeasures and generate action plan entries
		// ****************************************************************

		// parse measures
		for (int i = 0; i < usedMeasures.size(); i++) {

			// ****************************************************************
			// * create array of assets that are selected (for "per asset ALE")
			// ****************************************************************

			tmpAssets = createSelectedAssetsList();

			// ****************************************************************
			// * calculate values for action plan entry and add entry to
			// temporary action plan
			// ****************************************************************

			// ****************************************************************
			// * check if the measure is not a maturity measure -> NO
			// ****************************************************************
			if (!usedMeasures.get(i).getMeasureDescription().getReference().startsWith(Constant.MATURITY_REFERENCE)) {

				// ****************************************************************
				// * calculate action plan entry ALE and delta ALE from TMAList
				// ****************************************************************

				// reinitialise variables
				deltaALE = 0;

				totalALE = 0;

				// temporary store the measure
				measure = usedMeasures.get(i);

				// parse TMAList
				for (int j = 0; j < TMAList.size(); j++) {

					// ****************************************************************
					// * check if the measure is the current measure -> YES
					// ****************************************************************
					if (TMAList.get(j).getMeasure().equals(measure)) {

						// ****************************************************************
						// * take ALE to calculate the sum of ALE (total ALE)
						// ****************************************************************
						totalALE = totalALE + TMAList.get(j).getALE();

						// ****************************************************************
						// * calculate ALE by asset for this action plan entry
						// ****************************************************************

						// parse assets
						for (int ac = 0; ac < tmpAssets.size(); ac++) {

							// ****************************************************************
							// * take previous value and add current ALE and
							// rewrite previous value
							// (of tmpAssets)
							// ****************************************************************
							if (tmpAssets.get(ac).getAsset().equals(TMAList.get(j).getAssessment().getAsset())) {

								// ****************************************************************
								// * Calculate new ALE for this asset
								// ****************************************************************

								// store current value
								ALE = tmpAssets.get(ac).getCurrentALE();

								// add this ALE
								ALE = ALE + TMAList.get(j).getALE();

								// calculate minus deltaALE
								ALE = ALE - TMAList.get(j).getDeltaALE();

								// ****************************************************************
								// * update the object's ALE value
								// ****************************************************************
								tmpAssets.get(ac).setCurrentALE(ALE);
							}
						}

						// ****************************************************************
						// * take deltaALE to calculate the sum of deltaALE
						// ****************************************************************
						if (measure.getMeasureDescription().getStandard().isComputable())
							deltaALE = deltaALE + TMAList.get(j).getDeltaALE();
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
	 * Generate Action Plan Entries for the Maturity Chapters Inside an Action
	 * Plan.
	 * 
	 * @param tmpActionPlan
	 *            The Action Plan to Add Maturity Chapters
	 * @param usedMeasures
	 *            Measures to use on the actionplan
	 * @param TMAList
	 *            The list of TMA
	 * @throws InvalidAttributesException
	 * @throws TrickException
	 */
	private void generateMaturtiyChapterActionPlanEntries(List<ActionPlanEntry> tmpActionPlan, List<Measure> usedMeasures, List<TMA> TMAList) throws InvalidAttributesException,
			TrickException {

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
		NormalMeasure tmpMeasure = null;
		boolean found = false;
		List<ActionPlanAsset> tmpAssets = null;
		double ALE = 0;
		List<NormalMeasure> normalMeasureList = null;
		List<Double> tmpDeltaALEMat = null;
		double deltaALEMat = 0;
		double numberMeasures = 0;

		// ****************************************************************
		// * check action plan on untreated maturtiy entries to calculate
		// ****************************************************************

		// parse temporary action plan
		for (int apmc = 0; apmc < tmpActionPlan.size(); apmc++) {

			// check it is a maturity measure -> YES
			if (tmpActionPlan.get(apmc).getMeasure().getMeasureDescription().getReference().startsWith(Constant.MATURITY_REFERENCE)) {

				// ****************************************************************
				// * create array of assets that are selected (for
				// "per asset ALE")
				// ****************************************************************
				tmpAssets = createSelectedAssetsList();

				// ****************************************************************
				// * create a vector for maturity deltaALE and initialise it
				// ****************************************************************
				tmpDeltaALEMat = new ArrayList<Double>();

				// initialise deltaALEMat values to 0

				// parse all assets and create as much deltaALEMaturity values
				for (int asc = 0; asc < tmpAssets.size(); asc++) {

					// set deltaALEMaturity to 0
					tmpDeltaALEMat.add((double) 0);
				}

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

				// initialise cost to 0
				totalCost = 0;

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
				normalMeasureList = new ArrayList<NormalMeasure>();

				// ****************************************************************
				// * parse TMAList to calculate totalALE and deltaALE and
				// deltaALE Maturity for the
				// action plan entry
				// ****************************************************************

				// parse TMAList entries
				for (int napmc = 0; napmc < TMAList.size(); napmc++) {

					// temporary store measure
					tmpMeasure = (NormalMeasure) TMAList.get(napmc).getMeasure();

					// ****************************************************************
					// * parse TMAList for AnalysisStandard 27002 measures and
					// inside this chapter
					// ****************************************************************
					if ((TMAList.get(napmc).getStandard().getLabel().equals(Constant.STANDARD_27002))
							&& (tmpMeasure.getMeasureDescription().getReference().startsWith(maturityChapter))) {

						// ****************************************************************
						// * add measure to a list if it does not yet exist,
						// else do not add the
						// measure (measure can only be there once)
						// ****************************************************************

						// initialise flag
						found = false;

						// parse list of measures
						for (int i = 0; i < normalMeasureList.size(); i++) {

							// check if the measure already exists -> YES
							if (normalMeasureList.get(i).equals(tmpMeasure)) {

								// ****************************************************************
								// * measure already exists
								// ****************************************************************
								found = true;

								// exist loop
								break;
							}
						}

						// ****************************************************************
						// * measure not yet in the list? -> NO
						// ****************************************************************
						if (found == false) {

							// ****************************************************************
							// * add measre to list
							// ****************************************************************
							normalMeasureList.add(tmpMeasure);
						}

						// ****************************************************************
						// * calculate totalALE
						// ****************************************************************
						totalChapter = totalChapter + TMAList.get(napmc).getALE();

						// ****************************************************************
						// * update asset ALE values and delta ALE maturity
						// values
						// ****************************************************************

						// parse all assets
						for (int ac = 0; ac < tmpAssets.size(); ac++) {

							// ****************************************************************
							// * take previous value and add current ALE and
							// rewrite previous value
							// (of tmpAssets)
							// ****************************************************************
							if (tmpAssets.get(ac).getAsset().equals(TMAList.get(napmc).getAssessment().getAsset())) {

								// ****************************************************************
								// * update ALE of asset
								// ****************************************************************
								// store current value
								ALE = tmpAssets.get(ac).getCurrentALE();

								// add this ALE
								ALE = ALE + TMAList.get(napmc).getALE();

								// update the object's ALE value
								tmpAssets.get(ac).setCurrentALE(ALE);

								// ****************************************************************
								// * update delta ALE Maturity
								// ****************************************************************

								// take deltaALE for this deltaALEMat
								deltaALEMat = tmpDeltaALEMat.get(ac);

								// calculate addition of deltaALEMat
								deltaALEMat = deltaALEMat + TMAList.get(napmc).getDeltaALEMat();

								// rewrite current deltaALEMat with newest value
								tmpDeltaALEMat.set(ac, (double) deltaALEMat);
							}
						}

						// ****************************************************************
						// * calculate deltaALE
						// ****************************************************************
						deltaALE = deltaALE + TMAList.get(napmc).getDeltaALEMat();
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
				numberMeasures = (double) normalMeasureList.size();

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
					ALE = tmpAssets.get(asc).getCurrentALE();

					// divide with number of measures
					ALE = ALE / numberMeasures;

					// calculate minus deltaALEMat
					ALE = ALE - tmpDeltaALEMat.get(asc);

					// rewrite this asset ALE value
					tmpAssets.get(asc).setCurrentALE(ALE);
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
	 * Create a fresh List of Assets which are only selected. This is used to
	 * set the current ALE by Asset to the Action Plan Assets.
	 * 
	 * @return The Copy of the List of Assets
	 * 
	 * @throws InvalidAttributesException
	 * @throws TrickException
	 */
	private List<ActionPlanAsset> createSelectedAssetsList() throws InvalidAttributesException, TrickException {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		Asset tmpAsset = null;
		List<ActionPlanAsset> tmpAssets = new ArrayList<ActionPlanAsset>();

		// ****************************************************************
		// * take each asset and make a copy into another list
		// ****************************************************************

		// parse assets
		for (int asc = 0; asc < this.analysis.getAssets().size(); asc++) {

			// selected asset -> YES
			if (this.analysis.getAnAsset(asc).isSelected() && !tmpAssets.contains(this.analysis.getAnAsset(asc))) {

				// ****************************************************************
				// * create new asset object
				// ****************************************************************

				tmpAsset = new Asset();
				tmpAsset.setComment(this.analysis.getAnAsset(asc).getComment());
				tmpAsset.setId(this.analysis.getAnAsset(asc).getId());
				tmpAsset.setName(this.analysis.getAnAsset(asc).getName());
				tmpAsset.setSelected(this.analysis.getAnAsset(asc).isSelected());
				tmpAsset.setAssetType(new AssetType(this.analysis.getAnAsset(asc).getAssetType().getType()));
				tmpAsset.setValue(this.analysis.getAnAsset(asc).getValue());

				// ****************************************************************
				// * add asset to the list
				// ****************************************************************
				tmpAssets.add(new ActionPlanAsset(null, tmpAsset, 0));
			}
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
	 * @param actionPlanEntry
	 *            The Action Plan Entry(used to store ALE values of the Assets)
	 * @param normalMeasure
	 *            The taken AnalysisStandard Measure
	 * @throws TrickException
	 */
	private void adaptValuesForNormalMeasure(List<TMA> TMAList, ActionPlanEntry actionPlanEntry, Measure measure) throws TrickException {

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
		for (int i = 0; i < TMAList.size(); i++) {

			// temporary store TMA entry
			tmpTMA = TMAList.get(i);

			// check if the TMA entry has the given measure -> YES
			if (tmpTMA.getMeasure().equals(measure)) {

				// take the deltaALE for this measure
				deltaALE = tmpTMA.getDeltaALE();

				// ****************************************************************
				// * edit all ALE for the same assessment
				// ****************************************************************

				// reparse TMAList
				for (int j = 0; j < TMAList.size(); j++) {

					// check if assessment is the same -> YES
					if ((TMAList.get(j).getAssessment().equals(tmpTMA.getAssessment()))) {

						// ****************************************************************
						// * edit the ALE value of the TMAList element
						// ****************************************************************
						TMAList.get(j).setALE(TMAList.get(j).getALE() - deltaALE);

						// ****************************************************************
						// * recompute the DeltaALE
						// ****************************************************************
						TMAList.get(j).calculateDeltaALE();

						// if the measure is from 27002 -> YES
						if (TMAList.get(j).getStandard().getLabel().equals(Constant.STANDARD_27002)) {

							// ****************************************************************
							// * calculate deltaALEMaturity
							// ****************************************************************
							TMAList.get(j).calculateDeltaALEMaturity();
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
	 * @param TMAList
	 * @param actionPlanEntry
	 * @param maturityMeasure
	 * @throws TrickException
	 */
	private void adaptValuesForMaturityMeasure(List<TMA> TMAList, ActionPlanEntry actionPlanEntry, MaturityMeasure maturityMeasure) throws TrickException {

		// ****************************************************************
		// * variable initialisation
		// ****************************************************************
		double deltaALE;
		TMA tmpTMA = null;
		String chapter = "";
		Assessment assessment = null;

		// ****************************************************************
		// * determine the maturity chapter
		// ****************************************************************
		chapter = maturityMeasure.getMeasureDescription().getReference().substring(2, maturityMeasure.getMeasureDescription().getReference().length());

		// ****************************************************************
		// * parse assessments of analysis to update ALE values
		// ****************************************************************

		// parse assessments
		for (int indexAssessment = 0; indexAssessment < this.analysis.getAssessments().size(); indexAssessment++) {

			// temporary store assessment
			assessment = this.analysis.getAnAssessment(indexAssessment);

			// check if asset and scenario is selected for calculation and ALE >
			// 0 -> YES
			if (assessment.getAsset().isSelected() && assessment.getScenario().isSelected() && assessment.getALE() > 0) {

				// ****************************************************************
				// * calculate total deltaALE
				// ****************************************************************

				// initialise delta ALE
				deltaALE = 0;

				// parse all elements of the TMAList and sum deltaALE
				for (int i = 0; i < TMAList.size(); i++) {

					// temporary store the TMA element
					tmpTMA = TMAList.get(i);

					// parse each element where the measure is the one that was
					// taken, and when
					// assessment couple is the same
					if ((tmpTMA.getMeasure().getMeasureDescription().getReference().startsWith(chapter)) && (tmpTMA.getStandard().getLabel().equals(Constant.STANDARD_27002))
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
				for (int j = 0; j < TMAList.size(); j++) {

					// find all assessments that are the same as for the current
					if ((TMAList.get(j).getAssessment().getId() == assessment.getId())) {

						// ****************************************************************
						// * edit the ALE value
						// ****************************************************************
						TMAList.get(j).setALE(TMAList.get(j).getALE() - deltaALE);

						// ****************************************************************
						// * recompute the deltaALE
						// ****************************************************************
						TMAList.get(j).calculateDeltaALE();

						// if it is a 27002 standard ->YES
						if (TMAList.get(j).getStandard().getLabel().equals(Constant.STANDARD_27002) && maturitycomputation) {

							// ****************************************************************
							// * calculate the deltaALEMaturity
							// ****************************************************************
							TMAList.get(j).calculateDeltaALEMaturity();
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
	 * Generates a list of Measure-Assessment-Threat and Calculates for this
	 * Triple the deltaALE and if it is a Measure of the AnalysisStandard 27002
	 * the deltaALE Maturity. <br>
	 * The Parameter usedMeasures will have a list of Measures that are to be
	 * used for the Action Plan Calculation. The Method returns the List of TMA
	 * Entries and inside the parameter usedMeasures the Measures.
	 * 
	 * @param usedMeasures
	 *            List to store the Measures used for Action Plan Calculation
	 *            (will be filled inside)
	 * @param mode
	 *            Defines if the Mode is Normal, Optimistic or Pessimistic
	 * @param phase
	 *            Defines if the Phase Calculation is Enabled and what Phase to
	 *            take into account
	 * @param isCssf
	 *            Flag determinating if TMAList is for CSSF computation or not
	 * @param maturitycomputation
	 *            flag determinating if maturity is computed or not
	 * @param standards
	 *            List of AnalysisStandards to be used in the actionplan (to
	 *            generate only TMA entries for the given standards)
	 * @throws TrickException
	 */
	public static List<TMA> generateTMAList(Analysis analysis, List<Measure> usedMeasures, ActionPlanMode mode, int phase, boolean isCssf, boolean maturitycomputation,
			List<AnalysisStandard> standards) throws TrickException {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<TMA> TMAList = new ArrayList<TMA>();

		// ****************************************************************
		// * clear List
		// ****************************************************************
		if (usedMeasures != null) {
			usedMeasures.clear();
		}

		// ****************************************************************
		// * generate TMAListEntries
		// ****************************************************************

		// ****************************************************************
		// * parse all MeasureStandard measures to generate TMA entries
		// ****************************************************************

		// parse all standards
		for (int nC = 0; nC < analysis.getAnalysisStandards().size(); nC++) {

			// initialise standard

			AnalysisStandard anlysisStandard = analysis.getAnalysisStandard(nC);

			if (!standards.contains(anlysisStandard))
				continue;

			// ****************************************************************
			// * check if not Maturity standard -> NO
			// ****************************************************************
			if (anlysisStandard instanceof NormalStandard) {

				// store standard as it's real type
				NormalStandard normalStandard = (NormalStandard) anlysisStandard;

				// ****************************************************************
				// * parse all measures of the current standard
				// ****************************************************************
				for (int mC = 0; mC < normalStandard.getMeasures().size(); mC++) {

					// temporary store the measure
					NormalMeasure normalMeasure = normalStandard.getMeasure(mC);

					// ****************************************************************
					// * check conditions to add TMAListEntries to TMAList
					// ****************************************************************

					// ****************************************************************
					// * check if measure is applicable, mandatory and
					// implementation rate is not
					// 100% -> YES
					// ****************************************************************
					if (!(normalMeasure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE))
							&& (normalMeasure.getImplementationRate() < Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE) && (normalMeasure.getMeasureDescription().isComputable())
							&& (normalMeasure.getCost() >= 0)) {

						// ****************************************************************
						// * when phase computation, phase is bigger than 0,
						// take these values that
						// equals the phase number -> YES
						// ****************************************************************
						if (((phase > 0) && (normalMeasure.getPhase().getNumber() == phase)) || (phase == 0)) {

							// ****************************************************************
							// * generate TMA entry -> useful measure
							// ****************************************************************
							generateTMAEntry(analysis, TMAList, usedMeasures, mode, normalStandard.getStandard(), normalMeasure, true, maturitycomputation, standards);
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

							if (!isCssf && normalStandard.getStandard().getLabel().equals(Constant.STANDARD_27002) && maturitycomputation) {

								// ****************************************************************
								// * generate TMA entry -> not a useful measure
								// ****************************************************************
								generateTMAEntry(analysis, TMAList, usedMeasures, mode, normalStandard.getStandard(), normalMeasure, false, maturitycomputation, standards);
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
						if (!isCssf && !(normalMeasure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)) && (normalMeasure.getMeasureDescription().isComputable())
								&& (normalMeasure.getCost() >= 0) && (normalStandard.getStandard().getLabel().equals(Constant.STANDARD_27002) && (maturitycomputation))) {

							// ****************************************************************
							// * generate TMA entry -> not a useful measure
							// ****************************************************************
							generateTMAEntry(analysis, TMAList, usedMeasures, mode, normalStandard.getStandard(), normalMeasure, false, maturitycomputation, standards);
						}
					}
				}
			} else if (anlysisStandard instanceof AssetStandard) {

				AssetStandard assetStandard = null;

				// store standard as it's real type
				assetStandard = (AssetStandard) anlysisStandard;

				// ****************************************************************
				// * parse all measures of the current standard
				// ****************************************************************
				for (int mC = 0; mC < assetStandard.getMeasures().size(); mC++) {

					// temporary store the measure
					AssetMeasure assetMeasure = (AssetMeasure) assetStandard.getMeasure(mC);

					// ****************************************************************
					// * check conditions to add TMAListEntries to TMAList
					// ****************************************************************

					// ****************************************************************
					// * check if measure is applicable, mandatory and
					// implementation rate is not
					// 100% -> YES
					// ****************************************************************
					if (!(assetMeasure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE))
							&& (assetMeasure.getImplementationRate() < Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE) && (assetMeasure.getMeasureDescription().isComputable())
							&& (assetMeasure.getCost() >= 0)) {

						// ****************************************************************
						// * when phase computation, phase is bigger than 0,
						// take these values that
						// equals the phase number -> YES
						// ****************************************************************
						if (((phase > 0) && (assetMeasure.getPhase().getNumber() == phase)) || (phase == 0)) {

							// ****************************************************************
							// * generate TMA entry -> useful measure
							// ****************************************************************
							generateTMAEntry(analysis, TMAList, usedMeasures, mode, assetStandard.getStandard(), assetMeasure, true, maturitycomputation, standards);
						}
					}
				}
			}
		}

		// ****************************************************************
		// * add maturity chapters to list of useful measures
		// ****************************************************************

		if (!isCssf && usedMeasures != null && maturitycomputation) {

			addMaturityChaptersToUsedMeasures(analysis, usedMeasures, phase, standards);
		}

		// return TMAList
		return TMAList;

	}

	/**
	 * generateTMAEntry: <br>
	 * This method generates for a given Measure TMA (Threat Measure Assessment)
	 * entries in the List "TMAList". This method adds this measure to the list
	 * of usedMEasures given as parameter.
	 * 
	 * @param TMAList
	 *            The List to insert the current TMA Entry
	 * @param usedMeasures
	 *            The List of Measures to add the current Measure (from TMA
	 *            Entry) to be used
	 * @param mode
	 *            Defines which Type of Action Plan is Calculated (to take the
	 *            correct ALE value)
	 * @param normalStandard
	 *            The AnalysisStandard of the Measure (only NormalStandard)
	 * @param normalMeasure
	 *            The Measure of the AnalysisStandard (NormalMeasure)
	 * @param usefulMeasure
	 *            Flag to determine is this measure needs to be added to the
	 *            usedMeasures (a valid Measure)
	 * @throws TrickException
	 */
	private static void generateTMAEntry(Analysis analysis, List<TMA> TMAList, List<Measure> usedMeasures, ActionPlanMode mode, Standard standard, Measure measure,
			boolean usefulMeasure, boolean maturitycomputation, List<AnalysisStandard> standards) throws TrickException {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		TMA tmpTMA = null;
		Assessment tmpAssessment = null;
		MaturityStandard maturityStandard = null;
		boolean measureFound = false;
		String tmpReference = "";
		int matLevel = 0;
		Parameter param = null;
		double rrf = 0;
		double cMaxEff = -1;
		double nMaxEff = -1;

		// ****************************************************************
		// * parse assesments to generate TMA entries
		// ****************************************************************

		if ((usefulMeasure) || (maturitycomputation && !usefulMeasure)) {

			// parse each assessment
			for (int aC = 0; aC < analysis.getAssessments().size(); aC++) {

				// temporary store the assessment
				tmpAssessment = analysis.getAnAssessment(aC);

				// check if threat (scenario) and asset are selected for the
				// computation AND ALE > 0
				// -> YES
				if (tmpAssessment.isUsable()) {

					// ****************************************************************
					// * calculate RRF
					// ****************************************************************
					rrf = RRF.calculateRRF(tmpAssessment, analysis.getParameters(), measure);

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
							tmpTMA.calculateDeltaALE();
						else
							tmpTMA.setDeltaALE(0);
					} else
						tmpTMA.calculateDeltaALE();

					// ****************************************************************
					// * check if measure needs to taken into account for action
					// plan calculation.
					// TMA entries need to be generated for 27002 because of
					// maturity. Special case
					// ****************************************************************

					// measure needs to be taken into account? -> YES
					if (usefulMeasure && usedMeasures != null && standards != null) {

						// ****************************************************************
						// * check if measure is already on the list, if not:
						// add it
						// ****************************************************************

						// add this to useful measures list if exists variable
						// to check
						measureFound = false;

						// parse usedMeasures
						for (int unml = 0; unml < usedMeasures.size(); unml++) {

							// check if current measure exists in list -> YES
							if (usedMeasures.get(unml).equals(measure)) {

								// ****************************************************************
								// * the measure exist
								// ****************************************************************
								measureFound = true;

								// break out of loop
								break;
							}
						}

						// ****************************************************************
						// * check if the measure was found, if not: add it
						// ****************************************************************
						if (measureFound == false) {

							// ****************************************************************
							// * add to the list of measures
							// ****************************************************************
							usedMeasures.add(measure);
						}

					}

					// ****************************************************************
					// * check if measure is from 27002 standard (for maturity)
					// ****************************************************************
					if (standard.getLabel().equals(Constant.STANDARD_27002)) {

						// ****************************************************************
						// * retrieve reached SML
						// ****************************************************************

						// ****************************************************************
						// * extract useful reference data from reference (the
						// chapter part)
						// ****************************************************************

						// store reference
						tmpReference = measure.getMeasureDescription().getReference();

						// create chapter reference to check on maturity
						tmpReference = tmpReference.substring(0, tmpReference.indexOf("."));

						// ****************************************************************
						// * Parse standards to find maturity standard to
						// retrieve SML
						// from this chapter
						// (which is inside tmpReference)
						// ****************************************************************

						// parse all standards
						for (int tnc = 0; tnc < analysis.getAnalysisStandards().size(); tnc++) {

							// check if standard is maturity -> YES
							if (analysis.getAnalysisStandard(tnc) instanceof MaturityStandard) {

								// store maturity standard object
								maturityStandard = (MaturityStandard) analysis.getAnalysisStandard(tnc);

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
									if (maturityStandard.getMeasure(tmc).getMeasureDescription().getReference().equals(Constant.MATURITY_REFERENCE + tmpReference)) {

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
							for (int i = 0; i < analysis.getParameters().size(); i++) {

								// temporary store current parameter
								param = analysis.getAParameter(i);

								// check if it is current maxeffency -> YES
								if (param.getDescription().equals("SML" + String.valueOf(matLevel))) {

									// ****************************************************************
									// * store current max effency value
									// ****************************************************************
									cMaxEff = param.getValue();

									// check if both parameters were found ->
									// YES
									if ((cMaxEff > -1) && (nMaxEff > -1)) {

										// leave loop
										break;
									}
								} else {

									// check if it is current maxeffency -> NO

									// check if it is next maxeffency -> YES
									if (param.getDescription().equals("SML" + String.valueOf(matLevel + 1))) {

										// *************************************************************
										// * store next max effency value
										// *************************************************************
										nMaxEff = param.getValue();

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
							tmpTMA.calculateDeltaALEMaturity();
						}
					}

					// ****************************************************************
					// * add TMA object in the list of TMA's to calculate the
					// Action Plan
					// ****************************************************************
					TMAList.add(tmpTMA);
				}
			}
		}
	}

	/**
	 * addMaturityChaptersToUsedMeasures: <br>
	 * Parse Maturity Measure and Add only Chapters of Maturity to
	 * "usedmeasures" parameter. This is used to identify the Maturity Measures
	 * to Add to the Action Plan. If Parameter "phase" is not 0 then add
	 * Maturity Chapters for the given Phase.
	 * 
	 * @param analysis
	 *            analysis object
	 * @param usedMeasures
	 *            list of measures to use on the action plan
	 * @param phase
	 *            current phase number
	 * @param standards
	 *            list of standards to implement on the actionplan
	 */
	private static void addMaturityChaptersToUsedMeasures(Analysis analysis, List<Measure> usedMeasures, int phase, List<AnalysisStandard> standards) {

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
		for (int nc = 0; nc < analysis.getAnalysisStandards().size(); nc++) {

			AnalysisStandard analysisStandard = analysis.getAnalysisStandard(nc);

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
			for (int matmeasc = 0; matmeasc < maturityStandard.getMeasures().size(); matmeasc++) {

				// check reference if level 1 chapter that is currently parsed
				// and if reached SML <
				// 5
				if ((maturityStandard.getMeasure(matmeasc).getMeasureDescription().getLevel() == Constant.MEASURE_LEVEL_1)
						&& (maturityStandard.getMeasure(matmeasc).getReachedLevel() < 5)
						&& (((phase > 0) && (maturityStandard.getMeasure(matmeasc).getPhase().getNumber() == phase)) || (phase == 0))) {

					// add Maturity Chapter as nessesary
					addAMaturtiyChapterToUsedMeasures(analysis, usedMeasures, maturityStandard, maturityStandard.getMeasure(matmeasc));
				}
			}
		}
	}

	/**
	 * addAMaturtiyChapterToUsedMeasures: <br>
	 * Checks if a Maturity Chapter has a total cost > 0 and if for this
	 * chapter, there is at least 1 measure of 27002 applicable for this
	 * chapter. When both costrains are met, the measure will be added to the
	 * list "usedMeasures" given as parameter.
	 * 
	 * @param usedMeasures
	 *            The List of Measure to add the valid Maturity Chapter to
	 * @param maturityStandard
	 *            The Maturity AnalysisStandard Object
	 * @param measure
	 *            The Measure which represents the Maturity Chapter
	 */
	private static void addAMaturtiyChapterToUsedMeasures(Analysis analysis, List<Measure> usedMeasures, MaturityStandard maturityStandard, MaturityMeasure measure) {

		// extract chapter number from level 1 measure
		String chapterValue = measure.getMeasureDescription().getReference().substring(2, measure.getMeasureDescription().getReference().length());

		// check if measure has to be added -> YES
		if ((isMaturityChapterTotalCostBiggerThanZero(maturityStandard, measure)) && (hasUsable27002MeasuresInMaturityChapter(analysis, chapterValue))) {

			// add measure to list of used measures
			usedMeasures.add(measure);
		}
	}

	/**
	 * isMaturityChapterTotalCostBiggerThanZero: <br>
	 * Checks if the Total Cost of a Maturity Chapter is bigger than 0 euros.
	 * 
	 * @param maturityStandard
	 *            The Maturity AnalysisStandard Object
	 * @param chapter
	 *            The Maturity Chapter Measure Object (Level 1 Measure)
	 * @return True if the Cost is > 0; False if Cost is 0
	 */
	private static final boolean isMaturityChapterTotalCostBiggerThanZero(MaturityStandard maturityStandard, MaturityMeasure chapter) {

		// initialise measure cost
		double totalCost = 0;

		// extract chapter number from level 1 measure
		String chapterValue = chapter.getMeasureDescription().getReference().substring(2, chapter.getMeasureDescription().getReference().length());

		// parse measure of maturity standard
		for (int i = 0; i < maturityStandard.getMeasures().size(); i++) {

			// *********************************************************
			// * perform checks to take only cost of usable measures
			// *********************************************************

			// check if reference starts with
			// "M.<currentChapter>.<currentSML+1>." and if applicable
			// and implementation rate is less than 100%
			if ((maturityStandard.getMeasure(i).getMeasureDescription().getReference().startsWith(Constant.MATURITY_REFERENCE + chapterValue + "."
					+ String.valueOf(chapter.getReachedLevel() + 1) + "."))
					&& (!maturityStandard.getMeasure(i).getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE))
					&& (maturityStandard.getMeasure(i).getImplementationRateValue() < Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE)) {

				// *****************************************************
				// * useful measure was found: add the cost to the total cost of
				// measure
				// *****************************************************
				totalCost += maturityStandard.getMeasure(i).getCost();
			}
		}

		// check if cost is larger than 0 euros -> YES
		if (totalCost > 0) {

			// return true
			return true;
		} else {

			// return false
			return false;
		}
	}

	/**
	 * hasUsable27002MeasuresInMaturityChapter: <br>
	 * Checks if a given Maturity Chapter has usable Measures in the appropriate
	 * chapter in the 27002 AnalysisStandard.
	 * 
	 * @param chapter
	 *            The Maturity Chapter to check
	 * @return True if there is at least 1 Measure inside the AnalysisStandard
	 *         27002 Chapter that is applicable ;False if there are no Measures
	 *         in the 27002 AnalysisStandard
	 */
	private static boolean hasUsable27002MeasuresInMaturityChapter(Analysis analysis, String chapter) {

		// initialise variables
		NormalStandard normalStandard = null;
		boolean result = false;

		// ****************************************************************
		// * check if at least 1 measure of 27002 standard is applicable ->
		// Special
		// case
		// ****************************************************************

		// parse standards
		for (int i = 0; i < analysis.getAnalysisStandards().size(); i++) {

			// check if 27002 standard -> YES
			if (analysis.getAnalysisStandard(i) instanceof NormalStandard && analysis.getAnalysisStandard(i).getStandard().getLabel().equals(Constant.STANDARD_27002)) {

				// temporary store measure standard
				normalStandard = (NormalStandard) analysis.getAnalysisStandard(i);

				if (normalStandard.getStandard().getLabel().equals(Constant.STANDARD_27002))
					// leave loop
					break;

			}
		}

		// parse measures of standard
		for (int j = 0; j < normalStandard.getMeasures().size(); j++) {

			// the measure reference needs to start with the chapter number and
			// is applicable or
			// mandatory
			if ((normalStandard.getMeasure(j).getMeasureDescription().getReference().startsWith(chapter + "."))
					&& (!normalStandard.getMeasure(j).getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE) && (normalStandard.getMeasure(j).getMeasureDescription()
							.isComputable()))) {

				// *************************************************
				// * measure found increment counter
				// *************************************************
				result = true;

				// leave loop (only 1 measure is enough)
				break;
			}
		}

		// return the result
		return result;
	}

	/***********************************************************************************************
	 * TMAList - END
	 **********************************************************************************************/

	/***********************************************************************************************
	 * Action Plan Summary - BEGIN
	 **********************************************************************************************/

	/**
	 * computeSummary: <br>
	 * Computes the Summary for a Specific Action Plan.
	 * 
	 * @param mode
	 *            Defines which Type of Action Plan (Normal, Optimisitc or
	 *            Pessimistic)
	 * @throws TrickException
	 */
	private void computeSummary(ActionPlanMode mode) throws TrickException {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<SummaryStage> sumStage = new ArrayList<SummaryStage>();
		List<ActionPlanEntry> actionPlan = this.analysis.getActionPlan(mode);
		SummaryValues tmpval = new SummaryValues(this.standards);
		boolean anticipated = true;
		ActionPlanEntry ape = null;
		boolean byPhase = false;
		int phase = 0;

		// check if actionplan is empty -> YES: quit method
		if (actionPlan.isEmpty())
			return;

		// retirve actionplantype
		ActionPlanType apt = actionPlan.get(0).getActionPlanType();

		Map<Integer, Maintenance> maintenances = new LinkedHashMap<Integer, Maintenance>();

		// ****************************************************************
		// * retrieve internal rate and external rate
		// ****************************************************************

		// ****************************************************************
		// * generate first stage
		// ****************************************************************

		// reinitialise variables
		for (String key : tmpval.conformanceHelper.keySet())
			tmpval.conformanceHelper.get(key).conformance = 0;

		// add start value of ALE (for first stage (P0))
		tmpval.totalALE = actionPlan.get(0).getTotalALE() + actionPlan.get(0).getDeltaALE();

		// generate first stage
		generateStage(apt, tmpval, sumStage, "Start(P0)", true, 0, maintenances);

		// ****************************************************************
		// * check if calculation by phase
		// ****************************************************************

		// calculation by phase ? -> YES
		if ((apt.getId() == Constant.ACTIONPLAN_PHASE_NORMAL_MODE) || (apt.getId() == Constant.ACTIONPLAN_PHASE_OPTIMISTIC_MODE)
				|| (apt.getId() == Constant.ACTIONPLAN_PHASE_PESSIMISTIC_MODE)) {

			// set flag
			byPhase = true;

			// retrieve first phase number
			phase = actionPlan.get(0).getMeasure().getPhase().getNumber();
		}

		// ****************************************************************
		// * parse action plan and calculate summary until last stage
		// ****************************************************************

		// parse action plan
		for (int i = 0; i < actionPlan.size(); i++) {

			// store action plan
			ape = actionPlan.get(i);

			// check if calculation by phase -> YES
			if (byPhase) {

				// check if entry is in current phase -> YES
				if (ape.getMeasure().getPhase().getNumber() == phase) {

					// ****************************************************************
					// * calculate values for next run
					// ****************************************************************
					setValuesForNextEntry(tmpval, ape);

				} else {

					// check if entry is in current phase -> NO

					// ****************************************************************
					// * generate stage for previous phase
					// ****************************************************************

					generateStage(apt, tmpval, sumStage, "Phase " + phase, false, phase, maintenances);

					// ****************************************************************
					// * reinitialise variables
					// ****************************************************************

					for (String key : tmpval.conformanceHelper.keySet())
						tmpval.conformanceHelper.get(key).conformance = 0;

					tmpval.deltaALE = 0;
					tmpval.externalWorkload = 0;
					tmpval.internalWorkload = 0;
					tmpval.measureCost = 0;
					tmpval.measureCount = 0;
					tmpval.relativeROSI = 0;
					tmpval.ROSI = 0;
					tmpval.totalALE = 0;
					tmpval.totalCost = 0;

					// ****************************************************************
					// * update phase
					// ****************************************************************
					phase = ape.getMeasure().getPhase().getNumber();

					// ****************************************************************
					// * calculate values for next run
					// ****************************************************************
					setValuesForNextEntry(tmpval, ape);
				}
			} else {

				// check if calculation by phase -> NO

				// check if ROSI >= 0 -> YES
				if (ape.getROI() >= 0) {

					setValuesForNextEntry(tmpval, ape);
				} else {

					// check if ROSI >= 0 -> NO

					// check if anticipated was already added -> NO
					if (anticipated) {

						// ****************************************************************
						// * generate stage for anticipated level
						// ****************************************************************
						generateStage(apt, tmpval, sumStage, "Anticipated", false, phase, maintenances);

						// deactivate flag
						anticipated = false;
					}

					// ****************************************************************
					// * calculate values for next run
					// ****************************************************************
					setValuesForNextEntry(tmpval, ape);
				}
			}
		}

		// ****************************************************************
		// * calculate last phase
		// ****************************************************************

		// reinitialise variables

		for (String key : tmpval.conformanceHelper.keySet())

			tmpval.conformanceHelper.get(key).conformance = 0;

		// check if by phase -> YES
		if (byPhase) {

			// ****************************************************************
			// * generate stage for phase
			// ****************************************************************
			generateStage(apt, tmpval, sumStage, "Phase " + phase, false, phase, maintenances);
		} else {

			// check if by phase -> NO

			// ****************************************************************
			// * generate stage for all measures
			// ****************************************************************
			generateStage(apt, tmpval, sumStage, "All Measures", false, phase, maintenances);
		}

		// ****************************************************************
		// * set stages in correct list
		// ****************************************************************

		this.analysis.addSummaryEntries(sumStage);
	}

	/**
	 * setValuesForNextEntry: <br>
	 * This method is used to Update the Values of a Summary Stage.
	 * 
	 * @param tmpval
	 *            The Object that contains current Summary Stage Values
	 * @param ape
	 *            the ActionPlanEntry object
	 * @param ir
	 *            The Internal Setup Rate
	 * @param er
	 *            The External Setup Rate
	 * @param phasetime
	 *            The Time of the current Phase in Years
	 */
	private void setValuesForNextEntry(SummaryValues tmpval, ActionPlanEntry ape) {

		// ****************************************************************
		// * update phase characterisitc values
		// ****************************************************************

		// increment measure counter
		tmpval.measureCount++;

		SummaryStandardHelper shelper = tmpval.conformanceHelper.get(ape.getMeasure().getAnalysisStandard().getStandard().getLabel());

		shelper.measures.add(ape.getMeasure());

		// increment implemented counter
		tmpval.implementedCount++;

		// ****************************************************************
		// * update profitability values
		// ****************************************************************

		// set total ALE value
		tmpval.totalALE = ape.getTotalALE();

		// update delta ALE value
		tmpval.deltaALE += ape.getDeltaALE();

		// update cost of measure
		tmpval.measureCost += ape.getCost();

		// update ROSI
		tmpval.ROSI += ape.getROI();

		// calculate relative ROSI
		if (tmpval.measureCost == 0) {
			tmpval.relativeROSI = 0;
		} else {
			tmpval.relativeROSI = tmpval.ROSI / tmpval.measureCost;
		}

		// ****************************************************************
		// * update resource planning values
		// ****************************************************************

		// update internal workload
		tmpval.internalWorkload += ape.getMeasure().getInternalWL();

		// update external workload
		tmpval.externalWorkload += ape.getMeasure().getExternalWL();

		// update investment
		tmpval.investment += ape.getMeasure().getInvestment();

		// update internal maintenance

		// Depricated
		// double internalWL = ape.getMeasure().getInternalWL() *
		// ape.getMeasure().getMaintenance() / 100.;

		// in case of a phase calculation multiply internal maintenance with
		// phasetime
		tmpval.internalMaintenance += ape.getMeasure().getInternalMaintenance();

		// update external maintenance

		// Depricated
		// double externalWL = ape.getMeasure().getExternalWL() *
		// ape.getMeasure().getMaintenance() / 100.;

		// in case of a phase calculation multiply external maintenance with
		// phasetime
		tmpval.externalMaintenance += ape.getMeasure().getExternalMaintenance();

		// update recurrent investment
		tmpval.recurrentInvestment += ape.getMeasure().getRecurrentInvestment();

		// update recurrent cost
		// tmpval.recurrentCost += ape.getMeasure().getInvestment() *
		// ape.getMeasure().getMaintenance() / 100.;

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
			String[] chapters = chapter.split("[.]");
			return chapters[0] + "." + chapters[1];
		} else {
			return (chapter.contains(".") ? chapter.split("[.]")[0] : chapter);
		}

	}

	/**
	 * generateStage: <br>
	 * This Method Creates a Complete Summary Stage and Adds it to the List of
	 * Stages.
	 * 
	 * @param tmpval
	 *            The List of Calculation Variables
	 * @param sumStage
	 *            The List of Stages
	 * @param name
	 *            The Name to give for the Stage
	 * @param firstStage
	 *            Flag to tell if the Stage is the First Stage
	 * @param maintenances
	 * @throws TrickException
	 */
	private void generateStage(ActionPlanType type, SummaryValues tmpval, List<SummaryStage> sumStage, String name, boolean firstStage, int phasenumber,
			Map<Integer, Maintenance> maintenances) throws TrickException {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		SummaryStage aStage = null;
		Measure measure = null;

		double phasetime = 0;

		if (phasenumber > 0) {
			for (Phase phase : this.analysis.getPhases()) {
				if (phase.getNumber() == phasenumber)
					phasetime = Analysis.getYearsDifferenceBetweenTwoDates(phase.getBeginDate(), phase.getEndDate());
			}
		}
		// check if first stage -> YES
		if (firstStage)
			tmpval.implementedCount = 0;

		if (tmpval.previousStage != null)
			tmpval.measureCount = tmpval.previousStage.getMeasureCount();
		else
			tmpval.measureCount = 0;

		for (String key : tmpval.conformanceHelper.keySet()) {

			SummaryStandardHelper helper = tmpval.conformanceHelper.get(key);

			helper.conformance = 0;

			Map<String, Object[]> chapters = new HashMap<String, Object[]>();

			for (int i = 0; i < helper.standard.getMeasures().size(); i++) {

				measure = helper.standard.getMeasures().get(i);

				Hibernate.initialize(measure);

				measure = DAOHibernate.Initialise(measure);

				double imprate = 0;

				if (measure instanceof NormalMeasure)
					imprate = ((NormalMeasure) measure).getImplementationRateValue();

				else if (measure instanceof MaturityMeasure)
					imprate = ((MaturityMeasure) measure).getImplementationRateValue();

				if (measure.getMeasureDescription().isComputable()) {

					String chapterName = extractMainChapter(measure.getMeasureDescription().getReference());

					Object[] chapter = chapters.containsKey(chapterName) ? chapters.get(chapterName) : new Object[] { 0.0, new Integer(0), new Integer(0) };

					Double numerator = (Double) chapter[0];

					Integer denominator = (Integer) chapter[1];

					Integer implementation = (Integer) chapter[2];

					denominator++;

					if (firstStage) {

						if (imprate == Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE)
							implementation++;

						if (!measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE))
							numerator += (imprate / 100.);
						else
							numerator += (Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE / 100.);
					} else {

						if (!measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE))
							numerator += (imprate / 100.);
						else
							numerator += (Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE / 100.);

						for (int k = 0; k < helper.measures.size(); k++) {
							if (measure.equals(helper.measures.get(k))) {

								numerator += (1.) - (imprate / 100.);
								tmpval.measureCount++;
								break;
							}
						}
					}

					chapters.put(chapterName, new Object[] { numerator, denominator, implementation });
				}
			}

			for (String chapterkey : chapters.keySet()) {

				Object[] chapter = chapters.get(chapterkey);

				Double numerator = (Double) chapter[0];

				Integer denominator = (Integer) chapter[1];

				if (denominator == 0)
					throw new TrickException("error.summary.no.implemented.measure", "No implemented measure");

				helper.conformance += (numerator / (double) denominator);

				Integer implementation = (Integer) chapter[2];

				tmpval.implementedCount += implementation;
			}

			if (chapters.size() > 0) {

				helper.conformance /= (double) chapters.size();
			} else
				helper.conformance = 0;

			chapters.clear();

		}

		Maintenance maintenance = maintenances.containsKey(phasenumber - 1) ? maintenances.get(phasenumber - 1) : new Maintenance();

		if (maintenances.containsKey(phasenumber))
			maintenances.get(phasenumber).update(tmpval.internalMaintenance, tmpval.externalMaintenance);
		else
			maintenances.put(phasenumber, new Maintenance(tmpval.internalMaintenance, tmpval.externalMaintenance));
		
		// ****************************************************************
		// * create summary stage object
		// ****************************************************************
		aStage = new SummaryStage();

		// add values to summary stage object
		aStage.setStage(name);
		aStage.setActionPlanType(type);

		for (String key : tmpval.conformanceHelper.keySet())
			aStage.addConformance(tmpval.conformanceHelper.get(key).standard, tmpval.conformanceHelper.get(key).conformance);

		if (tmpval.previousStage != null)
			aStage.setMeasureCount(tmpval.implementedCount - tmpval.previousStage.getImplementedMeasuresCount());
		else
			aStage.setMeasureCount(tmpval.measureCount);
		aStage.setImplementedMeasuresCount(tmpval.implementedCount);
		aStage.setTotalALE(tmpval.totalALE);
		aStage.setDeltaALE(tmpval.deltaALE);
		aStage.setCostOfMeasures(tmpval.measureCost);
		aStage.setROSI(tmpval.ROSI);
		aStage.setRelativeROSI(tmpval.relativeROSI);
		aStage.setInternalWorkload(tmpval.internalWorkload);
		aStage.setExternalWorkload(tmpval.externalWorkload);
		aStage.setInvestment(tmpval.investment);
		aStage.setInternalMaintenance(maintenance.getInternalMaintenance() * phasetime);
		aStage.setExternalMaintenance(maintenance.getExternalMaintenance() * phasetime);
		aStage.setRecurrentInvestment(tmpval.recurrentInvestment * phasetime);

		double er = this.analysis.getParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE);

		double ir = this.analysis.getParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE);

		tmpval.recurrentCost = (aStage.getInternalMaintenance() * ir) + (aStage.getExternalMaintenance() * er) + aStage.getRecurrentInvestment();

		aStage.setRecurrentCost(tmpval.recurrentCost);

		// update total cost
		tmpval.totalCost += (tmpval.internalWorkload * ir) + (tmpval.externalWorkload * er) + tmpval.investment;

		// in case of a phase calculation multiply external maintenance,
		// internal maintenance with
		// phasetime and with internal and external setup as well as investment
		// with phasetime
		if (phasetime > 0)
			tmpval.totalCost += (maintenance.getInternalMaintenance() * phasetime * ir) + (maintenance.getExternalMaintenance() * phasetime * er)
					+ (tmpval.recurrentInvestment * phasetime);
		else
			tmpval.totalCost += (maintenance.getInternalMaintenance() * ir) + (maintenance.getExternalMaintenance() * er) + tmpval.recurrentInvestment;

		aStage.setTotalCostofStage(tmpval.totalCost);

		// ****************************************************************
		// * add summary stage to list of summary stages
		// ****************************************************************
		sumStage.add(aStage);

		tmpval.previousStage = aStage;
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
	 * @param standards
	 *            The Value to set the standards field
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
	 * @param uncertainty
	 *            The Value to set the uncertainty field
	 */
	public void setUncertainty(boolean uncertainty) {
		this.uncertainty = uncertainty;
	}

	/***********************************************************************************************
	 * Action Plan Summary - END
	 **********************************************************************************************/
}
