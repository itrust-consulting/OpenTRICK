package lu.itrust.business.TS.database.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.database.service.WorkersPoolManager;

/**
 * WorkersPoolManagerImpl.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.a.rl.
 * @version
 * @since Feb 13, 2013
 */
@Service
public class WorkersPoolManagerImpl implements WorkersPoolManager {

	private final Map<Long, Worker> workersPool = new LinkedHashMap<>();

	/**
	 * Constructor: <br>
	 */
	public WorkersPoolManagerImpl() {
	}

	/**
	 * add: <br>
	 * Description
	 * 
	 * @param worker
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.WorkersPoolManager#add(lu.itrust.business.TS.asynchronousWorkers.Worker)
	 */
	@Override
	public boolean add(Worker worker) {
		if (worker == null || workersPool.containsKey(worker.getId()))
			return false;
		workersPool.put(worker.getId(), worker);
		return workersPool.containsKey(worker.getId());
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.WorkersPoolManager#get(java.lang.Long)
	 */
	@Override
	public Worker get(Long id) {
		return workersPool.get(id);
	}

	/**
	 * remove: <br>
	 * Description
	 * 
	 * @param worker
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.WorkersPoolManager#remove(lu.itrust.business.TS.asynchronousWorkers.Worker)
	 */
	@Override
	public Worker remove(Worker worker) {
		return workersPool.remove(worker.getId());
	}

	/**
	 * remove: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.WorkersPoolManager#remove(java.lang.Long)
	 */
	@Override
	public Worker remove(Long id) {
		return workersPool.remove(id);
	}

	/**
	 * exist: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.WorkersPoolManager#exist(java.lang.Long)
	 */
	@Override
	public boolean exist(Long id) {
		return workersPool.containsKey(id);
	}

	/**
	 * poolSize: <br>
	 * Description
	 * 
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.WorkersPoolManager#poolSize()
	 */
	@Override
	public int poolSize() {
		return workersPool.size();
	}
}