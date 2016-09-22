/**
 * 
 */
package lu.itrust.business.TS.model.assessment;

/**
 * @author eomar
 *
 */
public interface IValue {

	int getLevel();
	
	String getVariable();
	
	double getNumeric();
	
	Object getValue();
}
