/**
 * 
 */
package lu.itrust.business.TS.exportation.helper;

/**
 * @author eomar
 *
 */
public class Twidth {
	
	private int fixed = -1;
	
	private int preference = -1;
	
	/**
	 * 
	 */
	public Twidth() {
	}
	/**
	 * @param fixed
	 * @param preference
	 */
	public Twidth(int fixed, int preference) {
		this.fixed = fixed;
		this.preference = preference;
	}
	/**
	 * @return the fixed
	 */
	public int getFixed() {
		return fixed;
	}
	/**
	 * @param fixed the fixed to set
	 */
	public void setFixed(int fixed) {
		this.fixed = fixed;
	}
	/**
	 * @return the preference
	 */
	public int getPreference() {
		return preference;
	}
	/**
	 * @param preference the preference to set
	 */
	public void setPreference(int preference) {
		this.preference = preference;
	}

}
