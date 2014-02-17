/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.cssf.RiskRegisterItem;
import lu.itrust.business.dao.DAORiskRegister;
import lu.itrust.business.service.ServiceRiskRegister;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author oensuifudine
 * 
 */
@Transactional
@Service
public class ServiceRiskRegisterImpl implements ServiceRiskRegister {

	@Autowired
	private DAORiskRegister daoRiskRegister;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceRiskRegister#getRiskRegister(int)
	 */
	@Override
	public RiskRegisterItem get(int id) throws Exception {
		// TODO Auto-generated method stub
		return daoRiskRegister.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceRiskRegister#getRiskRegisterItem(lu.itrust.business.TS.
	 * Scenario)
	 */
	@Override
	public RiskRegisterItem getByScenario(Scenario scenario) throws Exception {
		// TODO Auto-generated method stub
		return daoRiskRegister.getByScenario(scenario);
	}

	public List<RiskRegisterItem> loadAllFromAnalysis(Integer analysisID) throws Exception {
		return daoRiskRegister.loadAllFromAnalysis(analysisID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceRiskRegister#save(lu.itrust.business.TS.cssf.RiskRegisterItem
	 * )
	 */
	@Transactional
	@Override
	public void save(RiskRegisterItem riskRegisterItem) throws Exception {
		daoRiskRegister.save(riskRegisterItem);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceRiskRegister#saveOrUpdate(lu.itrust.business.TS.cssf.
	 * RiskRegisterItem)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(RiskRegisterItem riskRegisterItem) throws Exception {
		daoRiskRegister.saveOrUpdate(riskRegisterItem);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceRiskRegister#remove(lu.itrust.business.TS.cssf.RiskRegisterItem
	 * )
	 */
	@Transactional
	@Override
	public void remove(RiskRegisterItem riskRegisterItem) throws Exception {
		daoRiskRegister.remove(riskRegisterItem);

	}

	public void setDaoRiskRegister(DAORiskRegister daoRiskRegister) {
		this.daoRiskRegister = daoRiskRegister;
	}

}
