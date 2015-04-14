package lu.itrust.business.TS.model.actionplan.summary.helper;

public class Maintenance {

	/** Sum of Internal Maintenance to the Last Stage Entry */
	private double internalMaintenance = 0;
	
	/** Sum of External Maintenance to the Last Stage Entry */
	private double externalMaintenance = 0;
	
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
	public Maintenance(double internalMaintenance, double externalMaintenance) {
		update(internalMaintenance, externalMaintenance);
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

	public void update(double internalMaintenance, double externalMaintenance) {
		setInternalMaintenance(internalMaintenance);
		setExternalMaintenance(externalMaintenance);
		
	}
	
}
