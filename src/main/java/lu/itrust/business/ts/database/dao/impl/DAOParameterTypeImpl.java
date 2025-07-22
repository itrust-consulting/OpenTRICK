package lu.itrust.business.ts.database.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOParameterType;
import lu.itrust.business.ts.model.parameter.type.impl.ParameterType;

/**
 * DAOParameterTypeImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 31 janv. 2013
 */
@Repository
public class DAOParameterTypeImpl extends DAOHibernate implements DAOParameterType {

	/**
	 * Constructor: <br>
	 */
	public DAOParameterTypeImpl() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOParameterTypeImpl(Session session) {
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
	@Override
	public ParameterType getByName(String parameterTypeName)  {
		return  createQueryWithCache("From ParameterType where name = :name",ParameterType.class).setParameter("name", parameterTypeName).uniqueResultOptional().orElse(null);
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
		return (List<ParameterType>) createQueryWithCache("From ParameterType").getResultList();
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