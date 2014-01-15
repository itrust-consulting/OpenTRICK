/**
 * 
 */
package lu.itrust.business.view.controller;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author eomar
 * 
 */
@Secured("ROLE_USER")
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

	@RequestMapping("/Section")
	public String section(HttpSession session, Model model,
			RedirectAttributes attributes) {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;
		model.addAttribute("measure", serviceMeasure.findByAnalysis(idAnalysis));
		model.addAttribute("language", serviceLanguage.findByAnalysis(idAnalysis));
		return "analysis/components/measure";
	}

	@RequestMapping("/Compliance/{norm}")
	@ResponseBody
	String compliance(@PathVariable String norm, HttpSession session,
			Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;
		return (norm.equals(Constant.NORM_27001) || norm
				.equals(Constant.NORM_27002)) ? chartGenerator.compliance(
				idAnalysis, norm, locale) : null;
	}
}
