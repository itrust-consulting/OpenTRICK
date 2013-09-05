package lu.itrust.business.TS.messagehandler;

/** 
 * MessageHandler: <br>
 * Keeps information of a result and message used as return result in methods. One can check the 
 * field exception on errors, if no error was made, the field is null, on errors, the field contains
 * the exception thrown.
 *
 * @author itrust consulting s.�.rl. : BJA, SME, EOM
 * @version 0.1
 * @since 10 janv. 2013
 */
public class MessageHandler {
	
	/** The Exception */
	private Exception exception = null;

	public MessageHandler(Exception e) {
		this.exception = e;
	}

	/** getException: <br>
	 * Returns the exception field value.
	 * 
	 * @return The value of the exception field
	 */
	public Exception getException() {
		return exception;
	}

	/** setException: <br>
	 * Sets the Field "exception" with a value.
	 * 
	 * @param exception 
	 * 			The Value to set the exception field
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}
}