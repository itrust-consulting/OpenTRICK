package lu.itrust.business.ts.model.parameter.impl;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.model.parameter.IRiskAcceptanceParameter;

/**
 * Represents a risk acceptance parameter.
 * This class extends the ColoredParameter class and implements the IRiskAcceptanceParameter interface.
 * It provides methods to get and set the label of the parameter, as well as the base key.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "description", column = @Column(name = "dtDescription", length = 2048, nullable = false))
public class RiskAcceptanceParameter extends ColoredParameter implements IRiskAcceptanceParameter {

	@Column(name = "dtLabel", nullable = false)
	private String label;

	/**
	 * Default constructor for RiskAcceptanceParameter.
	 */
	public RiskAcceptanceParameter() {
	}

	/**
	 * Constructor for RiskAcceptanceParameter.
	 *
	 * @param label       the label of the parameter
	 * @param value       the value of the parameter
	 * @param color       the color of the parameter
	 * @param description the description of the parameter
	 */
	public RiskAcceptanceParameter(String label, double value, String color, String description) {
		super(value, color, description);
		setLabel(label);
	}

	/**
	 * Get the label of the RiskAcceptanceParameter.
	 *
	 * @return the label
	 */
	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * Set the label of the RiskAcceptanceParameter.
	 *
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Get the base key of the RiskAcceptanceParameter.
	 *
	 * @return the base key
	 */
	@Override
	public String getBaseKey() {
		return super.getBaseKey() + getLabel();
	}

}
