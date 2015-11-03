/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.ServletContext;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.util.FileCopyUtils;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.DatabaseHandler;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.database.dao.DAOUserSqLite;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOUserSqLiteHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.ExportAnalysis;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.UserSQLite;
import lu.itrust.business.TS.usermanagement.User;

/**
 * WorkerExportAnalysis.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl. :
 * @version
 * @since Jan 30, 2014
 */
public class WorkerExportAnalysis implements Worker {

	private String id = String.valueOf(System.nanoTime());

	private Date started = null;

	private Date finished = null;

	private Exception error;

	private boolean working = false;

	private boolean canceled = false;

	private int idAnalysis = 0;

	private File sqlite = null;

	private Principal principal = null;

	private ServletContext servletContext;

	private ServiceTaskFeedback serviceTaskFeedback;

	private SessionFactory sessionFactory;

	private WorkersPoolManager poolManager;

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
	public WorkerExportAnalysis(ServiceTaskFeedback serviceTaskFeedback, SessionFactory sessionFactory, Principal principal, ServletContext servletContext,
			WorkersPoolManager poolManager, int idAnalysis) {
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.sessionFactory = sessionFactory;
		this.idAnalysis = idAnalysis;
		this.poolManager = poolManager;
		this.servletContext = servletContext;
		this.principal = principal;
	}

	@Override
	public void run() {
		Session session = null;
		try {
			synchronized (this) {
				if (poolManager != null && !poolManager.exist(getId()))
					if (!poolManager.add(this))
						return;
				if (canceled || working)
					return;
				working = true;
				started = new Timestamp(System.currentTimeMillis());
			}
			session = sessionFactory.openSession();
			DAOAnalysis daoAnalysis = new DAOAnalysisHBM(session);
			serviceTaskFeedback.send(id, new MessageHandler("info.export.load.analysis", "Load analysis to export", null, 0));
			Analysis analysis = daoAnalysis.get(idAnalysis);
			if (analysis == null)
				serviceTaskFeedback.send(id, new MessageHandler("error.analysis.not_found", "Analysis cannot be found", null, null));
			else if (!analysis.hasData())
				serviceTaskFeedback.send(id, new MessageHandler("error.analysis.export.not_allow", "Empty analysis cannot be exported", null, null));
			else {
				sqlite = new File(servletContext.getRealPath("/WEB-INF/tmp/" + id + "_" + principal.getName()));
				if (!sqlite.exists())
					sqlite.createNewFile();
				DatabaseHandler databaseHandler = new DatabaseHandler(sqlite.getCanonicalPath());
				serviceTaskFeedback.send(id, new MessageHandler("info.export.build.structure", "Build sqLite structure", null, 2));
				buildSQLiteStructure(servletContext, databaseHandler);
				ExportAnalysis exportAnalysis = new ExportAnalysis(serviceTaskFeedback, session, databaseHandler, analysis, id);
				MessageHandler messageHandler = exportAnalysis.exportAnAnalysis();
				if (messageHandler != null)
					error = messageHandler.getException();
				else
					saveSqLite(session, analysis);
			}
		} catch (HibernateException e) {
			serviceTaskFeedback.send(id, new MessageHandler("error.export.analysis", "Analysis export has failed", null, this.error = e));
			TrickLogManager.Persist(e);
		} catch (TrickException e) {
			serviceTaskFeedback.send(id, new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), this.error = e));
			TrickLogManager.Persist(e);
		} catch (Exception e) {
			serviceTaskFeedback.send(id, new MessageHandler("error.export.analysis", "Analysis export has failed", null, this.error = e));
			TrickLogManager.Persist(e);
		} finally {
			try {
				if (session != null && session.isOpen())
					session.close();
			} catch (HibernateException e) {
				TrickLogManager.Persist(e);
			}

			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						working = false;
						finished = new Timestamp(System.currentTimeMillis());
					}
				}
			}

			if (sqlite != null && sqlite.exists()) {
				if (!sqlite.delete())
					sqlite.deleteOnExit();
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.asynchronousWorkers.Worker#isMatch(java.lang.String
	 * , java.lang.Object)
	 */
	@Override
	public boolean isMatch(String express, Object... values) {
		try {
			String[] expressions = express.split("\\+");
			boolean match = values.length == expressions.length && values.length == 2;
			for (int i = 0; i < expressions.length && match; i++) {
				switch (expressions[i]) {
				case "analysis.id":
					match &= values[i].equals(idAnalysis);
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

	private void saveSqLite(Session session, Analysis analysis) {
		DAOUser daoUser = new DAOUserHBM(session);
		DAOUserSqLite daoUserSqLite = new DAOUserSqLiteHBM(session);
		Transaction transaction = null;
		try {
			User user = daoUser.get(principal.getName());
			if (user == null) {
				serviceTaskFeedback.send(id, new MessageHandler("error.export.user.not_found", "User cannot be found", null, null));
				return;
			}
			if (error != null || sqlite == null || !sqlite.exists()) {
				serviceTaskFeedback.send(id, new MessageHandler("error.export.save.file.abort", "File cannot be save", null, null));
				return;
			}
			UserSQLite userSqLite = new UserSQLite(analysis.getIdentifier(), analysis.getLabel(), analysis.getVersion(), sqlite.getName(), user,
					FileCopyUtils.copyToByteArray(sqlite), sqlite.length());
			transaction = session.beginTransaction();
			daoUserSqLite.saveOrUpdate(userSqLite);
			transaction.commit();
			MessageHandler messageHandler = new MessageHandler("success.export.save.file", "File was successfully saved", null, 100);
			messageHandler.setAsyncCallback(new AsyncCallback("downloadExportedSqLite", userSqLite.getId()));
			serviceTaskFeedback.send(id, messageHandler);
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogType.ANALYSIS, "log.analysis.export",
					String.format("Analyis: %s, version: %s, type: data", analysis.getIdentifier(), analysis.getVersion()), user.getLogin(), LogAction.EXPORT,
					analysis.getIdentifier(), analysis.getVersion());
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			try {
				if (transaction != null)
					transaction.rollback();
			} catch (Exception e1) {
				TrickLogManager.Persist(e1);
			}
		}
	}

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

		InputStream inp = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;
		String text = "";

		try {
			// build path to structure from context

			File file = new File(context.getRealPath("/WEB-INF/data/sqlitestructure.sql"));

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
					TrickLogManager.Persist(e);
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					TrickLogManager.Persist(e);
				}
			}
			// close file
			if (inp != null) {
				try {
					inp.close();
				} catch (Exception e) {
					TrickLogManager.Persist(e);
				}
			}
		}
	}

	@Override
	public boolean isWorking() {
		return working;
	}

	@Override
	public boolean isCanceled() {
		return this.canceled;
	}

	@Override
	public Exception getError() {
		return error;
	}

	@Override
	public void setId(String id) {
		this.id = id;

	}

	@Override
	public void setPoolManager(WorkersPoolManager poolManager) {
		this.poolManager = poolManager;

	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public synchronized void start() {
		run();
	}

	@Override
	public void cancel() {
		try {
			if (isWorking() && !isCanceled()) {
				synchronized (this) {
					if (isWorking() && !isCanceled()) {
						Thread.currentThread().interrupt();
						canceled = true;
					}
				}
			}
		} catch (Exception e) {
			TrickLogManager.Persist(error = e);
		} finally {
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						working = false;
						finished = new Timestamp(System.currentTimeMillis());
					}
				}
			}
		}
	}

	@Override
	public Date getStarted() {
		return started;
	}

	@Override
	public Date getFinished() {
		return finished;
	}

}
