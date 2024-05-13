package lu.itrust.business.ts.model.parameter;

import lu.itrust.business.ts.model.parameter.type.IParameterType;

/**
 * ITypedParameter interface represents a typed parameter.
 * It extends the IParameter interface.
 */
public interface ITypedParameter extends IParameter {
	
	/**
	 * getType: <br>
	 * Returns the "type" field value.
	 * 
	 * @return The SimpleParameter Type Name
	 */
	IParameterType getType();

}
