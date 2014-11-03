/**
 * 
 */
package lu.itrust.business.TS.component.helper;


/**
 * @author eomar
 *
 */
public class RRFMeasure {
	
	private int id;
	
	private String reference;
	
	private double value;
	
	/**
	 * 
	 */
	public RRFMeasure() {
	}

	/**
	 * @param id
	 * @param reference
	 */
	public RRFMeasure(int id, String reference) {
		this.id = id;
		this.reference = reference;
	}
	
	/**
	 * @param id
	 * @param reference
	 * @param value
	 */
	public RRFMeasure(int id, String reference, double value) {
		this.id = id;
		this.reference = reference;
		this.value = value;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
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
