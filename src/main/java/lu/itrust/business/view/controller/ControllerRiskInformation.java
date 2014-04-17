/**
 * 
 */
package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.RiskInformation;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.RiskInformationManager;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceRiskInformation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author eomar
 * 
 */
@Controller
@RequestMapping("/RiskInformation")
@PreAuthorize(Constant.ROLE_MIN_USER)
public class ControllerRiskInformation {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceRiskInformation serviceRiskInformation;
	
	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@PreAuthorize(Constant.ROLE_ADMIN_ONLY)
	@RequestMapping(value = "/Generate/Default/From/Analysis/{idAnalysis}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody
	String genererateDefault(@PathVariable int idAnalysis, HttpServletRequest request, RedirectAttributes attributes, Locale locale) throws Exception {
		List<RiskInformation> riskInformations = serviceRiskInformation.loadAllFromAnalysisID(idAnalysis);
		if (riskInformations.isEmpty())
			return JsonMessage.Error(messageSource.getMessage("error.analysis.risk_information.empty", null, "Risk information are empty", locale));
		for (RiskInformation riskInformation : riskInformations) {
			riskInformation.setComment("");
			riskInformation.setExposed("");
			riskInformation.setHiddenComment("");
			riskInformation.setId(-1);
		}
		if (!RiskInformationManager.SaveDefault(riskInformations, request.getServletContext()))
			return JsonMessage.Error(messageSource.getMessage("error.analysis.risk_information.unknown", null, "Risk information are empty", locale));
		return JsonMessage.Success(messageSource.getMessage("success.analysis.risk_information.save", null, "Risk information were successfully saved", locale));
	}
	
	@RequestMapping(value = "/Load/Default", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#request.getSession().getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).ALL)")
	public String loadFromDefault(HttpServletRequest request, Principal principal, RedirectAttributes attributes, Locale locale) {
		try {
			Integer idAnalysis = (Integer) request.getSession().getAttribute("selectedAnalysis");
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			if(!analysis.getRiskInformations().isEmpty()){
				attributes.addFlashAttribute("error", messageSource.getMessage("error.analysis.has.risk_information", null, "Analysis already has risk information", locale));
				return "redirect:/Error";
			}
			List<RiskInformation> riskInformations = RiskInformationManager.LoadDefault(request.getServletContext());
			if(riskInformations == null)
			{
				attributes.addFlashAttribute("error", messageSource.getMessage("error.risk_information.load.default", null, "Default risk information cannot be loaded, please contact your administrator", locale));
				return "redirect:/Error";
			}
			analysis.setRiskInformations(riskInformations);
			serviceAnalysis.saveOrUpdate(analysis);
			attributes.addFlashAttribute("success", messageSource.getMessage("success.analysis.risk_information.add", null, "Risk information were successfully added", locale));
			return "redirect:/Success";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		attributes.addFlashAttribute("error", messageSource.getMessage("error.risk_information.add.unknown", null, "An unknown error occurred when saving analysis", locale));
		return "redirect:/Error";
	}

}
