/**
 * 
 */
package lu.itrust.business.ts.database.service;

import lu.itrust.business.ts.database.template.TemplateRiskAcceptanceParameter;

/**
 * @author eomar
 *
 */
public interface ServiceRiskAcceptanceParameter extends TemplateRiskAcceptanceParameter {

	boolean existsByAnalysisId(Integer analysisId);

}
