/**
 * 
 */
package lu.itrust.business.view.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import lu.itrust.business.TS.History;
import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.service.ServiceHistory;
import lu.itrust.business.service.ServiceItemInformation;
import lu.itrust.business.service.ServiceParameter;
import lu.itrust.business.view.model.FieldEditor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author eom
 * 
 */
@Secured("ROLE_USER")
@Controller
@RequestMapping("/editField")
public class ControllerEditField {

	@Autowired
	private ServiceItemInformation serviceItemInformation;
	
	@Autowired
	private ServiceParameter serviceParameter;
	
	@Autowired
	private ServiceHistory serviceHistory;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping(value = "/itemInformation", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String itemInformation(@RequestBody FieldEditor fieldEditor, Locale locale) {
		try {

			ItemInformation itemInformation = serviceItemInformation
					.get(fieldEditor.getId());
			if (itemInformation == null)
				return messageSource.getMessage(
						"error.itemInformation.not_found", null,
						"ItemInformation cannot be found", locale);
			Field field = itemInformation.getClass().getDeclaredField(
					fieldEditor.getFieldName());
			field.setAccessible(true);
			
			if(field.getType().equals(String.class))
				field.set(itemInformation, fieldEditor.getValue()+"");
			else field.set(itemInformation, fieldEditor.getValue());
			serviceItemInformation.saveOrUpdate(itemInformation);
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			return messageSource.getMessage(e.getMessage(), null,
					e.getMessage(), locale);
		}
		return messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved",
				locale);
	}
	
	@RequestMapping(value = "/parameter", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody String parameter(@RequestBody FieldEditor fieldEditor, Locale locale){
		try {
			Parameter parameter = serviceParameter.get(fieldEditor.getId());
			
			if(parameter == null)
				return messageSource.getMessage(
						"error.parameter.not_found", null,
						"Parameter cannot be found", locale);
			
			Field field = parameter.getClass().getDeclaredField(
					fieldEditor.getFieldName());
			field.setAccessible(true);
			if(field.getType().equals(String.class))
				field.set(parameter, fieldEditor.getValue()+"");
			else field.set(parameter, fieldEditor.getValue());
			serviceParameter.saveOrUpdate(parameter);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return messageSource.getMessage(e.getMessage(), null,
					e.getMessage(), locale);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved",
				locale);
	}
	
	@RequestMapping(value = "/history", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody String history(@RequestBody FieldEditor fieldEditor, Locale locale){
		try {
			History history = serviceHistory.get(fieldEditor.getId());
			
			if(history == null)
				return messageSource.getMessage(
						"error.history.not_found", null,
						"History cannot be found", locale);
			
			Field field = history.getClass().getDeclaredField(
					fieldEditor.getFieldName());
			field.setAccessible(true);
			if(field.getType().equals(String.class))
				field.set(history, fieldEditor.getValue()+"");
			else field.set(history, fieldEditor.getValue());
			serviceHistory.saveOrUpdate(history);
			return null;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return messageSource.getMessage(e.getMessage(), null,
					e.getMessage(), locale);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved",
				locale);
	}
	
}
