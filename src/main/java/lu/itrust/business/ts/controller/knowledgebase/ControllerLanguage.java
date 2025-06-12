package lu.itrust.business.ts.controller.knowledgebase;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceDataValidation;
import lu.itrust.business.ts.database.service.ServiceLanguage;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.validator.LanguageValidator;
import lu.itrust.business.ts.validator.field.ValidatorField;

/**
 * ControllerLanguage.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à r.l
 * @version
 * @since Oct 11, 2013
 */
@PreAuthorize(Constant.ROLE_MIN_CONSULTANT)
@Controller
@RequestMapping("/KnowledgeBase/Language")
public class ControllerLanguage {


	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	/**
	 * 
	 * Display all Language
	 * 
	 */
	@RequestMapping
	public String loadAllLanguages(Map<String, Object> model) throws Exception {
		model.put("languages", serviceLanguage.getAll());
		return "jsp/knowledgebase/language/languages";
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String section(Model model) throws Exception {
		model.addAttribute("languages", serviceLanguage.getAll());
		return "jsp/knowledgebase/language/languages";
	}

	/**
	 * 
	 * Display single Language
	 * 
	 */
	@RequestMapping("/{languageId}")
	public String loadSingleLanguage(@PathVariable("languageId") Integer languageId, HttpSession session, Map<String, Object> model, RedirectAttributes redirectAttributes,
			Locale locale) throws Exception {
		Language language = (Language) session.getAttribute("language");
		if (language == null || language.getId() != languageId)
			language = serviceLanguage.get(languageId);
		if (language == null) {
			String msg = messageSource.getMessage("error.language.not_exist", null, "Language does not exist", locale);
			redirectAttributes.addFlashAttribute("error", msg);
			return "redirect:/KnowLedgeBase/Language";
		}
		model.put("language", language);
		return "jsp/knowledgebase/language/showLanguage";
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param value
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Map<String, String> save(@RequestBody String value, Principal principal, Locale locale) {
		Map<String, String> errors = new LinkedHashMap<String, String>();
		try {
			Language language = new Language();
			if (!buildLanguage(errors, language, value, locale))
				return errors;
			serviceLanguage.saveOrUpdate(language);
			/**
			 * Log
			 */
			TrickLogManager.persist(LogType.ANALYSIS, "log.language.add_or_update", String.format("Language: %s", language.getAlpha3()), principal.getName(),
					LogAction.CREATE_OR_UPDATE, language.getAlpha3());

		} catch (Exception e) {
			errors.put("language",  messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.persist(e);
		}
		return errors;
	}

	/**
	 * 
	 * Delete single language
	 * 
	 */
	@RequestMapping(value = "/Delete/{languageId}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String deleteLanguage(@PathVariable("languageId") Integer languageId, Principal principal, Locale locale) {
		try {
			Language language = serviceLanguage.get(languageId);
			if (language == null)
				return JsonMessage.Error(messageSource.getMessage("error.language.not_exist", null, "Language does not exist", locale));
			if (serviceLanguage.isUsed(language))
				return JsonMessage.Error(messageSource.getMessage("error.language.in_use", null, "Language cannot be deleted, it is still used", locale));
			serviceLanguage.delete(language);
			/**
			 * Log
			 */
			TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.language.delete", String.format("Language: %s", language.getAlpha3()), principal.getName(),
					LogAction.DELETE, language.getAlpha3());
			return JsonMessage.Success(messageSource.getMessage("success.language.delete.successfully", null, "Language was deleted successfully", locale));
		} catch (Exception e) {
			TrickLogManager.persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.language.in_use", null, "Language is still used.", locale));
		}
	}

	/**
	 * buildLanguage: <br>
	 * Description
	 * 
	 * @param errors
	 * @param language
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildLanguage(Map<String, String> errors, Language language, String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			int id = jsonNode.get("id").asInt();

			ValidatorField validator = serviceDataValidation.findByClass(Language.class);
			if (validator == null)
				serviceDataValidation.register(validator = new LanguageValidator());

			String alpha3 = jsonNode.get("alpha3").asText();
			String name = jsonNode.get("name").asText();
			String altName = jsonNode.get("altName").asText();
			String error = validator.validate(language, "alpha3", alpha3);
			if (error != null)
				errors.put("alpha3", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				language.setAlpha3(alpha3);

			error = validator.validate(language, "name", name);
			if (error != null)
				errors.put("name", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				language.setName(name);

			error = validator.validate(language, "altName", altName);
			if (error != null)
				errors.put("altName", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				language.setAltName(altName);

			if (id > 0) {
				language.setId(id);
				if (serviceLanguage.existsByIdAndAlpha3(id, alpha3))
					errors.put("alpha3", messageSource.getMessage("error.language.alph_3.duplicate", null, "Alpha 3 code is already in use", locale));
				if (serviceLanguage.existsByIdAndName(id, name))
					errors.put("name", messageSource.getMessage("error.language.name.duplicate", null, "Name is already in use", locale));
				if (serviceLanguage.existsByIdAndAltName(id, altName))
					errors.put("altName", messageSource.getMessage("error.language.altName.duplicate", null, "Alternative name code is already in use", locale));

			} else {
				if (serviceLanguage.existsByAlpha3(language.getAlpha3()))
					errors.put("alpha3", messageSource.getMessage("error.language.alph_3.duplicate", null, "Alpha 3 code is already in use", locale));
				if (serviceLanguage.existsByAltName(language.getAltName()))
					errors.put("altName", messageSource.getMessage("error.language.altName.duplicate", null, "Alternative name code is already in use", locale));
				if (serviceLanguage.existsByName(language.getName()))
					errors.put("name", messageSource.getMessage("error.language.name.duplicate", null, "Name is already in use", locale));
			}
			return errors.isEmpty();

		} catch (Exception e) {
			errors.put("language",  messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.persist(e);
			return false;
		}

	}

	/**
	 * setServiceLanguage: <br>
	 * Description
	 * 
	 * @param serviceLanguage
	 */
	public void setServiceLanguage(ServiceLanguage serviceLanguage) {
		this.serviceLanguage = serviceLanguage;
	}
}