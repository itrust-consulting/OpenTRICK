package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ControllerMeasure.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl. :
 * @version
 * @since Feb 4, 2014
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Measure")
@Controller
public class ControllerMeasure {

	@Autowired
	private ServiceMeasure serviceMeasure;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceLanguage serviceLanguage;

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param session
	 * @param model
	 * @param principal
	 * @return
	 */
	@RequestMapping("/Section")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String section(HttpSession session, Model model, Principal principal) {
		
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;
		
		// add measures of the analysis
		model.addAttribute("measures", serviceMeasure.findByAnalysis(idAnalysis));
		
		// add language of the analysis
		model.addAttribute("language", serviceLanguage.findByAnalysis(idAnalysis).getAlpha3());
		
		return "analysis/components/measure";
	}

	/**
	 * sectionNorm: <br>
	 * Description
	 * 
	 * @param norm
	 * @param session
	 * @param model
	 * @param attributes
	 * @return
	 */
	@RequestMapping("/Section/{norm}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String sectionNorm(@PathVariable String norm, HttpSession session, Model model, Principal principal) {
		
		// get analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;
		
		// add measures of a norm
		model.addAttribute("measures", serviceMeasure.findByAnalysisAndNorm(idAnalysis, norm));
		
		// add language of analysis
		model.addAttribute("language", serviceLanguage.findByAnalysis(idAnalysis));
		
		return "analysis/components/measure";
	}

	/**
	 * compliance: <br>
	 * Description
	 * 
	 * @param norm
	 * @param session
	 * @param locale
	 * @return
	 */
	@RequestMapping("/Compliance/{norm}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@ResponseBody
	String compliance(@PathVariable String norm, HttpSession session, Locale locale, Principal principal) {
		
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		try {

			// return chart of either norm 27001 or 27002 or null
			return (norm.equals(Constant.NORM_27001) || norm.equals(Constant.NORM_27002)) ? chartGenerator.compliance(idAnalysis, norm, locale) : null;

		} catch (Exception e) {
			
			// retrun error
			e.printStackTrace();
			return null;
		}
	}
}