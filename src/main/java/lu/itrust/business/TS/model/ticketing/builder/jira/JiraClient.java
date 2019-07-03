/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.builder.jira;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicPriority;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.Resolution;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.model.ticketing.TicketingPageable;
import lu.itrust.business.TS.model.ticketing.TicketingProject;
import lu.itrust.business.TS.model.ticketing.TicketingTask;
import lu.itrust.business.TS.model.ticketing.builder.Client;
import lu.itrust.business.TS.model.ticketing.builder.ClientBuilder;
import lu.itrust.business.TS.model.ticketing.helper.CommentComparator;
import lu.itrust.business.TS.model.ticketing.impl.Comment;
import lu.itrust.business.TS.model.ticketing.impl.TicketingPageableImpl;
import lu.itrust.business.TS.model.ticketing.impl.jira.JiraCustomField;
import lu.itrust.business.TS.model.ticketing.impl.jira.JiraIssueLink;
import lu.itrust.business.TS.model.ticketing.impl.jira.JiraProject;
import lu.itrust.business.TS.model.ticketing.impl.jira.JiraTask;

/**
 * @author eomar
 *
 */
public class JiraClient implements Client {

	private static final String LOAD_BY_PROJECT_KEY = "project=%s order by key";

	private static final String PROJECT_S_AND_STATUS_OPEN_AND_KEY_NOT_IN_S = "project=%s and status=open and key not in (%s) order by key";

	private static final String PROJECT_S_AND_STATUS_OPEN_AND_KEY_IN_S = "project=%s and status=open and key in (%s)";

	private static final String PROJECT_S_AND_KEY_IN_S = "project=%s and key in (%s)";

	private JiraRestClient restClient;

	private ObjectMapper objectMapper;

	private CommentComparator comparator = new CommentComparator();

