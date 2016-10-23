/**
 * 
 */
package lu.itrust.business.TS.model.parameter.impl;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.helper.Bounds;

/**
 * @author eomar
 *
 */
@Entity
@AttributeOverride(name="id", column=@Column(name="idLikelihoodParameter"))
public class LikelihoodParameter extends AbstractProbability implements IBoundedParameter {
	
	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The Extended SimpleParameter Level (default: 0-5 or 0-6 -> NOT restricted) */
	@Column(name = "dtLevel", nullable = false)
	private int level = 0;

	/** Extended SimpleParameter From And To values */
	@Embedded
	private Bounds bounds = null;

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.parameter.ILevelParameter#getLevel()
	 */
	@Override
	public Integer getLevel() {
		return level;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.parameter.IBoundedParameter#getBounds()
	 */
	@Override
	public Bounds getBounds() {
		return bounds;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @param bounds the bounds to set
	 */
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	@Override
	public String getTypeName() {
		return Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME;
	}
	
}
