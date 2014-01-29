/**
 * 
 */
package lu.itrust.business.TS.usermanagement;

import java.sql.Timestamp;

/**
 * @author eomar
 * 
 */
public class UserSqLite {

	private long id = -1;

	private String fileName = null;

	private User user = null;
	
	private long size = 0;

	private byte[] sqLite;

	private Timestamp exportTime = null;

	private Timestamp deleteTime = null;

	/**
	 * 
	 */
	public UserSqLite() {
	}

	/**
	 * @param fileName
	 * @param user
	 * @param exportDate
	 */
	public UserSqLite(String fileName, User user, byte[] file) {
		this.fileName = fileName;
		this.user = user;
		this.sqLite = file;
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
	 * @param size the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}

}
