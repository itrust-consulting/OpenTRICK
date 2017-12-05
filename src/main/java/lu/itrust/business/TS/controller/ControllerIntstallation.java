package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerTSInstallation;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;

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
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("#{'${app.settings.default.languages}'.split(';')}")
	private List<String> defaultLanguages;

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

	@Value("${app.settings.default.profile.quantitative.sqlite.path}")
	private String defaultProfileQuantitativeSqlitePath;

	@Value("${app.settings.default.profile.qualitative.sqlite.path}")
	private String defaultProfileQualitativeSqlitePath;

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
	@RequestMapping(value = "/Install", method = RequestMethod.POST)
	public @ResponseBody Map<String, String> installTS(Model model, Principal principal, HttpServletRequest request, Locale locale) throws Exception {

		Map<String, String> errors = new LinkedHashMap<String, String>();

		installProfileCustomer(errors, locale);

		if (!errors.isEmpty())
			return errors;

		List<String> fileNames = new LinkedList<>();

		fileNames.add(request.getServletContext().getRealPath(defaultProfileQuantitativeSqlitePath));
		fileNames.add(request.getServletContext().getRealPath(defaultProfileQualitativeSqlitePath));

		installDefaultProfile(fileNames, principal, errors, locale);

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
			TrickLogManager.Persist(e);
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
	private boolean installDefaultProfile(List<String> fileNames, Principal principal, Map<String, String> errors, Locale locale) {

		try {

			// customer
			Customer customer = serviceCustomer.getProfile();

			if (customer == null) {
				customer = installProfileCustomer(errors, locale);
				if (customer == null) {
					System.out.println("Customer could not be installed!");
					errors.put("error", messageSource.getMessage("error.customer_profile.no_found", null, "Could not find profile customer!", locale));
					return false;
				}
			}

			installDefaultLanguage();

			// owner
			if (principal == null) {
				System.out.println("Could not determine owner! Canceling default Profile creation...");
				errors.put("error", messageSource.getMessage("error.analysis.owner.no_found", null, "Could not determine owner!", locale));
				return false;
			}
			Worker worker = new WorkerTSInstallation(version, workersPoolManager, sessionFactory, serviceTaskFeedback, fileNames, customer.getId(), principal.getName());
			if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale)) {
				errors.put("error", messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
				return false;
			}
			executor.execute(worker);
			errors.put("idTask", String.valueOf(worker.getId()));
			return true;

		} catch (TrickException e) {
			errors.put("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			return false;
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			errors.put("error", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			return false;
		}
	}

	private void installDefaultLanguage() {
		defaultLanguages.forEach(value -> {
			String[] values = value.split(",");
			if (values.length == 3 && !serviceLanguage.existsByAlpha3(values[0])) {
				serviceLanguage.saveOrUpdate(new Language(values[0], values[1], values[2]));
			}
		});
	}
}