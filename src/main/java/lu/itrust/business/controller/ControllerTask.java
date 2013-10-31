/**
 * 
 */
package lu.itrust.business.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.WorkersPoolManager;
import lu.itrust.business.task.Worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author eom
 * 
 */
@Controller
@RequestMapping("/Task")
@Secured("ROLE_USER")
public class ControllerTask {

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping("/Status/{id}")
	public @ResponseBody
	Object[] status(@PathVariable Long id, Principal principal, Locale locale) {
		String message = null;
		String status = null;
		int flag = 0;
		if (!serviceTaskFeedback.hasTask(principal.getName(), id)) {
			message = messageSource.getMessage("error.task.not_found", null,
					"Sorry, task cannot be found", locale);
			status = messageSource.getMessage("error", null, "Error", locale);
			flag = 0;
		} else {
			Worker worker = workersPoolManager.get(id);
			if (worker == null) {
				flag = 1;
				status = messageSource.getMessage("delete", null, "Deleted",
						locale);
			} else if (worker.isCanceled()) {
				flag = 2;
				status = messageSource.getMessage("abort", null, "Aborted",
						locale);
			} else if (worker.getError() != null) {
				flag = 3;
				status = messageSource.getMessage("fail", null, "Failed",
						locale);
			} else if (!worker.isWorking()) {
				flag = 4;
				status = messageSource.getMessage("success", null, "Success",
						locale);
			} else {
				flag = 5;
				status = messageSource.getMessage("process", null,
						"Processing", locale);
			}

			MessageHandler messageHandler = serviceTaskFeedback.recive(id);
			if (messageHandler != null)
				message = messageSource.getMessage(messageHandler.getCode(),
						messageHandler.getParameters(),
						messageHandler.getMessage(), locale);
			else
				message = null;
			if (flag != 5)
				serviceTaskFeedback.deregisterTask(principal.getName(), id);
		}
		return new Object[] { flag, message, status };
	}

	@RequestMapping("/Stop/{id}")
	public @ResponseBody
	String stop(@PathVariable Long id, Principal principal, Locale locale) {
		if (!serviceTaskFeedback.hasTask(principal.getName(), id)) {
			Worker worker = workersPoolManager.get(id);
			if (worker.isWorking()) {
				worker.cancel();
				return messageSource.getMessage("success.task.canceled", null,
						"Task was canceled successfully", locale);
			} else
				return messageSource.getMessage("failed.task.canceled", null,
						"Sorry, Task is not running", locale);
		} else
			return messageSource.getMessage("error.task.not_found", null,
					"Sorry, task cannot be found", locale);
	}

	@RequestMapping("/InProcessing")
	public @ResponseBody
	List<Long> processing(Principal principal) {
		return serviceTaskFeedback.tasks(principal.getName());
	}

	@RequestMapping("/Exist")
	public @ResponseBody
	boolean hasTask(Principal principal) {
		return serviceTaskFeedback.userHasTask(principal.getName());
	}

}
