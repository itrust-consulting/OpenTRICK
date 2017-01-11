/**
 * 
 */
package lu.itrust.business.TS.component.chartJS;

import java.util.LinkedList;
import java.util.List;

/**
 * @author eomar
 *
 */
public class Dataset {
	
	private String label;
	
	private List<Object> data = new LinkedList<>();
	
	private List<String> backgroundColor =  new LinkedList<>();

	/**
	 * @return the backgroundColor
	 */
	public List<String> getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(List<String> backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * @return the data
	 */
	public List<Object> getData() {
		return data;
	}

	/**
	 * @param data the data to set
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
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	

}
