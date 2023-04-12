package lu.itrust.business.ts.model.parameter;

public interface IAcronymParameter extends IParameter {

	/**
	 * Gets the acronym of this parameter.
	 */
	String getAcronym();

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.parameter.IParameter#getValue()
	 */
	@Override
	Double getValue();

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.parameter.IParameter#getKey()
	 */
	@Override
	default String getKey() {
		return String.format(KEY_PARAMETER_FORMAT, getTypeName(), getAcronym());
	}

}