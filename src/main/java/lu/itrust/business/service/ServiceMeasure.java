/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.NormMeasure;

/**
 * @author eomar
 * 
 */
public interface ServiceMeasure {

	Measure findOne(int id);

	List<Measure> findByAnalysis(int idAnalysis);

	List<NormMeasure> findNormMeasureByAnalysis(int idAnalysis);

	List<NormMeasure> findNormMeasureByAnalysisAndComputable(int idAnalysis);

	Measure findByIdAndAnalysis(Integer id, Integer idAnalysis);

	List<Measure> findByAnalysisAndNorm(int idAnalysis, int idNorm);

	List<Measure> findByAnalysisAndNorm(int idAnalysis, String norm);

	List<Measure> findByAnalysisAndNorm(int idAnalysis, Norm norm);

	Measure save(Measure measure);

	void saveOrUpdate(Measure measure);

	Measure merge(Measure measure);

	void delete(Measure measure);

	void delete(int id);
}
