package lu.itrust.business.TS.database.service;

import lu.itrust.business.TS.asynchronousWorkers.Worker;

/**
 * WorkersPoolManager.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.a.rl.
 * @version
 * @since Feb 13, 2013
 */
public interface WorkersPoolManager {

	 Worker get(String id);

	 boolean exist(String id);

	 int poolSize();

	 boolean add(Worker worker);

	 Worker remove(String id);

	 Worker remove(Worker worker);
	 
	 void cleaning();
}