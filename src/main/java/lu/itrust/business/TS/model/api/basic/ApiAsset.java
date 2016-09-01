/**
 * 
 */
package lu.itrust.business.TS.model.api.basic;

/**
 * @author eomar
 *
 */
public class ApiAsset extends ApiNamable {
	
	private double value;
	
	/**
	 * 
	 */
	public ApiAsset() {
	}

	/**
	 * @param id
	 * @param name
	 * @param value
	 */
	public ApiAsset(Integer id, String name, double value) {
		super(id, name);
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

}
