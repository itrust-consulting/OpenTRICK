package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.parameter.AcronymParameter;
import lu.itrust.business.TS.model.parameter.DynamicParameter;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.parameter.ParameterType;

/**
 * ServiceParameter.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceParameter {
	public Parameter get(Integer id) throws Exception;

	public Parameter getFromAnalysisById(Integer idAnalysis, Integer idParameter) throws Exception;

	public Parameter getFromAnalysisByTypeAndDescription(Integer idAnalysis, String parameterType, String description) throws Exception;

	public boolean belongsToAnalysis(Integer analysisId, Integer parameterId) throws Exception;

	public List<String> getExtendedParameterAcronymsFromAnalysis(Integer idAnalysis) throws Exception;

	public List<String> getExtendedParameterAcronymsFromAnalysisByType(Integer idAnalysis, String type) throws Exception;

	public List<String> getExtendedParameterAcronymsFromAnalysisByType(Integer idAnalysis, ParameterType type) throws Exception;

	public List<Parameter> getAll() throws Exception;

	public List<Parameter> getAllFromAnalysis(Integer idAnalysis) throws Exception;

	public List<Parameter> getAllFromAnalysisByPageAndSizeIndex(Integer idAnalysis, Integer pageIndex, Integer pageSize) throws Exception;

	public List<Parameter> getAllByPageAndSizeIndex(Integer pageIndex, Integer pageSize) throws Exception;

	public List<Parameter> getAllFromAnalysisByType(Integer idAnalysis, Integer idType) throws Exception;

	public List<Parameter> getAllFromAnalysisByType(Integer idAnalysis, String type) throws Exception;

	public List<Parameter> getAllFromAnalysisByType(Integer idAnalysis, ParameterType type) throws Exception;

	public List<Parameter> getAllInitialisedFromAnalysisByType(Integer idAnalysis, String type) throws Exception;

	public List<ExtendedParameter> getAllExtendedFromAnalysis(Integer idAnalysis) throws Exception;

	public List<ExtendedParameter> getAllExtendedFromAnalysisAndType(Integer idAnalysis, ParameterType type) throws Exception;

	public List<ExtendedParameter> getAllImpactFromAnalysis(Integer idAnalysis) throws Exception;

	public List<ExtendedParameter> getAllProbabilityFromAnalysis(Integer idAnalysis) throws Exception;
	
	public Parameter save(Parameter parameter) throws Exception;

	public void saveOrUpdate(Parameter parameter) throws Exception;

	public void saveOrUpdate(List<? extends Parameter> parameters) throws Exception;

	public Parameter merge(Parameter parameter) throws Exception;

	public void delete(Integer id) throws Exception;

	public void delete(Parameter parameter) throws Exception;

	public Parameter getByAnalysisIdAndDescription(Integer idAnalysis, String description);

	/**
	 * Gets a list of all parameters that are considered to be used as variable
	 * when evaluating an arithmetic expression. The parameter acronym is then
	 * replaced by the value of the respective parameter.
	 * @param idAnalysis The identifier of the analysis for which parameters shall be retrieved.
	 * @author Steve Muller (SMU), itrust consulting s.à r.l.
	 * @since Jun 10, 2015
	 */
	public List<AcronymParameter> getAllExpressionParametersFromAnalysis(Integer idAnalysis) throws Exception;

	/**
	 * Gets all dynamic parameters for the given analysis.
	 * @param idAnalysis The identifier of the analysis for which parameters shall be retrieved.
	 * @author Steve Muller (SMU), itrust consulting s.à r.l.
	 * @since Jun 11, 2015
	 */
	public List<DynamicParameter> getDynamicParametersFromAnalysis(Integer idAnalysis) throws Exception;
}