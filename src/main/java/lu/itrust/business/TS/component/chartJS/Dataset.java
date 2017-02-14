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
	
	private T backgroundColor =  null;
	
	public Dataset(T backgroundColor) {
		setBackgroundColor(backgroundColor);
	}

	public Dataset(String label, T backgroundColor ) {
		setLabel(label);
		setBackgroundColor(backgroundColor);
	}

	/**
	 * @return the backgroundColor
	 */
	public T getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor the backgroundColor to set
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
