/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eomar
 *
 */
public class AsyncCallback {

	private String action;

	private List<String> args;

	/**
	 * 
	 */
	public AsyncCallback() {
	}

	/**
	 * @param action
	 * @param args
	 */
	public AsyncCallback(String action, List<String> args) {
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
			this.args = new ArrayList<String>(args.length);
			for (Object arg : args)
				this.args.add(String.valueOf(arg));
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
	public List<String> getArgs() {
		return args;
	}

	/**
	 * @param args
	 *            the args to set
	 */
	public void setArgs(List<String> args) {
		this.args = args;
	}
}
