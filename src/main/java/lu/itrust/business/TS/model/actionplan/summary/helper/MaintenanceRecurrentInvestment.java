package lu.itrust.business.TS.model.actionplan.summary.helper;

public class MaintenanceRecurrentInvestment {

	/** Sum of Internal MaintenanceRecurrentInvestment to the Last Stage Entry */
	private double internalMaintenance = 0;
	
	/** Sum of External MaintenanceRecurrentInvestment to the Last Stage Entry */
	private double externalMaintenance = 0;
	
	private double recurrentInvestment = 0;
	
	/**
	 * 
	 */
	public MaintenanceRecurrentInvestment() {
	}

	/**
	 * @param internalMaintenance
	 * @param externalMaintenance
	 * @param recurrentInvestment
	 */
	public MaintenanceRecurrentInvestment(double internalMaintenance, double externalMaintenance,double recurrentInvestment) {
		update(internalMaintenance, externalMaintenance,recurrentInvestment);
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

	public void update(double internalMaintenance, double externalMaintenance, double recurrentInvestment) {
		setInternalMaintenance(internalMaintenance);
		setExternalMaintenance(externalMaintenance);
		setRecurrentInvestment(recurrentInvestment);
	}
	
	public void add(double internalMaintenance, double externalMaintenance, double recurrentInvestment) {
		setInternalMaintenance(getInternalMaintenance() + internalMaintenance);
		setExternalMaintenance(getExternalMaintenance() + externalMaintenance);
		setRecurrentInvestment(getRecurrentInvestment() + recurrentInvestment);
	}

	/**
	 * @return the recurrentInvestment
	 */
	public double getRecurrentInvestment() {
		return recurrentInvestment;
	}

	/**
	 * @param recurrentInvestment the recurrentInvestment to set
	 */
	public void setRecurrentInvestment(double recurrentInvestment) {
		this.recurrentInvestment = recurrentInvestment;
	}
	
}
