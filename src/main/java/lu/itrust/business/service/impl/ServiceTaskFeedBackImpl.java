/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.service.ServiceTaskFeedback;

import org.springframework.stereotype.Service;

/**
 * @author eom
 * 
 */
@Service
public class ServiceTaskFeedBackImpl implements ServiceTaskFeedback {

	private Map<String, List<Long>> userTasks = new LinkedHashMap<>();

	private Map<Long, MessageHandler> messageHandlers = new LinkedHashMap<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceTaskFeedback#send(lu.itrust.business
	 * .TS.messagehandler.MessageHandler)
	 */
	@Override
	public void send(MessageHandler handler) {
		messageHandlers.put(handler.getIdTask(), handler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceTaskFeedback#send(long,
	 * lu.itrust.business.TS.messagehandler.MessageHandler)
	 */
	@Override
	public void send(long id, MessageHandler handler) {
		messageHandlers.put(id, handler);
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
		return messageHandlers.remove(id);
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
		if(!userTasks.containsKey(userName))
			return handlers;
		for (Long taskId : userTasks.get(userName))
			if (messageHandlers.containsKey(taskId))
				handlers.add(messageHandlers.get(taskId));
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
	protected Map<Long, MessageHandler> getMessageHandlers() {
		return messageHandlers;
	}

	/**
	 * @param messageHandlers
	 *            the messageHandlers to set
	 */
	protected void setMessageHandlers(Map<Long, MessageHandler> messageHandlers) {
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
		messageHandlers.put(id, new MessageHandler("success.register.task",
				"Task was created successfully", null));
		List<Long> tasks = userTasks.containsKey(userName) ? userTasks
				.get(userName) : new LinkedList<Long>();
		tasks.add(id);
		if (!userTasks.containsKey(userName))
			userTasks.put(userName, tasks);
		return true;
	}

	@Override
	public void deregisterTask(String userName, long id) {
		messageHandlers.put(id, new MessageHandler("success.register.task",
				"Task was created successfully", null));
		List<Long> tasks = userTasks.containsKey(userName) ? userTasks
				.get(userName) : new LinkedList<Long>();
		tasks.remove(id);
		if (tasks.isEmpty())
			userTasks.remove(userName);
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
		for (Long task : userTasks.get(userName))
			if (id == task)
				return true;
		return false;
	}

}
