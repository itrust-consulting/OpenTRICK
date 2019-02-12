/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.builder.redmine;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import com.taskadapter.redmineapi.bean.IssueFactory;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.internal.ResultsWrapper;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.model.ticketing.TicketingField;
import lu.itrust.business.TS.model.ticketing.TicketingPageable;
import lu.itrust.business.TS.model.ticketing.TicketingProject;
import lu.itrust.business.TS.model.ticketing.TicketingTask;
import lu.itrust.business.TS.model.ticketing.builder.Client;
import lu.itrust.business.TS.model.ticketing.impl.Comment;
import lu.itrust.business.TS.model.ticketing.impl.TicketingPageableImpl;
import lu.itrust.business.TS.model.ticketing.impl.redmine.RedmineCustomField;
import lu.itrust.business.TS.model.ticketing.impl.redmine.RedmineIssueLink;
import lu.itrust.business.TS.model.ticketing.impl.redmine.RedmineProject;
import lu.itrust.business.TS.model.ticketing.impl.redmine.RedmineTask;

/**
 * @author eomar
 *
 */
public class RedmineClient implements Client {

	private static final String SOMETHING_WRONG_WITH_THE_TICKETING_SYSTEM = "Something wrong with the ticketing system";
	private static final String ERROR_TASK_EXTERNAL = "error.task.external";
	private static final String SELECTED_PROJECT_CANNOT_BE_FOUND = "Selected project cannot be found!";
	private static final String ERROR_PROJECT_NOT_FOUND = "error.project.not.found";
	private static final String PLEASE_CHECK_YOUR_HAVE_PROPER_PERSMISSIONS = "Please check your have proper persmissions";
	private static final String ERROR_TASK_AUTHORISATION = "error.task.authorisation";
	private static final String PLEASE_CHECK_YOUR_TICKETING_SYSTEM_CREDENTIALS = "Please check your ticketing system credentials";
	private static final String ERROR_TASK_AUTHENTICATION = "error.task.authentication";

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
		try {
			this.manager = RedmineManagerFactory.createWithUserAuth(url, username, password);
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
	 * lu.itrust.business.TS.model.ticketing.builder.Client#isBelongTask(java.lang.
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
	 * lu.itrust.business.TS.model.ticketing.builder.Client#createIssue(java.lang.
	 * String, lu.itrust.business.TS.model.ticketing.TicketingTask)
	 */
	@Override
	public boolean createIssue(String idProject, TicketingTask task) {
		if (manager == null)
			return false;
		try {
			Project project = manager.getProjectManager().getProjectById(Integer.parseInt(idProject));
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
			final Project project = projectManager.getProjectById(Integer.parseInt(projectId));
			Tracker tracker = project.getTrackerByName("Task");
			if (tracker == null)
				tracker = project.getTrackers().stream().findFirst().orElse(null);
			int min = handler.getProgress(), size = measures.size() + updateMeasures.size(), current = 0;
			for (Measure measure : measures) {
				final MeasureDescription description = measure.getMeasureDescription();
				final MeasureDescriptionText descriptionText = description.getMeasureDescriptionTextByAlpha2(language);
				final Issue issue = IssueFactory.create(project.getId(), String.format("%s - %s: %s",
						description.getStandard().getLabel(), description.getReference(), descriptionText.getDomain()));
				issue.setTracker(tracker);
				issue.setDescription(measure.getToDo());
				issue.setEstimatedHours((float) ((measure.getInternalWL() + measure.getExternalWL()) * 8.0));
				issue.setStartDate(measure.getPhase().getBeginDate());
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

						final MeasureDescriptionText descriptionText = description
								.getMeasureDescriptionTextByAlpha2(language);
						issue.setSubject(String.format("%s - %s: %s", description.getStandard().getLabel(),
								description.getReference(), descriptionText.getDomain()));
						issue.setDescription(measure.getToDo());
						issue.setEstimatedHours((float) ((measure.getInternalWL() + measure.getExternalWL()) * 8.0));
						issue.setStartDate(measure.getPhase().getBeginDate());
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
	 * lu.itrust.business.TS.model.ticketing.builder.Client#findTaskById(java.lang.
	 * String)
	 */
	@Override
	public TicketingTask findTaskById(String idTask) {
		try {
			if (manager == null)
				return null;
			return loadIssue(manager.getIssueManager().getIssueById(Integer.parseInt(idTask)));
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
	 * lu.itrust.business.TS.model.ticketing.builder.Client#findTaskByIdAndProjectId
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public TicketingTask findTaskByIdAndProjectId(String idTask, String idProject) {
		try {
			if (manager == null)
				return null;
			Issue issue = manager.getIssueManager().getIssueById(Integer.parseInt(idTask));
			return issue.getProjectId().toString().equals(idProject) ? loadIssue(issue) : null;
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
	 * lu.itrust.business.TS.model.ticketing.builder.Client#findProjectById(java.
	 * lang.String)
	 */
	@Override
	public TicketingProject findProjectById(String idProject) {
		try {
			return manager == null ? null
					: buildProject(manager.getProjectManager().getProjectById(Integer.parseInt(idProject)));
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
			throw new TrickException(ERROR_TASK_AUTHENTICATION, PLEASE_CHECK_YOUR_TICKETING_SYSTEM_CREDENTIALS, e);
		} catch (NotAuthorizedException e) {
			throw new TrickException(ERROR_TASK_AUTHORISATION, PLEASE_CHECK_YOUR_HAVE_PROPER_PERSMISSIONS, e);
		} catch (RedmineException e) {
			throw new TrickException(ERROR_TASK_EXTERNAL, SOMETHING_WRONG_WITH_THE_TICKETING_SYSTEM, e);
		}
	}

	private RedmineTask loadIssue(Issue issue) {
		final RedmineTask task = new RedmineTask(issue.getId().toString(), issue.getSubject(),
				issue.getTracker() == null ? null : issue.getTracker().getName(), issue.getStatusName(),
				issue.getDescription(), issue.getDoneRatio() == null ? 0 : issue.getDoneRatio());
		task.setAssignee(issue.getAssigneeName());
		task.setReporter(issue.getAuthorName());
		task.setCreated(issue.getCreatedOn());
		task.setUpdated(issue.getUpdatedOn());
		task.setDue(issue.getDueDate());
		task.setPriority(issue.getPriorityText());
		task.setCustomFields(issue.getCustomFields().stream()
				.map(cf -> new RedmineCustomField(cf.getId().toString(), cf.getName(),
						cf.isMultiple() ? cf.getValues() : cf.getValue()))
				.collect(Collectors.toMap(TicketingField::getName, Function.identity())));

		task.setComments(
				issue.getJournals().stream().sorted((j1, j2) -> j1.getCreatedOn().compareTo(j2.getCreatedOn()))
						.map(j -> new Comment(j.getId().toString(),
								j.getUser() == null ? null : j.getUser().getFullName(), j.getCreatedOn(), j.getNotes()))
						.collect(Collectors.toList()));
		task.setSubTask(issue.getChildren().stream()
				.map(st -> new RedmineTask(st.getId().toString(), st.getSubject(),
						st.getTracker() == null ? null : st.getTracker().getName(), st.getStatusName(),
						st.getDescription(), st.getDoneRatio() == null ? 0 : st.getDoneRatio()))
				.collect(Collectors.toList()));
		task.setIssueLinks(issue.getRelations().stream()
				.map(r -> new RedmineIssueLink(r.getId().toString(), r.getType(), r.getIssueId()))
				.collect(Collectors.toList()));
		return task;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.builder.Client#
	 * findOpenedByIdsAndProjectId(java.lang.String, java.util.Collection)
	 */
	@Override
	public List<TicketingTask> findOpenedByIdsAndProjectId(String projectid, Collection<String> ids) {
		return findByIdsAndProjectId(projectid, ids, false);
	}

	private List<TicketingTask> findByIdsAndProjectId(String projectid, Collection<String> ids, boolean all) {
		try {
			if (manager == null)
				return Collections.emptyList();
			final Map<String, String> parameters = new HashMap<>(1);
			parameters.put("project_id", projectid);
			parameters.put("issue_id", ids.stream().collect(Collectors.joining(",")));
			if (all)
				parameters.put("status_id", "*");
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.builder.Client#findByIdsAndProjectId(
	 * java.lang.String, java.util.Collection)
	 */
	@Override
	public List<TicketingTask> findByIdsAndProjectId(String projectId, Collection<String> ids) {
		return findByIdsAndProjectId(projectId, ids, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.builder.Client#
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
			final Params parameters = new Params();

			if (!(excludes == null || excludes.isEmpty())) {
				if (!excludes.isEmpty())
					return findOtherTasks(projectId, excludes, startIndex, size);
				parameters.add("f[]", "issue_id");
				parameters.add("op[issue_id]", "!");
				excludes.forEach(e -> parameters.add("v[issue_id][]", e));
			}
			parameters.add("status_id", "*");
			parameters.add("limit", size + "");
			parameters.add("project_id", projectId);
			parameters.add("offset", startIndex + "");

			return new TicketingPageableImpl<>(startIndex, size, manager.getIssueManager().getIssues(parameters)
					.getResults().stream().map(e -> loadIssue(e)).collect(Collectors.toList()));
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
		final Map<String, String> parameters = new LinkedHashMap<String, String>(4);
		final Map<Integer, Boolean> mapperExcludes = excludes.stream().map(Integer::parseInt)
				.collect(Collectors.toMap(Function.identity(), e -> false, (e1, e2) -> e1));
		parameters.put("status_id", "*");
		parameters.put("limit", size + "");
		parameters.put("project_id", projectId);
		parameters.put("offset", startIndex + "");
		while (true) {
			ResultsWrapper<Issue> wrapper = manager.getIssueManager().getIssues(parameters);
			if (!wrapper.hasSomeResults())
				break;
			final int currentSize = tasks.size(), reminde = size - currentSize;
			wrapper.getResults().stream().filter(i -> mapperExcludes.getOrDefault(i.getId(), true)).limit(reminde)
					.map(e -> loadIssue(e)).forEach(e -> tasks.add(e));
			if (tasks.size() >= size) {
				if (reminde < size && wrapper.getResultsNumber() == size) {
					System.out.println(String.format("Offset: %d, reminde: %d, size :%d, new offset: %d",
							tasks.getOffset(), reminde, size, tasks.getOffset() - size + reminde));
					((TicketingPageableImpl<?>) tasks).setOffset(tasks.getOffset() - size + reminde);

				}
				break;
			}
			parameters.put("offset", tasks.moveNext() + "");
		}
		return tasks;
	}

}
