package lu.itrust.business.TS.database.service;

import lu.itrust.business.TS.task.Worker;

/**
 * WorkersPoolManager.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.a.rl.
 * @version
 * @since Feb 13, 2013
 */
public interface WorkersPoolManager {

	public Worker get(Long id);

	public boolean exist(Long id);

	public int poolSize();

	public boolean add(Worker worker);

	public Worker remove(Long id);

	public Worker remove(Worker worker);
}