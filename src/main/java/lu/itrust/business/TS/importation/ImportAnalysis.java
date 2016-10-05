package lu.itrust.business.TS.importation;

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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.Session;

import lu.itrust.business.TS.component.DynamicParameterComputer;
import lu.itrust.business.TS.component.NaturalOrderComparator;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.DatabaseHandler;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOAssetType;
import lu.itrust.business.TS.database.dao.DAOLanguage;
import lu.itrust.business.TS.database.dao.DAOMeasureDescription;
import lu.itrust.business.TS.database.dao.DAOMeasureDescriptionText;
import lu.itrust.business.TS.database.dao.DAOParameterType;
import lu.itrust.business.TS.database.dao.DAOStandard;
import lu.itrust.business.TS.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOAssetTypeHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOLanguageHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOMeasureDescriptionHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOMeasureDescriptionTextHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOParameterTypeHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOStandardHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOUserAnalysisRightHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.general.SecurityCriteria;
import lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.model.history.History;
import lu.itrust.business.TS.model.history.helper.ComparatorHistoryVersion;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;
import lu.itrust.business.TS.model.parameter.DynamicParameter;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.MaturityParameter;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.parameter.ParameterType;
import lu.itrust.business.TS.model.parameter.helper.Bounds;
import lu.itrust.business.TS.model.parameter.helper.ParameterManager;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.scenario.ScenarioType;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.AssetStandard;
import lu.itrust.business.TS.model.standard.MaturityStandard;
import lu.itrust.business.TS.model.standard.NormalStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.StandardType;
import lu.itrust.business.TS.model.standard.measure.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.MaturityMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.MeasureAssetValue;
import lu.itrust.business.TS.model.standard.measure.MeasureProperties;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.usermanagement.User;

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

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	private String idTask;

	private DAOParameterType daoParameterType;

	private DAOAssetType daoAssetType;

	private DAOAnalysis daoAnalysis;

	private DAOLanguage daoLanguage;

	private DAOMeasureDescription daoMeasureDescription;

	private DAOMeasureDescriptionText daoMeasureDescriptionText;

	private DAOStandard daoStandard;

	private DAOUserAnalysisRight daoUserAnalysisRight;

	private ServiceTaskFeedback serviceTaskFeedback;

	private Session session;

	private String currentSqliteTable = "";

	/** The Analysis Object */
	private Analysis analysis = null;

	/** The SQLite Database Handler */
	private DatabaseHandler sqlite = null;

	private Map<Integer, AssetType> assetTypes = null;

	/** Map of Assets */
	private Map<Integer, Asset> assets = null;

	/** Map of Scenarios */
	private Map<Integer, Scenario> scenarios = null;

	/** Map of Phases */
	private Map<Integer, Phase> phases = null;

	/** Map of AnalysisStandards */
	private Map<Standard, AnalysisStandard> analysisStandards = null;

	/** Map of Standards */
	private Map<String, Standard> standards = null;

	/** Map of Measures */
	private Map<String, Measure> measures = null;

	private Map<String, ExtendedParameter> extendedParameters = null;

	/***********************************************************************************************
	 * Constructor
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 * 
	 * @param analysis
	 *            The Analysis Object
	 * @param sqlite
	 *            The SQLite Object (DatabaseHandler)
	 */
	public ImportAnalysis(Analysis analysis, DatabaseHandler sqlite) {
		this.analysis = analysis;
		this.sqlite = sqlite;
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	public ImportAnalysis() {
	}

	public ImportAnalysis(Analysis analysis, ServiceTaskFeedback serviceTaskFeedback) {
		setAnalysis(analysis);
		setServiceTaskFeedback(serviceTaskFeedback);
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
	 * ImportAnAnalysis: <br>
	 * Method used to import and given analysis using an sqlite file into the
	 * mysql database.
	 * 
	 * @param session
	 *            TODO
	 * 
	 * @throws Exception
	 */
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

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.importing", "Importing", 0));
			importAnalyses();

			// ****************************************************************
			// * import risk information
			// ****************************************************************

			serviceTaskFeedback.send(idTask, new MessageHandler("info.risk_information.importing", "Importing risk information", 1));
			importRiskInformation();

			// ****************************************************************
			// * import item information
			// ****************************************************************

			serviceTaskFeedback.send(idTask, new MessageHandler("info.risk_information.importing", "Import item information", 5));
			importItemInformation();

			// ****************************************************************
			// * import simple parameters
			// ****************************************************************

			serviceTaskFeedback.send(idTask, new MessageHandler("info.simple_parameters.importing", "Import simple parameters", 10));
			importSimpleParameters();

			// ****************************************************************
			// * import dynamic parameters
			// ****************************************************************

			serviceTaskFeedback.send(idTask, new MessageHandler("info.dynamic_parameters.importing", "Import dynamic parameters", 15));
			importDynamicParameters();

			// ****************************************************************
			// * import extended parameters
			// ****************************************************************

			serviceTaskFeedback.send(idTask, new MessageHandler("info.extended_parameters.importing", "Import extended parameters", 15));
			importExtendedParameters();

			// ****************************************************************
			// * import maturity parameters
			// ****************************************************************

			serviceTaskFeedback.send(idTask, new MessageHandler("info.maturity_parameters.importing", "Import maturity parameters", 20));
			importMaturityParameters();

			// ****************************************************************
			// * import assets
			// ****************************************************************

			serviceTaskFeedback.send(idTask, new MessageHandler("info.asset.importing", "Import assets", 25));
			importAssets();

			// ****************************************************************
			// * import scenarios
			// ****************************************************************

			serviceTaskFeedback.send(idTask, new MessageHandler("info.scenario.importing", "Import scenarios", 35));
			importScenarios();

			// ****************************************************************
			// * import assessments
			// ****************************************************************

			serviceTaskFeedback.send(idTask, new MessageHandler("info.assessments.importing", "Import assessments", 40));
			importAssessments();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.risk_profile.importing", "Import risk profile", 40));
			importRiskProfile();

			// ****************************************************************
			// * import phases
			// ****************************************************************

			serviceTaskFeedback.send(idTask, new MessageHandler("info.phase.importing", "Import phases", 55));
			importPhases();

			// ****************************************************************
			// * import AnalysisStandard measures
			// ****************************************************************
			serviceTaskFeedback.send(idTask, new MessageHandler("info.norm_measures.importing", "Analysis normal measures", 60));
			importNormalMeasures();

			// ****************************************************************
			// * import asset type values
			// ****************************************************************
			serviceTaskFeedback.send(idTask, new MessageHandler("info.asset_type_value.importing", "Import asset type values", 70));
			importAssetTypeValues();
			importAssetValues();

			// ****************************************************************
			// * import maturity measures
			// ****************************************************************

			serviceTaskFeedback.send(idTask, new MessageHandler("info.maturity_measure.importing", "Import maturity measures", 80));
			importMaturityMeasures();

			// System.out.println("Saving Data to Database...");

			serviceTaskFeedback.send(idTask, new MessageHandler("import.saving.analysis", "Saving Data to Database", 90));

			System.out.println("Saving Analysis Data...");

			// save or update analysis
			daoAnalysis.save(this.analysis);

			// Update values of dynamic parameters
			new DynamicParameterComputer(session, new AssessmentAndRiskProfileManager()).computeForAnalysis(this.analysis);

			// update ALE of asset objects
			new AssessmentAndRiskProfileManager().UpdateRiskDendencies(analysis, analysis.mapExtendedParameterByAcronym());

			daoAnalysis.saveOrUpdate(this.analysis);

			serviceTaskFeedback.send(idTask, new MessageHandler("info.commit.transcation", "Commit transaction", 95));
			if (session != null) {
				session.getTransaction().commit();
				serviceTaskFeedback.send(idTask, new MessageHandler("success.saving.analysis", "Analysis has been successfully saved", 100));
			}
			System.out.println("Import Done!");

			return true;
		} catch (Exception e) {
			try {
				serviceTaskFeedback.send(idTask, new MessageHandler(e.getMessage(), e.getMessage(), e));
				TrickLogManager.Persist(e);
				if (session != null && session.getTransaction().getStatus().canRollback())
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

	private void importRiskProfile() throws SQLException {
		ResultSet resultSet = sqlite.query("Select * From risk_profile");
		if (resultSet == null)
			return;
		List<RiskProfile> riskProfiles = new ArrayList<>(analysis.getAssessments().size());
		while (resultSet.next()) {
			RiskProfile riskProfile = new RiskProfile(assets.get(resultSet.getInt("id_asset")), scenarios.get(resultSet.getInt("id_threat")));

			riskProfile.setActionPlan(resultSet.getString("actionPlan"));
			riskProfile.setRiskTreatment(resultSet.getString("treatment"));
			riskProfile.setRiskStrategy(RiskStrategy.valueOf(resultSet.getString("strategy")));

			riskProfile.setExpProbaImpact(new RiskProbaImpact());
			riskProfile.setRawProbaImpact(new RiskProbaImpact());

			riskProfile.getExpProbaImpact().setImpactFin(extendedParameters.get(resultSet.getString("exp_impact_fin")));
			riskProfile.getExpProbaImpact().setImpactLeg(extendedParameters.get(resultSet.getString("exp_impact_leg")));
			riskProfile.getExpProbaImpact().setImpactOp(extendedParameters.get(resultSet.getString("exp_impact_op")));
			riskProfile.getExpProbaImpact().setImpactRep(extendedParameters.get(resultSet.getString("exp_impact_rep")));
			riskProfile.getExpProbaImpact().setProbability(extendedParameters.get(resultSet.getString("exp_probability")));

			riskProfile.getRawProbaImpact().setImpactFin(extendedParameters.get(resultSet.getString("raw_impact_fin")));
			riskProfile.getRawProbaImpact().setImpactLeg(extendedParameters.get(resultSet.getString("raw_impact_leg")));
			riskProfile.getRawProbaImpact().setImpactOp(extendedParameters.get(resultSet.getString("raw_impact_op")));
			riskProfile.getRawProbaImpact().setImpactRep(extendedParameters.get(resultSet.getString("raw_impact_rep")));
			riskProfile.getRawProbaImpact().setProbability(extendedParameters.get(resultSet.getString("raw_probability")));

			riskProfiles.add(riskProfile);

		}

		analysis.setRiskProfiles(riskProfiles);

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

			if (type == null)
				analysis.setType(getBoolean(rs, "cssf") ? AnalysisType.QUALITATIVE : AnalysisType.QUANTITATIVE);
			else
				analysis.setType(AnalysisType.valueOf(type));

			analysis.setUncertainty(getBoolean(rs, "uncertainty"));
		}

		// close result
		rs.close();

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
				|| daoUserAnalysisRight.isUserAuthorizedOrOwner(this.analysis.getIdentifier(), versions.get(versions.size() - 1), this.analysis.getOwner(), AnalysisRight.ALL)))
			throw new TrickException("error.not_authorized", "Insufficient permissions!");

		// initialise analysis version to the last history version
		Collections.sort(this.analysis.getHistories(), new ComparatorHistoryVersion());

		this.analysis.getHistories().stream().filter(analysisHistory -> !versions.contains(analysisHistory.getVersion()))
				.forEach(analysisHistory -> versions.add(analysisHistory.getVersion()));

		if (!versions.isEmpty())
			Collections.sort(versions, comparator);

		this.analysis.setVersion(this.analysis.getLastHistory().getVersion());

		// ****************************************************************
		// * Parse all history entries except last one
		// ****************************************************************

		// parse each history of analysis (needs to be at least one!)
		for (int i = 0; i < this.analysis.getHistories().size() - 1; i++) {

			// ****************************************************************
			// * check if analysis and version exists
			// ****************************************************************

			history = this.analysis.getAHistory(i);

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
						? daoAnalysis.getFromIdentifierVersionCustomer(this.analysis.getIdentifier(), versions.get(indexOfVersion - 1), this.analysis.getCustomer().getId()) : null;

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
		analysis = daoAnalysis.getFromIdentifierVersionCustomer(this.analysis.getIdentifier(), this.analysis.getVersion(), this.analysis.getCustomer().getId());

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
						String.format("Your file has already been imported, whether it is a new version( %s ), do not forget to increase version", analysis.getVersion()),
						analysis.getVersion());
			}
		}

		Analysis previousVersion = history == null ? null
				: daoAnalysis.getFromIdentifierVersionCustomer(this.analysis.getIdentifier(), history.getVersion(), this.analysis.getCustomer().getId());
		this.analysis.setBasedOnAnalysis(previousVersion);
		if (previousVersion != null) {
			for (UserAnalysisRight analysisRight : previousVersion.getUserRights())
				this.analysis.addUserRight(analysisRight.getUser(), analysisRight.getRight());
		}
		this.analysis.editUserRight(this.analysis.getOwner(), AnalysisRight.ALL);
	}

	/**
	 * importRiskInformation: <br>
	 * <ul>
	 * <li>Imports all Risk Information: Threat Source, Risks, Vulnerabilities
	 * </li>
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
			tempRI.setExposed(rs.getString(Constant.RI_EXPO));
			tempRI.setComment(rs.getString(Constant.RI_COMMENT));
			tempRI.setHiddenComment(rs.getString(Constant.RI_COMMENT2));

			// ****************************************************************
			// * add instance to list of risk information
			// ****************************************************************
			this.analysis.addARiskInformation(tempRI);
		}

		// Close ResultSet
		rs.close();

		// ****************************************************************
		// * Query sqlite for all vulnerabilities (vulnerabilities)
		// ****************************************************************

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
			tempRI.setComment(rs.getString(Constant.RI_COMMENT));
			tempRI.setHiddenComment(rs.getString(Constant.RI_COMMENT2));

			// ****************************************************************
			// * add instance to list of risk information
			// ****************************************************************
			this.analysis.addARiskInformation(tempRI);
		}

		// Close ResultSet
		rs.close();

		// ****************************************************************
		// * Query sqlite for all risks (threat_Source)
		// ****************************************************************

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
			this.analysis.addARiskInformation(tempRI);
		}

		// Close ResultSet
		rs.close();
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
		assetTypes = new HashMap<Integer, AssetType>();
		assets = new HashMap<Integer, Asset>();
		AssetType assetType = null;
		Asset tempAsset = null;

		// ****************************************************************
		// * Query sqlite for all assets types
		// ****************************************************************

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

		// ****************************************************************
		// * Query sqlite for all assets
		// ****************************************************************

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
			tempAsset.setSelected(rs.getString(Constant.ASSET_SEL_ASSET).equalsIgnoreCase(Constant.ASSET_SELECTED));

			// store asset to build assessment.
			assets.put(rs.getInt(Constant.ASSET_ID_ASSET), tempAsset);

			// ****************************************************************
			// * add instance to list of assets
			// ****************************************************************
			this.analysis.addAnAsset(tempAsset);
		}

		// Close ResultSet
		rs.close();
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

		// ****************************************************************
		// * Query sqlite for all scenario types
		// ****************************************************************

		/*
		 * // build query query =
		 * "SELECT * FROM threat_types order by id_type_threat";
		 * 
		 * // execute query rs = sqlite.query(query, null);
		 * 
		 * // Loop scenario types while (rs.next()) {
		 * 
		 * // ****************************************************************
		 * // * Insert data into scenario type table //
		 * **************************************************************** type
		 * = rs.getString(Constant.THREAT_TYPE_LABEL);
		 * 
		 * scenarioType = ScenarioType.getByName(type);
		 * 
		 * // add scneario type to map
		 * scenarioTypes.put(rs.getInt(Constant.THREAT_ID_TYPE_THREAT),
		 * scenarioType); }
		 */
		// System.out.println("scenariotypes ok");

		// ****************************************************************
		// * Query sqlite for all scenarios
		// ****************************************************************

		// build query
		query = "SELECT * FROM threats";

		// execute query
		rs = sqlite.query(query, null);

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
			tempScenario.setDescription(rs.getString(Constant.THREAT_DESCRIPTION_THREAT));
			tempScenario.setCategoryValue(Constant.CONFIDENTIALITY_RISK, rs.getInt(Constant.THREAT_CONFIDENTIALITY));
			tempScenario.setCategoryValue(Constant.INTEGRITY_RISK, rs.getInt(Constant.THREAT_INTEGRITY));
			tempScenario.setCategoryValue(Constant.AVAILABILITY_RISK, rs.getInt(Constant.THREAT_AVAILABILITY));

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
			String type = rs.getString("type_threat");
			tempScenario.setType(ScenarioType.getByName(type));

			// set scenario asset types
			setScenarioAssetValues(tempScenario, rs);

			// store scenario to build assessment.
			scenarios.put(rs.getInt(Constant.THREAT_ID_THREAT), tempScenario);

			// ****************************************************************
			// * add instance to list of scenarios
			// ****************************************************************
			this.analysis.addAScenario(tempScenario);
		}

		// Close ResultSet
		rs.close();
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
		Asset tempAsset = null;
		Scenario tempScenario = null;
		double likelihoodValue = 0;
		double impactRep = 0;
		double impactOp = 0;
		double impactLeg = 0;
		double impactFin = 0;
		double impactValue = 0;
		double ALE = 0;
		double ALEO = 0;
		double ALEP = 0;
		String query = "";
		String parameterName = null;
		Assessment tmpAssessment = null;

		// ****************************************************************
		// * Query sqlite for all assessment
		// ****************************************************************

		// build query
		query = "SELECT * FROM assessment";

		// execute query
		rs = sqlite.query(query, null);

		// Loop assessment
		while (rs.next()) {

			// ****************************************************************
			// retrieve likelihood value
			// ****************************************************************

			parameterName = rs.getString(Constant.ASSESSMENT_POTENTIALITY);

			if (extendedParameters.containsKey(parameterName))
				likelihoodValue = extendedParameters.get(parameterName).getValue();

			// ****************************************************************
			// * Retrieve Impact values
			// ****************************************************************

			// Reputation
			impactRep = convertImpactToDouble(rs.getString(Constant.ASSESSMENT_IMPACT_REP));

			// Financial
			impactFin = convertImpactToDouble(rs.getString(Constant.ASSESSMENT_IMPACT_FIN));

			// Operational
			impactOp = convertImpactToDouble(rs.getString(Constant.ASSESSMENT_IMPACT_OP));

			// Legal
			impactLeg = convertImpactToDouble(rs.getString(Constant.ASSESSMENT_IMPACT_LEG));

			// Determine biggest impact
			impactValue = Math.max(impactFin, Math.max(impactLeg, Math.max(impactOp, impactRep)));

			// ****************************************************************
			// ALE calculation
			// ****************************************************************

			// ALE=Impact * Likelihood
			ALE = impactValue * likelihoodValue;

			// ALEO=ALE / Uncertainty
			ALEO = ALE / rs.getDouble(Constant.ASSESSMENT_UNCERTAINTY);

			// ALEP=ALE * Uncertainty
			ALEP = ALE * rs.getDouble(Constant.ASSESSMENT_UNCERTAINTY);

			// ****************************************************************
			// * retrieve asset instance
			// ****************************************************************
			tempAsset = assets.get(rs.getInt(Constant.ASSET_ID_ASSET));

			// ****************************************************************
			// * retrieve scenario instance
			// ****************************************************************
			tempScenario = scenarios.get(rs.getInt(Constant.THREAT_ID_THREAT));

			// ****************************************************************
			// * create assessment instance
			// ****************************************************************
			tmpAssessment = new Assessment();
			tmpAssessment.setAsset(tempAsset);
			tmpAssessment.setScenario(tempScenario);
			tmpAssessment.setImpactRep(rs.getString(Constant.ASSESSMENT_IMPACT_REP));
			tmpAssessment.setImpactOp(rs.getString(Constant.ASSESSMENT_IMPACT_OP));
			tmpAssessment.setImpactLeg(rs.getString(Constant.ASSESSMENT_IMPACT_LEG));
			tmpAssessment.setImpactFin(rs.getString(Constant.ASSESSMENT_IMPACT_FIN));
			tmpAssessment.setImpactReal(rs.getDouble(Constant.ASSESSMENT_IMPACT_REAL));
			tmpAssessment.setLikelihood(rs.getString(Constant.ASSESSMENT_POTENTIALITY));
			tmpAssessment.setLikelihoodReal(rs.getDouble(Constant.ASSESSMENT_POTENTIALITY_REAL));
			tmpAssessment.setUncertainty(rs.getDouble(Constant.ASSESSMENT_UNCERTAINTY));
			tmpAssessment.setComment(rs.getString(Constant.ASSESSMENT_COMMENT));
			tmpAssessment.setHiddenComment(rs.getString(Constant.ASSESSMENT_HIDE_COMMENT));
			tmpAssessment.setOwner(getStringOrEmpty(rs, "owner"));
			tmpAssessment.setALEO(ALEO);
			tmpAssessment.setALE(ALE);
			tmpAssessment.setALEP(ALEP);
			tmpAssessment.setSelected(rs.getString(Constant.ASSESSMENT_SEL_ASSESSMENT).equals(Constant.ASSESSMENT_SELECTED));

			// System.out.println(tmpAssessment.getALE() + ":::" +
			// tmpAssessment.getAsset().getName() + ":::" +
			// tmpAssessment.getScenario().getName());

			// ****************************************************************
			// * add instance to list of assessments
			// ****************************************************************
			this.analysis.addAnAssessment(tmpAssessment);
		}

		// Close ResultSet
		rs.close();
	}

	/**
	 * importSimpleParameters: <br>
	 * <ul>
	 * <li>Imports all Simple Parameters (scope, maturity max efficiency)</li>
	 * <li>Creates Objects for each Simple Parameter</li>
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
		Parameter parameter = null;
		String query = "";
		ParameterType parameterType = null;

		currentSqliteTable = "scope";

		// ****************************************************************
		// * import scope values:
		// * - internal_setup_rate
		// * - external_setup_rate
		// * - lifetime_default
		// * - maintenance_default
		// * - tuning
		// ****************************************************************

		// build query
		query = "SELECT internal_setup_rate, external_setup_rate, lifetime_default, max_rrf, soaThreshold, mandatoryPhase FROM scope";

		// execute query
		rs = sqlite.query(query, null);

		// ****************************************************************
		// * retrieve parameter type for the instance
		// ****************************************************************

		// retrieve parameter type
		parameterType = daoParameterType.get(Constant.PARAMETERTYPE_TYPE_SINGLE);

		// paramter type does not exist -> NO
		if (parameterType == null)
			// save parameter type into database
			daoParameterType.save(parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_SINGLE, Constant.PARAMETERTYPE_TYPE_SINGLE_NAME));

		// Retrieve result
		if (rs.next()) {

			// ****************************************************************
			// * create parameter instance for internal setup rate
			// ****************************************************************
			parameter = new Parameter();
			parameter.setDescription(Constant.PARAMETER_INTERNAL_SETUP_RATE);
			parameter.setType(parameterType);
			parameter.setValue(rs.getInt(Constant.PARAMETER_INTERNAL_SETUP_RATE));

			// ****************************************************************
			// * add instance to list of parameters
			// ****************************************************************
			this.analysis.addAParameter(parameter);

			// ****************************************************************
			// * create instance for external setup rate
			// ****************************************************************
			parameter = new Parameter();
			parameter.setDescription(Constant.PARAMETER_EXTERNAL_SETUP_RATE);
			parameter.setType(parameterType);
			parameter.setValue(rs.getInt(Constant.PARAMETER_EXTERNAL_SETUP_RATE));

			// ****************************************************************
			// * add instance to list of parameters
			// ****************************************************************
			this.analysis.addAParameter(parameter);

			// ****************************************************************
			// * Insert default lifetime into simple parameter table
			// ****************************************************************

			// ****************************************************************
			// * create instance of parameter
			// ****************************************************************
			parameter = new Parameter();
			parameter.setDescription(Constant.PARAMETER_LIFETIME_DEFAULT);
			parameter.setType(parameterType);
			parameter.setValue(rs.getInt(Constant.PARAMETER_LIFETIME_DEFAULT));

			// ****************************************************************
			// * add instance to list of parameters
			// ****************************************************************
			this.analysis.addAParameter(parameter);

			// ****************************************************************
			// * Insert tuning into simple parameter table
			// ****************************************************************

			// ****************************************************************
			// * create instance of tuning
			// *****************************************************************
			parameter = new Parameter();
			parameter.setDescription(Constant.PARAMETER_MAX_RRF);
			parameter.setType(parameterType);
			parameter.setValue(rs.getInt(Constant.PARAMETER_MAX_RRF));
			/*
			 * //
			 * ****************************************************************
			 * // * add instance to list of parameters //
			 * ****************************************************************
			 */
			this.analysis.addAParameter(parameter);

			// ****************************************************************
			// * Insert mandatoryPhase into simple parameter table
			// ****************************************************************

			parameter = new Parameter(parameterType, Constant.SOA_THRESHOLD, rs.getDouble(Constant.SOA_THRESHOLD));
			this.analysis.addAParameter(parameter);

			// ****************************************************************
			// * create instance of mandatoryPhase
			// *****************************************************************

			parameter = new Parameter();
			parameter.setDescription(Constant.MANDATORY_PHASE);
			parameter.setType(parameterType);
			parameter.setValue(rs.getInt(Constant.MANDATORY_PHASE));
			this.analysis.addAParameter(parameter);

			/*
			 * //
			 * ****************************************************************
			 * // * add instance to list of parameters //
			 * ****************************************************************
			 */
		}
		// close result
		rs.close();

		parameterType = daoParameterType.get(Constant.PARAMETERTYPE_TYPE_CSSF);
		if (parameterType == null)
			daoParameterType.save(parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_CSSF, Constant.PARAMETERTYPE_TYPE_CSSF_NAME));

		rs = sqlite.query("SELECT cssfImpactThreshold, cssfProbabilityThreshold, cssfDirectSize, cssfIndirectSize, cssfCIASize FROM scope");
		if (rs == null) {
			this.analysis.addAParameter(new Parameter(parameterType, Constant.CSSF_IMPACT_THRESHOLD, (double) Constant.CSSF_IMPACT_THRESHOLD_VALUE));
			this.analysis.addAParameter(new Parameter(parameterType, Constant.CSSF_PROBABILITY_THRESHOLD, (double) Constant.CSSF_PROBABILITY_THRESHOLD_VALUE));
			this.analysis.addAParameter(new Parameter(parameterType, Constant.CSSF_DIRECT_SIZE, 20D));
			this.analysis.addAParameter(new Parameter(parameterType, Constant.CSSF_INDIRECT_SIZE, 5D));
			this.analysis.addAParameter(new Parameter(parameterType, Constant.CSSF_CIA_SIZE, -1D));
		} else {
			while (rs.next()) {
				this.analysis.addAParameter(new Parameter(parameterType, Constant.CSSF_IMPACT_THRESHOLD, rs.getDouble(Constant.CSSF_IMPACT_THRESHOLD)));
				this.analysis.addAParameter(new Parameter(parameterType, Constant.CSSF_PROBABILITY_THRESHOLD, rs.getDouble(Constant.CSSF_PROBABILITY_THRESHOLD)));
				this.analysis.addAParameter(new Parameter(parameterType, Constant.CSSF_DIRECT_SIZE, rs.getDouble(Constant.CSSF_DIRECT_SIZE)));
				this.analysis.addAParameter(new Parameter(parameterType, Constant.CSSF_INDIRECT_SIZE, rs.getDouble(Constant.CSSF_INDIRECT_SIZE)));
				this.analysis.addAParameter(new Parameter(parameterType, Constant.CSSF_CIA_SIZE, rs.getDouble(Constant.CSSF_CIA_SIZE)));
			}
			rs.close();
		}

		// ****************************************************************
		// * Import maturity_max_effency
		// ****************************************************************

		// ****************************************************************
		// * retrieve parametertype label
		// ****************************************************************

		parameterType = daoParameterType.get(Constant.PARAMETERTYPE_TYPE_MAX_EFF);

		// paramter type does not exist -> NO
		if (parameterType == null)
			// save parameter type into database
			daoParameterType.save(parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_MAX_EFF, Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME));

		// ****************************************************************
		// * retrieve maturity_max_effency
		// ****************************************************************

		currentSqliteTable = "maturity_max_eff";

		// build and execute query
		rs = sqlite.query("SELECT * FROM maturity_max_eff", null);

		// retrieve results
		while (rs.next()) {

			// ****************************************************************
			// * Insert data into simple parameter table for maturity_max_eff
			// ****************************************************************

			// ****************************************************************
			// * create instance
			// ****************************************************************
			parameter = new Parameter();
			parameter.setDescription("SML" + String.valueOf(rs.getInt(Constant.MATURITY_MAX_EFF_COL)));
			parameter.setType(parameterType);
			parameter.setValue(rs.getDouble(Constant.MATURITY_MAX_EFF_VALUE) * 100);

			// ****************************************************************
			// * add instance to list of parameters
			// ****************************************************************
			this.analysis.addAParameter(parameter);
		}

		// close result
		rs.close();

		// ****************************************************************
		// * Import maturity_IS
		// ****************************************************************

		// ****************************************************************
		// * retrieve parametertype label
		// ****************************************************************

		parameterType = daoParameterType.get(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE);

		// paramter type does not exist -> NO
		if (parameterType == null) {
			// save parameter type into database
			daoParameterType.save(parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE, Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME));
		}

		currentSqliteTable = "maturity_IS";

		// ****************************************************************
		// * retrieve maturity_IS
		// ****************************************************************

		// build and execute query
		rs = sqlite.query("SELECT * FROM maturity_IS", null);

		// retrieve results
		while (rs.next()) {

			// ****************************************************************
			// * Insert data into simple parameter table
			// ****************************************************************

			// ****************************************************************
			// * create instance
			// ****************************************************************
			parameter = new Parameter();

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

			parameter.setDescription(desc);
			parameter.setType(parameterType);
			parameter.setValue(rs.getDouble(Constant.MATURITY_IS_VALUE) * 100);

			// ****************************************************************
			// * add instance to list of parameters
			// ****************************************************************
			this.analysis.addAParameter(parameter);
		}

		// close result
		rs.close();

	}

	private void importDynamicParameters() throws Exception {

		// ****************************************************************
		// * Create parameter type for dynamic parameters
		// ****************************************************************

		// Retrieve parameter type if it exists
		ParameterType parameterType = daoParameterType.get(Constant.PARAMETERTYPE_TYPE_DYNAMIC);
		if (parameterType == null) {
			// It does not exist; create it
			parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_DYNAMIC, Constant.PARAMETERTYPE_TYPE_DYNAMIC_NAME);
			parameterType.setId(Constant.PARAMETERTYPE_TYPE_DYNAMIC);

			// Save parameter type into database
			daoParameterType.save(parameterType);
		}

		// Import dynamic parameters
		ResultSet rs = null;
		try {
			rs = sqlite.query("SELECT * FROM dynamic_parameter", null);
			while (rs.next()) {
				final DynamicParameter dynamicParameter = new DynamicParameter();
				dynamicParameter.setDescription(rs.getString(Constant.NAME_PARAMETER));
				dynamicParameter.setType(parameterType);
				dynamicParameter.setAcronym(rs.getString(Constant.ACRO_PARAMETER));
				dynamicParameter.setValue(rs.getDouble(Constant.VALUE_PARAMETER));
				this.analysis.addAParameter(dynamicParameter);
			}
		} catch (SQLException ex) {
			// Table does not exist, so we are dealing with an old database.
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	/**
	 * importExtendedParameters: <br>
	 * <ul>
	 * <li>Imports all Extended Parameters (Likelihood, Impact)</li>
	 * <li>Creates Objects for each Extended Parameter</li>
	 * <li>Adds the Objects to the "parameters" field List</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importExtendedParameters() throws Exception {

		System.out.println("Import Extended Parameters");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;
		ExtendedParameter extenededParameter = null;
		String query = "";
		Bounds parameterbounds = null;
		ParameterType parameterType = null;

		// ****************************************************************
		// * Import Impact
		// ****************************************************************

		// ****************************************************************
		// * retrieve parametertype
		// ****************************************************************
		parameterType = daoParameterType.get(Constant.PARAMETERTYPE_TYPE_IMPACT);

		// paramter type does not exist -> NO
		if (parameterType == null)
			// save parameter type into database
			daoParameterType.save(parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_IMPACT, Constant.PARAMETERTYPE_TYPE_IMPACT_NAME));

		// ****************************************************************
		// * retrieve impact values
		// ****************************************************************

		currentSqliteTable = "impact";

		// build query
		query = "SELECT * FROM impact";

		// execute query
		rs = sqlite.query(query, null);

		List<ExtendedParameter> extendedParameters = new ArrayList<ExtendedParameter>();

		// retrieve results
		while (rs.next()) {

			// ****************************************************************
			// * Insert data into extended parameter table
			// ****************************************************************

			// ****************************************************************
			// * create instance of extended parameter
			// ****************************************************************
			extenededParameter = new ExtendedParameter();
			extenededParameter.setDescription(rs.getString(Constant.NAME_IMPACT));
			extenededParameter.setType(parameterType);
			extenededParameter.setLevel(Integer.valueOf(rs.getString(Constant.SCALE_IMPACT)));
			extenededParameter.setAcronym(rs.getString(Constant.ACRO_IMPACT));
			extenededParameter.setValue(rs.getDouble(Constant.VALUE_IMPACT));
			parameterbounds = new Bounds(rs.getDouble(Constant.VALUE_FROM_IMPACT), rs.getDouble(Constant.VALUE_TO_IMPACT));
			extenededParameter.setBounds(parameterbounds);

			// ****************************************************************
			// * add instance to list of parameters
			// ****************************************************************
			extendedParameters.add(extenededParameter);
		}

		// close result
		rs.close();

		// ****************************************************************
		// * Import likelihood
		// ****************************************************************

		// ****************************************************************
		// * retrieve parameter type label
		// ****************************************************************

		ParameterManager.ComputeImpactValue(extendedParameters);

		this.analysis.getParameters().addAll(extendedParameters);

		this.extendedParameters = extendedParameters.stream().collect(Collectors.toMap(ExtendedParameter::getAcronym, Function.identity()));

		extendedParameters.clear();

		parameterType = daoParameterType.get(Constant.PARAMETERTYPE_TYPE_PROPABILITY);

		// paramter type does not exist -> NO
		if (parameterType == null)
			daoParameterType.save(parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_PROPABILITY, Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME));

		// ****************************************************************
		// * retrieve likelihood values
		// ****************************************************************

		currentSqliteTable = "potentiality";

		// build query
		query = "SELECT * FROM potentiality";

		// execute query
		rs = sqlite.query(query, null);

		// retrieve results
		while (rs.next()) {

			// ****************************************************************
			// * Insert data into extended parameter table
			// ****************************************************************

			// ****************************************************************
			// * create instance
			// ****************************************************************
			extenededParameter = new ExtendedParameter();
			extenededParameter.setDescription(rs.getString(Constant.NAME_POTENTIALITY));
			extenededParameter.setType(parameterType);
			extenededParameter.setLevel(Integer.valueOf(rs.getString(Constant.SCALE_POTENTIALITY)));
			extenededParameter.setAcronym(rs.getString(Constant.ACRO_POTENTIALITY));
			extenededParameter.setValue(rs.getDouble(Constant.VALUE_POTENTIALITY));
			parameterbounds = new Bounds(rs.getDouble(Constant.VALUE_FROM_POTENTIALITY), rs.getDouble(Constant.VALUE_TO_POTENTIALITY));
			extenededParameter.setBounds(parameterbounds);

			// ****************************************************************
			// * add instance to list of parameters
			// ****************************************************************
			extendedParameters.add(extenededParameter);
		}

		// close result
		rs.close();

		ParameterManager.ComputeImpactValue(extendedParameters);

		this.analysis.getParameters().addAll(extendedParameters);

		extendedParameters.forEach(parameter -> this.extendedParameters.put(parameter.getAcronym(), parameter));

		extendedParameters.clear();

		// recalculate parameter scales of impact and probability
		// this.analysis.computeParameterScales();
	}

	/**
	 * importMaturityParameters: <br>
	 * <ul>
	 * <li>Imports all Maturity Parameters</li>
	 * <li>Creates Objects for each Maturity Parameter</li>
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
		ParameterType parameterType = null;
		MaturityParameter maturityParameter = null;

		// ****************************************************************
		// * retrieve parameter type
		// ****************************************************************

		parameterType = daoParameterType.get(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_LEVEL_PER_SML);

		// paramter type does not exist -> NO
		if (parameterType == null)
			daoParameterType.save(
					parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_LEVEL_PER_SML, Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_LEVEL_PER_SML_NAME));

		// ****************************************************************
		// * import maturity parameters
		// ****************************************************************

		currentSqliteTable = "maturity_required_LIPS";

		// build query
		query = "SELECT * FROM maturity_required_LIPS";

		// execute query
		rs = sqlite.query(query, null);

		List<MaturityParameter> parameters = new ArrayList<MaturityParameter>();

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
				maturityParameter.setType(parameterType);
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

		for (Parameter parameter : parameters)
			this.analysis.addAParameter(parameter);

		// close result
		rs.close();
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

		// close result
		rs.close();

		// populate usedPhases list
		for (Phase phase2 : phases.values())
			this.analysis.addPhase(phase2);

		// order phases by phase number
		this.analysis.initialisePhases();
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
	 * importNormalMeasures: <br>
	 * <ul>
	 * <li>Imports all AnalysisStandard Measures (27001,27002,custom) except
	 * maturity</li>
	 * <li>Create Objects for each AnalysisStandard</li>
	 * <li>Create Objects for each Measure</li>
	 * <li>Create Objects for the Measure Phase</li>
	 * <li>Adds the Phase to the Measure Object</li>
	 * <li>Adds the Measure Objects to the their cosresponding AnalysisStandard
	 * (int the "standards" field)</li>
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
		analysisStandards = new HashMap<Standard, AnalysisStandard>();
		standards = new HashMap<String, Standard>();
		measures = new HashMap<String, Measure>();
		AnalysisStandard analysisStandard = null;
		Standard standard = null;
		String idNormalMeasure = "";
		String description = "";
		int standardVersion = 2005;
		boolean standardComputable = false;
		boolean measurecomputable = false;
		int phaseNumber = 0;
		String measureRefMeasure = "";
		MeasureDescription mesDesc = null;
		MeasureDescriptionText mesText = null;

		// ****************************************************************
		// * retrieve all measures
		// ****************************************************************

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

			idNormalMeasure = rs.getString(Constant.MEASURE_ID_NORM);

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

			standard = standards.get(idNormalMeasure + "_" + standardVersion);
			if (standard == null) {
				standard = daoStandard.getStandardByNameAndVersion(idNormalMeasure, standardVersion);
				// standard is not in database create new standard and save in
				// into
				// database for future
				if (standard == null) {
					standard = new Standard(idNormalMeasure, StandardType.getByName(rs.getString("norme_type")), standardVersion, description, standardComputable);
					standard.setAnalysisOnly(rs.getBoolean("norme_analysisOnly"));
					daoStandard.save(standard);
					// add standard to map
				} else if (standard.isAnalysisOnly()) {
					standard = standard.duplicate();
					standard.setVersion(daoStandard.getNextVersionByNameAndType(idNormalMeasure, standard.getType()));
					daoStandard.save(standard);
					// add standard to map
				}
				standards.put(idNormalMeasure + "_" + standardVersion, standard);
			}

			// retrieve analysisstandard of the standard
			analysisStandard = analysisStandards.get(standard);

			// standard is empty
			if (analysisStandard == null)

				// add standard to analysisstandards map as new analysis
				// standard

				if (standard.getType().equals(StandardType.NORMAL))
				analysisStandards.put(standard, analysisStandard = new NormalStandard(standard));
				else
				analysisStandards.put(standard, analysisStandard = new AssetStandard(standard));

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

			// if (phase.getAnalysis() == null) {
			// System.out.println(phase);
			// }

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

				// create link from measure description to measure description
				// text
				mesDesc.addMeasureDescriptionText(mesText);

				// fill measure description with data
				mesDesc.setStandard(analysisStandard.getStandard());
				mesDesc.setReference(measureRefMeasure);
				mesDesc.setLevel(rs.getInt(Constant.MEASURE_LEVEL));

				// fill measure description text with data
				// System.out.println(rs.getString(Constant.MEASURE_DOMAIN_MEASURE));
				mesText.setDomain(rs.getString(Constant.MEASURE_DOMAIN_MEASURE));
				mesText.setDescription(rs.getString(Constant.MEASURE_QUESTION_MEASURE));

				mesText.setLanguage(this.analysis.getLanguage());

				// save measure description to database
				daoMeasureDescription.save(mesDesc);

				// else: check if measure description text exists in the
				// language of the analysis ->
				// NO
			} else if (!daoMeasureDescriptionText.existsForMeasureDescriptionAndLanguage(mesDesc.getId(), this.analysis.getLanguage().getId())) {

				// System.out.println("Not found");

				// create new measure description text for this measure
				// description
				mesText = new MeasureDescriptionText();

				// create link from measure description to measure description
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
			Measure measure = null;

			if (standard.getType().equals(StandardType.NORMAL))
				measure = new NormalMeasure();
			else if (standard.getType().equals(StandardType.ASSET))
				measure = new AssetMeasure();

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
			measure.setStatus(rs.getString(Constant.MEASURE_STATUS));
			if (standard.getType().equals(StandardType.NORMAL))
				((NormalMeasure) measure).setToCheck(rs.getString(Constant.MEASURE_REVISION) == null ? "" : rs.getString(Constant.MEASURE_REVISION));
			else if (standard.getType().equals(StandardType.ASSET))
				((AssetMeasure) measure).setToCheck(rs.getString(Constant.MEASURE_REVISION));

			measure.setToDo(rs.getString(Constant.MEASURE_TODO));

			measure.setResponsible(getStringOrEmpty(rs, Constant.MEASURE_RESPONSIBLE));

			measure.getMeasureDescription().setComputable(measurecomputable);

			// calculate cost
			cost = Analysis.computeCost(this.analysis.getParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE), this.analysis.getParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE),
					this.analysis.getParameter(Constant.PARAMETER_LIFETIME_DEFAULT), measure.getInternalMaintenance(), measure.getExternalMaintenance(),
					measure.getRecurrentInvestment(), measure.getInternalWL(), measure.getExternalWL(), measure.getInvestment(), measure.getLifetime());

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
			measureProperties.setCategoryValue(Constant.CONFIDENTIALITY_RISK, rs.getInt(Constant.MEASURE_CONFIDENTIALITY));
			measureProperties.setCategoryValue(Constant.INTEGRITY_RISK, rs.getInt(Constant.MEASURE_INTEGRITY));
			measureProperties.setCategoryValue(Constant.AVAILABILITY_RISK, rs.getInt(Constant.MEASURE_AVAILABILITY));

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

			if (standard.getType().equals(StandardType.NORMAL))
				((NormalMeasure) measure).setMeasurePropertyList(measureProperties);
			else if (standard.getType().equals(StandardType.ASSET))
				((AssetMeasure) measure).setMeasurePropertyList(measureProperties);

			// ****************************************************************
			// * add measure to standard
			// ****************************************************************

			if (standard.getType().equals(StandardType.NORMAL))
				((NormalStandard) analysisStandard).addMeasure((NormalMeasure) measure);
			else if (standard.getType().equals(StandardType.ASSET))
				((AssetStandard) analysisStandard).addMeasure((AssetMeasure) measure);

			// add measure to standard

			// add measure to map
			measures.put(idNormalMeasure + "_" + standardVersion + "_" + measureRefMeasure, measure);
		}
		// close result
		rs.close();
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

		System.out.println("Import Asset Type Values ");

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
				String query = "";
				AssetTypeValue assetTypeValue = null;
				AssetType assetType = null;

				// ****************************************************************
				// * retrieve asset type values for measures
				// ****************************************************************

				currentSqliteTable = "spec_type_asset_measure";

				// build query
				query = "SELECT id_type_asset, value_spec FROM spec_type_asset_measure where id_norme=? and version_norme=? and ref_measure=?";

				List<Object> params = new ArrayList<Object>();

				params.add(normalMeasure.getAnalysisStandard().getStandard().getLabel());

				params.add(normalMeasure.getAnalysisStandard().getStandard().getVersion());

				params.add(normalMeasure.getMeasureDescription().getReference());

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
				// close result
				rs.close();
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
				String query = "";
				MeasureAssetValue assetValue = null;
				Asset asset = null;

				// ****************************************************************
				// * retrieve asset type values for measures
				// ****************************************************************

				currentSqliteTable = "spec_asset_measure";

				// build query
				query = "SELECT id_asset, value_spec FROM spec_asset_measure where id_norme = ? and version_norme = ? and ref_measure = ?";

				List<Object> params = new ArrayList<Object>();

				params.add(assetMeasure.getMeasureDescription().getStandard().getLabel());
				params.add(assetMeasure.getMeasureDescription().getStandard().getVersion());
				params.add(assetMeasure.getMeasureDescription().getReference());

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
				// close result
				rs.close();
			}
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
		String[] extendedScopes = new String[] { "financialParameters", "riskEvaluationCriteria", "impactCriteria", "riskAcceptanceCriteria" };
		int numColumns = 0;
		setCurrentSqliteTable("scope");

		// ****************************************************************
		// * Import data from scope
		// ****************************************************************

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
					// * As "dtLabel" of item information use the column name
					// ****************************************************************

					// ****************************************************************
					// * create instance
					// ****************************************************************

					// ****************************************************************
					// * add instance to list of item information
					// ****************************************************************
					this.analysis.addAnItemInformation(new ItemInformation(rsMetaData.getColumnName(i), Constant.ITEMINFORMATION_SCOPE, rs.getString(rsMetaData.getColumnName(i))));
				}
			}
		}

		// close result
		rs.close();

		// Add missing scope
		for (String scopeName : extendedScopes) {
			if (!this.analysis.getItemInformations().stream().anyMatch(itemInformation -> itemInformation.getDescription().equals(scopeName)))
				this.analysis.addAnItemInformation(new ItemInformation(scopeName, Constant.ITEMINFORMATION_SCOPE, ""));
		}

		// ****************************************************************
		// * Import data from organisation
		// ****************************************************************

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
				// DatabaseHandler.generateInsertQuery("ItemInformation", 7);

				// ****************************************************************
				// * create instance
				// ****************************************************************
				// ****************************************************************
				// * add instance to list of item information
				// ****************************************************************
				this.analysis
						.addAnItemInformation(new ItemInformation(rsMetaData.getColumnName(i), Constant.ITEMINFORMATION_ORGANISATION, rs.getString(rsMetaData.getColumnName(i))));
			}
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
		String status = "";
		Standard standard = null;
		MeasureDescription mesDesc = null;
		MeasureDescriptionText mesText = null;
		Parameter implementationRateParameter = null;
		double implementationRate = 0;
		MaturityMeasure maturityMeasure = null;
		Integer standardVersion = 2005;
		boolean standardComputable = true;
		boolean measurecomputable = false;
		String description = "Old Maturity measure to be used with the 2005 verison of 27001 ISO standard.";

		// ****************************************************************
		// * load each maturity
		// ****************************************************************

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
				standard = daoStandard.getStandardByNameAndVersion(Constant.STANDARD_MATURITY, standardVersion);
				if (standard == null) {
					// standard is not in database create new standard and save
					// in into
					// database for future
					standard = new Standard(Constant.STANDARD_MATURITY, StandardType.MATURITY, standardVersion, description, standardComputable);
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
			// * check phases of this measure and add it if it does not exist
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

				// create link from measure description to measure description
				// text
				mesDesc.addMeasureDescriptionText(mesText);

				// fill measure description data
				mesDesc.setStandard(analysisStandard.getStandard());
				mesDesc.setReference(chapter);
				mesDesc.setLevel(rs.getInt(Constant.MEASURE_LEVEL));

				// fill measure description text
				mesText.setDomain(rs.getString(Constant.MATURITY_DOMAIN).replace("'", "''"));
				mesText.setDescription(Constant.EMPTY_STRING);
				mesText.setLanguage(this.analysis.getLanguage());

				// else: measure description exist: measure description text
				// exists in the language
				// of the analysis -> NO
			} else if (!daoMeasureDescriptionText.existsForMeasureDescriptionAndLanguage(mesDesc.getId(), this.analysis.getLanguage().getId())) {

				// create new measure description text
				mesText = new MeasureDescriptionText();

				// create link from measure description to measure description
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
			if (rs.getInt(Constant.MEASURE_LEVEL) == Constant.MEASURE_LEVEL_1) {

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

					analysis.addPhase(tempPhase);
				}

			}

			// ****************************************************************
			// * Insert measure into maturitymeasure table
			// ****************************************************************

			// add parameters
			status = rs.getString(Constant.MEASURE_STATUS);

			// set status and by default not applicable (NA)
			if ((!status.equals(Constant.MEASURE_STATUS_APPLICABLE)) && (!status.equals(Constant.MEASURE_STATUS_MANDATORY))
					&& (!status.equals(Constant.MEASURE_STATUS_NOT_APPLICABLE))) {

				// set default status
				status = Constant.MEASURE_STATUS_NOT_APPLICABLE;
			}
			// ****************************************************************
			// * calculate cost
			// ****************************************************************

			// check if status is not NA -> YES
			if ((rs.getString(Constant.MEASURE_STATUS).replace("'", "''").equals(Constant.MEASURE_STATUS_APPLICABLE))
					|| (rs.getString(Constant.MEASURE_STATUS).replace("'", "''").equals(Constant.MEASURE_STATUS_MANDATORY))) {

				// calculate cost
				cost = Analysis.computeCost(this.analysis.getParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE), this.analysis.getParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE),
						this.analysis.getParameter(Constant.PARAMETER_LIFETIME_DEFAULT), rs.getInt("internal_maintenance"), rs.getInt("external_maintenance"),
						rs.getInt("recurrent_investment"), rs.getInt(Constant.MATURITY_INTWL), rs.getInt(Constant.MATURITY_EXTWL), rs.getInt(Constant.MATURITY_INVESTMENT),
						rs.getInt(Constant.MEASURE_LIFETIME));
			} else {

				// check if status is not NA -> NO

				// set cost to 0
				cost = 0;
			}

			// ****************************************************************
			// * create instance
			// ****************************************************************

			// ****************************************************************
			// * create parameter for implementation rate
			// ****************************************************************
			implementationRate = rs.getDouble(Constant.MATURITY_RATE) * 100;

			// System.out.println(implementationRate);

			// parse implmentation rate parameters
			for (Parameter parameter : analysis.getParameters()) {

				// System.out.println(parameter.getType().getLabel());

				// find implementation rate parameter and wanted value
				if (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME)) {
					if (parameter.getValue() == implementationRate) {
						// retrieve object
						implementationRateParameter = parameter;
						break;
					}
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
			measures.put(analysisStandard.getStandard().getLabel() + "_" + analysisStandard.getStandard().getVersion() + "_" + chapter, maturityMeasure);
		}

		// close result
		rs.close();

		// add analysis standards from map to the analysis
		for (AnalysisStandard analysisStandard2 : analysisStandards.values())
			analysis.addAnalysisStandard(analysisStandard2);
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

		// close results
		rs.close();
	}

	/**
	 * setAllCriteriaCategories: <br>
	 * Adds all Scenario Categories to the given scenario or measure object.
	 * <br>
	 * Scenario categories are: <br>
	 * <ul>
	 * <li>Direct: d1, d2, d3, d4, d5, d6, d6.1, d6.2, d6.3, d6.4, d7</li>
	 * <li>Indirect: i1, i2, i3, i4, i5, i6, i7, i8, i8.1, i8.2, i8.3, i8.4, i9,
	 * i10</li>
	 * </ul>
	 * 
	 * @param criteria
	 *            The scenario or measure object to set the categories with
	 *            values
	 * @param resultSet
	 *            The ResultSet from where the categorie values come from
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

	/**
	 * setScenarioAssetValues: <br>
	 * Sets all Asset Type Values for a given Scenario using a SQL Result.
	 * 
	 * @param scenario
	 *            The Scenario Object to set the Asset Type Values
	 * @param rs
	 *            The SQL Result to take values from
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
			scenario.addAssetTypeValue(new AssetTypeValue(assetType, rs.getInt(key)));
		}
	}

	/**
	 * convertImpactToDouble: <br>
	 * Takes a string value (value from SQLite file) and converts it into a
	 * valid double value.
	 * 
	 * @param impact
	 *            The impact value as string
	 * @return A valid Double value
	 */
	private double convertImpactToDouble(String impact) {
		// ****************************************************************
		// * Initialise variables
		// ****************************************************************
		// ****************************************************************
		// * pattern matches a acronym -> YES
		// ****************************************************************
		try {
			if (extendedParameters.containsKey(impact))
				return extendedParameters.get(impact).getValue();
			return Double.parseDouble(impact);
		} catch (NumberFormatException | NullPointerException e) {
			return 0;
		}
	}

	public void setAnalysis(Analysis analysis2) {
		this.analysis = analysis2;
	}

	public void setDatabaseHandler(DatabaseHandler sqlite2) {
		this.sqlite = sqlite2;

	}

	/**
	 * @return the idTask
	 */
	public String getIdTask() {
		return idTask;
	}

	/**
	 * @param idTask
	 *            the idTask to set
	 */
	public void setIdTask(String idTask) {
		this.idTask = idTask;
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
	}

	public static String getString(ResultSet rs, String name) {
		return getString(rs, name, null);
	}

	public static String getStringOrEmpty(ResultSet rs, String name) {
		return getString(rs, name, "");
	}

	public static String getString(ResultSet rs, String name, String defaultValue) {
		try {
			return rs.getString(name);
		} catch (SQLException e) {
			return defaultValue;
		}
	}

	public static double getDouble(ResultSet rs, String name) {
		try {
			return rs.getDouble(name);
		} catch (SQLException e) {
			return 0;
		}
	}

	public static int getInt(ResultSet rs, String name) {
		try {
			return rs.getInt(name);
		} catch (SQLException e) {
			return 0;
		}
	}

	public static boolean getBoolean(ResultSet rs, String name) {
		try {
			return rs.getBoolean(name);
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * @param serviceTaskFeedback
	 *            the serviceTaskFeedback to set
	 */
	public ServiceTaskFeedback getServiceTaskFeedback() {
		return this.serviceTaskFeedback;
	}

	/**
	 * @param serviceTaskFeedback
	 *            the serviceTaskFeedback to set
	 */
	public void setServiceTaskFeedback(ServiceTaskFeedback serviceTaskFeedback) {
		this.serviceTaskFeedback = serviceTaskFeedback;
	}

	/**
	 * @param daoParameterType
	 *            the daoParameterType to set
	 */
	public void setDaoParameterType(DAOParameterType daoParameterType) {
		this.daoParameterType = daoParameterType;
	}

	/**
	 * @param daoAssetType
	 *            the daoAssetType to set
	 */
	public void setDaoAssetType(DAOAssetType daoAssetType) {
		this.daoAssetType = daoAssetType;
	}

	/**
	 * @param daoAnalysis
	 *            the daoAnalysis to set
	 */
	public void setDaoAnalysis(DAOAnalysis daoAnalysis) {
		this.daoAnalysis = daoAnalysis;
	}

	/**
	 * @param daoLanguage
	 *            the daoLanguage to set
	 */
	public void setDaoLanguage(DAOLanguage daoLanguage) {
		this.daoLanguage = daoLanguage;
	}

	/**
	 * @param daoMeasureDescription
	 *            the daoMeasureDescription to set
	 */
	public void setDaoMeasureDescription(DAOMeasureDescription daoMeasureDescription) {
		this.daoMeasureDescription = daoMeasureDescription;
	}

	/**
	 * @param daoMeasureDescriptionText
	 *            the daoMeasureDescriptionText to set
	 */
	public void setDaoMeasureDescriptionText(DAOMeasureDescriptionText daoMeasureDescriptionText) {
		this.daoMeasureDescriptionText = daoMeasureDescriptionText;
	}

	/**
	 * @param daoStandard
	 *            the daoStandard to set
	 */
	public void setDaoStandard(DAOStandard daoStandard) {
		this.daoStandard = daoStandard;
	}

	public DAOUserAnalysisRight getDaoUserAnalysisRight() {
		return daoUserAnalysisRight;
	}

	public void setDaoUserAnalysisRight(DAOUserAnalysisRight daoUserAnalysisRight) {
		this.daoUserAnalysisRight = daoUserAnalysisRight;
	}

	/**
	 * @return the currentSqliteTable
	 */
	public String getCurrentSqliteTable() {
		return currentSqliteTable;
	}

	/**
	 * @param currentSqliteTable
	 *            the currentSqliteTable to set
	 */
	public void setCurrentSqliteTable(String currentSqliteTable) {
		this.currentSqliteTable = currentSqliteTable;
	}

	public Session getSession() {
		return session;
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

}
