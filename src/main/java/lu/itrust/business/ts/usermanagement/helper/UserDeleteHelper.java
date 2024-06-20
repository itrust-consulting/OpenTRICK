/**
 * 
 */
package lu.itrust.business.ts.usermanagement.helper;

import java.util.List;
import java.util.Map;


/**
 * The UserDeleteHelper class represents a helper class for deleting user-related data.
 */
public class UserDeleteHelper {
	
	private int idUser;
	
	private Map<Integer, Integer> switchOwners;
	
	private List<Integer> deleteAnalysis;
	
	/**
	 * Constructs a new UserDeleteHelper object.
	 */
	public UserDeleteHelper() {
	}

	/**
	 * Gets the ID of the user.
	 * 
	 * @return the ID of the user
	 */
	public int getIdUser() {
		return idUser;
	}

	/**
	 * Sets the ID of the user.
	 * 
	 * @param idUser the ID of the user
	 */
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	/**
	 * Gets the map of switch owners.
	 * 
	 * @return the map of switch owners
	 */
	public Map<Integer, Integer> getSwitchOwners() {
		return switchOwners;
	}

	/**
	 * Sets the map of switch owners.
	 * 
	 * @param switchOwners the map of switch owners
	 */
	public void setSwitchOwners(Map<Integer, Integer> switchOwners) {
		this.switchOwners = switchOwners;
	}

	/**
	 * Gets the list of analyses to delete.
	 * 
	 * @return the list of analyses to delete
	 */
	public List<Integer> getDeleteAnalysis() {
		return deleteAnalysis;
	}

	/**
	 * Sets the list of analyses to delete.
	 * 
	 * @param deleteAnalysis the list of analyses to delete
	 */
	public void setDeleteAnalysis(List<Integer> deleteAnalysis) {
		this.deleteAnalysis = deleteAnalysis;
	}

	/**
	 * Checks if there are analyses to delete.
	 * 
	 * @return true if there are analyses to delete, false otherwise
	 */
	public boolean hasAnalysesToDelete() {
		return !(deleteAnalysis == null || deleteAnalysis.isEmpty());
	}
	
	/**
	 * Checks if there are analyses to switch.
	 * 
	 * @return true if there are analyses to switch, false otherwise
	 */
	public boolean hasAnalysesToSwitch() {
		return !(switchOwners == null || switchOwners.isEmpty());
	}

	@Override
	public String toString() {
		return "UserDeleteHelper [idUser=" + idUser + ", switchOwners=" + switchOwners + ", deleteAnalysis=" + deleteAnalysis + "]";
	}
}
