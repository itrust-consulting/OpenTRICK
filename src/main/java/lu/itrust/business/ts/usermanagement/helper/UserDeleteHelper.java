/**
 * 
 */
package lu.itrust.business.ts.usermanagement.helper;

import java.util.List;
import java.util.Map;

/**
 * @author eomar
 *
 */
public class UserDeleteHelper {
	
	private int idUser;
	
	private Map<Integer, Integer> switchOwners;
	
	private List<Integer> deleteAnalysis;
	
	/**
	 * 
	 */
	public UserDeleteHelper() {
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public Map<Integer, Integer> getSwitchOwners() {
		return switchOwners;
	}

	public void setSwitchOwners(Map<Integer, Integer> switchOwners) {
		this.switchOwners = switchOwners;
	}

	public List<Integer> getDeleteAnalysis() {
		return deleteAnalysis;
	}

	public void setDeleteAnalysis(List<Integer> deleteAnalysis) {
		this.deleteAnalysis = deleteAnalysis;
	}

	public boolean hasAnalysesToDelete() {
		return !(deleteAnalysis == null || deleteAnalysis.isEmpty());
	}
	
	public boolean hasAnalysesToSwitch() {
		return !(switchOwners == null || switchOwners.isEmpty());
	}

	@Override
	public String toString() {
		return "UserDeleteHelper [idUser=" + idUser + ", switchOwners=" + switchOwners + ", deleteAnalysis=" + deleteAnalysis + "]";
	}
}
