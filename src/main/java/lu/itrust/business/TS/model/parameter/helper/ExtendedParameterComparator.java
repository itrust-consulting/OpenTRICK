/**
 * 
 */
package lu.itrust.business.TS.model.parameter.helper;

import java.util.Comparator;

import lu.itrust.business.TS.model.parameter.ExtendedParameter;

/**
 * @author eomar
 *
 */
public class ExtendedParameterComparator implements Comparator<ExtendedParameter> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(ExtendedParameter o1, ExtendedParameter o2) {
		return Integer.compare(o1.getLevel(), o2.getLevel());
	}
}
