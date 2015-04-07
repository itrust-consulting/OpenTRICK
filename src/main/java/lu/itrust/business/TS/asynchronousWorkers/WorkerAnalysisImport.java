package lu.itrust.business.TS.asynchronousWorkers;

import java.io.File;
import java.io.IOException;

import lu.itrust.business.TS.data.general.Customer;
import lu.itrust.business.TS.database.DatabaseHandler;
import lu.itrust.business.TS.database.dao.hbm.DAOCustomerHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.importation.ImportAnalysis;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.usermanagement.User;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @author eom
 * 
 */
public class WorkerAnalysisImport implements Worker {

	private String id = String.valueOf(System.nanoTime());

	private Exception error = null;

	private boolean working = false;

	private boolean canceled = false;

	private boolean canDeleteFile = true;

	private WorkersPoolManager poolManager;

	private ImportAnalysis importAnalysis;

	private SessionFactory sessionFactory;

	private int customerId;

	private String fileName;

	private String username;

	private AsyncCallback asyncCallback;

	private MessageHandler messageHandler;

	/**
	 * 
	 */
	public WorkerAnalysisImport() {
	}

	/**
	 * @param importAnalysis
	 * @param fileName
	 */
	public WorkerAnalysisImport(ImportAnalysis importAnalysis, String fileName) {
		this.importAnalysis = importAnalysis;
		this.fileName = fileName;
	}

	/**
	 * @param importAnalysis
	 * @param customerId
	 * @param importFile
	 * @throws IOException
	 * 
	 */
	public WorkerAnalysisImport(ImportAnalysis importAnalysis, File importFile, int customerId) throws IOException {
		setCustomerId(customerId);
		setFileName(importFile.getCanonicalPath());
		setImportAnalysis(importAnalysis);
	}

	public WorkerAnalysisImport(SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback, String filename, int customerId, String userName) throws IOException {
		setUsername(userName);
		setCustomerId(customerId);
		setFileName(filename);
		setImportAnalysis(new ImportAnalysis());
		setSessionFactory(sessionFactory);
		importAnalysis.setServiceTaskFeedback(serviceTaskFeedback);
	}

	public WorkerAnalysisImport(SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback, File importFile, int customerId, String userName) throws IOException {
		setUsername(userName);
		setCustomerId(customerId);
		setFileName(importFile.getCanonicalPath());
		setImportAnalysis(new ImportAnalysis());
		setSessionFactory(sessionFactory);
		importAnalysis.setServiceTaskFeedback(serviceTaskFeedback);
	}

	public void initialise(File importFile, int customerId) throws IOException {
		setCustomerId(customerId);
		setFileName(importFile.getCanonicalPath());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.task.Worker#setId(java.lang.Long)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.task.Worker#getId()
	 */
	@Override
	public String getId() {
		return id;
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
				if (canDeleteFile) {
					File file = new File(fileName);
					if (file.exists())
						file.delete();
				}
			}
			if (poolManager != null)
				poolManager.remove(getId());
		}
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	protected void OnSuccess() {
		if (getMessageHandler() == null)
			setMessageHandler(new MessageHandler("success.analysis.import", "Import Done!", null, 100));
		if (getAsyncCallback() == null)
			setAsyncCallback(new AsyncCallback("window.location.assign(context+\"/Analysis\")", null));
		getMessageHandler().setAsyncCallback(getAsyncCallback());
		importAnalysis.getServiceTaskFeedback().send(getId(), getMessageHandler());
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
				OnStarted();
			}
			DatabaseHandler DatabaseHandler = new DatabaseHandler(fileName);
			session = sessionFactory.openSession();
			User user = new DAOUserHBM(session).get(username);
			if (user == null)
				throw new TrickException("error.user.cannot.found", String.format("User (%s) cannot be found", username), username);
			Customer customer = new DAOCustomerHBM(session).get(customerId);
			if (customer == null)
				throw new TrickException("error.customer.not_exist", "Customer does not exist");
			importAnalysis.updateAnalysis(customer, user);
			importAnalysis.setIdTask(getId());
			importAnalysis.setDatabaseHandler(DatabaseHandler);
			if (importAnalysis.ImportAnAnalysis(session))
				OnSuccess();
		} catch (TrickException e) {
			try {
				e.printStackTrace();
				if (session != null && session.getTransaction().isInitiator())
					session.getTransaction().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}finally{
				importAnalysis.getServiceTaskFeedback().send(id, new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), error = e));
			}
		} catch (Exception e) {
			try {
				e.printStackTrace();
				if (session != null && session.getTransaction().isInitiator())
					session.getTransaction().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				importAnalysis.getServiceTaskFeedback().send(id, new MessageHandler("error.unknown.occurred", "An unknown error occurred", null, error = e));
			}
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				synchronized (this) {
					working = false;
					File file = new File(fileName);
					if (canDeleteFile && file.exists())
						file.delete();
				}
				if (poolManager != null)
					poolManager.remove(getId());
			}
		}
	}

	protected synchronized void OnStarted() throws Exception {
		working = true;
	}

	@Override
	public boolean isWorking() {
		return working;
	}

	@Override
	public boolean isCanceled() {

		return canceled;
	}

	@Override
	public Exception getError() {
		return error;
	}

	@Override
	public void setPoolManager(WorkersPoolManager poolManager) {
		this.poolManager = poolManager;

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

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
