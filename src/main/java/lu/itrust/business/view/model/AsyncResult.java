/**
 * 
 */
package lu.itrust.business.view.model;

import lu.itrust.business.TS.messagehandler.TaskName;

/**
 * @author eom
 * 
 */

public class AsyncResult {

	private Long taskID;

	private int flag = 0;

	private TaskName taskName;

	private String status;

	private String message;
	
	private AsyncCallback asyncCallback = null;

	private int progress = 0;

	/**
	 * @param taskID
	 */
	public AsyncResult(Long taskID) {
		this.taskID = taskID;
	}

	/**
	 * @param status
	 * @param taskID
	 */
	public AsyncResult(String status, Long taskID) {
		this.status = status;
		this.taskID = taskID;
	}

	/**
	 * @param taskName
	 * @param status
	 * @param taskID
	 * @param message
	 */
	public AsyncResult(TaskName taskName, String status, Long taskID, String message) {
		this.taskName = taskName;
		this.status = status;
		this.taskID = taskID;
		this.message = message;
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
	public Long getTaskID() {
		return taskID;
	}

	/**
	 * @param taskID
	 *            the taskID to set
	 */
	public void setTaskID(Long taskID) {
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
	public AsyncCallback getAsyncCallback() {
		return asyncCallback;
	}

	/**
	 * @param asyncCallback the asyncCallback to set
	 */
	public void setAsyncCallback(AsyncCallback asyncCallback) {
		this.asyncCallback = asyncCallback;
	}

}
