package lu.itrust.business.ts.asynchronousWorkers;

import java.util.Date;

import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.messagehandler.TaskName;

/**
 * The abstract implementation of the Worker interface.
 */
public abstract class WorkerImpl implements Worker {

	private String id = String.valueOf(System.nanoTime());
	private TaskName name;
	private Date started = null;
	private Date finished = null;
	private Exception error;
	private boolean working = false;
	private boolean canceled = false;
	private Thread current;

	/**
	 * Gets the ID of the worker.
	 *
	 * @return the ID of the worker
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * Sets the ID of the worker.
	 *
	 * @param id the ID to set
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the start time of the worker.
	 *
	 * @return the start time of the worker
	 */
	@Override
	public Date getStarted() {
		return started;
	}

	/**
	 * Sets the start time of the worker.
	 *
	 * @param started the start time to set
	 */
	protected void setStarted(Date started) {
		this.started = started;
	}

	/**
	 * Gets the finish time of the worker.
	 *
	 * @return the finish time of the worker
	 */
	@Override
	public Date getFinished() {
		return finished;
	}

	/**
	 * Sets the finish time of the worker.
	 *
	 * @param finished the finish time to set
	 */
	protected void setFinished(Date finished) {
		this.finished = finished;
	}

	/**
	 * Gets the error that occurred during the worker execution.
	 *
	 * @return the error that occurred during the worker execution
	 */
	@Override
	public Exception getError() {
		return error;
	}

	/**
	 * Sets the error that occurred during the worker execution.
	 *
	 * @param e the error to set
	 */
	protected void setError(Exception e) {
		this.error = e;
		TrickLogManager.Persist(e);
	}

	/**
	 * Checks if the worker is currently working.
	 *
	 * @return true if the worker is currently working, false otherwise
	 */
	@Override
	public boolean isWorking() {
		return working;
	}

	/**
	 * Sets the working status of the worker.
	 *
	 * @param working the working status to set
	 */
	protected void setWorking(boolean working) {
		this.working = working;
	}

	/**
	 * Checks if the worker has been canceled.
	 *
	 * @return true if the worker has been canceled, false otherwise
	 */
	@Override
	public boolean isCanceled() {
		return canceled;
	}

	/**
	 * Sets the canceled status of the worker.
	 *
	 * @param canceled the canceled status to set
	 */
	protected void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	/**
	 * Gets the name of the worker.
	 *
	 * @return the name of the worker
	 */
	@Override
	public TaskName getName() {
		return name;
	}

	/**
	 * Sets the name of the worker.
	 *
	 * @param name the name to set
	 */
	public void setName(TaskName name) {
		this.name = name;
	}

	/**
	 * Gets the current thread associated with the worker.
	 *
	 * @return the current thread associated with the worker
	 */
	public Thread getCurrent() {
		return current;
	}

	/**
	 * Sets the current thread associated with the worker.
	 *
	 * @param current the current thread to set
	 */
	public void setCurrent(Thread current) {
		this.current = current;
	}
}
