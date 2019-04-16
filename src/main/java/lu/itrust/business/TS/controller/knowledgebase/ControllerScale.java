/**
 * 
 */
package lu.itrust.business.TS.controller.knowledgebase;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceScaleType;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.model.scale.ScaleType;

/**
 * @author eomar
 *
 */
@PreAuthorize(Constant.ROLE_MIN_CONSULTANT)
@Controller
@RequestMapping("/KnowledgeBase/ScaleType")
public class ControllerScale {

	@Autowired
	private ServiceScaleType serviceScaleType;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping
	public String home(Model model, Locale locale) {
		model.addAttribute("scaleTypes", serviceScaleType.findAll());
		model.addAttribute("languages", serviceLanguage.getAll());
		model.addAttribute("locale", locale.getLanguage());
		return "knowledgebase/scale/home";
	}

	@RequestMapping(value = "/Add", method = RequestMethod.GET)
	public String addType(Model model, Locale locale) {
		model.addAttribute("languages", serviceLanguage.getAll());
		model.addAttribute("locale", locale.getLanguage());
		return "knowledgebase/scale/form";
	}

	@RequestMapping(value = "/{id}/Edit", method = RequestMethod.GET)
	public String editType(@PathVariable int id, Model model, Locale locale) {
		model.addAttribute("scaleType", serviceScaleType.findOne(id));
		model.addAttribute("languages", serviceLanguage.getAll());
		model.addAttribute("locale", locale.getLanguage());
		return "knowledgebase/scale/form";
	}

	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object save(@ModelAttribute ScaleType scaleType, BindingResult result, Locale locale) {
		if (result.hasFieldErrors())
			return result.getAllErrors().stream().collect(
					Collectors.toMap(ObjectError::getObjectName, error -> messageSource.getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(), locale)));
		scaleType.setName(scaleType.getName().trim().toUpperCase());
		if (scaleType.getId() < 1) {
			if (serviceScaleType.hasAcronym(scaleType.getAcronym()))
				result.rejectValue("acronym", "error.scale.acronym.in_used", null, "Acronym is already in used");
			if (serviceScaleType.exists(scaleType.getName()))
				result.rejectValue("name", "error.scale.name.in_used", null, "Name is already in used");
		} else {
			ScaleType persisted = serviceScaleType.findOne(scaleType.getId());
			if (persisted == null)
				return JsonMessage.Error(messageSource.getMessage("error.scale_type.not_found", null, "Scale type cannot be found", locale));
			if (scaleType.getTranslations() != null)
				scaleType.forEach((key, translate) -> persisted.put(key, translate));
			scaleType = persisted;
		}
		serviceScaleType.saveOrUpdate(scaleType);
		return JsonMessage.Success(messageSource.getMessage("success.save.scale_type", null, "Scale type has been successfully saved", locale));
	}
	
	@RequestMapping(value = "/Delete", method = RequestMethod.POST, headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object deleteType(@RequestBody List<Integer> ids, Locale locale) {
		ids.forEach(id -> {
			ScaleType scaleType = serviceScaleType.findOne(id);
			if (scaleType != null)
				serviceScaleType.delete(scaleType);
		});

		return JsonMessage.Success(messageSource.getMessage("success.delete.scale_type", null, "Scale type has been successfully deleted", locale));
	}

}
