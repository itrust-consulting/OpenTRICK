package lu.itrust.business.TS;

import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import lu.itrust.business.exception.TrickException;
import org.hibernate.proxy.HibernateProxy;

/**
 * AssetStandard: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version 
 * @since Aug 25, 2014
 */
@Entity
@DiscriminatorValue("AssetStandard")
public class AssetStandard extends AnalysisStandard {

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
	public AssetStandard() {
		super();
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param analysis
	 *            The Analysis Object
	 */
	public AssetStandard(Analysis analysis) {
		super(analysis);
	}

	/**
	 * Constructor: <br>
	 * @param standard
	 */
	public AssetStandard(Standard standard) {
		super(standard);
	}

	/**
	 * Constructor: <br>
	 * @param analysis
	 * @param standard
	 */
	public AssetStandard(Analysis analysis, Standard standard) {
		super(analysis, standard);
	}

	/***********************************************************************************************
	 * Getters and Setters
	 *********************************************************************************************** 
	 * 
	 * /** getMeasure: <br>
	 * Returns a AssetMeasure Object at position "index" from the list of
	 * AnalysisStandard Measures ("measures" field)
	 * 
	 * @return The AssetMeasure at position "index"
	 */
	public AssetMeasure getMeasure(int index) {
		if ((index < 0) || (index >= getMeasures().size())) {
			throw new IndexOutOfBoundsException("Index (" + index + ") needs to be between 0 and " + (getMeasures().size() - 1));
		}

		if (getMeasures().get(index) instanceof HibernateProxy)
			return AssetMeasure.class.cast(((HibernateProxy) getMeasures().get(index)).getHibernateLazyInitializer().getImplementation());
		else {
			return AssetMeasure.class.cast(getMeasures().get(index));
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
	 * Adds a new AssetMeasure to the List of Measures ("measures" field)
	 * 
	 * @param measure
	 *            The new object to add
	 * @throws TrickException 
	 */
	public void addMeasure(AssetMeasure measure) throws TrickException {
		if (this.getMeasures().contains(measure))
			throw new TrickException("error.measure_norm.measure.duplicate", "Measure duplicates not accepted!");
		measure.setAnalysisStandard(this);
		this.getMeasures().add(measure);
	}
}