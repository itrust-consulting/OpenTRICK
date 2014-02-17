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
 * @author oensuifudine
 * 
 */
@Repository
public class DAORiskRegisterHBM extends DAOHibernate implements DAORiskRegister {

	/**
	 * 
	 */
	public DAORiskRegisterHBM() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sessionFactory
	 */
	public DAORiskRegisterHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORiskRegister#getRiskRegister(int)
	 */
	@Override
	public RiskRegisterItem get(int id) throws Exception {
		// TODO Auto-generated method stub
		return (RiskRegisterItem) getSession().get(RiskRegisterItem.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORiskRegister#getRiskRegisterItem(lu.itrust.
	 * business.TS.Scenario)
	 */
	@Override
	public RiskRegisterItem getByScenario(Scenario scenario) throws Exception {
		// TODO Auto-generated method stub
		return (RiskRegisterItem) getSession().createQuery("From RiskRegisterItem where scenario = :scenario").setParameter("scenario", scenario);
	}

	/**
	 * loadAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 */
	// TODO
	@Override
	public List<RiskRegisterItem> loadAllFromAnalysis(Integer analysisID) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORiskRegister#save(lu.itrust.business.TS.cssf
	 * .RiskRegisterItem)
	 */
	@Override
	public void save(RiskRegisterItem riskRegisterItem) throws Exception {
		getSession().save(riskRegisterItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORiskRegister#saveOrUpdate(lu.itrust.business
	 * .TS.cssf.RiskRegisterItem)
	 */
	@Override
	public void saveOrUpdate(RiskRegisterItem riskRegisterItem) throws Exception {
		getSession().saveOrUpdate(riskRegisterItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORiskRegister#remove(lu.itrust.business.TS.cssf
	 * .RiskRegisterItem)
	 */
	@Override
	public void remove(RiskRegisterItem riskRegisterItem) throws Exception {
		getSession().delete(riskRegisterItem);
	}

}
