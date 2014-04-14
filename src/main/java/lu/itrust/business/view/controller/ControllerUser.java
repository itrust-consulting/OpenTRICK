/**
 * 
 */
package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.service.ServiceRole;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.validator.UserValidator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author oensuifudine
 * 
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

	/**
	 * initBinder: <br>
	 * Description
	 * 
	 * @param binder
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.replaceValidators(new UserValidator());
	}

	/**
	 * getServiceUser: <br>
	 * Description
	 * 
	 * @return
	 */
	public ServiceUser getServiceUser() {
		return serviceUser;
	}

	/**
	 * setServiceUser: <br>
	 * Description
	 * 
	 * @param serviceUser
	 */
	public void setServiceUser(ServiceUser serviceUser) {
		this.serviceUser = serviceUser;
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
	public String profile(Principal principal, HttpSession session, Map<String, Object> model) throws Exception {

		// retrieve profile of the current user
		User user = serviceUser.get(principal.getName());

		// add profile to model
		model.put("userProfil", user);

		return "userProfile";
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
	public String profileOfUser(@PathVariable("userId") int userId, HttpSession session, Map<String, Object> model) throws Exception {

		// retireve profile
		User user = serviceUser.get(userId);

		// add profile to model
		model.put("userProfil", user);

		return "profilUser";
	}

	/**
	 * buildUser: <br>
	 * Description
	 * 
	 * @param errors
	 * @param customer
	 * @param source
	 * @param locale
	 * @return
	 */
	private User buildUser(List<String[]> errors, String source, Locale locale, Principal principal) {

		User user = null;

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			int id = jsonNode.get("id").asInt();
			if (id > 0) {
				user = serviceUser.get(jsonNode.get("id").asInt());
			} else {
				return null;
			}

				user.setPassword(jsonNode.get("password").asText());
				ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
				user.setPassword(passwordEncoder.encodePassword(user.getPassword(), user.getLogin()));
			
			user.setFirstName(jsonNode.get("firstName").asText());
			user.setLastName(jsonNode.get("lastName").asText());

			user.setEmail(jsonNode.get("email").asText());

			if (!principal.getName().equals(user.getLogin())) {

				user.disable();

				RoleType[] roletypes = RoleType.values();

				for (int i = 0; i < roletypes.length; i++) {
					Role role = serviceRole.findByName(roletypes[i].name());

					if (role == null) {
						role = new Role(roletypes[i]);
						serviceRole.save(role);
					}

					if (jsonNode.get(role.getType().name()).asText().equals(Constant.CHECKBOX_CONTROL_ON)) {
						user.addRole(role);
					}

				}

			}

			return user;

		} catch (Exception e) {

			errors.add(new String[] { "user", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
			return null;
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
	@RequestMapping(value = "/User/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody
	List<String[]> save(@RequestBody String value, Locale locale, Principal principal) throws Exception {

		List<String[]> errors = new LinkedList<>();
		
		try {

			User user = buildUser(errors, value, locale, principal);

			if (user == null) {
				return errors;
			} else {
				if (user.getId() < 1) {
					serviceUser.save(user);
				} else {
					serviceUser.saveOrUpdate(user);
				}
			}
		} catch (Exception e) {
			errors.add(new String[] { "user", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}

		return errors;

	}
	
}
