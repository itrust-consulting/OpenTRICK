/**
 * 
 */
package lu.itrust.business.ts.usermanagement;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.model.analysis.Analysis;


/**
 * Represents an IDS (Intrusion Detection System) user.
 * Implements the IUser interface.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class IDS implements IUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idIDS")
	private int id;

	@Column(name = "dtPrefix", unique = true, length = 32, nullable = false)
	private String prefix;

	@Column(name = "dtDescription", length = 255)
	private String description;

	@Column(name = "dtToken", length = 256, nullable = false)
	private String token;

	@Column(name = "dtLastUpdate")
	private Timestamp lastUpdate;

	@Column(name = "dtLastAlert")
	private Timestamp lastAlert;

	@Column(name = "dtEnabled")
	private boolean enable;

	@ManyToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinTable(name = "IDSSubscribers", joinColumns = { @JoinColumn(name = "fiIDS") }, inverseJoinColumns = {
			@JoinColumn(name = "fiAnalysis") }, uniqueConstraints = @UniqueConstraint(columnNames = { "fiIDS", "fiAnalysis" }))
	@Cascade(CascadeType.SAVE_UPDATE)
	private List<Analysis> subscribers;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the lastUpdate
	 */
	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * @return the lastAlert
	 */
	public Timestamp getLastAlert() {
		return lastAlert;
	}

	/**
	 * @param lastAlert the lastAlert to set
	 */
	public void setLastAlert(Timestamp lastAlert) {
		this.lastAlert = lastAlert;
	}

	/**
	 * @return the subscribers
	 */
	public List<Analysis> getSubscribers() {
		return subscribers;
	}

	/**
	 * @param subscribers the subscribers to set
	 */
	public void setSubscribers(List<Analysis> subscribers) {
		this.subscribers = subscribers;
	}

	/**
	 * Returns the login prefix of the IDS user.
	 * @return the login prefix
	 */
	@Override
	public String getLogin() {
		return prefix;
	}

	/**
	 * Returns the token of the IDS user.
	 * @return the token
	 */
	@Override
	public String getPassword() {
		return token;
	}

	/**
	 * Returns whether the IDS user is enabled or not.
	 * @return true if enabled, false otherwise
	 */
	@Override
	public boolean isEnable() {
		return enable;
	}

	/**
	 * Sets the enable status of the IDS user.
	 * @param enable the enable status to set
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	/**
	 * Returns the access role of the IDS user.
	 * @return the access role
	 */
	@Override
	public RoleType getAccess() {
		return RoleType.ROLE_IDS;
	}

	/**
	 * Returns the full name of the IDS user.
	 * @return the full name
	 */
	@Override
	public String getFullname() {
		return this.description;
	}

	/**
	 * Notifies an alert for the IDS user and updates the last alert and last update timestamps.
	 * @return the updated IDS object
	 */
	public IDS notifyAlert() {
		this.lastAlert = this.lastUpdate = new Timestamp(System.currentTimeMillis());
		return this;
	}

	/**
	 * Notifies an update for the IDS user and updates the last update timestamp.
	 * @return the updated IDS object
	 */
	public IDS notifyUpdate(){
		this.lastUpdate = new Timestamp(System.currentTimeMillis());
		return this;
	}
}
