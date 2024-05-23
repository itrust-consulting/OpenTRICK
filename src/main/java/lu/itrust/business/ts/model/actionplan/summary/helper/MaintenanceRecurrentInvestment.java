package lu.itrust.business.ts.model.actionplan.summary.helper;

/**
 * Represents a Maintenance Recurrent Investment.
 */
public class MaintenanceRecurrentInvestment {

	/** Sum of Internal Maintenance Recurrent Investment to the Last Stage Entry */
	private double internalMaintenance = 0;
	
	/** Sum of External Maintenance Recurrent Investment to the Last Stage Entry */
	private double externalMaintenance = 0;
	
	private double recurrentInvestment = 0;
	
	/**
	 * Constructs a new MaintenanceRecurrentInvestment object.
	 */
	public MaintenanceRecurrentInvestment() {
	}

	/**
	 * Constructs a new MaintenanceRecurrentInvestment object with the specified values.
	 * 
	 * @param internalMaintenance the sum of internal maintenance recurrent investment to the last stage entry
	 * @param externalMaintenance the sum of external maintenance recurrent investment to the last stage entry
	 * @param recurrentInvestment the recurrent investment
	 */
	public MaintenanceRecurrentInvestment(double internalMaintenance, double externalMaintenance, double recurrentInvestment) {
		update(internalMaintenance, externalMaintenance, recurrentInvestment);
	}

	/**
	 * Gets the sum of internal maintenance recurrent investment to the last stage entry.
	 * 
	 * @return the sum of internal maintenance recurrent investment
	 */
	public double getInternalMaintenance() {
		return internalMaintenance;
	}

	/**
	 * Sets the sum of internal maintenance recurrent investment to the last stage entry.
	 * 
	 * @param internalMaintenance the sum of internal maintenance recurrent investment
	 */
	public void setInternalMaintenance(double internalMaintenance) {
		this.internalMaintenance = internalMaintenance;
	}

	/**
	 * Gets the sum of external maintenance recurrent investment to the last stage entry.
	 * 
	 * @return the sum of external maintenance recurrent investment
	 */
	public double getExternalMaintenance() {
		return externalMaintenance;
	}

	/**
	 * Sets the sum of external maintenance recurrent investment to the last stage entry.
	 * 
	 * @param externalMaintenance the sum of external maintenance recurrent investment
	 */
	public void setExternalMaintenance(double externalMaintenance) {
		this.externalMaintenance = externalMaintenance;
	}

	/**
	 * Updates the values of internal maintenance, external maintenance, and recurrent investment.
	 * 
	 * @param internalMaintenance the sum of internal maintenance recurrent investment
	 * @param externalMaintenance the sum of external maintenance recurrent investment
	 * @param recurrentInvestment the recurrent investment
	 */
	public void update(double internalMaintenance, double externalMaintenance, double recurrentInvestment) {
		setInternalMaintenance(internalMaintenance);
		setExternalMaintenance(externalMaintenance);
		setRecurrentInvestment(recurrentInvestment);
	}
	
	/**
	 * Adds the specified values to the current internal maintenance, external maintenance, and recurrent investment.
	 * 
	 * @param internalMaintenance the sum of internal maintenance recurrent investment to add
	 * @param externalMaintenance the sum of external maintenance recurrent investment to add
	 * @param recurrentInvestment the recurrent investment to add
	 */
	public void add(double internalMaintenance, double externalMaintenance, double recurrentInvestment) {
		setInternalMaintenance(getInternalMaintenance() + internalMaintenance);
		setExternalMaintenance(getExternalMaintenance() + externalMaintenance);
		setRecurrentInvestment(getRecurrentInvestment() + recurrentInvestment);
	}

	/**
	 * Gets the recurrent investment.
	 * 
	 * @return the recurrent investment
	 */
	public double getRecurrentInvestment() {
		return recurrentInvestment;
	}

	/**
	 * Sets the recurrent investment.
	 * 
	 * @param recurrentInvestment the recurrent investment
	 */
	public void setRecurrentInvestment(double recurrentInvestment) {
		this.recurrentInvestment = recurrentInvestment;
	}
	
}
