/**
 * 
 */
package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.chartJS.Chart;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceActionPlanSummary;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.database.service.ServiceSimpleParameter;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;

/**
 * @author eomar
 * 
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/ActionPlanSummary")
@Controller
public class ControllerSummary {

	@Autowired
	private ServiceActionPlanSummary serviceActionPlanSummary;

	@Autowired
	private ServicePhase servicePhase;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceSimpleParameter serviceSimpleParameter;

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param session
	 * @param principal
	 * @param model
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	@RequestMapping("/Section")
	public String section(HttpSession session, Principal principal, Model model, Locale locale) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		model.addAttribute("analysisId", idAnalysis);

		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));

		// add phases of analysis
		model.addAttribute("phases", servicePhase.getAllFromAnalysis(idAnalysis));

		// add actionplan summaries
		model.addAttribute("summaries", serviceActionPlanSummary.getAllFromAnalysis(idAnalysis));

		model.addAttribute("language", serviceLanguage.getFromAnalysis(idAnalysis).getAlpha2());

		return "analyses/single/components/summary";
	}

	/**
	 * chartEvolutionProfitabityCompliance: <br>
	 * Description
	 * 
	 * @param actionPlanType
	 * @param principal
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Evolution/{actionPlanType}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Chart[] chartEvolutionProfitabityCompliance(@PathVariable String actionPlanType, Principal principal, HttpSession session, Locale locale)
			throws Exception {
		// retireve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		// generate chart
		return chartGenerator.evolutionProfitabilityCompliance((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS),
				serviceActionPlanSummary.getAllFromAnalysisAndActionPlanType(idAnalysis, actionPlanType), servicePhase.getAllFromAnalysis(idAnalysis), actionPlanType, locale);
	}

	/**
	 * chartBudget: <br>
	 * Description
	 * 
	 * @param actionPlanType
	 * @param principal
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Budget", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Chart[] chartBudget(Principal principal, HttpSession session, Locale locale) throws Exception {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		String actionPlanType = ActionPlanMode.APPN.getName();
		// return chart
		return chartGenerator.budget(serviceSimpleParameter.findByAnalysisId(idAnalysis), serviceActionPlanSummary.getAllFromAnalysisAndActionPlanType(idAnalysis, actionPlanType),
				servicePhase.getAllFromAnalysis(idAnalysis), actionPlanType, locale);
	}
}