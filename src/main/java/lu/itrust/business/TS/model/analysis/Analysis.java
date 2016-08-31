package lu.itrust.business.TS.model.analysis;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.history.History;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;
import lu.itrust.business.TS.model.parameter.AcronymParameter;
import lu.itrust.business.TS.model.parameter.DynamicParameter;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.MaturityParameter;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.parameter.helper.Bounds;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.MaturityStandard;
import lu.itrust.business.TS.model.standard.NormalStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.usermanagement.User;

/**
 * Analysis: <br>
 * This class represents an analysis and all its data of TRICK Service. This
 * class is used to store analysis data such as assets, scenarios, security
 * measures, item information, risk information, the version, parameters and
 * phases. After the data is stored, the action plan can be computed within this
 * class as well as the Action Plan Summary.
 * <ul>
 * <li>import Analysis from SQLite file</li>
 * <li>store analysis in java object to use during the calculations</li>
 * <li>calculate all Action Plans (Normal, optimistic, pessimistic, phase)</li>
 * <li>calculate Risk Register</li>
 * <li>Export a specific Analysis</li>
 * </ul>
 * 
 * 
 * @author itrust consulting s.a r.l. - SME,BJA
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "dtIdentifier", "dtVersion" }))
public class Analysis implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** Analysis id unsaved value = -1 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAnalysis")
	private int id = -1;

	/** ID of the Analysis */
	@Column(name = "dtIdentifier", nullable = false, length = 23)
	private String identifier;

	/** Version of the Analysis */
	@Column(name = "dtVersion", nullable = false, length = 12)
	private String version;

	/** Creation Date of the Analysis (and a specific version) */
	@Column(name = "dtCreationDate", nullable = false, columnDefinition = "datetime")
	private Timestamp creationDate;

	@Column(name = "dtProfile", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean profile = false;

	@Column(name = "dtDefaultProfile", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean defaultProfile = false;

	@Column(name = "dtUncertainty", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean uncertainty = false;

	@Column(name = "dtCssf", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean cssf = false;

	/** The Customer object */
	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "fiCustomer", nullable = false)
	private Customer customer;

	/** Analysis owner (the one that created or imported it) */
	@ManyToOne
	@JoinColumn(name = "fiOwner", nullable = false)
	@Cascade(CascadeType.SAVE_UPDATE)
	@Access(AccessType.FIELD)
	private User owner;

	/** Based on analysis */
	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "fiBasedOnAnalysis", nullable = true)
	private Analysis basedOnAnalysis;

	/** The Label of this Analysis */
	@Column(name = "dtLabel", nullable = false)
	private String label;

	/** Language object of the Analysis */
	@ManyToOne
	@JoinColumn(name = "fiLanguage", nullable = false)
	@Cascade(CascadeType.SAVE_UPDATE)
	@Access(AccessType.FIELD)
	private Language language;

	/** flag to determine if analysis has data */
	@Column(name = "dtData", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean data;

	/** Ticketing project id */
	@Column(name = "dtProject")
	private String project;

	/** List of users and their access rights */
	@OneToMany(mappedBy = "analysis")
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<UserAnalysisRight> userRights = new ArrayList<UserAnalysisRight>();

	/** List of History data of the Analysis */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<History> histories = new ArrayList<History>();

	/** List of Item Information */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<ItemInformation> itemInformations = new ArrayList<ItemInformation>();

	/** List of parameters */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<Parameter> parameters = new ArrayList<Parameter>();

	/** List of assets */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	@OrderBy("value DESC, ALE DESC, name ASC")
	private List<Asset> assets = new ArrayList<Asset>();

	/** List of Risk Information */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<RiskInformation> riskInformations = new ArrayList<RiskInformation>();

	/** List of Scenarios */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	@OrderBy("type,name")
	private List<Scenario> scenarios = new ArrayList<Scenario>();

	/** List of Assessment */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<Assessment> assessments = new ArrayList<Assessment>();

	/** List of Assessment */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<RiskProfile> riskProfiles = new ArrayList<RiskProfile>();

	/** List of Standards */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@OrderBy("standard")
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<AnalysisStandard> analysisStandards = new ArrayList<AnalysisStandard>();

	/** List of Phases that is used for Action Plan Computation */
	@OneToMany(mappedBy = "analysis")
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	@OrderBy("number")
	private List<Phase> phases = new ArrayList<Phase>();

	/** The Final Action Plan without Phase Computation - Normal */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<ActionPlanEntry> actionPlans = new ArrayList<ActionPlanEntry>();

	/** The Action Plan Summary without Phase Computation - Normal */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<SummaryStage> summaries = new ArrayList<SummaryStage>();

	/** The Risk Register (CSSF) */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	@OrderBy("dtNetEvaluationImportance desc, dtExpEvaluationImportance desc, dtRawEvaluationImportance desc")
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

	public Analysis(Customer customer, User owner) {
		setCustomer(customer);
		setOwner(owner);
		addUserRight(owner, AnalysisRight.ALL);
	}

	/**
	 * initialiseEmptyItemInformation: <br>
	 * Description
	 * 
	 * @param analysis
	 */
	public static final void InitialiseEmptyItemInformation(Analysis analysis) {

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
	 * @throws TrickException
	 */
	public double getALEOfAsset(Asset asset) throws TrickException {

		// initialise return value
		double result = 0;

		// check if asset exists and if assessments are not empty
		if (asset == null)
			throw new TrickException("error.ale.asset_null", "Asset cannot be empty");
		if (this.assessments.isEmpty())
			throw new TrickException("error.ale.Assessments_empty", "Assessment cannot be empty");
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
	 * Parse all history entries to find latest version (version has to be of
	 * format xx.xx.xx)
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
		cost = Analysis.computeCost(internalSetupValue, externalSetupValue, lifetimeDefault, measure.getInternalWL(), measure.getExternalWL(), measure.getInvestment(),
				measure.getLifetime(), measure.getInternalMaintenance(), measure.getExternalMaintenance(), measure.getRecurrentInvestment());

		// return calculated cost
		return cost;
	}

	/**
	 * computeCost: <br>
	 * Returns the Calculated Cost of a Measure. This method does no more need
	 * the parameter default maintenance, but needs to get the internal and
	 * external maintenance in md as well as the recurrent investment per year
	 * in keuro. <br>
	 * Formula used:<br>
	 * Cost = ((ir * iw) + (er * ew) + in) * ((1.0 / lt) + ((im * ir) + (em *
	 * er)+ ri))<br>
	 * With:<br>
	 * ir: The Internal Setup Rate in Euro per Man Day<br>
	 * iw: The Internal Workload in Man Days<br>
	 * er: The External Setup Rate in Euro per Man Day<br>
	 * ew: The External Workload in Man Days<br>
	 * in: The Investment in kEuro<br>
	 * lt: The Lifetime in Years :: if 0 -> use The Default LifeTime in Years
	 * <br>
	 * im: The Internal MaintenanceRecurrentInvestment in Man Days<br>
	 * em: The External MaintenanceRecurrentInvestment in Man Days<br>
	 * ri: The recurrent Investment in kEuro<br>
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
		// internal setup * internal wokload + external setup * external
		// workload
		// check if lifetime is not 0 -> YES: use default lifetime
		// return calculated cost
		return (((internalSetupRate * internalWorkLoad) + (externalSetupRate * externalWorkLoad) + investment) * (1. / (lifetime == 0 ? lifetimeDefault : lifetime)))
				+ ((internalMaintenance * internalSetupRate) + (externalMaintenance * externalSetupRate) + recurrentInvestment);
	}

	/***********************************************************************************************
	 * Computation of Measure Cost - END
	 **********************************************************************************************/

	public List<NormalStandard> getAllNormalStandards() {
		List<NormalStandard> normalStandards = new ArrayList<NormalStandard>();
		for (AnalysisStandard standard : analysisStandards)
			if (standard instanceof NormalStandard)
				normalStandards.add((NormalStandard) standard);
		return normalStandards;
	}

	public MaturityStandard getMaturityStandard() {
		for (AnalysisStandard standard : analysisStandards)
			if (standard.getStandard().getClass().isAssignableFrom(MaturityStandard.class))
				return (MaturityStandard) standard;
		return null;
	}

	/**
	 * initialisePhases: <br>
	 * Creates the Phase List "usedPhases" from Measures
	 */
	public void initialisePhases() {

		// ****************************************************************
		// * clear phase vector
		// ****************************************************************
		phases.clear();

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Phase> tmpPhases = new ArrayList<Phase>();

		Phase smallest = null;
		List<NormalStandard> normalStandards = this.getAllNormalStandards();
		MaturityStandard maturityStandard = this.getMaturityStandard();

		// ****************************************************************
		// * retrieve all phases and add them to the list of phases
		// * therefore parse all analysisStandard and all measures to check
		// phases
		// ****************************************************************

		// parse all analysisStandard
		for (int i = 0; i < normalStandards.size(); i++) {

			// parse all measures of the standard
			for (int j = 0; j < normalStandards.get(i).getMeasures().size(); j++) {

				int phaseNumber = normalStandards.get(i).getMeasure(j).getPhase().getNumber();

				if (this.getPhaseByNumber(phaseNumber) == null)
					this.addPhase(normalStandards.get(i).getMeasure(j).getPhase());

			}
		}

		if (maturityStandard != null) {

			// parse all measures of the standard
			for (int i = 0; i < maturityStandard.getLevel1Measures().size(); i++) {

				int phaseNumber = maturityStandard.getLevel1Measures().get(i).getPhase().getNumber();

				if (this.getPhaseByNumber(phaseNumber) == null)
					this.addPhase(maturityStandard.getLevel1Measures().get(i).getPhase());

			}
		}

		// ****************************************************************
		// * order phases ascending
		// ****************************************************************

		// check until temporary list is empty (phases are ordered)
		while (this.phases.size() > 0) {

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
			if (phases.get(0) != null) {

				// smallest number for the first one
				smallest = tmpPhases.get(0);

				// ****************************************************************
				// * check all other phases to determine the smallest number
				// ****************************************************************

				// parse all phases and check on the smallest
				for (int i = 0; i < phases.size(); i++) {

					// determine the smallest
					if (phases.get(i).getNumber() < smallest.getNumber()) {

						// current phase is smaller than the intended smallest
						// -> replace the value
						smallest = phases.get(i);
					}
				}

				// ****************************************************************
				// * at this time variable smallest has the smallest phase
				// ****************************************************************

				// ****************************************************************
				// * add phase to the final phase list
				// ****************************************************************

				tmpPhases.add(smallest);

				// ****************************************************************
				// * remove from smallest phase found from the temporary list
				// ****************************************************************
				phases.remove(smallest);
			}
		}

		phases = tmpPhases;

		// for (int i=0; i < usedPhases.size();i++) {
		// System.out.println("ID: " + usedPhases.get(i).getId() +
		// "::: Number: " + usedPhases.get(i).getNumber());
		// }

	}

	/**
	 * getYearsDifferenceBetweenTwoDates: <br>
	 * This method Calculates an Double Value that Indicates the Difference
	 * between two Dates. It is used to Calculate the Size of the Phase in
	 * Years.
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
	 * @return The Value of the Parameter if it exists, or defaultValue if the
	 *         parameter was not found
	 */
	public double getParameter(String type, String name, double defaultValue) {
		return parameters.stream().filter(parameter -> parameter.isMatch(type, name)).map(parameter -> parameter.getValue()).findAny().orElse(defaultValue);
	}

	/**
	 * getParameter: <br>
	 * Returns the Parameter value of a given Parameter.
	 * 
	 * @param parameter
	 *            The Label of the Parameter
	 * @return The Value of the Parameter if it exists, or -1 if the parameter
	 *         was not found
	 */
	public double getParameter(String name) {
		return getParameter(name, -1D);
	}

	/**
	 * getParameter: <br>
	 * Returns the Parameter value of a given Parameter.
	 * 
	 * @param parameter
	 *            The Label of the Parameter
	 * @return The Value of the Parameter if it exists, or -1 if the parameter
	 *         was not found
	 */
	public Parameter getParameterObject(String description) {
		return this.getParameters().stream().filter(parameter -> parameter.getDescription().equals(description)).findAny().orElse(null);
	}

	/**
	 * computeParameterScales: <br>
	 * This method will calculate the bounds of the extended parameters from and
	 * to values. Since CSSF implementation, impact and probability values need
	 * to be calculated using bounds.
	 * 
	 * @throws TrickException
	 */
	public void computeParameterScales() throws TrickException {

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
	 * Returns a Single Item Information from the List of Item Information at
	 * the postion "index"
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
	 * getSelectedAssets: <br>
	 * Description
	 * 
	 * @return
	 */
	public List<Asset> getSelectedAssets() {
		List<Asset> tmpassets = new ArrayList<Asset>();
		for (Asset asset : assets)
			if (asset.isSelected())
				tmpassets.add(asset);
		return tmpassets;
	}

	/**
	 * addAnAsset: <br>
	 * Adds an Asset Object to the List of Assets
	 * 
	 * @param asset
	 *            The asset Object to Add
	 * @throws TrickException
	 */
	public void addAnAsset(Asset asset) throws TrickException {
		if (this.assets.contains(asset))
			throw new TrickException("error.asset.duplicate", String.format("Asset (%s) is duplicated", asset.getName()), asset.getName());
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
	 * Returns a Risk Information from the List of Risk Information at position
	 * "index"
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
	 * getScenarioList: <br>
	 * Returns the Scenario List.
	 * 
	 * @return The Scenario List Object
	 */
	public List<Scenario> findSelectedScenarios() {
		List<Scenario> tmpscenarios = new ArrayList<Scenario>();
		for (Scenario scenario : scenarios)
			if (scenario.isSelected())
				tmpscenarios.add(scenario);
		return tmpscenarios;
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
	 * getAssessments: <br>
	 * Description
	 * 
	 * @return
	 */
	public List<Assessment> getAssessments() {
		return assessments;
	}

	/**
	 * getSelectedAssessments: <br>
	 * Description
	 * 
	 * @return
	 */
	public List<Assessment> getSelectedAssessments() {
		List<Assessment> tmpassessments = new ArrayList<Assessment>();
		for (Assessment assessment : assessments)
			if (assessment.isSelected())
				tmpassessments.add(assessment);
		return tmpassessments;
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
	 * @return the riskProfiles
	 */
	public List<RiskProfile> getRiskProfiles() {
		return riskProfiles;
	}

	/**
	 * @param riskProfiles
	 *            the riskProfiles to set
	 */
	public void setRiskProfiles(List<RiskProfile> riskProfiles) {
		this.riskProfiles = riskProfiles;
	}

	/**
	 * getAnalysisStandard: <br>
	 * Description
	 * 
	 * @param index
	 * @return
	 */
	public AnalysisStandard getAnalysisStandard(int index) {
		return analysisStandards.get(index);
	}

	public AnalysisStandard getAnalysisStandardByStandardId(Integer standardID) {
		for (AnalysisStandard standard : analysisStandards)
			if (standard.getStandard().getId() == standardID)
				return standard;
		return null;
	}

	/**
	 * getAnalysisStandardByLabel: <br>
	 * Description
	 * 
	 * @param label
	 * @return
	 */
	public AnalysisStandard getAnalysisStandardByLabel(String label) {
		for (AnalysisStandard analysisStandard : this.analysisStandards) {
			if (analysisStandard.getStandard().getLabel().equals(label)) {
				return analysisStandard;
			}
		}
		return null;
	}

	/**
	 * getAnalysisStandards: <br>
	 * Description
	 * 
	 * @return
	 */
	public List<AnalysisStandard> getAnalysisStandards() {
		return analysisStandards;
	}

	/**
	 * getAnalysisStandards: <br>
	 * Description
	 * 
	 * @return
	 */
	public List<AnalysisStandard> getAnalysisOnlyStandards() {
		return analysisStandards.stream().filter(standard -> standard.getStandard().isAnalysisOnly()).collect(Collectors.toList());

	}

	/**
	 * getAnalysisStandards: <br>
	 * Description
	 * 
	 * @return
	 */
	public List<Standard> getStandards() {
		return analysisStandards.stream().map(AnalysisStandard::getStandard).collect(Collectors.toList());

	}

	/**
	 * addAnalysisStandard: <br>
	 * Description
	 * 
	 * @param analysisStandard
	 */
	public void addAnalysisStandard(AnalysisStandard analysisStandard) {
		this.analysisStandards.add(analysisStandard);
	}

	/**
	 * addAnalysisStandard: <br>
	 * Description
	 * 
	 * @param analysisStandard
	 */
	public void removeAnalysisStandard(AnalysisStandard analysisStandard) {
		this.analysisStandards.remove(analysisStandard);
	}

	/**
	 * setAnalysisStandards: <br>
	 * Description
	 * 
	 * @param analysisStandards
	 */
	public void setAnalysisStandards(List<AnalysisStandard> analysisStandards) {
		this.analysisStandards = analysisStandards;
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
		return phases.get(index);
	}

	/**
	 * getPhaseByNumber: <br>
	 * Description
	 * 
	 * @param number
	 * @return
	 */
	public Phase getPhaseByNumber(int number) {
		for (Phase phase : phases) {
			if (phase.getNumber() == number)
				return phase;
		}
		return null;
	}

	/**
	 * getPhases: <br>
	 * Description
	 * 
	 * @return
	 */
	public List<Phase> getPhases() {
		return phases;
	}

	/**
	 * setPhases: <br>
	 * Description
	 * 
	 * @param phases
	 */
	public void setPhases(List<Phase> phases) {
		this.phases = phases;
	}

	public void addPhase(Phase phase) {
		if (this.phases == null)
			phases = new ArrayList<Phase>();
		if (!phases.contains(phase))
			phases.add(phase);
		else
			System.err.println("phase not add : " + phase.getNumber());
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
	 * Ticketing project id
	 * 
	 * @return the project
	 */
	public String getProject() {
		return project;
	}

	/**
	 * Ticketing project id
	 * 
	 * @param project
	 *            the project to set
	 */
	public void setProject(String project) {
		this.project = project;
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
	 * @return The List of Action Plan Entries for the requested Action Plan
	 *         Type
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
	 * @return The List of Action Plan Entries for the requested Action Plan
	 *         Type
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
	 * @return The List of Action Plan Entries for the requested Action Plan
	 *         Type
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

	/**
	 * [0] : Simple, [1] : Extended, [2] : Maturity
	 * 
	 * @param parameters
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Parameter>[] SplitParameters(List<Parameter> parameters) {
		List<?>[] splits = new List<?>[3];
		for (int i = 0; i < splits.length; i++)
			splits[i] = new ArrayList<>();
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

	public static List<MaturityParameter> SplitMaturityParameters(List<Parameter> parameters) {
		List<MaturityParameter> splits = new ArrayList<MaturityParameter>();
		for (Parameter parameter : parameters)
			if (parameter instanceof MaturityParameter)
				splits.add((MaturityParameter) parameter);
		return splits;
	}

	/**
	 * [0] : Simple Parameter, [1] : CSSF Parameter, [2] : MAXEFF, [3] : other, [4] : dynamic
	 * 
	 * @param parameters
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Parameter>[] SplitSimpleParameters(List<Parameter> parameters) {
		List<?>[] splits = new List<?>[5];
		for (int i = 0; i < splits.length; i++)
			splits[i] = new ArrayList<>();
		for (Parameter parameter : parameters) {
			if (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME))
				((List<Parameter>) splits[0]).add(parameter);
			else if (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_CSSF_NAME))
				((List<Parameter>) splits[1]).add(parameter);
			else if (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME))
				((List<Parameter>) splits[2]).add(parameter);
			else if (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_DYNAMIC_NAME))
				((List<Parameter>) splits[4]).add(parameter);
			else
				((List<Parameter>) splits[3]).add(parameter);
		}
		return (List<Parameter>[]) splits;
	}

	/**
	 * [0] IMPACT, [1]: PROBA, [2]: DYNAMIC
	 * 
	 * @param parameters
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Parameter>[] SplitExtendedParameters(List< ? extends Parameter> parameters) {
		List<?>[] splits = new List<?>[2];
		for (int i = 0; i < splits.length; i++)
			splits[i] = new ArrayList<>();
		for (Parameter parameter : parameters) {
			if (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
				((List<Parameter>) splits[0]).add(parameter);
			else if (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME))
				((List<Parameter>) splits[1]).add(parameter);
			else if (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_DYNAMIC_NAME))
				((List<Parameter>) splits[2]).add(parameter);
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
				+ ", histories=" + histories + ", language=" + language + ", empty=" + data + ", itemInformations=" + itemInformations + ", parameters=" + parameters + ", assets="
				+ assets + ", riskInformations=" + riskInformations + ", scenarios=" + scenarios + ", assessments=" + assessments + ", analysisStandards=" + analysisStandards
				+ ", phases=" + phases + ", actionPlans=" + actionPlans + ", summaries=" + summaries + ", riskRegisters=" + riskRegisters + "]";
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
	 * Method to identify if this object equals another. Equal means the fields
	 * identifier, version and creationDate are the same.
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
		addUserRight(userRight.getUser(), userRight.getRight());
	}

	/**
	 * addUserRights: <br>
	 * Description
	 * 
	 * @param userRight
	 * @return
	 */
	public UserAnalysisRight addUserRight(User user, AnalysisRight right) {
		if (user == null)
			return null;
		UserAnalysisRight userAnalysisRight = getRightsforUser(user);
		if (userAnalysisRight == null)
			this.userRights.add(userAnalysisRight = new UserAnalysisRight(this, user, right));
		else if (right == null)
			this.userRights.remove(userAnalysisRight);
		else if (!right.equals(userAnalysisRight.getRight()))
			userAnalysisRight.setRight(right);
		return userAnalysisRight;
	}

	/**
	 * getRightsforUser: <br>
	 * Description
	 * 
	 * @param user
	 * @return
	 */
	public UserAnalysisRight getRightsforUser(User user) {
		return getRightsforUserString(user.getLogin());
	}

	/**
	 * getRightsforUser: <br>
	 * Description
	 * 
	 * @param user
	 * @return
	 */
	public UserAnalysisRight getRightsforUserString(String login) {
		return userRights.stream().filter(userRight -> userRight.getUser().getLogin().equals(login)).findAny().orElse(null);
	}

	public AnalysisRight getRightValue(User user) {
		UserAnalysisRight analysisRight = getRightsforUser(user);
		return analysisRight == null ? null : analysisRight.getRight();
	}

	/**
	 * editUserRight: <br>
	 * Description
	 * 
	 * @param user
	 * @param newRight
	 */
	public void editUserRight(User user, AnalysisRight newRight) {
		addUserRight(user, newRight);
	}

	/**
	 * removeRights: <br>
	 * Description
	 * 
	 * @param user
	 * @return
	 */
	public UserAnalysisRight removeRights(User user) {
		UserAnalysisRight userRight = getRightsforUser(user);
		return userRight == null ? null : userRights.remove(userRight) ? userRight : null;
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

	/**
	 * isCssf: <br>
	 * Returns the cssf field value.
	 * 
	 * @return The value of the cssf field
	 */
	public boolean isCssf() {
		return cssf;
	}

	/**
	 * setCssf: <br>
	 * Sets the Field "cssf" with a value.
	 * 
	 * @param cssf
	 *            The Value to set the cssf field
	 */
	public void setCssf(boolean cssf) {
		this.cssf = cssf;
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
			copy.uncertainty = uncertainty;
			copy.cssf = cssf;
		}
		copy.actionPlans = new ArrayList<>();
		copy.riskRegisters = new ArrayList<>();
		copy.summaries = new ArrayList<>();
		copy.id = -1;
		return copy;
	}

	public Phase findPhaseByNumber(int number) {
		for (Phase phase : phases)
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

	public List<Asset> findSelectedAssets() {
		List<Asset> selectedAssets = new ArrayList<>();
		if (assets == null)
			return selectedAssets;
		assets.stream().filter(asset -> asset.isSelected()).forEach(asset -> selectedAssets.add(asset));
		return selectedAssets;
	}

	public Map<Integer, Assessment> findAssessmentByScenarioId(int id) {
		Map<Integer, Assessment> assessmentMap = new LinkedHashMap<>();
		if (assessments == null || assessments.isEmpty())
			return assessmentMap;
		for (Assessment assessment : assessments)
			if (assessment.getScenario().getId() == id)
				assessmentMap.put(assessment.getAsset().getId(), assessment);
		return assessmentMap;
	}

	public List<Scenario> findScenarioSelected() {
		List<Scenario> selectedScenarios = new ArrayList<>();
		if (scenarios == null || scenarios.isEmpty())
			return selectedScenarios;
		for (Scenario scenario : scenarios)
			if (scenario.isSelected())
				selectedScenarios.add(scenario);
		return selectedScenarios;
	}

	public Map<Integer, Assessment> findAssessmentByAssetId(int id) {
		Map<Integer, Assessment> assessmentMap = new LinkedHashMap<>();
		if (assessments == null || assessments.isEmpty())
			return assessmentMap;
		for (Assessment assessment : assessments)
			if (assessment.getAsset().getId() == id)
				assessmentMap.put(assessment.getScenario().getId(), assessment);
		return assessmentMap;
	}

	public static Map<Integer, List<Assessment>> MappedSelectedAssessmentByScenario(List<Assessment> assessments2) {
		Map<Integer, List<Assessment>> mappings = new LinkedHashMap<>();
		for (Assessment assessment : assessments2) {
			if (!assessment.isSelected())
				continue;
			Scenario scenario = assessment.getScenario();
			List<Assessment> assessments = mappings.get(scenario.getId());
			if (assessments == null)
				mappings.put(scenario.getId(), assessments = new ArrayList<Assessment>());
			assessments.add(assessment);
		}
		return mappings;
	}

	public static Map<Integer, List<Assessment>> MappedSelectedAssessmentByAsset(List<Assessment> assessments2) {
		Map<Integer, List<Assessment>> mappings = new LinkedHashMap<>();
		for (Assessment assessment : assessments2) {
			if (!assessment.isSelected())
				continue;
			Asset asset = assessment.getAsset();
			List<Assessment> assessments = mappings.get(asset.getId());
			if (assessments == null)
				mappings.put(asset.getId(), assessments = new ArrayList<Assessment>());
			assessments.add(assessment);
		}
		return mappings;
	}

	/**
	 * Mapping selected assessment by asset and scenario
	 * 
	 * @return Length : 2, 0 : Asset, 1 : Scenario
	 */
	@SuppressWarnings("unchecked")
	public static Map<Integer, List<Assessment>>[] MappedSelectedAssessment(List<Assessment> assessments2) {
		Map<Integer, List<Assessment>>[] mappings = new LinkedHashMap[2];
		for (int i = 0; i < mappings.length; i++)
			mappings[i] = new LinkedHashMap<Integer, List<Assessment>>();
		for (Assessment assessment : assessments2) {
			if (!assessment.isSelected())
				continue;
			Asset asset = assessment.getAsset();
			List<Assessment> assessments = mappings[0].get(asset.getId());
			if (assessments == null)
				mappings[0].put(asset.getId(), assessments = new ArrayList<Assessment>());
			assessments.add(assessment);
			Scenario scenario = assessment.getScenario();
			assessments = mappings[1].get(scenario.getId());
			if (assessments == null)
				mappings[1].put(scenario.getId(), assessments = new ArrayList<Assessment>());
			assessments.add(assessment);
		}
		return mappings;
	}

	public List<? extends Measure> findMeasureByStandard(String string) {
		for (AnalysisStandard analysisStandard : this.analysisStandards)
			if (analysisStandard.getStandard().getLabel().equalsIgnoreCase(string))
				return analysisStandard.getMeasures();
		return null;
	}

	public List<Measure> findMeasuresByActionPlan(ActionPlanMode appn) {
		List<Measure> measures = new ArrayList<Measure>();
		for (ActionPlanEntry planEntry : this.actionPlans)
			if (planEntry.getActionPlanType().getActionPlanMode() == appn)
				measures.add(planEntry.getMeasure());
		return measures;
	}

	public List<Measure> findMeasuresByActionPlanAndNotToImplement(ActionPlanMode appn) {
		List<Measure> measures = new ArrayList<Measure>();
		for (ActionPlanEntry planEntry : this.actionPlans)
			if (planEntry.getActionPlanType().getActionPlanMode() == appn && planEntry.getROI() <= 0.0)
				measures.add(planEntry.getMeasure());
		return measures;
	}

	public List<Phase> findUsablePhase() {
		List<Phase> phases = new ArrayList<Phase>();
		if (this.actionPlans == null || this.actionPlans.isEmpty())
			return phases;
		for (ActionPlanEntry actionPlanEntry : this.actionPlans) {
			Phase phase = actionPlanEntry.getMeasure().getPhase();
			if (phase != null && !phases.contains(phase))
				phases.add(phase);
		}

		for (int i = 0; i < phases.size(); i++) {
			Phase phase = phases.get(i);
			for (int j = 0; j < phases.size(); j++) {
				if (phases.get(j).getNumber() > phase.getNumber()) {
					phases.set(i, phases.get(j));
					phases.set(j, phase);
					phase = phases.get(i);
				}
			}
		}

		return phases;

	}

	public List<Assessment> findAssessmentBySelectedScenario() {
		List<Assessment> assessments = new ArrayList<Assessment>();
		for (Assessment assessment : this.assessments)
			if (assessment.getScenario().isSelected())
				assessments.add(assessment);
		return assessments;
	}

	public List<Assessment> findAssessmentBySelectedAsset() {
		List<Assessment> assessments = new ArrayList<Assessment>();
		for (Assessment assessment : this.assessments)
			if (assessment.getAsset().isSelected())
				assessments.add(assessment);
		return assessments;
	}

	public Map<Integer, Boolean> findIdMeasuresImplementedByActionPlanType(ActionPlanMode appn) {
		Map<Integer, Boolean> actionPlanMeasures = new LinkedHashMap<Integer, Boolean>();
		for (ActionPlanEntry planEntry : this.actionPlans)
			if (planEntry.getActionPlanType().getActionPlanMode() == appn)
				actionPlanMeasures.put(planEntry.getMeasure().getId(), true);
		return actionPlanMeasures;
	}

	public List<Asset> findNoAssetSelected() {
		return this.assets.stream().filter(asset -> !asset.isSelected()).collect(Collectors.toList());
	}

	public List<Assessment> removeAssessment(Asset asset) {
		List<Assessment> assessments = new LinkedList<Assessment>();
		this.assessments.removeIf(assessment -> assessment.getAsset().equals(asset) && assessments.add(assessment));
		return assessments;
	}

	public List<Assessment> removeAssessment(Scenario scenario) {
		List<Assessment> assessments = new LinkedList<Assessment>();
		this.assessments.removeIf(assessment -> assessment.getScenario().equals(scenario) && assessments.add(assessment));
		return assessments;
	}

	public List<AssetType> distinctAssetType() {
		return this.assets.stream().map(asset -> asset.getAssetType()).distinct().collect(Collectors.toList());
	}

	public Standard findStandardByAndAnalysisOnly(Integer idStandard) {
		return this.analysisStandards.stream().filter(analysisStandard -> analysisStandard.getStandard().getId() == idStandard && analysisStandard.isAnalysisOnly())
				.map(analysisStandard -> analysisStandard.getStandard()).findAny().orElse(null);
	}

	public Map<Scenario, List<Assessment>> getAssessmentByScenario() {
		Map<Scenario, List<Assessment>> mapping = new LinkedHashMap<>();
		assessments.forEach(assessment -> {
			List<Assessment> assessments = mapping.get(assessment.getScenario());
			if (assessments == null)
				mapping.put(assessment.getScenario(), assessments = new ArrayList<Assessment>());
			assessments.add(assessment);
		});
		return mapping;
	}

	public Map<Asset, List<Assessment>> getAssessmentByAsset() {
		Map<Asset, List<Assessment>> mapping = new LinkedHashMap<>();
		assessments.forEach(assessment -> {
			List<Assessment> assessments = mapping.get(assessment.getScenario());
			if (assessments == null)
				mapping.put(assessment.getAsset(), assessments = new ArrayList<Assessment>());
			assessments.add(assessment);
		});
		return mapping;
	}

	public void groupAssessmentByAssetAndScenario(Map<Asset, List<Assessment>> assetAssessments, Map<Scenario, List<Assessment>> scenarioAssessments) {
		if (assetAssessments == null || scenarioAssessments == null)
			return;
		assessments.forEach(assessment -> {
			List<Assessment> assessments = assetAssessments.get(assessment.getAsset());
			if (assessments == null)
				assetAssessments.put(assessment.getAsset(), assessments = new ArrayList<Assessment>());
			assessments.add(assessment);
			assessments = assetAssessments.get(assessment.getScenario());
			if (assessments == null)
				scenarioAssessments.put(assessment.getScenario(), assessments = new ArrayList<Assessment>());
			assessments.add(assessment);
		});
	}

	public Scenario findScenario(int idScenario) {
		return scenarios.stream().filter(scenario -> scenario.getId() == idScenario).findAny().orElse(null);
	}

	public Asset findAsset(int idAsset) {
		return assets.stream().filter(asset -> asset.getId() == idAsset).findAny().orElse(null);
	}

	public Assessment findAssessmentByAssetAndScenario(int idAsset, int idScenario) {
		return assessments.stream().filter(assessment -> assessment.is(idAsset, idScenario)).findAny().orElse(null);
	}

	public List<Assessment> findSelectedAssessmentByScenario(int idScenario) {
		return assessments.stream().filter(assessment -> assessment.getScenario().getId() == idScenario).collect(Collectors.toList());
	}

	public List<Assessment> findSelectedAssessmentByAsset(int idAsset) {
		return assessments.stream().filter(assessment -> assessment.getAsset().getId() == idAsset).collect(Collectors.toList());
	}

	public Map<String, Double> mapAcronymToValue() {
		return parameters.stream().filter(parameter -> parameter instanceof ExtendedParameter).map(parameter -> (ExtendedParameter) parameter)
				.collect(Collectors.toMap(ExtendedParameter::getAcronym, ExtendedParameter::getValue));
	}

	public Map<String, ExtendedParameter> mapExtendedParameterByAcronym() {
		return parameters.stream().filter(parameter -> parameter instanceof ExtendedParameter).map(parameter -> (ExtendedParameter) parameter)
				.collect(Collectors.toMap(ExtendedParameter::getAcronym, Function.identity()));
	}

	/**
	 * Retrieve extended parameters: Impact and Probabilities
	 * 
	 * @param probabilities
	 * @param impacts
	 */
	public void groupExtended(List<ExtendedParameter> probabilities, List<ExtendedParameter> impacts) {
		parameters.stream().filter(parameter -> parameter instanceof ExtendedParameter).map(parameter -> (ExtendedParameter) parameter).forEach(parameter -> {
			if (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
				impacts.add(parameter);
			else if (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME))
				probabilities.add(parameter);
		});
	}

	/**
	 * @see RiskProfile#getKey
	 * @return map< RiskProfile::getKey,RiskProfile >
	 */
	public Map<String, RiskProfile> mapRiskProfile() {
		return riskProfiles.stream().collect(Collectors.toMap(RiskProfile::getKey, Function.identity()));
	}

	public Map<String, Assessment> mapAssessment() {
		return assessments.stream().collect(Collectors.toMap(Assessment::getKey, Function.identity()));
	}

	public RiskProfile findRiskProfileByAssetAndScenario(int idAsset, int idScenario) {
		return riskProfiles.stream().filter(riskProfile -> riskProfile.is(idAsset, idScenario)).findAny().orElse(null);
	}

	public RiskRegisterItem findRiskRegisterByAssetAndScenario(int idAsset, int idScenario) {
		return riskRegisters.stream().filter(riskRegister -> riskRegister.is(idAsset, idScenario)).findAny().orElse(null);
	}

	public List<RiskProfile> removeRiskProfile(Asset asset) {
		List<RiskProfile> profiles = new LinkedList<RiskProfile>();
		riskProfiles.removeIf(riskProfile -> riskProfile.getAsset().equals(asset) && profiles.add(riskProfile));
		return profiles;
	}

	public List<RiskProfile> removeRiskProfile(Scenario scenario) {
		List<RiskProfile> profiles = new LinkedList<RiskProfile>();
		riskProfiles.removeIf(riskProfile -> riskProfile.getScenario().equals(scenario) && profiles.add(riskProfile));
		return profiles;
	}

	public List<RiskProfile> findRiskProfileByAsset(Asset asset) {
		return riskProfiles.stream().filter(riskRegister -> riskRegister.getAsset().equals(asset)).collect(Collectors.toList());
	}

	public boolean hasRiskProfile(Asset asset) {
		return riskProfiles.stream().anyMatch(riskRegister -> riskRegister.getAsset().equals(asset));
	}

	public boolean hasRiskProfile(Scenario scenario) {
		return riskProfiles.stream().anyMatch(riskRegister -> riskRegister.getScenario().equals(scenario));
	}

	public Map<Integer, RiskProfile> findRiskProfileByAssetId(int idAsset) {
		return riskProfiles.stream().filter(riskRegister -> riskRegister.getAsset().getId() == idAsset)
				.collect(Collectors.toMap(riskRegister -> riskRegister.getScenario().getId(), Function.identity()));
	}

	public ExtendedParameter findExtendedByTypeAndLevel(String type, int level) {
		return (ExtendedParameter) parameters.stream()
				.filter(parameter -> parameter instanceof ExtendedParameter && parameter.getType().getLabel().equals(type) && ((ExtendedParameter) parameter).getLevel() == level)
				.findAny().orElse(null);
	}

	public Map<Integer, RiskProfile> findRiskProfileByScenarioId(int idScenario) {
		return riskProfiles.stream().filter(riskRegister -> riskRegister.getScenario().getId() == idScenario)
				.collect(Collectors.toMap(riskRegister -> riskRegister.getAsset().getId(), Function.identity()));
	}

	public List<Parameter> findParametersByType(String type) {
		return parameters.stream().filter(parameter -> parameter.isMatch(type)).collect(Collectors.toList());
	}

	public Map<String, Parameter> mapParametersByType(String type) {
		return parameters.stream().filter(parameter -> parameter.isMatch(type)).collect(Collectors.toMap(Parameter::getDescription, Function.identity()));
	}

	public boolean hasParameterType(String type) {
		return parameters.stream().anyMatch(parameter -> parameter.isMatch(type));
	}

	public Parameter findParameter(String type, String description) {
		return parameters.stream().filter(parameter -> parameter.isMatch(type, description)).findAny().orElse(null);
	}

	public Measure findMeasureById(int idMeasure) {
		return analysisStandards.stream().flatMap(measures -> measures.getMeasures().stream()).filter(measure -> measure.getId() == idMeasure).findAny().orElse(null);
	}

	public boolean hasTicket(String idTicket) {
		if (idTicket == null)
			return false;
		return analysisStandards.stream().flatMap(measures -> measures.getMeasures().stream()).anyMatch(measure -> idTicket.equals(measure.getTicket()));
	}

	public boolean hasProject() {
		return !(project == null || project.isEmpty());
	}

	public double getParameter(String name, double defaultValue) {
		return parameters.stream().filter(parameter -> parameter.getDescription().equals(name)).map(parameter -> parameter.getValue()).findAny().orElse(defaultValue);
	}

	/**
	 * Gets the list of all parameters that shall be taken into consideration
	 * whenever an expression (e.g. for likelihood) is evaluated.
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getAllExpressionParametersFromAnalysis(Integer)
	 */
	public List<AcronymParameter> getExpressionParameters() {
		// We assume that all parameters that have an acronym can be used in an
		// expression
		// Maybe we want to change this in the future (checking parameter.type);
		// then this is the place to act.
		// In that case, we must update
		// lu.itrust.business.TS.database.dao.DAOParameter#getAllExpressionParametersFromAnalysis(Integer),
		// so in particular
		// lu.itrust.business.TS.database.dao.hbm.DAOParameterHBM#getAllExpressionParametersFromAnalysis(Integer).
		List<AcronymParameter> expressionParameters = new ArrayList<>();
		for (Parameter parameter : this.parameters)
			if (parameter instanceof AcronymParameter)
				expressionParameters.add((AcronymParameter) parameter);
		return expressionParameters;
	}

	/**
	 * Gets a list of all parameters that are considered to be used as variable
	 * when evaluating an arithmetic expression. The parameter acronym is then
	 * replaced by the value of the respective parameter.
	 * 
	 * @author Steve Muller (SMU), itrust consulting s.à r.l.
	 * @since Jun 10, 2015
	 */
	public List<AcronymParameter> findExpressionParametersByAnalysis() {
		List<AcronymParameter> acronymParameters = new ArrayList<>();
		for (Parameter parameter : parameters) {
			if (parameter instanceof AcronymParameter)
				acronymParameters.add((AcronymParameter) parameter);
		}
		return acronymParameters;
	}

	public Map<String, DynamicParameter> findDynamicParametersByAnalysisAsMap() {
		return parameters.stream().filter(parameter -> parameter instanceof DynamicParameter)
				.collect(Collectors.toMap(parameter -> ((DynamicParameter) parameter).getAcronym(), parameter -> (DynamicParameter) parameter));
	}

	public Map<String, AcronymParameter> mapExpressionParametersByAcronym() {
		return parameters.stream()
				.filter(parameter -> parameter instanceof AcronymParameter)
				.map(parameter -> (AcronymParameter) parameter)
				.collect(Collectors.toMap(AcronymParameter::getAcronym, Function.identity()));
	}

	public Parameter findParameterByTypeAndDescription(String typeLabel, String description) {
		return parameters.stream().filter(p -> p.getType().getLabel().equals(typeLabel) && p.getDescription().equals(description)).findAny().orElse(null);
	}

	public boolean isUserAuthorized(String username, AnalysisRight right) {
		return userRights.stream().anyMatch(userRight -> userRight.getUser().getLogin().equals(username) && UserAnalysisRight.userIsAuthorized(userRight, right));
	}
}
