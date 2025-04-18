/**
 * 
 */
package lu.itrust.business.ts.asynchronousWorkers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.util.FileCopyUtils;

import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.DatabaseHandler;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOUser;
import lu.itrust.business.ts.database.dao.DAOUserSqLite;
import lu.itrust.business.ts.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOUserSqLiteHBM;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.exportation.sqlite.ExportAnalysis;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.messagehandler.TaskName;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.ExportFileName;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.document.impl.UserSQLite;
import lu.itrust.business.ts.model.general.helper.Utils;
import lu.itrust.business.ts.usermanagement.User;

/**
 * WorkerExportAnalysis is a class that extends the WorkerImpl class and represents a worker task for exporting analysis.
 * It performs the necessary steps to export the analysis, including loading the analysis, building the SQLite structure, and exporting the analysis.
 * If any exceptions occur during the export process, error messages are sent to the service task feedback.
 * After the export is completed or an error occurs, the worker task is marked as finished.
 * @author itrust consulting s.à.rl.
 * @since Jan 30, 2014
 */
public class WorkerExportAnalysis extends WorkerImpl {

	private int idAnalysis;

	private String username;

	/**
	 * WorkerExportAnalysis: desc
	 * 
	 * @param serviceTaskFeedback
	 * @param sessionFactory
	 * @param principal
	 * @param servletContext
	 * @param poolManager
	 * @param idAnalysis
	 */
	public WorkerExportAnalysis(String username, int idAnalysis) {
		setIdAnalysis(idAnalysis);
		setUsername(username);
	}

