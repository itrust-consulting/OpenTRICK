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

	static IValue maxByLevel(IValue v1, IValue v2) {
		return (v1 == null ? v2 : (v2 == null ? v1 : (v1.getLevel() == v2.getLevel() ? (v1.getReal() > v2.getReal() ? v1 : v2) : v1.getLevel() > v2.getLevel() ? v1 : v2)));
	}
	
	static IValue maxByReal(IValue v1, IValue v2) {
		return (v1 == null ? v2 : (v2 == null ? v1 : (v1.getReal() == v2.getReal() ? (v1.getLevel() > v2.getLevel() ? v1 : v2) : v1.getReal() > v2.getReal() ? v1 : v2)));
	}
	
	static IValue minByLevel(IValue v1, IValue v2) {
		return (v1 == null ? v2 : (v2 == null ? v1 : (v1.getLevel() == v2.getLevel() ? (v1.getReal() < v2.getReal() ? v1 : v2) : v1.getLevel() < v2.getLevel() ? v1 : v2)));
	}
	
	static IValue minByReal(IValue v1, IValue v2) {
		return (v1 == null ? v2 : (v2 == null ? v1 : (v1.getReal() == v2.getReal() ? (v1.getLevel() < v2.getLevel() ? v1 : v2) : v1.getReal() < v2.getReal() ? v1 : v2)));
	}
}
