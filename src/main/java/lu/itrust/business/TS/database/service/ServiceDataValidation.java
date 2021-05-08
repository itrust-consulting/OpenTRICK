package lu.itrust.business.TS.database.service;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;

import lu.itrust.business.TS.validator.field.ValidatorField;

/**
 * ServiceDataValidation.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since May 13, 2013
 */
public interface ServiceDataValidation {
	public boolean register(ValidatorField validator);

	public boolean unregister(Class<?> clazz);

	public boolean isRegistred(Class<?> clazz);

	public boolean isRegistred(ValidatorField validator);

	public ValidatorField findByClass(Class<?> clazz);

	public String validate(Object object, String fieldName, Object data);

	public String validate(Object o, String fieldName, Object candidate, Object[] choose);

	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose);

	public String ParseError(String message, MessageSource messageSource, Locale locale);

	public Map<String, String> validate(Object object);

	public Map<String, String> validate(Object o, Map<Object, Object> choose);

}