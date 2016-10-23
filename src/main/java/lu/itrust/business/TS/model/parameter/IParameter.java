package lu.itrust.business.TS.model.parameter;

import javax.persistence.Transient;

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

	String getTypeName();

	/**
	 * getId: <br>
	 * Returns the id field value.
	 * 
	 * @return The value of the id field
	 */
	Integer getId();

	IParameter duplicate();

	default String getKey() {
		return String.format(KEY_PARAMETER_FORMAT, getTypeName(), getDescription());
	}

	default Boolean isMatch(String typeName, String description) {
		return getTypeName().equals(typeName) && getDescription().equals(description);
	}

	default Boolean isMatch(String type) {
		return getTypeName().equals(type);
	}

}