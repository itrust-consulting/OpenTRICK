/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import lu.itrust.business.TS.database.service.WorkersPoolManager;


/**
 * @author eom
 *
 */
public interface Worker extends Runnable{
	
	boolean isWorking();
	
	boolean isCanceled();
	
	Exception getError();
	
	void setId(String id);
	
	void setPoolManager(WorkersPoolManager poolManager);
	
	String getId();
	
	void start();
	
	void cancel();
}
