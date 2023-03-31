/**
 * 
 */
package lu.itrust.business.ts.asynchronousWorkers;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import lu.itrust.business.ts.component.Duplicator;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOCustomer;
import lu.itrust.business.ts.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOCustomerHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.usermanagement.User;

/**
 * @author eomar
 * 
 */
public class WorkerCreateAnalysisProfile extends WorkerImpl {

	private String name;

	private String username;

	private Integer analysisId;

	private List<Integer> standards;

	/**
	 * @param serviceTaskFeedback
	 * @param sessionFactory
	 * @param poolManager
	 * @param analysisProfile
	 */
	public WorkerCreateAnalysisProfile(Integer analysisId, String name, List<Integer> standards, String username) {
		this.name = name;
		this.username = username;
		this.analysisId = analysisId;
		this.standards = standards;
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
				if (getWorkersPoolManager() != null && !getWorkersPoolManager().exist(getId()))
					if (!getWorkersPoolManager().add(this))
						return;
				if (isCanceled() || isWorking())
					return;
				setWorking(true);
				setStarted(new Timestamp(System.currentTimeMillis()));
				setCurrent(Thread.currentThread());
			}
			session = getSessionFactory().openSession();
			DAOAnalysis daoAnalysis = new DAOAnalysisHBM(session);
			DAOCustomer daoCustomer = new DAOCustomerHBM(session);
			User owner = new DAOUserHBM(session).get(username);
			Customer customer = daoCustomer.getProfile();
			if (customer == null) {
				getServiceTaskFeedback().send(getId(), new MessageHandler("error.not.customer.profile", "Please add a profile customer before creating an analysis profile", null));
				return;
			}
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.analysis.profile.load", "Load analysis", 1));
			Analysis analysis = daoAnalysis.get(analysisId);
			Analysis copy = new Duplicator(session).createProfile(analysis, name, standards, getServiceTaskFeedback(), getId());
			copy.setCustomer(customer);
			copy.setOwner(owner);
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.analysis.profile.save", "Save analysis profile", 96));
			transaction = session.beginTransaction();
			daoAnalysis.saveOrUpdate(copy);
			transaction.commit();
			getServiceTaskFeedback().send(getId(), new MessageHandler("success.analysis.profile", "New analysis profile was successfully created", 100));
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogType.ANALYSIS, "log.analysis.profile.create",
					String.format("Analyis: %s, version: %s, profile: %s, name: %s, version: %s", analysis.getIdentifier(), analysis.getVersion(), copy.getIdentifier(),
							copy.getLabel(), copy.getVersion()),
					username, LogAction.CREATE, analysis.getIdentifier(), analysis.getVersion(), copy.getIdentifier(), copy.getLabel(), copy.getVersion());
		} catch (TrickException e) {
			try {
				setError(e);
				getServiceTaskFeedback().send(getId(), new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
				if (transaction != null && transaction.getStatus().canRollback())
					transaction.rollback();
			} catch (Exception e1) {
				TrickLogManager.Persist(e1);
			}
		} catch (Exception e) {
			try {
				setError(e);
				getServiceTaskFeedback().send(getId(), new MessageHandler("error.analysis.profile", "Creating a profile analysis failed", e));
				if (transaction != null && transaction.getStatus().canRollback())
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
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
					}
				}
			}
		}

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
						if (getCurrent() == null)
							Thread.currentThread().interrupt();
						else
							getCurrent().interrupt();
						setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
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
		}
	}

}
