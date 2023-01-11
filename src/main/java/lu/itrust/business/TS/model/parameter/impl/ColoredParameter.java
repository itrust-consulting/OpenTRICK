/**
 * 
 */
package lu.itrust.business.TS.model.parameter.impl;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lu.itrust.business.TS.model.parameter.IColoredParameter;

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
