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
		model.addAttribute("phases", servicePhase.loadAllFromAnalysis(idAnalysis));

		// add actionplan summaries
		model.addAttribute("summaries", serviceActionPlanSummary.findByAnalysis(idAnalysis));

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
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).DELETE)")
	@RequestMapping(value = "/Delete/{id}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody
	String delete(@PathVariable int id, Principal principal, HttpSession session, Locale locale) {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		// retrieve a single actionplansummary entry of analysis
		SummaryStage summaryStage = serviceActionPlanSummary.findByIdAndAnalysis(id, idAnalysis);
		if (summaryStage == null)
			return JsonMessage.Error(messageSource.getMessage("error.summary.not_found", null, "Summary cannot be found", locale));
		else
			try {

				// delete entry
				serviceActionPlanSummary.remove(summaryStage);

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
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).DELETE)")
	@RequestMapping(value = "/Delete", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody
	List<String> delete(Principal principal, HttpSession session, Locale locale) {

		// create errors list
		List<String> errors = new LinkedList<String>();

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		// get summaries
		List<SummaryStage> summaryStages = serviceActionPlanSummary.findByAnalysis(idAnalysis);

		try {

			// parse stages
			for (SummaryStage summaryStage : summaryStages) {
				// remove current
				serviceActionPlanSummary.remove(summaryStage);
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@RequestMapping(value = "/Evolution/{actionPlanType}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody
	String chartEvolutionProfitabityCompliance(@PathVariable String actionPlanType, Principal principal, HttpSession session, Locale locale) throws Exception {

		// retireve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		// load all phases of analysis
		List<Phase> phases = servicePhase.loadAllFromAnalysis(idAnalysis);

		// load all summaries of analysis
		List<SummaryStage> summaryStages = serviceActionPlanSummary.findByAnalysisAndActionPlanType(idAnalysis, actionPlanType);

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@RequestMapping(value = "/Budget/{actionPlanType}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody
	String chartBudget(@PathVariable String actionPlanType, Principal principal, HttpSession session, Locale locale) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		// retrieve phases
		List<Phase> phases = servicePhase.loadAllFromAnalysis(idAnalysis);

		// retrieve summaries
		List<SummaryStage> summaryStages = serviceActionPlanSummary.findByAnalysisAndActionPlanType(idAnalysis, actionPlanType);

		// return chart
		return chartGenerator.budget(summaryStages, phases, actionPlanType, locale);
	}
}