/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import lu.itrust.business.TS.component.Duplicator;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOCustomer;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOCustomerHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 * 
 */
public class WorkerCreateAnalysisProfile implements Worker {

	private String id = String.valueOf(System.nanoTime());

	private Date started = null;

	private Date finished = null;

	private Exception error;

	private boolean working = false;

	private boolean canceled = false;

	private ServiceTaskFeedback serviceTaskFeedback;

	private SessionFactory sessionFactory;

	private WorkersPoolManager poolManager;

	private Integer analysisId;

	private String name;

	private List<Integer> standards;

	private String username = null;

	/**
	 * @param serviceTaskFeedback
	 * @param sessionFactory
	 * @param poolManager
	 * @param analysisProfile
	 */
	public WorkerCreateAnalysisProfile(ServiceTaskFeedback serviceTaskFeedback, SessionFactory sessionFactory, WorkersPoolManager poolManager, Integer analysisId, String name,
			List<Integer> standards, String username) {
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.sessionFactory = sessionFactory;
		this.poolManager = poolManager;
		this.username = username;
		this.analysisId = analysisId;
		this.name = name;
		this.standards = standards;
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
					match &= values[i].equals(analysisId);
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
				started = new Timestamp(System.currentTimeMillis());
			}
			session = sessionFactory.openSession();
			DAOAnalysis daoAnalysis = new DAOAnalysisHBM(session);
			DAOCustomer daoCustomer = new DAOCustomerHBM(session);
			User owner = new DAOUserHBM(session).get(username);
			Customer customer = daoCustomer.getProfile();
			if (customer == null) {
				serviceTaskFeedback.send(id, new MessageHandler("error.not.customer.profile", "Please add a profile customer before creating an analysis profile", null));
				return;
			}
			serviceTaskFeedback.send(id, new MessageHandler("info.analysis.profile.load", "Load analysis", 1));
			Analysis analysis = daoAnalysis.get(analysisId);
			Analysis copy = new Duplicator(session).createProfile(analysis, name, standards, serviceTaskFeedback, id);
			copy.setCustomer(customer);
			copy.setOwner(owner);
			serviceTaskFeedback.send(id, new MessageHandler("info.analysis.profile.save", "Save analysis profile", 96));
			transaction = session.beginTransaction();
			daoAnalysis.saveOrUpdate(copy);
			transaction.commit();
			serviceTaskFeedback.send(id, new MessageHandler("success.analysis.profile", "New analysis profile was successfully created", 100));
			/**
			 * Log
			 */
			TrickLogManager.Persist(
					LogType.ANALYSIS,
					"log.analysis.profile.create",
					String.format("Analyis: %s, version: %s, profile: %s, name: %s, version: %s", analysis.getIdentifier(), analysis.getVersion(), copy.getIdentifier(),
							copy.getLabel(), copy.getVersion()), username, LogAction.CREATE, analysis.getIdentifier(), analysis.getVersion(), copy.getIdentifier(),
					copy.getLabel(), copy.getVersion());
		} catch (TrickException e) {
			try {
				serviceTaskFeedback.send(id, new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), this.error = e));
				TrickLogManager.Persist(e);
				if (transaction != null)
					transaction.rollback();
			} catch (Exception e1) {
				TrickLogManager.Persist(e1);
			}
		} catch (Exception e) {
			try {
				this.error = e;
				serviceTaskFeedback.send(id, new MessageHandler("error.analysis.profile", "Creating a profile analysis failed", e));
				TrickLogManager.Persist(e);
				if (transaction != null)
					transaction.rollback();
			} catch (Exception e1) {
				TrickLogManager.Persist(e1);
			}

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
