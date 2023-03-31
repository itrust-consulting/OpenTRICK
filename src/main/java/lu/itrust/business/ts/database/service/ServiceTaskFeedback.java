package lu.itrust.business.ts.database.service;

import java.util.List;
import java.util.Locale;

import lu.itrust.business.ts.messagehandler.MessageHandler;

/**
 * ServiceTaskFeedback.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Feb 13, 2013
 */
public interface ServiceTaskFeedback {

	public boolean exists(String id);

	public boolean hasMessage(String id);

	public boolean userHasTask(String userName);

	public boolean hasTask(String userName, String id);

	public boolean registerTask(String userName, String id, Locale locale);

	public void unregisterTask(String userName, String id);

	public void send(MessageHandler handler);

	public void send(String id, MessageHandler handler);

	public void send(String userName, String id, MessageHandler handler, Locale locale);

	public MessageHandler recieveById(String id);

	public List<MessageHandler> recieve(String userName);

	public List<String> tasks(String userName);

	public String findUsernameById(String id);

	public void update(String username, Locale locale);

	boolean isWebSocketSupported(String username);

	void setWebSocketSupported(String username, boolean support);
}