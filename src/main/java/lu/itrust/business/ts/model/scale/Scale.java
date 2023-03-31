/**
 * 
 */
package lu.itrust.business.ts.model.scale;

/**
 * @author eomar
 *
 */
public class Scale {

	private ScaleType type;

	private int level;

	private double maxValue;

	/**
	 * 
	 */
	public Scale() {
	}

	public Scale(ScaleType type, int level, double maxValue) {
		setType(type);
		setLevel(level);
		setMaxValue(maxValue);
	}


	/**
	 * @return the type
	 */
	public ScaleType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(ScaleType type) {
		this.type = type;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return the maxValue
	 */
	public double getMaxValue() {
		return maxValue;
	}

	/**
	 * @param maxValue
	 *            the maxValue to set
	 */
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	
	public void merge(Scale scale) {
		if (scale.type != null)
			scale.type.forEach((local, translate) -> this.type.put(local, translate));
		this.level = scale.level;
		this.maxValue = scale.maxValue;
	}

}
