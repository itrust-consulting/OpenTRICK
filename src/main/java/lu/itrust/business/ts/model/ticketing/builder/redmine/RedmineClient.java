/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.builder.redmine;

import java.io.Closeable;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.NotAuthorizedException;
import com.taskadapter.redmineapi.NotFoundException;
import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.ProjectManager;
import com.taskadapter.redmineapi.RedmineAuthenticationException;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueRelation;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.internal.ResultsWrapper;

import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.ts.model.ticketing.TicketingField;
import lu.itrust.business.ts.model.ticketing.TicketingPageable;
import lu.itrust.business.ts.model.ticketing.TicketingProject;
import lu.itrust.business.ts.model.ticketing.TicketingTask;
import lu.itrust.business.ts.model.ticketing.builder.Client;
import lu.itrust.business.ts.model.ticketing.builder.ClientBuilder;
import lu.itrust.business.ts.model.ticketing.impl.Comment;
import lu.itrust.business.ts.model.ticketing.impl.TicketingPageableImpl;
import lu.itrust.business.ts.model.ticketing.impl.redmine.RedmineCustomField;
import lu.itrust.business.ts.model.ticketing.impl.redmine.RedmineIssueLink;
import lu.itrust.business.ts.model.ticketing.impl.redmine.RedmineProject;
import lu.itrust.business.ts.model.ticketing.impl.redmine.RedmineTask;

/**
 * The RedmineClient class is responsible for connecting to a Redmine ticketing system and performing various operations such as creating issues and checking task ownership.
 * It implements the Client interface.
 */
public class RedmineClient implements Client {

