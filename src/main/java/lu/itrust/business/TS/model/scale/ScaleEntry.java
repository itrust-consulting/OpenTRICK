/**
 * 
 */
package lu.itrust.business.TS.model.scale;

import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.model.parameter.helper.Bounds;

/**
 * @author eomar
 *
 */
@Entity
public class ScaleEntry {

	@Id
	@Column(name = "idScaleEntry")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name="dtLavel")
	private int level;

	@Column(name="dtAcronym")
	private String acronym;

	@Column(name="dtValue")
	private double value;

	@Embedded
	private Bounds bounds;

	@ElementCollection
	@MapKeyColumn(name = "dtLocale")
	@Column(name = "dtQualification")
	@Cascade(CascadeType.ALL)
	@CollectionTable(name = "ScaleEntryQualifications", joinColumns = @JoinColumn(name = "fiScaleEntry"))
	private Map<String, String> qualifications;

	/**
	 * 
	 */
	public ScaleEntry() {
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
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * @return the bounds
	 */
	public Bounds getBounds() {
		return bounds;
	}

	/**
	 * @param bounds
	 *            the bounds to set
	 */
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	/**
	 * @return the qualifications
	 */
	public Map<String, String> getQualifications() {
		return qualifications;
	}

	/**
	 * @param qualifications
	 *            the qualifications to set
	 */
	public void setQualifications(Map<String, String> qualifications) {
		this.qualifications = qualifications;
	}
}
