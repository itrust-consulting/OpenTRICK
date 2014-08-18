package lu.itrust.business.TS;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lu.itrust.business.TS.tsconstant.Constant;

/** 
 * TrickService.java: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.à.rl.
 * @version 
 * @since Apr 23, 2014
 */
@Entity public class TrickService {

	/** database ID */
	@Id @GeneratedValue private int id = -1;
	
	private String version = Constant.TRICKSERVICE_VERSION;
	
	private boolean installed = false;

	/**
	 * Constructor: <br>
	 */
	public TrickService() {}
	
	/**
	 * Constructor: <br>
	 * @param version
	 * @param installed
	 */
	public TrickService(String version, boolean installed) {
		this.version = version;
		this.installed = installed;
	}
	
	/** isInstalled: <br>
	 * Returns the installed field value.
	 * 
	 * @return The value of the installed field
	 */
	public boolean isInstalled() {
		return installed;
	}

	/** setInstalled: <br>
	 * Sets the Field "installed" with a value.
	 * 
	 * @param installed 
	 * 			The Value to set the installed field
	 */
	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

	/** getVersion: <br>
	 * Returns the version field value.
	 * 
	 * @return The value of the version field
	 */
	public String getVersion() {
		return version;
	}

	/** setVersion: <br>
	 * Sets the Field "version" with a value.
	 * 
	 * @param version 
	 * 			The Value to set the version field
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/** getId: <br>
	 * Returns the id field value.
	 * 
	 * @return The value of the id field
	 */
	public int getId() {
		return id;
	}

	/** setId: <br>
	 * Sets the Field "id" with a value.
	 * 
	 * @param id 
	 * 			The Value to set the id field
	 */
	public void setId(int id) {
		this.id = id;
	}
}