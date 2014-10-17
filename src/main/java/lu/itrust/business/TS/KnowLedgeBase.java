package lu.itrust.business.TS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.ServletContext;

import lu.itrust.business.TS.dbhandler.DatabaseHandler;
import lu.itrust.business.TS.export.ExportAnalysis;
import lu.itrust.business.TS.importation.ImportAnalysis;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.exception.TrickException;

/**
 * KnowLedgeBase: <br>
 * This class represents the knowledge base of TRICK Service that contains all
 * analyses of the TRICK Light tool, created by itrust consulting.
 * 
 * This class is used to import and export analysis over a web interface from or
 * into a SQLite file.
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
public class KnowLedgeBase {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	private ExportAnalysis exportAnalysis;

	private ImportAnalysis importAnalysis;

	/** The List of all Analyses */
	private Vector<Analysis> analyses = new Vector<Analysis>();

	/** The List of all Languages */
	private Vector<Language> languages = new Vector<Language>();

	/** The List of all Clients */
	private Vector<Customer> clients = new Vector<Customer>();

	/***********************************************************************************************
	 * Constructor
	 **********************************************************************************************/

	protected KnowLedgeBase() {
	}

	/***********************************************************************************************
	 * Methods
	 **********************************************************************************************/

	public KnowLedgeBase(ExportAnalysis exportAnalysis) {
		this.exportAnalysis = exportAnalysis;
	}

	public KnowLedgeBase(ImportAnalysis importAnalysis) {
		this.importAnalysis = importAnalysis;
	}

	/**
	 * <b>exportToSQLite:</b> <br>
	 * Export a specific analysis to a TRICK Light understandable format (SQLite
	 * file)
	 * 
	 * @param id
	 *            The analysis id to export
	 * @param version
	 *            The version of the analysis to export
	 * @param creationDate
	 *            The creation date when the version of the analysis was created
	 * 
	 * @return The created SQLite file that can be used to return to the user
	 * 
	 * @throws Exception
	 */

	/**
	 * buildSQLiteStructure: <br>
	 * Reads the sql file which creates the structure of TL inside the sqlite
	 * base.
	 * 
	 * @param context
	 *            context of the server
	 * @param sqlite
	 *            sqlite base object
	 * @throws SQLException
	 * @throws IOException
	 */
	private void buildSQLiteStructure(ServletContext context, DatabaseHandler sqlite) throws IOException, SQLException {

		// ****************************************************************
		// * Initialise variables
		// ****************************************************************
		String filename;
		InputStream inp;
		InputStreamReader isr;
		BufferedReader reader;
		String text = "";

		// build path to structure from context
		filename = context.getRealPath("/WEB-INF/data/sqlitestructure.sql");

		File file = new File(filename);

		// retrieve file from context
		// inp = context.getResourceAsStream(filename);

		// check if file is not null
		if (file.exists()) {

			// read line by line from file

			inp = new FileInputStream(file);

			isr = new InputStreamReader(inp);
			reader = new BufferedReader(isr);
			text = "";

			// parse each line
			while ((text = reader.readLine()) != null) {

				// remove white spaces
				text = text.trim();

				System.out.println(text);

				// check if line is a SQL command (not empty and not starting
				// with "-")
				if (!text.isEmpty() && !text.startsWith("-")) {

					// execute SQL query
					sqlite.query(text, null);
				}
			}

			// close stream
			isr.close();
			reader.close();

			// close file
			inp.close();

		}
	}

	/**
	 * <b>importSQLite:</b> <br>
	 * Convert sqlite file to mysql database Supports TRICK Light version 39+
	 * only
	 * 
	 * This method opens the received SQLite file of the user and imports the
	 * analysis inside the file into the MySQL database. While respecting
	 * versioning of TRICK Light analyses, the method adds even non existing
	 * versions of a analysis, if it occurs in the SQLite file
	 * 
	 * @param filename
	 *            The Sqlite filename and path to file
	 * @param file
	 *            The InputStream of the file (the data)
	 * @param customerID
	 *            The Customer Identifier to add to the database entry
	 * 
	 * @return Returns true if no error occurred; false if error occurred
	 * 
	 * @throws Exception
	 */
	public MessageHandler importSQLite(String originalFilename, InputStream inputStream, Customer customer) {

		// ****************************************************************
		// * Initialise variables
		// ****************************************************************
		OutputStream out = null;
		int read = 0;
		byte[] bytes = new byte[1024];
		Analysis analysis = null;
		DatabaseHandler sqlite = null;

		// ****************************************************************
		// * create a file reference to the received filename
		// ****************************************************************
		File sqlitefile = new File(originalFilename);

		try {

			// ****************************************************************
			// * create sqlite handlers
			// ****************************************************************

			// sqlite database handler
			sqlite = new DatabaseHandler(sqlitefile.getCanonicalPath());

			// ****************************************************************
			// * import analysis from file - BEGIN
			// ****************************************************************

			// write file contents
			out = new FileOutputStream(sqlitefile);
			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			// close the file and the outputstream
			inputStream.close();
			out.flush();
			out.close();

			// ****************************************************************
			// * create analysis object
			// ****************************************************************
			analysis = new Analysis();

			// ****************************************************************
			// * add customer of the analysis
			// ****************************************************************

			// check if customer is null -> send exception
			if (customer == null) {
				// customer does not exist
				return new MessageHandler(new TrickException("error.import_analysis.customer.empty", "Customer does not exist!"));
			} else {

				// ****************************************************************
				// * execute import of this analysis
				// ****************************************************************

				// set customer
				analysis.setCustomer(customer);

				// initialise import object

				importAnalysis.setDatabaseHandler(sqlite);

				importAnalysis.setAnalysis(analysis);

				importAnalysis.ImportAnAnalysis();
				// perform import
				return null;
			}

		} catch (Exception e) {
			// error text
			System.out.println("KnowledgeBase -> Import Error: " + e.getMessage());
			// e.printStackTrace();
			// set return value to false and return the value
			return new MessageHandler(e);
		} finally {
			try {
				// close Database connections
				if (sqlite != null) 
					sqlite.close();
			} catch (SQLException e) {
				// error text
				System.out.println("Could not close Database Connections!");
			}
		}
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getAnalysisList: <br>
	 * Returns the "analyses" field
	 * 
	 * @return The List of Analyses
	 */
	public Vector<Analysis> getAnalysisList() {
		return analyses;
	}

	/**
	 * addAnAnalysis: <br>
	 * Add a single Analysis to the list of analyses ("analyses" field)
	 * 
	 * @param analysis
	 *            The Analysis object to add
	 * @throws TrickException 
	 */
	public void addAnAnalysis(Analysis analysis) throws TrickException {
		if (this.analyses.contains(analysis))
			throw new TrickException("error.import.analysis.exist","Analysis already exists!");
		analyses.add(analysis);
	}

	/**
	 * getLanguageList: <br>
	 * Returns a list of all languages ("language" field)
	 * 
	 * @return The List of Languages
	 */
	public Vector<Language> getLanguageList() {
		return languages;
	}

	/**
	 * addALanguage: <br>
	 * Adds a language object into the list of languages ("language" field)
	 * 
	 * @param language
	 *            The language object to add
	 * @throws TrickException 
	 */
	public void addALanguage(Language language) throws TrickException {
		if (this.languages.contains(language))
			throw new TrickException("error.import.analysis.language.exist","Language already exists!");
		this.languages.add(language);
	}

	/**
	 * getClientList: <br>
	 * Returns a list of clients ("clients" field)
	 * 
	 * @return The List of Clients
	 */
	public Vector<Customer> getClientList() {
		return clients;
	}

	/**
	 * addAClient: <br>
	 * Adds a Client object to the list of clients ("clients" field)
	 * 
	 * @param client
	 *            The client object to add
	 * @throws TrickException 
	 */
	public void addAClient(Customer client) throws TrickException {
		if (this.clients.contains(client))
			throw new TrickException("error.import.analysis.customer.exist","Customer already exists!");
		this.clients.add(client);
	}

	public File exportToSQLite(Analysis analysis, String fileName, ServletContext servletContext) {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		File sqlitefile = null;
		DatabaseHandler sqlite = null;
		try {
			// ****************************************************************
			// * check if analysis has data to export -> YES
			// ****************************************************************
			if (analysis.hasData()) {

				// ****************************************************************
				// * create file object
				// ****************************************************************
				sqlitefile = new File(servletContext.getRealPath("/WEB-INF/tmp/" + fileName));

				// check if file does not exists -> YES
				if (!sqlitefile.exists()) {

					// create new file
					sqlitefile.createNewFile();
				} else {
					// check if file does not exists -> NO
					// delete file
					sqlitefile.delete();
				}

				// ****************************************************************
				// * create mysql and sqlite handlers - BEGIN
				// ****************************************************************

				// mysql database handler
				sqlite = new DatabaseHandler(sqlitefile.getCanonicalPath());

				if (sqlite != null) {

					// ****************************************************************
					// * Build Sqlite database structure - BEGIN
					// ****************************************************************

					buildSQLiteStructure(servletContext, sqlite);

					// ****************************************************************
					// * export analysis
					// ****************************************************************

					// create object
					exportAnalysis.setSqlite(sqlite);

					exportAnalysis.setAnalysis(analysis);

					// perform Export
					exportAnalysis.exportAnAnalysis();

				}
			}
		} catch (Exception e) {

			// Error text
			System.out.println("KnowledgeBase -> Export Analysis Error: " + e.getMessage());
			e.printStackTrace();
			// set sqlitefile to unusable
			sqlitefile = null;
		} finally {
			try {

				// close sqlite database handler
				if (sqlite != null) {
					sqlite.close();
				}
			} catch (SQLException e) {

				// error text
				System.out.println("Could not close Database connections!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ****************************************************************
		// * return sqlite file
		// ****************************************************************
		return sqlitefile;
	}
}