/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.helper;

import java.util.Comparator;

import lu.itrust.business.TS.model.ticketing.TickectingComment;

/**
 * @author eomar
 *
 */
public class CommentComparator implements Comparator<TickectingComment> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(TickectingComment o1, TickectingComment o2) {
		return  o1.getCreated().compareTo(o2.getCreated());
	}

}
