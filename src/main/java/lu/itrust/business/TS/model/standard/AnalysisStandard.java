package lu.itrust.business.TS.model.standard;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Formula;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;

/**
 * AnalysisStandard: <br>
 * This class represents a AnalysisStandard and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtDiscriminator")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiAnalysis", "fiStandard" }))
public abstract class AnalysisStandard implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** AnalysisStandard id */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAnalysisStandard")
	private int id = -1;
	
	/**
	 * Only for hibernate mapping, 
	 * see analysisStandard mapping from Analysis.
	 */
	@Formula("(Select STD.dtName From Standard STD where STD.idStandard = fiStandard)")
	private String name;

	/** AnalysisStandard Standard Object */
	@ManyToOne
	@JoinColumn(name = "fiStandard", nullable = false)
	@Access(AccessType.FIELD)
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE })
	private Standard standard = null;

	/** AnalysisStandard List of measures */
	@OneToMany(mappedBy = "analysisStandard")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<Measure> measures = new ArrayList<Measure>();

	@Column(name = "dtSOAEnabled",nullable = false)
	private boolean soaEnabled = false;

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

	public boolean isComputable() {
		if (standard == null)
			throw new TrickException("error.standard.null", "Standard cannot be null");
		return standard.isComputable();
	}

	public boolean isAnalysisOnly() {
		if (standard == null)
			throw new TrickException("error.standard.null", "Standard cannot be null");
		return standard.isAnalysisOnly();
	}

	/**
	 * setStandard: <br>
	 * Description
	 * 
	 * @param standard
	 * @throws TrickException
	 */
	public void setStandard(Standard standard) throws TrickException {
		if (standard == null)
			throw new TrickException("error.norm.null", "Standard cannot be empty");
		else if (standard.getLabel() == null)
			throw new TrickException("error.norm.label_null", "Standard name cannot be empty");
		else if(standard.getName() == null)
			throw new TrickException("error.norm.name_null", "Standard display name cannot be empty");
		this.standard = standard;
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
	 * getMeasures: <br>
	 * Returns the measures field value.
	 * 
	 * @return The value of the measures field
	 */
	public List<? extends Measure> getExendedMeasures() {
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

	/**
	 * @return the soaEnabled
	 */
	public boolean isSoaEnabled() {
		return soaEnabled;
	}

	/**
	 * @param soaEnabled
	 *            the soaEnabled to set
	 */
	public void setSoaEnabled(Boolean soaEnabled) {
		if (soaEnabled == null)
			soaEnabled = standard != null && standard.is(Constant.STANDARD_27002);
		this.soaEnabled = soaEnabled;
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

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 * @throws TrickException
	 */
	public AnalysisStandard duplicate() throws CloneNotSupportedException, TrickException {
		AnalysisStandard analysisStandard = (AnalysisStandard) super.clone();
		analysisStandard.id = -1;
		analysisStandard.measures = new ArrayList<>();
		for (Measure measure : measures) {
			Measure measure2 = (Measure) measure.duplicate(analysisStandard, measure.getPhase());
			analysisStandard.measures.add(measure2);
		}
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
	 * Check if this object equals another object of the same type. Equal means:
	 * the field id, description, domain and reference.
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

	public abstract void add(Measure measure);

	public abstract void add(MeasureDescription measureDescription);

}