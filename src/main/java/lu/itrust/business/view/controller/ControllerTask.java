package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.helper.AsyncResult;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.WorkersPoolManager;
import lu.itrust.business.task.Worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ControllerTask.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl. :
 * @version
 * @since Feb 5, 2014
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

	/**
	 * status: <br>
	 * Description
	 * 
	 * @param id
	 * @param principal
	 * @param locale
	 * @return
	 */
	@RequestMapping("/Status/{id}")
	public @ResponseBody AsyncResult status(@PathVariable Long id, Principal principal, Locale locale) {

		// create result
		AsyncResult asyncResult = new AsyncResult(id);

		// check if task exists
		if (!serviceTaskFeedback.hasTask(principal.getName(), id))
			return null;
		else {

			// load worker of task
			Worker worker = workersPoolManager.get(id);

			Locale customLocale = null;

			// retrieve last feedback message
			MessageHandler messageHandler = serviceTaskFeedback.recieveLast(id);

			if (messageHandler != null) {

				if (messageHandler.getLanguage() != null)
					customLocale = new Locale(messageHandler.getLanguage());
			}
			// set worker status

			if (worker == null) {
				asyncResult.setStatus(messageSource.getMessage("label.task_status.delete", null, "Deleted", customLocale != null ? customLocale : locale));
				asyncResult.setFlag(0);
			} else if (worker.isCanceled()) {
				asyncResult.setStatus(messageSource.getMessage("label.task_status.abort", null, "Aborted", customLocale != null ? customLocale : locale));
				asyncResult.setFlag(1);
			} else if (worker.getError() != null) {
				asyncResult.setStatus(messageSource.getMessage("label.task_status.failed", null, "Failed", customLocale != null ? customLocale : locale));
				asyncResult.setFlag(2);
			} else if (worker.isWorking()) {
				asyncResult.setStatus(messageSource.getMessage("label.task_status.process", null, "Processing", customLocale != null ? customLocale : locale));
				asyncResult.setFlag(3);
			} else if (serviceTaskFeedback.messageCount(id) > 1) {
				asyncResult.setStatus(messageSource.getMessage("label.task_status.success", null, "Success", customLocale != null ? customLocale : locale));
				asyncResult.setFlag(4);
			} else {
				asyncResult.setStatus(messageSource.getMessage("label.task_status.success", null, "Success", customLocale != null ? customLocale : locale));
				asyncResult.setFlag(5);
			}

			// check if message exists or set null
			if (messageHandler != null) {

				asyncResult.setMessage(messageSource.getMessage(messageHandler.getCode(), messageHandler.getParameters(), messageHandler.getMessage(), customLocale != null ? customLocale : locale));
				asyncResult.setProgress(messageHandler.getProgress());
				asyncResult.setTaskName(messageHandler.getTaskName());
				asyncResult.setAsyncCallback(messageHandler.getAsyncCallback());

				// check if task is already done ansd set data
				if (messageHandler.getProgress() == 100 || asyncResult.getFlag() == 0 && messageHandler.getException() == null) {
					asyncResult.setStatus(messageSource.getMessage("label.task_status.success", null, "Success", customLocale != null ? customLocale : locale));
					asyncResult.setFlag(5);
				}
			} else
				asyncResult.setMessage(null);

			// unrgister task when done or errors
			if (asyncResult.getFlag() == 5 || asyncResult.getFlag() < 3 && !serviceTaskFeedback.hasMessage(id)) {
				serviceTaskFeedback.unregisterTask(principal.getName(), id);
			}
		}

		// return
		return asyncResult;
	}

	@RequestMapping("/Stop/{id}")
	public @ResponseBody String stop(@PathVariable Long id, Principal principal, Locale locale) {

		// check if user has the task with given id
		if (serviceTaskFeedback.hasTask(principal.getName(), id)) {

			// retireve worker of task
			Worker worker = workersPoolManager.get(id);

			// check if worker is running
			if (worker != null && worker.isWorking()) {

				// stop worker
				worker.cancel();

				// return success messages
				return messageSource.getMessage("success.task.canceled", null, "Task was canceled successfully", locale);
			} else

				// return task not running
				return messageSource.getMessage("failed.task.canceled", null, "Sorry, Task is not running", locale);
		} else

			// return task not found
			return messageSource.getMessage("error.task.not_found", null, "Sorry, task cannot be found", locale);
	}

	/**
	 * processing: <br>
	 * Description
	 * 
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "/InProcessing", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody List<Long> processing(Principal principal) {

		List<Long> result = serviceTaskFeedback.tasks(principal.getName());

		if(result == null)
			result = new ArrayList<Long>();
		
		// get tasks of this user
		return result;
	}

	@RequestMapping("/Exist")
	public @ResponseBody boolean hasTask(Principal principal) {

		// check if user has a task
		return serviceTaskFeedback.userHasTask(principal.getName());
	}
}