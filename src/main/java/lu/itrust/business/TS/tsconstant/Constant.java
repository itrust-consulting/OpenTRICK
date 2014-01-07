package lu.itrust.business.TS.tsconstant;

/**
 * Constant: <br>
 * This Class contains all the Constants used inside TRICK Service.<br>
 * There are 3 groups of constants:
 * 
 * <ul>
 * <li>Commonly used Constants</li>
 * <li>MySQL Database Table Constants</li>
 * <li>SQLite Database Table Constants</li>
 * </ul>
 * 
 * @author itrust consulting s.ï¿½.rl. : SME
 * @version
 * @since 9 January 2013
 */
public class Constant {

	/***********************************************************************************************
	 * List of Regular Expressions - BEGIN
	 **********************************************************************************************/

	/** Regular expression to validate likelihood value */
	public static final String REGEXP_VALID_LIKELIHOOD = "i|l[13579]|r|3a|a|t|m";

	/** Regular expression to validate impactFin value */
	public static final String REGEXP_VALID_IMPACT = "\\d+|\\d+\\.\\d*|[cC]([0-9]|10)";

	/** Regular *Expression to check on valid Names */
	public static final String REGEXP_VALID_NAME = "^([a-zA-ZÃ Ã£Ã¢Ã¡Ã¤Ã£Ã¥Ã¦Ã¨ÃªÃ©Ã«Ã±Ã­Ã¯Ã¬Ã®Ã¸Ã´ÃµÃ²Ã³Ã¹Ã»ÃºÃ¼Ä‰Ä�Ä¥ÄµÅ�Å­Ã½Å¡Å¾Ä�Ã§ÃŸ-]+[',.]?\\s?){1,4}";

	/** Email Regular expression to be valid */
	public static final String REGEXP_VALID_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	/** Telephone number regular expression to be valid */
	public static final String REGEXP_VALID_PHONE = "^(\\+)?(\\d){5,}$";

	/** Regular Expression to check on valid Version */
	public static final String REGEXP_VALID_ANALYSIS_VERSION = "\\d+|\\d+\\.\\d+|\\d+\\.\\d+\\.\\d+";

	/** Regular Expression for a valid Item Information Type */
	public static final String REGEXP_VALID_ITEMINFORMATION_TYPE = "Scope|Organisation";

	/** Regular Expression to check on valid Alpha 3 code */
	public static final String REGEXP_VALID_ALPHA_3 = "[A-Z,a-z]{3}";

	/** Regular Expression to check on category */
	public static final String REGEXP_VALID_MATURITY_CATEGORY = "Policies|Procedure|Implementation|Test|Integration";

	/** Status regular expression */
	public static final String REGEXP_VALID_MEASURE_STATUS = "AP|NA|M";

	/** Norm Caption regular Expression */
	public static final String REGEXP_VALID_NORM_NAME = "Maturity|2700[1-2]|[cC]ustom";

	/** Regular expression for Parameter types */
	public final static String REGEXP_VALID_PARAMETERTYPE = "ILPS|IMPACT|IMPSCALE|MAXEFF|PROBA|SINGLE";

	/** Regular expression for asset types */
	public static final String REGEXP_VALID_ASSET_TYPE = "Serv|Info|SW|HW|Net|Staff|IV|Busi|Fin|Compl";

	/** Regular expression for exposed field value */
	public static final String REGEXP_VALID_RISKINFORMATION_EXPOSED = "N|\\+|\\+\\+|-|--|=||";

	/** Regular expression for Risk Information Categories */
	public static final String REGEXP_VALID_RISKINFORMATION_TYPE = "Vul|Threat|Risk_(TBS|TBA)";

	public static final String REGEXP_VALID_SCENARIO_TYPE = "Confidentiality|Integrity|Availability|D1-Strat|D2-RH|D3-Processus|D4-BCM|D5-Soustrait|D6-SI|"
			+ "D6\\.1-Secu|D6\\.2-Dev|D6\\.3-Expl|D6\\.4-Support|D7-Aut|I1-Strat|I2-Fin|I3-Leg|I4-RH|I5-Processus|"
			+ "I6-BCM|I7-Soustrait|I8-SI|I8\\.1-Secu|I8\\.2-Dev|I8\\.3-Expl|I8\\.4-Support|I9-Prest|I10-Aut";

	/** The Regular expression for valid Risk Categories */
	public static final String REGEXP_VALID_SCENARIO_CATEGORY = "Direct([1-7]|6\\.[1-4])|Indirect([1-9]|8\\.[1-4]|10)|Confidentiality|Availability|Integrity";

	/***********************************************************************************************
	 * List of Regular Expressions - END
	 **********************************************************************************************/

	/***********************************************************************************************
	 * List of Common Constants - BEGIN
	 **********************************************************************************************/

	/** AnalysisNorm names */
	public static final String NORM_MATURITY = "Maturity";
	public static final String NORM_27001 = "27001";
	public static final String NORM_27002 = "27002";
	public static final String NORM_CUSTOM = "Custom";

	/** AnalysisNorm References */
	public static final String MATURITY_REFERENCE = "M.";
	public static final String MATURITY_FIRSTCHAR_REFERENCE = "M";
	public static final String NORM27001_FIRSTCHAR_REFERENCE = "A";

