/**
 * 
 */
package lu.itrust.business.ts.model.parameter.value;

/**
 * @author eomar
 *
 */
public interface IValue extends Cloneable {

	String getName();
	
	Integer getLevel();

	String getVariable();

	Double getReal();
	
	Object getRaw();

	boolean merge(IValue value);

	/**
	 * Create copy of object instance, id will be reset but parameter must be
	 * updated.
	 * 
	 * @return copy
	 */
	IValue duplicate();

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

	static int compareByLevel(IValue v1, IValue v2) {
		return (v1 == null ? -1 : (v2 == null ? 1 : (v1.getLevel() == v2.getLevel() ? v1.getReal().compareTo(v2.getReal()) : v1.getLevel() > v2.getLevel() ? 1 : -1)));
	}

	static int compareByReal(IValue v1, IValue v2) {
		return (v1 == null ? -1 : (v2 == null ? 1 : (v1.getReal() == v2.getReal() ? Integer.compare(v1.getLevel(), v2.getLevel()) : v1.getReal() > v2.getReal() ? 1 : -1)));
	}
}
