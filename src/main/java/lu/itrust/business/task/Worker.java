/**
 * 
 */
package lu.itrust.business.task;

import lu.itrust.business.service.WorkersPoolManager;


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
