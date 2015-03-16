/**
 * 
 */
package lu.itrust.business.TS.data.general;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lu.itrust.business.TS.usermanagement.User;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * @author eomar
 *
 */
@Entity
public class WordReport {

	@Id
	@GeneratedValue
	@Column(name = "idWordReport")
	private long id;

	@ManyToOne
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.PERSIST })
	@JoinColumn(name = "fiUser")
	private User user;

	@Column(name = "dtVersion")
	private String version;

	@Column(name = "dtIdentifier")
	private String identifier;

	@Column(name = "dtLabel")
	private String label;

	@Column(name = "dtFilename", unique = true)
	private String filename;

	@Column(name = "dtSize")
	private long size;

	@Column(name = "dtFile", columnDefinition = "MEDIUMBLOB")
	private byte[] file;

	@Column(name = "dtCreated")
	private Timestamp created;

	/**
	 * 
	 */
	public WordReport() {
	}

	public WordReport(String identifier, String label, String version, User user, String name, long length, byte[] file) {
		this.setIdentifier(identifier);
		this.setLabel(label);
		this.setVersion(version);
		this.setFilename(name);
		this.setSize(length);
		this.setUser(user);
		this.setFile(file);
		this.setCreated(new Timestamp(System.currentTimeMillis()));
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}
}
