/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl;

import lu.itrust.business.ts.model.ticketing.TicketingField;


/**
 * This abstract class represents a ticketing field.
 * It provides common functionality and properties for all ticketing fields.
 */
public abstract class AbstractField implements TicketingField {

	private String id;
	private String name;

	/**
	 * Default constructor for AbstractField.
	 */
	public AbstractField() {
	}

	/**
	 * Constructor for AbstractField with specified id and name.
	 *
	 * @param id   the ID of the field
	 * @param name the name of the field
	 */
	public AbstractField(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Get the ID of the field.
	 *
	 * @return the ID of the field
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * Get the name of the field.
	 *
	 * @return the name of the field
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Set the ID of the field.
	 *
	 * @param id the ID of the field
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Set the name of the field.
	 *
	 * @param name the name of the field
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
}
