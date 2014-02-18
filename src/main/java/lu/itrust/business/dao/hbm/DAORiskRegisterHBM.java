/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.cssf.RiskRegisterItem;
import lu.itrust.business.dao.DAORiskRegister;

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
	 * @param session
	 */
	public DAORiskRegisterHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAORiskRegister#get(int)
	 */
	@Override
	public RiskRegisterItem get(int id) throws Exception {
		return (RiskRegisterItem) getSession().get(RiskRegisterItem.class, id);
	}

	/**
	 * getByScenario: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAORiskRegister#getByScenario(lu.itrust.business.TS.Scenario)
	 */
	@Override
	public RiskRegisterItem getByScenario(Scenario scenario) throws Exception {
		return (RiskRegisterItem) getSession().createQuery("From RiskRegister where scenario = :scenario").setParameter("scenario", scenario);
	}

	/**
	 * loadAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskRegisterItem> loadAllFromAnalysis(Integer analysisID) throws Exception {
		return (List<RiskRegisterItem>) getSession().createQuery("SELECT riskregisters FROM Analysis as analysis INNER JOIN analysis.riskRegisters as riskregisters WHERE analysis.id= :analysisID").setParameter("analysisID", analysisID).list();
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAORiskRegister#save(lu.itrust.business.TS.cssf.RiskRegisterItem)
	 */
	@Override
	public void save(RiskRegisterItem riskRegisterItem) throws Exception {
		getSession().save(riskRegisterItem);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAORiskRegister#saveOrUpdate(lu.itrust.business.TS.cssf.RiskRegisterItem)
	 */
	@Override
	public void saveOrUpdate(RiskRegisterItem riskRegisterItem) throws Exception {
		getSession().saveOrUpdate(riskRegisterItem);
	}

	/**
	 * remove: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAORiskRegister#remove(lu.itrust.business.TS.cssf.RiskRegisterItem)
	 */
	@Override
	public void remove(RiskRegisterItem riskRegisterItem) throws Exception {
		getSession().delete(riskRegisterItem);
	}
}