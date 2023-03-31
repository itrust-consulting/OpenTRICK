package lu.itrust.business.ts.model.parameter.impl;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.parameter.IProbabilityParameter;

/**
 * Represents a parameter which can be referenced to by an acronym.
 * 
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 10, 2015
 */
@MappedSuperclass
public abstract class AbstractProbability extends Parameter implements IProbabilityParameter {

	/**
	 * The acronym which can be used to refer to this parameter (e.g. in
	 * expressions).
	 */
	@Column(name = "dtAcronym", nullable = false)
	private String acronym = "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.parameter.IAcronymParameter#getAcronym()
	 */
	@Override
	public String getAcronym() {
		return acronym;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.model.parameter.IAcronymParameter#setAcronym(java.
	 * lang.String)
	 */
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	@Override
	public String getBaseKey() {
		return getAcronym();
	}

	public static String key(String type, String acronym) {
		return String.format(IParameter.KEY_PARAMETER_FORMAT, type, acronym);
	}

}