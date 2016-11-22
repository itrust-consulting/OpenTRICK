package lu.itrust.business.TS.model.parameter;

import lu.itrust.business.TS.model.parameter.helper.Bounds;

public interface IBoundedParameter extends ILevelParameter {

	/**
	 * getBounds: <br>
	 * Returns the "bounds" field value
	 * 
	 * @return The Bounds values (from and to values)
	 */
	Bounds getBounds();

}