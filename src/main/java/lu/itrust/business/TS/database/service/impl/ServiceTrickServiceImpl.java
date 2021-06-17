package lu.itrust.business.TS.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOTrickService;
import lu.itrust.business.TS.database.service.ServiceTrickService;
import lu.itrust.business.TS.model.TrickService;

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
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTrickService#get(int)
	 */
	@Override
	public TrickService get(Integer id)  {
		return daoTrickService.get(id);
	}

	/**
	 * getStatus: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTrickService#getStatus()
	 */
	@Override
	public TrickService getStatus()  {
		return daoTrickService.getStatus();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param trickservice
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTrickService#save(lu.itrust.business.TS.model.TrickService)
	 */
	@Transactional
	@Override
	public void save(TrickService trickservice)  {
		daoTrickService.save(trickservice);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param trickservice
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTrickService#saveOrUpdate(lu.itrust.business.TS.model.TrickService)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(TrickService trickservice)  {
		daoTrickService.saveOrUpdate(trickservice);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param trickservice
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTrickService#delete(lu.itrust.business.TS.model.TrickService)
	 */
	@Transactional
	@Override
	public void delete(TrickService trickservice)  {
		daoTrickService.delete(trickservice);
	}
}