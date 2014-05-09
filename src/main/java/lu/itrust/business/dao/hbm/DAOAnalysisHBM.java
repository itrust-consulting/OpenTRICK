package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.Parameter;
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
	 * Constructor: <br>
	 */
	public DAOAnalysisHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOAnalysisHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#get(int)
	 */
	@Override
	public Analysis get(int idAnalysis) throws Exception {
		return (Analysis) getSession().get(Analysis.class, idAnalysis);
	}

	/**
	 * getDefaultProfile: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getDefaultProfile()
	 */
	@Override
	public Analysis getDefaultProfile() throws Exception {
		return (Analysis) getSession().createQuery("Select analysis From Analysis as analysis where analysis.defaultProfile = true and analysis.profile = true").uniqueResult();
	}

	/**
	 * getFromIdentifierVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getFromIdentifierVersion(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Analysis getFromIdentifierVersion(String identifier, String version) throws Exception {
		Query query = getSession().createQuery("From Analysis where identifier = :identifier and version = :version");
		query.setString("identifier", identifier);
		query.setString("version", version);
		return (Analysis) query.uniqueResult();
	}

	/**
	 * exists: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#exists(int)
	 */
	@Override
	public boolean exists(int idAnalysis) {
		return ((Long) getSession().createQuery("Select count(*) From Analysis where id = :id").setInteger("id", idAnalysis).uniqueResult()).intValue() > 0;
	}

	/**
	 * isProfile: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#isProfile(int)
	 */
	@Override
	public boolean isProfile(int analysisid) {
		Boolean result =
			(Boolean) getSession().createQuery("Select analysis.profile From Analysis as analysis where analysis.id = :identifier").setParameter("identifier", analysisid).uniqueResult();
		return result == null ? false : result;
	}

	/**
	 * getAllAnalysisIDs: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getAllAnalysisIDs()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getAllAnalysisIDs() throws Exception {
		return (List<Integer>) getSession().createQuery("SELECT analysis.id From Analysis as analysis").list();
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAll() throws Exception {
		Query query = getSession().createQuery("From Analysis");
		return (List<Analysis>) query.list();
	}

	/**
	 * getAllNotEmpty: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getAllNotEmpty()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllNotEmpty() throws Exception {
		return getSession().createQuery("From Analysis as analysis where analysis.empty = :empty").setBoolean("empty", false).list();
	}

	/**
	 * getAllProfiles: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getAllProfiles()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllProfiles() {
		return getSession().createQuery("Select analysis From Analysis as analysis where analysis.profile = true").list();
	}

	/**
	 * getAllFromUserNameAndCustomerId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getAllFromUserNameAndCustomerId(java.lang.String,
	 *      java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromUserNameAndCustomerId(String userName, Integer customerID) throws Exception {
		return getSession()
				.createQuery(
						"Select userAnalysis.analysis from UserAnalysisRight as userAnalysis where userAnalysis.user.login = :username and userAnalysis.analysis.customer.id = :customer order by userAnalysis.analysis.creationDate desc, userAnalysis.analysis.identifier asc, userAnalysis.analysis.version desc, userAnalysis.analysis.data desc")
				.setParameter("username", userName).setParameter("customer", customerID).list();
	}

	/**
	 * getFromUserNameAndCustomerIdAndNotEmpty: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getFromUserNameAndCustomerIdAndNotEmpty(java.lang.String,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getFromUserNameAndCustomerIdAndNotEmpty(String userName, int idCustomer) {
		return getSession()
				.createQuery(
						"Select userAnalysis.analysis from UserAnalysisRight as userAnalysis where userAnalysis.user.login = :username and userAnalysis.analysis.data = true and userAnalysis.analysis.customer.id = :customer order by userAnalysis.analysis.creationDate desc, userAnalysis.analysis.identifier asc, userAnalysis.analysis.version desc")
				.setParameter("username", userName).setParameter("customer", idCustomer).list();
	}

	/**
	 * getFromUserNameAndCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getFromUserNameAndCustomer(java.lang.String,
	 *      java.lang.Integer, int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getFromUserNameAndCustomer(String login, Integer customer, int pageIndex, int pageSize) {
		return getSession()
				.createQuery(
						"Select userAnalysis.analysis from UserAnalysisRight as userAnalysis where userAnalysis.user.login = :username and userAnalysis.analysis.customer.id = :customer order by userAnalysis.analysis.creationDate desc, userAnalysis.analysis.identifier asc, userAnalysis.analysis.version desc, userAnalysis.analysis.data desc")
				.setParameter("username", login).setParameter("customer", customer).setMaxResults(pageSize).setFirstResult((pageIndex - 1) * pageSize).list();
	}

	/**
	 * getAllFromCustomerIdAndProfile: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getAllFromCustomerIdAndProfile(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromCustomerIdAndProfile(int idCustomer) {
		return (List<Analysis>) getSession().createQuery("SELECT analysis From Analysis as analysis where analysis.customer.id = :customer OR analysis.profile=true").setParameter(
				"customer", idCustomer).list();
	}

	/**
	 * getAllFromCustomerIdAndProfileByPageAndSize: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getAllFromCustomerIdAndProfileByPageAndSize(java.lang.Integer,
	 *      java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromCustomerIdAndProfileByPageAndSize(Integer customerID, Integer pageIndex, Integer pageSize) {
		return (List<Analysis>) getSession().createQuery("SELECT analysis From Analysis as analysis where analysis.customer.id = :customer OR analysis.profile=true").setParameter(
				"customer", customerID).setMaxResults(pageSize).setFirstResult((pageIndex) * pageSize).list();
	}

	/**
	 * getAllFromUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getAllFromUser(lu.itrust.business.TS.usermanagement.User)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromUser(User user) throws Exception {
		return getSession().createQuery("SELECT uar.analysis FROM UserAnalysisRight uar WHERE uar.user = :user").setParameter("user", user).list();
	}

	/**
	 * getAllFromCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getAllFromCustomer(lu.itrust.business.TS.Customer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromCustomer(Customer customer) throws Exception {
		Query query = getSession().createQuery("From Analysis where customer = :customer");
		query.setParameter("customer", customer);
		return (List<Analysis>) query.list();
	}

	/**
	 * getAllFromCustomerIdentifierVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getAllFromCustomerIdentifierVersion(lu.itrust.business.TS.Customer,
	 *      java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromCustomerIdentifierVersion(Customer customer, String identifier, String version) throws Exception {
		Query query = getSession().createQuery("From Analysis where customer = :customer and version = :version");
		query.setParameter("customer", customer);
		query.setString("version", version);
		return (List<Analysis>) query.list();
	}

	/**
	 * getParameterFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getParameterFromAnalysis(java.lang.Integer,
	 *      java.lang.String)
	 */
	@Override
	public Parameter getParameterFromAnalysis(Integer idAnalysis, String Parameter) throws Exception {
		return (Parameter) getSession().createQuery(
				"Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and parameter.description = :parameter")
				.setParameter("idAnalysis", idAnalysis).setParameter("parameter", Parameter).uniqueResult();
	}

	/**
	 * getLanguageOfAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getLanguageOfAnalysis(int)
	 */
	@Override
	public Language getLanguageOfAnalysis(int analysisID) throws Exception {
		return (Language) getSession().createQuery("SELECT language FROM Analysis analysis WHERE analysis.id = :analysisID").setParameter("analysisID", analysisID).uniqueResult();
	}

	/**
	 * getVersionOfAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getVersionOfAnalysis(int)
	 */
	@Override
	public String getVersionOfAnalysis(int id) throws Exception {
		Query query = getSession().createQuery("SELECT version From Analysis where id = :id");
		query.setInteger("id", id);
		return (String) query.uniqueResult();
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
	public void delete(Analysis analysis) throws Exception {
		getSession().delete(analysis);

	}

	@Override
	public void delete(Integer analysisId) throws Exception {
		Analysis analysis = get(analysisId);
		if (analysis != null) {
			delete(analysis);
		}
	}
}