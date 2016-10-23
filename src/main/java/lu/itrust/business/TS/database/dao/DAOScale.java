package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.scale.Scale;

public interface DAOScale {
	
	Scale findOne(int id);
	
	List<Scale> findAll();
	
	boolean exists(int id);
	
	int save(Scale scale);
	
	void saveOrUpdate(Scale scale);
	
	void delete(Scale scale);
	
	void delete(List<Integer> scales);
	
	void deleteAll();

}
