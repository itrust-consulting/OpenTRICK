package lu.itrust.business.ts.asynchronousWorkers;

import java.time.Instant;
import java.util.Date;

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

	/**
	 * Constructur.
	 * 
	 * @param userName
	 *            The name of the user for whom the computation will be done.
	 * @param dynamicParameterComputer
	 */
	public WorkerComputeDynamicParameters(String userName, DynamicParameterComputer dynamicParameterComputer) {
		setId(userName);
		this.dynamicParameterComputer = dynamicParameterComputer;
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
			//id = username
			dynamicParameterComputer.computeForAllAnalysesOfUser(getId());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			synchronized (this) {
				setWorking(false);
				setFinished(Date.from(Instant.now()));
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
