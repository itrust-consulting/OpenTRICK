/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOImpactParameter;
import lu.itrust.business.TS.database.dao.DAOLikelihoodParameter;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOImpactParameterHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOLikelihoodParameterHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.helper.ScaleLevelConvertor;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.TS.model.parameter.value.impl.LevelValue;
import lu.itrust.business.TS.model.parameter.value.impl.RealValue;
import lu.itrust.business.TS.model.parameter.value.impl.Value;
import lu.itrust.business.expressions.StringExpressionParser;

/**
 * @author eomar
 *
 */
public class WorkerScaleLevelMigrator extends WorkerImpl {

	private int idAnalysis;

	private ServiceTaskFeedback serviceTaskFeedback;

	private DAOAnalysis daoAnalysis;

	private DAOImpactParameter daoImpactParameter;

	private DAOLikelihoodParameter daoLikelihoodParameter;

	private Map<Integer, List<Integer>> levelMappers;

	/**
	 * @param poolManager
	 * @param sessionFactory
	 */
	public WorkerScaleLevelMigrator(int idAnalysis, Map<Integer, List<Integer>> levelMappers, ServiceTaskFeedback serviceTaskFeedback, WorkersPoolManager poolManager,
			SessionFactory sessionFactory) {
		super(poolManager, sessionFactory);
		setIdAnalysis(idAnalysis);
		setServiceTaskFeedback(serviceTaskFeedback);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#start()
	 */
	@Override
	public synchronized void start() {
		run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#cancel()
	 */
	@Override
	public void cancel() {
		try {
			if (isWorking() && !isCanceled()) {
				synchronized (this) {
					if (isWorking() && !isCanceled()) {
						Thread.currentThread().interrupt();
						setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
			cancelProcessing(null, e);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
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
				setWorking(true);
				setStarted(new Timestamp(System.currentTimeMillis()));
			}

			setUpDOA(session = getSessionFactory().openSession());
			session.beginTransaction();
			processing();
		} catch (TrickException e) {
			serviceTaskFeedback.send(getId(), new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
			cancelProcessing(session, e);
		} catch (Exception e) {
			serviceTaskFeedback.send(getId(), new MessageHandler("error.analysis.duplicate", "An unknown error occurred while copying analysis", 0));
			cancelProcessing(session, e);
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

	private void processing() {
		Analysis analysis = daoAnalysis.get(idAnalysis);
		ScaleLevelConvertor convertor = new ScaleLevelConvertor(levelMappers, analysis.getImpactParameters(), analysis.getLikelihoodParameters());
		ValueFactory factory = new ValueFactory(convertor.getParameters());

		analysis.getAssessments().forEach(assessment -> {
			assessment.getImpacts().forEach(value -> {
				if (value instanceof Value)
					((Value) value).setParameter(convertor.find((IBoundedParameter) value.getParameter()));
				else if (value instanceof RealValue)
					((RealValue) value).setParameter(factory.findParameter(value.getReal(), value.getParameter().getTypeName()));
				else if (value instanceof LevelValue)
					((LevelValue) value).setParameter(factory.findParameter(value.getLevel(), value.getParameter().getTypeName()));
			});
			
			IBoundedParameter parameter = convertor.find(assessment.getLikelihood());
			if (parameter != null)
				assessment.setLikelihood(parameter.getAcronym());
			AssessmentAndRiskProfileManager.ComputeAlE(assessment, factory);
		});
	}

	private void setUpDOA(Session session) {
		setDaoAnalysis(new DAOAnalysisHBM(session));
		setDaoImpactParameter(new DAOImpactParameterHBM(session));
		setDaoLikelihoodParameter(new DAOLikelihoodParameterHBM(session));
	}

	private void cancelProcessing(Session session, Exception e) {
		setError(e);
		rollback(session);
		TrickLogManager.Persist(e);
	}

	protected void rollback(Session session) {
		try {
			if (session != null && session.isOpen() && session.getTransaction().getStatus().canRollback())
				session.getTransaction().rollback();
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
	}

	public int getIdAnalysis() {
		return idAnalysis;
	}

	public void setIdAnalysis(int idAnalysis) {
		this.idAnalysis = idAnalysis;
	}

	public ServiceTaskFeedback getServiceTaskFeedback() {
		return serviceTaskFeedback;
	}

	public void setServiceTaskFeedback(ServiceTaskFeedback serviceTaskFeedback) {
		this.serviceTaskFeedback = serviceTaskFeedback;
	}

	public DAOAnalysis getDaoAnalysis() {
		return daoAnalysis;
	}

	public void setDaoAnalysis(DAOAnalysis daoAnalysis) {
		this.daoAnalysis = daoAnalysis;
	}

	public DAOImpactParameter getDaoImpactParameter() {
		return daoImpactParameter;
	}

	public void setDaoImpactParameter(DAOImpactParameter daoImpactParameter) {
		this.daoImpactParameter = daoImpactParameter;
	}

	public DAOLikelihoodParameter getDaoLikelihoodParameter() {
		return daoLikelihoodParameter;
	}

	public void setDaoLikelihoodParameter(DAOLikelihoodParameter daoLikelihoodParameter) {
		this.daoLikelihoodParameter = daoLikelihoodParameter;
	}

	public Map<Integer, List<Integer>> getLevelMappers() {
		return levelMappers;
	}

	public void setLevelMappers(Map<Integer, List<Integer>> levelMappers) {
		this.levelMappers = levelMappers;
	}

}
