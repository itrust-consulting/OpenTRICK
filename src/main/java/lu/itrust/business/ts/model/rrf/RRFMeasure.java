/**
 * 
 */
package lu.itrust.business.ts.model.rrf;


/**
 * Represents a measurement of RRF (Relative Response Factor).
 */
public class RRFMeasure {
	
	private int id;
	
	private String reference;
	
	private double value;
	
	/**
	 * Default constructor.
	 */
	public RRFMeasure() {
	}

	/**
	 * Constructs a new RRFMeasure object with the specified id and reference.
	 * 
	 * @param id        the ID of the RRF measure
	 * @param reference the reference of the RRF measure
	 */
	public RRFMeasure(int id, String reference) {
		this.id = id;
		this.reference = reference;
	}
	
	/**
	 * Constructs a new RRFMeasure object with the specified id, reference, and value.
	 * 
	 * @param id        the ID of the RRF measure
	 * @param reference the reference of the RRF measure
	 * @param value     the value of the RRF measure
	 */
	public RRFMeasure(int id, String reference, double value) {
		this.id = id;
		this.reference = reference;
		this.value = value;
	}

	/**
	 * Returns the reference of the RRF measure.
	 * 
	 * @return the reference of the RRF measure
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * Sets the reference of the RRF measure.
	 * 
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * Returns the ID of the RRF measure.
	 * 
	 * @return the ID of the RRF measure
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the ID of the RRF measure.
	 * 
	 * @param id the ID to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the value of the RRF measure.
	 * 
	 * @return the value of the RRF measure
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Sets the value of the RRF measure.
	 * 
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}
}
