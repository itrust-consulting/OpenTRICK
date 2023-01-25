package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.AccountLockerManager;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceEmailSender;
import lu.itrust.business.TS.database.service.ServiceResetPassword;
import lu.itrust.business.TS.database.service.ServiceRole;
import lu.itrust.business.TS.database.service.ServiceTSSetting;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.exception.ResourceNotFoundException;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.TSSettingName;
import lu.itrust.business.TS.usermanagement.ChangePasswordhelper;
import lu.itrust.business.TS.usermanagement.ResetPassword;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.usermanagement.helper.AccountLocker;
import lu.itrust.business.TS.usermanagement.helper.ResetPasswordHelper;
import lu.itrust.business.TS.validator.UserValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

/**
 * ControllerAdministration.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version
 * @since Dec 13, 2013
 */
@Controller
public class ControllerRegister {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceRole serviceRole;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private ServiceEmailSender serviceEmailSender;

	@Autowired
	private ServiceResetPassword serviceResetPassword;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${app.settings.time.to.valid.reset.password}")
	private int timeoutValue;

	@Value("${app.settings.time.attempt.timeout}")
	private int attemptTimeout;

	@Value("${app.settings.hostserver}")
	private String hostServer;

	@Value("${app.settings.max.attempt}")
	private int maxAttempt;

	@Autowired
	private ServiceTSSetting serviceTSSetting;

	@Autowired
	private AccountLockerManager accountLockerManager;

