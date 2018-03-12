/**
 * 
 */
package lu.itrust.business.TS.helper;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author eomar
 *
 */
public class Result {

	private boolean error = false;

	private String message = null;

	private Set<FieldValue> fields = new LinkedHashSet<FieldValue>();

	/**
	 * @param hasError
	 * @param message
	 */
	protected Result(boolean error, String message) {
		setError(error);
		setMessage(message);
	}

	/**
	 * @return the error
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * @param error
	 *            the error to set
	 */
	protected void setError(boolean error) {
		this.error = error;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	public void turnOnError(String message) {
		setError(true);
		setMessage(message);
	}

	public void turnOnSuccess(String message) {
		setError(true);
		setMessage(message);
	}

	/**
	 * @return the fields
	 */
	public Set<FieldValue> getFields() {
		return fields;
	}

	/**
	 * @param fields
	 *            the fields to set
	 */
	public void setFields(Set<FieldValue> fields) {
		this.fields = fields;
	}

	/**
	 * @return
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		return fields.isEmpty();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return fields.contains(o);
	}

	/**
	 * @param e
	 * @return
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(FieldValue e) {
		return fields.add(e);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return fields.remove(o);
	}

	public static Result Error(String message) {
		return new Result(true, message);
	}

	public static Result Success(String message) {
		return new Result(false, message);
	}

}
