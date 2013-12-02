/**
 * 
 */
package lu.itrust.business.component;

import java.util.List;

import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.dao.DAOAssessment;
import lu.itrust.business.dao.DAOAsset;
import lu.itrust.business.dao.DAOScenario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author eom
 *
 */
@Component
public class CustomDelete {
	
	@Autowired
	private DAOAssessment daoAssessment;
	
	@Autowired
	private DAOAsset daoAsset;
	
	@Autowired
	private DAOScenario daoScenario;
	
	@Transactional
	public void deleteAsset(Asset asset) throws Exception {
		List<Assessment> assessments = daoAssessment.loadAllFromAsset(asset);
		for (Assessment assessment : assessments)
			daoAssessment.remove(assessment);
		daoAsset.delete(asset);
	}
	
	@Transactional
	public void deleteScenario(Scenario scenario) throws Exception{
		List<Assessment> assessments = daoAssessment.loadAllFromScenario(scenario);
		for (Assessment assessment : assessments)
			daoAssessment.remove(assessment);
		daoScenario.remove(scenario);
	}

}
