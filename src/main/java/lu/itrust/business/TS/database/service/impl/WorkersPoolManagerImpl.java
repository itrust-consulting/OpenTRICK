package lu.itrust.business.TS.database.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.scheduling.annotation.Scheduled;
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

	private final Map<String, Worker> workersPool = new LinkedHashMap<>();

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
	 * @see lu.itrust.business.TS.database.service.WorkersPoolManager#get(String)
	 */
	@Override
	public Worker get(String id) {
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
	 * @see lu.itrust.business.TS.database.service.WorkersPoolManager#remove(String)
	 */
	@Override
	public Worker remove(String id) {
		return workersPool.remove(id);
	}

	/**
	 * exist: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.WorkersPoolManager#exist(String)
	 */
	@Override
	public boolean exist(String id) {
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

	@Override
	@Scheduled(initialDelay = 5000, fixedDelay = 5000)
	public void cleaning() {
		if (workersPool == null || workersPool.isEmpty())
			return;
		Date limit = new Timestamp(System.currentTimeMillis() - 5000);
		Iterator<Entry<String, Worker>> iterator = workersPool.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Worker> entry = iterator.next();
			if(entry.getValue().getFinished() == null)
				continue;
			else if(limit.before(entry.getValue().getFinished()))
				workersPool.remove(entry.getKey());
			
		}

	}
}