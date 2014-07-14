/**
 * 
 */
package lu.itrust.business.task;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.component.Duplicator;
import lu.itrust.business.component.helper.AnalysisProfile;
import lu.itrust.business.dao.DAOAnalysis;
import lu.itrust.business.dao.DAOCustomer;
import lu.itrust.business.dao.DAONorm;
import lu.itrust.business.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.dao.hbm.DAOCustomerHBM;
import lu.itrust.business.dao.hbm.DAONormHBM;
import lu.itrust.business.exception.TrickException;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.WorkersPoolManager;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * @author eomar
 * 
 */
public class WorkerCreateAnalysisProfile implements Worker {

	private long id = System.nanoTime();

	private Exception error;

	private boolean working = false;

	private boolean canceled = false;

	private ServiceTaskFeedback serviceTaskFeedback;

	private SessionFactory sessionFactory;

	private WorkersPoolManager poolManager;

	private AnalysisProfile analysisProfile;

	private User owner = null;
	
	/**
	 * @param serviceTaskFeedback
	 * @param sessionFactory
	 * @param poolManager
	 * @param analysisProfile
	 */
	public WorkerCreateAnalysisProfile(ServiceTaskFeedback serviceTaskFeedback, SessionFactory sessionFactory, WorkersPoolManager poolManager, AnalysisProfile analysisProfile, User owner) {
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.sessionFactory = sessionFactory;
		this.poolManager = poolManager;
		this.analysisProfile = analysisProfile;
		this.owner = owner;
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
			DAONorm daoNorm = new DAONormHBM(session);
			DAOAnalysis daoAnalysis = new DAOAnalysisHBM(session);
			DAOCustomer daoCustomer = new DAOCustomerHBM(session);

			Customer customer = daoCustomer.getProfile();
			if (customer == null) {
				serviceTaskFeedback.send(id, new MessageHandler("error.not.customer.profile", "Please add a customer profile before to create a analysis profile", null));
				return;
			}
			serviceTaskFeedback.send(id, new MessageHandler("info.analysis.profile.load.norm", "Load standards", 1));
			reloadNorm(daoNorm);
			serviceTaskFeedback.send(id, new MessageHandler("info.analysis.profile.load", "Load analysis", 2));
			Analysis analysis = daoAnalysis.get(analysisProfile.getIdAnalysis());
			Analysis copy = new Duplicator().createProfile(analysis, analysisProfile, serviceTaskFeedback, id);
			copy.setCustomer(customer);
			copy.setOwner(owner);
			serviceTaskFeedback.send(id, new MessageHandler("info.analysis.profile.save", "Save analysis profile", 96));
			transaction = session.beginTransaction();
			daoAnalysis.saveOrUpdate(copy);
			transaction.commit();
			serviceTaskFeedback.send(id, new MessageHandler("success.analysis.profile", "New analysis profile was successfully created", 100));
		}
		catch (TrickException e) {
			try {
				this.error = e;
				serviceTaskFeedback.send(id, new MessageHandler(e.getCode(),e.getParameters(),e.getMessage(), e));
				e.printStackTrace();
				if (transaction != null)
					transaction.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		catch (Exception e) {
			try {
				this.error = e;
				serviceTaskFeedback.send(id, new MessageHandler("error.analysis.profile", "Creating a profile analysis failed", e));
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
	
	
	private int parseId(String value){
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return -1;
		}
	}

	private void reloadNorm(DAONorm daoNorm) throws Exception {

		if(analysisProfile.getNorms()==null)
			return;
		
		for (int i = 0; i < analysisProfile.getNorms().size();) {
			int id = parseId(analysisProfile.getNorms().get(i).getLabel());
			Norm norm = daoNorm.get(id);
			if (norm == null)
				analysisProfile.getNorms().remove(i);
			else {
				analysisProfile.getNorms().set(i, norm);
				i++;
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
