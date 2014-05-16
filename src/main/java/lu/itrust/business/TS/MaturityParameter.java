package lu.itrust.business.TS;

import lu.itrust.business.TS.tsconstant.Constant;

/**
 * MaturityParameter: <br>
 * This class represents a Maturity Parameter and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
public class MaturityParameter extends Parameter implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** The Maturity Category of Parameter */
	private String category = "";

	private int SMLLevel = 0;
	
	private double SMLLevel0 = 0;

	private double SMLLevel1 = 0;

	private double SMLLevel2 = 0;

	private double SMLLevel3 = 0;

	private double SMLLevel4 = 0;

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
	 */
	public void setCategory(String category) {
		if ((category == null) || (!category.matches(Constant.REGEXP_VALID_MATURITY_CATEGORY))) {
			throw new IllegalArgumentException("Maturtiy Parameter Categories need to be one of the following: " + "Policies|Procedure|Implementation|Test|Integration!");
		}
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
	 */
	public void setSMLLevel0(double SMLLevel) {
		if ((SMLLevel < 0) || (SMLLevel > 5)) {
			throw new IllegalArgumentException("Maturtiy Parameter SML Level needs to be: 0 >= SML <= 5 !");
		}
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
	 */
	public void setSMLLevel1(double SMLLevel) {
		if ((SMLLevel < 0) || (SMLLevel > 5)) {
			throw new IllegalArgumentException("Maturtiy Parameter SML Level needs to be: 0 >= SML <= 5 !");
		}
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
	 */
	public void setSMLLevel2(double SMLLevel) {
		if ((SMLLevel < 0) || (SMLLevel > 5)) {
			throw new IllegalArgumentException("Maturtiy Parameter SML Level needs to be: 0 >= SML <= 5 !");
		}
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
	 */
	public void setSMLLevel3(double SMLLevel) {
		if ((SMLLevel < 0) || (SMLLevel > 5)) {
			throw new IllegalArgumentException("Maturtiy Parameter SML Level needs to be: 0 >= SML <= 5 !");
		}
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
	 */
	public void setSMLLevel4(double SMLLevel) {
		if ((SMLLevel < 0) || (SMLLevel > 5)) {
			throw new IllegalArgumentException("Maturtiy Parameter SML Level needs to be: 0 >= SML <= 5 !");
		}
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
	 */
	public void setSMLLevel5(double SMLLevel) {
		if ((SMLLevel < 0) || (SMLLevel > 5)) {
			throw new IllegalArgumentException("Maturtiy Parameter SML Level needs to be: 0 >= SML <= 5 !");
		}
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