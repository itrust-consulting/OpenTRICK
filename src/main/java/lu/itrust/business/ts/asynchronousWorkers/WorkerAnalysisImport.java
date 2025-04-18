package lu.itrust.business.ts.asynchronousWorkers;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;

import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.DatabaseHandler;
import lu.itrust.business.ts.database.dao.hbm.DAOCustomerHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.importation.ImportAnalysis;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.messagehandler.TaskName;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.usermanagement.User;


/**
 * This class represents a worker for analysis import in a multi-threaded environment.
 * It extends the `WorkerImpl` class and provides methods to start and cancel the worker task.
 * The worker imports analysis data from files and performs analysis on the data.
 * It also provides methods to retrieve and set the import analysis, customer ID, file names, and username.
 */
public class WorkerAnalysisImport extends WorkerImpl {

	private boolean canDeleteFile = true;

	private ImportAnalysis importAnalysis;

	private int customerId;

	private List<String> fileNames;

	private String username;

	private AsyncCallback asyncCallback;

	private MessageHandler messageHandler;

	/**
	 * Constructs a new WorkerAnalysisImport object.
	 *
	 * @param filenames  the list of filenames to be processed
	 * @param customerId the ID of the customer
	 * @param userName   the username of the user initiating the import
	 * @throws IOException if an I/O error occurs
	 */
	public WorkerAnalysisImport(List<String> filenames, int customerId, String userName) throws IOException {
		setUsername(userName);
		setCustomerId(customerId);
		setFileNames(filenames);
		setImportAnalysis(new ImportAnalysis());
	}

