package lu.itrust.business.ts.model.standard.measure;

import static lu.itrust.business.ts.model.general.helper.Utils.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.general.Phase;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureProperties;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.expressions.StringExpressionParser;

@Cacheable
@MappedSuperclass
public abstract class AbstractNormalMeasure extends Measure implements Cloneable {

	/** The "To Check" comment */
	private String toCheck = "";
	/** The List of Measure Properties */
	protected MeasureProperties measurePropertyList = null;

	protected AbstractNormalMeasure() {
	}

	protected AbstractNormalMeasure(MeasureDescription measureDescription) {
		super(measureDescription);
		setMeasurePropertyList(new MeasureProperties());
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
	 * @param measurePropertyList The measureProperties Object to set the List of
	 *                            Properties
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
	 * @param toCheck The value to set the "To Check" Comment
	 */
	public void setToCheck(String toCheck) {
		this.toCheck = toCheck == null ? "" : toCheck;
	}

	/**
	 * getImplementationRate: <br>
	 * Returns the Implementation Rate value
	 * 
	 * @return Implementation Rate value
	 * @see lu.itrust.business.ts.model.standard.measure.Measure#getImplementationRate()
	 */
	@Column(name = "dtImplementationRate", nullable = false)
	@Access(AccessType.FIELD)
	@Override
	public String getImplementationRate() {
		return (String) super.getImplementationRate();
	}

	/**
	 * getImplementationRateValue: <br>
	 * Returns the Implementation Rate value using the getImplementationRate method.
	 * 
	 * @return Implementation Rate Value
	 * @see lu.itrust.business.ts.model.standard.measure.Measure#getImplementationRateValue()
	 * @see lu.itrust.business.ts.model.standard.measure.impl.NormalMeasure#getImplementationRate()
	 */
	@Transient
	@Override
	public double getImplementationRateValue(ValueFactory factory) {
		try {
			final double value = (new StringExpressionParser(this.getImplementationRate(),
					StringExpressionParser.IMPLEMENTATION)).evaluate(factory);
			return (value < 0 ? 0 : (value > 100 ? 100 : value));
		} catch (Exception ex) {
			return 0.0;
		}
	}

	@Override
	public double getImplementationRateValue(Map<String, Double> factory) {
		try {
			final double value = (new StringExpressionParser(this.getImplementationRate(),
					StringExpressionParser.IMPLEMENTATION)).evaluate(factory);
			return (value < 0 ? 0 : (value > 100 ? 100 : value));
		} catch (Exception ex) {
			return 0.0;
		}
	}

	@Transient
	@Override
	public List<String> getVariablesInvolvedInImplementationRateValue() {
		try {
			return (new StringExpressionParser(this.getImplementationRate(), StringExpressionParser.IMPLEMENTATION))
					.getInvolvedVariables().stream().collect(Collectors.toList());
		} catch (Exception ex) {
			return Collections.emptyList();
		}
	}

	@Transient
	public String getSoaReference() {
		return measurePropertyList == null ? "" : measurePropertyList.getSoaReference();
	}

	@Transient
	public String getSoaComment() {
		String value = measurePropertyList == null ? "" : measurePropertyList.getSoaComment();
		return !hasText(value) && Constant.MEASURE_STATUS_NOT_APPLICABLE.equalsIgnoreCase(getStatus()) ? getComment()
				: value;
	}

	@Transient
	public String getSoaRisk() {
		return measurePropertyList == null ? "" : measurePropertyList.getSoaRisk();
	}

	/**
	 * setImplementationRate: <br>
	 * Sets the Implementation Rate with a Value
	 * 
	 * @param implementationRate The Implementation Rate Value as object
	 * @throws TrickException
	 * @see lu.itrust.business.ts.model.standard.measure.Measure#setImplementationRate(java.lang.Object)
	 */
	@Override
	public void setImplementationRate(Object implementationRate) throws TrickException {
		if (!(implementationRate instanceof String || implementationRate instanceof Number))
			throw new TrickException("error.norm_measure.implementation_rate.invalid",
					"ImplementationRate needs to be of Type String!");
		setImplementationRate(implementationRate.toString());
	}

	/**
	 * setImplementationRate: <br>
	 * Sets the Implementation Rate with a Value
	 * 
	 * @param implementationRate The Implementation Rate Value as Double
	 * @throws TrickException
	 * @see lu.itrust.business.ts.model.standard.measure.Measure#setImplementationRate(java.lang.Object)
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