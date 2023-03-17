/**
 * 
 */
package lu.itrust.business.TS.controller.analysis;

import static lu.itrust.business.TS.constants.Constant.ROLE_MIN_USER;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceIDS;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
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
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private MessageSource messageSource;

	/**
	 * USER ACCESS
	 */

	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping("/Admin/IDS/Section")
	public String section(Model model) {
		model.addAttribute("IDSs", serviceIDS.getAll());
		return "jsp/admin/ids/home";
	}

	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping("/Admin/IDS/Add")
	public String add(Model model, IDS ids) {
		model.addAttribute("ids", ids);
		return "jsp/admin/ids/form";
	}

	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping("/Admin/IDS/Edit/{id}")
	public String edit(@PathVariable Integer id, Model model) {
		IDS ids = serviceIDS.get(id);
		if (ids == null)
			throw new AccessDeniedException("Access denied");
		model.addAttribute("ids", ids);
		return "jsp/admin/ids/form";
	}

	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping(value = "/Admin/IDS/Save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
				do {
					ids.setToken(Sha512DigestUtils.shaHex(UUID.randomUUID().toString() + ids.getPrefix() + System.nanoTime()));
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
				return JsonMessage.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping(value = "/Admin/IDS/Delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object delete(@RequestBody List<Integer> IDs, Principal principal, Locale locale) {
		try {
			IDs.forEach(id -> {
				IDS ids = serviceIDS.get(id);
				if (ids != null) {
					serviceIDS.delete(ids);
					TrickLogManager.Persist(LogLevel.WARNING, LogType.ADMINISTRATION, "log.delete.ids", String.format("Prefix: %s", ids.getLogin()), principal.getName(),
							LogAction.DELETE, ids.getLogin());
				}
			});
			return JsonMessage.Success(IDs.size() > 1 ? messageSource.getMessage("success.delete.multi.ids", null, "IDSs have been successfully deleted", locale)
					: messageSource.getMessage("success.delete.single.ids", null, "IDS has been successfully deleted", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			if (e instanceof TrickException)
				return JsonMessage.Error(messageSource.getMessage(((TrickException) e).getCode(), ((TrickException) e).getParameters(), e.getMessage(), locale));
			else
				return JsonMessage.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping(value = "/Admin/IDS/Renew/Token", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object renewToken(@RequestBody List<Integer> IDs, Principal principal, Locale locale) {
		try {

			Map<Integer, String> newToken = new LinkedHashMap<>(IDs.size());
			IDs.forEach(id -> {
				IDS ids = serviceIDS.get(id);
				if (ids != null) {
					do {
						ids.setToken(Sha512DigestUtils.shaHex(UUID.randomUUID().toString() + ids.getPrefix() + System.nanoTime()));
					} while (serviceIDS.exists(ids.getToken()));
					serviceIDS.saveOrUpdate(ids);
					TrickLogManager.Persist(LogLevel.WARNING, LogType.ADMINISTRATION, "log.update.ids.token",
							String.format("IDS token has been renewed, Target: %s", ids.getLogin()), principal.getName(), LogAction.RENEW, ids.getLogin());
					newToken.put(id, ids.getToken());
				}
			});
			return newToken;
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			if (e instanceof TrickException)
				return JsonMessage.Error(messageSource.getMessage(((TrickException) e).getCode(), ((TrickException) e).getParameters(), e.getMessage(), locale));
			else
				return JsonMessage.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping("/Admin/Manage/IDS/{id}")
	public String adminManage(@PathVariable Integer id, Model model, Principal principal, Locale locale) {
		return analysisManage(id, model, principal, locale);
	}

	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping(value = "/Admin/Manage/IDS/{id}/Update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String adminSaveManagement(@PathVariable Integer id, @RequestBody Map<Integer, Boolean> subscriptions, Principal principal, Locale locale) {
		return saveManagement(id, subscriptions, principal, locale);
	}

	/**
	 * USER ACCESS
	 */
	@RequestMapping("/Analysis/Manage/IDS/{id}")
	@PreAuthorize("@permissionEvaluator.userOrOwnerIsAuthorized(#id, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).ALL)")
	public String analysisManage(@PathVariable Integer id, Model model, Principal principal, Locale locale) {
		Analysis analysis = serviceAnalysis.get(id);
		if (analysis.getType() == AnalysisType.QUALITATIVE)
			return null;
		List<IDS> IDSs = serviceIDS.getByAnalysisId(id);
		Map<Integer, Boolean> subscriptionsStates = IDSs.stream().collect(Collectors.toMap(IDS::getId, ids -> true));
		IDSs.addAll(serviceIDS.getAllAnalysisNoSubscribe(id));
		model.addAttribute("IDSs", IDSs);
		model.addAttribute("analysis", analysis);
		model.addAttribute("subscriptionsStates", subscriptionsStates);
		return "jsp/analyses/all/forms/ids";
	}

	@RequestMapping(value = "/Analysis/Manage/IDS/{id}/Update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@permissionEvaluator.userOrOwnerIsAuthorized(#id, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).ALL)")
	public @ResponseBody String saveManagement(@PathVariable Integer id, @RequestBody Map<Integer, Boolean> subscriptions, Principal principal, Locale locale) {
		Analysis analysis = serviceAnalysis.get(id);
		if (analysis.getType() == AnalysisType.QUALITATIVE)
			return JsonMessage.Error(messageSource.getMessage("error.action.not_authorise", null, "Action does not authorised", locale));
		Map<Integer, IDS> analysisSubscriptions = serviceIDS.getByAnalysisId(id).stream().collect(Collectors.toMap(IDS::getId, Function.identity()));
		subscriptions.forEach((IDSId, status) -> {
			IDS ids = analysisSubscriptions.get(IDSId);
			if (ids == null) {
				if (status) {
					ids = serviceIDS.get(IDSId);
					if (ids != null) {
						ids.getSubscribers().add(analysis);
						serviceIDS.saveOrUpdate(ids);
						TrickLogManager.Persist(LogType.ANALYSIS, "log.subscribe.analysis.ids",
								String.format("Subscription to IDS: analysis: %s, version: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(), ids.getLogin()),
								principal.getName(), LogAction.GIVE_ACCESS, analysis.getIdentifier(), analysis.getVersion(), ids.getLogin());
					}
				}
			} else if (!status) {
				ids.getSubscribers().remove(analysis);
				serviceIDS.saveOrUpdate(ids);
				TrickLogManager.Persist(LogType.ANALYSIS, "log.unsubscribe.analysis.ids",
						String.format("Unsubscription from IDS: analysis: %s, version: %s, target: %s", analysis.getIdentifier(), analysis.getVersion(), ids.getLogin()),
						principal.getName(), LogAction.REMOVE_ACCESS, analysis.getIdentifier(), analysis.getVersion(), ids.getLogin());
			}
		});
		return JsonMessage.Success(messageSource.getMessage("success.update.analysis.subscription", null, "Analysis subscriptions have been successfully updated", locale));
	}
}
