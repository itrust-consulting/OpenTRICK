package lu.itrust.business.ts.importation;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.util.StringUtils;

import lu.itrust.business.ts.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.ts.component.DynamicParameterComputer;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.DatabaseHandler;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOAssetType;
import lu.itrust.business.ts.database.dao.DAOLanguage;
import lu.itrust.business.ts.database.dao.DAOMeasureDescription;
import lu.itrust.business.ts.database.dao.DAOMeasureDescriptionText;
import lu.itrust.business.ts.database.dao.DAOParameterType;
import lu.itrust.business.ts.database.dao.DAOScaleType;
import lu.itrust.business.ts.database.dao.DAOStandard;
import lu.itrust.business.ts.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.ts.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOAssetTypeHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOLanguageHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOMeasureDescriptionHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOMeasureDescriptionTextHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOParameterTypeHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOScaleTypeHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOStandardHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOUserAnalysisRightHBM;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisSetting;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.asset.AssetType;
import lu.itrust.business.ts.model.cssf.RiskProbaImpact;
import lu.itrust.business.ts.model.cssf.RiskProfile;
import lu.itrust.business.ts.model.cssf.RiskStrategy;
import lu.itrust.business.ts.model.cssf.tools.CategoryConverter;
import lu.itrust.business.ts.model.general.AssetTypeValue;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.general.Phase;
import lu.itrust.business.ts.model.general.SecurityCriteria;
import lu.itrust.business.ts.model.general.document.impl.SimpleDocument;
import lu.itrust.business.ts.model.general.document.impl.SimpleDocumentType;
import lu.itrust.business.ts.model.history.History;
import lu.itrust.business.ts.model.history.helper.ComparatorHistoryVersion;
import lu.itrust.business.ts.model.ilr.AssetEdge;
import lu.itrust.business.ts.model.ilr.AssetNode;
import lu.itrust.business.ts.model.ilr.ILRImpact;
import lu.itrust.business.ts.model.ilr.Position;
import lu.itrust.business.ts.model.iteminformation.ItemInformation;
import lu.itrust.business.ts.model.parameter.IImpactParameter;
import lu.itrust.business.ts.model.parameter.IMaturityParameter;
import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.parameter.IProbabilityParameter;
import lu.itrust.business.ts.model.parameter.helper.Bounds;
import lu.itrust.business.ts.model.parameter.helper.ParameterManager;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.parameter.impl.DynamicParameter;
import lu.itrust.business.ts.model.parameter.impl.IlrSoaScaleParameter;
import lu.itrust.business.ts.model.parameter.impl.ImpactParameter;
import lu.itrust.business.ts.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.ts.model.parameter.impl.MaturityParameter;
import lu.itrust.business.ts.model.parameter.impl.Parameter;
import lu.itrust.business.ts.model.parameter.impl.RiskAcceptanceParameter;
import lu.itrust.business.ts.model.parameter.impl.SimpleParameter;
import lu.itrust.business.ts.model.parameter.type.impl.ParameterType;
import lu.itrust.business.ts.model.parameter.value.IParameterValue;
import lu.itrust.business.ts.model.parameter.value.IValue;
import lu.itrust.business.ts.model.parameter.value.impl.FormulaValue;
import lu.itrust.business.ts.model.parameter.value.impl.Value;
import lu.itrust.business.ts.model.riskinformation.RiskInformation;
import lu.itrust.business.ts.model.scale.ScaleType;
import lu.itrust.business.ts.model.scale.Translation;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.scenario.ScenarioType;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.AssetStandard;
import lu.itrust.business.ts.model.standard.MaturityStandard;
import lu.itrust.business.ts.model.standard.NormalStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.StandardType;
import lu.itrust.business.ts.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.ts.model.standard.measure.impl.MaturityMeasure;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureAssetValue;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureProperties;
import lu.itrust.business.ts.model.standard.measure.impl.NormalMeasure;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.ts.usermanagement.User;

/**
 * ImportAnalysis: <br>
 * This class is for importing an Analysis from an TRICK Light SQLite file into
 * Java objects.
 * 
 * @author itrust consulting s.a r.l. - BJA,SME
 * @version 0.1
 * @since 2012-12-14
 */
public class ImportAnalysis {

	/** The Analysis Object */
	private Analysis analysis = null;

	/** Map of AnalysisStandards */
	private Map<Standard, AnalysisStandard> analysisStandards = null;

	private Map<String, Assessment> assessments;

	/** Map of Assets */
	private Map<Integer, Asset> assets = null;

	private Map<Integer, AssetType> assetTypes = null;

	private boolean compability1X = false;

	private String currentSqliteTable = "";

	private String version = null;

	private DAOAnalysis daoAnalysis;

	private DAOAssetType daoAssetType;

	private DAOLanguage daoLanguage;

	private DAOMeasureDescription daoMeasureDescription;

	private DAOMeasureDescriptionText daoMeasureDescriptionText;

	private DAOParameterType daoParameterType;

	private DAOScaleType daoScaleType;

	private DAOStandard daoStandard;

	private DAOUserAnalysisRight daoUserAnalysisRight;

	private ValueFactory factory = null;

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	private String idTask;

	private Map<String, IImpactParameter> impactParameters = null;

	private Map<String, ScaleType> impactTypes;

	/** Map of Measures */
	private Map<String, Measure> measures = null;

	/** Map of Phases */
	private Map<Integer, Phase> phases = null;

	private Map<String, IProbabilityParameter> probabilities = null;

	private Map<String, RiskProfile> riskProfiles = null;

	private Map<Integer, List<Integer>> scenarioAssets = null;

	/** Map of Scenarios */
	private Map<Integer, Scenario> scenarios = null;

	private Session session;

	/** The SQLite Database Handler */
	private DatabaseHandler sqlite = null;

	/** Map of Standards */
	private Map<String, Standard> standards = null;

	private int maxProgress = 0;

	private int progress = 0;

	private int globalProgress = 0;

	/***********************************************************************************************
	 * Constructor
	 **********************************************************************************************/

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	public ImportAnalysis() {
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param analysis The Analysis Object
	 * @param sqlite   The SQLite Object (DatabaseHandler)
	 */
	public ImportAnalysis(Analysis analysis, DatabaseHandler sqlite) {
		this.analysis = analysis;
		this.sqlite = sqlite;
	}

	public ImportAnalysis(Analysis analysis) {
		setAnalysis(analysis);
	}

	/**
	 * getAnalysis: <br>
	 * Returns the analysis of the object
	 * 
	 * @return The analysis object
	 */
	public Analysis getAnalysis() {
		return this.analysis;
	}

	/**
	 * @return the currentSqliteTable
	 */
	public String getCurrentSqliteTable() {
		return currentSqliteTable;
	}

	/**
	 * @return the daoScaleType
	 */
	public DAOScaleType getDaoScaleType() {
		return daoScaleType;
	}

	public DAOUserAnalysisRight getDaoUserAnalysisRight() {
		return daoUserAnalysisRight;
	}

	/**
	 * @return the idTask
	 */
	public String getIdTask() {
		return idTask;
	}

	/**
	 * 
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * ImportAnAnalysis: <br>
	 * Method used to import and given analysis using an sqlite file into the mysql
	 * database.
	 * 
	 * @param session
	 * 
	 * 
	 * @throws Exception
	 */

	protected void notifyUpdate(MessageHandler handler, String code, String message, int progress) {
		handler.update(code, message, progress);
		InstanceManager.getServiceTaskFeedback().send(idTask, handler);
	}

	public boolean ImportAnAnalysis(Session hbernateSession) throws Exception {

		try {

			if (hbernateSession == null)
				throw new TrickException("error.database.no_session", "No database session");

			setSession(hbernateSession);

			initialise();

			session.beginTransaction();

			System.out.println("Importing...");

			// ****************************************************************
			// * create analysis id, analysis label, analysis language and
			// * Histories. Creates Analysis Entries into the Database
			// ****************************************************************

			MessageHandler handler = new MessageHandler();

			notifyUpdate(handler, "info.analysis.importing", "Importing", increase(1));
			importAnalyses();
			importSettings();

			// ****************************************************************
			// * import risk information
			// ****************************************************************

			notifyUpdate(handler, "info.risk_information.importing", "Importing risk information", increase(1));
			importRiskInformation();

			// ****************************************************************
			// * import item information
			// ****************************************************************

			notifyUpdate(handler, "info.risk_information.importing", "Import item information", increase(4));

			importItemInformation();

			// ****************************************************************
			// * import simple parameters
			// ****************************************************************

			notifyUpdate(handler, "info.simple_parameters.importing", "Import simple parameters", increase(6));
			importSimpleParameters();

			// ****************************************************************
			// * import dynamic parameters
			// ****************************************************************

			notifyUpdate(handler, "info.dynamic_parameters.importing", "Import dynamic parameters", increase(2));// 12%
			importDynamicParameters();

			// ****************************************************************
			// * import extended parameters
			// ****************************************************************
			notifyUpdate(handler, "info.impact_type.importing", "Import parameter type", increase(3));// 15%
			importImpactParameterTypes();

			notifyUpdate(handler, "info.extended_parameters.importing", "Import extended parameters", increase(3));// 18%
			importImpactParameters();
			// ****************************************************************
			// * import extended parameters
			// ****************************************************************
			notifyUpdate(handler, "info.extended_parameters.importing", "Import extended parameters", increase(2));// 20%
			importProbabilities();

			// ****************************************************************
			// * import maturity parameters
			// ****************************************************************

			notifyUpdate(handler, "info.maturity_parameters.importing", "Import maturity parameters", increase(3));// 23%
			importMaturityParameters();

			// ****************************************************************
			// * import assets
			// ****************************************************************

			notifyUpdate(handler, "info.asset.importing", "Import assets", increase(10));// 25%
			importAssets();

			// ****************************************************************
			// * import scenarios
			// ****************************************************************

			notifyUpdate(handler, "info.scenario.importing", "Import scenarios", increase(5));// 35%
			importScenarios();

			// Update value factory
			factory = new ValueFactory(this.analysis.getParameters());

			// ****************************************************************
			// * import assessments
			// ****************************************************************

			notifyUpdate(handler, "info.assessments.importing", "Import assessments", increase(5));// 40%
			importAssessments();

			notifyUpdate(handler, "info.risk_profile.importing", "Import risk profile", increase(5));// 45%
			importRiskProfile();

			notifyUpdate(handler, "info.dependancy_graph.importing", "Import asset dependency", increase(5));// 50%
			importDependencyGraph();

			// ****************************************************************
			// * import phases
			// ****************************************************************

			notifyUpdate(handler, "info.phase.importing", "Import phases", increase(5));// 55%
			importPhases();

			notifyUpdate(handler, "info.norm_measures.importing", "Analysis normal measures", increase(5));// 60%
			importSimpleDocuments();

			// ****************************************************************
			// * import AnalysisStandard measures
			// ****************************************************************
			notifyUpdate(handler, "info.norm_measures.importing", "Analysis normal measures", increase(5));// 65%
			importNormalMeasures();

			importRiskProfileMeasures();

			// ****************************************************************
			// * import asset type values
			// ****************************************************************
			notifyUpdate(handler, "info.asset_type_value.importing", "Import asset type values", increase(10));// 70%
			importAssetTypeValues();

			importAssetValues();

			// ****************************************************************
			// * import maturity measures
			// ****************************************************************

			notifyUpdate(handler, "info.maturity_measure.importing", "Import maturity measures", increase(10));// 80%
			importMaturityMeasures();

			if (measures != null)
				analysis.setAnalysisStandards(analysisStandards.values().stream()
						.collect(Collectors.toMap(a -> a.getStandard().getName(), Function.identity())));

			computeMeasureCost();

			// System.out.println("Saving Data to Database...");

			notifyUpdate(handler, "import.saving.analysis", "Saving Data to Database", increase(5));// 90%

			System.out.println("Saving Analysis Data...");

			// save analysis
			daoAnalysis.save(this.analysis);

			// Update values of dynamic parameters
			if (this.analysis.isQuantitative())
				new DynamicParameterComputer(session, daoAnalysis)
						.computeForAnalysis(this.analysis);

			// update ALE of asset objects
			AssessmentAndRiskProfileManager.updateRiskDendencies(analysis, factory);

			daoAnalysis.saveOrUpdate(this.analysis);

			notifyUpdate(handler, "info.commit.transcation", "Commit transaction", increase(5));// 95%
			if (session != null) {
				session.getTransaction().commit();
				notifyUpdate(handler, "success.saving.analysis", "Analysis has been successfully saved", increase(5));// 100%
			}
			System.out.println("Import Done!");

			return true;
		} catch (Exception e) {
			try {
				e.printStackTrace();
				if (session != null && session.isOpen() && session.getTransaction().getStatus().canRollback())
					session.getTransaction().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			throw e;
		} finally {
			// clear maps
			clearData();
		}
	}

	private void computeMeasureCost() {
		final boolean isFullCostRelated = this.analysis.findSetting(AnalysisSetting.ALLOW_FULL_COST_RELATED_TO_MEASURE);
		final double internalSetupRate = this.analysis.findParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE);
		final double externalSetupRate = this.analysis.findParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE);
		final double defaultLifetime = this.analysis.findParameter(Constant.PARAMETER_LIFETIME_DEFAULT);
		analysis.getAnalysisStandards().values().parallelStream().flatMap(e -> e.getMeasures().parallelStream())
				.forEach(m -> {
					final double implementationRate = m.getImplementationRateValue(factory) * 0.01;
					final double cost = Analysis.computeCost(internalSetupRate, externalSetupRate, defaultLifetime,
							m.getInternalMaintenance(), m.getExternalMaintenance(),
							m.getRecurrentInvestment(), m.getInternalWL(), m.getExternalWL(),
							m.getInvestment(), m.getLifetime(), implementationRate, isFullCostRelated);
					m.setCost(cost > 0D ? cost : 0D);
				});
	}

	private void importSimpleDocuments() throws SQLException {

		ResultSet rs = null;
		try {
			rs = sqlite.query("SELECT * FROM simple_document");
			if (rs == null)
				return;
			while (rs.next()) {
				final byte[] data = rs.getBytes("dtData");
				final String name = rs.getString("dtName");
				final long length = rs.getLong("dtLength");
				final Timestamp created = rs.getTimestamp("dtCreated");
				final SimpleDocumentType type = SimpleDocumentType.valueOf(rs.getString("dtType"));
				if (type == null || data == null || data.length == 0)
					continue;
				analysis.getDocuments().put(type, new SimpleDocument(type, name, length, data, created));
			}
		} finally {
			if (rs != null)
				rs.close();
		}

	}

	/**
	 * @return the compability1X
	 */
	public boolean isCompability1X() {
		return compability1X;
	}

	public void setAnalysis(Analysis analysis2) {
		this.analysis = analysis2;
	}