	/** Implementation rate completed -> 100% */
	public static final double MEASURE_IMPLEMENTATIONRATE_COMPLETE = 100;

	/** Measure level 1 or 3 */
	public static final int MEASURE_LEVEL_1 = 1;
	public static final int MEASURE_LEVEL_3 = 3;

	/** Double maximum Value */
	public static final double DOUBLE_MAX_VALUE = 1.79769313486231e+108;

	/** Asset, Threat (Scenario) and assessment selected sign */
	public static final String ASSET_SELECTED = "x";
	public static final String THREAT_SELECTED = "x";
	public static final String ASSESSMENT_SELECTED = "x";

	/** Asset Type Names */
	public static final String ASSET_TYPE_SERV = "Serv";
	public static final String ASSET_TYPE_INFO = "Info";
	public static final String ASSET_TYPE_SW = "SW";
	public static final String ASSET_TYPE_HW = "HW";
	public static final String ASSET_TYPE_NET = "Net";
	public static final String ASSET_TYPE_STAFF = "Staff";
	public static final String ASSET_TYPE_IV = "IV";
	public static final String ASSET_TYPE_BUSI = "Busi";
	public static final String ASSET_TYPE_FIN = "Fin";
	public static final String ASSET_TYPE_COMPL = "Compl";
	
	public static final String ASSET_TYPES = "Serv,Info,SW,HW,Net,Staff,IV,Busi,Fin,Compl";

	/** Item Information Categories */
	public static final String ITEMINFORMATION_SCOPE = "Scope";
	public static final String ITEMINFORMATION_ORGANISATION = "Organisation";

	/** Measure status */
	public static final String MEASURE_STATUS_APPLICABLE = "AP";
	public static final String MEASURE_STATUS_MANDATORY = "M";
	public static final String MEASURE_STATUS_NOT_APPLICABLE = "NA";

	/** Maturity Tasks and Categories */
	public static final String PARAMETER_MATURITY_TASK_NAME = "name";
	public static final String PARAMETER_MATURITY_TASK_POLICY = "Pol";
	public static final String PARAMETER_MATURITY_CATEGORY_POLICY = "Policies";
	public static final String PARAMETER_MATURITY_TASK_PROCEDURE = "Pro";
	public static final String PARAMETER_MATURITY_CATEGORY_PROCEDURE = "Procedure";
	public static final String PARAMETER_MATURITY_TASK_IMPLEMENTATION = "Imp";
	public static final String PARAMETER_MATURITY_CATEGORY_IMPLEMENTATION = "Implementation";
	public static final String PARAMETER_MATURITY_TASK_TEST = "Tes";
	public static final String PARAMETER_MATURITY_CATEGORY_TEST = "Test";
	public static final String PARAMETER_MATURITY_TASK_INTEGRATION = "Int";
	public static final String PARAMETER_MATURITY_CATEGORY_INTEGRATION = "Integration";

	/** Phase */
	public final static int PHASE_NOT_USABLE = 0;
	public final static int PHASE_DEFAULT = 0;

	/** Parameter Type Identifiers */
	public final static int PARAMETERTYPE_TYPE_IMPLEMENTATION_LEVEL_PER_SML = 1;
	public final static int PARAMETERTYPE_TYPE_PROPABILITY = 2;
	public final static int PARAMETERTYPE_TYPE_IMPACT = 3;
	public final static int PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE = 4;
	public final static int PARAMETERTYPE_TYPE_MAX_EFF = 5;
	public final static int PARAMETERTYPE_TYPE_SINGLE = 6;

	/** Parameter Type Names */
	public final static String PARAMETERTYPE_TYPE_IMPLEMENTATION_LEVEL_PER_SML_NAME = "ILPS";
	public final static String PARAMETERTYPE_TYPE_IMPACT_NAME = "IMPACT";
	public final static String PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME = "IMPSCALE";
	public final static String PARAMETERTYPE_TYPE_MAX_EFF_NAME = "MAXEFF";
	public final static String PARAMETERTYPE_TYPE_PROPABILITY_NAME = "PROBA";
	public final static String PARAMETERTYPE_TYPE_SINGLE_NAME = "SINGLE";

	/** Action Plan Type Names */
	public final static String ACTIONPLAN_NORMAL = "APN";
	public final static String ACTIONPLAN_PESSIMISTIC = "APP";
	public final static String ACTIONPLAN_OPTIMISTIC = "APO";
	public final static String ACTIONPLAN_PHASE_NORMAL = "APPN";
	public final static String ACTIONPLAN_PHASE_PESSIMISTIC = "APPP";
	public final static String ACTIONPLAN_PHASE_OPTIMISTIC = "APPO";

	/** Action Plan Type Identifiers */
	public final static int ACTIONPLAN_NORMAL_MODE = 1;
	public final static int ACTIONPLAN_OPTIMISTIC_MODE = 2;
	public final static int ACTIONPLAN_PESSIMISTIC_MODE = 3;
	public final static int ACTIONPLAN_PHASE_NORMAL_MODE = 4;
	public final static int ACTIONPLAN_PHASE_OPTIMISTIC_MODE = 5;
	public final static int ACTIONPLAN_PHASE_PESSIMISTIC_MODE = 6;

