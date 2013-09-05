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
public class MaturityParameter extends Parameter {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** The Maturity Category of Parameter */
	private String category = "";

	/** The SML Level of the Parameter */
	private int SMLLevel = 0;

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
			throw new IllegalArgumentException(
					"Maturtiy Parameter Categories need to be one of the following: "
						+ "Policies|Procedure|Implementation|Test|Integration!");
		}
		this.category = category;
	}

	/**
	 * getSMLLevel: <br>
	 * Returns the "SMLLevel" field value
	 * 
	 * @return The Level of SML
	 */
	public int getSMLLevel() {
		return SMLLevel;
	}

	/**
	 * setSMLLevel: <br>
	 * Sets the "SMLLevel" field with a value
	 * 
	 * @param SMLLevel
	 *            The value to set the SML
	 */
	public void setSMLLevel(int SMLLevel) {
		if ((SMLLevel < 0) || (SMLLevel > 5)) {
			throw new IllegalArgumentException(
					"Maturtiy Parameter SML Level needs to be: 0 >= SML <= 5 !");
		}
		this.SMLLevel = SMLLevel;
	}
}