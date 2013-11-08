/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.Parameter;
import lu.itrust.business.dao.DAOParameter;

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

}
