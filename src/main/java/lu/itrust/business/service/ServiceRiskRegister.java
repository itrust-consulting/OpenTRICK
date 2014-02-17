/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.cssf.RiskRegisterItem;

/**
 * @author oensuifudine
 *
 */
public interface ServiceRiskRegister {
	
	public RiskRegisterItem get(int id) throws Exception;

	public RiskRegisterItem getByScenario(Scenario scenario) throws Exception;

	public List<RiskRegisterItem> loadAllFromAnalysis(Integer analysisID) throws Exception;
	
	public void save(RiskRegisterItem riskRegisterItem) throws Exception;

	public void saveOrUpdate(RiskRegisterItem riskRegisterItem) throws Exception;

	public void remove(RiskRegisterItem riskRegisterItem) throws Exception;

}
