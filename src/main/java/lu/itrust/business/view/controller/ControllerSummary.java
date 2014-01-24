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
import lu.itrust.business.component.JsonMessage;
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

	@RequestMapping("/Section")
	public String section(HttpSession session, Principal principal, Model model, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;
		model.addAttribute("phases", servicePhase.loadAllFromAnalysis(idAnalysis));
		model.addAttribute("summaries", serviceActionPlanSummary.findByAnalysis(idAnalysis));
		return "analysis/components/summary";
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	@RequestMapping(value = "/Delete/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody
	String delete(@PathVariable int id, Principal principal, HttpSession session, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		SummaryStage summaryStage = serviceActionPlanSummary.findByIdAndAnalysis(id, idAnalysis);
		if (summaryStage == null)
			return JsonMessage.Error(messageSource.getMessage("error.summary.not_found", null, "Summary cannot be found", locale));
		else
			try {
				serviceActionPlanSummary.remove(summaryStage);
				return JsonMessage.Success(messageSource.getMessage("success.summary.delete", null, "Summary was successfully deleted", locale));
			} catch (Exception e) {
				e.printStackTrace();
				return JsonMessage.Error(messageSource.getMessage("error.internal.summary.delete", null, "An error occurred during the summary deleting", locale));
			}
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	@RequestMapping(value = "/Delete", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody
	List<String> delete(Principal principal, HttpSession session, Locale locale) {
		List<String> errors = new LinkedList<String>();
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		List<SummaryStage> summaryStages = serviceActionPlanSummary.findByAnalysis(idAnalysis);
		for (SummaryStage summaryStage : summaryStages) {
			try {
				serviceActionPlanSummary.remove(summaryStage);
				errors.add(JsonMessage.Success(messageSource.getMessage("success.summary.delete", null, "Summary was successfully deleted", locale)));
			} catch (Exception e) {
				e.printStackTrace();
				errors.add(JsonMessage.Error(messageSource.getMessage("error.internal.summary.delete", null, "An error occurred during the summary deleting", locale)));
			}
		}
		return errors;
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@RequestMapping(value = "/Evolution/{actionPlanType}", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody
	String chartEvolutionProfitabityCompliance(@PathVariable String actionPlanType, Principal principal, HttpSession session, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		List<Phase> phases = servicePhase.loadAllFromAnalysis(idAnalysis);
		List<SummaryStage> summaryStages = serviceActionPlanSummary.findByAnalysisAndActionPlanType(idAnalysis, actionPlanType);
		return chartGenerator.evolutionProfitabilityCompliance(summaryStages, phases, locale);
	}
}
