/**
 * 
 */
package lu.itrust.business.TS.model.analysis.helper;

import lu.itrust.business.TS.model.analysis.Analysis;


/**
 * @author eomar
 *
 */
public class AnalysisBaseInfo {

	/** Analysis id unsaved value = -1 */
	private int id = -1;

	private boolean profile = false;

	private boolean defaultProfile = false;

	/** ID of the Analysis */
	private String identifier;

	/** Version of the Analysis */
	private String version;

	/** The Label of this Analysis */
	private String label;

	/** flag to determine if analysis has data */
	private boolean empty;

	/**
	 * 
	 */
	public AnalysisBaseInfo() {
	}

	public AnalysisBaseInfo(Analysis analysis) {
		setId(analysis.getId());
		setProfile(analysis.isProfile());
		setDefaultProfile(analysis.isDefaultProfile());
		setIdentifier(analysis.getIdentifier());
		setVersion(analysis.getVersion());
		setLabel(analysis.getLabel());
		setEmpty(!analysis.hasData());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isProfile() {
		return profile;
	}

	public void setProfile(boolean profile) {
		this.profile = profile;
	}

	public boolean isDefaultProfile() {
		return defaultProfile;
	}

	public void setDefaultProfile(boolean defaultProfile) {
		this.defaultProfile = defaultProfile;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	public boolean hasData() {
		return !isEmpty();
	}

}
