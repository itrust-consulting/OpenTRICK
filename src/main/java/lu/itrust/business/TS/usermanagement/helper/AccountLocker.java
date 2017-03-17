/**
 * 
 */
package lu.itrust.business.TS.usermanagement.helper;

import java.io.Serializable;

/**
 * @author eomar
 *
 */
public class AccountLocker implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean locked = false;

	private int attemption = 0;

	private long lastAttemption = System.currentTimeMillis();

	private long lockTime = 0;

	/**
	 * 
	 */
	public AccountLocker() {

	}

	/**
	 * @return the locked
	 */
	public boolean isLocked() {
		if (locked) {
			if (lockTime > System.currentTimeMillis())
				return true;
			else
				unlocked();
		}
		return locked;
	}

	private void unlocked() {
		this.locked = false;
		this.lockTime = 0;
		clearAttemption();
	}

	/**
	 * @param locked
	 *            the locked to set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * @return the attemption
	 */
	public int getAttemption() {
		return attemption;
	}

	/**
	 * @param attemption
	 *            the attemption to set
	 */
	public void setAttemption(int attemption) {
		this.attemption = attemption;
	}

	/**
	 * @return the lastAttemption
	 */
	public long getLastAttemption() {
		return lastAttemption;
	}

	/**
	 * @param lastAttemption
	 *            the lastAttemption to set
	 */
	public void setLastAttemption(long lastAttemption) {
		this.lastAttemption = lastAttemption;
	}

	/**
	 * @return the lockTime
	 */
	public long getLockTime() {
		return lockTime;
	}

	/**
	 * @param lockTime
	 *            the lockTime to set
	 */
	public void setLockTime(long lockTime) {
		this.lockTime = lockTime;
	}

	public void lockedFor(long lockTime) {
		setLockTime(lockTime + System.currentTimeMillis());
		this.setLocked(true);
		clearAttemption();
	}

	public synchronized void attempts(long lockTime) {
		if (isLocked())
			return;
		if (this.attemption < 2) {
			this.attemption++;
			this.lastAttemption = System.currentTimeMillis();
		} else
			lockedFor(lockTime);
	}

	private void clearAttemption() {
		setAttemption(0);
		setLastAttemption(0);
	}

}
