/**
 * 
 */
package lu.itrust.business.task;

import java.io.File;
import java.io.IOException;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.dbhandler.DatabaseHandler;
import lu.itrust.business.TS.importation.ImportAnalysis;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.WorkersPoolManager;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

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
	public WorkerAnalysisImport(ImportAnalysis importAnalysis, File importFile,
			Customer customer2) throws IOException {
		setCustomer(customer2);
		setFileName(importFile.getCanonicalPath());
		setImportAnalysis(importAnalysis);
	}

	public WorkerAnalysisImport(SessionFactory sessionFactory,
			ServiceTaskFeedback serviceTaskFeedback, File importFile,
			Customer customer) throws IOException {
		setCustomer(customer);
		setFileName(importFile.getCanonicalPath());
		setImportAnalysis(new ImportAnalysis());
		importAnalysis.setServiceTaskFeedback(serviceTaskFeedback);
		importAnalysis.setSessionFactory(sessionFactory);
	}

	public void initialise(File importFile, Customer customer2)
			throws IOException {
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
	@Transactional
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
		synchronized (this) {
			try {
				if (working) {
					Thread.currentThread().interrupt();
					canceled = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				error = e;
			} finally {
				working = false;
				File file = new File(fileName);
				if (file.exists())
					file.delete();
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
					if (poolManager != null)
						poolManager.remove(getId());
				}
			}
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
			importAnalysis.setAnalysis(analysis);
			importAnalysis.setIdTask(getId());
			importAnalysis.setDatabaseHandler(DatabaseHandler);
			importAnalysis.ImportAnAnalysis();
		} catch (Exception e) {
			e.printStackTrace();
			error = e;
		} finally {
			synchronized (this) {
				working = false;
			}
			File file = new File(fileName);
			if (file.exists())
				file.delete();
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
				if (poolManager != null)
					poolManager.remove(getId());
			}
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
