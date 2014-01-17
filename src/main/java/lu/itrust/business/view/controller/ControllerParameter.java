/**
 * 
 */
package lu.itrust.business.view.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.service.ServiceParameter;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author eom
 * 
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/Parameter")
public class ControllerParameter {

	@Autowired
	private ServiceParameter serviceParameter;

	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json")
	public String section(Model model, HttpSession session, Principal principal)
			throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;
		model.addAttribute("parameters",
				serviceParameter.findByAnalysis(idAnalysis));
		return "analysis/components/parameter";
	}

	@RequestMapping(value = "/Maturity/ImplementationRate", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody List<Parameter> maturityImplementationRate(Model model,
			HttpSession session) throws JsonGenerationException,
			JsonMappingException, IOException {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;
		return serviceParameter.findByAnalysisAndTypeAndNoLazy(idAnalysis,
				Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME);
	}
}
