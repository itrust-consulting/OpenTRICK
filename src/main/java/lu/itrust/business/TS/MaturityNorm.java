package lu.itrust.business.TS;

import java.util.List;

/**
 * MaturityNorm: <br>
 * This class represents the MaturityNorm and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
public class MaturityNorm extends AnalysisNorm implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public MaturityNorm() {
		super();
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param analysis
	 *            The Analysis Object
	 * @param norm
	 *            The Norm Name
	 */
	public MaturityNorm(Analysis analysis, Norm norm) {
		super(analysis, norm);
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param analysis
	 *            The Analysis Object
	 */
	public MaturityNorm(Analysis analysis) {
		super(analysis);
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param norm
	 *            The Norm Name
	 */
	public MaturityNorm(Norm norm) {
		super(norm);
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getAMeasure: <br>
	 * Returns the Measure object at position "index" from the measure list ("measures" field)
	 * 
	 * @return The Maturity Measure Object at position "index"
	 */
	public MaturityMeasure getMeasure(int index) {
		if ((index < 0) || (index >= getMeasures().size())) {
			throw new IndexOutOfBoundsException("Maturtiy AnalysisNorm Index (" + index
				+ ") needs be between 0 and " + (getMeasures().size() - 1) + "!");
		}
		return (MaturityMeasure) getMeasures().get(index);
	}

	/**
	 * setMeasures: <br>
	 * Sets the Field "measures" with a value.
	 * 
	 * @param measures
	 *            The Value to set the measures field
	 */
	@Override
	public void setMeasures(List<Measure> measures) {
		for (Measure measure : measures) {
			if (!(measure instanceof MaturityMeasure))
				throw new IllegalArgumentException("Excepted MaturityMeasure");
		}
		super.setMeasures(measures);
	}

	/**
	 * addMeasure: <br>
	 * Adds a new Maturity Measure Object to the list of measures ("measures" field)
	 * 
	 * @param measure
	 *            The Maturity Measure Object to add
	 */
	public void addMeasure(MaturityMeasure measure) {
		if (getMeasures().contains(measure)) {
			throw new IllegalArgumentException(
					"Maturtiy AnalysisNorm Measure duplicates not accepted!");
		}
		measure.setAnalysisNorm(this);
		this.getMeasures().add(measure);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.AnalysisNorm#clone()
	 */
	@Override
	public MaturityNorm clone() throws CloneNotSupportedException {
		return (MaturityNorm) super.clone();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.AnalysisNorm#basicClone()
	 */
	@Override
	public MaturityNorm duplicate() throws CloneNotSupportedException {
		return (MaturityNorm) super.duplicate();
	}
	
	
}