	/** Risk Information Types */
	public static final String RI_TYPE_THREAT = "Threat";
	public static final String RI_TYPE_VUL = "Vul";
	public static final String RI_TYPE_RISK = "Risk";
	public static final String RI_TYPE_RISK_TBS = "Risk_TBS";
	public static final String RI_TYPE_RISK_TBA = "Risk_TBA";

	/** Parameter Attributes */
	public final static String PARAMATTRIBUTE_NAME = "Name";
	public final static String PARAMATTRIBUTE_VALUE = "Value";
	public final static String PARAMATTRIBUTE_MAT_CATEGORY = "Category";
	public final static String PARAMATTRIBUTE_MAT_SML = "SML";
	public final static String PARAMATTRIBUTE_EXT_ACRONYM = "Acronym";
	public final static String PARAMATTRIBUTE_EXT_LEVEL = "Level";
	public final static String PARAMATTRIBUTE_EXT_FROM = "From";
	public final static String PARAMATTRIBUTE_EXT_TO = "To";

	/** Parameter Attribute Value Types */
	public final static String PARAMATTRIBUTE_VALUE_TYPE_STRING = "S";
	public final static String PARAMATTRIBUTE_VALUE_TYPE_INTEGER = "I";
	public final static String PARAMATTRIBUTE_VALUE_TYPE_DOUBLE = "D";

	/** CIA */
	public static final String CONFIDENTIALITY_RISK = "Confidentiality";
	public static final String INTEGRITY_RISK = "Integrity";
	public static final String AVAILABILITY_RISK = "Availability";

	
	public final static String EMPTY_STRING = "";

	public final static String CHECKBOX_CONTROL_ON = "on";

	/***********************************************************************************************
	 * List of Common Constants - END
	 **********************************************************************************************/

	/***********************************************************************************************
	 * MYSQL Constant Fields - BEGIN
	 **********************************************************************************************/

	/** List of Constants for the MySQL Table "Analysis" */
	public final static String ANALYSIS_MYSQL_AUTHOR = "dtAuthor";
	public final static String ANALYSIS_MYSQL_COMMENT = "dtComment";
	public final static String ANALYSIS_MYSQL_DATE = "dtDateComment";
	public final static String ANALYSIS_MYSQL_VERSION = "idVersion";

	/** List of Constants for the MySQL Table "RiskInformation" */
	public final static String RI_MYSQL_CATEGORY = "dtCategory";
	public final static String RI_MYSQL_LABEL = "dtLabel";
	public final static String RI_MYSQL_ACRONYM = "dtAcronym";
	public final static String RI_MYSQL_CHAPTER = "dtChapter";
	public final static String RI_MYSQL_EXPOSED = "dtExposed";
	public final static String RI_MYSQL_COMMENT = "dtComment";
	public final static String RI_MYSQL_HIDDEN_COMMENT = "dtHiddenComment";

	/** List of Constants for the MySQL Table "ItemInformation" */
	public final static String ITEMINFO_MYSQL_VALUE = "dtValue";
	public final static String ITEMINFO_MYSQL_LABEL = "dtLabel";
	public final static String ITEMINFO_MYSQL_TYPE = "dtType";

	/** List of Constants for the MySQL Table "Parameter" */
	public final static String PARAM_MYSQL_ID = "idParameter";
	public final static String PARAM_MYSQL_TYPE = "fiParameterType";

	/** List of Constants for the MySQL Table "ParameterAttribute" */
	public final static String PARAMATTRIBUTE_MYSQL_ATTRIBUTE = "dtAttribute";
	public final static String PARAMATTRIBUTE_MYSQL_VALUE = "dtValue";

	/** List of Constants for the MySQL Table "ParameterType" */
	public final static String PARAMETERTYPE_MYSQL_LABEL = "dtLabel";

	/** List of Constants for the MySQL Table "Asset" */
	public final static String ASSET_MYSQL_ID = "idAsset";
	public final static String ASSET_MYSQL_LABEL = "dtLabel";
	public final static String ASSET_MYSQL_VALUE = "dtValue";
	public final static String ASSET_MYSQL_COMMENT = "dtComment";
	public final static String ASSET_MYSQL_SELECTED = "dtSelected";

	/** List of Constants for the MySQL Table "AssetType" */
	public final static String ASSETTYPE_MYSQL_ID = "idAssetType";
	public final static String ASSETTYPE_MYSQL_LABEL = "dtLabel";

	/** List of Constants for the MySQL Table "ScenarioAssetTypeValue" */
	public final static String SCENARIOASSETTYPEVALUE_MYSQL_ID = "dtValue";