	public WorkerAnalysisImport(String filename, int customerId, String userName) throws IOException {
		setUsername(userName);
		setCustomerId(customerId);
		setFileNames(new LinkedList<>());
		getFileNames().add(filename);
		setImportAnalysis(new ImportAnalysis());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.task.Worker#start()
	 */
	@Override
	public void start() {
		run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.task.Worker#cancel()
	 */
	@Override
	public void cancel() {
		try {
			System.out.println("Task has been canceled");
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
			TrickLogManager.persist(e);
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
			if (canDeleteFile)
				fileNames.forEach(fileName -> getServiceStorage().delete(fileName));
		}
	}

	/**
	 * @return the importAnalysis
	 */
	public ImportAnalysis getImportAnalysis() {
		return importAnalysis;
	}

	/**
	 * @param importAnalysis the importAnalysis to set
	 */
	public void setImportAnalysis(ImportAnalysis importAnalysis) {
		this.importAnalysis = importAnalysis;
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
				case "customer.id":
					match &= values[i].equals(customerId);
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
	 * This method is called when the analysis import is successful.
	 * It sets the message handler and async callback, sends the message handler,
	 * retrieves the analysis and username, and logs the import action.
	 */
	protected void OnSuccess() {
		if (getMessageHandler() == null)
			setMessageHandler(new MessageHandler("success.analysis.import", "Import Done!", 100));
		if (getAsyncCallback() == null)
			setAsyncCallback(new AsyncCallback("updateAnalysisFilter", customerId, "ALL"));
		getMessageHandler().setAsyncCallbacks(getAsyncCallback());
		InstanceManager.getServiceTaskFeedback().send(getId(), getMessageHandler());
		Analysis analysis = importAnalysis.getAnalysis();
		String username = InstanceManager.getServiceTaskFeedback().findUsernameById(this.getId());
		/**
		 * Log
		 */
		TrickLogManager.persist(LogType.ANALYSIS, "log.analysis.import", String.format("Analyis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()), username,
				LogAction.IMPORT, analysis.getIdentifier(), analysis.getVersion());
	}

	/**
	 * Executes the worker analysis import task.
	 * This method is called when the worker thread is started.
	 * It performs the necessary operations to import and process files for analysis.
	 * 
	 * @throws TrickException if an error occurs during the import and processing of files.
	 * @throws Exception if an unknown error occurs.
	 */
	@Override
	public void run() {
		Session session = null;
		try {
			synchronized (this) {
				if (getWorkersPoolManager() != null && !getWorkersPoolManager().exist(getId()))
					if (!getWorkersPoolManager().add(this))
						return;
				if (isCanceled() || isWorking())
					return;
				OnStarted();
			}
			setStarted(new Timestamp(System.currentTimeMillis()));
			session = getSessionFactory().openSession();
			User user = new DAOUserHBM(session).get(username);
			if (user == null)
				throw new TrickException("error.user.cannot.found", String.format("User (%s) cannot be found", username), username);
			Customer customer = new DAOCustomerHBM(session).get(customerId);
			if (customer == null)
				throw new TrickException("error.customer.not_exist", "Customer does not exist");
			int index = 1;
			for (String fileName : fileNames)
				process(index++, fileName, session, user, customer);

		} catch (TrickException e) {
			try {
				TrickLogManager.persist(e);
				if (session != null && session.getTransaction().getStatus().canRollback())
					session.getTransaction().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				getServiceTaskFeedback().send(getId(), new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
				setError(e);
			}
		} catch (Exception e) {
			try {
				TrickLogManager.persist(e);
				if (session != null && session.getTransaction().getStatus().canRollback())
					session.getTransaction().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				getServiceTaskFeedback().send(getId(), new MessageHandler("error.unknown.occurred", "An unknown error occurred", e));
				setError(e);
			}
		} finally {
			try {
				if (session != null && session.isOpen())
					session.close();
			} catch (Exception e) {
				TrickLogManager.persist(e);
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
	}

	/**
	 * Processes the analysis import for a specific file.
	 *
	 * @param index the index of the file in the list of file names
	 * @param fileName the name of the file to be processed
	 * @param session the session object
	 * @param user the user object
	 * @param customer the customer object
	 * @throws ClassNotFoundException if the specified class cannot be found
	 * @throws SQLException if a database access error occurs
	 * @throws Exception if an error occurs during the process
	 */
	protected void process(int index, String fileName, Session session, User user, Customer customer) throws ClassNotFoundException, SQLException, Exception {
		try (DatabaseHandler databaseHandler = new DatabaseHandler(getServiceStorage().load(fileName).toString())) {
			importAnalysis.setProgress(0);
			importAnalysis.setMaxProgress((int) (((double) index / (double) fileNames.size()) * 97));
			importAnalysis.setGlobalProgress((int) (((double) (index - 1) / (double) fileNames.size()) * 95) + 2);
			importAnalysis.updateAnalysis(customer, user);
			importAnalysis.setIdTask(getId());
			importAnalysis.setDatabaseHandler(databaseHandler);
			if (importAnalysis.ImportAnAnalysis(session) && fileNames.size() == index)
				OnSuccess();
		} finally {
			if (canDeleteFile)
				getServiceStorage().delete(fileName);
		}
	}

	/**
	 * Sets the worker state to started and performs necessary initialization tasks.
	 * 
	 * @throws Exception if an error occurs during initialization
	 */
	protected synchronized void OnStarted() throws Exception {
		setWorking(true);
		setStarted(new Timestamp(System.currentTimeMillis()));
		setName(TaskName.IMPORT_ANALYSIS);
		setCurrent(Thread.currentThread());
	}

	/**
	 * Returns the AsyncCallback object.
	 *
	 * @return the AsyncCallback object.
	 */
	public AsyncCallback getAsyncCallback() {
		return asyncCallback;
	}

	/**
	 * Sets the asynchronous callback for this worker.
	 *
	 * @param asyncCallback the asynchronous callback to set
	 */
	public void setAsyncCallback(AsyncCallback asyncCallback) {
		this.asyncCallback = asyncCallback;
	}

	/**
	 * Returns the message handler associated with this worker.
	 *
	 * @return the message handler
	 */
	public MessageHandler getMessageHandler() {
		return messageHandler;
	}

	/**
	 * Sets the message handler for this worker.
	 *
	 * @param messageHandler the message handler to be set
	 */
	public void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	/**
	 * Returns a boolean value indicating whether the file can be deleted.
	 *
	 * @return true if the file can be deleted, false otherwise.
	 */
	public boolean isCanDeleteFile() {
		return canDeleteFile;
	}

	/**
	 * Sets the flag indicating whether the file can be deleted.
	 *
	 * @param canDeleteFile true if the file can be deleted, false otherwise
	 */
	public void setCanDeleteFile(boolean canDeleteFile) {
		this.canDeleteFile = canDeleteFile;
	}

	/**
	 * Returns the customer ID associated with this worker.
	 *
	 * @return the customer ID
	 */
	public int getCustomerId() {
		return customerId;
	}

	/**
	 * Sets the customer ID for this worker.
	 *
	 * @param customerId the ID of the customer
	 */
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	/**
	 * Returns the username.
	 *
	 * @return the username as a String.
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
	 * @return the fileNames
	 */
	public List<String> getFileNames() {
		return fileNames;
	}

	/**
	 * @param fileNames the fileNames to set
	 */
	public void setFileNames(List<String> fileNames) {
		this.fileNames = fileNames;
	}

}
