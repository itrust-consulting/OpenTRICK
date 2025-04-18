package lu.itrust.business.ts.database.service;

import lu.itrust.business.ts.model.TrickService;

/**
 * ServiceTrickService.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Apr 23, 2014
 */
public interface ServiceTrickService {
	public TrickService get(Integer id);
	
	public TrickService getStatus();
	
	public void save(TrickService trickservice);
	
	public void saveOrUpdate(TrickService trickservice);
	
	public void delete(TrickService trickservice);
}
