/**
 * 
 */
package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.CURRENT_CUSTOMER;
import static lu.itrust.business.TS.constants.Constant.LAST_SELECTED_CUSTOMER_ID;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.model.analysis.Analysis;
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
			model.addAttribute("analyses", serviceAnalysis.getAllNotEmptyFromUserAndCustomer(principal.getName(), customer));
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

	private String computeTotalALE(List<Analysis> analyses, Locale locale) {
		List<ALE> ales = new ArrayList<>(analyses.size());
		analyses.forEach(analysis -> ales.add(new ALE(analysis.getLabel() + "<br />" + analysis.getVersion(),
				analysis.getAssessments().stream().filter(Assessment::isSelected).mapToDouble(Assessment::getALE).sum())));
		return chartGenerator.generateALEChart(locale, "Total ALE", ales);
	}

}
