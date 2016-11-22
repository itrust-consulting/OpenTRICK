/**
 * 
 */
package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.database.TemplateDAOService;
import lu.itrust.business.TS.model.parameter.impl.DynamicParameter;

/**
 * @author eomar
 *
 */
public interface ServiceDynamicParameter extends TemplateDAOService<DynamicParameter, Integer> {
	List<String> findAcronymByAnalysisId(Integer idAnalysis);
}
