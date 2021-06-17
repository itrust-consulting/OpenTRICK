package lu.itrust.business.TS.database.dao.hbm;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOTrickService;
import lu.itrust.business.TS.model.TrickService;

/**
 * DAOTrickServiceHBM.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since Apr 23, 2014
 */
@Repository
public class DAOTrickServiceHBM extends DAOHibernate implements DAOTrickService {

	/**
	 * Constructor: <br>
	 */
	public DAOTrickServiceHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOTrickServiceHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOTrickService#get(int)
	 */
	@Override
	public TrickService get(Integer id)  {
		return (TrickService) getSession().get(TrickService.class, id);
	}

	/**
	 * getStatus: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOTrickService#getStatus()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TrickService getStatus()  {
		return (TrickService) getSession().createQuery("From TrickService").uniqueResultOptional().orElse(null);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOTrickService#save(lu.itrust.business.TS.model.TrickService)
	 */
	@Override
	public void save(TrickService trickservice)  {
		getSession().save(trickservice);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOTrickService#saveOrUpdate(lu.itrust.business.TS.model.TrickService)
	 */
	@Override
	public void saveOrUpdate(TrickService trickservice)  {
		getSession().saveOrUpdate(trickservice);
	}

	/**
	 * remove: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOTrickService#remove(String)
	 */
	@Override
	public void delete(TrickService trickservice)  {
		getSession().delete(trickservice);
	}
}