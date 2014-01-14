/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.validator.field.ValidatorField;

/**
 * @author eomar
 * 
 */
@Service
public class ServiceDataValidationImpl implements ServiceDataValidation {

	private Map<Class<?>, ValidatorField> validators = new LinkedHashMap<Class<?>, ValidatorField>();

	@Override
	public boolean register(ValidatorField validator) {
		if (!validators.containsKey(validator.supported()))
			validators.put(validator.supported(), validator);
		return validators.containsKey(validator.supported());
	}

	@Override
	public boolean deregister(Class<?> clazz) {
		if (validators.containsKey(clazz))
			validators.remove(clazz);
		return !validators.containsKey(clazz);
	}

	@Override
	public ValidatorField findByClass(Class<?> clazz) {
		return validators.get(clazz);
	}

	@Override
	public Map<String, String> validate(Object object) {
		ValidatorField validator = findByClass(object.getClass());
		if (validator == null)
			return null;
		return validator.validate(object);
	}

	@Override
	public String validate( Object object, String fieldName,
			Object data) {
		ValidatorField validator = findByClass(object.getClass());
		if (validator == null)
			return null;
		return validator.validate(object, fieldName, data);
	}

	@Override
	public String ParseError(String message, MessageSource messageSource,
			Locale locale) {
		if (message == null || message.trim().isEmpty())
			return null;
		String[] messages = message.split(":");
		if (messages.length == 1)
			return messageSource.getMessage(messages[0], null, locale);
		else if (messages.length == 2)
			return messageSource.getMessage(messages[0], null, messages[1],
					locale);
		else
			return messageSource.getMessage(messages[0],
					messages[1].split(";"), messages[2], locale);
	}

	@Override
	public boolean isRegistred(Class<?> clazz) {
		return validators.containsKey(clazz);
	}

	@Override
	public boolean isRegistred(ValidatorField validator) {
		return validators.containsValue(validator);
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate,
			Object[] choose) {
		ValidatorField validator = findByClass(o.getClass());
		if (validator == null)
			return null;
		return validator.validate(o, fieldName, candidate,choose);
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate,
			List<Object> choose) {
		ValidatorField validator = findByClass(o.getClass());
		if (validator == null)
			return null;
		return validator.validate(o, fieldName, candidate,choose);
	}

	@Override
	public Map<String, String> validate(Object o, Map<Object, Object> choose) {
		ValidatorField validator = findByClass(o.getClass());
		if (validator == null)
			return null;
		return validator.validate(o,choose);
	}
}
