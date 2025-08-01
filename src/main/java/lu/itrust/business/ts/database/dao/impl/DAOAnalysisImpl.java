package lu.itrust.business.ts.database.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.model.actionplan.ActionPlanAsset;
import lu.itrust.business.ts.model.actionplan.ActionPlanEntry;
import lu.itrust.business.ts.model.actionplan.summary.SummaryStage;
import lu.itrust.business.ts.model.actionplan.summary.SummaryStandardConformance;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisSetting;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.analysis.helper.AnalysisBaseInfo;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.ts.usermanagement.User;

/**
 * DAOAnalysisImpl: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
@Repository
public class DAOAnalysisImpl extends DAOHibernate implements DAOAnalysis {

	/**
	 * Constructor: <br>
	 */
	public DAOAnalysisImpl() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOAnalysisImpl(Session session) {
		super(session);
	}

	@Override
	public Long countByIdentifier(String identifier) {
		return ((Long) createQueryWithCache("Select count(analysis) From Analysis analysis where analysis.identifier = :identifier")
				.setParameter("identifier", identifier)
				.getSingleResult());
	}

	@Override
	public int countNotEmpty() {
		return ((Long) createQueryWithCache("Select count(*) From Analysis where data = true").getSingleResult())
				.intValue();
	}

	public int countNotEmptyNoItemInformationAndRiskInformation() {
		return ((Long) createQueryWithCache(
						"Select count(analysis) From Analysis analysis where analysis.data = true and (analysis.itemInformations IS EMPTY or analysis.riskInformations IS EMPTY)")
				.getSingleResult()).intValue();
	}

	@Override
	public Long countNotProfileDistinctIdentifier() {
		return (Long) createQueryWithCache("Select count(distinct identifier) From Analysis where  profile = false")
				.getSingleResult();
	}

	/**
	 * remove: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#remove(String)
	 */
	@Override
	public void delete(Analysis analysis) {
		deleteActionPlanByAnalysisId(analysis.getId());
		deleteActionPlanSummaryByAnalysisId(analysis.getId());
		deleteRiskRegisterFromAnalysis(analysis.getId());
		analysis.getRiskProfiles().forEach(riskProfile -> riskProfile.getMeasures().clear());
		deleteStandardByAnalysis(analysis);
		getSession().remove(analysis);
	}

	@Override
	public void delete(Integer analysisId) {
		Analysis analysis = get(analysisId);
		if (analysis != null)
			delete(analysis);
	}

	@SuppressWarnings("unchecked")
	public void deleteRiskRegisterFromAnalysis(Integer analysisID) {
		createQueryWithCache(
				"SELECT riskregisters FROM Analysis as analysis INNER JOIN analysis.riskRegisters as riskregisters WHERE analysis.id= :analysisID")
				.setParameter("analysisID", analysisID).getResultList().stream()
				.forEach(riskRegister -> getSession().remove(riskRegister));
	}

	/**
	 * exists: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#exists(int)
	 */
	@Override
	public boolean exists(Integer idAnalysis) {
		return (boolean) createQueryWithCache("Select count(*)> 0 From Analysis where id = :id")
				.setParameter("id", idAnalysis).getSingleResult();
	}

	@Override
	public boolean exists(String identifier) {
		return (boolean) createQueryWithCache("Select count(analysis) >0 From Analysis analysis where analysis.identifier = :identifier")
				.setParameter("identifier", identifier)
				.getSingleResult();
	}

	/**
	 * exists: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#exists(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean exists(String identifier, String version) {
		return (boolean) createQueryWithCache(
				"Select count(*) > 0 From Analysis analysis where analysis.identifier = :identifier and analysis.version = :version")
				.setParameter("identifier", identifier).setParameter("version", version).getSingleResult();
	}

	@Override
	public boolean existsByNameAndCustomerId(String name, int idCustomer) {
		return (boolean) createQueryWithCache(
				"Select count(*)>0 From Analysis analysis where analysis.customer.id = :idCustomer and analysis.label = :name")
				.setParameter("name", name).setParameter("idCustomer", idCustomer).getSingleResult();
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#get(int)
	 */
	@Override
	public Analysis get(Integer idAnalysis) {
		return (Analysis) getSession().get(Analysis.class, idAnalysis);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAll() {
		return createQueryWithCache("From Analysis").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAll(List<Integer> ids) {
		return createQueryWithCache("From Analysis analysis where analysis.id in :analysisIds")
				.setParameterList("analysisIds", ids).getResultList();
	}

	/**
	 * getAllAnalysisIDs: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getAllAnalysisIDs()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getAllAnalysisIDs() {
		return (List<Integer>) createQueryWithCache("SELECT analysis.id From Analysis analysis").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllByIdentifier(String identifier) {
		return createQueryWithCache("From Analysis analysis where analysis.identifier = :identifier")
				.setParameter("identifier", identifier).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllByUserAndCustomerAndNameAndNotEmpty(String username, Integer idCustomer, String name) {
		return createQueryWithCache(
				"Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and analysis.customer.id = :customer and  analysis.data = true and analysis.label = :name  order by analysis.creationDate desc, analysis.identifier asc, analysis.version desc")
				.setParameter("username", username).setParameter("customer", idCustomer).setParameter("name", name)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllContains(MeasureDescription measureDescription) {
		return createQueryWithCache(
				"Select analysis From Analysis analysis inner join analysis.analysisStandards as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.standard = :standard and measure.measureDescription = :measureDescription")
				.setParameter("standard", measureDescription.getStandard())
				.setParameter("measureDescription", measureDescription).getResultList();
	}

	/**
	 * getAllFromCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getAllFromCustomer(lu.itrust.business.ts.model.general.Customer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromCustomer(Customer customer) {
		return (List<Analysis>) createQueryWithCache("From Analysis where customer = :customer")
				.setParameter("customer", customer).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromCustomer(Integer id) {
		return (List<Analysis>) createQueryWithCache("From Analysis where customer.id = :idCustomer")
				.setParameter("idCustomer", id).getResultList();
	}

	/**
	 * getAllFromCustomerIdAndProfile: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getAllFromCustomerIdAndProfile(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromCustomerAndProfile(Integer idCustomer) {
		String query = "SELECT analysis From Analysis as analysis where analysis.customer.id = :customer OR analysis.profile=true";
		return (List<Analysis>) createQueryWithCache(query).setParameter("customer", idCustomer).getResultList();
	}

	/**
	 * getAllFromCustomerIdAndProfileByPageAndSize: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getAllFromCustomerIdAndProfileByPageAndSize(java.lang.Integer,
	 *      java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromCustomerAndProfileByPageAndSizeIndex(Integer customerID, Integer pageIndex,
			Integer pageSize) {
		String query = "SELECT analysis From Analysis as analysis where analysis.customer.id = :customer OR analysis.profile=true";
		return (List<Analysis>) createQueryWithCache(query).setParameter("customer", customerID)
				.setMaxResults(pageSize).setFirstResult((pageIndex) * pageSize).getResultList();
	}

	/**
	 * getAllFromCustomerIdentifierVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getAllFromCustomerIdentifierVersion(lu.itrust.business.ts.model.general.Customer,
	 *      java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromCustomerIdentifierVersion(Customer customer, String identifier, String version) {
		String query = "From Analysis where customer = :customer and version = :version";
		return (List<Analysis>) createQueryWithCache(query).setParameter("customer", customer)
				.setParameter("version", version).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromOwner(User user) {
		return createQueryWithCache("From Analysis where owner = :owner").setParameter("owner", user)
				.getResultList();
	}

	/**
	 * getAllFromUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getAllFromUser(lu.itrust.business.ts.usermanagement.User)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromUser(User user) {
		return createQueryWithCache(
				"SELECT analysis FROM Analysis analysis join analysis.userRights userRight WHERE userRight.user = :user")
				.setParameter("user", user)
				.getResultList();
	}

	/**
	 * getAllFromUserNameAndCustomerId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getAllFromUserNameAndCustomerId(java.lang.String,
	 *      java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromUserAndCustomer(String userName, Integer customerID) {
		String query = "Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and analysis.customer.id = :customer ";
		query += "order by analysis.creationDate desc, analysis.identifier asc, analysis.version desc, analysis.data desc";
		return createQueryWithCache(query).setParameter("username", userName).setParameter("customer", customerID)
				.getResultList();
	}

	/**
	 * getFromUserNameAndCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getFromUserNameAndCustomer(java.lang.String,
	 *      java.lang.Integer, int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllFromUserAndCustomerByPageAndSizeIndex(String login, Integer customer, Integer pageIndex,
			Integer pageSize) {
		String query = "Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and analysis.customer.id = :customer ";
		query += "order by analysis.creationDate desc, analysis.identifier asc, analysis.version desc, analysis.data desc";
		return createQueryWithCache(query).setParameter("username", login).setParameter("customer", customer)
				.setMaxResults(pageSize).setFirstResult((pageIndex - 1) * pageSize)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllHasRightsAndContainsStandard(String username, List<AnalysisRight> rights,
			List<Standard> standards, AnalysisType... analysisTypes) {
		return analysisTypes.length < 1 ? Collections.emptyList()
				: createQueryWithCache(
						"Select distinct userAnalysisRight.analysis From UserAnalysisRight userAnalysisRight inner join userAnalysisRight.analysis.analysisStandards as analysisStandard  where  userAnalysisRight.user.login = :username and userAnalysisRight.analysis.profile = false and userAnalysisRight.analysis.type in :types and userAnalysisRight.right in :rights and analysisStandard.standard in :standards")
						.setParameter("username", username).setParameterList("rights", rights)
						.setParameterList("types", analysisTypes).setParameterList("standards", standards)
						.getResultList();
	}

	/**
	 * getAllNotEmpty: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getAllNotEmpty()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllNotEmpty() {
		return createQueryWithCache("From Analysis analysis where analysis.empty = :empty")
				.setParameter("empty", false).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllNotEmpty(int pageIndex, int pageSize) {
		return createQueryWithCache("From Analysis where data = true").setMaxResults(pageSize)
				.setFirstResult((pageIndex - 1) * pageSize).getResultList();
	}

	/**
	 * getFromUserNameAndCustomerIdAndNotEmpty: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getFromUserNameAndCustomerIdAndNotEmpty(java.lang.String,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllNotEmptyFromUserAndCustomer(String userName, Integer idCustomer) {
		String query = "Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and analysis.data = true and ";
		query += "analysis.customer.id = :customer order by analysis.creationDate desc, analysis.identifier asc, analysis.version desc";
		return createQueryWithCache(query).setParameter("username", userName).setParameter("customer", idCustomer)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllNotEmptyNoItemInformationAndRiskInformation(int pageIndex, int pageSize) {
		return createQueryWithCache(
						"Select analysis From Analysis analysis where analysis.data = true and (analysis.itemInformations IS EMPTY or analysis.riskInformations IS EMPTY)")
				.setMaxResults(pageSize).setFirstResult((pageIndex - 1) * pageSize).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllNotEmptyVersion(int idAnalysis) {
		return createQueryWithCache("Select distinct analysis.version from Analysis as analysis where analysis.data = true "
						+ "and analysis.identifier = (select analysis2.identifier from Analysis as analysis2 where analysis2.id = :idAnalysis)")
				.setParameter("idAnalysis", idAnalysis).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllNotEmptyVersion(String identifier) {
		return createQueryWithCache(
				"Select distinct analysis.version from Analysis as analysis where analysis.data = true and analysis.identifier = :identifier")
				.setParameter("identifier", identifier).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllProfileContainsStandard(List<Standard> standards, AnalysisType... analysisTypes) {
		return analysisTypes.length < 1 ? Collections.emptyList()
				: createQueryWithCache(
						"Select distinct analysis From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis.profile = true and analysis.type in :types and analysisStandard.standard in :standards")
						.setParameterList("standards", standards).setParameterList("types", analysisTypes)
						.getResultList();
	}

	/**
	 * getAllProfiles: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getAllProfiles()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getAllProfiles() {
		return createQueryWithCache("Select analysis From Analysis analysis where analysis.profile = true")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllProjectIds() {
		return createQueryWithCache("Select distinct project From Analysis where project IS NOT NULL")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllVersion(Integer analysisId) {
		return createQueryWithCache(
				"Select distinct analysis.version From Analysis analysis where analysis.identifier = (select analysis2.identifier From Analysis as analysis2 where analysis2 = :analysisId)")
				.setParameter("analysisId", analysisId).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllVersion(String identifier) {
		return createQueryWithCache(
				"Select distinct analysis.version from Analysis as analysis where analysis.identifier = :identifier")
				.setParameter("identifier", identifier)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public AnalysisType getAnalysisTypeById(int idAnalysis) {
		return (AnalysisType) createQueryWithCache("Select type From Analysis where id = :idAnalysis")
				.setParameter("idAnalysis", idAnalysis).uniqueResultOptional()
				.orElse(null);
	}

	@Override
	public List<AnalysisBaseInfo> getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(Integer id,
			String username, String identifier, AnalysisType type,
			List<AnalysisRight> rights) {
		String query = "Select analysis from Analysis analysis join analysis.userRights userRight where "
				+ "userRight.user.login = :username and (userRight.right in :rights or analysis.owner = userRight.user) and "
				+ "analysis.identifier = :identifier and analysis.type = :type and analysis.data = true and "
				+ "analysis.customer.id = :customer order by analysis.creationDate desc, analysis.identifier asc, analysis.version desc";
		return createQueryWithCache(query, Analysis.class).setParameter("username", username)
				.setParameterList("rights", rights).setParameter("identifier", identifier)
				.setParameter("customer", id).setParameter("type", type).getResultList().stream()
				.map(analysis -> new AnalysisBaseInfo((Analysis) analysis))
				.collect(Collectors.toList());
	}

	@Override
	public List<AnalysisBaseInfo> getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(Integer id,
			String username, String identifier, List<AnalysisRight> rights) {
		String query = "Select analysis from Analysis analysis join analysis.userRights userRight where "
				+ "userRight.user.login = :username and (userRight.right in :rights or analysis.owner = userRight.user) and "
				+ "analysis.identifier = :identifier  and analysis.data = true and "
				+ "analysis.customer.id = :customer order by analysis.creationDate desc, analysis.identifier asc, analysis.version desc";
		return createQueryWithCache(query, Analysis.class).setParameter("username", username)
				.setParameterList("rights", rights).setParameter("identifier", identifier)
				.setParameter("customer", id).getResultList().stream()
				.map(analysis -> new AnalysisBaseInfo((Analysis) analysis)).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Analysis getByAnalysisStandardId(int idAnalysisStandard) {
		return (Analysis) createQueryWithCache(
						"Select analysis From Analysis analysis inner join analysis.analysisStandards as analysisStandard where analysisStandard.id = :idAnalysisStandard")
				.setParameter("idAnalysisStandard", idAnalysisStandard).uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Analysis getByCustomerAndNameAndVersion(int customerId, String name, String version) {
		return (Analysis) createQueryWithCache("From Analysis where customer.id = :customerId and label = :name and version = :version")
				.setParameter("customerId", customerId)
				.setParameter("name", name).setParameter("version", version).uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Analysis getByIdentifierAndVersion(String identifier, String version) {
		return (Analysis) createQueryWithCache("from Analysis where identifier = :identifier and version = :version")
				.setParameter("identifier", identifier)
				.setParameter("version", version).uniqueResultOptional().orElse(null);
	}

	/**
	 * Select distinct x from tabX order by y. do not word with MYSQL 5.7.X
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getByUsernameAndCustomerAndNoEmptyAndGroupByIdentifier(String username, Integer customerId) {
		return createQueryWithCache(
				"Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and analysis.data = true and analysis.customer.id = :customer group by analysis.identifier order by analysis.label asc, analysis.identifier asc, analysis.version desc")
				.setParameter("username", username).setParameter("customer", customerId).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Analysis getByUsernameAndId(String username, Integer analysisId) {
		return (Analysis) createQueryWithCache(
				"Select analysis From UserAnalysisRight userAnalysisRight inner join userAnalysisRight.analysis as analysis where userAnalysisRight.user.login = :username and analysis.id = :analysisId")
				.setParameter("username", username).setParameter("analysisId", analysisId).uniqueResultOptional()
				.orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Analysis> getByUsernameAndIds(String username, List<Integer> ids) {
		return createQueryWithCache(
				"Select analysis From UserAnalysisRight userAnalysisRight inner join userAnalysisRight.analysis as analysis where userAnalysisRight.user.login = :username and analysis.id in :ids")
				.setParameter("username", username).setParameterList("ids", ids).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer getCustomerIdByIdAnalysis(int idAnalysis) {
		return (Integer) createQueryWithCache("select analysis.customer.id from Analysis as analysis where analysis.id = :idAnalysis")
				.setParameter("idAnalysis", idAnalysis)
				.uniqueResultOptional().orElse(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getCustomerNameFromId(int idAnalysis) {
		return (String) createQueryWithCache("SELECT customer.organisation From Analysis where id = :id")
				.setParameter("id", idAnalysis).uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getCustomersByIdAnalysis(int analysisId) {
		return createQueryWithCache(
				"Select distinct analysis.customer From Analysis analysis where analysis.identifier = (select analysis2.identifier From Analysis as analysis2 where analysis2.id = :analysisId)")
				.setParameter("analysisId", analysisId).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getCustomersByIdAnalysis(String identifier) {
		return createQueryWithCache(
				"Select distinct analysis.customer From Analysis analysis where analysis.identifier = :identifier")
				.setParameter("identifier", identifier)
				.getResultList();
	}

	/**
	 * getDefaultProfile: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getDefaultProfile()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Analysis getDefaultProfile(AnalysisType analysisType) {
		return (Analysis) createQueryWithCache(
						"Select analysis From Analysis analysis where analysis.defaultProfile = true and analysis.profile = true and analysis.type = :type", Analysis.class)
				.setParameter("type", analysisType).uniqueResultOptional().orElse(null);
	}

	@Override
	public int findDefaultProfileIdByAnalysisTypeAndLanguage(AnalysisType analysisType, Language language) {
		return createQueryWithCache(
						"Select analysis.id From Analysis analysis where analysis.defaultProfile = true and analysis.profile = true and analysis.type = :type and analysis.language = :language",
						Integer.class)
				.setParameter("type", analysisType).setParameter("language", language).uniqueResultOptional().orElse(0);
	}

	@Override
	public List<Analysis> getDefaultProfiles() {
		return createQueryWithCache(
				"Select analysis From Analysis analysis where analysis.defaultProfile = true and analysis.profile = true",
				Analysis.class).getResultList();
	}

	/**
	 * getFromIdentifierVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getFromIdentifierVersion(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Analysis getFromIdentifierVersionCustomer(String identifier, String version, Integer customerid) {
		String query = "Select analysis From Analysis analysis join analysis.customer customer where analysis.identifier = :identifier and analysis.version = :version and customer.id=analysis.customer.id and customer.id = :customerid";
		return createQueryWithCache(query, Analysis.class).setParameter("identifier", identifier)
				.setParameter("version", version).setParameter("customerid", customerid)
				.uniqueResultOptional().orElse(null);
	}

	@Override
	public List<Analysis> getFromUserNameAndNotEmpty(String username, List<AnalysisRight> rights) {
		return createQueryWithCache(
				"Select distinct userAnalysisRight.analysis From UserAnalysisRight userAnalysisRight where userAnalysisRight.user.login = :username and userAnalysisRight.analysis.data = true and userAnalysisRight.analysis.profile = false and userAnalysisRight.right in :rights",
				Analysis.class).setParameter("username", username).setParameterList("rights", rights).getResultList();
	}

	@Override
	public List<AnalysisBaseInfo> getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(Integer id,
			String username, AnalysisType type, List<AnalysisRight> rights) {
		Map<String, Boolean> filter = new HashMap<>();
		String query = "Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and (userRight.right in :rights or analysis.owner = userRight.user) and analysis.type = :type and analysis.data = true and analysis.customer.id = :customer order by analysis.label";
		return createQueryWithCache(query, Analysis.class).setParameter("type", type)
				.setParameter("username", username).setParameterList("rights", rights)
				.setParameter("customer", id).getResultList().stream().filter(filterByIdentifier(filter))
				.map(mapToBaseInfo()).collect(Collectors.toList());
	}

	@Override
	public List<AnalysisBaseInfo> getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(Integer id,
			String username, List<AnalysisRight> rights) {
		Map<String, Boolean> filter = new HashMap<>();
		String query = "Select analysis from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and (userRight.right in :rights or analysis.owner = userRight.user) and analysis.data = true and analysis.customer.id = :customer order by analysis.label";
		return createQueryWithCache(query, Analysis.class).setParameter("username", username)
				.setParameterList("rights", rights).setParameter("customer", id).getResultList()
				.stream().filter(filterByIdentifier(filter)).map(mapToBaseInfo()).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getIdAndVersionByIdentifierAndCustomerAndUsername(String identifier, Integer idCustomer,
			String username) {
		return createQueryWithCache(
				"Select userAnalysisRight.analysis.id , userAnalysisRight.analysis.version From UserAnalysisRight userAnalysisRight where  userAnalysisRight.user.login = :username and userAnalysisRight.analysis.customer.id = :customerId and userAnalysisRight.analysis.identifier = :identifier and userAnalysisRight.analysis.data = true and userAnalysisRight.analysis.profile = false and userAnalysisRight.right in :rights",
				Object[].class)
				.setParameter("username", username).setParameter("identifier", identifier)
				.setParameter("username", username).setParameter("customerId", idCustomer)
				.setParameterList("rights", new AnalysisRight[] { AnalysisRight.ALL, AnalysisRight.EXPORT })
				.getResultList();
	}

	/**
	 * Only analysis user can export.
	 * 
	 * @param name
	 * @param idCustomer
	 * @return Object[2], [0]=identifier, [1] = name
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getIdentifierAndNameByUserAndCustomer(String username, Integer idCustomer) {
		return createQueryWithCache(
				"Select userAnalysisRight.analysis.identifier , userAnalysisRight.analysis.label From UserAnalysisRight userAnalysisRight where  userAnalysisRight.user.login = :username and userAnalysisRight.analysis.customer.id = :customerId and userAnalysisRight.analysis.data = true and userAnalysisRight.analysis.profile = false and userAnalysisRight.right in :rights group by userAnalysisRight.analysis.identifier, userAnalysisRight.analysis.label",
				Object[].class)
				.setParameter("username", username).setParameter("customerId", idCustomer)
				.setParameterList("rights", new AnalysisRight[] { AnalysisRight.ALL, AnalysisRight.EXPORT })
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getIdentifierByIdAnalysis(int idAnalysis) {
		return (String) createQueryWithCache("select analysis.identifier from Analysis as analysis where analysis.id = :idAnalysis")
				.setParameter("idAnalysis", idAnalysis)
				.uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer getIdFromIdentifierAndVersion(String identifier, String version) {
		return (Integer) createQueryWithCache("select id from Analysis where identifier = :identifier and version = :version")
				.setParameter("identifier", identifier)
				.setParameter("version", version).uniqueResultOptional().orElse(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getLabelFromId(int idAnalysis) {
		return (String) createQueryWithCache("SELECT label From Analysis where id = :id")
				.setParameter("id", idAnalysis).uniqueResultOptional().orElse(null);
	}

	/**
	 * getLanguageOfAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getLanguageOfAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Language getLanguageOfAnalysis(Integer analysisID) {
		return (Language) createQueryWithCache("SELECT language FROM Analysis analysis WHERE analysis.id = :analysisID")
				.setParameter("analysisID", analysisID)
				.uniqueResultOptional().orElse(null);
	}

	/**
	 * Select distinct x from tabX order by y. do not word with MYSQL 5.7.X
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getNamesByUserAndCustomerAndNotEmpty(String username, Integer idCustomer) {
		return createQueryWithCache(
				"Select distinct analysis.label from Analysis analysis join analysis.userRights userRight where userRight.user.login = :username and analysis.data = true and analysis.customer.id = :customer order by analysis.label asc")
				.setParameter("username", username).setParameter("customer", idCustomer).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getNotProfileIdentifiers(int page, int size) {
		return createQueryWithCache("Select distinct identifier From Analysis where  profile = false")
				.setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
	}

	/**
	 * getParameterFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getParameterFromAnalysis(java.lang.Integer,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IParameter getParameterFromAnalysis(Integer idAnalysis, String Parameter) {
		String query = "Select parameter From Analysis as analysis inner join analysis.parameters as parameter where analysis.id = :idAnalysis and parameter.description = :parameter";
		return (IParameter) createQueryWithCache(query).setParameter("idAnalysis", idAnalysis)
				.setParameter("parameter", Parameter).uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Analysis getProfileByName(String name) {
		return (Analysis) createQueryWithCache("from Analysis where label = :name and profile = true")
				.setParameter("name", name).uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getProjectIdById(Integer idAnalysis) {
		return (String) createQueryWithCache("Select project From Analysis where id = :idAnalysis")
				.setParameter("idAnalysis", idAnalysis).uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getProjectIdByIdentifier(String identifier) {
		return (String) createQueryWithCache(
						"Select distinct project From Analysis where identifier = :identifier and project IS NOT NULL")
				.setParameter("identifier", identifier).uniqueResultOptional().orElse(null);
	}

	@Override
	public Map<String, String> getSettingsByIdAnalysis(Integer idAnalysis) {
		return createQueryWithCache(
						"Select KEY(setting) as key , VALUE(setting) as value  From Analysis analysis join analysis.settings as setting where analysis.id = :analysisid",
						Object[].class)
				.setParameter("analysisid", idAnalysis).getResultList().stream()
				.collect(Collectors.toMap(result -> result[0].toString(), result -> result[1].toString()));
	}

	/**
	 * getVersionOfAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#getVersionOfAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getVersionOfAnalysis(Integer id) {
		return (String) createQueryWithCache("SELECT version From Analysis where id = :id").setParameter("id", id)
				.uniqueResultOptional().orElse(null);
	}

	/**
	 * hasData: <br>
	 * Description
	 * 
	 * @{tags
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#hasData(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean hasData(Integer idAnalysis) {
		return (boolean) createQueryWithCache("Select analysis.data From Analysis analysis where analysis.id = :identifier")
				.setParameter("identifier", idAnalysis)
				.uniqueResultOptional().orElse(false);

	}

	@Override
	public boolean hasData(String identifier) {
		return (boolean) createQueryWithCache("Select count(*) > 0 From Analysis where identifier = :identifier and data = true")
				.setParameter("identifier", identifier)
				.getSingleResult();
	}

	@Override
	public boolean hasDefault(AnalysisType analysisType) {
		return createQueryWithCache(
						"Select count(analysis) > 0 From Analysis analysis where analysis.defaultProfile = true and analysis.profile = true and analysis.type = :type",
						Boolean.class)
				.setParameter("type", analysisType).uniqueResultOptional().orElse(false);
	}

	@Override
	public boolean hasProject(int idAnalysis) {
		String projectId = getProjectIdById(idAnalysis);
		return projectId != null && !projectId.isEmpty();
	}

	@Override
	public boolean isAnalysisCustomer(int idAnalysis, int idCustomer) {
		return (boolean) createQueryWithCache("Select count(*)> 0 From Analysis where id = :idAnalysis and customer.id = :idCustomer")
				.setParameter("idAnalysis", idAnalysis)
				.setParameter("idCustomer", idCustomer).getSingleResult();
	}

	@Override
	public boolean isAnalysisOwner(Integer analysisId, String userName) {
		return (boolean) createQueryWithCache(
				"select count(analysis)> 0 from Analysis as analysis where analysis.id = :idAnalysis and analysis.owner.login = :username")
				.setParameter("idAnalysis", analysisId).setParameter("username", userName).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isAnalysisUncertainty(Integer analysisID) {
		String query = "Select analysis.uncertainty from Analysis as analysis where analysis.id= :id";
		return (boolean) createQueryWithCache(query).setParameter("id", analysisID).uniqueResultOptional()
				.orElse(false);
	}

	@Override
	public boolean isDefaultProfile(int analysisId) {
		return createQueryWithCache(
						"Select count(analysis) > 0 From Analysis analysis where analysis.id = :idAnalysis and analysis.defaultProfile = true and analysis.profile = true",
						Boolean.class)
				.setParameter("idAnalysis", analysisId).uniqueResultOptional().orElse(false);
	}

	/**
	 * isProfile: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#isProfile(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean isProfile(Integer analysisid) {
		return (boolean) createQueryWithCache("Select analysis.profile From Analysis analysis where analysis.id = :analysisid")
				.setParameter("analysisid", analysisid)
				.uniqueResultOptional().orElse(false);

	}

	@Override
	public boolean isProfileNameInUsed(String name) {
		return (boolean) createQueryWithCache("Select count(*)>0 From Analysis where profile = true and label = :name")
				.setParameter("name", name).getSingleResult();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#save(lu.itrust.business.ts.model.analysis.Analysis)
	 */
	@Override
	public void save(Analysis analysis) {
		getSession().save(analysis);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysis#saveOrUpdate(lu.itrust.business.ts.model.analysis.Analysis)
	 */
	@Override
	public void saveOrUpdate(Analysis analysis) {
		getSession().saveOrUpdate(analysis);
	}

	@SuppressWarnings("unchecked")
	private void deleteActionPlanByAnalysisId(Integer analysisID) {
		String query = "Select actionplans FROM Analysis analysis INNER JOIN analysis.actionPlans actionplans WHERE analysis.id= :analysisID";

		List<ActionPlanEntry> actionplans = createQueryWithCache(query, ActionPlanEntry.class)
				.setParameter("analysisID", analysisID).getResultList();
		for (ActionPlanEntry entry : actionplans) {
			List<ActionPlanAsset> assets = entry.getActionPlanAssets();
			for (ActionPlanAsset asset : assets)
				getSession().remove(asset);
			getSession().remove(entry);
		}

	}

	/**
	 * deleteAllFromAnalysis: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanSummary#deleteAllFromAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	private void deleteActionPlanSummaryByAnalysisId(Integer analysisID) {
		String query = "Select summary From Analysis as analysis inner join analysis.summaries as summary where analysis.id = :idAnalysis";
		List<SummaryStage> summaries = (List<SummaryStage>) createQueryWithCache(query)
				.setParameter("idAnalysis", analysisID).getResultList();
		for (SummaryStage summary : summaries) {
			for (SummaryStandardConformance conformance : summary.getConformances())
				getSession().remove(conformance);
			getSession().remove(summary);
		}
	}

	@SuppressWarnings("unchecked")
	private void deleteStandardByAnalysis(Analysis analysis) {

		List<AnalysisStandard> standards = new ArrayList<AnalysisStandard>();

		for (AnalysisStandard standard : analysis.getAnalysisStandards().values()) {

			getSession().remove(standard);

			if (standard.getStandard().isAnalysisOnly())
				standards.add(standard);
		}

		analysis.getAnalysisStandards().clear();

		getSession().saveOrUpdate(analysis);

		for (AnalysisStandard standard : standards) {

			Standard tmpstandard = standard.getStandard();

			List<MeasureDescription> mesDescs = (List<MeasureDescription>) getSession()
					.createQuery("SELECT mesDesc from MeasureDescription mesDesc where mesDesc.standard= :standard")
					.setParameter("standard", tmpstandard).getResultList();

			for (MeasureDescription mesDesc : mesDescs) {
				for (MeasureDescriptionText mesDescText : mesDesc.getMeasureDescriptionTexts())
					getSession().remove(mesDescText);
				getSession().remove(mesDesc);
			}
			getSession().remove(tmpstandard);
		}

	}

	/**
	 * group by identifier do not supported by mysql 5.7
	 */
	private Predicate<? super Analysis> filterByIdentifier(Map<String, Boolean> filter) {
		return analysis -> filter.put(analysis.getIdentifier(), true) == null;
	}

	private Function<? super Analysis, ? extends AnalysisBaseInfo> mapToBaseInfo() {
		return analysis -> new AnalysisBaseInfo(analysis);

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T findSetting(Integer idAnalysis, AnalysisSetting setting) {
		String value = createQueryWithCache(
						"Select VALUE(setting) From Analysis analysis inner join analysis.settings as setting where analysis.id = :idAnalysis and KEY(setting) = :key",
						String.class)
				.setParameter("idAnalysis", idAnalysis).setParameter("key", setting.name()).uniqueResult();
		return (T) Analysis.findSetting(setting, value);
	}

	@Override
	public Analysis findByIdAndEager(Integer analysisId) {
		Analysis analysis = get(analysisId);
		if (analysis != null) {
			Hibernate.isInitialized(analysis);
			Hibernate.isInitialized(analysis.getActionPlans());
			Hibernate.isInitialized(analysis.getAnalysisStandards());
			Hibernate.isInitialized(analysis.getAssessments());
			Hibernate.isInitialized(analysis.getAssets());
			Hibernate.isInitialized(analysis.getCustomer());
			Hibernate.isInitialized(analysis.getDynamicParameters());
			Hibernate.isInitialized(analysis.getHistories());
			Hibernate.isInitialized(analysis.getImpactParameters());
			Hibernate.isInitialized(analysis.getItemInformations());
			Hibernate.isInitialized(analysis.getLanguage());
			Hibernate.isInitialized(analysis.getLikelihoodParameters());
			Hibernate.isInitialized(analysis.getMaturityParameters());
			Hibernate.isInitialized(analysis.getPhases());
			Hibernate.isInitialized(analysis.getRiskAcceptanceParameters());
			Hibernate.isInitialized(analysis.getRiskProfiles());
			Hibernate.isInitialized(analysis.getRiskRegisters());
			Hibernate.isInitialized(analysis.getScenarios());
			Hibernate.isInitialized(analysis.getSimpleParameters());
			Hibernate.isInitialized(analysis.getSummaries());
		}
		return analysis;
	}

	@Override
	public String findIdentifierByCustomerAndLabel(int customerId, String label) {
		return createQueryWithCache(
				"Select analysis.identifier From Analysis analysis where analysis.label = :label and analysis.customer.id = :customerId",
				String.class)
				.setParameter("label", label).setParameter("customerId", customerId).setMaxResults(1).uniqueResult();
	}

	@Override
	public Analysis findByIdAndCustomer(Integer id, Customer customer) {
		return createQueryWithCache("From Analysis analysis where analysis.id = :id and analysis.customer = :customer",
						Analysis.class)
				.setParameter("id", id).setParameter("customer", customer).uniqueResult();
	}
}
