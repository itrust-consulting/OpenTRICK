/**
 * 
 */
package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Phase;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.TS.actionplan.SummaryStandardConformance;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.service.ServiceActionPlanSummary;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServicePhase;
import lu.itrust.business.service.ServiceUser;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author eomar
 * 
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/ActionPlanSummary")
@Controller
public class ControllerSummary {

	@Autowired
	private ServiceActionPlanSummary serviceActionPlanSummary;

	@Autowired
	private ServicePhase servicePhase;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@RequestMapping("/Section")
	public String section(HttpSession session, Principal principal, Model model, Locale locale) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		// add phases of analysis
		model.addAttribute("phases", servicePhase.getAllFromAnalysis(idAnalysis));

		// add actionplan summaries
		model.addAttribute("summaries", serviceActionPlanSummary.getAllFromAnalysis(idAnalysis));

		return "analysis/components/summary";
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
	@RequestMapping(value = "/Evolution/{actionPlanType}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public @ResponseBody String chartEvolutionProfitabityCompliance(@PathVariable String actionPlanType, Principal principal, HttpSession session, Locale locale) throws Exception {

		// retireve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		// load all phases of analysis
		List<Phase> phases = servicePhase.getAllFromAnalysis(idAnalysis);

		// load all summaries of analysis
		List<SummaryStage> summaryStages = serviceActionPlanSummary.getAllFromAnalysisAndActionPlanType(idAnalysis, actionPlanType);

		for (SummaryStage stage : summaryStages) {
			Hibernate.initialize(stage);
			for (SummaryStandardConformance conformance : stage.getConformances()) {
				Hibernate.initialize(conformance);
				Hibernate.initialize(conformance.getAnalysisStandard().getStandard());
			}
		}

		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));

		// generate chart
		return chartGenerator.evolutionProfitabilityCompliance((Integer) session.getAttribute("selectedAnalysis"), summaryStages, phases, actionPlanType, customLocale != null ? customLocale : locale);
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
	@RequestMapping(value = "/Budget/{actionPlanType}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public @ResponseBody String chartBudget(@PathVariable String actionPlanType, Principal principal, HttpSession session, Locale locale) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		// retrieve phases
		List<Phase> phases = servicePhase.getAllFromAnalysis(idAnalysis);

		// retrieve summaries
		List<SummaryStage> summaryStages = serviceActionPlanSummary.getAllFromAnalysisAndActionPlanType(idAnalysis, actionPlanType);

		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));

		// return chart
		return chartGenerator.budget(summaryStages, phases, actionPlanType, customLocale != null ? customLocale : locale);
	}
}