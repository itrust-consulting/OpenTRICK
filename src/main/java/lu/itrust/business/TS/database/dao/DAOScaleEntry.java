/**
 * 
 */
package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.scale.ScaleEntry;

/**
 * @author eomar
 *
 */
public interface DAOScaleEntry {

	ScaleEntry findOne(int id);

	List<ScaleEntry> findAll();

	boolean exists(int id);

	int save(ScaleEntry scaleEntry);

	void saveOrUpdate(ScaleEntry scaleEntry);

	void delete(ScaleEntry scaleEntry);

	void delete(List<ScaleEntry> scaleEntries);

	void deleteAll();

}
