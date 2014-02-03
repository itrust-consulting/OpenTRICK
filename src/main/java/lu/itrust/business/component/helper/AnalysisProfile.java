/**
 * 
 */
package lu.itrust.business.component.helper;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.Norm;

/**
 * @author eomar
 * 
 */
public class AnalysisProfile {

	private String name;

	private int idAnalysis;

	private List<Norm> norms = new ArrayList<Norm>();

	private boolean parameter;

	private boolean itemInformation;

	private boolean riskInformation;

	private boolean scenario;

	private boolean asset;

	/**
	 * 
	 */
	public AnalysisProfile() {
	}

	public AnalysisProfile(int analysisId) {
		setIdAnalysis(analysisId);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the norms
	 */
	public List<Norm> getNorms() {
		return norms;
	}

	/**
	 * @param norms
	 *            the norms to set
	 */
	public void setNorms(List<Norm> norms) {
		this.norms = norms;
	}

	/**
	 * @return the idAnalysis
	 */
	public int getIdAnalysis() {
		return idAnalysis;
	}

	/**
	 * @param idAnalysis
	 *            the idAnalysis to set
	 */
	public void setIdAnalysis(int idAnalysis) {
		this.idAnalysis = idAnalysis;
	}

	/**
	 * @return the parameter
	 */
	public boolean isParameter() {
		return parameter;
	}

	/**
	 * @param parameter
	 *            the parameter to set
	 */
	public void setParameter(boolean parameter) {
		this.parameter = parameter;
	}

	

	/**
	 * @return the riskInformation
	 */
	public boolean isRiskInformation() {
		return riskInformation;
	}

	/**
	 * @param riskInformation
	 *            the riskInformation to set
	 */
	public void setRiskInformation(boolean riskInformation) {
		this.riskInformation = riskInformation;
	}

	/**
	 * @return the scenario
	 */
	public boolean isScenario() {
		return scenario;
	}

	/**
	 * @param scenario
	 *            the scenario to set
	 */
	public void setScenario(boolean scenario) {
		this.scenario = scenario;
	}

	/**
	 * @return the asset
	 */
	public boolean isAsset() {
		return asset;
	}

	/**
	 * @param asset
	 *            the asset to set
	 */
	public void setAsset(boolean asset) {
		this.asset = asset;
	}

	/**
	 * @return the itemInformation
	 */
	public boolean isItemInformation() {
		return itemInformation;
	}

	/**
	 * @param itemInformation the itemInformation to set
	 */
	public void setItemInformation(boolean itemInformation) {
		this.itemInformation = itemInformation;
	}

}
