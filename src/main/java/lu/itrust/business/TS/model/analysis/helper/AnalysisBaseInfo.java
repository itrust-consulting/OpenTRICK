/**
 * 
 */
package lu.itrust.business.TS.model.analysis.helper;

import java.util.LinkedList;
import java.util.List;

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

	private String type;

	private List<AnalysisStandardBaseInfo> analysisStandardBaseInfo;

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
		setType(analysis.getType().name());
		setAnalysisStandardBaseInfo(new LinkedList<AnalysisStandardBaseInfo>());
		analysis.getAnalysisStandards().values().forEach(analysisStandard -> analysisStandardBaseInfo.add(new AnalysisStandardBaseInfo(analysis.getId(), analysisStandard)));

	}

	public AnalysisBaseInfo(int idAnalysis) {
		id = idAnalysis;
		defaultProfile = profile = true;
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

	/**
	 * @return the analysisStandardBaseInfo
	 */
	public List<AnalysisStandardBaseInfo> getAnalysisStandardBaseInfo() {
		return analysisStandardBaseInfo;
	}

	/**
	 * @param analysisStandardBaseInfo the analysisStandardBaseInfo to set
	 */
	public void setAnalysisStandardBaseInfo(List<AnalysisStandardBaseInfo> analysisStandardBaseInfo) {
		this.analysisStandardBaseInfo = analysisStandardBaseInfo;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
