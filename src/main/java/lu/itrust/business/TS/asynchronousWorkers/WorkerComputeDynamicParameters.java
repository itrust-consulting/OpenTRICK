package lu.itrust.business.TS.asynchronousWorkers;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import lu.itrust.business.TS.component.DynamicParameterComputer;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.messagehandler.TaskName;

public class WorkerComputeDynamicParameters implements Worker {
	
	private String id = String.valueOf(System.nanoTime());

	private Exception error;

	private boolean working = false;

	private boolean canceled = false;

	private DynamicParameterComputer dynamicParameterComputer;

	private WorkersPoolManager poolManager;

	/** The name of the user for whom the dynamic parameters are computed. */
	private String userName;

	/**
	 * Map (userName => computer) of all computers that are awaiting execution.
	 */
	private static Map<String, WorkerComputeDynamicParameters> workers = new HashMap<>();

	private Date dateStarted, dateFinished;
	
	private Thread current;

	/**
	 * Constructur.
	 * 
	 * @param userName
	 *            The name of the user for whom the computation will be done.
	 * @param dynamicParameterComputer
	 */
	public WorkerComputeDynamicParameters(String userName, DynamicParameterComputer dynamicParameterComputer) {
		this.userName = userName;
		this.dynamicParameterComputer = dynamicParameterComputer;
	}

	/**
	 * Triggers the computation of the dynamic parameters for the given user.
	 * The computation itself is not performed immediately, but rather postponed
	 * so that multiple calls do not cause denial-of-services. If another
	 * computation is scheduled, the call to this method is ignored.
	 * 
	 * @param userName
	 *            The name of the user for whom the computation will be done.
	 * @param computationDelayInSeconds
	 *            The number of sends which the computation shall get delayed.
	 *            During that waiting period, all new triggers will be ignored.
	 * @param dynamicParameterComputer
	 *            The computer instance itself.
	 * @param scheduler
	 *            A task scheduler which will invoke the worker.
	 * @param poolManager
	 *            A pool manager for workers.
	 */
	public static void trigger(String userName, int computationDelayInSeconds, DynamicParameterComputer dynamicParameterComputer, ThreadPoolTaskScheduler scheduler,
			WorkersPoolManager poolManager) {
		WorkerComputeDynamicParameters worker;
		synchronized (workers) {
			// Ignore call if a computation has already been scheduled
			if (workers.containsKey(userName))
				return;

			// Instantiate new worker and put it into the map
			worker = new WorkerComputeDynamicParameters(userName, dynamicParameterComputer);
			worker.setPoolManager(poolManager);
			workers.put(userName, worker);
		}

		// Schedule
		Date scheduleTime = Date.from(Instant.now().plusSeconds(computationDelayInSeconds));
		scheduler.schedule(worker, scheduleTime);
	}

	@Override
	public void run() {
		try {
			synchronized (this) {
				this.current = Thread.currentThread();
				if (poolManager != null && !poolManager.exist(getId()))
					if (!poolManager.add(this))
						return;
				if (canceled || working)
					return;
				working = true;
				dateStarted = Date.from(Instant.now());
			}

			dynamicParameterComputer.computeForAllAnalysesOfUser(userName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			synchronized (this) {
				working = false;
				dateFinished = Date.from(Instant.now());
			}
			synchronized (workers) {
				workers.remove(userName);
			}
			if (poolManager != null)
				poolManager.remove(getId());
		}
	}

	/*
	 * ****************************** Worker-specific methods
	 ******************************/

	@Override
	public boolean isWorking() {
		return working;
	}

	@Override
	public boolean isCanceled() {
		return canceled;
	}

	@Override
	public Date getStarted() {
		return dateStarted;
	}

	@Override
	public Date getFinished() {
		return dateFinished;
	}

	@Override
	public Exception getError() {
		return error;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void setPoolManager(WorkersPoolManager poolManager) {
		this.poolManager = poolManager;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void start() {
		run();
	}

	@Override
	public void cancel() {
		try {
			synchronized (this) {
				if (isWorking() && !isCanceled()) {
					if(getCurrent() == null)
						Thread.currentThread().interrupt();
					else getCurrent().interrupt();
					canceled = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = e;
		} finally {
			synchronized (this) {
				working = false;
			}
			synchronized (workers) {
				workers.remove(userName);
			}
			if (poolManager != null)
				poolManager.remove(getId());
		}
	}

	@Override
	public TaskName getName() {
		return TaskName.COMPUTE_DYNAMIC_PARAMETER;
	}

	@Override
	public Thread getCurrent() {
		return current;
	}

}
