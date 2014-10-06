/**
 * 
 */
package lu.itrust.business.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.sql.SQLException;

import javax.servlet.ServletContext;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.dbhandler.DatabaseHandler;
import lu.itrust.business.TS.export.ExportAnalysis;
import lu.itrust.business.TS.export.UserSQLite;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.component.helper.AsyncCallback;
import lu.itrust.business.dao.DAOAnalysis;
import lu.itrust.business.dao.DAOUser;
import lu.itrust.business.dao.DAOUserSqLite;
import lu.itrust.business.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.dao.hbm.DAOUserHBM;
import lu.itrust.business.dao.hbm.DAOUserSqLiteHBM;
import lu.itrust.business.exception.TrickException;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.WorkersPoolManager;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.util.FileCopyUtils;

/**
 * WorkerExportAnalysis.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl. :
 * @version
 * @since Jan 30, 2014
 */
public class WorkerExportAnalysis implements Worker {

	private long id = System.nanoTime();

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
			}
			session = sessionFactory.openSession();
			DAOAnalysis daoAnalysis = new DAOAnalysisHBM(session);
			serviceTaskFeedback.send(id, new MessageHandler("info.export.load.analysis", "Load analysis to export", 0));
			Analysis analysis = daoAnalysis.get(idAnalysis);
			if (analysis == null)
				serviceTaskFeedback.send(id, new MessageHandler("error.analysis.not_found", "Analysis cannot be found", null));
			else if (!analysis.hasData())
				serviceTaskFeedback.send(id, new MessageHandler("error.analysis.export.not_allow", "Empty analysis cannot be exported", null));
			else {
				sqlite = new File(servletContext.getRealPath("/WEB-INF/tmp/" + id + "_" + principal.getName()));
				if (!sqlite.exists())
					sqlite.createNewFile();
				DatabaseHandler databaseHandler = new DatabaseHandler(sqlite.getCanonicalPath());
				serviceTaskFeedback.send(id, new MessageHandler("info.export.build.structure", "Build sqLite structure", 2));
				buildSQLiteStructure(servletContext, databaseHandler);
				ExportAnalysis exportAnalysis = new ExportAnalysis(serviceTaskFeedback, session, databaseHandler, analysis, id);
				MessageHandler messageHandler = exportAnalysis.exportAnAnalysis();
				if (messageHandler != null)
					error = messageHandler.getException();
				else
					saveSqLite(session, analysis.getIdentifier());
			}
		} catch (HibernateException e) {
			this.error = e;
			serviceTaskFeedback.send(id, new MessageHandler("error.export.analysis", "Analysis export has failed", e));
			e.printStackTrace();
		}catch (TrickException e) {
			this.error = e;
			serviceTaskFeedback.send(id, new MessageHandler(e.getCode(), e.getParameters(),e.getMessage(), e));
			e.printStackTrace();
		} 
		catch (Exception e) {
			this.error = e;
			serviceTaskFeedback.send(id, new MessageHandler("error.export.analysis", "Analysis export has failed", e));
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (HibernateException e) {
				e.printStackTrace();
			}
			if (sqlite != null && sqlite.exists())
				sqlite.delete();
			synchronized (this) {
				working = false;
			}
			if (poolManager != null)
				poolManager.remove(getId());
		}

	}

	private void saveSqLite(Session session, String analysisIdentifier) {
		DAOUser daoUser = new DAOUserHBM(session);
		DAOUserSqLite daoUserSqLite = new DAOUserSqLiteHBM(session);
		Transaction transaction = null;
		try {
			User user = daoUser.get(principal.getName());
			if (user == null) {
				serviceTaskFeedback.send(id, new MessageHandler("error.export.user.not_found", "User cannot be found", null));
				return;
			}
			if (error != null || sqlite == null || !sqlite.exists()) {
				serviceTaskFeedback.send(id, new MessageHandler("error.export.save.file.abort", "File cannot be save", null));
				return;
			}
			UserSQLite userSqLite = new UserSQLite(sqlite.getName(), user, FileCopyUtils.copyToByteArray(sqlite));
			userSqLite.setSize(sqlite.length());
			userSqLite.setAnalysisIdentifier(analysisIdentifier);
			transaction = session.beginTransaction();
			daoUserSqLite.saveOrUpdate(userSqLite);
			transaction.commit();
			MessageHandler messageHandler = new MessageHandler("success.export.save.file", "File was successfully saved", 100);
			messageHandler.setAsyncCallback(new AsyncCallback("downloadExportedSqLite(\"" + userSqLite.getId() + "\")", null));
			serviceTaskFeedback.send(id, messageHandler);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (transaction != null)
					transaction.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
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
	public void setId(Long id) {
		this.id = id;

	}

	@Override
	public void setPoolManager(WorkersPoolManager poolManager) {
		this.poolManager = poolManager;

	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public synchronized void start() {
		run();
	}

	@Override
	public void cancel() {
		try {
			synchronized (this) {
				if (working) {
					Thread.currentThread().interrupt();
					canceled = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = e;
		} finally {
			synchronized (this) {
				working = false;
			}
			if (poolManager != null)
				poolManager.remove(getId());
		}
	}

}
