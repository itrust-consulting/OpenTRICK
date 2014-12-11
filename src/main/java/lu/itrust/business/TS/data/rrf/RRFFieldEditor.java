/**
 * 
 */
package lu.itrust.business.TS.data.rrf;

import lu.itrust.business.TS.component.FieldEditor;

/**
 * @author eomar
 *
 */
public class RRFFieldEditor extends FieldEditor {
	
	private RRFFilter filter;
	
	/**
	 * 
	 */
	public RRFFieldEditor() {
	}

	/**
	 * @return the filter
	 */
	public RRFFilter getFilter() {
		return filter;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(RRFFilter filter) {
		this.filter = filter;
	}

}
