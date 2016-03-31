/**
 * 
 */
package lu.itrust.business.TS.model.general;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
@Entity
public class WordReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idWordReport")
	private long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "dtType", columnDefinition = "varchar(255) default 'STA'")
	ReportType type;

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

	protected WordReport(String identifier, ReportType type, String label, String version, User user, String name, long length, byte[] file) {
		this.setIdentifier(identifier);
		this.setLabel(label);
		this.setVersion(version);
		this.setFilename(name);
		this.setSize(length);
		this.setUser(user);
		this.setFile(file);
		this.setCreated(new Timestamp(System.currentTimeMillis()));
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	

	/**
	 * @return the type
	 */
	public ReportType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ReportType type) {
		this.type = type;
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

	public static WordReport BuildReport(String identifier, String label, String version, User user, String name, long length, byte[] file) {
		return new WordReport(identifier, ReportType.STA, label, version, user, name, length, file);
	}

	public static WordReport BuildRiskSheet(String identifier, String label, String version, User user, String name, long length, byte[] file) {
		return new WordReport(identifier, ReportType.RISK_SHEET, label, version, user, name, length, file);
	}
}
