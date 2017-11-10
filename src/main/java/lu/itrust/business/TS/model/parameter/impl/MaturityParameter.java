package lu.itrust.business.TS.model.parameter.impl;

import javax.persistence.AttributeOverride;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.parameter.IMaturityParameter;

/**
 * MaturityParameter: <br>
 * This class represents a Maturity SimpleParameter and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name="id", column=@Column(name="idMaturityParameter"))
public class MaturityParameter extends Parameter implements IMaturityParameter {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The Maturity Category of SimpleParameter */
	@Column(name = "dtCategory", nullable = false)
	private String category = "";

	@Column(name = "dtSML", nullable = false)
	private int SMLLevel = 0;

	@Column(name = "dtSML0", nullable = false)
	private double SMLLevel0 = 0;

	@Column(name = "dtSML1", nullable = false)
	private double SMLLevel1 = 0;

	@Column(name = "dtSML2", nullable = false)
	private double SMLLevel2 = 0;

	@Column(name = "dtSML3", nullable = false)
	private double SMLLevel3 = 0;

	@Column(name = "dtSML4", nullable = false)
	private double SMLLevel4 = 0;

	@Column(name = "dtSML5", nullable = false)
	private double SMLLevel5 = 0;

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.parameter.impl.IMaturityParameter#getCategory
	 * ()
	 */
	@Override
	public String getCategory() {
		return category;
	}

	/**
	 * setCategory: <br>
	 * Sets the "category" field with a value
	 * 
	 * @param category
	 *            The value to set the Maturity Category Name
	 * @throws TrickException
	 */
	public void setCategory(String category) throws TrickException {
		if (category == null || !category.matches(Constant.REGEXP_VALID_MATURITY_CATEGORY))
			throw new TrickException("error.measure_parameter.category", "Categories need to be one of the following: Policies|Procedure|Implementation|Test|Integration!");
		this.category = category;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.parameter.impl.IMaturityParameter#
	 * getSMLLevel0()
	 */
	@Override
	public double getSMLLevel0() {
		return SMLLevel0;
	}

	/**
	 * setSMLLevel: <br>
	 * Sets the "SMLLevel" field with a value
	 * 
	 * @param SMLLevel
	 *            The value to set the SML
	 * @throws TrickException
	 */
	public void setSMLLevel0(double SMLLevel) throws TrickException {
		if ((SMLLevel < 0) || (SMLLevel > 5))
			throw new TrickException("error.measure_parameter.sml_level", "SML level needs to be between 0 and 5");
		this.SMLLevel0 = SMLLevel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.parameter.impl.IMaturityParameter#
	 * getSMLLevel1()
	 */
	@Override
	public double getSMLLevel1() {
		return SMLLevel1;
	}

	/**
	 * setSMLLevel: <br>
	 * Sets the "SMLLevel" field with a value
	 * 
	 * @param SMLLevel
	 *            The value to set the SML
	 * @throws TrickException
	 */
	public void setSMLLevel1(double SMLLevel) throws TrickException {
		if ((SMLLevel < 0) || (SMLLevel > 5))
			throw new TrickException("error.measure_parameter.sml_level", "SML level needs to be between 0 and 5");
		this.SMLLevel1 = SMLLevel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.parameter.impl.IMaturityParameter#
	 * getSMLLevel2()
	 */
	@Override
	public double getSMLLevel2() {
		return SMLLevel2;
	}

	/**
	 * setSMLLevel: <br>
	 * Sets the "SMLLevel" field with a value
	 * 
	 * @param SMLLevel
	 *            The value to set the SML
	 * @throws TrickException
	 */
	public void setSMLLevel2(double SMLLevel) throws TrickException {
		if ((SMLLevel < 0) || (SMLLevel > 5))
			throw new TrickException("error.measure_parameter.sml_level", "SML level needs to be between 0 and 5");
		this.SMLLevel2 = SMLLevel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.parameter.impl.IMaturityParameter#
	 * getSMLLevel3()
	 */
	@Override
	public double getSMLLevel3() {
		return SMLLevel3;
	}

	/**
	 * setSMLLevel: <br>
	 * Sets the "SMLLevel" field with a value
	 * 
	 * @param SMLLevel
	 *            The value to set the SML
	 * @throws TrickException
	 */
	public void setSMLLevel3(double SMLLevel) throws TrickException {
		if ((SMLLevel < 0) || (SMLLevel > 5))
			throw new TrickException("error.measure_parameter.sml_level", "SML level needs to be between 0 and 5");
		this.SMLLevel3 = SMLLevel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.parameter.impl.IMaturityParameter#
	 * getSMLLevel4()
	 */
	@Override
	public double getSMLLevel4() {
		return SMLLevel4;
	}

	/**
	 * setSMLLevel: <br>
	 * Sets the "SMLLevel" field with a value
	 * 
	 * @param SMLLevel
	 *            The value to set the SML
	 * @throws TrickException
	 */
	public void setSMLLevel4(double SMLLevel) throws TrickException {
		if ((SMLLevel < 0) || (SMLLevel > 5))
			throw new TrickException("error.measure_parameter.sml_level", "SML level needs to be between 0 and 5");
		this.SMLLevel4 = SMLLevel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.parameter.impl.IMaturityParameter#
	 * getSMLLevel5()
	 */
	@Override
	public double getSMLLevel5() {
		return SMLLevel5;
	}

	/**
	 * setSMLLevel: <br>
	 * Sets the "SMLLevel" field with a value
	 * 
	 * @param SMLLevel
	 *            The value to set the SML
	 * @throws TrickException
	 */
	public void setSMLLevel5(double SMLLevel) throws TrickException {
		if ((SMLLevel < 0) || (SMLLevel > 5))
			throw new TrickException("error.measure_parameter.sml_level", "SML level needs to be between 0 and 5");
		this.SMLLevel5 = SMLLevel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.parameter.impl.IMaturityParameter#getSMLLevel
	 * ()
	 */
	@Override
	public int getSMLLevel() {
		return SMLLevel;
	}

	/**
	 * setSMLLevel: <br>
	 * Sets the Field "sMLLevel" with a value.
	 * 
	 * @param sMLLevel
	 *            The Value to set the sMLLevel field
	 */
	public void setSMLLevel(int sMLLevel) {
		SMLLevel = sMLLevel;
	}

	@Override
	public String getTypeName() {
		return Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_LEVEL_PER_SML_NAME;
	}

}