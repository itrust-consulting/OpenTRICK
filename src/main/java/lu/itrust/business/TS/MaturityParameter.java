package lu.itrust.business.TS;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.exception.TrickException;

/**
 * MaturityParameter: <br>
 * This class represents a Maturity Parameter and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@PrimaryKeyJoinColumn(name="idMaturityParameter")
public class MaturityParameter extends Parameter implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	@Transient
	private static final long serialVersionUID = 1L;

	/** The Maturity Category of Parameter */
	@Column(name="dtCategory", nullable=false)
	@Access(AccessType.FIELD)
	private String category = "";

	@Column(name="dtSML", nullable=false)
	@Access(AccessType.FIELD)
	private int SMLLevel = 0;
	
	@Column(name="dtSML0", nullable=false)
	@Access(AccessType.FIELD)
	private double SMLLevel0 = 0;

	@Column(name="dtSML1", nullable=false)
	@Access(AccessType.FIELD)
	private double SMLLevel1 = 0;

	@Column(name="dtSML2", nullable=false)
	@Access(AccessType.FIELD)
	private double SMLLevel2 = 0;

	@Column(name="dtSML3", nullable=false)
	@Access(AccessType.FIELD)
	private double SMLLevel3 = 0;

	@Column(name="dtSML4", nullable=false)
	@Access(AccessType.FIELD)
	private double SMLLevel4 = 0;

	@Column(name="dtSML5", nullable=false)
	@Access(AccessType.FIELD)
	private double SMLLevel5 = 0;

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getCategory: <br>
	 * Returns the "category" field value
	 * 
	 * @return The Maturity Category Name
	 */
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
			throw new TrickException("error.measure_parameter.category","Categories need to be one of the following: Policies|Procedure|Implementation|Test|Integration!");
		this.category = category;
	}

	/**
	 * getSMLLevel: <br>
	 * Returns the "SMLLevel" field value
	 * 
	 * @return The Level of SML
	 */
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
			throw new TrickException("error.measure_parameter.sml_level","SML level needs to be between 0 and 5");
		this.SMLLevel0 = SMLLevel;
	}

	/**
	 * getSMLLevel: <br>
	 * Returns the "SMLLevel" field value
	 * 
	 * @return The Level of SML
	 */
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
			throw new TrickException("error.measure_parameter.sml_level","SML level needs to be between 0 and 5");
		this.SMLLevel1 = SMLLevel;
	}

	/**
	 * getSMLLevel: <br>
	 * Returns the "SMLLevel" field value
	 * 
	 * @return The Level of SML
	 */
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
			throw new TrickException("error.measure_parameter.sml_level","SML level needs to be between 0 and 5");
		this.SMLLevel2 = SMLLevel;
	}

	/**
	 * getSMLLevel: <br>
	 * Returns the "SMLLevel" field value
	 * 
	 * @return The Level of SML
	 */
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
			throw new TrickException("error.measure_parameter.sml_level","SML level needs to be between 0 and 5");
		this.SMLLevel3 = SMLLevel;
	}

	/**
	 * getSMLLevel: <br>
	 * Returns the "SMLLevel" field value
	 * 
	 * @return The Level of SML
	 */
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
			throw new TrickException("error.measure_parameter.sml_level","SML level needs to be between 0 and 5");
		this.SMLLevel4 = SMLLevel;
	}

	/**
	 * getSMLLevel: <br>
	 * Returns the "SMLLevel" field value
	 * 
	 * @return The Level of SML
	 */
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
			throw new TrickException("error.measure_parameter.sml_level","SML level needs to be between 0 and 5");
		this.SMLLevel5 = SMLLevel;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.Parameter#clone()
	 */
	@Override
	public MaturityParameter clone() throws CloneNotSupportedException {
		return (MaturityParameter) super.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.Parameter#duplicate()
	 */
	@Override
	public MaturityParameter duplicate() throws CloneNotSupportedException {
		return (MaturityParameter) super.duplicate();
	}

	/** getSMLLevel: <br>
	 * Returns the sMLLevel field value.
	 * 
	 * @return The value of the sMLLevel field
	 */
	public int getSMLLevel() {
		return SMLLevel;
	}

	/** setSMLLevel: <br>
	 * Sets the Field "sMLLevel" with a value.
	 * 
	 * @param sMLLevel 
	 * 			The Value to set the sMLLevel field
	 */
	public void setSMLLevel(int sMLLevel) {
		SMLLevel = sMLLevel;
	}

}