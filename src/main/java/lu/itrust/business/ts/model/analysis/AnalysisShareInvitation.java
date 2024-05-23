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

/**
 * Represents an invitation to share an analysis with a user.
 * 
 * This class is an entity that maps to the "AnalysisShareInvitation" table in the database.
 * It contains information about the analysis being shared, the user being invited, and the access rights granted.
 * 
 * The class provides getters and setters for accessing and modifying the properties of an analysis share invitation.
 * 
 * Example usage:
 * AnalysisShareInvitation invitation = new AnalysisShareInvitation("token123", analysis, hostUser, "user@example.com", AnalysisRight.READ_WRITE);
 * invitation.setAnalysis(newAnalysis);
 * invitation.setEmail("newuser@example.com");
 * 
 * @param token The unique token associated with the invitation.
 * @param analysis The analysis being shared.
 * @param host The user who is sharing the analysis.
 * @param email The email address of the user being invited.
 * @param right The access rights granted to the invited user.
 */
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
	 * Default constructor for AnalysisShareInvitation.
	 */
	public AnalysisShareInvitation() {
	}

	/**
	 * Represents an invitation to share an analysis with a user.
	 * 
	 * @param token    The invitation token.
	 * @param analysis The analysis to be shared.
	 * @param host     The user who is sharing the analysis.
	 * @param email    The email of the user being invited to share the analysis.
	 * @param right    The access rights for the invited user.
	 */
	public AnalysisShareInvitation(String token, Analysis analysis, User host, String email, AnalysisRight right) {
		this.token = token;
		this.analysis = analysis;
		this.host = host;
		this.email = email;
		this.right = right;
	}

	/**
	 * Get the analysis associated with this invitation.
	 * 
	 * @return The analysis.
	 */
	public Analysis getAnalysis() {
		return analysis;
	}

	/**
	 * Get the email of the user being invited.
	 * 
	 * @return The email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Get the ID of the invitation.
	 * 
	 * @return The ID.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Get the access rights for the invited user.
	 * 
	 * @return The access rights.
	 */
	public AnalysisRight getRight() {
		return right;
	}

	/**
	 * Get the invitation token.
	 * 
	 * @return The token.
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Set the analysis associated with this invitation.
	 * 
	 * @param analysis The analysis to set.
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	/**
	 * Set the email of the user being invited.
	 * 
	 * @param email The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Set the ID of the invitation.
	 * 
	 * @param id The ID to set.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Set the access rights for the invited user.
	 * 
	 * @param right The access rights to set.
	 */
	public void setRight(AnalysisRight right) {
		this.right = right;
	}

	/**
	 * Set the invitation token.
	 * 
	 * @param token The token to set.
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * Get the user who is sharing the analysis.
	 * 
	 * @return The host user.
	 */
	public User getHost() {
		return host;
	}

	/**
	 * Set the user who is sharing the analysis.
	 * 
	 * @param host The host user to set.
	 */
	public void setHost(User host) {
		this.host = host;
	}

}
