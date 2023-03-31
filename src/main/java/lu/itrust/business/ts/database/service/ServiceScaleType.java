/**
 * 
 */
package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.scale.ScaleType;

/**
 * @author eomar
 *
 */
public interface ServiceScaleType {
	
	void delete(List<Integer> scaleTypes);
	
	void delete(ScaleType scaleType);

	void deleteAll();

	boolean exists(int id);
	
	boolean exists(String name);
	
	List<ScaleType> findAll();
	
	List<ScaleType> findAllExpect(String ... names);

	List<ScaleType> findAllFree();

	ScaleType findByAcronym(String acronym);

	List<ScaleType> findFromAnalysis(Integer idAnalysis);

	ScaleType findOne(int id);

	ScaleType findOne(String name);

	ScaleType findOneByAnalysisId(Integer analysisId);

	ScaleType findOneQualitativeByAnalysisId(Integer idAnalysis);

	boolean hasAcronym(String acronym);
	
	int save(ScaleType scaleType);

	void saveOrUpdate(ScaleType scaleType);

}
