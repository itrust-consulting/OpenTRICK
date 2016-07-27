/**
 * 
 */
package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.CURRENT_CUSTOMER;
import static lu.itrust.business.TS.constants.Constant.LAST_SELECTED_CUSTOMER_ID;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.component.ALEChart;
import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.AnalysisBaseInfo;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.ALE;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/Risk-evolution")
public class ControllerRiskEvolution {

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping
	public String home(Principal principal, HttpSession session, Model model) throws Exception {
		LoadUserAnalyses(session, principal, model);
		return "analyses/risk-evolution/home";
	}

	private void LoadUserAnalyses(HttpSession session, Principal principal, Model model) throws Exception {
		Integer customer = (Integer) session.getAttribute(CURRENT_CUSTOMER);
		List<Customer> customers = serviceCustomer.getAllNotProfileOfUser(principal.getName());
		User user = null;
		if (customer == null) {
			user = serviceUser.get(principal.getName());
			if (user == null)
				throw new AccessDeniedException("Access denied");
			customer = user.getInteger(LAST_SELECTED_CUSTOMER_ID);
			// check if the current customer is set -> no
			if (customer == null && !customers.isEmpty()) {
				// use first customer as selected customer
				user.setSetting(LAST_SELECTED_CUSTOMER_ID, customer = customers.get(0).getId());
				serviceUser.saveOrUpdate(user);
			}
			session.setAttribute(CURRENT_CUSTOMER, customer);
		}

		if (customer != null) {
			model.addAttribute("customer", customer);
			model.addAttribute("analyses", serviceAnalysis.getByUsernameAndCustomerAndNoEmptyAndGroupByIdentifier(principal.getName(), customer));
		}

		model.addAttribute("customers", customers);
	}

	@RequestMapping(value = "/Chart/Total-ALE", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String loadTotalALE(Principal principal, @RequestParam(name = "customerId") int customerId, @RequestParam(name = "analyses") List<Integer> analysisIds,
			Locale locale) {
		try {
			Customer customer = serviceCustomer.getFromUsernameAndId(principal.getName(), customerId);
			List<Analysis> analyses = serviceAnalysis.getByUsernameAndIds(principal.getName(), analysisIds);
			analyses.removeIf(analysis -> !analysis.getCustomer().equals(customer));
			return computeTotalALE(analyses, locale);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@RequestMapping(value = "/Chart/ALE-by-scenario-type", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String loadALEByScenarioType(Principal principal, @RequestParam(name = "customerId") int customerId,
			@RequestParam(name = "analyses") List<Integer> analysisIds, Locale locale) {
		int index = 0;
		Customer customer = serviceCustomer.getFromUsernameAndId(principal.getName(), customerId);
		List<Analysis> analyses = serviceAnalysis.getByUsernameAndIds(principal.getName(), analysisIds);
		analyses.removeIf(analysis -> !analysis.getCustomer().equals(customer));
		ALEChart[] charts = new ALEChart[analyses.size()];
		for (Analysis analysis : analyses) {
			List<Assessment> assessments = analysis.getSelectedAssessments();
			Map<Integer, ALE> mappedALEs = new LinkedHashMap<Integer, ALE>();
			List<ALE> ales = new LinkedList<ALE>();
			for (Assessment assessment : assessments) {
				ALE ale = mappedALEs.get(assessment.getScenario().getType().getValue());
				if (ale == null) {
					mappedALEs.put(assessment.getScenario().getType().getValue(), ale = new ALE(assessment.getScenario().getType().getName(), 0));
					ales.add(ale);
				}
				ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
			}

			ales.sort((ale2, ale1) -> {
				int result = Double.compare(ale1.getValue(), ale2.getValue());
				return result == 0 ? ale1.getAssetName().compareToIgnoreCase(ale2.getAssetName()) : result;
			});
			charts[index++] = new ALEChart(analysis.getLabel() + " - " + analysis.getVersion(), ales);
		}
		return chartGenerator.generateALEChart(locale, messageSource.getMessage("label.title.chart.ale_by_scenario_type", null, "ALE by Scenario Type", locale), charts);
	}

	@RequestMapping(value = "/Chart/ALE-by-asset-type", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String loadALEByAssetType(Principal principal, @RequestParam(name = "customerId") int customerId,
			@RequestParam(name = "analyses") List<Integer> analysisIds, Locale locale) {
		int index = 0;
		Customer customer = serviceCustomer.getFromUsernameAndId(principal.getName(), customerId);
		List<Analysis> analyses = serviceAnalysis.getByUsernameAndIds(principal.getName(), analysisIds);
		analyses.removeIf(analysis -> !analysis.getCustomer().equals(customer));
		ALEChart[] charts = new ALEChart[analyses.size()];
		for (Analysis analysis : analyses) {
			List<Assessment> assessments = analysis.getSelectedAssessments();
			Map<Integer, ALE> mappedALEs = new LinkedHashMap<Integer, ALE>();
			List<ALE> ales = new LinkedList<ALE>();
			for (Assessment assessment : assessments) {
				ALE ale = mappedALEs.get(assessment.getAsset().getAssetType().getId());
				if (ale == null) {
					mappedALEs.put(assessment.getAsset().getAssetType().getId(), ale = new ALE(assessment.getAsset().getAssetType().getType(), 0));
					ales.add(ale);
				}
				ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
			}

			ales.sort((ale2, ale1) -> {
				int result = Double.compare(ale1.getValue(), ale2.getValue());
				return result == 0 ? ale1.getAssetName().compareToIgnoreCase(ale2.getAssetName()) : result;
			});
			charts[index++] = new ALEChart(analysis.getLabel() + " - " + analysis.getVersion(), ales);
		}
		return chartGenerator.generateALEChart(locale, messageSource.getMessage("label.title.chart.ale_by_asset_type", null, "ALE by Asset Type", locale), charts);
	}

	private String computeTotalALE(List<Analysis> analyses, Locale locale) {
		List<ALE> ales = new ArrayList<>(analyses.size());
		analyses.forEach(analysis -> ales.add(new ALE(analysis.getLabel() + "<br />" + analysis.getVersion(),
				analysis.getAssessments().stream().filter(Assessment::isSelected).mapToDouble(Assessment::getALE).sum())));
		ales.sort((ale2, ale1) -> {
			int result = Double.compare(ale1.getValue(), ale2.getValue());
			return result == 0 ? ale1.getAssetName().compareToIgnoreCase(ale2.getAssetName()) : result;
		});;
		return chartGenerator.generateALEChart(locale, "Total ALE", ales);
	}

	@RequestMapping(value = "/Customer/{id}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody List<AnalysisBaseInfo> findByCustomer(@PathVariable Integer id, Principal principal) {
		return serviceAnalysis.getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(id, principal.getName(), AnalysisRight.highRightFrom(AnalysisRight.READ));
	}

	@RequestMapping(value = "/Customer/{id}/Identifier/{identifier}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody List<AnalysisBaseInfo> findByCustomerAndIdentifier(@PathVariable Integer id, @PathVariable String identifier, Principal principal) {
		return serviceAnalysis.getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(id, principal.getName(), identifier, AnalysisRight.highRightFrom(AnalysisRight.READ));
	}

}
