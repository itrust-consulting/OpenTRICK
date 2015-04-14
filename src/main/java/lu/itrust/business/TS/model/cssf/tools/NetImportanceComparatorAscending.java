package lu.itrust.business.TS.model.cssf.tools;

import lu.itrust.business.TS.model.cssf.RiskRegisterItem;

/** 
 * NetImportanceComparatorAscending: <br>
 * Compare two RiskRegisterItem on based on importance net. Priority is on the net importance.
 * This class is used inside the Collections class as algorithm to decide on Ascending comparation.
 *
 * @author itrust consulting s.�.rl. : BJA, EOM, SME
 * @version 0.1
 * @since 27 d�c. 2012
 */
public class NetImportanceComparatorAscending extends RiskRegisterItemComparator {

	/**
	 * compare: <br>
	 * Compare net importance of 2 RiskRegisterItems.
	 * 
	 * @param o1
	 *            the first object
	 * @param o2
	 *            the second object
	 * 
	 * @return 0 if the two net importances are equal; 1 if first object importance is less than
	 *         the second or -1 if second is less than first
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(RiskRegisterItem o1, RiskRegisterItem o2) {

		// retrieve importance value of first item
		double value1 = o1.getNetEvaluation().getImportance();

		// retrieve importance value of second item
		double value2 = o2.getNetEvaluation().getImportance();
		
		// perform checks on the 2 values
		return Double.compare(value2, value1);
	}
}