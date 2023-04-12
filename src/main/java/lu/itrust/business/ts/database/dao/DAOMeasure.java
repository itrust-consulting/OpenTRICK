package lu.itrust.business.ts.database.dao;

import java.util.List;
import java.util.Map;

import lu.itrust.business.ts.model.actionplan.ActionPlanMode;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.impl.MaturityMeasure;
import lu.itrust.business.ts.model.standard.measure.impl.NormalMeasure;

/**
 * DAOMeasure.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since May 12, 2014
 */
public interface DAOMeasure {
	public Measure get(Integer id);

	public Measure getFromAnalysisById(Integer idAnalysis, Integer id);

	public Measure getFromAnalysisAndStandardAndReference(Integer idAnalysis, Integer idStandard, String reference);

	public boolean belongsToAnalysis(Integer analysisId, Integer measureId);

	public List<Measure> getAll();

	public List<Measure> getAllFromAnalysis(Integer idAnalysis);

	public List<Measure> getAllComputableFromAnalysis(Integer idAnalysis);

	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, Integer idStandard);

	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, String standard);

	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, Standard standard);

	public List<NormalMeasure> getAllNormalMeasuresFromAnalysis(Integer idAnalysis);

	public List<NormalMeasure> getAllNormalMeasuresFromAnalysisAndComputable(Integer idAnalysis);

	public List<Measure> getAllNotMaturityMeasuresFromAnalysisAndComputable(Integer idAnalysis);
	
	public List<Measure> getAllNotMaturityMeasuresFromAnalysisByMeasureIdList(Integer idAnalysis, List<Integer> measures);
	
	public List<NormalMeasure> getAllNormalMeasuresFromAnalysisByMeasureIdList(Integer idAnalysis, List<Integer> measures);

	public Measure save(Measure measure);

	public void saveOrUpdate(Measure measure);

	public Measure merge(Measure measure);

	public void delete(Integer id);

	public void delete(Measure measure);

	public Map<String, Measure> mappingAllFromAnalysisAndStandard(Integer idAnalysis, Integer idStandard);

	public int countNormalMeasure();

	public List<NormalMeasure> getAllNormalMeasure(int pageIndex, int pageSize);

	public List<Integer> getIdMeasuresImplementedByActionPlanTypeFromIdAnalysisAndStandard(int idAnalysis, String standard, ActionPlanMode appn);

	public List<Measure> getByIdAnalysisAndIds(Integer idAnalysis, List<Integer> ids);

	public Measure getByAnalysisAndStandardAndReference(Integer idAnalysis, String standard, String reference);

	public List<Measure> getByAnalysisAndStandardAndReferences(Integer idAnalysis, String standard, List<String> references);

	public List<Measure> getReferenceStartWith(Integer idAnalysis, String standard, String reference);

	public List<Measure> getByAnalysisIdStandardAndChapters(Integer idAnalysis, String standard, List<String> chapters);

	public MaturityMeasure getMaturityMeasure(Integer id) throws Exception;
}