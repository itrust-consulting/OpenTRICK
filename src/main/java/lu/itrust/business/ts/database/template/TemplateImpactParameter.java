/**
 * 
 */
package lu.itrust.business.ts.database.template;

import java.util.List;

import lu.itrust.business.ts.model.parameter.impl.ImpactParameter;
import lu.itrust.business.ts.model.scale.ScaleType;

/**
 * @author eomar
 *
 */
public interface TemplateImpactParameter extends TemplateAnalysisMember<ImpactParameter, Integer> {
	
	List<ImpactParameter> findByTypeAndAnalysisId(String type, Integer idAnalysis);
	
	List<ImpactParameter> findByTypeAndAnalysisId(ScaleType type, Integer idAnalysis);

	List<String> findAcronymByTypeAndAnalysisId(String type, Integer idAnalysis);

	List<ImpactParameter> findByIdAnalysisAndLevel(Integer idAnalysis, Integer level);

}
