package lu.itrust.business.ts.model.parameter;

import lu.itrust.business.ts.model.parameter.helper.Bounds;

public interface IBoundedParameter extends ILevelParameter {

	/**
	 * getBounds: <br>
	 * Returns the "bounds" field value
	 * 
	 * @return The Bounds values (from and to values)
	 */
	Bounds getBounds();
	
	String getLabel();

}