/**
 * 
 */
package lu.itrust.business.TS.model.analysis.helper;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;
import lu.itrust.business.TS.model.standard.measure.Measure;

/**
 * @author eomar
 *
 */
public final class AnalysisDataManagement {

	/**
	 * initialiseEmptyItemInformation: <br>
	 * Description
	 * 
	 * @param analysis
	 */
	public static final void initialiseEmptyItemInformation(Analysis analysis) {
		if (analysis == null)
			return;
		analysis.getItemInformations().clear();
		ItemInformation iteminfo;
		iteminfo = new ItemInformation(Constant.TYPE_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.TYPE_PROFIT_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.NAME_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.PRESENTATION_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.SECTOR_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.RESPONSIBLE_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.STAFF_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.ACTIVITIES_ORGANISM, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.EXCLUDED_ASSETS, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.OCCUPATION, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.FUNCTIONAL, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.JURIDIC, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.POL_ORGANISATION, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.MANAGEMENT_ORGANISATION, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.PREMISES, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.REQUIREMENTS, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.EXPECTATIONS, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.ENVIRONMENT, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.INTERFACE, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.STRATEGIC, Constant.ITEMINFORMATION_SCOPE, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.PROCESSUS_DEVELOPMENT, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.STAKEHOLDER_IDENTIFICATION, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.ROLE_RESPONSABILITY, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.STAKEHOLDER_RELATION, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.ESCALATION_WAY, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
		iteminfo = new ItemInformation(Constant.DOCUMENT_CONSERVE, Constant.ITEMINFORMATION_ORGANISATION, Constant.EMPTY_STRING);
		analysis.add(iteminfo);
	}

	/**
	 * computeCost: <br>
	 * Returns the Calculated Cost of a Measure. This method does no more need the
	 * parameter default maintenance, but needs to get the internal and external
	 * maintenance in md as well as the recurrent investment per year in keuro. <br>
	 * Formula used:<br>
	 * Cost = ((ir * iw) + (er * ew) + in) * ((1.0 / lt) + ((im * ir) + (em * er)+
	 * ri))<br>
	 * With:<br>
	 * ir: The Internal Setup Rate in Euro per Man Day<br>
	 * iw: The Internal Workload in Man Days<br>
	 * er: The External Setup Rate in Euro per Man Day<br>
	 * ew: The External Workload in Man Days<br>
	 * in: The Investment in kEuro<br>
	 * lt: The Lifetime in Years :: if 0 -> use The Default LifeTime in Years <br>
	 * im: The Internal MaintenanceRecurrentInvestment in Man Days<br>
	 * em: The External MaintenanceRecurrentInvestment in Man Days<br>
	 * ri: The recurrent Investment in kEuro<br>
	 * 
	 * @param internalSetupRate
	 * 
	 * @param externalSetupRate
	 * 
	 * @param lifetimeDefault
	 * 
	 * @param internalMaintenance
	 * 
	 * @param externalMaintenance
	 * 
	 * @param recurrentInvestment
	 * 
	 * @param internalWorkLoad
	 * 
	 * @param externalWorkLoad
	 * 
	 * @param investment
	 * 
	 * @param lifetime
	 * 
	 * @return The Calculated Cost
	 */
	public static final double computeCost(double internalSetupRate, double externalSetupRate, double lifetimeDefault, double internalMaintenance, double externalMaintenance,
			double recurrentInvestment, double internalWorkLoad, double externalWorkLoad, double investment, double lifetime) {

		// ****************************************************************
		// * variable initialisation
		// ****************************************************************
		// internal setup * internal wokload + external setup * external
		// workload
		// check if lifetime is not 0 -> YES: use default lifetime
		// return calculated cost
		return (((internalSetupRate * internalWorkLoad) + (externalSetupRate * externalWorkLoad) + investment) * (1. / (lifetime == 0 ? lifetimeDefault : lifetime)))
				+ ((internalMaintenance * internalSetupRate) + (externalMaintenance * externalSetupRate) + recurrentInvestment);
	}

	/**
	 * computeCost: <br>
	 * Returns the Calculated Cost of a given Measure.
	 * 
	 * @param measure
	 *            The Measure to calculate the Cost
	 * 
	 * @return The Calculated Cost
	 */
	public static double computeCost(Measure measure, Analysis analysis) {

		// ****************************************************************
		// * variable initialisation
		// ****************************************************************
		double cost = 0;
		double externalSetupValue = -1;
		double internalSetupValue = -1;
		double lifetimeDefault = -1;

		// ****************************************************************
		// * select external and internal setup rate from parameters
		// ****************************************************************

		internalSetupValue = analysis.getParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE);

		externalSetupValue = analysis.getParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE);

		lifetimeDefault = analysis.getParameter(Constant.PARAMETER_LIFETIME_DEFAULT);

		// calculate the cost
		cost = computeCost(internalSetupValue, externalSetupValue, lifetimeDefault, measure.getInternalWL(), measure.getExternalWL(), measure.getInvestment(),
				measure.getLifetime(), measure.getInternalMaintenance(), measure.getExternalMaintenance(), measure.getRecurrentInvestment());

		// return calculated cost
		return cost;
	}

	public static Phase findPhaseById(int id, Analysis analysis) {
		return analysis.getPhases().stream().filter(p -> p.getId() == id).findAny().orElse(null);
	}

	public static Phase findLastPhase(Analysis analysis) {
		return analysis.getPhases().stream().max((p1, p2) -> Integer.compare(p1.getNumber(), p2.getNumber())).orElse(null);
	}

	public static Phase findPreviousPhase(Phase phase, Analysis analysis) {
		return phase.getNumber() <= 1 ? null : findPhaseByNumber(phase.getNumber() - 1, analysis);
	}

	public static Phase findPhaseByNumber(final int number, Analysis analysis) {
		return analysis.getPhases().stream().filter(p -> p.getNumber() == number).findAny().orElse(null);
	}
}
