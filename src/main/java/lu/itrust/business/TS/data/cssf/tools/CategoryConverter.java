package lu.itrust.business.TS.data.cssf.tools;

import java.util.HashMap;
import java.util.Map;

import lu.itrust.business.TS.data.scenario.Scenario;

/**
 * CategoryConverter: <br>
 * Manage Risk Category conversion:
 * <ul>
 * <li>MySQL <-> Generic Type</li>
 * <li>SQLite <-> Generic Type</li>
 * <li>SQLite <-> MySQL</li>
 * <li>Scenario type -> Generic Type</li>
 * </ul>
 * 
 * @author itrust consulting s.�.rl. : BJA, EOM, SME
 * @version 0.1
 * @since 4 janv. 2013
 */
public class CategoryConverter {

	/***********************************************************************************************
	 * Constants
	 **********************************************************************************************/

	/**
	 * SQLITEKEYS:<br>
	 * confidentiality, integrity, availability, d1, d2, d3, d4, d5, d6, d61, d62, d63, d64, d7, i1,
	 * i2, i3, i4, i5, i6, i7, i8, i81, i82, i83, i84, i9,i10
	 */
	public static final String[] SQLITEKEYS = new String[] { "confidentiality", "integrity",
		"availability", "d1", "d2", "d3", "d4", "d5", "d6", "d61", "d62", "d63", "d64", "d7", "i1",
		"i2", "i3", "i4", "i5", "i6", "i7", "i8", "i81", "i82", "i83", "i84", "i9", "i10" };

	/**
	 * MYSQLKEYS: <br>
	 * dtConfidentialityCat, dtIntegrityCat, dtAvailabilityCat, dtDirect1Cat, dtDirect2Cat,
	 * dtDirect3Cat, dtDirect4Cat, dtDirect5Cat, dtDirect6Cat, dtDirect6.1Cat, dtDirect6.2Cat,
	 * dtDirect6.3Cat, dtDirect6.4Cat, dtDirect7Cat, dtIndirect1Cat, dtIndirect2Cat, dtIndirect3Cat,
	 * dtIndirect4Cat, dtIndirect5Cat, dtIndirect6Cat, dtIndirect7Cat, dtIndirect8Cat,
	 * dtIndirect8.1Cat, dtIndirect8.2Cat, dtIndirect8.3Cat, dtIndirect8.4Cat, dtIndirect9Cat,
	 * dtIndirect10Cat
	 */
	public static final String[] MYSQLKEYS = new String[] { "dtConfidentialityCat",
		"dtIntegrityCat", "dtAvailabilityCat", "dtDirect1Cat", "dtDirect2Cat", "dtDirect3Cat",
		"dtDirect4Cat", "dtDirect5Cat", "dtDirect6Cat", "dtDirect6.1Cat", "dtDirect6.2Cat",
		"dtDirect6.3Cat", "dtDirect6.4Cat", "dtDirect7Cat", "dtIndirect1Cat", "dtIndirect2Cat",
		"dtIndirect3Cat", "dtIndirect4Cat", "dtIndirect5Cat", "dtIndirect6Cat", "dtIndirect7Cat",
		"dtIndirect8Cat", "dtIndirect8.1Cat", "dtIndirect8.2Cat", "dtIndirect8.3Cat",
		"dtIndirect8.4Cat", "dtIndirect9Cat", "dtIndirect10Cat" };

	/**
	 * JAVAKEYS: <br>
	 * Confidentiality, Integrity, Availability, Direct1, Direct2, Direct3, Direct4, Direct5,
	 * Direct6, Direct6.1, Direct6.2, Direct6.3, Direct6.4, Direct7, Indirect1, Indirect2,
	 * Indirect3, Indirect4, Indirect5, Indirect6,Indirect7, Indirect8, Indirect8.1, Indirect8.2,
	 * Indirect8.3, Indirect8.4, Indirect9,Indirect10
	 */
	public static final String[] JAVAKEYS = new String[] { "Confidentiality", "Integrity",
		"Availability", "Direct1", "Direct2", "Direct3", "Direct4", "Direct5", "Direct6",
		"Direct6.1", "Direct6.2", "Direct6.3", "Direct6.4", "Direct7", "Indirect1", "Indirect2",
		"Indirect3", "Indirect4", "Indirect5", "Indirect6", "Indirect7", "Indirect8",
		"Indirect8.1", "Indirect8.2", "Indirect8.3", "Indirect8.4", "Indirect9", "Indirect10" };

