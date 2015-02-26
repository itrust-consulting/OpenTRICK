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

	public Worker get(String id);

	public boolean exist(String id);

	public int poolSize();

	public boolean add(Worker worker);

	public Worker remove(String id);

	public Worker remove(Worker worker);
}