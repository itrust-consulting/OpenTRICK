/**
 * 
 */
package lu.itrust.business.TS.model.iteminformation.helper;

import java.util.Comparator;

import lu.itrust.business.TS.model.iteminformation.ItemInformation;

/**
 * @author eomar
 *
 */
public class ComparatorItemInformation implements Comparator<ItemInformation> {

	@Override
	public int compare(ItemInformation o1, ItemInformation o2) {
		return Integer.compare(WeigthOf(o1.getDescription()), WeigthOf(o2.getDescription()));
	}

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
		default:
			return 1;
		}

	}
}
