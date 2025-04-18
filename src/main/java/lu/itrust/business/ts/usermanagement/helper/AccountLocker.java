/**
 * 
 */
package lu.itrust.business.ts.usermanagement.helper;

import java.io.Serializable;

import lu.itrust.business.ts.constants.Constant;

/**
 * The AccountLocker class represents a lock mechanism for user accounts.
 * It keeps track of the lock status, number of attempts, last attempt time, and lock duration.
 */
public class AccountLocker implements Serializable {

	/**
	 * The serial version UID for serialization.
	 */
	private static final long serialVersionUID = 1L;

	private boolean locked = false; // Flag indicating if the account is locked
	private int attempts = 0; // Number of login attempts
	private long lastAttempt = System.currentTimeMillis(); // Time of the last login attempt
	private long lockTime = 0; // Duration of the lock in milliseconds

	/**
	 * Constructs a new AccountLocker object.
	 */
	public AccountLocker() {

	}

	/**
	 * Checks if the account is locked.
	 * If the account is locked and the lock duration has not expired, returns true.
	 * Otherwise, unlocks the account and returns the lock status.
	 *
	 * @return true if the account is locked, false otherwise
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

	/**
	 * Unlocks the account and resets the lock duration and login attempts.
	 */
	private void unlocked() {
		this.locked = false;
		this.lockTime = 0;
		clearAttempts();
	}

	/**
	 * Sets the lock status of the account.
	 *
	 * @param locked the lock status to set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * Gets the number of login attempts.
	 *
	 * @return the number of login attempts
	 */
	public int getAttempts() {
		return attempts;
	}

	/**
	 * Sets the number of login attempts.
	 *
	 * @param attempts the number of login attempts to set
	 */
	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	/**
	 * Gets the time of the last login attempt.
	 *
	 * @return the time of the last login attempt
	 */
	public long getLastAttempt() {
		return lastAttempt;
	}

	/**
	 * Sets the time of the last login attempt.
	 *
	 * @param lastAttempt the time of the last login attempt to set
	 */
	public void setLastAttempt(long lastAttempt) {
		this.lastAttempt = lastAttempt;
	}

	/**
	 * Gets the lock duration.
	 *
	 * @return the lock duration in milliseconds
	 */
	public long getLockTime() {
		return lockTime;
	}

	/**
	 * Sets the lock duration.
	 *
	 * @param lockTime the lock duration to set in milliseconds
	 */
	public void setLockTime(long lockTime) {
		this.lockTime = lockTime;
	}

	/**
	 * Locks the account for the specified duration.
	 * Sets the lock status to true and clears the login attempts.
	 *
	 * @param lockTime the duration to lock the account in milliseconds
	 */
	public void lockedFor(long lockTime) {
		setLockTime(lockTime + System.currentTimeMillis());
		this.setLocked(true);
		clearAttempts();
	}

	/**
	 * Increments the login attempts and updates the last attempt time.
	 * If the maximum number of attempts is reached, locks the account.
	 *
	 * @param lockTime the duration to lock the account in milliseconds
	 */
	public synchronized void attempts(long lockTime) {
		if (isLocked())
			return;
		if (this.attempts < (Constant.APPLICATION_SECURITY_MAX_ATTEMPTS - 1)) {
			this.attempts++;
			this.lastAttempt = System.currentTimeMillis();
		} else
			lockedFor(lockTime);
	}

	/**
	 * Clears the login attempts and last attempt time.
	 */
	private void clearAttempts() {
		setAttempts(0);
		setLastAttempt(0);
	}

}
