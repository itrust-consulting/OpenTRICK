/**
 * 
 */
package lu.itrust.business.TS.model.analysis.helper;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.usermanagement.User;

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

	public void updateAnalysisRights(Principal principal, Integer idAnalysis, JsonNode jsonNode) throws Exception {
		List<User> users = daoUser.getAll();
		Analysis analysis = daoAnalysis.get(idAnalysis);
		for (User user : users) {
			if (user.getLogin().equals(principal.getName()) && !analysis.getOwner().getLogin().equals(principal.getName()))
				continue;

			JsonNode rightNode = jsonNode.get("analysisRight_" + user.getId());

			if (rightNode == null)
				continue;

			int useraccess = rightNode.asInt();

			if (analysis.getOwner().equals(user) && !AnalysisRight.isValid(useraccess))
				continue;

			UserAnalysisRight uar = analysis.getRightsforUser(user);
			if (uar != null) {
				if (useraccess == -1) {
					analysis.getUserRights().remove(uar);
					daoUserAnalysisRight.delete(uar);
					/**
					 * Log
					 */
					TrickLogManager.Persist(LogType.ANALYSIS, "log.remove.analysis.access.right",
							String.format("Analysis: %s, version: %s, access: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(), uar.getRight().toLower(),
									user.getLogin()),
							principal.getName(), LogAction.REMOVE_ACCESS, analysis.getIdentifier(), analysis.getVersion(), uar.getRight().toLower(), user.getLogin());
				} else {
					AnalysisRight analysisRight = AnalysisRight.valueOf(useraccess);
					if (analysisRight != uar.getRight()) {
						uar.setRight(analysisRight);
						daoUserAnalysisRight.saveOrUpdate(uar);
						/**
						 * Log
						 */
						if (uar.getUser().getLogin().equals(principal.getName()))
							TrickLogManager.Persist(LogType.ANALYSIS, "log.auto.grant.analysis.access.right",
									String.format("Analysis: %s, version: %s, access: %s", analysis.getIdentifier(), analysis.getVersion(), uar.getRight().toLower()),
									principal.getName(), LogAction.AUTO_GRANT, analysis.getIdentifier(), analysis.getVersion(), uar.getRight().toLower());
						else
							TrickLogManager.Persist(LogType.ANALYSIS, "log.grant.analysis.access.right",
									String.format("Analysis: %s, version: %s, access: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(), uar.getRight().toLower(),
											user.getLogin()),
									principal.getName(), LogAction.GRANT_ACCESS, analysis.getIdentifier(), analysis.getVersion(), uar.getRight().toLower(), user.getLogin());
					}
				}
			} else {
				if (useraccess != -1) {
					if (!user.getCustomers().contains(analysis.getCustomer()))
						user.addCustomer(analysis.getCustomer());
					uar = analysis.addUserRight(user, AnalysisRight.valueOf(useraccess));
					daoUserAnalysisRight.save(uar);
					/**
					 * Log
					 */

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

	public void updateAnalysisRights(Principal principal, AnalysisRightForm rightsForm) {
		Analysis analysis = daoAnalysis.get(rightsForm.getAnalysisId());
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		rightsForm.getUserRights().forEach((userId, rightForm) -> {
			User user = daoUser.get(userId);
			if (user != null) {
				if (rightForm.getNewRight() == null)
					removeRight(principal, analysis, user);
				else {
					UserAnalysisRight userRight = analysis.getRightsforUser(user);
					if (userRight == null)
						giveAccess(principal, analysis, rightForm, user);
					else
						grantAccess(principal, analysis, rightForm, user, userRight);
				}
			}
		});
		daoAnalysis.saveOrUpdate(analysis);
	}

	private void grantAccess(Principal principal, Analysis analysis, RightForm rightForm, User user, UserAnalysisRight userRight) {
		userRight.setRight(rightForm.getNewRight());
		if (user.getLogin().equals(principal.getName()))
			TrickLogManager.Persist(LogType.ANALYSIS, "log.auto.grant.analysis.access.right",
					String.format("Analysis: %s, version: %s, access: %s", analysis.getIdentifier(), analysis.getVersion(), userRight.getRight().toLower()), principal.getName(),
					LogAction.AUTO_GRANT, analysis.getIdentifier(), analysis.getVersion(), userRight.getRight().toLower());
		else
			TrickLogManager.Persist(LogType.ANALYSIS, "log.grant.analysis.access.right",
					String.format("Analysis: %s, version: %s, access: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(), userRight.getRight().toLower(),
							user.getLogin()),
					principal.getName(), LogAction.GRANT_ACCESS, analysis.getIdentifier(), analysis.getVersion(), userRight.getRight().toLower(), user.getLogin());
	}

	private void giveAccess(Principal principal, Analysis analysis, RightForm rightForm, User user) {
		analysis.addUserRight(user, rightForm.getNewRight());
		if (!user.containsCustomer(analysis.getCustomer())) {
			user.addCustomer(analysis.getCustomer());
			TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.give.access.to.customer",
					String.format("Customer: %s, target: %s", analysis.getCustomer().getOrganisation(), user.getLogin()), principal.getName(), LogAction.GIVE_ACCESS,
					analysis.getCustomer().getOrganisation(), user.getLogin());
		}
		TrickLogManager.Persist(LogType.ANALYSIS, "log.give.analysis.access.right",
				String.format("Analysis: %s, version: %s, access: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(), rightForm.getNewRight().name().toLowerCase(),
						user.getLogin()),
				principal.getName(), LogAction.GIVE_ACCESS, analysis.getIdentifier(), analysis.getVersion(), rightForm.getNewRight().name().toLowerCase(), user.getLogin());
	}

	private void removeRight(Principal principal, Analysis analysis, User user) {
		if (analysis.getOwner().equals(user))
			return;
		UserAnalysisRight userRight = analysis.removeRights(user);
		if (userRight == null)
			return;
		daoUserAnalysisRight.delete(userRight);
		TrickLogManager.Persist(LogType.ANALYSIS, "log.remove.analysis.access.right",
				String.format("Analysis: %s, version: %s, access: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(), userRight.getRight().toLower(),
						user.getLogin()),
				principal.getName(), LogAction.REMOVE_ACCESS, analysis.getIdentifier(), analysis.getVersion(), userRight.getRight().toLower(), user.getLogin());

	}

}
