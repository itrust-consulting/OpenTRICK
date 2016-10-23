package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.parameter.type.impl.ParameterType;

/**
 * ServiceParameterType.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceParameterType {
	public ParameterType get(Integer id);

	public ParameterType getByName(String parameterTypeName);

	public List<ParameterType> getAll();

	public void save(ParameterType parameterType);

	public void saveOrUpdate(ParameterType parameterType);

	public void delete(ParameterType parameterType);
}