/**
 * 
 */
package lu.itrust.business.TS.model.scale;

import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * @author eomar
 *
 */
@Entity
public class Scale {

	@Id
	@Column(name = "idScale")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "dtName", unique = true)
	private String name;

	@Column(name = "dtAcronym", unique = true)
	private String acronym;

	@ElementCollection
	@MapKeyColumn(name = "dtLocale")
	@Column(name = "dtTranslate")
	@Cascade(CascadeType.ALL)
	@CollectionTable(name = "ScaleTranslations", joinColumns = @JoinColumn(name = "fiScale"))
	private Map<String, String> translations;

	@Column(name = "dtLevel")
	private int level;

	@Column(name = "dtMinValue")
	private double minValue;

	@Column(name = "dtMaxValue")
	private double maxValue;

	@Cascade(CascadeType.ALL)
	@JoinColumn(name = "fiScale")
	@OneToMany
	private List<ScaleEntry> scaleEntries;

	/**
	 * 
	 */
	public Scale() {
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the translations
	 */
	public Map<String, String> getTranslations() {
		return translations;
	}

	/**
	 * @param translations
	 *            the translations to set
	 */
	public void setTranslations(Map<String, String> translations) {
		this.translations = translations;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return the minValue
	 */
	public double getMinValue() {
		return minValue;
	}

	/**
	 * @param minValue
	 *            the minValue to set
	 */
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	/**
	 * @return the maxValue
	 */
	public double getMaxValue() {
		return maxValue;
	}

	/**
	 * @param maxValue
	 *            the maxValue to set
	 */
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * @return the scaleEntries
	 */
	public List<ScaleEntry> getScaleEntries() {
		return scaleEntries;
	}

	/**
	 * @param scaleEntries
	 *            the scaleEntries to set
	 */
	public void setScaleEntries(List<ScaleEntry> scaleEntries) {
		this.scaleEntries = scaleEntries;
	}

	/**
	 * @return the acronym
	 */
	public String getAcronym() {
		return acronym;
	}

	/**
	 * @param acronym
	 *            the acronym to set
	 */
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	public void merge(Scale scale) {
		if(scale.translations != null)
			scale.translations.forEach((local, translate) -> this.translations.put(local, translate));
		this.level = scale.level;
		this.maxValue = scale.maxValue;
		this.minValue = scale.minValue;
	}

}