	/**
	 * TYPE_CSSF_KEYS:<br>
	 * Direct1, Direct2, Direct3, Direct4, Direct5, Direct6, Direct6.1, Direct6.2, Direct6.3,
	 * Direct6.4, Direct7, Indirect1, Indirect2, Indirect3, Indirect4, Indirect5,
	 * Indirect6,Indirect7, Indirect8, Indirect8.1, Indirect8.2, Indirect8.3, Indirect8.4,
	 * Indirect9,Indirect10
	 */
	public static final String[] TYPE_CSSF_KEYS = new String[] { "Direct1", "Direct2", "Direct3",
		"Direct4", "Direct5", "Direct6", "Direct6.1", "Direct6.2", "Direct6.3", "Direct6.4",
		"Direct7", "Indirect1", "Indirect2", "Indirect3", "Indirect4", "Indirect5", "Indirect6",
		"Indirect7", "Indirect8", "Indirect8.1", "Indirect8.2", "Indirect8.3", "Indirect8.4",
		"Indirect9", "Indirect10" };

	/**
	 * TYPE_CIA_KEYS:<br>
	 * Confidentiality, Integrity, Availability
	 */
	public static final String[] TYPE_CIA_KEYS = new String[] { "Confidentiality", "Integrity",
		"Availability" };

	/**
	 * SCENARIO_TYPE_CSSF:<br>
	 * D1-Strat, D2-RH,D3-Processus, D4-BCM, D5-Soustrait, D6-SI, D6.1-Secu, D6.2-Dev, D6.3-Expl,
	 * D6.4-Support, D7-Aut, I1-Strat, I2-Fin, I3-Leg, I4-RH, I5-Processus, I6-BCM, I7-Soustrait,
	 * I8-SI, I8.1-Secu, I8.2-Dev, I8.3-Expl, I8.4-Support, I9-Prest, I10-Aut
	 */
	public static final String[] SCENARIO_TYPE_CSSF_KEYS = new String[] { "D1-Strat", "D2-RH",
		"D3-Processus", "D4-BCM", "D5-Soustrait", "D6-SI", "D6.1-Secu", "D6.2-Dev", "D6.3-Expl",
		"D6.4-Support", "D7-Aut", "I1-Strat", "I2-Fin", "I3-Leg", "I4-RH", "I5-Processus",
		"I6-BCM", "I7-Soustrait", "I8-SI", "I8.1-Secu", "I8.2-Dev", "I8.3-Expl", "I8.4-Support",
		"I9-Prest", "I10-Aut" };

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	/**
	 * <b>sqliteToType</b>
	 * <ul>
	 * <li>Key = Sqlite category (d61)</li>
	 * <li>Value = java type</li>
	 * </ul>
	 * <b>example:</b> sqliteToType.get("d61") == Direct6.1
	 */
	protected static Map<String, String> sqliteToType = null;

	/**
	 * <b>typeToSqlite</b>
	 * <ul>
	 * <li>Key = java type</li>
	 * <li>Value = SQLite category</li>
	 * </ul>
	 * <b>example:</b> typeToSqlite.get("Direct6.1") == d61
	 */
	protected static Map<String, String> typeToSqlite = null;

	/**
	 * <b>typeToMysql</b>
	 * <ul>
	 * <li>Key = Java type</li>
	 * <li>Value = MySQL Category</li>
	 * </ul>
	 * <b>example:</b> typeToMysql.get("Direct6.1") == dtDirect6.1Cat
	 */
	protected static Map<String, String> typeToMysql = null;

	/**
	 * <b>mysqlToType</b>
	 * <ul>
	 * <li>Key = MySQL category</li>
	 * <li>Value = java type</li>
	 * </ul>
	 * <b>example:</b> mysqlToType.get("dtDirect6.1Cat") == Direct6.1
	 */
	protected static Map<String, String> mysqlToType = null;

	/**
	 * <b>scenarioTypeToType</b>
	 * <ul>
	 * <li>Key = Scenario type</li>
	 * <li>Value = Category type</li>
	 * </ul>
	 * <b>example:</b> scenarioTypeToType.get("D6.1-Secu") == Direct6.1
	 */
	protected static Map<String, String> scenarioTypeToType = null;

	/***********************************************************************************************
	 * Methods
	 **********************************************************************************************/

