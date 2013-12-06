package lu.itrust.business.view.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Language;
import lu.itrust.business.service.ServiceLanguage;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
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
@Secured("ROLE_USER")
@Controller
@RequestMapping("/KnowledgeBase/Language")
public class ControllerLanguage {

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private MessageSource messageSource;

	/**
	 * 
	 * Display all Language
	 * 
	 * */
	@RequestMapping
	public String loadAllLanguages(Map<String, Object> model) throws Exception {
		model.put("languages", serviceLanguage.loadAll());
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
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json")
	public String section(Model model) throws Exception {
		model.addAttribute("languages", serviceLanguage.loadAll());
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
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	List<String[]> save(@RequestBody String value, Locale locale) {
		List<String[]> errors = new LinkedList<>();
		try {

			Language language = new Language();
			if (!buildLanguage(errors, language, value, locale))
				return errors;
			if (language.getId() < 1) {
				serviceLanguage.save(language);
			} else {
				serviceLanguage.saveOrUpdate(language);
			}
		} catch (Exception e) {
			errors.add(new String[] { "language", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}
		return errors;
	}

	/**
	 * 
	 * Delete single language
	 * 
	 * */
	@RequestMapping(value = "/Delete/{languageId}", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody String[] deleteLanguage(@PathVariable("languageId") Integer languageId, Locale locale) throws Exception {
		serviceLanguage.remove(languageId);		
		return new String[] {
			"error",
			messageSource.getMessage("success.language.delete.successfully", null,
					"Language was deleted successfully", locale) 
		};
		
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
	private boolean buildLanguage(List<String[]> errors, Language language, String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			int id = jsonNode.get("id").asInt();
			if (id > 0)
				language.setId(jsonNode.get("id").asInt());
			
			language.setAlpha3(jsonNode.get("alpha3").asText());
			language.setName(jsonNode.get("name").asText());
			language.setAltName(jsonNode.get("altName").asText());

			return true;

		} catch (Exception e) {

			errors.add(new String[] { "language", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
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