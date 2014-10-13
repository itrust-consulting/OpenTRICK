package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.UserAnalysisRight;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.hbm.DAOHibernate;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.service.ServiceUserAnalysisRight;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisID, #principal, T(lu.itrust.business.TS.AnalysisRight).ALL)")
	public String manageaccessrights(@PathVariable("analysisID") int analysisID, Principal principal, Model model, HttpSession session) throws Exception {

		Map<User, AnalysisRight> userrights = new LinkedHashMap<>();

		Analysis analysis = serviceAnalysis.get(analysisID);

		List<UserAnalysisRight> uars = analysis.getUserRights();

		for (User user : serviceUser.getAll())
			userrights.put(DAOHibernate.Initialise(user), null);

		for (UserAnalysisRight uar : uars)
			userrights.put(DAOHibernate.Initialise(uar.getUser()), DAOHibernate.Initialise(uar.getRight()));

		model.addAttribute("currentUser", (DAOHibernate.Initialise(serviceUser.get(principal.getName())).getId()));
		model.addAttribute("analysisRights", AnalysisRight.values());
		model.addAttribute("analysis", analysis);
		model.addAttribute("userrights", userrights);
		return "analysis/forms/manageUserAnalysisRights";
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisID, #principal, T(lu.itrust.business.TS.AnalysisRight).ALL)")
	public String updatemanageaccessrights(@PathVariable("analysisID") int analysisID, Principal principal, Model model, @RequestBody String value, Locale locale) throws Exception {

		try {

			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(value);

			Map<User, AnalysisRight> userrights = new LinkedHashMap<>();

			Analysis analysis = serviceAnalysis.get(analysisID);

			List<UserAnalysisRight> uars = analysis.getUserRights();

			for (User user : serviceUser.getAll())
				userrights.put(DAOHibernate.Initialise(user), null);

			for (UserAnalysisRight uar : uars)
				userrights.put(DAOHibernate.Initialise(uar.getUser()), DAOHibernate.Initialise(uar.getRight()));

			int currentUser = jsonNode.get("userselect").asInt();

			model.addAttribute("currentUser", currentUser);

			for (User user : serviceUser.getAll()) {

				if (user.getLogin().equals(principal.getName()))
					continue;

				int useraccess = jsonNode.get("analysisRight_" + user.getId()).asInt();

				UserAnalysisRight uar = analysis.getRightsforUser(user);

				if (uar != null) {

					if (useraccess == -1) {
						analysis.removeRights(user);
						serviceUserAnalysisRight.delete(uar);
						serviceAnalysis.saveOrUpdate(analysis);
						userrights.put(user, null);
					} else {
						uar.setRight(AnalysisRight.valueOf(useraccess));
						serviceUserAnalysisRight.saveOrUpdate(uar);
						serviceAnalysis.saveOrUpdate(analysis);
						userrights.put(user, uar.getRight());
					}
				} else {

					if (!user.getCustomers().contains(analysis.getCustomer()))
						user.addCustomer(analysis.getCustomer());

					if (useraccess != -1) {
						uar = new UserAnalysisRight(user, analysis, AnalysisRight.valueOf(useraccess));
						serviceUserAnalysisRight.save(uar);
						serviceAnalysis.saveOrUpdate(analysis);
						userrights.put(user, uar.getRight());
					}

				}
			}
			model.addAttribute("success", messageSource.getMessage("label.analysis.manage.users.success", null, "Analysis access rights, EXPECT your own, were successfully updated!", locale));
			model.addAttribute("analysisRights", AnalysisRight.values());
			model.addAttribute("analysis", analysis);
			model.addAttribute("userrights", userrights);
			return "analysis/forms/manageUserAnalysisRights";
		} catch (Exception e) {
			// return errors
			model.addAttribute("errors", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return "analysis/forms/manageUserAnalysisRights";
		}
	}
}
