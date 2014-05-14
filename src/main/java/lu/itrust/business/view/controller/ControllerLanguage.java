package lu.itrust.business.view.controller;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.validator.LanguageValidator;
import lu.itrust.business.validator.field.ValidatorField;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
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

/**
 * ControllerLanguage.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
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
	 * */
	@RequestMapping
	public String loadAllLanguages(Map<String, Object> model) throws Exception {
		model.put("languages", serviceLanguage.getAll());
		return "knowledgebase/language/languages";
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public String section(Model model) throws Exception {
		model.addAttribute("languages", serviceLanguage.getAll());
		return "knowledgebase/language/languages";
	}

	/**
	 * 
	 * Display single Language
	 * 
	 * */
	@RequestMapping("/{languageId}")
	public String loadSingleLanguage(@PathVariable("languageId") Integer languageId, HttpSession session, Map<String, Object> model, RedirectAttributes redirectAttributes, Locale locale)
			throws Exception {
		Language language = (Language) session.getAttribute("language");
		if (language == null || language.getId() != languageId)
			language = serviceLanguage.get(languageId);
		if (language == null) {
			String msg = messageSource.getMessage("errors.language.notexist", null, "Language does not exist", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
			return "redirect:/KnowLedgeBase/Language";
		}
		model.put("language", language);
		return "knowledgebase/language/showLanguage";
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param value
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody
	Map<String, String> save(@RequestBody String value, Locale locale) {
		Map<String, String> errors = new LinkedHashMap<String, String>();
		try {
			Language language = new Language();
			if (!buildLanguage(errors, language, value, locale))
				return errors;
			if (language.getId() < 1) {
				if (serviceLanguage.existsByAlpha3(language.getAlpha3()))
					errors.put("alpha3", messageSource.getMessage("error.language.alph3.duplicate", null, "Alpha 3 code is already in use", locale));
				if (serviceLanguage.existsByAltName(language.getAltName()))
					errors.put("altName", messageSource.getMessage("error.language.altName.duplicate", null, "Alternative name code is already in use", locale));
				if (serviceLanguage.existsByName(language.getName()))
					errors.put("name", messageSource.getMessage("error.language.name.duplicate", null, "Name is already in use", locale));
				if (errors.isEmpty())
					serviceLanguage.save(language);
			} else
				serviceLanguage.saveOrUpdate(language);
		} catch (Exception e) {
			errors.put("language", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
		}
		return errors;
	}

	/**
	 * 
	 * Delete single language
	 * 
	 * */
	@RequestMapping(value = "/Delete/{languageId}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody
	String[] deleteLanguage(@PathVariable("languageId") Integer languageId, Locale locale) throws Exception {
		serviceLanguage.delete(languageId);
		return new String[] { "error", messageSource.getMessage("success.language.delete.successfully", null, "Language was deleted successfully", locale) };

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
			if (id > 0)
				language.setId(jsonNode.get("id").asInt());
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
			return errors.isEmpty();

		} catch (Exception e) {
			errors.put("language", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
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