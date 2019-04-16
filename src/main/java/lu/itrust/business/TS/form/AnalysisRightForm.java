/**
 * 
 */
package lu.itrust.business.TS.form;

import java.util.Collections;
import java.util.Map;

/**
 * @author eomar
 *
 */
public class AnalysisRightForm {
	
	private int analysisId;
	
	private Map<Integer, RightForm> userRights = Collections.emptyMap();
	
	private Map<String, RightForm> invitations = Collections.emptyMap();

	/**
	 * 
	 */
	public AnalysisRightForm() {
	}

	/**
	 * @return the analysisId
	 */
	public int getAnalysisId() {
		return analysisId;
	}

	/**
	 * @param analysisId the analysisId to set
	 */
	public void setAnalysisId(int analysisId) {
		this.analysisId = analysisId;
	}

	/**
	 * @return the userRights
	 */
	public Map<Integer, RightForm> getUserRights() {
		return userRights;
	}

	/**
	 * @param userRights the userRights to set
	 */
	public void setUserRights(Map<Integer, RightForm> userRights) {
		this.userRights = userRights;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AnalysisRightForm [analysisId=" + analysisId + ", userRights=" + userRights + "]";
	}

	public Map<String, RightForm> getInvitations() {
		return invitations;
	}

	public void setInvitations(Map<String, RightForm> invitations) {
		this.invitations = invitations;
	}
	
	

}
