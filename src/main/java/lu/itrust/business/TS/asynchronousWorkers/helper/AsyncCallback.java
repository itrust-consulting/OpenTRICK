/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eomar
 *
 */
public class AsyncCallback {

	private String action;

	private List<Object> args;

	/**
	 * 
	 */
	public AsyncCallback() {
	}

	/**
	 * @param action
	 * @param args
	 */
	public AsyncCallback(String action, List<Object> args) {
		this.action = action;
		this.args = args;
	}

	/**
	 * @param action
	 * @param args
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
	 * @return the args
	 */
	public List<Object> getArgs() {
		return args;
	}

	/**
	 * @param args
	 *            the args to set
	 */
	public void setArgs(List<Object> args) {
		this.args = args;
	}
}
