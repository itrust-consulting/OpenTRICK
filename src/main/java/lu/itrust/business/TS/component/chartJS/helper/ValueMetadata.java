/**
 * 
 */
package lu.itrust.business.TS.component.chartJS.helper;

/**
 * @author eomar
 *
 */
public class ValueMetadata<T> {
	
	private T old;
	
	private T next;

	/**
	 * 
	 */
	public ValueMetadata() {
	}
	
	/**
	 * @param old
	 * @param next
	 */
	public ValueMetadata(T old, T next) {
		this.setOld(old);
		this.setNext(next);
	}

	/**
	 * @return the old
	 */
	public T getOld() {
		return old;
	}

	/**
	 * @param old the old to set
	 */
	public void setOld(T old) {
		this.old = old;
	}

	/**
	 * @return the next
	 */
	public T getNext() {
		return next;
	}

	/**
	 * @param next the next to set
	 */
	public void setNext(T next) {
		this.next = next;
	}

	
}
