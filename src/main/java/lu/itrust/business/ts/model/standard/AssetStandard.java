package lu.itrust.business.ts.model.standard;

import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import org.hibernate.proxy.HibernateProxy;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;

/**
 * AssetMeasureNorm: <br>
 * Detailed description...
 */
@Entity
@DiscriminatorValue("AssetStandard")
public class AssetStandard extends AnalysisStandard {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

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
	 * @param norm
	 *            The Norm Object
	 */
	public AssetStandard(Standard norm) {
		super(norm);
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
	
	

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.standard.AnalysisStandard#getExendedMeasures()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AssetMeasure> getExendedMeasures() {
		return (List<AssetMeasure>) super.getExendedMeasures();
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
	 * Adds a new NormMeasure to the List of Measures ("measures" field)
	 * 
	 * @param measure
	 *            The new object to add
	 * @throws TrickException
	 */
	public void addMeasure(AssetMeasure measure) throws TrickException {
		if (this.getMeasures().contains(measure))
			throw new TrickException("error.measure_norm.measure.duplicate", "Measure duplicates not accepted!");
		this.getMeasures().add(measure);
	}

	/**
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.model.standard.AnalysisStandard#clone()
	 */
	@Override
	public AssetStandard clone() throws CloneNotSupportedException {
		return (AssetStandard) super.clone();
	}

	/**
	 * duplicate: <br>
	 * Description
	 * @throws TrickException 
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.model.standard.AnalysisStandard#duplicate()
	 */
	@Override
	public AssetStandard duplicate() throws CloneNotSupportedException, TrickException {
		return (AssetStandard) super.duplicate();
	}

	/**
	 * Adds a measure to the asset.
	 *
	 * @param measure the measure to be added
	 */
	@Override
	public void add(Measure measure) {
		addMeasure((AssetMeasure) measure);
	}

	/**
	 * Adds a new measure description to the asset standard.
	 *
	 * @param measureDescription the measure description to be added
	 */
	@Override
	public void add(MeasureDescription measureDescription) {
		add(new AssetMeasure(measureDescription));
		
	}

}