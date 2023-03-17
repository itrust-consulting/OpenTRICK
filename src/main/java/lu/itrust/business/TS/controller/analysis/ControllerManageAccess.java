package lu.itrust.business.TS.controller.analysis;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisShareInvitation;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.form.AnalysisRightForm;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.ManageAnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.usermanagement.User;

/**
 * ControllerManageAccess.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since Oct 13, 2014
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/Analysis/ManageAccess")
public class ControllerManageAccess {

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAnalysisShareInvitation serviceAnalysisShareInvitation;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ManageAnalysisRight manageAnalysisRight;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@GetMapping("/{token}/Accept")
	public String acceptInvitation(@PathVariable String token, Principal principal, RedirectAttributes attributes, Locale locale) {
		try {
			if (serviceAnalysisShareInvitation.exists(token)) {
				manageAnalysisRight.acceptInvitation(principal, token);
				attributes.addFlashAttribute("success", messageSource.getMessage("success.accept.invitation", null, "Access has been successfully granted", locale));
			} else
				attributes.addFlashAttribute("error", messageSource.getMessage("error.accept.invitation.token.not.found", null, "Token has already been used or cancelled", locale));
		} catch (TrickException e) {
			attributes.addFlashAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			attributes.addFlashAttribute("error", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}
		return "redirect:/Analysis/All";
	}

	/**
	 * manageaccessrights: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @param principal
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{analysisID}")
	@PreAuthorize("@permissionEvaluator.hasManagementPermission(#analysisID, #principal)")
	public String manageaccessrights(@PathVariable("analysisID") int analysisID, Principal principal, Model model, HttpSession session) throws Exception {
		final Analysis analysis = serviceAnalysis.get(analysisID);
		final Map<User, AnalysisRight> userRights = new LinkedHashMap<>();
		analysis.getUserRights().stream().sorted(userRightComparator() ).forEach(userRight -> userRights.put(userRight.getUser(), userRight.getRight()));
		serviceCustomer.findUserByCustomer(analysis.getCustomer()).stream().filter(user -> !userRights.containsKey(user)).forEach(user -> userRights.put(user, null));
		model.addAttribute("isAdmin", false);
		model.addAttribute("analysis", analysis);
		model.addAttribute("userrights", userRights);
		model.addAttribute("invitations", serviceAnalysisShareInvitation.findByAnalysisId(analysisID));
		model.addAttribute("ownerId", analysis.getOwner().getId());
		model.addAttribute("myId", serviceUser.get(principal.getName()).getId());
		return "jsp/analyses/all/forms/rights";
	}

	/**
	 * updatemanageaccessrights: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @param principal
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.hasManagementPermission(#rightsForm.analysisId, #principal)")
	public @ResponseBody String updatemanageaccessrights(@RequestBody AnalysisRightForm rightsForm, Principal principal, Locale locale) throws Exception {
		try {
			Customer customer = serviceCustomer.findByAnalysisId(rightsForm.getAnalysisId());
			rightsForm.getUserRights().keySet().removeIf(idUser -> !serviceCustomer.hasAccess(idUser, customer));
			manageAnalysisRight.updateAnalysisRights(principal, rightsForm);
			return JsonMessage.Success(messageSource.getMessage("success.update.analysis.right", null, "Analysis access rights were successfully updated!", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			if (e instanceof TrickException)
				return JsonMessage.Error(messageSource.getMessage(((TrickException) e).getCode(), ((TrickException) e).getParameters(), e.getMessage(), locale));
			return JsonMessage.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	private Comparator<? super UserAnalysisRight> userRightComparator() {
		return (u1, u2) ->{
			int result = NaturalOrderComparator.compareTo(u1.getUser().getFirstName(), u2.getUser().getFirstName());
			if(result == 0) {
				result = NaturalOrderComparator.compareTo(u1.getUser().getLastName(), u2.getUser().getLastName());
				if(result == 0)
					result = NaturalOrderComparator.compareTo(u1.getUser().getEmail(), u2.getUser().getEmail());
			}
			return result;
		};
	}

}
