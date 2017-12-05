/**
 * 
 */
package lu.itrust.business.TS.database.template;

import java.util.List;

import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;

/**
 * @author eomar
 *
 */
public interface TemplateLikelihoodParameter extends TemplateAnalysisMember<LikelihoodParameter, Integer> {

	List<String> findAcronymByAnalysisId(Integer idAnalysis);

	Integer findMaxLevelByIdAnalysis(Integer idAnalysis);

}
