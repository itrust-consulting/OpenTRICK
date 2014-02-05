package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.service.ServiceCustomer;

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
	private MessageSource messageSource;

	/**
	 * loadAllCustomers: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public String loadAllCustomers(Map<String, Object> model, Principal principal) throws Exception {

		// load only customers of this user
		model.put("customers", serviceCustomer.loadByUser(principal.getName()));

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
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {

		// load only customers of this user
		model.addAttribute("customers", serviceCustomer.loadByUser(principal.getName()));

		return "knowledgebase/customer/customers";
	}

	/**
	 * loadSingleCustomer: <br>
	 * Description
	 * 
	 * @param customerId
	 * @param session
	 * @param model
	 * @param redirectAttributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{customerId}")
	public String loadSingleCustomer(@PathVariable("customerId") Integer customerId, HttpSession session, Map<String, Object> model, RedirectAttributes redirectAttributes, Locale locale)
			throws Exception {
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
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	List<String[]> save(@RequestBody String value, Locale locale) {

		// create errors list
		List<String[]> errors = new LinkedList<>();

		try {

			// create empty customer object
			Customer customer = new Customer();

			// build customer
			if (!buildCustomer(errors, customer, value, locale))

				// return errors on failure
				return errors;

			// check if user is to save or to update
			if (customer.getId() < 1) {

				// save
				serviceCustomer.save(customer);
			} else {

				// update
				serviceCustomer.saveOrUpdate(customer);
			}

			// return success message (errors are empty -> no errors)
			return errors;
		} catch (Exception e) {

			// return errors
			errors.add(new String[] { "customer", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
			return errors;
		}
	}

	/**
	 * deleteCustomer: <br>
	 * Description
	 * 
	 * @param customerId
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Delete/{customerId}", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String[] deleteCustomer(@PathVariable("customerId") Integer customerId, Locale locale) throws Exception {

		try {

			// try to delete the customer
			serviceCustomer.remove(customerId);

			// return success message
			return new String[] { "success", messageSource.getMessage("success.customer.delete.successfully", null, "Customer was deleted successfully", locale) };
		} catch (Exception e) {

			// return error message
			e.printStackTrace();
			return new String[] { "error", messageSource.getMessage("success.customer.delete.failed", null, "Customer deletion failed", locale) };
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
	private boolean buildCustomer(List<String[]> errors, Customer customer, String source, Locale locale) {

		try {

			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			// retrieve customer id node
			int id = jsonNode.get("id").asInt();

			// check if id is to be created (new) or to update
			if (id > 0)
				customer.setId(jsonNode.get("id").asInt());

			// add data
			customer.setOrganisation(jsonNode.get("organisation").asText());
			customer.setContactPerson(jsonNode.get("contactPerson").asText());
			customer.setTelephoneNumber(jsonNode.get("telephoneNumber").asText());
			customer.setEmail(jsonNode.get("email").asText());
			customer.setAddress(jsonNode.get("address").asText());
			customer.setCity(jsonNode.get("city").asText());
			customer.setZIPCode(jsonNode.get("ZIPCode").asText());
			customer.setCountry(jsonNode.get("country").asText());

			// return success message
			return true;

		} catch (Exception e) {

			// return error message
			e.printStackTrace();
			return false;
		}
	}
}