	/** List of Constants for the MySQL Table "Scenario" */
	public final static String SCENARIO_MYSQL_ID = "idScenario";
	public final static String SCENARIO_MYSQL_LABEL = "dtLabel";
	public final static String SCENARIO_MYSQL_SELECTED = "dtSelected";
	public final static String SCENARIO_MYSQL_DESCRIPTION = "dtDescription";
	public final static String SCENARIO_MYSQL_CONFIDENTIALITY = "dtConfidentialityCat";
	public final static String SCENARIO_MYSQL_INTEGRITY = "dtIntegrityCat";
	public final static String SCENARIO_MYSQL_AVAILABILITY = "dtAvailabilityCat";
	public final static String SCENARIO_MYSQL_PREVENTIVE = "dtPreventive";
	public final static String SCENARIO_MYSQL_DETECTIVE = "dtDetective";
	public final static String SCENARIO_MYSQL_LIMITABILITY = "dtLimitability";
	public final static String SCENARIO_MYSQL_CORRECTIVE = "dtCorrective";
	public final static String SCENARIO_MYSQL_INTENTIONAL = "dtIntentional";
	public final static String SCENARIO_MYSQL_ACCIDENTAL = "dtAccidental";
	public final static String SCENARIO_MYSQL_ENVIRONMENTAL = "dtEnvironmental";
	public final static String SCENARIO_MYSQL_INTERNALTHREAT = "dtInternalThreat";
	public final static String SCENARIO_MYSQL_EXTERNALTHREAT = "dtExternalThreat";

	/** List of Constants for the MySQL Table "ScenarioType" */
	public static final String SCENARIOTYPE_MYSQL_ID = "idScenarioType";
	public static final String SCENARIOTYPE_MYSQL_LABEL = "dtLabel";

	/** List of Constants for the MySQL Table "ActionPlan" */
	public final static String ACTIONPLAN_MYSQL_ID = "idActionPlanCalculation";
	public final static String ACTIONPLAN_MYSQL_MEASURE_ID = "idMeasureDescription";
	public final static String ACTIONPLAN_MYSQL_ORDER = "dtOrder";
	public final static String ACTIONPLAN_MYSQL_COST = "dtCost";
	public final static String ACTIONPLAN_MYSQL_ROI = "dtROI";
	public final static String ACTIONPLAN_MYSQL_TOTALALE = "dtTotalALE";
	public final static String ACTIONPLAN_MYSQL_DELTAALE = "dtDeltaALE";

	/** List of Constants for the MySQL Table "ActionPlanAsset" */
	public final static String ACTIONPLANASSET_MYSQL_ASSET_ID = "idAsset";

	/** List of Constants for the MySQL Table "ActionPlanType" */
	public final static String ACTIONPLANTYPE_MYSQL_ID = "idActionPlanType";
	public final static String ACTIONPLANTYPE_MYSQL_LABEL = "dtLabel";

	/** List of Constants for the MySQL Table "ActionPlanAsset" */
	public final static String ACTIONPLANASSET_MYSQL_CURRENTALE = "dtCurrentALE";

	/** List of Constants for the MySQL Table "actionplansummary" */
	public static final String ACTIONPLANSUMMARY_MYSQL_ID = "idActionPlanSummary";
	public final static String ACTIONPLANSUMMARY_MYSQL_CONFORMANCE27001 = "dt27001Conformance";
	public final static String ACTIONPLANSUMMARY_MYSQL_CONFORMANCE27002 = "dt27001Conformance";
	public final static String ACTIONPLANSUMMARY_MYSQL_CURRENTMEASURECOST = "dtCurrentCostMeasures";
	public final static String ACTIONPLANSUMMARY_MYSQL_CURRENTDELTAALE = "dtCurrentDeltaALE";
	public final static String ACTIONPLANSUMMARY_MYSQL_TOTALEXTERNALMAINTENANCE = "dtTotalExternalMaintenance";
	public final static String ACTIONPLANSUMMARY_MYSQL_TOTALEXTERNALWORKLOAD = "dtTotalExternalWorkload";
	public final static String ACTIONPLANSUMMARY_MYSQL_IMPLEMENTEDMEASURECOUNT = "dtImplementedMeasureCount";
	public final static String ACTIONPLANSUMMARY_MYSQL_TOTALINTERNALMAINTENANCE = "dtTotalInternalMaintenance";
	public final static String ACTIONPLANSUMMARY_MYSQL_TOTALINTERNALWORKLOAD = "dtTotalInternalWorkload";
	public final static String ACTIONPLANSUMMARY_MYSQL_INVESTMENT = "dtInvestment";
	public final static String ACTIONPLANSUMMARY_MYSQL_MEASURECOUNT = "dtMeasureCount";
	public final static String ACTIONPLANSUMMARY_MYSQL_RECURRENTCOST = "dtRecurrentCost";
	public final static String ACTIONPLANSUMMARY_MYSQL_RELATIVEROSI = "dtRelativeROSI";
	public final static String ACTIONPLANSUMMARY_MYSQL_ROSI = "dtROSI";
	public final static String ACTIONPLANSUMMARY_MYSQL_STAGENAME = "idName";
	public final static String ACTIONPLANSUMMARY_MYSQL_CURRENTTOTALALE = "dtCurrentTotalALE";
	public final static String ACTIONPLANSUMMARY_MYSQL_TOTALCOST = "dtTotalCost";

