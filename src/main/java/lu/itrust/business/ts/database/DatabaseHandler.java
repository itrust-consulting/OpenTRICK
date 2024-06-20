package lu.itrust.business.ts.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import lu.itrust.business.ts.component.TrickLogManager;

/**
 * DatabaseHandler: This class handles the creation, closing and queriyng from
 * the databases mysql and sqlite
 * 
 * @author itrust consulting s.� r.l. - 
 * @version 0.1
 * @since 2012-08-21
 */
public class DatabaseHandler implements AutoCloseable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The connection to the Database */
	private Connection con = null;

	/** Savepoint used for a Transaction to Rollback to */
	private Savepoint sp = null;

	/**
	 * PreparedStatment for the prepared statments to query the database (secure
	 * query)
	 */
	private PreparedStatement st = null;

	/**
	 * DatabaseHandler: <br>
	 * This constructor creates a Sqlite Database Connection
	 * 
	 * @param database
	 *                 The Database Name OR the Sqlite Filename
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * 
	 * @
	 */
	public DatabaseHandler(String database) throws ClassNotFoundException, SQLException {

		// ****************************************************************
		// * use JDBC driver
		// ****************************************************************
		Class.forName("org.sqlite.JDBC");

		// ****************************************************************
		// * create URL for the connection
		// ****************************************************************
		String url = "jdbc:sqlite:" + database;

		// ****************************************************************
		// * create connection using JDBC driver
		// ****************************************************************
		con = DriverManager.getConnection(url);
	}

	/***********************************************************************************************
	 * Constructor
	 **********************************************************************************************/

	protected DatabaseHandler() {
	}

	/***********************************************************************************************
	 * Methods
	 **********************************************************************************************/

	/**
	 * beginTransaction: <br>
	 * Start a MySQL Transaction, set a SavePoint and set Autocommit to False.
	 * 
	 * @return The status if it worked
	 */
	public boolean beginTransaction() {

		System.out.println("Begin Transaction...");

		// ****************************************************************
		// * initialise return value
		// ****************************************************************
		boolean res = false;

		try {

			// ****************************************************************
			// * change the Autocommit to false (to enable transaction)
			// ****************************************************************
			con.setAutoCommit(false);

			// ****************************************************************
			// * create a savepoint to rollback to in case of an error
			// ****************************************************************
			sp = con.setSavepoint("savepoint");

			// ****************************************************************
			// return status if autocommit was really set to false
			// ****************************************************************
			res = !con.getAutoCommit();

		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
		return res;
	}

	/**
	 * close: <br>
	 * Closes the Database Connection.
	 * 
	 * @throws SQLException
	 */
	public void close() throws SQLException {

		// ****************************************************************
		// * close the database connection
		// ****************************************************************
		if (con != null && !con.isClosed())
			con.close();
	}

	/**
	 * commit: <br>
	 * Performs a Commit at the End of a Transaction and Sets Autocommit to back
	 * to True.
	 * 
	 * @return The Autocommit Status
	 */
	public boolean commit() {

		// ****************************************************************
		// * initialise return variable
		// ****************************************************************
		boolean res = false;

		try {
			// commit a transaction
			con.commit();
			// release savepoint
			con.releaseSavepoint(sp);
			// set autocommit to true (end transaction)
			con.setAutoCommit(true);
			// return autocommit status
			res = con.getAutoCommit();

		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
		return res;
	}

	/**
	 * getLastInsertId: <br>
	 * Retrieve Last ID that was Inserted into the Database.
	 * 
	 * @return The ID that was Inserted in the Last INSERT Statment
	 * 
	 * @throws SQLException
	 */
	public int getLastInsertId() throws SQLException {

		// ****************************************************************
		// * initialise the last inserted ID
		// ****************************************************************
		int lastid = 0;

		// ****************************************************************
		// * create prepared statment for the query
		// ****************************************************************
		try (PreparedStatement st1 = con.prepareStatement("SELECT LAST_INSERT_ID()")) {
			// execute the query
			ResultSet rs = st1.executeQuery();
			// retrieve the result
			while (rs.next()) {
				// ****************************************************************
				// * set the variable with the last ID
				// ****************************************************************
				lastid = rs.getInt(1);
			}
		}
		// ****************************************************************
		// * return the result
		// ****************************************************************
		return lastid;
	}

	/**
	 * getLastInsertRowId: <br>
	 * Retrieve the Last ID that was Inserted into the Database. <br>
	 * Note: Has to be used with Sqlite (Sqlite does not recognize
	 * LAST_INSERT_ID).
	 * 
	 * @return The ID that was Inserted in the Last executed INSERT Statment
	 * 
	 * @throws SQLException
	 */
	public int getLastInsertRowId() throws SQLException {

		// ****************************************************************
		// * initialise the last inserted ID
		// ****************************************************************
		int lastid = 0;

		// ****************************************************************
		// * create prepared statment for the query
		// ****************************************************************
		try (PreparedStatement st1 = con.prepareStatement("SELECT LAST_INSERT_ROWID()")) {
			// execute the query
			ResultSet rs = st1.executeQuery();
			// retrieve the result
			while (rs.next()) {
				// ****************************************************************
				// * set the variable with the last ID
				// ****************************************************************
				lastid = rs.getInt(1);
			}
		}
		// ****************************************************************
		// * return the result
		// ****************************************************************
		return lastid;

	}

	/**
	 * query: <br>
	 * - Creates a Prepared Statment - Uses "params" List to set Query
	 * Parameters into Query Place Holders - Executes the Query - Returns the
	 * Query Result
	 * 
	 * @param query
	 *               The SQL Structured Query
	 * @param params
	 *               The List of parameters (any Value Type)
	 * 
	 * @return The Result of the executed Query
	 * 
	 * @throws SQLException
	 */
	public ResultSet query(String query) {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet res = null;
		try {
			// ****************************************************************
			// * create prepared statment of the given query
			// ****************************************************************
			st = con.prepareStatement(query);
			// ****************************************************************
			// * execute prepared statment query
			// ****************************************************************
			// check if query was successfully executed -> YES
			if (st.execute()) {
				// ****************************************************************
				// * set the result in from of resultset
				// ****************************************************************
				res = st.getResultSet();
			}
		} catch (SQLException e) {
			TrickLogManager.Persist(e);
		}

		// ****************************************************************
		// * return the result
		// ****************************************************************
		return res;
	}

	/**
	 * query: <br>
	 * - Creates a Prepared Statment - Uses "params" List to set Query
	 * Parameters into Query Place Holders - Executes the Query - Returns the
	 * Query Result
	 * 
	 * @param query
	 *               The SQL Structured Query
	 * @param params
	 *               The List of parameters (any Value Type)
	 * 
	 * @return The Result of the executed Query
	 * 
	 * @throws SQLException
	 */
	public ResultSet query(String query, List<Object> params) throws SQLException {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		ResultSet res = null;

		// ****************************************************************
		// * create prepared statment of the given query
		// ****************************************************************
		st = con.prepareStatement(query);

		// check if params exist -> YES
		if (params != null) {

			// ****************************************************************
			// * parse all parameters and add them to the prepared statment
			// ****************************************************************

			// parse params list
			for (int i = 1; i < params.size() + 1; i++) {

				// ****************************************************************
				// * add object type as parameter (type independent)
				// ****************************************************************
				st.setObject(i, params.get(i - 1));
			}
		}

		// ****************************************************************
		// * execute prepared statment query
		// ****************************************************************

		// check if query was successfully executed -> YES
		if (st.execute()) {

			// ****************************************************************
			// * set the result in from of resultset
			// ****************************************************************
			res = st.getResultSet();
		}

		// ****************************************************************
		// * return the result
		// ****************************************************************
		return res;
	}

	/**
	 * rollback: <br>
	 * Rollback a Transaction to a Savepoint and Sets the Autocommit to True
	 * 
	 * @return The autocommit status
	 */
	public boolean rollback() {

		// ****************************************************************
		// * initialise return variable
		// ****************************************************************
		boolean res = false;

		try {
			// perform a rollback to savepoint
			con.rollback(sp);
			// release the savepoint
			con.releaseSavepoint(sp);
			// set autocommit to true (end transaction)
			con.setAutoCommit(true);
			// return autocommit status
			res = con.getAutoCommit();
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
		return res;
	}

	/**
	 * generateInsertQuery: <br>
	 * This method builds a INSERT Query with a given number (paramNumber) of
	 * place holders to add parameters to a given SQL Table.
	 * 
	 * @param table
	 *                    The MySQL or Sqlite Table
	 * @param paramNumber
	 *                    The Number of Parameters (Place Holders)
	 */
	public static final String generateInsertQuery(String table, int paramNumber) {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		StringBuilder query = new StringBuilder();

		// check if at least 1 parameter -> YES
		if (paramNumber > 0) {

			// ****************************************************************
			// * build query
			// ****************************************************************

			// build first part of query

			query.append("INSERT INTO " + table + " VALUES(");

			// parse parameters and add number of ? parameters
			for (int index = 1; index <= paramNumber; index++) {
				// check if last parameter -> YES
				if (index == paramNumber) {
					// set last part of query
					query.append("?)");
				} else {
					// check if last part of query -> NO
					// set next parameter
					query.append("?,");
				}
			}
		}

		// ****************************************************************
		// * return built query
		// ****************************************************************
		return query.toString();
	}
}