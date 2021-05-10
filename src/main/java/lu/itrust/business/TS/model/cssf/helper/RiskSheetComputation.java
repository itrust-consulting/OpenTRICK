/**
 * @author eomar
 *
 */
package lu.itrust.business.TS.model.cssf.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.helper.ActionPlanComputation;
import lu.itrust.business.TS.model.actionplan.helper.TMA;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.cssf.EvaluationResult;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;
import lu.itrust.business.TS.model.cssf.tools.CSSFSort;
import lu.itrust.business.TS.model.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.general.SecurityCriteria;
import lu.itrust.business.TS.model.parameter.ILevelParameter;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;

/**
 * RiskSheetComputation: <br>
 * Computes NET Evaluation, RAW Evaluation and Expected Importance.
 * 
 * @author itrust consulting s.�.rl. : BJA, EOM, SME
 * @version 0.1
 * @since 1/04/2016
 */
public class RiskSheetComputation {

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	/** Analysis Object */
	private Analysis analysis = null;

	private ValueFactory factory;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor: <br>
	 * Creates the object with analysis and databasehander as parameter.
	 * 
	 * @param analysis   The Analysis Object
	 * @param cssfFilter
	 */
	public RiskSheetComputation(Analysis analysis) {
		this.analysis = analysis;
	}

	/**
	 * computeRiskRegister: <br>
	 * Calculates the Risk Register and stores it inside the Analysis Object field
	 * "riskRegister". This field is of the Type List of RiskRegisterItem.
	 * 
	 * @return An object of MessageHandler which is null when no errors where made,
	 *         or it contains an exception
	 */
	public MessageHandler computeRiskRegister() {

		// create a messagehandler object
		ComputationHelper helper = null;
		try {

			System.out.println("Risk Register calculation...");
			List<ILevelParameter> impactParameters = this.analysis.getImpactParameters().stream().collect(Collectors.toList());
			this.analysis.getLikelihoodParameters().forEach(probability -> impactParameters.add(probability));
			CSSFFilter filter = new CSSFFilter();
			int mandatoryPhase = 0, impactThreshold = Constant.CSSF_IMPACT_THRESHOLD_VALUE, probabilityThreshold = Constant.CSSF_PROBABILITY_THRESHOLD_VALUE;
			for (IParameter parameter : this.analysis.getSimpleParameters()) {
				if (parameter.isMatch(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.MANDATORY_PHASE))
					mandatoryPhase = (int) parameter.getValue().intValue();
				else if (parameter.isMatch(Constant.PARAMETERTYPE_TYPE_CSSF_NAME, Constant.CSSF_CIA_SIZE))
					filter.setCia((int) parameter.getValue().intValue());
				else if (parameter.isMatch(Constant.PARAMETERTYPE_TYPE_CSSF_NAME, Constant.CSSF_DIRECT_SIZE))
					filter.setDirect((int) parameter.getValue().intValue());
				else if (parameter.isMatch(Constant.PARAMETERTYPE_TYPE_CSSF_NAME, Constant.CSSF_INDIRECT_SIZE))
					filter.setIndirect((int) parameter.getValue().intValue());
				else if (parameter.isMatch(Constant.PARAMETERTYPE_TYPE_CSSF_NAME, Constant.CSSF_IMPACT_THRESHOLD))
					impactThreshold = (int) parameter.getValue().intValue();
				else if (parameter.isMatch(Constant.PARAMETERTYPE_TYPE_CSSF_NAME, Constant.CSSF_PROBABILITY_THRESHOLD))
					probabilityThreshold = (int) parameter.getValue().intValue();
			}
			// ****************************************************************
			// * calculate RiskRegister using CSSFComputation
			// ****************************************************************
			helper = new ComputationHelper(impactParameters);
			filter.setImpact(impactThreshold);
			filter.setProbability(probabilityThreshold);
			setFactory(helper.getFactory());
			this.analysis.setRiskRegisters(CSSFComputation(this.analysis.getAssessments(), generateTMAs(analysis, helper.getFactory(), mandatoryPhase), helper, filter));
			// print risk register into console
			// printRegister(this.analysis.getRiskRegisters());
			return null;
		} catch (Exception e) {
			// print error message
			System.out.println("Risk Register calculation and saving failed!");
			TrickLogManager.Persist(e);
			return new MessageHandler(e);
		} finally {
			if (helper != null) {
				helper.destroy();
			}
		}
	}

