package lu.itrust.business.TS.model.parameter.value;

import lu.itrust.business.TS.model.parameter.ILevelParameter;

public interface IParameterValue extends IValue {
	
	/**
	 * 
	 * @return {@link #ILevelParameter}.getTypeName()
	 */
	default String getName() {
		return getParameter().getTypeName();
	}

	ILevelParameter getParameter();

}
