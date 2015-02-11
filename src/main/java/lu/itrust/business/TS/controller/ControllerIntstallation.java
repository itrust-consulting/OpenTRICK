package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerTSInstallation;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.general.Customer;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.ServiceTrickService;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.importation.ImportAnalysis;
import lu.itrust.business.TS.usermanagement.User;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
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

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;
	
	@Autowired
	private WorkersPoolManager workersPoolManager;
	
	@Autowired
	private TaskExecutor executor;

	@Value("${app.settings.version}")
	private String version;
	
	@Value("${app.settings.default.profile.sqlite.path}")
	private String defaultProfileSqlitePath;

	/**
	 * install: <br>
	 * Description
	 * 
	 * @param model
	 * @param principal
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Install")
	public String install(Model model, Principal principal, HttpServletRequest request) throws Exception {
		return "redirect:/RemoveDefaultProfile";
	}

	/**
	 * removeDefault: <br>
	 * Description
	 * 
	 * @param model
	 * @param principal
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/RemoveDefaultProfile")
	public String removeDefault(Model model, Principal principal, HttpServletRequest request) throws Exception {

		Analysis analysis = serviceAnalysis.getDefaultProfile();

		if (analysis != null)
			serviceAnalysis.delete(analysis);

		return "redirect:/InstallTS";
	}

	/**
	 * installTS: <br>
	 * Description
	 * 
	 * @param model
	 * @param principal
	 * @param request
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/InstallTS")
	public @ResponseBody Map<String, String> installTS(Model model, Principal principal, HttpServletRequest request, Locale locale) throws Exception {

		Map<String, String> errors = new LinkedHashMap<String, String>();
		
		String fileName = request.getServletContext().getRealPath(defaultProfileSqlitePath);

		installProfileCustomer(errors, locale);

		if(!errors.isEmpty())
			return errors;
		
		installDefaultProfile(fileName, principal, errors, locale);
		
		return errors;

	}

	/**
	 * installProfileCustomer: <br>
	 * Description
	 * 
	 * @param errors
	 * @param locale
	 * @return
	 */
	private Customer installProfileCustomer(Map<String, String> errors, Locale locale) {
		try {
			Customer customer = serviceCustomer.getProfile();
			if (customer == null) {
				customer = new Customer();
				customer.setOrganisation("Profile");
				customer.setContactPerson("Profile");
				customer.setEmail("profile@trickservice.lu");
				customer.setPhoneNumber("00000000");
				customer.setAddress("Profile");
				customer.setCity("Profile");
				customer.setZIPCode("Profile");
				customer.setCountry("Profile");
				customer.setCanBeUsed(false);
				serviceCustomer.save(customer);
			}
			return customer;
		} catch (TrickException e) {
			errors.put("installProfileCustomer", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			errors.put("installProfileCustomer", e.getMessage());
			return null;
		}
	}

	/**
	 * installDefaultProfile: <br>
	 * Description
	 * 
	 * @param fileName
	 * @param principal
	 * @param errors
	 * @param locale
	 * @return
	 */
	private boolean installDefaultProfile(String fileName, Principal principal, Map<String, String> errors, Locale locale) {

		Customer customer;

		User owner;

		Analysis analysis = null;

		try {

			// customer
			customer = serviceCustomer.getProfile();

			if (customer == null) {
				customer = installProfileCustomer(errors, locale);
				if (customer == null) {
					System.out.println("Customer could not be installed!");
					errors.put("error", messageSource.getMessage("error.customer_profile.no_found", null, "Could not find profile customer!", locale));
					return false;
				}
			}

			// owner
			owner = serviceUser.get(principal.getName());

			if (owner == null) {
				System.out.println("Could not determine owner! Canceling default Profile creation...");
				errors.put("error", messageSource.getMessage("error.analysis.owner.no_found", null, "Could not determine owner!", locale));
				return false;
			}

			// create analysis
			analysis = new Analysis(customer, owner);
			analysis.setProfile(true);
			analysis.setDefaultProfile(true);
			analysis.setLabel("SME: Small and Medium Entreprises (Default Profile from installer)");
			
			ImportAnalysis importAnalysis = new ImportAnalysis(analysis,serviceTaskFeedback, sessionFactory);
			Worker worker = new WorkerTSInstallation(version,importAnalysis, fileName);
			worker.setPoolManager(workersPoolManager);
			if(!serviceTaskFeedback.registerTask(principal.getName(), worker.getId())){
				errors.put("error", messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
				return false;
			}
			executor.execute(worker);
			errors.put("idTask", String.valueOf(worker.isWorking()));
			return true;

		} catch (TrickException e) {
			errors.put("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			errors.put("error", e.getMessage());
			return false;
		}
	}
}