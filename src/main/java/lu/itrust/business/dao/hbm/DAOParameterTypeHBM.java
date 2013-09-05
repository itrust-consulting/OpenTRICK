package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.ParameterType;
import lu.itrust.business.dao.DAOParameterType;

/**
 * DAOParameterTypeHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since 31 janv. 2013
 */
public class DAOParameterTypeHBM extends DAOHibernate implements
		DAOParameterType {

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameterType#get(int)
	 */
	@Override
	public ParameterType get(int id) throws Exception {

		return (ParameterType) getSession().get(ParameterType.class, id);

	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameterType#get(java.lang.String)
	 */
	@Override
	public ParameterType get(String parameterTypeName) throws Exception {
		return (ParameterType) getSession()
				.createQuery("From ParameterType where type = :type")
				.setString("type", parameterTypeName).uniqueResult();

	}

	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameterType#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ParameterType> loadAll() throws Exception {
		return (List<ParameterType>) getSession().createQuery(
				"From ParameterType").list();

	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameterType#save(lu.itrust.business.TS.ParameterType)
	 */
	@Override
	public void save(ParameterType parameterType) throws Exception {
		getSession().save(parameterType);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameterType#saveOrUpdate(lu.itrust.business.TS.ParameterType)
	 */
	@Override
	public void saveOrUpdate(ParameterType parameterType) throws Exception {
		getSession().saveOrUpdate(parameterType);

	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameterType#delete(lu.itrust.business.TS.ParameterType)
	 */
	@Override
	public void delete(ParameterType parameterType) throws Exception {
		getSession().delete(parameterType);
	}

}
