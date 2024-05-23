package lu.itrust.business.ts.model.parameter;

import jakarta.persistence.Transient;

/**
 * The IParameter interface represents a parameter in a business model.
 * It provides methods to retrieve information about the parameter.
 */
public interface IParameter extends Cloneable {

	/**
	 * The format for generating a unique key for each parameter type.
	 */
	@Transient
	public static final String KEY_PARAMETER_FORMAT = "%s-#-_##_-#-%s";

	/**
	 * Returns the description of the parameter.
	 *
	 * @return The description of the parameter.
	 */
	String getDescription();

	/**
	 * Returns the value of the parameter.
	 *
	 * @return The value of the parameter.
	 */
	Number getValue();

	/**
	 * Returns the name of the parameter type.
	 *
	 * @return The name of the parameter type.
	 */
	String getTypeName();

	/**
	 * Returns the category of the parameter.
	 *
	 * @return The category of the parameter.
	 */
	String getGroup();

	/**
	 * Returns the ID of the parameter.
	 *
	 * @return The ID of the parameter.
	 */
	Integer getId();

	/**
	 * Creates a duplicate of the parameter.
	 *
	 * @return A duplicate of the parameter.
	 */
	IParameter duplicate();

	/**
	 * Returns the base key for the parameter.
	 * By default, it returns the description of the parameter.
	 *
	 * @return The base key for the parameter.
	 */
	default String getBaseKey() {
		return getDescription();
	}

	/**
	 * Returns the unique key for the parameter.
	 *
	 * @return The unique key for the parameter.
	 */
	default String getKey() {
		return String.format(KEY_PARAMETER_FORMAT, getTypeName(), getBaseKey());
	}

	/**
	 * Checks if the parameter matches the given type name and base key.
	 *
	 * @param typeName The type name to match.
	 * @param baseKey The base key to match.
	 * @return true if the parameter matches the given type name and base key, false otherwise.
	 */
	default boolean isMatch(String typeName, String baseKey) {
		return getTypeName().equalsIgnoreCase(typeName) && getBaseKey().equalsIgnoreCase(baseKey);
	}

	/**
	 * Checks if the parameter matches the given type.
	 *
	 * @param type The type to match.
	 * @return true if the parameter matches the given type, false otherwise.
	 */
	default boolean isMatch(String type) {
		return getTypeName().equalsIgnoreCase(type);
	}
}