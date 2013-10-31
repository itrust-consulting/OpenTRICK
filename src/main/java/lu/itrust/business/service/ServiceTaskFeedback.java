/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.messagehandler.MessageHandler;

/**
 * @author eom
 *
 */
public interface ServiceTaskFeedback {
	
	boolean taskExist(long id);
	
	boolean userHasTask(String userName);
	
	boolean hasTask(String userName, long id);
	
	boolean registerTask(String userName, long id);
	
	void deregisterTask(String userName, long id);
	
	void send(MessageHandler handler);
	
	void send(long id, MessageHandler handler);
	
	void send(String userName ,long id, MessageHandler handler);
	
	MessageHandler recive(long id);
	
	List<MessageHandler> recive(String userName);
	
	List<Long> tasks(String userName);
}
