package lu.itrust.business.TS.database.service;

import lu.itrust.business.TS.model.TrickService;

/**
 * ServiceTrickService.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since Apr 23, 2014
 */
public interface ServiceTrickService {
	public TrickService get(Integer id) ;
	
	public TrickService getStatus() ;
	
	public void save(TrickService trickservice) ;
	
	public void saveOrUpdate(TrickService trickservice) ;
	
	public void delete(TrickService trickservice) ;
}
