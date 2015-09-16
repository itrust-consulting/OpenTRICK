/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.util.Date;

import lu.itrust.business.TS.database.service.WorkersPoolManager;


/**
 * @author eom
 *
 */
public interface Worker extends Runnable{
	
	Date getStarted();
	
	Date getFinished();
	
	boolean isWorking();
	
	boolean isCanceled();
	
	Exception getError();
	
	void setId(String id);
	
	void setPoolManager(WorkersPoolManager poolManager);
	/**
	 * @param express
	 * @param values
	 * <br>
	 * Example for {@link WorkerComputeActionPlan}:<br>
	 * isMatch("class+analysis.id",Worker.class,12)
	 * @return
	 */
	default boolean isMatch(String express, Object... values) {return false;}
	
	String getId();
	
	void start();
	
	void cancel();
}
