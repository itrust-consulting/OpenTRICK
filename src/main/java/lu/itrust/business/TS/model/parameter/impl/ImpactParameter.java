package lu.itrust.business.TS.model.parameter.impl;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
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
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "idImpactParameter")),
		@AttributeOverride(name = "description", column = @Column(name = "dtDescription", nullable = false, length = 1024)) })
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
	public ImpactParameter() {
	}

	public ImpactParameter(ScaleType type, int level, String acronym) {
		setType(type);
		setLevel(level);
		setAcronym(acronym);
	}

	public ImpactParameter(ScaleType type, int level, String acronym, double value) {
		this(type, level, acronym);
		setValue(value);
	}

	/**
	 * @param type
	 * @param acronym
	 * @param level
	 * @param value
	 * @param description
	 * @param bounds
	 * @param label
	 */
	public ImpactParameter(ScaleType type, String acronym, int level, double value, String description, Bounds bounds) {
		super(value, description);
		this.type = type;
		this.acronym = acronym;
		this.level = level;
		this.bounds = bounds;
	}

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
		if (level < 0)
			throw new TrickException("error.impact.level", "Impact level must be 0 or greater");
		this.level = level;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.parameter.impl.IBoundedParameter#getBounds()
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
	public static void ComputeScales(List<ImpactParameter> impacts) {
		impacts.sort((p1, p2) -> Integer.compare(p1.getLevel(), p2.getLevel()));
		if (impacts.size() % 2 == 0) {
			for (int level = 0, maxLevel = impacts.size() - 1; level < impacts.size(); level++) {
				if (level == 0) {
					ImpactParameter current = impacts.get(level);
					if (level == maxLevel)
						current.setBounds(new Bounds(0, Constant.DOUBLE_MAX_VALUE));
					else
						current.setBounds(new Bounds(0, Math.sqrt(current.getValue() * impacts.get(level + 1).getValue())));
				} else if (level == maxLevel)
					impacts.get(level).setBounds(new Bounds(impacts.get(level - 1).getBounds().getTo(), Constant.DOUBLE_MAX_VALUE));
				else {
					ImpactParameter current = impacts.get(level);
					current.setBounds(new Bounds(impacts.get(level - 1).getBounds().getTo(), Math.sqrt(current.getValue() * impacts.get(level + 1).getValue())));
				}
			}
		} else {
			ImpactParameter prev = null;
			for (int level = 1, maxLevel = impacts.size() - 1; level < maxLevel; level += 2) {
				ImpactParameter current = impacts.get(level), next = impacts.get(level + 1);
				prev = impacts.get(level - 1);
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