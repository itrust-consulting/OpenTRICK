/**
 * 
 */
package lu.itrust.business.ts.model.parameter.impl;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lu.itrust.business.ts.model.parameter.IColoredParameter;

/**
 * @author eomar
 *
 */

@MappedSuperclass
public abstract class ColoredParameter extends Parameter implements IColoredParameter {

	@Column(name = "dtColor", nullable = false)
	private String color;

	/**
	 * 
	 */
	protected ColoredParameter() {
	}

	/**
	 * @param value
	 * @param description
	 * @param color
	 * @param label
	 */
	protected ColoredParameter(double value, String color, String description) {
		super(value, description);
		this.color = color;
		
	}

	@Override
	public String getColor() {
		return this.color;
	}

	/**
	 * @param color
	 *              the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}
}
