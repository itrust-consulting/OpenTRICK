/**
 * 
 */
package lu.itrust.business.TS.data.analysis.helper;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.data.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.TS.usermanagement.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author eomar
 *
 */
@Component
@Transactional
public class ManageAnalysisRight {

	private DAOUserAnalysisRight daoUserAnalysisRight;

	private DAOAnalysis daoAnalysis;

	public void switchAnalysisToReadOnly(String identifier, int idAnalysis) throws Exception {
		List<UserAnalysisRight> userAnalysisRights = daoUserAnalysisRight.getAllFromIdenfierExceptAnalysisIdAndRightNotRead(identifier, idAnalysis);
		for (UserAnalysisRight userAnalysisRight : userAnalysisRights) {
			userAnalysisRight.setRight(AnalysisRight.READ);
			daoUserAnalysisRight.saveOrUpdate(userAnalysisRight);
		}
	}

	public Map<User, AnalysisRight> updateAnalysisRights(Principal principal, Analysis analysis, List<User> users, JsonNode jsonNode) throws Exception {
		Map<User, AnalysisRight> userrights = new LinkedHashMap<>();
		for (User user : users) {

			if (analysis.getRightsforUser(user) != null)
				userrights.put(user, analysis.getRightsforUser(user).getRight());
			else
				userrights.put(user, null);

			if (user.getLogin().equals(principal.getName()) && !analysis.getOwner().getLogin().equals(principal.getName()))
				continue;

			int useraccess = jsonNode.get("analysisRight_" + user.getId()).asInt();

			if (analysis.getOwner().equals(user) && !AnalysisRight.isValid(useraccess))
				continue;

			UserAnalysisRight uar = analysis.getRightsforUser(user);

			if (uar != null) {

				if (useraccess == -1) {
					analysis.getUserRights().remove(uar);
					daoUserAnalysisRight.delete(uar);
					userrights.put(user, null);
				} else {
					uar.setRight(AnalysisRight.valueOf(useraccess));
					daoUserAnalysisRight.saveOrUpdate(uar);
					userrights.put(user, uar.getRight());
				}
			} else {

				if (useraccess != -1) {

					if (!user.getCustomers().contains(analysis.getCustomer()))
						user.addCustomer(analysis.getCustomer());
					
					uar = analysis.addUserRight(user,  AnalysisRight.valueOf(useraccess));
					daoUserAnalysisRight.save(uar);
					userrights.put(user, uar.getRight());
				}

			}
		}

		daoAnalysis.saveOrUpdate(analysis);

		return userrights;
	}

	@Autowired
	public void setDaoUserAnalysisRight(DAOUserAnalysisRight daoUserAnalysisRight) {
		this.daoUserAnalysisRight = daoUserAnalysisRight;
	}

	@Autowired
	public void setDaoAnalysis(DAOAnalysis daoAnalysis) {
		this.daoAnalysis = daoAnalysis;
	}

}
