/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.builder.jira;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import lu.itrust.business.TS.model.ticketing.TicketingProject;
import lu.itrust.business.TS.model.ticketing.TicketingTask;
import lu.itrust.business.TS.model.ticketing.builder.Client;
import lu.itrust.business.TS.model.ticketing.impl.jira.JiraTask;

/**
 * @author eomar
 *
 */
public class JiraClient implements Client {

	private JiraRestClient restClient;

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
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#connect(java.util.
	 * Map)
	 */
	@Override
	public boolean connect(Map<String, Object> settings) {
		return false;
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
		return issue != null && issue.getProject().getId().toString().equals(idProject);
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
		Issue issue = restClient.getIssueClient().getIssue(idTask).claim();
		JiraTask task = new JiraTask(idTask, issue.getSummary(), issue.getIssueType().getDescription(), issue.getStatus().getDescription(), issue.getDescription(),
				issue.getResolution().equals("fixed") ? 100 : 0);
		task.setReporter(issue.getReporter().getDisplayName());
		task.setAssignee(issue.getAssignee().getDisplayName());
		task.setCreated(issue.getCreationDate().toDate());
		task.setDue(issue.getDueDate().toDate());
		task.setUpdated(issue.getUpdateDate().toDate());
		Iterable<Subtask> subTask = issue.getSubtasks();

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
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.builder.Client#findProjects()
	 */
	@Override
	public List<TicketingProject> findProjects() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

}
