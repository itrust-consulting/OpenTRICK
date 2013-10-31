package lu.itrust.business.TS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.tsconstant.Constant;

/**
 * AnalysisNorm: <br>
 * This class represents a AnalysisNorm and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
public abstract class AnalysisNorm implements Serializable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** AnalysisNorm id */
	private int id = -1;

	/** AnalysisNorm Analysis object */
	private Analysis analysis = null;

	/** AnalysisNorm Norm Object */
	private Norm norm = null;

	/** AnalysisNorm List of measures */
	private List<Measure> measures = new ArrayList<Measure>();

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
	 */
	public void setNorm(Norm name) {
		if (name == null)
			throw new IllegalArgumentException("error.norm.null");
		else if(name.getLabel() == null)
			throw new IllegalArgumentException("error.norm.label_null");
		else if(!name.getLabel().matches(Constant.REGEXP_VALID_NORM_NAME))
			throw new IllegalArgumentException("error.norm.label_no_meet_regex");
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
}