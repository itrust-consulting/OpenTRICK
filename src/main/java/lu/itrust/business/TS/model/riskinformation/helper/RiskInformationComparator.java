package lu.itrust.business.TS.model.riskinformation.helper;

import java.util.Comparator;

import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;

public class RiskInformationComparator implements Comparator<RiskInformation>, NaturalOrderComparator<RiskInformation> {

	@Override
	public int compare(RiskInformation o1, RiskInformation o2) {
		if (o1.getCategory().equals(o2.getCategory()))
			return NaturalOrderComparator.compareTo(o1.getChapter(), o2.getChapter());
		else
			return comprareCategory(o2.getCategory(), o1.getCategory());
	}

	private int comprareCategory(String category1, String category2) {
		if (category1.equalsIgnoreCase("Threat"))
			return 1;
		else if (category2.equalsIgnoreCase("Threat"))
			return -1;
		else if (category1.equalsIgnoreCase("Vul"))
			return 1;
		else if (category2.equalsIgnoreCase("Vul"))
			return -1;
		else if (category1.equalsIgnoreCase("Risk_TBS"))
			return 1;
		else
			return -1;
	}

}
