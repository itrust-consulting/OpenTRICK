/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.builder;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

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
	
	TicketingTask findTaskById(String idTask);

	TicketingProject findProjectById(String idProject);
	
	List<TicketingProject> findProjects();

	List<TicketingTask> findTaskByProjectId(String idProject);
}