	/**
	 * @param compability1x the compability1X to set
	 */
	public void setCompability1X(boolean compability1x) {
		compability1X = compability1x;
	}

	/**
	 * @param currentSqliteTable the currentSqliteTable to set
	 */
	public void setCurrentSqliteTable(String currentSqliteTable) {
		this.currentSqliteTable = currentSqliteTable;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @param daoAnalysis the daoAnalysis to set
	 */
	public void setDaoAnalysis(DAOAnalysis daoAnalysis) {
		this.daoAnalysis = daoAnalysis;
	}

	/**
	 * @param daoAssetType the daoAssetType to set
	 */
	public void setDaoAssetType(DAOAssetType daoAssetType) {
		this.daoAssetType = daoAssetType;
	}

	/**
	 * @param daoLanguage the daoLanguage to set
	 */
	public void setDaoLanguage(DAOLanguage daoLanguage) {
		this.daoLanguage = daoLanguage;
	}

	/**
	 * @param daoMeasureDescription the daoMeasureDescription to set
	 */
	public void setDaoMeasureDescription(DAOMeasureDescription daoMeasureDescription) {
		this.daoMeasureDescription = daoMeasureDescription;
	}

	/**
	 * @param daoMeasureDescriptionText the daoMeasureDescriptionText to set
	 */
	public void setDaoMeasureDescriptionText(DAOMeasureDescriptionText daoMeasureDescriptionText) {
		this.daoMeasureDescriptionText = daoMeasureDescriptionText;
	}

	/**
	 * @param daoParameterType the daoParameterType to set
	 */
	public void setDaoParameterType(DAOParameterType daoParameterType) {
		this.daoParameterType = daoParameterType;
	}

	/**
	 * @param daoScaleType the daoScaleType to set
	 */
	public void setDaoScaleType(DAOScaleType daoScaleType) {
		this.daoScaleType = daoScaleType;
	}

	/**
	 * @param daoStandard the daoStandard to set
	 */
	public void setDaoStandard(DAOStandard daoStandard) {
		this.daoStandard = daoStandard;
	}

	public void setDaoUserAnalysisRight(DAOUserAnalysisRight daoUserAnalysisRight) {
		this.daoUserAnalysisRight = daoUserAnalysisRight;
	}

	public void setDatabaseHandler(DatabaseHandler sqlite2) {
		this.sqlite = sqlite2;

	}

	/**
	 * @param idTask the idTask to set
	 */
	public void setIdTask(String idTask) {
		this.idTask = idTask;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public void updateAnalysis(Customer customer, User owner) {
		if (this.analysis == null)
			this.analysis = new Analysis(customer, owner);
		else {
			this.analysis.setCustomer(customer);
			this.analysis.setOwner(owner);
			this.analysis.addUserRight(owner, AnalysisRight.ALL);
		}
	}

	protected void initialise() {
		setDaoAnalysis(new DAOAnalysisHBM(session));
		setDaoAssetType(new DAOAssetTypeHBM(session));
		setDaoLanguage(new DAOLanguageHBM(session));
		setDaoMeasureDescription(new DAOMeasureDescriptionHBM(session));
		setDaoMeasureDescriptionText(new DAOMeasureDescriptionTextHBM(session));
		setDaoStandard(new DAOStandardHBM(session));
		setDaoParameterType(new DAOParameterTypeHBM(session));
		setDaoUserAnalysisRight(new DAOUserAnalysisRightHBM(session));
		setDaoScaleType(new DAOScaleTypeHBM(session));
	}

	private void addImpactType(String name, String translate, String shortName, String prefix) {
		ScaleType type = daoScaleType.findOne(name);
		if (type == null) {
			type = new ScaleType(name, generateAcronym(name, prefix + name.substring(0, 1).toLowerCase()));
			type.put(this.analysis.getLanguage().getAlpha2(), new Translation(translate, shortName));
			daoScaleType.saveOrUpdate(type);
		}
		impactTypes.put(type.getName(), type);
	}

	/**
	 * clearData: <br>
	 * Clear maps
	 */
	private void clearData() {
		if (analysisStandards != null)
			analysisStandards.clear();
		if (assets != null)
			assets.clear();
		if (measures != null)
			measures.clear();
		if (standards != null)
			standards.clear();
		if (phases != null)
			phases.clear();
		if (scenarios != null)
			scenarios.clear();
		if (impactParameters != null)
			impactParameters.clear();
		if (assessments != null)
			assessments.clear();
		if (probabilities != null)
			probabilities.clear();
		if (impactTypes != null)
			impactTypes.clear();
		if (riskProfiles != null)
			riskProfiles.clear();
	}

	private String generateAcronym(String name, String acronym) {
		int length = 1;
		while (daoScaleType.hasAcronym(acronym)) {
			if (acronym.equals(name) || length >= name.length())
				throw new TrickException("error.generate.impact.acronym",
						"Impact acronym cannot be generated, please contact your support.");
			acronym = "i" + name.substring(0, length++).toLowerCase();
		}
		return acronym;
	}

	/**
	 * importAnalyses: <br>
	 * <ul>
	 * <li>Creates Analysis ID from Sqlite File</li>
	 * <li>Creates Analysis Versions from Sqlite History Table</li>
	 * <li>Sets the Language Object of the Analysis and set the "language" field
	 * </li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importAnalyses() throws Exception {

		System.out.println("Import versions...");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		String acroLanguage = "";
		Language language = null;

		ResultSet rs = null;
		String query = "";
		setCurrentSqliteTable("identifier");

		// ****************************************************************
		// * Retrieve analysis ID and label
		// ****************************************************************

		try {
			// build query
			query = "SELECT * from identifier LIMIT 1";
			// execute query
			rs = sqlite.query(query, null);
			// retrieve results
			if (rs.next()) {

				// ****************************************************************
				// * set analysis ID
				// ****************************************************************
				if (analysis.getIdentifier() == null)
					this.analysis.setIdentifier(rs.getString(Constant.IDENTIFIER_ID));

				// ****************************************************************
				// * set analysis label
				// ****************************************************************
				if (analysis.getLabel() == null)
					this.analysis.setLabel(rs.getString(Constant.IDENTIFIER_LABEL));

				String type = getString(rs, "analysis_type");

				if (type == null) {
					analysis.setType(getBoolean(rs, "cssf") ? AnalysisType.QUALITATIVE : AnalysisType.QUANTITATIVE);
					setVersion("1.8");
				} else
					analysis.setType(AnalysisType.valueOf(type));

				if (getVersion() == null) {
					setVersion(getString(rs, "version"));
					if (getVersion() == null)
						setVersion("2.3");
				}
				if (analysis.isQuantitative())
					analysis.setUncertainty(getBoolean(rs, "uncertainty"));
				else
					analysis.setUncertainty(false);
			}
			setCompability1X(getVersion() == null || getVersion().equals("1.8"));
		} finally {
			// close result
			if (rs != null)
				rs.close();
		}

		List<Customer> customers = daoAnalysis.getCustomersByIdAnalysis(this.analysis.getIdentifier());
		if (customers.size() > 1 || !(customers.isEmpty() || customers.contains(this.analysis.getCustomer())))
			throw new TrickException("error.bad.customer", "This analysis already belong to an other customer");
		this.analysis.setCreationDate(new Timestamp(System.currentTimeMillis()));

		// ****************************************************************
		// * Retireve language
		// ****************************************************************

		// extract language from analysisid
		acroLanguage = this.analysis.getIdentifier().substring(0, 3);

		// retrieve language from acronym
		language = daoLanguage.getByAlpha3(acroLanguage);

		// if language is not found, create the english language object and save
		// it to the database
		if (language == null) {
			language = new Language();
			if (acroLanguage.equalsIgnoreCase("FRA")) {
				language.setAlpha3("FRA");
				language.setName("Français");
				language.setAltName("French");
			} else {
				language.setAlpha3("ENG");
				language.setName("English");
				language.setAltName("Anglais");
			}

			// ****************************************************************
			// * create language object
			// ****************************************************************

		}

		// ****************************************************************
		// * add the language to the object variable
		// ****************************************************************
		this.analysis.setLanguage(language);

		if (analysis.isProfile()) {
			Timestamp ts = new Timestamp(System.currentTimeMillis());

			SimpleDateFormat outDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String tsstring = outDateFormat.format(ts);

			getAnalysis().setIdentifier(getAnalysis().getLanguage().getAlpha3() + "_" + tsstring);
		}

		// ****************************************************************
		// * load Histories
		// ****************************************************************
		loadHistoryFromSqlite();

		// ****************************************************************
		// * Imports the Analysis and Versions into the Database
		// ****************************************************************
		importAnalysisAndVersions();
	}

	/**
	 * importAnalysisAndVersions: <br>
	 * <ul>
	 * <li>Generates all History Entries for this Analysis in the Database</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importAnalysisAndVersions() throws Exception {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************

		// create DAO Analysis object
		Analysis analysis = null;

		History history = null;

		// ****************************************************************
		// * add analysis and / or version if not exists
		// ****************************************************************

		// last history is not empty (after hisotry loop)
		this.analysis.setData(true);

		// set analysis creationdate
		this.analysis.setCreationDate(new Timestamp(System.currentTimeMillis()));

		List<String> versions = daoAnalysis.getAllVersion(this.analysis.getIdentifier());

		Comparator<String> comparator = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return NaturalOrderComparator.compareTo(o1, o2);
			}
		};

		Collections.sort(versions, comparator);

		if (!(versions.isEmpty()
				|| daoUserAnalysisRight.isUserAuthorizedOrOwner(this.analysis.getIdentifier(),
						versions.get(versions.size() - 1), this.analysis.getOwner(), AnalysisRight.ALL)))
			throw new TrickException("error.not_authorized", "Insufficient permissions!");

		// initialise analysis version to the last history version
		Collections.sort(this.analysis.getHistories(), new ComparatorHistoryVersion());

		this.analysis.getHistories().stream()
				.filter(analysisHistory -> !versions.contains(analysisHistory.getVersion()))
				.forEach(analysisHistory -> versions.add(analysisHistory.getVersion()));

		if (!versions.isEmpty())
			Collections.sort(versions, comparator);

		if (this.analysis.getHistories().isEmpty())
			this.analysis.setVersion("1.0.0");
		else
			this.analysis.setVersion(this.analysis.getLastHistory().getVersion());

		// ****************************************************************
		// * Parse all history entries except last one
		// ****************************************************************

		// parse each history of analysis (needs to be at least one!)
		for (int i = 0; i < this.analysis.getHistories().size() - 1; i++) {

			// ****************************************************************
			// * check if analysis and version exists
			// ****************************************************************

			history = this.analysis.getHistories().get(i);

			// check if analysis with this version does NOT already exist -> YES
			if (!daoAnalysis.exists(this.analysis.getIdentifier(), history.getVersion())) {
				// ****************************************************************
				// * store analysis with history entries to the current version
				// into database
				// ****************************************************************

				// create new analysis object
				analysis = new Analysis();
				int indexOfVersion = versions.indexOf(history.getVersion());
				Analysis previousVersion = indexOfVersion > 0
						? daoAnalysis.getFromIdentifierVersionCustomer(this.analysis.getIdentifier(),
								versions.get(indexOfVersion - 1), this.analysis.getCustomer().getId())
						: null;

				// set data for analyses
				analysis.setIdentifier(this.analysis.getIdentifier());
				analysis.setVersion(history.getVersion());
				analysis.setCreationDate(this.analysis.getCreationDate());
				analysis.setLabel(this.analysis.getLabel());
				analysis.setLanguage(this.analysis.getLanguage());
				analysis.setCustomer(this.analysis.getCustomer());
				analysis.setOwner(this.analysis.getOwner());
				analysis.setBasedOnAnalysis(previousVersion);
				if (previousVersion != null) {
					for (UserAnalysisRight analysisRight : previousVersion.getUserRights())
						analysis.addUserRight(analysisRight.getUser(), analysisRight.getRight());
				}

				analysis.editUserRight(this.analysis.getOwner(), AnalysisRight.ALL);
				// add history entries to this history entry
				for (int j = 0; j <= i; j++) {
					// clone history entry and add it to the list of history
					// entries
					analysis.addAHistory((History) this.analysis.getHistories().get(j).duplicate());
				}

				// set empty analysis or filled analysis
				analysis.setData(false);

				// save analysis into database
				daoAnalysis.saveOrUpdate(analysis);
			}
		}

		// retrive last version of the analysis if it exists
		analysis = daoAnalysis.getFromIdentifierVersionCustomer(this.analysis.getIdentifier(),
				this.analysis.getVersion(), this.analysis.getCustomer().getId());

		// if analysis is not null (The Analysis and its version has aready been
		// imported)
		if (analysis != null) {

			// check if the analysis has already data -> YES
			if (!analysis.hasData()) {

				// fill the analysis with data and change flag of hasData
				// analysis
				System.out.println("Your file has already been imported without data");
				this.analysis = analysis;
				this.analysis.setData(true);
			} else {
				// analysis had already been imported and has data
				throw new TrickException("error.import.analysis.version.exist",
						String.format(
								"Your file has already been imported, whether it is a new version( %s ), do not forget to increase version",
								analysis.getVersion()),
						analysis.getVersion());
			}
		}

		Analysis previousVersion = history == null ? null
				: daoAnalysis.getFromIdentifierVersionCustomer(this.analysis.getIdentifier(), history.getVersion(),
						this.analysis.getCustomer().getId());
		this.analysis.setBasedOnAnalysis(previousVersion);
		if (previousVersion != null) {
			for (UserAnalysisRight analysisRight : previousVersion.getUserRights())
				this.analysis.addUserRight(analysisRight.getUser(), analysisRight.getRight());
		}
		this.analysis.editUserRight(this.analysis.getOwner(), AnalysisRight.ALL);
	}

	/**
	 * importAssessments: <br>
	 * <ul>
	 * <li>Imports all Assessments</li>
	 * <li>Calculates ALE, ALEO, ALEP</li>
	 * <li>Creates Objects for each Assessment</li>
	 * <li>Adds the Objects to the "assessments" field List</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importAssessments() throws Exception {

		System.out.println("Import Assessment");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;

		// ****************************************************************
		// * Query sqlite for all assessment
		// ****************************************************************

		try {
			// build query
			String query = "SELECT * FROM assessment";

			// execute query
			rs = sqlite.query(query);

			// Loop assessment
			while (rs.next()) {

				// ****************************************************************
				// * retrieve asset instance
				// ****************************************************************

				int assetId = rs.getInt(Constant.ASSET_ID_ASSET), scenarioId = rs.getInt(Constant.THREAT_ID_THREAT);

				// ****************************************************************
				// * create assessment instance
				// ****************************************************************
				Assessment tmpAssessment = new Assessment();
				tmpAssessment.setAsset(assets.get(assetId));
				tmpAssessment.setScenario(scenarios.get(scenarioId));
				tmpAssessment.setImpactReal(rs.getDouble(Constant.ASSESSMENT_IMPACT_REAL));
				tmpAssessment.setLikelihood(factory.findProb(rs.getString(Constant.ASSESSMENT_POTENTIALITY)));
				tmpAssessment.setLikelihoodReal(rs.getDouble(Constant.ASSESSMENT_POTENTIALITY_REAL));
				tmpAssessment.setVulnerability(getInt(rs, Constant.ASSESSMENT_VULNERABILITY));
				tmpAssessment.setUncertainty(rs.getDouble(Constant.ASSESSMENT_UNCERTAINTY));
				tmpAssessment.setComment(rs.getString(Constant.ASSESSMENT_COMMENT));
				tmpAssessment.setHiddenComment(rs.getString(Constant.ASSESSMENT_HIDE_COMMENT));
				tmpAssessment.setCockpit(getString(rs, Constant.ASSESSMENT_COCKPIT));
				tmpAssessment.setOwner(getStringOrEmpty(rs, "owner"));

				tmpAssessment.setSelected(
						rs.getString(Constant.ASSESSMENT_SEL_ASSESSMENT).equals(Constant.ASSESSMENT_SELECTED));

				tmpAssessment.setALE(tmpAssessment.getImpactReal() * tmpAssessment.getLikelihoodReal());

				tmpAssessment.setALEO(tmpAssessment.getALE() / tmpAssessment.getUncertainty());

				tmpAssessment.setALEP(tmpAssessment.getALE() * tmpAssessment.getUncertainty());

				if (isCompability1X()) {
					if (analysis.isQuantitative())
						setImpact(tmpAssessment, Constant.DEFAULT_IMPACT_NAME, tmpAssessment.getImpactReal());

					if (analysis.isQualitative()) {
						for (int i = 0; i < Constant.ASSESSMENT_IMPACT_NAMES.length; i++)
							setImpact(tmpAssessment, Constant.DEFAULT_IMPACT_TYPE_NAMES[i],
									getString(rs, Constant.ASSESSMENT_IMPACT_NAMES[i]));
					}
				} else {
					if (assessments == null)
						assessments = new HashMap<>();
					assessments.put(key(assetId, scenarioId), tmpAssessment);
				}
				// ****************************************************************
				// * add instance to list of assessments
				// ****************************************************************
				this.analysis.add(tmpAssessment);
			}

			/**
			 * load impact
			 */

			if (!(assessments == null || assessments.isEmpty())) {
				rs.close();
				// execute query
				rs = sqlite.query("SELECT * FROM assessment_impacts");
				if (rs == null)
					return;
				boolean isCompatibilityMode = false;
				boolean isFirst = true;
				while (rs.next()) {
					if (isFirst) {
						isCompatibilityMode = getString(rs, "raw_value") == null;
						isFirst = false;
					}

					final IValue impact;
					final double value = getDouble(rs, "value");
					final String name = getString(rs, "name");
					final int assetId = rs.getInt(Constant.ASSET_ID_ASSET);
					final int scenarioId = rs.getInt(Constant.THREAT_ID_THREAT);

					if (isCompatibilityMode)
						impact = findValue(value, name);
					else {
						final String raw_value = getString(rs, "raw_value");
						if (raw_value == null || raw_value.trim().isEmpty())
							impact = findValue(value, name);
						else
							impact = findValue(raw_value, name);
					}
					assessments.get(key(assetId, scenarioId)).setImpact(impact);
				}
			}

		} finally {
			// Close ResultSet
			if (rs != null)
				rs.close();
		}
	}

