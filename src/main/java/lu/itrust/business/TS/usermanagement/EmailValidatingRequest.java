package lu.itrust.business.TS.usermanagement;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class EmailValidatingRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idEmailValidatingRequest")
	private long id;

	@ManyToOne
	@JoinColumn(name = "fiUser", unique = true)
	@Cascade(CascadeType.SAVE_UPDATE)
	private User user;

	@Column(name = "dtEmail", unique = true)
	private String email;

	@Column(name = "dtToken", unique = true)
	private String token;

	/**
	 * 
	 */
	public EmailValidatingRequest() {
	}

	/**
	 * @param user
	 * @param token
	 */
	public EmailValidatingRequest(User user, String token) {
		this.user = user;
		this.email = user.getEmail();
		this.token = token;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
