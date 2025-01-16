/**
 * 
 */
package lu.itrust.business.ts.messagehandler;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author eom
 *
 */
public enum TaskName {

	COMPUTE_ACTION_PLAN("label.title.compute.action_plan"),
	COMPUTE_DYNAMIC_PARAMETER("label.title.compute.dynamic.parameter"),
	COMPUTE_RISK_REGISTER("label.title.compute.risk_register"),
	CREATE_ANALYSIS_PROFILE("label.title.create.analysis.profile"),
	CREATE_ANALYSIS_VERSION("label.title.create.analysis.version"), 
	EXPORT_ANALYSIS("label.title.export.analysis"),
	EXPORT_ANALYSIS_REPORT("label.title.export.analysis.report"),
	EXPORT_RISK_ESTIMATION("label.title.export.risk_estimation"),
	EXPORT_RISK_REGISTER("label.title.export.risk_register"), 
	EXPORT_RISK_SHEET("label.title.export.risk_sheet"),
	EXPORT_SOA("label.title.export.soa"), 
	GENERATE_TICKETS("label.title.generate.ticket"),
	IMPORT_ANALYSIS("label.title.import.analysis"), 
	IMPORT_ASSET("label.title.import.asset"),
	IMPORT_ITEM_INFORMATION("label.title.import.item.information"),
	IMPORT_MEASURE_COLLECTION("label.title.import.measure.collection"),
	IMPORT_MEASURE_DATA("label.title.import.measure.data"),
	IMPORT_RISK_ESTIMATION("label.title.import.risk.estimation"),
	IMPORT_RISK_INFORMATION("label.title.import.risk.information"), 
	IMPORT_SCENARIO("label.title.import.scenario"),
	INSTALL_APPLICATION("label.title.install.application"), 
	RESET_ANALYSIS_RIGHT("label.title.reset.analysis.right"),
	SCALE_LEVEL_MIGRATE("label.scale.level.migrate"),
	SYNCHRONIZE_ANALYSES_MEASURE_COLLECION("label.title.synchronise.analyses.measure.collection");

	private String action;

	private String name;

	TaskName(String name) {
		this.setName(name);
		this.setAction(null);
	}

	TaskName(String name, String redirect) {
		this.setName(name);
		this.setAction(redirect);
	}

	/**
	 * @return the action
	 */
	@JsonProperty
	public String getAction() {
		return action;
	}

	/**
	 * @return the name
	 */
	@JsonProperty
	public String getName() {
		return name;
	}

	/**
	 * @param action the redirect to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
