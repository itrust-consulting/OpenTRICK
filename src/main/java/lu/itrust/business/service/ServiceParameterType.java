/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.ParameterType;
import lu.itrust.business.dao.DAOParameterType;

/**
 * @author oensuifudine
 *
 */
public interface ServiceParameterType {
	
	public ParameterType get(int id)throws Exception;
	
	public ParameterType get(String parameterTypeName)throws Exception;
	
	public List<ParameterType> loadAll() throws Exception;
	
	public void save(ParameterType parameterType) throws Exception;
	
	public void saveOrUpdate(ParameterType parameterType) throws Exception;
	
	public void delete(ParameterType parameterType) throws Exception;
	
	public DAOParameterType getDaoParameterType();

}
