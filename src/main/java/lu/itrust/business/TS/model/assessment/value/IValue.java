/**
 * 
 */
package lu.itrust.business.TS.model.assessment.value;

import lu.itrust.business.TS.model.parameter.AcronymParameter;

/**
 * @author eomar
 *
 */
public interface IValue {
	
	Integer getLevel();
	
	String getVariable();
	
	Double getNumeric();
	
	AcronymParameter getParameter();
}
