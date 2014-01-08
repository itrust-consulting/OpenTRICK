/**
 * 
 */
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
 * @author eom
 * 
 */
@Repository
public class DAOParameterHBM extends DAOHibernate implements DAOParameter {

	/**
	 * 
	 */
	public DAOParameterHBM() {
	}

	/**
	 * @param session
	 */
	public DAOParameterHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#get(int)
	 */
	@Override
	public Parameter get(int id) {
		return (Parameter) getSession().get(Parameter.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#findAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> findAll() {
		return getSession().createQuery("From Parameter").list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#findAll(int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> findAll(int pageIndex, int pageSize) {
		return getSession().createQuery("From Parameter")
				.setFirstResult((pageIndex - 1) * pageSize)
				.setMaxResults(pageSize).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#findByAnalysis(int, int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> findByAnalysis(int idAnalysis, int pageIndex,
			int pageSize) {
		return getSession()
				.createQuery(
						"Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis")
				.setParameter("idAnalysis", idAnalysis)
				.setFirstResult((pageIndex - 1) * pageSize)
				.setMaxResults(pageSize).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOParameter#save(lu.itrust.business.TS.Parameter)
	 */
	@Override
	public Parameter save(Parameter parameter) {
		return (Parameter) getSession().save(parameter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOParameter#saveOrUpdate(lu.itrust.business.TS
	 * .Parameter)
	 */
	@Override
	public void saveOrUpdate(Parameter parameter) {
		getSession().saveOrUpdate(parameter);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOParameter#merge(lu.itrust.business.TS.Parameter
	 * )
	 */
	@Override
	public Parameter merge(Parameter parameter) {
		return (Parameter) getSession().merge(parameter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOParameter#delete(lu.itrust.business.TS.Parameter
	 * )
	 */
	@Override
	public void delete(Parameter parameter) {
		getSession().delete(parameter);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOParameter#delete(int)
	 */
	@Override
	public void delete(int id) {
		getSession().delete(get(id));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> findByAnalysisAndType(int idAnalysis, String type) {
		return getSession()
				.createQuery(
						"Select parameter "
								+ "From Analysis as analysis "
								+ "inner join analysis.parameters as parameter "
								+ "where analysis.id = :analysisId and parameter.type.label = :type")
				.setInteger("analysisId", idAnalysis).setString("type", type)
				.list();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> findByAnalysisAndType(int idAnalysis, int idType) {
		return getSession()
				.createQuery(
						"Select parameter "
								+ "From Analysis as analysis "
								+ "inner join analysis.parameters as parameter "
								+ "where analysis.id = :analysisId and parameter.type.id = :idType")
				.setInteger("analysisId", idAnalysis)
				.setInteger("idType", idType).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> findByAnalysisAndType(int idAnalysis,
			ParameterType type) {
		return getSession()
				.createQuery(
						"Select parameter "
								+ "From Analysis as analysis "
								+ "inner join analysis.parameters as parameter "
								+ "where analysis.id = :analysisId and parameter.type= :type")
				.setInteger("analysisId", idAnalysis)
				.setParameter("type", type).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExtendedParameter> findImpactByAnalysisAndType(int idAnalysis) {
		return getSession()
				.createQuery(
						"Select parameter "
								+ "From Analysis as analysis "
								+ "inner join analysis.parameters as parameter "
								+ "where analysis.id = :analysisId and parameter.type.label = :type order by parameter.level asc")
				.setInteger("analysisId", idAnalysis)
				.setString("type", Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME)
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExtendedParameter> findProbaByAnalysisAndType(int idAnalysis) {
		return getSession()
				.createQuery(
						"Select parameter "
								+ "From Analysis as analysis "
								+ "inner join analysis.parameters as parameter "
								+ "where analysis.id = :analysisId and parameter.type.label = :type order by parameter.level asc")
				.setInteger("analysisId", idAnalysis)
				.setString("type", Constant.PARAMETERTYPE_TYPE_IMPACT_NAME)
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExtendedParameter> findExtendedByAnalysisAndType(
			int idAnalysis, ParameterType type) {
		return getSession()
				.createQuery(
						"Select parameter "
								+ "From Analysis as analysis "
								+ "inner join analysis.parameters as parameter "
								+ "where analysis.id = :analysisId and parameter.type = :type order by parameter.level asc")
				.setInteger("analysisId", idAnalysis)
				.setParameter("type", type).list();
	}

	@Override
	public void saveOrUpdate(List<? extends Parameter> parameters) {
		for (Parameter parameter : parameters)
			saveOrUpdate(parameter);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> findByAnalysis(int idAnalysis) {
		return getSession()
				.createQuery(
						"Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis")
				.setParameter("idAnalysis", idAnalysis).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExtendedParameter> findExtendedByAnalysis(int idAnalysis) {
		return getSession()
				.createQuery(
						"Select parameter "
								+ "From Analysis as analysis inner join analysis.parameters as parameter "
								+ "where analysis.id = :idAnalysis "
								+ "and (parameter.type.label = :impact or parameter.type.label = :proba ) "
								+ "order by parameter.type.id, parameter.level")
				.setParameter("idAnalysis", idAnalysis)
				.setString("impact", Constant.PARAMETERTYPE_TYPE_IMPACT_NAME)
				.setString("proba",
						Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME).list();
	}

	@Override
	public List<Parameter> findByAnalysisAndTypeAndNoLazy(int idAnalysis,
			String type) {
		List<Parameter> parameters = findByAnalysisAndType(idAnalysis, type);
		for (int i = 0; i < parameters.size(); i++) {
			parameters.set(i, Initialise(parameters.get(i)));
			parameters.get(i).setType(Initialise(parameters.get(i).getType()));
		}
		return parameters;
	}

}
