package lu.itrust.business.TS.actionplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.InvalidAttributesException;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.MaturityMeasure;
import lu.itrust.business.TS.MaturityNorm;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.MeasureNorm;
import lu.itrust.business.TS.NormMeasure;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.dao.DAOActionPlanType;
import lu.itrust.business.dao.DAOAnalysis;
import lu.itrust.business.service.ServiceTaskFeedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ActionPlanComputation: <br>
 * This class is used to calculate the action plan for an Analysis. This class
 * is also used to generate the TMAList (Threat - Measure - Asset Triples). This
 * class will initialize the Lists of ActionPlan Entries inside the Analysis
 * class (The final Action Plans) as well as the Summary for each Action Plans.
 * After the Action Plans are calculated, this class will save the results to
 * the MySQL Database.
 * 
 * @author itrust consulting s.���.rl. : SME
 * @version 0.1
 * @since 9 janv. 2013
 */
@Component
public class ActionPlanComputation {

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	@Autowired
	private DAOActionPlanType serviceActionPlanType;
	
	@Autowired
	private DAOAnalysis sericeAnalysis;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	private Long idTask;

	/** Analysis Object */
	private Analysis analysis = null;

	/***********************************************************************************************
	 * Constructor
	 **********************************************************************************************/

	/**
	 * Constructor: This creates an object and takes as parameter an loaded
	 * Analysis and an MySQL Database Handler.
	 * 
	 * @param analysis
	 *            The Analysis Object
	 * @param mysql
	 *            The MySQL Database Handler
	 */
	public ActionPlanComputation(Analysis analysis) {
		this.analysis = analysis;
	}

	/**
	 * 
	 */
	public ActionPlanComputation() {
	}
	
	/**
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
	 * @param serviceActionPlanType
	 * @param sericeAnalysis
	 * @param serviceTaskFeedback
	 * @param idTask
	 * @param analysis
	 */
	public ActionPlanComputation(DAOActionPlanType serviceActionPlanType, DAOAnalysis sericeAnalysis, ServiceTaskFeedback serviceTaskFeedback, Long idTask,
			Analysis analysis) {
		this.serviceActionPlanType = serviceActionPlanType;
		this.sericeAnalysis = sericeAnalysis;
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.idTask = idTask;
		this.analysis = analysis;
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

		serviceTaskFeedback.send(idTask, new MessageHandler("info.action_plan.computing", "Computing Action Plans", 10));

		System.out.println("Computing Action Plans...");

		// ****************************************************************
		// * initialise phases and order phases ascending
		// ****************************************************************
		//this.analysis.initialisePhases();

		//serviceTaskFeedback.send(idTask, new MessageHandler("success.phase.initialise", null, "Phases ware initialised successfully"));

		// ****************************************************************
		// * Begin transaction
		// ****************************************************************
		// this.mysql.beginTransaction();

		try {

			// ****************************************************************
			// * Compute action plans
			// ****************************************************************
			/*
			 * //
			 * ****************************************************************
			 * // * compute Action Plan - normal mode
			 * ****************************************************************
			 * System.out.println("compute Action Plan - normal mode");
			 */
			// computeActionPlan(ActionPlanMode.NORMAL);
			/*
			 * //
			 * ****************************************************************
			 * // * compute Action Plan - optimistic mode //
			 * ****************************************************************
			 * System.out.println("compute Action Plan - optimistic mode");
			 */
			// computeActionPlan(ActionPlanMode.OPTIMISTIC);
			/*
			 * //
			 * ****************************************************************
			 * // * compute Action Plan - pessimistic mode //
			 * ****************************************************************
			 * System.out.println("compute Action Plan - pessimistic mode");
			 */
			// computeActionPlan(ActionPlanMode.PESSIMISTIC);
			/*
			 * / //
			 * ****************************************************************
			 * // * compute Action Plan - normal mode - Phase //
			 * ****************************************************************
			 */System.out.println("compute Action Plan - normal mode - Phase");

			serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.phase.normal_mode", "Compute Action Plan - normal mode - Phase", 20));

			computePhaseActionPlan(ActionPlanMode.PHASE_NORMAL);

			// ****************************************************************
			// * compute Action Plan - optimistic mode - Phase
			// ****************************************************************
			serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.phase.optimistic_mode", "Compute Action Plan - optimistic mode - Phase", 30));

			System.out.println("compute Action Plan - optimistic mode - Phase");
			computePhaseActionPlan(ActionPlanMode.PHASE_OPTIMISTIC);

			// ****************************************************************
			// * compute Action Plan - pessimistic mode - Phase
			// ****************************************************************

			serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.phase.optimistic_mode", "Compute Action Plan -  pessimistic mode - Phase", 40));

			System.out.println("compute Action Plan - pessimistic mode - Phase");

			computePhaseActionPlan(ActionPlanMode.PHASE_PESSIMISTIC);

			// ****************************************************************
			// * set positions relative to normal action plan for all action 
			// * plans
			// ****************************************************************
			 determinePositions();

			// ****************************************************************
			// * Compute summary of action plans
			// ****************************************************************
			/*
			 * //
			 * ****************************************************************
			 * // * create summary for normal action plan summary //
			 * ****************************************************************
			 */// computeSummary(ActionPlanMode.NORMAL);
			/*
			 * //
			 * ****************************************************************
			 * // * create summary for optimistic action plan summary //
			 * ****************************************************************
			 */// computeSummary(ActionPlanMode.OPTIMISTIC);
			/*
			 * //
			 * ****************************************************************
			 * // * create summary for pessimistic action plan summary //
			 * ****************************************************************
			 */// computeSummary(ActionPlanMode.PESSIMISTIC);
			/*o
			 * / //
			 * ****************************************************************
			 * // * create summary for normal phase action plan summary //
			 * ****************************************************************
			 */
			serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.create_summary.normal_phase", "Create summary for normal phase action plan summary", 50));
			computeSummary(ActionPlanMode.PHASE_NORMAL);

			// ****************************************************************
			// * create summary for optimistic phase action plan summary
			// ****************************************************************
			serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.create_summary.optimistic_phase", "Create summary for optimistic phase action plan summary", 60));
			computeSummary(ActionPlanMode.PHASE_OPTIMISTIC);

			// ****************************************************************
			// * create summary for pessimistic phase action plan summary
			// ****************************************************************
			serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.create_summary.pessimistic_phase", "Create summary for pessimistic phase action plan summary", 70));
			//computeSummary(ActionPlanMode.PHASE_PESSIMISTIC);

			// ****************************************************************
			// * Store action plans into database
			// ****************************************************************

			for (int i = 0; i < this.analysis.getActionPlan(ActionPlanMode.PHASE_NORMAL).size(); i++) {

				ActionPlanEntry ape = this.analysis.getActionPlan(ActionPlanMode.PHASE_NORMAL).get(i);

				System.out.println(ape.getPosition() + "|" + ape.getMeasure().getAnalysisNorm().getNorm().getLabel() + "|"
						+ ape.getMeasure().getMeasureDescription().getReference() + "|" + ape.getTotalALE() + "|" + ape.getROI() + "|" + ape.getCost());
			}

			serviceTaskFeedback.send(idTask, new MessageHandler("info.info.action_plan.saved", "Saving Action Plans", 90));
			
			sericeAnalysis.saveOrUpdate(analysis);

			System.out.println("Saving Action Plans...");

			return null;

		
		} catch (Exception e) {
			System.out.println("Action Plan saving failed! ");
			MessageHandler messageHandler = new MessageHandler(e.getMessage(), "Action Plan saving failed", e);
			serviceTaskFeedback.send(idTask, messageHandler);
			e.printStackTrace();
			return messageHandler;
		}
	}

	

	

