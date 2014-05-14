/**
 * 
 */
package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Phase;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.service.ServiceActionPlanSummary;
import lu.itrust.business.service.ServicePhase;

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
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @param principal
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Delete/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'ActionPlanSummary', #principal, T(lu.itrust.business.TS.AnalysisRight).DELETE)")
	public @ResponseBody
	String delete(@PathVariable int elementID, Principal principal, HttpSession session, Locale locale) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		// retrieve a single actionplansummary entry of analysis
		SummaryStage summaryStage = serviceActionPlanSummary.getFromAnalysisById(idAnalysis, elementID);
		if (summaryStage == null)
			return JsonMessage.Error(messageSource.getMessage("error.summary.not_found", null, "Summary cannot be found", locale));
		else
			try {

				// delete entry
				serviceActionPlanSummary.delete(summaryStage);

				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.summary.delete", null, "Summary was successfully deleted", locale));
			} catch (Exception e) {

				// returen error message
				e.printStackTrace();
				return JsonMessage.Error(messageSource.getMessage("error.internal.summary.delete", null, "An error occurred during the summary deleting", locale));
			}
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param principal
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).DELETE)")
	@RequestMapping(value = "/Delete", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody
	List<String> delete(Principal principal, HttpSession session, Locale locale) throws Exception {

		// create errors list
		List<String> errors = new LinkedList<String>();

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		// get summaries
		List<SummaryStage> summaryStages = serviceActionPlanSummary.getAllFromAnalysis(idAnalysis);

		try {

			// parse stages
			for (SummaryStage summaryStage : summaryStages) {
				// remove current
				serviceActionPlanSummary.delete(summaryStage);
			}

			// add success message
			errors.add(JsonMessage.Success(messageSource.getMessage("success.summary.delete", null, "Summary was successfully deleted", locale)));

			// return empty errors -> success
			return errors;

		} catch (Exception e) {
			e.printStackTrace();
			errors.add(JsonMessage.Error(messageSource.getMessage("error.internal.summary.delete", null, "An error occurred during the summary deleting", locale)));
			return errors;
		}
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
	public @ResponseBody
	String chartEvolutionProfitabityCompliance(@PathVariable String actionPlanType, Principal principal, HttpSession session, Locale locale) throws Exception {

		// retireve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		// load all phases of analysis
		List<Phase> phases = servicePhase.getAllFromAnalysis(idAnalysis);

		// load all summaries of analysis
		List<SummaryStage> summaryStages = serviceActionPlanSummary.getAllFromAnalysisAndActionPlanType(idAnalysis, actionPlanType);

		// generate chart
		return chartGenerator.evolutionProfitabilityCompliance(summaryStages, phases, actionPlanType, locale);
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
	public @ResponseBody
	String chartBudget(@PathVariable String actionPlanType, Principal principal, HttpSession session, Locale locale) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		// retrieve phases
		List<Phase> phases = servicePhase.getAllFromAnalysis(idAnalysis);

		// retrieve summaries
		List<SummaryStage> summaryStages = serviceActionPlanSummary.getAllFromAnalysisAndActionPlanType(idAnalysis, actionPlanType);

		// return chart
		return chartGenerator.budget(summaryStages, phases, actionPlanType, locale);
	}
}