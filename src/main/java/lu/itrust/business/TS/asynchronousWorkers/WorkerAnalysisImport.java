package lu.itrust.business.TS.asynchronousWorkers;

import java.io.File;
import java.io.IOException;

import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.data.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.data.general.Customer;
import lu.itrust.business.TS.database.DatabaseHandler;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.importation.ImportAnalysis;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.usermanagement.User;

import org.hibernate.SessionFactory;

/**
 * @author eom
 * 
 */
public class WorkerAnalysisImport implements Worker {

	private Long id = System.nanoTime();

	private Exception error = null;

	private boolean working = false;

	private boolean canceled = false;

	private WorkersPoolManager poolManager;

	private ImportAnalysis importAnalysis;

	private String fileName;

	private Customer customer;

	private User owner;

	/**
	 * 
	 */
	public WorkerAnalysisImport() {
	}

	/**
	 * @param importAnalysis
	 * @param customer2
	 * @param importFile
	 * @throws IOException
	 * 
	 */
	public WorkerAnalysisImport(ImportAnalysis importAnalysis, File importFile, Customer customer2) throws IOException {
		setCustomer(customer2);
		setFileName(importFile.getCanonicalPath());
		setImportAnalysis(importAnalysis);
	}

	public WorkerAnalysisImport(SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback, File importFile, Customer customer, User owner) throws IOException {
		setOwner(owner);
		setCustomer(customer);
		setFileName(importFile.getCanonicalPath());
		setImportAnalysis(new ImportAnalysis());

		importAnalysis.setServiceTaskFeedback(serviceTaskFeedback);
		importAnalysis.setSessionFactory(sessionFactory);
	}

	public void initialise(File importFile, Customer customer2) throws IOException {
		setCustomer(customer2);
		setFileName(importFile.getCanonicalPath());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.task.Worker#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.task.Worker#getId()
	 */
	@Override
	public Long getId() {
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
				File file = new File(fileName);
				if (file.exists())
					file.delete();
			}
			if (poolManager != null)
				poolManager.remove(getId());
		}
	}

	/**
	 * getOwner: <br>
	 * Returns the owner field value.
	 * 
	 * @return The value of the owner field
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * setOwner: <br>
	 * Sets the Field "owner" with a value.
	 * 
	 * @param owner
	 *            The Value to set the owner field
	 */
	public void setOwner(User owner) {
		this.owner = owner;
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
	 * @return the customer
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * @param customer
	 *            the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
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

	@Override
	public void run() {
		try {
			synchronized (this) {
				if (poolManager != null && !poolManager.exist(getId()))
					if (!poolManager.add(this))
						return;
				if (canceled || working)
					return;
				working = true;
			}
			DatabaseHandler DatabaseHandler = new DatabaseHandler(fileName);
			Analysis analysis = new Analysis();
			analysis.setCustomer(customer);
			analysis.setOwner(owner);
			UserAnalysisRight uar = new UserAnalysisRight(owner, AnalysisRight.ALL);

			analysis.addUserRight(uar);

			importAnalysis.setAnalysis(analysis);
			importAnalysis.setIdTask(getId());
			importAnalysis.setDatabaseHandler(DatabaseHandler);
			if (importAnalysis.ImportAnAnalysis()) {
				MessageHandler messageHandler = new MessageHandler("success.analysis.import", "Import Done!", null, 100);
				messageHandler.setAsyncCallback(new AsyncCallback("window.location.assign(\"../Analysis\")", null));
				importAnalysis.getServiceTaskFeedback().send(getId(), messageHandler);
			}

		} catch (Exception e) {
			error = e;
		} finally {
			synchronized (this) {
				working = false;
				File file = new File(fileName);
				if (file.exists())
					file.delete();
			}
			if (poolManager != null)
				poolManager.remove(getId());
		}
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
}
