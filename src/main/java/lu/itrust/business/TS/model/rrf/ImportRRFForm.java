/**
 * 
 */
package lu.itrust.business.TS.model.rrf;

import java.util.List;

/**
 * @author eomar
 *
 */
public class ImportRRFForm {

	private int profile;
	
	private List<Integer> standards;
	
	/**
	 * 
	 */
	public ImportRRFForm() {
	}

	public int getProfile() {
		return profile;
	}

	public void setProfile(int profile) {
		this.profile = profile;
	}

	public List<Integer> getStandards() {
		return standards;
	}

	public void setStandards(List<Integer> standards) {
		this.standards = standards;
	}
}
