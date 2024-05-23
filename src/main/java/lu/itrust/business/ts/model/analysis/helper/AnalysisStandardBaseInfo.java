package lu.itrust.business.ts.model.analysis.helper;

import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.StandardType;

/**
 * Represents the base information of an analysis standard.
 */
public class AnalysisStandardBaseInfo {
	
	private int idAnalysis;
	private int idAnalysisStandard;
	private String name;
	private int version;
	private StandardType type;
	
	/**
	 * Default constructor.
	 */
	public AnalysisStandardBaseInfo() {
	}
	
	/**
	 * Constructs an AnalysisStandardBaseInfo object with the given analysis ID and analysis standard.
	 * @param idAnalysis The ID of the analysis.
	 * @param analysisStandard The analysis standard.
	 */
	public AnalysisStandardBaseInfo(int idAnalysis, AnalysisStandard analysisStandard) {
		setIdAnalysis(idAnalysis);
		setIdAnalysisStandard(analysisStandard.getId());
		setName(analysisStandard.getStandard().getName());
		setVersion(analysisStandard.getStandard().getVersion());
		setType(analysisStandard.getStandard().getType());
	}

	/**
	 * Constructs an AnalysisStandardBaseInfo object with the given analysis ID.
	 * @param idAnalysis The ID of the analysis.
	 */
	public AnalysisStandardBaseInfo(int idAnalysis) {
		setIdAnalysis(idAnalysis);
	}

	/**
	 * Returns the ID of the analysis.
	 * @return The ID of the analysis.
	 */
	public int getIdAnalysis() {
		return idAnalysis;
	}

	/**
	 * Sets the ID of the analysis.
	 * @param idAnalysis The ID of the analysis to set.
	 */
	public void setIdAnalysis(int idAnalysis) {
		this.idAnalysis = idAnalysis;
	}

	/**
	 * Returns the ID of the analysis standard.
	 * @return The ID of the analysis standard.
	 */
	public int getIdAnalysisStandard() {
		return idAnalysisStandard;
	}

	/**
	 * Sets the ID of the analysis standard.
	 * @param idAnalysisStandard The ID of the analysis standard to set.
	 */
	public void setIdAnalysisStandard(int idAnalysisStandard) {
		this.idAnalysisStandard = idAnalysisStandard;
	}

	/**
	 * Returns the name of the analysis standard.
	 * @return The name of the analysis standard.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the analysis standard.
	 * @param name The name of the analysis standard to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the version of the analysis standard.
	 * @return The version of the analysis standard.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Sets the version of the analysis standard.
	 * @param version The version of the analysis standard to set.
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * Returns the type of the analysis standard.
	 * @return The type of the analysis standard.
	 */
	public StandardType getType() {
		return type;
	}

	/**
	 * Sets the type of the analysis standard.
	 * @param type The type of the analysis standard to set.
	 */
	public void setType(StandardType type) {
		this.type = type;
	}

}
