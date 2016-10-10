/**
 * 
 */
package lu.itrust.business.TS.model.parameter.helper.value;

import lu.itrust.business.TS.model.parameter.AcronymParameter;

/**
 * @author eomar
 *
 */
public interface IValue {
	
	Integer getLevel();
	
	String getVariable();
	
	Double getReal();
	
	AcronymParameter getParameter();
}