	/** List of Constants for the MySQL Table "MaturityMeasure" */
	public final static String MATMEASURE_MYSQL_ID = "idMaturityMeasure";
	public final static String MATMEASURE_MYSQL_STATUS = "dtStatus";
	public final static String MATMEASURE_MYSQL_DESCRIPTION_ID = "fiMeasureDescription";
	public final static String MATMEASURE_MYSQL_IMP_RATE = "fiImplementationRateParameter";
	public final static String MATMEASURE_MYSQL_INTERNALWORKLOAD = "dtInternalWorkLoad";
	public final static String MATMEASURE_MYSQL_EXTERNALWORKLOAD = "dtExternalWorkLoad";
	public final static String MATMEASURE_MYSQL_INVESTMENT = "dtInvestment";
	public final static String MATMEASURE_MYSQL_LIFETIME = "dtLifetime";
	public final static String MATMEASURE_MYSQL_MAINTENANCE = "dtMaintenance";
	public final static String MATMEASURE_MYSQL_COST = "dtCost";
	public final static String MATMEASURE_MYSQL_COMMENT = "dtComment";
	public final static String MATMEASURE_MYSQL_TODO = "dtToDo";
	public final static String MATMEASURE_MYSQL_REACHEDSML = "dtReachedLevel";
	public final static String MATMEASURE_MYSQL_SML1COST = "dtSML1Cost";
	public final static String MATMEASURE_MYSQL_SML2COST = "dtSML2Cost";
	public final static String MATMEASURE_MYSQL_SML3COST = "dtSML3Cost";
	public final static String MATMEASURE_MYSQL_SML4COST = "dtSML4Cost";
	public final static String MATMEASURE_MYSQL_SML5COST = "dtSML5Cost";

	/** List of Constants for the MySQL Table "NormMeasure" */
	public final static String MEASURE_MYSQL_ID = "idNormMeasure";
	public final static String MEASURE_MYSQL_STATUS = "dtStatus";
	public final static String MEASURE_MYSQL_DESCRIPTION_ID = "fiMeasureDescription";
	public final static String MEASURE_MYSQL_IMP_RATE = "dtImplementationRate";
	public final static String MEASURE_MYSQL_PHASE_ID = "fiPhase";
	public final static String MEASURE_MYSQL_INTERNALWORKLOAD = "dtInternalWorkLoad";
	public final static String MEASURE_MYSQL_EXTERNALWORKLOAD = "dtExternalWorkLoad";
	public final static String MEASURE_MYSQL_INVESTMENT = "dtInvestment";
	public final static String MEASURE_MYSQL_LIFETIME = "dtLifetime";
	public final static String MEASURE_MYSQL_MAINTENANCE = "dtMaintenance";
	public final static String MEASURE_MYSQL_COST = "dtCost";
	public final static String MEASURE_MYSQL_COMMENT = "dtComment";
	public final static String MEASURE_MYSQL_TODO = "dtToDo";
	public final static String MEASURE_MYSQL_TOCHECK = "dtToCheck";
	public final static String MEASURE_MYSQL_STRENGTH_MEASURE = "dtMeasureStrength";
	public final static String MEASURE_MYSQL_STRENGTH_SECTORAL = "dtSectoralStrength";
	public final static String MEASURE_MYSQL_CIA_CONFIDENTIALITY = "dtConfidentialityCat";
	public final static String MEASURE_MYSQL_CIA_INTEGRITY = "dtIntegrityCat";
	public final static String MEASURE_MYSQL_CIA_AVAILABILITY = "dtAvailabilityCat";
	public final static String MEASURE_MYSQL_RISK_PREVENTIVE = "dtPreventiveRisk";
	public final static String MEASURE_MYSQL_RISK_DETECTIVE = "dtDetectiveRisk";
	public final static String MEASURE_MYSQL_RISK_LIMITABILITY = "dtLimitabilityRisk";
	public final static String MEASURE_MYSQL_RISK_CORRECTIVE = "dtCorrectiveRisk";
	public final static String MEASURE_MYSQL_RISK_INTENTIONAL = "dtIntentionalRisk";
	public final static String MEASURE_MYSQL_RISK_ACCIDENTAL = "dtAccidentalRisk";
	public final static String MEASURE_MYSQL_RISK_ENVIRONMENTAL = "dtEnvironmentalRisk";
	public final static String MEASURE_MYSQL_RISK_INTERNALTHREAT = "dtInternalThreatRisk";
	public final static String MEASURE_MYSQL_RISK_EXTERNALTHREAT = "dtExternalThreatRisk";
	public final static String MEASURE_MYSQL_SOA_REFERENCE = "dtReferenceSOA";
	public final static String MEASURE_MYSQL_SOA_COMMENT = "dtCommentSOA";
	public final static String MEASURE_MYSQL_SOA_RISK = "dtRiskSOA";

	/** List of Constants for the MySQL Table "MeasureDescription" */
	public static final String MEASUREDESCRIPTION_MYSQL_ID = "idMeasureDescription";
	public final static String MEASUREDESCRIPTION_MYSQL_LEVEL = "dtLevel";
	public final static String MEASUREDESCRIPTION_MYSQL_REFERENCE = "dtReference";
	public final static String MEASUREDESCRIPTION_MYSQL_NORM_ID = "fiNorm";

