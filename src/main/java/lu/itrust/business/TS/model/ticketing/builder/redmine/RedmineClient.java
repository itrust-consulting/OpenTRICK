/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.builder.redmine;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.NotFoundException;
import com.taskadapter.redmineapi.ProjectManager;
import com.taskadapter.redmineapi.RedmineAuthenticationException;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueFactory;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.internal.ResultsWrapper;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.model.ticketing.TicketingProject;
import lu.itrust.business.TS.model.ticketing.TicketingTask;
import lu.itrust.business.TS.model.ticketing.builder.Client;
import lu.itrust.business.TS.model.ticketing.impl.redmine.RedmineProject;
import lu.itrust.business.TS.model.ticketing.impl.redmine.RedmineTask;

/**
 * @author eomar
 *
 */
public class RedmineClient implements Client {

	private RedmineManager manager;

	/**
	 * 
	 */
	public RedmineClient() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		this.manager = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#connect(java.util.Map)
	 */
	@Override
	public boolean connect(Map<String, Object> settings) {
		return connect(settings.getOrDefault("url", "").toString(), settings.getOrDefault("username", "").toString(),
				settings.getOrDefault("password", "").toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.builder.Client#connect(java.lang.
	 * String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean connect(String url, String username, String password) {
		this.manager = RedmineManagerFactory.createWithUserAuth(url, username, password);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#isBelongTask(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public boolean isBelongTask(String idProject, String taskId) {
		try {
			Issue issue = this.manager.getIssueManager().getIssueById(Integer.getInteger(taskId));
			return issue != null && Integer.getInteger(idProject).equals(issue.getParentId());
		} catch (NotFoundException e) {
			return false;
		} catch (RedmineAuthenticationException e) {
			throw new TrickException("error.task.authentication", "Please check your ticketing system credentials", e);
		} catch (RedmineException e) {
			throw new TrickException("error.task.external", "Something wrong with the ticketing system", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#createIssue(java.lang.
	 * String, lu.itrust.business.TS.model.ticketing.TicketingTask)
	 */
	@Override
	public boolean createIssue(String idProject, TicketingTask task) {
		if (manager == null)
			return false;
		try {
			Project project = manager.getProjectManager().getProjectById(Integer.getInteger(idProject));
			Tracker tracker = project.getTrackerByName(task.getType());
			if (tracker == null)
				tracker = project.getTrackers().stream().findFirst().orElse(null);
			Issue issue = IssueFactory.create(project.getId(), task.getName());
			issue.setDescription(task.getDescription());
			issue.setAuthorName(task.getReporter());
			if (task.getAssignee() != null)
				issue.setAssigneeName(task.getAssignee());
			if (task.getDue() != null)
				issue.setDueDate(task.getDue());
			if (task.getProgress() > 0)
				issue.setDoneRatio(task.getProgress());
			if (task.getStatus() != null)
				issue.setStatusName(task.getStatus());
			Issue persisted = manager.getIssueManager().createIssue(issue);
			task.setId(persisted.getId().toString());
			return false;
		} catch (RedmineAuthenticationException e) {
			throw new TrickException("error.task.authentication", "Please check your ticketing system credentials", e);
		} catch (NotFoundException e) {
			throw new TrickException("error.project.not.found", "Selected project cannot be found!", e);
		} catch (RedmineException e) {
			throw new TrickException("error.task.external", "Something wrong with the ticketing system", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#createIssues(java.lang.
	 * String, java.lang.String, java.util.Collection, java.util.Collection,
	 * lu.itrust.business.TS.messagehandler.MessageHandler, int)
	 */
	@Override
	public boolean createIssues(String projectId, String language, Collection<Measure> measures,
			Collection<Measure> updateMeasures, MessageHandler handler, int maxProgess) {
		if (manager == null)
			throw new TrickException("error.500.message", "Internal error");
		try {
			final ProjectManager projectManager = manager.getProjectManager();
			final IssueManager issueManager = manager.getIssueManager();
			final Project project = projectManager.getProjectById(Integer.getInteger(projectId));
			Tracker tracker = project.getTrackerByName("Task");
			if (tracker == null)
				tracker = project.getTrackers().stream().findFirst().orElse(null);
			int min = handler.getProgress(), size = measures.size() + updateMeasures.size(), current = 0;
			for (Measure measure : measures) {
				final MeasureDescription description = measure.getMeasureDescription();
				final MeasureDescriptionText descriptionText = description.findByAlph2(language);
				final Issue issue = IssueFactory.create(project.getId(), String.format("%s - %s: %s",
						description.getStandard().getLabel(), description.getReference(), descriptionText.getDomain()));
				issue.setTracker(tracker);
				issue.setDescription(measure.getToDo());
				issue.setEstimatedHours((float) ((measure.getInternalWL() + measure.getExternalWL()) * 24.0));
				issue.setDueDate(measure.getPhase().getEndDate());
				issue.setDoneRatio((int) measure.getImplementationRateValue(Collections.emptyMap()));
				if (!(measure.getResponsible() == null || measure.getResponsible().isEmpty()))
					issue.setAssigneeName(measure.getResponsible());
				Issue persisted = issueManager.createIssue(issue);
				measure.setTicket(persisted.getId().toString());
				handler.setProgress(min + (int) ((++current / (double) size) * (maxProgess - min)));
			}

			if (!updateMeasures.isEmpty()) {
				handler.update("info.updating.tickets", "Updating tasks", handler.getProgress());
				final Map<String, String> parameters = new HashMap<>(2);
				parameters.put("project_id", projectId);
				for (Measure measure : updateMeasures) {
					final MeasureDescription description = measure.getMeasureDescription();
					parameters.put("id", measure.getTicket());
					ResultsWrapper<Issue> result = issueManager.getIssues(parameters);
					if (!result.hasSomeResults())
						handler.update("error.ticket.not_found",
								String.format("Task for (%s - %s) cannot be found",
										description.getStandard().getLabel(), description.getReference()),
								0, description.getStandard().getLabel(), description.getReference());
					else {
						final Issue issue = result.getResults().get(0);
						issue.setDescription(measure.getToDo());
						issue.setEstimatedHours((float) ((measure.getInternalWL() + measure.getExternalWL()) * 24.0));
						issue.setDueDate(measure.getPhase().getEndDate());
						issue.setDoneRatio((int) measure.getImplementationRateValue(Collections.emptyMap()));
						if (!(measure.getResponsible() == null || measure.getResponsible().isEmpty()))
							issue.setAssigneeName(measure.getResponsible());
						issueManager.update(issue);
					}
					handler.setProgress(min + (int) ((++current / (double) size) * (maxProgess - min)));
				}
			}
			return true;
		} catch (RedmineAuthenticationException e) {
			throw new TrickException("error.task.authentication", "Please check your ticketing system credentials", e);
		} catch (NotFoundException e) {
			throw new TrickException("error.project.not.found", "Selected project cannot be found!", e);
		} catch (RedmineException e) {
			throw new TrickException("error.task.external", "Something wrong with the ticketing system", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#findTaskById(java.lang.
	 * String)
	 */
	@Override
	public TicketingTask findTaskById(String idTask) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#findTaskByIdAndProjectId
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public TicketingTask findTaskByIdAndProjectId(String idTask, String idProject) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#findProjectById(java.
	 * lang.String)
	 */
	@Override
	public TicketingProject findProjectById(String idProject) {
		try {
			return manager == null ? null
					: buildProject(manager.getProjectManager().getProjectById(Integer.getInteger(idProject)));
		} catch (NotFoundException e) {
			return null;
		} catch (RedmineAuthenticationException e) {
			throw new TrickException("error.task.authentication", "Please check your ticketing system credentials", e);
		} catch (RedmineException e) {
			throw new TrickException("error.task.external", "Something wrong with the ticketing system", e);
		}
	}

	private final static TicketingProject buildProject(Project project) {
		return new RedmineProject(project.getId().toString(), project.getName(), project.getDescription());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.builder.Client#findProjects()
	 */
	@Override
	public List<TicketingProject> findProjects() {
		try {
			return manager == null ? Collections.emptyList()
					: manager.getProjectManager().getProjects().stream().map(RedmineClient::buildProject)
							.collect(Collectors.toList());
		} catch (RedmineAuthenticationException e) {
			throw new TrickException("error.task.authentication", "Please check your ticketing system credentials", e);
		} catch (RedmineException e) {
			throw new TrickException("error.task.external", "Something wrong with the ticketing system", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#findTaskByProjectId(java
	 * .lang.String)
	 */
	@Override
	public List<TicketingTask> findTaskByProjectId(String idProject) {
		try {
			if (manager == null)
				return Collections.emptyList();
			final Map<String, String> parameters = new HashMap<>(1);
			parameters.put("project_id", idProject);
			parameters.put("status_id", "*");
			ResultsWrapper<Issue> results = manager.getIssueManager().getIssues(parameters);
			return results.hasSomeResults()
					? results.getResults().stream().map(e -> loadIssue(e)).collect(Collectors.toList())
					: Collections.emptyList();
		} catch (RedmineAuthenticationException e) {
			throw new TrickException("error.task.authentication", "Please check your ticketing system credentials", e);
		} catch (RedmineException e) {
			throw new TrickException("error.task.external", "Something wrong with the ticketing system", e);
		}
	}

	private RedmineTask loadIssue(Issue e) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.builder.Client#
	 * findOpenedByIdsAndProjectId(java.lang.String, java.util.Collection)
	 */
	@Override
	public List<TicketingTask> findOpenedByIdsAndProjectId(String projectid, Collection<String> ids) {
		if(manager == null)
			return Collections.emptyList();
		//Param
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#findByIdsAndProjectId(
	 * java.lang.String, java.util.Collection)
	 */
	@Override
	public List<TicketingTask> findByIdsAndProjectId(String project, Collection<String> keyIssues) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.builder.Client#
	 * findOtherTasksByProjectId(java.lang.String, java.util.Collection, int, int)
	 */
	@Override
	public List<TicketingTask> findOtherTasksByProjectId(String project, Collection<String> excludes, int maxSize,
			int startIndex) {
		return null;
	}

}
