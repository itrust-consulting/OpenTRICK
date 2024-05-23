package lu.itrust.business.ts.model.parameter.type;

import lu.itrust.business.ts.exception.TrickException;

/**
 * The {@code IParameterType} interface represents a parameter type.
 * It defines methods to get and set the id and name fields of a parameter type.
 */
public interface IParameterType {

	/**
	 * Returns the id field value.
	 *
	 * @return The value of the id field
	 */
	int getId();

	/**
	 * Sets the id field with a value.
	 *
	 * @param id The value to set the id field
	 */
	void setId(int id);

	/**
	 * Returns the name field value.
	 *
	 * @return The value of the name field
	 */
	String getName();

	/**
	 * Sets the name field with a value.
	 *
	 * @param name The value to set the name field
	 * @throws TrickException if an error occurs while setting the name field
	 */
	void setName(String name);

}