package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.messagehandler.MessageHandler;

/**
 * ServiceTaskFeedback.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Feb 13, 2013
 */
public interface ServiceTaskFeedback {

	public int messageCount(String id);

	public boolean taskExist(String id);

	public boolean hasMessage(String id);

	public boolean userHasTask(String userName);

	public boolean hasTask(String userName, String id);

	public boolean registerTask(String userName, String id);

	public void unregisterTask(String userName, String id);

	public void send(MessageHandler handler);

	public void send(String id, MessageHandler handler);

	public void send(String userName, String id, MessageHandler handler);

	public MessageHandler recieveById(String id);

	public MessageHandler recieveLast(String id);

	public List<MessageHandler> recieve(String userName);

	public List<String> tasks(String userName);
}