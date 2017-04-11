/**
 * 
 */
package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.NaturalOrderComparator;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceRiskInformation;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.riskinformation.helper.RiskInformationComparator;

/**
 * @author eomar
 *
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/Risk-information")
public class ControllerRiskInformation {

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceRiskInformation serviceRiskInformation;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping(value = "/Manage", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String manage(Model model, HttpSession session, Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Locale analysisLocale = new Locale(serviceLanguage.getFromAnalysis(idAnalysis).getAlpha2());
		Map<String, List<RiskInformation>> riskInformationMap = serviceRiskInformation.getAllFromAnalysis(idAnalysis).stream().map(riskInformation -> {
			if (!riskInformation.isCustom()) {
				switch (riskInformation.getCategory()) {
				case "Risk_TBA":
					riskInformation.setLabel(messageSource.getMessage(String.format("label.risk_information.risk_tba.", riskInformation.getChapter().replace(".", "_")), null,
							riskInformation.getLabel(), analysisLocale));
					break;
				case "Risk_TBS":
					riskInformation.setLabel(messageSource.getMessage(String.format("label.risk_information.risk_tbs.", riskInformation.getChapter().replace(".", "_")), null,
							riskInformation.getLabel(), analysisLocale));
					break;
				default:
					riskInformation.setLabel(messageSource.getMessage(
							String.format("label.risk_information.%s.", riskInformation.getCategory().toLowerCase(), riskInformation.getChapter().replace(".", "_")), null,
							riskInformation.getLabel(), analysisLocale));
					break;
				}
			}
			return riskInformation;
		}).collect(Collectors.groupingBy(riskInformation -> riskInformation.getCategory().startsWith("Risk_TB") ? "Risk" : riskInformation.getCategory()));
		riskInformationMap.values().forEach(riskInformation -> riskInformation.sort((o1, o2) -> NaturalOrderComparator.compareTo(o1.getChapter(), o2.getChapter())));
		model.addAttribute("riskInformationMap", riskInformationMap);
		return "analyses/single/components/risk-information/manage";
	}

	@RequestMapping(value = "/Manage/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object save(@RequestBody List<RiskInformation> riskInformations, HttpSession session, Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		Map<String, RiskInformation> riskInformationMap = analysis.getRiskInformations().stream().collect(Collectors.toMap(RiskInformation::getKey, Function.identity()));
		riskInformations.forEach(riskInformation -> {
			RiskInformation persisted = riskInformationMap.remove(riskInformation.getKey());
			if (riskInformation.isCustom()) {
				if (persisted == null)
					analysis.getRiskInformations().add(riskInformation);
				else {
					persisted.setLabel(riskInformation.getLabel());
					persisted.setCustom(true);
				}
			}
		});
		
		analysis.getRiskInformations().removeAll(riskInformationMap.values());
		analysis.getRiskInformations().sort(new RiskInformationComparator());
		serviceRiskInformation.delete(riskInformationMap.values());
		serviceAnalysis.saveOrUpdate(analysis);
		return JsonMessage.Success(messageSource.getMessage("success.update.risk_information", null, "Risk information has been successfully updated", locale));
	}
	
	@RequestMapping(value = "/Section/{type}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String section(@PathVariable String type, Model model, HttpSession session, Principal principal, Locale locale){
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		model.addAttribute("riskInformation",analysis.getRiskInformations().stream().filter(riskInformation -> riskInformation.isMatch(type)).collect(Collectors.toList()));
		model.addAttribute("showHiddenComment", analysis.getSetting(AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT));
		return "analyses/single/components/risk-information/home";
	}

}
