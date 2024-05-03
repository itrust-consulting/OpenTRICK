/**
 * This class represents an asynchronous callback.
 */
package lu.itrust.business.ts.asynchronousWorkers.helper;

import java.util.ArrayList;
import java.util.List;

public class AsyncCallback {

	private String action;
	private List<Object> args;

	/**
	 * Default constructor for AsyncCallback.
	 */
	public AsyncCallback() {
	}

	/**
	 * Constructor for AsyncCallback with action and args.
	 *
	 * @param action the action to be performed
	 * @param args   the arguments for the action
	 */
	public AsyncCallback(String action, List<Object> args) {
		this.action = action;
		this.args = args;
	}

	/**
	 * Constructor for AsyncCallback with action and variable number of args.
	 *
	 * @param action the action to be performed
	 * @param args   the arguments for the action
	 */
	public AsyncCallback(String action, Object... args) {
		this.action = action;
		if (args.length > 0) {
			this.args = new ArrayList<Object>(args.length);
			for (Object arg : args)
				this.args.add(arg);
		}
	}

	/**
	 * Get the action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Set the action.
	 *
	 * @param action the action to be set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Get the arguments.
	 *
	 * @return the arguments
	 */
	public List<Object> getArgs() {
		return args;
	}

	/**
	 * Set the arguments.
	 *
	 * @param args the arguments to be set
	 */
	public void setArgs(List<Object> args) {
		this.args = args;
	}
}
