package lu.itrust.business.ts.model.parameter.value;

/**
 * The {@code IValue} interface represents a value object.
 * It provides methods to retrieve various properties of the value.
 */
public interface IValue extends Cloneable {

	/**
	 * Returns the name of the value.
	 *
	 * @return the name of the value
	 */
	String getName();
	
	/**
	 * Returns the level of the value.
	 *
	 * @return the level of the value
	 */
	Integer getLevel();

	/**
	 * Returns the variable of the value.
	 *
	 * @return the variable of the value
	 */
	String getVariable();

	/**
	 * Returns the real value of the value.
	 *
	 * @return the real value of the value
	 */
	Double getReal();
	
	/**
	 * Returns the raw value of the value.
	 *
	 * @return the raw value of the value
	 */
	Object getRaw();

	/**
	 * Merges the given value with this value.
	 *
	 * @param value the value to merge
	 * @return {@code true} if the merge was successful, {@code false} otherwise
	 */
	boolean merge(IValue value);

	/**
	 * Creates a copy of this value object.
	 * The ID will be reset, but other parameters must be updated.
	 *
	 * @return a copy of this value object
	 */
	IValue duplicate();

	/**
	 * Returns the maximum value based on the level of two values.
	 *
	 * @param v1 the first value
	 * @param v2 the second value
	 * @return the maximum value based on the level
	 */
	static IValue maxByLevel(IValue v1, IValue v2) {
		return (v1 == null ? v2 : (v2 == null ? v1 : (v1.getLevel() == v2.getLevel() ? (v1.getReal() > v2.getReal() ? v1 : v2) : v1.getLevel() > v2.getLevel() ? v1 : v2)));
	}

	/**
	 * Returns the maximum value based on the real value of two values.
	 *
	 * @param v1 the first value
	 * @param v2 the second value
	 * @return the maximum value based on the real value
	 */
	static IValue maxByReal(IValue v1, IValue v2) {
		return (v1 == null ? v2 : (v2 == null ? v1 : (v1.getReal() == v2.getReal() ? (v1.getLevel() > v2.getLevel() ? v1 : v2) : v1.getReal() > v2.getReal() ? v1 : v2)));
	}

	/**
	 * Returns the minimum value based on the level of two values.
	 *
	 * @param v1 the first value
	 * @param v2 the second value
	 * @return the minimum value based on the level
	 */
	static IValue minByLevel(IValue v1, IValue v2) {
		return (v1 == null ? v2 : (v2 == null ? v1 : (v1.getLevel() == v2.getLevel() ? (v1.getReal() < v2.getReal() ? v1 : v2) : v1.getLevel() < v2.getLevel() ? v1 : v2)));
	}

	/**
	 * Returns the minimum value based on the real value of two values.
	 *
	 * @param v1 the first value
	 * @param v2 the second value
	 * @return the minimum value based on the real value
	 */
	static IValue minByReal(IValue v1, IValue v2) {
		return (v1 == null ? v2 : (v2 == null ? v1 : (v1.getReal() == v2.getReal() ? (v1.getLevel() < v2.getLevel() ? v1 : v2) : v1.getReal() < v2.getReal() ? v1 : v2)));
	}

	/**
	 * Compares two values based on their level.
	 *
	 * @param v1 the first value
	 * @param v2 the second value
	 * @return a negative integer, zero, or a positive integer as the first value is less than, equal to, or greater than the second value
	 */
	static int compareByLevel(IValue v1, IValue v2) {
		return (v1 == null ? -1 : (v2 == null ? 1 : (v1.getLevel() == v2.getLevel() ? v1.getReal().compareTo(v2.getReal()) : v1.getLevel() > v2.getLevel() ? 1 : -1)));
	}

	/**
	 * Compares two values based on their real value.
	 *
	 * @param v1 the first value
	 * @param v2 the second value
	 * @return a negative integer, zero, or a positive integer as the first value is less than, equal to, or greater than the second value
	 */
	static int compareByReal(IValue v1, IValue v2) {
		return (v1 == null ? -1 : (v2 == null ? 1 : (v1.getReal() == v2.getReal() ? Integer.compare(v1.getLevel(), v2.getLevel()) : v1.getReal() > v2.getReal() ? 1 : -1)));
	}
}
