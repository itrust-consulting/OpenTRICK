package lu.itrust.business.ts.model.parameter.value;

import lu.itrust.business.ts.model.parameter.ILevelParameter;

/**
 * This interface represents a parameter value for a level parameter.
 * It extends the {@link IValue} interface.
 */
public interface IParameterValue extends IValue {
	
	/**
	 * Returns the name of the parameter.
	 * This is equivalent to calling {@link #getParameter()}.getTypeName().
	 * 
	 * @return the name of the parameter
	 * {@link #ILevelParameter}.getTypeName()
	 */

	default String getName() {
		return getParameter().getTypeName();
	}

	/**
	 * Returns the level parameter associated with this parameter value.
	 * 
	 * @return the level parameter
	 */
	ILevelParameter getParameter();

}
