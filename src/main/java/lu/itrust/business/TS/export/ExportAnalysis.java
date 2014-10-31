package lu.itrust.business.TS.export;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.History;
import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.TS.MaturityMeasure;
import lu.itrust.business.TS.MaturityStandard;
import lu.itrust.business.TS.MaturityParameter;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.TS.NormalStandard;
import lu.itrust.business.TS.NormalMeasure;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.RiskInformation;
import lu.itrust.business.TS.ScenarioType;
import lu.itrust.business.TS.SecurityCriteria;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.TS.dbhandler.DatabaseHandler;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.dao.DAOAssetType;
import lu.itrust.business.dao.DAOScenarioType;
import lu.itrust.business.dao.hbm.DAOAssetTypeHBM;
import lu.itrust.business.dao.hbm.DAOScenarioTypeHBM;
import lu.itrust.business.exception.TrickException;
import lu.itrust.business.service.ServiceTaskFeedback;

import org.hibernate.Session;

/**
 * ExportAnalysis: <br>
 * This class is used to export a specific Analysis into a SQLite file to be used inside TRICK
 * Light.
 * 
 * @author itrust consulting s.��� r.l. - SME,BJA,EOM
 * @version 0.1
 * @since 2012-12-17
 */
public class ExportAnalysis {

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	/** SQLite Database Handler */
	private DatabaseHandler sqlite = null;

	private long idTask = 0;

	/** Analysis object */
	private Analysis analysis = null;

	private ServiceTaskFeedback serviceTaskFeedback;

	private DAOAssetType serviceAssetType;

	private DAOScenarioType serviceScenarioType;

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

