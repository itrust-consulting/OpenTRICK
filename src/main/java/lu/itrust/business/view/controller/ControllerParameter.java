/**
 * 
 */
package lu.itrust.business.view.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.ParameterType;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.ParameterManager;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.dao.hbm.DAOHibernate;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceParameter;
import lu.itrust.business.service.ServiceParameterType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ServiceParameterType serviceParameterType;

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;

		// add parameters to model
		model.addAttribute("parameters", serviceParameter.findByAnalysis(idAnalysis));

		return "analysis/components/parameter";
	}

	/**
	 * maturityImplementationRate: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@RequestMapping(value = "/Maturity/ImplementationRate", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public @ResponseBody
	List<Parameter> maturityImplementationRate(Model model, HttpSession session, Principal principal) throws JsonGenerationException, JsonMappingException, IOException {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;

		// load parameters of analysis
		return serviceParameter.findByAnalysisAndTypeAndNoLazy(idAnalysis, Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME);
	}

	@PreAuthorize(Constant.ROLE_ADMIN_ONLY)
	@RequestMapping(value = "/Generate/Default/From/Analysis/{idAnalysis}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody
	String genererateDefault(@PathVariable int idAnalysis, HttpServletRequest request, RedirectAttributes attributes, Locale locale) throws Exception {
		List<Parameter> parameters = serviceParameter.findByAnalysis(idAnalysis);
		Hibernate.initialize(parameters);
		for (Parameter parameter : parameters){
			parameter.setId(-1);
			parameter.setType(DAOHibernate.Initialise(parameter.getType()));
		}
		if (parameters.isEmpty())
			return JsonMessage.Error(messageSource.getMessage("error.analysis.parameter.empty", null, "Parameters are empty", locale));
		if (!ParameterManager.SaveDefault(parameters, request.getServletContext()))
			return JsonMessage.Error(messageSource.getMessage("error.analysis.parameter.unknown", null, "Parameters are empty", locale));
		return JsonMessage.Success(messageSource.getMessage("success.analysis.parameter.save", null, "Parameters were successfully saved", locale));
	}

	@RequestMapping(value = "/Load/Default", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#request.getSession().getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).ALL)")
	public String loadFromDefault(HttpServletRequest request, Principal principal, RedirectAttributes attributes, Locale locale) {
		try {
			Integer idAnalysis = (Integer) request.getSession().getAttribute("selectedAnalysis");
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			if (!analysis.getParameters().isEmpty()) {
				attributes.addFlashAttribute("error", messageSource.getMessage("error.analysis.has.parameter", null, "Analysis already has parameters", locale));
				return "redirect:/Error";
			}
			List<Parameter> parameters = ParameterManager.LoadDefault(request.getServletContext());
			if (parameters == null) {
				attributes.addFlashAttribute("error",
						messageSource.getMessage("error.parameter.load.default", null, "Default parameters cannot be loaded, please contact your administrator", locale));
				return "redirect:/Error";
			}
			
			for (Parameter parameter : parameters) {
				ParameterType parameterType = serviceParameterType.get(parameter.getType().getLabel());
				parameter.setType(parameterType);
			}
			
			analysis.setParameters(parameters);
			serviceAnalysis.saveOrUpdate(analysis);
			attributes.addFlashAttribute("success", messageSource.getMessage("success.analysis.parameter.add", null, "parameters were successfully added", locale));
			return "redirect:/Success";

		} catch (Exception e) {
			e.printStackTrace();
		}
		attributes.addFlashAttribute("error", messageSource.getMessage("error.parameter.add.unknown", null, "An unknown error occurred when saving analysis", locale));
		return "redirect:/Error";
	}

}
