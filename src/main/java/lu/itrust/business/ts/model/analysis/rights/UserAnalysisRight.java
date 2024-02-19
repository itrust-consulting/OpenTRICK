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
 * UserAnalysisRight.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version 0.1
 * @since Jan 9, 2014
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
	 * Constructor: <br>
	 */
	public UserAnalysisRight() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param user
	 * @param analysis
	 * @param right
	 */
	public UserAnalysisRight(User user, AnalysisRight right) {
		this.user = user;
		this.right = right;
	}

	public UserAnalysisRight(Analysis analysis, User user, AnalysisRight right) {
		setAnalysis(analysis);
		setRight(right);
		setUser(user);
	}

	/**
	 * getUser: <br>
	 * Returns the user field value.
	 * 
	 * @return The value of the user field
	 */
	public User getUser() {
		return user;
	}

	/**
	 * setUser: <br>
	 * Sets the Field "user" with a value.
	 * 
	 * @param user
	 *            The Value to set the user field
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * getRight: <br>
	 * Returns the right field value.
	 * 
	 * @return The value of the right field
	 */
	public AnalysisRight getRight() {
		return right;
	}

	/**
	 * setRight: <br>
	 * Sets the Field "right" with a value.
	 * 
	 * @param right
	 *            The Value to set the right field
	 */
	public void setRight(AnalysisRight right) {
		this.right = right;
	}

	/**
	 * getId: <br>
	 * Returns the id field value.
	 * 
	 * @return The value of the id field
	 */
	public long getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the Field "id" with a value.
	 * 
	 * @param id
	 *            The Value to set the id field
	 */
	public void setId(long id) {
		this.id = id;
	}

	/** getAnalysis: <br>
	 * Returns the analysis field value.
	 * 
	 * @return The value of the analysis field
	 */
	public Analysis getAnalysis() {
		return analysis;
	}

	/** setAnalysis: <br>
	 * Sets the Field "analysis" with a value.
	 * 
	 * @param analysis 
	 * 			The Value to set the analysis field
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}
	
	public String rightToString(){
		return right == null? null : right.name();
	}

	/**
	 * userIsAuthorized: <br>
	 * Checks if this user has the given right.
	 * 
	 * @param right
	 * @return true if th euser has the right or false if not.
	 */
	public static final boolean userIsAuthorized(UserAnalysisRight uar, AnalysisRight right) {
		return (uar.getRight().ordinal() <= right.ordinal() ? true : false);

	}

	/**
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public UserAnalysisRight clone() throws CloneNotSupportedException {
		return (UserAnalysisRight) super.clone();
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public UserAnalysisRight duplicate() throws CloneNotSupportedException {
		UserAnalysisRight copy = (UserAnalysisRight) super.clone();
		copy.setId(0);
		return copy;
	}

	public String rightToLower() {
		return right == null? null : right.name().toLowerCase();
	}

}
