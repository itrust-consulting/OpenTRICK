/**
 * 
 */
package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Norm;

/**
 * @author eomar
 *
 */
public interface DAOMeasure {

	Measure findOne(int id);
	
	List<Measure> findByAnalysis(int idAnalysis);
	
	List<Measure> findByAnalysisAndNorm(int idAnalysis, int idNorm);
	
	List<Measure> findByAnalysisAndNorm(int idAnalysis, String norm);
	
	List<Measure> findByAnalysisAndNorm(int idAnalysis, Norm norm);

	Measure save(Measure measure);
	
	void saveOrUpdate(Measure measure);
	
	Measure merge(Measure measure);
	
	void delete(Measure measure);
	
	void delete(int id);
}
