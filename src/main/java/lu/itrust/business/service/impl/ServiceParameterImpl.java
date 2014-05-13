package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.ParameterType;
import lu.itrust.business.dao.DAOParameter;
import lu.itrust.business.service.ServiceParameter;

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
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#get(int)
	 */
	@Override
	public Parameter get(Integer id) throws Exception {
		return daoParameter.get(id);
	}

	/**
	 * getFromAnalysisIdById: <br>
	 * Description
	 * 
	 * @param idParameter
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getFromAnalysisIdById(int,
	 *      java.lang.Integer)
	 */
	@Override
	public Parameter getFromAnalysisById(Integer idAnalysis, Integer idParameter) throws Exception {
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
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getParameterFromAnalysisByParameterTypeAndDescription(int,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public Parameter getFromAnalysisByTypeAndDescription(Integer idAnalysis, String parameterType, String description) throws Exception {
		return daoParameter.getFromAnalysisByTypeAndDescription(idAnalysis, parameterType, description);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param parameterId
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer parameterId, Integer analysisId) throws Exception {
		return daoParameter.belongsToAnalysis(parameterId, analysisId);
	}

	/**
	 * getExtendedParameterAcronymsFromAnalysisId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getExtendedParameterAcronymsFromAnalysisId(int)
	 */
	@Override
	public List<String> getExtendedParameterAcronymsFromAnalysis(Integer idAnalysis) throws Exception {
		return daoParameter.getExtendedParameterAcronymsFromAnalysis(idAnalysis);
	}

	/**
	 * getExtendedParameterAcronymsFromAnalysisByParameterTypeName: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param type
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getExtendedParameterAcronymsFromAnalysisByParameterTypeName(int,
	 *      java.lang.String)
	 */
	@Override
	public List<String> getExtendedParameterAcronymsFromAnalysisByType(Integer idAnalysis, String type) throws Exception {
		return daoParameter.getExtendedParameterAcronymsFromAnalysisByType(idAnalysis, type);
	}

	/**
	 * getExtendedParameterAcronymsFromAnalysisByParameterType: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param type
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getExtendedParameterAcronymsFromAnalysisByParameterType(int,
	 *      lu.itrust.business.TS.ParameterType)
	 */
	@Override
	public List<String> getExtendedParameterAcronymsFromAnalysisByType(Integer idAnalysis, ParameterType type) throws Exception {
		return daoParameter.getExtendedParameterAcronymsFromAnalysisByType(idAnalysis, type);
	}

	/**
	 * getAllParameters: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getAllParameters()
	 */
	@Override
	public List<Parameter> getAll() throws Exception {
		return daoParameter.getAll();
	}

	/**
	 * getAllParametersFromAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getAllParametersFromAnalysis(int)
	 */
	@Override
	public List<Parameter> getAllFromAnalysis(Integer idAnalysis) throws Exception {
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
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getAllParametersFromAnalysisByPageAndSizeIndex(int,
	 *      int, int)
	 */
	@Override
	public List<Parameter> getAllFromAnalysisByPageAndSizeIndex(Integer idAnalysis, Integer pageIndex, Integer pageSize) throws Exception {
		return daoParameter.getAllFromAnalysisByPageAndSizeIndex(idAnalysis, pageIndex, pageSize);
	}

	/**
	 * getAllParametersByPageAndSizeIndex: <br>
	 * Description
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getAllParametersByPageAndSizeIndex(int, int)
	 */
	@Override
	public List<Parameter> getAllByPageAndSizeIndex(Integer pageIndex, Integer pageSize) throws Exception {
		return daoParameter.getAllByPageAndSizeIndex(pageIndex, pageSize);
	}

	/**
	 * getParametersFromAnalysisIdByParameterTypeId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param idType
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getParametersFromAnalysisIdByParameterTypeId(int,
	 *      int)
	 */
	@Override
	public List<Parameter> getAllFromAnalysisByType(Integer idAnalysis, Integer idType) throws Exception {
		return daoParameter.getAllFromAnalysisByType(idAnalysis, idType);
	}

	/**
	 * getParametersFromAnalysisIdByParameterTypeName: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param type
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getParametersFromAnalysisIdByParameterTypeName(int,
	 *      java.lang.String)
	 */
	@Override
	public List<Parameter> getAllFromAnalysisByType(Integer idAnalysis, String type) throws Exception {
		return daoParameter.getAllFromAnalysisByType(idAnalysis, type);
	}

	/**
	 * getParametersFromAnalysisIdByParameterType: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param type
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getParametersFromAnalysisIdByParameterType(int,
	 *      lu.itrust.business.TS.ParameterType)
	 */
	@Override
	public List<Parameter> getAllFromAnalysisByType(Integer idAnalysis, ParameterType type) throws Exception {
		return daoParameter.getAllFromAnalysisByType(idAnalysis, type);
	}

	/**
	 * getInitialisedParametersFromAnalysisIdByParameterTypeName: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param type
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getInitialisedParametersFromAnalysisIdByParameterTypeName(int,
	 *      java.lang.String)
	 */
	@Override
	public List<Parameter> getAllInitialisedFromAnalysisByType(Integer idAnalysis, String type) throws Exception {
		return daoParameter.getAllInitialisedFromAnalysisByType(idAnalysis, type);
	}

	/**
	 * getAllExtendedParametersFromAnalysisId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getAllExtendedParametersFromAnalysisId(int)
	 */
	@Override
	public List<ExtendedParameter> getAllExtendedFromAnalysis(Integer idAnalysis) throws Exception {
		return daoParameter.getAllExtendedFromAnalysis(idAnalysis);
	}

	/**
	 * getAllExtendedParametersFromAnalysisIdAndParameterType: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param type
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getAllExtendedParametersFromAnalysisIdAndParameterType(int,
	 *      lu.itrust.business.TS.ParameterType)
	 */
	@Override
	public List<ExtendedParameter> getAllExtendedFromAnalysisAndType(Integer idAnalysis, ParameterType type) throws Exception {
		return daoParameter.getAllExtendedFromAnalysisAndType(idAnalysis, type);
	}

	/**
	 * getImpactParameterByAnalysisId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getImpactParameterByAnalysisId(int)
	 */
	@Override
	public List<ExtendedParameter> getAllImpactFromAnalysis(Integer idAnalysis) throws Exception {
		return daoParameter.getAllImpactFromAnalysis(idAnalysis);
	}

	/**
	 * getProbaParameterByAnalysisId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#getProbaParameterByAnalysisId(int)
	 */
	@Override
	public List<ExtendedParameter> getAllProbabilityFromAnalysis(Integer idAnalysis) throws Exception {
		return daoParameter.getAllProbabilityFromAnalysis(idAnalysis);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param parameter
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#save(lu.itrust.business.TS.Parameter)
	 */
	@Transactional
	@Override
	public Parameter save(Parameter parameter) throws Exception {
		return daoParameter.save(parameter);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param parameter
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#saveOrUpdate(lu.itrust.business.TS.Parameter)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Parameter parameter) throws Exception {
		daoParameter.saveOrUpdate(parameter);

	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @param parameter
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#merge(lu.itrust.business.TS.Parameter)
	 */
	@Transactional
	@Override
	public Parameter merge(Parameter parameter) throws Exception {
		return daoParameter.merge(parameter);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param parameter
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#delete(lu.itrust.business.TS.Parameter)
	 */
	@Transactional
	@Override
	public void delete(Parameter parameter) throws Exception {
		daoParameter.delete(parameter);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#delete(int)
	 */
	@Transactional
	@Override
	public void delete(Integer id) throws Exception {
		daoParameter.delete(id);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param parameters
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#saveOrUpdate(java.util.List)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(List<? extends Parameter> parameters) throws Exception {
		daoParameter.saveOrUpdate(parameters);
	}
}