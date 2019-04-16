/**
 * 
 */
package lu.itrust.business.TS.form;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.analysis.helper.AnalysisStandardBaseInfo;
import lu.itrust.business.TS.model.history.History;
import lu.itrust.business.TS.model.scale.Scale;

/**
 * @author eomar
 *
 */
public class AnalysisForm {

	private boolean assessment;

	private boolean riskProfile;

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

	private List<AnalysisStandardBaseInfo> standards;

	private List<Integer> impacts = Collections.emptyList();

	private Scale scale;

	private String version;

	private boolean uncertainty;

	private AnalysisType type;

	/**
	 * 
	 */
	public AnalysisForm() {
	}

	public boolean isAssessment() {
		return assessment && asset > 0 && asset == scenario && asset == parameter;
	}

	public void setAssessment(boolean assessment) {
		this.assessment = assessment;
	}

	/**
	 * @return the riskProfile
	 */
	public boolean isRiskProfile() {
		return riskProfile && AnalysisType.isQualitative(type) && asset > 0 && asset == scenario && asset == parameter;
	}

	/**
	 * @param riskProfile
	 *            the riskProfile to set
	 */
	public void setRiskProfile(boolean riskProfile) {
		this.riskProfile = riskProfile;
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "AnalysisForm [assessment=" + assessment + ", asset=" + asset + ", author=" + author + ", comment=" + comment + ", customer=" + customer + ", language=" + language
				+ ", parameter=" + parameter + ", phase=" + phase + ", riskInformation=" + riskInformation + ", scenario=" + scenario + ", scope=" + scope + ", standards="
				+ this.standards + ", version=" + version + "]";
	}

	public History generateHistory() {
		return new History(version, new Timestamp(System.currentTimeMillis()), author, comment);
	}

	public void updateProfile() {
		if (scope < 1)
			scope = profile;
		if (riskInformation < 1)
			riskInformation = profile;
		if (parameter < 1)
			parameter = profile;
		if (standards == null || standards.isEmpty()) {
			if (standards == null)
				standards = new LinkedList<AnalysisStandardBaseInfo>();
			standards.add(new AnalysisStandardBaseInfo(profile));
		}

		if (scenario < 1)
			scenario = profile;
	}

	public int getProfile() {
		return profile;
	}

	public void setProfile(int profile) {
		this.profile = profile;
	}

	/**
	 * isUncertainty: <br>
	 * Returns the uncertainty field value.
	 * 
	 * @return The value of the uncertainty field
	 */
	public boolean isUncertainty() {
		return uncertainty;
	}

	/**
	 * setUncertainty: <br>
	 * Sets the Field "uncertainty" with a value.
	 * 
	 * @param uncertainty
	 *            The Value to set the uncertainty field
	 */
	public void setUncertainty(boolean uncertainty) {
		this.uncertainty = uncertainty;
	}

	/**
	 * @return the type
	 */
	public AnalysisType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(AnalysisType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the standrads
	 */
	public List<AnalysisStandardBaseInfo> getStandards() {
		return standards;
	}

	/**
	 * @param standrads
	 *            the standrads to set
	 */
	public void setStandards(List<AnalysisStandardBaseInfo> standards) {
		this.standards = standards;
	}

	/**
	 * @return the impacts
	 */
	public List<Integer> getImpacts() {
		return impacts;
	}

	/**
	 * @param impacts
	 *            the impacts to set
	 */
	public void setImpacts(List<Integer> impacts) {
		this.impacts = impacts;
	}

	/**
	 * @return the scale
	 */
	public Scale getScale() {
		return scale;
	}

	/**
	 * @param scale
	 *            the scale to set
	 */
	public void setScale(Scale scale) {
		this.scale = scale;
	}

}
