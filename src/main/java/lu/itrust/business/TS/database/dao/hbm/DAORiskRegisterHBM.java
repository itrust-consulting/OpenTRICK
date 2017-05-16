/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAORiskRegister;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;

/**
 * DAORiskRegisterHBM.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Feb 18, 2014
 */
@Repository
public class DAORiskRegisterHBM extends DAOHibernate implements DAORiskRegister {

	/**
	 * Constructor: <br>
	 */
	public DAORiskRegisterHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAORiskRegisterHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORiskRegister#get(int)
	 */
	@Override
	public RiskRegisterItem get(Integer id) {
		return (RiskRegisterItem) getSession().get(RiskRegisterItem.class, id);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORiskRegister#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	public boolean belongsToAnalysis(Integer analysisId, Integer riskregisterItemId) {
		String query = "Select count(riskregisterItem)>0 From Analysis as analysis inner join analysis.riskRegisters as riskregisterItem where analysis.id = :analysisid and riskregisterItem.id = ";
		query += ":riskregisterItemId";
		return (boolean) getSession().createQuery(query).setParameter("analysisid", analysisId).setParameter("riskregisterItemId", riskregisterItemId).getSingleResult();
	}

	/*
	 * @Override public RiskRegisterItem getByScenario(Scenario scenario) {
	 * return (RiskRegisterItem)
	 * getSession().createQuery("From RiskRegister where scenario = :scenario").
	 * setParameter ("scenario", scenario); }
	 */
	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORiskRegister#getAllFromAnalysisId(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskRegisterItem> getAllFromAnalysis(Integer analysisId) {
		String query = "SELECT riskregister FROM Analysis as analysis INNER JOIN analysis.riskRegisters as riskregister WHERE analysis.id= :analysisID order by riskregister.netEvaluation.importance desc, riskregister.expectedEvaluation.importance desc, riskregister.rawEvaluation.importance desc";
		return (List<RiskRegisterItem>) getSession().createQuery(query).setParameter("analysisID", analysisId).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORiskRegister#save(lu.itrust.business.TS.model.cssf.RiskRegisterItem)
	 */
	@Override
	public void save(RiskRegisterItem riskRegisterItem) {
		getSession().save(riskRegisterItem);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORiskRegister#saveOrUpdate(lu.itrust.business.TS.model.cssf.RiskRegisterItem)
	 */
	@Override
	public void saveOrUpdate(RiskRegisterItem riskRegisterItem) {
		getSession().saveOrUpdate(riskRegisterItem);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORiskRegister#delete(lu.itrust.business.TS.model.cssf.RiskRegisterItem)
	 */
	@Override
	public void delete(RiskRegisterItem riskRegisterItem) {
		getSession().delete(riskRegisterItem);
	}

	@Override
	public void deleteAllFromAnalysis(Integer analysisID) {
		for (RiskRegisterItem riskItem : getAllFromAnalysis(analysisID))
			getSession().delete(riskItem);
	}

	@Override
	public void delete(Integer id) {
		getSession().createQuery("Delete From RiskRegisterItem where id = :id").setParameter("id", id).executeUpdate();

	}

	@Override
	public RiskRegisterItem merge(RiskRegisterItem riskRegister) {
		return (RiskRegisterItem) getSession().merge(riskRegister);
	}

	@Override
	public RiskRegisterItem getByAssetIdAndScenarioId(int idAsset, int idScenario) {
		return getSession().createQuery("FROM RiskRegisterItem where asset.id = :idAsset and scenario.id = :idScenario", RiskRegisterItem.class).setParameter("idAsset", idAsset)
				.setParameter("idScenario", idScenario).uniqueResultOptional().orElse(null);
	}
}