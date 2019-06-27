package lu.itrust.business.TS.asynchronousWorkers;

import java.util.Date;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.messagehandler.TaskName;

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
	 * @return the id
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the started
	 */
	@Override
	public Date getStarted() {
		return started;
	}

	/**
	 * @param started
	 *            the started to set
	 */
	protected void setStarted(Date started) {
		this.started = started;
	}

	/**
	 * @return the finished
	 */
	@Override
	public Date getFinished() {
		return finished;
	}

	/**
	 * @param finished
	 *            the finished to set
	 */
	protected void setFinished(Date finished) {
		this.finished = finished;
	}

	/**
	 * @return the error
	 */
	@Override
	public Exception getError() {
		return error;
	}

	/**
	 * @param error
	 *            the error to set
	 */
	protected void setError(Exception e) {
		this.error = e;
		TrickLogManager.Persist(e);
	}

	/**
	 * @return the working
	 */
	@Override
	public boolean isWorking() {
		return working;
	}

	/**
	 * @param working
	 *            the working to set
	 */
	protected void setWorking(boolean working) {
		this.working = working;
	}

	/**
	 * @return the canceled
	 */
	@Override
	public boolean isCanceled() {
		return canceled;
	}

	/**
	 * @param canceled
	 *            the canceled to set
	 */
	protected void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}


	/**
	 * @return the name
	 */
	@Override
	public TaskName getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(TaskName name) {
		this.name = name;
	}

	public Thread getCurrent() {
		return current;
	}

	public void setCurrent(Thread current) {
		this.current = current;
	}

}
