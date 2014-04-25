package lu.itrust.business.TS.importation;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.TS.Bounds;
import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.History;
import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MaturityMeasure;
import lu.itrust.business.TS.MaturityNorm;
import lu.itrust.business.TS.MaturityParameter;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.TS.MeasureNorm;
import lu.itrust.business.TS.MeasureProperties;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.NormMeasure;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.ParameterType;
import lu.itrust.business.TS.Phase;
import lu.itrust.business.TS.RiskInformation;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.ScenarioType;
import lu.itrust.business.TS.SecurityCriteria;
import lu.itrust.business.TS.UserAnalysisRight;
import lu.itrust.business.TS.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.dbhandler.DatabaseHandler;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.helper.AsyncCallback;
import lu.itrust.business.dao.DAOAnalysis;
import lu.itrust.business.dao.DAOAssetType;
import lu.itrust.business.dao.DAOLanguage;
import lu.itrust.business.dao.DAOMeasureDescription;
import lu.itrust.business.dao.DAOMeasureDescriptionText;
import lu.itrust.business.dao.DAONorm;
import lu.itrust.business.dao.DAOParameterType;
import lu.itrust.business.dao.DAOScenarioType;
import lu.itrust.business.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.dao.hbm.DAOAssetTypeHBM;
import lu.itrust.business.dao.hbm.DAOLanguageHBM;
import lu.itrust.business.dao.hbm.DAOMeasureDescriptionHBM;
import lu.itrust.business.dao.hbm.DAOMeasureDescriptionTextHBM;
import lu.itrust.business.dao.hbm.DAONormHBM;
import lu.itrust.business.dao.hbm.DAOParameterTypeHBM;
import lu.itrust.business.dao.hbm.DAOScenarioTypeHBM;
import lu.itrust.business.service.ServiceTaskFeedback;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * ImportAnalysis: <br>
 * This class is for importing an Analysis from an TRICK Light SQLite file into Java objects.
 * 
 * @author itrust consulting s.��� r.l. - BJA,SME
 * @version 0.1
 * @since 2012-12-14
 */
public class ImportAnalysis {

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	private long idTask;

	private DAOParameterType daoParameterType;

	private DAOAssetType daoAssetType;

	private DAOScenarioType daoScenarioType;

	private DAOAnalysis daoAnalysis;

	private DAOLanguage daoLanguage;

	private DAOMeasureDescription daoMeasureDescription;

	private DAOMeasureDescriptionText daoMeasureDescriptionText;

	private DAONorm daoNorm;

	private ServiceTaskFeedback serviceTaskFeedback;

	private SessionFactory sessionFactory;

	private String currentSqliteTable = "";

	/** The Analysis Object */
	private Analysis analysis = null;

	/** The SQLite Database Handler */
	private DatabaseHandler sqlite = null;

	/** Map of Assets */
	private Map<Integer, Asset> assets = null;

	/** Map of Scenarios */
	private Map<Integer, Scenario> scenarios = null;

	/** Map of Phases */
	private Map<Integer, Phase> phases = null;

	/** Map of AnalysisNorms */
	private Map<Norm, AnalysisNorm> analysisNorms = null;

	/** Map of Norms */
	private Map<String, Norm> norms = null;

	/** Map of Measures */
	private Map<String, Measure> measures = null;

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

	/**
	 * getAnalysis: <br>
	 * Returns the analysis of the object
	 * 
	 * @return The analysis object
	 */
	public Analysis getAnalysis() {
		return this.analysis;
	}

	/***********************************************************************************************
	 * Methods
	 **********************************************************************************************/

	/**
	 * ImportAnAnalysis: <br>
	 * Method used to import and given analysis using an sqlite file into the mysql database.
	 * 
	 * @throws Exception
	 */
	@Transactional
	public void ImportAnAnalysis() throws Exception {

		Session session = null;

		try {

			if (sessionFactory != null) {
				session = sessionFactory.openSession();
				initialiseDAO(session);
				session.getTransaction().begin();
			}

			System.out.println("Importing...");

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.importing", "Importing", 0));

			// ****************************************************************
			// * create analysis id, analysis label, analysis language and
			// * Histories. Creates Analysis Entries into the Database
			// ****************************************************************
			importAnalyses();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.risk_information.importing", "Importing risk information", 1));

			// ****************************************************************
			// * import risk information
			// ****************************************************************
			importRiskInformation();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.risk_information.importing", "Import item information", 5));

			// ****************************************************************
			// * import item information
			// ****************************************************************
			importItemInformation();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.simple_parameters.importing", "Import simple parameters", 10));

			// ****************************************************************
			// * import simple parameters
			// ****************************************************************
			importSimpleParameters();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.extended_parameters.importing", "Import extended parameters", 15));

			// ****************************************************************
			// * import extended parameters
			// ****************************************************************
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

			serviceTaskFeedback.send(idTask, new MessageHandler("info.scenario.importing", "Import scenarios", 40));
			importScenarios();

			// ****************************************************************
			// * import assessments
			// ****************************************************************

			serviceTaskFeedback.send(idTask, new MessageHandler("info.assessments.importing", "Import assessments", 50));
			importAssessments();

			// ****************************************************************
			// * import phases
			// ****************************************************************

			serviceTaskFeedback.send(idTask, new MessageHandler("info.phase.importing", "Import phases", 55));
			importPhases();

			// ****************************************************************
			// * import AnalysisNorm measures
			// ****************************************************************
			serviceTaskFeedback.send(idTask, new MessageHandler("info.norm_measures.importing", "Analysis norm measures", 60));
			importNormMeasures();

			// ****************************************************************
			// * import asset type values
			// ****************************************************************
			serviceTaskFeedback.send(idTask, new MessageHandler("info.asset_type_value.importing", "Import asset type values", 70));
			importAssetTypeValues();

			// ****************************************************************
			// * import maturity measures
			// ****************************************************************

			serviceTaskFeedback.send(idTask, new MessageHandler("info.maturity_measure.importing", "Import maturity measures", 80));
			importMaturityMeasures();

			// System.out.println("Saving Data to Database...");

			serviceTaskFeedback.send(idTask, new MessageHandler("import.saving.analysis", "Saving Data to Database", 90));

			AssessmentManager asm = new AssessmentManager();

			System.out.println("Saving Analysis Data...");
			
			// save or update analysis
			daoAnalysis.save(this.analysis);

			// update ALE of asset objects
			asm.UpdateAssessment(this.analysis);

			daoAnalysis.saveOrUpdate(this.analysis);
			
			if (session != null)
				session.getTransaction().commit();

			MessageHandler messageHandler = new MessageHandler("success.analysis.import", "Import Done!", 100);
			
			messageHandler.setAsyncCallback(new AsyncCallback("window.location.assign(\"../Analysis\")", null));
			
			serviceTaskFeedback.send(idTask, messageHandler);
			
			System.out.println("Import Done!");
		} catch (SQLException e) {

			try {

				int index = e.getMessage().indexOf("no such column:");
				if (index != -1) {
					int index2 = e.getMessage().lastIndexOf(")");
					index += "no such column: ".length();
					String column = e.getMessage().substring(index, index2);
					serviceTaskFeedback.send(idTask, new MessageHandler("error.colums.not_found", new String[] { this.currentSqliteTable, column }, "Please check table '"
						+ this.currentSqliteTable + "', column '" + column + "'", e));
				} else {
					serviceTaskFeedback.send(idTask, new MessageHandler(e.getMessage(), e.getMessage(), e));
				}
			} catch (Exception ed) {
				ed.printStackTrace();
			}

			e.printStackTrace();
			throw e;

		} catch (Exception e) {
			serviceTaskFeedback.send(idTask, new MessageHandler(e.getMessage(), e.getMessage(), e));
			e.printStackTrace();
			throw e;
		} finally {
			// clear maps
			clearData();
			if (session != null)
				session.close();
		}
	}

