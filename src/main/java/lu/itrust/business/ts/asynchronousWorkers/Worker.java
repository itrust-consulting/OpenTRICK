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
 * The Worker interface represents a worker that performs asynchronous tasks.
 * It extends the Runnable interface, allowing it to be executed by a thread.
 */
public interface Worker extends Runnable {
	/**
	 * Gets the date and time when the worker started.
	 *
	 * @return The date and time when the worker started.
	 */
	Date getStarted();

	/**
	 * Gets the date and time when the worker finished.
	 *
	 * @return The date and time when the worker finished.
	 */
	Date getFinished();

	/**
	 * Checks if the worker is currently working.
	 *
	 * @return true if the worker is working, false otherwise.
	 */
	boolean isWorking();

	/**
	 * Checks if the worker has been canceled.
	 *
	 * @return true if the worker has been canceled, false otherwise.
	 */
	boolean isCanceled();

	/**
	 * Gets the name of the task associated with the worker.
	 *
	 * @return The name of the task.
	 */
	TaskName getName();

	/**
	 * Gets the error that occurred during the execution of the worker.
	 *
	 * @return The error that occurred, or null if no error occurred.
	 */
	Exception getError();

	/**
	 * Sets the ID of the worker.
	 *
	 * @param id The ID of the worker.
	 */
	void setId(String id);

	/**
	 * Gets the default SessionFactory instance.
	 *
	 * @return The default SessionFactory instance.
	 */
	default SessionFactory getSessionFactory() {
		return InstanceManager.getSessionFactory();
	}

	/**
	 * Gets the default WorkersPoolManager instance.
	 *
	 * @return The default WorkersPoolManager instance.
	 */
	default WorkersPoolManager getWorkersPoolManager() {
		return InstanceManager.getWorkersPoolManager();
	}

	/**
	 * Gets the default ServiceTaskFeedback instance.
	 *
	 * @return The default ServiceTaskFeedback instance.
	 */
	default ServiceTaskFeedback getServiceTaskFeedback() {
		return InstanceManager.getServiceTaskFeedback();
	}

	/**
	 * Gets the default MessageSource instance.
	 *
	 * @return The default MessageSource instance.
	 */
	default MessageSource getMessageSource() {
		return InstanceManager.getMessageSource();
	}

	/**
	 * Gets the default ServiceStorage instance.
	 *
	 * @return The default ServiceStorage instance.
	 */
	default ServiceStorage getServiceStorage() {
		return InstanceManager.getServiceStorage();
	}

	/**
	 * Checks if the worker matches the given expression and values.
	 *
	 * @param express The expression to match.
	 * @param values  The values to match against the expression.
	 * @return true if the worker matches the expression and values, false otherwise.
	 */
	default boolean isMatch(String express, Object... values) {
		return false;
	}

	/**
	 * Gets the ID of the worker.
	 *
	 * @return The ID of the worker.
	 */
	String getId();

	/**
	 * Starts the execution of the worker.
	 */
	void start();

	/**
	 * Cancels the execution of the worker.
	 */
	void cancel();

	/**
	 * Gets the current thread associated with the worker.
	 *
	 * @return The current thread associated with the worker.
	 */
	Thread getCurrent();
}
