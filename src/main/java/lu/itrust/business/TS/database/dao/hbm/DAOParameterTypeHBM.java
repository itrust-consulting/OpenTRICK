package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.data.parameter.ParameterType;
import lu.itrust.business.TS.database.dao.DAOParameterType;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOParameterTypeHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 31 janv. 2013
 */
@Repository
public class DAOParameterTypeHBM extends DAOHibernate implements DAOParameterType {

	/**
	 * Constructor: <br>
	 */
	public DAOParameterTypeHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOParameterTypeHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameterType#get(int)
	 */
	@Override
	public ParameterType get(Integer id) throws Exception {
		return (ParameterType) getSession().get(ParameterType.class, id);
	}

	/**
	 * getByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameterType#getByName(java.lang.String)
	 */
	@Override
	public ParameterType getByName(String parameterTypeName) throws Exception {
		return (ParameterType) getSession().createQuery("From ParameterType where label = :label").setParameter("label", parameterTypeName).uniqueResult();
	}

	/**
	 * getAllParameterTypes: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameterType#getAllParameterTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ParameterType> getAll() throws Exception {
		return (List<ParameterType>) getSession().createQuery("From ParameterType").list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameterType#save(lu.itrust.business.TS.data.parameter.ParameterType)
	 */
	@Override
	public void save(ParameterType parameterType) throws Exception {
		getSession().save(parameterType);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameterType#saveOrUpdate(lu.itrust.business.TS.data.parameter.ParameterType)
	 */
	@Override
	public void saveOrUpdate(ParameterType parameterType) throws Exception {
		getSession().saveOrUpdate(parameterType);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameterType#delete(lu.itrust.business.TS.data.parameter.ParameterType)
	 */
	@Override
	public void delete(ParameterType parameterType) throws Exception {
		getSession().delete(parameterType);
	}
}