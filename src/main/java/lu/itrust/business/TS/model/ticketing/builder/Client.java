/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.builder;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.ticketing.TicketingProject;
import lu.itrust.business.TS.model.ticketing.TicketingTask;

/**
 * @author eomar
 *
 */
public interface Client extends Closeable{
	
	boolean connect(Map<String, Object> settings);

	boolean connect(String url, String username, String passward);
	
	boolean isBelongTask(String idProject, String taskId);
	
	boolean createIssue(String idProject ,TicketingTask task);
	
	boolean createIssue(String project, String username, String language, List<Measure> measures);
	
	TicketingTask findTaskById(String idTask);
	
	TicketingTask findTaskByIdAndProjectId(String idTask, String idProject);

	TicketingProject findProjectById(String idProject);
	
	List<TicketingProject> findProjects();

	List<TicketingTask> findTaskByProjectId(String idProject);

	List<TicketingTask> findTasksByIdsAndProjectId(String project, List<String> keyIssues);

	List<TicketingTask> findOtherTasksByProjectId(String project, List<String> excludes);

	
}
