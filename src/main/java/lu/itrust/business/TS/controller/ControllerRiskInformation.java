/**
 * 
 */
package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lu.itrust.business.TS.component.NaturalOrderComparator;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceRiskInformation;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;

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

}
