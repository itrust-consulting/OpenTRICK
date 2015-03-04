package lu.itrust.business.TS.validator.field;

import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.exception.TrickException;

public interface ValidatorField {
	
	boolean supports(Class<?> clazz);
	
	String validate(String fieldName, Object candidate) throws TrickException;

	String validate(Object o, String fieldName, Object candidate) throws TrickException;
	
	String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException;
	
	String validate(Object o, String fieldName, Object candidate, List<Object> choose) throws TrickException;
	
	Map<String, String> validate(Object o, Map<Object, Object> choose) throws TrickException;

	Map<String, String> validate(Object object) throws TrickException;
	
	Class<?> supported();
}
