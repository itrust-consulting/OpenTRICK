package lu.itrust.business.TS.exportation.sqlite;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.Session;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.DatabaseHandler;
import lu.itrust.business.TS.database.dao.DAOAssetType;
import lu.itrust.business.TS.database.dao.hbm.DAOAssetTypeHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.general.SecurityCriteria;
import lu.itrust.business.TS.model.history.History;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;
import lu.itrust.business.TS.model.parameter.IAcronymParameter;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.IImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.DynamicParameter;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.TS.model.parameter.impl.MaturityParameter;
import lu.itrust.business.TS.model.parameter.impl.RiskAcceptanceParameter;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.model.scale.Translation;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.AssetStandard;
import lu.itrust.business.TS.model.standard.MaturityStandard;
import lu.itrust.business.TS.model.standard.NormalStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.impl.MaturityMeasure;
import lu.itrust.business.TS.model.standard.measure.impl.MeasureAssetValue;
import lu.itrust.business.TS.model.standard.measure.impl.NormalMeasure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;

/**
 * ExportAnalysis: <br>
 * This class is used to export a specific Analysis into a SQLite file to be
 * used inside TRICK Light.
 *
 * @author itrust consulting s.à r.l. - SME,BJA,EOM,SMU
 * @version 0.1
 * @since 2012-12-17
 */
public class ExportAnalysis {

	private static final int SCENARIO_COLUMN_COUNT = 55;

	private static final int MEASURE_ROW_COUNT = 70;

	private static final int MATURITY_MEASURE_ROW_COUNT = 27;

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	/** SQLite Database Handler */
	private DatabaseHandler sqlite = null;

	private String idTask;

	/** Analysis object */
	private Analysis analysis = null;

	private ServiceTaskFeedback serviceTaskFeedback;

	private DAOAssetType serviceAssetType;

	private List<ScaleType> scaleTypes;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	protected ExportAnalysis() {
	}

	/***********************************************************************************************
	 * Methods
	 **********************************************************************************************/

	public ExportAnalysis(Analysis analysis2, DatabaseHandler sqlite2) {
		this.analysis = analysis2;
		this.sqlite = sqlite2;
	}

	public ExportAnalysis(ServiceTaskFeedback serviceTaskFeedback, Session session, DatabaseHandler sqlite2, Analysis analysis, String idTask) {
		this.serviceAssetType = new DAOAssetTypeHBM(session);
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.sqlite = sqlite2;
		this.analysis = analysis;
		this.idTask = idTask;
	}

	/**
	 * exportAnAnalysis: <br>
	 * Description
	 *
	 * @return
	 */
	public MessageHandler exportAnAnalysis() {

		System.out.println("Exporting...");

		try {

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.identifier", "Export identifier", 10));

			scaleTypes = analysis.getImpactParameters().stream().map(ImpactParameter::getType).distinct().collect(Collectors.toList());

			// ****************************************************************
			// * export Identifier
			// ****************************************************************
			exportIdentifier();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.history", "Export histories", 15));

			// ****************************************************************
			// * export History
			// ****************************************************************
			exportHistory();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.risk_information", "Export risk information", 20));

			// ****************************************************************
			// * export risk information
			// ****************************************************************
			exportRiskInformation();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.item_information", "Export item information", 25));

			// ****************************************************************
			// * export item information
			// ****************************************************************
			exportItemInformation();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.parameters", "Export Parameters", 30));

			// ****************************************************************
			// * export simple parameters
			// ****************************************************************
			exportParameters();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.assets", "Export assets", 40));

			// ****************************************************************
			// * export assets
			// ****************************************************************
			exportAssets();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.scenarios", "Export scenarios", 50));

			// ****************************************************************
			// * export scenarios
			// ****************************************************************
			exportScenarios();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.assessments", "Export assessments", 60));

			// ****************************************************************
			// * export assessments
			// ****************************************************************
			exportAssessments();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.risk_profile", "Export risk profile", 65));

			exportRiskProfile();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.measures", "Export measures", 70));

			// ****************************************************************
			// * export all measures
			// ****************************************************************
			exportMeasuresAndMaturity();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.phases", "Export phases", 75));

			// ****************************************************************
			// * export phase
			// ****************************************************************
			exportPhase();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.action_plan", "Export action plans", 80));

			// ****************************************************************
			// * export action plans
			// ****************************************************************
			exportActionPlans();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.summaries", "Export summaries", 85));

			// ****************************************************************
			// * export summary
			// ****************************************************************
			exportActionPlanSummaries(this.analysis.getSummaries());

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.risk_register", "Export risk registers", 90));

			// ****************************************************************
			// * export Risk Register
			// ****************************************************************
			exportRiskRegister();

			serviceTaskFeedback.send(idTask, new MessageHandler("success.export.analysis", "Export done successfully", 95));

			System.out.println("Export Done!");

			return null;

		} catch (TrickException e) {
			// ****************************************************************
			// * Display error message
			// ****************************************************************
			MessageHandler handler = new MessageHandler(e);
			serviceTaskFeedback.send(idTask, handler);
			System.out.println(e.getMessage());
			TrickLogManager.Persist(e);
			return handler;
		} catch (Exception e) {
			// ****************************************************************
			// * Display error message
			// ****************************************************************
			serviceTaskFeedback.send(idTask, new MessageHandler("error.export.unknown", "An unknown error occurred while exporting", e));
			System.out.println("Error while exporting!");
			return new MessageHandler(e);
			// set return value exception
		} finally {
			if (getSqlite() != null) {
				try {
					getSqlite().close();
				} catch (SQLException e) {
					TrickLogManager.Persist(e);
				}
			}

		}
	}

	private void exportRiskProfile() throws SQLException {

		if (analysis.getType() == AnalysisType.QUANTITATIVE)
			return;

		System.out.println("Export Risk profile");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> params = new ArrayList<Object>();
		String query = "", unionQuery = " UNION Select ?,?,?,?,?,?,?",
				baseQuery = "INSERT INTO risk_profile SELECT ? as id_threat,? as id_asset,? as actionPlan,? as treatment,? as strategy,? as exp_probability,? as raw_probability";

		// ****************************************************************
		// * Export the Risk Register Item by Item
		// ****************************************************************

		LikelihoodParameter defaultProbability = analysis.findLikelihoodByTypeAndLevel(0);

		// parse all Risk Register Entries
		for (RiskProfile riskProfile : analysis.getRiskProfiles()) {
			// add parameters for the current Risk Register Item
			if (query.isEmpty())
				query = baseQuery;
			else if (params.size() + 7 > 999) {
				sqlite.query(query, params);
				query = baseQuery;
				params.clear();
			} else
				query += unionQuery;
			addRiskProfile(params, defaultProbability, riskProfile);
		}

		if (!params.isEmpty()) {
			sqlite.query(query, params);
			params.clear();
		}

		query = "";
		unionQuery = " UNION Select ?,?,?,?";
		baseQuery = "INSERT INTO risk_profile_impact SELECT ? as id_threat,? as id_asset,? as name,? as value";
		// parse all Risk Register Entries
		for (RiskProfile riskProfile : analysis.getRiskProfiles()) {
			// add parameters for the current Risk Register Item
			if (query.isEmpty())
				query = baseQuery;
			else if (params.size() + 8 * scaleTypes.size() > 999) {
				sqlite.query(query, params);
				query = baseQuery;
				params.clear();
			} else
				query += unionQuery;
			int index = scaleTypes.size();
			for (ScaleType scaleType : scaleTypes) {
				addRiskProfileImpact(params, riskProfile, "RAW", getImpact(riskProfile.getRawProbaImpact(), scaleType));
				query += unionQuery;
				addRiskProfileImpact(params, riskProfile, "EXP", getImpact(riskProfile.getExpProbaImpact(), scaleType));
				if (--index > 0)
					query += unionQuery;
			}
		}

		if (!params.isEmpty()) {
			sqlite.query(query, params);
			params.clear();
		}

		query = "";
		unionQuery = " UNION Select ?,?,?,?,?";
		baseQuery = "INSERT INTO risk_profile_measure SELECT ? as id_threat,? as id_asset,? as id_norme,? as version_norme, ? as ref_measure";
		// parse all Risk Register Entries
		for (RiskProfile riskProfile : analysis.getRiskProfiles()) {
			// add parameters for the current Risk Register Item
			if (riskProfile.getMeasures().isEmpty())
				continue;
			if (query.isEmpty())
				query = baseQuery;
			else if (params.size() + 5 * riskProfile.getMeasures().size() > 999) {
				sqlite.query(query, params);
				query = baseQuery;
				params.clear();
			} else
				query += unionQuery;
			int index = riskProfile.getMeasures().size();
			for (Measure measure : riskProfile.getMeasures()) {
				params.add(riskProfile.getScenario().getId());
				params.add(riskProfile.getAsset().getId());
				params.add(measure.getAnalysisStandard().getStandard().getLabel());
				params.add(measure.getAnalysisStandard().getStandard().getVersion());
				params.add(measure.getMeasureDescription().getReference());
				if (--index > 0)
					query += unionQuery;
			}
		}

		if (!params.isEmpty()) {
			sqlite.query(query, params);
			params.clear();
		}

	}

	private String getImpact(RiskProbaImpact riskProbaImpact, ScaleType scaleType) {
		return riskProbaImpact == null ? scaleType.getAcronym() + 0 : getAcronym(riskProbaImpact.get(scaleType.getName()), scaleType);
	}

	private String getAcronym(IImpactParameter impactParameter, ScaleType scaleType) {
		return impactParameter == null ? scaleType.getAcronym() + 0 : impactParameter.getAcronym();
	}

	private void addRiskProfileImpact(List<Object> params, RiskProfile riskProfile, String name, String value) {
		params.add(riskProfile.getScenario().getId());
		params.add(riskProfile.getAsset().getId());
		params.add(name);
		params.add(value);
	}