	/**
	 * initialiseSqliteToJava: <br>
	 * Initialise List of sqlite category names ("sqliteToType").<br>
	 * initialise the List of Type to Sqlite List ("typeToSqlite")
	 */
	protected synchronized static void initialiseSqliteToTypeAndTypeToSQLite() {

		// check if List is already initialised - YES
		if (sqliteToType != null && typeToSqlite != null)
			return;

		// initialise the List with the siez of cssf and cia list
		Map<String, String> map = new HashMap<String, String>(JAVAKEYS.length);

		// initialise the List with the siez of cssf and cia list
		Map<String, String> map2 = new HashMap<String, String>(JAVAKEYS.length);

		// parse java generic type
		for (int i = 0; i < JAVAKEYS.length; i++) {
			// add sqlite key and generic value
			map.put(SQLITEKEYS[i], JAVAKEYS[i]);
			// add entry with reversed key and value to the typetosqlite list
			map2.put(JAVAKEYS[i], SQLITEKEYS[i]);
		}

		// set sqliteToType with created map
		sqliteToType = map;

		// set typeToSqlite with created map2
		typeToSqlite = map2;
	}

	/**
	 * initialiseMySQLToTypeAndTypeToMySQL: <br>
	 * Initialise List of Mysql category names ("mysqlToType"). Initialise List of Type category
	 * names ("typeToMysql").
	 */
	protected synchronized static void initialiseMySQLToTypeAndTypeToMySQL() {

		// check if List is already initialised - YES
		if (mysqlToType != null && typeToMysql != null)
			return;

		// initialise the List with the siez of cssf and cia list
		Map<String, String> map = new HashMap<String, String>(JAVAKEYS.length);

		// initialise the List with the siez of cssf and cia list
		Map<String, String> map2 = new HashMap<String, String>(JAVAKEYS.length);

		// parse java generic keys
		for (int i = 0; i < JAVAKEYS.length; i++) {

			// set mysql keys and generic type values
			map.put(MYSQLKEYS[i], JAVAKEYS[i]);

			// set generic type as keys and set mysql as keys
			map2.put(JAVAKEYS[i], MYSQLKEYS[i]);

			//System.out.println("Java: " + JAVAKEYS[i] + " MySQL:" + MYSQLKEYS[i]);
		}
		// set mysqltotype with map
		mysqlToType = map;

		// set typeToMysql with map2
		typeToMysql = map2;
	}

	/**
	 * initialiseScenarioToType <br>
	 * initialisation of scenario type (D1-Start,...) to type (Direct1,...) List
	 */
	protected synchronized static void initialiseScenarioTypeToType() {

		// check if List is already initialised
		if (scenarioTypeToType != null)
			return;

		// initialise size of List with cia and cssf keys
		Map<String, String> map =
			new HashMap<String, String>(TYPE_CIA_KEYS.length + SCENARIO_TYPE_CSSF_KEYS.length);

		// parse all cia keys and add them as keys with javakeys as value
		for (int i = 0; i < TYPE_CIA_KEYS.length; i++)
			map.put(TYPE_CIA_KEYS[i], JAVAKEYS[i]);

		// parse all cssf keys and add them as keys with javakeys as value

		// start at the size of the cia list (because index starts afterwards)
		for (int i = 0; i < SCENARIO_TYPE_CSSF_KEYS.length; i++) {
			map.put(SCENARIO_TYPE_CSSF_KEYS[i], JAVAKEYS[i + TYPE_CIA_KEYS.length]);
		}

		// set scenarioTypeToType
		scenarioTypeToType = map;
	}

	/**
	 * getTypeFromSQLite: <br>
	 * takes SQLite category and retrieves generic type
	 * 
	 * @param key
	 *            sqlite category
	 * @return type if key exist otherwise null
	 */
	public static String getTypeFromSQLite(String key) {

		// check if list is initialised, if not it will be done
		if (sqliteToType == null)
			initialiseSqliteToTypeAndTypeToSQLite();

		// return the corresponding value
		return sqliteToType.get(key);
	}

	/**
	 * getTypeFromMySQL: <br>
	 * CSSF: takes MySQL type and returns same generic type.
	 * 
	 * @param key
	 *            MySQL CSSF type.
	 * @return CSSF generic type otherwise null.
	 */
	public static String getTypeFromMySQL(String key) {

		if (mysqlToType == null)
			initialiseMySQLToTypeAndTypeToMySQL();

		// check if key is not null and if key is a MySQL DatabaseField (starts with "dt" and end
		// with "Cat")
		// if this check is false (it is not a valid MySQL Field) the method returns null else, the
		return mysqlToType.get(key);
	}

