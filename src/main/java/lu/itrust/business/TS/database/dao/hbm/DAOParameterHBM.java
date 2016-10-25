package lu.itrust.business.TS.database.dao.hbm;

import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_DYNAMIC;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_IMPACT_NAME;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_PROPABILITY;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME;
import static lu.itrust.business.TS.constants.Constant.SOA_THRESHOLD;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOParameter;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.parameter.IAcronymParameter;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.IImpactParameter;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.IProbabilityParameter;
import lu.itrust.business.TS.model.parameter.impl.DynamicParameter;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;
import lu.itrust.business.TS.model.parameter.type.impl.ParameterType;

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
	public SimpleParameter get(Integer id) {
		return (SimpleParameter) getSession().get(SimpleParameter.class, id);
	}

	/**
	 * getFromAnalysisIdById: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getFromAnalysisIdById(int,
	 *      java.lang.Integer)
	 */
	@Override
	public SimpleParameter getFromAnalysisById(Integer idAnalysis, Integer idParameter) {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and parameter.id = :idParameter";
		return (SimpleParameter) getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("idParameter", idParameter).uniqueResult();
	}

	/**
	 * getParameterFromAnalysisByParameterTypeAndDescription: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getParameterFromAnalysisByParameterTypeAndDescription(int,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public IParameter getFromAnalysisByTypeAndDescription(Integer idAnalysis, String type, String description) {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type.name = :name and ";
		query += "parameter.description = :description";
		return (IParameter) getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("name", type).setParameter("description", description)
				.uniqueResult();
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer parameterId) {
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
	public List<String> getExtendedParameterAcronymsFromAnalysis(Integer idAnalysis) {
		String query = "Select parameter.acronym From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and (parameter.type.name = :impact ";
		query += "or parameter.type.name = :proba ) order by parameter.level";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("impact", PARAMETERTYPE_TYPE_IMPACT_NAME)
				.setParameter("proba", PARAMETERTYPE_TYPE_PROPABILITY_NAME).list();
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
	public List<String> getExtendedParameterAcronymsFromAnalysisByType(Integer idAnalysis, String type) {
		String query = "Select parameter.acronym From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and parameter.type.name = :name ";
		query += "order by parameter.level";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("name", type).list();
	}

	/**
	 * getExtendedParameterAcronymsFromAnalysisByParameterType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getExtendedParameterAcronymsFromAnalysisByParameterType(int,
	 *      lu.itrust.business.TS.model.parameter.type.impl.ParameterType)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getExtendedParameterAcronymsFromAnalysisByType(Integer idAnalysis, ParameterType name) {
		String query = "Select parameter.acronym From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and parameter.name = :name order by ";
		query += "parameter.level";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("name", name).list();
	}

	/**
	 * getAllParameters: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getAllParameters()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IParameter> getAll() {
		return getSession().createQuery("From SimpleParameter").list();
	}

	/**
	 * getAllParametersFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getAllParametersFromAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IParameter> getAllFromAnalysis(Integer idAnalysis) {
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
	public List<IParameter> getAllFromAnalysisByPageAndSizeIndex(Integer idAnalysis, Integer pageIndex, Integer pageSize) {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize).list();
	}

	/**
	 * getAllParametersByPageAndSizeIndex: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getAllParametersByPageAndSizeIndex(int,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IParameter> getAllByPageAndSizeIndex(Integer pageIndex, Integer pageSize) {
		return getSession().createQuery("From SimpleParameter").setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize).list();
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
	public List<IParameter> getAllFromAnalysisByType(Integer idAnalysis, Integer idType) {
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
	public List<IParameter> getAllFromAnalysisByType(Integer idAnalysis, String... names) {
		return getSession()
				.createQuery(
						"Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type.name in :names")
				.setParameter("analysisId", idAnalysis).setParameterList("names", names).list();
	}

	/**
	 * getParametersFromAnalysisIdByParameterType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getParametersFromAnalysisIdByParameterType(int,
	 *      lu.itrust.business.TS.model.parameter.type.impl.ParameterType)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IParameter> getAllFromAnalysisByType(Integer idAnalysis, ParameterType name) {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.name= :name";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("name", name).list();
	}

	/**
	 * getInitialisedParametersFromAnalysisIdByParameterTypeName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getInitialisedParametersFromAnalysisIdByParameterTypeName(int,
	 *      java.lang.String)
	 */
	@Override
	public List<IParameter> getAllInitialisedFromAnalysisByType(Integer idAnalysis, String name) {
		return getAllFromAnalysisByType(idAnalysis, name);

	}

	/**
	 * getAllExtendedParametersFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getAllExtendedParametersFromAnalysisId(int)
	 */
	@Override
	public List<IAcronymParameter> findAllAcronymParameterByAnalysisId(Integer idAnalysis) {
		Analysis analysis = (Analysis) getSession().createQuery("From Analysis as analysis where analysis.id = :idAnalysis").setParameter("idAnalysis", idAnalysis).uniqueResult();
		return analysis == null ? Collections.emptyList()
				: analysis.getParameters().stream().filter(parameter -> parameter instanceof IAcronymParameter).map(parameter -> (IAcronymParameter) parameter)
						.collect(Collectors.toList());
	}

	/**
	 * getAllExtendedParametersFromAnalysisIdAndParameterType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getAllExtendedParametersFromAnalysisIdAndParameterType(int,
	 *      lu.itrust.business.TS.model.parameter.type.impl.ParameterType)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IBoundedParameter> getAllExtendedFromAnalysisAndType(Integer idAnalysis, ParameterType type) {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type = :type order by parameter.level asc";
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
	public List<IImpactParameter> getAllImpactFromAnalysis(Integer idAnalysis) {
		String query = "Select parameter From Analysis as analysis  inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type.name = :name order by ";
		query += "parameter.level asc";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("type", PARAMETERTYPE_TYPE_PROPABILITY_NAME).list();
	}

	/**
	 * getProbaParameterByAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#getProbaParameterByAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IProbabilityParameter> getAllProbabilityFromAnalysis(Integer idAnalysis) {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.type.name = :name order by ";
		query += "parameter.level asc";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("type", PARAMETERTYPE_TYPE_IMPACT_NAME).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#save(lu.itrust.business.TS.model.parameter.impl.SimpleParameter)
	 */
	@Override
	public IParameter save(IParameter parameter) {
		return (IParameter) getSession().save(parameter);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#saveOrUpdate(lu.itrust.business.TS.model.parameter.impl.SimpleParameter)
	 */
	@Override
	public void saveOrUpdate(IParameter parameter) {
		getSession().saveOrUpdate(parameter);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#saveOrUpdate(java.util.List)
	 */
	@Override
	public void saveOrUpdate(List<? extends IParameter> parameters) {
		for (IParameter parameter : parameters)
			saveOrUpdate(parameter);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#merge(lu.itrust.business.TS.model.parameter.impl.SimpleParameter)
	 */
	@Override
	public IParameter merge(IParameter parameter) {
		return (IParameter) getSession().merge(parameter);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#delete(int)
	 */
	@Override
	public void delete(Integer id) {
		getSession().delete(get(id));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOParameter#delete(lu.itrust.business.TS.model.parameter.impl.SimpleParameter)
	 */
	@Override
	public void delete(IParameter parameter) {
		getSession().delete(parameter);
	}

	@Override
	public IParameter getByAnalysisIdAndDescription(Integer idAnalysis, String description) {
		return (IParameter) getSession()
				.createQuery(
						"Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :analysisId and parameter.description = :description")
				.setParameter("analysisId", idAnalysis).setParameter("description", SOA_THRESHOLD).uniqueResult();
	}

	/**
	 * {@inheritDoc}<br>
	 * <b>Updated by eomar 06/10/2016: Add filter, retrieves Dynamic +
	 * Probability</b>
	 * 
	 * @author Steve Muller (SMU), itrust consulting s.à r.l.
	 * @since Jun 10, 2015
	 * @see lu.itrust.business.TS.model.analysis.Analysis#getExpressionParameters()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IProbabilityParameter> getAllExpressionParametersFromAnalysis(Integer idAnalysis) {
		// We assume that all parameters that have an acronym can be used in an
		// expression
		// Maybe we want to change this in the future (checking parameter.type);
		// then this is the place to act.
		// In that case,
		// lu.itrust.business.TS.model.analysis.Analysis#getExpressionParameters()
		// must also be updated.
		return getSession()
				.createQuery(
						"Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and parameter.type.id in :types")
				.setParameter("idAnalysis", idAnalysis).setParameterList("types", new Integer[] { PARAMETERTYPE_TYPE_DYNAMIC, PARAMETERTYPE_TYPE_PROPABILITY }).list();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DynamicParameter> findAllDynamicByAnalysisId(Integer idAnalysis) {
		return getSession()
				.createQuery(
						"Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and parameter.type.id = :typeId")
				.setInteger("typeId", PARAMETERTYPE_TYPE_DYNAMIC).list();
	}
}