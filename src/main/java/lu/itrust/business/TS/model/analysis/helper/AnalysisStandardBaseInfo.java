package lu.itrust.business.TS.model.analysis.helper;

import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.StandardType;

public class AnalysisStandardBaseInfo {
	
	private int idAnalysis;
	
	private int idAnalysisStandard;
	
	private String name;
	
	private int version;
	
	private StandardType type;
	
	public AnalysisStandardBaseInfo() {
	}
	
	public AnalysisStandardBaseInfo(int idAnalysis, AnalysisStandard analysisStandard) {
		setIdAnalysis(idAnalysis);
		setIdAnalysisStandard(analysisStandard.getId());
		setName(analysisStandard.getStandard().getLabel());
		setVersion(analysisStandard.getStandard().getVersion());
		setType(analysisStandard.getStandard().getType());
	}

	public AnalysisStandardBaseInfo(int idAnalysis) {
		setIdAnalysis(idAnalysis);
	}

	/**
	 * @return the idAnalysis
	 */
	public int getIdAnalysis() {
		return idAnalysis;
	}

	/**
	 * @param idAnalysis the idAnalysis to set
	 */
	public void setIdAnalysis(int idAnalysis) {
		this.idAnalysis = idAnalysis;
	}

	/**
	 * @return the idAnalysisStandard
	 */
	public int getIdAnalysisStandard() {
		return idAnalysisStandard;
	}

	/**
	 * @param idAnalysisStandard the idAnalysisStandard to set
	 */
	public void setIdAnalysisStandard(int idAnalysisStandard) {
		this.idAnalysisStandard = idAnalysisStandard;
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
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return the type
	 */
	public StandardType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(StandardType type) {
		this.type = type;
	}

}