	/**
	 * determinePositions: <br>
	 * Calculates the Position of each Action Plan Entry refered to the Normal
	 * Action Plan Calculation
	 */
	private void determinePositions() {

		// ****************************************************************
		// initialise variable
		// ****************************************************************
		String position = "";
		List<ActionPlanEntry> actionPlan = this.analysis.getActionPlan(ActionPlanMode.NORMAL);
		List<ActionPlanEntry> actionPlanO = this.analysis.getActionPlan(ActionPlanMode.OPTIMISTIC);
		List<ActionPlanEntry> actionPlanP = this.analysis.getActionPlan(ActionPlanMode.PESSIMISTIC);
		List<ActionPlanEntry> phaseActionPlan = this.analysis.getActionPlan(ActionPlanMode.PHASE_NORMAL);
		List<ActionPlanEntry> phaseActionPlanO = this.analysis.getActionPlan(ActionPlanMode.PHASE_OPTIMISTIC);
		List<ActionPlanEntry> phaseActionPlanP = this.analysis.getActionPlan(ActionPlanMode.PHASE_PESSIMISTIC);

		// ****************************************************************
		// * APN - Action Plan Normal
		// ****************************************************************

		// parse all entries of the action plan
		for (int i = 0; i < actionPlan.size(); i++) {

			// set correct position
			actionPlan.get(i).setPosition(String.valueOf(i + 1));
		}

		// ****************************************************************
		// * APPN - Action Plan Phase Normal
		// ****************************************************************

		// parse all entries of the action plan
		for (int i = 0; i < phaseActionPlan.size(); i++) {

			// set correct position
			phaseActionPlan.get(i).setPosition(String.valueOf(i + 1));
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
					actionPlanO.get(i).setPosition(position);
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

							// check if the value is less than 0 -> NO
							// make an even sign
							position = "=";
						}
					}

					// add the position ot the action plan
					actionPlanP.get(i).setPosition(position);
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

