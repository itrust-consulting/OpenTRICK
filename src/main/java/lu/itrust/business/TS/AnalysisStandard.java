package lu.itrust.business.TS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lu.itrust.business.exception.TrickException;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * AnalysisStandard: <br>
 * This class represents a AnalysisStandard and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtDiscriminator")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiAnalysis", "fiStandard" }))
public abstract class AnalysisStandard implements Serializable, Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	@Transient
	private static final long serialVersionUID = 1L;

	/** AnalysisStandard id */
	@Id
	@GeneratedValue
	@Column(name = "idAnalysisStandard")
	private int id = -1;

	/** AnalysisStandard Standard Object */
	@ManyToOne
	@JoinColumn(name = "fiStandard", nullable = false)
	@Cascade({ CascadeType.SAVE_UPDATE })
	private Standard standard = null;

	/** AnalysisStandard List of measures */
	@OneToMany(mappedBy = "analysisStandard")
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.DELETE })
	private List<Measure> measures = new ArrayList<Measure>();

	/***********************************************************************************************
	 * Constructor
	 **********************************************************************************************/

	/**
	 * Constructor: <br>
	 * 
	 * @param analysis
	 * @param standard
	 */
	public AnalysisStandard(Standard standard) {
		this.standard = standard;
	}

	/**
	 * Constructor: <br>
	 * 
	 */
	public AnalysisStandard() {
	}

	/***********************************************************************************************
	 * Getter and Setter
	 **********************************************************************************************/

	/**
	 * getName: <br>
	 * Returns the "name" field value
	 * 
	 * @return The AnalysisStandard Name
	 */
	public Standard getStandard() {
		return standard;
	}

	/**
	 * setStandard: <br>
	 * Description
	 * 
	 * @param name
	 * @throws TrickException
	 */
	public void setStandard(Standard name) throws TrickException {
		if (name == null)
			throw new TrickException("error.norm.null", "Standard cannot be empty");
		else if (name.getLabel() == null)
			throw new TrickException("error.norm.label_null", "Standard name cannot be empty");
		this.standard = name;
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
	 * getMeasures: <br>
	 * Returns the measures field value.
	 * 
	 * @return The value of the measures field
	 */
	public List<Measure> getMeasures() {
		return measures;
	}

	/**
	 * setMeasures: <br>
	 * Sets the Field "measures" with a value.
	 * 
	 * @param measures
	 *            The Value to set the measures field
	 */
	public void setMeasures(List<Measure> measures) {
		this.measures = measures;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		AnalysisStandard analysisStandard = (AnalysisStandard) super.clone();
		analysisStandard.measures = new ArrayList<>();
		for (Measure measure : measures) {
			Measure measure2 = (Measure) measure.clone();
			measure2.setAnalysisStandard(analysisStandard);
			analysisStandard.measures.add(measure2);
		}
		return analysisStandard;
	}

	public AnalysisStandard duplicate() throws CloneNotSupportedException {
		AnalysisStandard analysisStandard = (AnalysisStandard) super.clone();
		analysisStandard.id = -1;
		return analysisStandard;
	}

	/**
	 * hashCode:<br>
	 * Used inside equals method.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((standard == null) ? 0 : standard.hashCode());
		result = prime * result + id;
		return result;
	}

	/**
	 * equals: <br>
	 * Check if this object equals another object of the same type. Equal means: the field id,
	 * description, domain and reference.
	 * 
	 * @param obj
	 *            The other object to check on
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AnalysisStandard other = (AnalysisStandard) obj;
		if (standard == null) {
			if (other.standard != null) {
				return false;
			}
		} else if (!standard.equals(other.standard)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		return true;
	}

}