package lu.itrust.business.TS.database.dao.hbm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.model.actionplan.ActionPlanAsset;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStandardConformance;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.AnalysisBaseInfo;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.usermanagement.User;

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
	public Analysis get(Integer idAnalysis) {
		return (Analysis) getSession().get(Analysis.class, idAnalysis);
	}

	/**
	 * getDefaultProfile: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getDefaultProfile()
	 */
	@Override
	public Analysis getDefaultProfile() {
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
	public Analysis getFromIdentifierVersionCustomer(String identifier, String version, Integer customerid) {
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
	public boolean exists(Integer idAnalysis) {
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
	public boolean exists(String identifier, String version) {
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
	public boolean isProfile(Integer analysisid) {
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
	public boolean hasData(Integer idAnalysis) {
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
	public List<Integer> getAllAnalysisIDs() {
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
	public List<Analysis> getAll() {
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
	public List<Analysis> getAllNotEmpty() {
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
	public List<Analysis> getAllProfiles() {
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
	public List<Analysis> getAllFromUserAndCustomer(String userName, Integer customerID) {
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
	public List<Analysis> getAllNotEmptyFromUserAndCustomer(String userName, Integer idCustomer) {
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
	public List<Analysis> getAllFromUserAndCustomerByPageAndSizeIndex(String login, Integer customer, Integer pageIndex, Integer pageSize) {
		String query = "Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and analysis.customer.id = :customer ";
		query += "order by analysis.creationDate desc, analysis.identifier asc, analysis.version desc, analysis.data desc";
		return getSession().createQuery(query).setParameter("username", login).setParameter("customer", customer).setMaxResults(pageSize).setFirstResult((pageIndex - 1) * pageSize)
				.list();
	}

	/**
	 * getAllFromCustomerIdAndProfile: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getAllFromCustomerIdAndProfile(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromCustomerAndProfile(Integer idCustomer) {
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
	public List<Analysis> getAllFromCustomerAndProfileByPageAndSizeIndex(Integer customerID, Integer pageIndex, Integer pageSize) {
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
	public List<Analysis> getAllFromUser(User user) {
		return getSession().createQuery("SELECT analysis FROM Analysis analysis join analysis.userRights userRight WHERE userRight.user = :user").setParameter("user", user).list();
	}

	/**
	 * getAllFromCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getAllFromCustomer(lu.itrust.business.TS.model.general.Customer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromCustomer(Customer customer) {
		return (List<Analysis>) getSession().createQuery("From Analysis where customer = :customer").setParameter("customer", customer).list();
	}

	/**
	 * getAllFromCustomerIdentifierVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getAllFromCustomerIdentifierVersion(lu.itrust.business.TS.model.general.Customer,
	 *      java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromCustomerIdentifierVersion(Customer customer, String identifier, String version) {
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
	public Parameter getParameterFromAnalysis(Integer idAnalysis, String Parameter) {
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
	public Language getLanguageOfAnalysis(Integer analysisID) {
		return (Language) getSession().createQuery("SELECT language FROM Analysis analysis WHERE analysis.id = :analysisID").setParameter("analysisID", analysisID).uniqueResult();
	}

	/**
	 * getVersionOfAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#getVersionOfAnalysis(int)
	 */
	@Override
	public String getVersionOfAnalysis(Integer id) {
		return (String) getSession().createQuery("SELECT version From Analysis where id = :id").setParameter("id", id).uniqueResult();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#save(lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@Override
	public void save(Analysis analysis) {
		getSession().save(analysis);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#saveOrUpdate(lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@Override
	public void saveOrUpdate(Analysis analysis) {
		getSession().saveOrUpdate(analysis);
	}

	/**
	 * remove: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysis#remove(String)
	 */
	@Override
	public void delete(Analysis analysis) {
		deleteActionPlanByAnalysisId(analysis.getId());
		deleteActionPlanSummaryByAnalysisId(analysis.getId());
		deleteRiskRegisterFromAnalysis(analysis.getId());
		deleteStandardByAnalysis(analysis);
		getSession().delete(analysis);
	}

	@SuppressWarnings("unchecked")
	private void deleteActionPlanByAnalysisId(Integer analysisID) {
		String query = "Select actionplans FROM Analysis analysis INNER JOIN analysis.actionPlans actionplans WHERE analysis.id= :analysisID";

		List<ActionPlanEntry> actionplans = (List<ActionPlanEntry>) getSession().createQuery(query).setParameter("analysisID", analysisID).list();
		for (ActionPlanEntry entry : actionplans) {
			List<ActionPlanAsset> assets = entry.getActionPlanAssets();
			for (ActionPlanAsset asset : assets)
				getSession().delete(asset);
			getSession().delete(entry);
		}

	}

	/**
	 * deleteAllFromAnalysis: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlanSummary#deleteAllFromAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	private void deleteActionPlanSummaryByAnalysisId(Integer analysisID) {
		String query = "Select summary From Analysis as analysis inner join analysis.summaries as summary where analysis.id = :idAnalysis";
		List<SummaryStage> summaries = (List<SummaryStage>) getSession().createQuery(query).setParameter("idAnalysis", analysisID).list();
		for (SummaryStage summary : summaries) {
			for (SummaryStandardConformance conformance : summary.getConformances())
				getSession().delete(conformance);
			getSession().delete(summary);
		}
	}

	@SuppressWarnings("unchecked")
	public void deleteRiskRegisterFromAnalysis(Integer analysisID) {
		getSession().createQuery("SELECT riskregisters FROM Analysis as analysis INNER JOIN analysis.riskRegisters as riskregisters WHERE analysis.id= :analysisID")
				.setParameter("analysisID", analysisID).list().stream().forEach(riskRegister -> getSession().delete(riskRegister));
	}

	@SuppressWarnings("unchecked")
	private void deleteStandardByAnalysis(Analysis analysis) {

		List<AnalysisStandard> standards = new ArrayList<AnalysisStandard>();

		for (AnalysisStandard standard : analysis.getAnalysisStandards()) {

			getSession().delete(standard);

			if (standard.getStandard().isAnalysisOnly())
				standards.add(standard);
		}

		analysis.getAnalysisStandards().clear();

		getSession().saveOrUpdate(analysis);

		for (AnalysisStandard standard : standards) {

			Standard tmpstandard = standard.getStandard();

			List<MeasureDescription> mesDescs = (List<MeasureDescription>) getSession()
					.createQuery("SELECT mesDesc from MeasureDescription mesDesc where mesDesc.standard= :standard").setParameter("standard", tmpstandard).list();

			for (MeasureDescription mesDesc : mesDescs) {
				for (MeasureDescriptionText mesDescText : mesDesc.getMeasureDescriptionTexts())
					getSession().delete(mesDescText);
				getSession().delete(mesDesc);
			}
			getSession().delete(tmpstandard);
		}

	}

	@Override
	public void delete(Integer analysisId) {
		Analysis analysis = get(analysisId);
		if (analysis != null)
			delete(analysis);
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
	public List<AnalysisBaseInfo> getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(Integer id, String name, List<AnalysisRight> rights) {
		List<AnalysisBaseInfo> analysisBaseInfos = new ArrayList<AnalysisBaseInfo>();
		String query = "Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and (userRight.right in :rights or analysis.owner = userRight.user) and analysis.data = true and ";
		query += "analysis.customer.id = :customer group by analysis.identifier order by analysis.identifier";
		List<Analysis> analyses = (List<Analysis>) getSession().createQuery(query).setParameter("username", name).setParameterList("rights", rights).setParameter("customer", id)
				.list();
		for (Analysis analysis : analyses)
			analysisBaseInfos.add(new AnalysisBaseInfo(analysis));
		return analysisBaseInfos;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisBaseInfo> getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(Integer id, String username, String identifier, List<AnalysisRight> rights) {
		List<AnalysisBaseInfo> analysisBaseInfos = new ArrayList<AnalysisBaseInfo>();
		String query = "Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and (userRight.right in :rights or analysis.owner = userRight.user) and analysis.identifier = :identifier  and analysis.data = true and ";
		query += "analysis.customer.id = :customer order by analysis.creationDate desc, analysis.identifier asc, analysis.version desc";
		Iterator<Analysis> iterator = getSession().createQuery(query).setParameter("username", username).setParameterList("rights", rights).setString("identifier", identifier)
				.setParameter("customer", id).iterate();
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
	public boolean isAnalysisUncertainty(Integer analysisID) {
		String query = "Select analysis.uncertainty from Analysis as analysis where analysis.id= :id";
		return (boolean) getSession().createQuery(query).setParameter("id", analysisID).uniqueResult();
	}

	@Override
	public boolean isAnalysisCssf(Integer analysisID) {
		String query = "Select analysis.cssf from Analysis as analysis where analysis.id= :id";
		return (boolean) getSession().createQuery(query).setParameter("id", analysisID).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllNotEmptyVersion(int idAnalysis) {
		return getSession()
				.createQuery("Select distinct analysis.version from Analysis as analysis where analysis.data = true "
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
		return getSession().createQuery("Select distinct analysis.version from Analysis as analysis where analysis.identifier = :identifier").setParameter("identifier", identifier)
				.list();
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
		return getSession().createQuery("Select distinct analysis.customer From Analysis analysis where analysis.identifier = :identifier").setString("identifier", identifier)
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllByIdentifier(String identifier) {
		return getSession().createQuery("From Analysis analysis where analysis.identifier = :identifier").setString("identifier", identifier).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getCustomersByIdAnalysis(int analysisId) {
		return getSession()
				.createQuery(
						"Select distinct analysis.customer From Analysis analysis where analysis.identifier = (select analysis2.identifier From Analysis as analysis2 where analysis2 = :analysisId)")
				.setInteger("analysisId", analysisId).list();
	}

	@Override
	public boolean isAnalysisCustomer(int idAnalysis, int idCustomer) {
		return (boolean) getSession().createQuery("Select count(*)>0 From Analysis where id = :idAnalysis and customer.id = :idCustomer").setInteger("idAnalysis", idAnalysis)
				.setInteger("idCustomer", idCustomer).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getNamesByUserAndCustomerAndNotEmpty(String username, Integer idCustomer) {
		return getSession()
				.createQuery(
						"Select distinct analysis.label from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and analysis.data = true and analysis.customer.id = :customer order by analysis.creationDate desc, analysis.identifier asc, analysis.version desc")
				.setParameter("username", username).setParameter("customer", idCustomer).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllByUserAndCustomerAndNameAndNotEmpty(String username, Integer idCustomer, String name) {
		return getSession()
				.createQuery(
						"Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and analysis.customer.id = :customer and  analysis.data = true and analysis.label = :name  order by analysis.creationDate desc, analysis.identifier asc, analysis.version desc")
				.setParameter("username", username).setParameter("customer", idCustomer).setString("name", name).list();
	}

	@Override
	public boolean isProfileNameInUsed(String name) {
		return (boolean) getSession().createQuery("Select count(*)>0 From Analysis where profile = true and label = :name").setString("name", name).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllVersion(Integer analysisId) {
		return getSession()
				.createQuery(
						"Select distinct analysis.version From Analysis analysis where analysis.identifier = (select analysis2.identifier From Analysis as analysis2 where analysis2 = :analysisId)")
				.setInteger("analysisId", analysisId).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAll(List<Integer> ids) {
		return getSession().createQuery("From Analysis analysis where analysis.id in :analysisIds").setParameterList("analysisIds", ids).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllContains(MeasureDescription measureDescription) {
		return getSession()
				.createQuery(
						"Select analysis From Analysis analysis inner join analysis.analysisStandards as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.standard = :standard and measure.measureDescription = :measureDescription")
				.setParameter("standard", measureDescription.getStandard()).setParameter("measureDescription", measureDescription).list();
	}

	@Override
	public Long countNotProfileDistinctIdentifier() {
		return (Long) getSession().createQuery("Select count(distinct identifier) From Analysis where  profile = false").uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getNotProfileIdentifiers(int page, int size) {
		return getSession().createQuery("Select distinct identifier From Analysis where  profile = false").setFirstResult((page - 1) * size).setMaxResults(size).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromOwner(User user) {
		return getSession().createQuery("From Analysis where owner = :owner").setParameter("owner", user).list();
	}

	@Override
	public boolean hasData(String identifier) {
		return (boolean) getSession().createQuery("Select count(*) > 0 From Analysis where identifier = :identifier and data = true").setString("identifier", identifier)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllHasRightsAndContainsStandard(String username, List<AnalysisRight> rights, List<Standard> standards) {
		return getSession()
				.createQuery(
						"Select distinct userAnalysisRight.analysis From UserAnalysisRight userAnalysisRight inner join userAnalysisRight.analysis.analysisStandards as analysisStandard  where  userAnalysisRight.user.login = :username and userAnalysisRight.analysis.profile = false and userAnalysisRight.right in :rights and analysisStandard.standard in :standards")
				.setString("username", username).setParameterList("rights", rights).setParameterList("standards", standards).list();
	}

	@Override
	public boolean existsByNameAndCustomerId(String name, int idCustomer) {
		return (boolean) getSession().createQuery("Select count(*)>0 From Analysis analysis where analysis.customer.id = :idCustomer and analysis.label = :name")
				.setString("name", name).setInteger("idCustomer", idCustomer).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllNotEmpty(int pageIndex, int pageSize) {
		return getSession().createQuery("From Analysis where data = true").setMaxResults(pageSize).setFirstResult((pageIndex - 1) * pageSize).list();
	}

	@Override
	public int countNotEmpty() {
		return ((Long) getSession().createQuery("Select count(*) From Analysis where data = true").uniqueResult()).intValue();
	}

	@Override
	public Analysis getByCustomerAndNameAndVersion(int customerId, String name, String version) {
		return (Analysis) getSession().createQuery("From Analysis where customer.id = :customerId and label = :name and version = :version").setInteger("customerId", customerId)
				.setString("name", name).setString("version", version).uniqueResult();
	}

	@Override
	public Analysis getByIdentifierAndVersion(String identifier, String version) {
		return (Analysis) getSession().createQuery("from Analysis where identifier = :identifier and version = :version").setParameter("identifier", identifier)
				.setString("version", version).uniqueResult();
	}

	@Override
	public Analysis getProfileByName(String name) {
		return (Analysis) getSession().createQuery("from Analysis where label = :name and profile = true").setParameter("name", name).uniqueResult();
	}

	@Override
	public Analysis getByAnalysisStandardId(int idAnalysisStandard) {
		return (Analysis) getSession()
				.createQuery("Select analysis From Analysis analysis inner join analysis.analysisStandards as analysisStandard where analysisStandard.id = :idAnalysisStandard")
				.setInteger("idAnalysisStandard", idAnalysisStandard).uniqueResult();
	}

	public int countNotEmptyNoItemInformationAndRiskInformation() {
		return ((Long) getSession()
				.createQuery(
						"Select count(analysis) From Analysis analysis where analysis.data = true and (analysis.itemInformations IS EMPTY or analysis.riskInformations IS EMPTY)")
				.uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllNotEmptyNoItemInformationAndRiskInformation(int pageIndex, int pageSize) {
		return getSession()
				.createQuery("Select analysis From Analysis analysis where analysis.data = true and (analysis.itemInformations IS EMPTY or analysis.riskInformations IS EMPTY)")
				.setMaxResults(pageSize).setFirstResult((pageIndex - 1) * pageSize).list();
	}

	@Override
	public String getProjectIdById(Integer idAnalysis) {
		return (String) getSession().createQuery("Select project From Analysis where id = :idAnalysis").setParameter("idAnalysis", idAnalysis).uniqueResult();
	}

	@Override
	public boolean hasProject(int idAnalysis) {
		String projectId = getProjectIdById(idAnalysis);
		return projectId != null && !projectId.isEmpty();
	}

	@Override
	public String getProjectIdByIdentifier(String identifier) {
		return (String) getSession().createQuery("Select distinct project From Analysis where identifier = :identifier and project IS NOT NULL")
				.setParameter("identifier", identifier).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllProjectIds() {
		return getSession().createQuery("Select distinct project From Analysis where project IS NOT NULL").list();
	}
}
