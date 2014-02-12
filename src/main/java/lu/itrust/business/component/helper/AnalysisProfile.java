/**
 * 
 */
package lu.itrust.business.component.helper;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.Norm;

/**
 * AnalysisProfile.java: <br>
 * Detailed description...
 *
 * @author eomar, itrust consulting s.à.rl.
 * @version 
 * @since Feb 12, 2014
 */
public class AnalysisProfile {

	/** name */
	private String name;

	private int idAnalysis;

	private List<Norm> norms = new ArrayList<Norm>();

	private boolean scenario = true;
	
	private String comment;

	/**
	 * Constructor: <br>
	 */
	public AnalysisProfile() {
	}

	/**
	 * Constructor: <br>
	 * @param analysisId
	 */
	public AnalysisProfile(int analysisId) {
		setIdAnalysis(analysisId);
	}

	/**
	 * getName: <br>
	 * Description
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * setName: <br>
	 * Description
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * getNorms: <br>
	 * Description
	 * 
	 * @return
	 */
	public List<Norm> getNorms() {
		return norms;
	}

	/**
	 * setNorms: <br>
	 * Description
	 * 
	 * @param norms
	 */
	public void setNorms(List<Norm> norms) {
		this.norms = norms;
	}

	/**
	 * getIdAnalysis: <br>
	 * Description
	 * 
	 * @return
	 */
	public int getIdAnalysis() {
		return idAnalysis;
	}

	/**
	 * setIdAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 */
	public void setIdAnalysis(int idAnalysis) {
		this.idAnalysis = idAnalysis;
	}

	/**
	 * isScenario: <br>
	 * Description
	 * 
	 * @return
	 */
	public boolean isScenario() {
		return scenario;
	}

	/**
	 * setScenario: <br>
	 * Description
	 * 
	 * @param scenario
	 */
	public void setScenario(boolean scenario) {
		this.scenario = scenario;
	}

	/**
	 * getComment: <br>
	 * Description
	 * 
	 * @return
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * setComment: <br>
	 * Description
	 * 
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
}