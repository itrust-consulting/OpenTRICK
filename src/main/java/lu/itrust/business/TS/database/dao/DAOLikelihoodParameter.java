/**
 * 
 */
package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.database.TemplateDAOService;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;

/**
 * @author eomar
 *
 */
public interface DAOLikelihoodParameter extends TemplateDAOService<LikelihoodParameter, Integer> {
	List<String> findAcronymByAnalysisId(Integer idAnalysis);
}
