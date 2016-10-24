/**
 * 
 */
package lu.itrust.business.TS.model.scale;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.model.parameter.type.impl.AbstractParameterType;

/**
 * @author eomar
 *
 */
@Entity
public class ScaleType extends AbstractParameterType {

	@Column(name = "dtAcronym", unique = true)
	private String acronym;

	@ElementCollection
	@MapKeyColumn(name = "dtLocale")
	@Column(name = "dtTranslate")
	@Cascade(CascadeType.ALL)
	@CollectionTable(name = "ScaleTypeTranslations", joinColumns = @JoinColumn(name = "fiScaleType"))
	private Map<String, String> translations = new LinkedHashMap<>();

	/**
	 * 
	 */
	public ScaleType() {
	}

	public ScaleType(String name, String acronym) {
		super(name);
		setAcronym(acronym);
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
	 * @param key
	 * @return
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public String get(String key) {
		return translations.get(key);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public String put(String key, String value) {
		return translations.put(key, value);
	}

	/**
	 * @param action
	 * @see java.util.Map#forEach(java.util.function.BiConsumer)
	 */
	public void forEach(BiConsumer<? super String, ? super String> action) {
		translations.forEach(action);
	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

}
