package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.TrickService;
import lu.itrust.business.TS.dbhandler.DatabaseHandler;
import lu.itrust.business.TS.importation.ImportAnalysis;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceCustomer;
import lu.itrust.business.service.ServiceTrickService;
import lu.itrust.business.service.ServiceUser;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ControllerIntstallation.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since Apr 23, 2014
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_ADMIN)
public class ControllerIntstallation {

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceTrickService serviceTrickService;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private SessionFactory sessionFactory;

	@RequestMapping("/Install")
	public String install(Model model, Principal principal, HttpServletRequest request) throws Exception {
		return "redirect:/RemoveDefaultProfile";
	}

	@RequestMapping("/InstallTS")
	public @ResponseBody
	Map<String, String> installTS(Model model, Principal principal, HttpServletRequest request) throws Exception {

		Map<String, String> errors = new LinkedHashMap<String, String>();

		TrickService status = serviceTrickService.getStatus();

		if (status == null) {
			errors.put("status", "Call analysis status before installing TRICK Service!");
			return errors;
		}

		String fileName = request.getServletContext().getRealPath("/WEB-INF/data") + "/TL1.4_TRICKService_DefaultProfile_v1.1.sqlite";

		installProfileCustomer(errors);

		installDefaultProfile(fileName, principal, errors);

		return errors;

	}

	@RequestMapping("/RemoveDefaultProfile")
	public String removeDefault(Model model, Principal principal, HttpServletRequest request) throws Exception {

		Analysis analysis = serviceAnalysis.getDefaultProfile();

		if (analysis != null)
			serviceAnalysis.delete(analysis);

		return "redirect:/InstallTS";
	}

	private Customer installProfileCustomer(Map<String, String> errors) {
		try {

			Customer customer = serviceCustomer.getProfile();

			if (customer == null) {
				customer = new Customer();
				customer.setOrganisation("Profile");
				customer.setContactPerson("Profile");
				customer.setEmail("profile@trickservice.lu");
				customer.setTelephoneNumber("0123456");
				customer.setAddress("Profile");
				customer.setCity("Profile");
				customer.setZIPCode("Profile");
				customer.setCountry("Profile");
				customer.setCanBeUsed(false);
				serviceCustomer.save(customer);
			}

			return customer;
		} catch (Exception e) {
			e.printStackTrace();

			errors.put("installProfileCustomer", e.getMessage());
			return null;
		}
	}

	private boolean installDefaultProfile(String fileName, Principal principal, Map<String, String> errors) {

		Customer customer;

		User owner;

		Analysis analysis = null;

		DatabaseHandler sqlitehandler = null;

		try {

			// customer
			customer = serviceCustomer.getProfile();

			if (customer == null) {
				customer = installProfileCustomer(errors);
				if (customer == null) {
					System.out.println("Customer could not be installed!");
					errors.put("installDefaultProfile - Customer", "Could not find profile customer!");
					return false;
				}
			}

			// owner
			owner = serviceUser.get(principal.getName());

			if (owner == null) {
				System.out.println("Could not determine owner! Canceling default Profile creation...");
				errors.put("installDefaultProfile - Owner", "Could not determine owner!");
				return false;
			}

			// create analysis
			analysis = new Analysis();
			analysis.setCustomer(customer);
			analysis.setOwner(owner);
			analysis.setProfile(true);
			analysis.setDefaultProfile(true);

			sqlitehandler = new DatabaseHandler(fileName);

			// import default values
			ImportAnalysis importAnalysis = new ImportAnalysis(analysis, sqlitehandler);
			importAnalysis.setSessionFactory(sessionFactory);

			boolean returnvalue = importAnalysis.simpleAnalysisImport();

			importAnalysis.getAnalysis().setLabel("Default Profile from installer");
			importAnalysis.getAnalysis().setIdentifier("SME");

			serviceAnalysis.saveOrUpdate(importAnalysis.getAnalysis());

			return returnvalue;

		} catch (Exception e) {
			e.printStackTrace();
			errors.put("installDefaultProfile - Create Profile", e.getMessage());
			return false;
		}
	}
}