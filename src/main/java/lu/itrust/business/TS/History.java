package lu.itrust.business.TS;

import java.io.Serializable;
import java.util.Date;

/**
 * History: <br>
 * This class represents an History and all its data.
 * 
 * This class is used to store History. Each analysis has only one single
 * history. Each analysis in the knowledgebase can be the same, but has
 * different histories (versions).
 * 
 * @author itrust consulting s.à r.l. - SME,BJA
 * @version 0.1
 * @since 2012-08-21
 */
public class History implements Serializable, Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** id History unsaved */
	private int id = -1;

	/** The Analysis Version (Version of the History entry) */
	private String version = "";

	/** The Date when the History entry was created */
	private Date date = null;

	/**
	 * The Name of the Author that created the History Entry (The Analysis at
	 * this Version)
	 */
	private String author = "";

	/** The Comment an Author gave to the History Entry */
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

	private static int toInt(String version) {
		try {
			if (version.isEmpty())
				return 0;
			return Integer.parseInt(version);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static final int VersionComparator(String versionA, String versionB) {
		String[] versionsA = versionA.split("\\.", 2);
		String[] versionsB = versionB.split("\\.", 2);
		int valueA = toInt(versionsA[0]);
		int valueB = toInt(versionsB[0]);
		if (valueA > valueB)
			return 1;
		else if (valueA < valueB)
			return -1;
		else if (valueA == valueB
				&& (versionsA.length == 1 && versionsB.length == 1))
			return 0;
		return VersionComparator(versionsA.length > 1 ? versionsA[1] : "",
				versionsB.length > 1 ? versionsB[1] : "");
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
	 */
	public void setDate(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("History Date cannot be null!");
		}
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
		return "History [id=" + id + ", version=" + version + ", date=" + date
				+ ", author=" + author + ", comment=" + comment + "]";
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
		history.id = -1;
		return history;
	}
}