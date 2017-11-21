package lu.itrust.business.TS.database.service.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lu.itrust.business.TS.asynchronousWorkers.AsyncResult;
import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.messagehandler.MessageHandler;

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

	private Map<String, Locale> userLocales = Collections.synchronizedMap(new LinkedHashMap<String, Locale>());

	private Map<String, String> taskUsers = Collections.synchronizedMap(new LinkedHashMap<String, String>());

	private Map<String, List<String>> userTasks = Collections.synchronizedMap(new LinkedHashMap<String, List<String>>());

	private Map<String, Queue<MessageHandler>> messageHandlers = Collections.synchronizedMap(new LinkedHashMap<String, Queue<MessageHandler>>());

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Value("${app.settings.background.task.max.pool.size}")
	private int maxPoolSize;

	@Value("${app.settings.background.task.max.user.size}")
	private int maxUserSize;

	/**
	 * messageCount: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#messageCount(String)
	 */
	@Override
	public int messageCount(String id) {
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
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#taskExist(String)
	 */
	@Override
	public boolean taskExist(String id) {
		return messageHandlers.containsKey(id);
	}

	/**
	 * hasMessage: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#hasMessage(String)
	 */
	@Override
	public boolean hasMessage(String id) {
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
		List<String> tasks = userTasks.get(userName);
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
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#hasTask(java.lang.String,
	 *      String)
	 */
	@Override
	public boolean hasTask(String userName, String id) {
		if (!userTasks.containsKey(userName))
			return false;
		for (String task : userTasks.get(userName))
			if (task.equals(id))
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
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#registerTask(java.lang.String,
	 *      String)
	 */
	@Override
	public boolean registerTask(String userName, String id, Locale locale) {
		if (messageHandlers.containsKey(id))
			return false;
		List<String> tasks = userTasks.containsKey(userName) ? userTasks.get(userName) : Collections.synchronizedList(new LinkedList<String>());
		if (tasks.size() >= maxUserSize || workersPoolManager.poolSize() >= maxPoolSize)
			return false;
		messageHandlers.put(id, new LinkedList<MessageHandler>());
		taskUsers.put(id, userName);
		tasks.add(id);
		if (!userTasks.containsKey(userName))
			userTasks.put(userName, tasks);
		userLocales.put(userName, locale);
		return true;
	}

	/**
	 * unregisterTask: <br>
	 * Description
	 * 
	 * @param userName
	 * @param id
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#unregisterTask(java.lang.String,
	 *      String)
	 */
	@Override
	public void unregisterTask(String userName, String id) {
		try {
			if (!userTasks.containsKey(userName))
				return;
			List<String> tasks = userTasks.get(userName);
			if (tasks == null || tasks.isEmpty())
				return;
			tasks.remove(id);
			if (tasks.isEmpty())
				userTasks.remove(userName);
			Queue<MessageHandler> handlers = messageHandlers.remove(id);
			if (!(handlers == null || handlers.isEmpty()))
				handlers.clear();
			taskUsers.remove(id);
			workersPoolManager.remove(id);
		} finally {
			if (!userTasks.containsKey(userName))
				userLocales.remove(userName);
		}
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
		if (handler == null || handler.getIdTask() == null)
			return;
		sendMessage(handler);
	}

	/**
	 * send: <br>
	 * Description
	 * 
	 * @param id
	 * @param handler
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#send(String,
	 *      lu.itrust.business.TS.messagehandler.MessageHandler)
	 */
	@Override
	public void send(String id, MessageHandler handler) {
		if (id == null || handler == null)
			return;
		if (handler.getIdTask() == null)
			handler.setIdTask(id);
		sendMessage(handler);
	}

	private void sendMessage(MessageHandler handler) {
		Queue<MessageHandler> queue = messageHandlers.get(handler.getIdTask());
		if (queue == null)
			return;
		queue.add(handler);
		sendToUser(handler);
	}

	private void sendToUser(MessageHandler handler) {
		String username = null;
		AsyncResult asyncResult = null;
		try {
			username = findUsernameById(handler.getIdTask());
			if (username == null)
				return;
			Locale locale = userLocales.get(username);
			asyncResult = new AsyncResult(handler.getIdTask());
			// load worker of task
			Worker worker = workersPoolManager.get(handler.getIdTask());
			// set worker status
			if (worker == null) {
				asyncResult.setStatus(messageSource.getMessage("label.task_status.delete", null, "Deleted", locale));
				asyncResult.setFlag(0);
			} else {
				asyncResult.setTaskName(worker.getName());
				if (worker.isCanceled()) {
					asyncResult.setStatus(messageSource.getMessage("label.task_status.abort", null, "Aborted", locale));
					asyncResult.setFlag(1);
				} else if (worker.getError() != null) {
					asyncResult.setStatus(messageSource.getMessage("label.task_status.failed", null, "Failed", locale));
					asyncResult.setFlag(2);
				} else if (worker.isWorking()) {
					asyncResult.setStatus(messageSource.getMessage("label.task_status.process", null, "Processing", locale));
					asyncResult.setFlag(3);
				} else {
					asyncResult.setStatus(messageSource.getMessage("label.task_status.success", null, "Success", locale));
					asyncResult.setFlag(5);
				}
			}
			
			// check if message exists or set null
			asyncResult.setMessage(messageSource.getMessage(handler.getCode(), handler.getParameters(), handler.getMessage(), locale));
			asyncResult.setProgress(handler.getProgress());
			asyncResult.setAsyncCallbacks(handler.getAsyncCallbacks());
			if (handler.getException() != null) {
				asyncResult.setFlag(2);
				asyncResult.setStatus(messageSource.getMessage("label.task_status.failed", null, "Failed", locale));
			} else if (handler.getProgress() == 100 || asyncResult.getFlag() == 0) {
				asyncResult.setStatus(messageSource.getMessage("label.task_status.success", null, "Success", locale));
				asyncResult.setFlag(5);
			}

			this.messagingTemplate.convertAndSendToUser(username, "/Task", asyncResult);
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		} finally {
			if (!(asyncResult == null || username == null) && (asyncResult.getFlag() == 5 || asyncResult.getFlag() < 3))
				unregisterTask(username, handler.getIdTask());
		}
	}

	/**
	 * send: <br>
	 * Description
	 * 
	 * @param userName
	 * @param id
	 * @param handler
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#send(java.lang.String,
	 *      String, lu.itrust.business.TS.messagehandler.MessageHandler)
	 */
	@Override
	public void send(String userName, String id, MessageHandler handler, Locale locale) {
		if (!hasTask(userName, id) && !registerTask(userName, id, locale))
			return;
		send(id, handler);
	}

	/**
	 * recieve: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#recieveById(String)
	 */
	@Override
	public MessageHandler recieveById(String id) {
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
	 * @see lu.itrust.business.TS.database.service.ServiceTaskFeedback#recieveLast(String)
	 */
	@Override
	public MessageHandler recieveLast(String id) {
		Queue<MessageHandler> queue = messageHandlers.get(id);
		if (queue == null || queue.isEmpty())
			return null;
		MessageHandler messageHandler = null;
		synchronized (queue) {
			while (queue.size() > 1)
				queue.poll();
			if (!queue.isEmpty())
				messageHandler = queue.peek();
		}
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
		for (String taskId : userTasks.get(userName))
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
	public List<String> tasks(String userName) {
		return userTasks.get(userName);
	}

	@Override
	public String findUsernameById(String id) {
		String username = taskUsers.get(id);
		return username == null ? userTasks.entrySet().parallelStream().filter(entry -> entry.getValue().contains(id)).map(entry -> entry.getKey()).findAny().orElse(username)
				: username;
	}

	public Map<String, Locale> getUserLocales() {
		return userLocales;
	}

	public void setUserLocales(Map<String, Locale> userLocales) {
		this.userLocales = userLocales;
	}

	@Override
	public void update(String username, Locale locale) {
		if (userTasks.containsKey(username))
			userLocales.put(username, locale);
		else if (userLocales.containsKey(username))
			userLocales.remove(username);
	}
}