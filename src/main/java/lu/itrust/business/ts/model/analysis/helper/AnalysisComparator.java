/**
 * 
 */
package lu.itrust.business.ts.model.analysis.helper;

import java.util.Comparator;

import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.analysis.Analysis;

/**
 * @author eomar
 *
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
