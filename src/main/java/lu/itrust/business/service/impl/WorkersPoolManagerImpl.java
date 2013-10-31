/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import lu.itrust.business.service.WorkersPoolManager;
import lu.itrust.business.task.Worker;

/**
 * @author eom
 *
 */
@Service
public class WorkersPoolManagerImpl implements WorkersPoolManager {
	
	private final Map<Long, Worker> workersPool = new LinkedHashMap<>();
	
	/**
	 * 
	 */
	public WorkersPoolManagerImpl() {
	}

	@Override
	public boolean add(Worker worker) {
		if(worker == null || workersPool.containsKey(worker.getId()) )
			return false;
		workersPool.put(worker.getId(), worker);
		return workersPool.containsKey(worker.getId());
	}

	@Override
	public Worker get(Long id) {
		return workersPool.get(id);
	}

	@Override
	public Worker remove(Worker worker) {
		return workersPool.remove(worker.getId());
	}

	@Override
	public Worker remove(Long id) {
		return workersPool.remove(id);
	}

	@Override
	public boolean exist(Long id) {
		return workersPool.containsKey(id);
	}

	@Override
	public int poolSize() {
		return workersPool.size();
	}
}
