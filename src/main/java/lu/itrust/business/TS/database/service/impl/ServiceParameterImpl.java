package lu.itrust.business.TS.database.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOParameter;
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.model.parameter.AcronymParameter;
import lu.itrust.business.TS.model.parameter.DynamicParameter;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.parameter.ParameterType;

/**
 * ServiceParameterImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
public class ServiceParameterImpl implements ServiceParameter {

	@Autowired
	private DAOParameter daoParameter;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#get(int)
	 */
	@Override
	public Parameter get(Integer id)  {
		return daoParameter.get(id);
	}

	/**
	 * getFromAnalysisIdById: <br>
	 * Description
	 * 
	 * @param idParameter
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getFromAnalysisIdById(int,
	 *      java.lang.Integer)
	 */
	@Override
	public Parameter getFromAnalysisById(Integer idAnalysis, Integer idParameter)  {
		return daoParameter.getFromAnalysisById(idAnalysis, idParameter);
	}

	/**
	 * getParameterFromAnalysisByParameterTypeAndDescription: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param parameterType
	 * @param description
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getParameterFromAnalysisByParameterTypeAndDescription(int,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public Parameter getFromAnalysisByTypeAndDescription(Integer idAnalysis, String parameterType, String description)  {
		return daoParameter.getFromAnalysisByTypeAndDescription(idAnalysis, parameterType, description);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param parameterId
	 * @param analysisId
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer parameterId, Integer analysisId)  {
		return daoParameter.belongsToAnalysis(parameterId, analysisId);
	}

	/**
	 * getExtendedParameterAcronymsFromAnalysisId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getExtendedParameterAcronymsFromAnalysisId(int)
	 */
	@Override
	public List<String> getExtendedParameterAcronymsFromAnalysis(Integer idAnalysis)  {
		return daoParameter.getExtendedParameterAcronymsFromAnalysis(idAnalysis);
	}

	/**
	 * getExtendedParameterAcronymsFromAnalysisByParameterTypeName: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param type
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getExtendedParameterAcronymsFromAnalysisByParameterTypeName(int,
	 *      java.lang.String)
	 */
	@Override
	public List<String> getExtendedParameterAcronymsFromAnalysisByType(Integer idAnalysis, String type)  {
		return daoParameter.getExtendedParameterAcronymsFromAnalysisByType(idAnalysis, type);
	}

	/**
	 * getExtendedParameterAcronymsFromAnalysisByParameterType: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param type
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getExtendedParameterAcronymsFromAnalysisByParameterType(int,
	 *      lu.itrust.business.TS.model.parameter.ParameterType)
	 */
	@Override
	public List<String> getExtendedParameterAcronymsFromAnalysisByType(Integer idAnalysis, ParameterType type)  {
		return daoParameter.getExtendedParameterAcronymsFromAnalysisByType(idAnalysis, type);
	}

	/**
	 * getAllParameters: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getAllParameters()
	 */
	@Override
	public List<Parameter> getAll()  {
		return daoParameter.getAll();
	}

	/**
	 * getAllParametersFromAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getAllParametersFromAnalysis(int)
	 */
	@Override
	public List<Parameter> getAllFromAnalysis(Integer idAnalysis)  {
		return daoParameter.getAllFromAnalysis(idAnalysis);
	}

	/**
	 * getAllParametersFromAnalysisByPageAndSizeIndex: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getAllParametersFromAnalysisByPageAndSizeIndex(int,
	 *      int, int)
	 */
	@Override
	public List<Parameter> getAllFromAnalysisByPageAndSizeIndex(Integer idAnalysis, Integer pageIndex, Integer pageSize)  {
		return daoParameter.getAllFromAnalysisByPageAndSizeIndex(idAnalysis, pageIndex, pageSize);
	}

	/**
	 * getAllParametersByPageAndSizeIndex: <br>
	 * Description
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getAllParametersByPageAndSizeIndex(int, int)
	 */
	@Override
	public List<Parameter> getAllByPageAndSizeIndex(Integer pageIndex, Integer pageSize)  {
		return daoParameter.getAllByPageAndSizeIndex(pageIndex, pageSize);
	}

	/**
	 * getParametersFromAnalysisIdByParameterTypeId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param idType
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getParametersFromAnalysisIdByParameterTypeId(int,
	 *      int)
	 */
	@Override
	public List<Parameter> getAllFromAnalysisByType(Integer idAnalysis, Integer idType)  {
		return daoParameter.getAllFromAnalysisByType(idAnalysis, idType);
	}

	/**
	 * getParametersFromAnalysisIdByParameterTypeName: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param types
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getParametersFromAnalysisIdByParameterTypeName(int,
	 *      java.lang.String)
	 */
	@Override
	public List<Parameter> getAllFromAnalysisByType(Integer idAnalysis, String... types)  {
		return daoParameter.getAllFromAnalysisByType(idAnalysis, types);
	}

	/**
	 * getParametersFromAnalysisIdByParameterType: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param type
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getParametersFromAnalysisIdByParameterType(int,
	 *      lu.itrust.business.TS.model.parameter.ParameterType)
	 */
	@Override
	public List<Parameter> getAllFromAnalysisByType(Integer idAnalysis, ParameterType type)  {
		return daoParameter.getAllFromAnalysisByType(idAnalysis, type);
	}

	/**
	 * getInitialisedParametersFromAnalysisIdByParameterTypeName: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param type
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getInitialisedParametersFromAnalysisIdByParameterTypeName(int,
	 *      java.lang.String)
	 */
	@Override
	public List<Parameter> getAllInitialisedFromAnalysisByType(Integer idAnalysis, String type)  {
		return daoParameter.getAllInitialisedFromAnalysisByType(idAnalysis, type);
	}

	/**
	 * getAllExtendedParametersFromAnalysisId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getAllExtendedParametersFromAnalysisId(int)
	 */
	@Override
	public List<ExtendedParameter> getAllExtendedFromAnalysis(Integer idAnalysis)  {
		return daoParameter.getAllExtendedFromAnalysis(idAnalysis);
	}

	/**
	 * getAllExtendedParametersFromAnalysisIdAndParameterType: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param type
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getAllExtendedParametersFromAnalysisIdAndParameterType(int,
	 *      lu.itrust.business.TS.model.parameter.ParameterType)
	 */
	@Override
	public List<ExtendedParameter> getAllExtendedFromAnalysisAndType(Integer idAnalysis, ParameterType type)  {
		return daoParameter.getAllExtendedFromAnalysisAndType(idAnalysis, type);
	}

	/**
	 * getImpactParameterByAnalysisId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getImpactParameterByAnalysisId(int)
	 */
	@Override
	public List<ExtendedParameter> getAllImpactFromAnalysis(Integer idAnalysis)  {
		return daoParameter.getAllImpactFromAnalysis(idAnalysis);
	}

	/**
	 * getProbaParameterByAnalysisId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#getProbaParameterByAnalysisId(int)
	 */
	@Override
	public List<ExtendedParameter> getAllProbabilityFromAnalysis(Integer idAnalysis)  {
		return daoParameter.getAllProbabilityFromAnalysis(idAnalysis);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param parameter
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#save(lu.itrust.business.TS.model.parameter.Parameter)
	 */
	@Transactional
	@Override
	public Parameter save(Parameter parameter)  {
		return daoParameter.save(parameter);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param parameter
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#saveOrUpdate(lu.itrust.business.TS.model.parameter.Parameter)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Parameter parameter)  {
		daoParameter.saveOrUpdate(parameter);

	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @param parameter
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#merge(lu.itrust.business.TS.model.parameter.Parameter)
	 */
	@Transactional
	@Override
	public Parameter merge(Parameter parameter)  {
		return daoParameter.merge(parameter);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param parameter
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#delete(lu.itrust.business.TS.model.parameter.Parameter)
	 */
	@Transactional
	@Override
	public void delete(Parameter parameter)  {
		daoParameter.delete(parameter);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#delete(int)
	 */
	@Transactional
	@Override
	public void delete(Integer id)  {
		daoParameter.delete(id);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param parameters
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceParameter#saveOrUpdate(java.util.List)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(List<? extends Parameter> parameters)  {
		daoParameter.saveOrUpdate(parameters);
	}

	@Override
	public Parameter getByAnalysisIdAndDescription(Integer idAnalysis, String description) {
		return daoParameter.getByAnalysisIdAndDescription(idAnalysis, description);
	}

	/**
	 * {@inheritDoc}
	 * @author Steve Muller (SMU), itrust consulting s.à r.l.
	 * @since Jun 10, 2015
	 */
	@Override
	public List<AcronymParameter> getAllExpressionParametersFromAnalysis(Integer idAnalysis) throws Exception {
		return daoParameter.getAllExpressionParametersFromAnalysis(idAnalysis);
	}

	/**
	 * {@inheritDoc}
	 * @author Steve Muller (SMU), itrust consulting s.à r.l.
	 * @since Jun 10, 2015
	 */
	@Override
	public List<DynamicParameter> getDynamicParametersFromAnalysis(Integer idAnalysis) throws Exception {
		return daoParameter.getDynamicParametersFromAnalysis(idAnalysis);
	}

	@Override
	public List<String> getAllExpressionParameterAcronymsFromAnalysis(Integer idAnalysis) throws Exception {
		return daoParameter
				.getAllExpressionParametersFromAnalysis(idAnalysis).stream()
				.map(parameter -> parameter.getAcronym())
				.collect(Collectors.toList());
	}
}