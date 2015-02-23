package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.data.general.helper.FilterControl;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceRole;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserSqLite;
import lu.itrust.business.TS.database.service.ServiceWordReport;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.validator.UserValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	private static final String FILTER_CONTROL_SQLITE = "SQLITE";

	private static final String FILTER_CONTROL_REPORT = "REPORT";

	private static final String FILTER_CONTROL_SORT_KEY = "%s_SORT";

	private static final String FILTER_CONTROL_SORT_DIRCTION_KEY = "%s_SORT_DIRECTION";

	private static final String FILTER_CONTROL_SIZE_KEY = "%s_SIZE";

	private static final String FILTER_CONTROL_FILTER_KEY = "%s_FILTER";

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceRole serviceRole;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceUserSqLite serviceUserSqLite;

	@Autowired
	private ServiceWordReport serviceWordReport;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

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
		user.setPassword(Constant.EMPTY_STRING);
		// add profile to model
		model.addAttribute("user", user);
		model.addAttribute("sqliteIdentifiers", serviceUserSqLite.getDistinctIdentifierByUser(user));
		model.addAttribute("reportIdentifiers", serviceWordReport.getDistinctIdentifierByUser(user));
		FilterControl filterControl = buildFromUser(user, FILTER_CONTROL_SQLITE);
		session.setAttribute("sqliteControl", filterControl);
		filterControl = buildFromUser(user, FILTER_CONTROL_REPORT);
		session.setAttribute("reportControl", filterControl);
		return "user/home";
	}

	private FilterControl buildFromUser(User user, String type) {
		String sort = user.getSetting(String.format(FILTER_CONTROL_SORT_KEY, type)), direction = user.getSetting(String.format(FILTER_CONTROL_SORT_DIRCTION_KEY, type)), filter = user
				.getSetting(String.format(FILTER_CONTROL_FILTER_KEY, type));
		Integer size = user.getInteger(String.format(FILTER_CONTROL_SIZE_KEY, type));
		if (size == null)
			size = 30;
		if (sort == null)
			sort = "identifier";
		if (filter == null)
			filter = "ALL";
		if(direction == null)
			direction = "asc";
		return new FilterControl(sort, direction, size, filter);
	}

	@RequestMapping(value = "/Section/Sqlite", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public String sectionSqlite(@RequestParam(defaultValue = "1") Integer page, HttpSession session, Principal principal, Model model) throws Exception {
		FilterControl filter = (FilterControl) session.getAttribute("sqliteControl");
		if (filter == null)
			filter = new FilterControl();
		model.addAttribute("sqlites", serviceUserSqLite.getAllFromUserByFilterControl(principal.getName(), page, filter));
		return "user/sqlites";
	}

	@RequestMapping(value = "/Section/Report", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public String sectionReport(@RequestParam(defaultValue = "1") Integer page, HttpSession session, Principal principal, Model model) {
		FilterControl filter = (FilterControl) session.getAttribute("sqliteControl");
		if (filter == null)
			filter = new FilterControl();
		model.addAttribute("reports", serviceWordReport.getAllFromUserByFilterControl(principal.getName(), page, filter));
		return "user/reports";

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
	@RequestMapping(value = "/Update", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
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

			errors.put("user", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
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

			String currentPassword = jsonNode.get("currentPassword").asText();
			String password = jsonNode.get("password").asText();
			String repeatedPassword = jsonNode.get("repeatPassword").asText();
			String firstname = jsonNode.get("firstName").asText();
			String lastname = jsonNode.get("lastName").asText();
			String email = jsonNode.get("email").asText();
			String userlocale = jsonNode.get("locale").asText();
			String error = null;
			String oldPassword = user.getPassword();

			if (currentPassword != Constant.EMPTY_STRING) {

				if (!oldPassword.equals(passwordEncoder.encodePassword(currentPassword, user.getLogin())))
					errors.put("currentPassword", messageSource.getMessage("error.user.current_password.not_matching", null, "Current Password is not correct", locale));

				if (password != Constant.EMPTY_STRING) {

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
				else
					user.setEmail(email);

				error = validator.validate(user, "locale", userlocale);
				if (error != null)
					errors.put("locale", serviceDataValidation.ParseError(error, messageSource, locale));
				else
					user.setLocale(userlocale);

			} else
				errors.put("currentPassword", messageSource.getMessage("error.user.currentpassword_empty", null, "Enter current password for changes to take effect!", locale));

		} catch (Exception e) {
			errors.put("user", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
		}

		return errors.isEmpty();

	}

}
