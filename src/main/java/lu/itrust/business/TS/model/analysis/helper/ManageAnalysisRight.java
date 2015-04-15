/**
 * 
 */
package lu.itrust.business.TS.model.analysis.helper;

import java.security.Principal;
import java.util.List;

import javax.transaction.Transactional;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.model.general.LogType;
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

	private DAOUser daoUser;

	public void switchAnalysisToReadOnly(String identifier, int idAnalysis) throws Exception {
		List<UserAnalysisRight> userAnalysisRights = daoUserAnalysisRight.getAllFromIdenfierExceptAnalysisIdAndRightNotRead(identifier, idAnalysis);
		for (UserAnalysisRight userAnalysisRight : userAnalysisRights) {
			userAnalysisRight.setRight(AnalysisRight.READ);
			daoUserAnalysisRight.saveOrUpdate(userAnalysisRight);
		}
	}

	public void updateAnalysisRights(Principal principal, Integer idAnalysis, JsonNode jsonNode) throws Exception {
		List<User> users = daoUser.getAll();
		Analysis analysis = daoAnalysis.get(idAnalysis);
		for (User user : users) {

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
					TrickLogManager.Persist(LogType.ANALYSIS, "info.remove.analysis.access.right", String.format("Analysis: %s, version: %s, %s removed %s access to %s",
							analysis.getIdentifier(), analysis.getVersion(), principal.getName(), uar.getRight().name().toLowerCase(), user.getLogin()), analysis.getIdentifier(),
							analysis.getVersion(), principal.getName(), uar.getRight().name().toLowerCase(), user.getLogin());
				} else {
					uar.setRight(AnalysisRight.valueOf(useraccess));
					daoUserAnalysisRight.saveOrUpdate(uar);
					TrickLogManager.Persist(LogType.ANALYSIS, "info.grante.analysis.access.right", String.format("Analysis: %s, version: %s, %s granted %s access to %s",
							analysis.getIdentifier(), analysis.getVersion(), principal.getName(), uar.getRight().name().toLowerCase(), user.getLogin()), analysis.getIdentifier(),
							analysis.getVersion(), principal.getName(), uar.getRight().name().toLowerCase(), user.getLogin());
				}
			} else {
				if (useraccess != -1) {
					if (!user.getCustomers().contains(analysis.getCustomer()))
						user.addCustomer(analysis.getCustomer());
					uar = analysis.addUserRight(user, AnalysisRight.valueOf(useraccess));
					daoUserAnalysisRight.save(uar);
					TrickLogManager.Persist(LogType.ANALYSIS, "info.give.analysis.access.right", String.format("Analysis: %s, version: %s, %s gave %s access to %s",
							analysis.getIdentifier(), analysis.getVersion(), principal.getName(), uar.getRight().name().toLowerCase(), user.getLogin()), analysis.getIdentifier(),
							analysis.getVersion(), principal.getName(), uar.getRight().name().toLowerCase(), user.getLogin());
				}

			}
		}

		daoAnalysis.saveOrUpdate(analysis);
	}

	@Autowired
	public void setDaoUserAnalysisRight(DAOUserAnalysisRight daoUserAnalysisRight) {
		this.daoUserAnalysisRight = daoUserAnalysisRight;
	}

	@Autowired
	public void setDaoAnalysis(DAOAnalysis daoAnalysis) {
		this.daoAnalysis = daoAnalysis;
	}

	@Autowired
	public void setDaoUser(DAOUser daoUser) {
		this.daoUser = daoUser;
	}
	
	

}
