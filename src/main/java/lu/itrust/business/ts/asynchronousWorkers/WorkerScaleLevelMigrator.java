/**
 * 
 */
package lu.itrust.business.ts.asynchronousWorkers;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.ts.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOImpactParameter;
import lu.itrust.business.ts.database.dao.DAOLikelihoodParameter;
import lu.itrust.business.ts.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOImpactParameterHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOLikelihoodParameterHBM;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.messagehandler.TaskName;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.cssf.RiskProbaImpact;
import lu.itrust.business.ts.model.parameter.IBoundedParameter;
import lu.itrust.business.ts.model.parameter.helper.ScaleLevelConvertor;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.parameter.impl.ImpactParameter;
import lu.itrust.business.ts.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.ts.model.parameter.value.AbstractValue;
import lu.itrust.business.ts.model.parameter.value.IValue;
import lu.itrust.business.ts.model.parameter.value.impl.FormulaValue;
import lu.itrust.business.ts.model.parameter.value.impl.LevelValue;
import lu.itrust.business.ts.model.parameter.value.impl.RealValue;
import lu.itrust.business.ts.model.parameter.value.impl.Value;
import lu.itrust.business.expressions.TokenType;
import lu.itrust.business.expressions.TokenizerToString;

/**
 * @author eomar
 *
 */
/**
 * This class represents a worker responsible for migrating scale levels in an analysis.
 * It extends the WorkerImpl class.
 * 
 * The worker migrates scale levels by updating the analysis, impact parameters, likelihood parameters,
 * and risk profiles based on the provided level mappers.
 * 
 * The worker runs asynchronously and can be started and canceled.
 * 
 * Usage:
 * WorkerScaleLevelMigrator migrator = new WorkerScaleLevelMigrator(idAnalysis, levelMappers);
 * migrator.start(); // Start the worker
 * migrator.cancel(); // Cancel the worker
 */
public class WorkerScaleLevelMigrator extends WorkerImpl {

	private int idAnalysis;

	private DAOAnalysis daoAnalysis;

	private DAOImpactParameter daoImpactParameter;

	private DAOLikelihoodParameter daoLikelihoodParameter;

	private Map<Integer, List<Integer>> levelMappers;

