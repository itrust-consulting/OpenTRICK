package lu.itrust.business.ts.model.standard;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import org.hibernate.proxy.HibernateProxy;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.impl.MaturityMeasure;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;

/**
 * MaturityStandard: <br>
 * This class represents the MaturityStandard and its data
 */
@Entity
@DiscriminatorValue("MaturityStandard")
public class MaturityStandard extends AnalysisStandard implements Cloneable {

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
	 * Returns the Measure object at position "index" from the measure list
	 * ("measures" field)
	 * 
	 * @return The Maturity Measure Object at position "index"
	 */
	public MaturityMeasure getMeasure(int index) {
		if (index < 0 || getMeasures() == null || index >= getMeasures().size())
			throw new IndexOutOfBoundsException("Maturtiy AnalysisStandard Index (" + index + ") needs be between 0 and " + (getMeasures().size() - 1) + "!");
		if (getMeasures().get(index) instanceof HibernateProxy)
			return MaturityMeasure.class.cast(((HibernateProxy) getMeasures().get(index)).getHibernateLazyInitializer().getImplementation());
		else {
			return (MaturityMeasure) getMeasures().get(index);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.model.standard.AnalysisStandard#getExendedMeasures()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MaturityMeasure> getExendedMeasures() {
		return (List<MaturityMeasure>) super.getExendedMeasures();
	}

	/**
	 * getLevel1Measures: <br>
	 * Description
	 * 
	 * @return
	 */
	public List<Measure> getNotComputableMeasures() {
		return super.getMeasures().stream().filter(m -> !m.getMeasureDescription().isComputable()).collect(Collectors.toList());
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
	public MaturityStandard clone() throws CloneNotSupportedException {
		return (MaturityStandard) super.clone();
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @throws TrickException
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.model.standard.AnalysisStandard#duplicate()
	 */
	@Override
	public MaturityStandard duplicate() throws CloneNotSupportedException, TrickException {
		return (MaturityStandard) super.duplicate();
	}

	/**
	 * Adds a measure to the maturity standard.
	 *
	 * @param measure the measure to be added
	 */
	@Override
	public void add(Measure measure) {
		addMeasure((MaturityMeasure) measure);
	}

	/**
	 * Adds a new measure description to the maturity standard.
	 *
	 * @param measureDescription the measure description to be added
	 */
	@Override
	public void add(MeasureDescription measureDescription) {
		add(new MaturityMeasure(measureDescription));
	}

}