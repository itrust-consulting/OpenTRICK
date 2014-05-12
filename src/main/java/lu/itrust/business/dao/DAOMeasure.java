package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.NormMeasure;

/**
 * DAOMeasure.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since May 12, 2014
 */
public interface DAOMeasure {

	public Measure get(int id) throws Exception;

	public Measure getMeasureFromAnalysisIdById(Integer idAnalysis, Integer id) throws Exception;

	public boolean belongsToAnalysis(int assetId, int analysisId) throws Exception;

	public List<Measure> getAllMeasures() throws Exception;

	public List<Measure> getAllFromAnalysisId(int idAnalysis) throws Exception;

	public List<Measure> getSOAMeasuresFromAnalysis(int idAnalysis) throws Exception;

	public List<Measure> getAllMeasuresFromAnalysisIdAndComputable(int idAnalysis) throws Exception;

	public List<Measure> getAllMeasuresFromAnalysisIdAndNormId(int idAnalysis, int idNorm) throws Exception;

	public List<Measure> getAllMeasuresFromAnalysisIdAndNormLabel(int idAnalysis, String norm) throws Exception;

	public List<Measure> getAllMeasuresFromAnalysisIdAndNorm(int idAnalysis, Norm norm) throws Exception;

	public List<NormMeasure> getAllNormMeasuresFromAnalysisId(int idAnalysis) throws Exception;

	public List<NormMeasure> getAllNormMeasuresFromAnalysisIdAndComputable(int idAnalysis) throws Exception;

	public List<NormMeasure> getAllAnalysisNormsFromAnalysisByMeasureIdList(int idAnalysis, List<Integer> measures) throws Exception;

	public Measure save(Measure measure) throws Exception;

	public void saveOrUpdate(Measure measure) throws Exception;

	public Measure merge(Measure measure) throws Exception;

	public void delete(int id) throws Exception;

	public void delete(Measure measure) throws Exception;
}