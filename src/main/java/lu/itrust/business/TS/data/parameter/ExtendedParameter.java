package lu.itrust.business.TS.data.parameter;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.data.parameter.helper.Bounds;
import lu.itrust.business.TS.exception.TrickException;

/**
 * ExtendedParameter: <br>
 * This class represents an Extended Parameter and all its data.
 * 
 * The Class extends Parameter which has basic parameter fields.
 * 
 * This class is used to store Extended Parameter. Extended parameters are: <br>
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
@PrimaryKeyJoinColumn(name = "idExtendedParameter")
public class ExtendedParameter extends Parameter implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The Extended Parameter Level (default: 0-5 or 0-6 -> NOT restricted) */
	@Column(name = "dtLevel", nullable = false)
	private int level = 0;

	/** The Extended Parameter Acronym */
	@Column(name = "dtAcronym", nullable = false)
	private String acronym = "";

	/** Extended Parameter From And To values */
	@Embedded
	private Bounds bounds = null;

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getLevel: <br>
	 * Returns the "level" field value
	 * 
	 * @return The Level of the Extended Parameter
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * setLevel: <br>
	 * Sets the "level" field with a value
	 * 
	 * @param level
	 *            The value to set the Level
	 * @throws TrickException
	 */
	public void setLevel(int level) throws TrickException {
		if (level < 0 || level > 10)
			throw new TrickException("error.extended_parameter.level", "Level needs to be between 0 and 10 included!");
		this.level = level;
	}

	/**
	 * getAcronym: <br>
	 * Returns the "acronym" field value
	 * 
	 * @return The Acronym
	 */
	public String getAcronym() {
		return acronym;
	}

	/**
	 * setAcronym: <br>
	 * Sets the "acronym" field with a value
	 * 
	 * @param acronym
	 *            The value to set the Acronym
	 */
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	/**
	 * getBounds: <br>
	 * Returns the "bounds" field value
	 * 
	 * @return The Bounds values (from and to values)
	 */
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
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.data.parameter.Parameter#clone()
	 */
	@Override
	public ExtendedParameter clone() throws CloneNotSupportedException {
		ExtendedParameter parameter = (ExtendedParameter) super.clone();
		parameter.bounds = (Bounds) this.bounds.clone();
		return parameter;
	}

	/**
	 * duplicate: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.data.parameter.Parameter#duplicate()
	 */
	@Override
	public ExtendedParameter duplicate() throws CloneNotSupportedException {
		ExtendedParameter parameter = (ExtendedParameter) super.duplicate();
		parameter.bounds = (Bounds) this.bounds.clone();
		return parameter;
	}

	/**
	 * ComputeScales: <br>
	 * Description
	 * 
	 * @param extendedParameter
	 * @param extendedParameterPrev
	 * @param extendedParameterNext
	 */
	public static void ComputeScales(ExtendedParameter extendedParameter, ExtendedParameter extendedParameterPrev, ExtendedParameter extendedParameterNext) {
		extendedParameter.setValue(Math.sqrt(extendedParameterPrev.getValue() * extendedParameterNext.getValue()));

		if (extendedParameterPrev.level == 0) {
			extendedParameterPrev.bounds = new Bounds(0, Math.sqrt(extendedParameter.getValue() * extendedParameterPrev.getValue()));
		} else {
			extendedParameterPrev.bounds = new Bounds(extendedParameterPrev.bounds.getFrom(), Math.sqrt(extendedParameter.getValue() * extendedParameterPrev.getValue()));
		}

		extendedParameter.bounds = new Bounds(extendedParameterPrev.bounds.getTo(), Math.sqrt(extendedParameter.getValue() * extendedParameterNext.getValue()));

		if (extendedParameterNext.level == 10)
			extendedParameterNext.bounds = new Bounds(extendedParameter.bounds.getTo(), Constant.DOUBLE_MAX_VALUE);
		else
			extendedParameterNext.bounds = new Bounds(extendedParameter.bounds.getTo(), extendedParameter.bounds.getTo() + 1);

	}
}