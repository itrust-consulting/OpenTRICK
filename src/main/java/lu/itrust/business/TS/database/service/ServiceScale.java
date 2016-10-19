/**
 * 
 */
package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.scale.Scale;

/**
 * @author eomar
 *
 */
public interface ServiceScale {
	
	Scale findOne(int id);

	Scale findByName(String name);

	Scale findByAcronym(String acronym);

	List<Scale> findAll();

	boolean exists(int id);

	boolean hasName(String name);

	boolean hasAcronym(String acronym);

	int save(Scale scale);

	void saveOrUpdate(Scale scale);

	void delete(Scale scale);

	void delete(List<Scale> scales);

	void deleteAll();
}
