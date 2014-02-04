package lu.itrust.business.dao.hbm;

import java.sql.Timestamp;
import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAOAnalysis;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOAnalysisHBM: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
@Repository
public class DAOAnalysisHBM extends DAOHibernate implements DAOAnalysis {

	/**
	 * 
	 */
	public DAOAnalysisHBM() {
	}

	/**
	 * @param sessionFactory
	 */
	public DAOAnalysisHBM(Session session) {
		super(session);
	}

	@Override
	public Analysis get(int id) throws Exception {
		return (Analysis) getSession().get(Analysis.class, id);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#get(long, java.lang.String,
	 *      java.sql.Date)
	 */
	@Override
	public Analysis get(int id, String identifier, String version, String creationDate) throws Exception {

		Query query = getSession().createQuery("From Analysis where id = :id identifier = :identifier and version = :version and creationDate = :creationDate");

		query.setInteger("id", id);

		query.setString("identifier", identifier);

		query.setString("version", version);

		query.setString("creationDate", creationDate);

		return (Analysis) query.uniqueResult();

	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#get(java.lang.String,
	 *      java.lang.String, java.sql.Date)
	 */
	@Override
	public Analysis get(int id, String identifier, String version, Timestamp creationDate) throws Exception {
		return get(id, identifier, version, creationDate);
	}

	@Override
	public Analysis getFromIdentifierVersion(String identifier, String version) throws Exception {
		Query query = getSession().createQuery("From Analysis where identifier = :identifier and version = :version");

		query.setString("identifier", identifier);

		query.setString("version", version);

		return (Analysis) query.uniqueResult();
	}

	/**
	 * analysisExist: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#analysisExist(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean analysisExist(String identifier, String version) throws Exception {
		Query query = getSession().createQuery("select count(*) From Analysis where identifier = :identifier and version = :version");

		query.setString("identifier", identifier);

		query.setString("version", version);

		return ((Long) query.uniqueResult()) != 0;
	}

	/**
	 * loadAllFromCustomerIdentifierVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#loadAllFromCustomerIdentifierVersion(lu.itrust.business.TS.Customer,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> loadAllFromCustomerIdentifierVersion(Customer customer, String identifier, String version) throws Exception {

		Query query = getSession().createQuery("From Analysis where customer = :customer and version = :version");

		query.setParameter("customer", customer);

		query.setString("version", version);

		return (List<Analysis>) query.list();

	}

	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#loadAllFromCustomer(lu.itrust.business.TS.Customer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> loadAllFromCustomer(Customer customer) throws Exception {
		Query query = getSession().createQuery("From Analysis where customer = :customer");

		query.setParameter("customer", customer);

		return (List<Analysis>) query.list();

	}

	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> loadAll() throws Exception {

		Query query = getSession().createQuery("From Analysis");

		return (List<Analysis>) query.list();

	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#save(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public void save(Analysis analysis) throws Exception {
		getSession().save(analysis);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#saveOrUpdate(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public void saveOrUpdate(Analysis analysis) throws Exception {
		getSession().saveOrUpdate(analysis);
	}

	/**
	 * remove: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#remove(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public void remove(Analysis analysis) throws Exception {
		getSession().delete(analysis);

	}

	@Override
	public void remove(Integer analysisId) throws Exception {
		Analysis analysis = get(analysisId);
		if (analysis != null) {
			remove(analysis);
			System.out.println("Analysis was deleted");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> loadAllNotEmpty() throws Exception {
		return getSession().createQuery("From Analysis as analysis where analysis.empty = :empty").setBoolean("empty", false).list();
	}

	@Override
	public boolean exist(int id) {
		return ((Long) getSession().createQuery("Select count(*) From Analysis where id = :id").setInteger("id", id).uniqueResult()).intValue() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> loadAllFromUser(User user) throws Exception {
		return getSession().createQuery("SELECT uar.analysis FROM UserAnalysisRight uar WHERE uar.user = :user").setParameter("user", user).list();
	}

	@Override
	public Language getLanguageOfAnalysis(int analysisID) throws Exception {
		return (Language) getSession().createQuery("SELECT language FROM Analysis analysis WHERE analysis.id = :analysisID").setParameter("analysisID", analysisID).uniqueResult();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> loadByUserAndCustomer(String login, String customer) {
		System.out.println(login + " "+customer);
		return getSession()
				.createQuery(
						"Select userAnalysis.analysis from UserAnalysisRight as userAnalysis where userAnalysis.user.login = :username and userAnalysis.analysis.customer.organisation = :customer order by userAnalysis.analysis.creationDate desc, userAnalysis.analysis.identifier asc, userAnalysis.analysis.version desc, userAnalysis.analysis.data desc")
				.setParameter("username", login).setParameter("customer", customer).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> loadByUserAndCustomer(String login, String customer, int pageIndex, int pageSize) {
		return getSession()
				.createQuery(
						"Select userAnalysis.analysis from UserAnalysisRight as userAnalysis where userAnalysis.user.login = :username and userAnalysis.analysis.customer.organisation = :customer order by userAnalysis.analysis.creationDate desc, userAnalysis.analysis.identifier asc, userAnalysis.analysis.version desc, userAnalysis.analysis.data desc")
				.setParameter("username", login).setParameter("customer", customer).setMaxResults(pageSize).setFirstResult((pageIndex - 1) * pageSize).list();
	}

	@Override
	public String getVersionOfAnalysis(int id) throws Exception {
		Query query = getSession().createQuery("SELECT version From Analysis where id = :id");

		query.setInteger("id", id);

		return (String) query.uniqueResult();
	}

}