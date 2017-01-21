package lu.itrust.business.TS.constants;

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
	 * List of Role and permissions (for controllers) - BEGIN
	 **********************************************************************************************/

	/** Role user */
	public static final String ROLE_USER_ONLY = "hasRole('ROLE_USER')";

	/** Role consultant */
	public static final String ROLE_CONSULTANT_ONLY = "hasRole('ROLE_CONSULTANT')";

	/** Role admin */
	public static final String ROLE_ADMIN_ONLY = "hasRole('ROLE_ADMIN')";

	/** Role supervisor */
	public static final String ROLE_SUPERVISOR_ONLY = "hasRole('ROLE_SUPERVISOR')";

	/** Role at least consultant */
	public static final String ROLE_MIN_USER = "hasAnyRole('ROLE_USER', 'ROLE_CONSULTANT', 'ROLE_ADMIN', 'ROLE_SUPERVISOR')";

	/** Role at least consultant */
	public static final String ROLE_MIN_CONSULTANT = "hasAnyRole('ROLE_CONSULTANT', 'ROLE_ADMIN', 'ROLE_SUPERVISOR')";

	/** Role supervisor */
	public static final String ROLE_MIN_ADMIN = "hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')";

	public static final String SETTING_DEFAULT_SHOW_UNCERTAINTY = "DEFAULT_SHOW_UNCERTAINTY";

	public static final String SETTING_DEFAULT_SHOW_CSSF = "DEFAULT_SHOW_CSSF";

	public static final String SETTING_DEFAULT_UI_LANGUAGE = "DEFAULT_UI_LANGUAGE";

	public static final String SETTING_SHOW_UNCERTAINTY = "SHOW_UNCERTAINTY";

	public static final String SETTING_SHOW_CSSF = "SHOW_CSSF";

	public static final String SETTING_LANGUAGE = "LANGUAGE";

	/***********************************************************************************************
	 * List of Role and permissions (for controllers)s - BEGIN
	 **********************************************************************************************/

	/***********************************************************************************************
	 * action plan modes used inside controller - BEGIN
	 **********************************************************************************************/

	/** normal (only phase normal) */
	public static final String ACTIONPLAN_CONTROLLER_NORMAL = "normal";

	/** Role consultant */
	public static final String ACTIONPLAN_CONTROLLER_UNCERTAINTY = "all";

	/***********************************************************************************************
	 * action plan modes used inside controller - END
	 **********************************************************************************************/

	/***********************************************************************************************
	 * item information field values - BEGIN
	 **********************************************************************************************/

	public static final String TYPE_ORGANISM = "type_organism";
	public static final String TYPE_PROFIT_ORGANISM = "type_profit_organism";
	public static final String NAME_ORGANISM = "name_organism";
	public static final String PRESENTATION_ORGANISM = "presentation_organism";
	public static final String SECTOR_ORGANISM = "sector_organism";
	public static final String RESPONSIBLE_ORGANISM = "responsible_organism";
	public static final String STAFF_ORGANISM = "staff_organism";
	public static final String ACTIVITIES_ORGANISM = "activities_organism";
	public static final String EXCLUDED_ASSETS = "excluded_assets";
	public static final String OCCUPATION = "occupation";
	public static final String FUNCTIONAL = "functional";
	public static final String JURIDIC = "juridic";
	public static final String POL_ORGANISATION = "pol_organisation";
	public static final String MANAGEMENT_ORGANISATION = "management_organisation";
	public static final String PREMISES = "premises";
	public static final String REQUIREMENTS = "requirements";
	public static final String EXPECTATIONS = "expectations";
	public static final String ENVIRONMENT = "environment ";
	public static final String INTERFACE = "interface";
	public static final String STRATEGIC = "strategic";
	public static final String PROCESSUS_DEVELOPMENT = "processus_development";
	public static final String STAKEHOLDER_IDENTIFICATION = "stakeholder_identification";
	public static final String ROLE_RESPONSABILITY = "role_responsability";
	public static final String STAKEHOLDER_RELATION = "stakeholder_relation";
	public static final String ESCALATION_WAY = "escalation_way";
	public static final String DOCUMENT_CONSERVE = "document_conserve";

	public static final String IS_NOT_ACHIEVED = "not_achieved";
	public static final String IS_RUDIMENTARY_ACHIEVED = "rudimentary_achieved";
	public static final String IS_PARTIALLY_ACHIEVED = "partially_achieved";
	public static final String IS_LARGELY_ACHIEVED = "largly_achieved";
	public static final String IS_FULLY_ACHIEVED = "fully_achieved";

	/***********************************************************************************************
	 * item information field values - END
	 **********************************************************************************************/

	/***********************************************************************************************
	 * List of Regular Expressions - BEGIN
	 **********************************************************************************************/

	/** Regular expression to validate likelihood value */
	public static final String REGEXP_VALID_LIKELIHOOD = "i|l[13579]|r|3a|a|t|m";

	/** Regular expression to validate impactFin value */
	public static final String REGEXP_VALID_IMPACT = "\\d+|\\d+\\.\\d*|[cC]([0-9]|10)";

	public static final String REGEXP_VALID_IMPACT_ACRONYM = "[cC]([0-9]|10)";

	/** Regular *Expression to check on valid Names */
	public static final String REGEXP_VALID_NAME = "^([a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð -]+[',.]?\\s?){1,4}";

	public static final String REGEXP_VALID_USERNAME = "^([a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð_0-9]+[.]?){1,4}";

	/** Email Regular expression to be valid */
	public static final String REGEXP_VALID_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	/** Password Regular expression */
	public static final String REGEXP_VALID_PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*)(?=\\S+$).{8,}$";

	/** Telephone number regular expression to be valid */
	public static final String REGEXP_VALID_PHONE = "^(\\+)?(\\d){5,}$";

	/** Regular Expression to check on valid Version */
	public static final String REGEXP_VALID_ANALYSIS_VERSION = "\\d+|\\d+\\.\\d+|\\d+\\.\\d+\\.\\d+";

	/** Regular Expression for a valid Item Information Type */
	public static final String REGEXP_VALID_ITEMINFORMATION_TYPE = "Scope|Organisation";

	/** Regular Expression to check on valid Alpha 3 code */
	public static final String REGEXP_VALID_ALPHA_2 = "[A-Z,a-z]{2}";

	/** Regular Expression to check on valid Alpha 3 code */
	public static final String REGEXP_VALID_ALPHA_3 = "[A-Z,a-z]{3}";

	/** Regular Expression to check on category */
	public static final String REGEXP_VALID_MATURITY_CATEGORY = "Policies|Procedure|Implementation|Test|Integration";

	/** Status regular expression */
	public static final String REGEXP_VALID_MEASURE_STATUS = "AP|NA|M";

	/** Status regular expression */
	public static final String REGEXP_VALID_STANDARD_MEASURE_STATUS = "AP|NA";

	/** Status regular expression */
	public static final String REGEXP_VALID_MATURITY_MEASURE_STATUS = "M|NA";

	/** Standard Caption regular Expression */
	public static final String REGEXP_VALID_STANDARD_NAME = "Maturity|2700[1-2]|[cC]ustom";

	/** Regular expression for SimpleParameter types */
	public final static String REGEXP_VALID_PARAMETERTYPE = "ILPS|IMPSCALE|MAXEFF|PROBA|SINGLE|DYNAMIC|CSSF";

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

	/** Standard names */
	public static final String STANDARD_MATURITY = "Maturity";
	public static final String STANDARD_27001 = "27001";
	public static final String STANDARD_27002 = "27002";
	public static final String STANDARD_CUSTOM = "Custom";

	/** AnalysisStandard References */
	public static final String MATURITY_REFERENCE = "M.";
	public static final String MATURITY_FIRSTCHAR_REFERENCE = "M";
	public static final String STANDARD27001_FIRSTCHAR_REFERENCE = "A";

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
	public final static int PHASE_DEFAULT = 1;

	/** Parameter Type Identifiers */
	public final static int PARAMETERTYPE_TYPE_IMPLEMENTATION_LEVEL_PER_SML = 1;
	public final static int PARAMETERTYPE_TYPE_PROPABILITY = 2;
	public final static int PARAMETERTYPE_TYPE_IMPACT = 3;
	public final static int PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE = 4;
	public final static int PARAMETERTYPE_TYPE_MAX_EFF = 5;
	public final static int PARAMETERTYPE_TYPE_SINGLE = 6;
	public final static int PARAMETERTYPE_TYPE_CSSF = 7;
	public final static int PARAMETERTYPE_TYPE_DYNAMIC = 8;
	public final static int PARAMETERTYPE_TYPE_RISK_ACCEPTANCE = 9;
	public final static Integer[] ALL_ACRONYM_TYPE_IDS = { PARAMETERTYPE_TYPE_PROPABILITY, PARAMETERTYPE_TYPE_DYNAMIC };

	/** Parameter Type Names */
	public final static String PARAMETERTYPE_TYPE_IMPLEMENTATION_LEVEL_PER_SML_NAME = "ILPS";
	public final static String PARAMETERTYPE_TYPE_IMPACT_NAME = "IMPACT";
	public final static String PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME = "IMPSCALE";
	public final static String PARAMETERTYPE_TYPE_MAX_EFF_NAME = "MAXEFF";
	public final static String PARAMETERTYPE_TYPE_PROPABILITY_NAME = "PROBA";
	public final static String PARAMETERTYPE_TYPE_SINGLE_NAME = "SINGLE";
	public final static String PARAMETERTYPE_TYPE_CSSF_NAME = "CSSF";
	public final static String PARAMETERTYPE_TYPE_DYNAMIC_NAME = "DYNAMIC";
	public final static String PARAMETERTYPE_TYPE_RISK_ACCEPTANCE_NAME = "RISK_ACCEPTANCE";

	public final static String[] ALL_ACRONYM_TYPE_NAMES = { PARAMETERTYPE_TYPE_PROPABILITY_NAME, PARAMETERTYPE_TYPE_DYNAMIC_NAME };

	/**
	 * FINANCIAL, LEGAL, OPERATIONAL, REPUTATIONAL
	 * 
	 * @see #DEFAULT_IMPACT_TYPE_TRANSLATES
	 * @see #ASSESSMENT_IMPACT_NAMES
	 */
	public final static String[] DEFAULT_IMPACT_TYPE_NAMES = { "FINANCIAL", "LEGAL", "OPERATIONAL", "REPUTATIONAL" };

	/**
	 * Financial, Legal, Operational, Reputational
	 * 
	 * @see #DEFAULT_IMPACT_TYPE_TRANSLATES
	 * @see #ASSESSMENT_IMPACT_NAMES
	 */
	public final static String[] DEFAULT_IMPACT_TYPE_TRANSLATES = { "Financial", "Legal", "Operational", "Reputational" };
	
	public final static String[] DEFAULT_IMPACT_TYPE_SHORT_NAMES = { "Fin.", "Leg.", "Op.", "Rep." };

	public static final String DEFAULT_IMPACT_NAME = PARAMETERTYPE_TYPE_IMPACT_NAME;

	public static final String DEFAULT_IMPACT_TRANSLATE = "Impact";
	
	public static final String DEFAULT_IMPACT_SHORT_NAME = "Imp.";

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

	/** SimpleParameter Attributes */
	public final static String PARAMATTRIBUTE_NAME = "Name";
	public final static String PARAMATTRIBUTE_VALUE = "Value";
	public final static String PARAMATTRIBUTE_MAT_CATEGORY = "Category";
	public final static String PARAMATTRIBUTE_MAT_SML = "SML";
	public final static String PARAMATTRIBUTE_EXT_ACRONYM = "Acronym";
	public final static String PARAMATTRIBUTE_EXT_LEVEL = "Level";
	public final static String PARAMATTRIBUTE_EXT_FROM = "From";
	public final static String PARAMATTRIBUTE_EXT_TO = "To";

	/** SimpleParameter Attribute Value Types */
	public final static String PARAMATTRIBUTE_VALUE_TYPE_STRING = "S";
	public final static String PARAMATTRIBUTE_VALUE_TYPE_INTEGER = "I";
	public final static String PARAMATTRIBUTE_VALUE_TYPE_DOUBLE = "D";

	/** Parameter Category **/

	public final static String PARAMETER_CATEGORY_IMPACT = "IMPACT";

	public final static String PARAMETER_CATEGORY_PROBABILITY = "PROBABILITY";

	public final static String PARAMETER_CATEGORY_PROBABILITY_DYNAMIC = "DYNAMIC";

	public final static String PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD = "LIKELIHOOD";

	public final static String PARAMETER_CATEGORY_MATURITY = "MATURITY";

	public final static String PARAMETER_CATEGORY_SIMPLE = "SIMPLE";

	public final static String PARAMETER_CATEGORY_RISK_ACCEPTANCE = PARAMETERTYPE_TYPE_RISK_ACCEPTANCE_NAME;

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

	/** SML LEVEL **/
	public static final String MATURITY_LEVEL_SML5 = "SML5";
	public static final String MATURITY_LEVEL_SML4 = "SML4";
	public static final String MATURITY_LEVEL_SML3 = "SML3";
	public static final String MATURITY_LEVEL_SML2 = "SML2";
	public static final String MATURITY_LEVEL_SML1 = "SML1";
	public static final String MATURITY_LEVEL_SML0 = "SML0";

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
	public static final String MEASURE_AVAILABILITY = AVAILABILITY_RISK.toLowerCase();
	public static final String MEASURE_INTEGRITY = INTEGRITY_RISK.toLowerCase();
	public static final String MEASURE_CONFIDENTIALITY = CONFIDENTIALITY_RISK.toLowerCase();
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
	public static final String MEASURE_VERSION_NORM = "version_norme";
	public static final String MEASURE_RESPONSIBLE = "responsible";
	public static final String MEASURE_STANDARD_COMPUTABLE = "norme_computable";
	public static final String MEASURE_MEASURE_COMPUTABLE = "measure_computable";
	public static final String MEASURE_STANDARD_DESCRIPTION = "norme_description";
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
	public static final String LABEL_POTENTIALITY = "label_potentiality";
	public static final String VALUE_FROM_POTENTIALITY = "value_from_potentiality";
	public static final String VALUE_TO_POTENTIALITY = "value_to_potentiality";

	/** List of Constants for the Sqlite Table "impact" */
	public static final String SCALE_IMPACT = "scale_impact";
	public static final String VALUE_IMPACT = "value_impact";
	public static final String ACRO_IMPACT = "acro_impact";
	public static final String NAME_IMPACT = "name_impact";
	public static final String LABEL_IMPACT = "label_impact";
	public static final String VALUE_FROM_IMPACT = "value_from_impact";
	public static final String VALUE_TO_IMPACT = "value_to_impact";

	/** List of Constants for the Sqlite Table "parameter" */
	public static final String PARAMETER_MAX_RRF = "max_rrf";
	public static final String MANDATORY_PHASE = "mandatoryPhase";
	public static final String SOA_THRESHOLD = "soaThreshold";
	public static final String IMPORTANCE_THRESHOLD = "importanceThreshold";

	public static final String PARAMETER_MAINTENANCE_DEFAULT = "maintenance_default";
	public static final String PARAMETER_LIFETIME_DEFAULT = "lifetime_default";
	public static final String PARAMETER_EXTERNAL_SETUP_RATE = "external_setup_rate";
	public static final String PARAMETER_INTERNAL_SETUP_RATE = "internal_setup_rate";

	/** CSSF Parameters **/
	public static final String CSSF_IMPACT_THRESHOLD = "cssfImpactThreshold";
	public static final String CSSF_PROBABILITY_THRESHOLD = "cssfProbabilityThreshold";
	public static final String CSSF_DIRECT_SIZE = "cssfDirectSize";
	public static final String CSSF_INDIRECT_SIZE = "cssfIndirectSize";
	public static final String CSSF_CIA_SIZE = "cssfCIASize";
	public static final int CSSF_IMPACT_THRESHOLD_VALUE = 5;
	public static final int CSSF_PROBABILITY_THRESHOLD_VALUE = 6;

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

	/**
	 * Order must be same to {@link #DEFAULT_IMPACT_TYPE_NAMES}
	 * 
	 * @see #DEFAULT_IMPACT_TYPE_NAMES
	 * @see #DEFAULT_IMPACT_TYPE_TRANSLATES
	 */
	public static final String[] ASSESSMENT_IMPACT_NAMES = { ASSESSMENT_IMPACT_FIN, ASSESSMENT_IMPACT_LEG, ASSESSMENT_IMPACT_OP, ASSESSMENT_IMPACT_REP };

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

	public static final String SCOPE_EXCLUDE = "security_requirement_organism,key_information_organism,composants_organism,soaThreshold,mandatoryPhase,importanceThreshold,internal_setup_rate,external_setup_rate,lifetime_default,maintenance_default,max_rrf,cssfImpactThreshold,cssfProbabilityThreshold,cssfDirectSize,cssfIndirectSize,cssfCIASize,dynamic_parameter_timespan";

	/** List of Constants for the Sqlite Table "asset_types" */
	public static final String ASSET_TYPE_LABEL = "name_type_asset";

	/** List of Constants for the Risk Information tables in the Sqlite File */
	public static final String RI_TYPE = "type";
	public static final String RI_COMMENT2 = "comment2";
	public static final String RI_COMMENT = MEASURE_COMMENT;
	public static final String RI_OWNER = "owner";
	public static final String RI_EXPO = "expo";
	public static final String RI_LEVEL = "level";
	public static final String RI_ACRO = "acro";
	public static final String RI_NAME = "name";

	/***********************************************************************************************
	 * SQLITE CONSTANT FIELDS - END
	 **********************************************************************************************/

	/** Controller */
	public static final String SELECTED_ANALYSIS = "selectedAnalysis";

	public static final String OPEN_MODE = "selected-analysis-open-mode";

	public static final String SELECTED_ANALYSIS_LANGUAGE = "selected-analysis-language";

	public static final String REGEX_SPLIT_REFERENCE = "\\.|\\s|;|-|,";

	public static final String ACCEPT_APPLICATION_JSON_CHARSET_UTF_8 = "Accept=application/json;charset=UTF-8";

	public static final String FILTER_CONTROL_SQLITE = "SQLITE";

	public static final String FILTER_CONTROL_REPORT = "REPORT";

	public static final String FILTER_CONTROL_SORT_KEY = "%s_SORT";

	public static final String FILTER_CONTROL_SORT_DIRCTION_KEY = "%s_SORT_DIRECTION";

	public static final String FILTER_CONTROL_SIZE_KEY = "%s_SIZE";

	public static final String FILTER_CONTROL_FILTER_KEY = "%s_FILTER";

	public static final String FILTER_ANALYSIS_NAME = "filter_analysis_name";

	public static final String LAST_SELECTED_ANALYSIS_NAME = "last-selected-analysis-name";

	public static final String ANALYSIS_TASK_ID = "analysis_task_id";

	public static final String CURRENT_CUSTOMER = "currentCustomer";

	public static final String ALLOWED_TICKETING = "allowedTicketing";

	public static final String TICKETING_NAME = "ticketingName";

	public static final String TICKETING_URL = "ticketingURL";

	public static final String LAST_SELECTED_CUSTOMER_ID = "last-selected-customer-id";

	public static final String USER_TICKETING_SYSTEM_USERNAME = "user-titcketing-credential-username";

	public static final String USER_TICKETING_SYSTEM_PASSWORD = "user-titcketing-credential-password";

	public static final String USER_TICKETING_SYSTEM_IV = "user-titcketing-credential-iv";

	/*
	 * *************************************************************************
	 * ********************* EXTERNAL NOTIFICATIONS & DYNAMIC PARAMETERS
	 **********************************************************************************************/

	/**
	 * The maximum number of past seconds which the chart of dynamic parameter
	 * evolution should display.
	 */
	public static final long CHART_DYNAMIC_PARAMETER_EVOLUTION_HISTORY_IN_SECONDS = 12 * 30 * 86400;
	/**
	 * Factor by which the time interval (x-axis) is progressively multiplied.
	 */
	public static final double CHART_DYNAMIC_PARAMETER_LOGARITHMIC_FACTOR = 1.5;
	/**
	 * The number of seconds in the past after which the logarithmic scale
	 * should stop (in favour of a linear scale with step size equal the current
	 * step size resulting from the logarithmic increase).
	 */
	public static final long CHART_DYNAMIC_PARAMETER_MAX_SIZE_OF_LOGARITHMIC_SCALE = 86400 * 14;

	/* SQLite columns of table "dynamic_parameter" */
	public static final String NAME_PARAMETER = "name_parameter";
	public static final String ACRO_PARAMETER = "acro_parameter";
	public static final String VALUE_PARAMETER = "value_parameter";

	public static final double EVOLUTION_MIN_ALE_ABSOLUTE_DIFFERENCE = 500.; // in
																				// k€/y
	public static final double EVOLUTION_MIN_ALE_RELATIVE_DIFFERENCE = 0.2; // +/-

	public static final String API_AUTHENTICATION_TOKEN_NAME = "X-Auth-Token";

}