package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.scale.Scale;

public interface DAOScale {
	
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
