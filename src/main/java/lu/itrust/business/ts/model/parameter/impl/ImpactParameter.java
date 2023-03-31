package lu.itrust.business.ts.model.parameter.impl;

import java.util.List;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.parameter.IImpactParameter;
import lu.itrust.business.ts.model.parameter.ITypedParameter;
import lu.itrust.business.ts.model.parameter.helper.Bounds;
import lu.itrust.business.ts.model.scale.ScaleType;

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
@AttributeOverride(name = "id", column = @Column(name = "idImpactParameter"))
@AttributeOverride(name = "description", column = @Column(name = "dtDescription", nullable = false, length = 1024))
public class ImpactParameter extends Parameter implements ITypedParameter, IImpactParameter {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The SimpleParameter Type */
	@ManyToOne
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
	 * @see lu.itrust.business.ts.model.parameter.ILevelParameter#getLevel()
	 */
	@Override
	public Integer getLevel() {
		return level;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.parameter.ILevelParameter#setLevel(int)
	 */
	public void setLevel(int level) throws TrickException {
		if (level < 0)
			throw new TrickException("error.impact.level", "Impact level must be 0 or greater");
		this.level = level;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.parameter.impl.IBoundedParameter#getBounds()
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
	 *               The value to set the Bound values (from and to values)
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
	 *             the type to set
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
	 *                the acronym to set
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
	 *              the label to set
	 */

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getBaseKey() {
		return getAcronym();
	}

	/**
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.model.parameter.impl.SimpleParameter#clone()
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
	 * @see lu.itrust.business.ts.model.parameter.impl.SimpleParameter#duplicate()
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
		// if (impacts.size() % 2 == 0) {
		for (int level = 0, maxLevel = impacts.size() - 1; level < impacts.size(); level++) {
			if (level == 0) {
				ImpactParameter current = impacts.get(level);
				if (level == maxLevel)
					current.setBounds(new Bounds(0, Constant.DOUBLE_MAX_VALUE));
				else
					current.setBounds(new Bounds(0, Math.sqrt(current.getValue() * impacts.get(level + 1).getValue())));
			} else if (level == maxLevel)
				impacts.get(level)
						.setBounds(new Bounds(impacts.get(level - 1).getBounds().getTo(), Constant.DOUBLE_MAX_VALUE));
			else {
				ImpactParameter current = impacts.get(level);
				current.setBounds(new Bounds(impacts.get(level - 1).getBounds().getTo(),
						Math.sqrt(current.getValue() * impacts.get(level + 1).getValue())));
			}
		}

	}

}