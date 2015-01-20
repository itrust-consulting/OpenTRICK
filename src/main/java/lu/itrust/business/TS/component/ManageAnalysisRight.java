/**
 * 
 */
package lu.itrust.business.TS.component;

import java.util.List;

import javax.transaction.Transactional;

import lu.itrust.business.TS.data.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.data.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOUserAnalysisRight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author eomar
 *
 */
@Component
@Transactional
public class ManageAnalysisRight {
	
	@Autowired
	private DAOUserAnalysisRight daoUserAnalysisRight;
	
	@Autowired
	private DAOAnalysis daoAnalysis;
	
	public void switchAnalysisToReadOnly(String identifier, int idAnalysis) throws Exception {
		List<UserAnalysisRight> userAnalysisRights = daoUserAnalysisRight.getAllFromIdenfierExceptAnalysisIdAndRightNotRead(identifier,idAnalysis);
		for (UserAnalysisRight userAnalysisRight : userAnalysisRights) {
			userAnalysisRight.setRight(AnalysisRight.READ);
			daoUserAnalysisRight.saveOrUpdate(userAnalysisRight);
		}
	}
}
