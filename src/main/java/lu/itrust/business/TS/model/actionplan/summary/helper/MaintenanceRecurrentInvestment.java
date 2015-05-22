/**
 * 
 */
package lu.itrust.business.TS.model.actionplan.summary.helper;

/**
 * @author eomar
 *
 */
public class MaintenanceRecurrentInvestment extends Maintenance {

	private double recurrentInvestment;

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
	public MaintenanceRecurrentInvestment(double internalMaintenance, double externalMaintenance, double recurrentInvestment) {
		super(internalMaintenance, externalMaintenance);
		this.recurrentInvestment = recurrentInvestment;
	}

	/**
	 * @return the recurrentInvestment
	 */
	public double getRecurrentInvestment() {
		return recurrentInvestment;
	}

	/**
	 * @param recurrentInvestment
	 *            the recurrentInvestment to set
	 */
	public void setRecurrentInvestment(double recurrentInvestment) {
		this.recurrentInvestment = recurrentInvestment;
	}

	public void update(double internalMaintenance, double externalMaintenance, double recurrentInvestment) {
		super.update(internalMaintenance, externalMaintenance);
		setRecurrentInvestment(recurrentInvestment);
	}

	public void add(double internalMaintenance, double externalMaintenance, double recurrentInvestment) {
		setInternalMaintenance(getInternalMaintenance() + internalMaintenance);
		setExternalMaintenance(getExternalMaintenance() + externalMaintenance);
		setRecurrentInvestment(getRecurrentInvestment() + recurrentInvestment);
	}
}
