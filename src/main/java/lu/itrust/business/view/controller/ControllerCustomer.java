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
import org.springframework.security.access.annotation.Secured;
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
	 * 
	 * Display all customers
	 * 
	 * */
	@RequestMapping
	public String loadAllCustomers(Map<String, Object> model) throws Exception {
		model.put("customers", serviceCustomer.loadAll());
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
	public String section(Model model, HttpSession session, Principal principal) throws Exception{
		model.addAttribute("customers", serviceCustomer.loadAll());
		return "knowledgebase/customer/customers";
	}
	
	/**
	 * 
	 * Display single customer
	 * 
	 * */
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
	 * Delete single customer
	 * 
	 * */
	@RequestMapping(value = "/Delete/{customerId}", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody String[] deleteCustomer(@PathVariable("customerId") Integer customerId, Locale locale) throws Exception {
		serviceCustomer.remove(customerId);		
		return new String[] {
			"error",
			messageSource.getMessage("success.customer.delete.successfully", null,
					"Customer was deleted successfully", locale) 
		};
		
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