	/**
	 * importAssets: <br>
	 * <ul>
	 * <li>Imports all Assets for this Version of Analysis</li>
	 * <li>Creates Objects for each Asset</li>
	 * <li>Adds the Objects to the "assets" field List</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importAssets() throws Exception {

		System.out.println("Import Assets");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;
		String query = "";
		String typename = "";
		assetTypes = new HashMap<>();
		assets = new HashMap<>();
		AssetType assetType = null;
		Asset tempAsset = null;

		// ****************************************************************
		// * Query sqlite for all assets types
		// ****************************************************************

		// ****************************************************************
		// * Query sqlite for all assets
		// ****************************************************************

		try {
			// build query
			query = "SELECT * FROM asset_types order by id_type_asset";
			// execute query
			rs = sqlite.query(query, null);
			// Loop assets
			while (rs.next()) {

				// ****************************************************************
				// * Insert data into asset type table
				// ****************************************************************

				// build query
				typename = rs.getString(Constant.ASSET_TYPE_LABEL);

				// retrieve asset type by name
				assetType = daoAssetType.getByName(typename);

				// check if asset type exists -> NO
				if (assetType == null) {

					// create new asset type
					assetType = new AssetType(typename);

					// save asset type into database
					daoAssetType.save(assetType);
				}

				// add asset type to map of asset types
				assetTypes.put(rs.getInt(Constant.ASSET_ID_TYPE_ASSET), assetType);
			}

			// close result
			rs.close();
			// build query
			query = "SELECT * FROM assets";
			// execute query
			rs = sqlite.query(query, null);
			// Loop assets
			while (rs.next()) {

				// ****************************************************************
				// * Insert data into asset table
				// ****************************************************************

				// ****************************************************************
				// * select label of asset type
				// ****************************************************************

				// ****************************************************************
				// * create asset instance
				// ****************************************************************
				tempAsset = new Asset();
				tempAsset.setName(rs.getString(Constant.ASSET_NAME_ASSET));
				tempAsset.setAssetType(assetTypes.get(rs.getInt(Constant.ASSET_ID_TYPE_ASSET)));
				tempAsset.setValue(rs.getDouble(Constant.ASSET_VALUE_ASSET));
				tempAsset.setComment(rs.getString(Constant.ASSET_COMMENT_ASSET));
				tempAsset.setHiddenComment(rs.getString(Constant.ASSET_HIDE_COMMENT_ASSET));
				tempAsset.setRelatedName(getString(rs, Constant.ASSET_RELATED_NAME));
				tempAsset.setSelected(rs.getString(Constant.ASSET_SEL_ASSET).equalsIgnoreCase(Constant.ASSET_SELECTED));

				// store asset to build assessment.
				assets.put(rs.getInt(Constant.ASSET_ID_ASSET), tempAsset);

				// ****************************************************************
				// * add instance to list of assets
				// ****************************************************************
				this.analysis.add(tempAsset);
			}
		} finally {
			// Close ResultSet
			if (rs != null)
				rs.close();
		}

	}

	/**
	 * importAssetTypeValues: <br>
	 * <ul>
	 * <li>Imports all Asset Type Values for Measure standards</li>
	 * <li>creates Objects for each Asset Type Value in each Measure</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importAssetTypeValues() throws Exception {

		currentSqliteTable = "spec_type_asset_measure";

		System.out.println("Import Asset Type Values ");

		String query = "SELECT id_type_asset, value_spec FROM spec_type_asset_measure where id_norme=? and version_norme=? and ref_measure=?";

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		NormalMeasure normalMeasure = null;

		// ****************************************************************
		// * create asset type default values in mysql database
		// ****************************************************************
		// createAssetTypeDefaultValues();

		// ****************************************************************
		// * check assettypevalues for values of 101 (-1) then find
		// * previous levels until an acceptable value was found
		// ****************************************************************

		// parse measures
		for (Measure measure : measures.values()) {

			// standard measure -> YES
			if (measure instanceof NormalMeasure) {

				// store into object
				normalMeasure = (NormalMeasure) measure;

				ResultSet rs = null;

				AssetTypeValue assetTypeValue = null;
				AssetType assetType = null;

				// ****************************************************************
				// * retrieve asset type values for measures
				// ****************************************************************

				final List<Object> params = new ArrayList<>();

				params.add(normalMeasure.getMeasureDescription().getStandard().getLabel());

				params.add(normalMeasure.getMeasureDescription().getStandard().getVersion());

				params.add(normalMeasure.getMeasureDescription().getReference());

				try {
					// execute query
					rs = sqlite.query(query, params);
					while (rs.next()) {

						// ****************************************************************
						// * retrieve standard and measure
						// ****************************************************************
						assetTypeValue = new AssetTypeValue();

						// ****************************************************************
						// * retrieve asset type label for the instance creation
						// ****************************************************************

						assetType = assetTypes.get(rs.getInt(Constant.ASSET_ID_TYPE_ASSET));
						assetTypeValue.setAssetType(assetType);
						assetTypeValue.setValue(rs.getInt(Constant.VALUE_SPEC));

						// add the asset type value to the measure
						normalMeasure.addAnAssetTypeValue(assetTypeValue);
					}
				} finally {
					// close result
					if (rs != null)
						rs.close();
				}

			}
		}
	}

	/**
	 * importAssetTypeValues: <br>
	 * <ul>
	 * <li>Imports all Asset Type Values for Measure standards</li>
	 * <li>creates Objects for each Asset Type Value in each Measure</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importAssetValues() throws Exception {

		System.out.println("Import Asset Values ");

		currentSqliteTable = "spec_asset_measure";

		final String query = "SELECT id_asset, value_spec FROM spec_asset_measure where id_norme = ? and version_norme = ? and ref_measure = ?";

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		AssetMeasure assetMeasure = null;

		// ****************************************************************
		// * create asset type default values in mysql database
		// ****************************************************************
		// createAssetTypeDefaultValues();

		// ****************************************************************
		// * check assettypevalues for values of 101 (-1) then find
		// * previous levels until an acceptable value was found
		// ****************************************************************

		// parse measures
		for (Measure measure : measures.values()) {

			// standard measure -> YES
			if (measure instanceof AssetMeasure && measure.getMeasureDescription().isComputable()) {

				// store into object
				assetMeasure = (AssetMeasure) measure;

				ResultSet rs = null;

				MeasureAssetValue assetValue = null;
				Asset asset = null;

				// ****************************************************************
				// * retrieve asset type values for measures
				// ****************************************************************

				final List<Object> params = new ArrayList<>();

				params.add(assetMeasure.getMeasureDescription().getStandard().getLabel());
				params.add(assetMeasure.getMeasureDescription().getStandard().getVersion());
				params.add(assetMeasure.getMeasureDescription().getReference());

				try {
					// execute query
					rs = sqlite.query(query, params);
					while (rs.next()) {

						// ****************************************************************
						// * retrieve standard and measure
						// ****************************************************************

						asset = assets.get(rs.getInt("id_asset"));

						if (asset == null)
							continue;

						assetValue = new MeasureAssetValue(asset, rs.getInt("value_spec"));

						// add the asset type value to the measure
						assetMeasure.addAnMeasureAssetValue(assetValue);
					}
				} finally {
					// close result
					if (rs != null)
						rs.close();
				}

			}
		}
	}

	private void importDependencyGraph() throws SQLException, NoSuchAlgorithmException {
		// Import dependency graph
		ResultSet rs = null;
		try {
			rs = sqlite.query("SELECT * FROM asset_node");
			if (rs == null)
				return;
			final SecureRandom random = SecureRandom.getInstanceStrong();
			final Map<Integer, AssetNode> nodes = new HashMap<>();
			while (rs.next()) {
				final int assetId = rs.getInt("id_asset");
				final AssetNode node = new AssetNode(assets.get(assetId));
				node.setInheritedConfidentiality(rs.getInt("confidentiality"));
				node.setInheritedIntegrity(rs.getInt("integrity"));
				node.setInheritedAvailability(rs.getInt("availability"));

				final Double x = getDouble(rs, "position_x", null);
				if (x == null) {
					node.setPosition(Position.generate(assets.size(), random));
				} else {
					node.setPosition(new Position(x, getDouble(rs, "position_y")));
				}
				this.analysis.getAssetNodes().add(node);
				nodes.put(assetId, node);
			}

			rs.close();

			rs = sqlite.query("SELECT * FROM asset_edge");
			if (rs != null) {
				while (rs.next()) {
					final AssetEdge edge = new AssetEdge(nodes.get(rs.getInt("id_parent")),
							nodes.get(rs.getInt("id_child")),
							rs.getDouble("weight"));
					edge.getParent().getEdges().put(edge.getChild(), edge);
				}
				rs.close();
			}

			rs = sqlite.query("SELECT * FROM asset_node_impact");
			if (rs != null) {
				final Set<ScaleType> usedScales = new HashSet<>(impactTypes.size());
				while (rs.next()) {
					final AssetNode node = nodes.get(rs.getInt("id_asset"));
					final ScaleType scaleType = impactTypes.get(rs.getString("impact_type"));
					final ILRImpact impact = new ILRImpact(scaleType, rs.getInt("impact_value"));
					switch (rs.getString("category")) {
						case Constant.CONFIDENTIALITY:
							node.getImpact().getConfidentialityImpacts().put(impact.getType(), impact);
							break;
						case Constant.INTEGRITY:
							node.getImpact().getIntegrityImpacts().put(impact.getType(), impact);
							break;
						case Constant.AVAILABILITY:
							node.getImpact().getAvailabilityImpacts().put(impact.getType(), impact);
							break;
						default:
							// TODO: not implemented
					}
					usedScales.add(scaleType);
				}
				analysis.setIlrImpactTypes(new ArrayList<>(usedScales));
			}

		} finally {
			if (rs != null)
				rs.close();
		}
	}

	private void importDynamicParameters() throws Exception {
		// Import dynamic parameters
		ResultSet rs = null;
		try {
			rs = sqlite.query("SELECT * FROM dynamic_parameter");
			if (rs == null)
				return;
			while (rs.next()) {
				final DynamicParameter dynamicParameter = new DynamicParameter();
				dynamicParameter.setDescription(rs.getString(Constant.NAME_PARAMETER));
				dynamicParameter.setAcronym(rs.getString(Constant.ACRO_PARAMETER));
				dynamicParameter.setValue(rs.getDouble(Constant.VALUE_PARAMETER));
				this.analysis.add(dynamicParameter);
			}
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	/**
	 * importExtendedParameters: <br>
	 * <ul>
	 * <li>Imports all Extended Parameters (Likelihood, Impact)</li>
	 * <li>Creates Objects for each Extended SimpleParameter</li>
	 * <li>Adds the Objects to the "parameters" field List</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importImpactParameters() throws Exception {

		System.out.println("Import Extended Parameters");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;
		String query = "";

		// ****************************************************************
		// * Import Impact
		// ****************************************************************

		// ****************************************************************
		// * retrieve parametertype
		// ****************************************************************

		List<ImpactParameter> impactParameters;

		try {
			currentSqliteTable = "impact";
			// build query
			query = "SELECT * FROM impact";
			// execute query
			rs = sqlite.query(query);
			impactParameters = new ArrayList<>(11);
			// retrieve results
			while (rs.next()) {

				// ****************************************************************
				// * Insert data into extended parameter table
				// ****************************************************************

				// ****************************************************************
				// * create instance of extended parameter
				// ****************************************************************
				ImpactParameter impactParameter = new ImpactParameter();
				if (!isCompability1X()) {
					impactParameter.setType(impactTypes.get(getString(rs, "type").toUpperCase()));
					if (analysis.isQualitative())
						impactParameter.setLabel(getString(rs, Constant.LABEL_IMPACT, ""));
				}
				impactParameter.setDescription(rs.getString(Constant.NAME_IMPACT));
				impactParameter.setLevel(Integer.valueOf(rs.getString(Constant.SCALE_IMPACT)));
				impactParameter.setAcronym(rs.getString(Constant.ACRO_IMPACT));
				impactParameter.setValue(rs.getDouble(Constant.VALUE_IMPACT));
				Bounds parameterbounds = new Bounds(rs.getDouble(Constant.VALUE_FROM_IMPACT),
						rs.getDouble(Constant.VALUE_TO_IMPACT));
				impactParameter.setBounds(parameterbounds);

				// ****************************************************************
				// * add instance to list of parameters
				// ****************************************************************
				impactParameters.add(impactParameter);
			}

			if (isCompability1X()) {
				ParameterManager.ComputeImpactValue(impactParameters);
				this.impactParameters = new LinkedHashMap<>();
				impactTypes.values().forEach(scaleType -> {
					impactParameters.forEach(parameter -> {
						ImpactParameter impactParameter = parameter.clone();
						impactParameter.setType(scaleType);
						this.impactParameters.put(Parameter.key(scaleType.getName(), impactParameter.getAcronym()),
								impactParameter);
						impactParameter.setAcronym(scaleType.getAcronym() + impactParameter.getLevel());
						this.analysis.add(impactParameter);
					});
				});
				impactParameters.clear();
			} else {
				this.analysis.getParameters().put(Constant.PARAMETER_CATEGORY_IMPACT, impactParameters);
				this.impactParameters = impactParameters.stream()
						.collect(Collectors.toMap(ImpactParameter::getAcronym, Function.identity()));
			}

		} finally {
			// close result
			if (rs != null)
				rs.close();
		}

	}

	private void importImpactParameterTypes() throws SQLException {
		ResultSet resultSet = null;
		try {
			impactTypes = new LinkedHashMap<>();
			if (isCompability1X()) {
				if (analysis.isQuantitative())
					addImpactType(Constant.DEFAULT_IMPACT_NAME, Constant.DEFAULT_IMPACT_TRANSLATE,
							Constant.DEFAULT_IMPACT_SHORT_NAME, "");
				if (analysis.isQualitative()) {
					for (int i = 0; i < Constant.DEFAULT_IMPACT_TYPE_NAMES.length; i++)
						addImpactType(Constant.DEFAULT_IMPACT_TYPE_NAMES[i], Constant.DEFAULT_IMPACT_TYPE_TRANSLATES[i],
								Constant.DEFAULT_IMPACT_TYPE_SHORT_NAMES[i], "i");
				}
			} else {
				resultSet = sqlite.query("Select * From impact_type");
				while (resultSet.next()) {
					String name = resultSet.getString("name");
					String acronym = resultSet.getString("acronym");
					ScaleType type = daoScaleType.findOne(name);
					if (type == null) {
						type = new ScaleType(name.toUpperCase(), generateAcronym(name, acronym.toLowerCase()));
						type.put(this.analysis.getLanguage().getAlpha2(),
								new Translation(resultSet.getString("translation"), resultSet.getString("shortName")));
						daoScaleType.saveOrUpdate(type);
					}
					impactTypes.put(type.getName(), type);
				}
			}
		} finally {
			if (resultSet != null)
				resultSet.close();
		}
	}

	private void importSettings() throws SQLException {
		ResultSet result = null;
		try {
			result = sqlite.query("Select * From settings");
			if (result == null)
				return;
			while (result.next()) {
				String name = result.getString("name"), value = result.getString("value");
				if (StringUtils.hasText(name) && StringUtils.hasText(value))
					analysis.getSettings().put(name, value);
			}
		} finally {
			if (result != null)
				result.close();
		}
	}

	/**
	 * importItemInformation: <br>
	 * <ul>
	 * <li>Imports all Item Information from Scope and Organisation</li>
	 * <li>Create Objects for each Item Information</li>
	 * <li>Adds the Objects to the "itemInformation" field</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importItemInformation() throws Exception {

		System.out.println("Import Item Information");

		// *********************************************
		// * initialise variables
		// *********************************************
		ResultSet rs = null;
		String query = "";
		ResultSetMetaData rsMetaData = null;
		String[] extendedScopes = new String[] { "financialParameters", "riskEvaluationCriteria", "impactCriteria",
				"riskAcceptanceCriteria" };
		int numColumns = 0;
		setCurrentSqliteTable("scope");

		// ****************************************************************
		// * Import data from scope
		// ****************************************************************

		// ****************************************************************
		// * Import data from organisation
		// ****************************************************************

		try {
			// build query
			query = "SELECT * FROM scope LIMIT 1";
			// execute query
			rs = sqlite.query(query, null);
			// retrieve result
			if (rs.next()) {

				// Get meta data for column names
				rsMetaData = rs.getMetaData();
				// get column count
				numColumns = rsMetaData.getColumnCount();
				// ****************************************************************
				// * parse columns and add data to item information table
				// ****************************************************************
				// parse columns
				for (int i = 1; i < numColumns + 1; i++) {

					// check all column names if they are not parameters
					if (!Constant.SCOPE_EXCLUDE.contains(rsMetaData.getColumnName(i))) {

						// ****************************************************************
						// * Insert scope data into item information table.
						// * As "dtLabel" of item information use the column
						// name
						// ****************************************************************

						// ****************************************************************
						// * create instance
						// ****************************************************************

						// ****************************************************************
						// * add instance to list of item information
						// ****************************************************************
						this.analysis.add(new ItemInformation(rsMetaData.getColumnName(i),
								Constant.ITEMINFORMATION_SCOPE, rs.getString(rsMetaData.getColumnName(i))));
					}
				}
			}
			// close result
			rs.close();
			// Add missing scope
			for (String scopeName : extendedScopes) {
				if (this.analysis.getItemInformations().stream()
						.noneMatch(itemInformation -> itemInformation.getDescription().equals(scopeName)))
					this.analysis.add(new ItemInformation(scopeName, Constant.ITEMINFORMATION_SCOPE, ""));
			}
			setCurrentSqliteTable("organisation");
			// build and execute query
			rs = sqlite.query("SELECT * FROM organisation", null);
			// retrieve result
			if (rs.next()) {

				// Get meta data for column names
				rsMetaData = rs.getMetaData();

				// get column count
				numColumns = rsMetaData.getColumnCount();

				// ****************************************************************
				// * parse all columns and add organisation data
				// ****************************************************************

				// parse columns
				for (int i = 1; i < numColumns + 1; i++) {

					// ****************************************************************
					// * Insert organisation data into iteminformation table.
					// * As "dtLabel" of item information use the column name
					// ****************************************************************

					// build query
					// query =
					// DatabaseHandler.generateInsertQuery("ItemInformation",
					// 7);

					// ****************************************************************
					// * create instance
					// ****************************************************************
					// ****************************************************************
					// * add instance to list of item information
					// ****************************************************************
					this.analysis.add(new ItemInformation(rsMetaData.getColumnName(i),
							Constant.ITEMINFORMATION_ORGANISATION, rs.getString(rsMetaData.getColumnName(i))));
				}
			}
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	/**
	 * importMaturityMeasures: <br>
	 * <ul>
	 * <li>Imports all Maturity Measures</li>
	 * <li>Create Objects for each Measure</li>
	 * <li>Creates Maturity AnalysisStandard Object</li>
	 * <li>Adds measures to the Maturity AnalysisStandard</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importMaturityMeasures() throws Exception {

		System.out.println("Import maturity measures");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;
		int numPhase = 0;
		String chapter = "";
		double cost = 0;
		AnalysisStandard analysisStandard = null;
		Phase tempPhase = null;
		Standard standard = null;
		MeasureDescription mesDesc = null;
		MeasureDescriptionText mesText = null;
		IParameter implementationRateParameter = null;
		/// double implementationRate = 0;
		MaturityMeasure maturityMeasure = null;
		Integer standardVersion = 2005;
		boolean standardComputable = true;
		boolean measurecomputable = false;
		String description = "Old Maturity measure to be used with the 2005 verison of 27001 ISO standard.";

		// ****************************************************************
		// * load each maturity
		// ****************************************************************

		try {
			// build and execute query
			rs = sqlite.query("SELECT * from maturities", null);
			// retrieve results
			while (rs.next()) {

				currentSqliteTable = "maturities";
				// retrieve standard from map
				tempPhase = null;
				if (columnExists(rs, Constant.MEASURE_VERSION_NORM)) {
					standardVersion = rs.getInt(Constant.MEASURE_VERSION_NORM);
					standardComputable = rs.getBoolean(Constant.MEASURE_STANDARD_COMPUTABLE);
					measurecomputable = rs.getBoolean(Constant.MEASURE_MEASURE_COMPUTABLE);
					description = rs.getString(Constant.MEASURE_STANDARD_DESCRIPTION);
				} else {
					standardVersion = 2005;
					standardComputable = true;
					description = "old standard (before 2013)";
					if (rs.getInt(Constant.MEASURE_LEVEL) == Constant.MEASURE_LEVEL_3) {
						measurecomputable = true;
					} else {
						measurecomputable = false;
					}
				}

				standard = standards.get(String.format("%s_%d", Constant.STANDARD_MATURITY, standardVersion));
				if (standard == null) {
					standard = daoStandard.getStandardByLabelAndVersion(Constant.STANDARD_MATURITY, standardVersion);
					if (standard == null) {
						// standard is not in database create new standard and
						// save
						// in into
						// database for future
						standard = new Standard(getString(rs, Constant.MEASURE_NAME_NORM, Constant.STANDARD_MATURITY),
								Constant.STANDARD_MATURITY, StandardType.MATURITY,
								standardVersion, description, standardComputable);
						daoStandard.save(standard);
						// add standard to map
					}
					standards.put(String.format("%s_%d", Constant.STANDARD_MATURITY, standardVersion), standard);
				}

				// get analysisstandard from map
				analysisStandard = analysisStandards.get(standard);
				// analysis does not yet exist
				if (analysisStandard == null)

					// add standard to analysistandard map as analaysisstandard
					// object
					analysisStandards.put(standard, analysisStandard = new MaturityStandard(standard));

				// ****************************************************************
				// * retrieve measure description and implementation rate ID to
				// * insert data into database
				// ****************************************************************

				// ****************************************************************
				// * check phases of this measure and add it if it does not
				// exist
				// ****************************************************************

				// store chapter
				chapter = rs.getString(Constant.MATURITY_REF);

				// retrieve measuredescription from database
				mesDesc = daoMeasureDescription.getByReferenceAndStandard(chapter, analysisStandard.getStandard());

				// measure description does not exist
				if (mesDesc == null) {

					// System.out.println(chapter);

					// create measuredescription
					mesDesc = new MeasureDescription();

					// create text of measuredescription
					mesText = new MeasureDescriptionText();

					// create link from measure description to measure
					// description
					// text
					mesDesc.addMeasureDescriptionText(mesText);

					// fill measure description data
					mesDesc.setStandard(analysisStandard.getStandard());
					mesDesc.setReference(chapter);
					// mesDesc.setLevel(rs.getInt(Constant.MEASURE_LEVEL));

					// fill measure description text
					mesText.setDomain(rs.getString(Constant.MATURITY_DOMAIN).replace("'", "''"));
					mesText.setDescription(Constant.EMPTY_STRING);
					mesText.setLanguage(this.analysis.getLanguage());

					// else: measure description exist: measure description text
					// exists in the language
					// of the analysis -> NO
				} else if (!daoMeasureDescriptionText.existsForMeasureDescriptionAndLanguage(mesDesc.getId(),
						this.analysis.getLanguage().getId())) {

					// create new measure description text
					mesText = new MeasureDescriptionText();

					// create link from measure description to measure
					// description
					// text
					mesDesc.addMeasureDescriptionText(mesText);

					// create link from measure description text to measure
					// description
					mesText.setMeasureDescription(mesDesc);

					// set data to measure description text
					mesText.setDomain(rs.getString(Constant.MATURITY_DOMAIN).replace("'", "''"));
					mesText.setDescription(Constant.EMPTY_STRING);
					mesText.setLanguage(this.analysis.getLanguage());
				}

				// check if measure if level 1 (a chapter) -> YES -> add a
				// attributed phase

				if (getInt(rs, Constant.MEASURE_LEVEL, 0) == Constant.MEASURE_LEVEL_1) {

					// set phase number
					numPhase = rs.getInt("phase");

					// retrieve phase from number of the map
					tempPhase = phases.get(numPhase);
				}

				// phase does not exist
				if (tempPhase == null) {

					// use default phase

					// set phase 1
					numPhase = Constant.PHASE_DEFAULT;

					// ****************************************************************
					// * create phase instance
					// ****************************************************************

					tempPhase = phases.get(numPhase);

					if (tempPhase == null) {

						tempPhase = new Phase();
						tempPhase.setNumber(numPhase);
						tempPhase.setBeginDate(null);
						tempPhase.setBeginDate(null);
						tempPhase.setAnalysis(analysis);
						phases.put(numPhase, tempPhase);

						analysis.add(tempPhase);
					}

				}

				// ****************************************************************
				// * Insert measure into maturitymeasure table
				// ****************************************************************

				// add parameters
				// String status = rs.getString(Constant.MEASURE_STATUS);

				// set status and by default not applicable (NA)
				/*
				 * if ((!status.equals(Constant.MEASURE_STATUS_APPLICABLE))
				 * && (!status.equals(Constant.MEASURE_STATUS_MANDATORY))
				 * && (!status.equals(Constant.MEASURE_STATUS_NOT_APPLICABLE))) {
				 * 
				 * // set default status
				 * status = Constant.MEASURE_STATUS_NOT_APPLICABLE;
				 * }
				 * // ****************************************************************
				 * // * calculate cost
				 * // ****************************************************************
				 * 
				 * // check if status is not NA -> YES
				 * if ((rs.getString(Constant.MEASURE_STATUS).replace("'", "''")
				 * .equals(Constant.MEASURE_STATUS_APPLICABLE))
				 * || (rs.getString(Constant.MEASURE_STATUS).replace("'", "''")
				 * .equals(Constant.MEASURE_STATUS_MANDATORY))) {
				 * 
				 * // calculate cost
				 * cost = Analysis.computeCost(this.analysis.findParameter(Constant.
				 * PARAMETER_INTERNAL_SETUP_RATE),
				 * this.analysis.findParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE),
				 * this.analysis.findParameter(Constant.PARAMETER_LIFETIME_DEFAULT),
				 * rs.getInt("internal_maintenance"), rs.getInt("external_maintenance"),
				 * rs.getInt("recurrent_investment"), rs.getInt(Constant.MATURITY_INTWL),
				 * rs.getInt(Constant.MATURITY_EXTWL), rs.getInt(Constant.MATURITY_INVESTMENT),
				 * rs.getInt(Constant.MEASURE_LIFETIME));
				 * } else {
				 * 
				 * // check if status is not NA -> NO
				 * 
				 * // set cost to 0
				 * cost = 0;
				 * }
				 */

