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
	
	private long idTask = 0;
	
	private String code = null;
	
	private Object [] parameters = null;
	
	private String message = null;
	
	/** The Exception */
	
	private Exception exception = null;

	public MessageHandler(Exception e) {
		this.exception = e;
	}
	
	/**
	 * @param code
	 * @param message
	 * @param exception
	 */
	public MessageHandler(String code, String message, Exception exception) {
		this.code = code;
		this.message = message;
		this.exception = exception;
	}

	/**
	 * @param code
	 * @param message
	 */
	public MessageHandler(String code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * @param code
	 * @param parameters
	 * @param message
	 */
	public MessageHandler(String code, Object[] parameters, String message) {
		this.code = code;
		this.parameters = parameters;
		this.message = message;
	}

	/**
	 * @param code
	 * @param parameters
	 * @param message
	 * @param exception
	 */
	public MessageHandler(String code, Object[] parameters, String message,
			Exception exception) {
		this.code = code;
		this.parameters = parameters;
		this.message = message;
		this.exception = exception;
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

	/**
	 * @return the parameters
	 */
	public Object [] getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Object [] parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the idTask
	 */
	public long getIdTask() {
		return idTask;
	}

	/**
	 * @param idTask the idTask to set
	 */
	public void setIdTask(long idTask) {
		this.idTask = idTask;
	}
}