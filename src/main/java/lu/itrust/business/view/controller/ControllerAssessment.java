/**
 * 
 */
package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAssessment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author eom
 *
 */
@RequestMapping("/Assessment")
@Secured("ROLE_USER")
@Controller
public class ControllerAssessment {

	@Autowired
	private ServiceAssessment serviceAssessment;
	
	@Autowired
	private ServiceAnalysis serviceAnalysis;
	
	@Autowired
	private AssessmentManager assessmentManager;
	
	@Autowired
	private MessageSource messageSource;
	
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {
		Integer integer = (Integer) session.getAttribute("selectedAnalysis");
		if (integer == null)
			return null;
		model.addAttribute("assessments", serviceAssessment.loadAllFromAnalysisID(integer));
		return "analysis/components/assessment";
	}
	
	@RequestMapping(value = "/Generate/Missing", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody Object generateMissing(HttpSession session, Locale locale) {
		try {
			Integer integer = (Integer) session.getAttribute("selectedAnalysis");
			
			if(integer == null)
				return new String("{\"error\":\""+messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale)+"\" }");
			Analysis analysis = serviceAnalysis.get(integer);
			if(analysis == null)
				return new String("{\"error\":\""+messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale)+"\" }");
			assessmentManager.generateMissingAssessment(analysis);
			
			return new String("{\"success\":\""+messageSource.getMessage("success.assessment.missing.generate", null, "Missing assessments were successfully generated", locale)+"\"}");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String("{\"error\":\""+messageSource.getMessage("error.internal.assessment.generation", null, "An error occurred during the generation", locale)+"\"}");
	}
	
	
}
