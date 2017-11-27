/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lu.itrust.business.TS.database.service.ServiceMessageNotifier;
import lu.itrust.business.TS.model.general.helper.Notification;

/**
 * @author eomar
 *
 */
@Service
public class ServiceMessageNotifierImpl implements ServiceMessageNotifier, Serializable {

	private static final String ALL_USER_KEY = "---0-0-0-ALL-USER-123-AZERDFXF=NO-2-~&+ASCXYTS---][aqwlsodddfkj,cc";

	@Autowired
	private SessionRegistry sessionRegistry;

	private Map<String, Notification> notifications = Collections.synchronizedMap(new LinkedHashMap<>());

	private Map<String, List<String>> usernameToIds = Collections.synchronizedMap(new LinkedHashMap<>());

	private Map<String, List<String>> idToUsernames = Collections.synchronizedMap(new LinkedHashMap<>());

	@Autowired(required = false)
	private SimpMessagingTemplate messagingTemplate;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMessageNotifier#clear()
	 */
	@Override
	public void clear() {
		synchronized (this) {
			usernameToIds.values().parallelStream().forEach(Collection::clear);
			idToUsernames.values().parallelStream().forEach(Collection::clear);
			usernameToIds.clear();
			idToUsernames.clear();
			notifications.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.ServiceMessageNotifier#remove(java.
	 * lang.String)
	 */
	@Override
	public void remove(String id) {
		List<String> usernames = idToUsernames.remove(id);
		if (usernames != null)
			usernames.parallelStream().forEach(username -> usernameToIds.getOrDefault(username, Collections.emptyList()).remove(id));
		internalRemove(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.ServiceMessageNotifier#notifyAll(lu.
	 * itrust.business.TS.model.general.helper.Notification)
	 */
	@Override
	public void notifyAll(Notification notification) {
		notifyConnected(notification);
		notifyUser(ALL_USER_KEY, notification, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.ServiceMessageNotifier#notifyConnected
	 * (lu.itrust.business.TS.model.general.helper.Notification)
	 */
	@Override
	public void notifyConnected(Notification notification) {
		register(notification);
		sessionRegistry.getAllPrincipals().parallelStream().filter(principal -> principal instanceof Principal).map(principal -> (Principal) principal)
				.forEach(principal -> notifyUser(principal.getName(), notification, false));

	}

	private void notifyUser(String username, Notification notification, boolean save) {
		if (save)
			register(notification);
		List<String> ids = usernameToIds.get(username);
		if (ids == null)
			usernameToIds.put(username, ids = new LinkedList<>());
		ids.add(notification.getId());

		List<String> usernames = idToUsernames.get(notification.getId());
		if (usernames == null)
			idToUsernames.put(notification.getId(), usernames = new LinkedList<>());

		usernames.add(username);

		if (!(messagingTemplate == null || username.equals(ALL_USER_KEY)))
			messagingTemplate.convertAndSendToUser(username, "/Info/Notification", notification);
	}

	private void register(Notification notification) {
		notifications.put(notification.getId(), notification);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.ServiceMessageNotifier#notifyUser(java
	 * .lang.String, lu.itrust.business.TS.model.general.helper.Notification)
	 */
	@Override
	public void notifyUser(String username, Notification notification) {
		notifyUser(username, notification, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMessageNotifier#
	 * findAllByUsername(java.lang.String)
	 */
	@Override
	public List<Notification> findAllByUsername(String username) {
		String name = StringUtils.isEmpty(username) || username.equals(ALL_USER_KEY) ? ALL_USER_KEY : username;
		List<String> ids = new LinkedList<>();
		ids.addAll(usernameToIds.getOrDefault(name, Collections.emptyList()));
		ids.addAll(usernameToIds.getOrDefault(ALL_USER_KEY, Collections.emptyList()));
		
		List<Notification> notifications = ids.parallelStream().distinct().map(id -> this.notifications.get(id)).filter(notification -> notification != null)
				.collect(Collectors.toList());
		
		if (!name.equals(ALL_USER_KEY))
			notifications.parallelStream().filter(Notification::isOnce).forEach(notification -> remove(notification.getId(), name));
		
		return notifications;
	}

	@Override
	public void remove(String id, String username) {
		List<String> usernames = idToUsernames.getOrDefault(id, Collections.emptyList());
		usernames.remove(username);
		if (usernames.isEmpty())
			internalRemove(id);

	}

	private void internalRemove(String id) {
		notifications.remove(id);
		idToUsernames.remove(id);
	}

}
