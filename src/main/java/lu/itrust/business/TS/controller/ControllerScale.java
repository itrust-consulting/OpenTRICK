/**
 * 
 */
package lu.itrust.business.TS.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceScale;
import lu.itrust.business.TS.database.service.ServiceScaleEntry;
import lu.itrust.business.TS.database.service.ServiceScaleType;
import lu.itrust.business.TS.model.parameter.helper.Bounds;
import lu.itrust.business.TS.model.scale.Scale;
import lu.itrust.business.TS.model.scale.ScaleEntry;
import lu.itrust.business.TS.model.scale.ScaleType;

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
	private ServiceScaleType serviceScaleType;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping
	public String home(Model model, Locale locale) {
		model.addAttribute("scales", serviceScale.findAll());
		model.addAttribute("scaleTypes", serviceScaleType.findAll());
		model.addAttribute("languages", serviceLanguage.getAll());
		model.addAttribute("locale", locale.getLanguage());
		return "knowledgebase/scale/home";
	}

	@RequestMapping("/Entry")
	public String entryHome(Model model, Locale locale) {
		home(model, locale);
		return "knowledgebase/scale/entry/home";
	}

	@RequestMapping("/Type")
	public String typeHome(Model model, Locale locale) {
		model.addAttribute("scaleTypes", serviceScaleType.findAll());
		model.addAttribute("languages", serviceLanguage.getAll());
		model.addAttribute("locale", locale.getLanguage());
		return "knowledgebase/scale/type/home";
	}

	@RequestMapping(value = "/Type/Add", method = RequestMethod.GET)
	public String addType(Model model, Locale locale) {
		model.addAttribute("languages", serviceLanguage.getAll());
		model.addAttribute("locale", locale.getLanguage());
		return "knowledgebase/scale/type/form";
	}

	@RequestMapping(value = "/Type/{id}/Edit", method = RequestMethod.GET)
	public String editType(@PathVariable int id, Model model, Locale locale) {
		model.addAttribute("scaleType", serviceScaleType.findOne(id));
		model.addAttribute("languages", serviceLanguage.getAll());
		model.addAttribute("locale", locale.getLanguage());
		return "knowledgebase/scale/type/form";
	}

	@RequestMapping(value = "/Type/Save", method = RequestMethod.POST, headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object save(@ModelAttribute ScaleType scaleType, BindingResult result, Locale locale) {
		if (result.hasFieldErrors())
			return result.getAllErrors().stream().collect(
					Collectors.toMap(ObjectError::getObjectName, error -> messageSource.getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(), locale)));
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
	
	@RequestMapping(value = "/Type/Delete", method = RequestMethod.POST, headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object deleteType(@RequestBody List<Integer> ids, Locale locale) {
		ids.forEach(id -> {
			ScaleType scaleType = serviceScaleType.findOne(id);
			if (scaleType != null)
				serviceScaleType.delete(scaleType);
		});

		return JsonMessage.Success(messageSource.getMessage("success.delete.scale_type", null, "Scale type has been successfully deleted", locale));
	}

	@RequestMapping(value = "/{id}/Edit", method = RequestMethod.GET)
	public String edit(@PathVariable int id, Model model, Locale locale) {
		model.addAttribute("scale", serviceScale.findOne(id));
		model.addAttribute("locale", locale.getLanguage());
		return "knowledgebase/scale/form";
	}

	@RequestMapping(value = "/Add", method = RequestMethod.GET)
	public String add(Model model, Locale locale) {
		model.addAttribute("languages", serviceLanguage.getAll());
		model.addAttribute("locale", locale.getLanguage());
		model.addAttribute("scaleTypes", serviceScaleType.findAllFree());
		return "knowledgebase/scale/form";
	}

	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object save(@ModelAttribute Scale scale, BindingResult result, Locale locale) {
		if (result.hasFieldErrors())
			return result.getAllErrors().stream().collect(
					Collectors.toMap(ObjectError::getObjectName, error -> messageSource.getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(), locale)));
		scale.setMaxValue(scale.getMaxValue() * 1000);
		return scale.getId() > 0 ? updateSave(scale, result, locale) : checkSave(scale, result, locale);
	}

	private Object checkSave(Scale scale, BindingResult result, Locale locale) {
		if (result.hasFieldErrors())
			return result.getAllErrors().stream().collect(
					Collectors.toMap(ObjectError::getObjectName, error -> messageSource.getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(), locale)));
		ScaleType type = serviceScaleType.findOne(scale.getType().getId());
		if (type == null)
			return JsonMessage.Field("type", messageSource.getMessage("error.scale_type.not_found", null, "Scale type cannot be found", locale));
		else
			scale.setType(type);
		return computeSave(scale, result, locale);
	}

	private Object computeSave(Scale scale, BindingResult result, Locale locale) {
		scale.setScaleEntries(new ArrayList<>(scale.getLevel()));
		double currentValue = scale.getMaxValue();
		if (scale.getLevel() % 2 == 0) {
			for (int i = scale.getLevel() - 1; i >= 0; i--) {
				if (scale.getScaleEntries().isEmpty())
					scale.getScaleEntries().add(new ScaleEntry(i, scale.getType().getAcronym() + i, currentValue));
				else
					scale.getScaleEntries().add(new ScaleEntry(i, scale.getType().getAcronym() + i, currentValue *= 0.5));
			}

			Collections.reverse(scale.getScaleEntries());

			for (int i = 0, maxLevel = scale.getLevel() - 1; i < scale.getLevel(); i++) {
				if (i == 0) {
					ScaleEntry current = scale.getScaleEntries().get(i);
					if (i == maxLevel)
						current.setBounds(new Bounds(0, Constant.DOUBLE_MAX_VALUE));
					else
						current.setBounds(new Bounds(0, Math.sqrt(current.getValue() * scale.getScaleEntries().get(i + 1).getValue())));
				} else if (i == maxLevel)
					scale.getScaleEntries().get(i).setBounds(new Bounds(scale.getScaleEntries().get(i - 1).getBounds().getTo(), Constant.DOUBLE_MAX_VALUE));
				else {
					ScaleEntry current = scale.getScaleEntries().get(i);
					current.setBounds(
							new Bounds(scale.getScaleEntries().get(i - 1).getBounds().getTo(), Math.sqrt(current.getValue() * scale.getScaleEntries().get(i + 1).getValue())));
				}
			}

		} else {
			ScaleEntry prev = null;
			for (int i = scale.getLevel() - 2; i > 0; i -= 2) {
				ScaleEntry current = new ScaleEntry(i, scale.getType().getAcronym() + i),
						next = prev == null ? new ScaleEntry(i + 1, scale.getType().getAcronym() + (i + 1), currentValue) : prev;
				if (prev == null)
					scale.getScaleEntries().add(next);
				prev = new ScaleEntry(i - 1, scale.getType().getAcronym() + (i - 1));
				prev.setValue(currentValue *= 0.5);
				scale.getScaleEntries().add(current);
				scale.getScaleEntries().add(prev);
				current.setValue(Math.sqrt(next.getValue() * prev.getValue()));
			}

			Collections.reverse(scale.getScaleEntries());

			for (int i = 1, maxLevel = scale.getLevel() - 1; i < maxLevel; i += 2) {
				ScaleEntry current = scale.getScaleEntries().get(i), next = scale.getScaleEntries().get(i + 1);
				prev = scale.getScaleEntries().get(i - 1);
				if (prev.getLevel() == 0)
					prev.setBounds(new Bounds(0, Math.sqrt(current.getValue() * prev.getValue())));
				else
					prev.setBounds(new Bounds(prev.getBounds().getFrom(), Math.sqrt(current.getValue() * prev.getValue())));

				current.setBounds(new Bounds(prev.getBounds().getTo(), Math.sqrt(current.getValue() * next.getValue())));

				next.setBounds(new Bounds(current.getBounds().getTo(), Constant.DOUBLE_MAX_VALUE));
			}
		}
		serviceScale.saveOrUpdate(scale);
		return JsonMessage.Success(messageSource.getMessage("success.save.scale", null, "Scale has been successfully saved", locale));
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
		return computeSave(persisted, result, locale);
	}

	@RequestMapping(value = "/Delete", method = RequestMethod.POST, headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object delete(@RequestBody List<Integer> ids, Locale locale) {
		ids.forEach(id -> {
			Scale scale = serviceScale.findOne(id);
			if (scale != null)
				serviceScale.delete(scale);
		});

		return JsonMessage.Success(messageSource.getMessage("success.delete.scale", null, "Scale has been successfully deleted", locale));
	}
}
