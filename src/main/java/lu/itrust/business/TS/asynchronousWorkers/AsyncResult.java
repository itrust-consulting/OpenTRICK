/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import lu.itrust.business.TS.messagehandler.TaskName;

/**
 * @author eom
 * 
 */

public class AsyncResult {

	private String taskID;

	private int flag = 0;

	private String name;

	private String action;

	private String status;

	private String message;

	private AsyncCallback [] asyncCallbacks = null;

	private int progress = 0;

	/**
	 * @param id
	 */
	public AsyncResult(String id) {
		this.taskID = id;
	}

	/**
	 * @param status
	 * @param taskID
	 */
	public AsyncResult(String status, String taskID) {
		this.status = status;
		this.taskID = taskID;
	}

	/**
	 * @param taskName
	 * @param status
	 * @param taskID
	 * @param message
	 */
	public AsyncResult(TaskName taskName, String status, String taskID, String message) {
		setTaskName(taskName);
		this.status = status;
		this.taskID = taskID;
		this.message = message;

	}

	/**
	 * @param taskName
	 *            the taskName to set
	 */
	public void setTaskName(TaskName taskName) {
		if (taskName != null) {
			setName(taskName.getName());
			setAction(taskName.getAction());
		}
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the taskID
	 */
	public String getTaskID() {
		return taskID;
	}

	/**
	 * @param taskID
	 *            the taskID to set
	 */
	public void setTaskID(String taskID) {
		this.taskID = taskID;
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
	 * @return the flag
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(int flag) {
		this.flag = flag;
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

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