	public ExportAnalysis(ServiceTaskFeedback serviceTaskFeedback, Session session, DatabaseHandler sqlite2, Analysis analysis, long idTask) {
		this.serviceAssetType = new DAOAssetTypeHBM(session);
		this.serviceScenarioType = new DAOScenarioTypeHBM(session);
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

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.identifier", "Export identifier", null, 10));

			// ****************************************************************
			// * export Identifier
			// ****************************************************************
			exportIdentifier();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.history", "Export histories", null, 15));

			// ****************************************************************
			// * export History
			// ****************************************************************
			exportHistory();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.risk_information", "Export risk information", null, 20));

			// ****************************************************************
			// * export risk information
			// ****************************************************************
			exportRiskInformation();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.item_information", "Export item information", null, 25));

			// ****************************************************************
			// * export item information
			// ****************************************************************
			exportItemInformation();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.parameters", "Export Parameters", null, 30));

			// ****************************************************************
			// * export simple parameters
			// ****************************************************************
			exportParameters();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.assets", "Export assets", null, 40));

			// ****************************************************************
			// * export assets
			// ****************************************************************
			exportAssets();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.scenarios", "Export scenarios", null, 50));

			// ****************************************************************
			// * export scenarios
			// ****************************************************************
			exportScenarios();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.assessments", "Export assessments", null, 60));

			// ****************************************************************
			// * export assessments
			// ****************************************************************
			exportAssessments();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.measures", "Export measures", null, 70));

			// ****************************************************************
			// * export all measures
			// ****************************************************************
			exportMeasuresAndMaturity();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.phases", "Export phases", null, 75));

			// ****************************************************************
			// * export phase
			// ****************************************************************
			exportPhase();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.action_plan", "Export action plans", null, 80));

			// ****************************************************************
			// * export action plans
			// ****************************************************************
			exportActionPlans();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.summaries", "Export summaries", null, 85));

			// ****************************************************************
			// * export summary
			// ****************************************************************
			exportActionPlanSummaries(this.analysis.getSummaries());

			serviceTaskFeedback.send(idTask, new MessageHandler("info.export.risk_register", "Export risk registers", null, 90));

			// ****************************************************************
			// * export Risk Register
			// ****************************************************************
			exportRiskRegister();

			serviceTaskFeedback.send(idTask, new MessageHandler("success.export.analysis", "Export done successfully", null, 95));

			System.out.println("Export Done!");

			return null;

		} catch (TrickException e) {
			// ****************************************************************
			// * Display error message
			// ****************************************************************
			MessageHandler handler = new MessageHandler(e);
			serviceTaskFeedback.send(idTask, handler);
			System.out.println(e.getMessage());
			e.printStackTrace();
			return handler;
		} catch (Exception e) {
			// ****************************************************************
			// * Display error message
			// ****************************************************************
			serviceTaskFeedback.send(idTask, new MessageHandler("error.export.unknown", "An unknown error occurred while exporting", null, e));
			System.out.println("Error while exporting!");
			e.printStackTrace();
			return new MessageHandler(e);
			// set return value exception
		}
	}

	/**
	 * exportRiskInformation: <br>
	 * Exports the Risk Information to an Sqlite File using an Sqlite Database Handler.
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
		RiskInformation information = null;

		// ****************************************************************
		// * parse all risk information to export
		// ****************************************************************

		// parse risk information
		for (int index = 0; index < this.analysis.getRiskInformations().size(); index++) {

			// store risk information
			information = this.analysis.getARiskInformation(index);

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
					riskquery += "type,? as expo,? as comment,? as comment2 UNION";

					// set number of ? -> sqlite ? limit is 999 -> before 999 is
					// reached, a execute
					// needs to be done
					riskcounter = 6;
				} else {

					// first time query is used ? -> NO

					// check if limit of ? is reached with next query -> YES
					if (riskcounter + 6 >= 999) {

						// remove UNION from query
						riskquery = riskquery.substring(0, riskquery.length() - 6);

						// execute query
						sqlite.query(riskquery, riskparams);

						// clear array
						riskparams.clear();

						// rebuild first query part
						riskquery = "INSERT INTO threat_source SELECT ? as level,? as name,";
						riskquery += "? as type,? as expo,? as comment,? as comment2 UNION";

						// reset number of ?
						riskcounter = 6;
					} else {

						// check if limit of ? is reached -> NO

						// add values to query ( execute 1 query with multiple
						// rows)
						riskquery += " SELECT ?,?,?,?,?,? UNION";

						// add number of ? used
						riskcounter += 6;
					}
				}

				// add parameters
				riskparams.add(information.getChapter());
				riskparams.add(information.getLabel());
				riskparams.add(information.getCategory().split("_")[1]);
				riskparams.add(information.getExposed());
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
					threatquery += "? as acro,? as expo,? as comment,? as comment2 UNION";
					threatcounter = 6;
				} else {

					// first part ? -> NO

					// limit of ? reached -> YES
					if (threatcounter + 6 >= 999) {

						// execute query
						threatquery = threatquery.substring(0, threatquery.length() - 6);
						sqlite.query(threatquery, threatparams);

						// clear parameters
						threatparams.clear();

						// reset query
						threatquery = "INSERT INTO threat_typology SELECT ? as level,? as name";
						threatquery += ",? as acro,? as expo,? as comment,? as comment2";
						threatquery += " UNION";

						// reset number of ?
						threatcounter = 6;
					} else {

						// limit of ? reached -> NO

						// add value
						threatquery += " SELECT ?,?,?,?,?,? UNION";

						// add number of ? used
						threatcounter += 6;
					}
				}

				// add parameters
				threatparams.add(information.getChapter());
				threatparams.add(information.getLabel());
				threatparams.add(information.getAcronym());
				threatparams.add(information.getExposed());
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
					vulquery += "? as expo,? as comment,? as comment2 UNION";

					// set number of ? used
					vulcounter = 5;
				} else {

					// check if limit is reached -> YES
					if (vulcounter + 5 >= 999) {

						// execute query
						vulquery = vulquery.substring(0, vulquery.length() - 6);
						sqlite.query(vulquery, vulparams);

						// clean parameters
						vulparams.clear();

						// reset query
						vulquery = "INSERT INTO vulnerabilities SELECT ? as level,? as name, ";
						vulquery += "? as expo,? as comment,? as comment2 UNION";

						// reset number of ?
						vulcounter = 5;
					} else {

						// check if limit reached -> NO

						// set values
						vulquery += " SELECT ?,?,?,?,? UNION";

						// add number of ? used
						vulcounter += 5;
					}
				}

				// add parameters
				vulparams.add(information.getChapter());
				vulparams.add(information.getLabel());
				vulparams.add(information.getExposed());
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
	 * Exports the Item Information to an Sqlite File using an Sqlite Database Handler.
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
		ItemInformation information = null;

		// ****************************************************************
		// * insert data into scope or organisation table
		// ****************************************************************

		// Loop item information
		for (int index = 0; index < this.analysis.getItemInformations().size(); index++) {

			// store item information
			information = this.analysis.getAnIteminformation(index);

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

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> params = new ArrayList<Object>();
		String query = "";

		// ****************************************************************
		// * retrieve phases to export
		// ****************************************************************

		// if phase is valid -> YES
		for (int i = 0; i < this.analysis.getPhases().size(); i++) {

			// ****************************************************************
			// * export current phase
			// ****************************************************************

			// should not save
			if (this.analysis.getPhases().get(i).getNumber() == 0)
				continue;

			// build query
			query = DatabaseHandler.generateInsertQuery("info_phases", 3);

			// add parameters
			params.clear();
			params.add(this.analysis.getAPhase(i).getNumber());
			params.add(String.valueOf(this.analysis.getAPhase(i).getBeginDate()));
			params.add(String.valueOf(this.analysis.getAPhase(i).getEndDate()));

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
		query = DatabaseHandler.generateInsertQuery("identifier", 2);

		// add parameters
		params.clear();
		params.add(this.analysis.getIdentifier());
		params.add(this.analysis.getLabel());

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
		List<Object> params = new ArrayList<Object>();
		String query = "";
		History history = null;

		// add date of the comment
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		// ****************************************************************
		// * parse all history entries
		// ****************************************************************

		// Loop histories
		for (int index = 0; index < this.analysis.getHistories().size(); index++) {

			// store history
			history = this.analysis.getAHistory(index);

			// ****************************************************************
			// * export current history entry
			// ****************************************************************

			// build query
			query = DatabaseHandler.generateInsertQuery("history", 4);

			// add parameters
			params.clear();
			params.add(history.getVersion());
			params.add(dateFormat.format(history.getDate()));
			params.add(history.getAuthor());
			params.add(history.getComment());

			// execute the query
			sqlite.query(query, params);
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
		Parameter parameter = null;

		// ****************************************************************
		// * parse parameters and export simple parameters
		// ****************************************************************

		// parse parameters
		for (int index = 0; index < this.analysis.getParameters().size(); index++) {

			// ****************************************************************
			// * export max effency parameter
			// ****************************************************************

			// check if max efficiency -> YES
			if (this.analysis.getAParameter(index).getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME)) {

				// ****************************************************************
				// * export parameter
				// ****************************************************************

				// store parameter
				parameter = (Parameter) this.analysis.getAParameter(index);

				// build query
				query = DatabaseHandler.generateInsertQuery("maturity_max_eff", 2);

				// add parameters
				params.clear();
				params.add(parameter.getDescription().substring(parameter.getDescription().length() - 1));
				params.add(parameter.getValue() / 100.);

				// execute the query
				sqlite.query(query, params);
			}

			// ****************************************************************
			// * export implementation scale parameter
			// ****************************************************************

			// check if implementation rate -> YES
			if (this.analysis.getAParameter(index).getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME)) {

				// ****************************************************************
				// * export parameter
				// ****************************************************************

				// store parameter
				parameter = (Parameter) this.analysis.getAParameter(index);

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
				params.add(parameter.getValue() / 100.);

				// execute the query
				sqlite.query(query, params);
			}

			// ****************************************************************
			// * export simple parameter
			// ****************************************************************

			// check if single parameter -> YES
			if (this.analysis.getAParameter(index).getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME)) {

				// store parameter in object
				parameter = (Parameter) this.analysis.getAParameter(index);

				// ****************************************************************
				// * export parameter
				// ****************************************************************

				// build query
				query = "UPDATE scope set " + parameter.getDescription() + "=?";

				// add parameters
				params.clear();

				Double value = parameter.getValue();
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
	 * @throws Exception
	 */
	private void exportExtendedParameters() throws Exception {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		List<Object> params = new ArrayList<Object>();
		String query = "";
		ExtendedParameter extendedParameter = null;

		// ****************************************************************
		// * parse parameters and export impact and propability only
		// ****************************************************************

		// parse parameters
		for (int index = 0; index < this.analysis.getParameters().size(); index++) {

			// check if impact OR propability -> YES
			if ((this.analysis.getAParameter(index).getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
				|| (this.analysis.getAParameter(index).getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME))) {

				// store parameter in object
				extendedParameter = (ExtendedParameter) this.analysis.getAParameter(index);

				// ****************************************************************
				// * export parameter
				// ****************************************************************

				// build query

				// check if propability -> YES
				if (this.analysis.getAParameter(index).getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME)) {

					// build the query
					query = DatabaseHandler.generateInsertQuery("potentiality", 7);

				} else {

					// check if propability -> NO --> can only be impact

					// build the query
					query = DatabaseHandler.generateInsertQuery("impact", 7);
				}

				// add parameters
				params.clear();
				params.add(null);
				params.add(extendedParameter.getLevel());
				params.add(extendedParameter.getDescription());
				params.add(extendedParameter.getAcronym());
				params.add(extendedParameter.getValue());
				params.add(extendedParameter.getBounds().getFrom());
				params.add(extendedParameter.getBounds().getTo());

				// execute query
				sqlite.query(query, params);
			}
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
		MaturityParameter maturityParameter = null;
		int counter = 0;

		// ****************************************************************
		// * parse parameters and export maturtiy parameter only
		// ****************************************************************

		// parse parameters
		for (int index = 0; index < this.analysis.getParameters().size(); index++) {

			// check if ILPS
			if (this.analysis.getAParameter(index).getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_LEVEL_PER_SML_NAME)) {

				// store maturity parameter
				maturityParameter = (MaturityParameter) this.analysis.getAParameter(index);

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
	 * Exports the Simple, Extended and Maturity Parameters to an Sqlite File using an Sqlite
	 * Database Handler.
	 * 
	 * @throws Exception
	 */
	private void exportParameters() throws Exception {

		System.out.println("Export Parameters");

		// ****************************************************************
		// * export simple parameters
		// ****************************************************************
		exportSimpleParameters();

		// ****************************************************************
		// * export extended parameters
		// ****************************************************************
		exportExtendedParameters();

		// ****************************************************************
		// * export maturity parameters
		// ****************************************************************
		exportMaturityParameters();
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
			params.add(assetType.getType());

			// System.out.println(rs.getInt(Constant.ASSETTYPE_MYSQL_ID));

			// execute query
			sqlite.query(query, params);
		}

		// ****************************************************************
		// * export Assets
		// ****************************************************************

		// Loop assets
		for (int index = 0; index < this.analysis.getAssets().size(); index++) {

			// ****************************************************************
			// * export asset
			// ****************************************************************

			// build query
			query = DatabaseHandler.generateInsertQuery("assets", 8);

			// add parameters
			params.clear();
			params.add(this.analysis.getAnAsset(index).getId());
			params.add(this.analysis.getAnAsset(index).getName());
			params.add(this.analysis.getAnAsset(index).getAssetType().getId());
			params.add(this.analysis.getAnAsset(index).getValue());
			params.add(this.analysis.getAnAsset(index).getComment());
			params.add(this.analysis.getAnAsset(index).getHiddenComment());
			if (this.analysis.getAnAsset(index).isSelected()) {
				params.add(Constant.ASSET_SELECTED);
			} else {
				params.add(Constant.EMPTY_STRING);
			}
			params.add(this.analysis.getALEOfAsset(this.analysis.getAnAsset(index)));

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
		String query = "";

		List<ScenarioType> scenarioTypes = null;

		if (analysis.isCssf())
			scenarioTypes = ScenarioType.getAllCSSF();
		else
			ScenarioType.getAllCIA();

		// ****************************************************************
		// * export scenario types
		// ****************************************************************

		// parse scenario types
		for (ScenarioType scenarioType : scenarioTypes) {

			// ****************************************************************
			// * export scenario types
			// ****************************************************************

			// build query
			query = "INSERT INTO threat_types VALUES (?, ?)";

			// add parameters
			params.clear();
			params.add(scenarioType.getValue());
			params.add(scenarioType.getName());

			// execute query
			sqlite.query(query, params);
		}

		// ****************************************************************
		// * export scenarios
		// ****************************************************************

		// parse scenarios
		for (int index = 0; index < this.analysis.getScenarios().size(); index++) {

			// build query
			query = DatabaseHandler.generateInsertQuery("threats", 52);

			// add aprameters
			params.clear();
			params.add(this.analysis.getAScenario(index).getId());
			params.add(this.analysis.getAScenario(index).getName());
			// System.out.println(this.analysis.getAScenario(index).getType()+",");
			params.add(this.analysis.getAScenario(index).getScenarioType().getId());
			params.add(this.analysis.getAScenario(index).getDescription());
			if (this.analysis.getAScenario(index).isSelected()) {
				params.add(Constant.ASSET_SELECTED);
			} else {
				params.add(Constant.EMPTY_STRING);
			}

			// insert Asset Type values for the current scneario
			insertScenarioAssetTypeValues(params, this.analysis.getAScenario(index).getAssetTypeValues());

			// save Risk data
			insertCategories(params, this.analysis.getAScenario(index));
			params.add(this.analysis.getAScenario(index).getPreventive());
			params.add(this.analysis.getAScenario(index).getDetective());
			params.add(this.analysis.getAScenario(index).getLimitative());
			params.add(this.analysis.getAScenario(index).getCorrective());
			params.add(this.analysis.getAScenario(index).getIntentional());
			params.add(this.analysis.getAScenario(index).getAccidental());
			params.add(this.analysis.getAScenario(index).getEnvironmental());
			params.add(this.analysis.getAScenario(index).getInternalThreat());
			params.add(this.analysis.getAScenario(index).getExternalThreat());

			// execute the query
			sqlite.query(query, params);
		}
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
		List<Object> params = new ArrayList<Object>();
		String query = "";

		Map<Integer, Double> totalALEs = new HashMap<Integer, Double>();

		for (Assessment assessment : this.analysis.getAssessments()) {

			Integer key = assessment.getAsset().getId();

			double ale = assessment.getImpactReal() * assessment.getLikelihoodReal();

			double totalALE = (totalALEs.containsKey(key) ? totalALEs.get(key) + ale : ale);

			totalALEs.put(key, totalALE);
		}

		int counter = 0;

		// ****************************************************************
		// * export assessment
		// ****************************************************************

		// parse assessment
		for (int index = 0; index < this.analysis.getAssessments().size(); index++) {

			// ****************************************************************
			// * export assessment
			// ****************************************************************

			// check if first part of query -> YES
			if (query.equals(Constant.EMPTY_STRING)) {

				// build query
				query =
					"INSERT INTO Assessment SELECT ? as 'id_asset', ? as id_threat,? as selected,? " + "as impact_reputation,? as impact_operational, ? as impact_legal, ? as "
						+ "impact_financial,? as impact_hidden,? as potentiality,? as " + "potentiality_hidden,? as comment,? as comment_2,? as total_ALE,? as " + "uncertainty UNION";

				// set ? limit
				counter = 14;
			} else {

				// check if first part of query -> NO

				// limit reached ? -> YES
				if (counter + 14 >= 999) {

					// execute query
					query = query.substring(0, query.length() - 6);
					sqlite.query(query, params);

					// clean parameters
					params.clear();

					// reset query
					// build query
					query =
						"INSERT INTO Assessment SELECT ? as 'id_asset', ? as id_threat,? as selected,? " + "as impact_reputation,? as impact_operational, ? as impact_legal, ? as "
							+ "impact_financial,? as impact_hidden,? as potentiality,? as " + "potentiality_hidden,? as comment,? as comment_2,? as total_ALE,? as " + "uncertainty UNION";

					// set ? limit
					counter = 14;
				} else {

					// limit reached ? -> NO

					// add data to query
					query += " SELECT ?,?,?,?,?,?,?,?,?,?,?,?,?,? UNION";

					// increment limit
					counter += 14;
				}
			}

			Integer key = this.analysis.getAnAssessment(index).getAsset().getId();
			// add parameters
			params.add(this.analysis.getAnAssessment(index).getAsset().getId());
			params.add(this.analysis.getAnAssessment(index).getScenario().getId());
			params.add(this.analysis.getAnAssessment(index).isSelected() ? Constant.ASSESSMENT_SELECTED : Constant.EMPTY_STRING);
			params.add(this.analysis.getAnAssessment(index).getImpactRep());
			params.add(this.analysis.getAnAssessment(index).getImpactOp());
			params.add(this.analysis.getAnAssessment(index).getImpactLeg());
			params.add(this.analysis.getAnAssessment(index).getImpactFin());
			params.add(this.analysis.getAnAssessment(index).getImpactReal());
			params.add(this.analysis.getAnAssessment(index).getLikelihood());
			params.add(this.analysis.getAnAssessment(index).getLikelihoodReal());
			params.add(this.analysis.getAnAssessment(index).getComment());
			params.add(this.analysis.getAnAssessment(index).getHiddenComment());
			params.add(totalALEs.get(key));
			params.add(this.analysis.getAnAssessment(index).getUncertainty());
		}

		if (query.endsWith("UNION"))
			query = query.substring(0, query.length() - 6);

		if (!query.isEmpty())
			// execute the query
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
		List<Object> measureparams = new ArrayList<Object>();
		List<Object> specparams = new ArrayList<Object>();
		List<Object> defaultspecparams = new ArrayList<Object>();
		String specquery = "";
		String specdefaultquery = "";
		NormalStandard normalStandard = null;
		NormalMeasure measure = null;
		MaturityStandard maturityStandard = null;
		MaturityMeasure maturity = null;
		AssetTypeValue assetTypeValue = null;
		String measurequery = "";
		int measurecounter = 0;
		int speccounter = 0;
		int specdefaultcounter = 0;

		// ****************************************************************
		// * export standard measures (27001, 27002, custom)
		// ****************************************************************

		// parse standards
		for (int indexStandard = 0; indexStandard < this.analysis.getAnalysisStandards().size(); indexStandard++) {

			// ****************************************************************
			// * retrieve standard that is not maturity
			// ****************************************************************

			// ****************************************************************
			// standard not maturity -> YES
			// ****************************************************************
			if (this.analysis.getAnalysisStandard(indexStandard) instanceof NormalStandard) {

				// store standard as measurestandard
				normalStandard = (NormalStandard) this.analysis.getAnalysisStandard(indexStandard);

				// ****************************************************************
				// * parse measures of this standard
				// ****************************************************************

				// reinitialise variables
				measureparams.clear();
				specparams.clear();
				defaultspecparams.clear();
				measurequery = "";
				specquery = "";
				specdefaultquery = "";

				// parse measures
				for (int index = 0; index < normalStandard.getMeasures().size(); index++) {

					// store measure
					measure = normalStandard.getMeasure(index);

					// ****************************************************************
					// * export measure
					// ****************************************************************

					// check if first part of query -> YES
					if (measurequery.isEmpty()) {

						// build query
						measurequery =
							"INSERT INTO measures SELECT " + "? as 'id_norme'," + "? as 'version_norme'," + "? as 'norme_computable'," + "? as 'norme_description'," + "? as 'ref_measure',"
								+ "? as 'measure_computable'," + "? as 'domain_measure'," + "? as 'question_measure'," + "? as 'level'," + "? as 'strength_measure'," + "? as 'strength_sectoral',"
								+ "? as 'confidentiality'," + "? as 'integrity'," + "? as 'availability'," + "? as 'd1'," + "? as 'd2'," + "? as 'd3'," + "? as 'd4'," + "? as 'd5'," + "? as 'd6',"
								+ "? as 'd61'," + "? as 'd62'," + "? as 'd63'," + "? as 'd64'," + "? as 'd7'," + "? as 'i1'," + "? as 'i2'," + "? as 'i3'," + "? as 'i4'," + "? as 'i5',"
								+ "? as 'i6'," + "? as 'i7'," + "? as 'i8'," + "? as 'i81'," + "? as 'i82'," + "? as 'i83'," + "? as 'i84'," + "? as 'i9'," + "? as 'i10'," + "? as 'preventive',"
								+ "? as 'detective'," + "? as 'limiting'," + "? as 'corrective'," + "? as 'intentional'," + "? as 'accidental'," + "? as 'environmental'," + "? as 'internal_threat',"
								+ "? as 'external_threat'," + "? as 'internal_setup'," + "? as 'external_setup'," + "? as 'investment'," + "? as 'lifetime'," + "? as 'maintenance',"
								+ "? as 'internal_maintenance'," + "? as 'external_maintenance'," + "? as 'recurrent_investment'," + "? as 'implmentation_rate'," + "? as 'status',"
								+ "? as 'comment'," + "? as 'todo'," + "? as 'revision'," + "? as 'phase'," + "? as 'soa_reference'," + "? as 'soa_risk'," + "? as 'soa_comment',"
								+ "? as 'index2' UNION";

						// System.out.println(measurequery);

						// set ? limit
						measurecounter = 66;
					} else {

						// check if first part of query -> NO

						// limit reached ? -> YES
						if (measurecounter + 66 >= 999) {

							// execute query
							measurequery = measurequery.substring(0, measurequery.length() - 6);
							sqlite.query(measurequery, measureparams);

							// clean parameters
							measureparams.clear();

							// reset query
							measurequery =
								"INSERT INTO measures SELECT " + "? as 'id_norme'," + "? as 'version_norme'," + "? as 'norme_computable'," + "? as 'norme_description'," + "? as 'ref_measure',"
									+ "? as 'measure_computable'," + "? as 'domain_measure'," + "? as 'question_measure'," + "? as 'level'," + "? as 'strength_measure'," + "? as 'strength_sectoral',"
									+ "? as 'confidentiality'," + "? as 'integrity'," + "? as 'availability'," + "? as 'd1'," + "? as 'd2'," + "? as 'd3'," + "? as 'd4'," + "? as 'd5',"
									+ "? as 'd6'," + "? as 'd61'," + "? as 'd62'," + "? as 'd63'," + "? as 'd64'," + "? as 'd7'," + "? as 'i1'," + "? as 'i2'," + "? as 'i3'," + "? as 'i4',"
									+ "? as 'i5'," + "? as 'i6'," + "? as 'i7'," + "? as 'i8'," + "? as 'i81'," + "? as 'i82'," + "? as 'i83'," + "? as 'i84'," + "? as 'i9'," + "? as 'i10',"
									+ "? as 'preventive'," + "? as 'detective'," + "? as 'limiting'," + "? as 'corrective'," + "? as 'intentional'," + "? as 'accidental'," + "? as 'environmental',"
									+ "? as 'internal_threat'," + "? as 'external_threat'," + "? as 'internal_setup'," + "? as 'external_setup'," + "? as 'investment'," + "? as 'lifetime',"
									+ "? as 'maintenance'," + "? as 'internal_maintenance'," + "? as 'external_maintenance'," + "? as 'recurrent_investment'," + "? as 'implmentation_rate',"
									+ "? as 'status'," + "? as 'comment'," + "? as 'todo'," + "? as 'revision'," + "? as 'phase'," + "? as 'soa_reference'," + "? as 'soa_risk',"
									+ "? as 'soa_comment'," + "? as 'index2' UNION";

							// reset limit counter
							measurecounter = 66;
						} else {

							// limit reached ? -> NO

							// add data to query
							measurequery += " SELECT ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? UNION";

							// increment limit
							measurecounter += 66;
						}
					}

					// ****************************************************************
					// * add params to query
					// ****************************************************************

					// for standard
					measureparams.add(normalStandard.getStandard().getLabel());
					measureparams.add(normalStandard.getStandard().getVersion());
					measureparams.add(normalStandard.getStandard().isComputable());
					measureparams.add(normalStandard.getStandard().getDescription());

					// for measure
					measureparams.add(measure.getMeasureDescription().getReference());
					measureparams.add(measure.getMeasureDescription().isComputable());
					MeasureDescriptionText descriptionText = measure.getMeasureDescription().getAMeasureDescriptionText(this.analysis.getLanguage());
					measureparams.add(descriptionText == null ? "" : descriptionText.getDomain());
					measureparams.add(descriptionText == null ? "" : descriptionText.getDescription());
					measureparams.add(measure.getMeasureDescription().getLevel());
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
					measureparams.add(measure.getMaintenance());
					measureparams.add(measure.getInternalMaintenance());
					measureparams.add(measure.getExternalMaintenance());
					measureparams.add(measure.getRecurrentInvestment());
					measureparams.add(measure.getImplementationRate());
					measureparams.add(measure.getStatus());
					measureparams.add(measure.getComment());
					measureparams.add(measure.getToDo());
					measureparams.add(measure.getToCheck());
					measureparams.add(measure.getPhase().getNumber());
					measureparams.add(measure.getMeasurePropertyList().getSoaReference());
					measureparams.add(measure.getMeasurePropertyList().getSoaRisk());
					measureparams.add(measure.getMeasurePropertyList().getSoaComment());
					measureparams.add(generateIndexOfReference(measure.getMeasureDescription().getReference()));

					// ****************************************************************
					// * export asset type values
					// ****************************************************************

					for (int indexAssetTypeValue = 0; indexAssetTypeValue < measure.getAssetTypeValues().size(); indexAssetTypeValue++) {

						// store asset type value object
						assetTypeValue = measure.getAssetTypeValue(indexAssetTypeValue);

						// ****************************************************************
						// * export asset type value into
						// spec_default_type_asset_measure sqlite
						// table
						// ****************************************************************

						// build query

						// check if first part -> YES
						if (specdefaultquery.isEmpty()) {

							// set query
							specdefaultquery =
								"INSERT INTO spec_default_type_asset_measure SELECT ? as 'id_type_asset', ? as 'id_norme', ? as 'version_norme', ? as 'ref_measure', ? as 'value_spec' UNION";

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
								specdefaultquery =
									"INSERT INTO spec_default_type_asset_measure SELECT ? as 'id_type_asset', ? as 'id_norme', ? as 'version_norme', ? as 'ref_measure', ? as 'value_spec' UNION";

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

						// check if standard is custom -> YES
						if (normalStandard.getStandard().getLabel().startsWith(Constant.STANDARD_CUSTOM)) {

							// set custom standard name
							defaultspecparams.add(Constant.STANDARD_CUSTOM);
						} else {

							// check if standard is custom -> NO

							// set all other standard name
							defaultspecparams.add(normalStandard.getStandard().getLabel());
						}

						defaultspecparams.add(normalStandard.getStandard().getVersion());

						defaultspecparams.add(measure.getMeasureDescription().getReference());

						if (assetTypeValue.getValue() == 101)
							defaultspecparams.add(-1);
						else
							defaultspecparams.add(assetTypeValue.getValue());

						// check if measure is level 3 -> YES
						if (measure.getMeasureDescription().isComputable()) {

							// ****************************************************************
							// * export level 3 asset type value into
							// spec_type_asset_measure
							// * sqlite table
							// ****************************************************************

							// check if first part -> YES
							if (specquery.isEmpty()) {

								// set query
								specquery = "INSERT INTO spec_type_asset_measure SELECT ? as 'id_type_asset', ? as 'id_norme',? as 'version_norme', ? as 'ref_measure', ? as 'value_spec' UNION";

								// set limit
								speccounter = 5;
							} else {

								// check if first part -> NO

								// check if limit reached -> YES
								if (speccounter + 5 >= 999) {

									// execute query
									specquery = specquery.substring(0, specquery.length() - 6);
									sqlite.query(specquery, specparams);

									// reset parameters
									specparams.clear();

									// reset query
									specquery = "INSERT INTO spec_type_asset_measure SELECT ? as 'id_type_asset', ? as 'id_norme',? as 'version_norme', ? as 'ref_measure', ? as 'value_spec' UNION";

									// reset limit
									speccounter = 5;
								} else {

									// check if limit reached -> NO

									// add insert values to query
									specquery += " SELECT ?,?,?,?,? UNION";

									// increment limit
									speccounter += 5;
								}
							}

							// add parameters
							specparams.add(assetTypeValue.getAssetType().getId());

							// check if standard is custom -> YES
							if (normalStandard.getStandard().getLabel().startsWith(Constant.STANDARD_CUSTOM)) {

								// set custom standard name
								specparams.add(Constant.STANDARD_CUSTOM);
							} else {

								// check if standard is custom -> NO

								// set all other standard name
								specparams.add(normalStandard.getStandard().getLabel());
							}

							specparams.add(normalStandard.getStandard().getVersion());

							specparams.add(measure.getMeasureDescription().getReference());
							if (assetTypeValue.getValue() == 101)
								specparams.add(-1);
							else
								specparams.add(assetTypeValue.getValue());
						}
					}
				}

				// ****************************************************************
				// * execute last part of inserts of measures and asset type
				// values
				// ****************************************************************

				// remove UNION from queries
				if (measurequery.endsWith("UNION"))
					measurequery = measurequery.substring(0, measurequery.length() - 6);
				if (specquery.endsWith("UNION"))
					specquery = specquery.substring(0, specquery.length() - 6);
				if (specdefaultquery.endsWith("UNION"))
					specdefaultquery = specdefaultquery.substring(0, specdefaultquery.length() - 6);

				// execute the query
				if (!measurequery.isEmpty())
					sqlite.query(measurequery, measureparams);

				if (!specquery.isEmpty())
					// execute the query
					sqlite.query(specquery, specparams);

				if (!specdefaultquery.isEmpty())
					// execute the query
					sqlite.query(specdefaultquery, defaultspecparams);

			} else if (this.analysis.getAnalysisStandard(indexStandard) instanceof MaturityStandard) {

				// ****************************************************************
				// standard not maturity -> NO
				// ****************************************************************

				// store maturity standard
				maturityStandard = (MaturityStandard) this.analysis.getAnalysisStandard(indexStandard);

				// ****************************************************************
				// * parse all maturity measures
				// ****************************************************************

				// reinitialise variables
				measureparams.clear();
				specparams.clear();
				measurequery = "";
				specquery = "";
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
						measurequery = "INSERT INTO maturities SELECT ? as 'version_norme', ? as 'norme_description', ? as 'norm_computable',? as 'ref',? as 'measure_computable',? as";
						measurequery += " 'domain',? as 'phase', ? as 'level',? as 'status',? as 'rate',? as 'intwl',? as 'extwl',? as 'investment',? as 'lifetime',? as 'maintenance', ? as ";
						measurequery += "'internal_maintenance',? as 'external_maintenance',? as 'recurrent_investment',? as 'comment',? as 'todo',? as 'sml1',? as 'sml2',? as 'sml3',";
						measurequery += "? as 'sml4',? as 'sml5',? as 'index2',? as 'reached' UNION";

						// set limit
						measurecounter = 27;

					} else {

						// check if first part -> NO

						// check if limit reached -> YES
						if (measurecounter + 27 >= 999) {

							// execute query
							measurequery = measurequery.substring(0, measurequery.length() - 6);
							sqlite.query(measurequery, measureparams);

							// clean parameters
							measureparams.clear();

							// reset query
							measurequery = "INSERT INTO maturities SELECT ? as 'version_norme', ? as 'norme_description', ? as 'norm_computable',? as 'ref',? as 'measure_computable',? as";
							measurequery += " 'domain',? as 'phase', ? as 'level',? as 'status',? as 'rate',? as 'intwl',? as 'extwl',? as 'investment',? as 'lifetime',? as 'maintenance', ";
							measurequery += "? as 'internal_maintenance',? as 'external_maintenance',? as 'recurrent_investment',? as 'comment',? as 'todo',? as 'sml1',? as 'sml2',? as 'sml3',";
							measurequery += "? as 'sml4',? as 'sml5',? as 'index2',? as 'reached' UNION";

							// reset limit
							measurecounter = 27;
						} else {

							// limit reached -> NO

							// add insert data to query
							measurequery += " SELECT ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? UNION";

							// increment limit
							measurecounter += 27;
						}
					}

					// add parameters
					measureparams.add(maturity.getAnalysisStandard().getStandard().getVersion());
					measureparams.add(maturity.getAnalysisStandard().getStandard().getDescription());
					measureparams.add(maturity.getAnalysisStandard().getStandard().isComputable());
					measureparams.add(maturity.getMeasureDescription().getReference());
					measureparams.add(maturity.getMeasureDescription().isComputable());
					MeasureDescriptionText descriptionText = maturity.getMeasureDescription().getAMeasureDescriptionText(this.analysis.getLanguage());
					measureparams.add(descriptionText == null ? "" : descriptionText.getDomain());
					measureparams.add(maturity.getPhase().getNumber());
					measureparams.add(maturity.getMeasureDescription().getLevel());
					measureparams.add(maturity.getStatus());
					measureparams.add(maturity.getImplementationRateValue() / 100.0);
					measureparams.add(maturity.getInternalWL());
					measureparams.add(maturity.getExternalWL());
					measureparams.add(maturity.getInvestment());
					measureparams.add(maturity.getLifetime());
					measureparams.add(maturity.getMaintenance());
					measureparams.add(maturity.getInternalMaintenance());
					measureparams.add(maturity.getExternalMaintenance());
					measureparams.add(maturity.getRecurrentInvestment());
					measureparams.add(maturity.getComment());
					measureparams.add(maturity.getToDo());
					measureparams.add(maturity.getSML1Cost());
					measureparams.add(maturity.getSML2Cost());
					measureparams.add(maturity.getSML3Cost());
					measureparams.add(maturity.getSML4Cost());
					measureparams.add(maturity.getSML5Cost());
					measureparams.add(generateIndexOfReference(maturity.getMeasureDescription().getReference()));
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
	 * @param type
	 *            The Action Plan Type ID
	 * @param actionPlanEntry
	 *            The Action Plan Entry
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
	 * @param summaryStages
	 *            The List of Summary Stages
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
			params.add(summaryStages.get(index).getSingleConformance("27001"));
			params.add(summaryStages.get(index).getSingleConformance("27002"));
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
	 * @param type
	 *            The Type of Action Plan
	 * @param actionPlanEntries
	 *            The List of Action Plan Entries
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
		query = DatabaseHandler.generateInsertQuery("risk_register", 13);

		// parse all Risk Register Entries
		for (int i = 0; i < this.analysis.getRiskRegisters().size(); i++) {

			// add parameters for the current Risk Register Item
			params.clear();
			params.add(this.analysis.getARiskRegisterEntry(i).getScenario().getId());
			params.add(this.analysis.getARiskRegisterEntry(i).getAsset().getId());
			params.add(this.analysis.getARiskRegisterEntry(i).getPosition());
			params.add(this.analysis.getARiskRegisterEntry(i).getRawEvaluation().getProbability());
			params.add(this.analysis.getARiskRegisterEntry(i).getRawEvaluation().getImpact());
			params.add(this.analysis.getARiskRegisterEntry(i).getRawEvaluation().getImportance());
			params.add(this.analysis.getARiskRegisterEntry(i).getNetEvaluation().getProbability());
			params.add(this.analysis.getARiskRegisterEntry(i).getNetEvaluation().getImpact());
			params.add(this.analysis.getARiskRegisterEntry(i).getNetEvaluation().getImportance());
			params.add(this.analysis.getARiskRegisterEntry(i).getExpectedImportance().getProbability());
			params.add(this.analysis.getARiskRegisterEntry(i).getExpectedImportance().getImpact());
			params.add(this.analysis.getARiskRegisterEntry(i).getExpectedImportance().getImportance());
			params.add(this.analysis.getARiskRegisterEntry(i).getStrategy());

			// execute query
			sqlite.query(query, params);
		}
	}

	/**
	 * insertCategories: <br>
	 * Adds Risk Categories to the SQL parameter List.
	 * 
	 * @param params
	 *            The SQL Parameter List
	 * @param criteria
	 *            The Object containing the Data to add
	 * @throws TrickException
	 */
	private void insertCategories(List<Object> params, SecurityCriteria criteria) throws TrickException {

		// parse all categories
		for (String key : SecurityCriteria.getCategoryKeys())

			// add data to the list of parameters
			params.add(criteria.getCategoryValue(key));
	}

	/**
	 * insertScenarioAssetTypeValues: <br>
	 * Adds all Scenario Asset Type Values to a given SQL Parameter List.
	 * 
	 * @param params
	 *            The SQL Parameter List
	 * @param assetTypeValueList
	 *            The List of Scenario Asset Type Values
	 */
	private void insertScenarioAssetTypeValues(List<Object> params, List<AssetTypeValue> assetTypeValueList) {

		// set Asset Type keys
		final String[] keys = Constant.ASSET_TYPES.split(",");

		// parse all asset type keys
		for (String key : keys) {

			// find current key inside list
			for (int i = 0; i < assetTypeValueList.size(); i++) {

				// key matches item in the list -> YES
				if (assetTypeValueList.get(i).getAssetType().getType().equalsIgnoreCase(key)) {

					// add the asset type value to the list of parameters
					params.add(assetTypeValueList.get(i).getValue());

					// leave loop, only 1 item at a time
					break;
				}
			}
		}
	}

	/**
	 * generateIndexOfReference: <br>
	 * Generate Index of Measure or Maturity Reference. This will be used inside Measure Export, to
	 * set the Field in the Sqlite File to define the Order inside TRICK Light.
	 * 
	 * @param reference
	 *            The Reference of the Measure
	 * @throws SQLException
	 */
	private long generateIndexOfReference(String reference) {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		String[] split = reference.split("\\.");

		final long[] multi = new long[4];

		for (int indexMulti = (4 - 1); indexMulti >= 0; indexMulti--)
			multi[indexMulti] = (long) Math.pow(1000, indexMulti);

		long index = 0;

		// ****************************************************************
		// * create the index of aperence of reference inside sqlite table
		// ****************************************************************

		// check if length of split is greater than 0 -> YES
		if (split.length > 0) {

			// Case of 27001 and Maturity -> YES
			if (split[0].equals(Constant.STANDARD27001_FIRSTCHAR_REFERENCE) || split[0].equals(Constant.MATURITY_FIRSTCHAR_REFERENCE)) {

				index = 0;

				// create index for 27001 and maturity
				for (int i = 1; i < split.length; i++) {

					// ****************************************************************
					// * set index
					// ****************************************************************
					long multiplicator = multi[(multi.length - 1) - i];

					long value = Long.parseLong(split[i]);

					index += (value * multiplicator);
				}

			} else {

				// Case of 27001 and Maturity -> NO

				index = 0;

				// create index for 27002 and custom
				for (int i = 0; i < split.length; i++) {

					// ****************************************************************
					// * set index
					// ****************************************************************

					long multiplicator = multi[(multi.length - 1) - i];

					long value = Long.parseLong(split[i]);

					index += (value * multiplicator);
				}
			}
		}

		// ****************************************************************
		// * return result
		// ****************************************************************
		return index;
	}

	/**
	 * getLinefromMaturityCategory: <br>
	 * Get the Line corresponding to the maturity category. If label belongs to nothing, return -1.
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
	 * @param sqlite
	 *            the sqlite to set
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
	 * @param analysis
	 *            the analysis to set
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}
}
