package lu.itrust.business.TS.data.standard.measuredescription;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lu.itrust.business.TS.data.general.Language;
import lu.itrust.business.TS.exception.TrickException;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * MeasureDescriptionText: <br>
 * Represents the Domain and Description of a measure in a single language
 * 
 * @author itrust consulting s.à r.l. : SME, BJA, EOM
 * @version 0.1
 * @since Jan 28, 2013
 */
@Entity
public class MeasureDescriptionText implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The MeasureDescriptionText id */
	@Id
	@GeneratedValue
	@Column(name = "idMeasureDescriptionText")
	private int id = -1;

	/** The Measure Description Reference (Reference to the measure) */
	@ManyToOne
	@JoinColumn(name = "fiMeasureDescription", nullable = false)
	@Cascade({ CascadeType.SAVE_UPDATE })
	@Access(AccessType.FIELD)
	private MeasureDescription measureDescription = null;

	/** The Language Object */
	@ManyToOne
	@JoinColumn(name = "fiLanguage", nullable = false)
	@Cascade({ CascadeType.SAVE_UPDATE })
	@Access(AccessType.FIELD)
	private Language language = null;

	/** The Domain Text */
	@Column(name = "dtDomain", nullable = false, columnDefinition = "TEXT")
	private String domain = "";

	/** The Description Text */
	@Column(name = "dtDescription", nullable = false, columnDefinition = "LONGTEXT")
	private String description = "";

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getDomain: <br>
	 * Returns the "domain" field value
	 * 
	 * @return The Domain Value
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * setDomain: <br>
	 * Sets the "domain" field with a value
	 * 
	 * @param domain
	 *            The value to set the Domain
	 * @throws TrickException
	 */
	public void setDomain(String domain) throws TrickException {
		if (domain == null || domain.trim().isEmpty())
			throw new TrickException("error.measure_description.domain", "Measure Domain cannot be empty!");
		this.domain = domain;
	}

	/**
	 * getDescription: <br>
	 * Returns the "description" field value
	 * 
	 * @return The Measure Description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * setDescription: <br>
	 * Sets the "description" field with a value
	 * 
	 * @param description
	 *            The Value to set the Measure Description
	 * @throws TrickException
	 */
	public void setDescription(String description) throws TrickException {
		if (this.measureDescription == null)
			throw new NullPointerException("Measuredescription field needs to be set before!");
		if ((this.measureDescription.getLevel() == 1) || (this.measureDescription.getLevel() == 2))
			this.description = "";
		else if (this.measureDescription.getLevel() == -1)
			throw new TrickException("error.measure_description.description.early.initialise", "Level needs to be initialise before adding a description!");
		else if (description == null)
			throw new TrickException("error.measure_description.description", "The description cannot be empty");
		this.description = description;
	}

	/**
	 * getLang: <br>
	 * Returns the lang field value.
	 * 
	 * @return The value of the lang field
	 */
	public Language getLanguage() {
		return language;
	}

	/**
	 * setLang: <br>
	 * Sets the Field "lang" with a value.
	 * 
	 * @param lang
	 *            The Value to set the lang field
	 */
	public void setLanguage(Language lang) {
		this.language = lang;
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
	 * getMeasureDescription: <br>
	 * Returns the measureDescription field value.
	 * 
	 * @return The value of the measureDescription field
	 */
	public MeasureDescription getMeasureDescription() {
		return measureDescription;
	}

	/**
	 * setMeasureDescription: <br>
	 * Sets the Field "measureDescription" with a value.
	 * 
	 * @param measureDescription
	 *            The Value to set the measureDescription field
	 */
	public void setMeasureDescription(MeasureDescription measureDescription) {
		this.measureDescription = measureDescription;
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
	public MeasureDescriptionText clone() throws CloneNotSupportedException {
		return (MeasureDescriptionText) super.clone();
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @param description
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public MeasureDescriptionText duplicate(MeasureDescription description) throws CloneNotSupportedException {
		MeasureDescriptionText measureDescriptionText = (MeasureDescriptionText) super.clone();
		measureDescriptionText.id = -1;
		measureDescriptionText.measureDescription = description;
		return measureDescriptionText;
	}

}