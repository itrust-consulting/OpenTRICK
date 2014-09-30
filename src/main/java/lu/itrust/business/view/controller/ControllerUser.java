package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import lu.itrust.business.TS.settings.ApplicationSetting;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.service.ServiceRole;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.validator.UserValidator;
import lu.itrust.business.validator.field.ValidatorField;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ControllerUser.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Apr 15, 2014
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Profile")
@Controller
public class ControllerUser {

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceRole serviceRole;

	@Autowired
	private MessageSource messageSource;

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
	public String profile(Model model, Principal principal) {

		try {
		
			User user = serviceUser.get(principal.getName());
	
			user.setPassword(Constant.EMPTY_STRING);
	
			// add profile to model
			model.addAttribute("user", user);
			
			model.addAttribute("defaultlang", user.getApplicationSettingsAsMap().get(Constant.SETTING_DEFAULT_UI_LANGUAGE).getValue());
			
			model.addAttribute("defaultShowUncertainty", user.getApplicationSettingsAsMap().get(Constant.SETTING_DEFAULT_SHOW_UNCERTAINTY).getValue());
			
			model.addAttribute("defaultShowCssf", user.getApplicationSettingsAsMap().get(Constant.SETTING_DEFAULT_SHOW_UNCERTAINTY).getValue());
		
			return "userProfile";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", null);
			return "userProfile";
		}
		
		
		
	}

	/**
	 * profileOfUser: <br>
	 * Description
	 * 
	 * @param userId
	 * @param session
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping("/{userId}")
	public String profileOfUser(@PathVariable("userId") int userId, Model model) throws Exception {

		try {
			
			User user = serviceUser.get(userId);
	
			user.setPassword(Constant.EMPTY_STRING);
	
			// add profile to model
			model.addAttribute("user", user);
			
			model.addAttribute("defaultlang", user.getApplicationSettingsAsMap().get(Constant.SETTING_DEFAULT_UI_LANGUAGE).getValue());
			
			model.addAttribute("defaultlang", user.getApplicationSettingsAsMap().get(Constant.SETTING_DEFAULT_SHOW_UNCERTAINTY).getValue());
		
			return "userProfile";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", null);
			return "userProfile";
		}
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
	@RequestMapping(value = "/Update", method = RequestMethod.POST,headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody Map<String, String> save(@RequestBody String source, RedirectAttributes attributes, Locale locale, Principal principal, HttpServletResponse response) throws Exception {

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
			String defaultlanguage = jsonNode.get("defaultlanguage").asText();
			String defaultShowUncertainty = jsonNode.get("default_show_uncertainty").asText();
			String defaultShowCssf = jsonNode.get("default_show_cssf").asText();
			String error = null;
			String oldPassword = user.getPassword();
			
			if(currentPassword!=Constant.EMPTY_STRING) {
			
				if (!oldPassword.equals(passwordEncoder.encodePassword(currentPassword, user.getLogin())))
					errors.put("currentPassword", messageSource.getMessage("error.user.current_password.not_matching", null, "Current Password is not correct", locale));
				
				error = validator.validate(user, "password", password);
				if (error != null)
					errors.put("password", serviceDataValidation.ParseError(error, messageSource, locale));
				else 
					user.setPassword(password);
				
				error = validator.validate(user, "repeatPassword", repeatedPassword);
				if (error != null) {
					user.setPassword(oldPassword);
					errors.put("repeatPassword", serviceDataValidation.ParseError(error, messageSource, locale));
				}
				else {
					
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

			Map<String, ApplicationSetting> settings = user.getApplicationSettingsAsMap();
			
			boolean settingserrors = false;
			
			// default interface language
			
			ApplicationSetting setting = settings.get(Constant.SETTING_DEFAULT_UI_LANGUAGE);
			
			if(!defaultlanguage.equals("en") && !defaultlanguage.equals("fr")) {
				errors.put("defaultlanguage", messageSource.getMessage("error.user.defaultlanguage.not_recognised", null, "Language not recognised!", locale));
				settingserrors = true;
			} else {
				setting.setValue(defaultlanguage);
				settings.put(Constant.SETTING_DEFAULT_UI_LANGUAGE, setting);
			}
			
			// default show uncertainty
			
			setting = settings.get(Constant.SETTING_DEFAULT_SHOW_UNCERTAINTY);
			
			if(!defaultShowUncertainty.equals("true") && !defaultShowUncertainty.equals("false")) {
				errors.put("default_show_uncertainty", messageSource.getMessage("error.user.defaultshowuncertainty.not_allowed", null, "Only \"True\" and \"False\" are allowed!", locale));
				settingserrors = true;
			} else{
				setting.setValue(defaultShowUncertainty);
				settings.put(Constant.SETTING_DEFAULT_SHOW_UNCERTAINTY, setting);
			}
			
			// default show cssf
			
			setting = settings.get(Constant.SETTING_DEFAULT_SHOW_CSSF);
			
			if(!defaultShowCssf.equals("true") && !defaultShowCssf.equals("false")) {
				errors.put("default_show_cssf", messageSource.getMessage("error.user.defaultshowcssf.not_allowed", null, "Only \"True\" and \"False\" are allowed!", locale));
				settingserrors = true;
			} else{
				setting.setValue(defaultShowCssf);
				settings.put(Constant.SETTING_DEFAULT_SHOW_CSSF, setting);
			}
			
			if(!settingserrors) {
				List<ApplicationSetting> asettings = new ArrayList<ApplicationSetting>(settings.values());
				user.setApplicationSettings(asettings);
			}
			
		} catch (Exception e) {
			errors.put("user", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
		}


		return errors.isEmpty();

	}
	
}
