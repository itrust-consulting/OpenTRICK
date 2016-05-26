package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.AnalysisRightForm;
import lu.itrust.business.TS.model.analysis.helper.ManageAnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
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
public class ControllerAnalysisManageAccess {

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ManageAnalysisRight manageAnalysisRight;

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
	@PreAuthorize("@permissionEvaluator.userOrOwnerIsAuthorized(#analysisID, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).ALL)")
	public String manageaccessrights(@PathVariable("analysisID") int analysisID, Principal principal, Model model, HttpSession session) throws Exception {
		Map<User, AnalysisRight> userrights = new LinkedHashMap<>();
		Analysis analysis = serviceAnalysis.get(analysisID);
		List<UserAnalysisRight> uars = analysis.getUserRights();
		serviceUser.getAll().forEach(user-> userrights.put(user, null));
		uars.forEach(uar-> userrights.put(uar.getUser(), uar.getRight()));
		model.addAttribute("myId", serviceUser.get(principal.getName()).getId());
		model.addAttribute("analysis", analysis);
		model.addAttribute("userrights", userrights);
		return "analyses/all/forms/rights";
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
	@RequestMapping(value="/Update/{analysisID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userOrOwnerIsAuthorized(#analysisID, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).ALL)")
	public @ResponseBody String updatemanageaccessrights(@PathVariable("analysisID") int analysisID, Principal principal, Model model, @RequestBody AnalysisRightForm rightsForm, Locale locale) throws Exception {
		
		System.out.println(rightsForm);
		
		return JsonMessage.Success(messageSource.getMessage("label.analysis.manage.users.success", null, "Analysis access rights, EXPECT your own, were successfully updated!", locale));
		/*try {
			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(value);
			int currentUser = jsonNode.get("userselect").asInt();
			model.addAttribute("currentUser", currentUser);
			manageAnalysisRight.updateAnalysisRights(principal, analysisID, jsonNode);
			Analysis analysis = serviceAnalysis.get(analysisID);
			Map<User, AnalysisRight> userrights = new LinkedHashMap<User, AnalysisRight>();
			analysis.getUserRights().forEach(useraccess -> userrights.put(useraccess.getUser(), useraccess.getRight()));
			serviceUser.getAllOthers(userrights.keySet()).forEach(user -> userrights.put(user, null));
			model.addAttribute("success",
					messageSource.getMessage("label.analysis.manage.users.success", null, "Analysis access rights, EXPECT your own, were successfully updated!", locale));
			model.addAttribute("analysisRights", AnalysisRight.values());
			model.addAttribute("analysis", analysis);
			model.addAttribute("userrights", userrights);
			return "analyses/all/forms/manageUserAnalysisRights";
		} catch (Exception e) {
			// return errors
			model.addAttribute("errors",  messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
			return "analyses/all/forms/manageUserAnalysisRights";
		}*/
	}
}
