/**
 * 
 */
package lu.itrust.business.service;

import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.cssf.RiskRegisterItem;

/**
 * @author oensuifudine
 *
 */
public interface ServiceRiskRegister {
	
	public RiskRegisterItem getRiskRegister(int id) throws Exception;

	public RiskRegisterItem getRiskRegisterItem(Scenario scenario) throws Exception;

	public void save(RiskRegisterItem riskRegisterItem) throws Exception;

	public void saveOrUpdate(RiskRegisterItem riskRegisterItem) throws Exception;

	public void remove(RiskRegisterItem riskRegisterItem) throws Exception;

}
