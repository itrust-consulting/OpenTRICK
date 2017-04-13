package lu.itrust.business.TS.model.analysis;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.component.NaturalOrderComparator;
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
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.IProbabilityParameter;
import lu.itrust.business.TS.model.parameter.impl.DynamicParameter;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.TS.model.parameter.impl.MaturityParameter;
import lu.itrust.business.TS.model.parameter.impl.RiskAcceptanceParameter;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.scale.ScaleType;
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

	/** The Final Action Plan without Phase Computation - Normal */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<ActionPlanEntry> actionPlans = new ArrayList<ActionPlanEntry>();

	/** List of Standards */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@OrderBy("standard")
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<AnalysisStandard> analysisStandards = new ArrayList<AnalysisStandard>();

	/** List of Assessment */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<Assessment> assessments = new ArrayList<Assessment>();

	/** List of assets */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	@OrderBy("value DESC, ALE DESC, name ASC")
	private List<Asset> assets = new ArrayList<Asset>();

	/** Based on analysis */
	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "fiBasedOnAnalysis", nullable = true)
	private Analysis basedOnAnalysis;

	/** Creation Date of the Analysis (and a specific version) */
	@Column(name = "dtCreationDate", nullable = false)
	private Timestamp creationDate;

	/** The Customer object */
	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "fiCustomer", nullable = false)
	private Customer customer;

	/** flag to determine if analysis has data */
	@Column(name = "dtData", nullable = false)
	private boolean data;

	@Column(name = "dtDefaultProfile", nullable = false)
	private boolean defaultProfile = false;

	/** List of History data of the Analysis */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<History> histories = new ArrayList<History>();

	/** Analysis id unsaved value = -1 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAnalysis")
	private int id = -1;

	/** ID of the Analysis */
	@Column(name = "dtIdentifier", nullable = false, length = 23)
	private String identifier;

	/** List of Item Information */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<ItemInformation> itemInformations = new ArrayList<ItemInformation>();

	/** The Label of this Analysis */
	@Column(name = "dtLabel", nullable = false)
	private String label;

	/** Language object of the Analysis */
	@ManyToOne
	@JoinColumn(name = "fiLanguage", nullable = false)
	@Cascade(CascadeType.SAVE_UPDATE)
	@Access(AccessType.FIELD)
	private Language language;

	/** Analysis owner (the one that created or imported it) */
	@ManyToOne
	@JoinColumn(name = "fiOwner", nullable = false)
	@Cascade(CascadeType.SAVE_UPDATE)
	@Access(AccessType.FIELD)
	private User owner;

	/** List of parameters */
	@Transient
	private Map<String, List<? extends IParameter>> parameters = new LinkedHashMap<>();

	/** List of Phases that is used for Action Plan Computation */
	@OneToMany(mappedBy = "analysis")
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	@OrderBy("number")
	private List<Phase> phases = new ArrayList<Phase>();

	@Column(name = "dtProfile", nullable = false)
	private boolean profile = false;

	/** Ticketing project id */
	@Column(name = "dtProject")
	private String project;

	/** List of Risk Information */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<RiskInformation> riskInformations = new ArrayList<RiskInformation>();

	/** List of Assessment */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<RiskProfile> riskProfiles = new ArrayList<RiskProfile>();

	/** The Risk Register (CSSF) */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	@OrderBy("dtNetEvaluationImportance desc, dtExpEvaluationImportance desc, dtRawEvaluationImportance desc")
	private List<RiskRegisterItem> riskRegisters = new ArrayList<RiskRegisterItem>();

	/** List of Scenarios */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	@OrderBy("type,name")
	private List<Scenario> scenarios = new ArrayList<Scenario>();

	@ElementCollection
	@MapKeyColumn(name = "dtName")
	@Column(name = "dtValue")
	@Cascade(CascadeType.ALL)
	@CollectionTable(name = "AnalysisSetting", joinColumns = @JoinColumn(name = "fiAnalysis"))
	private Map<String, String> settings = new LinkedHashMap<>();

	/** The Action Plan Summary without Phase Computation - Normal */
	@OneToMany
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<SummaryStage> summaries = new ArrayList<SummaryStage>();

	@Column(name = "dtType", nullable = false)
	@Enumerated(EnumType.STRING)
	private AnalysisType type = AnalysisType.QUANTITATIVE;

	@Column(name = "dtUncertainty", nullable = false)
	private boolean uncertainty = false;

	/** List of users and their access rights */
	@OneToMany(mappedBy = "analysis")
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<UserAnalysisRight> userRights = new ArrayList<UserAnalysisRight>();

	/** Version of the Analysis */
	@Column(name = "dtVersion", nullable = false, length = 12)
	private String version;

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
	 * addAnalysisStandard: <br>
	 * Description
	 * 
	 * @param analysisStandard
	 */
	public void add(AnalysisStandard analysisStandard) {
		this.analysisStandards.add(analysisStandard);
	}

	/**
	 * addAnAssessment<br>
	 * Adds an Assessment Object to the List of Assessments
	 * 
	 * @param assessment
	 *            The Assessment Object to Add
	 */
	public void add(Assessment assessment) {
		if (this.assessments.contains(assessment))
			throw new IllegalArgumentException("error.assessment.duplicate");
		this.assessments.add(assessment);
	}

	/**
	 * addAnAsset: <br>
	 * Adds an Asset Object to the List of Assets
	 * 
	 * @param asset
	 *            The asset Object to Add
	 * @throws TrickException
	 */
	public void add(Asset asset) throws TrickException {
		if (this.assets.contains(asset))
			throw new TrickException("error.asset.duplicate", String.format("Asset (%s) is duplicated", asset.getName()), asset.getName());
		this.assets.add(asset);
	}

	/**
	 * addAParameter: <br>
	 * Adds a SimpleParameter to the List of Parameters
	 * 
	 * @param param
	 *            The SimpleParameter object to Add
	 */
	@SuppressWarnings("unchecked")
	public boolean add(IParameter param) {
		List<IParameter> parameters = (List<IParameter>) this.parameters.get(param.getGroup());
		if (parameters == null)
			this.parameters.put(param.getGroup(), parameters = new ArrayList<>());
		return parameters.add(param);
	}

	/***********************************************************************************************
	 * Computation of Measure Cost - BEGIN
	 **********************************************************************************************/

	/**
	 * addAnItemInformation: <br>
	 * Adds an Item Information Object to the List of Item Information
	 * 
	 * @param iteminformation
	 *            The Item Information Object to Add
	 */
	public void add(ItemInformation itemInformation) {
		this.itemInformations.add(itemInformation);
	}

	public void add(Phase phase) {
		if (this.phases == null)
			phases = new ArrayList<Phase>();
		if (!phases.contains(phase))
			phases.add(phase);
		else
			System.err.println("phase not add : " + phase.getNumber());
	}

	/**
	 * addARiskInformation: <br>
	 * Adds an Risk Information Object to the List of Risk Information
	 * 
	 * @param riskInfo
	 *            The Risk Information Object to Add
	 */
	public void add(RiskInformation riskInfo) {
		this.riskInformations.add(riskInfo);
	}

	/**
	 * setAScenario: <br>
	 * Adds a Scenario Object to the List of Scenarios
	 * 
	 * @param scenario
	 *            The Scenario Object to Add
	 */
	public void add(Scenario scenario) {
		if (this.scenarios.contains(scenario)) {
			throw new IllegalArgumentException("error.scenario.duplicate");
		}
		this.scenarios.add(scenario);
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

	/***********************************************************************************************
	 * Getter's and Setter's
	 **********************************************************************************************/

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
	 * addUserRights: <br>
	 * Description
	 * 
	 * @param userRight
	 */
	public void addUserRight(UserAnalysisRight userRight) {
		addUserRight(userRight.getUser(), userRight.getRight());
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

	public List<AssetType> distinctAssetType() {
		return this.assets.stream().map(asset -> asset.getAssetType()).distinct().collect(Collectors.toList());
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
		analysis.settings = new LinkedHashMap<>(this.settings);
		analysis.id = -1;
		return analysis;
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
			copy.type = type;
		}
		copy.actionPlans = new ArrayList<>();
		copy.riskRegisters = new ArrayList<>();
		copy.summaries = new ArrayList<>();
		copy.settings = new LinkedHashMap<>(settings);
		copy.id = -1;
		return copy;
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

	public Assessment findAssessmentByAssetAndScenario(int idAsset, int idScenario) {
		return assessments.stream().filter(assessment -> assessment.is(idAsset, idScenario)).findAny().orElse(null);
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

	public Map<Integer, Assessment> findAssessmentByAssetId(int id) {
		Map<Integer, Assessment> assessmentMap = new LinkedHashMap<>();
		if (assessments == null || assessments.isEmpty())
			return assessmentMap;
		for (Assessment assessment : assessments)
			if (assessment.getAsset().getId() == id)
				assessmentMap.put(assessment.getScenario().getId(), assessment);
		return assessmentMap;
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

	public List<Assessment> findAssessmentBySelectedAsset() {
		List<Assessment> assessments = new ArrayList<Assessment>();
		for (Assessment assessment : this.assessments)
			if (assessment.getAsset().isSelected())
				assessments.add(assessment);
		return assessments;
	}

	public List<Assessment> findAssessmentBySelectedScenario() {
		List<Assessment> assessments = new ArrayList<Assessment>();
		for (Assessment assessment : this.assessments)
			if (assessment.getScenario().isSelected())
				assessments.add(assessment);
		return assessments;
	}

	public Asset findAsset(int idAsset) {
		return assets.stream().filter(asset -> asset.getId() == idAsset).findAny().orElse(null);
	}

	public List<IParameter> findByGroup(String... groups) {
		List<IParameter> parameters = new LinkedList<>();
		for (String group : groups) {
			if (this.parameters.containsKey(group))
				parameters.addAll(this.parameters.get(group));
		}
		return parameters;
	}

	public Map<String, DynamicParameter> findDynamicParametersByAnalysisAsMap() {
		return getDynamicParameters().stream().collect(Collectors.toMap(parameter -> ((DynamicParameter) parameter).getAcronym(), parameter -> (DynamicParameter) parameter));
	}

	public Map<Integer, Boolean> findIdMeasuresImplementedByActionPlanType(ActionPlanMode appn) {
		Map<Integer, Boolean> actionPlanMeasures = new LinkedHashMap<Integer, Boolean>();
		for (ActionPlanEntry planEntry : this.actionPlans)
			if (planEntry.getActionPlanType().getActionPlanMode() == appn)
				actionPlanMeasures.put(planEntry.getMeasure().getId(), true);
		return actionPlanMeasures;
	}

	public LikelihoodParameter findLikelihoodByTypeAndLevel(int level) {
		return getLikelihoodParameters().stream().filter(parameter -> parameter.getLevel() == level).findAny().orElse(null);
	}

	public Measure findMeasureById(int idMeasure) {
		return analysisStandards.stream().flatMap(measures -> measures.getMeasures().stream()).filter(measure -> measure.getId() == idMeasure).findAny().orElse(null);
	}

	public List<? extends Measure> findMeasureByStandard(String standard) {
		for (AnalysisStandard analysisStandard : this.analysisStandards) {
			if (analysisStandard.getStandard().is(standard))
				return analysisStandard.getMeasures();
		}
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

	public List<Asset> findNoAssetSelected() {
		return this.assets.stream().filter(asset -> !asset.isSelected()).collect(Collectors.toList());
	}

	public IParameter findParameter(String type, String description) {
		return this.parameters.values().stream().flatMap(paramters -> paramters.stream()).filter(parameter -> parameter.isMatch(type, description)).findAny().orElse(null);
	}

	public IProbabilityParameter findParameterByTypeAndAcronym(String type, String acronym) {
		return (IProbabilityParameter) this.parameters.values().stream().flatMap(paramters -> paramters.stream())
				.filter(parameter -> (parameter instanceof IProbabilityParameter) && parameter.isMatch(type) && ((IProbabilityParameter) parameter).getAcronym().equals(acronym))
				.findAny().orElse(null);
	}

	public IParameter findParameterByTypeAndDescription(String typeLabel, String description) {
		return this.parameters.values().stream().flatMap(paramters -> paramters.stream()).filter(p -> p.getTypeName().equals(typeLabel) && p.getDescription().equals(description))
				.findAny().orElse(null);
	}

	public List<? extends IParameter> findParametersByType(String type) {
		return this.parameters.values().stream().flatMap(paramters -> paramters.stream()).filter(parameter -> parameter.isMatch(type)).collect(Collectors.toList());
	}

	public double findParameterValueByTypeAndAcronym(String type, String acronym) {
		return findParameterValueByTypeAndAcronym(type, acronym, 0D);
	}

	public Double findParameterValueByTypeAndAcronym(String type, String acronym, Double defaultValue) {
		IProbabilityParameter parameter = findParameterByTypeAndAcronym(type, acronym);
		return parameter == null ? defaultValue : parameter.getValue().doubleValue();
	}

	public Phase findPhaseByNumber(int number) {
		return phases.stream().filter(phase -> phase.getNumber() == number).findAny().orElse(null);
	}

	public List<RiskProfile> findRiskProfileByAsset(Asset asset) {
		return riskProfiles.stream().filter(riskRegister -> riskRegister.getAsset().equals(asset)).collect(Collectors.toList());
	}

	public RiskProfile findRiskProfileByAssetAndScenario(int idAsset, int idScenario) {
		return riskProfiles.stream().filter(riskProfile -> riskProfile.is(idAsset, idScenario)).findAny().orElse(null);
	}

	public Map<Integer, RiskProfile> findRiskProfileByAssetId(int idAsset) {
		return riskProfiles.stream().filter(riskRegister -> riskRegister.getAsset().getId() == idAsset)
				.collect(Collectors.toMap(riskRegister -> riskRegister.getScenario().getId(), Function.identity()));
	}

	public Map<Integer, RiskProfile> findRiskProfileByScenarioId(int idScenario) {
		return riskProfiles.stream().filter(riskRegister -> riskRegister.getScenario().getId() == idScenario)
				.collect(Collectors.toMap(riskRegister -> riskRegister.getAsset().getId(), Function.identity()));
	}

	public RiskRegisterItem findRiskRegisterByAssetAndScenario(int idAsset, int idScenario) {
		return riskRegisters.stream().filter(riskRegister -> riskRegister.is(idAsset, idScenario)).findAny().orElse(null);
	}

	public Scenario findScenario(int idScenario) {
		return scenarios.stream().filter(scenario -> scenario.getId() == idScenario).findAny().orElse(null);
	}

	public List<Scenario> findScenarioSelected() {
		if (scenarios == null)
			return Collections.emptyList();
		return scenarios.stream().filter(Scenario::isSelected).collect(Collectors.toList());

	}

	public Map<Asset, List<Assessment>> findSelectedAssessmentByAsset() {
		Map<Asset, List<Assessment>> mapping = new LinkedHashMap<>();
		assessments.stream().filter(Assessment::isSelected).sorted((a1, a2) -> {
			return NaturalOrderComparator.compareTo(a1.getAsset().getName(), a2.getAsset().getName());
		}).forEach(assessment -> {
			List<Assessment> assessments = mapping.get(assessment.getAsset());
			if (assessments == null)
				mapping.put(assessment.getAsset(), assessments = new LinkedList<Assessment>());
			assessments.add(assessment);
		});
		return mapping;
	}

	public List<Assessment> findSelectedAssessmentByAsset(int idAsset) {
		return assessments.stream().filter(assessment -> assessment.isSelected() && assessment.getAsset().getId() == idAsset).collect(Collectors.toList());
	}

	public List<Assessment> findSelectedAssessmentByScenario(int idScenario) {
		return assessments.stream().filter(assessment -> assessment.isSelected() && assessment.getScenario().getId() == idScenario).collect(Collectors.toList());
	}

	public List<Asset> findSelectedAsset() {
		List<Asset> assets = new LinkedList<Asset>();
		for (Asset asset : this.assets) {
			if (asset.isSelected())
				assets.add(asset);
		}
		return assets;
	}

	public List<Asset> findSelectedAssets() {
		if (assets == null)
			return Collections.emptyList();
		return assets.stream().filter(Asset::isSelected).collect(Collectors.toList());

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

	public SimpleParameter findSimpleParameter(String type, String description) {
		return getSimpleParameters().stream().filter(parameter -> parameter.isMatch(type, description)).findAny().orElse(null);
	}

	public Standard findStandardByAndAnalysisOnly(Integer idStandard) {
		return this.analysisStandards.stream().filter(analysisStandard -> analysisStandard.getStandard().getId() == idStandard && analysisStandard.isAnalysisOnly())
				.map(analysisStandard -> analysisStandard.getStandard()).findAny().orElse(null);
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
	 * getAnalysisStandard: <br>
	 * Description
	 * 
	 * @param index
	 * @return
	 */
	public AnalysisStandard getAnalysisStandard(int index) {
		return analysisStandards.get(index);
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
			if (analysisStandard.getStandard().is(label)) {
				return analysisStandard;
			}
		}
		return null;
	}

	public AnalysisStandard getAnalysisStandardByStandardId(Integer standardID) {
		for (AnalysisStandard standard : analysisStandards)
			if (standard.getStandard().getId() == standardID)
				return standard;
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

	public Map<Asset, List<Assessment>> getAssessmentByAsset() {
		Map<Asset, List<Assessment>> mapping = new LinkedHashMap<>();
		assessments.forEach(assessment -> {
			List<Assessment> assessments = mapping.get(assessment.getAsset());
			if (assessments == null)
				mapping.put(assessment.getAsset(), assessments = new ArrayList<Assessment>());
			assessments.add(assessment);
		});
		return mapping;
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
	 * getAsset: <br>
	 * Returns a list of Assets.
	 * 
	 * @return The List of Asset Objects
	 */
	public List<Asset> getAssets() {
		return assets;
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

	public List<IBoundedParameter> getBoundedParamters() {
		List<IBoundedParameter> parameters = getImpactParameters().stream().collect(Collectors.toList());
		parameters.addAll(getLikelihoodParameters());
		return parameters;
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
	 * getCustimerId: <br>
	 * Returns the "customer" field value
	 * 
	 * @return The Customer ID
	 */
	public Customer getCustomer() {
		return customer;
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

	@OneToMany
	@JoinColumn(name = "fiAnalysis")
	@Access(AccessType.PROPERTY)
	@Cascade(CascadeType.ALL)
	@SuppressWarnings("unchecked")
	public List<DynamicParameter> getDynamicParameters() {
		List<DynamicParameter> parameters = (List<DynamicParameter>) this.parameters.get(Constant.PARAMETER_CATEGORY_PROBABILITY_DYNAMIC);
		if (parameters == null)
			this.parameters.put(Constant.PARAMETER_CATEGORY_PROBABILITY_DYNAMIC, parameters = new ArrayList<>());
		return parameters;
	}

	/**
	 * Gets the list of all parameters that shall be taken into consideration
	 * whenever an expression (e.g. for likelihood) is evaluated.<br>
	 * <b>Updated by eomar 06/10/2016: <br>
	 * Add filter by Type: Dynamic + likelihood</b>
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#findExpressionParameterByAnalysis(Integer)
	 */
	public List<IProbabilityParameter> getExpressionParameters() {
		// We assume that all parameters that have an acronym can be used in an
		// expression
		// Maybe we want to change this in the future (checking parameter.type);
		// then this is the place to act.
		// In that case, we must update
		// lu.itrust.business.TS.database.dao.DAOParameter#getAllExpressionParametersFromAnalysis(Integer),
		// so in particular
		// lu.itrust.business.TS.database.dao.hbm.DAOParameterHBM#getAllExpressionParametersFromAnalysis(Integer).
		return this.parameters.entrySet().stream()
				.filter(entry -> entry.getKey().equals(Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD)
						|| entry.getKey().equals(Constant.PARAMETER_CATEGORY_PROBABILITY_DYNAMIC))
				.flatMap(entry -> entry.getValue().stream()).map(parameter -> (IProbabilityParameter) parameter).collect(Collectors.toList());
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
	 * getId: <br>
	 * Returns the id field value.
	 * 
	 * @return The value of the id field
	 */
	public int getId() {
		return id;
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

	@OneToMany
	@JoinColumn(name = "fiAnalysis")
	@Access(AccessType.PROPERTY)
	@Cascade(CascadeType.ALL)
	@OrderBy("type,level")
	@SuppressWarnings("unchecked")
	public List<ImpactParameter> getImpactParameters() {
		List<ImpactParameter> impacts = (List<ImpactParameter>) parameters.get(Constant.PARAMETER_CATEGORY_IMPACT);
		if (impacts == null)
			parameters.put(Constant.PARAMETER_CATEGORY_IMPACT, impacts = new ArrayList<>());
		return impacts;
	}

	public List<ScaleType> getImpacts() {
		return getImpactParameters().stream().map(ImpactParameter::getType).distinct().sorted((s1, s2) -> s1.getName().compareTo(s2.getName())).collect(Collectors.toList());
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
	 * getLabel: <br>
	 * Returns the "label" field value
	 * 
	 * @return The Analysis Label
	 */
	public String getLabel() {
		return label;
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
	 * getLastHistory: <br>
	 * returns last history
	 * 
	 * @return last history
	 */
	public History getLastHistory() {
		return histories == null || histories.isEmpty() ? null : histories.get(histories.size() - 1);
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

	@OneToMany
	@JoinColumn(name = "fiAnalysis")
	@Access(AccessType.PROPERTY)
	@Cascade(CascadeType.ALL)
	@OrderBy("level")
	@SuppressWarnings("unchecked")
	public List<LikelihoodParameter> getLikelihoodParameters() {
		List<LikelihoodParameter> parameters = (List<LikelihoodParameter>) this.parameters.get(Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD);
		if (parameters == null)
			this.parameters.put(Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD, parameters = new ArrayList<>());
		return parameters;
	}

	@OneToMany
	@JoinColumn(name = "fiAnalysis")
	@Access(AccessType.PROPERTY)
	@Cascade(CascadeType.ALL)
	@SuppressWarnings("unchecked")
	public List<MaturityParameter> getMaturityParameters() {
		List<MaturityParameter> parameters = (List<MaturityParameter>) this.parameters.get(Constant.PARAMETER_CATEGORY_MATURITY);
		if (parameters == null)
			this.parameters.put(Constant.PARAMETER_CATEGORY_MATURITY, parameters = new ArrayList<>());
		return parameters;
	}

	public MaturityStandard getMaturityStandard() {
		return (MaturityStandard) analysisStandards.stream().filter(analysisStandard -> analysisStandard instanceof MaturityStandard).findAny().orElse(null);
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
	 * getParameter: <br>
	 * Returns the SimpleParameter value of a given SimpleParameter.
	 * 
	 * @param parameter
	 *            The Label of the SimpleParameter
	 * @return The Value of the SimpleParameter if it exists, or -1 if the
	 *         parameter was not found
	 */
	public double getParameter(String name) {
		return getParameter(name, -1D);
	}

	public double getParameter(String name, double defaultValue) {
		return this.parameters.values().stream().flatMap(paramters -> paramters.stream()).filter(parameter -> parameter.getDescription().equals(name))
				.map(parameter -> parameter.getValue().doubleValue()).findAny().orElse(defaultValue);
	}

	/**
	 * getParameter: <br>
	 * Returns the SimpleParameter value of a given SimpleParameter.
	 * 
	 * @param parameter
	 *            The Label of the SimpleParameter
	 * @return The Value of the SimpleParameter if it exists, or defaultValue if
	 *         the parameter was not found
	 */
	public double getParameter(String type, String name, double defaultValue) {
		return parameters.values().stream().flatMap(parametersList -> parametersList.stream()).filter(parameter -> parameter.isMatch(type, name))
				.map(parameter -> parameter.getValue().doubleValue()).findAny().orElse(defaultValue);
	}

	/**
	 * getParameterList: <br>
	 * Returns the SimpleParameter List.
	 * 
	 * @return The SimpleParameter Object List
	 */
	public Map<String, List<? extends IParameter>> getParameters() {
		return parameters;
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
	 * Ticketing project id
	 * 
	 * @return the project
	 */
	public String getProject() {
		return project;
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

	@OneToMany
	@JoinColumn(name = "fiAnalysis")
	@Access(AccessType.PROPERTY)
	@Cascade(CascadeType.ALL)
	@OrderBy("value,color, label, description")
	@SuppressWarnings("unchecked")
	public List<RiskAcceptanceParameter> getRiskAcceptanceParameters() {
		List<RiskAcceptanceParameter> parameters = (List<RiskAcceptanceParameter>) this.parameters.get(Constant.PARAMETER_CATEGORY_RISK_ACCEPTANCE);
		if (parameters == null)
			this.parameters.put(Constant.PARAMETER_CATEGORY_RISK_ACCEPTANCE, parameters = new ArrayList<>());
		return parameters;
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
	 * @return the riskProfiles
	 */
	public List<RiskProfile> getRiskProfiles() {
		return riskProfiles;
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
	 * getScenarioList: <br>
	 * Returns the Scenario List.
	 * 
	 * @return The Scenario List Object
	 */
	public List<Scenario> getScenarios() {
		return scenarios;
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

	public <T> T getSetting(AnalysisSetting setting) {
		return findSetting(setting, settings.get(setting.name()));
	}

	@OneToMany
	@JoinColumn(name = "fiAnalysis")
	@Access(AccessType.PROPERTY)
	@Cascade(CascadeType.ALL)
	@SuppressWarnings("unchecked")
	public List<SimpleParameter> getSimpleParameters() {
		List<SimpleParameter> parameters = (List<SimpleParameter>) this.parameters.get(Constant.PARAMETER_CATEGORY_SIMPLE);
		if (parameters == null)
			this.parameters.put(Constant.PARAMETER_CATEGORY_SIMPLE, parameters = new ArrayList<>());
		return parameters;
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
	 * @return the type
	 */
	public AnalysisType getType() {
		return type;
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
	 * getVersion: <br>
	 * Returns the "version" field value
	 * 
	 * @return The Analysis Version
	 */
	public String getVersion() {
		return version;
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

	/**
	 * Retrieve extended parameters: Impact and Probabilities
	 * 
	 * @param probabilities
	 * @param impacts
	 */
	public void groupExtended(List<LikelihoodParameter> probabilities, List<ImpactParameter> impacts) {
		this.parameters.values().stream().flatMap(paramters -> paramters.stream()).filter(parameter -> parameter instanceof IBoundedParameter)
				.map(parameter -> (IBoundedParameter) parameter).forEach(parameter -> {
					if ((parameter instanceof ImpactParameter) && parameter.getTypeName().equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
						impacts.add((ImpactParameter) parameter);
					else if (parameter instanceof LikelihoodParameter)
						probabilities.add((LikelihoodParameter) parameter);
				});
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

	public boolean hasParameterType(String type) {
		return this.parameters.values().stream().flatMap(paramters -> paramters.stream()).anyMatch(parameter -> parameter.isMatch(type));
	}

	public boolean hasPhase(int number) {
		return findPhaseByNumber(number) != null;
	}

	public boolean hasProject() {
		return !(project == null || project.isEmpty());
	}

	public boolean hasRiskProfile(Asset asset) {
		return riskProfiles.stream().anyMatch(riskRegister -> riskRegister.getAsset().equals(asset));
	}

	public boolean hasRiskProfile(Scenario scenario) {
		return riskProfiles.stream().anyMatch(riskRegister -> riskRegister.getScenario().equals(scenario));
	}

	public boolean hasTicket(String idTicket) {
		if (idTicket == null)
			return false;
		return analysisStandards.stream().flatMap(measures -> measures.getMeasures().stream()).anyMatch(measure -> idTicket.equals(measure.getTicket()));
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

				if (this.findPhaseByNumber(phaseNumber) == null)
					this.add(normalStandards.get(i).getMeasure(j).getPhase());

			}
		}

		if (maturityStandard != null) {

			// parse all measures of the standard
			for (int i = 0; i < maturityStandard.getLevel1Measures().size(); i++) {

				int phaseNumber = maturityStandard.getLevel1Measures().get(i).getPhase().getNumber();

				if (this.findPhaseByNumber(phaseNumber) == null)
					this.add(maturityStandard.getLevel1Measures().get(i).getPhase());

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
	 * isDefaultProfile: <br>
	 * Returns the defaultProfile field value.
	 * 
	 * @return The value of the defaultProfile field
	 */
	public boolean isDefaultProfile() {
		return defaultProfile;
	}

	/**
	 * @return the profile
	 */
	public boolean isProfile() {
		return profile;
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

	public boolean isUserAuthorized(String username, AnalysisRight right) {
		return userRights.stream().anyMatch(userRight -> userRight.getUser().getLogin().equals(username) && UserAnalysisRight.userIsAuthorized(userRight, right));
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
			if (uar.getUser().equals(user))
				return UserAnalysisRight.userIsAuthorized(uar, right);
		}
		return false;
	}

	public Map<String, Assessment> mapAssessment() {
		return assessments.stream().collect(Collectors.toMap(Assessment::getKey, Function.identity()));
	}

	/**
	 * @see RiskProfile#getKey
	 * @return map< RiskProfile::getKey,RiskProfile >
	 */
	public Map<String, RiskProfile> mapRiskProfile() {
		return riskProfiles.stream().collect(Collectors.toMap(RiskProfile::getKey, Function.identity()));
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

	public List<Scenario> removeFromScenario(Asset asset) {
		return this.scenarios.stream().filter(scenario -> scenario.getLinkedAssets().remove(asset)).collect(Collectors.toList());
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
	 * setAnalysisStandards: <br>
	 * Description
	 * 
	 * @param analysisStandards
	 */
	public void setAnalysisStandards(List<AnalysisStandard> analysisStandards) {
		this.analysisStandards = analysisStandards;
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
	 * setDefaultProfile: <br>
	 * Sets the Field "defaultProfile" with a value.
	 * 
	 * @param defaultProfile
	 *            The Value to set the defaultProfile field
	 */
	public void setDefaultProfile(boolean defaultProfile) {
		this.defaultProfile = defaultProfile;
	}

	public void setDynamicParameters(List<DynamicParameter> parameters) {
		this.parameters.put(Constant.PARAMETER_CATEGORY_PROBABILITY_DYNAMIC, parameters);
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
	 * setId: <br>
	 * Sets the Field "id" with a value.
	 * 
	 * @param id
	 *            The Value to set the id field
	 */
	public void setId(int id) {
		this.id = id;
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

	public void setImpactParameters(List<ImpactParameter> impacts) {
		parameters.put(Constant.PARAMETER_CATEGORY_IMPACT, impacts);
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
	 * setLanguage: <br>
	 * Sets the "language" field with a Language Object
	 * 
	 * @param language
	 *            The Object to set the Language
	 */
	public void setLanguage(Language language) {
		this.language = language;
	}

	public void setLikelihoodParameters(List<LikelihoodParameter> parameters) {
		this.parameters.put(Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD, parameters);
	}

	public void setMaturityParameters(List<MaturityParameter> parameters) {
		this.parameters.put(Constant.PARAMETER_CATEGORY_MATURITY, parameters);
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
	 * setParameters: <br>
	 * Adds a SimpleParameter to the List of Parameters
	 * 
	 * @param params
	 *            The SimpleParameter object to Add
	 */
	public void setParameters(Map<String, List<? extends IParameter>> params) {
		this.parameters = params;
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

	/**
	 * @param profile
	 *            the profile to set
	 */
	public void setProfile(boolean profile) {
		this.profile = profile;
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

	public void setRiskAcceptanceParameters(List<RiskAcceptanceParameter> parameters) {
		this.parameters.put(Constant.PARAMETER_CATEGORY_RISK_ACCEPTANCE, parameters);
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
	 * @param riskProfiles
	 *            the riskProfiles to set
	 */
	public void setRiskProfiles(List<RiskProfile> riskProfiles) {
		this.riskProfiles = riskProfiles;
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
	 * setScenarios: <br>
	 * Sets the list of Scenario Objects.
	 * 
	 * @param scenarios
	 *            The List of Scenarios
	 */
	public void setScenarios(List<Scenario> scenarios) {
		this.scenarios = scenarios;
	}

	public void setSetting(String name, Object value) {
		if (name == null)
			return;
		else if (value == null)
			this.settings.remove(name);
		else
			this.settings.put(name, String.valueOf(value));
	}

	public void setSimpleParameters(List<SimpleParameter> parameters) {
		this.parameters.put(Constant.PARAMETER_CATEGORY_SIMPLE, parameters);
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
	 * @param type
	 *            the type to set
	 */
	public void setType(AnalysisType type) {
		this.type = type;
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

	@SuppressWarnings("unchecked")
	public static <T> T findSetting(AnalysisSetting setting, String value) {
		try {
			if (value == null)
				return (T) setting.getDefaultValue();
			return (T) ParseSettingValue(value, setting.getType());
		} catch (Exception e) {
			return (T) setting.getDefaultValue();
		}
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
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.TYPE_PROFIT_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.NAME_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.PRESENTATION_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.SECTOR_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.RESPONSIBLE_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.STAFF_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.ACTIVITIES_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.EXCLUDED_ASSETS, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.OCCUPATION, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.FUNCTIONAL, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.JURIDIC, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.POL_ORGANISATION, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.MANAGEMENT_ORGANISATION, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.PREMISES, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.REQUIREMENTS, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.EXPECTATIONS, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.ENVIRONMENT, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.INTERFACE, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.STRATEGIC, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.PROCESSUS_DEVELOPMENT, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.STAKEHOLDER_IDENTIFICATION, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.ROLE_RESPONSABILITY, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.STAKEHOLDER_RELATION, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.ESCALATION_WAY, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.DOCUMENT_CONSERVE, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
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

	@SuppressWarnings("unchecked")
	public static <T> T ParseSettingValue(String value, Class<T> type) {
		if (String.class.equals(type))
			return (T) value;
		else if (Boolean.class.equals(type))
			return (T) Boolean.valueOf(value);
		else if (Integer.class.equals(type))
			return (T) (Integer) Integer.parseInt(value);
		else if (Double.class.equals(type))
			return (T) (Double) Double.parseDouble(value);
		else if (Long.class.equals(type))
			return (T) (Long) Long.parseLong(value);
		else if (Float.class.equals(type))
			return (T) (Float) Float.parseFloat(value);
		return (T) value;
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
	 * Retrieves parameter by type
	 * 
	 * @param parameters
	 * @return Map<String, SimpleParameter>
	 */
	public static Map<String, List<IParameter>> SplitParameters(List<? extends IParameter> parameters) {
		Map<String, List<IParameter>> mappedParameters = new LinkedHashMap<>();
		parameters.stream().forEach(parameter -> {
			List<IParameter> currentParameters = mappedParameters.get(parameter.getTypeName());
			if (currentParameters == null)
				mappedParameters.put(parameter.getTypeName(), currentParameters = new ArrayList<>());
			currentParameters.add(parameter);
		});

		return mappedParameters;

	}

	/**
	 * Retrieves parameter by type
	 * 
	 * @param parameters
	 * @return Map<String, SimpleParameter>
	 */
	public static Map<String, List<IParameter>> SplitParameters(Map<String, List<? extends IParameter>> parameters) {
		Map<String, List<IParameter>> mappedParameters = new LinkedHashMap<>();
		parameters.values().parallelStream().flatMap(list -> list.parallelStream()).forEach(parameter -> {
			List<IParameter> currentParameters = mappedParameters.get(parameter.getTypeName());
			if (currentParameters == null)
				mappedParameters.put(parameter.getTypeName(), currentParameters = new ArrayList<>());
			currentParameters.add(parameter);
		});

		return mappedParameters;

	}

}
