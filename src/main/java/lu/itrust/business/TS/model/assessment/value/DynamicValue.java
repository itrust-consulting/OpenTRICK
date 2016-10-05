/**
 * 
 */
package lu.itrust.business.TS.model.assessment.value;

import lu.itrust.business.TS.model.parameter.AcronymParameter;

/**
 * @author eomar
 *
 */
public class DynamicValue extends AcronymValue {

	private int level;

	/**
	 * @param value
	 * @param level
	 */
	public DynamicValue(AcronymParameter value, int level) {
		super(value);
		this.level = level;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.assessment.helper.IValue#getLevel()
	 */
	@Override
	public Integer getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

}
