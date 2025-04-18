/**
 * 
 */
package lu.itrust.business.ts.model.scale;


/**
 * Represents a scale used in a measurement system.
 */
public class Scale {

	private ScaleType type;

	private int level;

	private double maxValue;

	/**
	 * Default constructor for the Scale class.
	 */
	public Scale() {
	}

	/**
	 * Constructs a Scale object with the specified type, level, and maximum value.
	 *
	 * @param type      the type of the scale
	 * @param level     the level of the scale
	 * @param maxValue  the maximum value of the scale
	 */
	public Scale(ScaleType type, int level, double maxValue) {
		setType(type);
		setLevel(level);
		setMaxValue(maxValue);
	}

	/**
	 * Returns the type of the scale.
	 *
	 * @return the type of the scale
	 */
	public ScaleType getType() {
		return type;
	}

	/**
	 * Sets the type of the scale.
	 *
	 * @param type the type of the scale to set
	 */
	public void setType(ScaleType type) {
		this.type = type;
	}

	/**
	 * Returns the level of the scale.
	 *
	 * @return the level of the scale
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Sets the level of the scale.
	 *
	 * @param level the level of the scale to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Returns the maximum value of the scale.
	 *
	 * @return the maximum value of the scale
	 */
	public double getMaxValue() {
		return maxValue;
	}

	/**
	 * Sets the maximum value of the scale.
	 *
	 * @param maxValue the maximum value of the scale to set
	 */
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	
	/**
	 * Merges the properties of the specified scale into this scale.
	 *
	 * @param scale the scale to merge
	 */
	public void merge(Scale scale) {
		if (scale.type != null)
			scale.type.forEach((local, translate) -> this.type.put(local, translate));
		this.level = scale.level;
		this.maxValue = scale.maxValue;
	}

}
