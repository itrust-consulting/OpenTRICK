/**
 * 
 */
package lu.itrust.business.view.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceParameter;
import lu.itrust.business.service.ServiceParameterType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
	
	@RequestMapping(value = "/Update/ImplementationScale", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody Map<String,String> updateImplementationScaleNames() {
		
		Map<String, String> errors = new LinkedHashMap<String,String>();
		
		try {
		
		List<Analysis> analyses = serviceAnalysis.loadAll();
		
		for(Analysis analysis: analyses) {
			
			List<Parameter> parameters = serviceParameter.findByAnalysisAndType(analysis.getId(), Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME);
			
			for(Parameter parameter : parameters) {
				if(!parameter.getDescription().startsWith("ImpScale"))
					continue;
				else {
					Integer line = Integer.valueOf(parameter.getDescription().substring(8));
					String desc = "";
					switch(line){
						case 1:desc=Constant.IS_NOT_ACHIEVED;break;
						case 2:desc=Constant.IS_RUDIMENTARY_ACHIEVED;break;
						case 3:desc=Constant.IS_PARTIALLY_ACHIEVED;break;
						case 4:desc=Constant.IS_LARGELY_ACHIEVED;break;
						case 5:desc=Constant.IS_FULLY_ACHIEVED;break;
						default:desc="ImpScale"+String.valueOf(line);break;
					}
					
					parameter.setDescription(desc);
					serviceParameter.saveOrUpdate(parameter);
				}
			}
		}
		return errors;
		}catch(Exception e) {
			errors.put("error", e.getMessage());
			e.printStackTrace();
			return errors;
		}
	}
}
