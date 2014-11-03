package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.data.basic.ExtendedParameter;
import lu.itrust.business.TS.data.basic.Parameter;
import lu.itrust.business.TS.data.basic.ParameterType;
import lu.itrust.business.TS.database.dao.DAOParameter;
import lu.itrust.business.TS.tsconstant.Constant;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOParameterHBM.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since Feb 12, 2013
 */
@Repository
public class DAOParameterHBM extends DAOHibernate implements DAOParameter {

	/**
	 * Constructor: <br>
	 */
	public DAOParameterHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOParameterHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#get(int)
	 */
	@Override
	public Parameter get(Integer id) throws Exception {
		return (Parameter) getSession().get(Parameter.class, id);
	}

	/**
	 * getFromAnalysisIdById: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getFromAnalysisIdById(int, java.lang.Integer)
	 */
	@Override
	public Parameter getFromAnalysisById(Integer idAnalysis, Integer idParameter) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and parameter.id = :idParameter";
		return (Parameter) getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("idParameter", idParameter).uniqueResult();
	}

	/**
	 * getParameterFromAnalysisByParameterTypeAndDescription: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getParameterFromAnalysisByParameterTypeAndDescription(int,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public Parameter getFromAnalysisByTypeAndDescription(Integer idAnalysis, String type, String description) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type.label = :type and ";
		query += "parameter.description = :description";
		return (Parameter) getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("type", type).setParameter("description", description).uniqueResult();
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer parameterId) throws Exception {
		String query = "Select count(parameter) From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisid and parameter.id = :parameterId";
		return ((Long) getSession().createQuery(query).setParameter("analysisid", analysisId).setParameter("parameterId", parameterId).uniqueResult()).intValue() > 0;
	}

	/**
	 * getExtendedParameterAcronymsFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getExtendedParameterAcronymsFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getExtendedParameterAcronymsFromAnalysis(Integer idAnalysis) throws Exception {
		String query = "Select parameter.acronym From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and (parameter.type.label = :impact ";
		query += "or parameter.type.label = :proba ) order by parameter.level";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("impact", Constant.PARAMETERTYPE_TYPE_IMPACT_NAME).setParameter("proba",
				Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME).list();
	}

	/**
	 * getExtendedParameterAcronymsFromAnalysisByParameterTypeName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getExtendedParameterAcronymsFromAnalysisByParameterTypeName(int,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getExtendedParameterAcronymsFromAnalysisByType(Integer idAnalysis, String type) throws Exception {
		String query = "Select parameter.acronym From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and parameter.type.label = :type ";
		query += "order by parameter.level";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("type", type).list();
	}

	/**
	 * getExtendedParameterAcronymsFromAnalysisByParameterType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getExtendedParameterAcronymsFromAnalysisByParameterType(int,
	 *      lu.itrust.business.TS.data.basic.ParameterType)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getExtendedParameterAcronymsFromAnalysisByType(Integer idAnalysis, ParameterType type) throws Exception {
		String query = "Select parameter.acronym From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and parameter.type = :type order by ";
		query += "parameter.level";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("type", type).list();
	}

	/**
	 * getAllParameters: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getAllParameters()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getAll() throws Exception {
		return getSession().createQuery("From Parameter").list();
	}

	/**
	 * getAllParametersFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getAllParametersFromAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getAllFromAnalysis(Integer idAnalysis) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * getAllParametersFromAnalysisByPageAndSizeIndex: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getAllParametersFromAnalysisByPageAndSizeIndex(int,
	 *      int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getAllFromAnalysisByPageAndSizeIndex(Integer idAnalysis, Integer pageIndex, Integer pageSize) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize).list();
	}

	/**
	 * getAllParametersByPageAndSizeIndex: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getAllParametersByPageAndSizeIndex(int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getAllByPageAndSizeIndex(Integer pageIndex, Integer pageSize) throws Exception {
		return getSession().createQuery("From Parameter").setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize).list();
	}

	/**
	 * getParametersFromAnalysisIdByParameterTypeId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getParametersFromAnalysisIdByParameterTypeId(int,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getAllFromAnalysisByType(Integer idAnalysis, Integer idType) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type.id = :idType";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("idType", idType).list();
	}

	/**
	 * getParametersFromAnalysisIdByParameterTypeName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getParametersFromAnalysisIdByParameterTypeName(int,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getAllFromAnalysisByType(Integer idAnalysis, String type) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type.label = :type";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("type", type).list();
	}

	/**
	 * getParametersFromAnalysisIdByParameterType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getParametersFromAnalysisIdByParameterType(int,
	 *      lu.itrust.business.TS.data.basic.ParameterType)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getAllFromAnalysisByType(Integer idAnalysis, ParameterType type) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type= :type";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("type", type).list();
	}

	/**
	 * getInitialisedParametersFromAnalysisIdByParameterTypeName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getInitialisedParametersFromAnalysisIdByParameterTypeName(int,
	 *      java.lang.String)
	 */
	@Override
	public List<Parameter> getAllInitialisedFromAnalysisByType(Integer idAnalysis, String type) throws Exception {
		List<Parameter> parameters = getAllFromAnalysisByType(idAnalysis, type);
		for (int i = 0; i < parameters.size(); i++) {
			parameters.set(i, Initialise(parameters.get(i)));
			parameters.get(i).setType(Initialise(parameters.get(i).getType()));
		}
		return parameters;
	}

	/**
	 * getAllExtendedParametersFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getAllExtendedParametersFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExtendedParameter> getAllExtendedFromAnalysis(Integer idAnalysis) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and (parameter.type.label = :impact or ";
		query += "parameter.type.label = :proba) order by parameter.type.id, parameter.level";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("impact", Constant.PARAMETERTYPE_TYPE_IMPACT_NAME).setParameter("proba",
				Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME).list();
	}

	/**
	 * getAllExtendedParametersFromAnalysisIdAndParameterType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getAllExtendedParametersFromAnalysisIdAndParameterType(int,
	 *      lu.itrust.business.TS.data.basic.ParameterType)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExtendedParameter> getAllExtendedFromAnalysisAndType(Integer idAnalysis, ParameterType type) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type = :type order by ";
		query += "parameter.level asc";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("type", type).list();
	}

	/**
	 * getImpactParameterByAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getImpactParameterByAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExtendedParameter> getAllImpactFromAnalysis(Integer idAnalysis) throws Exception {
		String query = "Select parameter From Analysis as analysis  inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type.label = :type order by ";
		query += "parameter.level asc";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("type", Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME).list();
	}

	/**
	 * getProbaParameterByAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getProbaParameterByAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExtendedParameter> getAllProbabilityFromAnalysis(Integer idAnalysis) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type.label = :type order by ";
		query += "parameter.level asc";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("type", Constant.PARAMETERTYPE_TYPE_IMPACT_NAME).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#save(lu.itrust.business.TS.data.basic.Parameter)
	 */
	@Override
	public Parameter save(Parameter parameter) throws Exception {
		return (Parameter) getSession().save(parameter);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#saveOrUpdate(lu.itrust.business.TS.data.basic.Parameter)
	 */
	@Override
	public void saveOrUpdate(Parameter parameter) throws Exception {
		getSession().saveOrUpdate(parameter);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#saveOrUpdate(java.util.List)
	 */
	@Override
	public void saveOrUpdate(List<? extends Parameter> parameters) throws Exception {
		for (Parameter parameter : parameters)
			saveOrUpdate(parameter);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#merge(lu.itrust.business.TS.data.basic.Parameter)
	 */
	@Override
	public Parameter merge(Parameter parameter) throws Exception {
		return (Parameter) getSession().merge(parameter);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#delete(int)
	 */
	@Override
	public void delete(Integer id) throws Exception {
		getSession().delete(get(id));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#delete(lu.itrust.business.TS.data.basic.Parameter)
	 */
	@Override
	public void delete(Parameter parameter) throws Exception {
		getSession().delete(parameter);
	}
}