				// ****************************************************************
				// * create instance
				// ****************************************************************

				// ****************************************************************
				// * create parameter for implementation rate
				// ****************************************************************
				double implementationRate = rs.getDouble(Constant.MATURITY_RATE) * 100;

				// System.out.println(implementationRate);

				// parse implmentation rate parameters
				for (SimpleParameter parameter : analysis.getSimpleParameters()) {
					// find implementation rate parameter and wanted value
					if (parameter.getValue() == implementationRate
							&& parameter.getTypeName().equals(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME)) {
						// retrieve object
						implementationRateParameter = parameter;
						break;
					}
				}

				// create maturity measure with data
				maturityMeasure = new MaturityMeasure();
				maturityMeasure.setMeasureDescription(mesDesc);
				maturityMeasure.getMeasureDescription().setComputable(measurecomputable);
				maturityMeasure.setComment(rs.getString(Constant.MEASURE_COMMENT).replace("'", "''"));
				maturityMeasure.setCost(cost);
				maturityMeasure.setInternalWL(rs.getInt(Constant.MATURITY_INTWL));
				maturityMeasure.setExternalWL(rs.getInt(Constant.MATURITY_EXTWL));
				maturityMeasure.setImplementationRate(implementationRateParameter);
				maturityMeasure.setInvestment(rs.getDouble(Constant.MATURITY_INVESTMENT));
				maturityMeasure.setLifetime(rs.getInt(Constant.MEASURE_LIFETIME));
				maturityMeasure.setInternalMaintenance(rs.getDouble("internal_maintenance"));
				maturityMeasure.setExternalMaintenance(rs.getDouble("external_maintenance"));
				maturityMeasure.setRecurrentInvestment(rs.getDouble("recurrent_investment"));
				maturityMeasure.setImportance(getInt(rs, "importance", 2));
				maturityMeasure.setStatus(rs.getString(Constant.MEASURE_STATUS).replace("'", "''"));
				maturityMeasure.setToDo(rs.getString(Constant.MEASURE_TODO).replace("'", "''"));
				maturityMeasure.setResponsible(getStringOrEmpty(rs, Constant.MEASURE_RESPONSIBLE));
				maturityMeasure.setReachedLevel(rs.getInt(Constant.MATURITY_REACHED));
				maturityMeasure.setSML1Cost(rs.getInt(Constant.MATURITY_SML1));
				maturityMeasure.setSML2Cost(rs.getInt(Constant.MATURITY_SML2));
				maturityMeasure.setSML3Cost(rs.getInt(Constant.MATURITY_SML3));
				maturityMeasure.setSML4Cost(rs.getInt(Constant.MATURITY_SML4));
				maturityMeasure.setSML5Cost(rs.getInt(Constant.MATURITY_SML5));

