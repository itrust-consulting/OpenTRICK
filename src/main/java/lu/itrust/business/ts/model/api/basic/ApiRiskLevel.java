package lu.itrust.business.ts.model.api.basic;

import lu.itrust.business.ts.model.parameter.IBoundedParameter;

/**
 * Represents a level on a likelihood/impact scale.
 *
 */
/**
 * Represents an API risk level.
 */
public class ApiRiskLevel {
	private int level;
	private String label;
	private String description;
	private double value;
	private double valueBoundsFrom;
	private double valueBoundsTo;

	/**
	 * Creates an instance of ApiRiskLevel based on the provided bounded parameter.
	 *
	 * @param parameter The bounded parameter used to create the ApiRiskLevel.
	 * @return The created ApiRiskLevel object.
	 */
	public static ApiRiskLevel create(IBoundedParameter parameter) {
		ApiRiskLevel o = new ApiRiskLevel();
		o.setLevel(parameter.getLevel());
		o.setLabel(parameter.getLabel());
		o.setDescription(parameter.getDescription());
		o.setValue(parameter.getValue());
		o.setValueBoundsFrom(parameter.getBounds().getFrom());
		o.setValueBoundsTo(parameter.getBounds().getTo());
		return o;
	}

	/**
	 * Gets the level of the risk.
	 *
	 * @return The level of the risk.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Sets the level of the risk.
	 *
	 * @param level The level of the risk.
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Gets the label of the risk.
	 *
	 * @return The label of the risk.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label of the risk.
	 *
	 * @param label The label of the risk.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the description of the risk.
	 *
	 * @return The description of the risk.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the risk.
	 *
	 * @param description The description of the risk.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the value of the risk.
	 *
	 * @return The value of the risk.
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Sets the value of the risk.
	 *
	 * @param value The value of the risk.
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * Gets the lower bound value of the risk.
	 *
	 * @return The lower bound value of the risk.
	 */
	public double getValueBoundsFrom() {
		return valueBoundsFrom;
	}

	/**
	 * Sets the lower bound value of the risk.
	 *
	 * @param valueBoundsFrom The lower bound value of the risk.
	 */
	public void setValueBoundsFrom(double valueBoundsFrom) {
		this.valueBoundsFrom = valueBoundsFrom;
	}

	/**
	 * Gets the upper bound value of the risk.
	 *
	 * @return The upper bound value of the risk.
	 */
	public double getValueBoundsTo() {
		return valueBoundsTo;
	}

	/**
	 * Sets the upper bound value of the risk.
	 *
	 * @param valueBoundsTo The upper bound value of the risk.
	 */
	public void setValueBoundsTo(double valueBoundsTo) {
		this.valueBoundsTo = valueBoundsTo;
	}
}