	/**
	 * clearData: <br>
	 * Clear maps
	 */
	private void clearData() {
		if (analysisNorms != null)
			analysisNorms.clear();
		if (assets != null)
			assets.clear();
		if (measures != null)
			measures.clear();
		if (norms != null)
			norms.clear();
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
	 * <li>Sets the Language Object of the Analysis and set the "language" field</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importAnalyses() throws Exception {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;
		String acroLanguage = "";
		String query = "";
		Language language = null;

		setCurrentSqliteTable("identifier");

		// ****************************************************************
		// * Retrieve analysis ID and label
		// ****************************************************************

		// build query
		query = "SELECT id_analysis,label from identifier LIMIT 1";

		// execute query
		rs = sqlite.query(query, null);

		// retrieve results
		if (rs.next()) {

			// ****************************************************************
			// * set analysis ID
			// ****************************************************************
			this.analysis.setIdentifier(rs.getString(Constant.IDENTIFIER_ID));

			// ****************************************************************
			// * set analysis label
			// ****************************************************************
			this.analysis.setLabel(rs.getString(Constant.IDENTIFIER_LABEL));
		}

		// close result
		rs.close();

		this.analysis.setCreationDate(new Timestamp(System.currentTimeMillis()));

		// ****************************************************************
		// * Retireve language
		// ****************************************************************

		// extract language from analysisid
		acroLanguage = this.analysis.getIdentifier().substring(0, 3);

		// retrieve language from acronym
		language = daoLanguage.loadFromAlpha3(acroLanguage);

		// if language is not found, create the english language object and save
		// it to the database
		if (language == null) {

			// ****************************************************************
			// * create language object
			// ****************************************************************
			language = new Language();
			language.setAlpha3(acroLanguage);
			language.setName(acroLanguage);
			language.setAltName(acroLanguage);
			daoLanguage.save(language);
		}

		// ****************************************************************
		// * add the language to the object variable
		// ****************************************************************
		this.analysis.setLanguage(language);

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

		// initialise analysis version to the last history version
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
			if (!daoAnalysis.analysisExist(this.analysis.getIdentifier(), history.getVersion())) {

				// ****************************************************************
				// * store analysis with history entries to the current version
				// into database
				// ****************************************************************

				// create new analysis object
				analysis = new Analysis();

				// set data for analyses
				analysis.setIdentifier(this.analysis.getIdentifier());
				analysis.setVersion(history.getVersion());
				analysis.setCreationDate(this.analysis.getCreationDate());
				analysis.setLabel(this.analysis.getLabel());
				analysis.setLanguage(this.analysis.getLanguage());
				analysis.setCustomer(this.analysis.getCustomer());
				analysis.setOwner(this.analysis.getOwner());
				analysis.addUserRight(new UserAnalysisRight(this.analysis.getOwner(), analysis, AnalysisRight.ALL));
				if (i == 0) {
					analysis.setBasedOnAnalysis(null);
				} else {
					analysis.setBasedOnAnalysis(daoAnalysis.getFromIdentifierVersion(this.analysis.getIdentifier(), this.analysis.getAHistory(i - 1).getVersion()));
				}

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
		analysis = daoAnalysis.getFromIdentifierVersion(this.analysis.getIdentifier(), this.analysis.getVersion());

		// if analysis is not null (The Analysis and its version has aready been
		// imported)
		if (analysis != null) {

			// check if the analysis has already data -> YES
			if (!analysis.hasData()) {

				// fill the analysis with data and change flag of hasData analysis
				System.out.println("Your file has already been imported without data");
				this.analysis = analysis;
				this.analysis.setData(true);
			} else {

				// analysis had already been imported and has data
				throw new IllegalArgumentException("Your file has already been imported, whether it is a new version, do not forget to increase version");
			}
		}

		if (history == null) {
			this.analysis.setBasedOnAnalysis(null);
		} else {
			this.analysis.setBasedOnAnalysis(daoAnalysis.getFromIdentifierVersion(this.analysis.getIdentifier(), history.getVersion()));
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
		Map<Integer, AssetType> assetTypes = new HashMap<Integer, AssetType>();
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
			assetType = daoAssetType.get(typename);

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
		Map<Integer, ScenarioType> scenarioTypes = new HashMap<Integer, ScenarioType>();
		scenarios = new HashMap<Integer, Scenario>();
		String type = null;
		ScenarioType scenarioType = null;
		Scenario tempScenario = null;

		// ****************************************************************
		// * Query sqlite for all scenario types
		// ****************************************************************

		// build query
		query = "SELECT * FROM threat_types order by id_type_threat";

		// execute query
		rs = sqlite.query(query, null);

		// Loop scenario types
		while (rs.next()) {

			// ****************************************************************
			// * Insert data into scenario type table
			// ****************************************************************
			type = rs.getString(Constant.THREAT_TYPE_LABEL);

			// retrieve scenario type from database
			scenarioType = daoScenarioType.get(type);

			// check if type does not exist
			if (scenarioType == null) {

				// create new scenario type
				scenarioType = new ScenarioType(type);

				// save scenario type to database
				daoScenarioType.save(scenarioType);
			}

			// add scneario type to map
			scenarioTypes.put(rs.getInt(Constant.THREAT_ID_TYPE_THREAT), scenarioType);
		}

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
			tempScenario.setScenarioType(scenarioTypes.get(rs.getInt(Constant.THREAT_ID_TYPE_THREAT)));

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

			for (int i = 0; i < this.analysis.getParameters().size(); i++) {
				if (this.analysis.getAParameter(i).getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME)) {
					if (((ExtendedParameter) this.analysis.getAParameter(i)).getAcronym().equals(parameterName)) {
						likelihoodValue = this.analysis.getAParameter(i).getValue();
						break;
					}
				}
			}

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
		query = "SELECT internal_setup_rate, external_setup_rate, lifetime_default, ";
		query += "maintenance_default, tuning, soaThreshold, mandatoryPhase, importanceThreshold FROM scope";

		// execute query
		rs = sqlite.query(query, null);

		// ****************************************************************
		// * retrieve parameter type for the instance
		// ****************************************************************

		// retrieve parameter type
		parameterType = daoParameterType.get(Constant.PARAMETERTYPE_TYPE_SINGLE);

		// paramter type does not exist -> NO
		if (parameterType == null) {

			// create new parameter type single

			parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME);

			parameterType.setId(Constant.PARAMETERTYPE_TYPE_SINGLE);

			// save parameter type into database
			daoParameterType.saveOrUpdate(parameterType);
		}

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
			// * Insert default maintenance into simple parameter table
			// ****************************************************************

			// ****************************************************************
			// // * create
			// * instance of default maintenance //
			// *
			// ****************************************************************
			parameter = new Parameter();
			parameter.setDescription(Constant.PARAMETER_MAINTENANCE_DEFAULT);
			parameter.setType(parameterType);
			parameter.setValue(rs.getInt(Constant.PARAMETER_MAINTENANCE_DEFAULT));

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
			parameter.setDescription(Constant.PARAMETER_TUNING);
			parameter.setType(parameterType);
			parameter.setValue(rs.getInt(Constant.PARAMETER_TUNING));
			/*
			 * // **************************************************************** // * add instance
			 * to list of parameters //
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
			 * // **************************************************************** // * add instance
			 * to list of parameters //
			 * ****************************************************************
			 */
			parameter = new Parameter(parameterType, Constant.IMPORTANCE_THRESHOLD, rs.getDouble(Constant.IMPORTANCE_THRESHOLD));
			this.analysis.addAParameter(parameter);
		}
		// close result
		rs.close();

		// ****************************************************************
		// * Import maturity_max_effency
		// ****************************************************************

		// ****************************************************************
		// * retrieve parametertype label
		// ****************************************************************

		parameterType = daoParameterType.get(Constant.PARAMETERTYPE_TYPE_MAX_EFF);

		// paramter type does not exist -> NO
		if (parameterType == null) {

			// create new parameter type
			parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME);

			parameterType.setId(Constant.PARAMETERTYPE_TYPE_MAX_EFF);

			// save parameter type into database
			daoParameterType.saveOrUpdate(parameterType);
		}

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

