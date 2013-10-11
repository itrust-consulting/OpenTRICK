package lu.itrust.business.controller;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import lu.itrust.business.TS.Customer;
import lu.itrust.business.service.ServiceCustomer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
	@RequestMapping("KnowLedgeBase/Customer/Display")
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
	@RequestMapping("KnowLedgeBase/Customer/{customerId}")
	public String profil(@PathVariable("customerId") Integer customerId, HttpSession session, Map<String, Object> model, RedirectAttributes redirectAttributes, 
			Locale locale) throws Exception {
		Customer customer = (Customer) session.getAttribute("customer");
		if (customer == null || customer.getId() != customerId)
			customer = serviceCustomer.get(customerId);
			if (customer == null) {
				String msg = messageSource.getMessage("errors.customer.notexist", null, "Customer does not exist",locale);
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
	@RequestMapping("KnowLedgeBase/Customer/Add")
	public String addCustomer(Map<String, Object> model) {
		model.put("customer", new Customer());
		return "customer/addCustomer";
	}

	/** 
	 * 
	 * Perform add new customer
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Customer/Create")
	public String createCustomer(@ModelAttribute("customer") @Valid Customer customer,
			BindingResult result) throws Exception {
		this.serviceCustomer.save(customer);
		return "redirect:../Customer/Display";
	}

	/** 
	 * 
	 * Request edit single customer
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Customer/Edit/{customerId}")
	public String editCustomer(@PathVariable("customerId") Integer customerId, HttpSession session, Map<String, Object> model, RedirectAttributes redirectAttributes, 
			Locale locale) throws Exception {
		Customer customer = (Customer) session.getAttribute("customer");
		if (customer == null || customer.getId() != customerId)
			customer = serviceCustomer.get(customerId);
			if (customer == null) {
				String msg = messageSource.getMessage("errors.customer.notexist", null, "Customer does not exist",locale);
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
	@RequestMapping("KnowLedgeBase/Customer/Update/{customerId}")
	public String updateCustomer(@PathVariable("customerId") Integer customerId, @ModelAttribute("customer") @Valid Customer customer, BindingResult result,
		   RedirectAttributes redirectAttributes,Locale locale) throws Exception {
		if (customer == null || customer.getId() != customerId) {
			String msg = messageSource.getMessage("errors.customer.update.notrecognized", null, "Customer not recognized",locale);
			redirectAttributes.addFlashAttribute("errors", msg);
		} else {
			try {
				serviceCustomer.saveOrUpdate(customer);
				String msg = messageSource.getMessage("success.customer.update.success", null, "Customer had been updated!",locale);
				redirectAttributes.addFlashAttribute("success", msg);
			} catch (Exception e) {
				String msg = messageSource.getMessage("errors.customer.update.fail", null, "Customer update failed!",locale);
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
	@RequestMapping("KnowLedgeBase/Customer/Delete/{customerId}")
	public String deleteCustomer(@PathVariable("customerId") Integer customerId) throws Exception {
		serviceCustomer.remove(customerId);
		return "redirect:../Display";
	}
	
	public void setServiceCustomer(ServiceCustomer serviceCustomer){
		this.serviceCustomer = serviceCustomer;
	}
}