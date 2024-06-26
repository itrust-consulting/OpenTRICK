package lu.itrust.business.ts.model;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Value;

/** 
 * TrickService.java: <br>
 * Detailed description...
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TrickService {

	/** database ID */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idTrickService")
	private int id = 0;
	
	@Column(name="dtVersion", nullable=false)
	@Value("${app.settings.version}")
	private String version;
	
	@Column(name="dtInstalled", nullable=false)
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