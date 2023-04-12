package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOParameterType;
import lu.itrust.business.ts.database.service.ServiceParameterType;
import lu.itrust.business.ts.model.parameter.type.impl.ParameterType;

/**
 * ServiceParameterTypeImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
@Transactional(readOnly = true)
public class ServiceParameterTypeImpl implements ServiceParameterType {

	@Autowired
	private DAOParameterType daoParameterType;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceParameterType#get(int)
	 */
	@Override
	public ParameterType get(Integer id)  {
		return daoParameterType.get(id);
	}

	/**
	 * getByName: <br>
	 * Description
	 * 
	 * @param parameterTypeName
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceParameterType#getByName(java.lang.String)
	 */
	@Override
	public ParameterType getByName(String parameterTypeName)  {
		return daoParameterType.getByName(parameterTypeName);
	}

	/**
	 * getAllParameterTypes: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceParameterType#getAllParameterTypes()
	 */
	@Override
	public List<ParameterType> getAll()  {
		return daoParameterType.getAll();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param parameterType
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceParameterType#save(lu.itrust.business.ts.model.parameter.type.impl.ParameterType)
	 */
	@Transactional
	@Override
	public void save(ParameterType parameterType)  {
		daoParameterType.save(parameterType);

	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param parameterType
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceParameterType#saveOrUpdate(lu.itrust.business.ts.model.parameter.type.impl.ParameterType)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ParameterType parameterType)  {
		daoParameterType.saveOrUpdate(parameterType);

	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param parameterType
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceParameterType#delete(lu.itrust.business.ts.model.parameter.type.impl.ParameterType)
	 */
	@Transactional
	@Override
	public void delete(ParameterType parameterType)  {
		daoParameterType.delete(parameterType);
	}
}