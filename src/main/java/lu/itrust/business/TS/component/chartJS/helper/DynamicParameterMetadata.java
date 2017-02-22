/**
 * 
 */
package lu.itrust.business.TS.component.chartJS.helper;

/**
 * @author eomar
 *
 */
public class DynamicParameterMetadata {
	
	private String name;
	
	private ValueMetadata<Double> ale;
	
	private ValueMetadata<Double> value;

	/**
	 * 
	 */
	public DynamicParameterMetadata() {
	}

	/**
	 * @param name
	 * @param ale
	 * @param value
	 */
	public DynamicParameterMetadata(String name, ValueMetadata<Double> ale, ValueMetadata<Double> value) {
		this.setName(name);
		this.setAle(ale);
		this.setValue(value);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the ale
	 */
	public ValueMetadata<Double> getAle() {
		return ale;
	}

	/**
	 * @param ale the ale to set
	 */
	public void setAle(ValueMetadata<Double> ale) {
		this.ale = ale;
	}

	/**
	 * @return the value
	 */
	public ValueMetadata<Double> getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(ValueMetadata<Double> value) {
		this.value = value;
	}

}
