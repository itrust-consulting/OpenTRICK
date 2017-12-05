/**
 * 
 */
package lu.itrust.business.TS.database.template;

import java.util.List;

import lu.itrust.business.TS.model.parameter.impl.DynamicParameter;

/**
 * @author eomar
 *
 */
public interface TemplateDynamicParameter extends TemplateAnalysisMember<DynamicParameter, Integer> {
	
	List<String> findAcronymByAnalysisId(Integer idAnalysis);
}
