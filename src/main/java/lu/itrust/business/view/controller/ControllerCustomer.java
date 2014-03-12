package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.component.CustomDelete;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.service.ServiceCustomer;
import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.validator.CustomerValidator;
import lu.itrust.business.validator.field.ValidatorField;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
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
		model.put("customers", serviceCustomer.loadByUser(principal.getName()));
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
		model.addAttribute("customer", serviceCustomer.get(customerID));
		model.addAttribute("users", serviceUser.loadAll());
		model.addAttribute("customerusers", serviceUser.loadByCustomer(customerID));
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
			List<User> users = serviceUser.loadAll();
			List<User> customerusers = serviceUser.loadByCustomer(customerID);
			for (User user : users) {
				boolean userhasaccess = jsonNode.get("user_" + user.getId()).asBoolean();
				if (userhasaccess)
					user.addCustomer(customer);
				else
					user.removeCustomer(customer);

				serviceUser.saveOrUpdate(user);
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
		if (referer != null && referer.contains("/trickservice/Admin")) {
			User user = serviceUser.get(principal.getName());
			if (user.isAutorise(RoleType.ROLE_ADMIN)) {
				model.addAttribute("adminView", true);
				model.addAttribute("customers", serviceCustomer.loadAll());
			} else
				model.addAttribute("customers", serviceCustomer.loadByUser(principal.getName()));
		} else
			model.addAttribute("customers", serviceCustomer.loadByUser(principal.getName()));
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
			String msg = messageSource.getMessage("errors.customer.notexist", null, "Customer does not exist", locale);
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
	public @ResponseBody
	Map<String, String> save(@RequestBody String value, Principal principal, Locale locale) {
		Map<String, String> errors = new LinkedHashMap<>();
		try {
			Customer customer = new Customer();
			if (!buildCustomer(errors, customer, value, locale))
				return errors;
			User user = serviceUser.get(principal.getName());

			if (customer.getId() < 1) {
				if (serviceCustomer.exist(customer.getOrganisation())) {
					errors.put("organisation", messageSource.getMessage("error.customer.duplicate.organisation", null, "Name is not available", locale));
					return errors;
				} else if (customer.isCanBeUsed()) {
					user.addCustomer(customer);
					serviceUser.saveOrUpdate(user);
				} else if (!serviceCustomer.hasProfileCustomer())
					serviceCustomer.save(customer);
				else
					errors.put("canBeUsed", messageSource.getMessage("error.customer.profile.duplicate", null, "A customer profile already exists", locale));
			} else if (serviceCustomer.hasUser(customer.getId()) && customer.isCanBeUsed() || !(serviceCustomer.hasUser(customer.getId()) || customer.isCanBeUsed())
					&& (!serviceCustomer.hasProfileCustomer() || serviceCustomer.isProfile(customer.getId())))
				serviceCustomer.saveOrUpdate(customer);
			else
				errors.put("canBeUsed",
						messageSource.getMessage("error.customer.profile.attach.user", null, "Only a customer who is not attached to a user can be used as profile", locale));
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
	public @ResponseBody
	String deleteCustomer(@PathVariable("customerId") int customerId, Principal principal, HttpServletRequest request, Locale locale) throws Exception {
		try {
			Customer customer = serviceCustomer.get(customerId);
			if (customer == null)
				return JsonMessage.Error(messageSource.getMessage("error.customer.not_found", null, "Customer cannot be found", locale));

			String referer = request.getHeader("Referer");

			if (referer != null && referer.contains("/trickservice/Admin")) {
				User user = serviceUser.get(principal.getName());
				if (user.isAutorise(RoleType.ROLE_ADMIN)) {
					customDelete.deleteCustomer(customer);
					return JsonMessage.Success(messageSource.getMessage("success.customer.delete.successfully", null, "Customer was deleted successfully", locale));
				} else
					JsonMessage.Error(messageSource.getMessage("errors.403.access.denied", null, "You do not have the nessesary permissions to perform this action!", locale));
			}
			customDelete.deleteCustomerByUser(customer, principal.getName());
			return JsonMessage.Success(messageSource.getMessage("success.customer.delete.successfully", null, "Customer was deleted successfully", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.customer.delete", null, "Customer cannot be deleted", locale));
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
			String telephoneNumber = jsonNode.get("telephoneNumber").asText();
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

			error = validator.validate(customer, "telephoneNumber", telephoneNumber);
			if (error != null)
				errors.put("telephoneNumber", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setTelephoneNumber(telephoneNumber);

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