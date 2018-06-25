/**
 * 
 */
package lu.itrust.business.TS.controller.form;

/**
 * @author eomar
 *
 */
public class DataManagerItem {
	
	private String name;

	private String processURL;
	
	private String viewURL;
	
	private String extensions;
	

	/**
	 * 
	 */
	public DataManagerItem() {
	}

	/**
	 * @param name
	 * @param processURL
	 * @param extensions
	 */
	public DataManagerItem(String name, String processURL, String viewURL, String extensions) {
		this.name = name;
		this.processURL = processURL;
		this.extensions = extensions;
		this.viewURL = viewURL;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProcessURL() {
		return processURL;
	}

	public void setProcessURL(String processURL) {
		this.processURL = processURL;
	}

	public String getExtensions() {
		return extensions;
	}

	public void setExtensions(String extensions) {
		this.extensions = extensions;
	}

	public String getViewURL() {
		return viewURL;
	}

	public void setViewURL(String viewURL) {
		this.viewURL = viewURL;
	}

}
