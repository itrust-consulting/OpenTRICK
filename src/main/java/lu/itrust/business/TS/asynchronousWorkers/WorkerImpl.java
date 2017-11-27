package lu.itrust.business.TS.asynchronousWorkers;

import java.util.Date;

import org.hibernate.SessionFactory;

import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.messagehandler.TaskName;

public abstract class WorkerImpl implements Worker {

	private String id = String.valueOf(System.nanoTime());

	private TaskName name;

	private Date started = null;

	private Date finished = null;

	private Exception error;

	private boolean working = false;

	private boolean canceled = false;

	private WorkersPoolManager poolManager;

	private SessionFactory sessionFactory;

	private Thread current;

	/**
	 * @param poolManager
	 * @param sessionFactory
	 */
	public WorkerImpl(WorkersPoolManager poolManager, SessionFactory sessionFactory) {
		this.poolManager = poolManager;
		this.sessionFactory = sessionFactory;
	}

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
	protected void setError(Exception error) {
		this.error = error;
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
	 * @return the poolManager
	 */
	protected WorkersPoolManager getPoolManager() {
		return poolManager;
	}

	/**
	 * @param poolManager
	 *            the poolManager to set
	 */
	@Override
	public void setPoolManager(WorkersPoolManager poolManager) {
		this.poolManager = poolManager;
	}

	/**
	 * @return the sessionFactory
	 */
	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	protected void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
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
