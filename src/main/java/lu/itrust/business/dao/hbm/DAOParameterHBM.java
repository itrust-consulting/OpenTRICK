package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.ParameterType;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.dao.DAOParameter;

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
	 * @see lu.itrust.business.dao.DAOParameter#get(int)
	 */
	@Override
	public Parameter get(int id) throws Exception {
		return (Parameter) getSession().get(Parameter.class, id);
	}

	/**
	 * getFromAnalysisIdById: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getFromAnalysisIdById(int, java.lang.Integer)
	 */
	@Override
	public Parameter getFromAnalysisIdById(int idParameter, Integer idAnalysis) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and parameter.id = :idParameter";
		return (Parameter) getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("idParameter", idParameter).uniqueResult();
	}

	/**
	 * getParameterFromAnalysisByParameterTypeAndDescription: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getParameterFromAnalysisByParameterTypeAndDescription(int,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public Parameter getParameterFromAnalysisByParameterTypeAndDescription(int idAnalysis, String type, String description) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type.label = :type and ";
		query += "parameter.description = :description";
		return (Parameter) getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("type", type).setParameter("description", description).uniqueResult();
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer parameterId, Integer analysisId) throws Exception {
		String query = "Select count(parameter) From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisid and parameter.id = : parameterId";
		return ((Long) getSession().createQuery(query).setParameter("analysisid", analysisId).setParameter("parameterId", parameterId).uniqueResult()).intValue() > 0;
	}

	/**
	 * getExtendedParameterAcronymsFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getExtendedParameterAcronymsFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getExtendedParameterAcronymsFromAnalysisId(int idAnalysis) throws Exception {
		String query = "Select parameter.acronym From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and (parameter.type.label = :impact ";
		query += "or parameter.type.label = :proba ) order by parameter.level";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("impact", Constant.PARAMETERTYPE_TYPE_IMPACT_NAME).setParameter("proba",
				Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME).list();
	}

	/**
	 * getExtendedParameterAcronymsFromAnalysisByParameterTypeName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getExtendedParameterAcronymsFromAnalysisByParameterTypeName(int,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getExtendedParameterAcronymsFromAnalysisByParameterTypeName(int idAnalysis, String type) throws Exception {
		String query = "Select parameter.acronym From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and parameter.type.label = :type ";
		query += "order by parameter.level";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("type", type).list();
	}

	/**
	 * getExtendedParameterAcronymsFromAnalysisByParameterType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getExtendedParameterAcronymsFromAnalysisByParameterType(int,
	 *      lu.itrust.business.TS.ParameterType)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getExtendedParameterAcronymsFromAnalysisByParameterType(int idAnalysis, ParameterType type) throws Exception {
		String query = "Select parameter.acronym From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and parameter.type = :type order by ";
		query += "parameter.level";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("type", type).list();
	}

	/**
	 * getAllParameters: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getAllParameters()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getAllParameters() throws Exception {
		return getSession().createQuery("From Parameter").list();
	}

	/**
	 * getAllParametersFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getAllParametersFromAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getAllParametersFromAnalysis(int idAnalysis) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * getAllParametersFromAnalysisByPageAndSizeIndex: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getAllParametersFromAnalysisByPageAndSizeIndex(int,
	 *      int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getAllParametersFromAnalysisByPageAndSizeIndex(int idAnalysis, int pageIndex, int pageSize) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize).list();
	}

	/**
	 * getAllParametersByPageAndSizeIndex: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getAllParametersByPageAndSizeIndex(int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getAllParametersByPageAndSizeIndex(int pageIndex, int pageSize) throws Exception {
		return getSession().createQuery("From Parameter").setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize).list();
	}

	/**
	 * getParametersFromAnalysisIdByParameterTypeId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getParametersFromAnalysisIdByParameterTypeId(int,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getParametersFromAnalysisIdByParameterTypeId(int idAnalysis, int idType) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type.id = :idType";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("idType", idType).list();
	}

	/**
	 * getParametersFromAnalysisIdByParameterTypeName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getParametersFromAnalysisIdByParameterTypeName(int,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getParametersFromAnalysisIdByParameterTypeName(int idAnalysis, String type) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type.label = :type";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("type", type).list();
	}

	/**
	 * getParametersFromAnalysisIdByParameterType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getParametersFromAnalysisIdByParameterType(int,
	 *      lu.itrust.business.TS.ParameterType)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getParametersFromAnalysisIdByParameterType(int idAnalysis, ParameterType type) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type= :type";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("type", type).list();
	}

	/**
	 * getInitialisedParametersFromAnalysisIdByParameterTypeName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getInitialisedParametersFromAnalysisIdByParameterTypeName(int,
	 *      java.lang.String)
	 */
	@Override
	public List<Parameter> getInitialisedParametersFromAnalysisIdByParameterTypeName(int idAnalysis, String type) throws Exception {
		List<Parameter> parameters = getParametersFromAnalysisIdByParameterTypeName(idAnalysis, type);
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
	 * @see lu.itrust.business.dao.DAOParameter#getAllExtendedParametersFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExtendedParameter> getAllExtendedParametersFromAnalysisId(int idAnalysis) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and (parameter.type.label = :impact or ";
		query += "parameter.type.label = :proba) order by parameter.type.id, parameter.level";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("impact", Constant.PARAMETERTYPE_TYPE_IMPACT_NAME).setParameter("proba",
				Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME).list();
	}

	/**
	 * getAllExtendedParametersFromAnalysisIdAndParameterType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getAllExtendedParametersFromAnalysisIdAndParameterType(int,
	 *      lu.itrust.business.TS.ParameterType)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExtendedParameter> getAllExtendedParametersFromAnalysisIdAndParameterType(int idAnalysis, ParameterType type) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type = :type order by ";
		query += "parameter.level asc";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("type", type).list();
	}

	/**
	 * getImpactParameterByAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getImpactParameterByAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExtendedParameter> getImpactParameterByAnalysisId(int idAnalysis) throws Exception {
		String query = "Select parameter From Analysis as analysis  inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type.label = :type order by ";
		query += "parameter.level asc";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("type", Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME).list();
	}

	/**
	 * getProbaParameterByAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#getProbaParameterByAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExtendedParameter> getProbaParameterByAnalysisId(int idAnalysis) throws Exception {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type.label = :type order by ";
		query += "parameter.level asc";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("type", Constant.PARAMETERTYPE_TYPE_IMPACT_NAME).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#save(lu.itrust.business.TS.Parameter)
	 */
	@Override
	public Parameter save(Parameter parameter) throws Exception {
		return (Parameter) getSession().save(parameter);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#saveOrUpdate(lu.itrust.business.TS.Parameter)
	 */
	@Override
	public void saveOrUpdate(Parameter parameter) throws Exception {
		getSession().saveOrUpdate(parameter);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#saveOrUpdate(java.util.List)
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
	 * @see lu.itrust.business.dao.DAOParameter#merge(lu.itrust.business.TS.Parameter)
	 */
	@Override
	public Parameter merge(Parameter parameter) throws Exception {
		return (Parameter) getSession().merge(parameter);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#delete(int)
	 */
	@Override
	public void delete(int id) throws Exception {
		getSession().delete(get(id));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#delete(lu.itrust.business.TS.Parameter)
	 */
	@Override
	public void delete(Parameter parameter) throws Exception {
		getSession().delete(parameter);
	}
}