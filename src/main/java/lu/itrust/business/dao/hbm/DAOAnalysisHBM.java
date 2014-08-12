package lu.itrust.business.dao.hbm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.component.helper.AnalysisBaseInfo;
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
	public Analysis get(Integer idAnalysis) throws Exception {
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
		String query = "From Analysis where identifier = :identifier and version = :version";
		return (Analysis) getSession().createQuery(query).setString("identifier", identifier).setString("version", version).uniqueResult();
	}

	/**
	 * exists: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#exists(int)
	 */
	@Override
	public boolean exists(Integer idAnalysis) throws Exception {
		return ((Long) getSession().createQuery("Select count(*) From Analysis where id = :id").setInteger("id", idAnalysis).uniqueResult()).intValue() > 0;
	}

	/**
	 * exists: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#exists(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean exists(String identifier, String version) throws Exception {
		String query = "Select count(analysis) From Analysis as analysis where analysis.identifier = :identifier and analysis.version = :version";
		return ((Long) getSession().createQuery(query).setString("identifier", identifier).setString("version", version).uniqueResult()).intValue() > 0;
	}

	/**
	 * isProfile: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#isProfile(int)
	 */
	@Override
	public boolean isProfile(Integer analysisid) throws Exception {
		String query = "Select analysis.profile From Analysis as analysis where analysis.id = :identifier";
		Boolean result = (Boolean) getSession().createQuery(query).setParameter("identifier", analysisid).uniqueResult();
		return result == null ? false : result;
	}

	/**
	 * hasData: <br>
	 * Description
	 * 
	 * @{tags
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#hasData(java.lang.Integer)
	 */
	@Override
	public boolean hasData(Integer idAnalysis) throws Exception {
		String query = "Select analysis.data From Analysis as analysis where analysis.id = :identifier";
		Boolean result = (Boolean) getSession().createQuery(query).setParameter("identifier", idAnalysis).uniqueResult();
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
	public List<Analysis> getAllProfiles() throws Exception {
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
	public List<Analysis> getAllFromUserAndCustomer(String userName, Integer customerID) throws Exception {
		String query = "Select userAnalysis.analysis from UserAnalysisRight as userAnalysis where userAnalysis.user.login = :username and userAnalysis.analysis.customer.id = :customer ";
		query += "order by userAnalysis.analysis.creationDate desc, userAnalysis.analysis.identifier asc, userAnalysis.analysis.version desc, userAnalysis.analysis.data desc";
		return getSession().createQuery(query).setParameter("username", userName).setParameter("customer", customerID).list();
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
	public List<Analysis> getAllNotEmptyFromUserAndCustomer(String userName, Integer idCustomer) throws Exception {
		String query = "Select userAnalysis.analysis from UserAnalysisRight as userAnalysis where userAnalysis.user.login = :username and userAnalysis.analysis.data = true and ";
		query += "userAnalysis.analysis.customer.id = :customer order by userAnalysis.analysis.creationDate desc, userAnalysis.analysis.identifier asc, userAnalysis.analysis.version desc";
		return getSession().createQuery(query).setParameter("username", userName).setParameter("customer", idCustomer).list();
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
	public List<Analysis> getAllFromUserAndCustomerByPageAndSizeIndex(String login, Integer customer, Integer pageIndex, Integer pageSize) throws Exception {
		String query = "Select userAnalysis.analysis from UserAnalysisRight as userAnalysis where userAnalysis.user.login = :username and userAnalysis.analysis.customer.id = :customer ";
		query += "order by userAnalysis.analysis.creationDate desc, userAnalysis.analysis.identifier asc, userAnalysis.analysis.version desc, userAnalysis.analysis.data desc";
		return getSession().createQuery(query).setParameter("username", login).setParameter("customer", customer).setMaxResults(pageSize)
				.setFirstResult((pageIndex - 1) * pageSize).list();
	}

	/**
	 * getAllFromCustomerIdAndProfile: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getAllFromCustomerIdAndProfile(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromCustomerAndProfile(Integer idCustomer) throws Exception {
		String query = "SELECT analysis From Analysis as analysis where analysis.customer.id = :customer OR analysis.profile=true";
		return (List<Analysis>) getSession().createQuery(query).setParameter("customer", idCustomer).list();
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
	public List<Analysis> getAllFromCustomerAndProfileByPageAndSizeIndex(Integer customerID, Integer pageIndex, Integer pageSize) throws Exception {
		String query = "SELECT analysis From Analysis as analysis where analysis.customer.id = :customer OR analysis.profile=true";
		return (List<Analysis>) getSession().createQuery(query).setParameter("customer", customerID).setMaxResults(pageSize).setFirstResult((pageIndex) * pageSize).list();
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
		return (List<Analysis>) getSession().createQuery("From Analysis where customer = :customer").setParameter("customer", customer).list();
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
		String query = "From Analysis where customer = :customer and version = :version";
		return (List<Analysis>) getSession().createQuery(query).setParameter("customer", customer).setString("version", version).list();
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
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and parameter.description = :parameter";
		return (Parameter) getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("parameter", Parameter).uniqueResult();
	}

	/**
	 * getLanguageOfAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getLanguageOfAnalysis(int)
	 */
	@Override
	public Language getLanguageOfAnalysis(Integer analysisID) throws Exception {
		return (Language) getSession().createQuery("SELECT language FROM Analysis analysis WHERE analysis.id = :analysisID").setParameter("analysisID", analysisID).uniqueResult();
	}

	/**
	 * getVersionOfAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysis#getVersionOfAnalysis(int)
	 */
	@Override
	public String getVersionOfAnalysis(Integer id) throws Exception {
		return (String) getSession().createQuery("SELECT version From Analysis where id = :id").setParameter("id", id).uniqueResult();
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromCustomer(Integer id) {
		return (List<Analysis>) getSession().createQuery("From Analysis where customer.id = :idCustomer").setParameter("idCustomer", id).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllProfileContainsNorm(List<Norm> norms) {
		return getSession()
				.createQuery(
						"Select distinct analysis From Analysis analysis inner join analysis.analysisNorms analysisNorm where analysis.profile = true and analysisNorm.norm in :norms")
				.setParameterList("norms", norms).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisBaseInfo> getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(Integer id, String name) {
		List<AnalysisBaseInfo> analysisBaseInfos = new ArrayList<AnalysisBaseInfo>();
		String query = "Select userAnalysis.analysis from UserAnalysisRight as userAnalysis where userAnalysis.user.login = :username and userAnalysis.analysis.data = true and ";
		query += "userAnalysis.analysis.customer.id = :customer group by userAnalysis.analysis.identifier order by userAnalysis.analysis.identifier";
		Iterator<Analysis> iterator = getSession().createQuery(query).setParameter("username", name).setParameter("customer", id).iterate();
		while (iterator.hasNext())
			analysisBaseInfos.add(new AnalysisBaseInfo(iterator.next()));
		return analysisBaseInfos;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisBaseInfo> getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(Integer id, String username, String identifier) {
		List<AnalysisBaseInfo> analysisBaseInfos = new ArrayList<AnalysisBaseInfo>();
		String query = "Select userAnalysis.analysis from UserAnalysisRight as userAnalysis where userAnalysis.user.login = :username and userAnalysis.analysis.identifier = :identifier  and userAnalysis.analysis.data = true and ";
		query += "userAnalysis.analysis.customer.id = :customer order by userAnalysis.analysis.creationDate desc, userAnalysis.analysis.identifier asc, userAnalysis.analysis.version desc";
		Iterator<Analysis> iterator = getSession().createQuery(query).setParameter("username", username).setString("identifier", identifier).setParameter("customer", id).iterate();
		while (iterator.hasNext())
			analysisBaseInfos.add(new AnalysisBaseInfo(iterator.next()));
		return analysisBaseInfos;
	}

	@Override
	public int getDefaultProfileId() {
		Integer id = (Integer) getSession().createQuery("Select analysis.id From Analysis as analysis where analysis.defaultProfile = true and analysis.profile = true")
				.uniqueResult();
		return id == null ? -1 : id;
	}
}