/**
 * 
 */
package lu.itrust.business.component.helper;

import java.sql.Timestamp;

import lu.itrust.business.TS.History;

/**
 * @author eomar
 *
 */
public class CustomAnalysisForm {

	private boolean assessment;

	private int asset;

	private String author;

	private String comment;

	private int customer;

	private int language;

	private int parameter;

	private boolean phase;

	private int riskInformation;

	private int scenario;

	private int scope;

	private int standard;

	private String version;

	/**
	 * 
	 */
	public CustomAnalysisForm() {
	}

	public boolean isAssessment() {
		return assessment;
	}

	public void setAssessment(boolean assessment) {
		this.assessment = assessment;
	}

	public int getAsset() {
		return asset;
	}

	public void setAsset(int asset) {
		this.asset = asset;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getCustomer() {
		return customer;
	}

	public void setCustomer(int customer) {
		this.customer = customer;
	}

	public int getLanguage() {
		return language;
	}

	public void setLanguage(int language) {
		this.language = language;
	}

	public int getParameter() {
		return parameter;
	}

	public void setParameter(int parameter) {
		this.parameter = parameter;
	}

	public boolean isPhase() {
		return phase;
	}

	public void setPhase(boolean phase) {
		this.phase = phase;
	}

	public int getRiskInformation() {
		return riskInformation;
	}

	public void setRiskInformation(int riskInformation) {
		this.riskInformation = riskInformation;
	}

	public int getScenario() {
		return scenario;
	}

	public void setScenario(int scenario) {
		this.scenario = scenario;
	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public int getStandard() {
		return standard;
	}

	public void setStandard(int standard) {
		this.standard = standard;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "CustomAnalysisForm [assessment=" + assessment + ", asset=" + asset + ", author=" + author + ", comment=" + comment + ", customer=" + customer + ", language="
				+ language + ", parameter=" + parameter + ", phase=" + phase + ", riskInformation=" + riskInformation + ", scenario=" + scenario + ", scope=" + scope
				+ ", standard=" + standard + ", version=" + version + "]";
	}

	public History generateHistory() {
		return new History(version, new Timestamp(System.currentTimeMillis()), author, comment);
	}

	public void setDefaultProfile(int defaultProfileId) {
		if (scope < 1)
			scope = defaultProfileId;
		if (riskInformation < 1)
			riskInformation = defaultProfileId;
		if (parameter < 1)
			parameter = defaultProfileId;

	}

}
