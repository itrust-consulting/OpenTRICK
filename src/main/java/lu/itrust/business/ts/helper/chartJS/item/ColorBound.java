/**
 * 
 */
package lu.itrust.business.ts.helper.chartJS.item;

/**
 * @author eomar
 *
 */
public class ColorBound {

	private String color;

	private String label;

	private int min;

	private int max;

	private int count;

	public ColorBound(String color, String label, int min, int max) {
		setColor(color);
		setLabel(label);
		setMin(min);
		setMax(max);
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return the min
	 */
	public int getMin() {
		return min;
	}

	/**
	 * @param min
	 *            the min to set
	 */
	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @param max
	 *            the max to set
	 */
	public void setMax(int max) {
		this.max = max;
	}

	public boolean isAccepted(int value) {
		return min == 0 ? value >= min && value <= max : value > min && value <= max;

	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

}
