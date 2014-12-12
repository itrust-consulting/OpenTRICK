package lu.itrust.business.TS.data.cssf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.data.actionplan.ActionPlanMode;
import lu.itrust.business.TS.data.actionplan.helper.ActionPlanComputation;
import lu.itrust.business.TS.data.actionplan.helper.TMA;
import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.assessment.Assessment;
import lu.itrust.business.TS.data.asset.Asset;
import lu.itrust.business.TS.data.cssf.tools.CSSFSort;
import lu.itrust.business.TS.data.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.data.general.Phase;
import lu.itrust.business.TS.data.general.SecurityCriteria;
import lu.itrust.business.TS.data.parameter.ExtendedParameter;
import lu.itrust.business.TS.data.parameter.Parameter;
import lu.itrust.business.TS.data.scenario.Scenario;
import lu.itrust.business.TS.data.standard.measure.AssetMeasure;
import lu.itrust.business.TS.data.standard.measure.Measure;
import lu.itrust.business.TS.data.standard.measure.NormalMeasure;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;

/**
 * RiskRegisterComputation: <br>
 * Computes NET Evaluation, RAW Evaluation and Expected Importance.
 * 
 * @author itrust consulting s.�.rl. : BJA, EOM, SME
 * @version 0.1
 * @since 11 d�c. 2012
 */
public class RiskRegisterComputation {

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	/** Analysis Object */
	private Analysis analysis = null;

	/** Value to identify reputation impact like max impact */
	public static final int MAX_IMPACT_REPUTATION = 0;

	/** Value to identify operational impact like max impact */
	public static final int MAX_IMPACT_OPERATIONAL = 1;

	/** Value to identify legal impact like max impact */
	public static final int MAX_IMPACT_LEGAL = 2;

	/** Value to identify financial impact like max impact */
	public static final int MAX_IMPACT_FINANCIAL = 3;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor: <br>
	 * Creates the object with analysis and databasehander as parameter.
	 * 
	 * @param analysis
	 *            The Analysis Object
	 */
	public RiskRegisterComputation(Analysis analysis) {
		this.analysis = analysis;
	}

	public List<TMA> generateTMAs(Analysis analysis) throws TrickException {
		List<TMA> tmas = new ArrayList<TMA>();

		List<Phase> usePhases = analysis.getPhases();

		List<Measure> useMeasures = new ArrayList<Measure>();

		int mandatoryPhase = usePhases.isEmpty() ? 0 : usePhases.get(usePhases.size() - 1).getNumber();

		for (Parameter parameter : analysis.getParameters()) {
			if (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME) && parameter.getDescription().equals(Constant.MANDATORY_PHASE))
				mandatoryPhase = new Double(parameter.getValue()).intValue();
		}

