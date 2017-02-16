/**
 * 
 */
package lu.itrust.business.TS.component.chartJS;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eomar
 *
 */
public class Dataset<T> {

	private String label;

	private List<Object> data = new ArrayList<>();

	private T backgroundColor = null;

	private T borderColor = null;

	private T pointBackgroundColor = null;

	public Dataset(T backgroundColor) {
		setBackgroundColor(backgroundColor);
		if (backgroundColor instanceof String) {
			setBorderColor(backgroundColor);
			setPointBackgroundColor(backgroundColor);
		}
	}

	public Dataset(String label, T backgroundColor) {
		setLabel(label);
		setBackgroundColor(backgroundColor);
		if (backgroundColor instanceof String) {
			setBorderColor(backgroundColor);
			setPointBackgroundColor(backgroundColor);
		}
	}

	/**
	 * @return the backgroundColor
	 */
	public T getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor
	 *            the backgroundColor to set
	 */
	public void setBackgroundColor(T backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * @return the data
	 */
	public List<Object> getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(List<Object> data) {
		this.data = data;
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

	/**
	 * @return the borderColor
	 */
	public T getBorderColor() {
		return borderColor;
	}

	/**
	 * @param borderColor
	 *            the borderColor to set
	 */
	public void setBorderColor(T borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * @return the pointBackgroundColor
	 */
	public T getPointBackgroundColor() {
		return pointBackgroundColor;
	}

	/**
	 * @param pointBackgroundColor
	 *            the pointBackgroundColor to set
	 */
	public void setPointBackgroundColor(T pointBackgroundColor) {
		this.pointBackgroundColor = pointBackgroundColor;
	}

}
