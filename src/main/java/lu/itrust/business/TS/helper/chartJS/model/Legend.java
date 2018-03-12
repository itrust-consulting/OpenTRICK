/**
 * 
 */
package lu.itrust.business.TS.helper.chartJS.model;

/**
 * @author eomar
 *
 */
public class Legend {
	
	private String label;
	
	private String color;

	/**
	 * 
	 */
	public Legend() {
	}

	/**
	 * @param label
	 * @param color
	 */
	public Legend(String label, String color) {
		this.label = label;
		this.color = color;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

}
