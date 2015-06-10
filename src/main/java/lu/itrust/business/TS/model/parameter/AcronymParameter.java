package lu.itrust.business.TS.model.parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 * Represents a parameter which can be referenced to by an acronym.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 10, 2015
 */
@Entity
@PrimaryKeyJoinColumn(name = "idAcronymParameter")
@Inheritance(strategy = InheritanceType.JOINED)
public class AcronymParameter extends Parameter implements Cloneable {

	/** The acronym which can be used to refer to this parameter (e.g. in expressions). */
	@Column(name = "dtAcronym", nullable = false)
	private String acronym = "";

	/**
	 * Gets the acronym of this parameter.
	 */
	public String getAcronym() {
		return acronym;
	}

	/**
	 * Sets the acronym of this parameter.
	 */
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	/**
	 * {@inheritDoc}
	 * @see lu.itrust.business.TS.model.parameter.Parameter#clone()
	 */
	@Override
	public AcronymParameter clone() throws CloneNotSupportedException {
		return (AcronymParameter) super.clone();
	}

	/**
	 * {@inheritDoc}
	 * @see lu.itrust.business.TS.model.parameter.Parameter#duplicate()
	 */
	@Override
	public AcronymParameter duplicate() throws CloneNotSupportedException {
		return (AcronymParameter) super.duplicate();
	}
}