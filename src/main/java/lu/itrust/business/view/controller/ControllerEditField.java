/**
 * 
 */
package lu.itrust.business.view.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.History;
import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.component.JsonMessage;
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
@RequestMapping("/EditField")
public class ControllerEditField {

	@Autowired
	private ServiceItemInformation serviceItemInformation;

	@Autowired
	private ServiceParameter serviceParameter;

	@Autowired
	private ServiceHistory serviceHistory;

	@Autowired
	private MessageSource messageSource;

	protected boolean setFieldData(Field field, Object object,
			FieldEditor fieldEditor) throws IllegalArgumentException,
			IllegalAccessException, ParseException, NumberFormatException {
		if (fieldEditor.getType().equalsIgnoreCase("string"))
			field.set(object, (String) fieldEditor.getValue());
		else if (fieldEditor.getType().equalsIgnoreCase("integer"))
			field.set(object, Integer.parseInt(fieldEditor.getValue()));
		else if (fieldEditor.getType().equalsIgnoreCase("double"))
			field.set(object, Double.parseDouble(fieldEditor.getValue()));
		else if (fieldEditor.getType().equalsIgnoreCase("float"))
			field.set(object, Float.parseFloat(fieldEditor.getValue()));
		else if (fieldEditor.getType().equalsIgnoreCase("boolean"))
			field.set(object, Boolean.parseBoolean(fieldEditor.getValue()));
		else if (fieldEditor.getType().equalsIgnoreCase("date")) {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			field.set(object, format.parse(fieldEditor.getValue()));
		} else
			return false;
		return true;
	}

	@RequestMapping(value = "/ItemInformation", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String itemInformation(@RequestBody FieldEditor fieldEditor, Locale locale) {
		try {
			ItemInformation itemInformation = serviceItemInformation
					.get(fieldEditor.getId());
			if (itemInformation == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.itemInformation.not_found", null,
						"ItemInformation cannot be found", locale));
			Field field = itemInformation.getClass().getDeclaredField(
					fieldEditor.getFieldName());
			field.setAccessible(true);

			if (setFieldData(field, itemInformation, fieldEditor)) {
				serviceItemInformation.saveOrUpdate(itemInformation);
				return JsonMessage.Success(messageSource.getMessage(
						"success.itemInformation.updated", null,
						"ItemInformation was successfully updated", locale));
			} else
				return JsonMessage.Error(messageSource.getMessage(
						"error.edit.type.field", null,
						"Data cannot be updated", locale));
		} catch (NumberFormatException e) {
			return JsonMessage.Error(messageSource.getMessage(
					"error.format.number", null, "Number expected", locale));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(),
					null, e.getMessage(), locale));
		}
		return JsonMessage.Error(messageSource.getMessage(
				"error.edit.save.field", null, "Data cannot be saved", locale));
	}

	@RequestMapping(value = "/Parameter", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String parameter(@RequestBody FieldEditor fieldEditor, Locale locale) {
		try {
			Parameter parameter = serviceParameter.get(fieldEditor.getId());

			if (parameter == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.parameter.not_found", null,
						"Parameter cannot be found", locale));
			Field field = parameter.getClass().getDeclaredField(
					fieldEditor.getFieldName());
			field.setAccessible(true);
			if (setFieldData(field, parameter, fieldEditor)) {
				serviceParameter.saveOrUpdate(parameter);
				return JsonMessage.Success(messageSource.getMessage(
						"success.parameter.updated", null,
						"Parameter was successfully updated", locale));
			} else
				return JsonMessage.Success(messageSource.getMessage(
						"error.edit.type.field", null,
						"Data cannot be updated", locale));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			return JsonMessage.Error(messageSource.getMessage(
					"error.format.number", null, "Number expected", locale));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(),
					null, e.getMessage(), locale));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(
					"error.format.date", null, "Date expected", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(),
					null, e.getMessage(), locale));
		}
		return JsonMessage.Error(messageSource.getMessage(
				"error.edit.save.field", null, "Data cannot be saved", locale));
	}

	@RequestMapping(value = "/ExtendedParameter", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String extendedParameter(@RequestBody FieldEditor fieldEditor, Locale locale) {
		try {
			ExtendedParameter parameter = (ExtendedParameter) serviceParameter
					.get(fieldEditor.getId());
			if (parameter == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.parameter.not_found", null,
						"Parameter cannot be found", locale));
			Field field = null;
			if ("value id type description"
					.contains(fieldEditor.getFieldName()))
				field = parameter.getClass().getSuperclass()
						.getDeclaredField(fieldEditor.getFieldName());
			else
				field = parameter.getClass().getDeclaredField(
						fieldEditor.getFieldName());
			field.setAccessible(true);
			if (setFieldData(field, parameter, fieldEditor)) {
				serviceParameter.saveOrUpdate(parameter);
				return JsonMessage.Success(messageSource.getMessage(
						"success.extendedParameter.update", null,
						"Parameter was successfully update", locale));
			} else
				return JsonMessage.Error(messageSource.getMessage(
						"error.edit.type.field", null,
						"Data cannot be updated", locale));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			return JsonMessage.Error(messageSource.getMessage(
					"error.format.number", null, "Number expected", locale));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(),
					null, e.getMessage(), locale));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(
					"error.format.date", null, "Date expected", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(),
					null, e.getMessage(), locale));
		}
		return JsonMessage.Error(messageSource.getMessage(
				"error.edit.save.field", null, "Data cannot be saved", locale));
	}

	@RequestMapping(value = "/History", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String history(@RequestBody FieldEditor fieldEditor, Locale locale) {
		try {
			History history = serviceHistory.get(fieldEditor.getId());

			if (history == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.history.not_found", null,
						"History cannot be found", locale));

			Field field = history.getClass().getDeclaredField(
					fieldEditor.getFieldName());
			field.setAccessible(true);

			if (setFieldData(field, history, fieldEditor)) {
				serviceHistory.saveOrUpdate(history);
				return JsonMessage.Success(messageSource.getMessage(
						"success.history.updated", null,
						"History was successfully updated", locale));
			} else
				return JsonMessage.Error(messageSource.getMessage(
						"error.edit.type.field", null,
						"Data cannot be updated", locale));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			return JsonMessage.Error(messageSource.getMessage(
					"error.format.number", null, "Number expected", locale));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(),
					null, e.getMessage(), locale));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(
					"error.format.date", null, "Date expected", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(),
					null, e.getMessage(), locale));
		}
		return JsonMessage.Error(messageSource.getMessage(
				"error.edit.save.field", null, "Data cannot be saved", locale));
	}

}
