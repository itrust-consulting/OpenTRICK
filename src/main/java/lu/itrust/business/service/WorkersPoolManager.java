/**
 * 
 */
package lu.itrust.business.service;

import lu.itrust.business.task.Worker;

/**
 * @author eom
 *
 */
public interface WorkersPoolManager {
	
	int poolSize();
	
	boolean add(Worker worker);
	
	Worker get(Long id);
	
	Worker remove(Worker worker);
	
	Worker remove(Long id);
	
	boolean exist(Long id);
}
