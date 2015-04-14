package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.database.dao.DAOItemInformation;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOItemInformationHBM.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Jan 16, 2013
 */
@Repository
public class DAOItemInformationHBM extends DAOHibernate implements DAOItemInformation {

	/**
	 * Constructor: <br>
	 */
	public DAOItemInformationHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOItemInformationHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOItemInformation#get(int)
	 */
	@Override
	public ItemInformation get(Integer id) throws Exception {
		return (ItemInformation) getSession().get(ItemInformation.class, id);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param idIteminformation
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOItemInformation#getFromAnalysisById(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public ItemInformation getFromAnalysisById(Integer idAnalysis, Integer idIteminformation) throws Exception {
		String query =
			"Select iteminformation From Analysis as analysis inner join analysis.itemInformations as iteminformation where analysis.id = :idAnalysis and iteminformation.id = :idIteminformation";
		return (ItemInformation) getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("idIteminformation", idIteminformation).uniqueResult();
	}

	/**
	 * getFromAnalysisIdByDescription: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOItemInformation#getFromAnalysisIdByDescription(int,
	 *      java.lang.String)
	 */
	@Override
	public ItemInformation getFromAnalysisByDescription(Integer analysisId, String description) throws Exception {
		String query = "Select itemInformation From Analysis as analysis inner join analysis.itemInformations as itemInformation  where analysis.id = :id and ";
		query += "iteminformation.description = :iteminformation";
		return (ItemInformation) getSession().createQuery(query).setParameter("id", analysisId).setParameter("description", description).uniqueResult();
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOItemInformation#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer iteminformationId) throws Exception {
		String query = "Select count(itemInformation) From Analysis as analysis inner join analysis.itemInformations as itemInformation where analysis.id = :analysisid and ";
		query += "itemInformation.id = :itemInformationId";
		return ((Long) getSession().createQuery(query).setParameter("analysisid", analysisId).setParameter("itemInformationId", iteminformationId).uniqueResult()).intValue() > 0;
	}

	/**
	 * getAllItemInformation: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOItemInformation#getAllItemInformation()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ItemInformation> getAll() throws Exception {
		return getSession().createQuery("From ItemInformation").list();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOItemInformation#getAllFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ItemInformation> getAllFromAnalysis(Integer analysisID) throws Exception {
		String casepart  = "";
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
		
		String query = "Select itemInformation From Analysis as analysis inner join analysis.itemInformations as itemInformation  where analysis.id = :id order by case itemInformation.description " + casepart;
		return getSession().createQuery(query).setInteger("id", analysisID).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOItemInformation#save(lu.itrust.business.TS.model.iteminformation.ItemInformation)
	 */
	@Override
	public void save(ItemInformation itemInformation) throws Exception {
		getSession().save(itemInformation);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOItemInformation#saveOrUpdate(lu.itrust.business.TS.model.iteminformation.ItemInformation)
	 */
	@Override
	public void saveOrUpdate(ItemInformation itemInformation) throws Exception {
		getSession().saveOrUpdate(itemInformation);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOItemInformation#delete(lu.itrust.business.TS.model.iteminformation.ItemInformation)
	 */
	@Override
	public void delete(ItemInformation itemInformation) throws Exception {
		getSession().delete(itemInformation);
	}
}