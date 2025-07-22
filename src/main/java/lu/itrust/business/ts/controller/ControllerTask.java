package lu.itrust.business.ts.controller;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.ts.asynchronousWorkers.Worker;
import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncResult;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceTaskFeedback;
import lu.itrust.business.ts.database.service.WorkersPoolManager;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.messagehandler.MessageHandler;

/**
 * ControllerTask.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à r.l
 * @version
 * @since Feb 5, 2014
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_USER)
public class ControllerTask {

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private MessageSource messageSource;

	/**
	 * status: <br>
	 * Description
	 * 
	 * @param id
	 * @param principal
	 * @param locale
	 * @return
	 */
	@GetMapping(value="/Task/Status/{id}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody AsyncResult status(@PathVariable String id, Principal principal, Locale locale) {

		// create result
		AsyncResult asyncResult = new AsyncResult(id);

		// check if task exists
		if (!serviceTaskFeedback.hasTask(principal.getName(), id))
			return null;
		else {

			// load worker of task
			Worker worker = workersPoolManager.get(id);

			// retrieve last feedback message
			MessageHandler messageHandler = serviceTaskFeedback.recieveById(id);
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
				} else if (serviceTaskFeedback.exists(id)) {
					asyncResult.setStatus(messageSource.getMessage("label.task_status.success", null, "Success", locale));
					asyncResult.setFlag(4);
				} else {
					asyncResult.setStatus(messageSource.getMessage("label.task_status.success", null, "Success", locale));
					asyncResult.setFlag(5);
				}
			}

			// check if message exists or set null
			if (messageHandler != null) {

				asyncResult.setMessage(messageSource.getMessage(messageHandler.getCode(), messageHandler.getParameters(), messageHandler.getMessage(), locale));
				asyncResult.setProgress(messageHandler.getProgress());
				asyncResult.setAsyncCallbacks(messageHandler.getAsyncCallbacks());
				if (messageHandler.getException() != null) {
					asyncResult.setFlag(2);
					asyncResult.setStatus(messageSource.getMessage("label.task_status.failed", null, "Failed", locale));
				} else if (messageHandler.getProgress() == 100 || asyncResult.getFlag() == 0) {
					asyncResult.setStatus(messageSource.getMessage("label.task_status.success", null, "Success", locale));
					asyncResult.setFlag(5);
				}
			} else
				asyncResult.setMessage(null);

		}

		// return
		return asyncResult;
	}

	/**
	 * Mixte mapping
	 * @param id
	 * @param principal
	 * @param locale
	 * @return
	 */
	@MessageMapping("/Task/Done")
	@GetMapping(value="/Task/{id}/Done",headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@ResponseBody
	public String stop(@PathVariable String id, Principal principal, Locale locale) {
		serviceTaskFeedback.unregisterTask(principal.getName(), id);
		return JsonMessage.info(messageSource.getMessage("success.task.done", null, "Task was done successfully", locale));
	}

	/**
	 * Mixte Mapping
	 * processing: <br>
	 * Description
	 * 
	 * @param principal
	 * @return
	 */
	@MessageMapping("Task/In-progress")
	@SendToUser("/Task")
	@GetMapping(value = "/Task/In-progress", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@ResponseBody
	public List<?> processing(@RequestParam(name = "legacy", defaultValue = "true") boolean legacy, Principal principal, Locale locale) {
		List<String> result = serviceTaskFeedback.tasks(principal.getName());
		if (!result.isEmpty())
			serviceTaskFeedback.setWebSocketSupported(principal.getName(), !legacy);
		if (legacy)
			return result;
		else
			return serviceTaskFeedback.tasks(principal.getName()).parallelStream().map(id -> status(id, principal, locale)).filter(task -> task != null)
					.collect(Collectors.toList());
	}

	@GetMapping(value="/Task/Exist", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody boolean hasTask(Principal principal) {
		// check if user has a task
		return serviceTaskFeedback.userHasTask(principal.getName());
	}
}