package lu.itrust.business.dao;

import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.cssf.RiskRegisterItem;

/** 
 * DAORiskRegister.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.�.rl. :
 * @version 
 * @since 16 janv. 2013
 */
public interface DAORiskRegister {
	
	public RiskRegisterItem getRiskRegister(int id) throws Exception;

	public RiskRegisterItem getRiskRegisterItem(Scenario scenario) throws Exception;

	public void save(RiskRegisterItem riskRegisterItem) throws Exception;

	public void saveOrUpdate(RiskRegisterItem riskRegisterItem) throws Exception;

	public void remove(RiskRegisterItem riskRegisterItem) throws Exception;
}
