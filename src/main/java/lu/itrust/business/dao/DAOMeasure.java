package lu.itrust.business.dao;

import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Standard;
import lu.itrust.business.TS.NormalMeasure;
import lu.itrust.business.TS.actionplan.ActionPlanMode;

/**
 * DAOMeasure.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since May 12, 2014
 */
public interface DAOMeasure {
	public Measure get(Integer id) throws Exception;

	public Measure getFromAnalysisById(Integer idAnalysis, Integer id) throws Exception;

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
	
	public Map<String,Measure> mappingAllFromAnalysisAndStandard(Integer idAnalysis, Integer idStandard);

	public int countNormalMeasure();

	public List<NormalMeasure> getAllNormalMeasure(int pageIndex, int pageSize);

	public List<Integer> getIdMeasuresImplementedByActionPlanTypeFromIdAnalysisAndStandard(int idAnalysis, String standard, ActionPlanMode appn);
}