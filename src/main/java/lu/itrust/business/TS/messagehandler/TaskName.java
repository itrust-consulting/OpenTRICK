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
	
	IMPORT_ANALYSIS("label.import.analysis", "reloadSection(\"section_analysis\")"), EXPORT_ANALYSIS("label.compute.risk_register","\"downloadExportedSqlLite()\""), COMPUTE_ACTION_PLAN("label.compute.actionPlan","\"reloadSection(\"section_actionplans\")\""), COMPUTE_RISK_REGISTER("label.compute.risk_register","\"reloadSection(\"section_riskregisters\")\"");
	
	@JsonProperty
	private String name;
	
	@JsonProperty
	private String action;
	
	TaskName(String name){
		this.setName(name);
		this.setAction(null);
	}
	
	TaskName(String name, String redirect){
		this.setName(name);
		this.setAction(redirect);
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
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the redirect to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
}
