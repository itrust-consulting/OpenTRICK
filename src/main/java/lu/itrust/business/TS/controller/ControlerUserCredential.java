/**
 * 
 */
package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.ACCEPT_TEXT_PLAIN_CHARSET_UTF_8;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

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

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.controller.form.UserCredentialForm;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserCredential;
import lu.itrust.business.TS.exception.ResourceNotFoundException;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.model.general.CredentialType;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.TicketingSystem;
import lu.itrust.business.TS.model.ticketing.builder.Client;
import lu.itrust.business.TS.model.ticketing.builder.ClientBuilder;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.usermanagement.UserCredential;

/**
 * @author eomar
 *
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@PostAuthorize("@permissionEvaluator.isAllowed(T(lu.itrust.business.TS.model.general.TSSettingName).SETTING_ALLOWED_TICKETING_SYSTEM_LINK)")
@RequestMapping("/Account/Credential")
@Controller
public class ControlerUserCredential {

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceUserCredential serviceUserCredential;

	@PostMapping(value = "/Save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
						messageSource.getMessage(form.getType() == CredentialType.TOKEN ? "error.credential.token.empty" : "error.credential.password.empty", null, locale));
			if (!(StringUtils.hasText(form.getName()) || form.getType() == CredentialType.TOKEN))
				result.put("name", messageSource.getMessage("error.credential.username.empty", null, locale));
		}

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
			serviceUser.saveOrUpdate(user);
			result.put("success", messageSource.getMessage("success.save.credential", null, locale));
		}
		return result;
	}

	@GetMapping(value = "/Form", consumes = MediaType.TEXT_PLAIN_VALUE, headers = ACCEPT_TEXT_PLAIN_CHARSET_UTF_8, produces = MediaType.TEXT_HTML_VALUE)
	public String add(Model model, Principal principal, Locale locale) {
		final User user = serviceUser.get(principal.getName());
		final UserCredentialForm form = model.containsAttribute("form") ? (UserCredentialForm) model.asMap().get("form") : new UserCredentialForm();
		model.addAttribute("customers",
				user.getCustomers().stream()
						.filter(c -> c.getTicketingSystem() != null
								&& (form.getCustomer() == c.getId() || (form.getCustomer() < 1 && !user.getCredentials().containsKey(c.getTicketingSystem())))
								&& c.getTicketingSystem().isEnabled())
						.sorted((c1, c2) -> NaturalOrderComparator.compareTo(c1.getOrganisation(), c2.getOrganisation())).collect(Collectors.toList()));
		model.addAttribute("types", CredentialType.values());
		model.addAttribute("form", form);
		return "user/credential/form";
	}

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
		model.addAttribute("form", form);
		return add(model, principal, locale);
	}

	@DeleteMapping(value = "/{id}/Delete", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody boolean delete(@PathVariable long id, Principal principal, Locale locale) {
		final User user = serviceUser.get(principal.getName());
		boolean result = user.getCredentials().entrySet().removeIf(e -> e.getKey().isEnabled() && e.getValue().getId() == id);
		if (result)
			serviceUser.saveOrUpdate(user);
		return result;
	}

	@DeleteMapping(value = "/Delete", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody Map<Long, Boolean> delete(@RequestBody List<Long> ids, Principal principal, Locale locale) {
		final User user = serviceUser.get(principal.getName());
		final Map<Long, Boolean> results = new HashMap<Long, Boolean>();
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

	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String sectionCredential(HttpSession session, Principal principal, Model model) {
		model.addAttribute("credentials", serviceUserCredential.findByUsername(principal.getName()));
		return "user/credential/section";
	}

	private Boolean isConnected(UserCredentialForm credential, TicketingSystem ticketingSystem) {
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
			return (client = ClientBuilder.Build(ticketingSystem.getType().name().toLowerCase())).connect(settings);
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

}
