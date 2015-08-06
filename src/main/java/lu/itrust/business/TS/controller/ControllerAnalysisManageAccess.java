package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.ManageAnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.usermanagement.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

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
		model.addAttribute("currentUser", serviceUser.get(principal.getName()).getId());
		model.addAttribute("analysisRights", AnalysisRight.values());
		model.addAttribute("analysis", analysis);
		model.addAttribute("userrights", userrights);
		return "analyses/all/forms/manageUserAnalysisRights";
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
	@RequestMapping("/Update/{analysisID}")
	@PreAuthorize("@permissionEvaluator.userOrOwnerIsAuthorized(#analysisID, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).ALL)")
	public String updatemanageaccessrights(@PathVariable("analysisID") int analysisID, Principal principal, Model model, @RequestBody String value, Locale locale) throws Exception {

		try {
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
			model.addAttribute("errors", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return "analyses/all/forms/manageUserAnalysisRights";
		}
	}
}
