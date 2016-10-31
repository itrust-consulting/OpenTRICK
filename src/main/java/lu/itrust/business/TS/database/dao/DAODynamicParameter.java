/**
 * 
 */
package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.database.TemplateDAOService;
import lu.itrust.business.TS.model.parameter.impl.DynamicParameter;

/**
 * @author eomar
 *
 */
public interface DAODynamicParameter extends TemplateDAOService<DynamicParameter, Integer> {
	List<String> findAcronymByAnalysisId(Integer idAnalysis);
}
