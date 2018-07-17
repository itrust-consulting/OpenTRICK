package lu.itrust.business.TS.asynchronousWorkers;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import lu.itrust.business.TS.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.DatabaseHandler;
import lu.itrust.business.TS.database.dao.hbm.DAOCustomerHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.importation.ImportAnalysis;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.messagehandler.TaskName;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eom
 * 
 */
public class WorkerAnalysisImport extends WorkerImpl {

	private boolean canDeleteFile = true;

	private ImportAnalysis importAnalysis;

	private int customerId;

	private List<String> fileNames;

	private String username;

	private AsyncCallback asyncCallback;

	private MessageHandler messageHandler;

	public WorkerAnalysisImport(WorkersPoolManager workersPoolManager, SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback, List<String> filenames,
			int customerId, String userName) throws IOException {
		super(workersPoolManager, sessionFactory);
		setUsername(userName);
		setCustomerId(customerId);
		setFileNames(filenames);
		setImportAnalysis(new ImportAnalysis());
		setSessionFactory(sessionFactory);
		importAnalysis.setServiceTaskFeedback(serviceTaskFeedback);
	}

	public WorkerAnalysisImport(WorkersPoolManager workersPoolManager, SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback, File importFile, int customerId,
			String userName) throws IOException {
		super(workersPoolManager, sessionFactory);
		setUsername(userName);
		setCustomerId(customerId);
		setFileNames(new LinkedList<>());
		getFileNames().add(importFile.getCanonicalPath());
		setImportAnalysis(new ImportAnalysis());
		setSessionFactory(sessionFactory);
		importAnalysis.setServiceTaskFeedback(serviceTaskFeedback);
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
						if(getCurrent() == null)
							Thread.currentThread().interrupt();
						else getCurrent().interrupt();
						setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
			TrickLogManager.Persist(e);
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
			if (canDeleteFile) {
				fileNames.forEach(fileName -> {
					File file = new File(fileName);
					if (file.exists()) {
						if (!file.delete()) {
							file.deleteOnExit();
						}
					}
				});

			}
		}
	}

	/**
	 * @return the importAnalysis
	 */
	public ImportAnalysis getImportAnalysis() {
		return importAnalysis;
	}

	/**
	 * @param importAnalysis
	 *            the importAnalysis to set
	 */
	public void setImportAnalysis(ImportAnalysis importAnalysis) {
		this.importAnalysis = importAnalysis;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.asynchronousWorkers.Worker#isMatch(java.lang.String ,
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

	protected void OnSuccess() {
		if (getMessageHandler() == null)
			setMessageHandler(new MessageHandler("success.analysis.import", "Import Done!", 100));
		if (getAsyncCallback() == null)
			setAsyncCallback(new AsyncCallback("updateAnalysisFilter", customerId, "ALL"));
		getMessageHandler().setAsyncCallbacks(getAsyncCallback());
		importAnalysis.getServiceTaskFeedback().send(getId(), getMessageHandler());
		Analysis analysis = importAnalysis.getAnalysis();
		String username = importAnalysis.getServiceTaskFeedback().findUsernameById(this.getId());
		/**
		 * Log
		 */
		TrickLogManager.Persist(LogType.ANALYSIS, "log.analysis.import", String.format("Analyis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()), username,
				LogAction.IMPORT, analysis.getIdentifier(), analysis.getVersion());
	}

	@Override
	public void run() {
		Session session = null;
		try {
			synchronized (this) {
				if (getPoolManager() != null && !getPoolManager().exist(getId()))
					if (!getPoolManager().add(this))
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
				TrickLogManager.Persist(e);
				if (session != null && session.getTransaction().getStatus().canRollback())
					session.getTransaction().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				importAnalysis.getServiceTaskFeedback().send(getId(), new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
				setError(e);
			}
		} catch (Exception e) {
			try {
				TrickLogManager.Persist(e);
				if (session != null && session.getTransaction().getStatus().canRollback())
					session.getTransaction().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				importAnalysis.getServiceTaskFeedback().send(getId(), new MessageHandler("error.unknown.occurred", "An unknown error occurred", e));
				setError(e);
			}
		} finally {
			try {
				if (session != null && session.isOpen())
					session.close();
			} catch (Exception e) {
				TrickLogManager.Persist(e);
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

	protected void process(int index, String fileName, Session session, User user, Customer customer) throws ClassNotFoundException, SQLException, Exception {
		DatabaseHandler databaseHandler = null;
		try {
			databaseHandler = new DatabaseHandler(fileName);
			importAnalysis.setProgress(0);
			importAnalysis.setMaxProgress((int) (((double) index / (double) fileNames.size()) * 97));
			importAnalysis.setGlobalProgress((int) (((double) (index - 1) / (double) fileNames.size()) * 95) + 2);
			importAnalysis.updateAnalysis(customer, user);
			importAnalysis.setIdTask(getId());
			importAnalysis.setDatabaseHandler(databaseHandler);
			if (importAnalysis.ImportAnAnalysis(session) && fileNames.size() == index)
				OnSuccess();
		} finally {
			if (databaseHandler != null)
				databaseHandler.close();
			if (canDeleteFile) {
				File file = new File(fileName);
				if (file.exists() && !file.delete())
					file.deleteOnExit();
			}
		}
	}

	protected synchronized void OnStarted() throws Exception {
		setWorking(true);
		setStarted(new Timestamp(System.currentTimeMillis()));
		setName(TaskName.IMPORT_ANALYSIS);
		setCurrent(Thread.currentThread());
	}

	public AsyncCallback getAsyncCallback() {
		return asyncCallback;
	}

	public void setAsyncCallback(AsyncCallback asyncCallback) {
		this.asyncCallback = asyncCallback;
	}

	public MessageHandler getMessageHandler() {
		return messageHandler;
	}

	public void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	public boolean isCanDeleteFile() {
		return canDeleteFile;
	}

	public void setCanDeleteFile(boolean canDeleteFile) {
		this.canDeleteFile = canDeleteFile;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getUsername() {
		return username;
	}

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
	 * @param fileNames
	 *            the fileNames to set
	 */
	public void setFileNames(List<String> fileNames) {
		this.fileNames = fileNames;
	}

}
