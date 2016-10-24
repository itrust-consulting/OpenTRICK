/**
 * 
 */
package lu.itrust.business.TS.model.scale;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

	@ManyToOne
	@JoinColumn(name = "fiType", unique = true)
	private ScaleType type;

	@Column(name = "dtLevel")
	private int level;

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

	public Scale(ScaleType type, int level, double maxValue) {
		setType(type);
		setLevel(level);
		setMaxValue(maxValue);
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
	 * @return the type
	 */
	public ScaleType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(ScaleType type) {
		this.type = type;
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

	public void merge(Scale scale) {
		if (scale.type != null)
			scale.type.forEach((local, translate) -> this.type.put(local, translate));
		this.level = scale.level;
		this.maxValue = scale.maxValue;
	}

}
