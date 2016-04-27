/**
 * 
 */
package lu.itrust.business.TS.model.cssf.helper;

import java.util.Comparator;

import lu.itrust.business.TS.model.cssf.RiskProfile;

/**
 * @author eomar
 *
 */
public class RiskRegisterExportComparator implements Comparator<RiskProfile> {

	@Override
	public int compare(RiskProfile o1, RiskProfile o2) {
		if (o1.getIdentifier() == null || o1.getIdentifier().isEmpty()) {
			if (o2.getIdentifier() == null || o2.getIdentifier().isEmpty())
				return 0;
			return -1;
		} else if (o2.getIdentifier() == null)
			return 1;
		return o1.getIdentifier().compareTo(o2.getIdentifier());
	}
}
