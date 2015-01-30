package lu.itrust.business.TS.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceEmailSender;
import lu.itrust.business.TS.database.service.ServiceRole;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.validator.UserValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	/**
	 * add: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/Register")
	public String add(Map<String, Object> model) {

		// create new user object and add it to model
		model.put("user", new User());

		return "register";
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
	@RequestMapping(value = "/DoRegister", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody Map<String, String> save(@RequestBody String source, RedirectAttributes attributes, Locale locale, HttpServletResponse response) throws Exception {
		
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
			
			serviceEmailSender.sendRegistrationMail(admins, user);
			
			this.serviceUser.save(user);
			
			} catch (Exception e) {
			// save user
				e.printStackTrace();
				
				errors.put("general", messageSource.getMessage("error.user.save", null, "Error during account creation, please try again later...", locale) );
			}
			
			
			
			
			return errors;
		}
		catch (ConstraintViolationException | DataIntegrityViolationException e) {
			
			errors.put("constraint", messageSource.getMessage("error.user.constraint", null, "A username already exists with this email! Choose another username or email!", locale));
			errors.put("login",messageSource.getMessage("error.user.username.used_change", null, "Change the username", locale) );
			errors.put("email",messageSource.getMessage("error.user.email.used_change", null, "Change the email", locale) );
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
			//System.out.println(source);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
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
				user.setPassword(passwordEncoder.encodePassword(user.getPassword(), user.getLogin()));
			
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
			
		} catch (Exception e) {
			errors.put("user", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
		}


		return errors.isEmpty();

	}
	
}