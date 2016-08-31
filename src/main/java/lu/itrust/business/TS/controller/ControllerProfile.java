package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.EMPTY_STRING;
import static lu.itrust.business.TS.constants.Constant.FILTER_CONTROL_FILTER_KEY;
import static lu.itrust.business.TS.constants.Constant.FILTER_CONTROL_REPORT;
import static lu.itrust.business.TS.constants.Constant.FILTER_CONTROL_SIZE_KEY;
import static lu.itrust.business.TS.constants.Constant.FILTER_CONTROL_SORT_DIRCTION_KEY;
import static lu.itrust.business.TS.constants.Constant.FILTER_CONTROL_SORT_KEY;
import static lu.itrust.business.TS.constants.Constant.FILTER_CONTROL_SQLITE;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceTSSetting;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.database.service.ServiceUserSqLite;
import lu.itrust.business.TS.database.service.ServiceWordReport;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.TSSetting;
import lu.itrust.business.TS.model.general.TSSettingName;
import lu.itrust.business.TS.model.general.ReportType;
import lu.itrust.business.TS.model.general.UserSQLite;
import lu.itrust.business.TS.model.general.WordReport;
import lu.itrust.business.TS.model.general.helper.FilterControl;
import lu.itrust.business.TS.model.general.helper.TrickFilter;
import lu.itrust.business.TS.model.ticketing.builder.Client;
import lu.itrust.business.TS.model.ticketing.builder.ClientBuilder;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.validator.UserValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

