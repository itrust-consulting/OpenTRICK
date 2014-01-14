/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;

import lu.itrust.business.validator.field.ValidatorField;

/**
 * @author eomar
 * @param <T>
 * 
 */
public interface ServiceDataValidation {
	boolean register(ValidatorField validator);

	boolean deregister(Class<?> clazz);

	boolean isRegistred(Class<?> clazz);

	boolean isRegistred(ValidatorField validator);

	ValidatorField findByClass(Class<?> clazz);

	Map<String, String> validate(Object object);

	String validate(Object object, String fieldName, Object data);

	String validate(Object o, String fieldName, Object candidate,
			Object[] choose);

	String validate(Object o, String fieldName, Object candidate,
			List<Object> choose);

	Map<String, String> validate(Object o, Map<Object, Object> choose);

	String ParseError(String message, MessageSource messageSource, Locale locale);
}
