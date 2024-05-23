package lu.itrust.business.ts.model.analysis.rights;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
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

import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.usermanagement.User;

/**
 * Represents a user's analysis right.
 * 
 * This class encapsulates the information about a user's right to access an analysis.
 * It contains the user, analysis, and the analysis right associated with the user.
 * 
 * The class provides methods to get and set the user, analysis, and analysis right,
 * as well as methods to clone and duplicate the user analysis right.
 * 
 * @see User
 * @see Analysis
 * @see AnalysisRight
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiAnalysis", "fiUser" }))
public class UserAnalysisRight implements Cloneable {

	/** id */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "idUserAnalysisRight")
	private long id = 0;

	/** User */
	@ManyToOne
	@JoinColumn(name = "fiUser", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Access(AccessType.FIELD)
	@Cascade(CascadeType.SAVE_UPDATE)
	private User user;

	/** Analysis */
	@ManyToOne
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Access(AccessType.FIELD)
	@Cascade(CascadeType.SAVE_UPDATE)
	private Analysis analysis;

	/** rights */
	@Enumerated(EnumType.STRING)
	@Column(name = "dtRight", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private AnalysisRight right;

	/**
	 * Default constructor.
	 */
	public UserAnalysisRight() {
	}

	/**
	 * Constructor with user and right parameters.
	 * 
	 * @param user The user associated with the analysis right
	 * @param right The analysis right for the user
	 */
	public UserAnalysisRight(User user, AnalysisRight right) {
		this.user = user;
		this.right = right;
	}

	/**
	 * Constructor with analysis, user, and right parameters.
	 * 
	 * @param analysis The analysis associated with the user's right
	 * @param user The user associated with the analysis right
	 * @param right The analysis right for the user
	 */
	public UserAnalysisRight(Analysis analysis, User user, AnalysisRight right) {
		setAnalysis(analysis);
		setRight(right);
		setUser(user);
	}

	/**
	 * Returns the user associated with the analysis right.
	 * 
	 * @return The user associated with the analysis right
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the user associated with the analysis right.
	 * 
	 * @param user The user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Returns the analysis right for the user.
	 * 
	 * @return The analysis right for the user
	 */
	public AnalysisRight getRight() {
		return right;
	}

	/**
	 * Sets the analysis right for the user.
	 * 
	 * @param right The analysis right to set
	 */
	public void setRight(AnalysisRight right) {
		this.right = right;
	}

	/**
	 * Returns the ID of the user analysis right.
	 * 
	 * @return The ID of the user analysis right
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the ID of the user analysis right.
	 * 
	 * @param id The ID to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Returns the analysis associated with the user's right.
	 * 
	 * @return The analysis associated with the user's right
	 */
	public Analysis getAnalysis() {
		return analysis;
	}

	/**
	 * Sets the analysis associated with the user's right.
	 * 
	 * @param analysis The analysis to set
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	/**
	 * Returns the string representation of the analysis right.
	 * 
	 * @return The string representation of the analysis right
	 */
	public String rightToString() {
		return right == null ? null : right.name();
	}

	/**
	 * Checks if the user has the given analysis right.
	 * 
	 * @param uar The user analysis right to check
	 * @param right The analysis right to check against
	 * @return true if the user has the right, false otherwise
	 */
	public static final boolean userIsAuthorized(UserAnalysisRight uar, AnalysisRight right) {
		return (uar.getRight().ordinal() <= right.ordinal() ? true : false);
	}

	/**
	 * Clones the user analysis right.
	 * 
	 * @return A clone of the user analysis right
	 * @throws CloneNotSupportedException if cloning is not supported
	 */
	@Override
	public UserAnalysisRight clone() throws CloneNotSupportedException {
		return (UserAnalysisRight) super.clone();
	}

	/**
	 * Creates a duplicate of the user analysis right.
	 * 
	 * @return A duplicate of the user analysis right
	 * @throws CloneNotSupportedException if cloning is not supported
	 */
	public UserAnalysisRight duplicate() throws CloneNotSupportedException {
		UserAnalysisRight copy = (UserAnalysisRight) super.clone();
		copy.setId(0);
		return copy;
	}

	/**
	 * Returns the lowercase string representation of the analysis right.
	 * 
	 * @return The lowercase string representation of the analysis right
	 */
	public String rightToLower() {
		return right == null ? null : right.name().toLowerCase();
	}

}