	private void addRiskProfile(List<Object> params, LikelihoodParameter defaultProbability, RiskProfile riskProfile) {
		RiskStrategy riskStrategy;
		params.add(riskProfile.getScenario().getId());
		params.add(riskProfile.getAsset().getId());
		params.add(riskProfile.getActionPlan());
		params.add(riskProfile.getRiskTreatment());
		riskStrategy = riskProfile.getRiskStrategy();
		if (riskStrategy == null)
			riskStrategy = RiskStrategy.REDUCE;
		params.add(riskStrategy);
		if (riskProfile.getExpProbaImpact() == null)
			params.add(defaultProbability.getAcronym());
		else
			params.add(riskProfile.getExpProbaImpact().getProbability(defaultProbability).getAcronym());

		if (riskProfile.getRawProbaImpact() == null)
			params.add(defaultProbability.getAcronym());
		else
			params.add(riskProfile.getRawProbaImpact().getProbability(defaultProbability).getAcronym());
	}

	/**
	 * exportRiskInformation: <br>
	 * Exports the Risk Information to an Sqlite File using an Sqlite Database
	 * Handler.
	 *
	 * @throws Exception
	 */
	private void exportRiskInformation() throws Exception {

		System.out.println("Export risk information");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> riskparams = new ArrayList<Object>();
		List<Object> threatparams = new ArrayList<Object>();
		List<Object> vulparams = new ArrayList<Object>();
		String riskquery = "";
		String threatquery = "";
		String vulquery = "";
		int riskcounter = 0;
		int threatcounter = 0;
		int vulcounter = 0;

		// ****************************************************************
		// * parse all risk information to export
		// ****************************************************************

		// parse risk information
		for (RiskInformation information : this.analysis.getRiskInformations()) {
			// ****************************************************************
			// * check type of risk information
			// ****************************************************************

			// ****************************************************************
			// * check if RISK -> YES
			// ****************************************************************

			if (information.getCategory().startsWith(Constant.RI_TYPE_RISK)) {

				// ****************************************************************
				// * export risk information
				// ****************************************************************

				// ****************************************************************
				// * build query
				// ****************************************************************

				// first time query is used? -> YES
				if (riskquery.isEmpty()) {

					// build first query part
					riskquery = "INSERT INTO threat_source SELECT ? as level,? as name,? as ";
					riskquery += "type,? as expo,? as owner,? as custom,? as comment,? as comment2 UNION";

					// set number of ? -> sqlite ? limit is 999 -> before 999 is
					// reached, a execute
					// needs to be done
					riskcounter = 8;
				} else {

					// first time query is used ? -> NO

					// check if limit of ? is reached with next query -> YES
					if (riskcounter + 8 >= 999) {

						// remove UNION from query
						riskquery = riskquery.substring(0, riskquery.length() - 6);

						// execute query
						sqlite.query(riskquery, riskparams);

						// clear array
						riskparams.clear();

						// rebuild first query part
						riskquery = "INSERT INTO threat_source SELECT ? as level,? as name,";
						riskquery += "? as type,? as expo,? as owner,? as custom,? as comment,? as comment2 UNION";

						// reset number of ?
						riskcounter = 8;
					} else {

						// check if limit of ? is reached -> NO

						// add values to query ( execute 1 query with multiple
						// rows)
						riskquery += " SELECT ?,?,?,?,?,?,?,? UNION";

						// add number of ? used
						riskcounter += 8;
					}
				}

				// add parameters
				riskparams.add(information.getChapter());
				riskparams.add(information.getLabel());
				riskparams.add(information.getCategory().split("_")[1]);
				riskparams.add(information.getExposed());
				riskparams.add(information.getOwner());
				riskparams.add(information.isCustom());
				riskparams.add(information.getComment());
				riskparams.add(information.getHiddenComment());
			}

			// ****************************************************************
			// * check if THREAT -> YES
			// ****************************************************************

			if (information.getCategory().equals(Constant.RI_TYPE_THREAT)) {

				// ****************************************************************
				// * export threat information
				// ****************************************************************

				// ****************************************************************
				// * build query
				// ****************************************************************

				// first part ? -> YES
				if (threatquery.equals(Constant.EMPTY_STRING)) {
					threatquery = "INSERT INTO threat_typology SELECT ? as level,? as name,";
					threatquery += "? as acro,? as expo,? as owner,? as custom,? as comment,? as comment2 UNION";
					threatcounter = 7;
				} else {

					// first part ? -> NO

					// limit of ? reached -> YES
					if (threatcounter + 8 >= 999) {

						// execute query
						threatquery = threatquery.substring(0, threatquery.length() - 6);
						sqlite.query(threatquery, threatparams);

						// clear parameters
						threatparams.clear();

						// reset query
						threatquery = "INSERT INTO threat_typology SELECT ? as level,? as name";
						threatquery += ",? as acro,? as expo,? as owner, ? as custom,? as comment,? as comment2";
						threatquery += " UNION";

						// reset number of ?
						threatcounter = 8;
					} else {

						// limit of ? reached -> NO

						// add value
						threatquery += " SELECT ?,?,?,?,?,?,?,? UNION";

						// add number of ? used
						threatcounter += 8;
					}
				}

				// add parameters
				threatparams.add(information.getChapter());
				threatparams.add(information.getLabel());
				threatparams.add(information.getAcronym());
				threatparams.add(information.getExposed());
				threatparams.add(information.getOwner());
				threatparams.add(information.isCustom());
				threatparams.add(information.getComment());
				threatparams.add(information.getHiddenComment());
			}

			// ****************************************************************
			// * check if vulnerability -> YES
			// ****************************************************************

			if (information.getCategory().equals(Constant.RI_TYPE_VUL)) {

				// ****************************************************************
				// * export vulnerability information
				// ****************************************************************

				// ****************************************************************
				// * build query
				// ****************************************************************

				// check if first part -> YES
				if (vulquery.equals(Constant.EMPTY_STRING)) {

					// build query
					vulquery = "INSERT INTO vulnerabilities SELECT ? as level,? as name,";
					vulquery += "? as expo,? as owner, ? as custom,? as comment,? as comment2 UNION";

					// set number of ? used
					vulcounter = 7;
				} else {

					// check if limit is reached -> YES
					if (vulcounter + 7 >= 999) {

						// execute query
						vulquery = vulquery.substring(0, vulquery.length() - 6);
						sqlite.query(vulquery, vulparams);

						// clean parameters
						vulparams.clear();

						// reset query
						vulquery = "INSERT INTO vulnerabilities SELECT ? as level,? as name, ";
						vulquery += "? as expo,? as owner, ? as custom,? as comment,? as comment2 UNION";

						// reset number of ?
						vulcounter = 7;
					} else {

						// check if limit reached -> NO

						// set values
						vulquery += " SELECT ?,?,?,?,?,?,? UNION";

						// add number of ? used
						vulcounter += 7;
					}
				}

				// add parameters
				vulparams.add(information.getChapter());
				vulparams.add(information.getLabel());
				vulparams.add(information.getExposed());
				vulparams.add(information.getOwner());
				vulparams.add(information.isCustom());
				vulparams.add(information.getComment());
				vulparams.add(information.getHiddenComment());
			}
		}

		// ****************************************************************
		// * after the loop, the last entries need to be added
		// ****************************************************************

		if (!riskquery.isEmpty()) {
			if (riskquery.endsWith("UNION"))
				riskquery = riskquery.substring(0, riskquery.length() - 6);
			// execute last risk, threat and vulnerability values
			sqlite.query(riskquery, riskparams);
		}

		if (!threatquery.isEmpty()) {
			if (threatquery.endsWith("UNION"))
				threatquery = threatquery.substring(0, threatquery.length() - 6);
			// execute last risk, threat and vulnerability values
			sqlite.query(threatquery, threatparams);
		}

		if (!vulquery.isEmpty()) {
			if (vulquery.endsWith("UNION"))
				vulquery = vulquery.substring(0, vulquery.length() - 6);
			// execute last risk, threat and vulnerability values
			sqlite.query(vulquery, vulparams);
		}
	}

	/**
	 * exportItemInformation: <br>
	 * Exports the Item Information to an Sqlite File using an Sqlite Database
	 * Handler.
	 *
	 * @throws Exception
	 */
	private void exportItemInformation() throws Exception {

		System.out.println("Export item information");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> scopeparams = new ArrayList<Object>();
		List<Object> orgparams = new ArrayList<Object>();
		String orgquery = "update organisation SET ";
		String scopequery = "update scope SET ";
		int orgcounter = 0;
		int scopecounter = 0;

		// ****************************************************************
		// * insert data into scope or organisation table
		// ****************************************************************

		// Loop item information
		for (ItemInformation information : this.analysis.getItemInformations()) {

			// ****************************************************************
			// * insert item information
			// ****************************************************************

			// check if orgsanisation -> YES
			if (information.getType().equals(Constant.ITEMINFORMATION_ORGANISATION)) {

				// ****************************************************************
				// create query to send multiple inserts in the least number of
				// queries
				// ****************************************************************

				// limit reached ? -> YES
				if (orgcounter + 1 >= 999) {

					// execute query
					orgquery = orgquery.substring(0, orgquery.length() - 1);
					sqlite.query(orgquery, orgparams);

					// clean parameters
					orgparams.clear();

					// reset query
					orgquery = "UPDATE organisation SET ";
					orgcounter = 0;
				} else {

					// limit reached -> NO

					// set value
					orgquery += information.getDescription() + "=?,";

					// increment limit
					orgcounter++;
				}

				// add parameters
				orgparams.add(information.getValue());
			} else {

				// check if orgsanisation -> NO -> scope

				// check if limit is reached -> YES
				if (scopecounter + 1 >= 999) {

					// execute query
					scopequery = scopequery.substring(0, scopequery.length() - 1);
					sqlite.query(scopequery, scopeparams);

					// clean parameters
					scopeparams.clear();

					// reset query and limit
					scopequery = "UPDATE scope SET ";
					scopecounter = 0;
				} else {

					// limit reached -> NO

					// add values
					scopequery += information.getDescription() + "=?,";

					// increment limit
					scopecounter++;
				}

				// add parameters
				scopeparams.add(information.getValue());
			}
		}

		// ****************************************************************
		// add last part of data to sqlite file
		// ****************************************************************

		// execute scope query
		scopequery = scopequery.substring(0, scopequery.length() - 1);
		sqlite.query(scopequery, scopeparams);

		// execute organisation query
		orgquery = orgquery.substring(0, orgquery.length() - 1);
		sqlite.query(orgquery, orgparams);
	}

