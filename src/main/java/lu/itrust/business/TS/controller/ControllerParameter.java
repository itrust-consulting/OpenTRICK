package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceDynamicParameter;
import lu.itrust.business.TS.database.service.ServiceImpactParameter;
import lu.itrust.business.TS.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.TS.database.service.ServiceSimpleParameter;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.parameter.IParameter;

/**
 * @author eom
 * 
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/Analysis/SimpleParameter")
public class ControllerParameter {

	@Autowired
	private ServiceSimpleParameter serviceSimpleParameter;

	@Autowired
	private ServiceDynamicParameter serviceDynamicParameter;

	private ServiceImpactParameter serviceImpactParameter;

	private ServiceLikelihoodParameter serviceLikelihoodParameter;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

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
	@RequestMapping(value = "/Extended/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		List<IParameter> parameters = new LinkedList<>(serviceImpactParameter.findByAnalysisId(idAnalysis));
		parameters.addAll(serviceLikelihoodParameter.findByAnalysisId(idAnalysis));
		parameters.addAll(serviceDynamicParameter.findByAnalysisId(idAnalysis));
		model.addAttribute("mappedParameters", Analysis.SplitParameters(parameters));
		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));
		return "analyses/single/components/parameters/extended";
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
}
