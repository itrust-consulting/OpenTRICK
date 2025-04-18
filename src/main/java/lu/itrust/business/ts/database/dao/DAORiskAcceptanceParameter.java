/**
 * 
 */
package lu.itrust.business.ts.database.dao;

import lu.itrust.business.ts.database.template.TemplateRiskAcceptanceParameter;

/**
 * @author eomar
 *
 */
public interface DAORiskAcceptanceParameter extends TemplateRiskAcceptanceParameter {

	boolean existsByAnalysisId(Integer analysisId);

}
