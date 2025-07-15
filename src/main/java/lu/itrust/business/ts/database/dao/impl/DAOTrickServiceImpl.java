package lu.itrust.business.ts.database.dao.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOTrickService;
import lu.itrust.business.ts.model.TrickService;

/**
 * DAOTrickServiceImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Apr 23, 2014
 */
@Repository
public class DAOTrickServiceImpl extends DAOHibernate implements DAOTrickService {

	/**
	 * Constructor: <br>
	 */
	public DAOTrickServiceImpl() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOTrickServiceImpl(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOTrickService#get(int)
	 */
	@Override
	public TrickService get(Integer id)  {
		return (TrickService) getSession().get(TrickService.class, id);
	}

	/**
	 * getStatus: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOTrickService#getStatus()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TrickService getStatus()  {
		return (TrickService) createQueryWithCache("From TrickService").uniqueResultOptional().orElse(null);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOTrickService#save(lu.itrust.business.ts.model.TrickService)
	 */
	@Override
	public void save(TrickService trickservice)  {
		getSession().save(trickservice);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOTrickService#saveOrUpdate(lu.itrust.business.ts.model.TrickService)
	 */
	@Override
	public void saveOrUpdate(TrickService trickservice)  {
		getSession().saveOrUpdate(trickservice);
	}

	/**
	 * remove: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOTrickService#remove(String)
	 */
	@Override
	public void delete(TrickService trickservice)  {
		getSession().delete(trickservice);
	}
}