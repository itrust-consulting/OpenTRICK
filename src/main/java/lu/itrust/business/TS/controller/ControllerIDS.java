/**
 * 
 */
package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ROLE_MIN_USER;

import java.security.Principal;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceIDS;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.usermanagement.IDS;
import lu.itrust.business.TS.validator.IDSValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

/**
 * @author eomar
 *
 */
@Controller
@PreAuthorize(ROLE_MIN_USER)
public class ControllerIDS {

	@Autowired
	private ServiceIDS serviceIDS;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private MessageSource messageSource;

	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping("/Admin/IDS/Section")
	public String section(Model model) {
		model.addAttribute("IDSs", serviceIDS.getAll());
		return "admin/ids/home";
	}

	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping("/Admin/IDS/form")
	public String add(Model model, IDS ids) {
		model.addAttribute("ids", ids);
		return "admin/ids/form";
	}

	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping("/Admin/IDS/edit/{id}")
	public String edit(@PathVariable Integer id, Model model) {
		IDS ids = serviceIDS.get(id);
		if (ids == null)
			throw new AccessDeniedException("Access denied");
		model.addAttribute("ids", ids);
		return "admin/ids/form";
	}

	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping(value = "/Admin/IDS/Save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody Object save(@ModelAttribute @Validated IDS ids, BindingResult result, Principal principal, Model model, Locale locale) {
		try {
			ValidatorField validator = serviceDataValidation.findByClass(IDS.class);
			if (validator == null)
				serviceDataValidation.register(validator = new IDSValidator());

			Map<String, String> errors = serviceDataValidation.validate(ids);
			if (!errors.isEmpty()) {
				errors.keySet().forEach(field -> errors.put(field, serviceDataValidation.ParseError(errors.get(field), messageSource, locale)));
				return errors;
			}
			if (ids.getId() > 0) {
				IDS persisted = serviceIDS.get(ids.getId());
				if (persisted == null)
					return JsonMessage.Error(messageSource.getMessage("error.ids.not_found", null, "IDS cannot be found", locale));
				if (!persisted.getPrefix().equals(ids.getPrefix()) && serviceIDS.existByPrefix(ids.getPrefix()))
					return JsonMessage.Field("prefix", messageSource.getMessage("error.ids.prefix.used", null, "Name is already in used", locale));
				persisted.setPrefix(ids.getPrefix());
				persisted.setDescription(ids.getDescription());
				persisted.setEnable(ids.isEnable());
				ids = persisted;
			} else {
				if (serviceIDS.existByPrefix(ids.getPrefix()))
					return JsonMessage.Field("prefix", messageSource.getMessage("error.ids.prefix.used", null, "Name is already in used", locale));
				ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
				do {
					ids.setToken(passwordEncoder.encodePassword(UUID.randomUUID().toString(), ids.getPrefix()));
				} while (serviceIDS.exists(ids.getToken()));
			}

			serviceIDS.saveOrUpdate(ids);

			if (ids.getSubscribers() == null)
				TrickLogManager.Persist(LogLevel.WARNING, LogType.ADMINISTRATION, "log.add.ids", String.format("Prefix: %s", ids.getLogin()), principal.getName(), LogAction.CREATE,
						ids.getLogin());
			else
				TrickLogManager.Persist(LogLevel.WARNING, LogType.ADMINISTRATION, "log.update.ids", String.format("Prefix: %s", ids.getLogin()), principal.getName(),
						LogAction.UPDATE, ids.getLogin());

			return JsonMessage.Success(messageSource.getMessage("success.save.ids", null, "IDS has been successfully saved", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			if (e instanceof TrickException)
				return JsonMessage.Error(messageSource.getMessage(((TrickException) e).getCode(), ((TrickException) e).getParameters(), e.getMessage(), locale));
			else
				return JsonMessage.Error(messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
		}

	}
}
