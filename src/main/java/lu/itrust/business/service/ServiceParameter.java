/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.ParameterType;

/**
 * @author eom
 * 
 */
public interface ServiceParameter {

	Parameter get(int id);

	List<Parameter> findAll();

	List<Parameter> findByAnalysis(int idAnalysis);

	List<Parameter> findAll(int pageIndex, int pageSize);

	List<Parameter> findByAnalysis(int idAnalysis, int pageIndex, int pageSize);
	
	List<String> findAcronymByAnalysis(int idAnalysis);

	List<String> findAcronymByAnalysisAndType(int idAnalysis, String type);
	
	List<String> findAcronymByAnalysisAndType(int idAnalysis, ParameterType type);

	List<ExtendedParameter> findImpactByAnalysisAndType(int idAnalysis);

	List<ExtendedParameter> findExtendedByAnalysis(int idAnalysis);

	List<ExtendedParameter> findProbaByAnalysisAndType(int idAnalysis);

	List<ExtendedParameter> findExtendedByAnalysisAndType(int idAnalysis,
			ParameterType type);

	List<Parameter> findByAnalysisAndType(int idAnalysis, String type);

	List<Parameter> findByAnalysisAndTypeAndNoLazy(int idAnalysis,
			String type);

	List<Parameter> findByAnalysisAndType(int idAnalysis, int idType);

	List<Parameter> findByAnalysisAndType(int idAnalysis, ParameterType type);

	Parameter save(Parameter parameter);

	void saveOrUpdate(List<? extends Parameter> parameters);

	void saveOrUpdate(Parameter parameter);

	Parameter merge(Parameter parameter);

	void delete(Parameter parameter);

	void delete(int id);

}
