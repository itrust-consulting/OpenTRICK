package lu.itrust.business.TS.database.service;

import lu.itrust.business.TS.data.TrickService;

/**
 * ServiceTrickService.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since Apr 23, 2014
 */
public interface ServiceTrickService {
	public TrickService get(Integer id) throws Exception;
	
	public TrickService getStatus() throws Exception;
	
	public void save(TrickService trickservice) throws Exception;
	
	public void saveOrUpdate(TrickService trickservice) throws Exception;
	
	public void delete(TrickService trickservice) throws Exception;
}