							// check if the value is less than 0 -> NO
							// make an even sign
							position = "=";
						}
					}

					// add the position ot the action plan
					phaseActionPlanO.get(i).setPosition(position);
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

							// check if the value is less than 0 -> NO
							// make an even sign
							position = "=";
						}
					}

					// add the position ot the action plan
					phaseActionPlanP.get(i).setPosition(position);
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
		NormMeasure normMeasure = null;
		List<TMA> TMAList = null;
		List<Measure> usedMeasures = new ArrayList<Measure>();
		List<ActionPlanEntry> actionPlan = this.analysis.getActionPlans();
		ActionPlanType actionPlanType = serviceActionPlanType.get(mode.getValue());

		// check if actionplantype exists, when not add to database
		if (actionPlanType == null) {
			actionPlanType = new ActionPlanType(mode);
			serviceActionPlanType.save(actionPlanType);
		}

		// ****************************************************************
		// * generate TMA list for normal computation
		// ****************************************************************
		TMAList = generateTMAList(this.analysis, usedMeasures, mode, 0, false);

		// ****************************************************************
		// * parse all measures (to create complete action plan) until no
		// * more measures are in the list of measures
		// ****************************************************************
		while (usedMeasures.size() > 0) {

			// ****************************************************************
			// * calculate temporary Action Plan
			// ****************************************************************
			List<ActionPlanEntry> tmpActionPlan = generateTemporaryActionPlan(usedMeasures, actionPlanType, TMAList);

			// ****************************************************************
			// * take biggest ROSI or ROSMI from temporary action plan and add
			// it
			// * - to final action plan
			// * - remove measure from usefulmeasures list
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
				// biggest
				// * rosi
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
				// biggest
				// * ROSI
				// ****************************************************************

				// ****************************************************************
				// * update ALE values for next action plan run
				// ****************************************************************

				// initialise variables
				maturityMeasure = null;
				normMeasure = null;

				// check if it is a maturity measure -> YES
				if (actionPlanEntry.getMeasure().getAnalysisNorm().getNorm().getLabel().equals(Constant.NORM_MATURITY)) {

					// retrieve matrurity masure
					maturityMeasure = (MaturityMeasure) actionPlanEntry.getMeasure();

					// ****************************************************************
					// * update values for next run
					// ****************************************************************
					adaptValuesForMaturityMeasure(TMAList, actionPlanEntry, maturityMeasure);
				} else {

					// check if it is a maturity measure -> NO

					// retrieve measure
					normMeasure = (NormMeasure) actionPlanEntry.getMeasure();

					// ****************************************************************
					// * update values for next run
					// ****************************************************************
					adaptValuesForNormMeasure(TMAList, actionPlanEntry, normMeasure);
				}

				// ****************************************************************
				// * add measure to final action plan
				// ****************************************************************
				actionPlan.add(actionPlanEntry);

				// ****************************************************************
				// * remove measure from useful measures
				// ****************************************************************
				if (normMeasure != null) {

					// remove norm measure
					usedMeasures.remove(normMeasure);
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

	public static void clone(List<TMA> desc, List<TMA> scr) {
		for (int i = 0; i < scr.size(); i++)
			desc.add(scr.get(i));

	}

	/**
	 * computePhaseActionPlan: <br>
	 * Computes the Action Plan Phase By Phase.
	 * 
	 * @param mode
	 *            The Mode to Compute: Normal, Optimistic or Pessimistic
	 * @param phaseActionPlan
	 *            The Action Plan to Store the Final Values
	 * @throws Exception
	 * */
	private void computePhaseActionPlan(ActionPlanMode mode) throws Exception {

		// ****************************************************************
		// * variables initialisation
		// ****************************************************************
		ActionPlanEntry actionPlanEntry = null;
		MaturityMeasure maturityMeasure = null;
		NormMeasure normMeasure = null;
		List<TMA> TMAList = new ArrayList<TMA>();
		List<Measure> usedMeasures = new ArrayList<Measure>();
		List<ActionPlanEntry> phaseActionPlan = this.analysis.getActionPlans();
		ActionPlanType actionPlanType = serviceActionPlanType.get(mode.getValue());

		if (actionPlanType == null) {
			actionPlanType = new ActionPlanType(mode);
			serviceActionPlanType.save(actionPlanType);
		}

		// ****************************************************************
		// * parse all phases where measures are in
		// ****************************************************************

		// parse all phases
		for (int phase = 0; phase < this.analysis.getUsedPhases().size(); phase++) {

			// ****************************************************************
			// * generate TMA list for phase computation
			// ****************************************************************
			if (this.analysis.getUsedPhases().get(phase).getNumber() == 0)
				continue;

			// ****************************************************************
			// * check if TMAList is empty -> NO
			// * after first time TMAList is not empty, so ALE values need to
			// * be reused
			// ****************************************************************
			if (TMAList.size() > 0) {

				// ****************************************************************
				// * TMAList was not empty, so take ALE values from current to
				// * continue calculations on previous values
				// ****************************************************************

				// ****************************************************************
				// * take a copy of the TMAList values
				// ****************************************************************

				// clone TMAList for ALE values
				@SuppressWarnings("unchecked")
				List<TMA> tmpTMAList = (List<TMA>) ((ArrayList<TMA>) TMAList).clone();
				
				//System.out.println("generate TMA for phase: "+ phase);

				// ****************************************************************
				// * generate the TMAList
				// ****************************************************************
				TMAList = generateTMAList(this.analysis, usedMeasures, mode, this.analysis.getAPhase(phase).getNumber(), false);

				//System.out.println("generated TMA for phase: "+ phase);
				
				// ****************************************************************
				// * update the created TMAList with previous values (ALE
				// values)
				// ****************************************************************

				// parse TMAList to edit the ALE values by assessment
				for (int i = 0; i < TMAList.size(); i++) {

					// ****************************************************************
					// * for each TMAList entry, parse temporary TMAList to find
					// * assessments that are the same to change the ALE values
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
							// * if 27002 norm, recalculate deltaALE maturity ->
							// YES
							// ****************************************************************
							if (TMAList.get(i).getNorm().getLabel().equals(Constant.NORM_27002)) {

								// ****************************************************************
								// * recalculate delta ALE Maturity
								// ****************************************************************
								TMAList.get(i).calculateDeltaALEMaturity();
							}
						}
					}
				}
			} else {

				// ****************************************************************
				// * check if TMAList is empty -> YES
				// * for the first time, the TMAList is empty, so do nothing
				// ****************************************************************
				TMAList = generateTMAList(this.analysis, usedMeasures, mode, this.analysis.getAPhase(phase).getNumber(), false);
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
				// and add it
				// * - to final action plan
				// * - remove measure from usefulmeasures list
				// * - adapt values for the next run
				// ****************************************************************

				// check if first element is not null
				if (tmpactionPlan.get(0) != null) {

					// ****************************************************************
					// * start with the first element to check if it is the
					// biggest
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

					// ****************************************************************
					// * update TMAList ALE values for next run
					// ****************************************************************

					// initialise variables
					maturityMeasure = null;
					normMeasure = null;

					// check if the biggest rosi/rosmi entry is a maturity
					// measure -> YES

					if (actionPlanEntry.getMeasure().getAnalysisNorm().getNorm().getLabel().equals(Constant.NORM_MATURITY)) {

						// create temporary maturity measure object
						maturityMeasure = (MaturityMeasure) actionPlanEntry.getMeasure();

						// ****************************************************************
						// * change values for the next run
						// ****************************************************************
						adaptValuesForMaturityMeasure(TMAList, actionPlanEntry, maturityMeasure);
					} else {

						// check if the biggest rosi/rosmi entry is a maturity
						// measure -> NO

						// create temporary norm measure object
						normMeasure = (NormMeasure) actionPlanEntry.getMeasure();

						// ****************************************************************
						// * change values for the next run
						// ****************************************************************
						adaptValuesForNormMeasure(TMAList, actionPlanEntry, normMeasure);
					}

					// ****************************************************************
					// * add measure to the final action plan
					// ****************************************************************
					phaseActionPlan.add(actionPlanEntry);

					// ****************************************************************
					// * remove measure from useful measures (either it is
					// maturity or
					// * norm)
					// ****************************************************************
					if (normMeasure != null) {

						// remove norm measure
						usedMeasures.remove(normMeasure);
					} else {
						if (maturityMeasure != null) {

							// remove maturity measure
							usedMeasures.remove(maturityMeasure);
						}
					}
				}
			}
		}

		// ****************************************************************
		// * clear TMAList after all action plan computation
		// ****************************************************************
		TMAList.clear();
	}

	/***********************************************************************************************
	 * Temporary Action Plan - BEGIN
	 **********************************************************************************************/

	/**
	 * generateTemporaryActionPlan: <br>
	 * Generates the Temporary Action Plan based on the "TMAList" values and the
	 * usedMeasures List. Where usedMeasures ise the List fo Measures to add to
	 * the Action Plan.
	 * 
	 * @return The Temporary Action Plan Entries
	 * 
	 * @throws InvalidAttributesException
	 */
	private List<ActionPlanEntry> generateTemporaryActionPlan(List<Measure> usedMeasures, ActionPlanType actionPlanType, List<TMA> TMAList) throws InvalidAttributesException {

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
	 * Action Plan with Calculations only for the AnalysisNorm Measures,
	 * Maturity Measures are added but no Calculation is done for Maturity.
	 * Calculations for Maturity Entries are done in the Method
	 * "generateMaturtiyChapterActionPlanEntries".
	 * 
	 * @param tmpActionPlan
	 *            The Temporary Action Plan with all usable Entries
	 * @throws InvalidAttributesException
	 */
	private void generateNormalActionPlanEntries(List<ActionPlanEntry> tmpActionPlan, ActionPlanType actionPlanType, List<Measure> usedMeasures, List<TMA> TMAList)
			throws InvalidAttributesException {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		double deltaALE = 0;
		// Norm norm = null;
		double totalALE = 0;
		NormMeasure normMeasure = null;
		ActionPlanEntry actionPlanEntry = null;
		MaturityMeasure maturityMeasure = null;
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
			// * temporary action plan
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
				// norm = null;
				totalALE = 0;

				// temporary store the norm measure
				normMeasure = (NormMeasure) usedMeasures.get(i);

				// parse TMAList
				for (int j = 0; j < TMAList.size(); j++) {

					// ****************************************************************
					// * check if the measure is the current measure -> YES
					// ****************************************************************
					if (TMAList.get(j).getMeasure().equals(normMeasure)) {

						// store the norm
						// norm = TMAList.get(j).getNorm().getNorm();

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
							// rewrite previous
							// * value (of tmpAssets)
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
								tmpAssets.get(ac).setCurrentALE(tmpAssets.get(ac).getCurrentALE() + ALE);
							}
						}

						// ****************************************************************
						// * take deltaALE to calculate the sum of deltaALE
						// ****************************************************************
						deltaALE = deltaALE + TMAList.get(j).getDeltaALE();
					}
				}

				// ****************************************************************
				// * generate action plan entry object
				// * + calculate ROI
				// * + update totalALE given with delta ALE (for next
				// calculation)
				// ****************************************************************
				actionPlanEntry = new ActionPlanEntry(normMeasure, actionPlanType, tmpAssets, totalALE, deltaALE);

				// ****************************************************************
				// * add ActionPlanEntry to list of temporary action plan
				// ****************************************************************
				tmpActionPlan.add(actionPlanEntry);
			} else {

				// ****************************************************************
				// * check if the measure is not a maturity measure -> YES
				// ****************************************************************

				// maturity Measure

				// initialise variables
				// maturityNorm = null;

				// store current measure as maturtiy measure
				maturityMeasure = (MaturityMeasure) usedMeasures.get(i);

				// ****************************************************************
				// * get maturtiy norm object
				// ****************************************************************

				// parse norms
				// for (int nc = 0; nc < this.analysis.getNorms().size(); nc++)
				// {

				// check if maturity norm
				// if
				// (this.analysis.getANorm(nc).getNorm().equals(Constant.NORM_MATURITY))
				// {

				// ****************************************************************
				// * store maturity norm object and exit loop
				// ****************************************************************
				// maturityNorm = (MaturityNorm) this.analysis.getANorm(nc);

				// leave loop
				// break;
				// }
				// }

				// ****************************************************************
				// * generate object with delta ALE to 0
				// ****************************************************************
				actionPlanEntry = new ActionPlanEntry(maturityMeasure, 0);

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
	 * @throws InvalidAttributesException
	 */
	private void generateMaturtiyChapterActionPlanEntries(List<ActionPlanEntry> tmpActionPlan, List<Measure> usedMeasures, List<TMA> TMAList) throws InvalidAttributesException {

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
		NormMeasure tmpMeasure = null;
		boolean found = false;
		List<ActionPlanAsset> tmpAssets = null;
		double ALE = 0;
		List<NormMeasure> normMeasureList = null;
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
				normMeasureList = new ArrayList<NormMeasure>();

				// ****************************************************************
				// * parse TMAList to calculate totalALE and deltaALE and
				// deltaALE
				// * Maturity for the action plan entry
				// ****************************************************************

				// parse TMAList entries
				for (int napmc = 0; napmc < TMAList.size(); napmc++) {

					// temporary store measure
					tmpMeasure = (NormMeasure) TMAList.get(napmc).getMeasure();

					// ****************************************************************
					// * parse TMAList for AnalysisNorm 27002 measures and
					// inside this
					// chapter
					// ****************************************************************
					if ((TMAList.get(napmc).getNorm().getLabel().equals(Constant.NORM_27002)) && (tmpMeasure.getMeasureDescription().getReference().startsWith(maturityChapter))) {

						// ****************************************************************
						// * add measure to a list if it does not yet exist,
						// else do not
						// * add the measure (measure can only be there once)
						// ****************************************************************

						// initialise flag
						found = false;

						// parse list of measures
						for (int i = 0; i < normMeasureList.size(); i++) {

							// check if the measure already exists -> YES
							if (normMeasureList.get(i).equals(tmpMeasure)) {

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
							normMeasureList.add(tmpMeasure);
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
							// rewrite previous
							// * value (of tmpAssets)
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
				numberMeasures = (double) normMeasureList.size();

				// store totalALE in the ActionPlan entry for this maturity
				// measure divide to the
				// number of measures to have the correct value
				actionPlanEntry.setTotalALE(totalChapter / numberMeasures);

				// ****************************************************************
				// * parse assets to divide ALE with number of measures then
				// * calculate minus deltaALEMat
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
	 */
	private List<ActionPlanAsset> createSelectedAssetsList() throws InvalidAttributesException {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		Asset tmpAsset = null;
		// List<ActionPlanAssessment> tmpAssets = new
		// ArrayList<ActionPlanAssessment>();

		List<ActionPlanAsset> tmpAssets = new ArrayList<ActionPlanAsset>();

		// List<Assessment> actionplanassessments = new ArrayList<Assessment>();

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

		// parse assets
		/*
		 * for (int asc = 0; asc < this.analysis.getAssessments().size(); asc++)
		 * {
		 * 
		 * // selected asset -> YES if
		 * (this.analysis.getAnAssessment(asc).isSelected()) {
		 * 
		 * // actionplanassessments.add(this.analysis.getAnAssessment(asc));
		 * tmpAssets.add(new ActionPlanAssessment(null, this.analysis
		 * .getAnAssessment(asc), 0)); } }
		 */

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
	 * adaptValuesForNormMeasure: <br>
	 * Adapt ALE for the Next Run of the Action Plan Calculation when a
	 * NormMeasure was taken.
	 * 
	 * @param actionPlanEntry
	 *            The Action Plan Entry(used to store ALE values of the Assets)
	 * @param normMeasure
	 *            The taken AnalysisNorm Measure
	 */
	private void adaptValuesForNormMeasure(List<TMA> TMAList, ActionPlanEntry actionPlanEntry, NormMeasure normMeasure) {

		// ****************************************************************
		// * variable initialisation
		// ****************************************************************
		double deltaALE = 0;
		TMA tmpTMA = null;
		NormMeasure tmpNormMEasure = null;

		// ****************************************************************
		// * parse TMAList to update ALE values
		// ****************************************************************
		for (int i = 0; i < TMAList.size(); i++) {

			// temporary store TMA entry
			tmpTMA = TMAList.get(i);

			// store the measure
			tmpNormMEasure = (NormMeasure) (tmpTMA.getMeasure());

			// check if the TMA entry has the given measure -> YES
			if (tmpNormMEasure.equals(normMeasure)) {

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
						if (TMAList.get(j).getNorm().getLabel().equals(Constant.NORM_27002)) {

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
	 * @param actionPlanEntry
	 *            The Action Plan Entry
	 * @param maturityMeasure
	 *            The Maturity Measure
	 */
	private void adaptValuesForMaturityMeasure(List<TMA> TMAList, ActionPlanEntry actionPlanEntry, MaturityMeasure maturityMeasure) {

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
					if ((tmpTMA.getMeasure().getMeasureDescription().getReference().startsWith(chapter)) && (tmpTMA.getNorm().getLabel().equals(Constant.NORM_27002))
							&& (tmpTMA.getAssessment().getId() == assessment.getId())) {

						// ****************************************************************
						// * store the deltaALEMaturity
						// ****************************************************************
						deltaALE += tmpTMA.getDeltaALEMat();

					}
				}

				// System.out.println("assessment=" +
				// assessment.getAsset().getName() + "," +
				// assessment.getScenario().getName() + "---->deltaALE=" +
				// deltaALE);

				// ****************************************************************
				// * update ALE value for each assessment
				// ****************************************************************

				// parse TMAList elements
				for (int j = 0; j < TMAList.size(); j++) {

					// find all assessments that are the same as for the current
					if ((TMAList.get(j).getAssessment().getId() == assessment.getId())) {

						// System.out.println(TMAList.get(j).assessment.getAsset().getId()
						// +
						// " " +
						// TMAList.get(j).assessment.getScenario().getId()
						// + " ALE=" +
						// TMAList.get(j).ALE + ", deltaALE=" +deltaALE );

						// ****************************************************************
						// * edit the ALE value
						// ****************************************************************
						TMAList.get(j).setALE(TMAList.get(j).getALE() - deltaALE);

						// System.out.println(TMAList.get(j).assessment.getAsset().getId()
						// +
						// " " +
						// TMAList.get(j).assessment.getScenario().getId()
						// + " ALE=" +
						// TMAList.get(j).ALE);

						// ****************************************************************
						// * recompute the deltaALE
						// ****************************************************************
						TMAList.get(j).calculateDeltaALE();

						// if it is a 27002 norm ->YES
						if (TMAList.get(j).getNorm().getLabel().equals(Constant.NORM_27002)) {

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
	 * Triple the deltaALE and if it is a Measure of the AnalysisNorm 27002 the
	 * deltaALE Maturity. <br>
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
	 */
	public static List<TMA> generateTMAList(Analysis analysis, List<Measure> usedMeasures, ActionPlanMode mode, int phase, boolean isCssf) {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		MeasureNorm measureNorm = null;
		NormMeasure normMeasure = null;
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
		// * parse all MeasureNorm measures to generate TMA entries
		// ****************************************************************

		// parse all norms
		for (int nC = 0; nC < analysis.getAnalysisNorms().size(); nC++) {

			// initialise norm
			measureNorm = null;

			// ****************************************************************
			// * check if not Maturity norm -> NO
			// ****************************************************************
			if (analysis.getAnalysisNorm(nC) instanceof MeasureNorm) {

				// store norm as it's real type
				measureNorm = (MeasureNorm) analysis.getAnalysisNorm(nC);

				// ****************************************************************
				// * parse all measures of the current norm
				// ****************************************************************
				for (int mC = 0; mC < measureNorm.getMeasures().size(); mC++) {

					// temporary store the measure
					normMeasure = measureNorm.getMeasure(mC);

					// ****************************************************************
					// * check conditions to add TMAListEntries to TMAList
					// ****************************************************************

					// ****************************************************************
					// * check if measure is applicable, mandatory and
					// implementation
					// * rate is not 100% -> YES
					// ****************************************************************
					if (!(normMeasure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE))
							&& (normMeasure.getImplementationRate() < Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE)
							&& (normMeasure.getMeasureDescription().getLevel() == Constant.MEASURE_LEVEL_3) && (normMeasure.getCost() >= 0)) {

						// ****************************************************************
						// * when phase computation, phase is bigger than 0,
						// take these
						// * values that equals the phase number -> YES
						// ****************************************************************
						if (((phase > 0) && (normMeasure.getPhase().getNumber() == phase)) || (phase == 0)) {

							// ****************************************************************
							// * generate TMA entry -> useful measure
							// ****************************************************************
							generateTMAEntry(analysis, TMAList, usedMeasures, mode, measureNorm, normMeasure, true);
						} else {

							// ****************************************************************
							// * when phase computation, phase is bigger than 0,
							// take these
							// * values that equals the phase number -> NO
							// ****************************************************************

							// ****************************************************************
							// * check if norm 27002 measure for Maturity
							// calculation
							// ****************************************************************
							if (!isCssf && measureNorm.getNorm().getLabel().equals(Constant.NORM_27002)) {

								// ****************************************************************
								// * generate TMA entry -> not a useful measure
								// ****************************************************************
								generateTMAEntry(analysis, TMAList, usedMeasures, mode, measureNorm, normMeasure, false);
							}
						}
					} else {

						// ****************************************************************
						// * check if measure is applicable, mandatory and
						// implementation
						// * rate is not 100% -> NO
						// ****************************************************************

						// ****************************************************************
						// * check the same except take measures where
						// implementation rate
						// * is not relevant AND check if norm 27002 measure for
						// Maturity
						// * calculation
						// ****************************************************************
						if (!isCssf && !(normMeasure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE))
								&& (normMeasure.getMeasureDescription().getLevel() == Constant.MEASURE_LEVEL_3) && (normMeasure.getCost() >= 0)
								&& (measureNorm.getNorm().getLabel().equals(Constant.NORM_27002))) {

							// ****************************************************************
							// * generate TMA entry -> not a useful measure
							// ****************************************************************
							generateTMAEntry(analysis, TMAList, usedMeasures, mode, measureNorm, normMeasure, false);
						}
					}
				}
			}
		}

		// ****************************************************************
		// * add maturity chapters to list of useful measures
		// ****************************************************************

		if (!isCssf && usedMeasures != null) {

			addMaturityChaptersToUsedMeasures(analysis, usedMeasures, phase);
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
	 * @param measureNorm
	 *            The AnalysisNorm of the Measure (only MeasureNorm)
	 * @param normMeasure
	 *            The Measure of the AnalysisNorm (NormMeasure)
	 * @param usefulMeasure
	 *            Flag to determine is this measure needs to be added to the
	 *            usedMeasures (a valid Measure)
	 */
	private static void generateTMAEntry(Analysis analysis, List<TMA> TMAList, List<Measure> usedMeasures, ActionPlanMode mode, MeasureNorm measureNorm, NormMeasure normMeasure,
			boolean usefulMeasure) {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		TMA tmpTMA = null;
		Assessment tmpAssessment = null;
		MaturityNorm maturityNorm = null;
		boolean measureFound = false;
		String tmpReference = "";
		int matLevel = 0;
		Parameter param = null;
		double RRF = 0;
		double cMaxEff = -1;
		double nMaxEff = -1;

		// ****************************************************************
		// * parse assesments to generate TMA entries
		// ****************************************************************

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
				RRF = Analysis.calculateRRF(tmpAssessment, analysis.getParameters(), normMeasure);

				// ****************************************************************
				// * create TMA object and initialise with assessment and
				// measure
				// * and RRF
				// ****************************************************************
				tmpTMA = new TMA(mode, tmpAssessment, normMeasure, RRF);

				// ****************************************************************
				// * calculate deltaALE for this TMA
				// ****************************************************************
				tmpTMA.calculateDeltaALE();

				// ****************************************************************
				// * check if measure needs to taken into account for action
				// plan
				// * calculation.
				// * TMA entries need to be generated for 27002 because of
				// maturity.
				// * Special case
				// ****************************************************************

				// measure needs to be taken into account? -> YES
				if (usefulMeasure && usedMeasures != null) {

					// ****************************************************************
					// * check if measure is already on the list, if not: add it
					// ****************************************************************

					// add this to useful measures list if exists variable to
					// check
					measureFound = false;

					// parse usedMeasures
					for (int unml = 0; unml < usedMeasures.size(); unml++) {

						// check if current measure exists in list -> YES
						if (usedMeasures.get(unml).equals(normMeasure)) {

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
						usedMeasures.add(normMeasure);
					}

				}

				// ****************************************************************
				// * check if measure is from 27002 norm (for maturity)
				// ****************************************************************
				if (measureNorm.getNorm().getLabel().equals(Constant.NORM_27002)) {

					// ****************************************************************
					// * retrieve reached SML
					// ****************************************************************

					// ****************************************************************
					// * extract useful reference data from reference (the
					// chapter part)
					// ****************************************************************

					// store reference
					tmpReference = normMeasure.getMeasureDescription().getReference();

					// create chapter reference to check on maturity
					tmpReference = tmpReference.substring(0, tmpReference.indexOf("."));

					// ****************************************************************
					// * Parse norms to find maturity norm to retrieve SML from
					// this
					// * chapter (which is inside tmpReference)
					// ****************************************************************

					// parse all norms
					for (int tnc = 0; tnc < analysis.getAnalysisNorms().size(); tnc++) {

						// check if norm is maturity -> YES
						if (analysis.getAnalysisNorm(tnc) instanceof MaturityNorm) {

							// store maturity norm object
							maturityNorm = (MaturityNorm) analysis.getAnalysisNorm(tnc);

							// ****************************************************************
							// * parse measures of maturity to find the correct
							// chapter
							// * (level 1) with the reference extracted from
							// 27002 norm above
							// ****************************************************************

							// parse measures of maturity norm
							for (int tmc = 0; tmc < maturityNorm.getMeasures().size(); tmc++) {

								// check if the measure reference matches the
								// extracted
								// reference -> YES
								if (maturityNorm.getMeasure(tmc).getMeasureDescription().getReference().equals(Constant.MATURITY_REFERENCE + tmpReference)) {

									// *************************************************************
									// * store maturity level (SML) of this
									// chapter
									// *************************************************************
									matLevel = maturityNorm.getMeasure(tmc).getReachedLevel();

									// leave the loop, only this case is needed
									break;
								}
							}

							// leave the loop, only the Maturity norm is needed
							break;
						}
					}

					// ****************************************************************
					// * check if SML < 5 to be used to
					// * - retrieve "current max effency" and "next max effency"
					// * parameter
					// * - calculate delta ALE for maturity
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

								// check if both parameters were found -> YES
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

									// check if both parameters were found ->
									// YES
									if ((cMaxEff > -1) && (nMaxEff > -1)) {

										// leave loop
										break;
									}
								}
							}
						}

						// ****************************************************************
						// * store current and next max effency values in TMA
						// entry
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
				// * Action Plan
				// ****************************************************************
				TMAList.add(tmpTMA);
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
	 * @param usedMeasures
	 *            List to add the Maturity Chapters to
	 * @param phase
	 *            The Phase Number to take Maturity Measures from
	 */
	private static void addMaturityChaptersToUsedMeasures(Analysis analysis, List<Measure> usedMeasures, int phase) {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		MaturityNorm maturityNorm = null;

		// ****************************************************************
		// * parse all chapters of maturity (4-15)
		// ****************************************************************

		// ****************************************************************
		// * parse norms to find maturity
		// ****************************************************************

		// parse norms
		for (int nc = 0; nc < analysis.getAnalysisNorms().size(); nc++) {

			// check if norm is maturity norm -> YES
			if (analysis.getAnalysisNorm(nc) instanceof MaturityNorm) {

				// temporary store maturity norm
				maturityNorm = (MaturityNorm) analysis.getAnalysisNorm(nc);

				// leave loop
				break;
			}
		}

		// ****************************************************************
		// * parse all measures of maturity norm
		// ****************************************************************
		for (int matmeasc = 0; matmeasc < maturityNorm.getMeasures().size(); matmeasc++) {

			// check reference if level 1 chapter that is currently parsed and
			// if reached SML < 5
			if ((maturityNorm.getMeasure(matmeasc).getMeasureDescription().getLevel() == Constant.MEASURE_LEVEL_1) && (maturityNorm.getMeasure(matmeasc).getReachedLevel() < 5)
					&& (((phase > 0) && (maturityNorm.getMeasure(matmeasc).getPhase().getNumber() == phase)) || (phase == 0))) {

				// add Maturity Chapter as nessesary
				addAMaturtiyChapterToUsedMeasures(analysis, usedMeasures, maturityNorm, maturityNorm.getMeasure(matmeasc));
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
	 * @param maturityNorm
	 *            The Maturity AnalysisNorm Object
	 * @param measure
	 *            The Measure which represents the Maturity Chapter
	 */
	private static void addAMaturtiyChapterToUsedMeasures(Analysis analysis, List<Measure> usedMeasures, MaturityNorm maturityNorm, MaturityMeasure measure) {

		// extract chapter number from level 1 measure
		String chapterValue = measure.getMeasureDescription().getReference().substring(2, measure.getMeasureDescription().getReference().length());

		// check if measure has to be added -> YES
		if ((isMaturityChapterTotalCostBiggerThanZero(maturityNorm, measure)) && (hasUsable27002MeasuresInMaturityChapter(analysis, chapterValue))) {

			// add measure to list of used measures
			usedMeasures.add(measure);
		}
	}

	/**
	 * isMaturityChapterTotalCostBiggerThanZero: <br>
	 * Checks if the Total Cost of a Maturity Chapter is bigger than 0 euros.
	 * 
	 * @param maturityNorm
	 *            The Maturity AnalysisNorm Object
	 * @param chapter
	 *            The Maturity Chapter Measure Object (Level 1 Measure)
	 * @return True if the Cost is > 0; False if Cost is 0
	 */
	private static final boolean isMaturityChapterTotalCostBiggerThanZero(MaturityNorm maturityNorm, MaturityMeasure chapter) {

		// initialise measure cost
		double totalCost = 0;

		// extract chapter number from level 1 measure
		String chapterValue = chapter.getMeasureDescription().getReference().substring(2, chapter.getMeasureDescription().getReference().length());

		// parse measure of maturity norm
		for (int i = 0; i < maturityNorm.getMeasures().size(); i++) {

			// *********************************************************
			// * perform checks to take only cost of usable measures
			// *********************************************************

			// check if reference starts with
			// "M.<currentChapter>.<currentSML+1>." and if applicable
			// and implementation rate is less than 100%
			if ((maturityNorm.getMeasure(i).getMeasureDescription().getReference().startsWith(Constant.MATURITY_REFERENCE + chapterValue + "."
					+ String.valueOf(chapter.getReachedLevel() + 1) + "."))
					&& (!maturityNorm.getMeasure(i).getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE))
					&& (maturityNorm.getMeasure(i).getImplementationRateValue() < Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE)) {

				// *****************************************************
				// * useful measure was found:
				// * add the cost to the total cost
				// of measure
				// *****************************************************
				totalCost += maturityNorm.getMeasure(i).getCost();
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
	 * chapter in the 27002 AnalysisNorm.
	 * 
	 * @param chapter
	 *            The Maturity Chapter to check
	 * @return True if there is at least 1 Measure inside the AnalysisNorm 27002
	 *         Chapter that is applicable ;False if there are no Measures in the
	 *         27002 AnalysisNorm
	 */
	private static boolean hasUsable27002MeasuresInMaturityChapter(Analysis analysis, String chapter) {

		// initialise variables
		MeasureNorm measureNorm = null;
		boolean result = false;

		// ****************************************************************
		// * check if at least 1 measure of 27002 norm
		// is applicable
		// * -> Special case
		// ****************************************************************

		// parse norms
		for (int i = 0; i < analysis.getAnalysisNorms().size(); i++) {

			// check if 27002 norm -> YES
			if (analysis.getAnalysisNorm(i) instanceof MeasureNorm) {

				// *********************************************************
				// * 27002 norm -> count applicable,
				// mandatory, level 3 and
				// * part of current maturity chapter
				// *********************************************************

				// temporary store measure norm
				measureNorm = (MeasureNorm) analysis.getAnalysisNorm(i);

				// leave loop
				break;
			}
		}

		// parse measures of norm
		for (int j = 0; j < measureNorm.getMeasures().size(); j++) {

			// the measure reference needs to start with the chapter number and
			// is applicable or
			// mandatory
			if ((measureNorm.getMeasure(j).getMeasureDescription().getReference().startsWith(chapter + "."))
					&& (!measureNorm.getMeasure(j).getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE) && (measureNorm.getMeasure(j).getMeasureDescription().getLevel() == Constant.MEASURE_LEVEL_3))) {

				// *************************************************
				// * measure found
				// * increment counter
				// *************************************************
				result = true;

				// leave loop (only 1 measure is
				// enough)
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
	 * @param actionPlan
	 *            The Action Plan with Computed Entries
	 */
	private void computeSummary(ActionPlanMode mode) {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<SummaryStage> sumStage = new ArrayList<SummaryStage>();
		SummaryValues tmpval = new SummaryValues();
		boolean anticipated = true;
		double ir = -1;
		double er = -1;
		ActionPlanEntry ape = null;
		int phase = 0;
		boolean byPhase = false;
		double phasetime = 0;
		List<ActionPlanEntry> actionPlan = this.analysis.getActionPlan(mode);
		if(actionPlan.isEmpty())
			throw new IllegalArgumentException("error.actionPlanEntry.empty");
		ActionPlanType apt = actionPlan.get(0).getActionPlanType();

		// ****************************************************************
		// * retrieve internal rate and external rate
		// ****************************************************************
		
		er = this.analysis.getParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE);
		
		ir = this.analysis.getParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE);
		
		/*for (int i = 0; i < this.analysis.getParameters().size(); i++) {
			if (this.analysis.getAParameter(i).getDescription().equals(Constant.PARAMETER_EXTERNAL_SETUP_RATE)) {
				
				

				// set external setup rate parameter
				er = this.analysis.getAParameter(i).getValue();

				// check if external and internal setup rate was retrieved ->
				// YES
				if ((er != -1) && (ir != -1)) {

					// leave loop
					break;
				}
			} else {
				if (this.analysis.getAParameter(i).getDescription().equals(Constant.PARAMETER_INTERNAL_SETUP_RATE)) {

					// set internal setup rate
					ir = this.analysis.getAParameter(i).getValue();

					// check if external and internal setup rate was retrieved
					// -> YES
					if ((er != -1) && (ir != -1)) {

						// leave loop
						break;
					}
				}
			}
		}*/

		// ****************************************************************
		// * store 27001 and 27002 norm in objects
		// ****************************************************************

		// parse norms
		/*for (int i = 0; i < this.analysis.getAnalysisNorms().size(); i++) {

			// ****************************************************************
			// * check if 27001 -> YES
			// ****************************************************************
			if (this.analysis.getAnalysisNorm(i) instanceof MeasureNorm) {

				// ****************************************************************
				// * store 27001 norm
				// ****************************************************************

				MeasureNorm normMeasure = (MeasureNorm) this.analysis.getAnalysisNorm(i);

				if (normMeasure.getNorm().getLabel().equals(Constant.NORM_27001)) {
					tmpval.norm27001 = normMeasure;
				} else if (normMeasure.getNorm().getLabel().equals(Constant.NORM_27002)) {
					tmpval.norm27002 = normMeasure;
				}

				// check if both norms are retrieved -> YES
				if ((tmpval.norm27001 != null) && (tmpval.norm27002 != null)) {

					// leave loop
					break;
				}
			}
		}*/

		tmpval.norm27001 = (MeasureNorm) this.analysis.getAnalysisNormByLabel(Constant.NORM_27001);
		
		tmpval.norm27002 = (MeasureNorm) this.analysis.getAnalysisNormByLabel(Constant.NORM_27002);

		
		// Comparator<Measure> comparator = new ComparatorMeasure();

		// if (tmpval.norm27001 != null) {
		//
		// Collections.sort(tmpval.norm27001.getMeasures(), comparator);
		// System.out.println("Measure 27001: ");
		// for (int i = 0; i < tmpval.norm27001.getMeasures().size(); i++) {
		// System.out.println("Measure: "
		// + tmpval.norm27001.getMeasures().get(i)
		// .getMeasureDescription().getReference());
		// }
		// }
		//
		// if (tmpval.norm27002 != null) {
		// Collections.sort(tmpval.norm27002.getMeasures(), comparator);
		// System.out.println("Measure 27002: ");
		// for (int i = 0; i < tmpval.norm27002.getMeasures().size(); i++) {
		// System.out.println(tmpval.norm27002.getMeasures().get(i)
		// .getMeasureDescription().getReference());
		// }
		// }

		// ****************************************************************
		// * generate first stage
		// ****************************************************************

		// add start value of ALE (for first stage (P0))
		tmpval.totalALE = actionPlan.get(0).getTotalALE() + actionPlan.get(0).getDeltaALE();

		// generate first stage
		generateStage(apt, tmpval, sumStage, "Start(P0)", true);

		// ****************************************************************
		// * check if calculation by phase
		// ****************************************************************

		// reinitialise variables
		tmpval.conf27001 = 0;
		tmpval.conf27002 = 0;

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

				// calculate phasetime
				phasetime = Analysis.getYearsDifferenceBetweenTwoDates(ape.getMeasure().getPhase().getBeginDate(), ape.getMeasure().getPhase().getEndDate());

				// check if entry is in current phase -> YES
				if (ape.getMeasure().getPhase().getNumber() == phase) {

					// ****************************************************************
					// * calculate values for next run
					// ****************************************************************
					setValuesForNextEntry(tmpval, ape, ir, er, phasetime);
									
				} else {

					// check if entry is in current phase -> NO

					// ****************************************************************
					// * generate stage for previous phase
					// ****************************************************************
					generateStage(apt, tmpval, sumStage, "Phase " + phase, false);

					// ****************************************************************
					// * reinitialise variables
					// ****************************************************************
					tmpval.conf27001 = 0;
					tmpval.conf27002 = 0;
					tmpval.deltaALE = 0;
					tmpval.externalMaintenance = 0;
					tmpval.internalMaintenance = 0;
					tmpval.externalWorkload = 0;
					tmpval.internalWorkload = 0;
					tmpval.investment = 0;
					tmpval.measureCost = 0;
					tmpval.measureCount = 0;
					tmpval.recurrentCost = 0;
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
					setValuesForNextEntry(tmpval, ape, ir, er, phasetime);
				}
			} else {

				// check if calculation by phase -> NO

				// check if ROSI >= 0 -> YES
				if (ape.getROI() >= 0) {

					setValuesForNextEntry(tmpval, ape, ir, er, 0);
				} else {

					// check if ROSI >= 0 -> NO

					// check if anticipated was already added -> NO
					if (anticipated) {

						// ****************************************************************
						// * generate stage for anticipated level
						// ****************************************************************
						generateStage(apt, tmpval, sumStage, "Anticipated", false);

						// deactivate flag
						anticipated = false;
					}

					// ****************************************************************
					// * calculate values for next run
					// ****************************************************************
					setValuesForNextEntry(tmpval, ape, ir, er, 0);
				}
			}
		}

		// ****************************************************************
		// * calculate last phase
		// ****************************************************************

		// reinitialise variables
		tmpval.conf27001 = 0;
		tmpval.conf27002 = 0;

		// check if by phase -> YES
		if (byPhase) {

			// ****************************************************************
			// * generate stage for phase
			// ****************************************************************
			generateStage(apt, tmpval, sumStage, "Phase " + phase, false);
		} else {

			// check if by phase -> NO

			// ****************************************************************
			// * generate stage for all measures
			// ****************************************************************
			generateStage(apt, tmpval, sumStage, "All Measures", false);
		}

		// for (int i = 0; i < sumStage.size(); i++) {
		// System.out.println("Stage:" + sumStage.get(i).getStage());
		// System.out.println("conformance 27001:"
		// + (sumStage.get(i).getConformance27001() * 100) + "%");
		// System.out.println("conformance 27002:"
		// + (sumStage.get(i).getConformance27002() * 100) + "%");
		// System.out.println("Number of Measures:"
		// + sumStage.get(i).getMeasureCount());
		// System.out.println("Implemented Measures:"
		// + sumStage.get(i).getImplementedMeasuresCount());
		// System.out.println("ALE:" + sumStage.get(i).getTotalALE() +
		// "���");
		// System.out.println("Risk Reduction:"
		// + sumStage.get(i).getDeltaALE() + "���");
		// System.out.println("Avarage Cost:"
		// + sumStage.get(i).getCostOfMeasures() + "���");
		// System.out.println("ROSI:" + sumStage.get(i).getROSI() +
		// "���");
		// System.out.println("relative ROSI:"
		// + sumStage.get(i).getRelativeROSI() + "���");
		// System.out.println("InternalWorkload:"
		// + sumStage.get(i).getInternalWorkload() + "md");
		// System.out.println("ExternalWorkload:"
		// + sumStage.get(i).getExternalWorkload() + "md");
		// System.out.println("Investment:" + sumStage.get(i).getInvestment()
		// + "���");
		// System.out.println("Internal Maintenance:"
		// + sumStage.get(i).getInternalMaintenance() + "md");
		// System.out.println("External Maintenance:"
		// + sumStage.get(i).getExternalMaintenance() + "md");
		// System.out.println("Recurrent Cost:"
		// + sumStage.get(i).getRecurrentCost() + "���");
		// System.out.println("Total Cost:"
		// + sumStage.get(i).getTotalCostofStage() + "���");
		// System.out
		// .println("-----------------------------------------------------------------"
		// + "------------");
		// }

		// ****************************************************************
		// * set stages in correct list
		// ****************************************************************

		this.analysis.addSummaryEntries(sumStage);
	}

	/**
	 * setValuesForNextEntry: <br>
	 * This method is used to Update the Values of a Summary Stage
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
	private void setValuesForNextEntry(SummaryValues tmpval, ActionPlanEntry ape, double ir, double er, double phasetime) {

		// ****************************************************************
		// * update phase characterisitc values
		// ****************************************************************

		// increment measure counter
		tmpval.measureCount++;

		// add measure to list of 27001 or 27002 conformance measures
		// check if 27001 measure -> YES
		if (ape.getMeasure().getAnalysisNorm().getNorm().getLabel().equals(Constant.NORM_27001)) {

			// add measure to 27001 list
			tmpval.conformance27001measures.add((NormMeasure) ape.getMeasure());
		} else {

			// check if 27001 measure -> NO

			// check if 27002 measure -> YES
			if (ape.getMeasure().getAnalysisNorm().getNorm().getLabel().equals(Constant.NORM_27002)) {

				// add measure to 27002 list
				tmpval.conformance27002measures.add((NormMeasure) ape.getMeasure());
			}
		}

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

		//System.out.println("Relative ROSI:"+tmpval.relativeROSI+"="+"ROSI: "+tmpval.ROSI+" / measureCost: "+tmpval.measureCost);
		
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
		tmpval.internalMaintenance += ape.getMeasure().getInternalWL() * ape.getMeasure().getMaintenance() / 100.;

		// in case of a phase calculation multiply internal maintenance with
		// phasetime
		if (phasetime > 0) {
			tmpval.internalMaintenance *= phasetime;
		}

		// update external maintenance
		tmpval.externalMaintenance += ape.getMeasure().getExternalWL() * ape.getMeasure().getMaintenance() / 100.;

		// in case of a phase calculation multiply external maintenance with
		// phasetime
		if (phasetime > 0) {
			tmpval.externalMaintenance *= phasetime;
		}

		// update recurrent cost
		tmpval.recurrentCost += ape.getMeasure().getInvestment() * ape.getMeasure().getMaintenance() / 100.;

		// update total cost
		tmpval.totalCost += (ape.getMeasure().getInternalWL() * ir);
		tmpval.totalCost += (ape.getMeasure().getExternalWL() * er);
		tmpval.totalCost += (ape.getMeasure().getInvestment());

		// in case of a phase calculation multiply external maintenance,
		// internal maintenance with
		// phasetime and with internal and external setup
		// as well as investment with phasetime
		if (phasetime > 0) {
			tmpval.totalCost += (ape.getMeasure().getInternalWL() * ape.getMeasure().getMaintenance() / 100. * phasetime * ir);
			tmpval.totalCost += (ape.getMeasure().getExternalWL() * ape.getMeasure().getMaintenance() / 100. * phasetime * er);
			tmpval.totalCost += (ape.getMeasure().getInvestment() * ape.getMeasure().getMaintenance() / 100. * phasetime);
		} else {
			tmpval.totalCost += (ape.getMeasure().getInternalWL() * ape.getMeasure().getMaintenance() / 100. * ir);
			tmpval.totalCost += (ape.getMeasure().getExternalWL() * ape.getMeasure().getMaintenance() / 100. * er);
			tmpval.totalCost += (ape.getMeasure().getInvestment() * ape.getMeasure().getMaintenance() / 100.);
		}
	}

	public static String extractMainChapter(String chapter) {

		if (chapter.toUpperCase().startsWith("A.")) {
			String[] chapters = chapter.split("[.]");
			return "A." + (chapters.length == 1 ? chapters[0] : chapters[1]);
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
	 */
	private void generateStage(ActionPlanType type, SummaryValues tmpval, List<SummaryStage> sumStage, String name, boolean firstStage) {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		SummaryStage aStage = null;
		NormMeasure normMeasure = null;

		// check if first stage -> YES
		if (firstStage) {

			tmpval.implementedCount = 0;
		}

		tmpval.measureCount = 0;
		tmpval.conf27001 = 0;
		tmpval.conf27002 = 0;

		// ****************************************************************
		// * check compliance for norm 27001: retrieve implementation rates
		// ****************************************************************

		if (tmpval.norm27001 != null && tmpval.norm27001.getMeasures() != null) {

			Map<String, Object[]> chapters = new HashMap<String, Object[]>();
			// parse measures of 27001
			for (int j = 0; j < tmpval.norm27001.getMeasures().size(); j++) {

				// temporary store measure of 27001
				normMeasure = tmpval.norm27001.getMeasure(j);

				// check if measure applicable or mandatory and level 3 -> YES
				if ((!normMeasure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)) && (normMeasure.getMeasureDescription().getLevel() == Constant.MEASURE_LEVEL_3)) {

					// ****************************************************************
					// * calculate sum of implementation rates and number of
					// measures
					// * after the loop divide the implementation rates by the
					// number
					// * of measures
					// ****************************************************************

					String chapterName = extractMainChapter(normMeasure.getMeasureDescription().getReference());

					Object[] chapter = chapters.containsKey(chapterName) ? chapters.get(chapterName) : new Object[] { 0.0, new Integer(0), new Integer(0) };

					Double numerator = (Double) chapter[0];// rates for 2700x
															// conformance

					Integer denominator = (Integer) chapter[1];// increment
																// measure
																// counter

					Integer implementation = (Integer) chapter[2];// increment
																	// implemented
																	// counter

					// increment measure counter
					denominator++;

					// check if it is the first stage -> YES
					if (firstStage) {

						tmpval.measureCount++;
						// check if measure is already implemented -> YES
						if (normMeasure.getImplementationRate() == Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE) {

							// increment implemented counter
							implementation++;
						}

						// in each case add the implementation rate to the sum
						// of
						// implementation
						// rates for 2700x conformance
						numerator += (normMeasure.getImplementationRate() / 100.);
					} else {

						// check if it is the first stage -> NO

						// in each case add the implementation rate to the sum
						// of
						// implementation
						// rates for 27001 conformance
						numerator += (normMeasure.getImplementationRate() / 100.);

						// check if measure was already implemented then add
						// implementation rate of 100%
						// after that, the value inserted above needs to be
						// removed
						for (int k = 0; k < tmpval.conformance27001measures.size(); k++) {
							if (normMeasure.equals((NormMeasure) tmpval.conformance27001measures.get(k))) {

								// remove added value and add measure
								// implementation
								// value as finished
								numerator += (1.) - (normMeasure.getImplementationRate() / 100.);

								// leave loop
								break;
							}
						}
					}

					chapters.put(chapterName, new Object[] { numerator, denominator, implementation });
				}
			}

			// ****************************************************************
			// * check compliance for norm 27001: calculate percentage of
			// * conformance
			// ****************************************************************

			for (String key : chapters.keySet()) {

				Object[] chapter = chapters.get(key);

				Double numerator = (Double) chapter[0];// rates for 2700x
														// conformance

				Integer denominator = (Integer) chapter[1];// increment measure
															// counter

				tmpval.conf27001 += (numerator / (double) denominator);

				/*-------------------------------------------------------------*/
				Integer implementation = (Integer) chapter[2];// increment
																// implemented
																// counter

				// System.out.println("Chapter: " + (numerator / (double)
				// denominator));

				tmpval.implementedCount += implementation;

				tmpval.measureCount += denominator;
			}

			if (chapters.size() > 0)
				tmpval.conf27001 /= (double) chapters.size();
			else
				tmpval.conf27001 = 0;

			chapters.clear();

		}

		// ****************************************************************
		// check compliance for norm 27002: retrieve implementation rates
		// ****************************************************************

		if (tmpval.norm27002 != null && tmpval.norm27002.getMeasures() != null) {

			Map<String, Object[]> chapters = new HashMap<String, Object[]>();
			// parse norm 27002 measures
			for (int j = 0; j < tmpval.norm27002.getMeasures().size(); j++) {

				// temporary store measure of 27002
				normMeasure = tmpval.norm27002.getMeasure(j);

				// check if applicable or mandatory and level 3
				if ((!normMeasure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)) && (normMeasure.getMeasureDescription().getLevel() == Constant.MEASURE_LEVEL_3)) {

					// ****************************************************************
					// * calculate sum of implementation rates and number of
					// measures
					// * after the loop divide the implementation rates by the
					// number
					// * of measures
					// ****************************************************************
					// check if this is the first stage -> YES

					String chapterName = extractMainChapter(normMeasure.getMeasureDescription().getReference());

					Object[] chapter = chapters.containsKey(chapterName) ? chapters.get(chapterName) : new Object[] { 0.0, new Integer(0), new Integer(0) };

					Double numerator = (Double) chapter[0];// rates for 2700x
															// conformance

					Integer denominator = (Integer) chapter[1];// increment
																// measure
																// counter

					Integer implementation = (Integer) chapter[2];// increment
																	// implemented
																	// counter
					// increment measure counter
					denominator++;

					if (firstStage) {

						tmpval.measureCount++;

						// check if measure is already implemented
						if (normMeasure.getImplementationRate() == Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE) {

							// increment implemented counter
							implementation++;
						}

						// in each case add the implementation rate to the sum
						// of
						// implementation
						// rates for 27002 conformance
						numerator += (normMeasure.getImplementationRate() / 100.);
					} else {

						// check if this is the first stage -> NO

						// in each case add the implementation rate to the sum
						// of
						// implementation
						// rates for 27002 conformance
						numerator += (normMeasure.getImplementationRate() / 100.);

						// in each case add the implementation rate to the sum
						// of
						// implementation
						// rates for 27001 conformance

						// check if measure was already implemented then add
						// implementation rate of 100%
						// after that, the value inserted above needs to be
						// removed
						for (int k = 0; k < tmpval.conformance27002measures.size(); k++) {

							// check if measure was already implemented -> YES
							if (normMeasure.equals((NormMeasure) tmpval.conformance27002measures.get(k))) {

								// remove implementation rate and add completed
								// implementation rate
								numerator += (1.) - (normMeasure.getImplementationRate() / 100.);

								// leave loop
								break;
							}
						}
					}

					chapters.put(chapterName, new Object[] { numerator, denominator, implementation });
				}
			}

			// ****************************************************************
			// * check compliance for norm 27002: calculate percentage of
			// * conformance
			// ****************************************************************

			for (String key : chapters.keySet()) {

				Object[] chapter = chapters.get(key);

				Double numerator = (Double) chapter[0];// rates for 2700x
														// conformance

				Integer denominator = (Integer) chapter[1];// increment measure
															// counter

				tmpval.conf27002 += (numerator / (double) denominator);

				/*-------------------------------------------------------------*/
				Integer implementation = (Integer) chapter[2];// increment
																// implemented
				// counter

				// System.out.println("Chapter: " + (numerator / (double)
				// denominator));

				tmpval.implementedCount += implementation;
			}

			// System.out.println("tmpval.conf27002:" +
			// tmpval.conf27002+" :"+chapters.size());
			if (chapters.size() > 0)
				tmpval.conf27002 /= (double) chapters.size();
			else
				tmpval.conf27002 = 0;

			chapters.clear();
		}

		// ****************************************************************
		// * create summary stage object
		// ****************************************************************
		aStage = new SummaryStage();

		aStage.setAnalysis(this.analysis);

		// add values to summary stage object
		aStage.setStage(name);
		aStage.setActionPlanType(type);
		aStage.setConformance27001(tmpval.conf27001);
		aStage.setConformance27002(tmpval.conf27002);
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
		aStage.setInternalMaintenance(tmpval.internalMaintenance);
		aStage.setExternalMaintenance(tmpval.externalMaintenance);
		aStage.setRecurrentCost(tmpval.recurrentCost);
		aStage.setTotalCostofStage(tmpval.totalCost);
		
		System.out.println("stage: "+aStage.getStage()+ ":: conformance27001: "+ aStage.getConformance27001()+ ":: conformance27002: "+ aStage.getConformance27002()+":: totalALE: "+ 
							aStage.getTotalALE()+":: deltaALE: "+ aStage.getDeltaALE()+":: cost of measures: "+ aStage.getCostOfMeasures()+":: ROSI: "+ aStage.getROSI()+":: relative ROSI: "+
							aStage.getRelativeROSI()+":: internal workload: "+ aStage.getInternalWorkload()+":: external workload: "+ aStage.getExternalWorkload()+":: investment: "+ 
							aStage.getInvestment()+":: internal maintenance: "+ aStage.getInternalMaintenance()+":: external maintenance: "+ aStage.getExternalMaintenance()+
							":: recurrent cost: "+ aStage.getRecurrentCost()+":: total cost of stage: "+ aStage.getTotalCostofStage());
		
		// ****************************************************************
		// * add summary stage to list of summary stages
		// ****************************************************************
		sumStage.add(aStage);
	}

	public Analysis getAnalysis() {
		return analysis;
	}

	/**
	 * @return the idTask
	 */
	public Long getIdTask() {
		return idTask;
	}

	/**
	 * @param idTask
	 *            the idTask to set
	 */
	public void setIdTask(Long idTask) {
		this.idTask = idTask;
	}

	public void setServiceTaskFeedback(ServiceTaskFeedback serviceTaskFeedback2) {
		this.serviceTaskFeedback = serviceTaskFeedback2;

	}

	/***********************************************************************************************
	 * Action Plan Summary - END
	 **********************************************************************************************/
}