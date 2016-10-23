package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.parameter.IAcronymParameter;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.IImpactParameter;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.IProbabilityParameter;
import lu.itrust.business.TS.model.parameter.impl.DynamicParameter;
import lu.itrust.business.TS.model.parameter.type.impl.ParameterType;

/**
 * ServiceParameter.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceParameter {
	public IParameter get(Integer id);

	public IParameter getFromAnalysisById(Integer idAnalysis, Integer idParameter);

	public IParameter getFromAnalysisByTypeAndDescription(Integer idAnalysis, String parameterType, String description);

	public boolean belongsToAnalysis(Integer analysisId, Integer parameterId);

	public List<String> getExtendedParameterAcronymsFromAnalysis(Integer idAnalysis);

	public List<String> getExtendedParameterAcronymsFromAnalysisByType(Integer idAnalysis, String type);

	public List<String> getExtendedParameterAcronymsFromAnalysisByType(Integer idAnalysis, ParameterType type);

	public List<IParameter> getAll();

	public List<IParameter> getAllFromAnalysis(Integer idAnalysis);

	public List<IParameter> getAllFromAnalysisByPageAndSizeIndex(Integer idAnalysis, Integer pageIndex, Integer pageSize);

	public List<IParameter> getAllByPageAndSizeIndex(Integer pageIndex, Integer pageSize);

	public List<IParameter> getAllFromAnalysisByType(Integer idAnalysis, Integer idType);

	public List<IParameter> getAllFromAnalysisByType(Integer idAnalysis, String... types);

	public List<IParameter> getAllFromAnalysisByType(Integer idAnalysis, ParameterType type);

	public List<IParameter> getAllInitialisedFromAnalysisByType(Integer idAnalysis, String type);

	public List<IAcronymParameter> findAllAcronymParameterByAnalysisId(Integer idAnalysis);

	public List<IBoundedParameter> getAllExtendedFromAnalysisAndType(Integer idAnalysis, ParameterType type);

	public List<IImpactParameter> getAllImpactFromAnalysis(Integer idAnalysis);

	public List<IProbabilityParameter> getAllProbabilityFromAnalysis(Integer idAnalysis);

	public IParameter save(IParameter parameter);

	public void saveOrUpdate(IParameter parameter);

	public void saveOrUpdate(List<? extends IParameter> parameters);

	public IParameter merge(IParameter parameter);

	public void delete(Integer id);

	public void delete(IParameter parameter);

	public IParameter getByAnalysisIdAndDescription(Integer idAnalysis, String description);

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
	public List<IProbabilityParameter> getAllExpressionParametersFromAnalysis(Integer idAnalysis) throws Exception;

	/**
	 * Gets a list of acronyms of all parameters that are considered to be used
	 * as variable when evaluating an arithmetic expression. The parameter
	 * acronym is then replaced by the value of the respective parameter.
	 * 
	 * @param idAnalysis
	 *            The identifier of the analysis for which parameters shall be
	 *            retrieved.
	 * @author Steve Muller (SMU), itrust consulting s.à r.l.
	 * @since Jun 15, 2015
	 */
	public List<String> getAllExpressionParameterAcronymsFromAnalysis(Integer idAnalysis) throws Exception;

	public List<DynamicParameter> findAllDynamicByAnalysisId(Integer idAnalysis);

}