			// create new parameter type
			parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME);

			parameterType.setId(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE);

			// save parameter type into database
			daoParameterType.save(parameterType);
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
			
			switch(rs.getInt(Constant.MATURITY_IS_LINE)){
				case 1:desc=Constant.IS_NOT_ACHIEVED;
				case 2:desc=Constant.IS_RUDIMENTARY_ACHIEVED;
				case 3:desc=Constant.IS_PARTIALLY_ACHIEVED;
				case 4:desc=Constant.IS_LARGELY_ACHIEVED;
				case 5:desc=Constant.IS_FULLY_ACHIEVED;
				default:desc="ImpScale"+String.valueOf(rs.getInt(Constant.MATURITY_IS_LINE));
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
		if (parameterType == null) {

			// create new parameter type
			parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME);

			parameterType.setId(Constant.PARAMETERTYPE_TYPE_IMPACT);

			// save parameter type into database
			daoParameterType.saveOrUpdate(parameterType);
		}

		// ****************************************************************
		// * retrieve impact values
		// ****************************************************************

		currentSqliteTable = "impact";

		// build query
		query = "SELECT * FROM impact";

		// execute query
		rs = sqlite.query(query, null);

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
			this.analysis.addAParameter(extenededParameter);
		}

		// close result
		rs.close();

		// ****************************************************************
		// * Import likelihood
		// ****************************************************************

		// ****************************************************************
		// * retrieve parameter type label
		// ****************************************************************

		parameterType = daoParameterType.get(Constant.PARAMETERTYPE_TYPE_PROPABILITY);

		// paramter type does not exist -> NO
		if (parameterType == null) {

			// create new parameter type
			parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME);

			parameterType.setId(Constant.PARAMETERTYPE_TYPE_PROPABILITY);

			// save parameter type into database
			daoParameterType.save(parameterType);
		}

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
			this.analysis.addAParameter(extenededParameter);
		}

		// close result
		rs.close();

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
		if (parameterType == null) {

			// create new parameter type
			parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_LEVEL_PER_SML_NAME);

			parameterType.setId(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_LEVEL_PER_SML);

			// save parameter type into database
			daoParameterType.save(parameterType);
		}

		// ****************************************************************
		// * import maturity parameters
		// ****************************************************************

		currentSqliteTable = "maturity_required_LIPS";

		// build query
		query = "SELECT * FROM maturity_required_LIPS";

		// execute query
		rs = sqlite.query(query, null);

		// retrieve the name, and with the name find out the category
		while (rs.next()) {

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

			// ****************************************************************
			// * create instance
			// ****************************************************************
			maturityParameter = new MaturityParameter();
			maturityParameter.setCategory(cat);
			maturityParameter.setDescription(label);
			maturityParameter.setType(parameterType);
			maturityParameter.setSMLLevel(rs.getInt(Constant.MATURITY_REQUIRED_LIPS_SML));
			maturityParameter.setValue(rs.getDouble(Constant.MATURITY_REQUIRED_LIPS_VALUE));

			// ****************************************************************
			// * add instance to list of parameters
			// ****************************************************************
			this.analysis.addAParameter(maturityParameter);
		}

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
		// * create phase 0
		// ****************************************************************

		phase = new Phase();
		phase.setNumber(0);
		phase.setAnalysis(this.analysis);
		phase.setBeginDate(null);
		phase.setEndDate(null);

		// add phase 0 to the map
		phases.put(0, phase);

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

		// ****************************************************************
		// * retrieve maturity phases from maturity_phase table
		// ****************************************************************

		currentSqliteTable = "maturity_phase";
		// build and execute query
		query = "SELECT DISTINCT m.phase AS MeasurePhase, ma.phase AS MaturityPhase FROM measures m, maturity_phase ma";

		rs = sqlite.query(query, null);

		// retrieve results
		while (rs.next()) {

			// retrieve each maturtiy and measure phse
			int measurePhase = rs.getInt("MeasurePhase");

			int maturityPhase = rs.getInt("MaturityPhase");

			// check if measure phase exists -> NO -> Add new phase with number
			if (!phases.containsKey(measurePhase))
				phases.put(measurePhase, new Phase(measurePhase));

			// check if maturity phase exists -> NO -> Add new phase with number
			if (!phases.containsKey(maturityPhase))
				phases.put(maturityPhase, new Phase(maturityPhase));
		}

		// close result
		rs.close();

		// populate usedPhases list
		for (Phase phase2 : phases.values())
			this.analysis.addUsedPhase(phase2);

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
			rs.findColumn(Constant.MEASURE_VERSION_NORM);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * importNormMeasures: <br>
	 * <ul>
	 * <li>Imports all AnalysisNorm Measures (27001,27002,custom) except maturity</li>
	 * <li>Create Objects for each AnalysisNorm</li>
	 * <li>Create Objects for each Measure</li>
	 * <li>Create Objects for the Measure Phase</li>
	 * <li>Adds the Phase to the Measure Object</li>
	 * <li>Adds the Measure Objects to the their cosresponding AnalysisNorm (int the "norms" field)</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	private void importNormMeasures() throws Exception {

		System.out.println("Import Measures");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;
		double cost = 0;
		Phase phase = null;
		String query = "";
		analysisNorms = new HashMap<Norm, AnalysisNorm>();
		norms = new HashMap<String, Norm>();
		measures = new HashMap<String, Measure>();
		AnalysisNorm analysisNorm = null;
		Norm norm = null;
		String idMeasureNorm = "";
		String description = "";
		int normversion = 2005;
		boolean normcomputable = false;
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
			// * parse norms to find norm of measure
			// ****************************************************************

			// initialise norm variable

			idMeasureNorm = rs.getString(Constant.MEASURE_ID_NORM);

			if (columnExists(rs, Constant.MEASURE_VERSION_NORM)) {
				normversion = rs.getInt(Constant.MEASURE_VERSION_NORM);
				normcomputable = rs.getBoolean(Constant.MEASURE_NORM_COMPUTABLE);
				measurecomputable = rs.getBoolean(Constant.MEASURE_MEASURE_COMPUTABLE);
				description = rs.getString(Constant.MEASURE_NORM_DESCRIPTION);
			} else {
				normversion = 2005;
				normcomputable = true;
				description = "old norm (before 2013)";
				if (rs.getInt(Constant.MEASURE_LEVEL) == Constant.MEASURE_LEVEL_3) {
					measurecomputable = true;
				} else {
					measurecomputable = false;
				}
			}

			norm = daoNorm.loadSingleNormByNameAndVersion(idMeasureNorm, normversion);
			// norm is not in database create new norm and save in into
			// database for future
			if (norm == null) {
				norm = new Norm(idMeasureNorm, normversion, description, normcomputable);
				daoNorm.save(norm);
				// add norm to map
				norms.put(norm.getLabel() + "_" + norm.getVersion(), norm);
			}

			// retrieve analysisnorm of the norm
			analysisNorm = analysisNorms.get(norm);

			// norm is empty
			if (analysisNorm == null)

				// add norm to analysisnorms map as new analysis norm
				analysisNorms.put(norm, analysisNorm = new MeasureNorm(norm));

			// ****************************************************************
			// * Import measure to database
			// ****************************************************************

			// ****************************************************************
			// * Retrive phase
			// ****************************************************************
			phaseNumber = rs.getInt(Constant.MEASURE_PHASE);

			// retrieve phase from phases map
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
			mesDesc = daoMeasureDescription.getByReferenceNorm(measureRefMeasure, norm);

			// measure description was found -> NO
			if (mesDesc == null) {

				// System.out.println(measureRefMeasure + " " + idMeasureNorm);

				// create measuredescription
				mesDesc = new MeasureDescription();

				// create text of measuredescription
				mesText = new MeasureDescriptionText();

				// create link from measure description to measure description
				// text
				mesDesc.addMeasureDescriptionText(mesText);

				// fill measure description with data
				mesDesc.setNorm(analysisNorm.getNorm());
				mesDesc.setReference(measureRefMeasure);
				mesDesc.setLevel(rs.getInt(Constant.MEASURE_LEVEL));

				// fill measure description text with data
				// System.out.println(rs.getString(Constant.MEASURE_DOMAIN_MEASURE));
				mesText.setDomain(rs.getString(Constant.MEASURE_DOMAIN_MEASURE));
				mesText.setDescription(rs.getString(Constant.MEASURE_QUESTION_MEASURE));

				/*
				 * System.out.println(mesDesc.getReference() + ":::" + mesDesc.getNorm().getLabel()
				 * + ":::" + mesText.getDomain() + ":::" + mesText.getDescription());
				 */

				mesText.setLanguage(this.analysis.getLanguage());

				// save measure description to database
				daoMeasureDescription.save(mesDesc);

				// else: check if measure description text exists in the
				// language of the analysis ->
				// NO
			} else if (!daoMeasureDescriptionText.existsForLanguage(mesDesc.getId(), this.analysis.getLanguage().getId())) {

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

			// retrieve id for the instance creation (NormMeasure ID)
			// insertID = mysql.getLastInsertId();
			NormMeasure normMeasure = new NormMeasure();
			normMeasure.setMeasureDescription(mesDesc);
			normMeasure.setComment(rs.getString(Constant.MEASURE_COMMENT));
			normMeasure.setInternalWL(rs.getInt(Constant.MEASURE_INTERNAL_SETUP));
			normMeasure.setExternalWL(rs.getInt(Constant.MEASURE_EXTERNAL_SETUP));
			normMeasure.setImplementationRate(rs.getDouble(Constant.MEASURE_IMPLEMENTATION_RATE));
			normMeasure.setInvestment(rs.getDouble(Constant.MEASURE_INVESTISMENT));
			normMeasure.setLifetime(rs.getInt(Constant.MEASURE_LIFETIME));
			normMeasure.setMaintenance(rs.getString(Constant.MEASURE_MAINTENANCE).trim().isEmpty() ? -1 : rs.getInt(Constant.MEASURE_MAINTENANCE));
			normMeasure.setStatus(rs.getString(Constant.MEASURE_STATUS));
			normMeasure.setToCheck(rs.getString(Constant.MEASURE_REVISION));
			normMeasure.setToDo(rs.getString(Constant.MEASURE_TODO));
			normMeasure.getMeasureDescription().setComputable(measurecomputable);

			// calculate cost
			cost =
				Analysis.computeCost(this.analysis.getParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE), this.analysis.getParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE), this.analysis
						.getParameter(Constant.PARAMETER_LIFETIME_DEFAULT), this.analysis.getParameter(Constant.PARAMETER_MAINTENANCE_DEFAULT), normMeasure.getInternalWL(), normMeasure
						.getExternalWL(), normMeasure.getInvestment(), normMeasure.getLifetime(), normMeasure.getMaintenance());

			normMeasure.setCost(cost);

			// ****************************************************************
			// * add phase instance to the measure instance
			// ****************************************************************
			normMeasure.setPhase(phase);

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
			normMeasure.setMeasurePropertyList(measureProperties);

			// ****************************************************************
			// * add measure to norm
			// ****************************************************************

			// add measure to norm
			((MeasureNorm) analysisNorm).addMeasure(normMeasure);

			// add measure to map
			measures.put(idMeasureNorm + "_" + normversion + "_" + measureRefMeasure, normMeasure);
		}
		// close result
		rs.close();
	}

	/**
	 * importAssetTypeValues: <br>
	 * <ul>
	 * <li>Imports all Asset Type Values for Measure Norms</li>
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
		NormMeasure normMeasure = null;

		// ****************************************************************
		// * create asset type default values in mysql database
		// ****************************************************************
		createAssetTypeDefaultValues();

		// ****************************************************************
		// * check assettypevalues for values of 101 (-1) then find
		// * previous levels until an acceptable value was found
		// ****************************************************************

		// parse measures
		for (Measure measure : measures.values()) {

			// norm measure -> YES
			if (measure instanceof NormMeasure) {

				// store into object
				normMeasure = (NormMeasure) measure;

				// ****************************************************************
				// * for each norm: parse measures to find a 101 (-1) value
				// ****************************************************************

				// ****************************************************************
				// * parse each assettypevalue for this measure (each measure
				// has
				// * as much as there are asset types for this analysis)
				// * For each value of 101 (-1) lookup previous levels until a
				// * valid value (0-100) was found
				// ****************************************************************

				// parse assettypevalues of current measure
				for (AssetTypeValue assetTypeValue : normMeasure.getAssetTypeValues()) {

					// ****************************************************************
					// * check if asset type value is 101 (-1) and level is
					// 3
					// * -> to change to valid value
					// ****************************************************************

					// check if value is 101 and level 3 -> YES
					if ((assetTypeValue.getValue() == -1) && (normMeasure.getMeasureDescription().isComputable())) {

						// set this asset type value with a valid value
						updateAssetTypeValue(normMeasure, assetTypeValue);
					}
				}
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
		ItemInformation itemInformation = null;
		ResultSetMetaData rsMetaData = null;
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
					itemInformation = new ItemInformation(rsMetaData.getColumnName(i),Constant.ITEMINFORMATION_SCOPE, rs.getString(rsMetaData.getColumnName(i)));

					// ****************************************************************
					// * add instance to list of item information
					// ****************************************************************
					this.analysis.addAnItemInformation(itemInformation);
				}
			}
		}

		// close result
		rs.close();

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
				itemInformation = new ItemInformation(rsMetaData.getColumnName(i),Constant.ITEMINFORMATION_ORGANISATION, rs.getString(rsMetaData.getColumnName(i)));

				// ****************************************************************
				// * add instance to list of item information
				// ****************************************************************
				this.analysis.addAnItemInformation(itemInformation);
			}
		}
	}

	/**
	 * importMaturityMeasures: <br>
	 * <ul>
	 * <li>Imports all Maturity Measures</li>
	 * <li>Create Objects for each Measure</li>
	 * <li>Creates Maturity AnalysisNorm Object</li>
	 * <li>Adds measures to the Maturity AnalysisNorm</li>
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
		ResultSet rs2 = null;
		int numPhase = 0;
		String chapter = "";
		double cost = 0;
		List<Object> params = new Vector<Object>();
		AnalysisNorm analysisNorm = null;
		Phase tempPhase = null;
		String query = "";
		String status = "";
		Norm norm = null;
		MeasureDescription mesDesc = null;
		MeasureDescriptionText mesText = null;
		Integer chapterValue = 0;
		Parameter implementationRateParameter = null;
		double implementationRate = 0;
		MaturityMeasure maturityMeasure = null;
		Integer normversion = 2005;
		boolean normcomputable = true;
		boolean measurecomputable = false;
		String description = "Old Maturity measure to be used with the 2005 verison of 27001 ISO norm.";

		// ****************************************************************
		// * load each maturity
		// ****************************************************************

		// build and execute query
		rs = sqlite.query("SELECT * from maturities", null);

		// retrieve results
		while (rs.next()) {

			currentSqliteTable = "maturities";
			// retrieve norm from map
			norm = norms.get(Constant.NORM_MATURITY);
			
			tempPhase = null;
			
			if (columnExists(rs, Constant.MEASURE_VERSION_NORM)) {
				normversion = rs.getInt(Constant.MEASURE_VERSION_NORM);
				normcomputable = rs.getBoolean(Constant.MEASURE_NORM_COMPUTABLE);
				measurecomputable = rs.getBoolean(Constant.MEASURE_MEASURE_COMPUTABLE);
				description = rs.getString(Constant.MEASURE_NORM_DESCRIPTION);
			} else {
				normversion = 2005;
				normcomputable = true;
				description = "old norm (before 2013)";
				if (rs.getInt(Constant.MEASURE_LEVEL) == Constant.MEASURE_LEVEL_3) {
					measurecomputable = true;
				} else {
					measurecomputable = false;
				}
			}

			norm = daoNorm.loadSingleNormByNameAndVersion(Constant.NORM_MATURITY, normversion);
			// norm is not in database create new norm and save in into
			// database for future
			if (norm == null) {
				norm = new Norm(Constant.NORM_MATURITY, normversion, description, normcomputable);
				daoNorm.save(norm);
				// add norm to map
				norms.put(norm.getLabel() + "_" + norm.getVersion(), norm);
			}

			// get analysisNorm from map
			analysisNorm = analysisNorms.get(norm);

			// analysis does not yet exist
			if (analysisNorm == null)

				// add norm to analysinorm map as analaysisnorm object
				analysisNorms.put(norm, analysisNorm = new MaturityNorm(analysis, norm));

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
			mesDesc = daoMeasureDescription.getByReferenceNorm(chapter, analysisNorm.getNorm());

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
				mesDesc.setNorm(analysisNorm.getNorm());
				mesDesc.setReference(chapter);
				mesDesc.setLevel(rs.getInt(Constant.MEASURE_LEVEL));

				// fill measure description text
				mesText.setDomain(rs.getString(Constant.MATURITY_DOMAIN).replace("'", "''"));
				mesText.setDescription(Constant.EMPTY_STRING);
				mesText.setLanguage(this.analysis.getLanguage());

				// else: measure description exist: measure description text
				// exists in the language
				// of the analysis -> NO
			} else if (!daoMeasureDescriptionText.existsForLanguage(mesDesc.getId(), this.analysis.getLanguage().getId())) {

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

				// ****************************************************************
				// * measure is a chapter (example: M.4)
				// ****************************************************************

				// get chapter as integer (stored in database as integer)
				chapterValue = Integer.valueOf(chapter.substring(2, chapter.length()));

				// ****************************************************************
				// * select phase number for the chapter
				// ****************************************************************

				currentSqliteTable = "maturity_phase";
				// build query
				query = "SELECT phase FROM maturity_phase WHERE chapter=?";

				// add parameters
				params.clear();
				params.add(chapterValue);

				// execute query
				rs2 = sqlite.query(query, params);

				// retrieve result
				// result returns a row -> YES
				if (rs2.next()) {

					// set phase number
					numPhase = rs2.getInt(Constant.MATURITYPHASE_ID);

					// retrieve phase from number of the map
					tempPhase = phases.get(numPhase);
				}

				// close result
				rs2.close();
			}

			// phase does not exist
			if (tempPhase == null) {

				// add not usable phase (0)

				// set phase 0
				numPhase = Constant.PHASE_NOT_USABLE;

				// ****************************************************************
				// * create phase instance
				// ****************************************************************

				tempPhase = phases.get(numPhase);

				if (tempPhase == null) {

					tempPhase = new Phase();
					tempPhase.setNumber(numPhase);
					tempPhase.setBeginDate(null);
					tempPhase.setBeginDate(null);

					phases.get(numPhase);

					phases.put(numPhase, tempPhase);

					analysis.addUsedPhase(tempPhase);
				}

			}

			// ****************************************************************
			// * Insert measure into maturitymeasure table
			// ****************************************************************

			// add parameters
			status = rs.getString(Constant.MEASURE_STATUS);

			// set status and by default not applicable (NA)
			if ((!status.equals(Constant.MEASURE_STATUS_APPLICABLE)) && (!status.equals(Constant.MEASURE_STATUS_MANDATORY)) && (!status.equals(Constant.MEASURE_STATUS_NOT_APPLICABLE))) {

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
				cost =
					Analysis.computeCost(this.analysis.getParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE), this.analysis.getParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE),
							this.analysis.getParameter(Constant.PARAMETER_LIFETIME_DEFAULT), this.analysis.getParameter(Constant.PARAMETER_MAINTENANCE_DEFAULT), rs
									.getInt(Constant.MATURITY_INTWL), rs.getInt(Constant.MATURITY_EXTWL), rs.getInt(Constant.MATURITY_INVESTMENT), rs.getInt(Constant.MEASURE_LIFETIME), rs
									.getInt(Constant.MEASURE_MAINTENANCE));
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
			maturityMeasure.setMaintenance(rs.getInt(Constant.MEASURE_MAINTENANCE));
			maturityMeasure.setStatus(rs.getString(Constant.MEASURE_STATUS).replace("'", "''"));
			maturityMeasure.setToDo(rs.getString(Constant.MEASURE_TODO).replace("'", "''"));
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
			// * add measure to norm
			// ****************************************************************
			((MaturityNorm) analysisNorm).addMeasure(maturityMeasure);

			// add measure to measures map
			measures.put(analysisNorm.getNorm().getLabel() + "_" + analysisNorm.getNorm().getVersion() + "_" + chapter, maturityMeasure);
		}

		// close result
		rs.close();

		// add analysis norms from map to the analysis
		for (AnalysisNorm analysisNorm2 : analysisNorms.values())
			analysis.addAnalysisNorm(analysisNorm2);
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
	 * Adds all Scenario Categories to the given scenario or measure object. <br>
	 * Scenario categories are: <br>
	 * <ul>
	 * <li>Direct: d1, d2, d3, d4, d5, d6, d6.1, d6.2, d6.3, d6.4, d7</li>
	 * <li>Indirect: i1, i2, i3, i4, i5, i6, i7, i8, i8.1, i8.2, i8.3, i8.4, i9, i10</li>
	 * </ul>
	 * 
	 * @param criteria
	 *            The scenario or measure object to set the categories with values
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
			assetType = daoAssetType.get(key);

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
	 * checkIfStringToDoublePossible: <br>
	 * Checks if given String can be Transformed into a valid Double Value
	 * 
	 * @param value
	 *            The String Value to Convert to Double
	 * @retrun Retruns true or false if Transformation is Possible or not
	 */
	private boolean checkIfStringToDoublePossible(String value) {
		try {

			// ****************************************************************
			// * check if value can be cast to double type
			// ****************************************************************
			Double.valueOf(value);

			// ****************************************************************
			// * return result
			// ****************************************************************
			return true;
		} catch (NumberFormatException ex) {

			// ****************************************************************
			// * in case of an error return result
			// ****************************************************************
			return false;
		}
	}

	/**
	 * convertImpactToDouble: <br>
	 * Takes a string value (value from SQLite file) and converts it into a valid double value.
	 * 
	 * @param impact
	 *            The impact value as string
	 * @return A valid Double value
	 */
	private double convertImpactToDouble(String impact) {

		// ****************************************************************
		// * Initialise variables
		// ****************************************************************
		double result = 0;
		String pattern = "c([0-9]|10)";

		// ****************************************************************
		// * pattern matches a acronym -> YES
		// ****************************************************************
		if (impact.matches(pattern)) {

			// ****************************************************************
			// * retrieve double value for impact inside extened parameters
			// ****************************************************************

			// ****************************************************************
			// retrieve impact value from parameters
			// ****************************************************************

			for (int i = 0; i < this.analysis.getParameters().size(); i++) {
				if (this.analysis.getAParameter(i).getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME)) {
					if (((ExtendedParameter) this.analysis.getAParameter(i)).getAcronym().equals(String.valueOf(impact))) {
						result = this.analysis.getAParameter(i).getValue();
						break;
					}
				}
			}
		}

		// ****************************************************************
		// * check if pattern matches a acronym -> NO
		// ****************************************************************
		else {

			// ****************************************************************
			// * check if impact value can be used as double value
			// ****************************************************************

			// ****************************************************************
			// * set impact value
			// ****************************************************************

			// check if value can be transformed into double
			if (checkIfStringToDoublePossible(impact)) {

				// transform string value into double
				result = Double.valueOf(impact);

				// transform to kilo euro (*1000)
				// result *= 1000.;
			} else {

				// no value found so 0
				result = 0.;
			}
		}

		// ****************************************************************
		// * Return result
		// ****************************************************************
		return result;
	}

	/**
	 * createAssetTypeDefaultValuesInDatabase: <br>
	 * This method Inserts the Default Asset Type Values from the Sqlite File. Values of -1 are
	 * changed into 101 (still illegal value).
	 * 
	 * @throws Exception
	 */
	private void createAssetTypeDefaultValues() throws Exception {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet rs = null;
		NormMeasure measure = null;
		String query = "";
		AssetTypeValue assetTypeValue = null;
		AssetType assetType = null;

		// ****************************************************************
		// * retrieve asset type values for measures
		// ****************************************************************

		currentSqliteTable = "spec_default_type_asset_measure";

		// build query
		query = "SELECT * FROM spec_default_type_asset_measure";

		// execute query
		rs = sqlite.query(query, null);

		while (rs.next()) {

			// ****************************************************************
			// * retrieve norm and measure
			// ****************************************************************
			measure = null;
			assetTypeValue = new AssetTypeValue();

			// System.out.println(rs.getString(Constant.MEASURE_ID_NORM) + ":::"
			// +
			// rs.getString(Constant.MEASURE_REF_MEASURE) + ":::" +
			// rs.getInt(Constant.ASSET_ID_TYPE_ASSET));

			int normversion = 2005;

			if (columnExists(rs, Constant.MEASURE_VERSION_NORM)) {
				normversion = rs.getInt(Constant.MEASURE_VERSION_NORM);
			}

			measure = (NormMeasure) measures.get(rs.getString(Constant.MEASURE_ID_NORM) + "_" + normversion + "_" + rs.getString(Constant.MEASURE_REF_MEASURE));

			// ****************************************************************
			// * retrieve asset type label for the instance creation
			// ****************************************************************

			assetType = daoAssetType.get(rs.getInt(Constant.ASSET_ID_TYPE_ASSET));
			assetTypeValue.setAssetType(assetType);
			assetTypeValue.setValue(rs.getInt(Constant.VALUE_SPEC));

			// add the asset type value to the measure
			measure.addAnAssetTypeValue(assetTypeValue);
		}
		// close result
		rs.close();
	}

	/**
	 * updateAssetTypeValue: <br>
	 * <ul>
	 * <li>Update a given Asset Type Value for a given Measure in a given AnalysisNorm</li>
	 * </ul>
	 * 
	 * @param newMeasureNorm
	 *            The AnalysisNorm Object of the Measure
	 * @param normMeasure
	 *            The Measure Object
	 * @param assetTypeValue
	 *            The Asset Type Value to change
	 * 
	 * @throws Exception
	 */
	private void updateAssetTypeValue(NormMeasure normMeasure, AssetTypeValue assetTypeValue) throws Exception {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		String[] cuttedLevel = null;
		boolean hasFound = false;
		String previousLevel = "";
		String level = normMeasure.getMeasureDescription().getReference();
		String normName = normMeasure.getAnalysisNorm().getNorm().getLabel();
		int normVersion = normMeasure.getAnalysisNorm().getNorm().getVersion();
		NormMeasure prevNormMeasure = null;
		int value = -1;

		// intitialise to original reference to split
		// (to find upper level above
		// ex. : A.5.1.1(level 3) -> A.5.1(level 2) -> A.5(level
		// 1)
		// ****************************************************************
		// * parse previous level until you find a valid value
		// ****************************************************************
		// do this as long as you do not found the correct
		// value
		do {

			// *************************************************************
			// * create a list of the splitted reference parts
			// * example: A.5.1 -> Vector (A,5,1)
			// *************************************************************

			// empty the vector of the splitted reference
			// cuttedLevel.clear();

			// split the reference into the vector
			cuttedLevel = level.split("[.]");

			// Add last part to the vector
			// cuttedLevel.add(level);

			// *************************************************************
			// * build previous level
			// * (always (current level - 1) -> vector size - 1)
			// * example: A.5.1 --> A.5
			// *************************************************************

			// parse the vector to size -1 of the reference
			for (int index = 0; index < cuttedLevel.length - 1; index++) {

				// concatenate vector values to build previous reference
				if (index == 0)
					previousLevel = cuttedLevel[index];
				else
					previousLevel += "." + cuttedLevel[index];
			}

			// in case this previous level is also -1: check level above for the
			// previous level
			// (used when reentering loop)
			level = previousLevel;

			// *************************************************************
			// * parse all measures of this norm to find measure with the
			// reference built above
			// * (level above)
			// *************************************************************

			// parse measures of the same norm

			prevNormMeasure = (NormMeasure) measures.get(normName + "_" + normVersion + "_" + previousLevel);

			// check if the reference met the reference that was built above ->
			// YES
			if (prevNormMeasure != null) {

				// *****************************************************
				// * measure was found -> parse assettype values to find
				// matching assettype and
				// * update ifnot 101(-1)
				// *****************************************************

				// parse assettypevalues
				for (AssetTypeValue assetTypeValue2 : prevNormMeasure.getAssetTypeValues()) {

					// check if the assettype is the same AND check if the value
					// of it is not
					// 101 (usable)

					if (assetTypeValue2.getAssetType().equals(assetTypeValue.getAssetType())) {

						if (assetTypeValue2.getValue() != -1 || prevNormMeasure.getMeasureDescription().getLevel() == 1) {

							// *********************************************
							// * valid value was found update object and
							// database
							// *********************************************

							// valid value found, set flag to leave loop
							hasFound = true;

							// set value when previous value was not -1 or if
							// level is -1 and still
							// -1
							value = assetTypeValue2.getValue() == -1 ? 100 : assetTypeValue2.getValue();

							// *********************************************
							// * set new value for the object
							// *********************************************
							assetTypeValue.setValue(value);

							// value found, exit this loop
							break;
						}
					}
				}

				// initialise reference variable
				previousLevel = "";
			} else
				throw new Exception("Error level " + normMeasure.getMeasureDescription().getReference());
		} while (!hasFound);
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
	public long getIdTask() {
		return idTask;
	}

	/**
	 * @param idTask
	 *            the idTask to set
	 */
	public void setIdTask(long idTask) {
		this.idTask = idTask;
	}

	protected void initialiseDAO(Session session) {
		setDaoAnalysis(new DAOAnalysisHBM(session));
		setDaoAssetType(new DAOAssetTypeHBM(session));
		setDaoLanguage(new DAOLanguageHBM(session));
		setDaoMeasureDescription(new DAOMeasureDescriptionHBM(session));
		setDaoMeasureDescriptionText(new DAOMeasureDescriptionTextHBM(session));
		setDaoNorm(new DAONormHBM(session));
		setDaoParameterType(new DAOParameterTypeHBM(session));
		setDaoScenarioType(new DAOScenarioTypeHBM(session));
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
	 * @param daoScenarioType
	 *            the daoScenarioType to set
	 */
	public void setDaoScenarioType(DAOScenarioType daoScenarioType) {
		this.daoScenarioType = daoScenarioType;
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
	 * @param daoNorm
	 *            the daoNorm to set
	 */
	public void setDaoNorm(DAONorm daoNorm) {
		this.daoNorm = daoNorm;
	}

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
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

}