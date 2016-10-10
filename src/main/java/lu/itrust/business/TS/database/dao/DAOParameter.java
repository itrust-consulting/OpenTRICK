package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.parameter.AcronymParameter;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.parameter.ParameterType;

/**
 * DAOParameter.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOParameter {
	public Parameter get(Integer id);

	public Parameter getFromAnalysisById(Integer idAnalysis, Integer idParameter);

	public Parameter getFromAnalysisByTypeAndDescription(Integer idAnalysis, String parameterType, String description);

	public boolean belongsToAnalysis(Integer analysisId, Integer parameterId);

	public List<String> getExtendedParameterAcronymsFromAnalysis(Integer idAnalysis);

	public List<String> getExtendedParameterAcronymsFromAnalysisByType(Integer idAnalysis, String type);

	public List<String> getExtendedParameterAcronymsFromAnalysisByType(Integer idAnalysis, ParameterType type);

	public List<Parameter> getAll();

	public List<Parameter> getAllFromAnalysis(Integer idAnalysis);

	public List<Parameter> getAllFromAnalysisByPageAndSizeIndex(Integer idAnalysis, Integer pageIndex, Integer pageSize);

	public List<Parameter> getAllByPageAndSizeIndex(Integer pageIndex, Integer pageSize);

	public List<Parameter> getAllFromAnalysisByType(Integer idAnalysis, Integer idType);

	public List<Parameter> getAllFromAnalysisByType(Integer idAnalysis, String... types);

	public List<Parameter> getAllFromAnalysisByType(Integer idAnalysis, ParameterType type);

	public List<Parameter> getAllInitialisedFromAnalysisByType(Integer idAnalysis, String type);

	public List<AcronymParameter> findAllAcronymParameterByAnalysisId(Integer idAnalysis);

	public List<ExtendedParameter> getAllExtendedFromAnalysisAndType(Integer idAnalysis, ParameterType type);

	public List<ExtendedParameter> getAllImpactFromAnalysis(Integer idAnalysis);

	public List<ExtendedParameter> getAllProbabilityFromAnalysis(Integer idAnalysis);

	public Parameter save(Parameter parameter);

	public void saveOrUpdate(Parameter parameter);

	public void saveOrUpdate(List<? extends Parameter> parameters);

	public Parameter merge(Parameter parameter);

	public void delete(Integer id);

	public void delete(Parameter parameter);

	public Parameter getByAnalysisIdAndDescription(Integer idAnalysis, String description);

	/**
	 * Gets a list of all parameters that are considered to be used as variable
	 * when evaluating an arithmetic expression. The parameter acronym is then
	 * replaced by the value of the respective parameter.
	 * 
	 * @param idAnalysis
	 *            The identifier of the analysis for which parameters shall be
	 *            retrieved.
	 * @author Steve Muller (SMU), itrust consulting s.à r.l.
	 * @since Jun 10, 2015
	 */
	public List<AcronymParameter> getAllExpressionParametersFromAnalysis(Integer idAnalysis);

	public List<AcronymParameter> findAllDynamicByAnalysisId(Integer idAnalysis);
}