/**
 * 
 */
package lu.itrust.business.TS.model.parameter.impl;

import static lu.itrust.business.TS.constants.Constant.*;

import javax.persistence.Column;
import javax.persistence.Entity;

import lu.itrust.business.TS.model.parameter.IRiskAcceptanceParameter;

/**
 * @author eomar
 *
 */
@Entity
public class RiskAcceptanceParameter extends Parameter implements IRiskAcceptanceParameter {

	@Column(name = "dtColor", nullable = false)
	private String color;

	@Column(name = "dtLabel", nullable = false)
	private String label;

	/**
	 * 
	 */
	public RiskAcceptanceParameter() {
	}

	/**
	 * @param value
	 * @param description
	 * @param color
	 * @param label
	 */
	public RiskAcceptanceParameter(String label, double value, String color, String description) {
		super(value, description);
		this.color = color;
		this.label = label;
	}

	@Override
	public String getTypeName() {
		return PARAMETERTYPE_TYPE_RISK_ACCEPTANCE_NAME;
	}

	@Override
	public String getGroup() {
		return PARAMETER_CATEGORY_RISK_ACCEPTANCE;
	}

	@Override
	public String getColor() {
		return this.color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

}
