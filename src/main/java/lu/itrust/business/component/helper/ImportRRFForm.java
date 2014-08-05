/**
 * 
 */
package lu.itrust.business.component.helper;

import java.util.List;

/**
 * @author eomar
 *
 */
public class ImportRRFForm {

	private int profile;
	
	private List<Integer> norms;
	
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

	public List<Integer> getNorms() {
		return norms;
	}

	public void setNorms(List<Integer> norms) {
		this.norms = norms;
	}
}
