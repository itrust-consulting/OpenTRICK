/**
 * 
 */
package lu.itrust.business.TS.model.cssf.helper;

import lu.itrust.business.TS.component.NaturalOrderComparator;
import lu.itrust.business.TS.model.cssf.RiskProfile;

/**
 * @author eomar
 *
 */
public class RiskSheetExportComparator implements NaturalOrderComparator<RiskProfile> {

	@Override
	public int compare(RiskProfile o1, RiskProfile o2) {
		if (o1.getIdentifier() == null || o1.getIdentifier().isEmpty()) {
			if (o2.getIdentifier() == null || o2.getIdentifier().isEmpty())
				return 0;
			return -1;
		} else if (o2.getIdentifier() == null)
			return 1;
		return NaturalOrderComparator.compareTo(o1.getIdentifier(), o2.getIdentifier());
	}
}