	/**
	 * @param poolManager
	 * @param sessionFactory
	 */
	public WorkerScaleLevelMigrator(int idAnalysis, Map<Integer, List<Integer>> levelMappers) {
		setIdAnalysis(idAnalysis);
		setLevelMappers(levelMappers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.asynchronousWorkers.Worker#start()
	 */
	@Override
	public synchronized void start() {
		run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.asynchronousWorkers.Worker#cancel()
	 */
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
				if (getWorkersPoolManager() != null && !getWorkersPoolManager().exist(getId()))
					if (!getWorkersPoolManager().add(this))
						return;
				if (isCanceled() || isWorking())
					return;
				setWorking(true);
				setStarted(new Timestamp(System.currentTimeMillis()));
				setName(TaskName.SCALE_LEVEL_MIGRATE);
				setCurrent(Thread.currentThread());
			}
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.scale.level.migrate.initialise.data", "Initialising data", 1));
			setUpDOA(session = getSessionFactory().openSession());
			session.beginTransaction();
			processing();
			session.getTransaction().commit();
			MessageHandler handler = new MessageHandler("success.scale.level.migrate", "Scale level has been successfully migrated", 100);
			handler.setAsyncCallbacks(new AsyncCallback("reload"));
			getServiceTaskFeedback().send(getId(), handler);
		} catch (TrickException e) {
			getServiceTaskFeedback().send(getId(), new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
			cancelProcessing(session, e);
		} catch (Exception e) {
			getServiceTaskFeedback().send(getId(), new MessageHandler("error.scale.level.migrate", "An unknown error occurred while migrating scales level", 0));
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

	/**
	 * Performs the processing of migrating scale levels for assessments, risk profiles, and analysis parameters.
	 * This method migrates the scale levels by converting the existing parameters to the new scale levels.
	 * It updates the analysis, risk profiles, and parameters accordingly.
	 */
	private void processing() {
		MessageHandler handler = new MessageHandler("info.scale.level.migrate.parameters", "Migrating parameters", 2);
		getServiceTaskFeedback().send(getId(), handler);
		final Analysis analysis = daoAnalysis.get(idAnalysis);
		final ScaleLevelConvertor convertor = new ScaleLevelConvertor(levelMappers, analysis.getImpactParameters(), analysis.getLikelihoodParameters());
		final ValueFactory factory = new ValueFactory(convertor.getParameters());
		final int progress[] = { 0, analysis.getAssessments().size() * 2, 5, 90 };// current, size , min, max
		handler.update("info.scale.level.migrate.assessment", "Migrating estimations", progress[2]);
		analysis.getAssessments().forEach(assessment -> {
			assessment.getImpacts().forEach(value -> migrateValue(convertor, factory, value));
			migrateValue(convertor, factory, assessment.getLikelihood());
			AssessmentAndRiskProfileManager.ComputeAlE(assessment);
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

	/**
	 * Migrates the given value using the provided convertor and factory.
	 * 
	 * @param convertor The ScaleLevelConvertor used for parameter conversion.
	 * @param factory The ValueFactory used for parameter and value lookup.
	 * @param value The value to be migrated.
	 */
	private void migrateValue(final ScaleLevelConvertor convertor, final ValueFactory factory, IValue value) {
		if (value == null || convertor == null || factory == null)
			return;
		if (value instanceof Value)
			((Value) value).setParameter(convertor.find((IBoundedParameter) ((AbstractValue) value).getParameter()));
		else if (value instanceof RealValue)
			((RealValue) value).setParameter(factory.findParameter(value.getReal(), value.getName()));
		else if (value instanceof LevelValue) {
			IBoundedParameter boundedParameter = convertor.find(value.getLevel(), value.getName());
			if (boundedParameter == null)
				boundedParameter = convertor.find((IBoundedParameter) ((AbstractValue) value).getParameter());
			((LevelValue) value).setParameter(boundedParameter);
			((LevelValue) value).setLevel(boundedParameter.getLevel());
		} else if (value instanceof FormulaValue) {
			final TokenizerToString tokenizer = new TokenizerToString(value.getVariable());
			tokenizer.getTokens().parallelStream().filter(token -> token.getType() == TokenType.Variable).forEach(token -> {
				IBoundedParameter variable = convertor.find(token.getParameter().toString());
				if (variable != null)
					token.setParameter(variable.getAcronym());
			});
			final IValue aux = factory.findDynValue(tokenizer.toString(), value.getName());
			if (aux != null)
				value.merge(aux);
			else
				((FormulaValue) value).setVariable(tokenizer.toString());
		}
	}

	/**
	 * Calculates the increased progress based on the given array of progress values.
	 * 
	 * @param progress an array of progress values [current, target, currentStep, totalSteps]
	 * @return the increased progress value
	 */
	private int increaseProgress(int[] progress) {
		return (int) (progress[0] + (progress[1] - progress[0]) * ((double) ++progress[2] / (double) progress[3]));
	}

	/**
	 * Updates the given RiskProbaImpact object by converting its probability and impacts using the provided ScaleLevelConvertor.
	 *
	 * @param riskProbaImpact The RiskProbaImpact object to be updated.
	 * @param convertor The ScaleLevelConvertor used for converting the probability and impacts.
	 */
	private void update(RiskProbaImpact riskProbaImpact, ScaleLevelConvertor convertor) {
		if (riskProbaImpact.getProbability() != null)
			riskProbaImpact.setProbability((LikelihoodParameter) convertor.find(riskProbaImpact.getProbability()));
		final List<ImpactParameter> impactParameters = riskProbaImpact.getImpacts().stream().map(i -> (ImpactParameter) convertor.find(i)).collect(Collectors.toList());
		riskProbaImpact.getImpacts().clear();
		riskProbaImpact.setImpacts(impactParameters);
	}

	/**
	 * Sets up the Data Access Objects (DAOs) for the WorkerScaleLevelMigrator.
	 * Initializes the DAOAnalysisHBM, DAOImpactParameterHBM, and DAOLikelihoodParameterHBM objects
	 * with the provided session.
	 *
	 * @param session the Hibernate session to be used by the DAOs
	 */
	private void setUpDOA(Session session) {
		setDaoAnalysis(new DAOAnalysisHBM(session));
		setDaoImpactParameter(new DAOImpactParameterHBM(session));
		setDaoLikelihoodParameter(new DAOLikelihoodParameterHBM(session));
	}

	/**
	 * Cancels the processing of the worker and handles any exceptions that occur.
	 *
	 * @param session the current session
	 * @param e the exception that occurred during processing
	 */
	private void cancelProcessing(Session session, Exception e) {
		setError(e);
		rollback(session);
		TrickLogManager.Persist(e);
	}

	/**
	 * Rolls back the current transaction in the given session.
	 * If the session is open and the transaction is rollbackable, the transaction will be rolled back.
	 *
	 * @param session the session in which the transaction should be rolled back
	 */
	protected void rollback(Session session) {
		try {
			if (session != null && session.isOpen() && session.getTransaction().getStatus().canRollback())
				session.getTransaction().rollback();
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
	}

	/**
	 * Returns the ID analysis.
	 *
	 * @return the ID analysis
	 */
	public int getIdAnalysis() {
		return idAnalysis;
	}

	/**
	 * Sets the ID of the analysis.
	 *
	 * @param idAnalysis the ID of the analysis
	 */
	public void setIdAnalysis(int idAnalysis) {
		this.idAnalysis = idAnalysis;
	}

	/**
	 * This class represents the DAOAnalysis object.
	 * It provides methods to analyze and retrieve data from the DAO.
	 */
	public DAOAnalysis getDaoAnalysis() {
		return daoAnalysis;
	}

	/**
	 * Sets the DAOAnalysis object for this WorkerScaleLevelMigrator.
	 *
	 * @param daoAnalysis the DAOAnalysis object to be set
	 */
	public void setDaoAnalysis(DAOAnalysis daoAnalysis) {
		this.daoAnalysis = daoAnalysis;
	}

	/**
	 * Returns the DAOImpactParameter object.
	 *
	 * @return the DAOImpactParameter object.
	 */
	public DAOImpactParameter getDaoImpactParameter() {
		return daoImpactParameter;
	}

	/**
	 * Sets the DAO impact parameter.
	 *
	 * @param daoImpactParameter the DAO impact parameter to set
	 */
	public void setDaoImpactParameter(DAOImpactParameter daoImpactParameter) {
		this.daoImpactParameter = daoImpactParameter;
	}

	/**
	 * This class represents the DAO (Data Access Object) for the Likelihood Parameter.
	 * It provides methods to interact with the database and retrieve Likelihood Parameter objects.
	 */
	public DAOLikelihoodParameter getDaoLikelihoodParameter() {
		return daoLikelihoodParameter;
	}

	/**
	 * Sets the DAO likelihood parameter.
	 *
	 * @param daoLikelihoodParameter the DAO likelihood parameter to set
	 */
	public void setDaoLikelihoodParameter(DAOLikelihoodParameter daoLikelihoodParameter) {
		this.daoLikelihoodParameter = daoLikelihoodParameter;
	}

	/**
	 * Returns the level mappers map.
	 *
	 * @return the level mappers map
	 */
	public Map<Integer, List<Integer>> getLevelMappers() {
		return levelMappers;
	}

	/**
	 * Sets the level mappers for the WorkerScaleLevelMigrator.
	 * 
	 * @param levelMappers a map containing the level mappers
	 */
	public void setLevelMappers(Map<Integer, List<Integer>> levelMappers) {
		this.levelMappers = levelMappers;
	}

}
