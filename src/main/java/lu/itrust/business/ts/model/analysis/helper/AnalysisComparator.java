/**
 * 
 */
package lu.itrust.business.ts.model.analysis.helper;

import java.util.Comparator;

import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.analysis.Analysis;


/**
 * This class implements the Comparator interface to compare Analysis objects.
 * It compares the Analysis objects based on the following criteria:
 * 1. Customer's organization name (case-insensitive)
 * 2. Analysis identifier (case-insensitive)
 * 3. Availability of data (true or false)
 * 4. Analysis type
 * 5. Analysis version (using NaturalOrderComparator)
 *
 * The compare() method returns a negative integer, zero, or a positive integer
 * as the first argument is less than, equal to, or greater than the second argument.
 */
public class AnalysisComparator implements Comparator<Analysis> {

	@Override
	public int compare(Analysis o1, Analysis o2) {
		int test = o1.getCustomer().getOrganisation().compareToIgnoreCase(o2.getCustomer().getOrganisation());
		if (test == 0) {
			test = o1.getIdentifier().compareToIgnoreCase(o2.getIdentifier());
			if (test == 0) {
				test = Boolean.compare(o1.hasData(), o2.hasData());
				if (test == 0) {
					test = o1.getType().compareTo(o2.getType());
					if (test == 0)
						return NaturalOrderComparator.compareTo(o1.getVersion(), o2.getVersion());
				}
			}
		}
		return test;
	}

}
