package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.data.parameter.ParameterType;

/**
 * ServiceParameterType.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceParameterType {
	public ParameterType get(Integer id) throws Exception;

	public ParameterType getByName(String parameterTypeName) throws Exception;

	public List<ParameterType> getAll() throws Exception;

	public void save(ParameterType parameterType) throws Exception;

	public void saveOrUpdate(ParameterType parameterType) throws Exception;

	public void delete(ParameterType parameterType) throws Exception;
}