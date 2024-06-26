package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.impl.NormalMeasure;

/**
 * ServiceMeasure.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceMeasure {
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

	public List<NormalMeasure> getAllNormalMeasuresFromAnalysisByMeasureIdList(Integer idAnalysis, List<Integer> measures);

	public Measure save(Measure measure);

	public void saveOrUpdate(Measure measure);

	public Measure merge(Measure measure);

	public void delete(Integer id);

	public void delete(Measure measure);

	public List<Measure> getAllNotMaturityMeasuresFromAnalysisAndComputable(Integer idAnalysis);

	public List<Measure> getByIdAnalysisAndIds(Integer idAnalysis, List<Integer> measureIds);

	public Measure getByAnalysisAndStandardAndReference(Integer idAnalysis, String standard, String reference);

	public List<Measure> getByAnalysisAndStandardAndReferences(Integer idAnalysis, String standard, List<String> references);

	public List<Measure> getReferenceStartWith(Integer idAnalysis, String standard, String reference);

	public List<Measure> getByAnalysisIdStandardAndChapters(Integer idAnalysis, String standard27002, List<String> chapters);
}