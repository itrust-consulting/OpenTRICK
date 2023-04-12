/**
 * 
 */
package lu.itrust.business.ts.model.parameter.impl;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.model.parameter.IRiskAcceptanceParameter;

/**
 * @author eomar
 *
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "description", column = @Column(name = "dtDescription", length = 2048, nullable = false))
public class RiskAcceptanceParameter extends ColoredParameter implements IRiskAcceptanceParameter {

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
		super(value, color, description);
		setLabel(label);
	}

	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *              the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getBaseKey() {
		return super.getBaseKey() + getLabel();
	}

}
