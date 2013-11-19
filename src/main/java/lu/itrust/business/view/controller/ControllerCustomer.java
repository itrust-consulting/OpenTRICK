package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.service.ServiceCustomer;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
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
@Controller
@RequestMapping("/KnowledgeBase/Customer")
public class ControllerCustomer {

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private MessageSource messageSource;

	/**
	 * 
	 * Display all customers
	 * 
	 * */
	@RequestMapping
	public String loadAllCustomer(Map<String, Object> model) throws Exception {
		model.put("customers", serviceCustomer.loadAll());
		return "customer/customers";
	}

	/**
	 * 
	 * Display single customer
	 * 
	 * */
	@Secured("ROLE_USER")
	@RequestMapping("/{customerId}")
	public String profil(@PathVariable("customerId") Integer customerId, HttpSession session, Map<String, Object> model, RedirectAttributes redirectAttributes, Locale locale)
			throws Exception {
		Customer customer = (Customer) session.getAttribute("customer");
		if (customer == null || customer.getId() != customerId)
			customer = serviceCustomer.get(customerId);
		if (customer == null) {
			String msg = messageSource.getMessage("errors.customer.notexist", null, "Customer does not exist", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
			return "redirect:/KnowLedgeBase/Customer/Display";
		}
		model.put("customerProfil", customer);
		return "customer/showCustomer";
	}

	/**
	 * 
	 * Request add new customer
	 * 
	 * */
	@RequestMapping("/Add")
	public String addCustomer(Map<String, Object> model) {
		model.put("customer", new Customer());
		return "customer/addCustomer";
	}

	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	List<String[]> save(@RequestBody String value, HttpSession session, Principal principal, Locale locale) {
		List<String[]> errors = new LinkedList<>();
		try {

			Customer customer = new Customer();
			if (!buildCustomer(errors, customer, value, locale))
				return errors;
			if (customer.getId() < 1) {
				serviceCustomer.save(customer);
			} else {
				serviceCustomer.saveOrUpdate(customer);
			}
		} catch (Exception e) {
			errors.add(new String[] { "customer", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}
		return errors;
	}

	/**
	 * 
	 * Request edit single customer
	 * 
	 * */
	@RequestMapping("/Edit/{customerId}")
	public String editCustomer(@PathVariable("customerId") Integer customerId, HttpSession session, Map<String, Object> model, RedirectAttributes redirectAttributes, Locale locale)
			throws Exception {
		Customer customer = (Customer) session.getAttribute("customer");
		if (customer == null || customer.getId() != customerId)
			customer = serviceCustomer.get(customerId);
		if (customer == null) {
			String msg = messageSource.getMessage("errors.customer.notexist", null, "Customer does not exist", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
			return "redirect:/KnowLedgeBase/Customer/Display";
		}
		model.put("customerProfil", customer);
		return "customer/editCustomer";
	}

	/**
	 * 
	 * Perform edit single customer
	 * 
	 * */
	@RequestMapping("/Update/{customerId}")
	public String updateCustomer(@PathVariable("customerId") Integer customerId, @ModelAttribute("customer") @Valid Customer customer, BindingResult result,
			RedirectAttributes redirectAttributes, Locale locale) throws Exception {
		if (customer == null || customer.getId() != customerId) {
			String msg = messageSource.getMessage("errors.customer.update.notrecognized", null, "Customer not recognized", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
		} else {
			try {
				serviceCustomer.saveOrUpdate(customer);
				String msg = messageSource.getMessage("success.customer.update.success", null, "Customer had been updated!", locale);
				redirectAttributes.addFlashAttribute("success", msg);
			} catch (Exception e) {
				String msg = messageSource.getMessage("errors.customer.update.fail", null, "Customer update failed!", locale);
				redirectAttributes.addFlashAttribute("errors", msg);
			}
		}
		return "redirect:/KnowLedgeBase/Customer/Display";
	}

	/**
	 * 
	 * Delete single customer
	 * 
	 * */
	@RequestMapping("/Delete/{customerId}")
	public String deleteCustomer(@PathVariable("customerId") Integer customerId) throws Exception {
		serviceCustomer.remove(customerId);
		return "redirect:../Display";
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
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			int id = jsonNode.get("id").asInt();
			if (id > 0)
				customer.setId(jsonNode.get("id").asInt());
			
			customer.setOrganisation(jsonNode.get("organisation").asText());
			customer.setContactPerson(jsonNode.get("contactPerson").asText());
			customer.setTelephoneNumber(jsonNode.get("telephoneNumber").asText());
			customer.setEmail(jsonNode.get("email").asText());
			customer.setAddress(jsonNode.get("address").asText());
			customer.setCity(jsonNode.get("city").asText());
			customer.setZIPCode(jsonNode.get("ZIPCode").asText());
			customer.setCountry(jsonNode.get("country").asText());
			return true;

		} catch (Exception e) {

			errors.add(new String[] { "customer", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
			return false;
		}
		
	}

	public void setServiceCustomer(ServiceCustomer serviceCustomer) {
		this.serviceCustomer = serviceCustomer;
	}
}