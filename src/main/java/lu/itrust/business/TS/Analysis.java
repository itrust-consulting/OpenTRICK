package lu.itrust.business.TS;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.ActionPlanMode;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.TS.cssf.RiskRegisterItem;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.User;

/**
 * Analysis: <br>
 * This class represents an analysis and all its data of TRICK Service. This class is used to store
 * analysis data such as assets, scenarios, security measures, item information, risk information,
 * the version, parameters and phases. After the data is stored, the action plan can be computed
 * within this class as well as the Action Plan Summary.
 * <ul>
 * <li>import Analysis from SQLite file</li>
 * <li>store analysis in java object to use during the calculations</li>
 * <li>calculate all Action Plans (Normal, optimistic, pessimistic, phase)</li>
 * <li>calculate Risk Register</li>
 * <li>Export a specific Analysis</li>
 * </ul>
 * 
 * 
 * @author itrust consulting s.� r.l. - SME,BJA
 * @version 0.1
 * @since 2012-08-21
 */
public class Analysis implements Serializable, Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** Analysis id unsaved value = -1 */
	private int id = -1;

	private boolean profile = false;

	private boolean defaultProfile = false;

	/** The Customer object */
	private Customer customer;

	/** ID of the Analysis */
	private String identifier;

	/** Version of the Analysis */
	private String version;

	/** Creation Date of the Analysis (and a specific version) */
	private Timestamp creationDate;

	/** Analysis owner (the one that created or imported it) */
	private User owner;

	/** Based on analysis */
	private Analysis basedOnAnalysis;

	/** The Label of this Analysis */
	private String label;

	/** Language object of the Analysis */
	private Language language;

	/** flag to determine if analysis has data */
	private boolean data;

	/** List of users and their access rights */
	private List<UserAnalysisRight> userRights = new ArrayList<UserAnalysisRight>();

	/** List of History data of the Analysis */
	private List<History> histories = new ArrayList<History>();

	/** List of Item Information */
	private List<ItemInformation> itemInformations = new ArrayList<ItemInformation>();

	/** List of parameters */
	private List<Parameter> parameters = new ArrayList<Parameter>();

	/** List of assets */
	private List<Asset> assets = new ArrayList<Asset>();

	/** List of Risk Information */
	private List<RiskInformation> riskInformations = new ArrayList<RiskInformation>();

	/** List of Scenarios */
	private List<Scenario> scenarios = new ArrayList<Scenario>();

	/** List of Assessment */
	private List<Assessment> assessments = new ArrayList<Assessment>();

	/** List of Norms */
	private List<AnalysisNorm> analysisNorms = new ArrayList<AnalysisNorm>();

	/** List of Phases that is used for Action Plan Computation */
	private List<Phase> usedPhases = new ArrayList<Phase>();

	/** The Final Action Plan without Phase Computation - Normal */
	private List<ActionPlanEntry> actionPlans = new ArrayList<ActionPlanEntry>();

	/** The Action Plan Summary without Phase Computation - Normal */
	private List<SummaryStage> summaries = new ArrayList<SummaryStage>();

	/** The Action Plan Summary with Phase Computation Pessimistic */
	private List<RiskRegisterItem> riskRegisters = new ArrayList<RiskRegisterItem>();

	/***********************************************************************************************
	 * Constructor
	 **********************************************************************************************/

	/**
	 * Constructor: <br>
	 * This constructor to create a new Analysis.
	 */
	public Analysis() {
	}

	/***********************************************************************************************
	 * Methods
	 **********************************************************************************************/

	/**
	 * initialiseEmptyItemInformation: <br>
	 * Description
	 * 
	 * @param analysis
	 */
	public static final void initialiseEmptyItemInformation(Analysis analysis) {

		if (analysis == null)
			return;

		analysis.getItemInformations().clear();
		ItemInformation iteminfo;
		iteminfo = new ItemInformation(Constant.TYPE_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.TYPE_PROFIT_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.NAME_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.PRESENTATION_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.SECTOR_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.RESPONSIBLE_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.STAFF_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.ACTIVITIES_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.EXCLUDED_ASSETS, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.OCCUPATION, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.FUNCTIONAL, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.JURIDIC, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.POL_ORGANISATION, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.MANAGEMENT_ORGANISATION, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.PREMISES, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.REQUIREMENTS, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.EXPECTATIONS, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.ENVIRONMENT, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.INTERFACE, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.STRATEGIC, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.PROCESSUS_DEVELOPMENT, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.STAKEHOLDER_IDENTIFICATION, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.ROLE_RESPONSABILITY, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.STAKEHOLDER_RELATION, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.ESCALATION_WAY, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
		iteminfo = new ItemInformation(Constant.DOCUMENT_CONSERVE, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.addAnItemInformation(iteminfo);
	}

	/**
	 * getALEOfAsset: <br>
	 * Returns the Sum of ALE from Assessments for the given Asset.
	 * 
	 * @param asset
	 *            The Asset to get ALE from
	 * @return The Total ALE of the Asset
	 */
	public double getALEOfAsset(Asset asset) {

		// initialise return value
		double result = 0;

		// check if asset exists and if assessments are not empty
		if (asset == null)
			throw new IllegalArgumentException("error.ale.asset_null");
		if (this.assessments.isEmpty())
			throw new IllegalArgumentException("error.ale.Assessments_empty");
		// parse assessments
		for (Assessment assessment : assessments) {

			// check if current Asset equals Asset parameter
			if (assessment.getAsset().equals(asset)) {

				// sum the ALE value
				result += assessment.getALE();
			}
		}

		// return the result
		return result;
	}

	/**
	 * getLatestVersion: <br>
	 * Parse all history entries to find latest version (version has to be of format xx.xx.xx)
	 * 
	 * @return
	 */
	public String getLatestVersion() {

		Integer v = 0;

		String finalVersion = "";

		for (int i = 0; i < histories.size(); i++) {
			Integer t = 0;
			String version = histories.get(i).getVersion();
			String[] splittedVerison = version.split("\\.");
			t = (Integer.valueOf(splittedVerison[0])) + (Integer.valueOf(splittedVerison[1])) + (Integer.valueOf(splittedVerison[2]));
			if (v < t) {
				v = t;
				finalVersion = version;
			}
		}

		return finalVersion;

	}

	/**
	 * versionExists: <br>
	 * Checks if given version string exists in analysis
	 * 
	 * @param version
	 * @return
	 */
	public boolean versionExists(String version) {
		boolean res = false;

		for (int i = 0; i < histories.size(); i++) {
			if (histories.get(i).getVersion().equals(version)) {
				res = true;
				break;
			}
		}

		return res;
	}

	/***********************************************************************************************
	 * RRF - BEGIN
	 **********************************************************************************************/

	/**
	 * calculateRRF: <br>
	 * Calculates the RRF (Risk Reduction Factor) using the Formulas from a given measure, given
	 * Scenario and given Asset (asset and scenario together: assessment) values.
	 * 
	 * @param tmpAssessment
	 *            The Assessment to take Values to calculate
	 * @param parameters
	 *            The Parameters List
	 * @param measure
	 *            The Measure to take Values to calculate
	 * 
	 * @return The Calculated RRF
	 */
	public static double calculateRRF(Assessment tmpAssessment, List<Parameter> parameters, NormMeasure measure) {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		int assetTypeValue = 0;
		double tuning = 0;
		double strength = 0;
		double category = 0;
		double type = 0;
		double source = 0;
		double RRF = 0;

		// ****************************************************************
		// * retrieve tuning value
		// ****************************************************************

		// parse parameters
		for (int i = 0; i < parameters.size(); i++) {

			// check if parameter is tuning -> YES
			if ((parameters.get(i).getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME)) && (parameters.get(i).getDescription().equals(Constant.PARAMETER_TUNING))) {

				// ****************************************************************
				// * store tuning value
				// ****************************************************************
				tuning = parameters.get(i).getValue();

				// leave loop when found
				break;
			}
		}

		// ****************************************************************
		// * retrieve asset type value for this asset type
		// * (inside assessment)
		// ****************************************************************

		// parse assettype value list from given measure
		for (int atvc = 0; atvc < measure.getAssetTypeValues().size(); atvc++) {

			// check if asset type of measure matches asset type of assessment
			if (measure.getAssetTypeValue(atvc).getAssetType().getType().equals(tmpAssessment.getAsset().getAssetType().getType())) {

				// ****************************************************************
				// * store assetTypevalue
				// ****************************************************************
				assetTypeValue = measure.getAssetTypeValue(atvc).getValue();
				// System.out.println("Measure: " +
				// measure.getMeasureDescription().getReference() +
				// ":: Asset Type Value:" + assetTypeValue);

				// leave loop
				break;
			}
		}

		// ****************************************************************
		// * Strength calculation
		// ****************************************************************
		strength = measure.getMeasurePropertyList().getFMeasure();
		strength = strength * measure.getMeasurePropertyList().getFSectoral();
		strength = strength / 40.;

		// ****************************************************************
		// * Category calculation
		// ****************************************************************
		category = calculateRRFCategory(measure.getMeasurePropertyList(), tmpAssessment.getScenario());

		// ****************************************************************
		// * Type calculation
		// ****************************************************************
		type =
			((measure.getMeasurePropertyList().getLimitative() * tmpAssessment.getScenario().getLimitative())
				+ (measure.getMeasurePropertyList().getPreventive() * tmpAssessment.getScenario().getPreventive())
				+ (measure.getMeasurePropertyList().getDetective() * tmpAssessment.getScenario().getDetective()) + (measure.getMeasurePropertyList().getCorrective() * tmpAssessment
					.getScenario().getCorrective())) / 4.;

		// ****************************************************************
		// * Source calculation
		// ****************************************************************
		source =
			(measure.getMeasurePropertyList().getIntentional() * tmpAssessment.getScenario().getIntentional())
				+ (measure.getMeasurePropertyList().getAccidental() * tmpAssessment.getScenario().getAccidental())
				+ (measure.getMeasurePropertyList().getEnvironmental() * tmpAssessment.getScenario().getEnvironmental())
				+ (measure.getMeasurePropertyList().getInternalThreat() * tmpAssessment.getScenario().getInternalThreat())
				+ (measure.getMeasurePropertyList().getExternalThreat() * tmpAssessment.getScenario().getExternalThreat());

		source =
			source
				/ (4. * (double) (tmpAssessment.getScenario().getIntentional() + tmpAssessment.getScenario().getAccidental() + tmpAssessment.getScenario().getEnvironmental()
					+ tmpAssessment.getScenario().getInternalThreat() + tmpAssessment.getScenario().getExternalThreat()));

		// ****************************************************************
		// * RRF completion :
		// * (((Asset_Measure/100)*Strength*CID*Type*Source) / 500) * tuning
		// ****************************************************************

		RRF = ((assetTypeValue / 100. * strength * category * type * source) / 500.) * tuning;

		// if
		// ((measure.getMeasureDescription().getReference().equals("A.9.2.2")))
		// {
		// System.out.println("Measure: " +
		// measure.getMeasureDescription().getReference() +
		// "Asset: " + tmpAssessment.getAsset().getName() + "Scenario: " +
		// tmpAssessment.getScenario().getName() + " ;RRF=" + RRF + ", atv=" +
		// assetTypeValue +
		// ", strength=" + strength + ", Category=" + category + ", type=" +
		// type + ", source=" +
		// source + ", tuning=" + tuning);
		// }

		// ****************************************************************
		// * return the value
		// ****************************************************************
		return RRF;
	}

	/**
	 * calculateRRF: <br>
	 * Calculates the RRF (Risk Reduction Factor) using the Formulas from a given measure, given
	 * Scenario and given Asset (asset and scenario together: assessment) values.
	 * 
	 * @param scenario
	 *            The scenario to take Values to calculate
	 * @param assetType
	 *            The assetType to take Values to calculate
	 * @param parameter
	 *            The tuning parameter
	 * @param measure
	 *            The Measure to take Values to calculate
	 * @return The Calculated RRF
	 */
	public static double calculateRRF(Scenario scenario, AssetType assetType, Parameter parameter, NormMeasure measure) {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		int assetTypeValue = 0;
		double tuning = 0;
		double strength = 0;
		double category = 0;
		double type = 0;
		double source = 0;
		double RRF = 0;

		// ****************************************************************
		// * retrieve tuning value
		// ****************************************************************

		if (parameter != null)
			tuning = parameter.getValue();

		// ****************************************************************
		// * retrieve asset type value for this asset type
		// * (inside assessment)
		// ****************************************************************

		// parse assettype value list from given measure
		for (int atvc = 0; atvc < measure.getAssetTypeValues().size(); atvc++) {

			// check if asset type of measure matches asset type of assessment
			if (measure.getAssetTypeValue(atvc).getAssetType().equals(assetType)) {

				// ****************************************************************
				// * store assetTypevalue
				// ****************************************************************
				assetTypeValue = measure.getAssetTypeValue(atvc).getValue();
				// System.out.println("Measure: " +
				// measure.getMeasureDescription().getReference() +
				// ":: Asset Type Value:" + assetTypeValue);

				// leave loop
				break;
			}
		}

		// ****************************************************************
		// * Strength calculation
		// ****************************************************************
		strength = measure.getMeasurePropertyList().getFMeasure();
		strength = strength * measure.getMeasurePropertyList().getFSectoral();
		strength = strength / 40.;

		// ****************************************************************
		// * Category calculation
		// ****************************************************************
		category = calculateRRFCategory(measure.getMeasurePropertyList(), scenario);

		// ****************************************************************
		// * Type calculation
		// ****************************************************************
		type =
			((measure.getMeasurePropertyList().getLimitative() * scenario.getLimitative()) + (measure.getMeasurePropertyList().getPreventive() * scenario.getPreventive())
				+ (measure.getMeasurePropertyList().getDetective() * scenario.getDetective()) + (measure.getMeasurePropertyList().getCorrective() * scenario.getCorrective())) / 4.;

		// ****************************************************************
		// * Source calculation
		// ****************************************************************
		source =
			(measure.getMeasurePropertyList().getIntentional() * scenario.getIntentional()) + (measure.getMeasurePropertyList().getAccidental() * scenario.getAccidental())
				+ (measure.getMeasurePropertyList().getEnvironmental() * scenario.getEnvironmental()) + (measure.getMeasurePropertyList().getInternalThreat() * scenario.getInternalThreat())
				+ (measure.getMeasurePropertyList().getExternalThreat() * scenario.getExternalThreat());

		source = source / (4. * (double) (scenario.getIntentional() + scenario.getAccidental() + scenario.getEnvironmental() + scenario.getInternalThreat() + scenario.getExternalThreat()));

		// ****************************************************************
		// * RRF completion :
		// * (((Asset_Measure/100)*Strength*CID*Type*Source) / 500) * tuning
		// ****************************************************************

		RRF = ((assetTypeValue / 100. * strength * category * type * source) / 500.) * tuning;

		// if
		// ((measure.getMeasureDescription().getReference().equals("A.9.2.2")))
		// {
		// System.out.println("Measure: " +
		// measure.getMeasureDescription().getReference() +
		// "Asset: " + tmpAssessment.getAsset().getName() + "Scenario: " +
		// tmpAssessment.getScenario().getName() + " ;RRF=" + RRF + ", atv=" +
		// assetTypeValue +
		// ", strength=" + strength + ", Category=" + category + ", type=" +
		// type + ", source=" +
		// source + ", tuning=" + tuning);
		// }

		// ****************************************************************
		// * return the value
		// ****************************************************************
		return RRF;
	}

	/**
	 * calculateRRFCategory: <br>
	 * RRF Category calculation Returns SUM(Rm*RiS)/4*SUM(Rs): R =
	 * RISK(CONFIDENTIALITY,AVAILABILITY,INTEGRITY,Direct[1-7], Indirect[1-10]), M=MeasureProperties
	 * and S=scenario
	 * 
	 * @param properties
	 *            MeasureProperties
	 * @param scenario
	 *            Scenario
	 * 
	 * @return The Calculated RRF Category value
	 */
	public static double calculateRRFCategory(MeasureProperties properties, Scenario scenario) {

		// check if properties and scenario are not null to avoid failures
		if (properties == null)
			throw new IllegalArgumentException("error.rrf.compute.properties_null");

		if (scenario == null)
			throw new IllegalArgumentException("error.rrf.compute.scenario_null");

		// **************************************************************
		// * intialise variables
		// **************************************************************
		double categoryNumerator = 0;
		double categoryDenominator = 0;
		final double MULTIPLICATOR = 4;
		String[] keys = SecurityCriteria.getCategoryKeys();

		// **************************************************************
		// * calculate numerator and denominator of Category Formula
		// **************************************************************

		// parse Category Keys
		for (String risk : keys) {

			// calculate: Category of Measure * Category of Scenario
			categoryNumerator += properties.getCategoryValue(risk) * scenario.getCategoryValue(risk);

			// calculate: sum of Scenario Category
			categoryDenominator += scenario.getCategoryValue(risk);
		}

		// check if not Division by 0
		if (categoryDenominator == 0) {
			throw new ArithmeticException("error.rrf.compute.arithmetic_denominator_zero");
		}

		// **************************************************************
		// * return numerator / MULTIPLICATOR * denominator
		// **************************************************************
		return categoryNumerator / (MULTIPLICATOR * categoryDenominator);
	}

	/***********************************************************************************************
	 * RRF - END
	 **********************************************************************************************/

	/***********************************************************************************************
	 * Computation of Measure Cost - BEGIN
	 **********************************************************************************************/

	/**
	 * computeCost: <br>
	 * Returns the Calculated Cost of a given Measure.
	 * 
	 * @param measure
	 *            The Measure to calculate the Cost
	 * 
	 * @return The Calculated Cost
	 */
	public double computeCost(Measure measure) {

		// ****************************************************************
		// * variable initialisation
		// ****************************************************************
		double cost = 0;
		double externalSetupValue = -1;
		double internalSetupValue = -1;
		double lifetimeDefault = -1;

		// ****************************************************************
		// * select external and internal setup rate from parameters
		// ****************************************************************

		internalSetupValue = this.getParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE);
		
		externalSetupValue = this.getParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE);
		
		lifetimeDefault = this.getParameter(Constant.PARAMETER_LIFETIME_DEFAULT);

		// calculate the cost
		cost = Analysis.computeCost(internalSetupValue, externalSetupValue, lifetimeDefault, measure.getInternalWL(), measure.getExternalWL(), measure.getInvestment(), measure.getLifetime(),
					measure.getInternalMaintenance(), measure.getExternalMaintenance(), measure.getRecurrentInvestment());

		// return calculated cost
		return cost;
	}

	/**
	 * computeCost: <br>
	 * Returns the Calculated Cost of a Measure. <br>
	 * Formula used: <br>
	 * Formula: Cost = ((is * iw) + (es * ew) + in) * ((1 / lt) + (ma / 100))<br>
	 *	 With:<br>
	 *	 is: The Internal Setup Rate in Euro per Man Day<br>
	 *	 iw: The Internal Workload in Man Days<br>
	 *	 es: The External Setup Rate in Euro per Man Day<br>
	 *	 ew: The External Workload in Man Days<br>
	 *	 in:  The Investment in Euro<br>
	 *	 lt: The Lifetime in Years :: if 0 -> use The Default LifeTime in Years<br>
	 *	 ma: The Maintenance in Percentage (0,00 - 1,00 WHERE 0,00 = 0% and 0,1 = 100%) :: if 0 -> use The Default Maintenance in Percentage (0,00 - 1,00 WHERE 0,00 = 0% and 0,1 = 100%)
	 * 
	 * @param internalSetup
	 *            
	 * @param externalSetup
	 *            
	 * @param lifetimeDefault
	 *            
	 * @param maintenanceDefault
	 *            
	 * @param internalWorkLoad
	 *            
	 * @param externalWorkLoad
	 *            
	 * @param investment
	 *           
	 * @param lifetime
	 *            
	 * @param maintenance
	 * 
	 * @return The Calculated Cost
	 */
	@Deprecated
	public static final double computeCost(double internalSetup, double externalSetup, double lifetimeDefault, double maintenanceDefault, double maintenance, double internalWorkLoad, double externalWorkLoad, double investment, double lifetime) {

		// ****************************************************************
		// * variable initialisation
		// ****************************************************************
		double cost = 0;

		// internal setup * internal wokload
		cost = (internalSetup * internalWorkLoad);

		// + external setup * external wokload
		cost += (externalSetup * externalWorkLoad);

		// + investment
		cost += investment;

		// check if lifetime is not 0 -> YES: use default lifetime
		if (lifetime == 0) {

			// check if maintenance is -1 -> YES: use default maintenance
			if (maintenance == -1) {
				cost *= ((1. / lifetimeDefault) + (maintenanceDefault / 100.));
			} else

			// check if maintenance is 0 -> No: use existing maintenance value
			{
				cost *= ((1. / lifetimeDefault) + (maintenance / 100.));
			}
		} else

		// check if lifetime is 0 -> NO: use existing maintenance
		{
			// check if maintenance is -1 -> YES: use default maintenance
			if (maintenance == -1) {
				cost *= ((1. / lifetime) + (maintenanceDefault / 100.));
			} else

			// check if maintenance is 0 -> NO: use existing maintenance value
			{
				cost *= ((1. / lifetime) + (maintenance / 100.));
			}
		}

		// return calculated cost
		return cost;
	}
	
	/**
	 * computeCost: <br>
	 * Returns the Calculated Cost of a Measure. This method does no more need the parameter default maintenance, but needs to get the internal and external maintenance in md as well as the 
	 * recurrent investment per year in keuro. <br>
	 * Formula used:<br>
	 * Cost = ((ir * iw) + (er * ew) + in) * ((1 / lt) + ((im * ir) + (em * er) + ri))<br>
	 *	 With:<br>
	 *	 ir: The Internal Setup Rate in Euro per Man Day<br>
	 *	 iw: The Internal Workload in Man Days<br>
	 *	 er: The External Setup Rate in Euro per Man Day<br>
	 *	 ew: The External Workload in Man Days<br>
	 *	 in: The Investment in kEuro<br>
	 *	 lt: The Lifetime in Years :: if 0 -> use The Default LifeTime in Years<br>
	 *	 im: The Internal Maintenance in Man Days<br>
	 *   em: The External Maintenance in Man Days<br>
	 *   ri: The recurrent Investment in kEuro<br>
	 * 
	 * @param internalSetupRate
	 *           
	 * @param externalSetupRate
	 *            
	 * @param lifetimeDefault
	 *            
	 * @param internalMaintenance
	 *            
	 * @param externalMaintenance
	 *            
	 * @param recurrentInvestment
	 *            
	 * @param internalWorkLoad
	 *            
	 * @param externalWorkLoad
	 *            
	 * @param investment
	 *            
	 * @param lifetime
	 *           
	 * @return The Calculated Cost
	 */
	public static final double computeCost(double internalSetupRate, double externalSetupRate, double lifetimeDefault, double internalMaintenance, double externalMaintenance,
			double recurrentInvestment, double internalWorkLoad, double externalWorkLoad, double investment, double lifetime) {

		// ****************************************************************
		// * variable initialisation
		// ****************************************************************
		double cost = 0;

		// internal setup * internal wokload + external setup * external workload
		cost = (internalSetupRate * internalWorkLoad) + (externalSetupRate * externalWorkLoad);

		// + investment
		cost += investment;

		// check if lifetime is not 0 -> YES: use default lifetime
		if (lifetime == 0) {			
				cost *= (1. / lifetimeDefault);
		} else {
			cost *= (1. / lifetime);
		}

		cost += ((internalMaintenance * internalSetupRate)+(externalMaintenance * externalSetupRate) + recurrentInvestment);
		
		// return calculated cost
		return cost;
	}

	/***********************************************************************************************
	 * Computation of Measure Cost - END
	 **********************************************************************************************/

	/**
	 * initialisePhases: <br>
	 * Creates the Phase List "usedPhases" from Measures
	 */
	public void initialisePhases() {

		// ****************************************************************
		// * clear phase vector
		// ****************************************************************
		usedPhases.clear();

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Phase> tmpPhases = new ArrayList<Phase>();
		MaturityNorm maturityNorm = null;
		MaturityMeasure maturityMeasure = null;
		boolean phaseFound = false;
		MeasureNorm measureNorm = null;
		NormMeasure normMeasure = null;
		Phase smallest = null;

		// ****************************************************************
		// * retrieve all phases and add them to the list of phases
		// * therefore parse all analysisNorm and all measures to check phases
		// ****************************************************************

		// parse all analysisNorm
		for (int i = 0; i < analysisNorms.size(); i++) {

			// ****************************************************************
			// * make difference between maturity norm and measurenorm
			// ****************************************************************

			// check if maturity norm -> YES
			if (analysisNorms.get(i) instanceof MaturityNorm) {

				// temporary store maturity norm
				maturityNorm = (MaturityNorm) analysisNorms.get(i);

				// ****************************************************************
				// * parse all maturity measures
				// ****************************************************************

				// parse all measures of the norm
				for (int j = 0; j < maturityNorm.getMeasures().size(); j++) {

					// ****************************************************************
					// * check for each level 1 measure (chapter) if phase
					// exists in
					// * phase list
					// ****************************************************************

					// temporary store measure
					maturityMeasure = maturityNorm.getMeasure(j);

					// check if level is 1 (maturity chapter)
					if (maturityMeasure.getMeasureDescription().getLevel() == Constant.MEASURE_LEVEL_1) {

						// check if phase of that measure is already in list
						phaseFound = false;

						// parse exisiting phases
						for (int k = 0; k < tmpPhases.size(); k++) {

							// try to find current phase
							if (tmpPhases.get(k).getNumber() == maturityMeasure.getPhase().getNumber()) {

								// phase was found
								phaseFound = true;

								// exit loop
								break;
							}
						}

						// check if phase was found -> NO
						if (phaseFound == false) {

							// ****************************************************************
							// * add phase to list pf phases
							// ****************************************************************
							tmpPhases.add(maturityMeasure.getPhase());
						}
					}
				}
			} else {

				// check if maturity norm -> NO

				// store the measure norm
				measureNorm = (MeasureNorm) analysisNorms.get(i);

				// ****************************************************************
				// * parse all measure of this norm and check if phase exists in
				// * phase list
				// ****************************************************************

				// parse all measures of this norm
				for (int j = 0; j < measureNorm.getMeasures().size(); j++) {

					// ****************************************************************
					// * perfrom check if phase exist
					// ****************************************************************

					// store measure
					normMeasure = measureNorm.getMeasure(j);

					// check if phase already exists
					phaseFound = false;

					// parse phases
					for (int k = 0; k < tmpPhases.size(); k++) {

						// try to find current phase
						if (tmpPhases.get(k).getNumber() == normMeasure.getPhase().getNumber()) {

							// phase was found
							phaseFound = true;

							// exit loop
							break;
						}
					}

					// phase was not found -> NO
					if (phaseFound == false) {

						// ****************************************************************
						// * add to phases
						// ****************************************************************
						tmpPhases.add(normMeasure.getPhase());
					}
				}
			}
		}

		// ****************************************************************
		// * order phases ascending
		// ****************************************************************

		// check until temporary list is empty (phases are ordered)
		while (tmpPhases.size() > 0) {

			// ****************************************************************
			// * for each run use the first element as phase number to check to
			// the other elements (if others are smaller: take smallest)
			// ****************************************************************

			// ****************************************************************
			// * set phase number to the first element of temporary list
			// ****************************************************************

			// determine smallest phase number
			smallest = null;

			// start with the first list element to be the smallest
			if (tmpPhases.get(0) != null) {

				// smallest number for the first one
				smallest = tmpPhases.get(0);

				// ****************************************************************
				// * check all other phases to determine the smallest number
				// ****************************************************************

				// parse all phases and check on the smallest
				for (int i = 0; i < tmpPhases.size(); i++) {

					// determine the smallest
					if (tmpPhases.get(i).getNumber() < smallest.getNumber()) {

						// current phase is smaller than the intended smallest
						// -> replace the value
						smallest = tmpPhases.get(i);
					}
				}

				// ****************************************************************
				// * at this time variable smallest has the smallest phase
				// ****************************************************************

				// ****************************************************************
				// * add phase to the final phase list
				// ****************************************************************

				usedPhases.add(smallest);

				// ****************************************************************
				// * remove from smallest phase found from the temporary list
				// ****************************************************************
				tmpPhases.remove(smallest);
			}
		}

		// for (int i=0; i < usedPhases.size();i++) {
		// System.out.println("ID: " + usedPhases.get(i).getId() +
		// "::: Number: " + usedPhases.get(i).getNumber());
		// }

	}

	/**
	 * getYearsDifferenceBetweenTwoDates: <br>
	 * This method Calculates an Double Value that Indicates the Difference between two Dates. It is
	 * used to Calculate the Size of the Phase in Years.
	 * 
	 * @param beginDate
	 *            begin date (should be smallest date)
	 * @param endDate
	 *            end date (should be biggest date)
	 * @return
	 */
	public static final double getYearsDifferenceBetweenTwoDates(Date beginDate, Date endDate) {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		double result = 0;
		double yearInMiliseconds = 0;
		Calendar calendarBeginDate = Calendar.getInstance();
		Calendar calendarEndDate = Calendar.getInstance();

		// ****************************************************************
		// * calculate duration of years between two dates
		// ****************************************************************

		// ****************************************************************
		// check if dates are null
		// ****************************************************************
		if ((beginDate == null) || (endDate == null)) {

			// set defualt duration of 1 year
			result = 1.;
		}

		// set year in miliseconds
		yearInMiliseconds = 1000L * 60 * 60 * 24 * 365.25;

		// ****************************************************************
		// * set values for begin and end date
		// ****************************************************************
		calendarBeginDate.setTime(beginDate);
		calendarEndDate.setTime(endDate);

		// calculate difference between two dates
		result = Math.abs((calendarEndDate.getTimeInMillis() - calendarBeginDate.getTimeInMillis()) / yearInMiliseconds);

		// ****************************************************************
		// * return difference of two dates in years
		// ****************************************************************
		return result;
	}

	/**
	 * getParameter: <br>
	 * Returns the Parameter value of a given Parameter.
	 * 
	 * @param parameter
	 *            The Label of the Parameter
	 * @return The Value of the Parameter if it exists, or -1 if the parameter was not found
	 */
	public double getParameter(String parameter) {

		// initialise result value
		double result = -1;

		// parse all parameters
		for (int i = 0; i < this.getParameters().size(); i++) {

			// check if parameter is the one request -> YES
			if (this.getAParameter(i).getDescription().equals(parameter)) {

				// ****************************************************************
				// * set value
				// ****************************************************************
				result = this.getAParameter(i).getValue();
				break;
			}
		}

		// return the result
		return result;
	}

	/**
	 * computeParameterScales: <br>
	 * This method will calculate the bounds of the extended parameters from and to values. Since
	 * CSSF implementation, impact and probability values need to be calculated using bounds.
	 */
	public void computeParameterScales() {

		// ****************************************************************
		// * Variable initialisation
		// ****************************************************************
		ExtendedParameter currentParam = null;
		ExtendedParameter nextParam = null;
		Bounds previousImpactBounds = null;
		Bounds previousProbaBounds = null;

		// parse all parameters
		for (int i = 0; i < parameters.size(); i++) {

			// ****************************************************************
			// * Impact Parameters
			// ****************************************************************

			// check if the parameter is of type impact
			if (getAParameter(i).getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME)) {

				// store current parameter
				currentParam = (ExtendedParameter) getAParameter(i);

				// check if this is the last impact -> NO
				if ((i + 1 < parameters.size()) && (getAParameter(i + 1).getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))) {

					// store next impact parameter
					nextParam = (ExtendedParameter) getAParameter(i + 1);

					// update bounds with the previous bounds (to value) and
					// current and next values
					// if previousImpactBounds are null, the value 0 will be
					// used.
					currentParam.getBounds().updateBounds(previousImpactBounds, currentParam.getValue(), nextParam.getValue());

					// store current bounds for next turn's previous bounds
					previousImpactBounds = currentParam.getBounds();
				} else {

					// check if this is the last impact -> YES

					// update bounds with infinitive next value
					currentParam.getBounds().updateBounds(previousImpactBounds, currentParam.getValue(), Constant.DOUBLE_MAX_VALUE);
				}
			}

			// ****************************************************************
			// * Probability Parameters
			// ****************************************************************

			// check if the parameter is of type probability
			if (getAParameter(i).getType().equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME)) {

				// store current probability parameter
				currentParam = (ExtendedParameter) getAParameter(i);

				// check if this is the last probability -> NO
				if ((i + 1 < parameters.size()) && (getAParameter(i + 1).getType().equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME))) {

					// store next probability parameter
					nextParam = (ExtendedParameter) getAParameter(i + 1);

					// update bounds with the previous bounds (to value) and
					// current and next values
					// if previousImpactBounds are null, the value 0 will be
					// used.
					currentParam.getBounds().updateBounds(previousProbaBounds, currentParam.getValue(), nextParam.getValue());

					// store current bounds for next turn's previous bounds
					previousProbaBounds = currentParam.getBounds();
				} else {

					// check if this is the last probability -> YES

					// update bounds with infinitive next value
					currentParam.getBounds().updateBounds(previousProbaBounds, currentParam.getValue(), Constant.DOUBLE_MAX_VALUE);
				}
			}
		}
	}

	/***********************************************************************************************
	 * Getter's and Setter's
	 **********************************************************************************************/

	/**
	 * setCustomerid: <br>
	 * Sets the "customer" field with a object
	 * 
	 * @param customer
	 *            The Object to set the Customer
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * getCustimerId: <br>
	 * Returns the "customer" field value
	 * 
	 * @return The Customer ID
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * getIdentifier: <br>
	 * Returns the "identifier" field value
	 * 
	 * @return The Analysis ID
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * setIdentifier: <br>
	 * Sets the "identifier" field with a value
	 * 
	 * @param identifier
	 *            The value to set the Analysis ID
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * getVersion: <br>
	 * Returns the "version" field value
	 * 
	 * @return The Analysis Version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * setVersion: <br>
	 * Sets the "version" field with a value
	 * 
	 * @param version
	 *            The value to set the Analysis Version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * getCreationDate: <br>
	 * Returns the "creationDate" field value
	 * 
	 * @return The Analysis Creation Date
	 */
	public Timestamp getCreationDate() {
		return creationDate;
	}

	/**
	 * setCreationDate: <br>
	 * Sets the "creationDate" field with avalue
	 * 
	 * @param creationdate
	 *            The value to set the Creation Date
	 */
	public void setCreationDate(Timestamp creationdate) {
		this.creationDate = creationdate;
	}

	/**
	 * getLabel: <br>
	 * Returns the "label" field value
	 * 
	 * @return The Analysis Label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * setLabel: <br>
	 * Sets the "label" field with a value
	 * 
	 * @param label
	 *            The value to set the Analysis Label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * getAHistory: <br>
	 * Returns a History Entry at position "index" from the History List.
	 * 
	 * @param index
	 *            The Position to retrieve the Object
	 * 
	 * @return The Histroy Object at position "index"
	 */
	public History getAHistory(int index) {
		return histories.get(index);
	}

	/**
	 * getHistories: <br>
	 * Returns the List of History Entries from the Analysis.
	 * 
	 * @return The List of History Entries
	 */
	public List<History> getHistories() {
		return histories;
	}

	/**
	 * setHistories: <br>
	 * Description
	 * 
	 * @param histories
	 */
	public void setHistories(List<History> histories) {
		this.histories = histories;
	}

	/**
	 * addAHistory: <br>
	 * Adds a new History object to the List of History Entries.
	 * 
	 * @param hist
	 *            The History object to add
	 */
	public void addAHistory(History hist) {
		this.histories.add(hist);
	}

	/**
	 * setHistory: <br>
	 * Set History List object.
	 * 
	 * @param hist
	 *            The List of History objects to add
	 */
	public void setHistory(List<History> hists) {
		this.histories = hists;
	}

	/**
	 * getLanguage: <br>
	 * Returns the "language" field object
	 * 
	 * @return The Analysis Language Object
	 */
	public Language getLanguage() {
		return language;
	}

	/**
	 * setLanguage: <br>
	 * Sets the "language" field with a Language Object
	 * 
	 * @param language
	 *            The Object to set the Language
	 */
	public void setLanguage(Language language) {
		this.language = language;
	}

	/**
	 * getAnItemInformtation: <br>
	 * Returns a Single Item Information from the List of Item Information at the postion "index"
	 * 
	 * @param index
	 *            The Position in the List to retrieve the Item Information
	 * 
	 * @return The Item Information object at position "index"
	 */
	public ItemInformation getAnIteminformation(int index) {
		return itemInformations.get(index);
	}

	/**
	 * getItemInformationList: <br>
	 * Returns the Item Information List.
	 * 
	 * @return The Item Information List object
	 */
	public List<ItemInformation> getItemInformations() {
		return itemInformations;
	}

	/**
	 * addAnItemInformation: <br>
	 * Adds an Item Information Object to the List of Item Information
	 * 
	 * @param iteminformation
	 *            The Item Information Object to Add
	 */
	public void addAnItemInformation(ItemInformation itemInformation) {
		this.itemInformations.add(itemInformation);
	}

	/**
	 * setItemInformations: <br>
	 * Adds an Item Information Object to the List of Item Information
	 * 
	 * @param iteminformations
	 *            The Item Information Object to Add
	 */
	public void setItemInformations(List<ItemInformation> itemInformations) {
		this.itemInformations = itemInformations;
	}

	/**
	 * getAParameter: <br>
	 * Returns a Parameter from the List of Parameter at position "index"
	 * 
	 * @param index
	 *            The Position to retrieve the Object
	 * 
	 * @return The Parameter Object at position "index"
	 */
	public Parameter getAParameter(int index) {
		return parameters.get(index);
	}

	/**
	 * getParameterList: <br>
	 * Returns the Parameter List.
	 * 
	 * @return The Parameter Object List
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}

	/**
	 * addAParameter: <br>
	 * Adds a Parameter to the List of Parameters
	 * 
	 * @param param
	 *            The Parameter object to Add
	 */
	public void addAParameter(Parameter param) {
		this.parameters.add(param);
	}

	/**
	 * setParameters: <br>
	 * Adds a Parameter to the List of Parameters
	 * 
	 * @param params
	 *            The Parameter object to Add
	 */
	public void setParameters(List<Parameter> params) {
		this.parameters = params;
	}

	/**
	 * getAnAsset: <br>
	 * Returns an Asset from the List of Asset at position "index"
	 * 
	 * @param index
	 *            The Position to retrieve the Object
	 * 
	 * @return The AssetObject at position "index"
	 */
	public Asset getAnAsset(int index) {
		return assets.get(index);
	}

	/**
	 * getAsset: <br>
	 * Returns a list of Assets.
	 * 
	 * @return The List of Asset Objects
	 */
	public List<Asset> getAssets() {
		return assets;
	}

	/**
	 * addAnAsset: <br>
	 * Adds an Asset Object to the List of Assets
	 * 
	 * @param asset
	 *            The asset Object to Add
	 */
	public void addAnAsset(Asset asset) {
		if (this.assets.contains(asset))
			throw new IllegalArgumentException("error.asset.duplicate");
		this.assets.add(asset);
	}

	/**
	 * setAssets: <br>
	 * Sets the List of Asset Objects.
	 * 
	 * @param assets
	 *            The List of Asset Objects
	 */
	public void setAssets(List<Asset> assets) {
		this.assets = assets;
	}

	/**
	 * getARiskInformation: <br>
	 * Returns a Risk Information from the List of Risk Information at position "index"
	 * 
	 * @param index
	 *            The Position to retrieve the Object
	 * 
	 * @return The Risk Information Object at position "index"
	 */
	public RiskInformation getARiskInformation(int index) {
		return riskInformations.get(index);
	}

	/**
	 * getRiskInformationList: <br>
	 * Returns the list of Risk Information.
	 * 
	 * @return The Risk Information Object List
	 */
	public List<RiskInformation> getRiskInformations() {
		return riskInformations;
	}

	/**
	 * addARiskInformation: <br>
	 * Adds an Risk Information Object to the List of Risk Information
	 * 
	 * @param riskInfo
	 *            The Risk Information Object to Add
	 */
	public void addARiskInformation(RiskInformation riskInfo) {
		this.riskInformations.add(riskInfo);
	}

	/**
	 * setRiskInformations: <br>
	 * Sets the Risk Information List Object.
	 * 
	 * @param riskInfos
	 *            The List of Risk Information Objects
	 */
	public void setRiskInformations(List<RiskInformation> riskInfos) {
		this.riskInformations = riskInfos;
	}

	/**
	 * getAScenario: <br>
	 * Returns a Scenario from the List of Scenarios at position "index"
	 * 
	 * @param index
	 *            The Position to retrieve the Object
	 * 
	 * @return The Scenario Object at position "index"
	 */
	public Scenario getAScenario(int index) {
		return scenarios.get(index);
	}

	/**
	 * getScenarioList: <br>
	 * Returns the Scenario List.
	 * 
	 * @return The Scenario List Object
	 */
	public List<Scenario> getScenarios() {
		return scenarios;
	}

	/**
	 * setAScenario: <br>
	 * Adds a Scenario Object to the List of Scenarios
	 * 
	 * @param scenario
	 *            The Scenario Object to Add
	 */
	public void addAScenario(Scenario scenario) {
		if (this.scenarios.contains(scenario)) {
			throw new IllegalArgumentException("error.scenario.duplicate");
		}
		this.scenarios.add(scenario);
	}

	/**
	 * setScenarios: <br>
	 * Sets the list of Scenario Objects.
	 * 
	 * @param scenarios
	 *            The List of Scenarios
	 */
	public void setScenarios(List<Scenario> scenarios) {
		this.scenarios = scenarios;
	}

	/**
	 * getAnAssessment: <br>
	 * Returns an Assessment from the List of Assessment at position "index"
	 * 
	 * @param index
	 *            The Position to retrieve the Object
	 * 
	 * @return The Assessment Object at position "index"
	 */
	public Assessment getAnAssessment(int index) {
		return assessments.get(index);
	}

	/**
	 * getAssessmentList: <br>
	 * Returns the List of Assessments.
	 * 
	 * @return The List of Assessment Objects
	 */
	public List<Assessment> getAssessments() {
		return assessments;
	}

	/**
	 * addAnAssessment<br>
	 * Adds an Assessment Object to the List of Assessments
	 * 
	 * @param assessment
	 *            The Assessment Object to Add
	 */
	public void addAnAssessment(Assessment assessment) {
		if (this.assessments.contains(assessment))
			throw new IllegalArgumentException("error.assessment.duplicate");
		this.assessments.add(assessment);
	}

	/**
	 * setAssessments<br>
	 * Sets the list of Assessment Objects.
	 * 
	 * @param assessments
	 *            The List of Assessments
	 */
	public void setAssessments(List<Assessment> assessments) {
		this.assessments = assessments;
	}

	/**
	 * getANorm: <br>
	 * Returns a AnalysisNorm Object from the List of Norms at position "index"
	 * 
	 * @param index
	 *            The Position to retrieve the Object
	 * 
	 * @return The AnalysisNorm Object at position "index"
	 */
	public AnalysisNorm getAnalysisNorm(int index) {
		return analysisNorms.get(index);
	}

	public AnalysisNorm getAnalysisNormByLabel(String label) {
		for (AnalysisNorm anorm : this.analysisNorms) {
			if (anorm.getNorm().getLabel().equals(label)) {
				return anorm;
			}
		}
		return null;
	}

	/**
	 * getNorms: <br>
	 * Returns a List of AnalysisNorm Objects.
	 * 
	 * @return The List of AnalysisNorm Objects
	 */
	public List<AnalysisNorm> getAnalysisNorms() {
		return analysisNorms;
	}

	/**
	 * addANorm: <br>
	 * Adds a AnalysisNorm Object to the List of Norms
	 * 
	 * @param analysisNorms
	 *            The norm Object to Add
	 */
	public void addAnalysisNorm(AnalysisNorm norm) {
		norm.setAnalysis(this);
		this.analysisNorms.add(norm);
	}

	/**
	 * setNorms: <br>
	 * Sets all AnalysisNorm in form of a vecotr.
	 * 
	 * @param analysisNorm
	 *            The List of Norms to set
	 */
	public void setAnalysisNorms(List<AnalysisNorm> analysisNorm) {
		for (AnalysisNorm analysisNorm2 : analysisNorm)
			analysisNorm2.setAnalysis(this);
		this.analysisNorms = analysisNorm;
	}

	/**
	 * getAPhase: <br>
	 * Returns a Phase at the position "index" given as parameter.
	 * 
	 * @param index
	 *            The index of the Phase to return
	 * @return The Phase object at requested position
	 */
	public Phase getAPhase(int index) {
		return usedPhases.get(index);
	}

	/**
	 * getUsedphases: <br>
	 * Returns the usedphases field value.
	 * 
	 * @return The value of the usedphases field
	 */
	public List<Phase> getUsedPhases() {
		return usedPhases;
	}

	/**
	 * setUsedphases: <br>
	 * Sets the Field "usedphases" with a value.
	 * 
	 * @param usedphases
	 *            The Value to set the usedphases field
	 */
	public void setUsedPhases(List<Phase> usedphases) {
		for (Phase phase : usedphases)
			phase.setAnalysis(this);
		this.usedPhases = usedphases;
	}

	public void addUsedPhase(Phase phase) {
		if (this.usedPhases == null)
			usedPhases = new ArrayList<Phase>();
		phase.setAnalysis(this);
		if (!usedPhases.contains(phase))
			usedPhases.add(phase);
		else
			System.out.println("pahse not add : " + phase.getNumber());
	}

	/**
	 * getARiskRegisterEntry: <br>
	 * Returns a Risk Register Item from the RiskRegister at position "index"
	 * 
	 * @param index
	 *            The Position to retrieve the Object
	 * 
	 * @return The Risk Register Object at position "index"
	 */
	public RiskRegisterItem getARiskRegisterEntry(int index) {
		return riskRegisters.get(index);
	}

	/**
	 * getRiskRegisterList: <br>
	 * Returns the Risk Register List.
	 * 
	 * @return The Risk Register List Object
	 */
	public List<RiskRegisterItem> getRiskRegisters() {
		return riskRegisters;
	}

	/**
	 * addARiskRegisterItem: <br>
	 * Adds a Risk Register Item to the Risk Register
	 * 
	 * @param riskItem
	 *            The RiskRegisterItem Object to Add
	 */
	public void addARiskRegisterItem(RiskRegisterItem riskItem) {
		this.riskRegisters.add(riskItem);
	}

	/**
	 * setRiskRegisters: <br>
	 * Sets the Field "riskRegisters" with a value.
	 * 
	 * @param riskRegisters
	 *            The Value to set the riskRegisters field
	 */
	public void setRiskRegisters(List<RiskRegisterItem> riskRegisters) {
		this.riskRegisters = riskRegisters;
	}

	/**
	 * getData: <br>
	 * Returns the "hasData" field Value
	 * 
	 * @return The hasData Analysis Flag
	 */
	public boolean getData() {
		return data;
	}

	/**
	 * hasData: <br>
	 * Returns the "hasData" field Value
	 * 
	 * @return The hasData Analysis Flag
	 */
	public boolean hasData() {
		return data;
	}

	/**
	 * sethasData: <br>
	 * Sets the "hasData" field with a value
	 * 
	 * @param hasData
	 *            The value to set the hasData Analysis Flag
	 */
	public void setData(boolean data) {
		this.data = data;
	}

	/**
	 * getHistory: <br>
	 * returns history entry at index
	 * 
	 * @param index
	 *            The index inside the histories list
	 * 
	 * @return history
	 */
	public History getHistory(int index) {
		return histories == null || histories.isEmpty() ? null : histories.get(index);
	}

	/**
	 * getLastHistory: <br>
	 * returns last history
	 * 
	 * @return last history
	 */
	public History getLastHistory() {
		return histories == null || histories.isEmpty() ? null : histories.get(histories.size() - 1);
	}

	/**
	 * setHistory: <br>
	 * Description
	 * 
	 * @param history
	 */
	public void setHistory(History history) {
		if (histories == null)
			histories = new ArrayList<History>();
		histories.add(history);
	}

	/**
	 * addAnActionPlanEntry: <br>
	 * Adds an ActionPlanEntry of a given type to the corresponding Action Plan.
	 * 
	 * @param type
	 *            The Identifier of the Action Plan Type
	 * 
	 * @param actionplan
	 *            the action plan entry to add
	 */
	public void addAnActionPlanEntry(ActionPlanEntry actionplanentry) {
		this.actionPlans.add(actionplanentry);
	}

	/**
	 * getActionPlans: <br>
	 * Returns the Action Plan of a given Action Plan Type.
	 * 
	 * @param type
	 *            The Identifier of the Action Plan Type
	 * 
	 * @return The List of Action Plan Entries for the requested Action Plan Type
	 */
	public List<ActionPlanEntry> getActionPlans() {
		return this.actionPlans;
	}

	/**
	 * getActionPlan: <br>
	 * Returns the Action Plan of a given Action Plan Type.
	 * 
	 * @param type
	 *            The Identifier of the Action Plan Type
	 * 
	 * @return The List of Action Plan Entries for the requested Action Plan Type
	 */
	public List<ActionPlanEntry> getActionPlan(String mode) {

		List<ActionPlanEntry> ape = new ArrayList<ActionPlanEntry>();
		for (int i = 0; i < this.actionPlans.size(); i++) {
			if (this.actionPlans.get(i).getActionPlanType().getActionPlanMode().getName().equals(mode)) {
				ape.add(this.actionPlans.get(i));
			}
		}
		return ape;
	}

	/**
	 * getActionPlan: <br>
	 * Returns the Action Plan of a given Action Plan Type.
	 * 
	 * @param type
	 *            The Identifier of the Action Plan Type
	 * 
	 * @return The List of Action Plan Entries for the requested Action Plan Type
	 */
	public List<ActionPlanEntry> getActionPlan(ActionPlanMode mode) {

		List<ActionPlanEntry> ape = new ArrayList<ActionPlanEntry>();
		for (int i = 0; i < this.actionPlans.size(); i++) {
			if (this.actionPlans.get(i).getActionPlanType().getActionPlanMode() == mode) {
				ape.add(this.actionPlans.get(i));
			}
		}
		return ape;
	}

	@SuppressWarnings("unchecked")
	public static List<Parameter>[] SplitParameters(List<Parameter> parameters) {
		List<?>[] splits = new List<?>[3];
		splits[0] = new ArrayList<Parameter>();
		splits[1] = new ArrayList<ExtendedParameter>();
		splits[2] = new ArrayList<MaturityParameter>();
		for (Parameter parameter : parameters) {
			if (parameter instanceof ExtendedParameter)
				((List<ExtendedParameter>) splits[1]).add((ExtendedParameter) parameter);
			else if (parameter instanceof MaturityParameter)
				((List<MaturityParameter>) splits[2]).add((MaturityParameter) parameter);
			else
				((List<Parameter>) splits[0]).add(parameter);
		}
		return (List<Parameter>[]) splits;
	}

	@SuppressWarnings("unchecked")
	public static List<Parameter>[] SplitSimpleParameters(List<Parameter> parameters) {
		List<?>[] splits = new List<?>[3];
		splits[0] = new ArrayList<Parameter>();
		splits[1] = new ArrayList<ExtendedParameter>();
		splits[2] = new ArrayList<MaturityParameter>();
		for (Parameter parameter : parameters) {
			if (parameter.getType().getLabel().equals("SINGLE"))
				((List<Parameter>) splits[0]).add(parameter);
			else if (parameter.getType().getLabel().equals("MAXEFF"))
				((List<Parameter>) splits[1]).add(parameter);
			else
				((List<Parameter>) splits[2]).add(parameter);
		}
		return (List<Parameter>[]) splits;
	}

	@SuppressWarnings("unchecked")
	public static List<Parameter>[] SplitExtendedParameters(List<Parameter> parameters) {
		List<?>[] splits = new List<?>[2];
		splits[0] = new ArrayList<Parameter>();
		splits[1] = new ArrayList<ExtendedParameter>();
		for (Parameter parameter : parameters) {
			if (parameter.getType().getLabel().equals("IMPACT"))
				((List<Parameter>) splits[0]).add(parameter);
			else
				((List<Parameter>) splits[1]).add(parameter);
		}
		return (List<Parameter>[]) splits;
	}

	@SuppressWarnings("unchecked")
	public static List<ItemInformation>[] SplitItemInformations(List<ItemInformation> itemInformations) {
		List<?>[] splits = new List<?>[2];
		splits[0] = new ArrayList<ItemInformation>();
		splits[1] = new ArrayList<ItemInformation>();
		for (ItemInformation itemInformation : itemInformations) {
			if (itemInformation.getType().equalsIgnoreCase("scope"))
				((List<ItemInformation>) splits[0]).add(itemInformation);
			else
				((List<ItemInformation>) splits[1]).add(itemInformation);
		}
		return (List<ItemInformation>[]) splits;
	}

	/**
	 * setActionPlan: <br>
	 * Sets a List of ActionPlanEntries of a given type.
	 * 
	 * @param type
	 *            The Action Plan Type
	 * @param actionPlan
	 *            The List of ActionPlanEntries to set
	 */
	public void setActionPlans(List<ActionPlanEntry> actionPlan) {

		this.actionPlans = actionPlan;
	}

	/**
	 * getSummary: <br>
	 * Returns the summary of a given Action Plan Type.
	 * 
	 * @param type
	 *            The Identifier of the Action Plan Type
	 * 
	 * @return The List of Summary Entries for the requested Action Plan Type
	 */
	public List<SummaryStage> getSummaries() {
		return this.summaries;
	}

	/**
	 * setSummaries: <br>
	 * Sets the Field "summaries" with a value.
	 * 
	 * @param summaries
	 *            The Value to set the summaries field
	 */
	public void setSummaries(List<SummaryStage> summaries) {
		this.summaries = summaries;
	}

	/**
	 * getSummary: <br>
	 * Returns the summary of a given Action Plan Type.
	 * 
	 * @param type
	 *            The Identifier of the Action Plan Type
	 * 
	 * @return The List of Summary Entries for the requested Action Plan Type
	 */
	public List<SummaryStage> getSummary(ActionPlanMode mode) {

		List<SummaryStage> sums = new ArrayList<SummaryStage>();

		for (int i = 0; i < this.summaries.size(); i++) {
			if (this.summaries.get(i).getActionPlanType().getId() == mode.getValue()) {
				sums.add(this.summaries.get(i));
			}
		}

		return sums;
	}

	/**
	 * setSummary: <br>
	 * Sets a List of SummaryStages of a given type.
	 * 
	 * @param type
	 *            The Summary Type (same as Action Plan Type)
	 * @param summary
	 *            The List of SummaryStages to set
	 */
	public void addSummaryEntries(List<SummaryStage> summary) {
		this.summaries.addAll(summary);
	}

	/**
	 * toString: <br>
	 * Description
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Analysis [id=" + id + ", customer=" + customer + ", identifier=" + identifier + ", version=" + version + ", creationDate=" + creationDate + ", label=" + label
			+ ", histories=" + histories + ", language=" + language + ", empty=" + data + ", itemInformations=" + itemInformations + ", parameters=" + parameters + ", assets=" + assets
			+ ", riskInformations=" + riskInformations + ", scenarios=" + scenarios + ", assessments=" + assessments + ", analysisNorm=" + analysisNorms + ", usedphases=" + usedPhases
			+ ", actionPlans=" + actionPlans + ", summaries=" + summaries + ", riskRegisters=" + riskRegisters + "]";
	}

	/**
	 * hashCode: <br>
	 * used inside equals method to check if 2 objects are equal.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	/**
	 * equals: <br>
	 * Method to identify if this object equals another. Equal means the fields identifier, version
	 * and creationDate are the same.
	 * 
	 * @param obj
	 *            The other object to check
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Analysis other = (Analysis) obj;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	/**
	 * getId: <br>
	 * Returns the id field value.
	 * 
	 * @return The value of the id field
	 */
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the Field "id" with a value.
	 * 
	 * @param id
	 *            The Value to set the id field
	 */
	public void setId(int id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Analysis clone() throws CloneNotSupportedException {
		return (Analysis) super.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Analysis duplicate() throws CloneNotSupportedException {
		Analysis analysis = (Analysis) super.clone();
		analysis.actionPlans = new ArrayList<>();
		analysis.riskRegisters = new ArrayList<>();
		analysis.summaries = new ArrayList<>();
		analysis.id = -1;
		return analysis;
	}

	public List<ExtendedParameter> findExtendedByAnalysis() {
		List<ExtendedParameter> extendedParameters = new ArrayList<ExtendedParameter>();
		for (Parameter parameter : parameters) {
			if (parameter instanceof ExtendedParameter)
				extendedParameters.add((ExtendedParameter) parameter);
		}
		return extendedParameters;
	}

	public Map<Integer, List<Assessment>> findAssessmentByAssetAndSelected() {
		Map<Integer, List<Assessment>> assessmentSorted = new LinkedHashMap<Integer, List<Assessment>>();
		for (Assessment assessment : assessments) {
			if (assessment.isSelected()) {
				int assetId = assessment.getAsset().getId();
				List<Assessment> assessments = assessmentSorted.get(assetId);
				if (assessments == null)
					assessmentSorted.put(assetId, assessments = new ArrayList<Assessment>());
				assessments.add(assessment);
			}
		}
		return assessmentSorted;
	}

	public List<Asset> findAssessmentBySelected() {
		List<Asset> assets = new LinkedList<Asset>();
		for (Asset asset : this.assets) {
			if (asset.isSelected())
				assets.add(asset);
		}
		return assets;
	}

	/**
	 * getOwner: <br>
	 * Returns the owner field value.
	 * 
	 * @return The value of the owner field
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * setOwner: <br>
	 * Sets the Field "owner" with a value.
	 * 
	 * @param owner
	 *            The Value to set the owner field
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}

	/**
	 * getBasedOnAnalysis: <br>
	 * Returns the basedOnAnalysis field value.
	 * 
	 * @return The value of the basedOnAnalysis field
	 */
	public Analysis getBasedOnAnalysis() {
		return basedOnAnalysis;
	}

	/**
	 * setBasedOnAnalysis: <br>
	 * Sets the Field "basedOnAnalysis" with a value.
	 * 
	 * @param basedOnAnalysis
	 *            The Value to set the basedOnAnalysis field
	 */
	public void setBasedOnAnalysis(Analysis basedOnAnalysis) {
		this.basedOnAnalysis = basedOnAnalysis;
	}

	/**
	 * getUserRights: <br>
	 * Returns the userRights field value.
	 * 
	 * @return The value of the userRights field
	 */
	public List<UserAnalysisRight> getUserRights() {
		return userRights;
	}

	/**
	 * setUserRights: <br>
	 * Sets the Field "userRights" with a value.
	 * 
	 * @param userRights
	 *            The Value to set the userRights field
	 */
	public void setUserRights(List<UserAnalysisRight> userRights) {
		this.userRights = userRights;
	}

	/**
	 * addUserRights: <br>
	 * Description
	 * 
	 * @param userRight
	 */
	public void addUserRight(UserAnalysisRight userRight) {
		this.userRights.add(userRight);
	}

	/**
	 * addUserRights: <br>
	 * Description
	 * 
	 * @param userRight
	 */
	public void addUserRight(User user, AnalysisRight right) {
		this.userRights.add(new UserAnalysisRight(user, this, right));
	}

	/**
	 * getRightsforUser: <br>
	 * Description
	 * 
	 * @param user
	 * @return
	 */
	public UserAnalysisRight getRightsforUser(User user) {

		for (UserAnalysisRight userRight : userRights) {
			if (userRight.getUser().equals(user)) {
				return userRight;
			}
		}

		return null;
	}

	/**
	 * getRightsforUser: <br>
	 * Description
	 * 
	 * @param user
	 * @return
	 */
	public UserAnalysisRight getRightsforUserString(String login) {
		for (UserAnalysisRight userRight : userRights) {
			if (userRight.getUser().getLogin().equals(login)) {
				return userRight;
			}
		}
		return null;
	}

	/**
	 * editUserRight: <br>
	 * Description
	 * 
	 * @param user
	 * @param newRight
	 */
	public void editUserRight(User user, AnalysisRight newRight) {
		userRights.get(userRights.indexOf(getRightsforUser(user))).setRight(newRight);
	}

	/**
	 * removeRights: <br>
	 * Description
	 * 
	 * @param user
	 * @return
	 */
	public boolean removeRights(User user) {

		UserAnalysisRight userRight = getRightsforUser(user);

		if (userRight != null) {
			userRights.remove(userRight);
			return true;
		} else {
			return false;
		}

	}

	/**
	 * userIsAuthorized: <br>
	 * checks if a given user has the given right on the current analysis
	 * 
	 * @param user
	 * @param right
	 * @return
	 */
	public boolean isUserAuthorized(User user, AnalysisRight right) {

		for (UserAnalysisRight uar : userRights) {
			if (uar.getUser().equals(user)) {
				return UserAnalysisRight.userIsAuthorized(uar, right);
			}
		}
		return false;
	}

	/**
	 * @return the profile
	 */
	public boolean isProfile() {
		return profile;
	}

	/**
	 * @param profile
	 *            the profile to set
	 */
	public void setProfile(boolean profile) {
		this.profile = profile;
	}

	public Analysis duplicateTo(Analysis copy) throws CloneNotSupportedException {
		if (copy == null)
			copy = (Analysis) super.clone();
		else {
			copy.data = data;
			copy.creationDate = creationDate;
			copy.customer = customer;
			copy.identifier = identifier;
			copy.label = label;
			copy.language = language;
			copy.owner = owner;
			copy.profile = profile;
			copy.version = version;
		}
		copy.actionPlans = new ArrayList<>();
		copy.riskRegisters = new ArrayList<>();
		copy.summaries = new ArrayList<>();
		copy.id = -1;
		return copy;
	}

	public Phase findPhaseByNumber(int number) {
		for (Phase phase : usedPhases)
			if (phase.getNumber() == number)
				return phase;
		return null;
	}

	public boolean hasPhase(int number) {
		return findPhaseByNumber(number) != null;
	}

	/**
	 * isDefaultProfile: <br>
	 * Returns the defaultProfile field value.
	 * 
	 * @return The value of the defaultProfile field
	 */
	public boolean isDefaultProfile() {
		return defaultProfile;
	}

	/**
	 * setDefaultProfile: <br>
	 * Sets the Field "defaultProfile" with a value.
	 * 
	 * @param defaultProfile
	 *            The Value to set the defaultProfile field
	 */
	public void setDefaultProfile(boolean defaultProfile) {
		this.defaultProfile = defaultProfile;
	}
}