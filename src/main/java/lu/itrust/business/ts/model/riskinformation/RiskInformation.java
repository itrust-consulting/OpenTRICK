package lu.itrust.business.ts.model.riskinformation;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.exception.TrickException;


/**
 * RiskInformation represents a risk information entity in the system.
 * It contains various fields and methods to manage and manipulate risk information data.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiAnalysis", "dtLabel", "dtChapter", "dtCategory" }))
public class RiskInformation implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** Risk Information id */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idRiskInformation")
	private int id = 0;

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
	 *              the owner to set
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
	 *             The value to set the Acronym
	 */
	public void setAcronym(String acro) {
		this.acronym = cleanUpValue(acro);
	}

	private String cleanUpValue(String value) {
		return value == null ? "" : value.trim();
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
	 *              The value to set the Label
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
	 *             The value to set the Exposed value
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
	 *                The value to set the Comment
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
	 *                      The value to set the Hidden Comment
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
	 *                 The value to set the Category
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
	 *                The value to set the Chapter
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
	 *           The Value to set the id field
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
		riskInformation.id = 0;
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
	 *                 The Value to set the editable field
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
	 *               the custom to set
	 */
	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	/**
	 * Returns the key for the risk information.
	 *
	 * @return the key as a String.
	 */
	public String getKey() {
		return key(category, chapter);
	}

	/**
	 * Returns the main category of the risk information.
	 *
	 * @return the main category of the risk information
	 */
	public String getMainCategory() {
		return isMatch(Constant.RI_TYPE_RISK) ? Constant.RI_TYPE_RISK : this.category;
	}

	/**
	 * Returns a key based on the given category and chapter.
	 *
	 * @param category the category of the risk information
	 * @param chapter the chapter of the risk information
	 * @return the generated key
	 */
	public static String key(String category, String chapter) {
		return ((category == null ? "" : category.startsWith(Constant.RI_TYPE_RISK) ? Constant.RI_TYPE_RISK : category)
				+ "__risk-information__" + chapter).toLowerCase();
	}

	/**
	 * Checks if the given category matches the category of this RiskInformation object.
	 * 
	 * @param category the category to compare with
	 * @return true if the category matches, false otherwise
	 */
	public boolean isMatch(String category) {
		return category == null || this.category == null ? Objects.equals(this.category, category) //ignored warning as check null
				: category.equalsIgnoreCase(Constant.RI_TYPE_RISK) ? this.category.startsWith(Constant.RI_TYPE_RISK)
						: this.category.equalsIgnoreCase(category);
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