package lu.itrust.business.ts.model.analysis;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.usermanagement.User;

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