	/**
	 * add: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/Register")
	@PreAuthorize("@permissionEvaluator.isAllowed(T(lu.itrust.business.TS.model.general.TSSettingName).SETTING_ALLOWED_SIGNUP,true)")
	public String add(Map<String, Object> model) {
		model.put("user", new User());
		return "default/register";
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param user
	 * @param result
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/DoRegister", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PostAuthorize("@permissionEvaluator.isAllowed(T(lu.itrust.business.TS.model.general.TSSettingName).SETTING_ALLOWED_SIGNUP,true)")
	public @ResponseBody Map<String, String> save(@RequestBody String source, RedirectAttributes attributes,
			Locale locale, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, String> errors = new LinkedHashMap<>();
		try {
			User user = new User();
			if (!buildUser(errors, user, source, locale))
				return errors;
			// check if users exist and give first user admin role
			Role role = null;
			if (serviceUser.noUsers()) {
				// check if admin role exists and create it if not
				role = serviceRole.getByName(RoleType.ROLE_ADMIN.name());
				if (role == null) {
					role = new Role(RoleType.ROLE_ADMIN);
					serviceRole.save(role);
				}
			} else {
				// check if user role exists and create it if not
				role = serviceRole.getByName(RoleType.ROLE_USER.name());
				if (role == null) {
					role = new Role(RoleType.ROLE_USER);
					serviceRole.save(role);
				}
			}

			user.getRoles().clear();

			// set role of new user
			user.addRole(role);

			List<User> admins = serviceUser.getAllAdministrators();

			try {

				serviceEmailSender.send(admins, user);

				this.serviceUser.save(user);

			} catch (Exception e) {
				// save user
				TrickLogManager.Persist(e);

				errors.put("general", messageSource.getMessage("error.user.save", null,
						"Error during account creation, please try again later...", locale));
			}

			return errors;
		} catch (ConstraintViolationException | DataIntegrityViolationException e) {
			errors.put("constraint",
					messageSource.getMessage("error.user.constraint", null,
							"A username already exists with this email! Choose another username or email!", locale));
			errors.put("login",
					messageSource.getMessage("error.user.username.used_change", null, "Change the username", locale));
			errors.put("email",
					messageSource.getMessage("error.user.email.used_change", null, "Change the email", locale));
			return errors;
		}
	}

	public void checkAttempt(String name, HttpServletRequest request, Principal principal) {
		if (principal != null || !serviceTSSetting.isAllowed(TSSettingName.SETTING_ALLOWED_RESET_PASSWORD, true))
			throw new ResourceNotFoundException();
		final String ip = AccountLockerManager.getIP(request);
		if (accountLockerManager.isLocked(name, ip)) {
			final AccountLocker locker = accountLockerManager.lock(name, ip);
			final String serviceName = name.replace("service-attempt-", "");
			final Timestamp lockTime = new Timestamp(locker.getLockTime());
			final String expireDate = DateFormat.getDateInstance(DateFormat.FULL, request.getLocale()).format(lockTime);
			final String expireDateTime = DateFormat.getTimeInstance(DateFormat.MEDIUM, request.getLocale())
					.format(lockTime);

			throw new TrickException(String.format("error.attempt.%s", serviceName),
					String.format("Too many attempts to access to %s from %s, please try again after %s at %s",
							serviceName,
							ip,
							expireDate, expireDateTime),
					serviceName, ip, expireDate, expireDateTime);
		} else
			accountLockerManager.lock(name, ip);
	}

	private void clearAttempt(String name, HttpServletRequest request) {
		accountLockerManager.clean(name, AccountLockerManager.getIP(request));
	}

	@RequestMapping("/ResetPassword")
	public String resetPassword(Principal principal, Model model, HttpServletRequest request) {
		model.addAttribute("resetPassword", new ResetPasswordHelper());
		return "default/recovery/reset-password";
	}

	public static String URL(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName()
				+ ("http".equals(request.getScheme()) && request.getServerPort() == 80
						|| "https".equals(request.getScheme()) && request.getServerPort() == 443 ? ""
								: ":" + request.getServerPort())
				+ request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
	}

	@RequestMapping("/ResetPassword/Save")
	public String resetPassword(@ModelAttribute("resetPassword") ResetPasswordHelper resetPassword,
			BindingResult result, Principal principal, RedirectAttributes attributes,
			Locale locale, HttpServletRequest request) {
		if (resetPassword.isEmpty()) {
			result.reject("error.reset.password.field.empty", "Please enter your username or your eamil address");
			return "default/recovery/reset-password";
		}

		try {
			checkAttempt("service-attempt-reset-password", request, principal);
			String ipAdress = request.getHeader("X-FORWARDED-FOR");
			if (ipAdress == null)
				ipAdress = request.getRemoteAddr();
			User user = StringUtils.hasText(resetPassword.getUsername()) ? serviceUser.get(resetPassword.getUsername())
					: serviceUser.getByEmail(resetPassword.getEmail());
			if (!(user == null || user.getConnexionType() == User.LADP_CONNEXION)) {
				ResetPassword resetPassword2 = serviceResetPassword.get(user);
				if (resetPassword2 != null)
					serviceResetPassword.delete(resetPassword2);
				resetPassword2 = new ResetPassword(user,
						Sha512DigestUtils.shaHex(String.valueOf(System.nanoTime())
								+ String.valueOf(new Random(System.currentTimeMillis()).nextDouble())),
						new Timestamp(System.currentTimeMillis() + timeoutValue));
				serviceResetPassword.saveOrUpdate(resetPassword2);
				serviceEmailSender.send(resetPassword2,
						hostServer + "/ChangePassword/" + resetPassword2.getKeyControl());
				/**
				 * Log
				 */
				TrickLogManager.Persist(LogLevel.INFO, LogType.AUTHENTICATION, "log.request.reset.password",
						String.format("from: %s", ipAdress), user.getLogin(),
						LogAction.REQUEST_TO_RESET_PASSWORD, ipAdress);
			} else
				// Log
				TrickLogManager.Persist(LogLevel.ERROR, LogType.AUTHENTICATION, "log.bad.request.rest.password",
						String.format("Target: %s, from: %s", resetPassword.getData(), ipAdress), "anonymous",
						LogAction.REQUEST_TO_RESET_PASSWORD, resetPassword.getData(),
						ipAdress);

			attributes.addFlashAttribute("success",
					messageSource.getMessage("success.reset.password.email.send", null,
							"You will receive an email to reset your password, you have one hour to do.", locale));

		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			attributes.addFlashAttribute("error",
					messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);

			attributes.addFlashAttribute("error",
					messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
		return "redirect:/Login";
	}

	@RequestMapping("/ChangePassword/{keyControl}")
	public String updatePassword(@PathVariable String keyControl, Principal principal, Model model,
			RedirectAttributes attributes, Locale locale, HttpServletRequest request) {
		try {
			checkAttempt("service-attempt-change-password", request, principal);
			ResetPassword resetPassword = serviceResetPassword.get(keyControl);
			if (resetPassword == null)
				throw new ResourceNotFoundException();
			clearAttempt("service-attempt-change-password", request);
			if (resetPassword.getUser().getConnexionType() == User.LADP_CONNEXION) {
				attributes.addFlashAttribute("error",
						messageSource.getMessage("error.ldap.change.password", null,
								"To reset your password, please contact your administrator", locale));
				return "redirect:/Login";
			} else if (resetPassword.getLimitTime().getTime() < System.currentTimeMillis()) {
				attributes.addFlashAttribute("error",
						messageSource.getMessage("error.reset.password.request.expired", null,
								"Your request has been expired", locale));
				return "redirect:/Login";
			}

			model.addAttribute("changePassword", new ChangePasswordhelper(keyControl));
			return "default/recovery/change-password";
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			attributes.addFlashAttribute("error",
					messageSource.getMessage(e.getCode(), e.getParameters(),
							e.getMessage(), locale));
			return "redirect:/Login";
		}

	}

	@RequestMapping("/ChangePassword/{keyControl}/Cancel")
	public String cancel(@PathVariable String keyControl, Principal principal, Model model,
			RedirectAttributes attributes, Locale locale, HttpServletRequest request) {
		ResetPassword resetPassword = serviceResetPassword.get(keyControl);
		if (resetPassword != null)
			serviceResetPassword.delete(resetPassword);
		attributes.addFlashAttribute("success",
				messageSource.getMessage("success.reset.password.canceled", null,
						"Request to reset your password has been successfully canceled", locale));
		return "redirect:/Login";

	}

	@RequestMapping("/ChangePassword/Save")
	public String updatePassword(@ModelAttribute("changePassword") ChangePasswordhelper changePassword,
			BindingResult result, Principal principal, Model model,
			RedirectAttributes attributes, Locale locale, HttpServletRequest request) {
		ValidationUtils.rejectIfEmptyOrWhitespace(result, "password", "error.user.password.empty",
				"Password cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(result, "repeatPassword", "error.user.password.empty",
				"Password cannot be empty");
		if (!result.hasFieldErrors("password")
				&& !changePassword.getRepeatPassword().matches(Constant.REGEXP_VALID_PASSWORD))
			result.rejectValue("password", "error.user.password.invalid",
					"Password does not match policy (12 characters, at least one digit, one lower and one uppercase)");
		if (!result.hasFieldErrors("repeatPassword")
				&& !changePassword.getRepeatPassword().equals(changePassword.getPassword()))
			result.rejectValue("repeatPassword", "error.user.repeatPassword.not_same", "Passwords are not the same");
		if (result.hasErrors())
			return "default/recovery/change-password";

		try {

			checkAttempt("service-attempt-change-password", request, principal);
			ResetPassword resetPassword = serviceResetPassword.get(changePassword.getRequestId());
			if (resetPassword == null)
				throw new ResourceNotFoundException();
			clearAttempt("service-attempt-change-password", request);
			if (resetPassword.getLimitTime().getTime() < System.currentTimeMillis()) {
				attributes.addFlashAttribute("error",
						messageSource.getMessage("error.reset.password.request.expired", null,
								"Your request has been expired", locale));
				return "redirect:/Login";
			}

			String username = resetPassword.getUser().getLogin();
			resetPassword.getUser().setPassword(passwordEncoder.encode(changePassword.getPassword()));
			serviceUser.saveOrUpdate(resetPassword.getUser());
			serviceResetPassword.delete(resetPassword);
			attributes.addFlashAttribute("success", messageSource.getMessage("success.change.password", null,
					"Your password was successfully changed", locale));
			/**
			 * Log
			 */
			String ipAdress = request.getHeader("X-FORWARDED-FOR");
			if (ipAdress == null)
				ipAdress = request.getRemoteAddr();
			TrickLogManager.Persist(LogLevel.INFO, LogType.AUTHENTICATION, "log.reset.password",
					String.format("from: %s", ipAdress), username, LogAction.RESET_PASSWORD, ipAdress);
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			attributes.addFlashAttribute("error",
					messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			attributes.addFlashAttribute("error",
					messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}

		return "redirect:/Login";
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
	private boolean buildUser(Map<String, String> errors, User user, String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			ValidatorField validator = serviceDataValidation.findByClass(User.class);
			if (validator == null)
				serviceDataValidation.register(validator = new UserValidator());
			String login = jsonNode.get("login").asText();
			String password = jsonNode.get("password").asText();
			String repeatedPassword = jsonNode.get("repeatPassword").asText();
			String firstname = jsonNode.get("firstName").asText();
			String lastname = jsonNode.get("lastName").asText();
			String email = jsonNode.get("email").asText();
			String userlocale = jsonNode.get("locale").asText();
			String error = null;

			error = validator.validate(user, "login", login);
			if (error != null)
				errors.put("login", serviceDataValidation.ParseError(error, messageSource, locale));
			else if (serviceUser.existByUsername(login))
				errors.put("login",
						messageSource.getMessage("error.username.in_use", null, "Username is in use", locale));
			else
				user.setLogin(login);
			error = validator.validate(user, "password", password);
			if (error != null)
				errors.put("password", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				user.setPassword(password);

			error = validator.validate(user, "repeatPassword", repeatedPassword);
			if (error != null)
				errors.put("repeatPassword", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				user.setPassword(passwordEncoder.encode(user.getPassword()));

			error = validator.validate(user, "firstName", firstname);
			if (error != null)
				errors.put("firstName", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				user.setFirstName(firstname);

			error = validator.validate(user, "lastName", lastname);
			if (error != null)
				errors.put("lastName", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				user.setLastName(lastname);

			error = validator.validate(user, "email", email);
			if (error != null)
				errors.put("email", serviceDataValidation.ParseError(error, messageSource, locale));
			else if (serviceUser.existByEmail(email))
				errors.put("email", messageSource.getMessage("error.email.in_use", null, "Email is in use", locale));
			else
				user.setEmail(email);

			error = validator.validate(user, "locale", userlocale);
			if (error != null)
				errors.put("locale", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				user.setLocale(userlocale);

		} catch (Exception e) {
			errors.put("user", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}

		return errors.isEmpty();

	}
}