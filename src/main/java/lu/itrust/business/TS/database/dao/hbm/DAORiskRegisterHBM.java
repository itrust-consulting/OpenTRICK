/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.database.dao.DAORiskRegister;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

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
	public RiskRegisterItem get(Integer id) throws Exception {
		return (RiskRegisterItem) getSession().get(RiskRegisterItem.class, id);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORiskRegister#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	public boolean belongsToAnalysis(Integer analysisId,Integer riskregisterItemId) throws Exception {
		String query = "Select count(riskregisterItem) From Analysis as analysis inner join analysis.riskRegisters as riskregisterItem where analysis.id = :analysisid and riskregisterItem.id = ";
		query += ":riskregisterItemId";
		return ((Long) getSession().createQuery(query).setParameter("analysisid", analysisId).setParameter("riskregisterItemId", riskregisterItemId).uniqueResult()).intValue() > 0;
	}

	/*
	 * @Override public RiskRegisterItem getByScenario(Scenario scenario) throws Exception { return
	 * (RiskRegisterItem)
	 * getSession().createQuery("From RiskRegister where scenario = :scenario").setParameter
	 * ("scenario", scenario); }
	 */
	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORiskRegister#getAllFromAnalysisId(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskRegisterItem> getAllFromAnalysis(Integer analysisId) throws Exception {
		String query = "SELECT riskregister FROM Analysis as analysis INNER JOIN analysis.riskRegisters as riskregister WHERE analysis.id= :analysisID order by riskregister.position, riskregister.netEvaluation.importance desc, riskregister.expectedEvaluation.importance desc, riskregister.rawEvaluation.importance desc";
		return (List<RiskRegisterItem>) getSession().createQuery(query).setParameter("analysisID", analysisId).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORiskRegister#save(lu.itrust.business.TS.model.cssf.RiskRegisterItem)
	 */
	@Override
	public void save(RiskRegisterItem riskRegisterItem) throws Exception {
		getSession().save(riskRegisterItem);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORiskRegister#saveOrUpdate(lu.itrust.business.TS.model.cssf.RiskRegisterItem)
	 */
	@Override
	public void saveOrUpdate(RiskRegisterItem riskRegisterItem) throws Exception {
		getSession().saveOrUpdate(riskRegisterItem);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORiskRegister#delete(lu.itrust.business.TS.model.cssf.RiskRegisterItem)
	 */
	@Override
	public void delete(RiskRegisterItem riskRegisterItem) throws Exception {
		getSession().delete(riskRegisterItem);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteAllFromAnalysis(Integer analysisID) throws Exception {
		String query = "SELECT riskregisters FROM Analysis as analysis INNER JOIN analysis.riskRegisters as riskregisters WHERE analysis.id= :analysisID";
		List<RiskRegisterItem> riskregister = (List<RiskRegisterItem>) getSession().createQuery(query).setParameter("analysisID", analysisID).list();
		for(RiskRegisterItem riskItem : riskregister)
			getSession().delete(riskItem);
		
	}

	@Override
	public void delete(Integer id) {
		getSession().createQuery("Delete From RiskRegisterItem where id = :id").setInteger("id", id).uniqueResult();
		
	}

	@Override
	public RiskRegisterItem merge(RiskRegisterItem riskRegister) {
		return (RiskRegisterItem) getSession().merge(riskRegister);
		
	}
}