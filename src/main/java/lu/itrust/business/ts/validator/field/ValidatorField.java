package lu.itrust.business.ts.validator.field;

import java.util.Collection;
import java.util.Map;

import lu.itrust.business.ts.exception.TrickException;

/**
 * The ValidatorField interface defines methods for validating fields in an object.
 */
public interface ValidatorField {

	/**
	 * Checks if the validator supports the given class.
	 *
	 * @param clazz the class to check
	 * @return true if the validator supports the class, false otherwise
	 */
	boolean supports(Class<?> clazz);

	/**
	 * Checks if the field with the given name is editable.
	 *
	 * @param fieldName the name of the field
	 * @return true if the field is editable, false otherwise
	 */
	default boolean isEditable(String fieldName) {
		return true;
	}

	/**
	 * Validates the field with the given name and candidate value.
	 *
	 * @param fieldName the name of the field
	 * @param candidate the candidate value to validate
	 * @return the validation result as a string
	 * @throws TrickException if an error occurs during validation
	 */
	String validate(String fieldName, Object candidate) throws TrickException;

	/**
	 * Validates the field with the given name and candidate value in the specified object.
	 *
	 * @param o the object to validate
	 * @param fieldName the name of the field
	 * @param candidate the candidate value to validate
	 * @return the validation result as a string
	 * @throws TrickException if an error occurs during validation
	 */
	String validate(Object o, String fieldName, Object candidate) throws TrickException;

	/**
	 * Validates the field with the given name and candidate value in the specified object, with a set of choices.
	 *
	 * @param o the object to validate
	 * @param fieldName the name of the field
	 * @param candidate the candidate value to validate
	 * @param choose the set of choices for validation
	 * @return the validation result as a string
	 * @throws TrickException if an error occurs during validation
	 */
	String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException;

	/**
	 * Validates the field with the given name and candidate value in the specified object, with a collection of choices.
	 *
	 * @param o the object to validate
	 * @param fieldName the name of the field
	 * @param candidate the candidate value to validate
	 * @param choose the collection of choices for validation
	 * @return the validation result as a string
	 * @throws TrickException if an error occurs during validation
	 */
	String validate(Object o, String fieldName, Object candidate, Collection<Object> choose) throws TrickException;

	/**
	 * Validates the specified object using a map of choices.
	 *
	 * @param o the object to validate
	 * @param choose the map of choices for validation
	 * @return a map of field names and their validation results
	 * @throws TrickException if an error occurs during validation
	 */
	Map<String, String> validate(Object o, Map<Object, Object> choose) throws TrickException;

	/**
	 * Validates the specified object.
	 *
	 * @param object the object to validate
	 * @return a map of field names and their validation results
	 * @throws TrickException if an error occurs during validation
	 */
	Map<String, String> validate(Object object) throws TrickException;

	/**
	 * Gets the supported class by the validator.
	 *
	 * @return the supported class
	 */
	Class<?> supported();
}
