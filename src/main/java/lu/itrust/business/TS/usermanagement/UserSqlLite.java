/**
 * 
 */
package lu.itrust.business.TS.usermanagement;

import java.sql.Timestamp;

/**
 * @author eomar
 * 
 */
public class UserSqlLite {

	private long id = -1;

	private String fileName = null;

	private User user = null;

	private byte[] sqlLite;

	private Timestamp exportTime = null;

	private Timestamp deleteTime = null;

	/**
	 * 
	 */
	public UserSqlLite() {
	}

	/**
	 * @param fileName
	 * @param user
	 * @param exportDate
	 */
	public UserSqlLite(String fileName, User user, byte[] file) {
		this.fileName = fileName;
		this.user = user;
		this.sqlLite = file;
		this.setExportTime(new Timestamp(System.currentTimeMillis()));
		this.setDeleteTime(new Timestamp(System.currentTimeMillis() + 86400000));
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
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
	 * @return the sqlLite
	 */
	public byte[] getSqlLite() {
		return sqlLite;
	}

	/**
	 * @param sqlLite
	 *            the sqlLite to set
	 */
	public void setSqlLite(byte[] sqlLite) {
		this.sqlLite = sqlLite;
	}

}
