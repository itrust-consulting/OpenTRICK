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

import lu.itrust.business.TS.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.form.TicketingForm;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.messagehandler.TaskName;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
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

	private TicketingForm ticketingForm;

	/**
	 * @param idAnalysis
	 * @param client
	 * @param measureIds
	 * @param serviceTaskFeedback
	 * @param poolManager
	 * @param sessionFactory
	 */
	public WorkerGenerateTickets(Integer idAnalysis, Client client, TicketingForm ticketingForm) {
		this.idAnalysis = idAnalysis;
		this.client = client;
		this.ticketingForm = ticketingForm;
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
				setName(TaskName.GENERATE_TICKETS);
				setCurrent(Thread.currentThread());
			}
			session = getSessionFactory().openSession();
			final DAOAnalysis daoAnalysis = new DAOAnalysisHBM(session);
			session.beginTransaction();
			final Analysis analysis = daoAnalysis.get(idAnalysis);
			if (analysis.hasProject()) {
				final MessageHandler handler = new MessageHandler("info.load.measure", null, "Loading measures", 1);
				getServiceTaskFeedback().send(getId(), handler);
				final Map<Integer, Integer> contains = ticketingForm.getNews().stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
				ticketingForm.getUpdates().forEach(idMeasure -> contains.put(idMeasure, idMeasure));
				final Map<Integer, Measure> mapMeasures = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
						.filter(measure -> contains.containsKey(measure.getId())).collect(Collectors.toMap(Measure::getId, Function.identity()));
				if (!mapMeasures.isEmpty()) {
					final List<Measure> newMeasures = new LinkedList<>(), updateMeasures = new LinkedList<>();
					final ValueFactory valueFactory = new ValueFactory(analysis.getParameters());
					ticketingForm.getNews().stream().filter(key -> mapMeasures.containsKey(key)).forEach(key -> newMeasures.add(mapMeasures.get(key)));
					ticketingForm.getUpdates().stream().filter(key -> mapMeasures.containsKey(key)).forEach(key -> updateMeasures.add(mapMeasures.get(key)));
					handler.update("info.creating.tickets", "Creating tickets", 3);
					final boolean result = client.createIssues(analysis.getProject(), analysis.getLanguage().getAlpha2(), newMeasures, updateMeasures, valueFactory, handler, 95)
							|| handler.getCode().startsWith("error.");
					if (!result)
						handler.update("info.update.analysis", "Updating analysis", 95);
					daoAnalysis.saveOrUpdate(analysis);
					if (!result)
						handler.update("info.commit.transcation", "Commit transaction", 98);
					session.getTransaction().commit();
					if (!newMeasures.isEmpty()) {
						if (handler.getCode().startsWith("error."))
							handler.setProgress(100);
						else if (result)
							handler.update("error.ticketing.created", "Some tasks are not created!", 100);
						else
							handler.update("success.ticketing.created", "Tasks are successfully created", 100);

						final Standard standard = newMeasures.get(0).getAnalysisStandard().getStandard();
						final boolean isSame = !newMeasures.stream().anyMatch(measure -> !measure.getAnalysisStandard().getStandard().equals(standard));
						if (newMeasures.size() < 10) {
							int count = 0;
							final AsyncCallback[] callbacks = new AsyncCallback[newMeasures.size() + 1];
							callbacks[count++] = new AsyncCallback("reloadSection", "section_actionplans");
							for (Measure measure : newMeasures)
								callbacks[count++] = new AsyncCallback("reloadMeasureRow", measure.getId(), measure.getAnalysisStandard().getStandard().getId());
							handler.setAsyncCallbacks(callbacks);
						} else if (isSame)
							handler.setAsyncCallbacks(new AsyncCallback("reloadSection", "section_standard_" + standard.getId()),
									new AsyncCallback("reloadSection", "section_actionplans"));
						else
							handler.setAsyncCallbacks(new AsyncCallback("reload"));
					} else if (handler.getCode().startsWith("error."))
						handler.setProgress(100);
					else if (result)
						handler.update("error.ticketing.updated", "Some tasks are not updated!", 100);
					else
						handler.update("success.ticketing.updated", "Tasks are successfully updated", 100);

				}
				getServiceTaskFeedback().send(getId(), handler);
			}
		} catch (Exception e) {
			TrickLogManager.Persist(e);
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

		if (ticketingForm != null) {
			ticketingForm.clear();
			ticketingForm = null;
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
	 * @param idAnalysis the idAnalysis to set
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
	 * @param client the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}

}
