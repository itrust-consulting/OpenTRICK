package lu.itrust.business.ts.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOTrickService;
import lu.itrust.business.ts.database.service.ServiceTrickService;
import lu.itrust.business.ts.model.TrickService;

/**
 * ServiceTrickServiceImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Apr 23, 2014
 */
@Service
@Transactional(readOnly = true)
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
	 * @see lu.itrust.business.ts.database.service.ServiceTrickService#get(int)
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
	 * @see lu.itrust.business.ts.database.service.ServiceTrickService#getStatus()
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
	 * @see lu.itrust.business.ts.database.service.ServiceTrickService#save(lu.itrust.business.ts.model.TrickService)
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
	 * @see lu.itrust.business.ts.database.service.ServiceTrickService#saveOrUpdate(lu.itrust.business.ts.model.TrickService)
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
	 * @see lu.itrust.business.ts.database.service.ServiceTrickService#delete(lu.itrust.business.ts.model.TrickService)
	 */
	@Transactional
	@Override
	public void delete(TrickService trickservice)  {
		daoTrickService.delete(trickservice);
	}
}