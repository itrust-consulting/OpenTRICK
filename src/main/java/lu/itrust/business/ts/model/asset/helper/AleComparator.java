package lu.itrust.business.ts.model.asset.helper;

import java.util.Comparator;

import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.asset.Asset;

/**
 * This class implements the Comparator interface to compare Asset objects based on their ALE (Annual Loss Expectancy) values.
 * It compares the ALE values first, and if they are equal, it compares the ALEO (Annual Loss Expectancy Original) values.
 * If the ALEO values are also equal, it compares the ALEP (Annual Loss Expectancy Potential) values.
 * If the ALEP values are also equal, it compares the names of the assets using the NaturalOrderComparator.
 */
public class AleComparator implements Comparator<Asset> {

	@Override
	public int compare(Asset o1, Asset o2) {
		int comp = Double.compare(o1.getALE(), o2.getALE());
		return comp == 0 ? Double.compare(o1.getALEO(), o2.getALEO()) == 0
				? Double.compare(o1.getALEP(), o2.getALEP()) == 0 ? NaturalOrderComparator.compareTo(o1.getName(), o2.getName()) : Double.compare(o1.getALEP(), o2.getALEP())
				: Double.compare(o1.getALEO(), o2.getALEO()) : comp;
	}
}
