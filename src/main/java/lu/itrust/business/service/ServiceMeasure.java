package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.NormMeasure;

/**
 * ServiceMeasure.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceMeasure {
	public Measure get(Integer id) throws Exception;

	public Measure getFromAnalysisById(Integer idAnalysis, Integer id) throws Exception;

	public boolean belongsToAnalysis(Integer analysisId, Integer measureId) throws Exception;

	public List<Measure> getAll() throws Exception;

	public List<Measure> getAllFromAnalysis(Integer idAnalysis) throws Exception;

	public List<Measure> getSOAMeasuresFromAnalysis(Integer idAnalysis) throws Exception;

	public List<Measure> getAllComputableFromAnalysis(Integer idAnalysis) throws Exception;

	public List<Measure> getAllFromAnalysisAndNorm(Integer idAnalysis, Integer idNorm) throws Exception;

	public List<Measure> getAllFromAnalysisAndNorm(Integer idAnalysis, String norm) throws Exception;

	public List<Measure> getAllFromAnalysisAndNorm(Integer idAnalysis, Norm norm) throws Exception;

	public List<NormMeasure> getAllNormMeasuresFromAnalysis(Integer idAnalysis) throws Exception;

	public List<NormMeasure> getAllNormMeasuresFromAnalysisAndComputable(Integer idAnalysis) throws Exception;

	public List<NormMeasure> getAllNormMeasuresFromAnalysisByMeasureIdList(Integer idAnalysis, List<Integer> measures) throws Exception;

	public Measure save(Measure measure) throws Exception;

	public void saveOrUpdate(Measure measure) throws Exception;

	public Measure merge(Measure measure) throws Exception;

	public void delete(Integer id) throws Exception;

	public void delete(Measure measure) throws Exception;
}