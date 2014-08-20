/**
 * 
 */
package lu.itrust.business.TS.usermanagement;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author eomar
 * 
 */
@Entity
public class UserSQLite {

	@Id @GeneratedValue
	@Column(name="idUserSQLite")
	private int id = -1;

	@Column(name="dtFileName", unique=true)
	private String fileName = null;

	@Column(name="dtAnalysisIdentifier")
	private String analysisIdentifier = null;

	@ManyToOne
	@JoinColumn(name="fiUser")
	private User user = null;

	@Column(name="dtSize")
	private long size = 0;

	@Column(name="dtSQLite")
	private byte[] sqLite;

	@Column(name="dtExportTime")
	private Timestamp exportTime = null;

	@Column(name="dtDeleteTime")
	private Timestamp deleteTime = null;

	/**
	 * Constructor: <br>
	 */
	public UserSQLite() {
	}

	/**
	 * @param fileName
	 * @param user
	 * @param exportDate
	 */
	public UserSQLite(String fileName, User user, byte[] file) {
		this.fileName = fileName;
		this.user = user;
		this.sqLite = file;
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
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
	 * @return the analysisIdentifier
	 */
	public String getAnalysisIdentifier() {
		return analysisIdentifier;
	}

	/**
	 * @param analysisIdentifier
	 *            the analysisIdentifier to set
	 */
	public void setAnalysisIdentifier(String analysisIdentifier) {
		this.analysisIdentifier = analysisIdentifier;
	}

}
