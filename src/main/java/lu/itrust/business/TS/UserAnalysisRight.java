package lu.itrust.business.TS;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lu.itrust.business.TS.usermanagement.User;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * UserAnalysisRight.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version 0.1
 * @since Jan 9, 2014
 */
@Entity 
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"fiAnalysis","fiUser"}))
public class UserAnalysisRight implements Serializable,Cloneable {

	/** serialVersionUID */
	@Transient
	private static final long serialVersionUID = 1L;

	/** id */
	@Id @GeneratedValue 
	@Column(name="idUserAnalysisRight")
	private long id = -1;

	/** User */
	@ManyToOne
	@JoinColumn(name="fiUser", nullable=false)
	private User user;

	/** Analysis */
	@ManyToOne 
	@JoinColumn(name="fiAnalysis", nullable=false)
	private Analysis analysis;

	/** rights */
	@Enumerated(EnumType.STRING)
	@Column(name="dtRight", nullable=false)
	private AnalysisRight right;

	/**
	 * Constructor: <br>
	 */
	public UserAnalysisRight() {
	}

	/**
	 * Constructor: <br>
	 * @param user
	 * @param analysis
	 * @param right
	 */
	public UserAnalysisRight(User user, Analysis analysis, AnalysisRight right) {
		this.user = user;
		this.analysis = analysis;
		this.right = right;
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
	 * getAnalysis: <br>
	 * Returns the analysis field value.
	 * 
	 * @return The value of the analysis field
	 */
	public Analysis getAnalysis() {
		return analysis;
	}

	/**
	 * setAnalysis: <br>
	 * Sets the Field "analysis" with a value.
	 * 
	 * @param analysis
	 *            The Value to set the analysis field
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
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

	/* (non-Javadoc)
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
	public UserAnalysisRight duplicate()throws CloneNotSupportedException{
		UserAnalysisRight copy = (UserAnalysisRight) super.clone();
		copy.setId(-1);
		return copy;
	}
	
}
