/**
 * 
 */
package lu.itrust.business.TS.database.dao;

import lu.itrust.business.TS.database.template.TemplateRiskAcceptanceParameter;

/**
 * @author eomar
 *
 */
public interface DAORiskAcceptanceParameter extends TemplateRiskAcceptanceParameter {

	boolean existsByAnalysisId(Integer analysisId);

}
