package lu.itrust.business.ts.model.parameter.impl;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.parameter.IProbabilityParameter;

/**
 * Represents a parameter which can be referenced to by an acronym.
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

	/**
	 * Returns the base key for this probability.
	 *
	 * @return the base key as a string
	 */
	@Override
	public String getBaseKey() {
		return getAcronym();
	}

	/**
	 * Returns a formatted key for the given type and acronym.
	 *
	 * @param type    the type of the key
	 * @param acronym the acronym of the key
	 * @return the formatted key
	 */
	public static String key(String type, String acronym) {
		return String.format(IParameter.KEY_PARAMETER_FORMAT, type, acronym);
	}

}