package lu.itrust.business.TS.model.analysis;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.usermanagement.User;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiAnalysis", "dtEmail" }))
public class AnalysisShareInvitation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cascade(CascadeType.SAVE_UPDATE)
	private Analysis analysis;

	@Column(name = "dtRight", nullable = false)
	@Enumerated(EnumType.STRING)
	private AnalysisRight right;

	@Column(name = "dtToken", nullable = false, unique = true)
	private String token;

	@Column(name = "dtEmail", nullable = false)
	private String email;

	@ManyToOne
	@JoinColumn(name = "fiHost", nullable = false)
	@Cascade(CascadeType.SAVE_UPDATE)
	private User host;

	/**
	 * 
	 */
	public AnalysisShareInvitation() {
	}

	/**
	 * @param token
	 * @param analysis
	 * @param host
	 * @param email
	 * @param right
	 */
	public AnalysisShareInvitation(String token, Analysis analysis, User host, String email, AnalysisRight right) {
		this.token = token;
		this.analysis = analysis;
		this.host = host;
		this.email = email;
		this.right = right;
	}

	public Analysis getAnalysis() {
		return analysis;
	}

	public String getEmail() {
		return email;
	}

	public long getId() {
		return id;
	}

	public AnalysisRight getRight() {
		return right;
	}

	public String getToken() {
		return token;
	}

	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setRight(AnalysisRight right) {
		this.right = right;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getHost() {
		return host;
	}

	public void setHost(User host) {
		this.host = host;
	}

}
