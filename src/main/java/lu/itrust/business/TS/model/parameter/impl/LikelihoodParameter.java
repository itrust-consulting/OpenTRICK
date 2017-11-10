/**
 * 
 */
package lu.itrust.business.TS.model.parameter.impl;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.helper.Bounds;

/**
 * @author eomar
 *
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "idLikelihoodParameter")),
		@AttributeOverride(name = "description", column = @Column(name = "dtDescription", nullable = false, length = 1024)) })
public class LikelihoodParameter extends AbstractProbability implements IBoundedParameter {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/**
	 * The Extended SimpleParameter Level (default: 0-5 or 0-6 -> NOT restricted)
	 */
	@Column(name = "dtLevel", nullable = false)
	private int level = 0;

	/** Extended SimpleParameter From And To values */
	@Embedded
	private Bounds bounds = null;

	@Column(name = "dtLabel", nullable = false)
	private String label = "";

	/**
	 * 
	 */
	public LikelihoodParameter() {
	}

	/**
	 * @param level
	 * @param acronym
	 */
	public LikelihoodParameter(int level, String acronym) {
		setLevel(level);
		setAcronym(acronym);
	}

	/**
	 * @param level
	 * @param acronym
	 * @param value
	 */
	public LikelihoodParameter(int level, String acronym, double value) {
		this(level, acronym);
		setValue(value);

	}

	/**
	 * @param level
	 * @param acronym
	 * @param value
	 * @param bounds
	 */
	public LikelihoodParameter(int level, String acronym, double value, Bounds bounds) {
		this(level, acronym, value);
		setBounds(bounds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.parameter.ILevelParameter#getLevel()
	 */
	@Override
	public Integer getLevel() {
		return level;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.parameter.IBoundedParameter#getBounds()
	 */
	@Override
	public Bounds getBounds() {
		return bounds;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		if (level < 0)
			throw new TrickException("error.likelihood.level", "Probability level must be 0 or greater");
		this.level = level;
	}

	/**
	 * @param bounds
	 *            the bounds to set
	 */
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	@Override
	public String getTypeName() {
		return Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.parameter.IParameter#getCategory()
	 */
	@Override
	public String getGroup() {
		return Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD;
	}

	/**
	 * @return the label
	 */
	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.model.parameter.impl.SimpleParameter#clone()
	 */
	@Override
	public LikelihoodParameter clone() {
		LikelihoodParameter parameter = (LikelihoodParameter) super.clone();
		parameter.bounds = (Bounds) this.bounds.clone();
		return parameter;
	}

	/**
	 * duplicate: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.model.parameter.impl.SimpleParameter#duplicate()
	 */
	@Override
	public LikelihoodParameter duplicate() {
		LikelihoodParameter parameter = (LikelihoodParameter) super.duplicate();
		parameter.bounds = (Bounds) this.bounds.clone();
		return parameter;
	}

	public static void ComputeScales(LikelihoodParameter prev, LikelihoodParameter current, LikelihoodParameter next) {
		// throw new
		// TrickException("error.compute.scale.extended.parameter.bad.type",
		// "Scales cannot only compute for probability and financial impact");
		prev.setValue(Math.sqrt(current.getValue() * next.getValue()));
		if (current.level == 0)
			current.bounds = new Bounds(0, Math.sqrt(prev.getValue() * current.getValue()));
		else
			current.bounds = new Bounds(current.bounds.getFrom(), Math.sqrt(prev.getValue() * current.getValue()));
		prev.bounds = new Bounds(current.bounds.getTo(), Math.sqrt(prev.getValue() * next.getValue()));
		next.bounds = new Bounds(prev.bounds.getTo(), Constant.DOUBLE_MAX_VALUE);
	}

	public static void ComputeScales(List<LikelihoodParameter> parameters) {
		parameters.sort((p1, p2) -> Integer.compare(p1.getLevel(), p2.getLevel()));
		if (parameters.size() % 2 == 0) {
			for (int level = 0, maxLevel = parameters.size() - 1; level < parameters.size(); level++) {
				if (level == 0) {
					LikelihoodParameter current = parameters.get(level);
					if (level == maxLevel)
						current.setBounds(new Bounds(0, Constant.DOUBLE_MAX_VALUE));
					else
						current.setBounds(new Bounds(0, Math.sqrt(current.getValue() * parameters.get(level + 1).getValue())));
				} else if (level == maxLevel)
					parameters.get(level).setBounds(new Bounds(parameters.get(level - 1).getBounds().getTo(), Constant.DOUBLE_MAX_VALUE));
				else {
					LikelihoodParameter current = parameters.get(level);
					current.setBounds(new Bounds(parameters.get(level - 1).getBounds().getTo(), Math.sqrt(current.getValue() * parameters.get(level + 1).getValue())));
				}
			}
		} else {
			LikelihoodParameter prev = null;
			for (int level = 1, maxLevel = parameters.size() - 1; level < maxLevel; level += 2) {
				LikelihoodParameter current = parameters.get(level), next = parameters.get(level + 1);
				prev = parameters.get(level - 1);
				if (prev.getLevel() == 0)
					prev.setBounds(new Bounds(0, Math.sqrt(current.getValue() * prev.getValue())));
				else
					prev.setBounds(new Bounds(prev.getBounds().getFrom(), Math.sqrt(current.getValue() * prev.getValue())));
				current.setValue(Math.sqrt(prev.getValue() * next.getValue()));
				current.setBounds(new Bounds(prev.getBounds().getTo(), Math.sqrt(current.getValue() * next.getValue())));
				next.setBounds(new Bounds(current.getBounds().getTo(), Constant.DOUBLE_MAX_VALUE));
			}
		}
	}

}
