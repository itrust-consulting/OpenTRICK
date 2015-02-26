package lu.itrust.business.TS.database.dao.hbm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.analysis.helper.AnalysisBaseInfo;
import lu.itrust.business.TS.data.general.Customer;
import lu.itrust.business.TS.data.general.Language;
import lu.itrust.business.TS.data.parameter.Parameter;
import lu.itrust.business.TS.data.standard.Standard;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.usermanagement.User;

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
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#get(int)
	 */
	@Override
	public Analysis get(Integer idAnalysis) throws Exception {
		return (Analysis) getSession().get(Analysis.class, idAnalysis);
	}

	/**
	 * getDefaultProfile: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getDefaultProfile()
	 */
	@Override
	public Analysis getDefaultProfile() throws Exception {
		return (Analysis) getSession().createQuery("Select analysis From Analysis analysis where analysis.defaultProfile = true and analysis.profile = true").uniqueResult();
	}

	/**
	 * getFromIdentifierVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getFromIdentifierVersion(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Analysis getFromIdentifierVersionCustomer(String identifier, String version, Integer customerid) throws Exception {
		String query = "Select analysis From Analysis analysis join analysis.customer customer where analysis.identifier = :identifier and analysis.version = :version and customer.id=analysis.customer.id and customer.id = :customerid";
		return (Analysis) getSession().createQuery(query).setString("identifier", identifier).setString("version", version).setInteger("customerid", customerid).uniqueResult();
	}

	/**
	 * exists: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#exists(int)
	 */
	@Override
	public boolean exists(Integer idAnalysis) throws Exception {
		return ((Long) getSession().createQuery("Select count(*) From Analysis where id = :id").setInteger("id", idAnalysis).uniqueResult()).intValue() > 0;
	}

	/**
	 * exists: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#exists(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean exists(String identifier, String version) throws Exception {
		String query = "Select count(analysis) From Analysis analysis where analysis.identifier = :identifier and analysis.version = :version";
		return ((Long) getSession().createQuery(query).setString("identifier", identifier).setString("version", version).uniqueResult()).intValue() > 0;
	}

	/**
	 * isProfile: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#isProfile(int)
	 */
	@Override
	public boolean isProfile(Integer analysisid) throws Exception {
		String query = "Select analysis.profile From Analysis analysis where analysis.id = :identifier";
		Boolean result = (Boolean) getSession().createQuery(query).setParameter("identifier", analysisid).uniqueResult();
		return result == null ? false : result;
	}

	/**
	 * hasData: <br>
	 * Description
	 * 
	 * @{tags
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#hasData(java.lang.Integer)
	 */
	@Override
	public boolean hasData(Integer idAnalysis) throws Exception {
		String query = "Select analysis.data From Analysis analysis where analysis.id = :identifier";
		Boolean result = (Boolean) getSession().createQuery(query).setParameter("identifier", idAnalysis).uniqueResult();
		return result == null ? false : result;
	}

	/**
	 * getAllAnalysisIDs: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getAllAnalysisIDs()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getAllAnalysisIDs() throws Exception {
		return (List<Integer>) getSession().createQuery("SELECT analysis.id From Analysis analysis").list();
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getAll()
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
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getAllNotEmpty()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllNotEmpty() throws Exception {
		return getSession().createQuery("From Analysis analysis where analysis.empty = :empty").setBoolean("empty", false).list();
	}

	/**
	 * getAllProfiles: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getAllProfiles()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllProfiles() throws Exception {
		return getSession().createQuery("Select analysis From Analysis analysis where analysis.profile = true").list();
	}

	/**
	 * getAllFromUserNameAndCustomerId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getAllFromUserNameAndCustomerId(java.lang.String,
	 *      java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromUserAndCustomer(String userName, Integer customerID) throws Exception {
		String query = "Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and analysis.customer.id = :customer ";
		query += "order by analysis.creationDate desc, analysis.identifier asc, analysis.version desc, analysis.data desc";
		return getSession().createQuery(query).setParameter("username", userName).setParameter("customer", customerID).list();
	}

	/**
	 * getFromUserNameAndCustomerIdAndNotEmpty: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getFromUserNameAndCustomerIdAndNotEmpty(java.lang.String,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllNotEmptyFromUserAndCustomer(String userName, Integer idCustomer) throws Exception {
		String query = "Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and analysis.data = true and ";
		query += "analysis.customer.id = :customer order by analysis.creationDate desc, analysis.identifier asc, analysis.version desc";
		return getSession().createQuery(query).setParameter("username", userName).setParameter("customer", idCustomer).list();
	}

	/**
	 * getFromUserNameAndCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getFromUserNameAndCustomer(java.lang.String,
	 *      java.lang.Integer, int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromUserAndCustomerByPageAndSizeIndex(String login, Integer customer, Integer pageIndex, Integer pageSize) throws Exception {
		String query = "Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and analysis.customer.id = :customer ";
		query += "order by analysis.creationDate desc, analysis.identifier asc, analysis.version desc, analysis.data desc";
		return getSession().createQuery(query).setParameter("username", login).setParameter("customer", customer).setMaxResults(pageSize)
				.setFirstResult((pageIndex - 1) * pageSize).list();
	}

	/**
	 * getAllFromCustomerIdAndProfile: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getAllFromCustomerIdAndProfile(int)
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
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getAllFromCustomerIdAndProfileByPageAndSize(java.lang.Integer,
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
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getAllFromUser(lu.itrust.business.TS.usermanagement.User)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromUser(User user) throws Exception {
		return getSession().createQuery("SELECT analysis FROM Analysis analysis join analysis.userRights userRight WHERE userRight.user = :user").setParameter("user", user).list();
	}

	/**
	 * getAllFromCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getAllFromCustomer(lu.itrust.business.TS.data.general.Customer)
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
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getAllFromCustomerIdentifierVersion(lu.itrust.business.TS.data.general.Customer,
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
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getParameterFromAnalysis(java.lang.Integer,
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
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getLanguageOfAnalysis(int)
	 */
	@Override
	public Language getLanguageOfAnalysis(Integer analysisID) throws Exception {
		return (Language) getSession().createQuery("SELECT language FROM Analysis analysis WHERE analysis.id = :analysisID").setParameter("analysisID", analysisID).uniqueResult();
	}

	/**
	 * getVersionOfAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getVersionOfAnalysis(int)
	 */
	@Override
	public String getVersionOfAnalysis(Integer id) throws Exception {
		return (String) getSession().createQuery("SELECT version From Analysis where id = :id").setParameter("id", id).uniqueResult();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#save(lu.itrust.business.TS.data.analysis.Analysis)
	 */
	@Override
	public void save(Analysis analysis) throws Exception {
		getSession().save(analysis);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#saveOrUpdate(lu.itrust.business.TS.data.analysis.Analysis)
	 */
	@Override
	public void saveOrUpdate(Analysis analysis) throws Exception {
		getSession().saveOrUpdate(analysis);
	}

	/**
	 * remove: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#remove(String)
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
	public List<Analysis> getAllProfileContainsStandard(List<Standard> standards) {
		return getSession()
				.createQuery(
						"Select distinct analysis From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis.profile = true and analysisStandard.standard in :standards")
				.setParameterList("standards", standards).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisBaseInfo> getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(Integer id, String name) {
		List<AnalysisBaseInfo> analysisBaseInfos = new ArrayList<AnalysisBaseInfo>();
		String query = "Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and analysis.data = true and ";
		query += "analysis.customer.id = :customer group by analysis.identifier order by analysis.identifier";
		List<Analysis> analyses = (List<Analysis>) getSession().createQuery(query).setParameter("username", name).setParameter("customer", id).list();
		for (Analysis analysis : analyses)
			analysisBaseInfos.add(new AnalysisBaseInfo(analysis));
		return analysisBaseInfos;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisBaseInfo> getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(Integer id, String username, String identifier) {
		List<AnalysisBaseInfo> analysisBaseInfos = new ArrayList<AnalysisBaseInfo>();
		String query = "Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and analysis.identifier = :identifier  and analysis.data = true and ";
		query += "analysis.customer.id = :customer order by analysis.creationDate desc, analysis.identifier asc, analysis.version desc";
		Iterator<Analysis> iterator = getSession().createQuery(query).setParameter("username", username).setString("identifier", identifier).setParameter("customer", id).iterate();
		while (iterator.hasNext())
			analysisBaseInfos.add(new AnalysisBaseInfo(iterator.next()));
		return analysisBaseInfos;
	}

	@Override
	public int getDefaultProfileId() {
		Integer id = (Integer) getSession().createQuery("Select analysis.id From Analysis analysis where analysis.defaultProfile = true and analysis.profile = true")
				.uniqueResult();
		return id == null ? -1 : id;
	}

	@Override
	public String getLabelFromId(int idAnalysis) {
		return (String) getSession().createQuery("SELECT label From Analysis where id = :id").setParameter("id", idAnalysis).uniqueResult();
	}

	@Override
	public String getCustomerNameFromId(int idAnalysis) {
		return (String) getSession().createQuery("SELECT customer.organisation From Analysis where id = :id").setParameter("id", idAnalysis).uniqueResult();
	}

	@Override
	public boolean isAnalysisUncertainty(Integer analysisID) throws Exception {
		String query = "Select analysis.uncertainty from Analysis as analysis where analysis.id= :id";
		return (boolean) getSession().createQuery(query).setParameter("id", analysisID).uniqueResult();
	}

	@Override
	public boolean isAnalysisCssf(Integer analysisID) throws Exception {
		String query = "Select analysis.cssf from Analysis as analysis where analysis.id= :id";
		return (boolean) getSession().createQuery(query).setParameter("id", analysisID).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllNotEmptyVersion(int idAnalysis) {
		return getSession()
				.createQuery(
						"Select distinct analysis.version from Analysis as analysis where analysis.data = true "
								+ "and analysis.identifier = (select analysis2.identifier from Analysis as analysis2 where analysis2.id = :idAnalysis)")
				.setParameter("idAnalysis", idAnalysis).list();
	}

	@Override
	public String getIdentifierByIdAnalysis(int idAnalysis) {
		return (String) getSession().createQuery("select analysis.identifier from Analysis as analysis where analysis.id = :idAnalysis").setParameter("idAnalysis", idAnalysis)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllNotEmptyVersion(String identifier) {
		return getSession().createQuery("Select distinct analysis.version from Analysis as analysis where analysis.data = true and analysis.identifier = :identifier")
				.setParameter("identifier", identifier).list();
	}

	@Override
	public Integer getCustomerIdByIdAnalysis(int idAnalysis) {
		return (Integer) getSession().createQuery("select analysis.customer.id from Analysis as analysis where analysis.id = :idAnalysis").setParameter("idAnalysis", idAnalysis)
				.uniqueResult();
	}

	@Override
	public boolean isAnalysisOwner(Integer analysisId, String userName) {
		return (boolean) getSession().createQuery("select count(analysis)>0 from Analysis as analysis where analysis.id = :idAnalysis and analysis.owner.login = :username")
				.setParameter("idAnalysis", analysisId).setString("username", userName).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllVersion(String identifier) {
		return getSession().createQuery("Select distinct analysis.version from Analysis as analysis where analysis.identifier = :identifier")
				.setParameter("identifier", identifier).list();
	}

	@Override
	public Integer getIdFromIdentifierAndVersion(String identifier, String version) {
		return (Integer) getSession().createQuery("select id from Analysis where identifier = :identifier and version = :version").setParameter("identifier", identifier)
				.setString("version", version).uniqueResult();
	}

	@Override
	public boolean exists(String identifier) {
		return ((Long) getSession().createQuery("Select count(analysis) From Analysis analysis where analysis.identifier = :identifier").setString("identifier", identifier)
				.uniqueResult()).intValue() > 0;
	}

	@Override
	public Long countByIdentifier(String identifier) {
		return ((Long) getSession().createQuery("Select count(analysis) From Analysis analysis where analysis.identifier = :identifier").setString("identifier", identifier)
				.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getCustomersByIdAnalysis(String identifier) {
		return getSession().createQuery("Select distinct analysis.customer From Analysis analysis where analysis.identifier = :identifier").setString("identifier", identifier).list();
	}
}