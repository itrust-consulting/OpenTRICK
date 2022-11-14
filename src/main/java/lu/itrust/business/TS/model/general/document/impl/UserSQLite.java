/**
 * 
 */
package lu.itrust.business.TS.model.general.document.impl;

import java.sql.Timestamp;

import javax.persistence.AttributeOverride;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.TS.model.general.document.UserDocument;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "idUserSQLite"))
@AttributeOverride(name = "data", column = @Column(name = "dtSQLite", nullable = false, length = 16777216))
@AttributeOverride(name = "created", column = @Column(name = "dtExportTime", nullable = false))
public class UserSQLite extends UserDocument {

	@Column(name = "dtDeleteTime", nullable = false)
	private Timestamp deleteTime = null;

	/**
	 * Constructor: <br>
	 */
	public UserSQLite() {
	}

	/**
	 * @param user
	 * @param size
	 * @param string3
	 * @param string2
	 * @param string
	 * @param filename
	 * @param exportDate
	 */
	public UserSQLite(User user, String identifier, String label, String version, String fileName, byte[] file,
			long size) {
		super(user, identifier, label, version, fileName, file, size);
		setDeleteTime(new Timestamp(System.currentTimeMillis() + 86400000));
	}

	/**
	 * @return the deleteTime
	 */
	public Timestamp getDeleteTime() {
		return deleteTime;
	}

	/**
	 * @param deleteTime
	 *                   the deleteTime to set
	 */
	public void setDeleteTime(Timestamp deleteTime) {
		this.deleteTime = deleteTime;
	}

}
