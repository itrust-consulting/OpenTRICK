/**
 * 
 */
package lu.itrust.business.TS.controller;

import java.util.ArrayList;
import java.util.Iterator;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceScale;
import lu.itrust.business.TS.database.service.ServiceScaleEntry;
import lu.itrust.business.TS.model.scale.Scale;
import lu.itrust.business.TS.model.scale.ScaleEntry;

/**
 * @author eomar
 *
 */
@PreAuthorize(Constant.ROLE_MIN_CONSULTANT)
@Controller
@RequestMapping("/KnowledgeBase/Scale")
public class ControllerScale {

	@Autowired
	private ServiceScale serviceScale;

	@Autowired
	private ServiceScaleEntry serviceScaleEntry;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping
	public String home(Model model) {
		model.addAttribute("scales", serviceScale.findAll());
		return "knowledgebase/scale/home";
	}

	@RequestMapping
	public String edit(Model model) {
		model.addAttribute("scales", serviceScale.findAll());
		return "knowledgebase/scale/home";
	}

	@RequestMapping
	public String form(Model model) {
		model.addAttribute("scales", serviceScale.findAll());
		return "knowledgebase/scale/home";
	}

	@RequestMapping(value = "/KnowledgeBase/Scale/Save", method = RequestMethod.POST, headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object save(@ModelAttribute Scale scale, BindingResult result, Locale locale) {
		if (result.hasFieldErrors())
			return result.getAllErrors().stream().collect(
					Collectors.toMap(ObjectError::getObjectName, error -> messageSource.getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(), locale)));
		return scale.getId() > 0 ? updateSave(scale, result, locale) : checkSave(scale, result, locale);
	}

	private Object checkSave(Scale scale, BindingResult result, Locale locale) {
		if (serviceScale.hasAcronym(scale.getAcronym()))
			result.rejectValue("acronym", "error.scale.acronym.in_used", null, "Acronym is already in used");
		if (serviceScale.hasName(scale.getName()))
			result.rejectValue("name", "error.scale.name.in_used", null, "Name is already in used");
		if (result.hasFieldErrors())
			return result.getAllErrors().stream().collect(
					Collectors.toMap(ObjectError::getObjectName, error -> messageSource.getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(), locale)));
		return computeSave(scale, result, locale);
	}

	private Object computeSave(Scale scale, BindingResult result, Locale locale) {
		scale.setScaleEntries(new ArrayList<>(scale.getLevel()));
		double 
		

		return;
	}

	private Object updateSave(Scale scale, BindingResult result, Locale locale) {
		Scale persisted = serviceScale.findOne(scale.getId());
		Iterator<ScaleEntry> iterator = persisted.getScaleEntries().iterator();
		persisted.merge(scale);
		while (iterator.hasNext()) {
			ScaleEntry scaleEntry = iterator.next();
			iterator.remove();
			serviceScaleEntry.delete(scaleEntry);
		}
		return computeSave(scale, result, locale);
	}
}
