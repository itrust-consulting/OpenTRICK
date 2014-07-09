package lu.itrust.business.validator.field;

import java.util.List;
import java.util.Map;

public interface ValidatorField {
	
	boolean supports(Class<?> clazz);

	String validate(Object o, String fieldName, Object candidate);
	
	String validate(Object o, String fieldName, Object candidate, Object[] choose);
	
	String validate(Object o, String fieldName, Object candidate, List<Object> choose);
	
	Map<String, String> validate(Object o, Map<Object, Object> choose);

	Map<String, String> validate(Object object);
	
	Class<?> supported();
}
