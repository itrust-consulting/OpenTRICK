/**
 * 
 */
package lu.itrust.business.TS.validator.field;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.exception.TrickException;

/**
 * @author eomar
 * 
 */
public abstract class ValidatorFieldImpl implements ValidatorField {

	protected static boolean Contains(Object[] objects, Object element) {
		for (Object object : objects) {
			if (object == null && element == null || object != null
					&& object.equals(element))
				return true;
		}
		return false;
	}
	
	

	@Override
	public String validate(Object o, String fieldName, Object candidate) throws TrickException {
		if (o == null || !supports(o.getClass()))
			return null;
		return validate(fieldName, candidate);
	}

	public static boolean Contains(Object objects, Object element) {
		try {
			if (objects == null)
				return false;
			if (objects instanceof Object[])
				return Contains((Object[]) objects, element);
			else if (objects instanceof Collection<?>)
				return ((Collection<?>) objects).contains(element);
			else if (objects instanceof Map<?, ?>)
				return ((Map<?, ?>) objects).containsKey(element)
						|| ((Map<?, ?>) objects).containsValue(element);
			else if (objects instanceof String)
				return ((String) objects).contains(element.toString());
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return supported().isAssignableFrom(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.Validator#validate(java.lang.Object,
	 * java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> validate(Object o, Map<Object, Object> choose) throws TrickException {
		if (!supports(o.getClass()))
			return null;
		Map<String, String> errors = new LinkedHashMap<String, String>();
		for (Field field : o.getClass().getDeclaredFields()) {
			try {
				field.setAccessible(true);
				String error = null;
				if (choose == null || !choose.containsKey(field.getName()))
					error = validate(o, field.getName(), field.get(o));
				else {
					Object chooses = choose.get(field.getName());
					if (chooses instanceof List<?>)
						error = validate(o, field.getName(), field.get(o),
								(List<Object>) chooses);
					else if (chooses instanceof Object[])
						error = validate(o, field.getName(), field.get(o),
								(Object[]) chooses);
					else
						error = validate(o, field.getName(), field.get(o));
				}
				if (error != null)
					errors.put(field.getName(), error);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return errors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.Validator#validate(java.lang.Object)
	 */
	@Override
	public Map<String, String> validate(Object object) throws TrickException {
		if (!supports(object.getClass()))
			return null;
		Map<String, String> errors = new LinkedHashMap<String, String>();
		for (Field field : object.getClass().getDeclaredFields()) {
			try {
				field.setAccessible(true);
				String error = validate(object, field.getName(),
						field.get(object));
				if (error != null)
					errors.put(field.getName(), error);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return errors;
	}

}