	/***********************************************************************************************
	 * Methods
	 **********************************************************************************************/

	public MessageHandler computeRiskRegister(CSSFFilter filter) {
		// create a messagehandler object
		ComputationHelper helper = null;
		try {
			System.out.println("Risk Register calculation...");
			List<ILevelParameter> impactParameters = this.analysis.getImpactParameters().stream().collect(Collectors.toList());
			this.analysis.getLikelihoodParameters().forEach(probability -> impactParameters.add(probability));
			int mandatoryPhase = this.analysis.getSimpleParameters().stream()
					.filter(parameter -> parameter.isMatch(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.MANDATORY_PHASE)).map(SimpleParameter::getValue).findAny().orElse(0D)
					.intValue();
			// ****************************************************************
			// * calculate RiskRegister using CSSFComputation
			// ****************************************************************
			helper = new ComputationHelper(impactParameters);
			setFactory(helper.getFactory());
			this.analysis.setRiskRegisters(CSSFComputation(this.analysis.getAssessments(), generateTMAs(analysis, helper.getFactory(), mandatoryPhase), helper, filter));
			return null;
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return new MessageHandler(e);
		} finally {
			if (helper != null) {
				helper.destroy();
			}
		}
	}

	/***********************************************************************************************
	 * Print on Screen
	 **********************************************************************************************/

	public List<TMA> generateTMAs(Analysis analysis, ValueFactory factory, int mandatoryPhase) throws TrickException {
		final List<TMA> tmas = new ArrayList<TMA>();

		final List<Phase> usePhases = analysis.getPhases();

		final List<Measure> useMeasures = new ArrayList<Measure>();

		final List<AnalysisStandard> analysisStandards = analysis.findAllAnalysisStandard();

		for (int i = 0; i < usePhases.size(); i++) {
			if (usePhases.get(i).getNumber() == 0)
				continue;
			tmas.addAll(
					ActionPlanComputation.generateTMAList(this.analysis, factory, useMeasures, ActionPlanMode.APN, usePhases.get(i).getNumber(), true, true, analysisStandards));
			if (mandatoryPhase == usePhases.get(i).getNumber())
				break;
		}
		return tmas;

	}

	public Analysis getAnalysis() {
		return analysis;
	}

	/**
	 * @return the factory
	 */
	public ValueFactory getFactory() {
		return factory;
	}

	/***********************************************************************************************
	 * Main Method
	 **********************************************************************************************/

	/**
	 * @param factory the factory to set
	 */
	public void setFactory(ValueFactory factory) {
		this.factory = factory;
	}

