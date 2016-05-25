/**
 * 
 */
package lu.itrust.business.TS.usermanagement;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * @author eomar
 *
 */
@Entity
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
