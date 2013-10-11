package lu.itrust.business.controller;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import lu.itrust.business.TS.Language;
import lu.itrust.business.service.ServiceLanguage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** 
 * ControllerLanguage.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since Oct 11, 2013
 */
@Controller
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
	@RequestMapping("KnowLedgeBase/Language/Display")
	public String loadAllLanguages(Map<String, Object> model) throws Exception {
		model.put("languages", serviceLanguage.loadAll());
		return "language/languages";
	}

	/** 
	 * 
	 * Display single Language
	 * 
	 * */
	@Secured("ROLE_USER")
	@RequestMapping("KnowLedgeBase/Language/{languageId}")
	public String loadLanguage(@PathVariable("languageId") Integer languageId, HttpSession session, Map<String, Object> model, RedirectAttributes redirectAttributes, 
			Locale locale) throws Exception {
		Language language = (Language) session.getAttribute("language");
		if (language == null || language.getId() != languageId)
			language = serviceLanguage.get(languageId);
			if (language == null) {
				String msg = messageSource.getMessage("errors.language.notexist", null, "Langiage does not exist",locale);
				redirectAttributes.addFlashAttribute("errors", msg);
				return "redirect:/KnowLedgeBase/Language/Display";
			}
		model.put("language", language);
		return "language/showLanguage";
	}
	
	/** 
	 * 
	 * Request add new Language
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Language/Add")
	public String addLanguage(Map<String, Object> model) {
		model.put("language", new Language());
		return "language/addLanguage";
	}

	/** 
	 * 
	 * Perform add new Language
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Language/Create")
	public String createLanguage(@ModelAttribute("language") @Valid Language language,
			BindingResult result) throws Exception {
		this.serviceLanguage.save(language);
		return "redirect:../Language/Display";
	}

	/** 
	 * 
	 * Request edit single Language
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Language/Edit/{languageId}")
	public String editLanguage(@PathVariable("languageId") Integer languageId, HttpSession session, Map<String, Object> model, RedirectAttributes redirectAttributes, 
			Locale locale) throws Exception {
		Language language = (Language) session.getAttribute("language");
		if (language == null || language.getId() != languageId)
			language = serviceLanguage.get(languageId);
			if (language == null) {
				String msg = messageSource.getMessage("errors.language.notexist", null, "Language does not exist",locale);
				redirectAttributes.addFlashAttribute("errors", msg);
				return "redirect:/KnowLedgeBase/Language/Display";
			}
		model.put("language", language);
		return "language/editLanguage";
	}
	
	/** 
	 * 
	 * Perform edit single Language
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Language/Update/{languageId}")
	public String updateLanguage(@PathVariable("languageId") Integer languageId, @ModelAttribute("language") @Valid Language language, BindingResult result,
		   RedirectAttributes redirectAttributes,Locale locale) throws Exception {
		if (language == null || language.getId() != languageId) {
			String msg = messageSource.getMessage("errors.language.update.notrecognized", null, "Language not recognized",locale);
			redirectAttributes.addFlashAttribute("errors", msg);
		} else {
			try {
				serviceLanguage.saveOrUpdate(language);
				String msg = messageSource.getMessage("success.language.update.success", null, "Language had been updated!",locale);
				redirectAttributes.addFlashAttribute("success", msg);
			} catch (Exception e) {
				String msg = messageSource.getMessage("errors.language.update.fail", null, "Language update failed!",locale);
				redirectAttributes.addFlashAttribute("errors", msg);
			}
		}
		return "redirect:/KnowLedgeBase/Language/Display";
	}
	
	/** 
	 * 
	 * Delete single Language
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Language/Delete/{languageId}")
	public String deleteLanguage(@PathVariable("languageId") Integer languageId) throws Exception {
		serviceLanguage.remove(languageId);
		return "redirect:../Display";
	}
	
	public void setServiceLanguage(ServiceLanguage serviceLanguage){
		this.serviceLanguage = serviceLanguage;
	}
	
	
}
