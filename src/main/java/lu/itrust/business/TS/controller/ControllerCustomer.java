package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.CustomerBuilder;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.usermanagement.User;

/**
 * ControllerCustomer.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since Oct 11, 2013
 */
@PreAuthorize(Constant.ROLE_MIN_CONSULTANT)
@Controller
@RequestMapping("/KnowledgeBase/Customer")
public class ControllerCustomer {

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private CustomerBuilder customerBuilder;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ServiceLanguage serviceLanguage;

	/**
	 * 
	 * Display all customers
	 * 
	 */
	@RequestMapping
	public String loadAllCustomers(Principal principal, Map<String, Object> model) throws Exception {
		model.put("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));
		return "knowledgebase/customer/customers";
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
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String section(Model model, HttpSession session, Principal principal, HttpServletRequest request) throws Exception {
		model.addAttribute("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));
		return "knowledgebase/customer/customers";
	}

	/**
	 * 
	 * Display single customer
	 * 
	 */
	// @RequestMapping("/{customerId}")
	@Deprecated
	public String loadSingleCustomer(@PathVariable("customerId") Integer customerId, Principal principal, HttpSession session, Map<String, Object> model,
			RedirectAttributes redirectAttributes, Locale locale) throws Exception {
		Customer customer = (Customer) session.getAttribute("customer");
		if (customer == null || customer.getId() != customerId)
			customer = serviceCustomer.getFromUsernameAndId(principal.getName(), customerId);
		if (customer == null) {
			String msg = messageSource.getMessage("error.customer.not_exist", null, "Customer does not exist", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
			return "redirect:/KnowLedgeBase/Customer/Display";
		}
		model.put("customer", customer);
		return "knowledgebase/customer/showCustomer";
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param value
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Map<String, String> save(@RequestBody String value, Principal principal, Locale locale) {
		Map<String, String> errors = new LinkedHashMap<>();
		try {
			Customer customer = new Customer();
			if (!customerBuilder.buildCustomer(errors, customer, value, locale))
				return errors;
			User user = serviceUser.get(principal.getName());

			if (customer.getId() < 1) {
				if (customer.isCanBeUsed()) {
					user.addCustomer(customer);
					serviceUser.saveOrUpdate(user);
				} else if (!serviceCustomer.profileExists())
					serviceCustomer.save(customer);
				else
					errors.put("canBeUsed", messageSource.getMessage("error.customer.profile.duplicate", null, "A customer profile already exists", locale));
			} else if (serviceCustomer.hasUsers(customer.getId()) && customer.isCanBeUsed()
					|| !(serviceCustomer.hasUsers(customer.getId()) || customer.isCanBeUsed()) && (!serviceCustomer.profileExists() || serviceCustomer.isProfile(customer.getId())))
				serviceCustomer.saveOrUpdate(customer);
			else
				errors.put("canBeUsed",
						messageSource.getMessage("error.customer.profile.attach.user", null, "Only a customer who is not attached to a user can be used as profile", locale));
			/**
			 * Log
			 */
			if (errors.isEmpty())
				TrickLogManager.Persist(LogType.ANALYSIS, "log.add_or_update.customer", String.format("Customer: %s", customer.getOrganisation()), principal.getName(),
						LogAction.CREATE_OR_UPDATE, customer.getOrganisation());
		} catch (Exception e) {
			errors.put("customer", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}
		return errors;
	}

	/**
	 * 
	 * Delete single customer
	 * 
	 */
	@RequestMapping(value = "/{customerId}/Delete", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String deleteCustomer(@PathVariable("customerId") int customerId, Principal principal, Locale locale) throws Exception {
		try {
			if (!serviceCustomer.hasAccess(principal.getName(), customerId))
				throw new AccessDeniedException(messageSource.getMessage("error.customer.not_exist", null, "Customer does not exist", locale));
			customDelete.deleteCustomer(customerId, principal.getName());
			return JsonMessage.Success(messageSource.getMessage("success.customer.delete.successfully", null, "Customer was deleted successfully", locale));
		} catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		}
	}

	@GetMapping(value = "/{customerId}/Manage/Report-template", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String reportTemplateForm(@PathVariable("customerId") int customerId, Model model, Principal principal, Locale locale) {
		Customer customer = serviceCustomer.getFromUsernameAndId(principal.getName(), customerId);
		if (customer == null || !customer.isCanBeUsed())
			throw new AccessDeniedException(messageSource.getMessage("error.customer.not_exist", null, "Customer does not exist", locale));
		model.addAttribute("customer", customer);
		model.addAttribute("types", new AnalysisType[] { AnalysisType.QUANTITATIVE, AnalysisType.QUALITATIVE });
		model.addAttribute("languages", serviceLanguage.getByAlpha3("ENG", "FRA"));
		model.addAttribute("reportTemplates", customer.getTemplates());
		return "knowledgebase/customer/form/report-template";
	}

}