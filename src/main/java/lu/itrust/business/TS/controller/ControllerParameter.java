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

import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceDynamicParameter;
import lu.itrust.business.TS.database.service.ServiceImpactParameter;
import lu.itrust.business.TS.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.TS.database.service.ServiceRiskAcceptanceParameter;
import lu.itrust.business.TS.database.service.ServiceSimpleParameter;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.RiskAcceptanceParameter;

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
	private ServiceRiskAcceptanceParameter serviceRiskAcceptanceParameter;

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
		Map<String, String> settings = serviceAnalysis.getSettingsByIdAnalysis(idAnalysis);
		AnalysisSetting dynamicAnalysis = AnalysisSetting.ALLOW_DYNAMIC_ANALYSIS;
		model.addAttribute("showDynamicAnalysis", Analysis.findSetting(dynamicAnalysis, settings.get(dynamicAnalysis.name())));
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
		parameters.addAll(serviceRiskAcceptanceParameter.findByAnalysisId(idAnalysis));
		parameters.addAll(serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_CSSF_NAME, idAnalysis));
		model.addAttribute("mappedParameters", Analysis.SplitParameters(parameters));
		model.addAttribute("isEditable", !OpenMode.parseOrDefault(session.getAttribute(OPEN_MODE)).isReadOnly());
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
		model.addAttribute("parameters", serviceRiskAcceptanceParameter.findByAnalysisId(idAnalysis));
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
	public @ResponseBody String riskAcceptanceSave(@RequestBody List<RiskAcceptanceParameter> parameters, HttpSession session, Principal principal, Locale locale)
			throws Exception {
		try {
			Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
			Map<Integer, RiskAcceptanceParameter> riskAcceptanceParameters = analysis.getRiskAcceptanceParameters().stream()
					.collect(Collectors.toMap(RiskAcceptanceParameter::getId, Function.identity()));
			for (RiskAcceptanceParameter riskAcceptanceParameter : parameters) {
				RiskAcceptanceParameter parameter = riskAcceptanceParameters.remove(riskAcceptanceParameter.getId());
				if (parameter == null)
					analysis.add(riskAcceptanceParameter);
				else {
					parameter.setLabel(riskAcceptanceParameter.getLabel());
					parameter.setColor(riskAcceptanceParameter.getColor());
					parameter.setDescription(riskAcceptanceParameter.getDescription());
					parameter.setValue(riskAcceptanceParameter.getValue());
				}
			}

			if (!riskAcceptanceParameters.isEmpty()) {
				analysis.getParameters().get(Constant.PARAMETER_CATEGORY_RISK_ACCEPTANCE).removeIf(parameter -> riskAcceptanceParameters.containsKey(parameter.getId()));
				serviceRiskAcceptanceParameter.delete(riskAcceptanceParameters.values());
			}
			serviceAnalysis.saveOrUpdate(analysis);
			return JsonMessage.Success(messageSource.getMessage("success.update.risk_acceptance", null, "Risk acceptance has been successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			TrickLogManager.Persist(e);
			if (e instanceof TrickException)
				return JsonMessage.Error(messageSource.getMessage(((TrickException) e).getCode(), ((TrickException) e).getParameters(), e.getMessage(), locale));
			return JsonMessage.Error(messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
		}
	}
}
