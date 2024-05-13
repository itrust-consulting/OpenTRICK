package lu.itrust.business.ts.model.parameter;

import lu.itrust.business.ts.model.parameter.helper.Bounds;

/**
 * The {@code IBoundedParameter} interface represents a bounded parameter that extends the {@code ILevelParameter} interface.
 * It provides methods to retrieve the bounds and label of the parameter.
 */
public interface IBoundedParameter extends ILevelParameter {

	/**
	 * Returns the bounds of the parameter.
	 *
	 * @return The Bounds values (from and to values)
	 */
	Bounds getBounds();

	/**
	 * Returns the label of the parameter.
	 *
	 * @return The label of the parameter.
	 */
	String getLabel();

}