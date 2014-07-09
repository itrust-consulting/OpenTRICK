package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.ParameterType;

/**
 * DAOParameterType.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOParameterType {
	public ParameterType get(Integer id) throws Exception;

	public ParameterType getByName(String parameterTypeName) throws Exception;

	public List<ParameterType> getAll() throws Exception;

	public void save(ParameterType parameterType) throws Exception;

	public void saveOrUpdate(ParameterType parameterType) throws Exception;

	public void delete(ParameterType parameterType) throws Exception;
}