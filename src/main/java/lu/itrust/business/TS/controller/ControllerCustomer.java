package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.validator.CustomerValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private MessageSource messageSource;

	/**
	 * 
	 * Display all customers
	 * 
	 * */
	@RequestMapping
	public String loadAllCustomers(Principal principal, Map<String, Object> model) throws Exception {
		model.put("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));
		return "knowledgebase/customer/customers";
	}

	/**
	 * loadCustomerUsers: <br>
	 * Description
	 * 
	 * @param customerID
	 * @param model
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping("/{customerID}/Users")
	public String loadCustomerUsers(@PathVariable("customerID") int customerID, Model model, Principal principal) throws Exception {
		if (!model.containsAttribute("customer"))
			model.addAttribute("customer", serviceCustomer.get(customerID));
		if (!model.containsAttribute("users"))
			model.addAttribute("users", serviceUser.getAll());
		if (!model.containsAttribute("customerusers"))
			model.addAttribute("customerusers", serviceUser.getAllFromCustomer(customerID));
		return "knowledgebase/customer/customerusers";
	}

	/**
	 * updateCustomerUsers: <br>
	 * Description
	 * 
	 * @param customerID
	 * @param model
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping(value = "/{customerID}/Users/Update", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public String updateCustomerUsers(@RequestBody String value, @PathVariable("customerID") int customerID, Model model, Principal principal, Locale locale,
			RedirectAttributes redirectAttributes) throws Exception {
		// create errors list

		try {

			Customer customer = serviceCustomer.get(customerID);
			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(value);
			List<User> users = serviceUser.getAll();
			List<User> customerusers = serviceUser.getAllFromCustomer(customerID);
			for (User user : users) {
				boolean userhasaccess = jsonNode.get("user_" + user.getId()).asBoolean();
				if (userhasaccess) {
					if (!user.containsCustomer(customer)) {
						user.addCustomer(customer);
						serviceUser.saveOrUpdate(user);
						TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.give.access.to.customer",
								String.format("Customer: %s, target: %s", customer.getOrganisation(), user.getLogin()), principal.getName(), LogAction.GIVE_ACCESS,
								customer.getOrganisation(), user.getLogin());
					}
				} else
					customDelete.removeCustomerByUser(customerID, user.getLogin(), principal.getName());
			}

			model.addAttribute("users", users);

			model.addAttribute("customerusers", customerusers);

			model.addAttribute("customer", serviceCustomer.get(customerID));

			model.addAttribute("success", messageSource.getMessage("label.customer.manage.users.success", null, "Customer users successfully updated!", locale));

			return loadCustomerUsers(customerID, model, principal);
		} catch (Exception e) {
			// return errors
			model.addAttribute("errors", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return loadCustomerUsers(customerID, model, principal);
		}
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
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public String section(Model model, HttpSession session, Principal principal, HttpServletRequest request) throws Exception {
		String referer = request.getHeader("Referer");
		User user = serviceUser.get(principal.getName());
		if (referer != null && referer.contains("/Admin")) {
			model.addAttribute("adminView", true);
			if (user.isAutorised(RoleType.ROLE_ADMIN))
				model.addAttribute("customers", serviceCustomer.getAll());
		}
		if (!model.containsAttribute("customers"))
			model.addAttribute("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));
		return "knowledgebase/customer/customers";
	}

	/**
	 * 
	 * Display single customer
	 * 
	 * */
	@RequestMapping("/{customerId}")
	public String loadSingleCustomer(@PathVariable("customerId") Integer customerId, HttpSession session, Map<String, Object> model, RedirectAttributes redirectAttributes,
			Locale locale) throws Exception {
		Customer customer = (Customer) session.getAttribute("customer");
		if (customer == null || customer.getId() != customerId)
			customer = serviceCustomer.get(customerId);
		if (customer == null) {
			String msg = messageSource.getMessage("errors.customer.not_exist", null, "Customer does not exist", locale);
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
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody Map<String, String> save(@RequestBody String value, Principal principal, Locale locale) {
		Map<String, String> errors = new LinkedHashMap<>();
		try {
			Customer customer = new Customer();
			if (!buildCustomer(errors, customer, value, locale))
				return errors;
			User user = serviceUser.get(principal.getName());

			if (customer.getId() < 1) {
				if (serviceCustomer.existsByOrganisation(customer.getOrganisation())) {
					errors.put("organisation", messageSource.getMessage("error.customer.duplicate.organisation", null, "Name is not available", locale));
					return errors;
				} else if (customer.isCanBeUsed()) {
					user.addCustomer(customer);
					serviceUser.saveOrUpdate(user);
				} else if (!serviceCustomer.profileExists())
					serviceCustomer.save(customer);
				else
					errors.put("canBeUsed", messageSource.getMessage("error.customer.profile.duplicate", null, "A customer profile already exists", locale));
			} else if (serviceCustomer.hasUsers(customer.getId()) && customer.isCanBeUsed() || !(serviceCustomer.hasUsers(customer.getId()) || customer.isCanBeUsed())
					&& (!serviceCustomer.profileExists() || serviceCustomer.isProfile(customer.getId())))
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
			errors.put("customer", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
		}
		return errors;
	}

	/**
	 * 
	 * Delete single customer
	 * 
	 * */
	@RequestMapping(value = "/Delete/{customerId}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody String deleteCustomer(@PathVariable("customerId") int customerId, Principal principal, HttpServletRequest request, Locale locale) throws Exception {
		try {
			customDelete.deleteCustomer(customerId, principal.getName());
			return JsonMessage.Success(messageSource.getMessage("success.customer.delete.successfully", null, "Customer was deleted successfully", locale));
		} catch (TrickException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		}
	}

	/**
	 * buildCustomer: <br>
	 * Description
	 * 
	 * @param errors
	 * @param customer
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildCustomer(Map<String, String> errors, Customer customer, String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			int id = jsonNode.get("id").asInt();
			if (id > 0)
				customer.setId(jsonNode.get("id").asInt());

			ValidatorField validator = serviceDataValidation.findByClass(Customer.class);
			if (validator == null)
				serviceDataValidation.register(validator = new CustomerValidator());

			String organisation = jsonNode.get("organisation").asText();
			String contactPerson = jsonNode.get("contactPerson").asText();
			String telephoneNumber = jsonNode.get("phoneNumber").asText();
			String email = jsonNode.get("email").asText();
			String address = jsonNode.get("address").asText();
			String city = jsonNode.get("city").asText();
			String ZIPCode = jsonNode.get("ZIPCode").asText();
			String country = jsonNode.get("country").asText();
			String error = null;

			error = validator.validate(customer, "organisation", organisation);
			if (error != null)
				errors.put("organisation", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setOrganisation(organisation);

			error = validator.validate(customer, "contactPerson", contactPerson);
			if (error != null)
				errors.put("contactPerson", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setContactPerson(contactPerson);

			error = validator.validate(customer, "phoneNumber", telephoneNumber);
			if (error != null)
				errors.put("phoneNumber", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setPhoneNumber(telephoneNumber);

			error = validator.validate(customer, "email", email);
			if (error != null)
				errors.put("email", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setEmail(email);

			error = validator.validate(customer, "address", address);
			if (error != null)
				errors.put("address", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setAddress(address);

			error = validator.validate(customer, "city", city);
			if (error != null)
				errors.put("city", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setCity(city);

			error = validator.validate(customer, "ZIPCode", ZIPCode);
			if (error != null)
				errors.put("ZIPCode", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setZIPCode(ZIPCode);

			error = validator.validate(customer, "country", country);
			if (error != null)
				errors.put("country", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setCountry(country);

			customer.setCanBeUsed(jsonNode.get("canBeUsed") == null ? true : !jsonNode.get("canBeUsed").asText().equals("on"));
		} catch (Exception e) {
			errors.put("customer", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
		}

		return errors.isEmpty();

	}
}