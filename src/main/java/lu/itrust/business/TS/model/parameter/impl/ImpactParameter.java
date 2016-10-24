package lu.itrust.business.TS.model.parameter.impl;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.parameter.IImpactParameter;
import lu.itrust.business.TS.model.parameter.ITypedParameter;
import lu.itrust.business.TS.model.parameter.helper.Bounds;
import lu.itrust.business.TS.model.scale.ScaleType;

/**
 * ImpactParameter: <br>
 * This class represents an Extended SimpleParameter and all its data.
 * 
 * The Class extends SimpleParameter which has basic parameter fields.
 * 
 * This class is used to store Extended SimpleParameter. Extended parameters
 * are: <br>
 * <ul>
 * <li>Impact values</li>
 * <li>Likelihood values</li>
 * </ul>
 * 
 * @author itrust consulting s.à r.l. - SME,BJA
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@AttributeOverride(name = "id", column = @Column(name = "idImpactParameter"))
public class ImpactParameter extends Parameter implements ITypedParameter, IImpactParameter {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The SimpleParameter Type */
	@ManyToOne
	@JoinColumn(name = "fiParameterType", nullable = false)
	@Access(AccessType.FIELD)
	@Cascade(CascadeType.SAVE_UPDATE)
	private ScaleType type = null;

	/**
	 * The acronym which can be used to refer to this parameter (e.g. in
	 * expressions).
	 */
	@Column(name = "dtAcronym", nullable = false)
	private String acronym = "";

	/**
	 * The Extended SimpleParameter Level (default: 0-5 or 0-6 -> NOT
	 * restricted)
	 */
	@Column(name = "dtLevel", nullable = false)
	private int level = 0;

	/** Extended SimpleParameter From And To values */
	@Embedded
	private Bounds bounds = null;

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

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
	 * @see lu.itrust.business.TS.model.parameter.ILevelParameter#setLevel(int)
	 */
	public void setLevel(int level) throws TrickException {
		if (level < 0 || level > 10)
			throw new TrickException("error.extended_parameter.level", "Level needs to be between 0 and 10 included!");
		this.level = level;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.parameter.impl.IBoundedParameter#getBounds()
	 */
	@Override
	public Bounds getBounds() {
		return bounds;
	}

	/**
	 * setBounds: <br>
	 * Sets the "bounds" field with a value
	 * 
	 * @param bounds
	 *            The value to set the Bound values (from and to values)
	 */
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	/**
	 * @return the type
	 */
	@Override
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
	 * @return the acronym
	 */
	@Override
	public String getAcronym() {
		return acronym;
	}

	/**
	 * @param acronym
	 *            the acronym to set
	 */
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	@Override
	public String getTypeName() {
		return type.getName();
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
	public ImpactParameter clone() {
		ImpactParameter parameter = (ImpactParameter) super.clone();
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
	public ImpactParameter duplicate() {
		ImpactParameter parameter = (ImpactParameter) super.duplicate();
		parameter.bounds = (Bounds) this.bounds.clone();
		return parameter;
	}

	/**
	 * ComputeScales: <br>
	 * Description
	 * 
	 * @param prev
	 * @param current
	 * @param next
	 */
	public static void ComputeScales(ImpactParameter prev, ImpactParameter current, ImpactParameter next) {
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

}