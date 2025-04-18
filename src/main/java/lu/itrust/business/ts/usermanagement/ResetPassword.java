/**
 * 
 */
package lu.itrust.business.ts.usermanagement;

import java.sql.Timestamp;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;


/**
 * Represents a reset password entity.
 * This class is used to store information about a password reset request.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ResetPassword {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idResetPassword")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="fiUser")
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.MERGE })
	private User user;
	
	@Column(unique=true, name="dtKeyControl")
	private String keyControl;
	
	@Column(name="dtLimitTime")
	private Timestamp limitTime;

	/**
	 * 
	 */
	public ResetPassword() {
	}
	
	/**
	 * @param user
	 * @param keyControl
	 * @param limitTime
	 */
	public ResetPassword(User user, String keyControl, Timestamp limitTime) {
		this.user = user;
		this.keyControl = keyControl;
		this.limitTime = limitTime;
	}


	/**
	 * Returns the ID of the object.
	 *
	 * @return the ID of the object
	 */
	public Long getId() {
		return id;
	}

    /**
     * Sets the ID of the Temp object.
     * 
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the User associated with the Temp object.
     * 
     * @return the User object
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the User associated with the Temp object.
     * 
     * @param user the User object to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the key control of the Temp object.
     * 
     * @return the key control
     */
    public String getKeyControl() {
        return keyControl;
    }

	/**
	 * Sets the key control for resetting the password.
	 *
	 * @param keyControl the key control to set
	 */
	public void setKeyControl(String keyControl) {
		this.keyControl = keyControl;
	}

	/**
	 * Returns the limit time for password reset.
	 *
	 * @return the limit time as a Timestamp object
	 */
	public Timestamp getLimitTime() {
		return limitTime;
	}

	/**
	 * Sets the limit time for password reset.
	 *
	 * @param limitTime the limit time to set
	 */
	public void setLimitTime(Timestamp limitTime) {
		this.limitTime = limitTime;
	}

}
