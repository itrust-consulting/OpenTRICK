package lu.itrust.business.ts.asynchronousWorkers;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import lu.itrust.business.ts.component.DynamicParameterComputer;
import lu.itrust.business.ts.messagehandler.TaskName;

/**
 * This class represents a worker that computes dynamic parameters for a user.
 * It extends the `WorkerImpl` class and implements the `Runnable` interface.
 * 
 * The computation of dynamic parameters is triggered by calling the `trigger` method.
 * The computation itself is postponed to avoid denial-of-service attacks.
 * 
 * This class maintains a map of workers that are awaiting execution, where the key is the user name.
 * 
 * The computation is performed by invoking the `computeForAllAnalysesOfUser` method of the `DynamicParameterComputer` instance.
 * 
 * This class provides methods to start, cancel, and get the name of the worker.
 */
public class WorkerComputeDynamicParameters extends WorkerImpl {
	
	private DynamicParameterComputer dynamicParameterComputer;

	/** The name of the user for whom the dynamic parameters are computed. */
	private String userName;

	/**
	 * Map (userName => computer) of all computers that are awaiting execution.
	 */
	private static Map<String, WorkerComputeDynamicParameters> workers = new HashMap<>();

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
	public static void trigger(String userName, int computationDelayInSeconds, DynamicParameterComputer dynamicParameterComputer, ThreadPoolTaskScheduler scheduler) {
		WorkerComputeDynamicParameters worker;
		synchronized (workers) {
			// Ignore call if a computation has already been scheduled
			if (workers.containsKey(userName))
				return;
			// Instantiate new worker and put it into the map
			worker = new WorkerComputeDynamicParameters(userName, dynamicParameterComputer);
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
				setCurrent(Thread.currentThread());
				if (getWorkersPoolManager() != null && !getWorkersPoolManager().exist(getId()))
					if (!getWorkersPoolManager().add(this))
						return;
				if (isCanceled() || isWorking())
					return;
				setWorking(true);
				setStarted(Date.from(Instant.now()));
			}
			dynamicParameterComputer.computeForAllAnalysesOfUser(userName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			synchronized (this) {
				setWorking(false);
				setFinished(Date.from(Instant.now()));
			}
			
			synchronized (workers) {
				workers.remove(userName);
			}
			if (getWorkersPoolManager() != null)
				getWorkersPoolManager().remove(getId());
		}
	}

	/*
	 * ****************************** Worker-specific methods
	 ******************************/
	
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
					setCanceled(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			setError(e);
		} finally {
			synchronized (this) {
				setWorking(false);
			}
			synchronized (workers) {
				workers.remove(userName);
			}
			if (getWorkersPoolManager() != null)
				getWorkersPoolManager().remove(getId());
		}
	}

	/**
	 * This enum represents the different task names for computing dynamic parameters.
	 */
	@Override
	public TaskName getName() {
		return TaskName.COMPUTE_DYNAMIC_PARAMETER;
	}
	
}
