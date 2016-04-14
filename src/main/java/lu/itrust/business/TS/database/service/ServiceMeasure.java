package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;

/**
 * ServiceMeasure.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceMeasure {
	public Measure get(Integer id) ;

	public Measure getFromAnalysisById(Integer idAnalysis, Integer id) ;

	public Measure getFromAnalysisAndStandardAndReference(Integer idAnalysis, Integer idStandard, String reference) ;
	
	public boolean belongsToAnalysis(Integer analysisId, Integer measureId) ;

	public List<Measure> getAll() ;

	public List<Measure> getAllFromAnalysis(Integer idAnalysis) ;

	public List<Measure> getSOAMeasuresFromAnalysis(Integer idAnalysis) ;

	public List<Measure> getAllComputableFromAnalysis(Integer idAnalysis) ;

	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, Integer idStandard) ;

	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, String standard) ;

	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, Standard standard) ;

	public List<NormalMeasure> getAllNormalMeasuresFromAnalysis(Integer idAnalysis) ;

	public List<NormalMeasure> getAllNormalMeasuresFromAnalysisAndComputable(Integer idAnalysis) ;

	public List<NormalMeasure> getAllNormalMeasuresFromAnalysisByMeasureIdList(Integer idAnalysis, List<Integer> measures) ;

	public Measure save(Measure measure) ;

	public void saveOrUpdate(Measure measure) ;

	public Measure merge(Measure measure) ;

	public void delete(Integer id) ;

	public void delete(Measure measure) ;

	public List<Measure> getAllNotMaturityMeasuresFromAnalysisAndComputable(Integer idAnalysis) ;

	public List<Measure> getByIdAnalysisAndIds(Integer idAnalysis, List<Integer> measureIds);
}