/**
 * 
 */
package lu.itrust.business.ts.database.dao;

import java.util.List;

import lu.itrust.business.ts.model.scale.ScaleType;

/**
 * @author eomar
 *
 */
public interface DAOScaleType {
	
	ScaleType findOne(int id);
	
	ScaleType findOne(String name);

	ScaleType findByAcronym(String acronym);

	List<ScaleType> findAll();
	
	List<ScaleType> findAllExpect(String[] names);
	
	List<ScaleType> findAllFree();
	
	boolean exists(int id);

	boolean exists(String name);

	boolean hasAcronym(String acronym);

	int save(ScaleType scaleType);

	void saveOrUpdate(ScaleType scaleType);

	void delete(ScaleType scaleType);

	void delete(List<Integer> scaleTypes);

	void deleteAll();

	ScaleType findOneByAnalysisId(Integer analysisId);

	List<ScaleType> findFromAnalysis(Integer idAnalysis);

	ScaleType findOneQualitativeByAnalysisId(Integer idAnalysis);
}
