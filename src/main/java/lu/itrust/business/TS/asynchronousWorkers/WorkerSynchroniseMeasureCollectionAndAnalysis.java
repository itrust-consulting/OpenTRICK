/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import static lu.itrust.business.TS.component.MeasureManager.update;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.Session;

import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOAnalysisStandard;
import lu.itrust.business.TS.database.dao.DAOAssetType;
import lu.itrust.business.TS.database.dao.DAOMeasureDescription;
import lu.itrust.business.TS.database.dao.DAOStandard;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisStandardHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOAssetTypeHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOMeasureDescriptionHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOStandardHBM;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.messagehandler.TaskName;
import lu.itrust.business.TS.model.standard.AssetStandard;
import lu.itrust.business.TS.model.standard.MaturityStandard;
import lu.itrust.business.TS.model.standard.NormalStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;

/**
 * @author eomar
 *
 */
public class WorkerSynchroniseMeasureCollectionAndAnalysis extends WorkerImpl {

	private String username;

	private DAOStandard daoStandard;

	private DAOAnalysis daoAnalysis;

	private DAOAssetType daoAssetType;

	private DAOAnalysisStandard daoAnalysisStandard;

	private DAOMeasureDescription daoMeasureDescription;

	public WorkerSynchroniseMeasureCollectionAndAnalysis(String username) {
		setUsername(username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#start()
	 */
	@Override
	public void start() {
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
				setName(TaskName.SYNCHRONIZE_ANALYSES_MEASURE_COLLECION);
				setCurrent(Thread.currentThread());
			}
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.synchronise.analyses.measure.collection.initialise", "Initialising data", null));
			initialiseDAO(session = getSessionFactory().openSession());
			session.beginTransaction();
			synchroniseMeasure();
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.commit.transcation", "Commit transaction", 95));
			session.getTransaction().commit();
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("success.synchronise.analyses.measure.collection", "Analyses were been successfully synchronize with knowledge", 100));
		} catch (Exception e) {
			setError(e);
			if (e instanceof TrickException) {
				TrickException e1 = (TrickException) e;
				getServiceTaskFeedback().send(getId(), new MessageHandler(e1.getCode(), e1.getParameters(), e.getMessage(), e));
			} else
				getServiceTaskFeedback().send(getId(), new MessageHandler("error.500.message", null, "Internal error", e));
			if (session != null && session.isOpen() && session.getTransaction().getStatus().canRollback())
				session.getTransaction().rollback();
		} finally {
			if (session != null && session.isOpen()) {
				try {
					session.close();
				} catch (Exception e) {
				}
			}
			cleanUp();
		}
	}

	private void synchroniseMeasure() {
		final List<Standard> standards = daoStandard.getAllNotBoundToAnalysis();
		final int min = 5, max = 95, countStd = standards.size();
		final MessageHandler handler = new MessageHandler("info.synchronise.analyses.measure.collection", null, "Synchronising measure collection of knowledge base to analyses",
				min);
		int current = 0;
		getServiceTaskFeedback().send(getId(), handler);
		for (Standard standard : standards) {
			final int total = (int) daoAnalysisStandard.countByStandard(standard), size = 40, count = (total / size) + 1;
			final List<MeasureDescription> measureDescriptions = daoMeasureDescription.getAllByStandard(standard);
			for (int page = 1; page <= count; page++) {
				daoAnalysisStandard.findByStandard(page, size, standard).forEach(a -> {
					final Map<String, MeasureDescription> measures = measureDescriptions.stream().collect(Collectors.toMap(MeasureDescription::getReference, Function.identity()));

					a.getMeasures().forEach(m -> measures.remove(m.getMeasureDescription().getReference()));

					if (!measures.isEmpty()) {
						if (a instanceof MaturityStandard)
							update((MaturityStandard) a, measures.values(), daoAnalysisStandard, daoAnalysis);
						else if (a instanceof AssetStandard)
							update((AssetStandard) a, measures.values(), daoAnalysisStandard, daoAnalysis);
						else if (a instanceof NormalStandard)
							update((NormalStandard) a, measures.values(), daoAnalysisStandard, daoAnalysis, daoAssetType);
					}
				});
			}
			handler.setProgress((int) (min + (current++ / (double) countStd) * max));
			getServiceTaskFeedback().send(getId(), handler);
		}

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
			cleanUp();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public synchronized void run() {
		start();
	}

	private void cleanUp() {
		if (isWorking()) {
			synchronized (this) {
				if (isWorking()) {
					setWorking(false);
					setFinished(new Timestamp(System.currentTimeMillis()));
				}
			}
		}

	}

	public void initialiseDAO(Session session) {
		daoStandard = new DAOStandardHBM(session);
		daoAnalysis = new DAOAnalysisHBM(session);
		daoAssetType = new DAOAssetTypeHBM(session);
		daoAnalysisStandard = new DAOAnalysisStandardHBM(session);
		daoMeasureDescription = new DAOMeasureDescriptionHBM(session);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
