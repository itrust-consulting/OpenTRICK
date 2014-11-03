package lu.itrust.business.TS.database.service.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.messagehandler.MessageHandler;

import org.springframework.stereotype.Service;

/**
 * ServiceTaskFeedBackImpl.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.a.rl.
 * @version
 * @since Feb 13, 2013
 */
@Service
public class ServiceTaskFeedBackImpl implements ServiceTaskFeedback {

	private Map<String, List<Long>> userTasks = Collections.synchronizedMap(new LinkedHashMap<String, List<Long>>());

	private Map<Long, Queue<MessageHandler>> messageHandlers = Collections.synchronizedMap(new LinkedHashMap<Long, Queue<MessageHandler>>());

	/**
	 * messageCount: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#messageCount(long)
	 */
	@Override
	public int messageCount(long id) {
		Queue<MessageHandler> queue = messageHandlers.get(id);
		return queue == null ? 0 : queue.size();
	}

	/**
	 * taskExist: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#taskExist(long)
	 */
	@Override
	public boolean taskExist(long id) {
		return messageHandlers.containsKey(id);
	}

	/**
	 * hasMessage: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#hasMessage(long)
	 */
	@Override
	public boolean hasMessage(long id) {
		Queue<MessageHandler> queue = messageHandlers.get(id);
		return queue != null && !queue.isEmpty();
	}

	/**
	 * userHasTask: <br>
	 * Description
	 * 
	 * @param userName
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#userHasTask(java.lang.String)
	 */
	@Override
	public boolean userHasTask(String userName) {
		if (!userTasks.containsKey(userName))
			return false;
		List<Long> tasks = userTasks.get(userName);
		return tasks != null && !tasks.isEmpty();
	}

	/**
	 * hasTask: <br>
	 * Description
	 * 
	 * @param userName
	 * @param id
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#hasTask(java.lang.String, long)
	 */
	@Override
	public boolean hasTask(String userName, long id) {
		if (!userTasks.containsKey(userName))
			return false;
		for (Long task : userTasks.get(userName))
			if (id == task)
				return true;
		return false;
	}

	/**
	 * registerTask: <br>
	 * Description
	 * 
	 * @param userName
	 * @param id
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#registerTask(java.lang.String, long)
	 */
	@Override
	public boolean registerTask(String userName, long id) {
		if (messageHandlers.containsKey(id))
			return false;
		messageHandlers.put(id, new LinkedList<MessageHandler>());
		List<Long> tasks = userTasks.containsKey(userName) ? userTasks.get(userName) : Collections.synchronizedList(new LinkedList<Long>());
		tasks.add(id);
		if (!userTasks.containsKey(userName))
			userTasks.put(userName, tasks);
		return true;
	}

	/**
	 * unregisterTask: <br>
	 * Description
	 * 
	 * @param userName
	 * @param id
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#unregisterTask(java.lang.String, long)
	 */
	@Override
	public void unregisterTask(String userName, long id) {
		if (!userTasks.containsKey(userName))
			return;
		List<Long> tasks = userTasks.get(userName);
		if (tasks == null || tasks.isEmpty())
			return;
		tasks.remove(id);
		if (tasks.isEmpty())
			userTasks.remove(userName);
		if (messageHandlers.containsKey(id) && !messageHandlers.get(id).isEmpty())
			messageHandlers.get(id).clear();
		messageHandlers.remove(id);
	}

	/**
	 * send: <br>
	 * Description
	 * 
	 * @param handler
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#send(lu.itrust.business.TS.messagehandler.MessageHandler)
	 */
	@Override
	public void send(MessageHandler handler) {
		Queue<MessageHandler> queue = messageHandlers.get(handler.getIdTask());
		if (queue == null)
			return;
		queue.add(handler);
	}

	/**
	 * send: <br>
	 * Description
	 * 
	 * @param id
	 * @param handler
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#send(long,
	 *      lu.itrust.business.TS.messagehandler.MessageHandler)
	 */
	@Override
	public void send(long id, MessageHandler handler) {
		Queue<MessageHandler> queue = messageHandlers.get(id);
		if (queue == null)
			return;
		queue.add(handler);
	}

	/**
	 * send: <br>
	 * Description
	 * 
	 * @param userName
	 * @param id
	 * @param handler
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#send(java.lang.String, long,
	 *      lu.itrust.business.TS.messagehandler.MessageHandler)
	 */
	@Override
	public void send(String userName, long id, MessageHandler handler) {
		if (!hasTask(userName, id) && !registerTask(userName, id))
			return;
		send(id, handler);
	}

	/**
	 * recieve: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#recieve(long)
	 */
	@Override
	public MessageHandler recieve(long id) {
		Queue<MessageHandler> queue = messageHandlers.get(id);
		if (queue == null || queue.isEmpty())
			return null;
		return queue.poll();
	}

	/**
	 * recieveLast: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#recieveLast(long)
	 */
	@Override
	public MessageHandler recieveLast(long id) {
		Queue<MessageHandler> queue = messageHandlers.get(id);
		if (queue == null || queue.isEmpty())
			return null;
		MessageHandler messageHandler = null;
		while (!queue.isEmpty())
			messageHandler = queue.poll();
		return messageHandler;
	}

	/**
	 * recieve: <br>
	 * Description
	 * 
	 * @param userName
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#recieve(java.lang.String)
	 */
	@Override
	public List<MessageHandler> recieve(String userName) {
		List<MessageHandler> handlers = new LinkedList<>();
		if (!userTasks.containsKey(userName))
			return handlers;
		for (Long taskId : userTasks.get(userName))
			if (messageHandlers.containsKey(taskId))
				handlers.addAll(messageHandlers.get(taskId));
		return handlers;
	}

	/**
	 * tasks: <br>
	 * Description
	 * 
	 * @param userName
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#tasks(java.lang.String)
	 */
	@Override
	public List<Long> tasks(String userName) {
		return userTasks.get(userName);
	}
}