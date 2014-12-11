package lu.itrust.business.TS.database.dao.hbm;

import lu.itrust.business.TS.data.TrickService;
import lu.itrust.business.TS.database.dao.DAOTrickService;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

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
	public TrickService get(Integer id) throws Exception {
		return (TrickService) getSession().get(TrickService.class, id);
	}

	/**
	 * getStatus: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOTrickService#getStatus()
	 */
	@Override
	public TrickService getStatus() throws Exception {
		return (TrickService) getSession().get(TrickService.class, 1);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOTrickService#save(lu.itrust.business.TS.data.TrickService)
	 */
	@Override
	public void save(TrickService trickservice) throws Exception {
		getSession().save(trickservice);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOTrickService#saveOrUpdate(lu.itrust.business.TS.data.TrickService)
	 */
	@Override
	public void saveOrUpdate(TrickService trickservice) throws Exception {
		getSession().saveOrUpdate(trickservice);
	}

	/**
	 * remove: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOTrickService#remove(lu.itrust.business.TS.data.TrickService)
	 */
	@Override
	public void delete(TrickService trickservice) throws Exception {
		getSession().delete(trickservice);
	}
}