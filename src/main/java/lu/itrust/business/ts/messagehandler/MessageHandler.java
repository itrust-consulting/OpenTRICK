package lu.itrust.business.ts.messagehandler;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.ts.exception.TrickException;

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

	private String idTask;

	private TaskStatus taskStatus;

	private TaskName taskName = null;

	private AsyncCallback [] asyncCallbacks = null;

	private String code = null;

	private Object[] parameters = null;

	private String message = null;

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
	public MessageHandler(String code, String message, Exception exception) {
		this.code = code;
		this.message = message;
		setException(exception);
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
	 */
	public MessageHandler(String code, Object[] parameters, String message, int progress) {
		this.code = code;
		this.parameters = parameters;
		this.message = message;
		this.progress = progress;
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

	public MessageHandler(String code, String message, int progress) {
		this.code = code;
		this.message = message;
		this.progress = progress;
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
	public String getIdTask() {
		return idTask;
	}

	/**
	 * @param idTask
	 *            the idTask to set
	 */
	public void setIdTask(String idTask) {
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
	public AsyncCallback [] getAsyncCallbacks() {
		return asyncCallbacks;
	}

	/**
	 * @param asyncCallback
	 *            the asyncCallback to set
	 */
	public void setAsyncCallbacks(AsyncCallback... asyncCallbacks) {
		this.asyncCallbacks = asyncCallbacks;
	}

	public void update(String code, String message, int progress) {
		this.parameters = null;
		this.asyncCallbacks = null;
		this.code = code;
		this.message = message;
		this.progress = progress;
	}

	public void update(String code, String message, int progress, Object... parameters) {
		this.asyncCallbacks = null;
		this.code = code;
		this.message = message;
		this.parameters = parameters;
		if (progress > this.progress)
			this.progress = progress;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageHandler [idTask=" + idTask + ", taskStatus=" + taskStatus + ", taskName=" + taskName + ", asyncCallbacks=" + asyncCallbacks + ", code=" + code
				+ ", parameters=" + Arrays.toString(parameters) + ", message=" + message + ", progress=" + progress + ", exception=" + exception + "]";
	}
}