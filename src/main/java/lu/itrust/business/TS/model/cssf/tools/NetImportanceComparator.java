package lu.itrust.business.TS.model.cssf.tools;

import java.util.Comparator;

import lu.itrust.business.TS.component.NaturalOrderComparator;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;

/**
 * NetImportanceComparator: <br>
 * Compare two RiskRegisterItem on based on importance net. Priority is on the
 * net importance. This class is used inside the Collections class as algorithm
 * to decide on Ascending comparation.
 *
 * @author itrust consulting s.�.rl. : BJA, EOM, SME
 * @version 0.2
 * @since 27 d�c. 2012
 */
public class NetImportanceComparator implements Comparator<RiskRegisterItem> {

	/**
	 * compare: <br>
	 * Compare net importance of 2 RiskRegisterItems.
	 * 
	 * @param o1
	 *            the first object
	 * @param o2
	 *            the second object
	 * 
	 * @return 0 if the two net importances are equal; 1 if first object
	 *         importance is less than the second or -1 if second is less than
	 *         first
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(RiskRegisterItem o1, RiskRegisterItem o2) {

		int result = Double.compare(o1.getNetEvaluation().getImportance(), o2.getNetEvaluation().getImportance());
		if (result == 0) {
			result = Double.compare(o1.getExpectedEvaluation().getImportance(), o2.getExpectedEvaluation().getImportance());
			if (result == 0) {
				result = Double.compare(o1.getRawEvaluation().getImportance(), o2.getRawEvaluation().getImportance());
				if (result == 0) {
					result = NaturalOrderComparator.compareTo(o2.getScenario().getName(),o1.getScenario().getName());//inverse order
					if (result == 0) {
						result = NaturalOrderComparator.compareTo(o2.getAsset().getName(),o1.getAsset().getName());//inverse order
					}
				}
			}
		}
		// perform checks on the 2 values
		return result;
	}
}