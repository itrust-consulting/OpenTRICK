package lu.itrust.business.ts.model.parameter;

import lu.itrust.business.ts.model.parameter.type.IParameterType;

public interface ITypedParameter extends IParameter {
	
	/**
	 * getType: <br>
	 * Returns the "type" field value
	 * 
	 * @return The SimpleParameter Type Name
	 */
	IParameterType getType();

}
