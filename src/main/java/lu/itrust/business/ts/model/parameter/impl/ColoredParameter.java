/**
 * 
 */
package lu.itrust.business.ts.model.parameter.impl;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lu.itrust.business.ts.model.parameter.IColoredParameter;


/**
 * This abstract class represents a colored parameter that extends the base class Parameter and implements the IColoredParameter interface.
 * It provides functionality to store and retrieve the color of the parameter.
 */
@MappedSuperclass
public abstract class ColoredParameter extends Parameter implements IColoredParameter {

	@Column(name = "dtColor", nullable = false)
	private String color;

	/**
	 * Constructs a new ColoredParameter object.
	 */
	protected ColoredParameter() {
	}

	/**
	 * Constructs a new ColoredParameter object with the specified value, color, and description.
	 *
	 * @param value       the value of the parameter
	 * @param color       the color of the parameter
	 * @param description the description of the parameter
	 */
	protected ColoredParameter(double value, String color, String description) {
		super(value, description);
		this.color = color;
	}

	/**
	 * Returns the color of the parameter.
	 *
	 * @return the color of the parameter
	 */
	@Override
	public String getColor() {
		return this.color;
	}

	/**
	 * Sets the color of the parameter.
	 *
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}
}