		for (int i = 0; i < usePhases.size(); i++) {
			if (usePhases.get(i).getNumber() == 0)
				continue;
			tmas.addAll(ActionPlanComputation.generateTMAList(this.analysis, useMeasures, ActionPlanMode.APN, usePhases.get(i).getNumber(), true, true, analysis.getAnalysisStandards()));
			if (mandatoryPhase == usePhases.get(i).getNumber())
				break;
		}
		return tmas;

	}

	/***********************************************************************************************
	 * Methods
	 **********************************************************************************************/

	/**
	 * computeRiskRegister: <br>
	 * Calculates the Risk Register and stores it inside the Analysis Object field "riskRegister".
	 * This field is of the Type List of RiskRegisterItem.
	 * 
	 * @return An object of MessageHandler which is null when no errors where made, or it contains
	 *         an exception
	 */
	public MessageHandler computeRiskRegister() {

		// create a messagehandler object

		try {

			System.out.println("Risk Register calculation...");

			// ****************************************************************
			// * calculate RiskRegister using CSSFComputation
			// ****************************************************************
			this.analysis.setRiskRegisters(CSSFComputation(this.analysis.getAssessments(), generateTMAs(analysis), this.analysis.getParameters()));

			// print risk register into console
			printRegister(this.analysis.getRiskRegisters());

			return null;

		} catch (Exception e) {

			// print error message
			System.out.println("Risk Register calculation and saving failed!");
			e.printStackTrace();

			return new MessageHandler(e);
		}
	}

	/***********************************************************************************************
	 * Print on Screen
	 **********************************************************************************************/

	/**
	 * printRegister: <br>
	 * Prints the Risk Register Items into the console.
	 * 
	 * @param registers
	 *            The Risk Register to print on console
	 */
	public static void printRegister(List<RiskRegisterItem> registers) {

		// check if register is not empty
		if (registers == null || registers.isEmpty()) {
			return;
		}

		// priont header line
		System.out.println("ID | Position | idScenario | idAsset | Cat. | Risk title  |  RAW evaluation " + "|Net Evaluation | Expected Importance | Response stategy");

		// print each risk register item
		for (RiskRegisterItem registerItem : registers) {
			System.out.println("--------------------------------------------------------------------" + "----------------------------------------");
			System.out.print(registerItem.getId() + " | " + registerItem.getPosition() + " | " + registerItem.getScenario().getId() + " | " + registerItem.getAsset().getId() + " | "
				+ registerItem.getScenario().getType().getName() + " | " + registerItem.getScenario().getName());
			printRiskRegisterItem(registerItem.getRawEvaluation());
			printRiskRegisterItem(registerItem.getNetEvaluation());
			printRiskRegisterItem(registerItem.getExpectedImportance());
			System.out.print(" | " + registerItem.getStrategy() + "\n");
		}
	}

	/**
	 * printRiskRegisterItem: <br>
	 * Prints the single Risk Register Result into the console.
	 * 
	 * @param evaluationResult
	 */
	public static void printRiskRegisterItem(EvaluationResult evaluationResult) {

		// check if result is not null
		if (evaluationResult != null) {

			// print the probability, impact and importance
			System.out.print("|" + evaluationResult.getProbability() + "|" + evaluationResult.getImpact() + "|" + evaluationResult.getImportance() + "|");
		} else {

			// print null
			System.out.print("|null|null|null");
		}
	}

	/***********************************************************************************************
	 * Main Method
	 **********************************************************************************************/

	/**
	 * CSSFComputation: <br>
	 * This method does the following CSSF computations:
	 * <ul>
	 * <li>{@link #netEvaluationComputation(Map, List, List) Net Evaluation computation}</li>
	 * <li>{@link #rawEvaluationComputation(Map, Map, List, List) Raw importance computation}</li>
	 * <li>{@link #expectedImportanceComputation(Map, Map, List, List) Expected importance
	 * computation}</li>
	 * </ul>
	 * After these calculations, the Risk Register will be sorted by category in order to identify
	 * the following:
	 * <ul>
	 * <li>20 most important direct risk(referring to the net importance value)</li>
	 * <li>5 most important indirect risk(referring to the net importance value)</li>
	 * <li>Risks that have a net impact >=6 and net impact probability >= 5</li>
	 * </ul>
	 * 
	 * @param assessments
	 *            The List of Assessments
	 * @param tmas
	 *            The List of TMA Entries
	 * @param parameters
	 *            The List of Parameters
	 * 
	 * @return The Risk Register as a List of RiskRegisterItems
	 * @throws TrickException
	 * 
	 * @see CSSFTools#sortByGroup(Map)
	 * @see CSSFTools#sortAndConcatenateGroup(Map)
	 */
	public static List<RiskRegisterItem> CSSFComputation(final List<Assessment> assessments, final List<TMA> tmas, final List<Parameter> parameters) throws TrickException {

		// initialise ALE Array (this array will contain all net ALE's of each Risk)
		// Integer: Scenario ID, Double: ALE of the Scenario, calculated by Inet*Pnet)
		Map<String, Double> netALEs = new HashMap<String, Double>();

		// initialise a empty map that contains Impact objects
		Map<String, Impact> impacts = new HashMap<String, Impact>();

		// initialise the risk register as an empty list of risk register items
		Map<String, RiskRegisterItem> riskRegisters = new HashMap<String, RiskRegisterItem>();

		// create an empty list for probability relative impacts
		Map<String, double[]> probabilityRelativeImpacts = new HashMap<String, double[]>();

		// initialise a map for the rawALEs
		Map<String, Double> rawALEs = new HashMap<String, Double>();

		// initialise an empty map for delta ALEs
		Map<String, Double> deltaALEs = new HashMap<String, Double>();

		// calculate the NET Evaluation
		netEvaluationComputationData(riskRegisters, netALEs, impacts, assessments, parameters);

		// calculate RawALEs, DeltaALEs and Probability Relative Impacts
		computeRawALEAndDeltaALEAndProbabilityRelativeImpacts(probabilityRelativeImpacts, impacts, rawALEs, deltaALEs, netALEs, tmas, parameters);

		// compute
		cssfFinalComputation(riskRegisters, netALEs, impacts, probabilityRelativeImpacts, rawALEs, deltaALEs, parameters);

		// set register items into three groups direct, indirect and others
		Map<String, List<RiskRegisterItemGroup>> results = CSSFSort.sortByGroup(riskRegisters);

		// Concatenate direct and indirect using 20 direct and those with acceptable impact and
		// probability as well as 5 indirect and those with acceptable impact and probability

		double acceptableImportace = 0;

		return CSSFSort.sortAndConcatenateGroup(results, acceptableImportace);
	}

	/**
	 * cssfFinalComputation: <br>
	 * Computes last nessesary computations on raw and excepted evaluation.
	 * 
	 * @param riskRegisters
	 *            The Risk Register to compute
	 * @param netALEs
	 *            The netALE List
	 * @param impacts
	 *            The Impacts List
	 * @param probabilityRelativeImpacts
	 *            The probability relative impacts
	 * @param rawALEs
	 *            The raw ALE List
	 * @param deltaALEs
	 *            The delta ALE List
	 * @param parameters
	 *            The Parameters List
	 * @throws TrickException
	 */
	private static void cssfFinalComputation(Map<String, RiskRegisterItem> riskRegisters, Map<String, Double> netALEs, Map<String, Impact> impacts, Map<String, double[]> probabilityRelativeImpacts,
			Map<String, Double> rawALEs, Map<String, Double> deltaALEs, List<Parameter> parameters) throws TrickException {

		// parse all risk register items
		for (RiskRegisterItem registerItem : riskRegisters.values()) {

			// extract the scenario ID
			String key = registerItem.getScenario().getId() + "_" + registerItem.getAsset().getId();

			// retrieve for this scenario the netALE value
			double netALE = netALEs.get(key);

			/*
			 * if(!deltaALEs.containsKey(key)){
			 * System.out.println(registerItem.getScenario().getName()); System.out
			 * .println(registerItem.getScenario().getType().getTypeName()); }
			 */

			// retrieve for this scenario the deltaALE value
			Double deltaALE = deltaALEs.get(key);

			// retrieve for this scenario the probability relative Impacts value
			double[] probabilityRelativeImpact = probabilityRelativeImpacts.get(key);

			// retrieve for this scenario the rawALE value
			Double rawALE = rawALEs.get(key);

			// retrieve for this scenario the impact value
			double netImpact = impacts.get(key).getReal();

			// retrieve for this scenario the probability value
			double netProbability = netALE / netImpact;

			// determine netImpact and netProbability level from input data.
			registerItem.getNetEvaluation().setImpact(netImpact);

			registerItem.getNetEvaluation().setProbability(Double.isNaN(netProbability) ? 0 : netProbability);

			// calculate the last step of expected Evaluation
			expectedImportanceComputation(registerItem, deltaALE, netALE, probabilityRelativeImpact, parameters);

			// calculate the last step of raw evaluation Evaluation
			rawEvaluationComputation(registerItem, netALE, rawALE, probabilityRelativeImpact, parameters);
		}
	}

	/***********************************************************************************************
	 * NET EVALUATION - BEGIN
	 **********************************************************************************************/

	/**
	 * netEvaluationComputation: <br>
	 * Calculates the NET Evaluation<br>
	 * Steps:
	 * <ul>
	 * <li>Summing every impact category for each Scenario</li>
	 * <li>Identify maximum impact category for each Scenario</li>
	 * <li>Compute ALE of each Scenario (ALEnet)</li>
	 * <li>Compute generic probability (Pgen = (Pnet*Inet)/Imax) = Pgen = ALEnet/Imax</li>
	 * </ul>
	 * 
	 * @param riskRegisters
	 *            The Risk Register Item
	 * @param netALE
	 *            Array to store the ALE net (sum of each Probability * Impact of each Scenario
	 *            inside the Assessments array)
	 * @param impacts
	 *            The List of Impacts for each Risk
	 * @param assessments
	 *            The Array of Assessments
	 * @param parameters
	 *            The Array of Analysis Parameters
	 * @throws TrickException
	 */
	public static void netEvaluationComputationData(Map<String, RiskRegisterItem> riskRegisters, Map<String, Double> netALE, Map<String, Impact> impacts, final List<Assessment> assessments,
			final List<Parameter> parameters) throws TrickException {

		// ********************************************************
		// * first step: generate the sum of each impact category Categories are: reputation ,
		// operational , legal, financial
		// ********************************************************

		// set the impacts of each category (this will parse all assessment and will make a sum of
		// each impact category (inside the Impact class) for each Scenario)
		impacts.putAll(computeImpactGeneric(assessments, parameters));

		// ********************************************************
		// * second step: For each Scenario identify the biggest Impact Category
		// ********************************************************

		// calculates the ALE inside the netALE array (using the biggest Impact and probability) for
		// each Scenario and initialise the netEvaluation result (prepare the Array with size)
		riskRegisters.putAll(computeNetALE(netALE, impacts, assessments, parameters));

		// ********************************************************
		// * last step: calculate netALE/maxImpact for each Scenario and store result as
		// NetEvaluation inside netEvaluation array
		// ********************************************************
	}

	/**
	 * computeImpactGeneric: <br>
	 * This method will parse all Scenarios inside the Assessments List and will compute the sum of
	 * each category of impact (operational, financial, legal and reputation). Each Scenario that
	 * has a CSSF type will be checked inside the Assessments to make a sum of impact of each
	 * Categoryonly on CSSF scenarios. This computed List (Index: Scenario ID, Value: Impact Class)
	 * will be returned.Inside this list one can find the sum of Impacts of each CSSF Category of
	 * all Scenarios.
	 * 
	 * @param assessments
	 *            The List of Assessments
	 * @param parameters
	 *            The List of Parameters
	 * 
	 * @return A list of each Impact Category of each Scenario
	 */
	protected static Map<String, Impact> computeImpactGeneric(final List<Assessment> assessments, final List<Parameter> parameters) {

		Map<String, Parameter> mapParameters = new LinkedHashMap<String, Parameter>();

		for (Parameter parameter : parameters)
			if ((parameter instanceof ExtendedParameter) && parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
				mapParameters.put(((ExtendedParameter) parameter).getAcronym(), parameter);

		// initialise the result (which is an Array where each Entry is defined by the Scenario ID
		// and the Sum of Impacts of each Impact Category)
		Map<String, Impact> impacts = new HashMap<String, Impact>();

		// parse all assessments
		for (Assessment assessment : assessments) {

			// check if assessment is usable and if Scenario Type is CSSF
			if (assessment.isUsable()) {

				// System.out.println("Asset: "+assessment.getAsset().getName());

				// System.out.println("Scenario: "+assessment.getScenario().getName());

				// System.out.println("ALE: "+assessment.getALE());

				// store scenario
				Scenario scenario = assessment.getScenario();

				String key = scenario.getId() + "_" + assessment.getAsset().getId();

				// retrieve corresponding Impact Object of this Scenario from the Array
				Impact impact = impacts.get(key);

				// System.out.println("computeNetEvaluation Scenario: " + scenario.getName());

				// the following check is done once at the beginning when the Scenario Key was not
				// found in the impacts array, if this is the case, the Scenario will be added to
				// the Array
				// with an empty Impact Object

				// Check if the Impact Object exists (Key exists inside the Array) -> NO
				if (impact == null) {

					// create a new object
					impact = new Impact(0, 0, 0, 0, mapParameters);

					// add the object to the Array at the correct place
					impacts.put(key, impact);
				}

				// summing impacts (update previous value with the one of the assessment)

				// Reputation
				impact.setReputation(Impact.convertStringImpactToDouble(assessment.getImpactRep(), mapParameters) + impact.getRealReputation());

				// operational
				impact.setOperational(Impact.convertStringImpactToDouble(assessment.getImpactOp(), mapParameters) + impact.getRealOperational());

				// legal
				impact.setLegal(Impact.convertStringImpactToDouble(assessment.getImpactLeg(), mapParameters) + impact.getRealLegal());

				// financial
				impact.setFinancial(Impact.convertStringImpactToDouble(assessment.getImpactFin(), mapParameters) + impact.getRealFinancial());
			}
		}

		// return result
		return impacts;
	}

	/**
	 * computeNetALE: <br>
	 * Calculate the ALE (Pnet*Inet) of each Scenario and Sum them together to build the nominator
	 * of the Formula "Pgen=(sum(P*I))/(sum(Imax)))" Reference in the CSSF document section 3.1.1.
	 * 
	 * @param netALEs
	 *            The List of netALE's for each Scenario (numerator)
	 * @param impacts
	 *            The List of Impacts for each Scenario (Impact List)
	 * @param assessments
	 *            The Assessments List
	 * @param parameters
	 *            The parameters List
	 * 
	 * @return The initialised list of the risk register
	 * @throws TrickException
	 */
	private static Map<String, RiskRegisterItem> computeNetALE(Map<String, Double> netALEs, final Map<String, Impact> impacts, final List<Assessment> assessments, final List<Parameter> parameters)
			throws TrickException {

		Map<String, Parameter> mapParameters = new LinkedHashMap<String, Parameter>();

		for (Parameter parameter : parameters)
			if ((parameter instanceof ExtendedParameter))
				mapParameters.put(((ExtendedParameter) parameter).getAcronym(), parameter);

		// initialise the risk register with the size of elements inside the impacts list
		Map<String, RiskRegisterItem> riskRegisters = new HashMap<String, RiskRegisterItem>(impacts.size());

		// parse all assessments elements
		for (Assessment assessment : assessments) {

			// get scenario id of the assessments scenario
			String key = assessment.getScenario().getId() + "_" + assessment.getAsset().getId();

			// check if impacts list has this key (key of impacts= scenario ID)
			if (impacts.containsKey(key)) {

				// System.out.println(assessment.getScenario().getName() +
				// ", Impact Max index: "
				// + index + " Real value " + impacts.get(key));

				// get or initialises the netALE value (numerator = sum(Pnet*Inet))
				double netALE = (netALEs.containsKey(key) ? netALEs.get(key) : 0);

				// identify biggest impact and calculate the numerator (ALe using P*I)
				netALE += computeALE(impacts, assessment, mapParameters);

				// update ALE numerator
				netALEs.put(key, netALE);

				// Initialize risk register items with scenario object
				if (!riskRegisters.containsKey(key))
					riskRegisters.put(key, new RiskRegisterItem(assessment.getScenario(), assessment.getAsset()));
			}
		}

		// return the resulting riskregister (with size and scenario initialised)
		return riskRegisters;
	}

	/**
	 * getLikelihoodLevel: <br>
	 * Use parameters to retrieve Likelihood Level [0 ; 10]
	 * 
	 * @param likelihood
	 *            [0; +&infin;[
	 * @param parameters
	 *            List of parameters capable to identify likelihood level
	 * @return The determined likelihood level
	 */
	public static int getLikelihoodLevel(double likelihood, List<Parameter> parameters) {

		// retrieve the level using the probability parameters
		return getLevel(likelihood, Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME, parameters);
	}

	/**
	 * getImpactLevel: <br>
	 * Use parameters to retrieve Impact Level [0 ; 10]
	 * 
	 * @param impact
	 *            [0; +&infin;[
	 * @param parameters
	 *            List of parameters capable to identify likelihood level
	 * @return The determined Impact level
	 */
	public static int getImpactLevel(double impact, List<Parameter> parameters) {

		// retrieve the level using the impact parameters
		return getLevel(impact, Constant.PARAMETERTYPE_TYPE_IMPACT_NAME, parameters);
	}

	/**
	 * getMaxImpactCode: <br>
	 * retrieve max impact index:
	 * <ul>
	 * <li>{@value #MAX_IMPACT_REPUTATION} = reputation</li>
	 * <li>{@value #MAX_IMPACT_OPERATIONAL} = operational</li>
	 * <li>{@value #MAX_IMPACT_LEGAL} = legal</li>
	 * <li>{@value #MAX_IMPACT_FINANCIAL} = financial</li>
	 * </ul>
	 * 
	 * @param impact
	 *            The Impact Object with the impact category values to check
	 * @return code [ {@value #MAX_IMPACT_REPUTATION} = reputation ,
	 *         {@value #MAX_IMPACT_OPERATIONAL} = operational , {@value #MAX_IMPACT_LEGAL} = legal,
	 *         {@value #MAX_IMPACT_FINANCIAL} = financial]
	 */
	public static int getMaxImpactCode(Impact impact) {

		// retrieve impact categories
		double rep = impact.getRealReputation();
		double op = impact.getRealOperational();
		double leg = impact.getRealLegal();
		double fin = impact.getRealFinancial();

		// determine biggest impact category

		if ((rep >= op) && (rep >= leg) && (rep >= fin)) {

			// biggest is reputation
			return MAX_IMPACT_REPUTATION;
		}

		if ((op >= rep) && (op >= leg) && (op >= fin)) {

			// biggest is operational
			return MAX_IMPACT_OPERATIONAL;
		}

		if ((leg >= op) && (leg >= rep) && (leg >= fin)) {

			// biggest is legal
			return MAX_IMPACT_LEGAL;
		}

		if ((fin >= op) && (fin >= rep) && (fin >= leg)) {

			// biggest is fincancial
			return MAX_IMPACT_FINANCIAL;
		}

		// at this time an error happend and none is maximum
		return -1;
	}

	/**
	 * getLevel: <br>
	 * Use parameters to retrieve Level [0 ; 10]
	 * 
	 * @param value
	 *            [0; +&infin;[
	 * @param type
	 *            {PROPA, IMPACT}
	 * @param parameters
	 *            List of parameter capable to identify the Level
	 * @return The Level of the given value and type
	 */
	public static int getLevel(double value, String type, List<Parameter> parameters) {

		// check if value is >= 0
		if (value < 0)
			throw new IllegalArgumentException("RiskRegisterComputation#getLevel: value should be greater or equal 0");

		// parse parameters array
		for (Parameter parameter : parameters) {

			// check on the type of the parameter and if the value is in this bounds
			if (parameter instanceof ExtendedParameter && parameter.getType().getLabel().equals(type) && ((ExtendedParameter) parameter).getBounds().isInRange(value)) {
				// return the parameter level
				return ((ExtendedParameter) parameter).getLevel();
			}
		}

		// at this time the value could not be found in the parameters and we asume the level is the
		// biggest possible: 10
		return 10;
	}

	/**
	 * levelToValue: <br>
	 * Description
	 * 
	 * @param level
	 * @param type
	 * @param parameters
	 * @return
	 */
	public static double levelToValue(int level, String type, List<Parameter> parameters) {
		// check if value is >= 0
		if (level < 0)
			throw new IllegalArgumentException("RiskRegisterComputation#levelToValue: level should be greater or equal 0");

		// parse parameters array
		for (Parameter parameter : parameters) {

			// check on the type of the parameter and if the value is in this bounds
			if (parameter instanceof ExtendedParameter && parameter.getType().getLabel().equals(type) && ((ExtendedParameter) parameter).getLevel() == level) {
				// return the parameter level
				return ((ExtendedParameter) parameter).getValue();
			}
		}
		// at this time the value could not be found in the parameters and we asume the level is the
		// biggest possible: 10
		return Constant.DOUBLE_MAX_VALUE;
	}

	/**
	 * computeALE: <br>
	 * Description
	 * 
	 * @param impacts
	 * @param assessment
	 * @param parameters
	 * @return
	 */
	private static double computeALE(Map<String, Impact> impacts, Assessment assessment, Map<String, Parameter> parameters) {

		String key = assessment.getScenario().getId() + "_" + assessment.getAsset().getId();
		int index = getMaxImpactCode(impacts.get(key));

		// System.out.println(assessment.getScenario().getName() +
		// ", Impact Max index: "
		// + index + " Real value " + impacts.get(key));

		// get or initialises the netALE value (numerator = sum(Pnet*Inet))

		double ALE = 0;

		// identify biggest impact and calculate the numerator (ALe using P*I)

		switch (index) {

			case MAX_IMPACT_REPUTATION:
				ALE = Impact.convertStringImpactToDouble(assessment.getImpactRep(), parameters) * likelihoodToNumeric(assessment.getLikelihood(), parameters);
				break;
			case MAX_IMPACT_OPERATIONAL:
				ALE = Impact.convertStringImpactToDouble(assessment.getImpactOp(), parameters) * likelihoodToNumeric(assessment.getLikelihood(), parameters);
				break;
			case MAX_IMPACT_LEGAL:
				ALE = Impact.convertStringImpactToDouble(assessment.getImpactLeg(), parameters) * likelihoodToNumeric(assessment.getLikelihood(), parameters);
				break;
			case MAX_IMPACT_FINANCIAL:
				ALE = Impact.convertStringImpactToDouble(assessment.getImpactFin(), parameters) * likelihoodToNumeric(assessment.getLikelihood(), parameters);
				break;
			default:
				throw new IllegalArgumentException("RiskRegisterComputation#computeProbability: index should be between 0 and 3");
		}

		return ALE;

	}

	/**
	 * impactLevelToValue: <br>
	 * Description
	 * 
	 * @param level
	 * @param parameters
	 * @return
	 */
	public static double impactLevelToValue(int level, List<Parameter> parameters) {
		return levelToValue(level, Constant.PARAMETERTYPE_TYPE_IMPACT_NAME, parameters);
	}

	/**
	 * @param level
	 * @param parameters
	 * @return
	 */
	public static double likelihoodLevelToValue(int level, List<Parameter> parameters) {
		return levelToValue(level, Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME, parameters);
	}

	/**
	 * likelihoodToNumeric: <br>
	 * Retrieve value of likelihood (Acronym).
	 * 
	 * @param likelihood
	 *            The Acronym
	 * @param parameters
	 *            List of parameter
	 * 
	 * @return The likelihood value
	 */
	public static double likelihoodToNumeric(String likelihood, Map<String, Parameter> parameters) {

		if (parameters.containsKey(likelihood))
			return parameters.get(likelihood).getValue();
		// throw error if at this moment the parameter was not yet found
		throw new IllegalArgumentException("RiskRegisterComputation#likelihoodToNumeric: Acronym cannot be find :" + likelihood);
	}

	/***********************************************************************************************
	 * NET EVALUATION - END
	 **********************************************************************************************/

	/***********************************************************************************************
	 * RAW EVALUATION - BEGIN
	 **********************************************************************************************/

	/**
	 * computeRawALEAndDeltaALEAndProbabilityRelativeImpacts:<br>
	 * <ul>
	 * <li>pre-computation for Raw Evaluation and Expected Importance Evaluation:
	 * <ul>
	 * <li>Common : compute of probabilityRelativeImpacts</li>
	 * <li>Raw Evaluation : rawALEs computation</li>
	 * <li>Expected Importance Evaluation : deltaALEs</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param probabilityRelativeImpacts
	 *            output
	 * @param impacts
	 * @param rawALEs
	 *            List to store and calculate RAW ALE's for a affecting scenario category
	 * @param deltaALEs
	 *            List to store and calculate delta ALE's for a affecting scenario category
	 * @param netALEs
	 *            List of calculated netALE nominators (calculated inside netEvaluation)
	 * @param tmas
	 *            List of TMA entries generated from the Analysis
	 * @param parameters
	 *            List of Parameters of Analysis
	 * @throws TrickException
	 * 
	 * @see #computeDeltaALEs(Map, Map, TMA, List)
	 * @see #computeRawALE(Map, Map, TMA, List)
	 * @see #computeProbabilityRelativeImpact(Map, Scenario, Measure)
	 */
	private static void computeRawALEAndDeltaALEAndProbabilityRelativeImpacts(Map<String, double[]> probabilityRelativeImpacts, Map<String, Impact> impacts, Map<String, Double> rawALEs,
			Map<String, Double> deltaALEs, final Map<String, Double> netALEs, final List<TMA> tmas, final List<Parameter> parameters) throws TrickException {

		Map<String, Parameter> mapParameters = new LinkedHashMap<String, Parameter>();

		for (Parameter parameter : parameters)
			if ((parameter instanceof ExtendedParameter))
				mapParameters.put(((ExtendedParameter) parameter).getAcronym(), parameter);

		// parse all TMA entries
		for (TMA tma : tmas) {

			// store scenario
			Scenario scenario = tma.getAssessment().getScenario();

			// determine category
			String category = CategoryConverter.getTypeFromScenario(scenario);

			boolean hasinfluence = false;

			if (tma.getMeasure() instanceof NormalMeasure)
				hasinfluence = ((NormalMeasure) tma.getMeasure()).getMeasurePropertyList().hasInfluenceOnCategory(category);
			else if (tma.getMeasure() instanceof AssetMeasure)
				hasinfluence = ((AssetMeasure) tma.getMeasure()).getMeasurePropertyList().hasInfluenceOnCategory(category);

			// check if measure influences this scenario category -> YES
			if (hasinfluence) {

				Asset asset = tma.getAssessment().getAsset();

				String key = scenario.getId() + "_" + asset.getId();

				// Integer: The Scenario ID, Integer : Code that defines the biggest Category 0:
				// reputation, 1: operational, 2: legal, 3: financial

				double ALE = computeALE(impacts, tma.getAssessment(), mapParameters);

				// System.out.println("Scenario id: "+scenario.getName());

				// compute deltaALE
				computeDeltaALEs(deltaALEs, ALE, tma, parameters);

				// compute RawALE
				computeRawALE(rawALEs, netALEs, tma, parameters);

				// Compute Relative Probability and Relative Impact
				computeProbabilityRelativeImpact(probabilityRelativeImpacts, key, scenario);
			}

		}
	}

	/**
	 * computeDeltaALEs: <br>
	 * Sum deltaALE for a given TMA and store inside deltaALEs parameter.
	 * 
	 * @param deltaALEs
	 *            The given List to store the Sum of DeltaALE of given TMA entry
	 * @param tma
	 *            The TMA entry to retrieve delta ALE from
	 * @param parameters
	 *            The List of Parameters
	 * @throws TrickException
	 */
	private static void computeDeltaALEs(Map<String, Double> deltaALEs, double ALE, final TMA tma, final List<Parameter> parameters) throws TrickException {

		// retrieve scenario ID
		String key = tma.getAssessment().getScenario().getId() + "_" + tma.getAssessment().getAsset().getId();

		/*
		 * System.out .println("Scenario: " + tma.getAssessment().getScenario().getName() +
		 * ", AlE: " + ALE);
		 */

		ALE = deltaALEs.containsKey(key) ? ALE - deltaALEs.get(key) : ALE;

		double currentDeltaALE = TMA.calculateDeltaALE(ALE, tma.getRRF(), tma.getMeasure());

		// retireve existing summed value of this scneario and add current deltaALE, if none exist
		// use the current as new value

		double deltaALE = deltaALEs.containsKey(key) ? deltaALEs.get(key) + currentDeltaALE : currentDeltaALE;

		deltaALEs.put(key, deltaALE);

		// update deltaALE

	}

	/**
	 * computeRawALE: <br>
	 * Compute the Raw ALE and store the value inside the given rawALEs list.
	 * 
	 * @param rawALEs
	 *            The List of raw ALEs
	 * @param netALEs
	 *            The List of net ALEs
	 * @param tma
	 *            The TMA Entry
	 * @param parameters
	 *            The Parameter List
	 */
	private static void computeRawALE(Map<String, Double> rawALEs, final Map<String, Double> netALEs, final TMA tma, final List<Parameter> parameters) {

		// retrieve scenario ID
		String key = tma.getAssessment().getScenario().getId() + "_" + tma.getAssessment().getAsset().getId();

		// Retrieve current rawALE of the scenario ID if it does not exist take the start value of
		// the netALE of this scenario
		double rawALE = rawALEs.containsKey(key) ? rawALEs.get(key) : netALEs.get(key);

		// Retrieve implementation rate of the measure and transform it to percentage
		double ImplementationRate = tma.getMeasure().getImplementationRateValue() * 0.01;

		// calculate new RAW ALE using formula
		rawALE /= (1.0 - tma.getRRF() * ImplementationRate);

		/*
		 * System.out.println(tma.getAssessment().getScenario().getName() + ", RRF: " + tma.getRRF()
		 * + ", ImplementationRate: " + ImplementationRate);
		 */
		// update rawALE
		rawALEs.put(key, rawALE);
	}

	/**
	 * computeProbabilityRelativeImpact: <br>
	 * Final goal, compute X = (Preventive + 0,5* Detective) / (Preventive + Detective + Limitative
	 * + Preventive).<br />
	 * Here, we compute numerator and denominator.
	 * <ul>
	 * <li>Key = Scenario id</li>
	 * <li>
	 * Numerator = value[0]</li>
	 * <li>
	 * Denominator = value[1]</li>
	 * </ul>
	 * 
	 * @return Map(String,double[2]) where key = scenario name, value[0] = Numerator and value[1] =
	 *         Denominator
	 */
	private static void computeProbabilityRelativeImpact(Map<String, double[]> probabilityRelativeImpacts, String key, SecurityCriteria criteria) {

		// use exisisting value or initialise new
		double[] probabilityRelativeImpact = probabilityRelativeImpacts.containsKey(key) ? probabilityRelativeImpacts.get(key) : new double[] { 0, 0 };

		// compute numerator
		probabilityRelativeImpact[0] += (criteria.getPreventive() + criteria.getDetective() * 0.5);

		// Compute Denominator
		probabilityRelativeImpact[1] += (criteria.getPreventive() + criteria.getDetective() + criteria.getLimitative() + criteria.getCorrective());

		// save final compute
		probabilityRelativeImpacts.put(key, probabilityRelativeImpact);
	}

	/**
	 * rawEvaluationComputation: <br>
	 * Compute Raw evaluation. for a given risk register.
	 * 
	 * @param riskRegisters
	 *            The Risk Register
	 * @param netALEs
	 *            The calculated Net ALE list
	 * @param rawALEs
	 *            The calculated Raw ALE list
	 * @param probabilityRelativeImpacts
	 *            the calculated relativeimpact list
	 * @param parameters
	 *            The List of Parameters
	 * @throws TrickException
	 * @see #computeRawALE(Map, Map, List, List)
	 */
	public static void rawEvaluationComputation(RiskRegisterItem riskRegisters, final double netALE, final Double rawALE, double[] probabilityRelativeImpact, final List<Parameter> parameters)
			throws TrickException {

		// retrieve net impact
		double netImpact = riskRegisters.getNetEvaluation().getImpact();

		// retrieve net importance
		double netProbability = riskRegisters.getNetEvaluation().getProbability();

		// retrieve net importance
		// double netImportance = riskRegisters.getNetEvaluation().getImportance();

		// compute scale point of importance
		// double impScalePoint = netALE / netImportance;

		if (rawALE == null) {
			riskRegisters.setRawEvaluation(new EvaluationResult(netProbability, netImpact));
			return;
		}

		// compute deltaImportance double deltaRawImportance = (rawALE - netALE); // /
		// impScalePoint;

		// compute raw importance
		// double rawImportance = rawALE;

		/*
		 * System.out.println(riskRegisters.getScenario().getName() + " ImportanceNet: " +
		 * netImportance); System.out.println(riskRegisters.getScenario().getName() + " NetALE: " +
		 * netALE); System.out.println(riskRegisters.getScenario().getName() + " RawALE: " +
		 * rawALE); System.out.println(riskRegisters.getScenario().getName() + " ImpScalePoint: " +
		 * impScalePoint); System.out.println(riskRegisters.getScenario().getName() +
		 * " DeltaRawImportance: " + deltaRawImportance);
		 * System.out.println(riskRegisters.getScenario().getName() + " RawImportance: " +
		 * rawImportance);
		 */

		/**
		 * Computes "Expected Importance" and update riskRegisters.<br />
		 * Computes X, Pa and Ia:
		 * <ul>
		 * <li>
		 * Key = Scenario id</li>
		 * <li>
		 * Pc = riskRegisters[key].netEvaluation.Probability</li>
		 * <li>
		 * Ic = riskRegisters[key].netEvaluation.Impact</li>
		 * <li>
		 * Ra = rawImportance</li>
		 * <li>
		 * X = probabilityRelativeImpacts[key][0] / probabilityRelativeImpacts[Key][1]</li>
		 * <li>
		 * Pa = Pc*( Ra /( Pc * Ic ) ) ^ X</li>
		 * <li>
		 * Ia = Ra / Pa</li>
		 * </ul>
		 */
		// computation of proportional strength
		double x = probabilityRelativeImpact[0] / probabilityRelativeImpact[1];

		// update raw evaluation
		riskRegisters.setRawEvaluation(computeImpactAndProbability(x, netImpact, netProbability, rawALE));
	}

	/***********************************************************************************************
	 * RAW EVALUATION - END
	 **********************************************************************************************/

	/***********************************************************************************************
	 * Expected Importance EVALUATION - BEGIN
	 **********************************************************************************************/

	/**
	 * expectedImportanceComputation: <br>
	 * update expected importance in riskRegisters.<br>
	 * steps:
	 * <ul>
	 * <li>{@link #computeProbabilityRelativeImpact(List, List) Computation of Probability Relative
	 * Impact}</li>
	 * <li>{@link #computeExpectedImportance(Map, Map) Merge All}</li>
	 * </ul>
	 * 
	 * @param riskRegisters
	 *            The Risk Register Item
	 * @param netImpact
	 *            The netImpact value
	 * @param netProbability
	 *            The netProbability value
	 * @param deltaALE
	 *            The delta ALE List
	 * @param netALE
	 *            The net ALE List
	 * @param probabilityRelativeImpact
	 *            The probability relative impact list
	 * @param parameters
	 *            The Parameters List
	 * @throws TrickException
	 */
	public static void expectedImportanceComputation(RiskRegisterItem riskRegisters, final Double deltaALE, final double netALE, final double[] probabilityRelativeImpact,
			final List<Parameter> parameters) throws TrickException {

		// retrieve net impact
		double netImpact = riskRegisters.getNetEvaluation().getImpact();
		// retrieve net importance
		double netProbability = riskRegisters.getNetEvaluation().getProbability();
		// retrieve net importance
		double importanceNet = riskRegisters.getNetEvaluation().getImportance();

		if (deltaALE == null) {
			riskRegisters.setExpectedImportance(new EvaluationResult(netProbability, netImpact));
			return;
		}

		// compute quantitative value
		// double quantitativeValue = netALE / importanceNet;

		// compute delta Importance
		// double deltaImp = deltaALE / quantitativeValue;

		// compute expected importance
		double expImportance = importanceNet - deltaALE;

		/*
		 * System.out.println(riskRegisters.getScenario().getName() + " ImportanceNet: " +
		 * importanceNet);
		 * 
		 * System.out.println(riskRegisters.getScenario().getName() + " NetALE: " + netALE);
		 * 
		 * System.out.println(riskRegisters.getScenario().getName() + " DeltaALE: " + deltaALE);
		 * 
		 * System.out.println(riskRegisters.getScenario().getName() + " QuantitativeValue: " +
		 * quantitativeValue);
		 * 
		 * /*System.out.println(riskRegisters.getScenario().getName() + " DeltaImp: " + deltaImp);
		 * 
		 * System.out.println(riskRegisters.getScenario().getName() + " ExpImportance: " +
		 * expImportance);
		 */

		if (expImportance < 0) {
			riskRegisters.setExpectedImportance(new EvaluationResult(0, 0));
			System.err.println("Expected importance " + expImportance + "<0 for " + riskRegisters.getScenario().getName());
		} else {
			// computation of proportional strength
			double x = probabilityRelativeImpact[0] / probabilityRelativeImpact[1];

			// update expected importance
			riskRegisters.setExpectedImportance(computeImpactAndProbability(x, netImpact, netProbability, expImportance));
		}
	}

	/**
	 * computeImpactAndProbability: <br>
	 * Computes "Expected Importance" and update riskRegisters.<br />
	 * Computes X, Pa and Ia:
	 * <ul>
	 * <li>
	 * Key = Scenario id</li>
	 * <li>
	 * Pc = riskRegisters[key].netEvaluation.Probability</li>
	 * <li>
	 * Ic = riskRegisters[key].netEvaluation.Impact</li>
	 * <li>
	 * Ra = importance</li>
	 * <li>
	 * X = probabilityRelativeImpacts[key][0] /probabilityRelativeImpacts[Key][1]</li>
	 * <li>
	 * Pa = Pc*( Ra /( Pc * Ic ) ) ^ X</li>
	 * <li>
	 * Ia = Ra / Pa</li>
	 * </ul>
	 * 
	 * @param parameters
	 *            The Parameters List
	 * @param riskRegisters
	 *            the Risk Register Item
	 * @param probabilityRelativeImpacts
	 *            the list of probability relative impacts
	 * @throws TrickException
	 */
	public static EvaluationResult computeImpactAndProbability(final double x, final double currentImpact, final double currentProbability, final double importance) throws TrickException {

		if (Double.isNaN(x)) {
			System.err.println("A nan was detected X: " + x);
			return new EvaluationResult(0, 0);
		}

		// System.out.println("Caractere: " + x);

		// compute importance repport
		double rapportImportance = importance / (currentImpact * currentProbability);

		if (Double.isNaN(rapportImportance)) {
			System.err.println("A nan was detected rapportImportance: " + importance + "/ (" + currentImpact + "*" + currentProbability + ")");
			return new EvaluationResult(0, 0);
		}

		// retirve likelihood level
		double probability = currentProbability * Math.pow(rapportImportance, x);

		/*
		 * if (probability > 100) { System.out.println("X: " + x + " netImpact: " + currentImpact +
		 * " netProbability: " + currentProbability + " Input Importance: " + importance +
		 * " Rapport importance: " + rapportImportance + " probability: " + probability); }
		 */

		if (Double.isNaN(probability)) {
			System.err.println("A nan was detected probability: " + probability);
			return new EvaluationResult(0, 0);
		}

		// retrieve impact level
		double impact = currentImpact * Math.pow(rapportImportance, 1.0 - x);

		if (Double.isNaN(impact)) {
			System.err.println("A nan was detected impact: " + impact);
			return new EvaluationResult(0, 0);
		}

		// return the evaluation result
		return new EvaluationResult(probability, impact);
	}

	public Analysis getAnalysis() {
		return analysis;
	}
}