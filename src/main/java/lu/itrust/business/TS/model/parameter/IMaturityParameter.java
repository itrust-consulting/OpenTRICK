package lu.itrust.business.TS.model.parameter;

import lu.itrust.business.TS.constants.Constant;

public interface IMaturityParameter  extends IParameter{

	/**
	 * getCategory: <br>
	 * Returns the "category" field value
	 * 
	 * @return The Maturity Category Name
	 */
	String getCategory();

	/**
	 * getSMLLevel: <br>
	 * Returns the "SMLLevel" field value
	 * 
	 * @return The Level of SML
	 */
	double getSMLLevel0();

	/**
	 * getSMLLevel: <br>
	 * Returns the "SMLLevel" field value
	 * 
	 * @return The Level of SML
	 */
	double getSMLLevel1();

	/**
	 * getSMLLevel: <br>
	 * Returns the "SMLLevel" field value
	 * 
	 * @return The Level of SML
	 */
	double getSMLLevel2();

	/**
	 * getSMLLevel: <br>
	 * Returns the "SMLLevel" field value
	 * 
	 * @return The Level of SML
	 */
	double getSMLLevel3();

	/**
	 * getSMLLevel: <br>
	 * Returns the "SMLLevel" field value
	 * 
	 * @return The Level of SML
	 */
	double getSMLLevel4();

	/**
	 * getSMLLevel: <br>
	 * Returns the "SMLLevel" field value
	 * 
	 * @return The Level of SML
	 */
	double getSMLLevel5();

	/**
	 * getSMLLevel: <br>
	 * Returns the sMLLevel field value.
	 * 
	 * @return The value of the sMLLevel field
	 */
	int getSMLLevel();

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.parameter.IParameter#getGroup()
	 */
	@Override
	default String getGroup() {
		return Constant.PARAMETER_CATEGORY_MATURITY;
	}

}