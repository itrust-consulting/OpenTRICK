package lu.itrust.business.ts.controller.account;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.ts.constants.Constant.EMPTY_STRING;
import static lu.itrust.business.ts.constants.Constant.FILTER_CONTROL_FILTER_KEY;
import static lu.itrust.business.ts.constants.Constant.FILTER_CONTROL_INVITATION;
import static lu.itrust.business.ts.constants.Constant.FILTER_CONTROL_REPORT;
import static lu.itrust.business.ts.constants.Constant.FILTER_CONTROL_SIZE_KEY;
import static lu.itrust.business.ts.constants.Constant.FILTER_CONTROL_SORT_DIRCTION_KEY;
import static lu.itrust.business.ts.constants.Constant.FILTER_CONTROL_SORT_KEY;
import static lu.itrust.business.ts.constants.Constant.FILTER_CONTROL_SQLITE;

import java.net.URLEncoder;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceAnalysis;
import lu.itrust.business.ts.database.service.ServiceAnalysisShareInvitation;
import lu.itrust.business.ts.database.service.ServiceDataValidation;
import lu.itrust.business.ts.database.service.ServiceEmailSender;
import lu.itrust.business.ts.database.service.ServiceEmailValidatingRequest;
import lu.itrust.business.ts.database.service.ServiceTSSetting;
import lu.itrust.business.ts.database.service.ServiceUser;
import lu.itrust.business.ts.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.ts.database.service.ServiceUserSqLite;
import lu.itrust.business.ts.database.service.ServiceWordReport;
import lu.itrust.business.ts.exception.ResourceNotFoundException;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.analysis.helper.ManageAnalysisRight;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.ReportType;
import lu.itrust.business.ts.model.general.TSSettingName;
import lu.itrust.business.ts.model.general.document.impl.UserSQLite;
import lu.itrust.business.ts.model.general.document.impl.WordReport;
import lu.itrust.business.ts.model.general.helper.FilterControl;
import lu.itrust.business.ts.model.general.helper.InvitationFilter;
import lu.itrust.business.ts.model.general.helper.TrickFilter;
import lu.itrust.business.ts.model.general.helper.Utils;
import lu.itrust.business.ts.usermanagement.EmailValidatingRequest;
import lu.itrust.business.ts.usermanagement.RoleType;
import lu.itrust.business.ts.usermanagement.User;
import lu.itrust.business.ts.validator.UserValidator;
import lu.itrust.business.ts.validator.field.ValidatorField;
import net.glxn.qrgen.QRCode;

/**
 * ControllerProfile.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Apr 15, 2014
 */
//@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Account")
@Controller
public class ControllerAccount {

	@Value("${app.settings.otp.enable}")
	private boolean enabledOTP = true;

	@Value("${app.settings.otp.force}")
	private boolean forcedOTP = true;

	@Value("${app.settings.otp.name:TRICKService}")
	private String appName;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private ServiceTSSetting serviceTSSetting;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	@Autowired
	private ServiceUserSqLite serviceUserSqLite;

	@Autowired
	private ServiceWordReport serviceWordReport;

	@Autowired
	private ServiceEmailValidatingRequest serviceEmailValidatingRequest;

	@Autowired
	private ServiceAnalysisShareInvitation serviceAnalysisShareInvitation;

	@Autowired
	private ManageAnalysisRight manageAnalysisRight;

