package lu.itrust.business.TS.component;

import java.util.Comparator;

import lu.itrust.business.TS.data.basic.Asset;

public class AleComparator implements Comparator<Asset> {

	@Override
	public int compare(Asset o1, Asset o2) {
		int comp = Double.compare(o1.getALE(), o2.getALE());
		return comp == 0 ? Double.compare(o1.getALEO(), o2.getALEO()) == 0 ? Double
				.compare(o1.getALEP(), o2.getALEP()) == 0 ? o1.getName()
				.compareTo(o2.getName()) : Double.compare(o1.getALEP(),
				o2.getALEP()) : Double.compare(o1.getALEO(), o2.getALEO())
				: comp;
	}
}
