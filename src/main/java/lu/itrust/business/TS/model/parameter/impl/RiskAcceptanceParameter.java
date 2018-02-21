/**
 * 
 */
package lu.itrust.business.TS.model.parameter.impl;

import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_RISK_ACCEPTANCE_NAME;
import static lu.itrust.business.TS.constants.Constant.PARAMETER_CATEGORY_RISK_ACCEPTANCE;

import javax.persistence.AttributeOverride;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.TS.model.parameter.IRiskAcceptanceParameter;

/**
 * @author eomar
 *
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "description", column = @Column(name = "dtDescription", length = 2048, nullable = false))
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

	@Override
	public String getBaseKey() {
		return super.getBaseKey()+getLabel();
	}

}
