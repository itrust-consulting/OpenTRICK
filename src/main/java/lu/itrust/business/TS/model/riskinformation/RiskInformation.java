package lu.itrust.business.TS.model.riskinformation;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;

/**
 * RiskInformation: <br>
 * This class represents a RiskInformation and its data.
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiAnalysis", "dtLabel", "dtChapter" }))
public class RiskInformation implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** Risk Information id */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idRiskInformation")
	private int id = -1;

	/** editable */
	@Transient
	private boolean editable;

	@Column(name = "dtCustom", nullable = false)
	private boolean custom;

	/** The Risk Information Label */
	@Column(name = "dtLabel", nullable = false)
	private String label;

	/** The Risk Information Exposed Value */
	@Column(name = "dtExposed", nullable = false)
	private String exposed = "";

	/** The Risk Information Comment */
	@Column(name = "dtComment", nullable = false, length = 16777216)
	private String comment = "";

	/** The Risk Information Hidden Comment */
	@Column(name = "dtHiddenComment", nullable = false, length = 16777216)
	private String hiddenComment = "";

	/** The Risk Information Category */
	@Column(name = "dtCategory", nullable = false)
	private String category;

	/** The Risk Information Chapter */
	@Column(name = "dtChapter", nullable = false)
	private String chapter;

	/** The Risk Information Acronym */
	@Column(name = "dtAcronym", nullable = false, length = 15)
	private String acronym = "";

	@Column(name = "dtOwner")
	private String owner = "";

	/**
	 * 
	 */
	public RiskInformation() {
	}

	/**
	 * @param chapter
	 */
	public RiskInformation(String chapter) {
		setChapter(chapter);
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = cleanUpValue(owner);
	}

	/**
	 * getAcronym: <br>
	 * Returns the "acronym" field value
	 * 
	 * @return The Risk Information Acronym
	 */
	public String getAcronym() {
		return acronym;
	}

	/**
	 * setAcronym: <br>
	 * Sets the "acronym" field with a value
	 * 
	 * @param acro
	 *            The value to set the Acronym
	 */
	public void setAcronym(String acro) {
		this.acronym = cleanUpValue(acro);
	}

	private String cleanUpValue(String value) {
		return value == null? "" : value.trim();
	}

	/**
	 * getLabel: <br>
	 * Returns the "label" field value
	 * 
	 * @return The Risk Information Label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * setLabel: <br>
	 * Sets the "label" field with a value
	 * 
	 * @param label
	 *            The value to set the Label
	 */
	public void setLabel(String label) {
		this.label = cleanUpValue(label);
	}

	/**
	 * getExpo: <br>
	 * Returns the "exposed" field value
	 * 
	 * @return The Risk Information Exposed value
	 */
	public String getExposed() {
		return exposed;
	}

	/**
	 * setExpo: <br>
	 * Sets the "exposed" field with a value
	 * 
	 * @param expo
	 *            The value to set the Exposed value
	 */
	public void setExposed(String expo) {
		this.exposed = cleanUpValue(expo);
	}

	/**
	 * getComment: <br>
	 * Returns the "comment" field value
	 * 
	 * @return The Risk Information Comment
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
		this.comment = cleanUpValue(comment);
	}

	/**
	 * getHiddenComment: <br>
	 * Returns the "hiddenComment" field value
	 * 
	 * @return The Risk Information Hidden Comment
	 */
	public String getHiddenComment() {
		return hiddenComment;
	}

	/**
	 * setHiddenComment: <br>
	 * Sets the "hiddenComment" field with a value
	 * 
	 * @param hiddenComment
	 *            The value to set the Hidden Comment
	 */
	public void setHiddenComment(String hiddenComment) {
		this.hiddenComment = cleanUpValue(hiddenComment);
	}

	/**
	 * getCategory: <br>
	 * Returns the "category" field value
	 * 
	 * @return The Risk Information Category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * setCategory: <br>
	 * Sets the "category" field with a value
	 * 
	 * @param category
	 *            The value to set the Category
	 * @throws TrickException
	 */
	public void setCategory(String category) throws TrickException {
		if (category == null || !category.matches(Constant.REGEXP_VALID_RISKINFORMATION_TYPE))
			throw new TrickException("error.risk_information.category.invalid", "Category is empty or invalid");
		this.category = category;
	}

	/**
	 * getChapter: <br>
	 * Returns the "chapter" field value
	 * 
	 * @return The Risk Information Chapter
	 */
	public String getChapter() {
		return chapter;
	}

	/**
	 * setChapter: <br>
	 * Sets the "chapter" field with a value
	 * 
	 * @param chapter
	 *            The value to set the Chapter
	 */
	public void setChapter(String chapter) {
		this.chapter = cleanUpValue(chapter);
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
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public RiskInformation clone() throws CloneNotSupportedException {
		return (RiskInformation) super.clone();
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public RiskInformation duplicate() throws CloneNotSupportedException {
		RiskInformation riskInformation = (RiskInformation) super.clone();
		riskInformation.id = -1;
		return riskInformation;
	}

	/**
	 * isEditable: <br>
	 * Returns the editable field value.
	 * 
	 * @return The value of the editable field
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * setEditable: <br>
	 * Sets the Field "editable" with a value.
	 * 
	 * @param editable
	 *            The Value to set the editable field
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * @return the custom
	 */
	public boolean isCustom() {
		return custom;
	}

	/**
	 * @param custom
	 *            the custom to set
	 */
	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	public String getKey() {
		return key(category, chapter);
	}

	public String getMainCategory() {
		return isMatch(Constant.RI_TYPE_RISK) ? Constant.RI_TYPE_RISK : this.category;
	}

	public static String key(String category, String chapter) {
		return ((category == null ? "" : category.startsWith(Constant.RI_TYPE_RISK) ? Constant.RI_TYPE_RISK : category) + "__risk-information__" + chapter).toLowerCase();
	}

	public boolean isMatch(String category) {
		return category == null || this.category == null ? this.category == category
				: category.equalsIgnoreCase(Constant.RI_TYPE_RISK) ? this.category.startsWith(Constant.RI_TYPE_RISK) : this.category.equals(category);
	}

	/**
	 * reset owner, comment, exposed, hiddenComment, id
	 * 
	 * @return duplicate
	 */
	public RiskInformation anonymise() {
		try {
			RiskInformation riskInformation = this.duplicate();
			riskInformation.owner = "";
			riskInformation.comment = "";
			riskInformation.exposed = "";
			riskInformation.hiddenComment = "";
			return riskInformation;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}