	/**
	 * exportPhase: <br>
	 * Exports the Phases to an Sqlite File using an Sqlite Database Handler.
	 *
	 * @throws Exception
	 */
	private void exportPhase() throws Exception {

		System.out.println("Export phase");

		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> params = new ArrayList<Object>();
		String query = "";

		// ****************************************************************
		// * retrieve phases to export
		// ****************************************************************

		// if phase is valid -> YES
		for (Phase phase : this.analysis.getPhases()) {

			// ****************************************************************
			// * export current phase
			// ****************************************************************

			// should not save
			if (phase.getNumber() == 0)
				continue;

			// build query
			query = DatabaseHandler.generateInsertQuery("info_phases", 3);

			// add parameters
			params.clear();
			params.add(phase.getNumber());
			params.add(dateFormat.format(phase.getBeginDate()));
			params.add(dateFormat.format(phase.getEndDate()));

			// execute the query
			sqlite.query(query, params);
		}
	}

	/**
	 * exportIdentifier: <br>
	 * Exports the identifier to an sqlite file using an sqlite database handler.
	 *
	 * @throws Exception
	 */
	private void exportIdentifier() throws Exception {

		System.out.println("Export identifier");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> params = new ArrayList<Object>();
		String query = "";

		// ****************************************************************
		// * export identifier
		// ****************************************************************

		// build query
		query = "INSERT INTO identifier (id_analysis,label,analysis_type,uncertainty) VALUES(?,?,?,?)";
		// add parameters
		params.add(this.analysis.getIdentifier());
		params.add(this.analysis.getLabel());
		params.add(this.analysis.getType());
		params.add(this.analysis.isUncertainty());
		// execute the query
		sqlite.query(query, params);
	}

	/**
	 * exportHistory: <br>
	 * Exports the History to an Sqlite File using an Sqlite Database Handler.
	 *
	 * @throws Exception
	 */
	private void exportHistory() throws Exception {

		System.out.println("Export history");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************

		String query = "";
		List<Object> params = new ArrayList<Object>();
		// add date of the comment
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		// ****************************************************************
		// * parse all history entries
		// ****************************************************************

		// Loop histories
		for (History history : this.analysis.getHistories()) {
			// ****************************************************************
			// * export current history entry
			// ****************************************************************

			// build query
			query = DatabaseHandler.generateInsertQuery("history", 4);
			// add parameters
			params.add(history.getVersion());
			params.add(dateFormat.format(history.getDate()));
			params.add(history.getAuthor());
			params.add(history.getComment());
			// execute the query
			sqlite.query(query, params);
			params.clear();
		}
	}

	/**
	 * exportSimpleParameters: <br>
	 * Export Simple Parameters to an Sqlite File usaing a Sqlite Database Hanlder.
	 *
	 * @throws Exception
	 */
	private void exportSimpleParameters() throws Exception {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> params = new ArrayList<Object>();
		String query = "";

		// ****************************************************************
		// * parse parameters and export simple parameters
		// ****************************************************************

		// parse parameters
		for (SimpleParameter parameter : analysis.getSimpleParameters()) {

			// ****************************************************************
			// * export max effency parameter
			// ****************************************************************

			// check if max efficiency -> YES
			if (parameter.getTypeName().equals(Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME)) {

				// ****************************************************************
				// * export parameter
				// ****************************************************************
				// build query
				query = DatabaseHandler.generateInsertQuery("maturity_max_eff", 2);

				// add parameters
				params.clear();
				params.add(parameter.getDescription().substring(parameter.getDescription().length() - 1));
				params.add(parameter.getValue().doubleValue() / 100.);

				// execute the query
				sqlite.query(query, params);
			} else if (parameter.getTypeName().equals(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME)) {

				// ****************************************************************
				// * export parameter
				// ***************************************************************
				// build query
				query = DatabaseHandler.generateInsertQuery("maturity_IS", 2);

				// add parameters
				params.clear();

				int line = 0;

				switch (parameter.getDescription()) {
				case Constant.IS_NOT_ACHIEVED:
					line = 1;
					break;
				case Constant.IS_RUDIMENTARY_ACHIEVED:
					line = 2;
					break;
				case Constant.IS_PARTIALLY_ACHIEVED:
					line = 3;
					break;
				case Constant.IS_LARGELY_ACHIEVED:
					line = 4;
					break;
				case Constant.IS_FULLY_ACHIEVED:
					line = 5;
					break;
				default:
					line = 0;
					break;
				}

				params.add(line);
				params.add(parameter.getValue().doubleValue() / 100.);

				// execute the query
				sqlite.query(query, params);
			} else if (parameter.getTypeName().equals(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME)) {
				// ****************************************************************
				// * export parameter
				// ****************************************************************

				// build query
				query = "UPDATE scope set " + parameter.getDescription() + "=?";

				// add parameters
				params.clear();

				Double value = parameter.getValue().doubleValue();
				params.add(value.intValue());

				// execute the query
				sqlite.query(query, params);
			}
		}
	}

	/**
	 * exportExtendedParameters: <br>
	 * Export Extended Parameters to an Sqlite File using a Sqlite Database Handler.
	 *
	 * @author Steve Muller (SMU), itrust consulting s.à r.l.
	 */
	private void exportProbabilityAndImpactParameter() throws Exception {
		// Export all extended parameters of type IMPACT, PROBABILITY and
		// SEVERITY
		for (IBoundedParameter boundedParameter : this.analysis.getBoundedParamters()) {
			// Determine insert query
			String query = null;
			if (boundedParameter instanceof LikelihoodParameter)
				query = DatabaseHandler.generateInsertQuery("potentiality", 8);
			else if (boundedParameter instanceof ImpactParameter)
				query = DatabaseHandler.generateInsertQuery("impact", 9);
			// Determine insert query parameters
			final List<Object> queryParameters = new ArrayList<Object>();
			queryParameters.add(null); // id
			if (boundedParameter instanceof ImpactParameter)
				queryParameters.add(boundedParameter.getTypeName());
			queryParameters.add(boundedParameter.getLevel());
			queryParameters.add(boundedParameter.getLabel());
			queryParameters.add(boundedParameter.getDescription());
			queryParameters.add(boundedParameter.getAcronym());
			queryParameters.add(boundedParameter.getValue());
			queryParameters.add(boundedParameter.getBounds().getFrom());
			queryParameters.add(boundedParameter.getBounds().getTo());
			// Execute query
			sqlite.query(query, queryParameters);
		}
	}

	/**
	 * exportDynamicParameters: <br>
	 * Export Dynamic Parameters to an Sqlite File using a Sqlite Database Handler.
	 *
	 * @author Steve Muller (SMU), itrust consulting s.à r.l.
	 */
	private void exportDynamicParameters() throws Exception {
		// Export all acronym parameters of type DYNAMIC
		for (DynamicParameter dynamicParameter : this.analysis.getDynamicParameters()) {
			// Determine insert query
			String query = DatabaseHandler.generateInsertQuery("dynamic_parameter", 4);
			// Determine insert query parameters
			final List<Object> queryParameters = new ArrayList<Object>();
			queryParameters.add(null); // id
			queryParameters.add(dynamicParameter.getDescription());
			queryParameters.add(dynamicParameter.getAcronym());
			queryParameters.add(dynamicParameter.getValue());

			// Execute query
			sqlite.query(query, queryParameters);
		}
	}

	/**
	 * exportMaturityParameters: <br>
	 * Export Maturity Parameters to an Sqlite File using a Sqlite Database Handler.
	 *
	 * @throws Exception
	 */
	private void exportMaturityParameters() throws Exception {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> params = new ArrayList<Object>();
		String query = "";
		int counter = 0;

		// ****************************************************************
		// * parse parameters and export maturtiy parameter only
		// ****************************************************************

		// parse parameters
		for (MaturityParameter maturityParameter : this.analysis.getMaturityParameters()) {

			for (int i = 0; i < 6; i++) {
				// check if first part -> YES
				if (query.equals(Constant.EMPTY_STRING)) {

					// build query
					query = "INSERT INTO maturity_required_LIPS SELECT ? as name,? as line,? ";
					query += "as SML,? as value UNION";

					// set limit
					counter = 4;
				} else {

					// check if first part -> NO

					// check if limit is reached -> YES
					if (counter + 4 >= 999) {

						// execute query
						query = query.substring(0, query.length() - 6);
						sqlite.query(query, params);

						// clean parameters
						params.clear();

						// reset query and ,limit
						query = "INSERT INTO maturity_required_LIPS SELECT ? as name,? as ";
						query += "line,?as SML,? as value UNION";
						counter = 4;
					} else {

						// check if limit is reached -> NO

						// create value
						query += " SELECT ?,?,?,? UNION";

						// increment limit
						counter += 4;
					}
				}

				// add parameters
				params.add(maturityParameter.getDescription());
				params.add(getLinefromMaturityCategory(maturityParameter.getDescription()));
				params.add(i);

				// System.out.print(maturityParameter.getDescription() +
				// ":::SML level: " + i +
				// "::: value: ");

				switch (i) {
				case 0: {
					// System.out.println(maturityParameter.getSMLLevel0());
					params.add(maturityParameter.getSMLLevel0());
					break;
				}
				case 1: {
					// System.out.println(maturityParameter.getSMLLevel1());
					params.add(maturityParameter.getSMLLevel1());
					break;
				}
				case 2: {
					// System.out.println(maturityParameter.getSMLLevel2());
					params.add(maturityParameter.getSMLLevel2());
					break;
				}
				case 3: {
					// System.out.println(maturityParameter.getSMLLevel3());
					params.add(maturityParameter.getSMLLevel3());
					break;
				}
				case 4: {
					// System.out.println(maturityParameter.getSMLLevel4());
					params.add(maturityParameter.getSMLLevel4());
					break;
				}
				case 5: {
					// System.out.println(maturityParameter.getSMLLevel5());
					params.add(maturityParameter.getSMLLevel5());
					break;
				}
				}
			}
		}
		// ****************************************************************
		// * execute last part of maturity parameters
		// ****************************************************************

		// execute the query
		if (query.endsWith("UNION"))
			query = query.substring(0, query.length() - 6);
		if (!query.isEmpty())
			sqlite.query(query, params);
	}

