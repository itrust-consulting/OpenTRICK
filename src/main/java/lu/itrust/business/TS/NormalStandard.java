package lu.itrust.business.TS;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import lu.itrust.business.exception.TrickException;

import org.hibernate.proxy.HibernateProxy;

/**
 * NormalStandard: <br>
 * This class represents a NormalStandard and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@DiscriminatorValue("NormalStandard")
public class NormalStandard extends AnalysisStandard {

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
	public NormalStandard() {
		super();
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param analysis
	 *            The Analysis Object
	 */
	public NormalStandard(Analysis analysis) {
		super(analysis);
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param standard
	 */
	public NormalStandard(Standard standard) {
		super(standard);
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param analysis
	 *            The Analysis Object
	 * @param standard
	 *            The standard Object
	 */
	public NormalStandard(Analysis analysis, Standard standard) {
		super(analysis, standard);
	}

	/***********************************************************************************************
	 * Getters and Setters
	 *********************************************************************************************** 
	 * 
	 * /** getMeasure: <br>
	 * Returns a NormalMeasure Object at position "index" from the list of AnalysisStandard Measures
	 * ("measures" field)
	 * 
	 * @return The NormalMeasure at position "index"
	 */
	public NormalMeasure getMeasure(int index) {
		if ((index < 0) || (index >= getMeasures().size())) {
			throw new IndexOutOfBoundsException("Index (" + index + ") needs to be between 0 and " + (getMeasures().size() - 1));
		}

		if (getMeasures().get(index) instanceof HibernateProxy)
			return NormalMeasure.class.cast(((HibernateProxy) getMeasures().get(index)).getHibernateLazyInitializer().getImplementation());
		else {
			return NormalMeasure.class.cast(getMeasures().get(index));
		}

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
	 * Adds a new NormalMeasure to the List of Measures ("measures" field)
	 * 
	 * @param measure
	 *            The new object to add
	 * @throws TrickException
	 */
	public void addMeasure(NormalMeasure measure) throws TrickException {
		if (this.getMeasures().contains(measure))
			throw new TrickException("error.measure_norm.measure.duplicate", "Measure duplicates not accepted!");
		measure.setAnalysisStandard(this);
		this.getMeasures().add(measure);
	}
}