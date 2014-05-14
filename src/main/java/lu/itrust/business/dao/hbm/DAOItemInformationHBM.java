package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.dao.DAOItemInformation;

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
	 * @see lu.itrust.business.dao.DAOItemInformation#get(int)
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
	 * @see lu.itrust.business.dao.DAOItemInformation#getFromAnalysisById(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public ItemInformation getFromAnalysisById(Integer idAnalysis, Integer idIteminformation) throws Exception {
		String query =
			"Select iteminformation From Analysis as analysis inner join analysis.iteminformations as iteminformation where analysis.id = :idAnalysis and iteminformation.id = :idIteminformation";
		return (ItemInformation) getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("idIteminformation", idIteminformation).uniqueResult();
	}

	/**
	 * getFromAnalysisIdByDescription: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOItemInformation#getFromAnalysisIdByDescription(int,
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
	 * @see lu.itrust.business.dao.DAOItemInformation#belongsToAnalysis(java.lang.Integer,
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
	 * @see lu.itrust.business.dao.DAOItemInformation#getAllItemInformation()
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
	 * @see lu.itrust.business.dao.DAOItemInformation#getAllFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ItemInformation> getAllFromAnalysis(Integer analysisID) throws Exception {
		String query = "Select itemInformation From Analysis as analysis inner join analysis.itemInformations as itemInformation  where analysis.id = :id";
		return getSession().createQuery(query).setInteger("id", analysisID).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOItemInformation#save(lu.itrust.business.TS.ItemInformation)
	 */
	@Override
	public void save(ItemInformation itemInformation) throws Exception {
		getSession().save(itemInformation);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOItemInformation#saveOrUpdate(lu.itrust.business.TS.ItemInformation)
	 */
	@Override
	public void saveOrUpdate(ItemInformation itemInformation) throws Exception {
		getSession().saveOrUpdate(itemInformation);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOItemInformation#delete(lu.itrust.business.TS.ItemInformation)
	 */
	@Override
	public void delete(ItemInformation itemInformation) throws Exception {
		getSession().delete(itemInformation);
	}
}