	/**
	 * exportParameters: <br>
	 * Exports the Simple, Extended and Maturity Parameters to an Sqlite File using
	 * an Sqlite Database Handler.
	 *
	 * @throws Exception
	 */
	private void exportParameters() throws Exception {

		System.out.println("Export Parameters");

		// ****************************************************************
		// * export simple parameters
		// ****************************************************************
		exportSimpleParameters();

		exportRiskAcceptanceParameters();

		// ****************************************************************
		// * export extended parameters
		// ****************************************************************
		exportProbabilityAndImpactParameter();

		exportImpactType();

		// ****************************************************************
		// * export dynamic parameters
		// ****************************************************************
		exportDynamicParameters();

		// ****************************************************************
		// * export maturity parameters
		// ****************************************************************
		exportMaturityParameters();
	}

	private void exportRiskAcceptanceParameters() throws SQLException {
		if (analysis.getType() == AnalysisType.QUANTITATIVE)
			return;
		List<Object> params = new ArrayList<Object>();
		String query = "", unionQuery = " UNION SELECT ?,?,?,?", baseQuery = "INSERT INTO risk_acceptance SELECT ? as label, ? as level, ? as color, ? as description";
		List<RiskAcceptanceParameter> parameters = analysis.getRiskAcceptanceParameters();
		for (RiskAcceptanceParameter parameter : parameters) {
			if (query.isEmpty())
				query = baseQuery;
			else if (params.size() + 4 > 999) {
				sqlite.query(query, params);
				query = baseQuery;
				params.clear();
			} else
				query += unionQuery;
			params.add(parameter.getLabel());
			params.add(parameter.getValue().intValue());
			params.add(parameter.getColor());
			params.add(parameter.getDescription());
		}

		if (!query.isEmpty())
			sqlite.query(query, params);

	}

	private void exportImpactType() throws SQLException {
		List<Object> params = new ArrayList<Object>();
		String query = "", unionQuery = " UNION SELECT ?,?,?,?", baseQuery = "INSERT INTO impact_type SELECT ? as name, ? as acronym,? as translation, ? as short_name";
		for (ScaleType scaleType : scaleTypes) {
			if (query.isEmpty())
				query = baseQuery;
			else if (params.size() + 4 > 999) {
				sqlite.query(query, params);
				query = baseQuery;
				params.clear();
			} else
				query += unionQuery;
			params.add(scaleType.getName());
			params.add(scaleType.getAcronym());
			Translation translate = scaleType.get(analysis.getLanguage().getAlpha2());
			if (translate == null) {
				translate = scaleType.get("EN");
				if (translate == null)
					translate = new Translation(scaleType.getDisplayName(), scaleType.getShortName());
			}
			params.add(translate.getName());
			params.add(translate.getShortName());
		}

		if (!query.isEmpty())
			sqlite.query(query, params);

	}

	/**
	 * exportAssets: <br>
	 * Exports the Assets to an Sqlite File using an Sqlite Database Handler.
	 *
	 * @throws Exception
	 */
	private void exportAssets() throws Exception {

		System.out.println("Export assets");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> params = new ArrayList<Object>();
		String query = "";
		List<AssetType> assetTypes = serviceAssetType.getAll();

		// ****************************************************************
		// * export asset types
		// ****************************************************************

		// parse asset types
		for (AssetType assetType : assetTypes) {

			// build query
			query = "INSERT INTO asset_types VALUES (?, ?)";

			// add parameters
			params.clear();
			params.add(assetType.getId());
			params.add(assetType.getName());

			// System.out.println(rs.getInt(Constant.ASSETTYPE_MYSQL_ID));

			// execute query
			sqlite.query(query, params);
		}

		// ****************************************************************
		// * export Assets
		// ****************************************************************

		final Map<Asset, List<Assessment>> assessmentsAsset = this.analysis.findAssessmentByAsset();

		// Loop assets
		for (Asset asset : this.analysis.getAssets()) {

			// ****************************************************************
			// * export asset
			// ****************************************************************

			// build query
			query = DatabaseHandler.generateInsertQuery("assets", 8);

			// add parameters
			params.clear();
			params.add(asset.getId());
			params.add(asset.getName());
			params.add(asset.getAssetType().getId());
			params.add(asset.getValue());
			params.add(asset.getComment());
			params.add(asset.getHiddenComment());
			if (asset.isSelected()) {
				params.add(Constant.ASSET_SELECTED);
			} else {
				params.add(Constant.EMPTY_STRING);
			}

			final List<Assessment> assessments = assessmentsAsset.get(asset);
			params.add(assessments == null ? 0 : assessments.stream().mapToDouble(Assessment::getALE).sum());
			// execute query
			sqlite.query(query, params);
		}
	}

	/**
	 * exportScenarios: <br>
	 * Exports the Scenarios to an Sqlite File using an Sqlite Database Handler.
	 *
	 * @throws Exception
	 */
	private void exportScenarios() throws Exception {

		System.out.println("Export Scenarios");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> params = new ArrayList<Object>();
		List<Scenario> scenarios = new LinkedList<>();

		final String[] assetTypeNames = Constant.ASSET_TYPES.split(",");
		final Map<String, AssetType> assetTypes = serviceAssetType.getAll().stream().collect(Collectors.toMap(AssetType::getName, Function.identity()));
		String query = "", unionQuery = " UNION SELECT ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
				baseQuery = "INSERT INTO threats SELECT ? as id_threat, ? as name_threat, ? as type_threat, ? as linked_asset_threat, ? as description_threat, ? as sel_threat, ? as serv, ? as info, ? as sw, ? as hw, ? as net, ? as staff, ? as iv, ? as busi, ? as fin, ? as compl, ? as confidentiality, ? as integrity, ? as availability, ? as exploitability, ? as reliability, ? as d1, ? as d2 , ? as d3, ? as d4, ? as d5, ? as d6, ? as d61, ? as d62, ? as d63, ? as d64, ? as d7, ? as i1, ? as i2, ? as i3, ? as i4, ? as i5, ? as i6, ? as i7, ? as i8, ? as i81, ? as i82, ? as i83, ? as i84, ? as i9, ? as i10, ? as preventive, ? as detective, ? as limitative, ? as corrective, ? as intentional, ? as accidental, ? as environmental, ? as internal_threat, ? as external_threat";

		// ****************************************************************
		// * export scenarios
		// ****************************************************************

		// parse scenarios
		for (Scenario scenario : this.analysis.getScenarios()) {

			if (query.isEmpty())
				query = baseQuery;
			else if (params.size() + SCENARIO_COLUMN_COUNT > 999) {
				sqlite.query(query, params);
				query = baseQuery;
				params.clear();
			} else
				query += unionQuery;

			params.add(scenario.getId());
			params.add(scenario.getName());
			params.add(scenario.getType().name());
			params.add(scenario.isAssetLinked());
			params.add(scenario.getDescription());

			if (scenario.isSelected()) {
				params.add(Constant.ASSET_SELECTED);
			} else {
				params.add(Constant.EMPTY_STRING);
			}

			if (scenario.isAssetLinked()) {
				for (int i = 0; i < assetTypeNames.length; i++)
					params.add(0);
				if (!scenario.getLinkedAssets().isEmpty())
					scenarios.add(scenario);
			} else {
				for (String key : assetTypeNames)
					params.add(scenario.hasInfluenceOnAsset(assetTypes.get(key)) ? 1 : 0);
			}
			// save Risk data
			insertCategories(params, scenario);
			params.add(scenario.getPreventive());
			params.add(scenario.getDetective());
			params.add(scenario.getLimitative());
			params.add(scenario.getCorrective());
			params.add(scenario.getIntentional());
			params.add(scenario.getAccidental());
			params.add(scenario.getEnvironmental());
			params.add(scenario.getInternalThreat());
			params.add(scenario.getExternalThreat());
		}

		// execute the query
		if (!query.isEmpty())
			sqlite.query(query, params);

		query = "";
		unionQuery = " UNION SELECT ?,?";
		baseQuery = "INSERT INTO threat_assets SELECT ? as id_threat, ? as id_asset";
		params.clear();

		for (Scenario scenario : scenarios) {
			if (query.isEmpty())
				query = baseQuery;
			else if (params.size() + 2 * scenario.getLinkedAssets().size() > 999) {
				sqlite.query(query, params);
				query = baseQuery;
				params.clear();
			} else
				query += unionQuery;
			int count = scenario.getLinkedAssets().size();
			for (Asset asset : scenario.getLinkedAssets()) {
				params.add(scenario.getId());
				params.add(asset.getId());
				if (--count > 0)
					query += unionQuery;
			}
		}
		// execute the query
		if (!query.isEmpty())
			sqlite.query(query, params);

	}

