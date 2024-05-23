package lu.itrust.business.ts.model.api.basic;

import lu.itrust.business.ts.model.parameter.impl.RiskAcceptanceParameter;

/**
 * Represents a risk acceptance level in the API.
 */
public class ApiRiskAcceptanceLevel {
	private String label;
	private String description;
	private String color;
	private int threshold;

	/**
	 * Creates an instance of ApiRiskAcceptanceLevel based on the provided RiskAcceptanceParameter.
	 *
	 * @param parameter The RiskAcceptanceParameter used to create the ApiRiskAcceptanceLevel.
	 * @return The created ApiRiskAcceptanceLevel object.
	 */
	public static ApiRiskAcceptanceLevel create(RiskAcceptanceParameter parameter) {
		ApiRiskAcceptanceLevel o = new ApiRiskAcceptanceLevel();
		o.setLabel(parameter.getLabel());
		o.setDescription(parameter.getDescription());
		o.setColor(parameter.getColor());
		o.setThreshold(parameter.getValue().intValue());
		return o;
	}

	/**
	 * Gets the label of the risk acceptance level.
	 *
	 * @return The label of the risk acceptance level.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label of the risk acceptance level.
	 *
	 * @param label The label to set for the risk acceptance level.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the description of the risk acceptance level.
	 *
	 * @return The description of the risk acceptance level.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the risk acceptance level.
	 *
	 * @param description The description to set for the risk acceptance level.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the color of the risk acceptance level.
	 *
	 * @return The color of the risk acceptance level.
	 */
	public String getColor() {
		return color;
	}

	/**
	 * Sets the color of the risk acceptance level.
	 *
	 * @param color The color to set for the risk acceptance level.
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * Gets the threshold of the risk acceptance level.
	 *
	 * @return The threshold of the risk acceptance level.
	 */
	public int getThreshold() {
		return threshold;
	}

	/**
	 * Sets the threshold of the risk acceptance level.
	 *
	 * @param threshold The threshold to set for the risk acceptance level.
	 */
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
}
