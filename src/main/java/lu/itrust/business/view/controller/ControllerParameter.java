/**
 * 
 */
package lu.itrust.business.view.controller;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import lu.itrust.business.service.ServiceParameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author eom
 *
 */
@Secured("ROLE_USER")
@Controller
@RequestMapping("/Parameter")
public class ControllerParameter {
	
	@Autowired
	private ServiceParameter serviceParameter;
	
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;
		model.addAttribute("parameters", serviceParameter.findByAnalysis(idAnalysis));
		return "analysis/components/parameter";
	}

}
