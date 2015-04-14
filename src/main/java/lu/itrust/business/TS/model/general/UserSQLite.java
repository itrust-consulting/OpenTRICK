/**
 * 
 */
package lu.itrust.business.TS.model.general;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 * 
 */
@Entity
public class UserSQLite {

	@Id
	@GeneratedValue
	@Column(name = "idUserSQLite")
	private int id = -1;

	@Column(name = "dtFilename", unique = true, nullable = false)
	private String filename = null;

	@Column(name = "dtIdentifier", nullable = false)
	private String identifier = null;

	@Column(name = "dtLabel", nullable = false)
	private String label;

	@Column(name = "dtVersion", nullable = false)
	private String version;

	@ManyToOne
	@JoinColumn(name = "fiUser", nullable = false)
	private User user = null;

	@Column(name = "dtSize", nullable = false)
	private long size = 0;

	@Column(name = "dtSQLite", columnDefinition = "MEDIUMBLOB", nullable = false)
	private byte[] sqLite;

	@Column(name = "dtExportTime", nullable = false)
	private Timestamp exportTime = null;

	@Column(name = "dtDeleteTime", nullable = false)
	private Timestamp deleteTime = null;

	/**
	 * Constructor: <br>
	 */
	public UserSQLite() {
	}

	/**
	 * @param string3
	 * @param string2
	 * @param string
	 * @param filename
	 * @param user
	 * @param size
	 * @param exportDate
	 */
	public UserSQLite(String identifier, String label, String version, String fileName, User user, byte[] file, long size) {
		setIdentifier(identifier);
		setLabel(label);
		setVersion(version);
		setFilename(fileName);
		setUser(user);
		setSqLite(file);
		setSize(size);
		this.setExportTime(new Timestamp(System.currentTimeMillis()));
		this.setDeleteTime(new Timestamp(System.currentTimeMillis() + 86400000));
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the deleteTime
	 */
	public Timestamp getDeleteTime() {
		return deleteTime;
	}

	/**
	 * @param deleteTime
	 *            the deleteTime to set
	 */
	public void setDeleteTime(Timestamp deleteTime) {
		this.deleteTime = deleteTime;
	}

	/**
	 * @return the exportTime
	 */
	public Timestamp getExportTime() {
		return exportTime;
	}

	/**
	 * @param exportTime
	 *            the exportTime to set
	 */
	public void setExportTime(Timestamp exportTime) {
		this.exportTime = exportTime;
	}

	/**
	 * @return the sqLite
	 */
	public byte[] getSqLite() {
		return sqLite;
	}

	/**
	 * @param sqLite
	 *            the sqLite to set
	 */
	public void setSqLite(byte[] sqLite) {
		this.sqLite = sqLite;
	}

	public long getSize() {
		return this.size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
