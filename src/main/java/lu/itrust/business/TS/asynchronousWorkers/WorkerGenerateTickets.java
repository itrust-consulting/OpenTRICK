/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.util.StringUtils;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.ticketing.builder.Client;

/**
 * @author eomar
 *
 */
public class WorkerGenerateTickets extends WorkerImpl {

	private Integer idAnalysis;

	private Client client = null;

	private List<Integer> measureIds = null;

	private ServiceTaskFeedback serviceTaskFeedback;

	/**
	 * @param idAnalysis
	 * @param client
	 * @param measureIds
	 * @param serviceTaskFeedback
	 * @param poolManager
	 * @param sessionFactory
	 */
	public WorkerGenerateTickets(Integer idAnalysis, Client client, List<Integer> measureIds, ServiceTaskFeedback serviceTaskFeedback, WorkersPoolManager poolManager,
			SessionFactory sessionFactory) {
		super(poolManager, sessionFactory);
		this.idAnalysis = idAnalysis;
		this.client = client;
		this.measureIds = measureIds;
		this.serviceTaskFeedback = serviceTaskFeedback;
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
				if (getPoolManager() != null && !getPoolManager().exist(getId()))
					if (!getPoolManager().add(this))
						return;
				if (isCanceled() || isWorking())
					return;
				setWorking(true);
				setStarted(new Timestamp(System.currentTimeMillis()));
			}
			session = getSessionFactory().openSession();
			DAOAnalysis daoAnalysis = new DAOAnalysisHBM(session);
			session.beginTransaction();
			Analysis analysis = daoAnalysis.get(idAnalysis);
			if (analysis.hasProject()) {
				MessageHandler handler = new MessageHandler("info.load.measure", null, "Loading measures", 1);
				serviceTaskFeedback.send(getId(), handler);
				Map<Integer, Measure> mapMeasures;
				if (measureIds.size() > 5) {
					Map<Integer, Integer> contains = measureIds.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
					mapMeasures = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
							.filter(measure -> contains.containsKey(measure.getId()) && StringUtils.isEmpty(measure.getTicket()))
							.collect(Collectors.toMap(Measure::getId, Function.identity()));
				} else {
					mapMeasures = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
							.filter(measure -> StringUtils.isEmpty(measure.getTicket()) && measureIds.contains(measure.getId()))
							.collect(Collectors.toMap(Measure::getId, Function.identity()));
				}
				if (!mapMeasures.isEmpty()) {
					List<Measure> measures = new LinkedList<>();
					measureIds.stream().filter(key -> mapMeasures.containsKey(key)).forEach(key -> measures.add(mapMeasures.get(key)));
					handler.update("info.creating.tickets", "Creating tickets", 3);
					client.createIssues(analysis.getProject(), analysis.getLanguage().getAlpha2(), measures, handler, 95);
					handler.update("info.update.analysis", "Updating analysis", 95);
					daoAnalysis.saveOrUpdate(analysis);
					handler.update("info.commit.transcation", "Commit transaction", 98);
					session.getTransaction().commit();
					handler = new MessageHandler("success.ticketing.created", "Tickets are successfully created", 100);
					Standard standard = measures.get(0).getAnalysisStandard().getStandard();
					boolean isSame = !measures.stream().anyMatch(measure -> !measure.getAnalysisStandard().getStandard().equals(standard));
					if (measures.size() < 10) {
						String data = "reloadSection('section_actionplans');";
						for (Measure measure : measures)
							data += "reloadMeasureRow(" + measure.getId() + "," + measure.getAnalysisStandard().getStandard().getId() + ");";
						handler.setAsyncCallback(new AsyncCallback(data));
					} else if (isSame)
						handler.setAsyncCallback(new AsyncCallback("reloadSection(['section_standard_" + standard.getId() + "','section_actionplans'])"));
					else
						handler.setAsyncCallback(new AsyncCallback("location.reload()"));
				}
				serviceTaskFeedback.send(getId(), handler);
			}
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			if (e instanceof TrickException) {
				TrickException e1 = (TrickException) e;
				serviceTaskFeedback.send(getId(), new MessageHandler(e1.getCode(), e1.getParameters(), e.getMessage(), e));
			} else
				serviceTaskFeedback.send(getId(), new MessageHandler("error.internal", null, "Internal error", e));
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

	private void cleanUp() {
		if (isWorking()) {
			synchronized (this) {
				if (isWorking()) {
					setWorking(false);
					setFinished(new Timestamp(System.currentTimeMillis()));
				}
			}
		}

		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
				TrickLogManager.Persist(e);
			} finally {
				client = null;
			}
		}

		if (measureIds != null) {
			measureIds.clear();
			measureIds = null;
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
						Thread.currentThread().interrupt();
						setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
			setError(e);
			TrickLogManager.Persist(e);
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

	/**
	 * @return the idAnalysis
	 */
	public Integer getIdAnalysis() {
		return idAnalysis;
	}

	/**
	 * @param idAnalysis
	 *            the idAnalysis to set
	 */
	public void setIdAnalysis(Integer idAnalysis) {
		this.idAnalysis = idAnalysis;
	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}

}
