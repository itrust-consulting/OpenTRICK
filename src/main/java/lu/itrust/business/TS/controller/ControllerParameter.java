package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.OPEN_MODE;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gargoylesoftware.htmlunit.javascript.host.Console;

import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceDynamicParameter;
import lu.itrust.business.TS.database.service.ServiceImpactParameter;
import lu.itrust.business.TS.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.TS.database.service.ServiceParameterType;
import lu.itrust.business.TS.database.service.ServiceSimpleParameter;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;
import lu.itrust.business.TS.model.parameter.type.impl.ParameterType;

/**
 * @author eom
 * 
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/Analysis/Parameter")
public class ControllerParameter {

	@Autowired
	private ServiceSimpleParameter serviceSimpleParameter;

	@Autowired
	private ServiceDynamicParameter serviceDynamicParameter;

	@Autowired
	private ServiceImpactParameter serviceImpactParameter;

	@Autowired
	private ServiceLikelihoodParameter serviceLikelihoodParameter;

	@Autowired
	private ServiceParameterType serviceParameterType;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private MessageSource messageSource;

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
	@RequestMapping(value = "/Quantitative/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String quantitativeSection(Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		List<IParameter> parameters = new LinkedList<>(serviceImpactParameter.findByAnalysisId(idAnalysis));
		parameters.addAll(serviceLikelihoodParameter.findByAnalysisId(idAnalysis));
		parameters.addAll(serviceDynamicParameter.findByAnalysisId(idAnalysis));
		parameters.addAll(serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, idAnalysis));
		model.addAttribute("mappedParameters", Analysis.SplitParameters(parameters));
		model.addAttribute("type", AnalysisType.QUANTITATIVE);
		return "analyses/single/components/parameters/quantitative/home";
	}

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
	@RequestMapping(value = "/Qualitative/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String qualitativeSection(Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		List<IParameter> parameters = new LinkedList<>(serviceLikelihoodParameter.findByAnalysisId(idAnalysis));
		parameters.addAll(serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, idAnalysis));
		parameters.addAll(serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_RISK_ACCEPTANCE_NAME, idAnalysis));
		parameters.addAll(serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_CSSF_NAME, idAnalysis));
		model.addAttribute("mappedParameters", Analysis.SplitParameters(parameters));
		model.addAttribute("isEditable",!OpenMode.parseOrDefault(session.getAttribute(OPEN_MODE)).isReadOnly());
		model.addAttribute("type", AnalysisType.QUALITATIVE);
		return "analyses/single/components/parameters/qualitative/home";
	}

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
	@RequestMapping(value = "/Impact/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String impactSection(Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		List<ImpactParameter> parameters = serviceImpactParameter.findByAnalysisId(idAnalysis);
		model.addAttribute("impactTypes", parameters.parallelStream().map(ImpactParameter::getType).distinct().collect(Collectors.toList()));
		model.addAttribute("mappedParameters", Analysis.SplitParameters(parameters));
		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));
		return "analyses/single/components/parameters/impact/home";
	}

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
	@RequestMapping(value = "/Probability/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String probabilitySection(Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		model.addAttribute("mappedParameters", Analysis.SplitParameters(serviceLikelihoodParameter.findByAnalysisId(idAnalysis)));
		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));
		return "analyses/single/components/parameters/probability/home";
	}

	/**
	 * maturityImplementationRate: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Maturity/ImplementationRate", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object maturityImplementationRate(Model model, HttpSession session, Principal principal) throws Exception {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		// load parameters of analysis
		return serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME, idAnalysis);
	}

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
	@RequestMapping(value = "/Risk-acceptance/form", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String riskAcceptanceForm(Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), level = serviceLikelihoodParameter.findMaxLevelByIdAnalysis(idAnalysis);
		model.addAttribute("maxImportance", level * level);
		model.addAttribute("parameters", serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_RISK_ACCEPTANCE_NAME, idAnalysis));
		return "analyses/single/components/parameters/form/riskAcceptance";
	}

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
	@RequestMapping(value = "/Risk-acceptance/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String riskAcceptanceSave(@RequestBody List<SimpleParameter> parameters, HttpSession session, Principal principal, Locale locale) throws Exception {
		ParameterType parameterType = serviceParameterType.getByName(Constant.PARAMETERTYPE_TYPE_RISK_ACCEPTANCE_NAME);
		if (parameterType == null)
			parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_RISK_ACCEPTANCE_NAME);

		Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));

		Map<Integer, SimpleParameter> simpleParameters = analysis.findParametersByType(Constant.PARAMETERTYPE_TYPE_RISK_ACCEPTANCE_NAME).stream()
				.map(parameter -> (SimpleParameter) parameter).collect(Collectors.toMap(SimpleParameter::getId, Function.identity()));
		for (SimpleParameter simpleParameter : parameters) {
			SimpleParameter parameter = simpleParameters.remove(simpleParameter.getId());
			if (parameter == null) {
				simpleParameter.setType(parameterType);
				analysis.add(simpleParameter);
			} else {
				parameter.setDescription(simpleParameter.getDescription());
				parameter.setValue(simpleParameter.getValue());
			}
		}

		if (!simpleParameters.isEmpty()) {
			analysis.getParameters().get(Constant.PARAMETER_CATEGORY_SIMPLE).removeIf(parameter -> simpleParameters.containsKey(parameter.getId()));
			serviceSimpleParameter.delete(simpleParameters.values());
		}
		
		serviceAnalysis.saveOrUpdate(analysis);

		return JsonMessage.Success(messageSource.getMessage("success.update.risk_acceptance", null, "Risk acceptance has been successfully updated", locale));
	}
}
