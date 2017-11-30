/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import lu.itrust.business.TS.asynchronousWorkers.helper.AsyncCallback;
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
import lu.itrust.business.TS.messagehandler.TaskName;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.helper.ScaleLevelConvertor;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.parameter.value.impl.LevelValue;
import lu.itrust.business.TS.model.parameter.value.impl.RealValue;
import lu.itrust.business.TS.model.parameter.value.impl.Value;
import lu.itrust.business.expressions.TokenType;
import lu.itrust.business.expressions.TokenizerToString;

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
		setLevelMappers(levelMappers);
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
						if(getCurrent() == null)
							Thread.currentThread().interrupt();
						else getCurrent().interrupt();
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
				setName(TaskName.SCALE_LEVEL_MIGRATE);
				setCurrent(Thread.currentThread());
			}
			serviceTaskFeedback.send(getId(), new MessageHandler("info.scale.level.migrate.initialise.data", "Initialising data", 1));
			setUpDOA(session = getSessionFactory().openSession());
			session.beginTransaction();
			processing();
			session.getTransaction().commit();
			MessageHandler handler = new MessageHandler("success.scale.level.migrate", "Scale level has been successfully migrated", 100);
			handler.setAsyncCallbacks(new AsyncCallback("reload"));
			serviceTaskFeedback.send(getId(), handler);
		} catch (TrickException e) {
			serviceTaskFeedback.send(getId(), new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
			cancelProcessing(session, e);
		} catch (Exception e) {
			serviceTaskFeedback.send(getId(), new MessageHandler("error.scale.level.migrate", "An unknown error occurred while migrating scales level", 0));
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
		MessageHandler handler = new MessageHandler("info.scale.level.migrate.parameters", "Migrating parameters", 2);
		serviceTaskFeedback.send(getId(), handler);
		Analysis analysis = daoAnalysis.get(idAnalysis);
		ScaleLevelConvertor convertor = new ScaleLevelConvertor(levelMappers, analysis.getImpactParameters(), analysis.getLikelihoodParameters());
		ValueFactory factory = new ValueFactory(convertor.getParameters()),
				oldConvertor = analysis.isQuantitative() || analysis.isHybrid() ? null : new ValueFactory(analysis.getExpressionParameters());
		int progress[] = { 0, analysis.getAssessments().size() * 2, 5, 90 };// current, size , min, max
		handler.update("info.scale.level.migrate.assessment", "Migrating estimations", progress[2]);
		analysis.getAssessments().forEach(assessment -> {
			assessment.getImpacts().forEach(value -> {
				if (value instanceof Value)
					((Value) value).setParameter(convertor.find((IBoundedParameter) value.getParameter()));
				else if (value instanceof RealValue)
					((RealValue) value).setParameter(factory.findParameter(value.getReal(), value.getParameter().getTypeName()));
				else if (value instanceof LevelValue) {
					IBoundedParameter boundedParameter = convertor.find(value.getLevel(), value.getParameter().getTypeName());
					if (boundedParameter == null)
						boundedParameter = convertor.find((IBoundedParameter) value.getParameter());
					((LevelValue) value).setParameter(boundedParameter);
					((LevelValue) value).setLevel(boundedParameter.getLevel());
				}
			});
			IBoundedParameter parameter = convertor.find(assessment.getLikelihood());
			if (parameter != null)
				assessment.setLikelihood(parameter.getAcronym());
			else if (oldConvertor == null) {
				TokenizerToString tokenizer = new TokenizerToString(assessment.getLikelihood());
				tokenizer.getTokens().parallelStream().filter(token -> token.getType() == TokenType.Variable).forEach(token -> {
					IBoundedParameter variable = convertor.find(token.getParameter().toString());
					if (variable != null)
						token.setParameter(variable.getAcronym());
				});
				assessment.setLikelihood(tokenizer.toString());
			} else {// convert to level
				IValue value = oldConvertor.findExp(assessment.getLikelihood());
				if (value != null)
					assessment.setLikelihood(convertor.find(value.getVariable()).getAcronym());
			}
			AssessmentAndRiskProfileManager.ComputeAlE(assessment, factory);
			handler.setProgress(increaseProgress(progress));
		});

		handler.update("info.scale.level.migrate.risk-profile", "Migrating risks profile", handler.getProgress());

		analysis.getRiskProfiles().forEach(riskProfile -> {
			if (riskProfile.getRawProbaImpact() != null)
				update(riskProfile.getRawProbaImpact(), convertor);
			if (riskProfile.getExpProbaImpact() != null)
				update(riskProfile.getExpProbaImpact(), convertor);
		});

		handler.update("info.scale.level.migrate.analysis.update", "Updating analysis", 91);

		analysis.getImpactParameters().clear();
		analysis.getLikelihoodParameters().clear();
		convertor.getParameters().stream().forEach(paramenter -> {
			if (paramenter instanceof LikelihoodParameter)
				analysis.getLikelihoodParameters().add((LikelihoodParameter) paramenter);
			else if (paramenter instanceof ImpactParameter)
				analysis.getImpactParameters().add((ImpactParameter) paramenter);
			handler.setProgress(increaseProgress(progress));
		});

		handler.update("info.scale.level.migrate.analysis.save", "Saving analysis", 92);
		daoAnalysis.saveOrUpdate(analysis);

		handler.update("info.scale.level.migrate.parameter.delete", "Deleting old parameters", 93);
		convertor.getDeletables().forEach(paramenter -> {
			if (paramenter instanceof LikelihoodParameter)
				daoLikelihoodParameter.delete((LikelihoodParameter) paramenter);
			else if (paramenter instanceof ImpactParameter)
				daoImpactParameter.delete((ImpactParameter) paramenter);
		});

		convertor.clear();
		handler.update("info.scale.level.migrate.transaction.commit", "Writing data to database", 93);
	}

	private int increaseProgress(int[] progress) {
		return (int) (progress[0] + (progress[1] - progress[0]) * ((double) ++progress[2] / (double) progress[3]));
	}

	private void update(RiskProbaImpact riskProbaImpact, ScaleLevelConvertor convertor) {
		if (riskProbaImpact.getProbability() != null)
			riskProbaImpact.setProbability((LikelihoodParameter) convertor.find(riskProbaImpact.getProbability()));
		List<ImpactParameter> impactParameters = riskProbaImpact.getImpacts().stream().map(i -> (ImpactParameter) convertor.find(i)).collect(Collectors.toList());
		riskProbaImpact.getImpacts().clear();
		riskProbaImpact.setImpacts(impactParameters);
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