/**
 * ControllerProfile.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Apr 15, 2014
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Profile")
@Controller
public class ControllerProfile {

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceUserSqLite serviceUserSqLite;

	@Autowired
	private ServiceWordReport serviceWordReport;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	@Autowired
	private ServiceTSSetting serviceTSSetting;

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
		User user = serviceUser.get(principal.getName());
		if (user == null)
			return "redirect:/Logout";
		user.setPassword(EMPTY_STRING);
		// add profile to model
		model.addAttribute("user", user);
		model.addAttribute("allowedTicketing", serviceTSSetting.isAllowed(TSSettingName.SETTING_ALLOWED_TICKETING_SYSTEM_LINK));
		model.addAttribute("sqliteIdentifiers", serviceUserSqLite.getDistinctIdentifierByUser(user));
		model.addAttribute("reportIdentifiers", serviceWordReport.getDistinctIdentifierByUser(user));
		session.setAttribute("sqliteControl", buildFromUser(user, FILTER_CONTROL_SQLITE));
		session.setAttribute("reportControl", buildFromUser(user, FILTER_CONTROL_REPORT));

		return "user/home";
	}

	private TrickFilter buildFromUser(User user, String type) {
		String sort = user.getSetting(String.format(FILTER_CONTROL_SORT_KEY, type)), direction = user.getSetting(String.format(FILTER_CONTROL_SORT_DIRCTION_KEY, type)),
				filter = user.getSetting(String.format(FILTER_CONTROL_FILTER_KEY, type));
		Integer size = user.getInteger(String.format(FILTER_CONTROL_SIZE_KEY, type));

		if (size == null)
			size = 30;
		if (sort == null)
			sort = "identifier";
		if (filter == null)
			filter = "ALL";
		if (direction == null)
			direction = "asc";
		return new FilterControl(sort, direction, size, filter);
	}

	@RequestMapping(value = "/Control/Sqlite/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String updateSqliteControl(@RequestBody FilterControl filterControl, HttpSession session, Principal principal, Locale locale) throws Exception {

		if (!filterControl.validate())
			return JsonMessage.Error(messageSource.getMessage("error.invalid.data", null, "Invalid data", locale));
		User user = serviceUser.get(principal.getName());
		if (user == null)
			return JsonMessage.Error(messageSource.getMessage("error.authentication", null, "Authentication failed", locale));
		updateFilterControl(user, filterControl, FILTER_CONTROL_SQLITE);
		session.setAttribute("sqliteControl", filterControl);
		return JsonMessage.Success(messageSource.getMessage("success.filter.control.updated", null, "Filter has been successfully updated", locale));

	}

	@RequestMapping(value = "/Control/Report/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String updateReportControl(@RequestBody FilterControl filterControl, HttpSession session, Principal principal, Locale locale) throws Exception {

		if (!filterControl.validate())
			return JsonMessage.Error(messageSource.getMessage("error.invalid.data", null, "Invalid data", locale));
		User user = serviceUser.get(principal.getName());
		if (user == null)
			return JsonMessage.Error(messageSource.getMessage("error.authentication", null, "Authentication failed", locale));
		updateFilterControl(user, filterControl, FILTER_CONTROL_REPORT);
		session.setAttribute("reportControl", filterControl);
		return JsonMessage.Success(messageSource.getMessage("success.filter.control.updated", null, "Filter has been successfully updated", locale));

	}

	private void updateFilterControl(User user, FilterControl value, String type) throws Exception {
		user.setSetting(String.format(FILTER_CONTROL_SORT_KEY, type), value.getSort());
		user.setSetting(String.format(FILTER_CONTROL_SORT_DIRCTION_KEY, type), value.getDirection());
		user.setSetting(String.format(FILTER_CONTROL_FILTER_KEY, type), value.getFilter());
		user.setSetting(String.format(FILTER_CONTROL_SIZE_KEY, type), value.getSize());
		serviceUser.saveOrUpdate(user);
	}

	@RequestMapping(value = "/Section/Sqlite", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String sectionSqlite(@RequestParam(defaultValue = "1") Integer page, HttpSession session, Principal principal, Model model) throws Exception {
		FilterControl filter = (FilterControl) session.getAttribute("sqliteControl");
		if (filter == null)
			filter = new FilterControl();
		model.addAttribute("sqlites", serviceUserSqLite.getAllFromUserByFilterControl(principal.getName(), page, filter));
		return "user/sqlites";
	}

	@RequestMapping(value = "/Section/Report", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String sectionReport(@RequestParam(defaultValue = "1") Integer page, HttpSession session, Principal principal, Model model) {
		FilterControl filter = (FilterControl) session.getAttribute("reportControl");
		if (filter == null)
			filter = new FilterControl();
		model.addAttribute("reports", serviceWordReport.getAllFromUserByFilterControl(principal.getName(), page, filter));
		return "user/reports";

	}

	@RequestMapping(value = "/Sqlite/{id}/Delete", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String deleteSqlite(@PathVariable Integer id, Principal principal, Locale locale) throws Exception {
		UserSQLite userSQLite = serviceUserSqLite.getByIdAndUser(id, principal.getName());
		if (userSQLite == null)
			return JsonMessage.Error(messageSource.getMessage("error.resource.not.found", null, "Resource cannot be found", locale));
		serviceUserSqLite.delete(userSQLite);
		return JsonMessage.Success(messageSource.getMessage("success.resource.deleted", null, "Resource has been successfully deleted", locale));
	}

	@RequestMapping(value = "/Report/{id}/Delete", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody String deleteReport(@PathVariable Integer id, Principal principal, Locale locale) {
		WordReport report = serviceWordReport.getByIdAndUser(id, principal.getName());
		if (report == null)
			return JsonMessage.Error(messageSource.getMessage("error.resource.not.found", null, "Resource cannot be found", locale));
		serviceWordReport.delete(report);
		return JsonMessage.Success(messageSource.getMessage("success.resource.deleted", null, "Resource has been successfully deleted", locale));

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
	@RequestMapping("/Sqlite/{idFile}/Download")
	public String downloadSqlite(@PathVariable Integer idFile, Principal principal, HttpServletResponse response, Locale locale) throws Exception {

		// get user file by given file id and username
		UserSQLite userSqLite = serviceUserSqLite.getByIdAndUser(idFile, principal.getName());

		// if file could not be found retrun 404 error
		if (userSqLite == null)
			return "errors/404";

		Integer idAnalysis = serviceAnalysis.getIdFromIdentifierAndVersion(userSqLite.getIdentifier(), userSqLite.getVersion());
		if (idAnalysis == null || !serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.READ))
			throw new AccessDeniedException(messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));
		// set response contenttype to sqlite
		response.setContentType("sqlite");

		// retireve sqlite file name to set
		String identifierName = userSqLite.getIdentifier();

		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ (identifierName == null || identifierName.trim().isEmpty() ? "Analysis" : identifierName.trim().replaceAll(":|-|[ ]", "_")) + ".sqlite\"");

		// set sqlite file size as response size
		response.setContentLength((int) userSqLite.getSize());

		// return the sqlite file (as copy) to the response outputstream ( whihc
		// creates on the
		// client side the sqlite file)
		FileCopyUtils.copy(userSqLite.getSqLite(), response.getOutputStream());

		/**
		 * Log
		 */
		TrickLogManager.Persist(LogType.ANALYSIS, "log.analysis.store.data.download",
				String.format("Analysis: %s, version: %s, exported at: %s, type: data", userSqLite.getIdentifier(), userSqLite.getVersion(), userSqLite.getExportTime()),
				principal.getName(), LogAction.DOWNLOAD, userSqLite.getIdentifier(), userSqLite.getVersion(), String.valueOf(userSqLite.getExportTime()));
		// return
		return null;
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
	public String downloadReport(@PathVariable Integer id, Principal principal, HttpServletResponse response, Locale locale) throws Exception {

		// get user file by given file id and username
		WordReport wordReport = serviceWordReport.getByIdAndUser(id, principal.getName());

		// if file could not be found retrun 404 error
		if (wordReport == null)
			return "errors/404";

		Integer idAnalysis = serviceAnalysis.getIdFromIdentifierAndVersion(wordReport.getIdentifier(), wordReport.getVersion());

		if (idAnalysis == null || !serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.READ))
			throw new AccessDeniedException(messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));
		
		String extension = ReportType.getExtension(wordReport.getType());

		// set response contenttype to sqlite
		response.setContentType(extension);

		// set response header with location of the filename
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + String.format("%s_%s_V%s.%s", wordReport.getType(), wordReport.getLabel(), wordReport.getVersion(), extension) + "\"");

		// set sqlite file size as response size
		response.setContentLength((int) wordReport.getSize());

		// return the sqlite file (as copy) to the response outputstream ( whihc
		// creates on the
		// client side the sqlite file)
		FileCopyUtils.copy(wordReport.getFile(), response.getOutputStream());
		/**
		 * Log
		 */
		TrickLogManager.Persist(LogType.ANALYSIS, "log.analysis.store." + ReportType.getCodeName(wordReport.getType()) + ".download",
				String.format("Analysis: %s, version: %s, exported at: %s, type: %s", wordReport.getIdentifier(), wordReport.getVersion(), wordReport.getCreated(),
						ReportType.getDisplayName(wordReport.getType())),
				principal.getName(), LogAction.DOWNLOAD, wordReport.getIdentifier(), wordReport.getVersion(), String.valueOf(wordReport.getCreated()));

		// return
		return null;
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
	public @ResponseBody Map<String, String> save(@RequestBody String source, RedirectAttributes attributes, Locale locale, Principal principal, HttpServletResponse response)
			throws Exception {

		Map<String, String> errors = new LinkedHashMap<>();

		try {

			User user = serviceUser.get(principal.getName());

			if (!buildUser(errors, user, source, locale))
				return errors;

			serviceUser.saveOrUpdate(user);

			return errors;

		} catch (Exception e) {

			errors.put("user", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
			return errors;
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
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
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
			String error = null;
			String oldPassword = user.getPassword();
			if (user.getConnexionType() != User.LADP_CONNEXION) {
				if (StringUtils.isEmpty(currentPassword))
					errors.put("currentPassword", messageSource.getMessage("error.user.currentpassword_empty", null, "Enter current password for changes to take effect!", locale));
				else if (!oldPassword.equals(passwordEncoder.encodePassword(currentPassword, user.getLogin())))
					errors.put("currentPassword", messageSource.getMessage("error.user.current_password.not_matching", null, "Current Password is not correct", locale));
			}

			if (!errors.containsKey("currentPassword")) {
				if (!StringUtils.isEmpty(password)) {
					if (user.getConnexionType() == User.LADP_CONNEXION)
						errors.put("user", messageSource.getMessage("error.ldap.change.password", null, "Please contact your administrator to reset your password.", locale));
					else {
						error = validator.validate(user, "password", password);
						if (error != null)
							errors.put("password", serviceDataValidation.ParseError(error, messageSource, locale));
						else
							user.setPassword(password);

						error = validator.validate(user, "repeatPassword", repeatedPassword);
						if (error != null) {
							user.setPassword(oldPassword);
							errors.put("repeatPassword", serviceDataValidation.ParseError(error, messageSource, locale));
						} else {
							user.setPassword(passwordEncoder.encodePassword(user.getPassword(), user.getLogin()));
						}
					}
				}

				if (serviceTSSetting.isAllowed(TSSettingName.SETTING_ALLOWED_TICKETING_SYSTEM_LINK)) {
					String ticketingUsername = readStringValue(jsonNode, "ticketingUsername"), ticketingPassword = readStringValue(jsonNode, "ticketingPassword");
					if (StringUtils.isEmpty(ticketingUsername)) {
						user.getUserSettings().remove(Constant.USER_TICKETING_SYSTEM_USERNAME);
						user.getUserSettings().remove(Constant.USER_TICKETING_SYSTEM_PASSWORD);
					} else {
						user.setSetting(Constant.USER_TICKETING_SYSTEM_USERNAME, ticketingUsername);
						if (!StringUtils.isEmpty(ticketingPassword))
							user.setSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD, ticketingPassword);
						if (!isConnected(user)) {
							errors.put("ticketingUsername", messageSource.getMessage("error.bad.credential", null, "Please check your credentials", locale));
							errors.put("ticketingPassword", messageSource.getMessage("error.bad.credential", null, "Please check your credentials", locale));
							user.getUserSettings().remove(Constant.USER_TICKETING_SYSTEM_USERNAME);
							user.getUserSettings().remove(Constant.USER_TICKETING_SYSTEM_PASSWORD);
						}
					}
				}

				if (user.getConnexionType() == User.LADP_CONNEXION && !user.getEmail().equals(email))
					errors.put("email", messageSource.getMessage("error.ldap.change.email", null, "Please contact your administrator to update your email.", locale));
				else {
					error = validator.validate(user, "email", email);
					if (error != null)
						errors.put("email", serviceDataValidation.ParseError(error, messageSource, locale));
					else if (!user.getEmail().equals(email)) {
						if (serviceUser.existByEmail(email))
							errors.put("email", messageSource.getMessage("error.email.in_use", null, "Email is in use", locale));
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
			errors.put("user", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}

		return errors.isEmpty();

	}

	private Boolean isConnected(User user) {
		Client client = null;
		try {
			TSSetting urlSetting = serviceTSSetting.get(TSSettingName.TICKETING_SYSTEM_URL);
			TSSetting nameSetting = serviceTSSetting.get(TSSettingName.TICKETING_SYSTEM_NAME);
			if (urlSetting == null || nameSetting == null)
				throw new TrickException("error.load.setting", "Setting cannot be loaded");
			Map<String, Object> settings = new HashMap<>(3);
			settings.put("username", user.getSetting(Constant.USER_TICKETING_SYSTEM_USERNAME));
			settings.put("password", user.getSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD));
			settings.put("url", urlSetting.getValue());
			return (client = ClientBuilder.Build(nameSetting.getString())).connect(settings);
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return false;
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					TrickLogManager.Persist(e);
				}
			}
		}
	}

	private String readStringValue(JsonNode jsonNode, String fieldName) {
		return jsonNode.has(fieldName) ? jsonNode.get(fieldName).textValue() : null;
	}
}
