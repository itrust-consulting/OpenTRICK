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

	public int messageCount(long id);

	public boolean taskExist(long id);

	public boolean hasMessage(long id);

	public boolean userHasTask(String userName);

	public boolean hasTask(String userName, long id);

	public boolean registerTask(String userName, long id);

	public void unregisterTask(String userName, long id);

	public void send(MessageHandler handler);

	public void send(long id, MessageHandler handler);

	public void send(String userName, long id, MessageHandler handler);

	public MessageHandler recieve(long id);

	public MessageHandler recieveLast(long id);

	public List<MessageHandler> recieve(String userName);

	public List<Long> tasks(String userName);
}