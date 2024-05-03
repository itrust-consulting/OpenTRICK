/**
 * 
 */
package lu.itrust.business.ts.asynchronousWorkers;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.form.TicketingForm;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.messagehandler.TaskName;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.general.EmailTemplate;
import lu.itrust.business.ts.model.general.email.Email;
import lu.itrust.business.ts.model.general.email.Recipient;
import lu.itrust.business.ts.model.general.email.RecipientType;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.helper.MeasureComparator;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.ts.model.ticketing.builder.Client;


/**
 * This class represents a worker responsible for generating tickets.
 * It extends the `WorkerImpl` class.
 * 
 * The worker is initialized with an analysis ID, a client, and a ticketing form.
 * It executes the task of generating tickets by creating or updating issues based on the analysis and measures.
 * The generated tickets are associated with the analysis project and the customer's ticketing system.
 * 
 * The worker starts the task by calling the `start()` method, which opens a session and executes the task.
 * If an exception occurs during the task execution, it is logged and appropriate error messages are sent.
 * Finally, the session is closed and the worker is cleaned up.
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
	 * @see lu.itrust.business.ts.asynchronousWorkers.Worker#start()
	 */
	@Override
	public void start() {
		Session session = null;
		try {
			synchronized (this) {
				if (getWorkersPoolManager() != null && !getWorkersPoolManager().exist(getId())
						&& !getWorkersPoolManager().add(this))
					return;
				if (isCanceled() || isWorking())
					return;
				setWorking(true);
				setStarted(new Timestamp(System.currentTimeMillis()));
				setName(TaskName.GENERATE_TICKETS);
				setCurrent(Thread.currentThread());
			}
			session = getSessionFactory().openSession();
			executeTask(session);
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			if (e instanceof TrickException) {
				TrickException e1 = (TrickException) e;
				getServiceTaskFeedback().send(getId(),
						new MessageHandler(e1.getCode(), e1.getParameters(), e.getMessage(), e));
			} else
				getServiceTaskFeedback().send(getId(),
						new MessageHandler("error.500.message", null, "Internal error", e));
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

	/**
	 * Executes the task for generating tickets.
	 *
	 * @param session The session object for database operations.
	 * @throws InterruptedException If the execution of the task is interrupted.
	 */
	private void executeTask(Session session) throws InterruptedException {
		final DAOAnalysis daoAnalysis = new DAOAnalysisHBM(session);
		session.beginTransaction();
		final Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis.hasProject() || analysis.getCustomer().getTicketingSystem().getType().isNoClient()) {
			final MessageHandler handler = new MessageHandler("info.load.measure", null, "Loading measures", 1);
			getServiceTaskFeedback().send(getId(), handler);
			final Map<Integer, Integer> contains = ticketingForm.getNews().stream()
					.collect(Collectors.toMap(Function.identity(), Function.identity()));
			ticketingForm.getUpdates().forEach(idMeasure -> contains.put(idMeasure, idMeasure));
			final Map<Integer, Measure> mapMeasures = analysis.getAnalysisStandards().values().stream()
					.flatMap(listMeasures -> listMeasures.getMeasures().stream())
					.filter(measure -> contains.containsKey(measure.getId()))
					.collect(Collectors.toMap(Measure::getId, Function.identity()));
			if (!mapMeasures.isEmpty()) {
				final List<Measure> newMeasures = new LinkedList<>();
				final List<Measure> updateMeasures = new LinkedList<>();
				final MeasureComparator comparator = new MeasureComparator();
				final ValueFactory valueFactory = new ValueFactory(analysis.getParameters());

				ticketingForm.getNews().removeAll(ticketingForm.getUpdates());

				ticketingForm.getUpdates().removeAll(ticketingForm.getNews());

				ticketingForm.getNews().stream().filter(mapMeasures::containsKey).distinct()
						.forEach(key -> newMeasures.add(mapMeasures.get(key)));
				ticketingForm.getUpdates().stream().filter(mapMeasures::containsKey).distinct()
						.forEach(key -> updateMeasures.add(mapMeasures.get(key)));

				newMeasures.sort(comparator);

				updateMeasures.sort(comparator);

				handler.update("info.creating.tickets", "Creating tickets", 3);
				final boolean result = createIssues(analysis, newMeasures, updateMeasures, valueFactory,
						handler, 95)
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

					final Standard standard = newMeasures.get(0).getMeasureDescription().getStandard();
					final boolean isSame = newMeasures.stream()
							.allMatch(measure -> measure.getMeasureDescription().getStandard().equals(standard));
					if (newMeasures.size() < 10) {
						int count = 0;
						final AsyncCallback[] callbacks = new AsyncCallback[newMeasures.size() + 1];
						callbacks[count++] = new AsyncCallback("reloadSection", "section_actionplans");
						for (Measure measure : newMeasures)
							callbacks[count++] = new AsyncCallback("reloadMeasureRow", measure.getId(),
									measure.getMeasureDescription().getStandard().getId());
						handler.setAsyncCallbacks(callbacks);
					} else if (isSame)
						handler.setAsyncCallbacks(
								new AsyncCallback("reloadSection", "section_standard_" + standard.getId()),
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
	}

	/**
	 * Creates issues for the given analysis using the provided measures and value factory.
	 * 
	 * @param analysis The analysis object.
	 * @param newMeasures The list of new measures.
	 * @param updateMeasures The list of updated measures.
	 * @param valueFactory The value factory.
	 * @param handler The message handler.
	 * @param maxProgess The maximum progress value.
	 * @return True if the issues were created successfully, false otherwise.
	 * @throws InterruptedException If the operation is interrupted.
	 */
	private boolean createIssues(Analysis analysis, List<Measure> newMeasures,
			List<Measure> updateMeasures, ValueFactory valueFactory, MessageHandler handler, int maxProgess)
			throws InterruptedException {
		if (client != null)
			return client.createIssues(analysis.getProject(), analysis.getCustomer().getTicketingSystem().getTracker(),
					analysis.getLanguage().getAlpha2(), newMeasures,
					updateMeasures, valueFactory, handler, maxProgess);
		final EmailTemplate template = analysis.getCustomer().getTicketingSystem().getEmailTemplate();
		if (template == null)
			throw new TrickException("error.ticket_system.no_email_template",
					"The email template has not been configured");

		final String language = analysis.getLanguage().getAlpha2();

		final List<Measure> measures = new LinkedList<>(newMeasures);

		final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.FRANCE);

		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		decimalFormat.setMaximumFractionDigits(2);

		measures.addAll(updateMeasures);

		measures.sort(new MeasureComparator());

		final String subject = template.getTitle().trim().toLowerCase();

		final int min = handler.getProgress();
		final int size = measures.size();
		int current = 0;

		handler.update("info.creating.email.tickets", "Creating tickets by email", min);

		for (Measure measure : measures) {
			final Map<String, Object> model = new HashMap<>();
			final MeasureDescriptionText measureDescriptionText = measure.getMeasureDescription()
					.getMeasureDescriptionTextByAlpha2(language);
			final Email email = new Email();
			switch (subject) {
				case "securitymeasure":
				case "domain":
					email.setSubject(StringUtils.abbreviate(measureDescriptionText.getDomain(), 998));
					break;
				case "to do":
				case "todo":
					email.setSubject(StringUtils.abbreviate(measure.getToDo(), 998));
					break;
				case "to ckeck":
				case "tocheck":
					email.setSubject(StringUtils.abbreviate(measure.getToDo(), 998));
					break;
				default:
					email.setSubject(StringUtils.abbreviate(measure.getMeasureDescription().getStandard().getName() + ": "
							+ measureDescriptionText.getDomain(), 998));
			}

			email.getRecipients().add(new Recipient(template.getEmail(), RecipientType.TO));

			model.put("Std", measure.getMeasureDescription().getStandard().getName());
			model.put("Ref", measure.getMeasureDescription().getReference());
			model.put("SecMeasure", measureDescriptionText.getDomain());
			model.put("Desc", measureDescriptionText.getDescription());
			model.put("ST", measure.getStatus());
			model.put("IR", measure.getImplementationRateValue(valueFactory));
			model.put("IW", measure.getInternalWL());
			model.put("EW", measure.getExternalWL());
			model.put("INV", decimalFormat.format(measure.getInvestment() * .001));
			model.put("LT", measure.getLifetime());
			model.put("IM", measure.getInternalMaintenance());
			model.put("EM", measure.getExternalMaintenance());
			model.put("RM", decimalFormat.format(measure.getRecurrentInvestment() * .001));
			model.put("CS", decimalFormat.format(measure.getCost() * .001));
			model.put("PH", measure.getPhase().getNumber());
			model.put("PHB", dateFormat.format(measure.getPhase().getBeginDate()));
			model.put("PHE", dateFormat.format(measure.getPhase().getEndDate()));
			model.put("Comment", measure.getComment());
			model.put("ToDo", measure.getToDo());
			model.put("Owner", measure.getResponsible());
			if (measure instanceof AbstractNormalMeasure)
				model.put("ToCheck", ((AbstractNormalMeasure) measure).getToCheck());
			else
				model.put("ToCheck", "N/A");

			email.setBody(
					InstanceManager.getServiceEmailSender().processTemplateIntoString(template.getTemplate(), model));

			InstanceManager.getServiceEmailSender().send(email);

			handler.setProgress(min + (int) ((++current / (double) size) * (maxProgess - min)));

			// Todo: save email and use the id as ticket ID.

			measure.setTicket("ts@email-" + System.currentTimeMillis());

			Thread.sleep(template.getInternalTime());
		}
		return false;
	}

	/**
	 * Cleans up the resources used by the worker.
	 * This method sets the worker's status to not working, closes the client connection,
	 * clears the ticketing form, and sets the finished timestamp.
	 */
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
