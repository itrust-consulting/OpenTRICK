/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.ParameterType;
import lu.itrust.business.dao.DAOParameterType;
import lu.itrust.business.service.ServiceParameterType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author oensuifudine
 * 
 */
@Service
@Transactional
public class ServiceParameterTypeImpl implements ServiceParameterType {

	@Autowired
	private DAOParameterType daoParameterType;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceParameterType#get(int)
	 */
	@Override
	public ParameterType get(int id) throws Exception {
		// TODO Auto-generated method stub
		return daoParameterType.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceParameterType#get(java.lang.String)
	 */
	@Override
	public ParameterType get(String parameterTypeName) throws Exception {
		// TODO Auto-generated method stub
		return daoParameterType.get(parameterTypeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceParameterType#loadAll()
	 */
	@Override
	public List<ParameterType> loadAll() throws Exception {
		// TODO Auto-generated method stub
		return daoParameterType.loadAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceParameterType#save(lu.itrust.business
	 * .TS.ParameterType)
	 */
	@Transactional
	@Override
	public void save(ParameterType parameterType) throws Exception {
		daoParameterType.save(parameterType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceParameterType#saveOrUpdate(lu.itrust
	 * .business.TS.ParameterType)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ParameterType parameterType) throws Exception {
		daoParameterType.saveOrUpdate(parameterType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceParameterType#delete(lu.itrust.business
	 * .TS.ParameterType)
	 */
	@Transactional
	@Override
	public void delete(ParameterType parameterType) throws Exception {
		daoParameterType.delete(parameterType);
	}

	/**
	 * @return the daoParameterType
	 */
	public DAOParameterType getDaoParameterType() {
		return daoParameterType;
	}

	/**
	 * @param daoParameterType
	 *            the daoParameterType to set
	 */
	public void setDaoParameterType(DAOParameterType daoParameterType) {
		this.daoParameterType = daoParameterType;
	}

}
