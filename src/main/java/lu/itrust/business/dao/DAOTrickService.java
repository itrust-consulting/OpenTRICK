package lu.itrust.business.dao;

import lu.itrust.business.TS.TrickService;

/** 
 * DAOTrickService.java: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.à.rl.
 * @version 
 * @since Apr 23, 2014
 */
public interface DAOTrickService {
	
	public TrickService get(int id) throws Exception;
	
	public TrickService getStatus() throws Exception;
	
	public void save(TrickService trickservice) throws Exception;
	
	public void saveOrUpdate(TrickService trickservice) throws Exception;
	
	public void remove(TrickService trickservice) throws Exception;
}
