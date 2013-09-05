package lu.itrust.business.TS;

import java.util.List;

/**
 * MeasureNorm: <br>
 * This class represents a MeasureNorm and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
public class MeasureNorm extends AnalysisNorm {

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
	public MeasureNorm() {
		super();
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param analysis
	 *            The Analysis Object
	 */
	public MeasureNorm(Analysis analysis) {
		super(analysis);
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param norm
	 *            The Norm Object
	 */
	public MeasureNorm(Norm norm) {
		super(norm);
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param analysis
	 *            The Analysis Object
	 * @param norm
	 *            The Norm Object
	 */
	public MeasureNorm(Analysis analysis, Norm name) {
		super(analysis, name);
	}

	/***********************************************************************************************
	 * Getters and Setters
	 *********************************************************************************************** 
	 * 
	 * /** getMeasure: <br>
	 * Returns a NormMeasure Object at position "index" from the list of AnalysisNorm Measures
	 * ("measures" field)
	 * 
	 * @return The NormMeasure at position "index"
	 */
	public NormMeasure getMeasure(int index) {
		if ((index < 0) || (index >= getMeasures().size())) {
			throw new IndexOutOfBoundsException("Index (" + index + ") needs to be between 0 and "
				+ (getMeasures().size() - 1));
		}
		return (NormMeasure) getMeasures().get(index);
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
			if (!(measure instanceof NormMeasure))
				throw new IllegalArgumentException("Excepted NormMeasure");
		}
		super.setMeasures(measures);
	}

	/**
	 * addMeasure: <br>
	 * Adds a new NormMeasure to the List of Measures ("measures" field)
	 * 
	 * @param measure
	 *            The new object to add
	 */
	public void addMeasure(NormMeasure measure) {
		if (this.getMeasures().contains(measure)) {
			throw new IllegalArgumentException("Measure cannot be duplicated!");
		}
		measure.setAnalysisNorm(this);
		this.getMeasures().add(measure);
	}
}