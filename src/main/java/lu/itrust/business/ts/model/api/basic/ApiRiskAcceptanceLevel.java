package lu.itrust.business.ts.model.api.basic;

import lu.itrust.business.ts.model.parameter.impl.RiskAcceptanceParameter;

public class ApiRiskAcceptanceLevel {
	private String label;
	private String description;
	private String color;
	private int threshold;
	
	public static ApiRiskAcceptanceLevel create(RiskAcceptanceParameter parameter) {
		ApiRiskAcceptanceLevel o = new ApiRiskAcceptanceLevel();
		o.setLabel(parameter.getLabel());
		o.setDescription(parameter.getDescription());
		o.setColor(parameter.getColor());
		o.setThreshold(parameter.getValue().intValue());
		return o;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
}
