/**
 * 
 */
package lu.itrust.business.ts.database.template;

import java.util.List;

import lu.itrust.business.ts.model.parameter.impl.DynamicParameter;

/**
 * @author eomar
 *
 */
public interface TemplateDynamicParameter extends TemplateAnalysisMember<DynamicParameter, Integer> {
	
	List<String> findAcronymByAnalysisId(Integer idAnalysis);
}
