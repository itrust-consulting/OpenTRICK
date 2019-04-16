/**
 * 
 */
package lu.itrust.business.TS.form;

import java.util.UUID;

/**
 * @author eomar
 *
 */
public class DataManagerItem {
	
	private String name;

	private String processURL;
	
	private String viewURL;
	
	private String extensions;
	
	private String setupMethod;
	
	private String processMethod;
	
	private boolean background;
	
	private String token = UUID.randomUUID().toString();
	
	/**
	 * 
	 */
	public DataManagerItem() {
	}
	
	public DataManagerItem(String name, String processURL) {
		this.name = name;
		this.processURL = processURL;
	}
	
	

	public DataManagerItem(String name, String processURL, boolean background) {
		this(name, processURL);
		this.background = background;
	}

	public DataManagerItem(String name, String processURL, String extensions) {
		this(name, processURL);
		this.extensions = extensions;
	}
	
	/**
	 * @param name
	 * @param processURL
	 * @param extensions
	 */
	public DataManagerItem(String name, String viewURL, String processURL, String extensions) {
		this(name, processURL, extensions);
		setViewURL(viewURL);
	}
	
	public DataManagerItem(String name, String viewURL, String setupMethod, String processURL, String processMethod, String extensions) {
		this(name, viewURL, processURL, extensions);
		this.setupMethod = setupMethod;
		this.processMethod = processMethod;
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

	public String getSetupMethod() {
		return setupMethod;
	}

	public void setSetupMethod(String setupMethod) {
		this.setupMethod = setupMethod;
	}

	public String getProcessMethod() {
		return processMethod;
	}

	public void setProcessMethod(String processMethod) {
		this.processMethod = processMethod;
	}

	public boolean isBackground() {
		return background;
	}

	public void setBackground(boolean background) {
		this.background = background;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
