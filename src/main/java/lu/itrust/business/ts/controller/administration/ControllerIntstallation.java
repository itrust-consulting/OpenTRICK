package lu.itrust.business.ts.controller.administration;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.ts.asynchronousWorkers.Worker;
import lu.itrust.business.ts.asynchronousWorkers.WorkerTSInstallation;
import lu.itrust.business.ts.component.DefaultTemplateLoader;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceCustomer;
import lu.itrust.business.ts.database.service.ServiceStorage;
import lu.itrust.business.ts.database.service.ServiceTaskFeedback;
import lu.itrust.business.ts.database.service.WorkersPoolManager;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.general.Customer;

/**
 * ControllerIntstallation.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Apr 23, 2014
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_ADMIN)
public class ControllerIntstallation {

	@Autowired
	private ServiceCustomer serviceCustomer;

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

	private ServiceStorage serviceStorage;

	@Autowired
	private DefaultTemplateLoader defaultReportTemplateLoader;

	@Value("${app.settings.version}")
	private String version;

    @Value("${app.settings.default.profile.mixed.en.sqlite.path}")
	private String defaultProfileMixedEnSqlitePath;
   
	@Value("${app.settings.default.profile.mixed.fr.sqlite.path}")
	private String defaultProfileMixedFrSqlitePath;

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
	@RequestMapping(value = "/Install", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	public @ResponseBody Map<String, String> installTS(Model model, Principal principal, Locale locale) throws Exception {
		final Map<String, String> errors = new LinkedHashMap<>();
		installProfileCustomer(errors, locale);
		if (!errors.isEmpty())
			return errors;
		final List<String> fileNames = new LinkedList<>();
		fileNames.add(defaultProfileMixedEnSqlitePath);
		fileNames.add(defaultProfileMixedFrSqlitePath);
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
			return defaultReportTemplateLoader.getDefaultCustomer();
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
			defaultReportTemplateLoader.loadLanguages();
			defaultReportTemplateLoader.load();
			// owner
			if (principal == null) {
				System.out.println("Could not determine owner! Canceling default Profile creation...");
				errors.put("error", messageSource.getMessage("error.analysis.owner.no_found", null, "Could not determine owner!", locale));
				return false;
			}
			final Worker worker = new WorkerTSInstallation(version, workersPoolManager, sessionFactory, serviceTaskFeedback, serviceStorage, fileNames, customer.getId(), principal.getName());
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
			errors.put("error", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			return false;
		}
	}
}