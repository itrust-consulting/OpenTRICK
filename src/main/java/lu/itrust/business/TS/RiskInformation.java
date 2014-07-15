package lu.itrust.business.TS;

import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.exception.TrickException;

/**
 * RiskInformation: <br>
 * This class represents a RiskInformation and its data.
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
public class RiskInformation implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** Risk Information id */
	private int id = -1;

	/** editable */
	private boolean editable;
	
	/** The Risk Information Label */
	private String label;

	/** The Risk Information Exposed Value */
	private String exposed;

	/** The Risk Information Comment */
	private String comment;

	/** The Risk Information Hidden Comment */
	private String hiddenComment;

	/** The Risk Information Category */
	private String category;

	/** The Risk Information Chapter */
	private String chapter;

	/** The Risk Information Acronym */
	private String acronym;

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

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
		this.acronym = acro;
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
		this.label = label;
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
		this.exposed = expo;
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
		this.comment = comment;
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
		this.hiddenComment = hiddenComment;
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
		if (category == null
				|| !category
						.matches(Constant.REGEXP_VALID_RISKINFORMATION_TYPE))
			throw new TrickException("error.risk_information.category.invalid",
					"Category is empty or invalid");
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
		this.chapter = chapter;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public RiskInformation clone() throws CloneNotSupportedException {
		return (RiskInformation) super.clone();
	}

	public RiskInformation duplicate() throws CloneNotSupportedException {
		RiskInformation riskInformation = (RiskInformation) super.clone();
		riskInformation.id = -1;
		return riskInformation;
	}

	/** isEditable: <br>
	 * Returns the editable field value.
	 * 
	 * @return The value of the editable field
	 */
	public boolean isEditable() {
		return editable;
	}

	/** setEditable: <br>
	 * Sets the Field "editable" with a value.
	 * 
	 * @param editable 
	 * 			The Value to set the editable field
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

}