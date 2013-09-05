/**
 * 
 */
package lu.itrust.business.dao.hbm;

import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.cssf.RiskRegisterItem;
import lu.itrust.business.dao.DAORiskRegister;

/**
 * @author oensuifudine
 * 
 */
public class DAORiskRegisterHBM extends DAOHibernate implements DAORiskRegister {

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORiskRegister#getRiskRegister(int)
	 */
	@Override
	public RiskRegisterItem getRiskRegister(int id) throws Exception {
		// TODO Auto-generated method stub
		return (RiskRegisterItem) getSession().get(RiskRegisterItem.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAORiskRegister#getRiskRegisterItem(lu.itrust.
	 * business.TS.Scenario)
	 */
	@Override
	public RiskRegisterItem getRiskRegisterItem(Scenario scenario)
			throws Exception {
		// TODO Auto-generated method stub
		return (RiskRegisterItem) getSession().createQuery(
				"From RiskRegisterItem where scenario = :scenario")
				.setParameter("scenario", scenario);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAORiskRegister#save(lu.itrust.business.TS.cssf
	 * .RiskRegisterItem)
	 */
	@Override
	public void save(RiskRegisterItem riskRegisterItem) throws Exception {
		getSession().save(riskRegisterItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAORiskRegister#saveOrUpdate(lu.itrust.business
	 * .TS.cssf.RiskRegisterItem)
	 */
	@Override
	public void saveOrUpdate(RiskRegisterItem riskRegisterItem)
			throws Exception {
		getSession().saveOrUpdate(riskRegisterItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAORiskRegister#remove(lu.itrust.business.TS.cssf
	 * .RiskRegisterItem)
	 */
	@Override
	public void remove(RiskRegisterItem riskRegisterItem) throws Exception {
		getSession().delete(riskRegisterItem);
	}

}
