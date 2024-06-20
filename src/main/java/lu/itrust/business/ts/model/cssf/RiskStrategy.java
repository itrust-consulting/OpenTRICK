package lu.itrust.business.ts.model.cssf;

/**
 * The RiskStrategy enum represents different risk strategies that can be applied.
 */
public enum RiskStrategy {

	ACCEPT, REDUCE, TRANSFER, AVOID;

	/**
	 * Returns the name of the risk strategy.
	 *
	 * @return the name of the risk strategy
	 */
	public String getName() {
		return this.name();
	}

	/**
	 * Returns the lowercase name of the risk strategy.
	 *
	 * @return the lowercase name of the risk strategy
	 */
	public String getNameToLower() {
		return getName().toLowerCase();
	}
}
