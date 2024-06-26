package lu.itrust.business.ts.database.dao;

import java.util.List;

import lu.itrust.business.ts.model.parameter.type.impl.ParameterType;

/**
 * DAOParameterType.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOParameterType {
	public ParameterType get(Integer id);

	public ParameterType getByName(String parameterTypeName);

	public List<ParameterType> getAll();

	public void save(ParameterType parameterType);

	public void saveOrUpdate(ParameterType parameterType);

	public void delete(ParameterType parameterType);
}