	/** List of Constants for the MySQL Table "MeasureDescriptionText" */
	public final static String MEASUREDESCRIPTIONTEXT_MYSQL_DOMAIN = "dtDomain";
	public final static String MEASUREDESCRIPTIONTEXT_MYSQL_DESCRIPTION = "dtDescription";

	/** List of Constants for the MySQL Table "AnalysisNorm" */
	public final static String NORM_MYSQL_ID = "idNorm";
	public final static String NORM_MYSQL_LABEL = "dtLabel";

	/** List of Constants for the MySQL Table "Phase" */
	public final static String PHASE_MYSQL_ID = "idPhase";
	public final static String PHASE_MYSQL_BEGINDATE = "dtBeginDate";
	public final static String PHASE_MYSQL_ENDDATE = "dtEndDate";

	/** List of Constants for the MySQL Table "AssetTypeValue" */
	public final static String ASSETTYPEVALUE_MYSQL_VALUE = "dtValue";

	/** List of Constants for the MySQL Table "Assessment" */
	public final static String ASSESSMENT_MYSQL_ID = "idAssessment";
	public final static String ASSESSMENT_MYSQL_ASSET_ID = "idAsset";
	public final static String ASSESSMENT_MYSQL_SCENARIO_ID = "idScenario";
	public final static String ASSESSMENT_MYSQL_COMMENT = "dtComment";
	public static final String ASSESSMENT_MYSQL_IMPACT_LEG = "dtImpactLeg";
	public static final String ASSESSMENT_MYSQL_IMPACT_OP = "dtImpactOp";
	public static final String ASSESSMENT_MYSQL_IMPACT_REP = "dtImpactRep";
	public final static String ASSESSMENT_MYSQL_IMPACT_FIN = "dtImpactFin";
	public final static String ASSESSMENT_MYSQL_IMPACT_REAL = "dtImpactReal";
	public final static String ASSESSMENT_MYSQL_LIKELIHOOD = "dtLikelihood";
	public final static String ASSESSMENT_MYSQL_LIKELIHOOD_REAL = "dtLikelihoodReal";
	public final static String ASSESSMENT_MYSQL_UNCERTAINTY = "dtUncertainty";
	public final static String ASSESSMENT_MYSQL_ALE = "dtALE";
	public final static String ASSESSMENT_MYSQL_ALEO = "dtALEO";
	public final static String ASSESSMENT_MYSQL_ALEP = "dtALEP";

	/** List of Constants for the MySQL Table "Language" */
	public final static String LANGUAGE_MYSQL_ID = "idLanguage";
	public final static String LANGUAGE_MYSQL_ALPHA3 = "dtAlpha3";
	public final static String LANGUAGE_MYSQL_NAME = "dtName";
	public final static String LANGUAGE_MYSQL_ALTNAME = "dtAlternativeName";

	/** List of Constants for the MySQL Table "RiskRegister" */
	public static final String RISKREGISTER_MYSQL_ID = "idRiskRegisterEntry";
	public static final String RISKREGISTER_MYSQL_SCENARIO_ID = "idScenario";
	public static final String RISKREGISTER_MYSQL_POSITION = "dtOrder";
	public static final String RISKREGISTER_MYSQL_RAW_PROPABILITY = "dtRawEvaluationProbability";
	public static final String RISKREGISTER_MYSQL_RAW_IMPACT = "dtRawEvaluationImpact";
	public static final String RISKREGISTER_MYSQL_RAW_IMPORTANCE = "dtRawEvaluationImportance";
	public static final String RISKREGISTER_MYSQL_NET_PROPABILITY = "dtNetEvaluationProbability";
	public static final String RISKREGISTER_MYSQL_NET_IMPACT = "dtNetEvaluationImpact";
	public static final String RISKREGISTER_MYSQL_NET_IMPORTANCE = "dtNetEvaluationImportance";
	public static final String RISKREGISTER_MYSQL_EXP_PROPABILITY = "dtExpectedProbability";
	public static final String RISKREGISTER_MYSQL_EXP_IMPACT = "dtExpectedImpact";
	public static final String RISKREGISTER_MYSQL_EXP_IMPORTANCE = "dtExpectedImportance";
	public static final String RISKREGISTER_MYSQL_STRATEGY = "dtResponseStrategy";

	/***********************************************************************************************
	 * MYSQL Constant Fields - END
	 **********************************************************************************************/

	/***********************************************************************************************
	 * SQLITE Constant Fields - BEGIN
	 **********************************************************************************************/

	/** List of Constants for the Sqlite Table "identifier" */
	public static final String IDENTIFIER_ID = "id_analysis";
	public static final String IDENTIFIER_LABEL = "label";

	/** List of Constants for the Sqlite Table "maturity_required_LIPS" */
	public final static String MATURITY_REQUIRED_LIPS_SML = "SML";
	public final static String MATURITY_REQUIRED_LIPS_VALUE = "value";

	/** List of Constants for the Sqlite Table "maturity_IS" */
	public final static String MATURITY_IS_LINE = "line";
	public final static String MATURITY_IS_VALUE = "value";

