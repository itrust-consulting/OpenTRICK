package lu.itrust.business.ts.model.standard.measuredescription;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.general.Language;

/**
 * MeasureDescriptionText: <br>
 * Represents the Domain and Description of a measure in a single language
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MeasureDescriptionText implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The MeasureDescriptionText id */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "idMeasureDescriptionText")
	private int id = 0;

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
	@Column(name = "dtDomain", nullable = false, length=65536)
	private String domain = "";

	/** The Description Text */
	@Column(name = "dtDescription", nullable = false, length=16777216)
	private String description = "";
	
	/**
	 * 
	 */
	public MeasureDescriptionText() {
	}

	public MeasureDescriptionText(Language language) {
		setLanguage(language);
	}
	
	public MeasureDescriptionText(MeasureDescription measureDescription, Language language) {
		this(language);
		setMeasureDescription(measureDescription);
	}
	
	public MeasureDescriptionText(MeasureDescription measureDescription, String domain, String description, Language language) throws TrickException {
		this(measureDescription,language);
		setDomain(domain);
		setDescription(description);
	}

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
		if (domain == null)
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
		if (!this.measureDescription.isComputable())
			this.description = "";
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
		measureDescriptionText.id = 0;
		measureDescriptionText.measureDescription = description;
		return measureDescriptionText;
	}

	public void update(String domain, String description) throws TrickException {
		setDomain(domain);
		setDescription(description);
	}

}