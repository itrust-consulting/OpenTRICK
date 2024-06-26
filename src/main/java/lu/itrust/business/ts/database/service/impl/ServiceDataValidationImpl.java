package lu.itrust.business.ts.database.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import lu.itrust.business.ts.database.service.ServiceDataValidation;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.validator.field.ValidatorField;

/**
 * ServiceDataValidationImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl.
 * @version
 * @since May 13, 2013
 */
@Service
public class ServiceDataValidationImpl implements ServiceDataValidation {

	private Map<Class<?>, ValidatorField> validators = new LinkedHashMap<>();

	/**
	 * register: <br>
	 * Description
	 * 
	 * @param validator
	 * @return
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceDataValidation#register(lu.itrust.business.ts.validator.field.ValidatorField)
	 */
	@Override
	public boolean register(ValidatorField validator) {
		if (!validators.containsKey(validator.supported()))
			validators.put(validator.supported(), validator);
		return validators.containsKey(validator.supported());
	}

	/**
	 * unregister: <br>
	 * Description
	 * 
	 * @param clazz
	 * @return
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceDataValidation#unregister(java.lang.Class)
	 */
	@Override
	public boolean unregister(Class<?> clazz) {
		if (validators.containsKey(clazz))
			validators.remove(clazz);
		return !validators.containsKey(clazz);
	}

	/**
	 * findByClass: <br>
	 * Description
	 * 
	 * @param clazz
	 * @return
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceDataValidation#findByClass(java.lang.Class)
	 */
	@Override
	public ValidatorField findByClass(Class<?> clazz) {
		ValidatorField validatorField = validators.get(clazz);
		if (validatorField == null && !clazz.equals(Object.class))
			return findByClass(clazz.getSuperclass());
		return validatorField;
	}

	/**
	 * validate: <br>
	 * Description
	 * 
	 * @param object
	 * @return
	 * @throws TrickException
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceDataValidation#validate(java.lang.Object)
	 */
	@Override
	public Map<String, String> validate(Object object) throws TrickException {
		ValidatorField validator = findByClass(object.getClass());
		if (validator == null)
			return Collections.emptyMap();
		return validator.validate(object);
	}

	/**
	 * validate: <br>
	 * Description
	 * 
	 * @param object
	 * @param fieldName
	 * @param data
	 * @return
	 * @throws TrickException
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceDataValidation#validate(java.lang.Object,
	 *      java.lang.String, java.lang.Object)
	 */
	@Override
	public String validate(Object object, String fieldName, Object data) throws TrickException {
		ValidatorField validator = findByClass(object.getClass());
		if (validator == null)
			return null;
		return validator.validate(object, fieldName, data);
	}

	/**
	 * ParseError: <br>
	 * Description
	 * 
	 * @param message
	 * @param messageSource
	 * @param locale
	 * @return
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceDataValidation#ParseError(java.lang.String,
	 *      org.springframework.context.MessageSource, java.util.Locale)
	 */
	@Override
	public String ParseError(String message, MessageSource messageSource, Locale locale) {
		if (message == null || message.trim().isEmpty())
			return null;
		String[] messages = message.split(":");
		if (messages.length == 1)
			return messageSource.getMessage(messages[0], null, locale);
		else if (messages.length == 2)
			return messageSource.getMessage(messages[0], null, messages[1], locale);
		else if (messages[1].isEmpty())
			return messageSource.getMessage(messages[0], null, messages[2], locale);
		else
			return messageSource.getMessage(messages[0], messages[1].split(";"), messages[2], locale);
	}

	/**
	 * isRegistred: <br>
	 * Description
	 * 
	 * @param clazz
	 * @return
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceDataValidation#isRegistred(java.lang.Class)
	 */
	@Override
	public boolean isRegistred(Class<?> clazz) {
		return validators.containsKey(clazz);
	}

	/**
	 * isRegistred: <br>
	 * Description
	 * 
	 * @param validator
	 * @return
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceDataValidation#isRegistred(lu.itrust.business.ts.validator.field.ValidatorField)
	 */
	@Override
	public boolean isRegistred(ValidatorField validator) {
		return validators.containsValue(validator);
	}

	/**
	 * validate: <br>
	 * Description
	 * 
	 * @param o
	 * @param fieldName
	 * @param candidate
	 * @param choose
	 * @return
	 * @throws TrickException
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceDataValidation#validate(java.lang.Object,
	 *      java.lang.String, java.lang.Object, java.lang.Object[])
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException {
		ValidatorField validator = findByClass(o.getClass());
		if (validator == null)
			return null;
		return validator.validate(o, fieldName, candidate, choose);
	}

	/**
	 * validate: <br>
	 * Description
	 * 
	 * @param o
	 * @param fieldName
	 * @param candidate
	 * @param choose
	 * @return
	 * @throws TrickException
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceDataValidation#validate(java.lang.Object,
	 *      java.lang.String, java.lang.Object, Collection)
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose) throws TrickException {
		ValidatorField validator = findByClass(o.getClass());
		if (validator == null)
			return null;
		return validator.validate(o, fieldName, candidate, choose);
	}

	/**
	 * validate: <br>
	 * Description
	 * 
	 * @param o
	 * @param choose
	 * @return
	 * @throws TrickException
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceDataValidation#validate(java.lang.Object,
	 *      java.util.Map)
	 */
	@Override
	public Map<String, String> validate(Object o, Map<Object, Object> choose) throws TrickException {
		ValidatorField validator = findByClass(o.getClass());
		if (validator == null)
			return null;
		return validator.validate(o, choose);
	}
}