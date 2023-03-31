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
 * @author eomar
 *
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


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getKeyControl() {
		return keyControl;
	}

	public void setKeyControl(String keyControl) {
		this.keyControl = keyControl;
	}

	public Timestamp getLimitTime() {
		return limitTime;
	}

	public void setLimitTime(Timestamp limitTime) {
		this.limitTime = limitTime;
	}

}
