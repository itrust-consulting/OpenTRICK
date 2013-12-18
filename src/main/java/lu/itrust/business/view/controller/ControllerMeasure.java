/**
 * 
 */
package lu.itrust.business.view.controller;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceMeasure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author eomar
 *
 */
@Secured("ROLE_USER")
@RequestMapping("/Measure")
public class ControllerMeasure {
	
	@Autowired
	private ServiceMeasure serviceMeasure;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ServiceAnalysis serviceAnalysis;
	
	@RequestMapping("/section")
	public String section(HttpSession session, Model model, RedirectAttributes attributes, Locale locale){
		Integer idAnalysis = (Integer) session.getAttribute("selectAnalysis");
		if(idAnalysis == null)
			return null;
		model.addAttribute("measure", serviceMeasure.findByAnalysis(idAnalysis));
		model.addAttribute("locale", locale);
		return "analysis/components/measure";
	}
}
