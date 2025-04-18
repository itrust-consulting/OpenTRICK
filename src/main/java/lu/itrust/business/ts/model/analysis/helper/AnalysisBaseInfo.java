/**
 * 
 */
package lu.itrust.business.ts.model.analysis.helper;

import java.util.LinkedList;
import java.util.List;

import lu.itrust.business.ts.model.analysis.Analysis;

/**
 * The `AnalysisBaseInfo` class represents the base information of an analysis.
 * It contains properties such as the analysis ID, profile status, identifier, version, label, and more.
 * This class is used to store and retrieve information about an analysis.
 */
public class AnalysisBaseInfo {

	/** Analysis id unsaved value = -1 */
	private int id = 0;

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
	 * Default constructor for AnalysisBaseInfo.
	 */
	public AnalysisBaseInfo() {
	}

	/**
	 * Constructor for AnalysisBaseInfo with an existing Analysis object.
	 * 
	 * @param analysis The Analysis object to initialize the AnalysisBaseInfo from.
	 */
	public AnalysisBaseInfo(Analysis analysis) {
		setId(analysis.getId());
		setProfile(analysis.isProfile());
		setDefaultProfile(analysis.isDefaultProfile());
		setIdentifier(analysis.getIdentifier());
		setVersion(analysis.getVersion());
		setLabel(analysis.getLabel());
		setEmpty(!analysis.hasData());
		setType(analysis.getType().name());
		setAnalysisStandardBaseInfo(new LinkedList<>());
		analysis.getAnalysisStandards().values().forEach(analysisStandard -> analysisStandardBaseInfo.add(new AnalysisStandardBaseInfo(analysis.getId(), analysisStandard)));

	}

	/**
	 * Constructor for AnalysisBaseInfo with a specific analysis ID.
	 * 
	 * @param idAnalysis The ID of the analysis.
	 */
	public AnalysisBaseInfo(int idAnalysis) {
		id = idAnalysis;
		defaultProfile = profile = true;
	}

	/**
	 * Get the ID of the analysis.
	 * 
	 * @return The ID of the analysis.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the ID of the analysis.
	 * 
	 * @param id The ID of the analysis.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Check if the analysis is a profile.
	 * 
	 * @return True if the analysis is a profile, false otherwise.
	 */
	public boolean isProfile() {
		return profile;
	}

	/**
	 * Set whether the analysis is a profile.
	 * 
	 * @param profile True if the analysis is a profile, false otherwise.
	 */
	public void setProfile(boolean profile) {
		this.profile = profile;
	}

	/**
	 * Check if the analysis is the default profile.
	 * 
	 * @return True if the analysis is the default profile, false otherwise.
	 */
	public boolean isDefaultProfile() {
		return defaultProfile;
	}

	/**
	 * Set whether the analysis is the default profile.
	 * 
	 * @param defaultProfile True if the analysis is the default profile, false otherwise.
	 */
	public void setDefaultProfile(boolean defaultProfile) {
		this.defaultProfile = defaultProfile;
	}

	/**
	 * Get the identifier of the analysis.
	 * 
	 * @return The identifier of the analysis.
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Set the identifier of the analysis.
	 * 
	 * @param identifier The identifier of the analysis.
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Get the version of the analysis.
	 * 
	 * @return The version of the analysis.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Set the version of the analysis.
	 * 
	 * @param version The version of the analysis.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Get the label of the analysis.
	 * 
	 * @return The label of the analysis.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set the label of the analysis.
	 * 
	 * @param label The label of the analysis.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Check if the analysis is empty (has no data).
	 * 
	 * @return True if the analysis is empty, false otherwise.
	 */
	public boolean isEmpty() {
		return empty;
	}

	/**
	 * Set whether the analysis is empty (has no data).
	 * 
	 * @param empty True if the analysis is empty, false otherwise.
	 */
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	/**
	 * Check if the analysis has data.
	 * 
	 * @return True if the analysis has data, false otherwise.
	 */
	public boolean hasData() {
		return !isEmpty();
	}

	/**
	 * Get the list of analysis standard base information.
	 * 
	 * @return The list of analysis standard base information.
	 */
	public List<AnalysisStandardBaseInfo> getAnalysisStandardBaseInfo() {
		return analysisStandardBaseInfo;
	}

	/**
	 * Set the list of analysis standard base information.
	 * 
	 * @param analysisStandardBaseInfo The list of analysis standard base information.
	 */
	public void setAnalysisStandardBaseInfo(List<AnalysisStandardBaseInfo> analysisStandardBaseInfo) {
		this.analysisStandardBaseInfo = analysisStandardBaseInfo;
	}

	/**
	 * Get the type of the analysis.
	 * 
	 * @return The type of the analysis.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the type of the analysis.
	 * 
	 * @param type The type of the analysis.
	 */
	public void setType(String type) {
		this.type = type;
	}

}
