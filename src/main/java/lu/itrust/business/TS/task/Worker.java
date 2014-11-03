/**
 * 
 */
package lu.itrust.business.TS.task;

import lu.itrust.business.TS.database.service.WorkersPoolManager;


/**
 * @author eom
 *
 */
public interface Worker extends Runnable{
	
	boolean isWorking();
	
	boolean isCanceled();
	
	Exception getError();
	
	void setId(Long id);
	
	void setPoolManager(WorkersPoolManager poolManager);
	
	Long getId();
	
	void start();
	
	void cancel();
}
