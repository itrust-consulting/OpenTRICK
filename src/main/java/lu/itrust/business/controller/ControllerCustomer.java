/**
 * 
 */
package lu.itrust.business.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Customer;
import lu.itrust.business.service.ServiceCustomer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author oensuifudine
 * 
 */
@Secured("ROLE_ADMIN")
@RequestMapping("/customer")
@Controller
public class ControllerCustomer {

	@Autowired
	private ServiceCustomer serviceCustomer;

	@RequestMapping("/all")
	public String loadAllCustomer(Map<String, Object> model) throws Exception {
		model.put("customers", serviceCustomer.loadAll());
		return "customers";
	}

	@Secured("ROLE_USER")
	@RequestMapping("/{customerId}")
	public String profil(@PathVariable("customerId") Integer customerId,
			HttpSession session, Map<String, Object> model) throws Exception {
		Customer customer = (Customer) session.getAttribute("customer");
		if (customer == null || customer.getId() != customerId)
			customer = serviceCustomer.get(customerId);
		model.put("customerProfil", customer);
		return "profilCustomer";
	}

	@RequestMapping("/add")
	public String addCustomer(Map<String, Object> model) {
		model.put("customer", new Customer());
		return "addCustomerForm";
	}

	@RequestMapping("/save")
	public String saveCustomer(@ModelAttribute("customer") Customer customer,
			BindingResult result) throws Exception {
		this.serviceCustomer.saveOrUpdate(customer);
		return "redirect:/customer/all";
	}

	@RequestMapping("/delete/{customerId}")
	public String deleteCustomer(@PathVariable("customerId") Integer customerId) {
		serviceCustomer.remove(customerId);
		return "redirect:/customer/all";
	}

	public void setServiceCustomer(ServiceCustomer serviceCustomer) {
		this.serviceCustomer = serviceCustomer;
	}

}
