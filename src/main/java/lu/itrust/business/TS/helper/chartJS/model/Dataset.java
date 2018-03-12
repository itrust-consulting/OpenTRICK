/**
 * 
 */
package lu.itrust.business.TS.helper.chartJS.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author eomar
 *
 */
@JsonInclude(value = Include.NON_EMPTY)
public class Dataset<T> {

	private String label;
	
	private String title;
	
	private String legendText;

	private List<Object> data = new ArrayList<>();
	
	private List<Object> metaData = new ArrayList<>(); 
	
	private String type;
	
	private String stack;
	
	private String xAxisID;
	
	private String yAxisID;
	
	private boolean fill;

	private T backgroundColor = null;

	private T borderColor = null;

	private T pointBackgroundColor = null;

	private boolean hidden;

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

	public Dataset(String label, T backgroundColor, String type) {
		this(label, backgroundColor);
		setType(type);
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

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the stack
	 */
	public String getStack() {
		return stack;
	}

	/**
	 * @param stack the stack to set
	 */
	public void setStack(String stack) {
		this.stack = stack;
	}

	/**
	 * @return the metaData
	 */
	public List<Object> getMetaData() {
		return metaData;
	}

	/**
	 * @param metaData the metaData to set
	 */
	public void setMetaData(List<Object> metaData) {
		this.metaData = metaData;
	}

	public String getyAxisID() {
		return yAxisID;
	}

	public void setyAxisID(String yAxisID) {
		this.yAxisID = yAxisID;
	}

	public String getxAxisID() {
		return xAxisID;
	}

	public void setxAxisID(String xAxisID) {
		this.xAxisID = xAxisID;
	}

	public void setFill(boolean fill) {
		this.fill = fill;
		
	}

	public boolean isFill() {
		return fill;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLegendText() {
		return legendText;
	}

	public void setLegendText(String legendText) {
		this.legendText = legendText;
	}

}
