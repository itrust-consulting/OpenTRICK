package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.ActionPlanMode;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.ActionPlanManager;
import lu.itrust.business.service.ServiceActionPlan;
import lu.itrust.business.service.ServiceAsset;

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
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * ControllerAdministration.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version
 * @since Dec 13, 2013
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/ActionPlan")
@Controller
public class ControllerActionPlan {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceActionPlan serviceActionPlan;

	@Autowired
	private ServiceAsset serviceAsset;

	/**
	 * showActionPlan: <br>
	 * loads the action plans as requested (using mode). mode values: - normal (action plan by
	 * phase) - all (all action plans by phase including optimistic and pessimistic)
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@RequestMapping
	public String showActionPlan(HttpSession session, Map<String, Object> model, Principal principal) throws Exception {
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");
		List<ActionPlanEntry> actionplans = serviceActionPlan.loadAllFromAnalysis(selected);
		List<Asset> assets = ActionPlanManager.getAssetsByActionPlanType(actionplans);
		model.put("actionplans", actionplans);
		model.put("assets", assets);
		return "analysis/components/actionplan";
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@RequestMapping(value = "/Section/{type}", method = RequestMethod.GET, headers = "Accept=application/json")
	public String section(Map<String, Object> model, HttpSession session, Principal principal, @PathVariable("type") String type) throws Exception {
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");
		ActionPlanMode mode;
		try {
						
			mode = ActionPlanMode.valueOf(ActionPlanMode.getIndex(type));
			
			List<ActionPlanEntry> actionplans = serviceActionPlan.loadByAnalysisActionPlanType(selected, mode);
			List<Asset> assets = ActionPlanManager.getAssetsByActionPlanType(actionplans);
			model.put("actionplans", actionplans);
			model.put("assets", assets);
			return "analysis/components/actionplan";

		} catch (Exception e) {
			return "errors/405";
		}

	}

	/**
	 * emptyActionPlan: <br>
	 * Description
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	// TODO implement
	@RequestMapping("/Analysis/{analysisID}/ActionPlan/Empty")
	public @ResponseBody
	Boolean emptyActionPlan(@PathVariable("analysisID") int analysisID, Principal principal) throws Exception {
		try {
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}