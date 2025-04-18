/**
 * 
 */
package lu.itrust.business.ts.model.analysis.helper;

import static lu.itrust.business.ts.constants.Constant.ANONYMOUS;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOAnalysisShareInvitation;
import lu.itrust.business.ts.database.dao.DAOUser;
import lu.itrust.business.ts.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.ts.database.service.ServiceEmailSender;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.form.AnalysisRightForm;
import lu.itrust.business.ts.form.RightForm;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisShareInvitation;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.usermanagement.User;


/**
 * This class is responsible for managing analysis rights.
 * It provides methods to update analysis rights for users and handle analysis share invitations.
 * The class is annotated with @Component to be eligible for component scanning and dependency injection.
 * It is also annotated with @Transactional to enable transaction management for the methods in this class.
 */
@Component
@Transactional
public class ManageAnalysisRight {

	@Autowired
	private DAOUserAnalysisRight daoUserAnalysisRight;

	@Autowired
	private DAOAnalysis daoAnalysis;

	@Autowired
	private DAOUser daoUser;

	@Autowired
	private DAOAnalysisShareInvitation daoAnalysisShareInviatation;

	@Autowired
	private ServiceEmailSender serviceEmailSender;

	/**
	 * Updates the analysis rights for a given analysis.
	 *
	 * @param principal The principal object representing the currently authenticated user.
	 * @param idAnalysis The ID of the analysis to update the rights for.
	 * @param jsonNode The JSON node containing the updated rights for each user.
	 * @throws Exception If an error occurs during the update process.
	 */
	public void updateAnalysisRights(Principal principal, Integer idAnalysis, JsonNode jsonNode) throws Exception {

		final List<User> users = daoUser.getAll();
		final Analysis analysis = daoAnalysis.get(idAnalysis);

		for (User user : users) {
			if (user.getLogin().equals(principal.getName()) && !analysis.getOwner().getLogin().equals(principal.getName()))
				continue;

			JsonNode rightNode = jsonNode.get("analysisRight_" + user.getId());

			if (rightNode == null)
				continue;

			int useraccess = rightNode.asInt();

			if (analysis.getOwner().equals(user) && !AnalysisRight.isValid(useraccess))
				continue;

			UserAnalysisRight uar = analysis.findRightsforUser(user);
			if (uar != null) {
				if (useraccess == -1) {
					analysis.getUserRights().remove(uar);
					daoUserAnalysisRight.delete(uar);
					/**
					 * Log
					 */
					TrickLogManager.persist(LogType.ANALYSIS, "log.remove.analysis.access.right",
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
							TrickLogManager.persist(LogType.ANALYSIS, "log.auto.grant.analysis.access.right",
									String.format("Analysis: %s, version: %s, access: %s", analysis.getIdentifier(), analysis.getVersion(), uar.getRight().toLower()),
									principal.getName(), LogAction.AUTO_GRANT, analysis.getIdentifier(), analysis.getVersion(), uar.getRight().toLower());
						else
							TrickLogManager.persist(LogType.ANALYSIS, "log.grant.analysis.access.right",
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

	/**
	 * Updates the analysis rights for the specified principal and analysis right form.
	 *
	 * @param principal   the principal representing the user performing the update
	 * @param rightsForm  the analysis right form containing the updated rights
	 * @throws TrickException if the analysis cannot be found
	 */
	public void updateAnalysisRights(Principal principal, AnalysisRightForm rightsForm) {

		final Analysis analysis = daoAnalysis.get(rightsForm.getAnalysisId());

		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");

		rightsForm.getUserRights().forEach((userId, rightForm) -> {
			User user = daoUser.get(userId);
			if (user == null)
				return;
			if (rightForm.getNewRight() == null)
				removeRight(principal, analysis, user);
			else {
				UserAnalysisRight userRight = analysis.findRightsforUser(user);
				if (userRight == null)
					giveAccess(principal.getName(), analysis, rightForm, user);
				else
					grantAccess(principal, analysis, rightForm, user, userRight);
			}
		});

		final User host = daoUser.get(principal.getName());

		rightsForm.getInvitations().forEach((email, rightForm) -> {
			AnalysisShareInvitation invitation = daoAnalysisShareInviatation.findByEmailAndAnalysisId(email, rightsForm.getAnalysisId());
			if (invitation == null) {
				if (rightForm.getNewRight() != null)
					sendInvitation(email, analysis, host, rightForm.getNewRight());
			} else if (rightForm.getNewRight() == null)
				cancelInvitation(principal, invitation);
			else
				grantInvitationAccess(principal, invitation, rightForm.getNewRight());

		});

		daoAnalysis.saveOrUpdate(analysis);
	}

	/**
	 * Grants the specified analysis access right to the given invitation.
	 *
	 * @param principal The principal representing the user granting the access right.
	 * @param invitation The analysis share invitation to grant access to.
	 * @param right The analysis access right to grant.
	 */
	private void grantInvitationAccess(Principal principal, AnalysisShareInvitation invitation, AnalysisRight right) {
		final Analysis analysis = invitation.getAnalysis();

		invitation.setRight(right);
		daoAnalysisShareInviatation.saveOrUpdate(invitation);

		TrickLogManager.persist(LogType.ANALYSIS, "log.grant.analysis.access.right",
				String.format("Analysis: %s, version: %s, access: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(), right.toLower(), invitation.getEmail()),
				principal.getName(), LogAction.GRANT_ACCESS, analysis.getIdentifier(), analysis.getVersion(), right.toLower(), invitation.getEmail());
	}

	/**
	 * Sends an invitation to the specified email address for accessing the given analysis.
	 *
	 * @param email The email address of the recipient.
	 * @param analysis The analysis to be shared.
	 * @param host The user who is sharing the analysis.
	 * @param right The access rights for the recipient.
	 */
	private void sendInvitation(String email, Analysis analysis, User host, AnalysisRight right) {
		final SecureRandom random = new SecureRandom();
		final String token = Sha512DigestUtils
				.shaHex(UUID.randomUUID().toString() + "--" + System.nanoTime() + ":" + email + "-*/" + host.getEmail() + "@=" + analysis.getIdentifier() + random.nextLong());
		final AnalysisShareInvitation invitation = new AnalysisShareInvitation(token, analysis, host, email, right);
		daoAnalysisShareInviatation.saveOrUpdate(invitation);
		serviceEmailSender.send(invitation);
		TrickLogManager.persist(LogType.ANALYSIS, "log.send.share.analysis.access",
				String.format("Analysis: %s, version: %s, access: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(), right.toLower(), email), host.getLogin(),
				LogAction.ACCESS_REQUEST, analysis.getIdentifier(), analysis.getVersion(), right.toLower(), email);
	}

	/**
	 * Cancels the invitation with the specified token.
	 *
	 * @param principal the principal object representing the user
	 * @param token the token of the invitation to be canceled
	 */
	public void cancelInvitation(Principal principal, String token) {
		final AnalysisShareInvitation invitation = daoAnalysisShareInviatation.findByToken(token);
		if (invitation == null)
			return;
		cancelInvitation(principal, invitation);
	}

	/**
	 * Cancels the invitation for sharing analysis.
	 *
	 * @param principal The principal object representing the authenticated user.
	 * @param invitation The AnalysisShareInvitation object representing the invitation to be canceled.
	 */
	private void cancelInvitation(Principal principal, AnalysisShareInvitation invitation) {
		final String host = invitation.getHost().getLogin(), identifier = invitation.getAnalysis().getIdentifier(), version = invitation.getAnalysis().getVersion();
		daoAnalysisShareInviatation.delete(invitation);

		if (principal == null)
			TrickLogManager.persist(LogType.ANALYSIS, "log.reject.share.analysis.access",
					String.format("Analysis: %s, version: %s, Guest: %s, Host: %s", identifier, version, invitation.getEmail(), host), ANONYMOUS, LogAction.REJECT_ACCESS_REQUEST,
					identifier, version, invitation.getEmail(), host);
		else
			TrickLogManager.persist(LogType.ANALYSIS, "log.cancel.share.analysis.access",
					String.format("Analysis: %s, version: %s, Guest: %s, Host: %s", identifier, version, invitation.getEmail(), host), principal.getName(),
					LogAction.CANCEL_ACCESS_REQUEST, identifier, version, invitation.getEmail(), host);
	}

	/**
	 * Grants access to an analysis for a specific user.
	 *
	 * @param principal The principal object representing the current user.
	 * @param analysis The analysis object to grant access to.
	 * @param rightForm The form containing the new access right.
	 * @param user The user to grant access to.
	 * @param userRight The user's analysis right object.
	 */
	private void grantAccess(Principal principal, Analysis analysis, RightForm rightForm, User user, UserAnalysisRight userRight) {
		userRight.setRight(rightForm.getNewRight());
		if (user.getLogin().equals(principal.getName()))
			TrickLogManager.persist(LogType.ANALYSIS, "log.auto.grant.analysis.access.right",
					String.format("Analysis: %s, version: %s, access: %s", analysis.getIdentifier(), analysis.getVersion(), userRight.getRight().toLower()), principal.getName(),
					LogAction.AUTO_GRANT, analysis.getIdentifier(), analysis.getVersion(), userRight.getRight().toLower());
		else
			TrickLogManager.persist(LogType.ANALYSIS, "log.grant.analysis.access.right",
					String.format("Analysis: %s, version: %s, access: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(), userRight.getRight().toLower(),
							user.getLogin()),
					principal.getName(), LogAction.GRANT_ACCESS, analysis.getIdentifier(), analysis.getVersion(), userRight.getRight().toLower(), user.getLogin());
	}

	/**
	 * Accepts an invitation to access an analysis.
	 * 
	 * @param principal The principal object representing the authenticated user.
	 * @param token The token associated with the invitation.
	 * @throws TrickException If there is an error accepting the invitation.
	 */
	public void acceptInvitation(Principal principal, String token) {
		final User user = daoUser.get(principal.getName());
		final AnalysisShareInvitation invitation = daoAnalysisShareInviatation.findByToken(token);
		if (user == null || invitation == null)
			return;
		final Analysis analysis = invitation.getAnalysis();
		if (!user.isEmailValidated()) {

			TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.share.analysis.access.not.validated.mail",
					String.format("Cause: Invalidated e-mail, Analysis: %s, version: %s, access: %s, Guest: %s", analysis.getIdentifier(), analysis.getVersion(),
							invitation.getRight().toLower(), invitation.getEmail()),
					principal.getName(), LogAction.DENY_ACCESS, analysis.getIdentifier(), analysis.getVersion(), invitation.getRight().toLower(), invitation.getEmail());

			throw new TrickException("error.accpet.invitation.email.not.validate", "Please validate your address mail and try again: Account -> My Profile -> Verify email");
		}

		if (!user.getEmail().equalsIgnoreCase(invitation.getEmail())) {

			TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.share.analysis.access.bad.mail",
					String.format("Cause: Bad e-mail, Analysis: %s, version: %s, access: %s, Guest: %s, Host: %s", analysis.getIdentifier(), analysis.getVersion(),
							invitation.getRight().toLower(), invitation.getEmail(), user.getEmail()),
					principal.getName(), LogAction.DENY_ACCESS, analysis.getIdentifier(), analysis.getVersion(), invitation.getRight().toLower(), invitation.getEmail(),
					user.getEmail());
			throw new TrickException("error.accpet.invitation.bad.email", "Access denied: your email address does not match that of the guest");
		}

		giveAccess(null, invitation.getAnalysis(), new RightForm(null, invitation.getRight()), user);

		daoAnalysisShareInviatation.delete(invitation);

		TrickLogManager.persist(LogType.ANALYSIS, "log.accept.share.analysis.access",
				String.format("Analysis: %s, version: %s, access: %s, Guest: %s, Host: %s", analysis.getIdentifier(), analysis.getVersion(), invitation.getRight().toLower(),
						invitation.getEmail(), invitation.getHost().getLogin()),
				principal.getName(), LogAction.ACCEPT_ACCESS_REQUEST, analysis.getIdentifier(), analysis.getVersion(), invitation.getRight().toLower(), invitation.getEmail(),
				invitation.getHost().getLogin());
	}

	/**
	 * Gives access to a user for a specific analysis.
	 *
	 * @param username   The username of the user performing the action.
	 * @param analysis   The analysis to give access to.
	 * @param rightForm  The form containing the new access right.
	 * @param user       The user to give access to.
	 */
	private void giveAccess(String username, Analysis analysis, RightForm rightForm, User user) {
		analysis.addUserRight(user, rightForm.getNewRight());
		if (!user.containsCustomer(analysis.getCustomer())) {
			user.addCustomer(analysis.getCustomer());
			if (username != null) {
				TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.give.access.to.customer",
						String.format("Customer: %s, target: %s", analysis.getCustomer().getOrganisation(), user.getLogin()), username, LogAction.GIVE_ACCESS,
						analysis.getCustomer().getOrganisation(), user.getLogin());
			}
		}

		if (username != null) {
			TrickLogManager.persist(LogType.ANALYSIS, "log.give.analysis.access.right",
					String.format("Analysis: %s, version: %s, access: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(),
							rightForm.getNewRight().name().toLowerCase(), user.getLogin()),
					username, LogAction.GIVE_ACCESS, analysis.getIdentifier(), analysis.getVersion(), rightForm.getNewRight().name().toLowerCase(), user.getLogin());
		}
	}

	/**
	 * Removes the access right for a user from the given analysis.
	 *
	 * @param principal The principal object representing the current user.
	 * @param analysis The analysis from which the access right should be removed.
	 * @param user The user for whom the access right should be removed.
	 */
	private void removeRight(Principal principal, Analysis analysis, User user) {
		if (analysis.getOwner().equals(user))
			return;
		UserAnalysisRight userRight = analysis.removeRights(user);
		if (userRight == null)
			return;
		daoUserAnalysisRight.delete(userRight);
		TrickLogManager.persist(LogType.ANALYSIS, "log.remove.analysis.access.right",
				String.format("Analysis: %s, version: %s, access: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(), userRight.getRight().toLower(),
						user.getLogin()),
				principal.getName(), LogAction.REMOVE_ACCESS, analysis.getIdentifier(), analysis.getVersion(), userRight.getRight().toLower(), user.getLogin());
	}

}
