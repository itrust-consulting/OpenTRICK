package lu.itrust.business.ts.model.analysis;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKey;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.actionplan.ActionPlanEntry;
import lu.itrust.business.ts.model.actionplan.ActionPlanMode;
import lu.itrust.business.ts.model.actionplan.summary.SummaryStage;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.asset.AssetType;
import lu.itrust.business.ts.model.cssf.RiskProfile;
import lu.itrust.business.ts.model.cssf.RiskRegisterItem;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.general.Phase;
import lu.itrust.business.ts.model.general.document.impl.SimpleDocument;
import lu.itrust.business.ts.model.general.document.impl.SimpleDocumentType;
import lu.itrust.business.ts.model.history.History;
import lu.itrust.business.ts.model.ilr.AssetNode;
import lu.itrust.business.ts.model.iteminformation.ItemInformation;
import lu.itrust.business.ts.model.parameter.IAcronymParameter;
import lu.itrust.business.ts.model.parameter.IBoundedParameter;
import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.parameter.impl.DynamicParameter;
import lu.itrust.business.ts.model.parameter.impl.IlrSoaScaleParameter;
import lu.itrust.business.ts.model.parameter.impl.ImpactParameter;
import lu.itrust.business.ts.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.ts.model.parameter.impl.MaturityParameter;
import lu.itrust.business.ts.model.parameter.impl.RiskAcceptanceParameter;
import lu.itrust.business.ts.model.parameter.impl.SimpleParameter;
import lu.itrust.business.ts.model.riskinformation.RiskInformation;
import lu.itrust.business.ts.model.scale.ScaleType;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.MaturityStandard;
import lu.itrust.business.ts.model.standard.NormalStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.usermanagement.User;

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
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "dtIdentifier", "dtVersion" }))
public class Analysis implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The Final Action Plan without Phase Computation - Normal */
	@OneToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<ActionPlanEntry> actionPlans = new ArrayList<>();

	/** List of Standards */
	@OneToMany
	@MapKey(name = "name")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@OrderBy("name")
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private Map<String, AnalysisStandard> analysisStandards = new LinkedHashMap<>();

	/** List of Assessment */
	@OneToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<Assessment> assessments = new ArrayList<>();

	/** List of assets */
	@OneToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	@OrderBy("value DESC, ALE DESC, name ASC")
	private List<Asset> assets = new ArrayList<>();

	/** Based on analysis */
	@ManyToOne(fetch = FetchType.LAZY)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiBasedOnAnalysis", nullable = true)
	@Cascade(CascadeType.SAVE_UPDATE)
	@Access(AccessType.FIELD)
	private Analysis basedOnAnalysis;

	/** Creation Date of the Analysis (and a specific version) */
	@Column(name = "dtCreationDate", nullable = false)
	private Timestamp creationDate;

	/** The Customer object */
	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade(CascadeType.SAVE_UPDATE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiCustomer", nullable = false)
	private Customer customer;

	/** flag to determine if analysis has data */
	@Column(name = "dtData", nullable = false)
	private boolean data;

	@Column(name = "dtDefaultProfile", nullable = false)
	private boolean defaultProfile = false;

	/** List of History data of the Analysis */
	@OneToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<History> histories = new ArrayList<>();

	/** Analysis id unsaved value = -1 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAnalysis")
	private int id = 0;

	/** ID of the Analysis */
	@Column(name = "dtIdentifier", nullable = false, length = 23)
	private String identifier;

	/** List of Item Information */
	@OneToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<ItemInformation> itemInformations = new ArrayList<>();

	/** The Label of this Analysis */
	@Column(name = "dtLabel", nullable = false)
	private String label;

	/** Language object of the Analysis */
	@ManyToOne
	@JoinColumn(name = "fiLanguage", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Cascade(CascadeType.SAVE_UPDATE)
	@Access(AccessType.FIELD)
	private Language language;

	/** Analysis owner (the one that created or imported it) */
	@ManyToOne
	@JoinColumn(name = "fiOwner", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Cascade(CascadeType.SAVE_UPDATE)
	@Access(AccessType.FIELD)
	private User owner;

	/** List of parameters */
	@Transient
	private Map<String, List<? extends IParameter>> parameters = new LinkedHashMap<>();

	/** List of Phases that is used for Action Plan Computation */
	@OneToMany(mappedBy = "analysis")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	@OrderBy("number")
	private List<Phase> phases = new ArrayList<>();

	@Column(name = "dtProfile", nullable = false)
	private boolean profile = false;

	/** Ticketing project id */
	@Column(name = "dtProject")
	private String project;

	/** List of Risk Information */
	@OneToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<RiskInformation> riskInformations = new ArrayList<>();

	/** List of Assessment */
	@OneToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<RiskProfile> riskProfiles = new ArrayList<>();

	/** The Risk Register (CSSF) */
	@OneToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	@OrderBy("dtNetEvaluationImportance desc, dtExpEvaluationImportance desc, dtRawEvaluationImportance desc")
	private List<RiskRegisterItem> riskRegisters = new ArrayList<>();

	@ElementCollection
	@Column(name = "dtAcronym")
	@Cascade(CascadeType.ALL)
	@CollectionTable(name = "AnalysisExcludeAcronyms", joinColumns = @JoinColumn(name = "fiAnalysis"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"dtAcronym", "fiAnalysis" }))
	private Set<String> excludeAcronyms = new HashSet<>();

	/** List of Scenarios */
	@OneToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	@OrderBy("type,name")
	private List<Scenario> scenarios = new ArrayList<>();

	@ElementCollection
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@MapKeyColumn(name = "dtName")
	@Column(name = "dtValue")
	@Cascade(CascadeType.ALL)
	@CollectionTable(name = "AnalysisSetting", joinColumns = @JoinColumn(name = "fiAnalysis"))
	private Map<String, String> settings = new LinkedHashMap<>();

	/** The Action Plan Summary without Phase Computation - Normal */
	@OneToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<SummaryStage> summaries = new ArrayList<>();

	@Column(name = "dtType", nullable = false)
	@Enumerated(EnumType.STRING)
	private AnalysisType type = AnalysisType.QUANTITATIVE;

	@Column(name = "dtUncertainty", nullable = false)
	private boolean uncertainty = false;

	/** List of users and their access rights */
	@OneToMany(mappedBy = "analysis")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<UserAnalysisRight> userRights = new ArrayList<>();

	@OneToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<AssetNode> assetNodes = new ArrayList<>();

	@OneToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinTable(name = "AnalysisILRImpactTypes", joinColumns = @JoinColumn(name = "fiAnalysis", referencedColumnName = "idAnalysis", unique = false), inverseJoinColumns = @JoinColumn(name = "fiScaleType", referencedColumnName = "idScaleType", unique = false), uniqueConstraints = @UniqueConstraint(columnNames = {
			"fiAnalysis", "fiScaleType" }))
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE })
	@Access(AccessType.FIELD)
	@OrderBy("name")
	private List<ScaleType> ilrImpactTypes = new ArrayList<>();

	@OneToMany
	@MapKey(name = "type")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	@OrderBy("created")
	private Map<SimpleDocumentType, SimpleDocument> documents = new LinkedHashMap<>();

	/** Version of the Analysis */
	@Column(name = "dtVersion", nullable = false, length = 12)
	private String version;

	@Column(name = "dtArchived", nullable = false)
	private boolean archived = false;

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
		this.analysisStandards.put(analysisStandard.getStandard().getName(), analysisStandard);
	}

	/**
	 * addAnAssessment<br>
	 * Adds an Assessment Object to the List of Assessments
	 * 
	 * @param assessment The Assessment Object to Add
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
	 * @param asset The asset Object to Add
	 * @throws TrickException
	 */
	public void add(Asset asset) throws TrickException {
		if (this.assets.contains(asset))
			throw new TrickException("error.asset.duplicate",
					String.format("Asset (%s) is duplicated", asset.getName()), asset.getName());
		this.assets.add(asset);
	}

	/**
	 * addAParameter: <br>
	 * Adds a SimpleParameter to the List of Parameters
	 * 
	 * @param param The SimpleParameter object to Add
	 */
	@SuppressWarnings("unchecked")
	public boolean add(IParameter param) {
		return ((List<IParameter>) this.parameters.computeIfAbsent(param.getGroup(), k -> new ArrayList<>()))
				.add(param);
	}

	/***********************************************************************************************
	 * Computation of Measure Cost - BEGIN
	 **********************************************************************************************/

	/**
	 * addAnItemInformation: <br>
	 * Adds an Item Information Object to the List of Item Information
	 * 
	 * @param iteminformation The Item Information Object to Add
	 */
	public boolean add(ItemInformation itemInformation) {
		return this.itemInformations.add(itemInformation);
	}

	public void add(Phase phase) {
		if (this.phases == null)
			phases = new ArrayList<>();
		if (!phases.contains(phase)) {
			phases.add(phase);
			phase.setAnalysis(this);
		} else
			throw new TrickException("error.phase.duplicated",
					String.format("An other phase with the same number `%d` already exists", phase.getNumber()),
					phase.getNumber() + "");
	}

	/**
	 * addARiskInformation: <br>
	 * Adds an Risk Information Object to the List of Risk Information
	 * 
	 * @param riskInfo The Risk Information Object to Add
	 */
	public void add(RiskInformation riskInfo) {
		this.riskInformations.add(riskInfo);
	}

	/**
	 * setAScenario: <br>
	 * Adds a Scenario Object to the List of Scenarios
	 * 
	 * @param scenario The Scenario Object to Add
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
	 * @param hist The History object to add
	 */
	public void addAHistory(History hist) {
		this.histories.add(hist);
	}

	/**
	 * addAnActionPlanEntry: <br>
	 * Adds an ActionPlanEntry of a given type to the corresponding Action Plan.
	 * 
	 * @param type       The Identifier of the Action Plan Type
	 * 
	 * @param actionplan the action plan entry to add
	 */
	public void addAnActionPlanEntry(ActionPlanEntry actionplanentry) {
		this.actionPlans.add(actionplanentry);
	}

	/**
	 * addARiskRegisterItem: <br>
	 * Adds a Risk Register Item to the Risk Register
	 * 
	 * @param riskItem The RiskRegisterItem Object to Add
	 */
	public void addARiskRegisterItem(RiskRegisterItem riskItem) {
		this.riskRegisters.add(riskItem);
	}

	/**
	 * setSummary: <br>
	 * Sets a List of SummaryStages of a given type.
	 * 
	 * @param type    The Summary Type (same as Action Plan Type)
	 * @param summary The List of SummaryStages to set
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
		UserAnalysisRight userAnalysisRight = findRightsforUser(user);
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
	 * @param measure The Measure to calculate the Cost
	 * 
	 * @return The Calculated Cost
	 */
	public double computeCost(Measure measure) {

		// ****************************************************************
		// * select external and internal setup rate from parameters
		// ****************************************************************

		double internalSetupValue = this.findParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE);

		double externalSetupValue = this.findParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE);

		double lifetimeDefault = this.findParameter(Constant.PARAMETER_LIFETIME_DEFAULT);
		double implementationRate = measure.getImplementationRateValue(getExpressionParameters()) * 0.01;
		boolean isFullRelatedCost = this.findSetting(AnalysisSetting.ALLOW_FULL_COST_RELATED_TO_MEASURE);

		// calculate the cost
		return Analysis.computeCost(internalSetupValue, externalSetupValue, lifetimeDefault,
				measure.getInternalWL(),
				measure.getExternalWL(), measure.getInvestment(), measure.getLifetime(),
				measure.getInternalMaintenance(), measure.getExternalMaintenance(), measure.getRecurrentInvestment(),
				implementationRate, isFullRelatedCost);
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
		analysis.excludeAcronyms = new HashSet<>(this.excludeAcronyms);
		analysis.ilrImpactTypes = new ArrayList<>();
		analysis.id = 0;
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
		copy.ilrImpactTypes = new ArrayList<>(ilrImpactTypes);
		copy.excludeAcronyms = new HashSet<>(excludeAcronyms);
		copy.id = 0;
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
	 * @param obj The other object to check
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

	/***********************************************************************************************
	 * Computation of Measure Cost - END
	 **********************************************************************************************/

	public List<NormalStandard> findAllNormalStandards() {
		return analysisStandards.values().stream().filter(NormalStandard.class::isInstance).map(a -> (NormalStandard) a)
				.collect(Collectors.toList());
	}

	/**
	 * getAnalysisStandards: <br>
	 * Description
	 * 
	 * @return
	 */
	public List<AnalysisStandard> findAnalysisOnlyStandards() {
		return analysisStandards.values().stream().filter(standard -> standard.getStandard().isAnalysisOnly())
				.collect(Collectors.toList());

	}

	public AnalysisStandard findAnalysisStandardByStandardId(Integer standardID) {
		return standardID == null ? null
				: analysisStandards.values().stream().filter(standard -> standard.getStandard().getId() == standardID)
						.findAny().orElse(null);
	}

	public Map<Asset, List<Assessment>> findAssessmentByAsset() {
		final Map<Asset, List<Assessment>> mapping = new LinkedHashMap<>();
		assessments.forEach(assessment -> mapping.computeIfAbsent(assessment.getAsset(), k -> new ArrayList<>())
				.add(assessment));
		return mapping;
	}

	public Assessment findAssessmentByAssetAndScenario(int idAsset, int idScenario) {
		return assessments.stream().filter(assessment -> assessment.is(idAsset, idScenario)).findAny().orElse(null);
	}

	public Map<Integer, Assessment> findAssessmentByAssetId(int id) {
		final Map<Integer, Assessment> assessmentMap = new LinkedHashMap<>();
		if (assessments == null || assessments.isEmpty())
			return assessmentMap;
		for (Assessment assessment : assessments)
			if (assessment.getAsset().getId() == id)
				assessmentMap.put(assessment.getScenario().getId(), assessment);
		return assessmentMap;
	}

	public Map<Integer, Assessment> findAssessmentByScenarioId(int id) {
		final Map<Integer, Assessment> assessmentMap = new LinkedHashMap<>();
		if (assessments == null || assessments.isEmpty())
			return assessmentMap;
		for (Assessment assessment : assessments)
			if (assessment.getScenario().getId() == id)
				assessmentMap.put(assessment.getAsset().getId(), assessment);
		return assessmentMap;
	}

	public List<Assessment> findAssessmentBySelectedAsset() {
		return this.assessments.stream().filter(a -> a.getAsset().isSelected()).collect(Collectors.toList());
	}

	public List<Assessment> findAssessmentBySelectedScenario() {
		return this.assessments.stream().filter(a -> a.getScenario().isSelected()).collect(Collectors.toList());
	}

	public Asset findAsset(int idAsset) {
		return assets.stream().filter(asset -> asset.getId() == idAsset).findAny().orElse(null);
	}

	public List<IParameter> findByGroup(String... groups) {
		final List<IParameter> ps = new LinkedList<>();
		for (String group : groups) {
			if (this.parameters.containsKey(group))
				ps.addAll(this.parameters.get(group));
		}
		return ps;
	}

	public Map<String, DynamicParameter> findDynamicParametersByAnalysisAsMap() {
		return getDynamicParameters().stream()
				.collect(Collectors.toMap(DynamicParameter::getAcronym, Function.identity()));
	}

	public Map<Integer, Boolean> findIdMeasuresImplementedByActionPlanType(ActionPlanMode appn) {
		return this.actionPlans.stream().filter(a -> a.getActionPlanType().getActionPlanMode() == appn)
				.map(ActionPlanEntry::getMeasure).collect(Collectors.toMap(Measure::getId, e -> true, (m1, m2) -> m1));
	}

	public LikelihoodParameter findLikelihoodByTypeAndLevel(int level) {
		return getLikelihoodParameters().stream().filter(parameter -> parameter.getLevel() == level).findAny()
				.orElse(null);
	}

	public Measure findMeasureById(int idMeasure) {
		return analysisStandards.values().stream().flatMap(measures -> measures.getMeasures().stream())
				.filter(measure -> measure.getId() == idMeasure).findAny().orElse(null);
	}

	public List<? extends Measure> findMeasureByStandard(String standard) {
		return this.analysisStandards.values().stream().filter(a -> a.getStandard().is(standard)).findAny()
				.map(AnalysisStandard::getMeasures).orElse(Collections.emptyList());
	}

	public List<Measure> findMeasuresByActionPlan(ActionPlanMode appn) {
		return this.actionPlans.stream().filter(a -> a.getActionPlanType().getActionPlanMode() == appn)
				.map(ActionPlanEntry::getMeasure).collect(Collectors.toList());
	}

	public List<Measure> findMeasuresByActionPlanAndNotToImplement(ActionPlanMode appn) {
		return this.actionPlans.stream()
				.filter(a -> a.getActionPlanType().getActionPlanMode() == appn && a.getROI() <= 0.0)
				.map(ActionPlanEntry::getMeasure).collect(Collectors.toList());
	}

	public List<Asset> findNoAssetSelected() {
		return this.assets.stream().filter(asset -> !asset.isSelected()).collect(Collectors.toList());
	}

	public IParameter findParameter(String type, String baseKey) {
		return this.parameters.values().stream().flatMap(paramters -> paramters.stream())
				.filter(parameter -> parameter.isMatch(type, baseKey)).findAny().orElse(null);
	}

	public List<? extends IParameter> findParametersByType(String type) {
		return this.parameters.values().stream().flatMap(paramters -> paramters.stream())
				.filter(parameter -> parameter.isMatch(type)).collect(Collectors.toList());
	}

	public double findParameterValueByTypeAndAcronym(String type, String acronym) {
		return findParameterValueByTypeAndAcronym(type, acronym, 0D);
	}

	public Double findParameterValueByTypeAndAcronym(String type, String acronym, Double defaultValue) {
		IParameter parameter = findParameter(type, acronym);
		return parameter == null ? defaultValue : parameter.getValue().doubleValue();
	}

	public Phase findPhaseByNumber(int number) {
		return phases.stream().filter(phase -> phase.getNumber() == number).findAny().orElse(null);
	}

	public List<RiskProfile> findRiskProfileByAsset(Asset asset) {
		return riskProfiles.stream().filter(riskRegister -> riskRegister.getAsset().equals(asset))
				.collect(Collectors.toList());
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
		return riskRegisters.stream().filter(riskRegister -> riskRegister.is(idAsset, idScenario)).findAny()
				.orElse(null);
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
		return assessments.stream().filter(Assessment::isSelected).sorted((a1, a2) -> {
			int result = NaturalOrderComparator.compareTo(a1.getAsset().getName(), a2.getAsset().getName());
			if (result == 0)
				NaturalOrderComparator.compareTo(a1.getScenario().getName(), a2.getScenario().getName());
			return result;

		}).collect(Collectors.groupingBy(Assessment::getAsset, LinkedHashMap::new, Collectors.toList()));
	}

	public List<Assessment> findSelectedAssessmentByAsset(int idAsset) {
		return assessments.stream()
				.filter(assessment -> assessment.isSelected() && assessment.getAsset().getId() == idAsset)
				.collect(Collectors.toList());
	}

	public List<Assessment> findSelectedAssessmentByScenario(int idScenario) {
		return assessments.stream()
				.filter(assessment -> assessment.isSelected() && assessment.getScenario().getId() == idScenario)
				.collect(Collectors.toList());
	}

	public List<Asset> findSelectedAsset() {
		if (assets == null)
			return Collections.emptyList();
		return this.assets.stream().filter(Asset::isSelected).collect(Collectors.toList());
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
		List<Scenario> tmpscenarios = new ArrayList<>();
		for (Scenario scenario : scenarios)
			if (scenario.isSelected())
				tmpscenarios.add(scenario);
		return tmpscenarios;
	}

	public SimpleParameter findSimpleParameter(String type, String description) {
		return getSimpleParameters().stream().filter(parameter -> parameter.isMatch(type, description)).findAny()
				.orElse(null);
	}

	public Standard findStandardByAndAnalysisOnly(Integer idStandard) {
		return this.analysisStandards.values().stream()
				.filter(analysisStandard -> analysisStandard.getStandard().getId() == idStandard
						&& analysisStandard.isAnalysisOnly())
				.map(analysisStandard -> analysisStandard.getStandard()).findAny().orElse(null);
	}

	public List<Phase> findUsablePhase() {
		List<Phase> ps = new ArrayList<>();
		if (this.actionPlans == null || this.actionPlans.isEmpty())
			return ps;
		for (ActionPlanEntry actionPlanEntry : this.actionPlans) {
			Phase phase = actionPlanEntry.getMeasure().getPhase();
			if (phase != null && !ps.contains(phase))
				ps.add(phase);
		}

		for (int i = 0; i < ps.size(); i++) {
			Phase phase = ps.get(i);
			for (int j = 0; j < ps.size(); j++) {
				if (ps.get(j).getNumber() > phase.getNumber()) {
					ps.set(i, ps.get(j));
					ps.set(j, phase);
					phase = ps.get(i);
				}
			}
		}

		return ps;

	}

	/**
	 * getActionPlan: <br>
	 * Returns the Action Plan of a given Action Plan Type.
	 * 
	 * @param type The Identifier of the Action Plan Type
	 * 
	 * @return The List of Action Plan Entries for the requested Action Plan Type
	 */
	public List<ActionPlanEntry> findActionPlan(ActionPlanMode mode) {
		return this.actionPlans.stream()
				.filter(actionPlan -> actionPlan.getActionPlanType().getActionPlanMode() == mode)
				.collect(Collectors.toList());
	}

	/**
	 * getActionPlan: <br>
	 * Returns the Action Plan of a given Action Plan Type.
	 * 
	 * @param type The Identifier of the Action Plan Type
	 * 
	 * @return The List of Action Plan Entries for the requested Action Plan Type
	 */
	public List<ActionPlanEntry> findActionPlan(String mode) {

		List<ActionPlanEntry> ape = new ArrayList<>();
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
	 * @param type The Identifier of the Action Plan Type
	 * 
	 * @return The List of Action Plan Entries for the requested Action Plan Type
	 */
	public List<ActionPlanEntry> getActionPlans() {
		return this.actionPlans;
	}

	/**
	 * Map<{@link Standard#getName()}, {@link AnalysisStandard}>
	 * 
	 * getAnalysisStandards: <br>
	 * Description
	 * 
	 * @return
	 */
	public Map<String, AnalysisStandard> getAnalysisStandards() {
		return analysisStandards;
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
	 * getAssetNodes: <br>
	 * Returns a list of Asset nodes
	 * 
	 * @return The List of Asset nodes
	 */
	public List<AssetNode> getAssetNodes() {
		return assetNodes;
	}

	public void setAssetNodes(List<AssetNode> assetNodes) {
		this.assetNodes = assetNodes;
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

	@Transient
	public List<IBoundedParameter> getBoundedParamters() {
		final List<IBoundedParameter> ps = getImpactParameters().stream().collect(Collectors.toList());
		ps.addAll(getLikelihoodParameters());
		return ps;
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
	public boolean isData() {
		return data;
	}

	@OneToMany
	@JoinColumn(name = "fiAnalysis")
	@Access(AccessType.PROPERTY)
	@Cascade(CascadeType.ALL)
	@OrderBy("acronym,value")
	@SuppressWarnings("unchecked")
	public List<DynamicParameter> getDynamicParameters() {
		return (List<DynamicParameter>) this.parameters
				.computeIfAbsent(Constant.PARAMETER_CATEGORY_DYNAMIC, k -> new ArrayList<>());

	}

	/**
	 * Gets the list of all parameters that shall be taken into consideration
	 * whenever an expression (e.g. for likelihood) is evaluated.<br>
	 * <b>Updated by eomar 06/10/2016: <br>
	 * Add filter by Type: Dynamic + likelihood</b>
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOParameter#findExpressionParameterByAnalysis(Integer)
	 */
	@Transient
	public List<IAcronymParameter> getExpressionParameters() {
		// We assume that all parameters that have an acronym can be used in an
		// expression
		// May be we want to change this in the future (checking parameter.type);
		// then this is the place to act.
		// In that case, we must update
		// lu.itrust.business.ts.database.dao.DAOParameter#getAllExpressionParametersFromAnalysis(Integer),
		// so in particular
		// lu.itrust.business.ts.database.dao.hbm.DAOParameterHBM#getAllExpressionParametersFromAnalysis(Integer).
		return this.parameters.entrySet().stream()
				.filter(entry -> entry.getKey().equals(Constant.PARAMETER_TYPE_PROPABILITY_NAME)
						|| entry.getKey().equals(Constant.PARAMETER_TYPE_IMPACT_NAME)
						|| entry.getKey().equals(Constant.PARAMETER_CATEGORY_DYNAMIC))
				.flatMap(entry -> entry.getValue().stream()).map(IAcronymParameter.class::cast)
				.collect(Collectors.toList());
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
	@OrderBy("level")
	@SuppressWarnings("unchecked")
	public List<ImpactParameter> getImpactParameters() {
		return (List<ImpactParameter>) getParameters().computeIfAbsent(Constant.PARAMETER_CATEGORY_IMPACT,
				k -> new ArrayList<>());
	}

	public List<ScaleType> findImpacts() {
		return getImpactParameters().stream().map(ImpactParameter::getType).distinct()
				.sorted((s1, s2) -> s1.getName().equals(Constant.DEFAULT_IMPACT_NAME) ? 1
						: s2.getName().equals(Constant.DEFAULT_IMPACT_NAME) ? -1 : s1.getName().compareTo(s2.getName()))
				.collect(Collectors.toList());
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
	@Transient
	public History getLastHistory() {
		return histories == null ? null
				: histories.stream().max((c1, c2) -> NaturalOrderComparator.compareTo(c1.getVersion(), c2.getVersion()))
						.orElse(null);
	}

	/**
	 * getLatestVersion: <br>
	 * Parse all history entries to find latest version (version has to be of format
	 * xx.xx.xx)
	 * 
	 * @return
	 */
	@Transient
	public String getLatestVersion() {
		History history = getLastHistory();
		return history == null ? "" : history.getVersion();
	}

	@OneToMany
	@JoinColumn(name = "fiAnalysis")
	@Access(AccessType.PROPERTY)
	@Cascade(CascadeType.ALL)
	@OrderBy("value")
	@SuppressWarnings("unchecked")
	public List<IlrSoaScaleParameter> getIlrSoaScaleParameters() {
		return (List<IlrSoaScaleParameter>) this.getParameters()
				.computeIfAbsent(Constant.PARAMETER_CATEGORY_ILR_SOA_SCALE, k -> new ArrayList<>());
	}

	@OneToMany
	@JoinColumn(name = "fiAnalysis")
	@Access(AccessType.PROPERTY)
	@Cascade(CascadeType.ALL)
	@OrderBy("level")
	@SuppressWarnings("unchecked")
	public List<LikelihoodParameter> getLikelihoodParameters() {
		return (List<LikelihoodParameter>) this.getParameters()
				.computeIfAbsent(Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD, k -> new ArrayList<>());
	}

	@OneToMany
	@JoinColumn(name = "fiAnalysis")
	@Access(AccessType.PROPERTY)
	@Cascade(CascadeType.ALL)
	@SuppressWarnings("unchecked")
	public List<MaturityParameter> getMaturityParameters() {
		return (List<MaturityParameter>) this.getParameters()
				.computeIfAbsent(Constant.PARAMETER_CATEGORY_MATURITY, k -> new ArrayList<>());

	}

	@Transient
	public MaturityStandard getMaturityStandard() {
		return (MaturityStandard) analysisStandards.values().stream()
				.filter(analysisStandard -> analysisStandard instanceof MaturityStandard).findAny().orElse(null);
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
	 * @param parameter The Label of the SimpleParameter
	 * @return The Value of the SimpleParameter if it exists, or -1 if the parameter
	 *         was not found
	 */

	public double findParameter(String name) {
		return findParameter(name, -1D);
	}

	public double findParameter(String name, double defaultValue) {
		return getParameters().values().stream().flatMap(paramters -> paramters.stream())
				.filter(parameter -> parameter.getDescription().equals(name))
				.map(parameter -> parameter.getValue().doubleValue()).findAny().orElse(defaultValue);
	}

	/**
	 * 
	 * getParameter: <br>
	 * Returns the SimpleParameter value of a given SimpleParameter.
	 * 
	 * @param parameter The Label of the SimpleParameter
	 * @return The Value of the SimpleParameter if it exists, or defaultValue if the
	 *         parameter was not found
	 */
	public double findParameter(String type, String name, double defaultValue) {
		return getParameters().values().stream().flatMap(parametersList -> parametersList.stream())
				.filter(parameter -> parameter.isMatch(type, name)).map(parameter -> parameter.getValue().doubleValue())
				.findAny().orElse(defaultValue);
	}

	/**
	 * getParameterList: <br>
	 * Returns the SimpleParameter List.
	 * 
	 * @return The SimpleParameter Object List
	 */
	@Transient
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
	public UserAnalysisRight findRightsforUser(User user) {
		return findRightsforUserString(user.getLogin());
	}

	/**
	 * getRightsforUser: <br>
	 * Description
	 * 
	 * @param user
	 * @return
	 */
	public UserAnalysisRight findRightsforUserString(String login) {
		return userRights.stream().filter(userRight -> userRight.getUser().getLogin().equals(login)).findAny()
				.orElse(null);
	}

	public AnalysisRight findRightValue(User user) {
		UserAnalysisRight analysisRight = findRightsforUser(user);
		return analysisRight == null ? null : analysisRight.getRight();
	}

	@OneToMany
	@JoinColumn(name = "fiAnalysis")
	@Access(AccessType.PROPERTY)
	@Cascade(CascadeType.ALL)
	@OrderBy("value,color, label, description")
	@SuppressWarnings("unchecked")
	public List<RiskAcceptanceParameter> getRiskAcceptanceParameters() {
		return (List<RiskAcceptanceParameter>) this.parameters
				.computeIfAbsent(Constant.PARAMETER_CATEGORY_RISK_ACCEPTANCE, k -> new ArrayList<>());
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
	public List<Assessment> findSelectedAssessments() {
		return assessments.stream().filter(Assessment::isSelected).collect(Collectors.toList());
	}

	public <T> T findSetting(AnalysisSetting setting) {
		return findSetting(setting, settings.get(setting.name()));
	}

	public String findSetting(ReportSetting setting) {
		return setting == null ? null : settings.getOrDefault(setting.name(), setting.getValue());
	}

	public String findSetting(ExportFileName setting) {
		return setting == null ? null : settings.getOrDefault(setting.name(), "05-X_TSE");
	}

	@OneToMany
	@JoinColumn(name = "fiAnalysis")
	@Access(AccessType.PROPERTY)
	@Cascade(CascadeType.ALL)
	@SuppressWarnings("unchecked")
	public List<SimpleParameter> getSimpleParameters() {
		return (List<SimpleParameter>) this.parameters
				.computeIfAbsent(Constant.PARAMETER_CATEGORY_SIMPLE, k -> new ArrayList<>());
	}

	/**
	 * getAnalysisStandards: <br>
	 * Description
	 * 
	 * @return
	 */
	public List<Standard> findStandards() {
		return analysisStandards.values().stream().map(AnalysisStandard::getStandard)
				.sorted((e1, e2) -> NaturalOrderComparator.compareTo(e1.getName(), e2.getName()))
				.collect(Collectors.toList());

	}

	/**
	 * getSummary: <br>
	 * Returns the summary of a given Action Plan Type.
	 * 
	 * @param type The Identifier of the Action Plan Type
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
	 * @param type The Identifier of the Action Plan Type
	 * 
	 * @return The List of Summary Entries for the requested Action Plan Type
	 */
	public List<SummaryStage> findSummary(ActionPlanMode mode) {
		return getSummaries().stream().filter(e -> e.getActionPlanType().getActionPlanMode() == mode)
				.collect(Collectors.toList());
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

	public Map<String, String> getSettings() {
		return settings;
	}

	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}

	public Map<SimpleDocumentType, SimpleDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(Map<SimpleDocumentType, SimpleDocument> documents) {
		this.documents = documents;
	}

	public void groupAssessmentByAssetAndScenario(Map<Asset, List<Assessment>> assetAssessments,
			Map<Scenario, List<Assessment>> scenarioAssessments) {
		if (assetAssessments == null || scenarioAssessments == null)
			return;
		assessments.forEach(assessment -> {
			assetAssessments.computeIfAbsent(assessment.getAsset(), k -> new ArrayList<>()).add(assessment);
			scenarioAssessments.computeIfAbsent(assessment.getScenario(), k -> new ArrayList<>()).add(assessment);
		});
	}

	/**
	 * Retrieve extended parameters: Impact and Probabilities
	 * 
	 * @param probabilities
	 * @param impacts
	 */
	public void groupExtended(List<LikelihoodParameter> probabilities, List<ImpactParameter> impacts) {
		this.getParameters().values().stream().flatMap(Collection::stream)
				.filter(IBoundedParameter.class::isInstance)
				.map(IBoundedParameter.class::cast).forEach(parameter -> {
					if ((parameter instanceof ImpactParameter)
							&& parameter.getTypeName().equals(Constant.PARAMETER_TYPE_IMPACT_NAME))
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
		return this.getParameters().values().stream().flatMap(paramters -> paramters.stream())
				.anyMatch(parameter -> parameter.isMatch(type));
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
		return analysisStandards.values().stream().flatMap(measures -> measures.getMeasures().stream())
				.anyMatch(measure -> idTicket.equals(measure.getTicket()));
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
		List<Phase> tmpPhases = new ArrayList<>();

		Phase smallest = null;
		List<NormalStandard> normalStandards = this.findAllNormalStandards();
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
			List<Measure> measures = maturityStandard.getNotComputableMeasures();
			// parse all measures of the standard
			for (Measure measure : measures) {
				int phaseNumber = measure.getPhase().getNumber();
				if (this.findPhaseByNumber(phaseNumber) == null)
					this.add(measure.getPhase());
			}
		}

		// ****************************************************************
		// * order phases ascending
		// ****************************************************************

		// check until temporary list is empty (phases are ordered)
		while (!this.phases.isEmpty()) {

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
	 * @return the archived
	 */
	public boolean isArchived() {
		return archived;
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

	@Transient
	public boolean isHybrid() {
		return AnalysisType.isHybrid(type);
	}

	/**
	 * @return the profile
	 */
	public boolean isProfile() {
		return profile;
	}

	@Transient
	public boolean isQualitative() {
		return AnalysisType.isQualitative(type);
	}

	@Transient
	public boolean isQuantitative() {
		return AnalysisType.isQuantitative(type);
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

	@Transient
	public boolean isUserAuthorized(String username, AnalysisRight right) {
		return userRights.stream().anyMatch(userRight -> userRight.getUser().getLogin().equals(username)
				&& UserAnalysisRight.userIsAuthorized(userRight, right));
	}

	/**
	 * userIsAuthorized: <br>
	 * checks if a given user has the given right on the current analysis
	 * 
	 * @param user
	 * @param right
	 * @return
	 */
	@Transient
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
		this.analysisStandards.remove(analysisStandard.getStandard().getName());
	}

	public List<Assessment> removeAssessment(Asset asset) {
		final List<Assessment> assmts = new LinkedList<>();
		this.assessments.removeIf(assessment -> assessment.getAsset().equals(asset) && assmts.add(assessment));
		return assmts;
	}

	public List<Assessment> removeAssessment(Scenario scenario) {
		final List<Assessment> assmts = new LinkedList<>();
		this.assessments
				.removeIf(assessment -> assessment.getScenario().equals(scenario) && assmts.add(assessment));
		return assmts;
	}

	public List<Scenario> removeFromScenario(Asset asset) {
		return this.scenarios.stream().filter(scenario -> scenario.getLinkedAssets().remove(asset))
				.collect(Collectors.toList());
	}

	/**
	 * removeRights: <br>
	 * Description
	 * 
	 * @param user
	 * @return
	 */
	public UserAnalysisRight removeRights(User user) {
		final UserAnalysisRight userRight = findRightsforUser(user);
		return userRight == null ? null : this.userRights.remove(userRight) ? userRight : null;
	}

	public List<RiskProfile> removeRiskProfile(Asset asset) {
		final List<RiskProfile> profiles = new LinkedList<>();
		riskProfiles.removeIf(riskProfile -> riskProfile.getAsset().equals(asset) && profiles.add(riskProfile));
		return profiles;
	}

	public List<RiskProfile> removeRiskProfile(Scenario scenario) {
		final List<RiskProfile> profiles = new LinkedList<>();
		riskProfiles.removeIf(riskProfile -> riskProfile.getScenario().equals(scenario) && profiles.add(riskProfile));
		return profiles;
	}

	/**
	 * setActionPlan: <br>
	 * Sets a List of ActionPlanEntries of a given type.
	 * 
	 * @param type       The Action Plan Type
	 * @param actionPlan The List of ActionPlanEntries to set
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
		setAnalysisStandards(analysisStandards.stream()
				.collect(Collectors.toMap(a -> a.getStandard().getName(), Function.identity())));
	}

	/**
	 * setAnalysisStandards: <br>
	 * Description
	 * 
	 * @param analysisStandards
	 */
	public void setAnalysisStandards(Map<String, AnalysisStandard> analysisStandards) {
		this.analysisStandards = analysisStandards;
	}

	/**
	 * @param archived the archived to set
	 */
	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	/**
	 * setAssessments<br>
	 * Sets the list of Assessment Objects.
	 * 
	 * @param assessments The List of Assessments
	 */
	public void setAssessments(List<Assessment> assessments) {
		this.assessments = assessments;
	}

	/**
	 * setAssets: <br>
	 * Sets the List of Asset Objects.
	 * 
	 * @param assets The List of Asset Objects
	 */
	public void setAssets(List<Asset> assets) {
		this.assets = assets;
	}

	/**
	 * setBasedOnAnalysis: <br>
	 * Sets the Field "basedOnAnalysis" with a value.
	 * 
	 * @param basedOnAnalysis The Value to set the basedOnAnalysis field
	 */
	public void setBasedOnAnalysis(Analysis basedOnAnalysis) {
		this.basedOnAnalysis = basedOnAnalysis;
	}

	/**
	 * setCreationDate: <br>
	 * Sets the "creationDate" field with avalue
	 * 
	 * @param creationdate The value to set the Creation Date
	 */
	public void setCreationDate(Timestamp creationdate) {
		this.creationDate = creationdate;
	}

	/**
	 * setCustomerid: <br>
	 * Sets the "customer" field with a object
	 * 
	 * @param customer The Object to set the Customer
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * sethasData: <br>
	 * Sets the "hasData" field with a value
	 * 
	 * @param hasData The value to set the hasData Analysis Flag
	 */
	public void setData(boolean data) {
		this.data = data;
	}

	/**
	 * setDefaultProfile: <br>
	 * Sets the Field "defaultProfile" with a value.
	 * 
	 * @param defaultProfile The Value to set the defaultProfile field
	 */
	public void setDefaultProfile(boolean defaultProfile) {
		this.defaultProfile = defaultProfile;
	}

	public void setDynamicParameters(List<DynamicParameter> parameters) {
		this.getParameters().put(Constant.PARAMETER_CATEGORY_DYNAMIC, parameters);
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
			histories = new ArrayList<>();
		histories.add(history);
	}

	/**
	 * setHistory: <br>
	 * Set History List object.
	 * 
	 * @param hist The List of History objects to add
	 */
	public void setHistory(List<History> hists) {
		this.histories = hists;
	}

	/**
	 * setId: <br>
	 * Sets the Field "id" with a value.
	 * 
	 * @param id The Value to set the id field
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * setIdentifier: <br>
	 * Sets the "identifier" field with a value
	 * 
	 * @param identifier The value to set the Analysis ID
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setImpactParameters(List<ImpactParameter> impacts) {
		getParameters().put(Constant.PARAMETER_CATEGORY_IMPACT, impacts);
	}

	/**
	 * setItemInformations: <br>
	 * Adds an Item Information Object to the List of Item Information
	 * 
	 * @param iteminformations The Item Information Object to Add
	 */
	public void setItemInformations(List<ItemInformation> itemInformations) {
		this.itemInformations = itemInformations;
	}

	/**
	 * setLabel: <br>
	 * Sets the "label" field with a value
	 * 
	 * @param label The value to set the Analysis Label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * setLanguage: <br>
	 * Sets the "language" field with a Language Object
	 * 
	 * @param language The Object to set the Language
	 */
	public void setLanguage(Language language) {
		this.language = language;
	}

	public void setIlrSoaScaleParameters(List<IlrSoaScaleParameter> parameters) {
		this.getParameters().put(Constant.PARAMETER_CATEGORY_ILR_SOA_SCALE, parameters);
	}

	public void setLikelihoodParameters(List<LikelihoodParameter> parameters) {
		this.getParameters().put(Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD, parameters);
	}

	public void setMaturityParameters(List<MaturityParameter> parameters) {
		this.getParameters().put(Constant.PARAMETER_CATEGORY_MATURITY, parameters);
	}

	/**
	 * setOwner: <br>
	 * Sets the Field "owner" with a value.
	 * 
	 * @param owner The Value to set the owner field
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}

	/**
	 * setParameters: <br>
	 * Adds a SimpleParameter to the List of Parameters
	 * 
	 * @param params The SimpleParameter object to Add
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
	 * @param profile the profile to set
	 */
	public void setProfile(boolean profile) {
		this.profile = profile;
	}

	/**
	 * Ticketing project id
	 * 
	 * @param project the project to set
	 */
	public void setProject(String project) {
		this.project = project;
	}

	public void setRiskAcceptanceParameters(List<RiskAcceptanceParameter> parameters) {
		this.getParameters().put(Constant.PARAMETER_CATEGORY_RISK_ACCEPTANCE, parameters);
	}

	/**
	 * setRiskInformations: <br>
	 * Sets the Risk Information List Object.
	 * 
	 * @param riskInfos The List of Risk Information Objects
	 */
	public void setRiskInformations(List<RiskInformation> riskInfos) {
		this.riskInformations = riskInfos;
	}

	/**
	 * @param riskProfiles the riskProfiles to set
	 */
	public void setRiskProfiles(List<RiskProfile> riskProfiles) {
		this.riskProfiles = riskProfiles;
	}

	/**
	 * setRiskRegisters: <br>
	 * Sets the Field "riskRegisters" with a value.
	 * 
	 * @param riskRegisters The Value to set the riskRegisters field
	 */
	public void setRiskRegisters(List<RiskRegisterItem> riskRegisters) {
		this.riskRegisters = riskRegisters;
	}

	/**
	 * setScenarios: <br>
	 * Sets the list of Scenario Objects.
	 * 
	 * @param scenarios The List of Scenarios
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

	public Object removeSetting(String name) {
		if (name == null)
			return null;
		return this.settings.remove(name);
	}

	public void setSimpleParameters(List<SimpleParameter> parameters) {
		this.getParameters().put(Constant.PARAMETER_CATEGORY_SIMPLE, parameters);
	}

	/**
	 * setSummaries: <br>
	 * Sets the Field "summaries" with a value.
	 * 
	 * @param summaries The Value to set the summaries field
	 */
	public void setSummaries(List<SummaryStage> summaries) {
		this.summaries = summaries;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(AnalysisType type) {
		this.type = type;
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
	 * setUserRights: <br>
	 * Sets the Field "userRights" with a value.
	 * 
	 * @param userRights The Value to set the userRights field
	 */
	public void setUserRights(List<UserAnalysisRight> userRights) {
		this.userRights = userRights;
	}

	/**
	 * setVersion: <br>
	 * Sets the "version" field with a value
	 * 
	 * @param version The value to set the Analysis Version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the excludeAcronyms
	 */
	public Set<String> getExcludeAcronyms() {
		return excludeAcronyms;
	}

	/**
	 * @param excludeAcronyms the excludeAcronyms to set
	 */
	public void setExcludeAcronyms(Set<String> excludeAcronyms) {
		this.excludeAcronyms = excludeAcronyms;
	}

	public List<ScaleType> getIlrImpactTypes() {
		return ilrImpactTypes;
	}

	public void setIlrImpactTypes(List<ScaleType> ilrImpactTypes) {
		this.ilrImpactTypes = ilrImpactTypes;
	}


	@Transient
	public void updateType() {
		List<ScaleType> scaleTypes = findImpacts();
		if (scaleTypes.isEmpty())
			this.type = AnalysisType.QUALITATIVE;
		else if (scaleTypes.size() == 1)
			this.type = scaleTypes.get(0).getName().equals(Constant.DEFAULT_IMPACT_NAME) ? AnalysisType.QUANTITATIVE
					: AnalysisType.QUALITATIVE;
		else if (scaleTypes.stream().anyMatch(scaleType -> scaleType.getName().equals(Constant.DEFAULT_IMPACT_NAME)))
			this.type = AnalysisType.HYBRID;
		else
			this.type = AnalysisType.QUALITATIVE;
	}

	/**
	 * versionExists: <br>
	 * Checks if given version string exists in analysis
	 * 
	 * @param version
	 * @return
	 */
	@Transient
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
	 * Returns the Calculated Cost of a Measure. This method does no more need the
	 * parameter default maintenance, but needs to get the internal and external
	 * maintenance in md as well as the recurrent investment per year in keuro. <br>
	 * Formula used:<br>
	 * Cost = ((ir * iw) + (er * ew) + in) * ((1.0 / lt) + ((im * ir) + (em * er)+
	 * ri))*(1- (isFCRM? implR : 0))<br>
	 * With:<br>
	 * ir: The Internal Setup Rate in Euro per Man Day<br>
	 * iw: The Internal Workload in Man Days<br>
	 * er: The External Setup Rate in Euro per Man Day<br>
	 * ew: The External Workload in Man Days<br>
	 * in: The Investment in kEuro<br>
	 * lt: The Lifetime in Years :: if 0 -> use The Default LifeTime in Years <br>
	 * im: The Internal MaintenanceRecurrentInvestment in Man Days<br>
	 * em: The External MaintenanceRecurrentInvestment in Man Days<br>
	 * ri: The recurrent Investment in kEuro<br>
	 * implR: Implementation rate of the measure
	 * isFCRM: Full cost related to the measure (computation mode)
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
	 * @param implementationRate
	 * 
	 * @param isFullCostRelated	
	 * 
	 * @return The Calculated Cost
	 */
	@Transient
	public static final double computeCost(double internalSetupRate, double externalSetupRate, double lifetimeDefault,
			double internalMaintenance, double externalMaintenance, double recurrentInvestment, double internalWorkLoad,
			double externalWorkLoad, double investment, double lifetime, double implementationRate,
			boolean isFullCostRelated) {

		// ****************************************************************
		// * variable initialisation
		// ****************************************************************
		// internal setup * internal wokload + external setup * external
		// workload
		// check if lifetime is not 0 -> YES: use default lifetime
		// return calculated cost
		return (((internalSetupRate * internalWorkLoad) + (externalSetupRate * externalWorkLoad) + investment)
				* (1. / (lifetime == 0 ? lifetimeDefault : lifetime)))
				+ ((internalMaintenance * internalSetupRate) + (externalMaintenance * externalSetupRate)
						+ recurrentInvestment) * (1 - (isFullCostRelated ?  implementationRate : 0));
	}

	@Transient
	@SuppressWarnings("unchecked")
	public static <T> T findSetting(AnalysisSetting setting, String value) {
		try {
			if (value == null)
				return (T) setting.getDefaultValue();
			return (T) parseSettingValue(value, setting.getType());
		} catch (Exception e) {
			return (T) setting.getDefaultValue();
		}
	}

	/**
	 * initialiseEmptyItemInformation: <br>
	 * Description
	 * 
	 * @param analysis
	 */
	@Transient
	public static final void InitialiseEmptyItemInformation(Analysis analysis) {

		if (analysis == null)
			return;
		analysis.getItemInformations().clear();
		ItemInformation iteminfo;
		iteminfo = new ItemInformation(Constant.TYPE_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.TYPE_PROFIT_ORGANISM, Constant.ITEMINFORMATION_SCOPE,
				Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.NAME_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.PRESENTATION_ORGANISM, Constant.ITEMINFORMATION_SCOPE,
				Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.SECTOR_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.RESPONSIBLE_ORGANISM, Constant.ITEMINFORMATION_SCOPE,
				Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.STAFF_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.ACTIVITIES_ORGANISM, Constant.ITEMINFORMATION_SCOPE,
				Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.EXCLUDED_ASSETS, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.OCCUPATION, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.FUNCTIONAL, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.JURIDIC, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.POL_ORGANISATION, Constant.ITEMINFORMATION_SCOPE,
				Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.MANAGEMENT_ORGANISATION, Constant.ITEMINFORMATION_SCOPE,
				Constant.EMPTY_STRING);
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
		iteminfo = new ItemInformation(Constant.PROCESSUS_DEVELOPMENT, Constant.ITEMINFORMATION_ORGANISATION,
				Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.STAKEHOLDER_IDENTIFICATION, Constant.ITEMINFORMATION_ORGANISATION,
				Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.ROLE_RESPONSABILITY, Constant.ITEMINFORMATION_ORGANISATION,
				Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.STAKEHOLDER_RELATION, Constant.ITEMINFORMATION_ORGANISATION,
				Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.ESCALATION_WAY, Constant.ITEMINFORMATION_ORGANISATION,
				Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.DOCUMENT_CONSERVE, Constant.ITEMINFORMATION_ORGANISATION,
				Constant.EMPTY_STRING);
		analysis.add(iteminfo);
	}

	@Transient
	@SuppressWarnings("unchecked")
	public static <T> T parseSettingValue(String value, Class<T> type) {
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

	public Standard findStandardByLabel(String label) {
		return analysisStandards.values().stream().map(AnalysisStandard::getStandard)
				.filter(a -> a.getLabel().equalsIgnoreCase(label)).findAny().orElse(null);
	}

	public Standard findStandardByName(String name) {
		return analysisStandards.values().stream().map(AnalysisStandard::getStandard)
				.filter(a -> a.getName().equalsIgnoreCase(name)).findAny().orElse(null);
	}

	public List<AnalysisStandard> findAllAnalysisStandard() {
		return getAnalysisStandards().values().stream().collect(Collectors.toList());
	}
}
