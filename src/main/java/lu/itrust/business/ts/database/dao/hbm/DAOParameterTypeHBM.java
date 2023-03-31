package lu.itrust.business.ts.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOParameterType;
import lu.itrust.business.ts.model.parameter.type.impl.ParameterType;

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
	 * @see lu.itrust.business.ts.database.dao.DAOParameterType#get(int)
	 */
	@Override
	public ParameterType get(Integer id)  {
		return (ParameterType) getSession().get(ParameterType.class, id);
	}

	/**
	 * getByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOParameterType#getByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ParameterType getByName(String parameterTypeName)  {
		return (ParameterType) getSession().createQuery("From ParameterType where name = :name").setParameter("name", parameterTypeName).uniqueResultOptional().orElse(null);
	}

	/**
	 * getAllParameterTypes: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOParameterType#getAllParameterTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ParameterType> getAll()  {
		return (List<ParameterType>) getSession().createQuery("From ParameterType").getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOParameterType#save(lu.itrust.business.ts.model.parameter.type.impl.ParameterType)
	 */
	@Override
	public void save(ParameterType parameterType)  {
		getSession().save(parameterType);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOParameterType#saveOrUpdate(lu.itrust.business.ts.model.parameter.type.impl.ParameterType)
	 */
	@Override
	public void saveOrUpdate(ParameterType parameterType)  {
		getSession().saveOrUpdate(parameterType);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOParameterType#delete(lu.itrust.business.ts.model.parameter.type.impl.ParameterType)
	 */
	@Override
	public void delete(ParameterType parameterType)  {
		getSession().delete(parameterType);
	}
}