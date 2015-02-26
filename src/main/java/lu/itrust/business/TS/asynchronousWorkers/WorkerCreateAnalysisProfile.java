/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.util.List;

import lu.itrust.business.TS.component.Duplicator;
import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.general.Customer;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOCustomer;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOCustomerHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.usermanagement.User;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * @author eomar
 * 
 */
public class WorkerCreateAnalysisProfile implements Worker {

	private String id = String.valueOf(System.nanoTime());

	private Exception error;

	private boolean working = false;

	private boolean canceled = false;

	private ServiceTaskFeedback serviceTaskFeedback;

	private SessionFactory sessionFactory;

	private WorkersPoolManager poolManager;

	private Integer analysisId;

	private String name;

	private List<Integer> standards;

	private User owner = null;

	/**
	 * @param serviceTaskFeedback
	 * @param sessionFactory
	 * @param poolManager
	 * @param analysisProfile
	 */
	public WorkerCreateAnalysisProfile(ServiceTaskFeedback serviceTaskFeedback, SessionFactory sessionFactory, WorkersPoolManager poolManager, Integer analysisId, String name,
			List<Integer> standards, User owner) {
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.sessionFactory = sessionFactory;
		this.poolManager = poolManager;
		this.owner = owner;
		this.analysisId = analysisId;
		this.name = name;
		this.standards = standards;
	}

	@Override
	public void run() {
		Session session = null;
		Transaction transaction = null;
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
			DAOCustomer daoCustomer = new DAOCustomerHBM(session);

			Customer customer = daoCustomer.getProfile();
			if (customer == null) {
				serviceTaskFeedback.send(id, new MessageHandler("error.not.customer.profile", "Please add a profile customer before creating an analysis profile", null, null));
				return;
			}
			serviceTaskFeedback.send(id, new MessageHandler("info.analysis.profile.load", "Load analysis", null, 1));
			Analysis analysis = daoAnalysis.get(analysisId);
			Analysis copy = new Duplicator(session).createProfile(analysis, name, standards, serviceTaskFeedback, id);
			copy.setCustomer(customer);
			copy.setOwner(owner);
			serviceTaskFeedback.send(id, new MessageHandler("info.analysis.profile.save", "Save analysis profile", null, 96));
			transaction = session.beginTransaction();
			daoAnalysis.saveOrUpdate(copy);
			transaction.commit();
			serviceTaskFeedback.send(id, new MessageHandler("success.analysis.profile", "New analysis profile was successfully created", null, 100));
		} catch (TrickException e) {
			try {
				this.error = e;
				serviceTaskFeedback.send(id, new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
				e.printStackTrace();
				if (transaction != null)
					transaction.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			try {
				this.error = e;
				serviceTaskFeedback.send(id, new MessageHandler("error.analysis.profile", "Creating a profile analysis failed", null, e));
				e.printStackTrace();
				if (transaction != null)
					transaction.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		} finally {
			try {
				if (session != null)
					session.close();
			} catch (HibernateException e) {
				e.printStackTrace();
			}
			synchronized (this) {
				working = false;
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
