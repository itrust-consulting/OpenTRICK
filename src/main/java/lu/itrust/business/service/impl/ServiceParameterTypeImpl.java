package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.ParameterType;
import lu.itrust.business.dao.DAOParameterType;
import lu.itrust.business.service.ServiceParameterType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceParameterTypeImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
@Transactional
public class ServiceParameterTypeImpl implements ServiceParameterType {

	@Autowired
	private DAOParameterType daoParameterType;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameterType#get(int)
	 */
	@Override
	public ParameterType get(Integer id) throws Exception {
		return daoParameterType.get(id);
	}

	/**
	 * getByName: <br>
	 * Description
	 * 
	 * @param parameterTypeName
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameterType#getByName(java.lang.String)
	 */
	@Override
	public ParameterType getByName(String parameterTypeName) throws Exception {
		return daoParameterType.getByName(parameterTypeName);
	}

	/**
	 * getAllParameterTypes: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameterType#getAllParameterTypes()
	 */
	@Override
	public List<ParameterType> getAll() throws Exception {
		return daoParameterType.getAll();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param parameterType
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameterType#save(lu.itrust.business.TS.ParameterType)
	 */
	@Transactional
	@Override
	public void save(ParameterType parameterType) throws Exception {
		daoParameterType.save(parameterType);

	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param parameterType
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameterType#saveOrUpdate(lu.itrust.business.TS.ParameterType)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ParameterType parameterType) throws Exception {
		daoParameterType.saveOrUpdate(parameterType);

	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param parameterType
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameterType#delete(lu.itrust.business.TS.ParameterType)
	 */
	@Transactional
	@Override
	public void delete(ParameterType parameterType) throws Exception {
		daoParameterType.delete(parameterType);
	}
}