/**
 * 
 */
package lu.itrust.business.ts.form;

import lu.itrust.business.ts.model.cssf.helper.CSSFFilter;
import lu.itrust.business.ts.model.general.helper.ExportType;

/**
 * @author eomar
 *
 */
public class CSSFExportForm {
	
	private boolean cssf;
	
	private ExportType type;
	
	private String owner;
	
	private CSSFFilter filter;

	/**
	 * @return the type
	 */
	public ExportType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ExportType type) {
		this.type = type;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the filter
	 */
	public CSSFFilter getFilter() {
		return filter;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(CSSFFilter filter) {
		this.filter = filter;
	}
	
	
	public boolean hasOwner() {
		return !(owner ==null || owner.trim().isEmpty());
	}

	/**
	 * @return the cssf
	 */
	public boolean isCssf() {
		return cssf;
	}

	/**
	 * @param cssf the cssf to set
	 */
	public void setCssf(boolean cssf) {
		this.cssf = cssf;
	}
}
