package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.ParameterType;

/**
 * DAOParameter.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOParameter {

	Parameter get(int id);

	List<Parameter> findAll();
	
	List<Parameter> findByAnalysis(int idAnalysis);

	List<Parameter> findAll(int pageIndex, int pageSize);

	List<Parameter> findByAnalysis(int idAnalysis, int pageIndex, int pageSize);
	
	List<ExtendedParameter> findExtendedByAnalysis(int idAnalysis);
	
	List<ExtendedParameter> findImpactByAnalysisAndType(int idAnalysis);
	
	List<ExtendedParameter> findProbaByAnalysisAndType(int idAnalysis);
	
	List<ExtendedParameter> findExtendedByAnalysisAndType(int idAnalysis, ParameterType type);

	List<Parameter> findByAnalysisAndType(int idAnalysis, String type);

	List<Parameter> findByAnalysisAndType(int idAnalysis, int idType);

	List<Parameter> findByAnalysisAndType(int idAnalysis,
			ParameterType type);
	
	void saveOrUpdate(List<? extends Parameter> parameters);

	Parameter save(Parameter parameter);

	void saveOrUpdate(Parameter parameter);

	Parameter merge(Parameter parameter);

	void delete(Parameter parameter);

	void delete(int id);

	

	

	
}
