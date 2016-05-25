package lu.itrust.business.TS.model.cssf.tools;

import java.util.Comparator;

import lu.itrust.business.TS.model.cssf.RiskRegisterItemGroup;

/**
 * NetImportanceComparatorDescending: <br>
 * Compare two RiskRegisterItem on based on importance net. Priority is on the
 * net importance. This class is used inside the Collections class as algorithm
 * to decide on Descending comparation.
 * 
 * @author itrust consulting s.�.rl. : BJA, EOM, SME
 * @version 0.1
 * @since 27 d�c. 2012
 */
public class NetImportanceRegisterGroupComparator implements Comparator<RiskRegisterItemGroup> {

	/**
	 * compare: <br>
	 * Compare net importance of 2 RiskRegisterItems.
	 * 
	 * @param o1
	 *            the first object
	 * @param o2
	 *            the second object
	 * 
	 * @return 0 if the two net importances are equal; -1 if first object
	 *         importance is less than the second or 1 if second is less than
	 *         first
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(RiskRegisterItemGroup o1, RiskRegisterItemGroup o2) {
		int result = Double.compare(o1.getNetImportance(), o2.getNetImportance());
		if (result == 0) {
			result = Double.compare(o1.getExpectedImportance(), o2.getExpectedImportance());
			if (result == 0) {
				result = Double.compare(o1.getRawImportance(), o2.getRawImportance());
				if (result == 0 && !(o1.isEmpty() || o2.isEmpty()))
					result = String.CASE_INSENSITIVE_ORDER.compare(o2.get(0).getScenario().getName(),o1.get(0).getScenario().getName());//inverse order
			}
		}
		return result;
	}
}