package lu.itrust.business.ts.model.cssf;

public enum RiskStrategy {

	ACCEPT, REDUCE, TRANSFER, AVOID;

	public String getName() {
		return this.name();
	}

	public String getNameToLower() {
		return getName().toLowerCase();
	}

}
