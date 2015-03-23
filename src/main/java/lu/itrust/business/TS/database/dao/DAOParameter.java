package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.data.parameter.ExtendedParameter;
import lu.itrust.business.TS.data.parameter.Parameter;
import lu.itrust.business.TS.data.parameter.ParameterType;

/**
 * DAOParameter.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOParameter {
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
	
}