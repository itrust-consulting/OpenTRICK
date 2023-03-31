/**
 * 
 */
package lu.itrust.business.ts.asynchronousWorkers;

import java.util.Date;

import org.hibernate.SessionFactory;
import org.springframework.context.MessageSource;

import lu.itrust.business.ts.database.service.ServiceStorage;
import lu.itrust.business.ts.database.service.ServiceTaskFeedback;
import lu.itrust.business.ts.database.service.WorkersPoolManager;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.messagehandler.TaskName;

/**
 * @author eom
 *
 */
public interface Worker extends Runnable {

	Date getStarted();

	Date getFinished();

	boolean isWorking();

	boolean isCanceled();

	TaskName getName();

	Exception getError();

	void setId(String id);

	default SessionFactory getSessionFactory() {
		return InstanceManager.getSessionFactory();
	}

	default WorkersPoolManager getWorkersPoolManager() {
		return InstanceManager.getWorkersPoolManager();
	}

	default ServiceTaskFeedback getServiceTaskFeedback() {
		return InstanceManager.getServiceTaskFeedback();
	}

	default MessageSource getMessageSource() {
		return InstanceManager.getMessageSource();
	}
	
	default ServiceStorage getServiceStorage() {
		return InstanceManager.getServiceStorage();
	}

	/**
	 * @param express
	 * @param values  <br>
	 *                Example for {@link WorkerComputeActionPlan}:<br>
	 *                isMatch("class+analysis.id",Worker.class,12)
	 * @return
	 */
	default boolean isMatch(String express, Object... values) {
		return false;
	}

	String getId();

	void start();

	void cancel();

	Thread getCurrent();
}
