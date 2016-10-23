package lu.itrust.business.TS.model.parameter.type;

import lu.itrust.business.TS.exception.TrickException;

public interface IParameterType {

	/**
	 * getId: <br>
	 * Returns the id field value.
	 * 
	 * @return The value of the id field
	 */
	int getId();

	/**
	 * setId: <br>
	 * Sets the Field "id" with a value.
	 * 
	 * @param id
	 *            The Value to set the id field
	 */
	void setId(int id);

	/**
	 * getLabel: <br>
	 * Returns the name field value.
	 * 
	 * @return The value of the name field
	 */
	String getName();

	/**
	 * setLabel: <br>
	 * Sets the Field "name" with a value.
	 * 
	 * @param name
	 *            The Value to set the name field
	 * @throws TrickException
	 */
	void setName(String name);

}