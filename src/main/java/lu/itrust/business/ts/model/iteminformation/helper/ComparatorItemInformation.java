/**
 * 
 */
package lu.itrust.business.ts.model.iteminformation.helper;

import java.util.Comparator;

import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.iteminformation.ItemInformation;

/**
 * A comparator for comparing ItemInformation objects based on their
 * description.
 */
public class ComparatorItemInformation implements Comparator<ItemInformation> {

	/**
	 * Compares two ItemInformation objects based on the weight of their
	 * descriptions.
	 *
	 * @param o1 the first ItemInformation object
	 * @param o2 the second ItemInformation object
	 * @return a negative integer if o1's description has a lower weight than o2's
	 *         description,
	 *         zero if both descriptions have the same weight,
	 *         a positive integer if o1's description has a higher weight than o2's
	 *         description
	 */
	@Override
	public int compare(ItemInformation o1, ItemInformation o2) {
		var result = Integer.compare(WeigthOf(o1.getDescription()), WeigthOf(o2.getDescription()));
		return result == 0 ? NaturalOrderComparator.compareTo(o1.getDescription(), o2.getDescription()) : result;
	}

	/**
	 * Calculates the weight of a given value.
	 *
	 * @param value the value for which to calculate the weight
	 * @return the weight of the value
	 */
	public static int WeigthOf(String value) {
		switch (value) {
			case "type_organism":
				return -25;
			case "type_profit_organism":
				return -24;
			case "name_organism":
				return -23;
			case "presentation_organism":
				return -22;
			case "sector_organism":
				return -21;
			case "responsible_organism":
				return -20;
			case "staff_organism":
				return -19;
			case "activities_organism":
				return -18;
			case "occupation":
				return -17;
			case "juridic":
				return -16;
			case "pol_organisation":
				return -15;
			case "management_organisation":
				return -14;
			case "premises":
				return -13;
			case "requirements":
				return -12;
			case "stakeholder_identification":
				return -11;
			case "stakeholder_relation":
				return -10;
			case "expectations":
				return -9;
			case "environment":
				return -8;
			case "interface":
				return -7;
			case "role_responsability":
				return -6;
			case "escalation_way":
				return -5;
			case "processus_development":
				return -4;
			case "document_conserve":
				return -3;
			case "excluded_assets":
				return -2;
			case "functional":
				return -1;
			case "strategic":
				return 0;
			case "financialParameters", "financialparameters":
				return 1;
			case "riskEvaluationCriteria", "riskevaluationcriteria":
				return 2;
			case "impactCriteria", "impactcriteria":
				return 3;
			case "riskAcceptanceCriteria", "riskacceptancecriteria":
				return 4;
			default:
				return 5;
		}
	}
}
