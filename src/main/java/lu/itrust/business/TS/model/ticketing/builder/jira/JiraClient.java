/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.builder.jira;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicPriority;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.Resolution;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.model.ticketing.TicketingProject;
import lu.itrust.business.TS.model.ticketing.TicketingTask;
import lu.itrust.business.TS.model.ticketing.builder.Client;
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

	private static final String PROJECT_S_AND_STATUS_OPEN_AND_KEY_IN_S = "project=%s and status=open and key in (%s) order by key";

	private JiraRestClient restClient;

	private Map<String, Priority> priorities;

	/**
	 * @return the priorities
	 */
	public synchronized Map<String, Priority> getPriorities() {
		if (priorities == null) {
			Map<String, Priority> priorities = new HashMap<>();
			restClient.getMetadataClient().getPriorities().claim().forEach(priority -> priorities.put(priority.getName(), priority));
			setPriorities(priorities);
		}
		return priorities;
	}

	/**
	 * @param priorities
	 *            the priorities to set
	 */
	public void setPriorities(Map<String, Priority> priorities) {
		this.priorities = priorities;
	}

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
		if (priorities != null)
			priorities.clear();
		priorities = null;
		restClient = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#connect(java.util.
	 * Map)
	 */
	@Override
	public boolean connect(Map<String, Object> settings) {
		String url = settings.get("url")
				.toString()/*
							 * ,password =
							 * settings.get("password").toString(),username =
							 * settings.get("username").toString()
							 */;
		return connect(url, "eomar", "testJira");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#connect(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public boolean connect(String url, String username, String passward) {
		try {
			final JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
			this.restClient = factory.createWithBasicHttpAuthentication(new URI(url), username, passward);
			return this.restClient.getUserClient().getUser(username).claim() != null;
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#isBelongTask(java.
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
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#findTaskById(java.
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
		task.setComments(new LinkedList<>());
		task.setIssueLinks(new LinkedList<>());
		task.setCustomFields(new LinkedHashMap<>());

		Iterable<Subtask> subTasks = issue.getSubtasks();
		Iterable<Comment> comments = issue.getComments();
		Iterable<IssueLink> issueLinks = issue.getIssueLinks();

		if (resolution != null)
			task.getCustomFields().put("Resolution", new JiraCustomField(resolution.getId().toString(), "Resolution", resolution.getDescription()));

		if (comments != null) {
			comments.forEach(comment -> {
				BasicUser author = comment.getAuthor();
				task.getComments().add(new lu.itrust.business.TS.model.ticketing.impl.Comment(comment.getId().toString(), author == null ? null : author.getDisplayName(),
						comment.getCreationDate().toDate(), comment.getBody()));
			});
		}

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
			subTasks.forEach(subIssue -> task.getSubTasks().add(loadTask(subIssue)));

		return task;
	}

	private JiraTask loadTask(Subtask issue) {
		return new JiraTask(issue.getIssueKey(), issue.getSummary(), issue.getIssueType().getDescription(), issue.getStatus().getDescription());
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
		List<TicketingProject> projects = new LinkedList<>();
		restClient.getProjectClient().getAllProjects().claim().forEach(project -> projects.add(new JiraProject(project.getKey(), project.getName())));
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
		Priority priority = getPriorities().get(task.getPriority());
		if (priority != null)
			builder.setPriority(priority);
		BasicIssue issue = restClient.getIssueClient().createIssue(builder.build()).claim();
		task.setId(issue.getKey());
		return true;
	}

	@Override
	public List<TicketingTask> findTasksByIdsAndProjectId(String idProject, List<String> keyIssues) {
		List<TicketingTask> tasks = new LinkedList<>();
		String include = "";
		for (String key : keyIssues)
			include += (include.isEmpty() ? "" : ", ") + key;
		restClient.getSearchClient().searchJql(String.format(PROJECT_S_AND_STATUS_OPEN_AND_KEY_IN_S, idProject, include), keyIssues.size(), 0, null).claim().getIssues()
				.forEach(issue -> tasks.add(loadTask(issue)));
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
	public boolean createIssue(String idProject, String language, String username, List<Measure> measures) {
		if (restClient == null)
			return false;
		Project project = restClient.getProjectClient().getProject(idProject).claim();
		if (project == null)
			return false;
		Map<String, IssueType> issueTypes = new HashMap<>();
		IssueType issueType = null;
		for (IssueType type : project.getIssueTypes()) {
			if (type.getName().equalsIgnoreCase("Task")) {
				issueType = type;
				break;
			} else if (issueType == null)
				issueType = type;
		}
		for (Measure measure : measures) {
			MeasureDescription description = measure.getMeasureDescription();
			MeasureDescriptionText descriptionText = description.findByAlph2(language);
			IssueInputBuilder builder = new IssueInputBuilder(project, issueType,
					String.format("%s - %s: %s", description.getStandard().getLabel(), description.getReference(), descriptionText.getDomain()));
			builder.setDescription(measure.getToDo());
			// builder.setReporterName("");
			// builder.setAssigneeName(measure.getResponsible().isEmpty()?
			// username : measure.getResponsible());
			builder.setDueDate(new DateTime(measure.getPhase().getEndDate().getTime()));
			Priority priority = getPriorities().get("normal");
			if (priority != null)
				builder.setPriority(priority);
			BasicIssue issue = restClient.getIssueClient().createIssue(builder.build()).claim();
			measure.setTicket(issue.getKey());
		}

		return false;
	}

	@Override
	public List<TicketingTask> findOtherTasksByProjectId(String idProject, List<String> excludes) {
		List<TicketingTask> tasks = new LinkedList<>();
		String exclude = "";
		for (String key : excludes)
			exclude += (exclude.isEmpty() ? "" : ", ") + key;
		restClient.getSearchClient().searchJql(String.format(PROJECT_S_AND_STATUS_OPEN_AND_KEY_NOT_IN_S, idProject, exclude)).claim().getIssues()
				.forEach(issue -> tasks.add(loadTask(issue)));
		return tasks;
	}

}
