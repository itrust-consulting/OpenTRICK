package lu.itrust.business.ts.model.parameter;

import jakarta.persistence.Transient;

public interface IParameter extends Cloneable {

	@Transient
	public static final String KEY_PARAMETER_FORMAT = "%s-#-_##_-#-%s";

	/**
	 * getDescription: <br>
	 * Returns the "description" field value
	 * 
	 * @return The SimpleParameter Description
	 */
	String getDescription();

	/**
	 * getValue: <br>
	 * Returns the "value" field value
	 * 
	 * @return The SimpleParameter Value
	 */
	Number getValue();

	/**
	 * Get parameter name.
	 * 
	 * @return typeName
	 */
	String getTypeName();

	/**
	 * Parameters are grouped by a category
	 * 
	 * @return category
	 */
	String getGroup();

	/**
	 * getId: <br>
	 * Returns the id field value.
	 * 
	 * @return The value of the id field
	 */
	Integer getId();

	IParameter duplicate();
	
	/**
	 * Unique key in each type.
	 * By Default return description
	 * @return
	 */
	default String getBaseKey() {
		return getDescription();
	}

	default String getKey() {
		return String.format(KEY_PARAMETER_FORMAT, getTypeName(), getBaseKey());
	}

	default boolean isMatch(String typeName, String baseKey) {
		return getTypeName().equalsIgnoreCase(typeName) && getBaseKey().equalsIgnoreCase(baseKey);
	}

	default boolean isMatch(String type) {
		return getTypeName().equalsIgnoreCase(type);
	}

}