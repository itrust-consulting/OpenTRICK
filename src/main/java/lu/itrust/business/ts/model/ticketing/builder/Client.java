/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.builder;

import java.io.Closeable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.ticketing.TicketingPageable;
import lu.itrust.business.ts.model.ticketing.TicketingProject;
import lu.itrust.business.ts.model.ticketing.TicketingTask;

/**
 * The Client interface represents a client for interacting with a ticketing system.
 * It provides methods for connecting to the ticketing system, creating and finding tasks,
 * and retrieving project information.
 */
public interface Client extends Closeable {
	
	boolean connect(String url, String token);

	boolean connect(Map<String, Object> settings);

	boolean connect(String url, String username, String passward);

	boolean isBelongTask(String idProject, String taskId);

	boolean createIssue(String idProject, TicketingTask task);

	boolean createIssues(String project,String tracker, String language, Collection<Measure> measures, Collection<Measure> updateMeasures, ValueFactory factory, MessageHandler handler, int maxProgess);

	TicketingTask findTaskById(String idTask);

	TicketingTask findTaskByIdAndProjectId(String idTask, String idProject);

	TicketingProject findProjectById(String idProject);

	List<TicketingProject> findProjects();

	List<TicketingTask> findTaskByProjectId(String idProject);

	List<TicketingTask> findOpenedByIdsAndProjectId(String project, Collection<String> keyIssues);

	List<TicketingTask> findByIdsAndProjectId(String project, Collection<String> keyIssues);

	TicketingPageable<TicketingTask> findOtherTasksByProjectId(String project, Collection<String> excludes, int startIndex, int maxSize);

}
