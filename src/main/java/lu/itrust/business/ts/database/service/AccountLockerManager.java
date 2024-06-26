/**
 * 
 */
package lu.itrust.business.ts.database.service;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

import lu.itrust.business.ts.usermanagement.helper.AccountLocker;

/**
 * @author eomar
 *
 */
public interface AccountLockerManager {

	public static final String SEPRARATOR = "_--IP--_";

	boolean isLocked(String username);

	AccountLocker lock(String username);

	boolean isLocked(String username, String ip);

	AccountLocker lock(String username, String ip);

	public static String keyLock(String username, String ip) {
		return StringUtils.hasText(ip) ?  String.format("%s%s%s", username, SEPRARATOR, ip) :  username;
	}

	public static String getIP(HttpServletRequest request) {
		String remoteaddr = request.getHeader("X-FORWARDED-FOR");
		if (remoteaddr == null)
			remoteaddr = request.getRemoteAddr();
		

		return remoteaddr == null? remoteaddr : remoteaddr.replaceAll("[\\[\\]]", "");
	}

	void clean();
	
	void unlock(String code);

	void clean(String username, String ip);
}
