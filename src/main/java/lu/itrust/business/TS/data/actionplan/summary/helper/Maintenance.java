package lu.itrust.business.TS.data.actionplan.summary.helper;

public class Maintenance {

	/** Sum of Internal Maintenance to the Last Stage Entry */
	private double internalMaintenance = 0;
	
	/** Sum of External Maintenance to the Last Stage Entry */
	private double externalMaintenance = 0;
	
	/** Sum of recurrent investment to the Last Stage Entry */
	private double recurrentInvestment = 0;
	
	/**
	 * 
	 */
	public Maintenance() {
	}

	/**
	 * @param internalMaintenance
	 * @param externalMaintenance
	 * @param recurrentInvestment
	 */
	public Maintenance(double internalMaintenance, double externalMaintenance, double recurrentInvestment) {
		this.internalMaintenance = internalMaintenance;
		this.externalMaintenance = externalMaintenance;
		this.recurrentInvestment = recurrentInvestment;
	}

	public double getInternalMaintenance() {
		return internalMaintenance;
	}

	public void setInternalMaintenance(double internalMaintenance) {
		this.internalMaintenance = internalMaintenance;
	}

	public double getExternalMaintenance() {
		return externalMaintenance;
	}

	public void setExternalMaintenance(double externalMaintenance) {
		this.externalMaintenance = externalMaintenance;
	}

	public double getRecurrentInvestment() {
		return recurrentInvestment;
	}

	public void setRecurrentInvestment(double recurrentInvestment) {
		this.recurrentInvestment = recurrentInvestment;
	}
	
}
