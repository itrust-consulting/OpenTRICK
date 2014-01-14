/**
 * 
 */
package lu.itrust.business.TS.messagehandler;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author eom
 *
 */
public enum TaskName {
	
	IMPORT_ANALYSIS("label.import.analysis", "/Analysis/Display"), COMPUTE_ACTION_PLAN("label.compute.analysis","/Analysis/Display"), COMPUTE_RISK_REGISTER("label.compute.risk_register","/Analysis/Display");
	
	@JsonProperty
	private String name;
	
	@JsonProperty
	private String redirect;
	
	TaskName(String name){
		this.setName(name);
		this.setRedirect(null);
	}
	
	TaskName(String name, String redirect){
		this.setName(name);
		this.setRedirect(redirect);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the redirect
	 */
	public String getRedirect() {
		return redirect;
	}

	/**
	 * @param redirect the redirect to set
	 */
	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}
}