	/**
	 * 
	 */
	public JiraClient() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		if (restClient != null)
			restClient.close();
		restClient = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.builder.Client#connect(java.util.
	 * Map)
	 */
	@Override
	public boolean connect(Map<String, Object> settings) {
		final String url = settings.getOrDefault("url", "").toString();
		final String username = settings.getOrDefault("username", "").toString();
		return username.isEmpty() ? connect(url, settings.getOrDefault("token", "").toString()) : connect(url, username, settings.getOrDefault("password", "").toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.builder.Client#connect(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public boolean connect(String url, String username, String passward) {
		try {
			final JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
			this.restClient = factory.createWithBasicHttpAuthentication(new URI(ClientBuilder.getURL(url)), username, passward);
			return this.restClient.getUserClient().getUser(username).claim() != null;
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.builder.Client#isBelongTask(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public boolean isBelongTask(String idProject, String taskId) {
		Issue issue = restClient.getIssueClient().getIssue(taskId).claim();
		return issue != null && issue.getProject().getKey().toString().equals(idProject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.builder.Client#findTaskById(java.
	 * lang.String)
	 */
	@Override
	public TicketingTask findTaskById(String idTask) {
		return loadTask(restClient.getIssueClient().getIssue(idTask).claim());
	}

	private JiraTask loadTask(Issue issue) {
		IssueType type = issue.getIssueType();
		Status status = issue.getStatus();
		Resolution resolution = issue.getResolution();
		BasicPriority priority = issue.getPriority();
		User reporter = issue.getReporter(), assignee = issue.getAssignee();
		IssueField progress = issue.getField("progress");
		DateTime created = issue.getCreationDate(), due = issue.getDueDate(), update = issue.getUpdateDate();
		JiraTask task = new JiraTask(issue.getKey(), issue.getSummary(), type.getName(), status.getName(), issue.getDescription(), 0);

		if (reporter != null)
			task.setReporter(reporter.getDisplayName());
		if (assignee != null)
			task.setAssignee(assignee.getDisplayName());
		if (created != null)
			task.setCreated(created.toDate());
		if (due != null)
			task.setDue(due.toDate());
		if (update != null)
			task.setUpdated(update.toDate());
		if (priority != null)
			task.setPriority(priority.getName());
		if (progress != null) {
			try {
				JsonNode node = getObjectMapper().readTree(progress.getValue().toString());
				if (node.has("percent"))
					task.setProgress(node.get("percent").asInt(0));
			} catch (Exception e) {
				TrickLogManager.Persist(e);
			}
		}

		task.setComments(new LinkedList<>());
		task.setIssueLinks(new LinkedList<>());
		task.setCustomFields(new LinkedHashMap<>());
		task.setSubTask(new LinkedList<>());

		Iterable<Subtask> subTasks = issue.getSubtasks();
		Iterable<IssueLink> issueLinks = issue.getIssueLinks();

		if (resolution != null)
			task.getCustomFields().put("Resolution", new JiraCustomField(resolution.getId().toString(), "Resolution", resolution.getName()));

		issue.getComments().forEach(comment -> {
			BasicUser author = comment.getAuthor();
			task.getComments().add(new Comment(comment.getId().toString(), author == null ? null : author.getDisplayName(), comment.getCreationDate().toDate(), comment.getBody()));
		});

		issue.getWorklogs().forEach(workerLog -> {
			BasicUser author = workerLog.getUpdateAuthor();
			task.getComments().add(new Comment(null, author == null ? null : author.getDisplayName(), workerLog.getCreationDate().toDate(), workerLog.getComment()));
		});

		task.getComments().sort(comparator);

		if (issueLinks != null) {
			try {
				for (IssueLink issueLink : issueLinks)
					task.getIssueLinks()
							.add(new JiraIssueLink(issueLink.getTargetIssueKey(), issueLink.getIssueLinkType().getDescription(), issueLink.getTargetIssueUri().toURL().toString()));
			} catch (MalformedURLException e) {
				TrickLogManager.Persist(e);
			}
		}

		if (subTasks != null)
			subTasks.forEach(subIssue -> task.getSubTasks()
					.add(new JiraTask(subIssue.getIssueKey(), subIssue.getSummary(), subIssue.getIssueType().getName(), subIssue.getStatus().getName())));

		return task;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#findProjectById(java
	 * .lang.String)
	 */
	@Override
	public TicketingProject findProjectById(String idProject) {
		return buildProject(restClient.getProjectClient().getProject(idProject).claim());
	}

	private TicketingProject buildProject(Project project) {
		return new JiraProject(project.getKey(), project.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.builder.Client#findProjects()
	 */
	@Override
	public List<TicketingProject> findProjects() {
		final List<TicketingProject> projects = new LinkedList<>();
		restClient.getProjectClient().getAllProjects().claim().forEach(project -> projects.add(new JiraProject(project.getKey(), project.getName())));
		projects.sort((p0, p1)-> NaturalOrderComparator.compareTo(p0.getName(), p1.getName()));
		return projects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#findTaskByProjectId(
	 * java.lang.String)
	 */
	@Override
	public List<TicketingTask> findTaskByProjectId(String idProject) {
		List<TicketingTask> tasks = new LinkedList<>();
		restClient.getSearchClient().searchJql(String.format(LOAD_BY_PROJECT_KEY, idProject)).claim().getIssues().forEach(issue -> tasks.add(loadTask(issue)));
		return tasks;
	}

	@Override
	public boolean createIssue(String idProject, TicketingTask task) {
		if (restClient == null)
			return false;
		Project project = restClient.getProjectClient().getProject(idProject).claim();
		if (project == null)
			return false;
		IssueType issueType = null;
		for (IssueType type : project.getIssueTypes()) {
			if (type.getName().equals(task.getType())) {
				issueType = type;
				break;
			} else if (issueType == null)
				issueType = type;
		}
		IssueInputBuilder builder = new IssueInputBuilder(project, issueType, task.getName());
		builder.setDescription(task.getDescription());
		builder.setReporterName(task.getReporter());
		if (task.getAssignee() != null)
			builder.setAssigneeName(task.getAssignee());
		if (task.getDue() != null)
			builder.setDueDate(new DateTime(task.getDue().getTime()));
		BasicIssue issue = restClient.getIssueClient().createIssue(builder.build()).claim();
		task.setId(issue.getKey());
		return true;
	}

	@Override
	public List<TicketingTask> findOpenedByIdsAndProjectId(String idProject, Collection<String> keyIssues) {
		return findAllByProjectId(idProject, keyIssues, true);
	}

	private List<TicketingTask> findAllByProjectId(String idProject, Collection<String> keyIssues, boolean openOnly) {
		List<TicketingTask> tasks = new LinkedList<>();
		String include = "";
		for (String key : keyIssues)
			include += (include.isEmpty() ? "" : ", ") + key;
		Set<String> options = new HashSet<>(3);
		options.add("*navigable");
		options.add("comment");
		options.add("worklog");
		restClient.getSearchClient()
				.searchJql(String.format(openOnly ? PROJECT_S_AND_STATUS_OPEN_AND_KEY_IN_S : PROJECT_S_AND_KEY_IN_S, idProject, include), keyIssues.size(), 0, options).claim()
				.getIssues().forEach(issue -> tasks.add(loadTask(issue)));
		return tasks;
	}

	@Override
	public TicketingTask findTaskByIdAndProjectId(String idTask, String idProject) {
		Issue issue = restClient.getIssueClient().getIssue(idTask).claim();
		if (!issue.getProject().getKey().equals(idProject))
			throw new TrickException("error.task.not_found", "Task cannot be found");
		return loadTask(issue);
	}

	@Override
	public boolean createIssues(String idProject, String language, Collection<Measure> measures, Collection<Measure> updateMeasures,ValueFactory factory, MessageHandler handler, int maxProgess) {
		if (restClient == null)
			throw new TrickException("error.500.message", "Internal error");
		Project project = restClient.getProjectClient().getProject(idProject).claim();
		if (project == null)
			throw new TrickException("error.project.not_found", "Project cannot be found");
		IssueType issueType = null;
		for (IssueType type : project.getIssueTypes()) {
			if (type.getName().equalsIgnoreCase("Task")) {
				issueType = type;
				break;
			} else if (issueType == null)
				issueType = type;
		}
		int min = handler.getProgress(), size = measures.size() + updateMeasures.size(), current = 0;
		Map<String, Object> estimations = new HashMap<>(1);
		for (Measure measure : measures) {
			MeasureDescription description = measure.getMeasureDescription();
			MeasureDescriptionText descriptionText = description.getMeasureDescriptionTextByAlpha2(language);
			IssueInputBuilder builder = new IssueInputBuilder(project, issueType,
					String.format("%s - %s: %s", description.getStandard().getLabel(), description.getReference(), descriptionText.getDomain()));
			builder.setDescription(measure.getToDo());
			estimations.put("originalEstimate", (measure.getInternalWL() + measure.getExternalWL()) + "d");
			builder.setFieldInput(new FieldInput(IssueFieldId.TIMETRACKING_FIELD, new ComplexIssueInputFieldValue(estimations)));
			builder.setDueDate(new DateTime(measure.getPhase().getEndDate().getTime()));
			BasicIssue issue = restClient.getIssueClient().createIssue(builder.build()).claim();
			measure.setTicket(issue.getKey());
			handler.setProgress(min + (int) ((++current / (double) size) * (maxProgess - min)));
		}

		if (!updateMeasures.isEmpty()) {
			handler.update("info.updating.tickets", "Updating tasks", handler.getProgress());
			for (Measure measure : updateMeasures) {
				MeasureDescription description = measure.getMeasureDescription();
				try {
					Issue issue = restClient.getSearchClient().searchJql(String.format(PROJECT_S_AND_KEY_IN_S, idProject, measure.getTicket()), 1, 0, null).claim().getIssues()
							.iterator().next();
					if (issue == null) {
						handler.update("error.ticket.not_found",
								String.format("Task for (%s - %s) cannot be found", description.getStandard().getLabel(), description.getReference()), 0,
								description.getStandard().getLabel(), description.getReference());
					} else {
						MeasureDescriptionText descriptionText = description.getMeasureDescriptionTextByAlpha2(language);
						IssueInputBuilder builder = new IssueInputBuilder(project, issue.getIssueType(),
								String.format("%s - %s: %s", description.getStandard().getLabel(), description.getReference(), descriptionText.getDomain()));
						builder.setDescription(measure.getToDo());
						estimations.put("originalEstimate", (measure.getInternalWL() + measure.getExternalWL()) + "d");
						builder.setFieldInput(new FieldInput(IssueFieldId.TIMETRACKING_FIELD, new ComplexIssueInputFieldValue(estimations)));
						builder.setDueDate(new DateTime(measure.getPhase().getEndDate().getTime()));
						restClient.getIssueClient().updateIssue(measure.getTicket(), builder.build());
					}
				} catch (Exception e) {
					TrickLogManager.Persist(e);
					if (!handler.getCode().startsWith("error."))
						handler.update("error.update.ticket",
								String.format("An unknown error occurred while update task for %s - %s", description.getStandard().getLabel(), description.getReference()), 0,
								description.getStandard().getLabel(), description.getReference());
				} finally {
					handler.setProgress(min + (int) ((++current / (double) size) * (maxProgess - min)));
				}
			}
			;
		}

		return true;
	}

	@Override
	public TicketingPageable<TicketingTask> findOtherTasksByProjectId(String idProject, Collection<String> excludes, int startIndex, int maxSize) {
		TicketingPageable<TicketingTask> tasks = new TicketingPageableImpl<>(maxSize);
		Promise<SearchResult> promise = null;
		if (excludes == null || excludes.isEmpty())
			promise = restClient.getSearchClient().searchJql(String.format(LOAD_BY_PROJECT_KEY, idProject), maxSize, startIndex, null);
		else {
			String exclude = "";
			for (String key : excludes)
				exclude += (exclude.isEmpty() ? "" : ", ") + key;
			promise = restClient.getSearchClient().searchJql(String.format(PROJECT_S_AND_STATUS_OPEN_AND_KEY_NOT_IN_S, idProject, exclude), maxSize, startIndex, null);
		}

		promise.claim().getIssues().forEach(issue -> {
			tasks.add(loadTask(issue));
			tasks.increase(1);
		});

		return tasks;
	}

	@Override
	public List<TicketingTask> findByIdsAndProjectId(String idProject, Collection<String> keyIssues) {
		return findAllByProjectId(idProject, keyIssues, false);
	}

	/**
	 * @return the objectMapper
	 */
	protected ObjectMapper getObjectMapper() {
		if (objectMapper == null)
			objectMapper = new ObjectMapper();
		return objectMapper;
	}

	/**
	 * @param objectMapper the objectMapper to set
	 */
	protected void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public boolean connect(String url, String token) {
		return false;
	}

}
