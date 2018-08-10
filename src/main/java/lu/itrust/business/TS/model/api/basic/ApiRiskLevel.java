package lu.itrust.business.TS.model.api.basic;

import lu.itrust.business.TS.model.parameter.IBoundedParameter;

/**
 * Represents a level on a likelihood/impact scale.
 * @author steve.muller@itrust.lu
 * @since 2018-07-23
 */
public class ApiRiskLevel {
	private int level;
	private String label;
	private String description;
	private double value;
	private double valueBoundsFrom;
	private double valueBoundsTo;
	
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

	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
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
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public double getValueBoundsFrom() {
		return valueBoundsFrom;
	}
	public void setValueBoundsFrom(double valueBoundsFrom) {
		this.valueBoundsFrom = valueBoundsFrom;
	}
	public double getValueBoundsTo() {
		return valueBoundsTo;
	}
	public void setValueBoundsTo(double valueBoundsTo) {
		this.valueBoundsTo = valueBoundsTo;
	}
}
