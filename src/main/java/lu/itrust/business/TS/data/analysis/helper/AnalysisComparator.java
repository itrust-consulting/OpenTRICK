/**
 * 
 */
package lu.itrust.business.TS.data.analysis.helper;

import java.util.Comparator;

import lu.itrust.business.TS.component.GeneralComperator;
import lu.itrust.business.TS.data.analysis.Analysis;

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
			if(test == 0)
				return GeneralComperator.VersionComparator(o1.getVersion(), o2.getVersion());
		}
		return test;
	}

}