	private static final String SOMETHING_WRONG_WITH_THE_TICKETING_SYSTEM = "Something wrong with the ticketing system";
	private static final String ERROR_TASK_EXTERNAL = "error.task.external";
	private static final String SELECTED_PROJECT_CANNOT_BE_FOUND = "Selected project cannot be found!";
	private static final String ERROR_PROJECT_NOT_FOUND = "error.project.not.found";
	private static final String PLEASE_CHECK_YOUR_HAVE_PROPER_PERSMISSIONS = "Please check your have proper permissions";
	private static final String ERROR_TASK_AUTHORISATION = "error.task.authorisation";
	private static final String PLEASE_CHECK_YOUR_TICKETING_SYSTEM_CREDENTIALS = "Please check your ticketing system credentials";
	private static final String ERROR_TASK_AUTHENTICATION = "error.task.authentication";
	private static final String ISSUE_LINK_FORMAT = "%s/issues/%d";
	private static final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);

	private RedmineManager manager;

	private HttpClient client;

	private String url;

	/**
	 * 
	 */
	public RedmineClient() {
		DECIMAL_FORMAT.setMaximumFractionDigits(2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		this.manager = null;
		if (client != null) {
			try {
				((Closeable) client).close();
			} finally {
				client = null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.model.ticketing.builder.Client#connect(java.util.Map)
	 */
	@Override
	public boolean connect(Map<String, Object> settings) {
		final String myURL = settings.getOrDefault("url", "").toString();
		final String username = settings.getOrDefault("username", "").toString();
		return username.isEmpty() ? connect(myURL, settings.getOrDefault("token", "").toString())
				: connect(myURL, username, settings.getOrDefault("password", "").toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.ticketing.builder.Client#connect(java.lang.
	 * String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean connect(String url, String username, String password) {
		try {
			if (client == null)
				client = HttpClientBuilder.create().build();
			setUrl(url);
			this.manager = RedmineManagerFactory.createWithUserAuth(getUrl(), username, password, client);
			return this.manager.getUserManager().getCurrentUser() != null;
		} catch (RedmineException e) {
			TrickLogManager.Persist(e);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.model.ticketing.builder.Client#isBelongTask(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public boolean isBelongTask(String idProject, String taskId) {
		try {
			Issue issue = this.manager.getIssueManager().getIssueById(Integer.parseInt(taskId));
			return !(issue == null || issue.getParentId() == null)
					&& Integer.parseInt(idProject) == issue.getParentId();
		} catch (NotFoundException e) {
			return false;
		} catch (RedmineAuthenticationException e) {
			throw new TrickException(ERROR_TASK_AUTHENTICATION, PLEASE_CHECK_YOUR_TICKETING_SYSTEM_CREDENTIALS, e);
		} catch (NotAuthorizedException e) {
			throw new TrickException(ERROR_TASK_AUTHORISATION, PLEASE_CHECK_YOUR_HAVE_PROPER_PERSMISSIONS, e);
		} catch (RedmineException e) {
			throw new TrickException(ERROR_TASK_EXTERNAL, SOMETHING_WRONG_WITH_THE_TICKETING_SYSTEM, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.model.ticketing.builder.Client#createIssue(java.lang.
	 * String, lu.itrust.business.ts.model.ticketing.TicketingTask)
	 */
	@Override
	public boolean createIssue(String idProject, TicketingTask task) {
		if (manager == null)
			return true;
		try {
			Project project = manager.getProjectManager().getProjectById(Integer.parseInt(idProject));
			Tracker tracker = project.getTrackerByName(task.getType());
			if (tracker == null)
				tracker = project.getTrackers().stream().findFirst().orElse(null);
			Issue issue = new Issue(manager.getTransport(), project.getId(), task.getName());
			issue.setDescription(task.getDescription());
			assignedTicket(project, task.getAssignee(), issue);
			if (task.getDue() != null)
				issue.setDueDate(task.getDue());
			if (task.getProgress() > 0)
				issue.setDoneRatio(task.getProgress());
			if (task.getStatus() != null)
				issue.setStatusName(task.getStatus());
			if (tracker != null)
				issue.setTracker(tracker);
			Issue persisted = issue.create();
			task.setId(persisted.getId().toString());
			return false;
		} catch (RedmineAuthenticationException e) {
			throw new TrickException(ERROR_TASK_AUTHENTICATION, PLEASE_CHECK_YOUR_TICKETING_SYSTEM_CREDENTIALS, e);
		} catch (NotFoundException e) {
			throw new TrickException(ERROR_PROJECT_NOT_FOUND, SELECTED_PROJECT_CANNOT_BE_FOUND, e);
		} catch (NotAuthorizedException e) {
			throw new TrickException(ERROR_TASK_AUTHORISATION, PLEASE_CHECK_YOUR_HAVE_PROPER_PERSMISSIONS, e);
		} catch (RedmineException e) {
			throw new TrickException(ERROR_TASK_EXTERNAL, SOMETHING_WRONG_WITH_THE_TICKETING_SYSTEM, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.model.ticketing.builder.Client#createIssues(java.lang.
	 * String, java.lang.String, java.util.Collection, java.util.Collection,
	 * lu.itrust.business.ts.messagehandler.MessageHandler, int)
	 */
	@Override
	public boolean createIssues(String projectId, String trackerName, String language, Collection<Measure> measures,
			Collection<Measure> updateMeasures, ValueFactory factory, MessageHandler handler,
			int maxProgess) {
		if (manager == null)
			throw new TrickException("error.500.message", "Internal error");
		try {
			Boolean error = false;
			final ProjectManager projectManager = manager.getProjectManager();
			final IssueManager issueManager = manager.getIssueManager();
			final Project project = projectManager.getProjectByKey(projectId);
			Tracker tracker = project.getTrackerByName(trackerName);
			if (tracker == null)
				tracker = project.getTrackers().stream().findFirst().orElse(null);
			int min = handler.getProgress();
			int size = measures.size() + updateMeasures.size();
			int current = 0;
			for (Measure measure : measures) {
				if (StringUtils.isEmpty(measure.getToDo()) || StringUtils.isBlank(measure.getToDo())) {
					final MeasureDescription description = measure.getMeasureDescription();
					handler.update("error.ticket.measure.no.todo",
							String.format("Task for (%s - %s) cannot be created, please add a todo and try again!",
									description.getStandard().getName(), description.getReference()),
							0, description.getStandard().getName(), description.getReference());
					error = true;
				} else {
					final Issue issue = new Issue(manager.getTransport(), project.getId(),
							StringUtils.abbreviate(measure.getToDo(), 255));
					issue.setTracker(tracker);
					issue.setDescription(generateDescription(measure, language, factory));
					issue.setEstimatedHours((float) ((measure.getInternalWL() + measure.getExternalWL()) * 8.0));
					issue.setStartDate(measure.getPhase().getBeginDate());
					issue.setDueDate(measure.getPhase().getEndDate());
					assignedTicket(project, measure.getResponsible(), issue);
					measure.setTicket(issue.create().getId().toString());
				}
				handler.setProgress(min + (int) ((++current / (double) size) * (maxProgess - min)));
				InstanceManager.getServiceTaskFeedback().send(handler);
			}

			if (!updateMeasures.isEmpty()) {
				handler.update("info.updating.tickets", "Updating tasks", handler.getProgress());
				final Map<String, String> parameters = new HashMap<>(2);
				parameters.put("project_key", projectId);
				for (Measure measure : updateMeasures) {
					final MeasureDescription description = measure.getMeasureDescription();
					parameters.put("issue_id", measure.getTicket());
					final Issue issue = issueManager.getIssues(parameters).getResults().stream().findFirst()
							.orElse(null);
					if (issue == null) {
						handler.update("error.ticket.not_found",
								String.format("Task for (%s - %s) cannot be found", description.getStandard().getName(),
										description.getReference()),
								0,
								description.getStandard().getName(), description.getReference());
						error = true;
					} else {
						issue.setSubject(StringUtils.abbreviate(measure.getToDo(), 255));
						issue.setDescription(generateDescription(measure, language, factory));
						issue.setEstimatedHours((float) ((measure.getInternalWL() + measure.getExternalWL()) * 8.0));
						issue.setStartDate(measure.getPhase().getBeginDate());
						issue.setDueDate(measure.getPhase().getEndDate());
						issue.setTransport(manager.getTransport());
						assignedTicket(project, measure.getResponsible(), issue);
						issue.update();
					}
					handler.setProgress(min + (int) ((++current / (double) size) * (maxProgess - min)));
					InstanceManager.getServiceTaskFeedback().send(handler);
				}
			}
			return error;
		} catch (RedmineAuthenticationException e) {
			throw new TrickException(ERROR_TASK_AUTHENTICATION, PLEASE_CHECK_YOUR_TICKETING_SYSTEM_CREDENTIALS, e);
		} catch (NotFoundException e) {
			throw new TrickException(ERROR_PROJECT_NOT_FOUND, SELECTED_PROJECT_CANNOT_BE_FOUND, e);
		} catch (NotAuthorizedException e) {
			throw new TrickException(ERROR_TASK_AUTHORISATION, PLEASE_CHECK_YOUR_HAVE_PROPER_PERSMISSIONS, e);
		} catch (RedmineException e) {
			throw new TrickException(ERROR_TASK_EXTERNAL, SOMETHING_WRONG_WITH_THE_TICKETING_SYSTEM, e);
		}
	}

	private void assignedTicket(final Project project, String assignee, final Issue issue) throws RedmineException {
		if (StringUtils.isEmpty(issue.getAssigneeName())
				&& !StringUtils.isEmpty(assignee)) {
			final String cleanedAssignee = StringUtils.stripAccents(assignee);
			//Todo: Fork the lib to fixed this.
			manager.setObjectsPerPage(1000);//Make sure to retrieve 1000 entries 
			manager.getProjectManager().getProjectMembers(project.getIdentifier()).stream()
					.filter(e -> cleanedAssignee.equalsIgnoreCase(StringUtils.stripAccents(e.getGroupName()))
							|| cleanedAssignee.equalsIgnoreCase(StringUtils.stripAccents(e.getUserName())))
					.forEach(e -> {
						if (e.getUserId() == null) {
							issue.setAssigneeId(e.getGroupId());
							issue.setAssigneeName(e.getGroupName());
						} else {
							issue.setAssigneeId(e.getUserId());
							issue.setAssigneeName(e.getUserName());
						}
					});
		}
	}

	private String generateDescription(Measure measure, String language, ValueFactory factory) {
		final List<String> builder = new LinkedList<>();
		final MeasureDescription description = measure.getMeasureDescription();
		final MeasureDescriptionText descriptionText = measure.getMeasureDescription()
				.getMeasureDescriptionTextByAlpha2(language);
		if (measure.getToDo().length() > 255)
			builder.add(measure.getToDo() + "\n");

		if (!(measure.getComment() == null || measure.getComment().isEmpty()))
			builder.add(measure.getComment() + "\n");

		builder.add(String.format("%s - %s: %s", description.getStandard().getName(), description.getReference(),
				descriptionText.getDomain()) + "\n");
		builder.add(descriptionText.getDescription() + "\n");
		builder.add(String.format("IR: %d, IW: %s, EW: %s, INV: %s, LT: %s, IM: %s, EM: %s, RM: %s, PH: %d, Resp: %s",
				(int) measure.getImplementationRateValue(factory),
				DECIMAL_FORMAT.format(measure.getInternalWL()), DECIMAL_FORMAT.format(measure.getExternalWL()),
				DECIMAL_FORMAT.format(measure.getInvestment() * .001),
				DECIMAL_FORMAT.format(measure.getLifetime()), DECIMAL_FORMAT.format(measure.getInternalMaintenance()),
				DECIMAL_FORMAT.format(measure.getExternalMaintenance()),
				DECIMAL_FORMAT.format(measure.getRecurrentInvestment() * .001), measure.getPhase().getNumber(),
				measure.getResponsible()));
		return String.join("\n", builder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.model.ticketing.builder.Client#findTaskById(java.lang.
	 * String)
	 */
	@Override
	public TicketingTask findTaskById(String idTask) {
		try {
			if (manager == null)
				return null;
			return loadIssue(manager.getIssueManager().getIssueById(Integer.parseInt(idTask), Include.relations,
					Include.journals, Include.children));
		} catch (NotFoundException e) {
			return null;
		} catch (NotAuthorizedException e) {
			throw new TrickException(ERROR_TASK_AUTHORISATION, PLEASE_CHECK_YOUR_HAVE_PROPER_PERSMISSIONS, e);
		} catch (RedmineAuthenticationException e) {
			throw new TrickException(ERROR_TASK_AUTHENTICATION, PLEASE_CHECK_YOUR_TICKETING_SYSTEM_CREDENTIALS, e);
		} catch (RedmineException e) {
			throw new TrickException(ERROR_TASK_EXTERNAL, SOMETHING_WRONG_WITH_THE_TICKETING_SYSTEM, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.model.ticketing.builder.Client#findTaskByIdAndProjectId
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public TicketingTask findTaskByIdAndProjectId(String idTask, String idProject) {
		try {
			if (manager == null)
				return null;
			final Map<String, String> parameters = new HashMap<>(3);
			final Project project = manager.getProjectManager().getProjectByKey(idProject);
			parameters.put("project_id", project.getId() + "");
			parameters.put("status_id", "*");
			parameters.put("issue_id", idTask);
			return manager.getIssueManager().getIssues(parameters).getResults().stream().map(e -> loadIssue(e))
					.findAny().orElse(null);
		} catch (NotFoundException e) {
			return null;
		} catch (NotAuthorizedException e) {
			throw new TrickException(ERROR_TASK_AUTHORISATION, PLEASE_CHECK_YOUR_HAVE_PROPER_PERSMISSIONS, e);
		} catch (RedmineAuthenticationException e) {
			throw new TrickException(ERROR_TASK_AUTHENTICATION, PLEASE_CHECK_YOUR_TICKETING_SYSTEM_CREDENTIALS, e);
		} catch (RedmineException e) {
			throw new TrickException(ERROR_TASK_EXTERNAL, SOMETHING_WRONG_WITH_THE_TICKETING_SYSTEM, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.model.ticketing.builder.Client#findProjectById(java.
	 * lang.String)
	 */
	@Override
	public TicketingProject findProjectById(String idProject) {
		try {
			return manager == null ? null : buildProject(manager.getProjectManager().getProjectByKey(idProject));
		} catch (NotFoundException e) {
			return null;
		} catch (NotAuthorizedException e) {
			throw new TrickException(ERROR_TASK_AUTHORISATION, PLEASE_CHECK_YOUR_HAVE_PROPER_PERSMISSIONS, e);
		} catch (RedmineAuthenticationException e) {
			throw new TrickException(ERROR_TASK_AUTHENTICATION, PLEASE_CHECK_YOUR_TICKETING_SYSTEM_CREDENTIALS, e);
		} catch (RedmineException e) {
			throw new TrickException(ERROR_TASK_EXTERNAL, SOMETHING_WRONG_WITH_THE_TICKETING_SYSTEM, e);
		}
	}

	private static final TicketingProject buildProject(Project project) {
		return new RedmineProject(project.getIdentifier(), project.getName(), project.getDescription());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.ticketing.builder.Client#findProjects()
	 */
	@Override
	public List<TicketingProject> findProjects() {
		try {
			return manager == null ? Collections.emptyList()
					: manager.getProjectManager().getProjects().stream().map(RedmineClient::buildProject)
							.sorted((p0, p1) -> NaturalOrderComparator.compareTo(p0.getName(), p1.getName()))
							.collect(Collectors.toList());
		} catch (RedmineAuthenticationException e) {
			throw new TrickException(ERROR_TASK_AUTHENTICATION, PLEASE_CHECK_YOUR_TICKETING_SYSTEM_CREDENTIALS, e);
		} catch (NotAuthorizedException e) {
			throw new TrickException(ERROR_TASK_AUTHORISATION, PLEASE_CHECK_YOUR_HAVE_PROPER_PERSMISSIONS, e);
		} catch (RedmineException e) {
			throw new TrickException(ERROR_TASK_EXTERNAL, SOMETHING_WRONG_WITH_THE_TICKETING_SYSTEM, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.model.ticketing.builder.Client#findTaskByProjectId(java
	 * .lang.String)
	 */
	@Override
	public List<TicketingTask> findTaskByProjectId(String idProject) {
		try {
			if (manager == null)
				return Collections.emptyList();
			final Map<String, String> parameters = new HashMap<>(4);
			final Project project = manager.getProjectManager().getProjectByKey(idProject);
			parameters.put("project_id", project.getId() + "");
			parameters.put("status_id", "*");
			parameters.put("sort", "id:desc");
			parameters.put("limit", Integer.MAX_VALUE + "");
			return manager.getIssueManager().getIssues(parameters).getResults().stream().map(e -> loadIssue(e))
					.collect(Collectors.toList());
		} catch (RedmineAuthenticationException e) {
			throw new TrickException(ERROR_TASK_AUTHENTICATION, PLEASE_CHECK_YOUR_TICKETING_SYSTEM_CREDENTIALS, e);
		} catch (NotAuthorizedException e) {
			throw new TrickException(ERROR_TASK_AUTHORISATION, PLEASE_CHECK_YOUR_HAVE_PROPER_PERSMISSIONS, e);
		} catch (RedmineException e) {
			throw new TrickException(ERROR_TASK_EXTERNAL, SOMETHING_WRONG_WITH_THE_TICKETING_SYSTEM, e);
		}
	}

	private RedmineTask loadIssue(Issue issue) {
		final RedmineTask task = new RedmineTask(issue.getId().toString(), issue.getSubject(),
				issue.getTracker() == null ? null : issue.getTracker().getName(),
				issue.getStatusName(), issue.getDescription(), String.format(ISSUE_LINK_FORMAT, url, issue.getId()),
				issue.getDoneRatio() == null ? 0 : issue.getDoneRatio());
		task.setAssignee(issue.getAssigneeName());
		task.setReporter(issue.getAuthorName());
		task.setCreated(issue.getCreatedOn());
		task.setUpdated(issue.getUpdatedOn());
		task.setDue(issue.getDueDate());
		task.setPriority(issue.getPriorityText());
		task.setCustomFields(
				issue.getCustomFields().stream()
						.map(cf -> new RedmineCustomField(cf.getId().toString(), cf.getName(),
								cf.isMultiple() ? cf.getValues() : cf.getValue()))
						.collect(Collectors.toMap(TicketingField::getName, Function.identity())));

		task.setComments(issue.getJournals().stream().filter(c -> !(c.getNotes() == null || c.getNotes().isEmpty()))
				.sorted((j1, j2) -> j1.getCreatedOn().compareTo(j2.getCreatedOn()))
				.map(j -> new Comment(j.getId().toString(), j.getUser() == null ? null : j.getUser().getFullName(),
						j.getCreatedOn(), j.getNotes()))
				.collect(Collectors.toList()));
		task.setSubTask(issue
				.getChildren().stream()
				.map(st -> new RedmineTask(st.getId().toString(), st.getSubject(),
						st.getTracker() == null ? null : st.getTracker().getName(),
						st.getStatusName(), st.getDescription(), String.format(ISSUE_LINK_FORMAT, url, st.getId()),
						st.getDoneRatio() == null ? 0 : st.getDoneRatio()))
				.collect(Collectors.toList()));
		task.setIssueLinks(issue.getRelations().parallelStream().map(r -> load(r)).filter(i -> i != null)
				.sorted((e1, e2) -> NaturalOrderComparator.compareTo(e1.getName(), e2.getName()))
				.collect(Collectors.toList()));
		return task;
	}

	private TicketingField load(IssueRelation relation) {
		try {
			final Map<String, String> parameters = new HashMap<>(3);
			parameters.put("limit", "1");
			parameters.put("status_id", "*");
			parameters.put("issue_id", relation.getIssueId() + "");
			return manager.getIssueManager().getIssues(parameters).getResults().stream()
					.map(i -> new RedmineIssueLink(i.getId().toString(), i.getSubject(), i.getTracker().getName(),
							String.format(ISSUE_LINK_FORMAT, url, i.getId())))
					.findAny()
					.orElse(null);
		} catch (RedmineException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.ticketing.builder.Client#
	 * findOpenedByIdsAndProjectId(java.lang.String, java.util.Collection)
	 */
	@Override
	public List<TicketingTask> findOpenedByIdsAndProjectId(String projectid, Collection<String> ids) {
		return findByIdsAndProjectId(projectid, ids, false);
	}

	private List<TicketingTask> findByIdsAndProjectId(String projectid, Collection<String> ids, boolean all) {
		try {
			if (manager == null || ids == null || ids.isEmpty())
				return Collections.emptyList();
			final Project project = manager.getProjectManager().getProjectByKey(projectid);
			return ids.parallelStream().map(i -> {
				try {
					Issue issue = manager.getIssueManager().getIssueById(Integer.parseInt(i), Include.journals,
							Include.relations, Include.children);
					return !issue.getProjectId().equals(project.getId()) || !(all || issue.getClosedOn() == null) ? null
							: loadIssue(issue);
				} catch (RedmineException e) {
					return null;
				}
			}).filter(i -> i != null).collect(Collectors.toList());
		} catch (RedmineAuthenticationException e) {
			throw new TrickException(ERROR_TASK_AUTHENTICATION, PLEASE_CHECK_YOUR_TICKETING_SYSTEM_CREDENTIALS, e);
		} catch (NotAuthorizedException e) {
			throw new TrickException(ERROR_TASK_AUTHORISATION, PLEASE_CHECK_YOUR_HAVE_PROPER_PERSMISSIONS, e);
		} catch (RedmineException e) {
			throw new TrickException(ERROR_TASK_EXTERNAL, SOMETHING_WRONG_WITH_THE_TICKETING_SYSTEM, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.model.ticketing.builder.Client#findByIdsAndProjectId(
	 * java.lang.String, java.util.Collection)
	 */
	@Override
	public List<TicketingTask> findByIdsAndProjectId(String projectId, Collection<String> ids) {
		return findByIdsAndProjectId(projectId, ids, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.ticketing.builder.Client#
	 * findOtherTasksByProjectId(java.lang.String, java.util.Collection, int, int)
	 */
	@Override
	public TicketingPageable<TicketingTask> findOtherTasksByProjectId(String projectId, Collection<String> excludes,
			int startIndex, int size) {
		try {
			if (size < 1)
				size = Integer.MAX_VALUE;
			if (manager == null)
				return new TicketingPageableImpl<>(startIndex, size, Collections.emptyList());
			final Project project = manager.getProjectManager().getProjectByKey(projectId);
			if (!(excludes == null || excludes.isEmpty()))
				return findOtherTasks(project.getId() + "", excludes, startIndex, size);
			final Params parameters = new Params();
			parameters.add("status_id", "*");
			parameters.add("limit", size + "");
			parameters.add("project_id", project.getId() + "");
			parameters.add("offset", startIndex + "");
			parameters.add("sort", "id:desc");
			ResultsWrapper<Issue> wrapper = manager.getIssueManager().getIssues(parameters);
			return new TicketingPageableImpl<>(startIndex + size + 1, size,
					wrapper.getResults().stream().map(this::loadIssue).collect(Collectors.toList()));
		} catch (RedmineAuthenticationException e) {
			throw new TrickException(ERROR_TASK_AUTHENTICATION, PLEASE_CHECK_YOUR_TICKETING_SYSTEM_CREDENTIALS, e);
		} catch (NotAuthorizedException e) {
			throw new TrickException(ERROR_TASK_AUTHORISATION, PLEASE_CHECK_YOUR_HAVE_PROPER_PERSMISSIONS, e);
		} catch (RedmineException e) {
			throw new TrickException(ERROR_TASK_EXTERNAL, SOMETHING_WRONG_WITH_THE_TICKETING_SYSTEM, e);
		}
	}

	private TicketingPageable<TicketingTask> findOtherTasks(String projectId, Collection<String> excludes,
			int startIndex, int size) throws RedmineException {
		final TicketingPageable<TicketingTask> tasks = new TicketingPageableImpl<>(startIndex, size);
		final Map<String, String> parameters = new LinkedHashMap<>(5);
		final Map<Integer, Boolean> mapperExcludes = excludes.stream().map(Integer::parseInt)
				.collect(Collectors.toMap(Function.identity(), e -> false, (e1, e2) -> e1));
		parameters.put("status_id", "*");
		parameters.put("limit", size + "");
		parameters.put("project_id", projectId);
		parameters.put("offset", startIndex + "");
		parameters.put("sort", "id:desc");
		while (true) {
			final List<Issue> issues = manager.getIssueManager().getIssues(parameters).getResults();
			if (issues.isEmpty())
				break;
			for (int i = 0; i < issues.size(); i++) {
				final Issue issue = issues.get(i);
				tasks.increase(1);
				if (!mapperExcludes.containsKey(issue.getId())) {
					tasks.add(loadIssue(issue));
					if (tasks.size() >= size)
						break;
				}
			}
			if (tasks.size() >= size)
				break;
			parameters.put("offset", tasks.getOffset() + "");
		}
		return tasks;
	}

	/**
	 * Returns the URL of the Redmine client.
	 *
	 * @return the URL of the Redmine client
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL for the Redmine client.
	 * 
	 * @param url the URL to set
	 */
	public void setUrl(String url) {
		this.url = ClientBuilder.getURL(url);
	}

	/**
	 * Connects to the Redmine server using the specified URL and token.
	 * 
	 * @param url   the URL of the Redmine server
	 * @param token the authentication token for the Redmine server
	 * @return true if the connection is successful, false otherwise
	 */
	@Override
	public boolean connect(String url, String token) {
		try {
			if (client == null)
				client = HttpClientBuilder.create().build();
			setUrl(url);
			this.manager = RedmineManagerFactory.createWithApiKey(getUrl(), token, client);
			return this.manager.getUserManager().getCurrentUser() != null;
		} catch (RedmineException e) {
			TrickLogManager.Persist(e);
			return false;
		}
	}

}