	/** List of Constants for the Sqlite Table "maturity_MAX_EFF" */
	public final static String MATURITY_MAX_EFF_COL = "col";
	public final static String MATURITY_MAX_EFF_VALUE = "value";

	/** List of Constants for the Sqlite Table "maturities" */
	public static final String MATURITY_SML5 = "sml5";
	public static final String MATURITY_SML4 = "sml4";
	public static final String MATURITY_SML3 = "sml3";
	public static final String MATURITY_SML2 = "sml2";
	public static final String MATURITY_SML1 = "sml1";
	public static final String MATURITY_REACHED = "reached";
	public static final String MATURITY_INVESTMENT = "investment";
	public static final String MATURITY_EXTWL = "extwl";
	public static final String MATURITY_INTWL = "intwl";
	public static final String MATURITY_REF = "ref";
	public static final String MATURITY_RATE = "rate";
	public static final String MATURITY_DOMAIN = "domain";
	public static final String VALUE_SPEC = "value_spec";

	/** List of Constants for the Sqlite Table "maturity_phase" */
	public final static String MATURITYPHASE_ID = "phase";

	/** List of Constants for the Sqlite Table "measures" */
	public static final String MEASURE_QUESTION_MEASURE = "question_measure";
	public static final String MEASURE_DOMAIN_MEASURE = "domain_measure";
	public static final String MEASURE_SOA_RISK = "soa_risk";
	public static final String MEASURE_SOA_COMMENT = "soa_comment";
	public static final String MEASURE_SOA_REFERENCE = "soa_reference";
	public static final String MEASURE_INTERNAL_THREAT = "internal_threat";
	public static final String MEASURE_ENVIRONMENTAL = "environmental";
	public static final String MEASURE_ACCIDENTAL = "accidental";
	public static final String MEASURE_INTENTIONAL = "intentional";
	public static final String MEASURE_CORRECTIVE = "corrective";
	public static final String MEASURE_LIMITING = "limiting";
	public static final String MEASURE_DETECTIVE = "detective";
	public static final String MEASURE_PREVENTIVE = "preventive";
	public static final String MEASURE_AVAILABILITY = AVAILABILITY_RISK
			.toLowerCase();
	public static final String MEASURE_INTEGRITY = INTEGRITY_RISK.toLowerCase();
	public static final String MEASURE_CONFIDENTIALITY = CONFIDENTIALITY_RISK
			.toLowerCase();
	public static final String MEASURE_STRENGTH_SECTORAL = "strength_sectoral";
	public static final String MEASURE_STRENGTH_MEASURE = "strength_measure";
	public static final String MEASURE_REVISION = "revision";
	public static final String MEASURE_TODO = "todo";
	public static final String MEASURE_COMMENT = "comment";
	public static final String MEASURE_MAINTENANCE = "maintenance";
	public static final String MEASURE_LIFETIME = "lifetime";
	public static final String MEASURE_INVESTISMENT = "investisment";
	public static final String MEASURE_EXTERNAL_SETUP = "external_setup";
	public static final String MEASURE_INTERNAL_SETUP = "internal_setup";
	public static final String MEASURE_IMPLEMENTATION_RATE = "implementation_rate";
	public static final String MEASURE_PHASE = "phase";
	public static final String MEASURE_STATUS = "status";
	public static final String MEASURE_LEVEL = "level";
	public static final String MEASURE_REF_MEASURE = "ref_measure";
	public static final String MEASURE_ID_NORM = "id_norme";
	public static final String MEASURE_EXTERNAL_THREAT = "external_threat";
	public static final String MEASURE_DESCRIPTION_LEVEL = "level";
	public static final String MEASURE_DESCRIPTION_REF = "ref";

	/** List of Constants for the Sqlite Table "phase" */
	public final static String PHASE_NUMBER = "num_phase";
	public static final String PHASE_END_DATE = "end_date";
	public static final String PHASE_BEGIN_DATE = "begin_date";

	/** List of Constants for the Sqlite Table "potentiality" */
	public static final String SCALE_POTENTIALITY = "scale_potentiality";
	public static final String VALUE_POTENTIALITY = "value_potentiality";
	public static final String ACRO_POTENTIALITY = "acro_potentiality";
	public static final String NAME_POTENTIALITY = "name_potentiality";
	public static final String VALUE_FROM_POTENTIALITY = "value_from_potentiality";
	public static final String VALUE_TO_POTENTIALITY = "value_to_potentiality";

	/** List of Constants for the Sqlite Table "impact" */
	public static final String SCALE_IMPACT = "scale_impact";
	public static final String VALUE_IMPACT = "value_impact";
	public static final String ACRO_IMPACT = "acro_impact";
	public static final String NAME_IMPACT = "name_impact";
	public static final String VALUE_FROM_IMPACT = "value_from_impact";
	public static final String VALUE_TO_IMPACT = "value_to_impact";

	/** List of Constants for the Sqlite Table "parameter" */
	public static final String PARAMETER_TUNING = "tuning";
	public static final String MANDATORY_PHASE = "mandatoryPhase";
	public static final String SOA_THRESHOLD = "soaThreshold";
	public static final String IMPORTANCE_THRESHOLD = "importanceThreshold";

