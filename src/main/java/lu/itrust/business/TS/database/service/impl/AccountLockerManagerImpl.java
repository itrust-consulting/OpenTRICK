/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Component;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.AccountLockerManager;
import lu.itrust.business.TS.database.service.ServiceEmailSender;
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

	private Map<String, String> unlockCodes = new LinkedHashMap<>();

	@Autowired
	private ServiceEmailSender serviceEmailSender;

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
			String key = new ShaPasswordEncoder(256).encodePassword(UUID.randomUUID().toString(), accountLocker.hashCode());
			if (names.length == 2) {
				TrickLogManager.Persist(LogLevel.WARNING, "error.user.account.ip.locked",
						String.format("%s account is locked for %d minutes from %s", names[0], lockMinute, names[1]), names[0], LogAction.LOCK_ACCOUNT, names[0], lockMinute + "",
						names[1]);
				serviceEmailSender.sendAccountLocked(key, names[1], accountLocker.getLockTime(), names[0]);
			} else {
				TrickLogManager.Persist(LogLevel.WARNING, "error.user.account.locked", String.format("%s account is locked for %d minutes", username, lockMinute), username,
						LogAction.LOCK_ACCOUNT, username, lockMinute + "");
				serviceEmailSender.sendAccountLocked(key, null, accountLocker.getLockTime(), names[0]);
			}
			unlockCodes.put(key, username);
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
			if (!unlockCodes.isEmpty()) {
				synchronized (unlockCodes) {
					if (!unlockCodes.isEmpty())
						unlockCodes.entrySet().removeIf(entry -> !lockedUsers.containsKey(entry.getValue()));
				}
			}
		} else
			unlockCodes.clear();

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
		String key = AccountLockerManager.keyLock(username, ip);
		if (!lockedUsers.isEmpty())
			lockedUsers.remove(key);
		if (!unlockCodes.isEmpty())
			unlockCodes.entrySet().removeIf(entry -> entry.getValue().equals(key));
	}

	@Value("${app.settings.max.attempt}")
	public void setAttemptionCount(int attemption) {
		Constant.APPLICATION_SECURITY_MAX_ATTEMPTION = attemption;
	}

	@Override
	public void unlock(String code) {
		String username = unlockCodes.remove(code);
		if (username != null) {
			AccountLocker locker = lockedUsers.get(username);
			if (locker != null)
				locker.setLockTime(System.currentTimeMillis());
		}
	}
}
