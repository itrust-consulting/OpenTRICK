/**
 * 
 */
package lu.itrust.business.TS.controller.form;

import java.util.LinkedList;
import java.util.List;

/**
 * @author eomar
 *
 */
public class DataManagerImportForm {
	
	private List<DataManagerItem> items = new LinkedList<>();

	/**
	 * 
	 */
	public DataManagerImportForm() {
	}

	/**
	 * @param items
	 */
	public DataManagerImportForm(List<DataManagerItem> items) {
		this.items = items;
	}

	public List<DataManagerItem> getItems() {
		return items;
	}

	public void setItems(List<DataManagerItem> items) {
		this.items = items;
	}
}
