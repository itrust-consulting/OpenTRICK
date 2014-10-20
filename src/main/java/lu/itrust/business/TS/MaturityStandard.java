package lu.itrust.business.TS;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.exception.TrickException;

import org.hibernate.proxy.HibernateProxy;

/**
 * MaturityStandard: <br>
 * This class represents the MaturityStandard and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@DiscriminatorValue("MaturityStandard")
public class MaturityStandard extends AnalysisStandard implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	@Transient
	private static final long serialVersionUID = 1L;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public MaturityStandard() {
		super();
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param standard
	 *            The standard
	 */
	public MaturityStandard(Standard standard) {
		super(standard);
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
		if (index < 0 || getMeasures() == null || index >= getMeasures().size())
			throw new IndexOutOfBoundsException("Maturtiy AnalysisStandard Index (" + index + ") needs be between 0 and " + (getMeasures().size() - 1) + "!");
		if (getMeasures().get(index) instanceof HibernateProxy)
			return MaturityMeasure.class.cast(((HibernateProxy) getMeasures().get(index)).getHibernateLazyInitializer().getImplementation());
		else {
			return MaturityMeasure.class.cast(getMeasures().get(index));
		}
	}

	public List<Measure> getLevel1Measures() {
		List<Measure> measures = new ArrayList<Measure>();
		for (Measure measure : super.getMeasures())
			if (measure.getMeasureDescription().getLevel() == Constant.MEASURE_LEVEL_1)
				measures.add(measure);
		return measures;
	}

	/**
	 * setMeasures: <br>
	 * Sets the Field "measures" with a value.
	 * 
	 * @param measures
	 *            The Value to set the measures field
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setMeasures(List<Measure> measures) {

		if (measures instanceof HibernateProxy) {
			List<Measure> deproxiedmeasures = (List<Measure>) ((HibernateProxy) measures).getHibernateLazyInitializer().getImplementation();
			super.setMeasures(deproxiedmeasures);
		} else {
			super.setMeasures(measures);
		}
	}

	/**
	 * addMeasure: <br>
	 * Adds a new Maturity Measure Object to the list of measures ("measures" field)
	 * 
	 * @param measure
	 *            The Maturity Measure Object to add
	 * @throws TrickException
	 */
	public void addMeasure(MaturityMeasure measure) throws TrickException {
		if (getMeasures().contains(measure))
			throw new TrickException("error.maturity_norm.measure.duplicate", "Measure duplicates not accepted!");
		measure.setAnalysisStandard(this);
		this.getMeasures().add(measure);
	}

	/**
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.AnalysisStandard#clone()
	 */
	@Override
	public MaturityStandard clone() throws CloneNotSupportedException {
		return (MaturityStandard) super.clone();
	}

	/**
	 * duplicate: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.AnalysisStandard#duplicate()
	 */
	@Override
	public MaturityStandard duplicate() throws CloneNotSupportedException {
		return (MaturityStandard) super.duplicate();
	}

}