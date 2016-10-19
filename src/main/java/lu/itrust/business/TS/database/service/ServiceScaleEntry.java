/**
 * 
 */
package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.scale.ScaleEntry;

/**
 * @author eomar
 *
 */
public interface ServiceScaleEntry {

	ScaleEntry findOne(int id);

	List<ScaleEntry> findAll();

	boolean exists(int id);

	int save(ScaleEntry scaleEntry);

	void saveOrUpdate(ScaleEntry scaleEntry);

	void delete(ScaleEntry scaleEntry);

	void delete(List<ScaleEntry> scaleEntries);

	void deleteAll();

}