	/**
	 * exportAssessments: <br>
	 * Exports the Assessments to an Sqlite File using an Sqlite Database Handler.
	 *
	 * @throws Exception
	 */
	private void exportAssessments() throws Exception {

		System.out.println("Export assessment");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************

		Map<Integer, Double> totalALEs = new HashMap<Integer, Double>();

		for (Assessment assessment : this.analysis.getAssessments()) {

			Integer key = assessment.getAsset().getId();

			double ale = assessment.getImpactReal() * assessment.getLikelihoodReal();

			double totalALE = (totalALEs.containsKey(key) ? totalALEs.get(key) + ale : ale);

			totalALEs.put(key, totalALE);
		}

		List<Object> params = new ArrayList<Object>();
		String query = "", unionQuery = " UNION SELECT ?,?,?,?,?,?,?,?,?,?,?",
				baseQuery = "INSERT INTO Assessment SELECT ? as id_asset, ? as id_threat,? as selected, ? as impact_hidden,? as potentiality,? as potentiality_hidden,? as comment,? as comment_2, ? as owner,"
						+ "? as total_ALE,? as uncertainty";
		// ****************************************************************
		// * export assessment
		// ****************************************************************

		// parse assessment
		for (Assessment assessment : analysis.getAssessments()) {
			if (query.isEmpty())
				query = baseQuery;
			else if (params.size() + 11 > 999) {
				sqlite.query(query, params);
				query = baseQuery;
				params.clear();
			} else
				query += unionQuery;
			Integer key = assessment.getAsset().getId();
			// add parameters
			params.add(assessment.getAsset().getId());
			params.add(assessment.getScenario().getId());
			params.add(assessment.isSelected() ? Constant.ASSESSMENT_SELECTED : Constant.EMPTY_STRING);
			params.add(assessment.getImpactReal());
			if (assessment.getLikelihood() == null)
				params.add("0");
			else
				params.add(assessment.getLikelihood().getRaw() + "");
			params.add(assessment.getLikelihoodReal());
			params.add(assessment.getComment());
			params.add(assessment.getHiddenComment());
			params.add(assessment.getOwner());
			params.add(totalALEs.get(key));
			params.add(assessment.getUncertainty());
		}
		// execute the query
		if (!query.isEmpty())
			sqlite.query(query, params);

		query = "";
		unionQuery = " UNION SELECT ?,?,?,?";
		baseQuery = "INSERT INTO assessment_impacts SELECT ? as id_asset, ? as id_threat,? as name,? as value";
		params.clear();
		for (Assessment assessment : analysis.getAssessments()) {
			if (query.isEmpty())
				query = baseQuery;
			else if (params.size() + (assessment.getImpacts().size() * 4) > 999) {
				sqlite.query(query, params);
				query = baseQuery;
				params.clear();
			} else
				query += unionQuery;
			int count = assessment.getImpacts().size();
			for (IValue value : assessment.getImpacts()) {
				params.add(assessment.getAsset().getId());
				params.add(assessment.getScenario().getId());
				params.add(value.getName());
				params.add(value.getReal());
				if (--count > 0)
					query += unionQuery;
			}

		}
		// execute the query
		if (!query.isEmpty())
			sqlite.query(query, params);

	}

