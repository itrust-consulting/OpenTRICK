/**
 * 
 */
package lu.itrust.business.ts.model.api.basic;

/**
 * @author eomar
 *
 */
public class ApiMeasure extends ApiNamable {
	
	private int implRate;
	
	private double cost;
	
	private double rrf;

	/**
	 * 
	 */
	public ApiMeasure() {
	}

	/**
	 * @param id
	 * @param name
	 * @param implRate
	 * @param cost
	 * @param rrf
	 */
	public ApiMeasure(Integer id, String name, int implRate, double cost, double rrf) {
		super(id, name);
		this.implRate = implRate;
		this.cost = cost;
		this.rrf = rrf;
	}

	/**
	 * @return the implRate
	 */
	public int getImplRate() {
		return implRate;
	}

	/**
	 * @param implRate the implRate to set
	 */
	public void setImplRate(int implRate) {
		this.implRate = implRate;
	}

	/**
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * @param cost the cost to set
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}

	/**
	 * @return the rrf
	 */
	public double getRrf() {
		return rrf;
	}

	/**
	 * @param rrf the rrf to set
	 */
	public void setRrf(double rrf) {
		this.rrf = rrf;
	}
	
}
