/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.service.AccountLockerManager;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.usermanagement.helper.AccountLocker;

/**
 * @author eomar
 *
 */
@Component
public class AccountLockerManagerImpl implements AccountLockerManager, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Value("${app.settings.otp.lock.time}")
	private long lockTime;

	private Map<String, AccountLocker> lockedUsers = new LinkedHashMap<>();

	/**
	 * 
	 */
	public AccountLockerManagerImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.AccountLockerManager#isLocked(java
	 * .lang.String)
	 */
	@Override
	public boolean isLocked(String username) {
		AccountLocker locker = lockedUsers.get(username);
		return locker != null && locker.isLocked();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.AccountLockerManager#setLocked(
	 * java.lang.String)
	 */
	@Override
	public AccountLocker lock(String username) {
		AccountLocker accountLocker = lockedUsers.get(username);
		if (accountLocker == null) {
			synchronized (lockedUsers) {
				accountLocker = lockedUsers.get(username);
				if (accountLocker == null)
					lockedUsers.put(username, accountLocker = new AccountLocker());
			}
		}
		if (accountLocker.isLocked())
			return accountLocker;
		accountLocker.attempts(lockTime);
		if (accountLocker.isLocked()) {
			long lockMinute = lockTime / 60000;
			String[] names = username.split(AccountLockerManager.SEPRARATOR);
			if (names.length == 2)
				TrickLogManager.Persist(LogLevel.WARNING, "error.user.account.ip.locked",
						String.format("%s account is locked for %d minutes from %s", names[0], lockMinute, names[1]), names[0], LogAction.LOCK_ACCOUNT, names[0], lockMinute + "",
						names[1]);
			else
				TrickLogManager.Persist(LogLevel.WARNING, "error.user.account.locked", String.format("%s account is locked for %d minutes", username, lockMinute), username,
						LogAction.LOCK_ACCOUNT, username, lockMinute + "");
		}
		return accountLocker;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.service.AccountLockerManager#clean()
	 */
	@Scheduled(initialDelay = 900000, fixedDelay = 600000)
	@Override
	public void clean() {
		long currentTime = System.currentTimeMillis();
		if (!lockedUsers.isEmpty()) {
			synchronized (lockedUsers) {
				if (!lockedUsers.isEmpty())
					lockedUsers.entrySet().removeIf(entry -> (entry.getValue().getLastAttemption() + lockTime) < currentTime && entry.getValue().getLockTime() < currentTime);
			}
		}
	}

	@Override
	public boolean isLocked(String username, String ip) {
		return isLocked(AccountLockerManager.keyLock(username, ip));
	}

	@Override
	public AccountLocker lock(String username, String ip) {
		return lock(AccountLockerManager.keyLock(username, ip));
	}

	@Override
	public void clean(String username, String ip) {
		lockedUsers.remove(AccountLockerManager.keyLock(username, ip));
	}
}
