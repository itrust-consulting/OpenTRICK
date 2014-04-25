package lu.itrust.business.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	 * getStatus: <br>
	 * Description
	 *
	 * @see lu.itrust.business.service.ServiceTrickService#getStatus()
	 */
	@Override
	public TrickService getStatus() throws Exception {
		return daoTrickService.getStatus();
	}

	@Override
	public void save(TrickService trickservice) throws Exception {
		daoTrickService.save(trickservice);
		
	}

	@Override
	public void saveOrUpdate(TrickService trickservice) throws Exception {
		daoTrickService.saveOrUpdate(trickservice);
	}

	@Override
	public TrickService get(int id) throws Exception {
		return daoTrickService.get(id);
	}

	@Override
	public void remove(TrickService trickservice) throws Exception {
		daoTrickService.remove(trickservice);
	}

}
