/**
 * 
 */
package lu.itrust.business.ts.model.api.basic;

/**
 * Represents an API measure.
 */
public class ApiMeasure extends ApiNamable {
	
	private int implRate;
	private double cost;
	private double rrf;

	/**
	 * Default constructor.
	 */
	public ApiMeasure() {
	}

	/**
	 * Constructs an ApiMeasure object with the specified parameters.
	 *
	 * @param id       the ID of the measure
	 * @param name     the name of the measure
	 * @param implRate the implementation rate of the measure
	 * @param cost     the cost of the measure
	 * @param rrf      the relative risk factor of the measure
	 */
	public ApiMeasure(Integer id, String name, int implRate, double cost, double rrf) {
		super(id, name);
		this.implRate = implRate;
		this.cost = cost;
		this.rrf = rrf;
	}

	/**
	 * Returns the implementation rate of the measure.
	 *
	 * @return the implementation rate
	 */
	public int getImplRate() {
		return implRate;
	}

	/**
	 * Sets the implementation rate of the measure.
	 *
	 * @param implRate the implementation rate to set
	 */
	public void setImplRate(int implRate) {
		this.implRate = implRate;
	}

	/**
	 * Returns the cost of the measure.
	 *
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * Sets the cost of the measure.
	 *
	 * @param cost the cost to set
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}

	/**
	 * Returns the relative risk factor of the measure.
	 *
	 * @return the relative risk factor
	 */
	public double getRrf() {
		return rrf;
	}

	/**
	 * Sets the relative risk factor of the measure.
	 *
	 * @param rrf the relative risk factor to set
	 */
	public void setRrf(double rrf) {
		this.rrf = rrf;
	}
}
