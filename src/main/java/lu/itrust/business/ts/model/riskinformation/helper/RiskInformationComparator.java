package lu.itrust.business.ts.model.riskinformation.helper;

import java.util.Comparator;

import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.riskinformation.RiskInformation;

/**
 * A comparator for comparing RiskInformation objects based on their category and chapter.
 * This comparator implements both the Comparator and NaturalOrderComparator interfaces.
 */
public class RiskInformationComparator implements Comparator<RiskInformation>, NaturalOrderComparator<RiskInformation> {

	/**
	 * Compares two RiskInformation objects based on their category and chapter.
	 * If the categories are the same, the chapters are compared using the NaturalOrderComparator.
	 * If the categories are different, the categories are compared using the comprareCategory method.
	 *
	 * @param o1 the first RiskInformation object to compare
	 * @param o2 the second RiskInformation object to compare
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second argument
	 */
	@Override
	public int compare(RiskInformation o1, RiskInformation o2) {
		if (o1.getCategory().equals(o2.getCategory()))
			return NaturalOrderComparator.compareTo(o1.getChapter(), o2.getChapter());
		else
			return comprareCategory(o2.getCategory(), o1.getCategory());
	}

	/**
	 * Compares two category strings and determines their order based on predefined rules.
	 *
	 * @param category1 the first category string to compare
	 * @param category2 the second category string to compare
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second argument
	 */
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
