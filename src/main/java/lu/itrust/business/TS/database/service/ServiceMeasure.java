package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.data.basic.Measure;
import lu.itrust.business.TS.data.basic.NormalMeasure;
import lu.itrust.business.TS.data.basic.Standard;

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

	public Measure getFromAnalysisAndStandardAndReference(Integer idAnalysis, Integer idStandard, String reference) throws Exception;
	
	public boolean belongsToAnalysis(Integer analysisId, Integer measureId) throws Exception;

	public List<Measure> getAll() throws Exception;

	public List<Measure> getAllFromAnalysis(Integer idAnalysis) throws Exception;

	public List<Measure> getSOAMeasuresFromAnalysis(Integer idAnalysis) throws Exception;

	public List<Measure> getAllComputableFromAnalysis(Integer idAnalysis) throws Exception;

	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, Integer idStandard) throws Exception;

	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, String standard) throws Exception;

	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, Standard standard) throws Exception;

	public List<NormalMeasure> getAllNormalMeasuresFromAnalysis(Integer idAnalysis) throws Exception;

	public List<NormalMeasure> getAllNormalMeasuresFromAnalysisAndComputable(Integer idAnalysis) throws Exception;

	public List<NormalMeasure> getAllNormalMeasuresFromAnalysisByMeasureIdList(Integer idAnalysis, List<Integer> measures) throws Exception;

	public Measure save(Measure measure) throws Exception;

	public void saveOrUpdate(Measure measure) throws Exception;

	public Measure merge(Measure measure) throws Exception;

	public void delete(Integer id) throws Exception;

	public void delete(Measure measure) throws Exception;

	public List<Measure> getAllNotMaturityMeasuresFromAnalysisAndComputable(Integer idAnalysis) throws Exception;
}