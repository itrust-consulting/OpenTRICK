/**
 * 
 */
package lu.itrust.business.ts.model.analysis.helper;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.ts.model.standard.Standard;


/**
 * Represents an analysis profile.
 */
public class AnalysisProfile {

	/** The name of the analysis profile. */
	private String name;

	/** The ID of the analysis. */
	private int idAnalysis;

	/** The list of standards associated with the analysis profile. */
	private List<Standard> standards = new ArrayList<Standard>();

	/** Indicates whether the analysis profile is a scenario. */
	private boolean scenario = true;

	/**
	 * Default constructor for the AnalysisProfile class.
	 */
	public AnalysisProfile() {
	}

	/**
	 * Constructor for the AnalysisProfile class.
	 * 
	 * @param analysisId The ID of the analysis.
	 */
	public AnalysisProfile(int analysisId) {
		setIdAnalysis(analysisId);
	}

	/**
	 * Gets the name of the analysis profile.
	 * 
	 * @return The name of the analysis profile.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the analysis profile.
	 * 
	 * @param name The name of the analysis profile.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the list of standards associated with the analysis profile.
	 * 
	 * @return The list of standards associated with the analysis profile.
	 */
	public List<Standard> getStandards() {
		return standards;
	}

	/**
	 * Sets the list of standards associated with the analysis profile.
	 * 
	 * @param standards The list of standards associated with the analysis profile.
	 */
	public void setStandards(List<Standard> standards) {
		this.standards = standards;
	}

	/**
	 * Gets the ID of the analysis.
	 * 
	 * @return The ID of the analysis.
	 */
	public int getIdAnalysis() {
		return idAnalysis;
	}

	/**
	 * Sets the ID of the analysis.
	 * 
	 * @param idAnalysis The ID of the analysis.
	 */
	public void setIdAnalysis(int idAnalysis) {
		this.idAnalysis = idAnalysis;
	}

	/**
	 * Checks if the analysis profile is a scenario.
	 * 
	 * @return true if the analysis profile is a scenario, false otherwise.
	 */
	public boolean isScenario() {
		return scenario;
	}

	/**
	 * Sets whether the analysis profile is a scenario.
	 * 
	 * @param scenario true if the analysis profile is a scenario, false otherwise.
	 */
	public void setScenario(boolean scenario) {
		this.scenario = scenario;
	}
}