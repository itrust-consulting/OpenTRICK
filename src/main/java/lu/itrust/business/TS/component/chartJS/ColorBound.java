/**
 * 
 */
package lu.itrust.business.TS.component.chartJS;

import com.gargoylesoftware.htmlunit.javascript.host.Console;

/**
 * @author eomar
 *
 */
public class ColorBound {

	private String color;

	private int min;

	private int max;

	public ColorBound(String color, int min, int max) {
		setColor(color);
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
		return value>= min && value <= max;

	}

}
