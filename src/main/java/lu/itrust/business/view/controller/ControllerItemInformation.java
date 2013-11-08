/**
 * 
 */
package lu.itrust.business.view.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.service.ServiceItemInformation;
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
@RequestMapping("/itemInformation")
public class ControllerItemInformation {

	@Autowired
	private ServiceItemInformation serviceItemInformation;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping(value = "/editField", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String editField(@RequestBody FieldEditor fieldEditor, Locale locale) {
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
		return messageSource.getMessage("error.edit.field", null, "Error ",
				locale);
	}
}