				// ****************************************************************
				// * add phase to measure instance
				// ****************************************************************
				maturityMeasure.setPhase(tempPhase);

				// ****************************************************************
				// * add measure to standard
				// ****************************************************************
				((MaturityStandard) analysisStandard).addMeasure(maturityMeasure);
				// add measure to measures map
				measures.put(analysisStandard.getStandard().getLabel() + "_"
						+ analysisStandard.getStandard().getVersion() + "_" + chapter, maturityMeasure);
			}
		} finally {
			// close result
			if (rs != null)
				rs.close();
		}

	}

	/**
	 * importMaturityParameters: <br>
	 * <ul>
	 * <li>Imports all Maturity Parameters</li>
	 * <li>Creates Objects for each Maturity SimpleParameter</li>
	 * <li>Adds the Objects to the "parameters" field List</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importMaturityParameters() throws Exception {

		System.out.println("Import Maturity Parameters");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;
		String query = "";
		String label = "";
		String cat = "";
		String temp = "";
		MaturityParameter maturityParameter = null;

		// ****************************************************************
		// * import maturity parameters
		// ****************************************************************

		try {
			currentSqliteTable = "maturity_required_LIPS";
			// build query
			query = "SELECT * FROM maturity_required_LIPS";
			// execute query
			rs = sqlite.query(query, null);
			List<MaturityParameter> parameters = new ArrayList<>();
			// retrieve the name, and with the name find out the category
			while (rs.next()) {

				maturityParameter = null;

				// ****************************************************************
				// * set the label and category of the maturity parameter
				// ****************************************************************
				label = rs.getString(Constant.PARAMETER_MATURITY_TASK_NAME);
				cat = "";
				temp = label.substring(0, 3);

				// find category
				if (temp.equals(Constant.PARAMETER_MATURITY_TASK_POLICY)) {
					cat = Constant.PARAMETER_MATURITY_CATEGORY_POLICY;
				} else {
					if (temp.equals(Constant.PARAMETER_MATURITY_TASK_PROCEDURE)) {
						cat = Constant.PARAMETER_MATURITY_CATEGORY_PROCEDURE;
					} else {
						if (temp.equals(Constant.PARAMETER_MATURITY_TASK_IMPLEMENTATION)) {
							cat = Constant.PARAMETER_MATURITY_CATEGORY_IMPLEMENTATION;
						} else {
							if (temp.equals(Constant.PARAMETER_MATURITY_TASK_TEST)) {
								cat = Constant.PARAMETER_MATURITY_CATEGORY_TEST;
							} else {
								if (temp.equals(Constant.PARAMETER_MATURITY_TASK_INTEGRATION)) {
									cat = Constant.PARAMETER_MATURITY_CATEGORY_INTEGRATION;
								}
							}
						}
					}
				}

				for (MaturityParameter parameter : parameters)
					if (parameter.getCategory().equals(cat) && parameter.getDescription().equals(label)) {
						maturityParameter = parameter;
						break;
					}

				// ****************************************************************
				// * create instance
				// ****************************************************************

				if (maturityParameter == null) {
					maturityParameter = new MaturityParameter();
					maturityParameter.setCategory(cat);
					maturityParameter.setDescription(label);
					parameters.add(maturityParameter);
				}

				switch (rs.getInt(Constant.MATURITY_REQUIRED_LIPS_SML)) {
					case 0: {
						maturityParameter.setSMLLevel0(rs.getDouble(Constant.MATURITY_REQUIRED_LIPS_VALUE));
						maturityParameter.setValue(-1);
						break;
					}
					case 1: {
						maturityParameter.setSMLLevel1(rs.getDouble(Constant.MATURITY_REQUIRED_LIPS_VALUE));
						maturityParameter.setValue(-1);
						break;
					}
					case 2: {
						maturityParameter.setSMLLevel2(rs.getDouble(Constant.MATURITY_REQUIRED_LIPS_VALUE));
						maturityParameter.setValue(-1);
						break;
					}
					case 3: {
						maturityParameter.setSMLLevel3(rs.getDouble(Constant.MATURITY_REQUIRED_LIPS_VALUE));
						maturityParameter.setValue(-1);
						break;
					}
					case 4: {
						maturityParameter.setSMLLevel4(rs.getDouble(Constant.MATURITY_REQUIRED_LIPS_VALUE));
						maturityParameter.setValue(-1);
						break;
					}
					case 5: {
						maturityParameter.setSMLLevel5(rs.getDouble(Constant.MATURITY_REQUIRED_LIPS_VALUE));
						maturityParameter.setValue(-1);
						break;
					}
				}

			}
			for (IMaturityParameter parameter : parameters)
				this.analysis.add(parameter);
		} finally {
			// close result
			if (rs != null)
				rs.close();
		}

	}

	/**
	 * importNormalMeasures: <br>
	 * <ul>
	 * <li>Imports all AnalysisStandard Measures (27001,27002,custom) except
	 * maturity</li>
	 * <li>Create Objects for each AnalysisStandard</li>
	 * <li>Create Objects for each Measure</li>
	 * <li>Create Objects for the Measure Phase</li>
	 * <li>Adds the Phase to the Measure Object</li>
	 * <li>Adds the Measure Objects to the their cosresponding AnalysisStandard (int
	 * the "standards" field)</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importNormalMeasures() throws Exception {

		System.out.println("Import Measures");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;
		double cost = 0;
		Phase phase = null;
		String query = "";
		analysisStandards = new HashMap<>();
		standards = new HashMap<>();
		measures = new HashMap<>();
		AnalysisStandard analysisStandard = null;
		Standard standard = null;
		String standardName = "";
		String description = "";
		int standardVersion = 2005;
		boolean standardComputable = false;
		boolean measurecomputable = false;
		int phaseNumber = 0;
		String measureRefMeasure = "";
		MeasureDescription mesDesc = null;
		MeasureDescriptionText mesText = null;
		boolean hasNewType = NaturalOrderComparator.compareTo(version, "2.3") > 0;

		// ****************************************************************
		// * retrieve all measures
		// ****************************************************************

		try {
			currentSqliteTable = "measures";
			// build query
			query = "SELECT rowid, * FROM measures";
			// execute query
			rs = sqlite.query(query, null);
			// retrieve results
			while (rs.next()) {

				// measureID = rs.getInt("rowid");

				// ****************************************************************
				// * parse standards to find standard of measure
				// ****************************************************************

				// initialise standard variable

				standardName = rs.getString(Constant.MEASURE_ID_NORM);

				if (columnExists(rs, Constant.MEASURE_VERSION_NORM)) {
					standardVersion = rs.getInt(Constant.MEASURE_VERSION_NORM);
					standardComputable = rs.getBoolean(Constant.MEASURE_STANDARD_COMPUTABLE);
					measurecomputable = rs.getBoolean(Constant.MEASURE_MEASURE_COMPUTABLE);
					description = rs.getString(Constant.MEASURE_STANDARD_DESCRIPTION);
				} else {
					standardVersion = 2005;
					standardComputable = true;
					description = "old standard (before 2013)";
					measurecomputable = rs.getInt(Constant.MEASURE_LEVEL) == Constant.MEASURE_LEVEL_3;
				}

				standard = standards.get(standardName + "_" + standardVersion);
				if (standard == null) {
					standard = daoStandard.getStandardByLabelAndVersion(standardName, standardVersion);
					// standard is not in database create new standard and save
					// in
					// into
					// database for future
					if (standard == null) {
						standard = new Standard(getString(rs, Constant.MEASURE_NAME_NORM, standardName), standardName,
								StandardType.getByName(rs.getString("norme_type")),
								standardVersion, description, standardComputable);
						standard.setAnalysisOnly(rs.getBoolean("norme_analysisOnly"));
						daoStandard.save(standard);
						// add standard to map
					} else if (standard.isAnalysisOnly()) {
						standard = standard.duplicate();
						standard.setVersion(daoStandard.getNextVersionByLabelAndType(standardName, standard.getType()));
						daoStandard.save(standard);
						// add standard to map
					}
					standards.put(standardName + "_" + standardVersion, standard);
				}

				// retrieve analysisstandard of the standard
				analysisStandard = analysisStandards.computeIfAbsent(standard,
						e -> e.getType().equals(StandardType.NORMAL) ? new NormalStandard(e) : new AssetStandard(e));

				// ****************************************************************
				// * Import measure to database
				// ****************************************************************

				// ****************************************************************
				// * Retrive phase
				// ****************************************************************
				phaseNumber = rs.getInt(Constant.MEASURE_PHASE);

				if (phaseNumber == Constant.PHASE_NOT_USABLE)
					phaseNumber = Constant.PHASE_DEFAULT;

				// retrieve phase from phases map
				if (phaseNumber == 0)
					phaseNumber = 1;
				phase = phases.get(phaseNumber);

				// ****************************************************************
				// * retrieve measuredescription
				// ****************************************************************

				// get measure reference
				measureRefMeasure = rs.getString(Constant.MEASURE_REF_MEASURE);

				// get measure description from database
				mesDesc = daoMeasureDescription.getByReferenceAndStandard(measureRefMeasure, standard);

				// measure description was found -> NO
				if (mesDesc == null) {

					// create measuredescription
					mesDesc = new MeasureDescription();

					// create text of measuredescription
					mesText = new MeasureDescriptionText();

					// create link from measure description to measure
					// description
					// text
					mesDesc.addMeasureDescriptionText(mesText);

					// fill measure description with data
					mesDesc.setStandard(analysisStandard.getStandard());
					mesDesc.setReference(measureRefMeasure);
					// mesDesc.setLevel(rs.getInt(Constant.MEASURE_LEVEL));

					// fill measure description text with data
					mesText.setDomain(rs.getString(Constant.MEASURE_DOMAIN_MEASURE));
					mesText.setDescription(rs.getString(Constant.MEASURE_QUESTION_MEASURE));

					mesText.setLanguage(this.analysis.getLanguage());

					// save measure description to database
					daoMeasureDescription.save(mesDesc);

					// else: check if measure description text exists in the
					// language of the analysis ->
					// NO
				} else if (!daoMeasureDescriptionText.existsForMeasureDescriptionAndLanguage(mesDesc.getId(),
						this.analysis.getLanguage().getId())) {

					// System.out.println("Not found");

					// create new measure description text for this measure
					// description
					mesText = new MeasureDescriptionText();

					// create link from measure description to measure
					// description
					// text
					mesDesc.addMeasureDescriptionText(mesText);

					// create link from measure description text to measure
					// description
					mesText.setMeasureDescription(mesDesc);

					// fill measure description text
					mesText.setDomain(rs.getString(Constant.MEASURE_DOMAIN_MEASURE));
					mesText.setDescription(rs.getString(Constant.MEASURE_QUESTION_MEASURE));
					mesText.setLanguage(this.analysis.getLanguage());

					// save measure description text to database
					daoMeasureDescription.saveOrUpdate(mesDesc);
				}

				// ****************************************************************
				// * create object
				// ****************************************************************

				// retrieve id for the instance creation (NormalMeasure ID)
				// insertID = mysql.getLastInsertId();
				AbstractNormalMeasure measure = null;

				if (standard.getType().equals(StandardType.NORMAL))
					measure = new NormalMeasure();
				else if (standard.getType().equals(StandardType.ASSET))
					measure = new AssetMeasure();
				else
					throw new IllegalArgumentException("Unknown measure type");

				measure.setMeasureDescription(mesDesc);
				if (rs.getString(Constant.MEASURE_REVISION) == null)
					measure.setComment("");
				else
					measure.setComment(rs.getString(Constant.MEASURE_COMMENT));

				measure.setInternalWL(rs.getInt(Constant.MEASURE_INTERNAL_SETUP));
				measure.setExternalWL(rs.getInt(Constant.MEASURE_EXTERNAL_SETUP));
				measure.setImplementationRate(Double.toString(rs.getDouble(Constant.MEASURE_IMPLEMENTATION_RATE)));
				measure.setInvestment(rs.getDouble(Constant.MEASURE_INVESTISMENT));
				measure.setLifetime(rs.getInt(Constant.MEASURE_LIFETIME));
				measure.setInternalMaintenance(rs.getDouble("internal_maintenance"));
				measure.setExternalMaintenance(rs.getDouble("external_maintenance"));
				measure.setRecurrentInvestment(rs.getDouble("recurrent_investment"));
				measure.setImportance(getInt(rs, "importance", 2));
				measure.setStatus(rs.getString(Constant.MEASURE_STATUS));
				if (measure instanceof AbstractNormalMeasure)
					measure.setToCheck(getStringOrEmpty(rs, Constant.MEASURE_REVISION));

				measure.setToDo(getStringOrEmpty(rs, Constant.MEASURE_TODO));

				measure.setResponsible(getStringOrEmpty(rs, Constant.MEASURE_RESPONSIBLE));

				measure.getMeasureDescription().setComputable(measurecomputable);

				// calculate cost
				/*
				 * cost = Analysis.computeCost(this.analysis.findParameter(Constant.
				 * PARAMETER_INTERNAL_SETUP_RATE),
				 * this.analysis.findParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE),
				 * this.analysis.findParameter(Constant.PARAMETER_LIFETIME_DEFAULT),
				 * measure.getInternalMaintenance(), measure.getExternalMaintenance(),
				 * measure.getRecurrentInvestment(), measure.getInternalWL(),
				 * measure.getExternalWL(),
				 * measure.getInvestment(), measure.getLifetime());
				 */

				measure.setCost(cost);

				// ****************************************************************
				// * add phase instance to the measure instance
				// ****************************************************************
				measure.setPhase(phase);

				// ****************************************************************
				// * create measureproperties instance for this measure
				// ****************************************************************
				MeasureProperties measureProperties = new MeasureProperties();
				measureProperties.setFMeasure(rs.getInt(Constant.MEASURE_STRENGTH_MEASURE));
				measureProperties.setFSectoral(rs.getInt(Constant.MEASURE_STRENGTH_SECTORAL));
				measureProperties.setCategoryValue(Constant.CONFIDENTIALITY_RISK,
						rs.getInt(Constant.MEASURE_CONFIDENTIALITY));
				measureProperties.setCategoryValue(Constant.INTEGRITY_RISK, rs.getInt(Constant.MEASURE_INTEGRITY));
				measureProperties.setCategoryValue(Constant.AVAILABILITY_RISK,
						rs.getInt(Constant.MEASURE_AVAILABILITY));
				if (hasNewType) {
					measureProperties.setCategoryValue(Constant.EXPLOITABILITY_RISK,
							rs.getInt(Constant.MEASURE_EXPLOITABILITY));
					measureProperties.setCategoryValue(Constant.RELIABILITY_RISK,
							rs.getInt(Constant.MEASURE_RELIABILITY));
					measureProperties.setCategoryValue(Constant.ILR_RISK,
							getInt(rs, Constant.MEASURE_ILR));
				}
				// load CSSF Risk data from sqlLight
				setAllCriteriaCSSFCategories(measureProperties, rs);
				measureProperties.setPreventive(rs.getInt(Constant.MEASURE_PREVENTIVE));
				measureProperties.setDetective(rs.getInt(Constant.MEASURE_DETECTIVE));
				measureProperties.setLimitative(rs.getInt(Constant.MEASURE_LIMITING));
				measureProperties.setCorrective(rs.getInt(Constant.MEASURE_CORRECTIVE));
				measureProperties.setIntentional(rs.getInt(Constant.MEASURE_INTENTIONAL));
				measureProperties.setAccidental(rs.getInt(Constant.MEASURE_ACCIDENTAL));
				measureProperties.setEnvironmental(rs.getInt(Constant.MEASURE_ENVIRONMENTAL));
				measureProperties.setInternalThreat(rs.getInt(Constant.MEASURE_INTERNAL_THREAT));
				measureProperties.setExternalThreat(rs.getInt(Constant.THREAT_EXTERNAL_THREAT));
				measureProperties.setSoaComment(rs.getString(Constant.MEASURE_SOA_COMMENT));
				measureProperties.setSoaReference(rs.getString(Constant.MEASURE_SOA_REFERENCE));
				measureProperties.setSoaRisk(rs.getString(Constant.MEASURE_SOA_RISK));

				// ****************************************************************
				// * add measureporperties instance to measure instance
				// ****************************************************************
				analysisStandard.add(measure);
				measure.setMeasurePropertyList(measureProperties);
				measures.put(measureKey(standardName, standardVersion, measureRefMeasure), measure);
			}

		} finally {
			// close result
			if (rs != null)
				rs.close();
		}
	}

	/**
	 * importPhases: <br>
	 * <ul>
	 * <li>Imports all Phases</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importPhases() throws Exception {

		System.out.println("Import phases");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String query = "";
		Phase phase = null;
		phases = new HashMap<Integer, Phase>();
		String phaseBeginDate = "";
		String phaseEndDate = "";

		// ****************************************************************
		// * select all existing phases from info_phases
		// ****************************************************************

		try {
			currentSqliteTable = "info_phases";
			// build query
			query = "SELECT * FROM info_phases";
			// execute query
			rs = sqlite.query(query, null);
			// retrieve results
			while (rs.next()) {

				// ****************************************************************
				// * Add Phases to list of used phases
				// ****************************************************************

				phase = new Phase();
				phase.setNumber(rs.getInt(Constant.PHASE_NUMBER));
				phaseBeginDate = rs.getString(Constant.PHASE_BEGIN_DATE);
				phaseEndDate = rs.getString(Constant.PHASE_END_DATE);
				phase.setAnalysis(analysis);
				// set begin date if not empty
				if (phaseBeginDate.equals(Constant.EMPTY_STRING)) {
					phase.setBeginDate(null);
				} else {
					phase.setBeginDate(new Date(dateFormat.parse(phaseBeginDate).getTime()));
				}

				// set end date if not empty
				if (phaseEndDate.equals(Constant.EMPTY_STRING)) {
					phase.setEndDate(null);
				} else {
					phase.setEndDate(new Date(dateFormat.parse(phaseEndDate).getTime()));
				}

				// add phase to map
				phases.put(phase.getNumber(), phase);
			}

			// populate usedPhases list
			for (Phase phase2 : phases.values())
				this.analysis.add(phase2);

			// order phases by phase number
			this.analysis.initialisePhases();
		} finally {
			if (rs != null)
				rs.close();
		}
		// close result

	}

	private void importProbabilities() throws SQLException {

		System.out.println("Import probability Parameters");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;
		String query = "";
		Bounds parameterbounds = null;

		// ****************************************************************
		// * retrieve likelihood values
		// ****************************************************************

		List<LikelihoodParameter> likelihoodParameters;

		try {
			currentSqliteTable = "potentiality";
			// build query
			query = "SELECT * FROM potentiality";
			// execute query
			rs = sqlite.query(query, null);
			likelihoodParameters = new ArrayList<>();
			// retrieve results
			while (rs.next()) {

				// ****************************************************************
				// * Insert data into extended parameter table
				// ****************************************************************

				// ****************************************************************
				// * create instance
				// ****************************************************************
				LikelihoodParameter likelihoodParameter = new LikelihoodParameter();
				if (!isCompability1X() && analysis.isQualitative())
					likelihoodParameter.setLabel(getString(rs, Constant.LABEL_POTENTIALITY, ""));
				likelihoodParameter.setDescription(rs.getString(Constant.NAME_POTENTIALITY));
				likelihoodParameter.setLevel(Integer.valueOf(rs.getString(Constant.SCALE_POTENTIALITY)));
				likelihoodParameter.setAcronym(rs.getString(Constant.ACRO_POTENTIALITY));
				likelihoodParameter.setValue(rs.getDouble(Constant.VALUE_POTENTIALITY));
				likelihoodParameter.setIlrLevel(getInt(rs, "ilr_level", -1));
				parameterbounds = new Bounds(rs.getDouble(Constant.VALUE_FROM_POTENTIALITY),
						rs.getDouble(Constant.VALUE_TO_POTENTIALITY));
				likelihoodParameter.setBounds(parameterbounds);

				// ****************************************************************
				// * add instance to list of parameters
				// ****************************************************************
				likelihoodParameters.add(likelihoodParameter);
			}

			ParameterManager.ComputeLikehoodValue(likelihoodParameters);

			this.analysis.getParameters().put(Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD, likelihoodParameters);

			this.probabilities = likelihoodParameters.stream()
					.collect(Collectors.toMap(LikelihoodParameter::getAcronym, Function.identity()));

		} finally {
			if (rs != null)
				rs.close();
		}

	}

	/**
	 * importRiskInformation: <br>
	 * <ul>
	 * <li>Imports all Risk Information: Threat Source, Risks, Vulnerabilities</li>
	 * <li>Creates Objects for each Risk Information</li>
	 * <li>Adds the Objects to the "riskInfo" field List</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importRiskInformation() throws Exception {

		System.out.println("Import Risk Information");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;
		String query = "";
		RiskInformation tempRI = null;
		boolean hasCustom = NaturalOrderComparator.compareTo(version, "2.3") > 0;

		// ****************************************************************
		// * Query sqlite for all vulnerabilities (vulnerabilities)
		// ****************************************************************

		// ****************************************************************
		// * Query sqlite for all risks (threat_Source)
		// ****************************************************************

		try {
			setCurrentSqliteTable("threat_typology");
			// ****************************************************************
			// * Query sqlite for all threats (threat_typology)
			// ****************************************************************
			// build query
			query = "SELECT * FROM threat_typology";
			// execute query
			rs = sqlite.query(query, null);
			// Loop threats
			while (rs.next()) {

				// ****************************************************************
				// * Insert data into risk information table
				// ****************************************************************

				// ****************************************************************
				// * create instance of this risk information
				// ****************************************************************
				tempRI = new RiskInformation();
				tempRI.setCategory(Constant.RI_TYPE_THREAT);
				tempRI.setChapter(rs.getString(Constant.RI_LEVEL));
				tempRI.setLabel(rs.getString(Constant.RI_NAME));
				tempRI.setAcronym(rs.getString(Constant.RI_ACRO));
				tempRI.setOwner(getStringOrEmpty(rs, Constant.RI_OWNER));
				if (hasCustom)
					tempRI.setCustom(rs.getBoolean(Constant.RI_CUSTOM));
				tempRI.setExposed(rs.getString(Constant.RI_EXPO));
				tempRI.setComment(rs.getString(Constant.RI_COMMENT));
				tempRI.setHiddenComment(rs.getString(Constant.RI_COMMENT2));

				// ****************************************************************
				// * add instance to list of risk information
				// ****************************************************************
				this.analysis.add(tempRI);
			}
			// Close ResultSet
			rs.close();
			setCurrentSqliteTable("vulnerabilities");
			// build query
			query = "SELECT * FROM vulnerabilities";
			// execute query
			rs = sqlite.query(query, null);
			// Loop vulnerabilities
			while (rs.next()) {

				// ****************************************************************
				// * Insert data into risk information table
				// ****************************************************************

				// ****************************************************************
				// * create instance of this risk information
				// ****************************************************************
				tempRI = new RiskInformation();
				tempRI.setCategory(Constant.RI_TYPE_VUL);
				tempRI.setChapter(rs.getString(Constant.RI_LEVEL));
				tempRI.setLabel(rs.getString(Constant.RI_NAME));
				tempRI.setAcronym(Constant.EMPTY_STRING);
				tempRI.setExposed(rs.getString(Constant.RI_EXPO));
				tempRI.setOwner(getStringOrEmpty(rs, Constant.RI_OWNER));
				if (hasCustom)
					tempRI.setCustom(rs.getBoolean(Constant.RI_CUSTOM));
				tempRI.setComment(rs.getString(Constant.RI_COMMENT));
				tempRI.setHiddenComment(rs.getString(Constant.RI_COMMENT2));

				// ****************************************************************
				// * add instance to list of risk information
				// ****************************************************************
				this.analysis.add(tempRI);
			}
			// Close ResultSet
			rs.close();
			setCurrentSqliteTable("threat_Source");
			// build query
			query = "SELECT * FROM threat_Source";
			// execute query
			rs = sqlite.query(query, null);
			// Loop risks
			while (rs.next()) {

				// ****************************************************************
				// * Insert data into risk information table
				// ****************************************************************

				// ****************************************************************
				// * create instance of this risk information
				// ****************************************************************
				tempRI = new RiskInformation();
				tempRI.setCategory(Constant.RI_TYPE_RISK + "_" + rs.getString(Constant.RI_TYPE));

				if (hasCustom) {
					tempRI.setChapter(rs.getString(Constant.RI_LEVEL));
					tempRI.setCustom(rs.getBoolean(Constant.RI_CUSTOM));
				} else if (tempRI.getCategory().equals(Constant.RI_TYPE_RISK_TBA))
					tempRI.setChapter("7" + rs.getString(Constant.RI_LEVEL).substring(1));
				else
					tempRI.setChapter(rs.getString(Constant.RI_LEVEL));

				tempRI.setLabel(rs.getString(Constant.RI_NAME));
				tempRI.setAcronym(Constant.EMPTY_STRING);
				tempRI.setExposed(rs.getString(Constant.RI_EXPO));
				tempRI.setOwner(getStringOrEmpty(rs, Constant.RI_OWNER));
				tempRI.setComment(rs.getString(Constant.RI_COMMENT));
				tempRI.setHiddenComment(rs.getString(Constant.RI_COMMENT2));

				// ****************************************************************
				// * add instance to list of risk information
				// ****************************************************************
				this.analysis.add(tempRI);
			}
		} finally {
			// Close ResultSet
			if (rs != null)
				rs.close();
		}

	}

	private void importRiskProfile() throws SQLException {
		ResultSet resultSet = null;
		try {
			resultSet = sqlite.query("Select * From risk_profile");
			if (resultSet == null)
				return;
			riskProfiles = new LinkedHashMap<>(analysis.getAssessments().size());
			while (resultSet.next()) {

				int assetId = resultSet.getInt("id_asset"), scenarioId = resultSet.getInt("id_threat");

				RiskProfile riskProfile = new RiskProfile(assets.get(assetId), scenarios.get(scenarioId));

				riskProfile.setActionPlan(resultSet.getString("actionPlan"));
				riskProfile.setRiskTreatment(resultSet.getString("treatment"));
				riskProfile.setRiskStrategy(RiskStrategy.valueOf(resultSet.getString("strategy")));

				riskProfile.setExpProbaImpact(new RiskProbaImpact());
				riskProfile.setRawProbaImpact(new RiskProbaImpact());
				riskProfile.getExpProbaImpact().setVulnerability(getInt(resultSet, "exp_vulnerability", 1));
				riskProfile.getRawProbaImpact().setVulnerability(getInt(resultSet, "raw_vulnerability", 1));

				riskProfile.getExpProbaImpact().setProbability(
						(LikelihoodParameter) probabilities.get(resultSet.getString("exp_probability")));
				riskProfile.getRawProbaImpact().setProbability(
						(LikelihoodParameter) probabilities.get(resultSet.getString("raw_probability")));

				if (isCompability1X()) {
					riskProfile.getExpProbaImpact()
							.add((ImpactParameter) impactParameters.get(Parameter.key(
									Constant.DEFAULT_IMPACT_TYPE_NAMES[0], resultSet.getString("exp_impact_fin"))));
					riskProfile.getExpProbaImpact()
							.add((ImpactParameter) impactParameters.get(Parameter.key(
									Constant.DEFAULT_IMPACT_TYPE_NAMES[1], resultSet.getString("exp_impact_leg"))));
					riskProfile.getExpProbaImpact()
							.add((ImpactParameter) impactParameters.get(Parameter
									.key(Constant.DEFAULT_IMPACT_TYPE_NAMES[2], resultSet.getString("exp_impact_op"))));
					riskProfile.getExpProbaImpact()
							.add((ImpactParameter) impactParameters.get(Parameter.key(
									Constant.DEFAULT_IMPACT_TYPE_NAMES[3], resultSet.getString("exp_impact_rep"))));
					riskProfile.getRawProbaImpact()
							.add((ImpactParameter) impactParameters.get(Parameter.key(
									Constant.DEFAULT_IMPACT_TYPE_NAMES[0], resultSet.getString("raw_impact_fin"))));
					riskProfile.getRawProbaImpact()
							.add((ImpactParameter) impactParameters.get(Parameter.key(
									Constant.DEFAULT_IMPACT_TYPE_NAMES[1], resultSet.getString("raw_impact_leg"))));
					riskProfile.getRawProbaImpact()
							.add((ImpactParameter) impactParameters.get(Parameter
									.key(Constant.DEFAULT_IMPACT_TYPE_NAMES[2], resultSet.getString("raw_impact_op"))));
					riskProfile.getRawProbaImpact()
							.add((ImpactParameter) impactParameters.get(Parameter.key(
									Constant.DEFAULT_IMPACT_TYPE_NAMES[3], resultSet.getString("raw_impact_rep"))));
				}
				riskProfiles.put(key(assetId, scenarioId), riskProfile);
			}

			if (isCompability1X())
				return;

			resultSet.close();
			resultSet = sqlite.query("Select * From risk_profile_impact");
			if (resultSet == null)
				return;
			while (resultSet.next()) {
				RiskProfile riskProfile = riskProfiles
						.get(key(resultSet.getInt("id_asset"), resultSet.getInt("id_threat")));
				ImpactParameter impact = (ImpactParameter) impactParameters.get(resultSet.getString("value"));
				switch (resultSet.getString("name")) {
					case "RAW":
						riskProfile.getRawProbaImpact().add(impact);
						break;
					case "EXP":
						riskProfile.getExpProbaImpact().add(impact);
						break;
				}
			}
			analysis.setRiskProfiles(riskProfiles.values().parallelStream().collect(Collectors.toList()));
		} finally {
			if (resultSet != null)
				resultSet.close();
		}

	}

	private void importRiskProfileMeasures() throws SQLException {
		if (isCompability1X())
			return;
		ResultSet resultSet = null;
		try {
			resultSet = sqlite.query("Select * From risk_profile_measure");
			if (resultSet == null)
				return;
			while (resultSet.next()) {
				Measure measure = measures.get(measureKey(resultSet.getString(Constant.MEASURE_ID_NORM),
						resultSet.getInt(Constant.MEASURE_VERSION_NORM),
						resultSet.getString(Constant.MEASURE_REF_MEASURE)));
				RiskProfile riskProfile = riskProfiles
						.get(key(resultSet.getInt("id_asset"), resultSet.getInt("id_threat")));
				riskProfile.getMeasures().add(measure);
			}
		} finally {
			if (resultSet != null)
				resultSet.close();
		}

	}

	private void importScenarioAssets() throws SQLException {
		ResultSet resultSet = null;
		try {
			resultSet = sqlite.query("Select * From threat_assets");
			if (resultSet == null)
				return;
			scenarioAssets = new LinkedHashMap<>();
			while (resultSet.next()) {
				final Integer idScenario = resultSet.getInt("id_threat");
				final Integer idAsset = resultSet.getInt("id_asset");
				List<Integer> assetIds = scenarioAssets.get(idScenario);
				if (assetIds == null)
					scenarioAssets.put(idScenario, assetIds = new LinkedList<>());
				assetIds.add(idAsset);
			}
		} finally {
			if (resultSet != null)
				resultSet.close();
		}
	}

	/**
	 * importScenarios: <br>
	 * <ul>
	 * <li>Imports all Scenarios</li>
	 * <li>Creates Objects for each Scenario</li>
	 * <li>Adds the Objects to the "scenarios" field List</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importScenarios() throws Exception {

		System.out.println("Import Scenarios");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;
		String query = "";
		scenarios = new HashMap<Integer, Scenario>();
		Scenario tempScenario = null;
		Integer id = 0;
		importScenarioAssets();
		boolean hasNewType = NaturalOrderComparator.compareTo(version, "2.3") > 0;

		// ****************************************************************
		// * Query sqlite for all scenario types
		// ****************************************************************

		/*
		 * // build query query = "SELECT * FROM threat_types order by id_type_threat";
		 * 
		 * // execute query rs = sqlite.query(query, null);
		 * 
		 * // Loop scenario types while (rs.next()) {
		 * 
		 * // **************************************************************** // *
		 * Insert data into scenario type table //
		 * **************************************************************** type =
		 * rs.getString(Constant.THREAT_TYPE_LABEL);
		 * 
		 * scenarioType = ScenarioType.getByName(type);
		 * 
		 * // add scneario type to map
		 * scenarioTypes.put(rs.getInt(Constant.THREAT_ID_TYPE_THREAT), scenarioType); }
		 */
		// System.out.println("scenariotypes ok");

		// ****************************************************************
		// * Query sqlite for all scenarios
		// ****************************************************************

		try {
			// build query
			query = "SELECT * FROM threats";
			// execute query
			rs = sqlite.query(query);
			// Loop scenarios
			while (rs.next()) {

				// ****************************************************************
				// * Determine the scenario type
				// ****************************************************************

				// ****************************************************************
				// * create scenario instance
				// ****************************************************************
				tempScenario = new Scenario();
				tempScenario.setName(rs.getString(Constant.THREAT_NAME_THREAT));
				if (rs.getString(Constant.THREAT_SEL_THREAT).equalsIgnoreCase(Constant.THREAT_SELECTED)) {
					tempScenario.setSelected(true);
				} else {
					tempScenario.setSelected(false);
				}

				tempScenario.setAssetLinked(getBoolean(rs, "linked_asset_threat"));
				tempScenario.setDescription(rs.getString(Constant.THREAT_DESCRIPTION_THREAT));
				tempScenario.setCategoryValue(Constant.CONFIDENTIALITY_RISK,
						rs.getInt(Constant.THREAT_CONFIDENTIALITY));
				tempScenario.setCategoryValue(Constant.INTEGRITY_RISK, rs.getInt(Constant.THREAT_INTEGRITY));
				tempScenario.setCategoryValue(Constant.AVAILABILITY_RISK, rs.getInt(Constant.THREAT_AVAILABILITY));
				if (hasNewType) {
					tempScenario.setCategoryValue(Constant.EXPLOITABILITY_RISK,
							getInt(rs, Constant.THREAT_EXPLOITABILITY));
					tempScenario.setCategoryValue(Constant.RELIABILITY_RISK, getInt(rs, Constant.THREAT_RELIABILITY));
					tempScenario.setCategoryValue(Constant.ILR_RISK, getInt(rs, Constant.THREAT_ILR));
				}

				tempScenario.setThreat(getString(rs, "threat_code"));

				tempScenario.setVulnerability(getString(rs, "vulnerability_code"));

				// add cssf categories to object
				setAllCriteriaCSSFCategories(tempScenario, rs);
				tempScenario.setPreventive(rs.getDouble(Constant.THREAT_PREVENTIVE));
				tempScenario.setDetective(rs.getDouble(Constant.THREAT_DETECTIVE));
				tempScenario.setLimitative(rs.getDouble(Constant.THREAT_LIMITATIVE));
				tempScenario.setCorrective(rs.getDouble(Constant.THREAT_CORRECTIVE));
				tempScenario.setIntentional(rs.getInt(Constant.THREAT_INTENTIONAL));
				tempScenario.setAccidental(rs.getInt(Constant.THREAT_ACCIDENTAL));
				tempScenario.setEnvironmental(rs.getInt(Constant.THREAT_ENVIRONMENTAL));
				tempScenario.setExternalThreat(rs.getInt(Constant.THREAT_EXTERNAL_THREAT));
				tempScenario.setInternalThreat(rs.getInt(Constant.THREAT_INTERNAL_THREAT));
				tempScenario.setType(ScenarioType.getByName(rs.getString("type_threat")));
				// store scenario to build assessment.
				scenarios.put(id = rs.getInt(Constant.THREAT_ID_THREAT), tempScenario);
				// ****************************************************************
				// * add instance to list of scenarios
				// ****************************************************************
				this.analysis.add(tempScenario);
				if (tempScenario.isAssetLinked()) {
					for (Integer idAsset : scenarioAssets.getOrDefault(id, Collections.emptyList()))
						tempScenario.addApplicable(assets.get(idAsset));
				} else
					setScenarioAssetValues(tempScenario, rs);
			}

		} finally {
			// Close ResultSet
			if (rs != null)
				rs.close();
		}

	}

	/**
	 * importSimpleParameters: <br>
	 * <ul>
	 * <li>Imports all Simple Parameters (scope, maturity max efficiency)</li>
	 * <li>Creates Objects for each Simple SimpleParameter</li>
	 * <li>Adds the Objects to the "parameters" field List</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importSimpleParameters() throws Exception {

		System.out.println("Import Simple Parameters");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;
		SimpleParameter simpleParameter = null;
		String query = "";
		ParameterType parameterType = null;
		final boolean hasIlrScale = NaturalOrderComparator.compareTo(version, "2.5") >= 0;

		// ****************************************************************
		// * import scope values:
		// * - internal_setup_rate
		// * - external_setup_rate
		// * - lifetime_default
		// * - maintenance_default
		// * - tuning
		// ****************************************************************

		// ****************************************************************
		// * retrieve parameter type for the instance
		// ****************************************************************

		// ****************************************************************
		// * Import maturity_max_effency
		// ****************************************************************

		// ****************************************************************
		// * retrieve parametertype label
		// ****************************************************************

		// ****************************************************************
		// * retrieve maturity_max_effency
		// ****************************************************************

		// ****************************************************************
		// * Import maturity_IS
		// ****************************************************************

		// ****************************************************************
		// * retrieve parametertype label
		// ****************************************************************

		// ****************************************************************
		// * retrieve maturity_IS
		// ****************************************************************

		try {
			currentSqliteTable = "scope";
			// build query
			if (hasIlrScale) {
				query = "SELECT internal_setup_rate, external_setup_rate, lifetime_default, max_rrf, soaThreshold, mandatoryPhase, ilr_rrf_threshold FROM scope";
				// execute query
				rs = sqlite.query(query, null);
			} else {
				rs = sqlite.query(
						"SELECT internal_setup_rate, external_setup_rate, lifetime_default, max_rrf, soaThreshold, mandatoryPhase FROM scope");
			}
			// retrieve parameter type
			parameterType = daoParameterType.getByName(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME);
			// paramter type does not exist -> NO
			if (parameterType == null)
				// save parameter type into database
				daoParameterType.save(parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME));
			// Retrieve result
			if (rs.next()) {

				// ****************************************************************
				// * create parameter instance for internal setup rate
				// ****************************************************************
				simpleParameter = new SimpleParameter();
				simpleParameter.setDescription(Constant.PARAMETER_INTERNAL_SETUP_RATE);
				simpleParameter.setType(parameterType);
				simpleParameter.setValue(rs.getInt(Constant.PARAMETER_INTERNAL_SETUP_RATE));

				// ****************************************************************
				// * add instance to list of parameters
				// ****************************************************************
				this.analysis.add(simpleParameter);

				// ****************************************************************
				// * create instance for external setup rate
				// ****************************************************************
				simpleParameter = new SimpleParameter();
				simpleParameter.setDescription(Constant.PARAMETER_EXTERNAL_SETUP_RATE);
				simpleParameter.setType(parameterType);
				simpleParameter.setValue(rs.getInt(Constant.PARAMETER_EXTERNAL_SETUP_RATE));

				// ****************************************************************
				// * add instance to list of parameters
				// ****************************************************************
				this.analysis.add(simpleParameter);

				// ****************************************************************
				// * Insert default lifetime into simple parameter table
				// ****************************************************************

				// ****************************************************************
				// * create instance of parameter
				// ****************************************************************
				simpleParameter = new SimpleParameter();
				simpleParameter.setDescription(Constant.PARAMETER_LIFETIME_DEFAULT);
				simpleParameter.setType(parameterType);
				simpleParameter.setValue(rs.getInt(Constant.PARAMETER_LIFETIME_DEFAULT));

				// ****************************************************************
				// * add instance to list of parameters
				// ****************************************************************
				this.analysis.add(simpleParameter);

				// ****************************************************************
				// * Insert tuning into simple parameter table
				// ****************************************************************

				// ****************************************************************
				// * create instance of tuning
				// *****************************************************************
				simpleParameter = new SimpleParameter();
				simpleParameter.setDescription(Constant.PARAMETER_MAX_RRF);
				simpleParameter.setType(parameterType);
				simpleParameter.setValue(rs.getInt(Constant.PARAMETER_MAX_RRF));
				/*
				 * // ************************************************************* *** // * add
				 * instance to list of parameters //
				 * ************************************************************* ***
				 */
				this.analysis.add(simpleParameter);

				// ****************************************************************
				// * Insert mandatoryPhase into simple parameter table
				// ****************************************************************

				simpleParameter = new SimpleParameter(parameterType, Constant.SOA_THRESHOLD,
						rs.getDouble(Constant.SOA_THRESHOLD));
				this.analysis.add(simpleParameter);

				// ****************************************************************
				// * create instance of mandatoryPhase
				// *****************************************************************

				simpleParameter = new SimpleParameter();
				simpleParameter.setDescription(Constant.MANDATORY_PHASE);
				simpleParameter.setType(parameterType);
				simpleParameter.setValue(rs.getInt(Constant.MANDATORY_PHASE));
				this.analysis.add(simpleParameter);

				simpleParameter = new SimpleParameter();
				simpleParameter.setDescription(Constant.ILR_RRF_THRESHOLD);
				simpleParameter.setType(parameterType);
				simpleParameter.setValue(getDouble(rs, Constant.ILR_RRF_THRESHOLD, 5d));
				this.analysis.add(simpleParameter);

				/*
				 * // ************************************************************* *** // * add
				 * instance to list of parameters //
				 * ************************************************************* ***
				 */
			}
			// close result
			rs.close();
			parameterType = daoParameterType.getByName(Constant.PARAMETERTYPE_TYPE_CSSF_NAME);
			if (parameterType == null)
				daoParameterType.save(parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_CSSF_NAME));
			rs = sqlite.query(
					"SELECT cssfImpactThreshold, cssfProbabilityThreshold, cssfDirectSize, cssfIndirectSize, cssfCIASize FROM scope");
			if (rs == null) {
				this.analysis.add(new SimpleParameter(parameterType, Constant.CSSF_IMPACT_THRESHOLD,
						(double) Constant.CSSF_IMPACT_THRESHOLD_VALUE));
				this.analysis.add(new SimpleParameter(parameterType, Constant.CSSF_PROBABILITY_THRESHOLD,
						(double) Constant.CSSF_PROBABILITY_THRESHOLD_VALUE));
				this.analysis.add(new SimpleParameter(parameterType, Constant.CSSF_DIRECT_SIZE, 20D));
				this.analysis.add(new SimpleParameter(parameterType, Constant.CSSF_INDIRECT_SIZE, 5D));
				this.analysis.add(new SimpleParameter(parameterType, Constant.CSSF_CIA_SIZE, -1D));
			} else {
				while (rs.next()) {
					this.analysis.add(new SimpleParameter(parameterType, Constant.CSSF_IMPACT_THRESHOLD,
							rs.getDouble(Constant.CSSF_IMPACT_THRESHOLD)));
					this.analysis.add(new SimpleParameter(parameterType, Constant.CSSF_PROBABILITY_THRESHOLD,
							rs.getDouble(Constant.CSSF_PROBABILITY_THRESHOLD)));
					this.analysis.add(new SimpleParameter(parameterType, Constant.CSSF_DIRECT_SIZE,
							rs.getDouble(Constant.CSSF_DIRECT_SIZE)));
					this.analysis.add(new SimpleParameter(parameterType, Constant.CSSF_INDIRECT_SIZE,
							rs.getDouble(Constant.CSSF_INDIRECT_SIZE)));
					this.analysis.add(new SimpleParameter(parameterType, Constant.CSSF_CIA_SIZE,
							rs.getDouble(Constant.CSSF_CIA_SIZE)));
				}
				rs.close();
			}
			parameterType = daoParameterType.getByName(Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME);
			// paramter type does not exist -> NO
			if (parameterType == null)
				// save parameter type into database
				daoParameterType.save(parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME));
			currentSqliteTable = "maturity_max_eff";
			// build and execute query
			rs = sqlite.query("SELECT * FROM maturity_max_eff");
			// retrieve results
			while (rs.next()) {

				// ****************************************************************
				// * Insert data into simple parameter table for
				// maturity_max_eff
				// ****************************************************************

				// ****************************************************************
				// * create instance
				// ****************************************************************
				simpleParameter = new SimpleParameter();
				simpleParameter.setDescription("SML" + String.valueOf(rs.getInt(Constant.MATURITY_MAX_EFF_COL)));
				simpleParameter.setType(parameterType);
				simpleParameter.setValue(rs.getDouble(Constant.MATURITY_MAX_EFF_VALUE) * 100);

				// ****************************************************************
				// * add instance to list of parameters
				// ****************************************************************
				this.analysis.add(simpleParameter);
			}
			// close result
			rs.close();
			parameterType = daoParameterType.getByName(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME);
			// paramter type does not exist -> NO
			if (parameterType == null) {
				// save parameter type into database
				daoParameterType
						.save(parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME));
			}
			currentSqliteTable = "maturity_IS";
			// build and execute query
			rs = sqlite.query("SELECT * FROM maturity_IS");
			// retrieve results
			while (rs.next()) {

				// ****************************************************************
				// * Insert data into simple parameter table
				// ****************************************************************

				// ****************************************************************
				// * create instance
				// ****************************************************************
				simpleParameter = new SimpleParameter();

				String desc = "ImpScale";

				switch (rs.getInt(Constant.MATURITY_IS_LINE)) {
					case 1:
						desc = Constant.IS_NOT_ACHIEVED;
						break;
					case 2:
						desc = Constant.IS_RUDIMENTARY_ACHIEVED;
						break;
					case 3:
						desc = Constant.IS_PARTIALLY_ACHIEVED;
						break;
					case 4:
						desc = Constant.IS_LARGELY_ACHIEVED;
						break;
					case 5:
						desc = Constant.IS_FULLY_ACHIEVED;
						break;
					default:
						desc = "ImpScale" + String.valueOf(rs.getInt(Constant.MATURITY_IS_LINE));
						break;
				}

				simpleParameter.setDescription(desc);
				simpleParameter.setType(parameterType);
				simpleParameter.setValue(rs.getDouble(Constant.MATURITY_IS_VALUE) * 100);

				// ****************************************************************
				// * add instance to list of parameters
				// ****************************************************************
				this.analysis.add(simpleParameter);
			}

			if (!isCompability1X()) {
				// close result
				rs.close();
				parameterType = daoParameterType.getByName(Constant.PARAMETERTYPE_TYPE_RISK_ACCEPTANCE_NAME);
				if (parameterType == null)
					daoParameterType
							.save(parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_RISK_ACCEPTANCE_NAME));
				rs = sqlite.query("SELECT label, level, color, description from risk_acceptance order by level");
				if (rs != null) {
					while (rs.next())
						this.analysis.add(new RiskAcceptanceParameter(getStringOrEmpty(rs, "label"),
								rs.getDouble("level"), getStringOrEmpty(rs, "color"),
								getStringOrEmpty(rs, "description")));
					rs.close();
				}

				if (hasIlrScale) {
					parameterType = daoParameterType.getByName(Constant.PARAMETERTYPE_TYPE_ILR_SOA_SCALE_NAME);
					if (parameterType == null)
						daoParameterType
								.save(parameterType = new ParameterType(
										Constant.PARAMETERTYPE_TYPE_ILR_SOA_SCALE_NAME));

					rs = sqlite.query("SELECT level, color, description from ilr_soa_scale order by level");
					if (rs != null) {
						while (rs.next())
							this.analysis.add(new IlrSoaScaleParameter(
									rs.getDouble("level"), getStringOrEmpty(rs, "color"),
									getStringOrEmpty(rs, "description")));
						rs.close();
					}

					// ILR Vulnerability Scale
					if (NaturalOrderComparator.compareTo(version, "2.6") >= 0) {
						parameterType = daoParameterType
								.getByName(Constant.PARAMETERTYPE_TYPE_ILR_VULNERABILITY_SCALE_NAME);
						if (parameterType == null)
							daoParameterType
									.save(parameterType = new ParameterType(
											Constant.PARAMETERTYPE_TYPE_ILR_VULNERABILITY_SCALE_NAME));

						rs = sqlite.query("SELECT level, description from ilr_vulnerability_scale order by level");
						if (rs != null) {
							while (rs.next())
								this.analysis.add(new SimpleParameter(parameterType,
										getStringOrEmpty(rs, "description"), rs.getDouble("level")));
							rs.close();
						}

					}
				}
			}

		} finally {
			// close result
			if (rs != null)
				rs.close();
		}

	}

	private String key(int assetId, int scenarioId) {
		return assetId + "_" + scenarioId;
	}

	/**
	 * loadHistoryFromSqlite: <br>
	 * <ul>
	 * <li>Generates History Objects from Analysis</li>
	 * <li>Adds the Objects to the "histories" field List</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void loadHistoryFromSqlite() throws Exception {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;
		String query = "";
		History tempHist = null;
		setCurrentSqliteTable("history");

		// ****************************************************************
		// * Retrieve histories of the analysis
		// ****************************************************************

		try {
			// build query
			query = "SELECT * from history ORDER BY id_version ASC";
			// execute query
			rs = sqlite.query(query, null);
			// parse result
			while (rs.next()) {

				// ****************************************************************
				// * for each history entry, add a new history instance
				// ****************************************************************

				// ****************************************************************
				// * create history entry
				// ****************************************************************
				tempHist = new History();

				// add version
				tempHist.setVersion(rs.getString(Constant.HISTORY_ID_VERSION));

				// add date of the comment
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				tempHist.setDate(dateFormat.parse(rs.getString(Constant.HISTORY_DATE)));

				// add author
				tempHist.setAuthor(rs.getString(Constant.HISTORY_AUTHOR));

				// add comment
				tempHist.setComment(rs.getString(Constant.HISTORY_COMMENT));

				// ****************************************************************
				// * add history entry to list of histories
				// ****************************************************************
				this.analysis.addAHistory(tempHist);
			}
		} finally {
			// close results
			if (rs != null)
				rs.close();
		}

	}

	private String measureKey(String standardName, int standardVersion, String measureRefMeasure) {
		return standardName + "_" + standardVersion + "_" + measureRefMeasure;
	}

	/**
	 * setAllCriteriaCategories: <br>
	 * Adds all Scenario Categories to the given scenario or measure object. <br>
	 * Scenario categories are: <br>
	 * <ul>
	 * <li>Direct: d1, d2, d3, d4, d5, d6, d6.1, d6.2, d6.3, d6.4, d7</li>
	 * <li>Indirect: i1, i2, i3, i4, i5, i6, i7, i8, i8.1, i8.2, i8.3, i8.4, i9,
	 * i10</li>
	 * </ul>
	 * 
	 * @param criteria  The scenario or measure object to set the categories with
	 *                  values
	 * @param resultSet The ResultSet from where the categorie values come from
	 * 
	 * @throws Exception
	 */
	private void setAllCriteriaCSSFCategories(SecurityCriteria criteria, ResultSet resultSet) throws Exception {

		// set the list of SQLite keys that represent the categories

		// get object keys (other key names that represent the same)
		String[] keys = SecurityCriteria.getCSSFCategoryKeys();

		// parse each category from SQLight and set the corresponding category
		// with a value
		for (String category : keys)
			criteria.setCategoryValue(category, resultSet.getInt(CategoryConverter.getSQLiteFromType(category)));
	}

	private void setImpact(Assessment tmpAssessment, String type, Double value) {
		IValue impact = findValue(value, type);
		tmpAssessment.setImpact(impact == null ? findValue(0.0, type) : impact);
	}

	private void setImpact(Assessment tmpAssessment, String type, String value) {
		IImpactParameter parameter = impactParameters.get(Parameter.key(type, value));
		IValue impact = parameter == null ? findValue(value, type) : new Value(parameter);
		tmpAssessment.setImpact(impact == null ? findValue(0.0, type) : impact);
	}

	private IValue findValue(Object content, String type) {
		IValue value = factory.findValue(content, type);
		if (Constant.DEFAULT_IMPACT_NAME.equals(type)) {
			if (value != null)
				return value;
			else if (content instanceof String) {
				return new FormulaValue((String) content, 0D);
			}
		} else if (value != null) {
			if (value instanceof Value)
				return value;
			else if (value instanceof IParameterValue)
				return new Value(((IParameterValue) value).getParameter());
		}
		return null;

	}

	/**
	 * setScenarioAssetValues: <br>
	 * Sets all Asset Type Values for a given Scenario using a SQL Result.
	 * 
	 * @param scenario The Scenario Object to set the Asset Type Values
	 * @param rs       The SQL Result to take values from
	 * 
	 * @throws Exception
	 */
	private void setScenarioAssetValues(Scenario scenario, ResultSet rs) throws Exception {

		// retrive Asset Type Values
		final String[] keys = Constant.ASSET_TYPES.split(",");

		// initialise variables
		AssetType assetType = null;

		// parse all Asset Types (keys)
		for (String key : keys) {

			// retrieve asset type from database by asset type name
			assetType = daoAssetType.getByName(key);

			// asset type does not exist
			if (assetType == null) {

				// create new asset type
				assetType = new AssetType(key);

				// store asset type into database
				daoAssetType.save(assetType);
			}

			// add asset type values to scenario
			scenario.add(new AssetTypeValue(assetType, getInt(rs, key)));
		}
	}

	public static boolean getBoolean(ResultSet rs, String name) {
		try {
			return rs.getBoolean(name);
		} catch (SQLException e) {
			return false;
		}
	}

	public static Double getDouble(ResultSet rs, String name) {
		return getDouble(rs, name, 0D);
	}

	public static Double getDouble(ResultSet rs, String name, Double defaultValue) {
		try {
			return rs.getDouble(name);
		} catch (SQLException e) {
			return defaultValue;
		}
	}

	public static int getInt(ResultSet rs, String name) {
		return getInt(rs, name, 0);
	}

	public static int getInt(ResultSet rs, String name, int defaultValue) {
		try {
			return rs.getInt(name);
		} catch (SQLException e) {
			return defaultValue;
		}
	}

	public static String getString(ResultSet rs, String name) {
		return getString(rs, name, null);
	}

	public static String getString(ResultSet rs, String name, String defaultValue) {
		try {
			return rs.getString(name);
		} catch (SQLException e) {
			return defaultValue;
		}
	}

	public static String getStringOrEmpty(ResultSet rs, String name) {
		return getString(rs, name, "");
	}

	/**
	 * columnExists: <br>
	 * Description
	 * 
	 * @param rs
	 * @param columnname
	 * @return
	 */
	private static boolean columnExists(ResultSet rs, String columnname) {
		try {
			return rs.findColumn(columnname) >= 0;
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * @return the maxProgress
	 */
	public int getMaxProgress() {
		return maxProgress;
	}

	/**
	 * @param maxProgress the maxProgress to set
	 */
	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

	/**
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * @return the globalProgress
	 */
	public int getGlobalProgress() {
		return globalProgress;
	}

	/**
	 * @param globalProgress the globalProgress to set
	 */
	public void setGlobalProgress(int globalProgress) {
		this.globalProgress = globalProgress;
	}

	public int increase(int value) {
		if (!(value < 0 || value > 100)) {
			progress += value;
			if (progress > 100)
				setProgress(100);
		}
		return (int) (globalProgress + (maxProgress - globalProgress) * 0.01 * progress);
	}

}
