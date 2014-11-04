/**
 * 
 */
package lu.itrust.business.TS.data.standard.measuredescription.helper;

import java.util.Comparator;

import lu.itrust.business.TS.data.standard.measuredescription.MeasureDescription;

/**
 * @author eomar
 *
 */
public class ComparatorMeasureDescription implements Comparator<MeasureDescription> {

	@Override
	public int compare(MeasureDescription o1, MeasureDescription o2) {
		return Compare(o1.getReference(), o2.getReference());
	}
	
	public static int Compare(String reference1, String reference2){
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
			else return Compare(values1[1], values2[1]);
		}
		else 
			return Integer.compare(value1, value2);
	}
	
	private static int toInt(String value){
		try{
			return Integer.parseInt(value);
		}catch(NumberFormatException e){
			return 0;
		}
	}

}