	/**
	 * computeImpactAndProbability: <br>
	 * Computes "Expected Importance" and update riskRegisters.<br />
	 * Computes X, Pa and Ia:
	 * <ul>
	 * <li>Key = Scenario id</li>
	 * <li>Pc = riskRegisters[key].netEvaluation.Probability</li>
	 * <li>Ic = riskRegisters[key].netEvaluation.Impact</li>
	 * <li>Ra = importance</li>
	 * <li>X = probabilityRelativeImpacts[key][0]
	 * /probabilityRelativeImpacts[Key][1]</li>
	 * <li>Pa = Pc*( Ra /( Pc * Ic ) ) ^ X</li>
	 * <li>Ia = Ra / Pa</li>
	 * </ul>
	 *
	 * @param riskRegisters              the Risk Register Item
	 * @param probabilityRelativeImpacts the list of probability relative impacts
	 * @throws TrickException
	 */
	public static EvaluationResult computeImpactAndProbability(final double x, final double currentImpact, final double currentProbability, final double importance)
			throws TrickException {

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

	/**
	 * CSSFComputation: <br>
	 * This method does the following CSSF computations:
	 * <ul>
	 * <li>{@link #netEvaluationComputation(Map, List, List) Net Evaluation
	 * computation}</li>
	 * <li>{@link #rawEvaluationComputation(Map, Map, List, List) Raw importance
	 * computation}</li>
	 * <li>{@link #expectedImportanceComputation(Map, Map, List, List) Expected
	 * importance computation}</li>
	 * </ul>
	 * After these calculations, the Risk Register will be sorted by category in
	 * order to identify the following:
	 * <ul>
	 * <li>20 most important direct risk(referring to the net importance value)</li>
	 * <li>5 most important indirect risk(referring to the net importance value)
	 * </li>
	 * <li>Risks that have a net impact >=6 and net impact probability >= 5</li>
	 * </ul>
	 * 
	 * @param assessments The List of Assessments
	 * @param tmas        The List of TMA Entries
	 * @param helper      The List of Parameters
	 * @param cssfFilter
	 * @return The Risk Register as a List of RiskRegisterItems
	 * @throws TrickException
	 * 
	 * @see CSSFTools#sortAndConcatenateGroup(Map)
	 */
	public static List<RiskRegisterItem> CSSFComputation(final List<Assessment> assessments, final List<TMA> tmas, final ComputationHelper helper, CSSFFilter cssfFilter)
			throws TrickException {
		if (cssfFilter == null)
			cssfFilter = new CSSFFilter(6, 5);
		// calculate the NET Evaluation
		// set the impacts of each category (this will parse all assessment and
		// will make a sum of
		// each impact category (inside the Impact class) for each Scenario)
		// ********************************************************
		// * second step: For each Scenario identify the biggest Impact Category
		// ********************************************************
		// calculates the ALE inside the netALE array (using the biggest Impact
		// and probability) for
		// each Scenario and initialise the netEvaluation result (prepare the
		// Array with size)
		computeNetALE(helper, assessments);
		// calculate RawALEs, DeltaALEs and Probability Relative Impacts
		computeRawALEAndDeltaALEAndProbabilityRelativeImpacts(helper, tmas);
		// compute
		cssfFinalComputation(helper);
		// Concatenate direct and indirect using 20 direct and those with
		// acceptable impact and
		// probability as well as 5 indirect and those with acceptable impact
		// and probability
		return CSSFSort.sortAndConcatenate(helper, cssfFilter);
	}

	/**
	 * expectedImportanceComputation: <br>
	 * update expected importance in riskRegisters.<br>
	 * steps:
	 * <ul>
	 * <li>{@link #computeProbabilityRelativeImpact(List, List) Computation of
	 * Probability Relative Impact}</li>
	 * <li>{@link #computeExpectedImportance(Map, Map) Merge All}</li>
	 * </ul>
	 * 
	 * @param riskRegisters             The Risk Register Item
	 * @param netImpact                 The netImpact value
	 * @param netProbability            The netProbability value
	 * @param deltaALE                  The delta ALE List
	 * @param netALE                    The net ALE List
	 * @param probabilityRelativeImpact The probability relative impact list
	 * @throws TrickException
	 */
	public static void expectedImportanceComputation(RiskRegisterItem riskRegisters, final Double deltaALE, final double netALE, final double[] probabilityRelativeImpact)
			throws TrickException {

		// retrieve net impact
		double netImpact = riskRegisters.getNetEvaluation().getImpact();
		// retrieve net importance
		double netProbability = riskRegisters.getNetEvaluation().getProbability();
		// retrieve net importance
		double importanceNet = riskRegisters.getNetEvaluation().getImportance();

		if (deltaALE == null) {
			riskRegisters.setExpectedEvaluation(new EvaluationResult(netProbability, netImpact));
			return;
		}

		// compute quantitative value
		// double quantitativeValue = netALE / importanceNet;

		// compute delta Importance
		// double deltaImp = deltaALE / quantitativeValue;

		// compute expected importance
		double expImportance = importanceNet - deltaALE;

		if (expImportance < 0) {
			riskRegisters.setExpectedEvaluation(new EvaluationResult(0, 0));
			System.err.println("Expected importance " + expImportance + "<0 for " + riskRegisters.getScenario().getName());
		} else {
			// computation of proportional strength
			double x = probabilityRelativeImpact[0] / probabilityRelativeImpact[1];

			// update expected importance
			riskRegisters.setExpectedEvaluation(computeImpactAndProbability(x, netImpact, netProbability, expImportance));
		}
	}

	public static void print(RiskRegisterItem registerItem) {
		System.out.println("--------------------------------------------------------------------" + "----------------------------------------");
		System.out.print(registerItem.getId() + " | " + registerItem.getScenario().getId() + " | " + registerItem.getAsset().getId() + " | "
				+ registerItem.getScenario().getType().getName() + " | " + registerItem.getScenario().getName());
		printRiskRegisterItem(registerItem.getRawEvaluation());
		printRiskRegisterItem(registerItem.getNetEvaluation());
		printRiskRegisterItem(registerItem.getExpectedEvaluation());
		System.out.println();
	}

	/***********************************************************************************************
	 * NET EVALUATION - END
	 **********************************************************************************************/

	/***********************************************************************************************
	 * RAW EVALUATION - BEGIN
	 **********************************************************************************************/

	/**
	 * printRegister: <br>
	 * Prints the Risk Register Items into the console.
	 * 
	 * @param registers The Risk Register to print on console
	 */
	public static void printRegister(List<RiskRegisterItem> registers) {

		// check if register is not empty
		if (registers == null || registers.isEmpty())
			return;
		// print header line
		System.out.println("ID | Position | idScenario | idAsset | Cat. | Risk title  |  RAW evaluation " + "|Net Evaluation | Expected Importance | Response stategy");
		// print each risk register item
		for (RiskRegisterItem registerItem : registers)
			print(registerItem);
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

	/**
	 * rawEvaluationComputation: <br>
	 * Compute Raw evaluation. for a given risk register.
	 * 
	 * @param riskRegisters              The Risk Register
	 * @param netALEs                    The calculated Net ALE list
	 * @param rawALEs                    The calculated Raw ALE list
	 * @param probabilityRelativeImpacts the calculated relativeimpact list
	 * @throws TrickException
	 * @see #computeRawALE(Map, Map, List, List)
	 */
	public static void rawEvaluationComputation(RiskRegisterItem riskRegisters, final double netALE, final Double rawALE, double[] probabilityRelativeImpact)
			throws TrickException {

		// retrieve net impact
		double netImpact = riskRegisters.getNetEvaluation().getImpact();

		// retrieve net importance
		double netProbability = riskRegisters.getNetEvaluation().getProbability();

		// retrieve net importance
		// double netImportance =
		// riskRegisters.getNetEvaluation().getImportance();

		// compute scale point of importance
		// double impScalePoint = netALE / netImportance;

		if (rawALE == null) {
			riskRegisters.setRawEvaluation(new EvaluationResult(netProbability, netImpact));
			return;
		}

		/**
		 * Computes "Expected Importance" and update riskRegisters.<br />
		 * Computes X, Pa and Ia:
		 * <ul>
		 * <li>Key = Scenario id</li>
		 * <li>Pc = riskRegisters[key].netEvaluation.Probability</li>
		 * <li>Ic = riskRegisters[key].netEvaluation.Impact</li>
		 * <li>Ra = rawImportance</li>
		 * <li>X = probabilityRelativeImpacts[key][0] /
		 * probabilityRelativeImpacts[Key][1]</li>
		 * <li>Pa = Pc*( Ra /( Pc * Ic ) ) ^ X</li>
		 * <li>Ia = Ra / Pa</li>
		 * </ul>
		 */
		// computation of proportional strength
		double x = probabilityRelativeImpact[0] / probabilityRelativeImpact[1];

		// update raw evaluation
		riskRegisters.setRawEvaluation(computeImpactAndProbability(x, netImpact, netProbability, rawALE));
	}

	/**
	 * computeDeltaALEs: <br>
	 * Sum deltaALE for a given TMA and store inside deltaALEs parameter.
	 * 
	 * @param deltaALEs  The given List to store the Sum of DeltaALE of given TMA
	 *                   entry
	 * @param tma        The TMA entry to retrieve delta ALE from
	 * @param parameters The List of Parameters
	 * @throws TrickException
	 */
	private static void computeDeltaALEs(Map<String, Double> deltaALEs, double ALE, final TMA tma, String key, ValueFactory valueFactory) throws TrickException {

		double currentDeltaALE = TMA.calculateDeltaALE(deltaALEs.containsKey(key) ? ALE - deltaALEs.get(key) : ALE, tma.getRRF(), tma.getMeasure(), valueFactory);
		// retireve existing summed value of this scneario and add current
		// deltaALE, if none exist
		// use the current as new value
		deltaALEs.put(key, deltaALEs.containsKey(key) ? deltaALEs.get(key) + currentDeltaALE : currentDeltaALE);
		// update deltaALE
	}

	/***********************************************************************************************
	 * RAW EVALUATION - END
	 **********************************************************************************************/

	/***********************************************************************************************
	 * Expected Importance EVALUATION - BEGIN
	 **********************************************************************************************/

	/**
	 * computeNetALE: <br>
	 * Calculate the ALE (Pnet*Inet) of each Scenario and Sum them together to build
	 * the nominator of the Formula "Pgen=(sum(P*I))/(sum(Imax)))" Reference in the
	 * CSSF document section 3.1.1.
	 * 
	 * @param netALEs     The List of netALE's for each Scenario (numerator)
	 * @param impacts     The List of Impacts for each Scenario (Impact List)
	 * @param assessments The Assessments List
	 * @param parameters  The parameters List
	 * 
	 * @return The initialised list of the risk register
	 * @throws TrickException
	 */
	private static void computeNetALE(ComputationHelper helper, List<Assessment> assessments) {
		// parse all assessments elements
		for (Assessment assessment : assessments) {
			// get scenario id of the assessments scenario
			// check if impacts list has this key (key of impacts= scenario ID)
			if (assessment.isUsable()) {
				String key = getKey(assessment);
				// update ALE numerator
				RiskRegisterItem riskRegisterItem = new RiskRegisterItem(assessment.getScenario(), assessment.getAsset());
				riskRegisterItem.getNetEvaluation().setImpact(helper.getFactory().findRealValue(assessment.getImpacts()));
				if (assessment.getLikelihood() == null)
					riskRegisterItem.getNetEvaluation().setProbability(0d);
				else
					riskRegisterItem.getNetEvaluation().setProbability(assessment.getLikelihood().getReal());
				helper.getNetALEs().put(key, riskRegisterItem.getNetEvaluation().getImportance());
				helper.getRiskRegisters().put(key, riskRegisterItem);
			}
		}
	}

	/**
	 * computeProbabilityRelativeImpact: <br>
	 * Final goal, compute X = (Preventive + 0,5* Detective) / (Preventive +
	 * Detective + Limitative + Preventive).<br />
	 * Here, we compute numerator and denominator.
	 * <ul>
	 * <li>Key = Scenario id</li>
	 * <li>Numerator = value[0]</li>
	 * <li>Denominator = value[1]</li>
	 * </ul>
	 * 
	 * @return Map(String,double[2]) where key = scenario name, value[0] = Numerator
	 *         and value[1] = Denominator
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
	 * computeRawALE: <br>
	 * Compute the Raw ALE and store the value inside the given rawALEs list.
	 * 
	 * @param rawALEs    The List of raw ALEs
	 * @param netALEs    The List of net ALEs
	 * @param tma        The TMA Entry
	 * @param parameters The SimpleParameter List
	 */
	private static void computeRawALE(Map<String, Double> rawALEs, final Map<String, Double> netALEs, final TMA tma, String key, ValueFactory valueFactory) {

		// Retrieve current rawALE of the scenario ID if it does not exist take
		// the start value of
		// the netALE of this scenario
		double rawALE = rawALEs.containsKey(key) ? rawALEs.get(key) : netALEs.get(key);
		// Retrieve implementation rate of the measure and transform it to
		// percentage
		double ImplementationRate = tma.getMeasure().getImplementationRateValue(valueFactory) * 0.01;

		// calculate new RAW ALE using formula
		rawALE /= (1.0 - tma.getRRF() * ImplementationRate);
		/*
		 * System.out.println(tma.getAssessment().getScenario().getName() + ", RRF: " +
		 * tma.getRRF() + ", ImplementationRate: " + ImplementationRate);
		 */
		// update rawALE
		rawALEs.put(key, rawALE);
	}

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
	 * @param tmas                       List of TMA entries generated from the
	 *                                   Analysis
	 * @param probabilityRelativeImpacts output
	 * @param impacts
	 * @param rawALEs                    List to store and calculate RAW ALE's for a
	 *                                   affecting scenario category
	 * @param deltaALEs                  List to store and calculate delta ALE's for
	 *                                   a affecting scenario category
	 * @param netALEs                    List of calculated netALE nominators
	 *                                   (calculated inside netEvaluation)
	 * @param parameters                 List of Parameters of Analysis
	 * 
	 * @throws TrickException
	 * 
	 * @see #computeDeltaALEs(Map, Map, TMA, List)
	 * @see #computeRawALE(Map, Map, TMA, List)
	 * @see #computeProbabilityRelativeImpact(Map, Scenario, Measure)
	 */
	private static void computeRawALEAndDeltaALEAndProbabilityRelativeImpacts(ComputationHelper helper, List<TMA> tmas) {

		// parse all TMA entries
		tmas.forEach(tma -> {

			// store scenario
			Scenario scenario = tma.getAssessment().getScenario();

			// determine category
			String category = CategoryConverter.getTypeFromScenario(scenario);

			boolean hasinfluence = false;

			if (tma.getMeasure() instanceof AbstractNormalMeasure)
				hasinfluence = ((AbstractNormalMeasure) tma.getMeasure()).getMeasurePropertyList().hasInfluenceOnCategory(category);

			// check if measure influences this scenario category -> YES
			if (hasinfluence) {
				// Integer: The Scenario ID, Integer : Code that defines the
				// biggest Category 0:
				// reputation, 1: operational, 2: legal, 3: financial

				String key = getKey(tma.getAssessment());
				// compute deltaALE
				computeDeltaALEs(helper.getDeltaALEs(), helper.getRiskRegisters().get(key).getNetEvaluation().getImportance(), tma, key, helper.getFactory());
				// compute RawALE
				computeRawALE(helper.getRawALEs(), helper.getNetALEs(), tma, key, helper.getFactory());

				// Compute Relative Probability and Relative Impact
				computeProbabilityRelativeImpact(helper.getProbabilityRelativeImpacts(), key, scenario);
			}

		});

	}

	/**
	 * cssfFinalComputation: <br>
	 * Computes last nessesary computations on raw and excepted evaluation.
	 * 
	 * @param riskRegisters              The Risk Register to compute
	 * @param netALEs                    The netALE List
	 * @param impacts                    The Impacts List
	 * @param probabilityRelativeImpacts The probability relative impacts
	 * @param rawALEs                    The raw ALE List
	 * @param deltaALEs                  The delta ALE List
	 * @param parameters                 The Parameters List
	 * @throws TrickException
	 */
	private static void cssfFinalComputation(ComputationHelper helper) {

		// parse all risk register items

		helper.getRiskRegisters().forEach((key, registerItem) -> {
			// extract the scenario ID

			// retrieve for this scenario the netALE value
			double netALE = helper.getNetALEs().get(key);

			// retrieve for this scenario the deltaALE value
			Double deltaALE = helper.getDeltaALEs().get(key);

			// retrieve for this scenario the probability relative Impacts value
			double[] probabilityRelativeImpact = helper.getProbabilityRelativeImpacts().get(key);

			// retrieve for this scenario the rawALE value
			Double rawALE = helper.getRawALEs().get(key);

			// calculate the last step of expected Evaluation
			expectedImportanceComputation(registerItem, deltaALE, netALE, probabilityRelativeImpact);

			// calculate the last step of raw evaluation Evaluation
			rawEvaluationComputation(registerItem, netALE, rawALE, probabilityRelativeImpact);
		});

	}

	/***********************************************************************************************
	 * NET EVALUATION - BEGIN
	 **********************************************************************************************/

	private static String getKey(Assessment assessment) {
		return assessment.getScenario().getId() + "_" + assessment.getAsset().getId();
	}
}