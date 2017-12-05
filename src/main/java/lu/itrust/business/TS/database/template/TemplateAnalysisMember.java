package lu.itrust.business.TS.database.template;

import java.io.Serializable;
import java.util.List;

import lu.itrust.business.TS.database.TemplateDAOService;

public interface TemplateAnalysisMember<T, ID extends Serializable> extends TemplateDAOService<T, ID> {

	boolean belongsToAnalysis(Integer analysisId, ID id);
	
	List<T> findByAnalysisId(Integer idAnalysis);
	
	T findOne(ID id,Integer idAnalysis);
	
}
