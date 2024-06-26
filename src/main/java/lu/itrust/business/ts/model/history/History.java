package lu.itrust.business.ts.model.history;

import java.beans.Transient;
import java.sql.Timestamp;
import java.util.Date;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.exception.TrickException;

/**
 * History: <br>
 * This class represents an History and all its data.
 * 
 * This class is used to store History. Each analysis has only one single
 * history. Each analysis in the knowledgebase can be the same, but has
 * different histories (versions).
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiAnalysis", "dtVersion" }) )
public class History implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** id History unsaved */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "idHistory")
	private int id = 0;

	/** The Analysis Version (Version of the History entry) */
	@Column(name = "dtVersion", nullable = false, length = 12)
	private String version = "";

	/** The Date when the History entry was created */
	@Column(name = "dtDateComment", nullable = false)
	private Date date = null;

	/**
	 * The Name of the Author that created the History Entry (The Analysis at
	 * this Version)
	 */
	@Column(name = "dtAuthor", nullable = false)
	private String author = "";

	/** The Comment an Author gave to the History Entry */
	@Column(name = "dtComment", nullable = false, length=65536)
	private String comment = "";

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public History() {
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param version
	 *            The Analysis Version
	 * @param date
	 *            The Date of The History Entry
	 * @param author
	 *            The Author of the History Entry
	 * @param comment
	 *            The Comment of the Author
	 */
	public History(String version, Date date, String author, String comment) {
		this.version = version;
		this.date = date;
		this.author = author;
		this.comment = comment;
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getDate: <br>
	 * Returns the "date" field value
	 * 
	 * @return The Date when the History Entry was created
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * setDate Sets the "date" field with a value
	 * 
	 * @param date
	 *            The value to set the Date
	 * @throws TrickException
	 */
	public void setDate(Date date) throws TrickException {
		if (date == null)
			throw new TrickException("error.history.date.empty", "Date cannot be empty!");
		this.date = date;
	}

	/**
	 * getAuthor: <br>
	 * Returns the "author" field value
	 * 
	 * @return The Name of the Author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * setAuthor: <br>
	 * Sets the "author" field with a value
	 * 
	 * @param author
	 *            The value to set the Author Name
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * getComment: <br>
	 * Returns the "comment" field value
	 * 
	 * @return The Comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * setComment: <br>
	 * Sets the "comment" field with a value
	 * 
	 * @param comment
	 *            The value to set the Comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * setVersion: <br>
	 * Sets the "version" field with a value
	 * 
	 * @param version
	 *            The value to set the Version Identifier
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * getVersion: <br>
	 * Returns the "version" field value
	 * 
	 * @return The Version Identifier
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * getId: <br>
	 * Returns the id field value.
	 * 
	 * @return The value of the id field
	 */
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the Field "id" with a value.
	 * 
	 * @param id
	 *            The Value to set the id field
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * toString: <br>
	 * Description
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "History [id=" + id + ", version=" + version + ", date=" + date + ", author=" + author + ", comment=" + comment + "]";
	}

	/**
	 * clone: <br>
	 * Description
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public History clone() throws CloneNotSupportedException {
		return (History) super.clone();
	}

	/**
	 * clone: <br>
	 * Description
	 * 
	 * @see java.lang.Object#clone()
	 */
	public History duplicate() throws CloneNotSupportedException {
		History history = (History) super.clone();
		history.id = 0;
		return history;
	}

	/**
		* Generates a new Date object representing the current date and time.
		* 
		* @return the generated Date object
		*/
	@Transient
	public Date generateDate() {
		return date = new Timestamp(System.currentTimeMillis());
	}
}