	/**
	 * exportMeasuresAndMaturity: <br>
	 * Exports the Measures to an Sqlite File using an Sqlite Database Handlerl.
	 *
	 * @throws Exception
	 */
	private void exportMeasuresAndMaturity() throws Exception {

		System.out.println("Export measures");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> measureparams = new ArrayList<Object>(), specparams = new ArrayList<Object>(), defaultspecparams = new ArrayList<Object>();
		MaturityStandard maturityStandard = null;
		MaturityMeasure maturity = null;
		String measurequery = "", specdefaultquery = "";
		int measurecounter = 0, specdefaultcounter = 0, measureIndex = 1;
		List<IAcronymParameter> expressionParameters = analysis.getExpressionParameters();

		// ****************************************************************
		// * export standard measures (27001, 27002, custom)
		// ****************************************************************

		// parse standards
		for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards().values()) {

			// ****************************************************************
			// * retrieve standard that is not maturity
			// ****************************************************************

			// ****************************************************************
			// standard not maturity -> YES
			// ****************************************************************
			if (analysisStandard instanceof NormalStandard) {

				NormalStandard normalStandard = (NormalStandard) analysisStandard;

				// store standard as measurestandard

				// ****************************************************************
				// * parse measures of this standard
				// ****************************************************************

				// reinitialise variables
				measureparams.clear();
				specparams.clear();
				defaultspecparams.clear();
				measurequery = "";
				specdefaultquery = "";

				// parse measures
				for (int index = 0; index < normalStandard.getMeasures().size(); index++) {

					// store measure
					NormalMeasure measure = normalStandard.getMeasure(index);

					// ****************************************************************
					// * export measure
					// ****************************************************************

					// check if first part of query -> YES
					if (measurequery.isEmpty()) {

						// build query
						measurequery = "INSERT INTO measures SELECT ? as 'id_norme',? as 'name_norme', ? as 'version_norme', ? as 'norme_description', ? as 'norme_type',"
								+ "? as 'norme_computable',? as 'norme_analysisOnly',? as 'ref_measure',? as 'measure_computable',? as 'domain_measure',"
								+ "? as 'question_measure',? as 'strength_measure',? as 'strength_sectoral',? as 'confidentiality',"
								+ "? as 'integrity',? as 'availability',? as `exploitability`, ? as `reliability`,? as 'd1',? as 'd2',? as 'd3',? as 'd4',? as 'd5',? as 'd6',"
								+ "? as 'd61',? as 'd62',? as 'd63',? as 'd64',? as 'd7',? as 'i1',? as 'i2',? as 'i3',? as 'i4',"
								+ "? as 'i5',? as 'i6',? as 'i7',? as 'i8',? as 'i81',? as 'i82',? as 'i83',? as 'i84',? as 'i9',"
								+ "? as 'i10',? as 'preventive',? as 'detective',? as 'limiting',? as 'corrective',? as 'intentional',"
								+ "? as 'accidental',? as 'environmental',? as 'internal_threat',? as 'external_threat',? as 'internal_setup',"
								+ "? as 'external_setup',? as 'investment',? as 'lifetime',? as 'internal_maintenance',? as 'external_maintenance',"
								+ "? as 'recurrent_investment',? as 'implmentation_rate',? as 'status',? as 'comment',? as 'todo',? as 'revision',"
								+ "? as 'responsible',? as 'phase',? as 'soa_reference',? as 'soa_risk',? as 'soa_comment',? as 'index2' UNION";

						// System.out.println(measurequery);

						// set ? limit
						measurecounter = MEASURE_ROW_COUNT;
					} else {

						// check if first part of query -> NO

						// limit reached ? -> YES
						if (measurecounter + MEASURE_ROW_COUNT >= 999) {

							// execute query
							measurequery = measurequery.substring(0, measurequery.length() - 6);
							sqlite.query(measurequery, measureparams);

							// clean parameters
							measureparams.clear();

							// reset query
							measurequery = "INSERT INTO measures SELECT ? as 'id_norme', ? as 'name_norme',? as 'version_norme',? as 'norme_description',? as 'norme_type',"
									+ "? as 'norme_computable',? as 'norme_analysisOnly',? as 'ref_measure',? as 'measure_computable',? as 'domain_measure',"
									+ "? as 'question_measure',? as 'strength_measure',? as 'strength_sectoral',? as 'confidentiality',"
									+ "? as 'integrity',? as 'availability', ? as `exploitability`, ? as `reliability`, ? as 'd1',? as 'd2',? as 'd3',? as 'd4',? as 'd5',? as 'd6',"
									+ "? as 'd61',? as 'd62',? as 'd63',? as 'd64',? as 'd7',? as 'i1',? as 'i2',? as 'i3',? as 'i4',"
									+ "? as 'i5',? as 'i6',? as 'i7',? as 'i8',? as 'i81',? as 'i82',? as 'i83',? as 'i84',? as 'i9',"
									+ "? as 'i10',? as 'preventive',? as 'detective',? as 'limiting',? as 'corrective',? as 'intentional',"
									+ "? as 'accidental',? as 'environmental',? as 'internal_threat',? as 'external_threat',? as 'internal_setup',"
									+ "? as 'external_setup',? as 'investment',? as 'lifetime',? as 'internal_maintenance',? as 'external_maintenance',"
									+ "? as 'recurrent_investment',? as 'implmentation_rate',? as 'status',? as 'comment',? as 'todo',? as 'revision',"
									+ "? as 'responsible',? as 'phase',? as 'soa_reference',? as 'soa_risk',? as 'soa_comment',? as 'index2' UNION";

							// reset limit counter
							measurecounter = MEASURE_ROW_COUNT;
						} else {

							// limit reached ? -> NO

							// add data to query
							measurequery += " SELECT ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? UNION";

							// increment limit
							measurecounter += MEASURE_ROW_COUNT;
						}
					}

					// ****************************************************************
					// * add params to query
					// ****************************************************************

					// for standard
					measureparams.add(normalStandard.getStandard().getLabel());
					measureparams.add(normalStandard.getStandard().getName());
					measureparams.add(normalStandard.getStandard().getVersion());
					measureparams.add(normalStandard.getStandard().getDescription());
					measureparams.add(normalStandard.getStandard().getType().name());
					measureparams.add(normalStandard.getStandard().isComputable());
					measureparams.add(normalStandard.getStandard().isAnalysisOnly());

					// for measure
					measureparams.add(measure.getMeasureDescription().getReference());
					measureparams.add(measure.getMeasureDescription().isComputable());
					MeasureDescriptionText descriptionText = measure.getMeasureDescription().getAMeasureDescriptionText(this.analysis.getLanguage());
					measureparams.add(descriptionText == null ? "" : descriptionText.getDomain());
					measureparams.add(descriptionText == null ? "" : descriptionText.getDescription());
					measureparams.add(measure.getMeasurePropertyList().getFMeasure());
					measureparams.add(measure.getMeasurePropertyList().getFSectoral());
					insertCategories(measureparams, measure.getMeasurePropertyList());
					measureparams.add(measure.getMeasurePropertyList().getPreventive());
					measureparams.add(measure.getMeasurePropertyList().getDetective());
					measureparams.add(measure.getMeasurePropertyList().getLimitative());
					measureparams.add(measure.getMeasurePropertyList().getCorrective());
					measureparams.add(measure.getMeasurePropertyList().getIntentional());
					measureparams.add(measure.getMeasurePropertyList().getAccidental());
					measureparams.add(measure.getMeasurePropertyList().getEnvironmental());
					measureparams.add(measure.getMeasurePropertyList().getInternalThreat());
					measureparams.add(measure.getMeasurePropertyList().getExternalThreat());
					measureparams.add(measure.getInternalWL());
					measureparams.add(measure.getExternalWL());
					measureparams.add(measure.getInvestment());
					measureparams.add(measure.getLifetime());
					measureparams.add(measure.getInternalMaintenance());
					measureparams.add(measure.getExternalMaintenance());
					measureparams.add(measure.getRecurrentInvestment());
					measureparams.add(measure.getImplementationRate());
					measureparams.add(measure.getStatus());
					measureparams.add(measure.getComment());
					measureparams.add(measure.getToDo());
					measureparams.add(measure.getToCheck());
					measureparams.add(measure.getResponsible());
					measureparams.add(measure.getPhase().getNumber());
					measureparams.add(measure.getMeasurePropertyList().getSoaReference());
					measureparams.add(measure.getMeasurePropertyList().getSoaRisk());
					measureparams.add(measure.getMeasurePropertyList().getSoaComment());
					measureparams.add(measureIndex++);

					// ****************************************************************
					// * export asset type values
					// ****************************************************************

					for (int indexAssetTypeValue = 0; indexAssetTypeValue < measure.getAssetTypeValues().size(); indexAssetTypeValue++) {

						// store asset type value object
						AssetTypeValue assetTypeValue = measure.getAssetTypeValue(indexAssetTypeValue);

						// ****************************************************************
						// * export asset type value into
						// spec_default_type_asset_measure sqlite
						// table
						// ****************************************************************

						// build query

						// check if first part -> YES
						if (specdefaultquery.isEmpty()) {

							// set query
							specdefaultquery = "INSERT INTO spec_type_asset_measure SELECT ? as 'id_type_asset', ? as 'id_norme', ? as 'version_norme', ? as 'ref_measure', ? as 'value_spec' UNION";

							// set limit
							specdefaultcounter = 5;

						} else {

							// check if first part -> NO

							// check if limit reached -> YES
							if (specdefaultcounter + 5 >= 999) {

								// execute query
								specdefaultquery = specdefaultquery.substring(0, specdefaultquery.length() - 6);
								sqlite.query(specdefaultquery, defaultspecparams);

								// reset parameters
								defaultspecparams.clear();

								// reset query
								specdefaultquery = "INSERT INTO spec_type_asset_measure SELECT ? as 'id_type_asset', ? as 'id_norme', ? as 'version_norme', ? as 'ref_measure', ? as 'value_spec' UNION";

								// reset limit
								specdefaultcounter = 5;
							} else {

								// check if limit reached -> NO

								// add insert data to query
								specdefaultquery += " SELECT ?, ?, ?, ?, ? UNION";

								// incrment limit
								specdefaultcounter += 5;
							}
						}

						// add parameters
						defaultspecparams.add(assetTypeValue.getAssetType().getId());

						defaultspecparams.add(normalStandard.getStandard().getLabel());

						defaultspecparams.add(normalStandard.getStandard().getVersion());

						defaultspecparams.add(measure.getMeasureDescription().getReference());

						defaultspecparams.add(assetTypeValue.getValue());

					}
				}

				// ****************************************************************
				// * execute last part of inserts of measures and asset type
				// values
				// ****************************************************************

				// remove UNION from queries
				if (measurequery.endsWith("UNION"))
					measurequery = measurequery.substring(0, measurequery.length() - 6);
				if (specdefaultquery.endsWith("UNION"))
					specdefaultquery = specdefaultquery.substring(0, specdefaultquery.length() - 6);

				// execute the query
				if (!measurequery.isEmpty())
					sqlite.query(measurequery, measureparams);

				if (!specdefaultquery.isEmpty())
					// execute the query
					sqlite.query(specdefaultquery, defaultspecparams);

			} else if (analysisStandard instanceof AssetStandard) {

				// store standard as measurestandard
				AssetStandard assetstandard = (AssetStandard) analysisStandard;

				// ****************************************************************
				// * parse measures of this standard
				// ****************************************************************

				// reinitialise variables
				measureparams.clear();
				defaultspecparams.clear();
				measurequery = "";
				specdefaultquery = "";

				// parse measures
				for (int index = 0; index < assetstandard.getMeasures().size(); index++) {

					// store measure
					AssetMeasure measure = assetstandard.getMeasure(index);

					// ****************************************************************
					// * export measure
					// ****************************************************************

					// check if first part of query -> YES
					if (measurequery.isEmpty()) {

						// build query
						measurequery = "INSERT INTO measures SELECT ? as 'id_norme',? as 'name_norme',? as 'version_norme',? as 'norme_description',? as 'norme_type',"
								+ "? as 'norme_computable',? as 'norme_analysisOnly',? as 'ref_measure',? as 'measure_computable',? as 'domain_measure',"
								+ "? as 'question_measure',? as 'strength_measure',? as 'strength_sectoral',? as 'confidentiality',? as 'integrity',"
								+ "? as 'availability' , ? as `exploitability`, ? as `reliability` ,? as 'd1',? as 'd2',? as 'd3',? as 'd4',? as 'd5',"
								+ "? as 'd6',? as 'd61',? as 'd62',? as 'd63',? as 'd64',? as 'd7',? as 'i1',? as 'i2',? as 'i3',"
								+ "? as 'i4',? as 'i5',? as 'i6',? as 'i7',? as 'i8',? as 'i81',? as 'i82',? as 'i83',? as 'i84',"
								+ "? as 'i9',? as 'i10',? as 'preventive',? as 'detective',? as 'limiting',? as 'corrective',? as 'intentional',"
								+ "? as 'accidental',? as 'environmental',? as 'internal_threat',? as 'external_threat',? as 'internal_setup',"
								+ "? as 'external_setup',? as 'investment',? as 'lifetime',? as 'internal_maintenance',? as 'external_maintenance',"
								+ "? as 'recurrent_investment',? as 'implmentation_rate',? as 'status',? as 'comment',? as 'todo',? as 'revision',"
								+ "? as 'responsible',? as 'phase',? as 'soa_reference',? as 'soa_risk',? as 'soa_comment',? as 'index2' UNION";

						// set ? limit
						measurecounter = MEASURE_ROW_COUNT;
					} else {

						// check if first part of query -> NO

						// limit reached ? -> YES
						if (measurecounter + MEASURE_ROW_COUNT >= 999) {

							// execute query
							measurequery = measurequery.substring(0, measurequery.length() - 6);
							sqlite.query(measurequery, measureparams);

							// clean parameters
							measureparams.clear();

							// reset query
							measurequery = "INSERT INTO measures SELECT ? as 'id_norme',? as 'name_norme',? as 'version_norme',? as 'norme_description',? as 'norme_type',"
									+ "? as 'norme_computable',? as 'norme_analysisOnly',? as 'ref_measure',? as 'measure_computable',? as 'domain_measure',"
									+ "? as 'question_measure',? as 'strength_measure',? as 'strength_sectoral',? as 'confidentiality',? as 'integrity',"
									+ "? as 'availability', ? as `exploitability`, ? as `reliability`,? as 'd1',? as 'd2',? as 'd3',? as 'd4',? as 'd5',"
									+ "? as 'd6',? as 'd61',? as 'd62',? as 'd63',? as 'd64',? as 'd7',? as 'i1',? as 'i2',? as 'i3',"
									+ "? as 'i4',? as 'i5',? as 'i6',? as 'i7',? as 'i8',? as 'i81',? as 'i82',? as 'i83',? as 'i84',"
									+ "? as 'i9',? as 'i10',? as 'preventive',? as 'detective',? as 'limiting',? as 'corrective',? as 'intentional',"
									+ "? as 'accidental',? as 'environmental',? as 'internal_threat',? as 'external_threat',? as 'internal_setup',"
									+ "? as 'external_setup',? as 'investment',? as 'lifetime',? as 'internal_maintenance',? as 'external_maintenance',"
									+ "? as 'recurrent_investment',? as 'implmentation_rate',? as 'status',? as 'comment',? as 'todo',? as 'revision',"
									+ "? as 'responsible',? as 'phase',? as 'soa_reference',? as 'soa_risk',? as 'soa_comment',? as 'index2' UNION";

							// reset limit counter
							measurecounter = MEASURE_ROW_COUNT;
						} else {

							// limit reached ? -> NO

							// add data to query
							measurequery += " SELECT ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? UNION";

							// increment limit
							measurecounter += MEASURE_ROW_COUNT;
						}
					}

					// ****************************************************************
					// * add params to query
					// ****************************************************************

					// for standard
					measureparams.add(assetstandard.getStandard().getLabel());
					measureparams.add(assetstandard.getStandard().getName());
					measureparams.add(assetstandard.getStandard().getVersion());
					measureparams.add(assetstandard.getStandard().getDescription());
					measureparams.add(assetstandard.getStandard().getType().name());
					measureparams.add(assetstandard.getStandard().isComputable());
					measureparams.add(assetstandard.getStandard().isAnalysisOnly());

					// for measure
					measureparams.add(measure.getMeasureDescription().getReference());
					measureparams.add(measure.getMeasureDescription().isComputable());
					MeasureDescriptionText descriptionText = measure.getMeasureDescription().getAMeasureDescriptionText(this.analysis.getLanguage());
					measureparams.add(descriptionText == null ? "" : descriptionText.getDomain());
					measureparams.add(descriptionText == null ? "" : descriptionText.getDescription());
					measureparams.add(measure.getMeasurePropertyList().getFMeasure());
					measureparams.add(measure.getMeasurePropertyList().getFSectoral());
					insertCategories(measureparams, measure.getMeasurePropertyList());
					measureparams.add(measure.getMeasurePropertyList().getPreventive());
					measureparams.add(measure.getMeasurePropertyList().getDetective());
					measureparams.add(measure.getMeasurePropertyList().getLimitative());
					measureparams.add(measure.getMeasurePropertyList().getCorrective());
					measureparams.add(measure.getMeasurePropertyList().getIntentional());
					measureparams.add(measure.getMeasurePropertyList().getAccidental());
					measureparams.add(measure.getMeasurePropertyList().getEnvironmental());
					measureparams.add(measure.getMeasurePropertyList().getInternalThreat());
					measureparams.add(measure.getMeasurePropertyList().getExternalThreat());
					measureparams.add(measure.getInternalWL());
					measureparams.add(measure.getExternalWL());
					measureparams.add(measure.getInvestment());
					measureparams.add(measure.getLifetime());
					measureparams.add(measure.getInternalMaintenance());
					measureparams.add(measure.getExternalMaintenance());
					measureparams.add(measure.getRecurrentInvestment());
					measureparams.add(measure.getImplementationRate());
					measureparams.add(measure.getStatus());
					measureparams.add(measure.getComment());
					measureparams.add(measure.getToDo());
					measureparams.add(measure.getToCheck());
					measureparams.add(measure.getResponsible());
					measureparams.add(measure.getPhase().getNumber());
					measureparams.add(measure.getMeasurePropertyList().getSoaReference());
					measureparams.add(measure.getMeasurePropertyList().getSoaRisk());
					measureparams.add(measure.getMeasurePropertyList().getSoaComment());
					measureparams.add(measureIndex++);

					// ****************************************************************
					// * export asset values
					// ****************************************************************

					for (int indexAssetTypeValue = 0; indexAssetTypeValue < measure.getMeasureAssetValues().size(); indexAssetTypeValue++) {

						// store asset type value object
						MeasureAssetValue assetValue = measure.getAssetValue(indexAssetTypeValue);

						// ****************************************************************
						// * export asset type value into
						// spec_default_type_asset_measure sqlite
						// table
						// ****************************************************************

						// build query

						// check if first part -> YES
						if (specdefaultquery.isEmpty()) {

							// set query
							specdefaultquery = "INSERT INTO spec_asset_measure SELECT ? as 'id_asset', ? as 'id_norme', ? as 'version_norme', ? as 'ref_measure', ? as 'value_spec' UNION";

							// set limit
							specdefaultcounter = 5;

						} else {

							// check if first part -> NO

							// check if limit reached -> YES
							if (specdefaultcounter + 5 >= 999) {

								// execute query
								specdefaultquery = specdefaultquery.substring(0, specdefaultquery.length() - 6);
								sqlite.query(specdefaultquery, defaultspecparams);

								// reset parameters
								defaultspecparams.clear();

								// reset query
								specdefaultquery = "INSERT INTO spec_asset_measure SELECT ? as 'id_asset', ? as 'id_norme', ? as 'version_norme', ? as 'ref_measure', ? as 'value_spec' UNION";

								// reset limit
								specdefaultcounter = 5;
							} else {

								// check if limit reached -> NO

								// add insert data to query
								specdefaultquery += " SELECT ?, ?, ?, ?, ? UNION";

								// incrment limit
								specdefaultcounter += 5;
							}
						}

						// add parameters
						defaultspecparams.add(assetValue.getAsset().getId());

						defaultspecparams.add(assetstandard.getStandard().getLabel());

						defaultspecparams.add(assetstandard.getStandard().getVersion());

						defaultspecparams.add(measure.getMeasureDescription().getReference());

						defaultspecparams.add(assetValue.getValue());

						// check if measure is level 3 -> YES

					}
				}

				// ****************************************************************
				// * execute last part of inserts of measures and asset type
				// values
				// ****************************************************************

				// remove UNION from queries
				if (measurequery.endsWith("UNION"))
					measurequery = measurequery.substring(0, measurequery.length() - 6);

				if (specdefaultquery.endsWith("UNION"))
					specdefaultquery = specdefaultquery.substring(0, specdefaultquery.length() - 6);

				// execute the query
				if (!measurequery.isEmpty())
					sqlite.query(measurequery, measureparams);

				if (!specdefaultquery.isEmpty())
					// execute the query
					sqlite.query(specdefaultquery, defaultspecparams);

			} else if (analysisStandard instanceof MaturityStandard) {

				// ****************************************************************
				// standard not maturity -> NO
				// ****************************************************************

				// store maturity standard
				maturityStandard = (MaturityStandard) analysisStandard;

				// ****************************************************************
				// * parse all maturity measures
				// ****************************************************************

				// reinitialise variables
				measureparams.clear();
				specparams.clear();
				measurequery = "";
				measurecounter = 0;

				// parse measures
				for (int index = 0; index < maturityStandard.getMeasures().size(); index++) {

					// store maturity measure
					maturity = (MaturityMeasure) maturityStandard.getMeasure(index);

					// ****************************************************************
					// * export measure
					// ****************************************************************

					// check if first part -> YES
					if (measurequery.isEmpty()) {

						// set query
						measurequery = "INSERT INTO maturities SELECT ? as 'name_norme', ? as 'version_norme', ? as 'norme_description', ? as 'norm_computable',? as 'ref',? as 'measure_computable',? as";
						measurequery += " 'domain',? as 'phase',? as 'status',? as 'rate',? as 'intwl',? as 'extwl',? as 'investment',? as 'lifetime', ? as ";
						measurequery += "'internal_maintenance',? as 'external_maintenance',? as 'recurrent_investment',? as 'comment',? as 'todo', ? as 'responsible',? as 'sml1',? as 'sml2',? as 'sml3',";
						measurequery += "? as 'sml4',? as 'sml5',? as 'index2',? as 'reached' UNION";

						// set limit
						measurecounter = MATURITY_MEASURE_ROW_COUNT;

					} else {

						// check if first part -> NO

						// check if limit reached -> YES
						if (measurecounter + MATURITY_MEASURE_ROW_COUNT >= 999) {

							// execute query
							measurequery = measurequery.substring(0, measurequery.length() - 6);
							sqlite.query(measurequery, measureparams);

							// clean parameters
							measureparams.clear();

							// reset query
							measurequery = "INSERT INTO maturities SELECT ? as 'name_norme', ? as 'version_norme', ? as 'norme_description', ? as 'norm_computable',? as 'ref',? as 'measure_computable',? as";
							measurequery += " 'domain',? as 'phase',? as 'status',? as 'rate',? as 'intwl',? as 'extwl',? as 'investment',? as 'lifetime', ";
							measurequery += "? as 'internal_maintenance',? as 'external_maintenance',? as 'recurrent_investment',? as 'comment',? as 'todo',? as 'responsible',? as 'sml1',? as 'sml2',? as 'sml3',";
							measurequery += "? as 'sml4',? as 'sml5',? as 'index2',? as 'reached' UNION";

							// reset limit
							measurecounter = MATURITY_MEASURE_ROW_COUNT;
						} else {

							// limit reached -> NO

							// add insert data to query
							measurequery += " SELECT ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? UNION";

							// increment limit
							measurecounter += MATURITY_MEASURE_ROW_COUNT;
						}
					}

					// add parameters
					measureparams.add(maturity.getAnalysisStandard().getStandard().getName());
					measureparams.add(maturity.getAnalysisStandard().getStandard().getVersion());
					measureparams.add(maturity.getAnalysisStandard().getStandard().getDescription());
					measureparams.add(maturity.getAnalysisStandard().getStandard().isComputable());
					measureparams.add(maturity.getMeasureDescription().getReference());
					measureparams.add(maturity.getMeasureDescription().isComputable());
					MeasureDescriptionText descriptionText = maturity.getMeasureDescription().getAMeasureDescriptionText(this.analysis.getLanguage());
					measureparams.add(descriptionText == null ? "" : descriptionText.getDomain());
					measureparams.add(maturity.getPhase().getNumber());
					measureparams.add(maturity.getStatus());
					measureparams.add(maturity.getImplementationRateValue(expressionParameters) / 100.0);
					measureparams.add(maturity.getInternalWL());
					measureparams.add(maturity.getExternalWL());
					measureparams.add(maturity.getInvestment());
					measureparams.add(maturity.getLifetime());
					measureparams.add(maturity.getInternalMaintenance());
					measureparams.add(maturity.getExternalMaintenance());
					measureparams.add(maturity.getRecurrentInvestment());
					measureparams.add(maturity.getComment());
					measureparams.add(maturity.getToDo());
					measureparams.add(maturity.getResponsible());
					measureparams.add(maturity.getSML1Cost());
					measureparams.add(maturity.getSML2Cost());
					measureparams.add(maturity.getSML3Cost());
					measureparams.add(maturity.getSML4Cost());
					measureparams.add(maturity.getSML5Cost());
					measureparams.add(measureIndex++);
					measureparams.add(maturity.getReachedLevel());
				}

				// ****************************************************************
				// * execute last insert query
				// ****************************************************************

				if (measurequery.endsWith("UNION"))
					// remove UNION from query
					measurequery = measurequery.substring(0, measurequery.length() - 6);

				if (!measurequery.isEmpty())
					// execute the query
					sqlite.query(measurequery, measureparams);
			}
		}
	}

	/**
	 * exportActionPlans: <br>
	 * Exports the Action Plans to an Sqlite File using an Sqlite Database Handler.
	 *
	 * @throws Exception
	 */
	private void exportActionPlans() throws Exception {

		System.out.println("Export actionplans");

		// ****************************************************************
		// * export action plan types
		// ****************************************************************
		sqlite.query("INSERT INTO ActionPlanType SELECT 1 as 'idActionPlanType','APN' as 'dtLabel'" + "UNION SELECT 2,'APO' UNION SELECT 3,'APP' UNION SELECT 4,'APPN' UNION "
				+ "SELECT 5,'APPO' UNION SELECT 6,'APPP'", null);

		// ****************************************************************
		// * export action plans
		// ****************************************************************
		// System.out.println("export action plan phase normal");
		exportActionPlan(this.analysis.getActionPlans());
	}

	/**
	 * exportActionPlanAssets: <br>
	 * Exports for a given Action Plan Entry all Assets with the Current ALE.
	 *
	 * @param type            The Action Plan Type ID
	 * @param actionPlanEntry The Action Plan Entry
	 *
	 * @throws Exception
	 */
	private void exportActionPlanAssets(ActionPlanEntry actionPlanEntry) throws Exception {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> assetparams = new ArrayList<Object>();
		String assetquery = "";
		int assetcounter = 0;

		for (int indexAssets = 0; indexAssets < actionPlanEntry.getActionPlanAssets().size(); indexAssets++) {

			// ****************************************************************
			// * export ALE value for entry
			// ****************************************************************

			// build query

			// check if first part -> YES
			if (assetquery.equals(Constant.EMPTY_STRING)) {

				// set first part of query
				assetquery = "INSERT INTO actionplanasset SELECT ? as idActionPlanAssetCalculation, ? as idActionPlanCalculation, ? as idActionPlanType, ? as idNorm, ? as dtNormVersion,";
				assetquery += "? as idMeasureActionPlan, ? as idAsset,? as dtCurrentALE UNION";

				// set limit
				assetcounter = 8;
			} else {

				// check if first part -> NO

				// check if limit reached -> YES
				if (assetcounter + 8 >= 999) {

					// execute query
					assetquery = assetquery.substring(0, assetquery.length() - 6);
					sqlite.query(assetquery, assetparams);

					// reset parameters
					assetparams.clear();

					// reset first part of query
					assetquery = "INSERT INTO actionplanasset SELECT ? as idActionPlanAssetCalculation, ? as idActionPlanCalculation, ? as idActionPlanType, ? as idNorm, ? as dtNormVersion,";
					assetquery += "? as idMeasureActionPlan, ? as idAsset,? as dtCurrentALE UNION";

					// reset limit
					assetcounter = 8;
				} else {

					// check if limit reached -> NO

					// add insert values
					assetquery += " SELECT ?,?,?,?,?,?,?,? UNION";

					// increment counter
					assetcounter += 8;
				}
			}

			// add parameters
			assetparams.add(actionPlanEntry.getActionPlanAsset(indexAssets).getId());
			assetparams.add(actionPlanEntry.getId());
			assetparams.add(actionPlanEntry.getActionPlanType().getId());
			assetparams.add(actionPlanEntry.getMeasure().getAnalysisStandard().getStandard().getLabel());
			assetparams.add(actionPlanEntry.getMeasure().getAnalysisStandard().getStandard().getVersion());
			assetparams.add(actionPlanEntry.getMeasure().getMeasureDescription().getReference());
			assetparams.add(actionPlanEntry.getActionPlanAsset(indexAssets).getAsset().getId());
			assetparams.add(actionPlanEntry.getActionPlanAsset(indexAssets).getCurrentALE());
		}

		// action plan asset ALE values

		if (assetquery.endsWith("UNION"))
			// execute query
			assetquery = assetquery.substring(0, assetquery.length() - 6);
		if (!assetquery.isEmpty())
			sqlite.query(assetquery, assetparams);
	}

	/**
	 * exportActionPlanSummaries: <br>
	 * This method exports an Action Plan Summary to an Sqlite File.
	 *
	 * @param summaryStages The List of Summary Stages
	 *
	 * @throws Exception
	 */
	private void exportActionPlanSummaries(List<SummaryStage> summaryStages) throws Exception {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> params = new ArrayList<Object>();
		String query = "";

		// ****************************************************************
		// * export entry after entry
		// ****************************************************************
		for (int index = 0; index < summaryStages.size(); index++) {
			// ****************************************************************
			// * export the entry
			// ****************************************************************

			// build query
			query = DatabaseHandler.generateInsertQuery("ActionPlanSummary", 20);

			// add parameters
			params.clear();
			params.add(summaryStages.get(index).getId());
			params.add(summaryStages.get(index).getActionPlanType().getId());
			params.add(summaryStages.get(index).getStage());
			params.add(summaryStages.get(index).getConformanceValue("27001"));
			params.add(summaryStages.get(index).getConformanceValue("27002"));
			params.add(summaryStages.get(index).getMeasureCount());
			params.add(summaryStages.get(index).getImplementedMeasuresCount());
			params.add(summaryStages.get(index).getTotalALE());
			params.add(summaryStages.get(index).getDeltaALE());
			params.add(summaryStages.get(index).getCostOfMeasures());
			params.add(summaryStages.get(index).getROSI());
			params.add(summaryStages.get(index).getRelativeROSI());
			params.add(summaryStages.get(index).getInternalWorkload());
			params.add(summaryStages.get(index).getExternalWorkload());
			params.add(summaryStages.get(index).getInvestment());
			params.add(summaryStages.get(index).getInternalMaintenance());
			params.add(summaryStages.get(index).getExternalMaintenance());
			params.add(summaryStages.get(index).getRecurrentInvestment());
			params.add(summaryStages.get(index).getRecurrentCost());
			params.add(summaryStages.get(index).getTotalCostofStage());

			// execute query
			sqlite.query(query, params);
		}
	}

	/**
	 * exportActionPlan: <br>
	 * This method export a given Action Plan to an Sqlite File.
	 *
	 * @param type              The Type of Action Plan
	 * @param actionPlanEntries The List of Action Plan Entries
	 *
	 * @throws Exception
	 */
	private void exportActionPlan(List<ActionPlanEntry> actionPlanEntries) throws Exception {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> params = new ArrayList<Object>();
		String query = "";
		ActionPlanEntry actionPlanEntry = null;
		int counter = 0;

		// ****************************************************************
		// * export entry after entry
		// ****************************************************************

		if (actionPlanEntries.size() == 0)
			return;

		for (int index = 0; index < actionPlanEntries.size(); index++) {

			// store entry
			actionPlanEntry = actionPlanEntries.get(index);

			// ****************************************************************
			// * export the entry
			// ****************************************************************

			// build query

			// check if first part -> YES
			if (query.equals(Constant.EMPTY_STRING)) {

				// set first part query
				query = "INSERT INTO actionplan SELECT ? as 'idActionPlanCalculation',? as 'idActionPlanType', ? as 'norm',? as 'idMeasure',? as 'dtOrder',? as 'dtCost',? as 'dtROI',";
				query += "? as 'dtTotalALE',? as 'dtDeltaALE' UNION";

				// set limit
				counter = 9;
			} else {

				// check if first part -> NO

				// check if limit reached -> YES
				if (counter + 9 >= 999) {

					// execute query
					query = query.substring(0, query.length() - 6);
					sqlite.query(query, params);

					// clean params
					params.clear();

					// reset first part query
					query = "INSERT INTO actionplan SELECT ? as 'idActionPlanCalculation',? as 'idActionPlanType', ? as 'norm',? as 'idMeasure',? as 'dtOrder',? as 'dtCost',? as 'dtROI',";
					query += "? as 'dtTotalALE',? as 'dtDeltaALE' UNION";

					// reset limit
					counter = 9;
				} else {

					// check if limit reached -> NO
					query += " SELECT ?,?,?,?,?,?,?,?,? UNION";
					counter += 9;
				}
			}

			// add parameters
			params.add(actionPlanEntry.getId());
			params.add(actionPlanEntry.getActionPlanType().getId());
			params.add(actionPlanEntry.getMeasure().getAnalysisStandard().getStandard().getLabel());
			params.add(actionPlanEntry.getMeasure().getMeasureDescription().getReference());
			params.add(actionPlanEntry.getPosition());
			params.add(actionPlanEntry.getMeasure().getCost());
			params.add(actionPlanEntry.getROI());
			params.add(actionPlanEntry.getTotalALE());
			params.add(actionPlanEntry.getDeltaALE());

			// ****************************************************************
			// * export action plan assets for this action plan entry
			// ****************************************************************
			exportActionPlanAssets(actionPlanEntry);
		}

		// ****************************************************************
		// * export last insert part of action plan
		// ****************************************************************

		// action plans

		// execute query
		if (query.endsWith("UNION"))
			query = query.substring(0, query.length() - 6);
		if (query.isEmpty())
			sqlite.query(query, params);
	}

	/**
	 * exportRiskRegister: <br>
	 * Exports the Risk Register.
	 *
	 * @throws Exception
	 */
	private void exportRiskRegister() throws Exception {

		System.out.println("Export RiskRegister");

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> params = new ArrayList<Object>();
		String query = "";

		// ****************************************************************
		// * Export the Risk Register Item by Item
		// ****************************************************************

		// build query
		query = DatabaseHandler.generateInsertQuery("risk_register", 11);

		// parse all Risk Register Entries
		for (RiskRegisterItem registerItem : this.analysis.getRiskRegisters()) {

			// add parameters for the current Risk Register Item
			params.clear();
			params.add(registerItem.getScenario().getId());
			params.add(registerItem.getAsset().getId());
			params.add(registerItem.getRawEvaluation().getProbability());
			params.add(registerItem.getRawEvaluation().getImpact());
			params.add(registerItem.getRawEvaluation().getImportance());
			params.add(registerItem.getNetEvaluation().getProbability());
			params.add(registerItem.getNetEvaluation().getImpact());
			params.add(registerItem.getNetEvaluation().getImportance());
			params.add(registerItem.getExpectedEvaluation().getProbability());
			params.add(registerItem.getExpectedEvaluation().getImpact());
			params.add(registerItem.getExpectedEvaluation().getImportance());
			// execute query
			sqlite.query(query, params);
		}
	}

	/**
	 * insertCategories: <br>
	 * Adds Risk Categories to the SQL parameter List.
	 *
	 * @param params   The SQL SimpleParameter List
	 * @param criteria The Object containing the Data to add
	 * @throws TrickException
	 */
	private void insertCategories(List<Object> params, SecurityCriteria criteria) throws TrickException {
		// parse all categories
		for (String key : SecurityCriteria.getCategoryKeys())
			// add data to the list of parameters
			params.add(criteria.getCategoryValue(key));
	}

	/**
	 * getLinefromMaturityCategory: <br>
	 * Get the Line corresponding to the maturity category. If label belongs to
	 * nothing, return -1.
	 *
	 * @throws Exception
	 */
	private int getLinefromMaturityCategory(String label) throws Exception {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		int first = -1;

		// ****************************************************************
		// * calculate line for the task
		// ****************************************************************

		// split the label at a space expample: "Pol 1"
		String[] split = label.split(" ");

		// check if split was correct (2 words: example Pol and 1)
		if (split.length == 2) {

			// ****************************************************************
			// * set first line number
			// ****************************************************************

			if (split[0].equals(Constant.PARAMETER_MATURITY_TASK_POLICY)) {
				first = 0;
			} else if (split[0].equals(Constant.PARAMETER_MATURITY_TASK_PROCEDURE)) {
				first = 6;
			} else if (split[0].equals(Constant.PARAMETER_MATURITY_TASK_IMPLEMENTATION)) {
				first = 11;
			} else if (split[0].equals(Constant.PARAMETER_MATURITY_TASK_TEST)) {
				first = 14;
			} else if (split[0].equals(Constant.PARAMETER_MATURITY_TASK_INTEGRATION)) {
				first = 22;
			}

			// ****************************************************************
			// * add the task number to get correct line
			// ****************************************************************
			first += Integer.parseInt(split[1]);
		}

		// ****************************************************************
		// * return result
		// ****************************************************************
		return first;
	}

	/**
	 * @return the sqlite
	 */
	public DatabaseHandler getSqlite() {
		return sqlite;
	}

	/**
	 * @param sqlite the sqlite to set
	 */
	public void setSqlite(DatabaseHandler sqlite) {
		this.sqlite = sqlite;
	}

	/**
	 * @return the analysis
	 */
	public Analysis getAnalysis() {
		return analysis;
	}

	/**
	 * @param analysis the analysis to set
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}
}
