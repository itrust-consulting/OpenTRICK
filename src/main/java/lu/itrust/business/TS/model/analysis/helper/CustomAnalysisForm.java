/**
 * 
 */
package lu.itrust.business.TS.model.analysis.helper;

import java.sql.Timestamp;

import lu.itrust.business.TS.model.history.History;

/**
 * @author eomar
 *
 */
public class CustomAnalysisForm {

	private boolean assessment;

	private int asset;

	private String author;
	
	private String name;

	private String comment;

	private int profile;

	private int customer;

	private int language;

	private int parameter;

	private boolean phase;

	private int riskInformation;

	private int scenario;

	private int scope;

	private int standard;

	private String version;
	
	private boolean uncertainty;
	
	private boolean cssf;

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
		if (profile < 1)
			profile = defaultProfileId;
		if (scope < 1)
			scope = defaultProfileId;
		if (riskInformation < 1)
			riskInformation = defaultProfileId;
		if (parameter < 1)
			parameter = defaultProfileId;
		if (standard < 1)
			standard = defaultProfileId;
		if (scenario < 1)
			scenario = defaultProfileId;
	}

	public int getProfile() {
		return profile;
	}

	public void setProfile(int profile) {
		this.profile = profile;
	}

	/** isUncertainty: <br>
	 * Returns the uncertainty field value.
	 * 
	 * @return The value of the uncertainty field
	 */
	public boolean isUncertainty() {
		return uncertainty;
	}

	/** setUncertainty: <br>
	 * Sets the Field "uncertainty" with a value.
	 * 
	 * @param uncertainty 
	 * 			The Value to set the uncertainty field
	 */
	public void setUncertainty(boolean uncertainty) {
		this.uncertainty = uncertainty;
	}

	/** isCssf: <br>
	 * Returns the cssf field value.
	 * 
	 * @return The value of the cssf field
	 */
	public boolean isCssf() {
		return cssf;
	}

	/** setCssf: <br>
	 * Sets the Field "cssf" with a value.
	 * 
	 * @param cssf 
	 * 			The Value to set the cssf field
	 */
	public void setCssf(boolean cssf) {
		this.cssf = cssf;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