	/**
	 * Executes the worker task to export analysis.
	 * This method is called when the worker thread is started.
	 * It performs the necessary steps to export the analysis, including loading the analysis,
	 * building the SQLite structure, and exporting the analysis.
	 * If any exceptions occur during the export process, error messages are sent to the service task feedback.
	 * After the export is completed or an error occurs, the worker task is marked as finished.
	 */
	@Override
	public void run() {
		Session session = null;
		final File sqlite = InstanceManager.getServiceStorage().createTmpFile();
		try {
			synchronized (this) {
				if (getWorkersPoolManager() != null && !getWorkersPoolManager().exist(getId()))
					if (!getWorkersPoolManager().add(this))
						return;
				if (isCanceled() || isWorking())
					return;
				setWorking(true);
				setStarted(new Timestamp(System.currentTimeMillis()));
				setCurrent(Thread.currentThread());
			}
			session = getSessionFactory().openSession();
			final DAOAnalysis daoAnalysis = new DAOAnalysisHBM(session);
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.export.load.analysis", "Load analysis to export", 0));
			final Analysis analysis = daoAnalysis.get(getIdAnalysis());
			if (analysis == null)
				getServiceTaskFeedback().send(getId(),
						new MessageHandler("error.analysis.not_found", "Analysis cannot be found", null));
			else if (!(analysis.hasData() || analysis.isProfile()))
				getServiceTaskFeedback().send(getId(), new MessageHandler("error.analysis.export.not_allow",
						"Empty analysis cannot be exported", null));
			else {
				final DatabaseHandler databaseHandler = new DatabaseHandler(sqlite.getCanonicalPath());
				getServiceTaskFeedback().send(getId(),
						new MessageHandler("info.export.build.structure", "Build sqLite structure", 2));
				buildSQLiteStructure(databaseHandler);
				final ExportAnalysis exportAnalysis = new ExportAnalysis(getServiceTaskFeedback(), session,
						databaseHandler, analysis, getId());
				final MessageHandler messageHandler = exportAnalysis.exportAnAnalysis();
				if (messageHandler != null)
					setError(messageHandler.getException());
				else
					saveSqLite(session, analysis, sqlite);
			}
		} catch (HibernateException e) {
			setError(e);
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("error.export.analysis", "Analysis export has failed", e));
		} catch (TrickException e) {
			setError(e);
			getServiceTaskFeedback().send(getId(),
					new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
		} catch (Exception e) {
			setError(e);
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("error.export.analysis", "Analysis export has failed", e));
		} finally {
			try {
				if (session != null && session.isOpen())
					session.close();
			} catch (HibernateException e) {
				TrickLogManager.persist(e);
			}

			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
					}
				}
			}

			InstanceManager.getServiceStorage().delete(sqlite.getName());

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.asynchronousWorkers.Worker#isMatch(java.lang.String ,
	 * java.lang.Object)
	 */
	@Override
	public boolean isMatch(String express, Object... values) {
		try {
			String[] expressions = express.split("\\+");
			boolean match = values.length == expressions.length && values.length == 2;
			for (int i = 0; i < expressions.length && match; i++) {
				switch (expressions[i]) {
					case "analysis.id":
						match &= values[i].equals(getIdAnalysis());
						break;
					case "class":
						match &= values[i].equals(getClass());
						break;
					default:
						match = false;
						break;
				}
			}
			return match;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Saves the SQLite file for the given analysis.
	 *
	 * @param session The session object for database operations.
	 * @param analysis The analysis object to save the SQLite file for.
	 * @param sqlite The SQLite file to be saved.
	 */
	private void saveSqLite(Session session, Analysis analysis, File sqlite) {
		final DAOUser daoUser = new DAOUserHBM(session);
		final DAOUserSqLite daoUserSqLite = new DAOUserSqLiteHBM(session);
		Transaction transaction = null;
		try {
			final User user = daoUser.get(username);
			if (user == null) {
				getServiceTaskFeedback().send(getId(),
						new MessageHandler("error.export.user.not_found", "User cannot be found", null));
				return;
			}

			if (!sqlite.exists()) {
				getServiceTaskFeedback().send(getId(),
						new MessageHandler("error.export.save.file.abort", "File cannot be save", null));
				return;
			}

			final String filename = String.format(Constant.ITR_FILE_NAMING_WIHT_CTRL,
					Utils.cleanUpFileName(analysis.findSetting(ExportFileName.DATABASE)),
					Utils.cleanUpFileName(analysis.getCustomer().getOrganisation()),
					Utils.cleanUpFileName(analysis.getLabel()), "DB", analysis.getVersion(),
					"sqlite", System.nanoTime());

			UserSQLite userSqLite = new UserSQLite(user, analysis.getIdentifier(), analysis.getLabel(),
					analysis.getVersion(), filename,
					FileCopyUtils.copyToByteArray(sqlite), sqlite.length());
			transaction = session.beginTransaction();
			daoUserSqLite.saveOrUpdate(userSqLite);
			transaction.commit();
			MessageHandler messageHandler = new MessageHandler("success.export.save.file",
					"File was successfully saved", 100);
			messageHandler.setAsyncCallbacks(new AsyncCallback("download", "Sqlite", userSqLite.getId()));
			getServiceTaskFeedback().send(getId(), messageHandler);
			/**
			 * Log
			 */
			TrickLogManager.persist(LogType.ANALYSIS, "log.analysis.export",
					String.format("Analyis: %s, version: %s, type: data", analysis.getIdentifier(),
							analysis.getVersion()),
					user.getLogin(), LogAction.EXPORT, analysis.getIdentifier(), analysis.getVersion());
		} catch (Exception e) {
			TrickLogManager.persist(e);
			try {
				if (transaction != null)
					transaction.rollback();
			} catch (Exception e1) {
				TrickLogManager.persist(e1);
			}
		}
	}

	/**
	 * buildSQLiteStructure: <br>
	 * Reads the sql file which creates the structure of TL inside the sqlite base.
	 * 
	 * @param context context of the server
	 * @param sqlite  sqlite base object
	 * @throws SQLException
	 * @throws IOException
	 */
	private void buildSQLiteStructure(DatabaseHandler sqlite) throws IOException, SQLException {

		// ****************************************************************
		// * Initialise variables
		// ****************************************************************

		InputStream inp = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;
		String text = "";

		try {
			// build path to structure from context

			final File file = InstanceManager.getServiceStorage().loadAsFile("sqlitestructure.sql");

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
					// check if line is a SQL command (not empty and not
					// starting
					// with "-")
					if (!text.isEmpty() && !text.startsWith("-")) {

						// execute SQL query
						sqlite.query(text, null);
					}
				}

			}
		} finally {
			// close stream
			if (isr != null) {
				try {
					isr.close();
				} catch (Exception e) {
					TrickLogManager.persist(e);
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					TrickLogManager.persist(e);
				}
			}
			// close file
			if (inp != null) {
				try {
					inp.close();
				} catch (Exception e) {
					TrickLogManager.persist(e);
				}
			}
		}
	}

	@Override
	public synchronized void start() {
		run();
	}

	/**
	 * Cancels the current task being executed by the worker.
	 * If the worker is currently working and not already canceled, it interrupts the current thread or the thread associated with the current task.
	 * After cancellation, the worker's status is set to canceled and the finished timestamp is updated.
	 */
	@Override
	public void cancel() {
		try {
			if (isWorking() && !isCanceled()) {
				synchronized (this) {
					if (isWorking() && !isCanceled()) {
						if (getCurrent() == null)
							Thread.currentThread().interrupt();
						else
							getCurrent().interrupt();
						setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
			setError(e);
		} finally {
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
					}
				}
			}
		}
	}

	/**
	 * Represents the name of a task.
	 */
	@Override
	public TaskName getName() {
		return TaskName.EXPORT_ANALYSIS;
	}

	/**
	 * Returns the username associated with this worker.
	 *
	 * @return the username as a String
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username for the worker.
	 *
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Returns the ID of the analysis.
	 *
	 * @return the ID of the analysis
	 */
	private int getIdAnalysis() {
		return idAnalysis;
	}

	/**
	 * Sets the ID of the analysis.
	 *
	 * @param idAnalysis the ID of the analysis
	 */
	private void setIdAnalysis(int idAnalysis) {
		this.idAnalysis = idAnalysis;
	}
}