	@Autowired
	private ServiceEmailSender serviceEmailSender;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@RequestMapping(value = "/Report/{id}/Delete", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody String deleteReport(@PathVariable Long id, Principal principal, Locale locale) {
		WordReport report = serviceWordReport.getByIdAndUser(id, principal.getName());
		if (report == null)
			return JsonMessage.Error(
					messageSource.getMessage("error.resource.not.found", null, "Resource cannot be found", locale));
		serviceWordReport.delete(report);
		return JsonMessage.Success(messageSource.getMessage("success.resource.deleted", null,
				"Resource has been successfully deleted", locale));
	}

	@RequestMapping(value = "/Sqlite/{id}/Delete", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String deleteSqlite(@PathVariable Long id, Principal principal, Locale locale)
			throws Exception {
		UserSQLite userSQLite = serviceUserSqLite.getByIdAndUser(id, principal.getName());
		if (userSQLite == null)
			return JsonMessage.Error(
					messageSource.getMessage("error.resource.not.found", null, "Resource cannot be found", locale));
		serviceUserSqLite.delete(userSQLite);
		return JsonMessage.Success(messageSource.getMessage("success.resource.deleted", null,
				"Resource has been successfully deleted", locale));
	}

	@PostMapping(value = "/Invitation/{id}/Reject", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String rejectInvitation(@PathVariable Long id, Principal principal, Locale locale)
			throws Exception {
		String token = serviceAnalysisShareInvitation.findTokenByIdAndUsername(id, principal.getName());
		if (token == null)
			return JsonMessage.Error(messageSource.getMessage("error.invitation.not.found", null,
					"Invitation has already been accepted or cancelled!", locale));
		manageAnalysisRight.cancelInvitation(principal, token);
		return JsonMessage.Success(messageSource.getMessage("success.cancel.invitation", null,
				"Invitation has been successfully rejected!", locale));
	}

	@PostMapping(value = "/Invitation/{id}/Accept", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String acceptInvitation(@PathVariable Long id, Principal principal, Locale locale)
			throws Exception {
		try {
			String token = serviceAnalysisShareInvitation.findTokenByIdAndUsername(id, principal.getName());
			if (token == null)
				return JsonMessage.Error(messageSource.getMessage("error.invitation.not.found", null,
						"Invitation has already been accepted or cancelled!", locale));
			manageAnalysisRight.acceptInvitation(principal, token);
			return JsonMessage.Success(messageSource.getMessage("success.accept.invitation", null,
					"Access has been successfully granted", locale));
		} catch (TrickException e) {// already logged
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage
					.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	@GetMapping(value = "/Invitation/Count", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String anvitationCount(Principal principal, Locale locale) throws Exception {
		return JsonMessage.Field("count", serviceAnalysisShareInvitation.countByUsername(principal.getName()) + "");
	}

	/**
	 * download: <br>
	 * Description
	 * 
	 * @param id
	 * @param principal
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Report/{id}/Download")
	public String downloadReport(@PathVariable Long id, Principal principal, HttpServletResponse response,
			Locale locale) throws Exception {
		// get user file by given file id and username
		WordReport wordReport = serviceWordReport.getByIdAndUser(id, principal.getName());

		// if file could not be found retrun 404 error
		if (wordReport == null)
			return "jsp/errors/404";

		Integer idAnalysis = serviceAnalysis.getIdFromIdentifierAndVersion(wordReport.getIdentifier(),
				wordReport.getVersion());

		if (idAnalysis < 1
				|| !serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.READ))
			throw new AccessDeniedException(
					messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));

		final String filename = Utils.extractOrignalFilename(wordReport.getName());

		String extension = ReportType.getExtension(wordReport.getType(), filename);

		// set response contenttype to sqlite
		response.setContentType(extension);

		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

		// set sqlite file size as response size
		response.setContentLength((int) wordReport.getLength());

		// return the sqlite file (as copy) to the response outputstream ( whihc
		// creates on the
		// client side the sqlite file)
		FileCopyUtils.copy(wordReport.getData(), response.getOutputStream());
		/**
		 * Log
		 */
		TrickLogManager.Persist(LogType.ANALYSIS,
				"log.analysis.store." + ReportType.getCodeName(wordReport.getType()) + ".download",
				String.format("Analysis: %s, version: %s, exported at: %s, type: %s", wordReport.getIdentifier(),
						wordReport.getVersion(), wordReport.getCreated(),
						ReportType.getDisplayName(wordReport.getType())),
				principal.getName(), LogAction.DOWNLOAD, wordReport.getIdentifier(), wordReport.getVersion(),
				String.valueOf(wordReport.getCreated()));

		// return
		return null;
	}

	/**
	 * download: <br>
	 * Description
	 * 
	 * @param idFile
	 * @param principal
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Sqlite/{id}/Download")
	public String downloadSqlite(@PathVariable Long id, Principal principal, HttpServletResponse response,
			Locale locale) throws Exception {

		// get user file by given file id and username
		UserSQLite userSqLite = serviceUserSqLite.getByIdAndUser(id, principal.getName());

		// if file could not be found retrun 404 error
		if (userSqLite == null)
			return "jsp/errors/404";

		Integer idAnalysis = serviceAnalysis.getIdFromIdentifierAndVersion(userSqLite.getIdentifier(),
				userSqLite.getVersion());
		if (idAnalysis < 1
				|| !serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.READ))
			throw new AccessDeniedException(
					messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));
		// set response contenttype to sqlite
		response.setContentType("sqlite");

		// set response header with location of the filename
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + Utils.extractOrignalFilename(userSqLite.getName()) + "\"");

		// set sqlite file size as response size
		response.setContentLength((int) userSqLite.getLength());

		// return the sqlite file (as copy) to the response outputstream ( whihc
		// creates on the
		// client side the sqlite file)
		FileCopyUtils.copy(userSqLite.getData(), response.getOutputStream());

		/**
		 * Log
		 */
		TrickLogManager.Persist(LogType.ANALYSIS, "log.analysis.store.data.download",
				String.format("Analysis: %s, version: %s, exported at: %s, type: data", userSqLite.getIdentifier(),
						userSqLite.getVersion(), userSqLite.getCreated()),
				principal.getName(), LogAction.DOWNLOAD, userSqLite.getIdentifier(), userSqLite.getVersion(),
				String.valueOf(userSqLite.getCreated()));
		// return
		return null;
	}

	/**
	 * profile: <br>
	 * Description
	 * 
	 * @param principal
	 * @param session
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public String home(Model model, HttpSession session, Principal principal) throws Exception {
		if (principal == null)
			return "redirect:/Logout";
		final User user = serviceUser.get(principal.getName());
		if (user == null)
			return "redirect:/Logout";
		final boolean adminAllowedTicketing = serviceTSSetting
				.isAllowed(TSSettingName.SETTING_ALLOWED_TICKETING_SYSTEM_LINK);
		user.setPassword(EMPTY_STRING);
		// add profile to model
		model.addAttribute("user", user);
		model.addAttribute("enabledOTP", enabledOTP);
		model.addAttribute("forcedOTP", forcedOTP);
		model.addAttribute("roles", RoleType.ROLES);
		model.addAttribute(Constant.ADMIN_ALLOWED_TICKETING, adminAllowedTicketing);
		if (adminAllowedTicketing)
			model.addAttribute("credentials",
					user.getCredentials().values().stream().filter(e -> e.getTicketingSystem().isEnabled())
							.sorted((e1, e2) -> NaturalOrderComparator.compareTo(
									e1.getTicketingSystem().getCustomer().getOrganisation(),
									e2.getTicketingSystem().getCustomer().getOrganisation()))
							.collect(Collectors.toList()));
		model.addAttribute("sqliteIdentifiers", serviceUserSqLite.getDistinctIdentifierByUser(user));
		model.addAttribute("reportIdentifiers", serviceWordReport.getDistinctIdentifierByUser(user));
		model.addAttribute("invitationSortNames", InvitationFilter.SORTS());
		session.setAttribute("sqliteControl", buildFromUser(user, FILTER_CONTROL_SQLITE));
		session.setAttribute("reportControl", buildFromUser(user, FILTER_CONTROL_REPORT));
		session.setAttribute("invitationControl", buildFromUser(user, FILTER_CONTROL_INVITATION));
		if (enabledOTP) {
			String secret = user.getSecret();
			if ((user.isUsing2FA() || forcedOTP) && StringUtils.hasText(secret))
				model.addAttribute("qrcode", generateQRCode(user, secret));
		}
		return "jsp/user/home";
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
	@RequestMapping(value = "/OTP/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String saveOTP(@RequestBody Map<String, Boolean> settings, Model model, Locale locale, Principal principal) {
		if (!enabledOTP)
			throw new ResourceNotFoundException("error.page.not_found");
		User user = serviceUser.get(principal.getName());
		Boolean using2FA = settings.get("using2FA"), useApplication = settings.get("useApplication");
		if (using2FA == null)
			using2FA = false;
		if (useApplication == null || !(using2FA || forcedOTP))
			useApplication = false;
		if (!useApplication || !(using2FA || forcedOTP))
			user.setSecret(null);
		else {
			user.setSecret(Base32.random());
			model.addAttribute("qrcode", generateQRCode(user, user.getSecret()));
		}
		user.setUsing2FA(using2FA);
		serviceUser.saveOrUpdate(user);
		model.addAttribute("user", user);
		model.addAttribute("enabledOTP", enabledOTP);
		model.addAttribute("forcedOTP", forcedOTP);
		return "jsp/user/otp/section";

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
	@RequestMapping(value = "/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Map<String, String> save(@RequestBody String source, RedirectAttributes attributes,
			Locale locale, Principal principal, HttpServletResponse response) throws Exception {
		Map<String, String> errors = new LinkedHashMap<>();
		try {
			User user = serviceUser.get(principal.getName());
			if (!buildUser(errors, user, source, locale))
				return errors;
			serviceUser.saveOrUpdate(user);
			return errors;
		} catch (Exception e) {
			errors.put("user", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
			return errors;
		}
	}

	@RequestMapping(value = "/Section/Report", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String sectionReport(@RequestParam(defaultValue = "1") Integer page, HttpSession session,
			Principal principal, Model model) {
		FilterControl filter = (FilterControl) session.getAttribute("reportControl");
		if (filter == null)
			filter = new FilterControl();
		model.addAttribute("reports",
				serviceWordReport.getAllFromUserByFilterControl(principal.getName(), page, filter));
		return "jsp/user/report/section";
	}

	@RequestMapping(value = "/Section/Sqlite", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String sectionSqlite(@RequestParam(defaultValue = "1") Integer page, HttpSession session,
			Principal principal, Model model) throws Exception {
		FilterControl filter = (FilterControl) session.getAttribute("sqliteControl");
		if (filter == null)
			filter = new FilterControl();
		model.addAttribute("sqlites",
				serviceUserSqLite.getAllFromUserByFilterControl(principal.getName(), page, filter));
		return "jsp/user/sqlite/section";
	}

	@RequestMapping(value = "/Section/Invitation", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String sectionInvitation(@RequestParam(defaultValue = "1") Integer page, HttpSession session,
			Principal principal, Model model) throws Exception {
		InvitationFilter filter = (InvitationFilter) session.getAttribute("invitationControl");
		if (filter == null)
			filter = new InvitationFilter();
		model.addAttribute("invitations",
				serviceAnalysisShareInvitation.findAllByUsernameAndFilterControl(principal.getName(), page, filter));
		return "jsp/user/invitation/section";
	}

	@RequestMapping(value = "/Control/Report/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String updateReportControl(@RequestBody FilterControl filterControl, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		if (!filterControl.validate())
			return JsonMessage.Error(messageSource.getMessage("error.invalid.data", null, "Invalid data", locale));
		User user = serviceUser.get(principal.getName());
		if (user == null)
			return JsonMessage
					.Error(messageSource.getMessage("error.authentication", null, "Authentication failed", locale));
		updateFilterControl(user, filterControl, FILTER_CONTROL_REPORT);
		session.setAttribute("reportControl", filterControl);
		return JsonMessage.Success(messageSource.getMessage("success.filter.control.updated", null,
				"Filter has been successfully updated", locale));
	}

	@RequestMapping(value = "/Control/Sqlite/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String updateSqliteControl(@RequestBody FilterControl filterControl, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		if (!filterControl.validate())
			return JsonMessage.Error(messageSource.getMessage("error.invalid.data", null, "Invalid data", locale));
		User user = serviceUser.get(principal.getName());
		if (user == null)
			return JsonMessage
					.Error(messageSource.getMessage("error.authentication", null, "Authentication failed", locale));
		updateFilterControl(user, filterControl, FILTER_CONTROL_SQLITE);
		session.setAttribute("sqliteControl", filterControl);
		return JsonMessage.Success(messageSource.getMessage("success.filter.control.updated", null,
				"Filter has been successfully updated", locale));
	}

	@RequestMapping(value = "/Control/Invitation/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String updateInvitationControl(@RequestBody InvitationFilter filterControl,
			HttpSession session, Principal principal, Locale locale) throws Exception {
		if (!filterControl.validate())
			return JsonMessage.Error(messageSource.getMessage("error.invalid.data", null, "Invalid data", locale));
		User user = serviceUser.get(principal.getName());
		if (user == null)
			return JsonMessage
					.Error(messageSource.getMessage("error.authentication", null, "Authentication failed", locale));
		updateFilterControl(user, filterControl, FILTER_CONTROL_INVITATION);
		session.setAttribute("invitationControl", filterControl);
		return JsonMessage.Success(messageSource.getMessage("success.filter.control.updated", null,
				"Filter has been successfully updated", locale));
	}

	@PostMapping(value = "/Validate/Email", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String validateEmail(Principal principal, Locale locale) {
		final User user = serviceUser.get(principal.getName());
		final SecureRandom random = new SecureRandom();
		final String token = Sha512DigestUtils.shaHex(UUID.randomUUID().toString() + "-email-validation-"
				+ System.nanoTime() + user.getEmail() + random.nextLong() + principal.getName());
		final EmailValidatingRequest validatingRequest = new EmailValidatingRequest(user, token);
		serviceEmailValidatingRequest.deleteByUser(user);
		serviceEmailValidatingRequest.saveOrUpdate(validatingRequest);
		serviceEmailSender.send(validatingRequest);
		return JsonMessage.Success(messageSource.getMessage("success.send.email.validation", null, locale));

	}

	private TrickFilter buildFromUser(User user, String type) {
		String sort = user.getSetting(String.format(FILTER_CONTROL_SORT_KEY, type)),
				direction = user.getSetting(String.format(FILTER_CONTROL_SORT_DIRCTION_KEY, type)),
				filter = user.getSetting(String.format(FILTER_CONTROL_FILTER_KEY, type));
		Integer size = user.getInteger(String.format(FILTER_CONTROL_SIZE_KEY, type));
		try {
			if (size == null)
				size = 30;
			if (direction == null)
				direction = "asc";
			if (type.equals(FILTER_CONTROL_INVITATION)) {
				if (sort == null)
					sort = "analysis.identifier";
				return new InvitationFilter(sort, direction, size);
			} else {
				if (sort == null)
					sort = "identifier";
				if (filter == null)
					filter = "ALL";
				return new FilterControl(sort, direction, size, filter);
			}
		} catch (Exception e) {
			return type.equals(FILTER_CONTROL_INVITATION) ? new InvitationFilter() : new FilterControl();
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
	private boolean buildUser(Map<String, String> errors, User user, String source, Locale locale) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final JsonNode jsonNode = mapper.readTree(source);
			ValidatorField validator = serviceDataValidation.findByClass(User.class);
			if (validator == null)
				serviceDataValidation.register(validator = new UserValidator());
			String currentPassword = readStringValue(jsonNode, "currentPassword");
			String password = readStringValue(jsonNode, "password");
			String repeatedPassword = readStringValue(jsonNode, "repeatPassword");
			String firstname = readStringValue(jsonNode, "firstName");
			String lastname = readStringValue(jsonNode, "lastName");
			String email = readStringValue(jsonNode, "email");
			String userlocale = readStringValue(jsonNode, "locale");
			String oldPassword = user.getPassword();
			String error = null;
			if (user.getConnexionType() != User.LADP_CONNEXION) {
				if (!StringUtils.hasText(currentPassword))
					errors.put("currentPassword", messageSource.getMessage("error.user.currentpassword_empty", null,
							"Enter current password for changes to take effect!", locale));
				else if (!isMatch(oldPassword, currentPassword, user.getLogin()))
					errors.put("currentPassword", messageSource.getMessage("error.user.current_password.not_matching",
							null, "Current Password is not correct", locale));
			}

			if (!errors.containsKey("currentPassword")) {
				if (StringUtils.hasText(password)) {
					if (user.getConnexionType() == User.LADP_CONNEXION)
						errors.put("user", messageSource.getMessage("error.ldap.change.password", null,
								"Please contact your administrator to reset your password.", locale));
					else {
						error = validator.validate(user, "password", password);
						if (error != null)
							errors.put("password", serviceDataValidation.ParseError(error, messageSource, locale));
						else
							user.setPassword(password);

						error = validator.validate(user, "repeatPassword", repeatedPassword);
						if (error != null) {
							user.setPassword(oldPassword);
							errors.put("repeatPassword",
									serviceDataValidation.ParseError(error, messageSource, locale));
						} else {
							user.setPassword(passwordEncoder.encode(user.getPassword()));
						}
					}
				}

				if (user.getConnexionType() == User.LADP_CONNEXION && !user.getEmail().equals(email))
					errors.put("email", messageSource.getMessage("error.ldap.change.email", null,
							"Please contact your administrator to update your email.", locale));
				else {
					error = validator.validate(user, "email", email);
					if (error != null)
						errors.put("email", serviceDataValidation.ParseError(error, messageSource, locale));
					else if (!user.getEmail().equals(email)) {
						if (serviceUser.existByEmail(email))
							errors.put("email",
									messageSource.getMessage("error.email.in_use", null, "Email is in use", locale));
						else
							user.setEmail(email);
					}
				}

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

				error = validator.validate(user, "locale", userlocale);
				if (error != null)
					errors.put("locale", serviceDataValidation.ParseError(error, messageSource, locale));
				else
					user.setLocale(userlocale);
			}
		} catch (Exception e) {
			errors.put("user", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}

		return errors.isEmpty();

	}

	private boolean isMatch(String oldPassword, String currentPassword, String login) {
		return passwordEncoder.matches(currentPassword + (oldPassword.startsWith("{SHA-256}") ? "{" + login + "}" : ""),
				oldPassword);
	}

	private String generateQRCode(User user, String secret) {
		try {
			return Base64.encodeBase64String(QRCode
					.from(String.format("otpauth://totp/%s-%s?secret=%s", URLEncoder.encode(appName, "UTF-8"),
							URLEncoder.encode(user.getEmail(), "UTF-8"), secret))
					.withSize(131, 131).stream().toByteArray());
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return null;
		}
	}

	private String readStringValue(JsonNode jsonNode, String fieldName) {
		return jsonNode.has(fieldName) ? jsonNode.get(fieldName).textValue() : null;
	}

	private void updateFilterControl(User user, FilterControl value, String type) throws Exception {
		user.setSetting(String.format(FILTER_CONTROL_SORT_KEY, type), value.getSort());
		user.setSetting(String.format(FILTER_CONTROL_SORT_DIRCTION_KEY, type), value.getDirection());
		user.setSetting(String.format(FILTER_CONTROL_FILTER_KEY, type), value.getFilter());
		user.setSetting(String.format(FILTER_CONTROL_SIZE_KEY, type), value.getSize());
		serviceUser.saveOrUpdate(user);
	}
}