	/**
	 * getMySQLFromSQLite: <br>
	 * CSSF: takes SQLite type and returns same type for MySQL.
	 * 
	 * @param key
	 *            CSSF SQLite type.
	 * @return MySQL type otherwise null.
	 */
	public static String getMySQLFromSQLite(String key) {

		// check if array is not yet initialised -> NO -> initialise
		if (sqliteToType == null) {
			initialiseSqliteToTypeAndTypeToSQLite();
		}

		// check if array is not yet initialised -> NO -> initialise
		if (typeToMysql == null) {
			initialiseMySQLToTypeAndTypeToMySQL();
		}

		// return MYSQL KEy
		return getMySQLFromType(getTypeFromSQLite(key));
	}

	/**
	 * getMySQLFromType: <br>
	 * CSSF: takes generic type and returns same type for MySQL.
	 * 
	 * @param key
	 *            generic type.
	 * @return MySQL type otherwise null.
	 */
	public static String getMySQLFromType(String key) {

		// check if array is not yet initialised -> NO -> initialise
		if (typeToMysql == null) {
			initialiseMySQLToTypeAndTypeToMySQL();
		}

		// return mysql from type
		return typeToMysql.get(key);
	}

	/**
	 * getSQLiteFromType: <br>
	 * CSSF: takes generic type and returns same type for SQLite
	 * 
	 * @param key
	 *            generic type
	 * @return SQLite type otherwise null.
	 */
	public static String getSQLiteFromType(String key) {

		// check if array is not yet initialised -> NO -> initialise
		if (typeToSqlite == null) {
			initialiseSqliteToTypeAndTypeToSQLite();
		}

		// return sqlite from type
		return typeToSqlite.get(key);
	}

	/**
	 * getSQLiteFromMySQL: <br>
	 * CSSF: takes MySQL type and returns same type for SQLite
	 * 
	 * @param key
	 *            MySQL CSSF type
	 * @return SQLite type otherwise null.
	 */
	public static String getSQLiteFromMySQL(String key) {

		// check if array is not yet initialised -> NO -> initialise
		if (typeToSqlite == null) {
			initialiseSqliteToTypeAndTypeToSQLite();
		}

		// check if array is not yet initialised -> NO -> initialise
		if (mysqlToType == null) {
			initialiseMySQLToTypeAndTypeToMySQL();
		}

		// return sqlite from mysql
		return getSQLiteFromType(getTypeFromMySQL(key));
	}

	/**
	 * getTypeFromScenario: <br>
	 * retrieves the cssf or cia generic type of the given scenario, or null
	 * 
	 * @param scenario
	 *            scenario
	 * @return generic scenario type name
	 */
	public static String getTypeFromScenario(Scenario scenario) {

		// check if array is not yet initialised -> NO -> initialise
		if (scenarioTypeToType == null) {
			initialiseScenarioTypeToType();
		}

		// returns the type of the scneario if it is CSSF null when not
		return  scenarioTypeToType.get(scenario.getType().getName());
	}
	
	/**
	 * getTypeFromScenarioType: <br>
	 * retrieves CSSF genreic type or null
	 * 
	 * @param scenario
	 *            scenario
	 * @return CSSF type if scenario type is cssf otherwise null.
	 */
	public static String getTypeFromScenarioType(String scenarioType) {

		// check if array is not yet initialised -> NO -> initialise
		if (scenarioTypeToType == null) 
			initialiseScenarioTypeToType();
		// returns the type of the scneario if it is CSSF null when not
		return  scenarioTypeToType.get(scenarioType);
	}

	/**
	 * isCSSF: <br>
	 * check if scenario type is CSSF
	 * 
	 * @param scenario
	 *            scenario
	 * @return true if scenario type is CSSF Type otherwise false.
	 */
	public static boolean isCSSF(Scenario scenario) {

		// check if scnario is null -> YES
		if (scenario == null)
			return false;

		// parse CSSF keys and find the Type of the Scenario
		for (String string : SCENARIO_TYPE_CSSF_KEYS) {

			// scenario type was found in cssf
			if (string.equals(scenario.getScenarioType().getName()))
				return true;
		}

		// scenario type is not cssf
		return false;
	}
}