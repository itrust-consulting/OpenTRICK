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

	public Parameter get(int id) throws Exception;

	public Parameter getFromAnalysisIdById(int idParameter, Integer idAnalysis) throws Exception;

	public Parameter getParameterFromAnalysisByParameterTypeAndDescription(int idAnalysis, String parameterType, String description) throws Exception;

	public boolean belongsToAnalysis(Integer parameterId, Integer analysisId) throws Exception;

	public List<String> getExtendedParameterAcronymsFromAnalysisId(int idAnalysis) throws Exception;

	public List<String> getExtendedParameterAcronymsFromAnalysisByParameterTypeName(int idAnalysis, String type) throws Exception;

	public List<String> getExtendedParameterAcronymsFromAnalysisByParameterType(int idAnalysis, ParameterType type) throws Exception;

	public List<Parameter> getAllParameters() throws Exception;

	public List<Parameter> getAllParametersFromAnalysis(int idAnalysis) throws Exception;

	public List<Parameter> getAllParametersFromAnalysisByPageAndSizeIndex(int idAnalysis, int pageIndex, int pageSize) throws Exception;

	public List<Parameter> getAllParametersByPageAndSizeIndex(int pageIndex, int pageSize) throws Exception;

	public List<Parameter> getParametersFromAnalysisIdByParameterTypeId(int idAnalysis, int idType) throws Exception;

	public List<Parameter> getParametersFromAnalysisIdByParameterTypeName(int idAnalysis, String type) throws Exception;

	public List<Parameter> getParametersFromAnalysisIdByParameterType(int idAnalysis, ParameterType type) throws Exception;

	public List<Parameter> getInitialisedParametersFromAnalysisIdByParameterTypeName(int idAnalysis, String type) throws Exception;

	public List<ExtendedParameter> getAllExtendedParametersFromAnalysisId(int idAnalysis) throws Exception;

	public List<ExtendedParameter> getAllExtendedParametersFromAnalysisIdAndParameterType(int idAnalysis, ParameterType type) throws Exception;

	public List<ExtendedParameter> getImpactParameterByAnalysisId(int idAnalysis) throws Exception;

	public List<ExtendedParameter> getProbaParameterByAnalysisId(int idAnalysis) throws Exception;

	public Parameter save(Parameter parameter) throws Exception;

	public void saveOrUpdate(Parameter parameter) throws Exception;

	public void saveOrUpdate(List<? extends Parameter> parameters) throws Exception;

	public Parameter merge(Parameter parameter) throws Exception;

	public void delete(int id) throws Exception;

	public void delete(Parameter parameter) throws Exception;
}