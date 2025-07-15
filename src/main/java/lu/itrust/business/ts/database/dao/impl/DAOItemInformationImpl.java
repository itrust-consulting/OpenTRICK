package lu.itrust.business.ts.database.dao.impl;

import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOItemInformation;
import lu.itrust.business.ts.model.iteminformation.ItemInformation;

/**
 * DAOItemInformationImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Jan 16, 2013
 */
@Repository
public class DAOItemInformationImpl extends DAOHibernate implements DAOItemInformation {

	/**
	 * Constructor: <br>
	 */
	public DAOItemInformationImpl() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOItemInformationImpl(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOItemInformation#get(int)
	 */
	@Override
	public ItemInformation get(Integer id) {
		return (ItemInformation) getSession().get(ItemInformation.class, id);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param idIteminformation
	 * @return
	 * @
	 * 
	 *   @see
	 *   lu.itrust.business.ts.database.dao.DAOItemInformation#getFromAnalysisById(java.lang.Integer,
	 *   java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ItemInformation getFromAnalysisById(Integer idAnalysis, Integer idIteminformation) {
		String query = "Select iteminformation From Analysis as analysis inner join analysis.itemInformations as iteminformation where analysis.id = :idAnalysis and iteminformation.id = :idIteminformation";
		return (ItemInformation) createQueryWithCache(query).setParameter("idAnalysis", idAnalysis)
				.setParameter("idIteminformation", idIteminformation).uniqueResultOptional().orElse(null);
	}

	/**
	 * getFromAnalysisIdByDescription: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOItemInformation#getFromAnalysisIdByDescription(int,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ItemInformation getFromAnalysisByDescription(Integer analysisId, String description) {
		String query = "Select itemInformation From Analysis as analysis inner join analysis.itemInformations as itemInformation  where analysis.id = :id and ";
		query += "iteminformation.description = :iteminformation";
		return (ItemInformation) createQueryWithCache(query).setParameter("id", analysisId)
				.setParameter("description", description).uniqueResultOptional().orElse(null);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOItemInformation#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer iteminformationId) {
		String query = "Select count(itemInformation) > 0 From Analysis as analysis inner join analysis.itemInformations as itemInformation where analysis.id = :analysisid and ";
		query += "itemInformation.id = :itemInformationId";
		return (boolean) createQueryWithCache(query).setParameter("analysisid", analysisId)
				.setParameter("itemInformationId", iteminformationId).getSingleResult();
	}

	/**
	 * getAllItemInformation: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOItemInformation#getAllItemInformation()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ItemInformation> getAll() {
		return createQueryWithCache("From ItemInformation").getResultList();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOItemInformation#getAllFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ItemInformation> getAllFromAnalysis(Integer analysisID) {
		String casepart = "";
		casepart += "when 'type_organism' then -25 ";
		casepart += "when 'type_profit_organism' then -24 ";
		casepart += "when 'name_organism' then -23 ";
		casepart += "when 'presentation_organism' then -22 ";
		casepart += "when 'sector_organism' then -21 ";
		casepart += "when 'responsible_organism' then -20 ";
		casepart += "when 'staff_organism' then -19 ";
		casepart += "when 'activities_organism' then -18 ";
		casepart += "when 'occupation' then -17 ";
		casepart += "when 'juridic' then -16 ";
		casepart += "when 'pol_organisation' then -15 ";
		casepart += "when 'management_organisation' then -14 ";
		casepart += "when 'premises' then -13 ";
		casepart += "when 'requirements' then -12 ";
		casepart += "when 'stakeholder_identification' then -11 ";
		casepart += "when 'stakeholder_relation' then -10 ";
		casepart += "when 'expectations' then -9 ";
		casepart += "when 'environment' then -8 ";
		casepart += "when 'interface' then -7 ";
		casepart += "when 'role_responsability' then -6 ";
		casepart += "when 'escalation_way' then -5 ";
		casepart += "when 'processus_development' then -4 ";
		casepart += "when 'document_conserve' then -3 ";
		casepart += "when 'excluded_assets' then -2 ";
		casepart += "when 'functional' then -1 ";
		casepart += "when 'strategic' then 0 ";
		casepart += "else 1 ";
		casepart += "end";
		String query = "Select itemInformation From Analysis as analysis inner join analysis.itemInformations as itemInformation  where analysis.id = :id order by case itemInformation.description "
				+ casepart;
		return createQueryWithCache(query).setParameter("id", analysisID).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOItemInformation#save(lu.itrust.business.ts.model.iteminformation.ItemInformation)
	 */
	@Override
	public void save(ItemInformation itemInformation) {
		getSession().save(itemInformation);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOItemInformation#saveOrUpdate(lu.itrust.business.ts.model.iteminformation.ItemInformation)
	 */
	@Override
	public void saveOrUpdate(ItemInformation itemInformation) {
		getSession().saveOrUpdate(itemInformation);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOItemInformation#delete(lu.itrust.business.ts.model.iteminformation.ItemInformation)
	 */
	@Override
	public void delete(ItemInformation itemInformation) {
		getSession().delete(itemInformation);
	}

	@Override
	public void delete(Collection<ItemInformation> itemInformation) {
		if (itemInformation != null)
			itemInformation.forEach(this::delete);
	}
}