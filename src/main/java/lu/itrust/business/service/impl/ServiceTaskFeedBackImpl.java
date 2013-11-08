/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.service.ServiceTaskFeedback;

import org.springframework.stereotype.Service;

/**
 * @author eom
 * 
 */
@Service
public class ServiceTaskFeedBackImpl implements ServiceTaskFeedback {

	private Map<String, List<Long>> userTasks = Collections
			.synchronizedMap(new LinkedHashMap<String, List<Long>>());

	private Map<Long, Queue<MessageHandler>> messageHandlers = Collections
			.synchronizedMap(new LinkedHashMap<Long, Queue<MessageHandler>>());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceTaskFeedback#send(lu.itrust.business
	 * .TS.messagehandler.MessageHandler)
	 */
	@Override
	public void send(MessageHandler handler) {
		Queue<MessageHandler> queue = messageHandlers.get(handler.getIdTask());
		if (queue == null)
			return;
		queue.add(handler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceTaskFeedback#send(long,
	 * lu.itrust.business.TS.messagehandler.MessageHandler)
	 */
	@Override
	public void send(long id, MessageHandler handler) {
		Queue<MessageHandler> queue = messageHandlers.get(id);
		if (queue == null)
			return;
		queue.add(handler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceTaskFeedback#send(java.lang.String,
	 * long, lu.itrust.business.TS.messagehandler.MessageHandler)
	 */
	@Override
	public void send(String userName, long id, MessageHandler handler) {
		if (!hasTask(userName, id) && !registerTask(userName, id))
			return;
		send(id, handler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceTaskFeedback#recive(long)
	 */
	@Override
	public MessageHandler recive(long id) {
		Queue<MessageHandler> queue = messageHandlers.get(id);
		if (queue == null || queue.isEmpty())
			return null;
		return queue.poll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceTaskFeedback#recive(java.lang.String)
	 */
	@Override
	public List<MessageHandler> recive(String userName) {
		List<MessageHandler> handlers = new LinkedList<>();
		if (!userTasks.containsKey(userName))
			return handlers;
		for (Long taskId : userTasks.get(userName))
			if (messageHandlers.containsKey(taskId))
				handlers.addAll(messageHandlers.get(taskId));
		return handlers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceTaskFeedback#task(java.lang.String)
	 */
	@Override
	public List<Long> tasks(String userName) {
		return userTasks.get(userName);
	}

	/**
	 * @return the messageHandlers
	 */
	protected Map<Long, Queue<MessageHandler>> getMessageHandlers() {
		return messageHandlers;
	}

	/**
	 * @param messageHandlers
	 *            the messageHandlers to set
	 */
	protected void setMessageHandlers(
			Map<Long, Queue<MessageHandler>> messageHandlers) {
		this.messageHandlers = messageHandlers;
	}

	/**
	 * @return the userTask
	 */
	protected Map<String, List<Long>> getUserTasks() {
		return userTasks;
	}

	/**
	 * @param userTask
	 *            the userTask to set
	 */
	protected void setUserTasks(Map<String, List<Long>> userTask) {
		this.userTasks = userTask;
	}

	@Override
	public boolean registerTask(String userName, long id) {
		if (messageHandlers.containsKey(id))
			return false;
		messageHandlers.put(id, new LinkedList<MessageHandler>());
		List<Long> tasks = userTasks.containsKey(userName) ? userTasks
				.get(userName) : Collections
				.synchronizedList(new LinkedList<Long>());
		tasks.add(id);
		if (!userTasks.containsKey(userName))
			userTasks.put(userName, tasks);
		return true;
	}

	@Override
	public void deregisterTask(String userName, long id) {

		if (!userTasks.containsKey(userName))
			return;
		List<Long> tasks = userTasks.get(userName);

		if (tasks == null || tasks.isEmpty())
			return;
		tasks.remove(id);
		if (tasks.isEmpty())
			userTasks.remove(userName);
		if (messageHandlers.containsKey(id)
				&& !messageHandlers.get(id).isEmpty())
			messageHandlers.get(id).clear();
		messageHandlers.remove(id);
	}

	@Override
	public boolean taskExist(long id) {
		return messageHandlers.containsKey(id);
	}

	@Override
	public boolean userHasTask(String userName) {
		if (!userTasks.containsKey(userName))
			return false;
		List<Long> tasks = userTasks.get(userName);
		return tasks != null && !tasks.isEmpty();
	}

	@Override
	public boolean hasTask(String userName, long id) {

		if (!userTasks.containsKey(userName))
			return false;

		for (Long task : userTasks.get(userName))
			if (id == task)
				return true;
		return false;
	}

	@Override
	public boolean hasMessage(long id) {
		Queue<MessageHandler> queue = messageHandlers.get(id);
		return queue != null && !queue.isEmpty();
	}

	@Override
	public int messageCount(long id) {
		Queue<MessageHandler> queue = messageHandlers.get(id);
		return queue == null ? 0 : queue.size();
	}
}
