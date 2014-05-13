package lu.itrust.business.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;

import lu.itrust.business.validator.field.ValidatorField;

/**
 * ServiceDataValidation.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since May 13, 2013
 */
public interface ServiceDataValidation {
	public boolean register(ValidatorField validator) throws Exception;

	public boolean unregister(Class<?> clazz) throws Exception;

	public boolean isRegistred(Class<?> clazz) throws Exception;

	public boolean isRegistred(ValidatorField validator) throws Exception;

	public ValidatorField findByClass(Class<?> clazz) throws Exception;

	public String validate(Object object, String fieldName, Object data) throws Exception;

	public String validate(Object o, String fieldName, Object candidate, Object[] choose) throws Exception;

	public String validate(Object o, String fieldName, Object candidate, List<Object> choose) throws Exception;

	public String ParseError(String message, MessageSource messageSource, Locale locale) throws Exception;

	public Map<String, String> validate(Object object) throws Exception;

	public Map<String, String> validate(Object o, Map<Object, Object> choose) throws Exception;
}