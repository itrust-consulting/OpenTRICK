package lu.itrust.business.TS.model.standard.measure;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.impl.MeasureProperties;
import lu.itrust.business.expressions.StringExpressionParser;

@Cacheable
@MappedSuperclass
public abstract class AbstractNormalMeasure extends Measure implements Cloneable {

	/** The "To Check" comment */
	private String toCheck = "";
	/** The List of Measure Properties */
	protected MeasureProperties measurePropertyList = null;

	public AbstractNormalMeasure() {
	}

	/**
	 * getMeasurePropertyList: <br>
	 * Returns the MeasureProperties object which has all property values
	 * 
	 * @return The Measure Properties List object
	 */
	@ManyToOne
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiMeasureProperties", nullable = false, unique = true)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	public MeasureProperties getMeasurePropertyList() {
		return measurePropertyList;
	}

	/**
	 * setMeasurePropertyList: <br>
	 * Sets the "measurePropertyList" field with a measureProperties object
	 * 
	 * @param measurePropertyList
	 *            The measureProperties Object to set the List of Properties
	 * @throws TrickException
	 */
	public void setMeasurePropertyList(MeasureProperties measurePropertyList) throws TrickException {
		if (measurePropertyList == null)
			throw new TrickException("error.norm_measure.measure_property.empty", "Measure properties cannot be empty");
		this.measurePropertyList = measurePropertyList;
	}

	/**
	 * getToCheck: <br>
	 * Returns the "toCheck" field value
	 * 
	 * @return The To Check Value
	 */
	@Column(name = "dtToCheck", nullable = false, length = 1024)
	public String getToCheck() {
		return this.toCheck;
	}

	/**
	 * setToCheck: <br>
	 * Sets the "toCheck" field with a value
	 * 
	 * @param toCheck
	 *            The value to set the "To Check" Comment
	 */
	public void setToCheck(String toCheck) {
		this.toCheck = toCheck;
	}

	/**
	 * getImplementationRate: <br>
	 * Returns the Implementation Rate value
	 * 
	 * @return Implementation Rate value
	 * @see lu.itrust.business.TS.model.standard.measure.Measure#getImplementationRate()
	 */
	@Override
	@Column(name = "dtImplementationRate", nullable = false)
	@Access(AccessType.FIELD)
	public String getImplementationRate() {
		return (String) super.getImplementationRate();
	}

	/**
	 * getImplementationRateValue: <br>
	 * Returns the Implementation Rate value using the getImplementationRate method.
	 * 
	 * @return Implementation Rate Value
	 * @see lu.itrust.business.TS.model.standard.measure.Measure#getImplementationRateValue()
	 * @see lu.itrust.business.TS.model.standard.measure.impl.NormalMeasure#getImplementationRate()
	 */
	@Override
	@Transient
	public double getImplementationRateValue(ValueFactory factory) {
		try {
			return (new StringExpressionParser(this.getImplementationRate())).evaluate(factory);
		} catch (Exception ex) {
			return 0.0;
		}
	}

	@Override
	public double getImplementationRateValue(Map<String, Double> factory) {
		try {
			return (new StringExpressionParser(this.getImplementationRate())).evaluate(factory);
		} catch (Exception ex) {
			return 0.0;
		}
	}

	@Override
	@Transient
	public List<String> getVariablesInvolvedInImplementationRateValue() {
		try {
			return (new StringExpressionParser(this.getImplementationRate())).getInvolvedVariables().stream().collect(Collectors.toList());
		} catch (Exception ex) {
			return Collections.emptyList();
		}
	}

	/**
	 * setImplementationRate: <br>
	 * Sets the Implementation Rate with a Value
	 * 
	 * @param implementationRate
	 *            The Implementation Rate Value as object
	 * @throws TrickException
	 * @see lu.itrust.business.TS.model.standard.measure.Measure#setImplementationRate(java.lang.Object)
	 */
	@Override
	public void setImplementationRate(Object implementationRate) throws TrickException {
		if (!(implementationRate instanceof String || implementationRate instanceof Double))
			throw new TrickException("error.norm_measure.implementation_rate.invalid", "ImplementationRate needs to be of Type String!");
		setImplementationRate(implementationRate.toString());
	}

	/**
	 * setImplementationRate: <br>
	 * Sets the Implementation Rate with a Value
	 * 
	 * @param implementationRate
	 *            The Implementation Rate Value as Double
	 * @throws TrickException
	 * @see lu.itrust.business.TS.model.standard.measure.Measure#setImplementationRate(java.lang.Object)
	 */
	public void setImplementationRate(String implementationRate) throws TrickException {
		super.setImplementationRate(implementationRate);
	}

	@Override
	public AbstractNormalMeasure clone() throws CloneNotSupportedException {
		AbstractNormalMeasure measure = (AbstractNormalMeasure) super.clone();
		measure.measurePropertyList = (MeasureProperties) measure.measurePropertyList.clone();
		return measure;
	}

	@Override
	public AbstractNormalMeasure duplicate(AnalysisStandard astandard, Phase phase) throws CloneNotSupportedException {
		AbstractNormalMeasure measure = (AbstractNormalMeasure) super.duplicate(astandard, phase);
		measure.measurePropertyList = (MeasureProperties) measure.measurePropertyList.duplicate();
		return measure;
	}

}