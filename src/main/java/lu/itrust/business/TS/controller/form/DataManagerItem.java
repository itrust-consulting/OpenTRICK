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

	private String proccessURL;
	
	private String viewURL;
	
	private String extensions;
	

	/**
	 * 
	 */
	public DataManagerItem() {
	}

	/**
	 * @param name
	 * @param proccessURL
	 * @param extensions
	 */
	public DataManagerItem(String name, String proccessURL, String viewURL, String extensions) {
		this.name = name;
		this.proccessURL = proccessURL;
		this.extensions = extensions;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProccessURL() {
		return proccessURL;
	}

	public void setProccessURL(String proccessURL) {
		this.proccessURL = proccessURL;
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
