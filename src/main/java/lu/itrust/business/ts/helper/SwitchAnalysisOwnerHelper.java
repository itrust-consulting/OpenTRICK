/**
 * 
 */
package lu.itrust.business.ts.helper;

import java.security.Principal;

import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.service.ServiceAnalysis;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.usermanagement.User;

/**
 * @author eomar
 *
 */
public class SwitchAnalysisOwnerHelper {

	private DAOAnalysis daoAnalysis;

	private ServiceAnalysis serviceAnalysis;

	/**
	 * @param daoAnalysis
	 */
	public SwitchAnalysisOwnerHelper(DAOAnalysis daoAnalysis) {
		this.daoAnalysis = daoAnalysis;
	}

	/**
	 * @param serviceAnalysis
	 */
	public SwitchAnalysisOwnerHelper(ServiceAnalysis serviceAnalysis) {
		this.serviceAnalysis = serviceAnalysis;
	}

	protected void persist(Analysis analysis) throws Exception {
		if (daoAnalysis != null)
			daoAnalysis.saveOrUpdate(analysis);
		else
			serviceAnalysis.saveOrUpdate(analysis);
	}

	public void switchOwner(Principal principal, Analysis analysis, User owner) throws Exception {
		User previousOwner = analysis.getOwner();
		analysis.setOwner(owner);
		AnalysisRight right = null; // for log
		UserAnalysisRight userAnalysisRight = analysis.findRightsforUser(owner);
		if (userAnalysisRight == null)
			analysis.addUserRight(owner, AnalysisRight.ALL);
		else if ((right = userAnalysisRight.getRight()) != AnalysisRight.ALL)
			userAnalysisRight.setRight(AnalysisRight.ALL);
		boolean hasAccess;// for log
		if (!(hasAccess = owner.containsCustomer(analysis.getCustomer())))
			owner.addCustomer(analysis.getCustomer());
		persist(analysis);
		/**
		 * Log
		 */
		TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.analysis.switch.owner",
				String.format("Analysis: %s, version: %s, old: %s, new: %s", analysis.getIdentifier(), analysis.getVersion(), previousOwner.getLogin(), owner.getLogin()),
				principal.getName(), LogAction.SWITCH_OWNER, analysis.getIdentifier(), analysis.getVersion(), previousOwner.getLogin(), owner.getLogin());
		if (!(previousOwner == owner || previousOwner.getLogin().equals(principal.getName())) && owner.getLogin().equals(principal.getName())) {
			TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.claim.ownership.analysis",
					String.format("Claims ownership of Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()), principal.getName(), LogAction.AUTO_GRANT,
					analysis.getIdentifier(), analysis.getVersion());
		} else {
			if (right == null)
				TrickLogManager.persist(LogType.ANALYSIS, "log.give.analysis.access.right",
						String.format("Analysis: %s, version: %s, access: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(), AnalysisRight.ALL.toLower(),
								owner.getLogin()),
						principal.getName(), LogAction.GIVE_ACCESS, analysis.getIdentifier(), analysis.getVersion(), AnalysisRight.ALL.toLower(), owner.getLogin());
			else if (right != AnalysisRight.ALL)
				TrickLogManager.persist(LogType.ANALYSIS, "log.grant.analysis.access.right",
						String.format("Analysis: %s, version: %s, access: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(), AnalysisRight.ALL.toLower(),
								owner.getLogin()),
						principal.getName(), LogAction.GRANT_ACCESS, analysis.getIdentifier(), analysis.getVersion(), AnalysisRight.ALL.toLower(), owner.getLogin());
			if (!hasAccess)
				TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.give.access.to.customer",
						String.format("Customer: %s, target: %s", analysis.getCustomer().getOrganisation(), owner.getLogin()), principal.getName(), LogAction.GIVE_ACCESS,
						analysis.getCustomer().getOrganisation(), owner.getLogin());
		}

	}

}
