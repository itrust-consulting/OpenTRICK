/**
 * 
 */
package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.database.TemplateDAOService;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;
import lu.itrust.business.TS.model.parameter.type.impl.ParameterType;

/**
 * @author eomar
 *
 */
public interface DAOSimpleParameter extends TemplateDAOService<SimpleParameter, Integer> {

	List<SimpleParameter> findByTypeAndAnalysisId(String type, Integer idAnalysis);

	List<SimpleParameter> findByTypeAndAnalysisId(ParameterType type, Integer idAnalysis);

	SimpleParameter findByAnalysisIdAndDescription(Integer idAnalysis, String description);
	
	SimpleParameter findByAnalysisIdAndTypeAndDescription(Integer idAnalysis, String type, String description);
	
}
