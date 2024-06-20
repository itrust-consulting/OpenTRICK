package lu.itrust.business.ts.model.parameter;

import lu.itrust.business.ts.constants.Constant;

public interface IMaturityParameter extends IParameter {

	/**
	 * getCategory:
	 * Returns the "category" field value.
	 *
	 * @return The Maturity Category Name.
	 */
	String getCategory();

	/**
	 * getSMLLevel:
	 * Returns the "SMLLevel" field value.
	 *
	 * @return The Level of SML.
	 */
	double getSMLLevel0();

	/**
	 * getSMLLevel:
	 * Returns the "SMLLevel" field value.
	 *
	 * @return The Level of SML.
	 */
	double getSMLLevel1();

	/**
	 * getSMLLevel:
	 * Returns the "SMLLevel" field value.
	 *
	 * @return The Level of SML.
	 */
	double getSMLLevel2();

	/**
	 * getSMLLevel:
	 * Returns the "SMLLevel" field value.
	 *
	 * @return The Level of SML.
	 */
	double getSMLLevel3();

	/**
	 * getSMLLevel:
	 * Returns the "SMLLevel" field value.
	 *
	 * @return The Level of SML.
	 */
	double getSMLLevel4();

	/**
	 * getSMLLevel:
	 * Returns the "SMLLevel" field value.
	 *
	 * @return The Level of SML.
	 */
	double getSMLLevel5();

	/**
	 * getSMLLevel:
	 * Returns the sMLLevel field value.
	 *
	 * @return The value of the sMLLevel field.
	 */
	int getSMLLevel();

	/**
	 * getGroup:
	 * Returns the group of the parameter.
	 *
	 * @return The group of the parameter.
	 * @see lu.itrust.business.ts.model.parameter.IParameter#getGroup()
	 */
	@Override
	default String getGroup() {
		return Constant.PARAMETER_CATEGORY_MATURITY;
	}
}