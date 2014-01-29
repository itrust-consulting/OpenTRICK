/**
 * 
 */
package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.WorkersPoolManager;
import lu.itrust.business.task.Worker;
import lu.itrust.business.view.model.AsyncResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize(Constant.ROLE_MIN_USER)
public class ControllerTask {

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping("/Status/{id}")
	public @ResponseBody
	AsyncResult status(@PathVariable Long id, Principal principal, Locale locale) {
		AsyncResult asyncResult = new AsyncResult(id);
		if (!serviceTaskFeedback.hasTask(principal.getName(), id))
			return null;
		else {
			Worker worker = workersPoolManager.get(id);
			if (worker == null) {
				asyncResult.setStatus(messageSource.getMessage(
						"label.task_status.delete", null, "Deleted", locale));
				asyncResult.setFlag(0);
			} else if (worker.isCanceled()) {
				asyncResult.setStatus(messageSource.getMessage(
						"label.task_status.abort", null, "Aborted", locale));
				asyncResult.setFlag(1);
			} else if (worker.getError() != null) {
				asyncResult.setStatus(messageSource.getMessage(
						"label.task_status.failed", null, "Failed", locale));
				asyncResult.setFlag(2);
			} else if (worker.isWorking()) {
				asyncResult.setStatus(messageSource
						.getMessage("label.task_status.process", null,
								"Processing", locale));
				asyncResult.setFlag(3);
			} else if (serviceTaskFeedback.messageCount(id) > 1) {
				asyncResult.setStatus(messageSource.getMessage(
						"label.task_status.success", null, "Success", locale));
				asyncResult.setFlag(4);
			} else {
				asyncResult.setStatus(messageSource.getMessage(
						"label.task_status.success", null, "Success", locale));
				asyncResult.setFlag(5);
			}

			MessageHandler messageHandler = serviceTaskFeedback.reciveLast(id);
			if (messageHandler != null) {
				asyncResult.setMessage(messageSource.getMessage(
						messageHandler.getCode(),
						messageHandler.getParameters(),
						messageHandler.getMessage(), locale));
				asyncResult.setProgress(messageHandler.getProgress());
				asyncResult.setTaskName(messageHandler.getTaskName());
				asyncResult.setAsyncCallback(messageHandler.getAsyncCallback());
				
				if ( messageHandler.getProgress() == 100 || asyncResult.getFlag() == 0
						&& messageHandler.getException() == null) {
					asyncResult.setStatus(messageSource.getMessage(
							"label.task_status.success", null, "Success",
							locale));
					asyncResult.setFlag(5);
				}
			} else
				asyncResult.setMessage(null);

			if (asyncResult.getFlag() == 5 || asyncResult.getFlag() < 3
					&& !serviceTaskFeedback.hasMessage(id)){
				serviceTaskFeedback.deregisterTask(principal.getName(), id);
			}
		}
		return asyncResult;
	}

	@RequestMapping("/Stop/{id}")
	public @ResponseBody
	String stop(@PathVariable Long id, Principal principal, Locale locale) {
		if (serviceTaskFeedback.hasTask(principal.getName(), id)) {
			Worker worker = workersPoolManager.get(id);
			if (worker != null && worker.isWorking()) {
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
