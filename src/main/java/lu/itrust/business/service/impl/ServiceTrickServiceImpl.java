package lu.itrust.business.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.TrickService;
import lu.itrust.business.dao.DAOTrickService;
import lu.itrust.business.service.ServiceTrickService;

/**
 * ServiceTrickServiceImpl.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since Apr 23, 2014
 */
@Service
public class ServiceTrickServiceImpl implements ServiceTrickService {

	@Autowired
	private DAOTrickService daoTrickService;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceTrickService#get(int)
	 */
	@Override
	public TrickService get(Integer id) throws Exception {
		return daoTrickService.get(id);
	}

	/**
	 * getStatus: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceTrickService#getStatus()
	 */
	@Override
	public TrickService getStatus() throws Exception {
		return daoTrickService.getStatus();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param trickservice
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceTrickService#save(lu.itrust.business.TS.TrickService)
	 */
	@Transactional
	@Override
	public void save(TrickService trickservice) throws Exception {
		daoTrickService.save(trickservice);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param trickservice
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceTrickService#saveOrUpdate(lu.itrust.business.TS.TrickService)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(TrickService trickservice) throws Exception {
		daoTrickService.saveOrUpdate(trickservice);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param trickservice
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceTrickService#delete(lu.itrust.business.TS.TrickService)
	 */
	@Transactional
	@Override
	public void delete(TrickService trickservice) throws Exception {
		daoTrickService.delete(trickservice);
	}
}