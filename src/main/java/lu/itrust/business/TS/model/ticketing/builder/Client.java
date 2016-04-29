/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.builder;

import java.io.Closeable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.messagehandler.MessageHandler;
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
	
	boolean createIssues(String project, String language, Collection<Measure> measures,MessageHandler handler, int maxProgess);
	
	TicketingTask findTaskById(String idTask);
	
	TicketingTask findTaskByIdAndProjectId(String idTask, String idProject);

	TicketingProject findProjectById(String idProject);
	
	List<TicketingProject> findProjects();

	List<TicketingTask> findTaskByProjectId(String idProject);

	List<TicketingTask> findOpenedByIdsAndProjectId(String project, Collection<String> keyIssues);
	
	List<TicketingTask> findByIdsAndProjectId(String project, Collection<String> keyIssues);

	List<TicketingTask> findOtherTasksByProjectId(String project, Collection<String> excludes, int maxSize, int startIndex);
}