	public static final String PARAMETER_MAINTENANCE_DEFAULT = "maintenance_default";
	public static final String PARAMETER_LIFETIME_DEFAULT = "lifetime_default";
	public static final String PARAMETER_EXTERNAL_SETUP_RATE = "external_setup_rate";
	public static final String PARAMETER_INTERNAL_SETUP_RATE = "internal_setup_rate";

	/** List of Constants for the Sqlite Table "assessment" */
	public static final String ASSESSMENT_COMMENT = MEASURE_COMMENT;
	public static final String ASSESSMENT_HIDE_COMMENT = "comment_2";
	public static final String ASSESSMENT_UNCERTAINTY = "uncertainty";
	public static final String ASSESSMENT_IMPACT_REP = "impact_reputation";
	public static final String ASSESSMENT_IMPACT_LEG = "impact_legal";
	public static final String ASSESSMENT_IMPACT_OP = "impact_operational";
	public static final String ASSESSMENT_IMPACT_FIN = "impact_financial";
	public static final String ASSESSMENT_IMPACT_REAL = "impact_hidden";
	public static final String ASSESSMENT_POTENTIALITY = "potentiality";
	public static final String ASSESSMENT_POTENTIALITY_REAL = "potentiality_hidden";
	public static final String ASSESSMENT_SEL_ASSESSMENT = "selected";

	/** List of Constants for the Sqlite Table "history" */
	public static final String HISTORY_COMMENT = MEASURE_COMMENT;
	public static final String HISTORY_AUTHOR = "author";
	public static final String HISTORY_DATE = "date";
	public static final String HISTORY_ID_VERSION = "id_version";

	/** List of Constants for the Sqlite Table "Threat" */
	public static final String THREAT_INTERNAL_THREAT = MEASURE_INTERNAL_THREAT;
	public static final String THREAT_EXTERNAL_THREAT = "external_threat";
	public static final String THREAT_ENVIRONMENTAL = MEASURE_ENVIRONMENTAL;
	public static final String THREAT_ACCIDENTAL = MEASURE_ACCIDENTAL;
	public static final String THREAT_INTENTIONAL = MEASURE_INTENTIONAL;
	public static final String THREAT_CORRECTIVE = MEASURE_CORRECTIVE;
	public static final String THREAT_LIMITATIVE = "limitative";
	public static final String THREAT_DETECTIVE = MEASURE_DETECTIVE;
	public static final String THREAT_PREVENTIVE = MEASURE_PREVENTIVE;
	public static final String THREAT_AVAILABILITY = MEASURE_AVAILABILITY;
	public static final String THREAT_INTEGRITY = MEASURE_INTEGRITY;
	public static final String THREAT_CONFIDENTIALITY = MEASURE_CONFIDENTIALITY;
	public static final String THREAT_SERV = "serv";
	public static final String THREAT_INFO = "info";
	public static final String THREAT_SW = "sw";
	public static final String THREAT_HW = "hw";
	public static final String THREAT_NET = "net";
	public static final String THREAT_STAFF = "staff";
	public static final String THREAT_IV = "iv";
	public static final String THREAT_BUSI = "busi";
	public static final String THREAT_FIN = "fin";
	public static final String THREAT_COMPL = "compl";
	public static final String THREAT_DESCRIPTION_THREAT = "description_threat";
	public static final String THREAT_SEL_THREAT = "sel_threat";
	public static final String THREAT_NAME_THREAT = "name_threat";
	public static final String THREAT_ID_THREAT = "id_threat";
	public static final String THREAT_ID_TYPE_THREAT = "id_type_threat";

	/** List of Constants for the Sqlite Table "threat_types" */
	public static final String THREAT_TYPE_LABEL = "name_type_threat";

	/** List of Constants for the Sqlite Table "Asset" */
	public static final String ASSET_SEL_ASSET = "sel_asset";
	public static final String ASSET_COMMENT_ASSET = "comment_asset";
	public static final String ASSET_HIDE_COMMENT_ASSET = "comment_asset_2";
	public static final String ASSET_VALUE_ASSET = "value_asset";
	public static final String ASSET_NAME_ASSET = "name_asset";
	public static final String ASSET_ID_TYPE_ASSET = "id_type_asset";
	public static final String ASSET_ID_ASSET = "id_asset";

	public static final String SCOPE_EXCLUDE = "soaThreshold,mandatoryPhase,importanceThreshold,internal_setup_rate,external_setup_rate,lifetime_default,maintenance_default,tuning";

	/** List of Constants for the Sqlite Table "asset_types" */
	public static final String ASSET_TYPE_LABEL = "name_type_asset";

	/** List of Constants for the Risk Information tables in the Sqlite File */
	public static final String RI_TYPE = "type";
	public static final String RI_COMMENT2 = "comment2";
	public static final String RI_COMMENT = MEASURE_COMMENT;
	public static final String RI_EXPO = "expo";
	public static final String RI_LEVEL = "level";
	public static final String RI_ACRO = "acro";
	public static final String RI_NAME = "name";

	/***********************************************************************************************
	 * SQLITE CONSTANT FIELDS - END
	 **********************************************************************************************/
}