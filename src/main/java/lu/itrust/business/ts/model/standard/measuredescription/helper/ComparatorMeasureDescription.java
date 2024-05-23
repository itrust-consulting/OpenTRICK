package lu.itrust.business.ts.model.standard.measuredescription.helper;

import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;

/**
 * This class implements the NaturalOrderComparator interface and provides a comparison method for MeasureDescription objects.
 * It compares the references of two MeasureDescription objects using a custom algorithm.
 */
public class ComparatorMeasureDescription implements NaturalOrderComparator<MeasureDescription> {

	/**
	 * Compares two MeasureDescription objects based on their references.
	 * The comparison is done using a custom algorithm that splits the references into parts and compares them numerically.
	 * @param o1 the first MeasureDescription object to compare
	 * @param o2 the second MeasureDescription object to compare
	 * @return a negative integer if o1 is less than o2, zero if they are equal, or a positive integer if o1 is greater than o2
	 */
	@Override
	public int compare(MeasureDescription o1, MeasureDescription o2) {
		return NaturalOrderComparator.compareTo(o1.getReference(), o2.getReference());
	}
	
	/**
	 * Compares two reference strings based on their numerical values.
	 * The comparison is done by splitting the strings into parts and comparing them numerically.
	 * @param reference1 the first reference string to compare
	 * @param reference2 the second reference string to compare
	 * @return a negative integer if reference1 is less than reference2, zero if they are equal, or a positive integer if reference1 is greater than reference2
	 */
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
	
	/**
	 * Converts a string value to an integer.
	 * If the conversion fails, it returns 0.
	 * @param value the string value to convert
	 * @return the integer value of the string, or 0 if the conversion fails
	 */
	private static int toInt(String value){
		try{
			return Integer.parseInt(value);
		}catch(NumberFormatException e){
			return 0;
		}
	}

}
