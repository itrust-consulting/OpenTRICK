/**
 * 
 */
package lu.itrust.business.component;

import java.util.Comparator;

import lu.itrust.business.TS.Measure;

/**
 * @author eomar
 *
 */
public class ComparatorMeasureReferance implements Comparator<Measure> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Measure o1, Measure o2) {
		if(!o1.getAnalysisNorm().getNorm().equals(o2.getAnalysisNorm().getNorm()))
			return o1.getAnalysisNorm().getNorm().getLabel().compareToIgnoreCase(o2.getAnalysisNorm().getNorm().getLabel());
		return compare(o1.getMeasureDescription().getReference(), o2.getMeasureDescription().getReference());
	}
	
	private int compare(String reference1, String reference2){
		String [] values1 = reference1.split("\\.", 2);
		String [] values2 = reference2.split("\\.", 2);
		int value1 = toInt(values1[0]);
		int value2 = toInt(values2[0]);
		if(value1==value2){
			if(values1.length == 1 && values2.length==1)
				return 0;
			else if(values1.length==1 && values2.length>1)
				return -1;
			else if(values1.length>1 && values2.length==1)
				return 1;
			else return compare(values1[1], values2[1]);
		}
		else 
			return Integer.compare(value1, value2);
	}
	
	private int toInt(String value){
		try{
			return Integer.parseInt(value);
		}catch(NumberFormatException e){
			return 0;
		}
	}

}
