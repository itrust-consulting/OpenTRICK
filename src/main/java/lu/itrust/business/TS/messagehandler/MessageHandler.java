package lu.itrust.business.TS.messagehandler;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lu.itrust.business.TS.asynchronousWorkers.AsyncCallback;
import lu.itrust.business.TS.exception.TrickException;

/**
 * MessageHandler: <br>
 * Keeps information of a result and message used as return result in methods.
 * One can check the field exception on errors, if no error was made, the field
 * is null, on errors, the field contains the exception thrown.
 *
 * @author itrust consulting s.�.rl. : BJA, SME, EOM
 * @version 0.1
 * @since 10 janv. 2013
 */
public class MessageHandler {

	private long idTask = 0;

	private TaskStatus taskStatus;

	private TaskName taskName = null;

	private AsyncCallback asyncCallback = null;

	private String code = null;

	private Object[] parameters = null;

	private String message = null;

	private String language = null;
	
	private int progress = 0;

	/** The Exception */

	private Exception exception = null;
	
	public MessageHandler() {
	}

	public MessageHandler(Exception e) {
		setException(e);
	}

	/**
	 * @param code
	 * @param message
	 * @param exception
	 */
	public MessageHandler(String code, String message, String language, Exception exception) {
		this.code = code;
		this.message = message;
		this.language = language;
		setException(exception);
	}
	
	/**
	 * @param code
	 * @param message
	 */
	public MessageHandler(String code, String message, String language, int progress) {
		this.code = code;
		this.message = message;
		this.language = language;
		this.progress = progress;
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
	public MessageHandler(String code, Object[] parameters, String message, Exception exception) {
		this.code = code;
		this.parameters = parameters;
		this.message = message;
		setException(exception);
	}

	/**
	 * @param code
	 * @param parameters
	 * @param message
	 * @param exception
	 */
	public MessageHandler(String code, Object[] parameters, String message,String locale, Exception exception) {
		this.code = code;
		this.parameters = parameters;
		this.message = message;
		this.language = locale;
		setException(exception);
	}
	
	/** getLanguage: <br>
	 * Returns the language field value.
	 * 
	 * @return The value of the language field
	 */
	public String getLanguage() {
		return language;
	}

	/** setLanguage: <br>
	 * Sets the Field "language" with a value.
	 * 
	 * @param language 
	 * 			The Value to set the language field
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * getException: <br>
	 * Returns the exception field value.
	 * 
	 * @return The value of the exception field
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * setException: <br>
	 * Sets the Field "exception" with a value.
	 * 
	 * @param exception
	 *            The Value to set the exception field
	 */
	public void setException(Exception exception) {
		if (exception instanceof TrickException)
			setException((TrickException) exception);
		else
			this.exception = exception;
	}

	@JsonIgnore
	public void setException(TrickException e) {
		this.code = e.getCode();
		this.parameters = e.getParameters();
		this.message = e.getMessage();
		this.exception = e;
		this.exception = e;
	}

	/**
	 * @return the parameters
	 */
	public Object[] getParameters() {
		return parameters;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
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

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
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
	 * @param idTask
	 *            the idTask to set
	 */
	public void setIdTask(long idTask) {
		this.idTask = idTask;
	}

	/**
	 * @return the taskName
	 */
	public TaskName getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName
	 *            the taskName to set
	 */
	public void setTaskName(TaskName taskName) {
		this.taskName = taskName;
	}

	/**
	 * @return the taskStatus
	 */
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	/**
	 * @param taskStatus
	 *            the taskStatus to set
	 */
	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	/**
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @param progress
	 *            the progress to set
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * @return the asyncCallback
	 */
	public AsyncCallback getAsyncCallback() {
		return asyncCallback;
	}

	/**
	 * @param asyncCallback
	 *            the asyncCallback to set
	 */
	public void setAsyncCallback(AsyncCallback asyncCallback) {
		this.asyncCallback = asyncCallback;
	}
}