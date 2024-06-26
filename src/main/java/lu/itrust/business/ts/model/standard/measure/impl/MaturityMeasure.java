package lu.itrust.business.ts.model.standard.measure.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.general.Phase;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.parameter.impl.SimpleParameter;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;

/**
 * MaturityMeasure: <br>
 * This class represents the MaturityMeasure and its data
 */
@Entity
@PrimaryKeyJoinColumn(name = "idMaturityMeasure")
public class MaturityMeasure extends Measure implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The Reached Security Maturity Level */
	private int reachedLevel = 0;

	/** The Cost to get to the Security Maturity Level 1 */
	private double SML1Cost = 0;

	/** The Cost to get to the Security Maturity Level 2 */
	private double SML2Cost = 0;

	/** The Cost to get to the Security Maturity Level 3 */
	private double SML3Cost = 0;

	/** The Cost to get to the Security Maturity Level 4 */
	private double SML4Cost = 0;

	/** The Cost to get to the Security Maturity Level 5 */
	private double SML5Cost = 0;
	
	
	public MaturityMeasure() {
	}

	public MaturityMeasure(MeasureDescription measureDescription) {
		super(measureDescription);
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getReachedLevel: <br>
	 * Returns the "reachedLevel" field value
	 * 
	 * @return The reached SML
	 */
	@Column(name = "dtReachedLevel", nullable = false)
	public int getReachedLevel() {
		return reachedLevel;
	}

	/**
	 * setReachedLevel: <br>
	 * Sets the "reachedLevel" field with a value
	 * 
	 * @param reachedLevel
	 *            The value to set the Reached Value
	 * @throws TrickException
	 */
	public void setReachedLevel(int reachedLevel) throws TrickException {
		if (reachedLevel < 0 || reachedLevel > 5)
			throw new TrickException("error.measure.reached_level", "Reached level should be between 0 and 5");
		this.reachedLevel = reachedLevel;
	}

	/**
	 * getSML1Cost: <br>
	 * Returns the "SML1Cost" field value
	 * 
	 * @return The Cost to reach the SML 1
	 */
	@Column(name = "dtSML1Cost", nullable = false)
	public double getSML1Cost() {
		return SML1Cost;
	}

	/**
	 * setSML1Cost: <br>
	 * Sets the "SML1Cost" field with a value
	 * 
	 * @param SML1Cost
	 *            The value to set the cost to reach SML 1
	 * @throws TrickException
	 */
	public void setSML1Cost(double SML1Cost) throws TrickException {
		if (SML1Cost < 0)
			throw new TrickException("error.measure.sml_cost", "SML1 cost cannot be negative", "1");
		this.SML1Cost = SML1Cost;
	}

	/**
	 * getSML2Cost: <br>
	 * Returns the "SML2Cost" field value
	 * 
	 * @return The Cost to reach the SML 2
	 */
	@Column(name = "dtSML2Cost", nullable = false)
	public double getSML2Cost() {
		return SML2Cost;
	}

	/**
	 * setSML2Cost: <br>
	 * Sets the "SML2Cost" field with a value
	 * 
	 * @param SML2Cost
	 *            The value to set the cost to reach SML 2
	 * @throws TrickException
	 */
	public void setSML2Cost(double SML2Cost) throws TrickException {
		if (SML2Cost < 0)
			throw new TrickException("error.measure.sml_cost", "SML2 cost cannot be negative", "2");
		this.SML2Cost = SML2Cost;
	}

	/**
	 * getSML3Cost: <br>
	 * Returns the "SML3Cost" field value
	 * 
	 * @return The Cost to reach the SML 3
	 */
	@Column(name = "dtSML3Cost", nullable = false)
	public double getSML3Cost() {
		return SML3Cost;
	}

	/**
	 * setSML3Cost: <br>
	 * Sets the "SML3Cost" field with a value
	 * 
	 * @param SML3Cost
	 *            The value to set the cost to reach SML 3
	 * @throws TrickException
	 */
	public void setSML3Cost(double SML3Cost) throws TrickException {
		if (SML3Cost < 0)
			throw new TrickException("error.measure.sml_cost", "SML3 cost cannot be negative", "3");
		this.SML3Cost = SML3Cost;
	}

	/**
	 * getSML4Cost: <br>
	 * Returns the "SML4Cost" field value
	 * 
	 * @return The Cost to reach the SML 4
	 */
	@Column(name = "dtSML4Cost", nullable = false)
	public double getSML4Cost() {
		return SML4Cost;
	}

	/**
	 * setSML4Cost: <br>
	 * Sets the "SML4Cost" field with a value
	 * 
	 * @param SML4Cost
	 *            The value to set the cost to reach SML 4
	 * @throws TrickException
	 */
	public void setSML4Cost(double SML4Cost) throws TrickException {
		if (SML4Cost < 0)
			throw new TrickException("error.measure.sml_cost", "SML4 cost cannot be negative", "4");
		this.SML4Cost = SML4Cost;
	}

	/**
	 * getSML5Cost: <br>
	 * Returns the "SML5Cost" field value
	 * 
	 * @return The Cost to reach the SML 5
	 */
	@Column(name = "dtSML5Cost", nullable = false)
	public double getSML5Cost() {
		return SML5Cost;
	}

	/**
	 * setSML5Cost: <br>
	 * Sets the "SML5Cost" field with a value
	 * 
	 * @param SML5Cost
	 *            The value to set the cost to reach SML 5
	 * @throws TrickException
	 */
	public void setSML5Cost(double SML5Cost) throws TrickException {
		if (SML5Cost < 0)
			throw new TrickException("error.measure.sml_cost", "SML5 cost cannot be negative", "5");
		this.SML5Cost = SML5Cost;
	}

	/**
	 * getImplementationRate: <br>
	 * Returns the implementationRate field value (SimpleParameter Object).
	 * 
	 * @return The Object of SimpleParameter representing the implementation
	 *         rate
	 * @see lu.itrust.business.ts.model.standard.measure.Measure#getImplementationRate()
	 */
	@Override
	@ManyToOne
	@Access(AccessType.FIELD)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiImplementationRateParameter", nullable = false)
	public SimpleParameter getImplementationRate() {
		return (SimpleParameter) super.getImplementationRate();
	}

	@Override
	@Transient
	public List<String> getVariablesInvolvedInImplementationRateValue() {
		return new ArrayList<>();
	}

	/**
	 * setImplementationRate: <br>
	 * Sets the Field "implementationRate" with a SimpleParameter object.
	 * 
	 * @param implementationRate
	 *            The Value to set the implementationRate field PArameter Object
	 * @throws TrickException
	 * @see lu.itrust.business.ts.model.standard.measure.Measure#setImplementationRate(Object)
	 */
	@Override
	public void setImplementationRate(Object implementationRate) throws TrickException {
		if (!(implementationRate instanceof SimpleParameter))
			throw new TrickException("error.measure.maturity.implementation_rate", "Invalid implementation rate value");
		super.setImplementationRate((SimpleParameter) implementationRate);
	}

	/**
	 * getImplementationRateValue: <br>
	 * returns the Real Implementation Rate Value
	 * 
	 * @see lu.itrust.business.ts.model.standard.measure.Measure#getImplementationRateValue()
	 */
	@Override
	@Transient
	public double getImplementationRateValue(ValueFactory factory) {
		return getImplementationRate() != null ? getImplementationRate().getValue().doubleValue() : 0d;
	}

	/**
	 * getImplementationRateValue: <br>
	 * returns the Real Implementation Rate Value
	 * 
	 * @see lu.itrust.business.ts.model.standard.measure.Measure#getImplementationRateValue()
	 */
	@Override
	@Transient
	public double getImplementationRateValue(Map<String, Double> factory) {
		return getImplementationRateValue((ValueFactory) null);
	}

	/**
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.model.standard.measure.Measure#clone()
	 */
	@Override
	public MaturityMeasure clone() throws CloneNotSupportedException {
		return (MaturityMeasure) super.clone();
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.model.standard.measure.Measure#duplicate(lu.itrust.business.ts.model.standard.AnalysisStandard)
	 */
	@Override
	public MaturityMeasure duplicate(AnalysisStandard analysisStandard, Phase phase) throws CloneNotSupportedException {
		return (MaturityMeasure) super.duplicate(analysisStandard, phase);
	}

}