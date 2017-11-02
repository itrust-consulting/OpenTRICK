/**
 * 
 */
package lu.itrust.business.TS.database.service;

import lu.itrust.business.TS.database.template.TemplateRiskAcceptanceParameter;

/**
 * @author eomar
 *
 */
public interface ServiceRiskAcceptanceParameter extends TemplateRiskAcceptanceParameter {

	boolean existsByAnalysisId(Integer analysisId);

}
