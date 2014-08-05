package lu.itrust.business.TS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.exception.TrickException;

/**
 * AnalysisNorm: <br>
 * This class represents a AnalysisNorm and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
public abstract class AnalysisNorm implements Serializable, Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** AnalysisNorm id */
	private int id = -1;

	/** AnalysisNorm Norm Object */
	private Norm norm = null;

	/** AnalysisNorm List of measures */
	private List<Measure> measures = new ArrayList<Measure>();

	private Analysis analysis = null;

	/***********************************************************************************************
	 * Constructor
	 **********************************************************************************************/

	/**
	 * Constructor: <br>
	 * 
	 * @param analysis
	 *            The Analysis Object
	 * @param norm
	 *            The Norm Object
	 */
	public AnalysisNorm(Analysis analysis, Norm norm) {
		this.analysis = analysis;
		this.norm = norm;
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param norm
	 *            The Norm object
	 */
	public AnalysisNorm(Norm norm) {
		this.norm = norm;
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param analysis
	 *            The Analysis Object
	 */
	public AnalysisNorm(Analysis analysis) {
		this.analysis = analysis;
	}

	/**
	 * Constructor: <br>
	 * 
	 */
	public AnalysisNorm() {
	}

	/***********************************************************************************************
	 * Getter and Setter
	 **********************************************************************************************/

	/**
	 * getName: <br>
	 * Returns the "name" field value
	 * 
	 * @return The AnalysisNorm Name
	 */
	public Norm getNorm() {
		return norm;
	}

	/**
	 * setNorm: <br>
	 * Sets the "name" field with a value
	 * 
	 * @param name
	 *            The value to set the AnalysisNorm Name
	 * @throws TrickException 
	 */
	public void setNorm(Norm name) throws TrickException {
		if (name == null)
			throw new TrickException("error.norm.null", "Standard cannot be empty");
		else if (name.getLabel() == null)
			throw new TrickException("error.norm.label_null","Standard name cannot be empty");
		this.norm = name;
	}

	/**
	 * getAnalysis: <br>
	 * Returns the analysis field value.
	 * 
	 * @return The value of the analysis field
	 */
	public Analysis getAnalysis() {
		return analysis;
	}

	/**
	 * setAnalysis: <br>
	 * Sets the Field "analysis" with a value.
	 * 
	 * @param analysis
	 *            The Value to set the analysis field
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
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
		AnalysisNorm analysisNorm = (AnalysisNorm) super.clone();
		analysisNorm.measures = new ArrayList<>();
		for (Measure measure : measures) {
			Measure measure2 = (Measure) measure.clone();
			measure2.setAnalysisNorm(analysisNorm);
			analysisNorm.measures.add(measure2);
		}
		return analysisNorm;
	}

	public AnalysisNorm duplicate() throws CloneNotSupportedException {
		AnalysisNorm analysisNorm = (AnalysisNorm) super.clone();
		analysisNorm.id = -1;
		return analysisNorm;
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
		result =
			prime * result + ((norm == null) ? 0 : norm.hashCode());
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
		AnalysisNorm other = (AnalysisNorm) obj;
		if (norm == null) {
			if (other.norm != null) {
				return false;
			}
		} else if (!norm.equals(other.norm)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		return true;
	}
	
}