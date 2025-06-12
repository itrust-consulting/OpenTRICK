/**
 * 
 */
package lu.itrust.business.ts.controller.account;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.ts.constants.Constant.ACCEPT_TEXT_PLAIN_CHARSET_UTF_8;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceCustomer;
import lu.itrust.business.ts.database.service.ServiceUser;
import lu.itrust.business.ts.database.service.ServiceUserCredential;
import lu.itrust.business.ts.exception.ResourceNotFoundException;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.form.UserCredentialForm;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.general.CredentialType;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.TicketingSystem;
import lu.itrust.business.ts.model.general.helper.Utils;
import lu.itrust.business.ts.model.ticketing.builder.Client;
import lu.itrust.business.ts.model.ticketing.builder.ClientBuilder;
import lu.itrust.business.ts.usermanagement.User;
import lu.itrust.business.ts.usermanagement.UserCredential;


/**
 * This class is a controller for managing user credentials. It handles HTTP requests related to user credentials, such as saving, editing, and deleting credentials.
 * The controller is responsible for validating the input data, interacting with the database, and returning appropriate responses.
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@PostAuthorize("@permissionEvaluator.isAllowed(T(lu.itrust.business.ts.model.general.TSSettingName).SETTING_ALLOWED_TICKETING_SYSTEM_LINK)")
@RequestMapping("/Account/Credential")
@Controller
public class ControlerCredential {

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceUserCredential serviceUserCredential;

	/**
	 * Saves the user credential information.
	 * 
	 * @param form      The user credential form.
	 * @param principal The principal object representing the currently authenticated user.
	 * @param locale    The locale object representing the user's preferred language and region.
	 * @return An object containing the result of the save operation.
	 */
	@PostMapping(value = "/Save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object save(@RequestBody UserCredentialForm form, Principal principal, Locale locale) {
		final Customer customer = serviceCustomer.getFromUsernameAndId(principal.getName(), form.getCustomer());
		if (customer == null || customer.getTicketingSystem() == null || !customer.getTicketingSystem().isEnabled())
			throw new ResourceNotFoundException();
		final Map<String, String> result = new HashMap<>();
		final User user = serviceUser.get(principal.getName());
		UserCredential credential = user.getCredentials().get(customer.getTicketingSystem());
		if (credential == null) {
			user.getCredentials().put(customer.getTicketingSystem(), credential = new UserCredential());
			credential.setTicketingSystem(customer.getTicketingSystem());
		}

		if (form.getType() == null)
			result.put("type", messageSource.getMessage("error.credential.type.empty", null, locale));
		else {
			if (!StringUtils.hasText(form.getValue()))
				result.put("value",
						messageSource.getMessage(form.getType() == CredentialType.TOKEN ? "error.credential.token.empty"
								: "error.credential.password.empty", null, locale));
			if (!(StringUtils.hasText(form.getName()) || form.getType() == CredentialType.TOKEN))
				result.put("name", messageSource.getMessage("error.credential.username.empty", null, locale));
		}

		if (Utils.hasText(form.getPublicUrl()) && !Utils.isValidURL(form.getPublicUrl()))
			result.put("publicUrl", messageSource.getMessage("error.credential.public.url.invalid", null, locale));

		if (result.isEmpty() && !isConnected(form, customer.getTicketingSystem())) {
			if (form.getType() == CredentialType.PASSWORD) {
				result.put("name", messageSource.getMessage("error.credential.username", null, locale));
				result.put("value", messageSource.getMessage("error.credential.password", null, locale));
			} else
				result.put("value", messageSource.getMessage("error.credential.token", null, locale));
		}

		if (result.isEmpty()) {
			credential.setType(form.getType());
			credential.setName(form.getType() == CredentialType.TOKEN ? null : form.getName());
			credential.setValue(form.getValue());
			credential.setPublicUrl(form.getPublicUrl());
			serviceUser.saveOrUpdate(user);
			result.put("success", messageSource.getMessage("success.save.credential", null, locale));
		}
		return result;
	}

	/**
	 * Retrieves the HTML form for adding a new credential.
	 *
	 * @param model     the model object for the view
	 * @param principal the currently authenticated user
	 * @param locale    the locale of the user
	 * @return the HTML form for adding a new credential
	 */
	@GetMapping(value = "/Form", consumes = MediaType.TEXT_PLAIN_VALUE, headers = ACCEPT_TEXT_PLAIN_CHARSET_UTF_8, produces = MediaType.TEXT_HTML_VALUE)
	public String add(Model model, Principal principal, Locale locale) {
		final User user = serviceUser.get(principal.getName());
		final UserCredentialForm form = model.containsAttribute("form") ? (UserCredentialForm) model.asMap().get("form")
				: new UserCredentialForm();
		model.addAttribute("customers",
				user.getCustomers().stream()
						.filter(c -> c.getTicketingSystem() != null
								&& (form.getCustomer() == c.getId() || (form.getCustomer() < 1
										&& !user.getCredentials().containsKey(c.getTicketingSystem())))
								&& c.getTicketingSystem().isEnabled())
						.sorted((c1, c2) -> NaturalOrderComparator.compareTo(c1.getOrganisation(),
								c2.getOrganisation()))
						.collect(Collectors.toList()));
		model.addAttribute("types", CredentialType.values());
		model.addAttribute("form", form);
		return "jsp/user/credential/form";
	}

	/**
	 * Retrieves the HTML representation of the edit form for a specific account.
	 *
	 * @param id        The ID of the account to edit.
	 * @param model     The model object to populate with data.
	 * @param principal The currently authenticated user.
	 * @param locale    The locale of the user.
	 * @return The HTML representation of the edit form.
	 */
	@GetMapping(value = "/{id}/Edit", consumes = MediaType.TEXT_PLAIN_VALUE, headers = ACCEPT_TEXT_PLAIN_CHARSET_UTF_8, produces = MediaType.TEXT_HTML_VALUE)
	public String edit(@PathVariable long id, Model model, Principal principal, Locale locale) {
		final UserCredential credential = serviceUserCredential.findByIdAndUsername(id, principal.getName());
		if (credential == null || !credential.getTicketingSystem().isEnabled())
			throw new ResourceNotFoundException();
		final UserCredentialForm form = new UserCredentialForm();
		form.setCustomer(credential.getTicketingSystem().getCustomer().getId());
		form.setType(credential.getType());
		form.setName(credential.getName());
		if (credential.getType() == CredentialType.TOKEN)
			form.setValue(credential.getValue());
		if (StringUtils.hasText(credential.getPublicUrl()))
			form.setPublicUrl(credential.getPublicUrl());

		model.addAttribute("form", form);
		return add(model, principal, locale);
	}

	/**
	 * Deletes a credential with the specified ID.
	 *
	 * @param id the ID of the credential to delete
	 * @param principal the principal object representing the currently authenticated user
	 * @param locale the locale of the request
	 * @return true if the credential was successfully deleted, false otherwise
	 */
	@DeleteMapping(value = "/{id}/Delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody boolean delete(@PathVariable long id, Principal principal, Locale locale) {
		final User user = serviceUser.get(principal.getName());
		boolean result = user.getCredentials().entrySet()
				.removeIf(e -> e.getKey().isEnabled() && e.getValue().getId() == id);
		if (result)
			serviceUser.saveOrUpdate(user);
		return result;
	}

	/**
	 * Deletes the credentials with the specified IDs.
	 *
	 * @param ids      The list of IDs of the credentials to be deleted.
	 * @param principal The principal object representing the authenticated user.
	 * @param locale   The locale of the request.
	 * @return A map containing the IDs of the deleted credentials and a boolean value indicating the success of each deletion.
	 */
	@DeleteMapping(value = "/Delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<Long, Boolean> delete(@RequestBody List<Long> ids, Principal principal, Locale locale) {
		final User user = serviceUser.get(principal.getName());
		final Map<Long, Boolean> results = new HashMap<>();
		user.getCredentials().entrySet().removeIf(e -> {
			if (e.getKey().isEnabled() && !ids.contains(e.getValue().getId()))
				return false;
			results.put(e.getValue().getId(), true);
			return true;
		});
		if (!results.isEmpty())
			serviceUser.saveOrUpdate(user);
		return results;
	}

	/**
	 * Retrieves the section credential.
	 *
	 * @param session   the HttpSession object
	 * @param principal the Principal object representing the currently authenticated user
	 * @param model     the Model object used to pass data to the view
	 * @return the name of the view to render, in this case "jsp/user/credential/section"
	 */
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String sectionCredential(HttpSession session, Principal principal, Model model) {
		model.addAttribute("credentials", serviceUserCredential.findByUsername(principal.getName()));
		return "jsp/user/credential/section";
	}

	/**
	 * Checks if the user is connected to the ticketing system using the provided credentials.
	 *
	 * @param credential The user credential form containing the username, password, and token.
	 * @param ticketingSystem The ticketing system to connect to.
	 * @return true if the user is connected, false otherwise.
	 */
	private boolean isConnected(UserCredentialForm credential, TicketingSystem ticketingSystem) {
		Client client = null;
		try {
			if (credential == null || ticketingSystem == null)
				throw new TrickException("error.load.setting", "Setting cannot be loaded");
			final Map<String, Object> settings = new HashMap<>(3);
			if (credential.getType() == CredentialType.TOKEN)
				settings.put("token", credential.getValue());
			else {
				settings.put("username", credential.getName());
				settings.put("password", credential.getValue());
			}
			settings.put("url", ticketingSystem.getUrl());
			client = ClientBuilder.Build(ticketingSystem.getType().name().toLowerCase());
			return client.connect(settings);
		} catch (Exception e) {
			TrickLogManager.persist(e);
			return false;
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					TrickLogManager.persist(e);
				}
			}
		}
	}

}
