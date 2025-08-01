package lu.itrust.business.ts.model.standard.measuredescription;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.standard.Standard;

/**
 * MeasureDescription: <br>
 * Represents the description of a Measure:
 * <ul>
 * <li>The Standard Object</li>
 * <li>The Level of Measure (1-3)</li>
 * <li>The Measure Reference inside the Standard</li>
 * <li>Measure Description Texts which represents the Domain and Description f a
 * Measure in one to more languages</li>
 * </ul>
 */
/**
 * Represents a measure description.
 * This class is used to store information about a measure, including its ID, standard, description texts, reference, and computable flag.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiStandard", "dtReference" }))
public class MeasureDescription implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** Measure Description id */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idMeasureDescription")
	private int id = 0;

	/** Measure Standard Object */
	@ManyToOne
	@JoinColumn(name = "fiStandard", nullable = false)
	@Cascade(CascadeType.SAVE_UPDATE)
	@Access(AccessType.FIELD)
	private Standard standard = null;

	/** Measure Description Text List (one entry represents one language) */
	@OneToMany(mappedBy = "measureDescription", fetch = FetchType.EAGER)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<MeasureDescriptionText> measureDescriptionTexts = new ArrayList<>();

	/** Measure Level */
	//@Column(name = "dtLevel", nullable = false)
	//private int level = 3;

	/** Measure Reference */
	@Column(name = "dtReference", nullable = false)
	private String reference = "";

	/**
	 * Flag to determine if measure can be used in the action plan (before:
	 * measure had to be level 3)
	 */
	@Column(name = "dtComputable", nullable = false)
	private boolean computable = true;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/
	
	

	/**
	 * Constructor:<br>
	 * 
	 * @param reference
	 *            Reference of the Measure
	 * @param standard
	 *            standard of the Measure
	 */
	public MeasureDescription(String reference, Standard standard, boolean computable) {
		this.standard = standard;
		this.reference = reference;
		this.computable = computable;
	}

	/**
	 * @param reference
	 * @param standard
	 */
	public MeasureDescription(String reference, Standard standard) {
		setReference(reference);
		setStandard(standard);
	}

	/**
	 * getMeasureDescriptionText: <br>
	 * Description
	 * 
	 * @param language
	 * @return
	 */
	public MeasureDescriptionText getMeasureDescriptionText(Language language) {
		return getMeasureDescriptionTextByAlpha3(language.getAlpha3());
	}

	/**
	 * findByLanguage: <br>
	 * Description
	 * 
	 * @param language
	 * @return
	 */
	public MeasureDescriptionText findByLanguage(Language language) {
		return measureDescriptionTexts.parallelStream().filter(measureDescriptionText -> measureDescriptionText.getLanguage().equals(language)).findAny().orElse(null);
	}

	/**
	 * findByAlph3: <br>
	 * Description
	 * 
	 * @param alpha3
	 * @return
	 */
	public MeasureDescriptionText findByAlph2(String alpha2) {
		return measureDescriptionTexts.parallelStream().filter(measureDescriptionText -> measureDescriptionText.getLanguage().getAlpha2().equalsIgnoreCase(alpha2)).findAny().orElse(null);
	}

	/**
	 * getMeasureDescriptionTextByAlpha3: <br>
	 * Description
	 * 
	 * @param alpha3
	 * @return
	 */
	public MeasureDescriptionText getMeasureDescriptionTextByAlpha2(String alpha2) {
		MeasureDescriptionText descriptionTextEnglish = null;
		for (MeasureDescriptionText measureDescriptionText : measureDescriptionTexts) {
			if (measureDescriptionText.getLanguage().getAlpha2().equalsIgnoreCase(alpha2))
				return measureDescriptionText;
			else if (measureDescriptionText.getLanguage().getAlpha2().equalsIgnoreCase("en"))
				descriptionTextEnglish = measureDescriptionText;
		}
		return descriptionTextEnglish != null ? descriptionTextEnglish : !measureDescriptionTexts.isEmpty() ? measureDescriptionTexts.get(0) : null;
	}

	/**
	 * findByAlph3: <br>
	 * Description
	 * 
	 * @param alpha3
	 * @return
	 */
	public MeasureDescriptionText findByAlph3(String alpha3) {
		for (MeasureDescriptionText measureDescriptionText : measureDescriptionTexts)
			if (measureDescriptionText.getLanguage().getAlpha3().equalsIgnoreCase(alpha3))
				return measureDescriptionText;
		return null;
	}

	/**
	 * getMeasureDescriptionTextByAlpha3: <br>
	 * Description
	 * 
	 * @param alpha3
	 * @return
	 */
	public MeasureDescriptionText getMeasureDescriptionTextByAlpha3(String alpha3) {

		MeasureDescriptionText descriptionText = null;
		MeasureDescriptionText descriptionTextEnglish = null;

		for (MeasureDescriptionText measureDescriptionText : measureDescriptionTexts) {
			if (measureDescriptionText.getLanguage().getAlpha3().equalsIgnoreCase(alpha3))
				return measureDescriptionText;
			else if (measureDescriptionText.getLanguage().getAlpha3().equalsIgnoreCase("eng"))
				descriptionTextEnglish = measureDescriptionText;
		}

		return descriptionText == null && descriptionTextEnglish != null ? descriptionTextEnglish
				: descriptionText == null && measureDescriptionTexts.size() > 0 ? measureDescriptionTexts.get(0) : descriptionText;
	}

	/**
	 * Constructor: <br>
	 *
	 */
	public MeasureDescription() {
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	public MeasureDescription(MeasureDescriptionText measureDescriptionText) {
		measureDescriptionTexts.add(measureDescriptionText);
	}

	public MeasureDescription(Standard standard, MeasureDescriptionText measureDescriptionText) {
		this.standard = standard;
		this.measureDescriptionTexts.add(measureDescriptionText);
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
	 * getStandard: <br>
	 * Description
	 * 
	 * @return
	 */
	public Standard getStandard() {
		return standard;
	}

	/**
	 * setStandard: <br>
	 * Description
	 * 
	 * @param standard
	 */
	public void setStandard(Standard standard) {
		this.standard = standard;
	}

	/**
	 * getLevel: <br>
	 * Returns the level field value.
	 * 
	 * @return The value of the level field
	 */
	/*public int getLevel() {
		return level;
	}*/

	/**
	 * setLevel: <br>
	 * Sets the Field "level" with a value.
	 * 
	 * @param level
	 *            The Value to set the level field
	 */
	/*public void setLevel(int level) {
		this.level = level;
	}*/

	/**
	 * getReference: <br>
	 * Returns the reference field value.
	 * 
	 * @return The value of the reference field
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * setReference: <br>
	 * Sets the Field "reference" with a value.
	 * 
	 * @param reference
	 *            The Value to set the reference field
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * getMeasureDescriptionText: <br>
	 * Returns the measureDescriptionTexts field value.
	 * 
	 * @return The value of the measureDescriptionTexts field
	 */
	public MeasureDescriptionText getAMeasureDescriptionText(int index) {
		if (index > -1 && measureDescriptionTexts.size() > index)
			return measureDescriptionTexts.get(index);
		return null;
	}

	/**
	 * getMeasureDescriptionText: <br>
	 * Returns the measureDescriptionTexts field value.
	 * 
	 * @return The value of the measureDescriptionTexts field
	 */
	public MeasureDescriptionText getAMeasureDescriptionText(Language lang) {
		for (int i = 0; i < measureDescriptionTexts.size(); i++) {
			if (measureDescriptionTexts.get(i).getLanguage().equals(lang)) {
				return measureDescriptionTexts.get(i);
			}
		}
		return null;
	}

	/**
	 * setMeasureDescriptionText: <br>
	 * Sets the Field "measureDescriptionTexts" with a value.
	 * 
	 * @param measureDescriptionTexts
	 *            The Value to set the measureDescriptionTexts field
	 */
	public void addMeasureDescriptionText(MeasureDescriptionText measureDescriptionText) {
		measureDescriptionText.setMeasureDescription(this);
		this.measureDescriptionTexts.add(measureDescriptionText);
	}

	/**
	 * getMeasureDescriptionTexts: <br>
	 * Returns the measureDescriptionTexts field value.
	 * 
	 * @return The value of the measureDescriptionTexts field
	 */
	public List<MeasureDescriptionText> getMeasureDescriptionTexts() {
		return measureDescriptionTexts;
	}

	/**
	 * setMeasureDescriptionTexts: <br>
	 * Sets the Field "measureDescriptionTexts" with a value.
	 * 
	 * @param measureDescriptionTexts
	 *            The Value to set the measureDescriptionTexts field
	 */
	public void setMeasureDescriptionTexts(List<MeasureDescriptionText> measureDescriptionTexts) {
		for (MeasureDescriptionText measureDescriptionText : measureDescriptionTexts)
			measureDescriptionText.setMeasureDescription(this);
		this.measureDescriptionTexts = measureDescriptionTexts;
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
	public MeasureDescription clone() throws CloneNotSupportedException {
		return (MeasureDescription) super.clone();
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public MeasureDescription duplicate(Standard standard) throws CloneNotSupportedException {
		MeasureDescription measureDescription = (MeasureDescription) super.clone();
		measureDescription.id = 0;
		measureDescription.standard = standard;
		List<MeasureDescriptionText> texts = new ArrayList<MeasureDescriptionText>();
		for (MeasureDescriptionText text : this.measureDescriptionTexts)
			texts.add(text.duplicate(measureDescription));
		measureDescription.setMeasureDescriptionTexts(texts);
		return measureDescription;
	}

	/**
	 * isComputable: <br>
	 * Returns the computable field value.
	 * 
	 * @return The value of the computable field
	 */
	public boolean isComputable() {
		return computable;
	}

	/**
	 * setComputable: <br>
	 * Sets the Field "computable" with a value.
	 * 
	 * @param computable
	 *            The Value to set the computable field
	 */
	public void setComputable(boolean computable) {
		this.computable = computable;
	}
	
}
