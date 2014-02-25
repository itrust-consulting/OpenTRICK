package lu.itrust.business.view.controller;

import java.io.File;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.CustomDelete;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasureDescription;
import lu.itrust.business.service.ServiceMeasureDescriptionText;
import lu.itrust.business.service.ServiceNorm;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.WorkersPoolManager;
import lu.itrust.business.task.Worker;
import lu.itrust.business.task.WorkerImportNorm;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ControllerNorm.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version 0.1
 * @since Oct 14, 2013
 */
@PreAuthorize(Constant.ROLE_MIN_CONSULTANT)
@Controller
@RequestMapping("/KnowledgeBase/Norm")
public class ControllerNorm {

	@Autowired
	private ServiceNorm serviceNorm;

	@Autowired
	private ServiceMeasureDescription serviceMeasureDescription;

	@Autowired
	private ServiceMeasureDescriptionText serviceMeasureDescriptionText;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private TaskExecutor executor;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CustomDelete customDelete;

	/**
	 * displayAll: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public String displayAll(Model model) throws Exception {

		// load all norms to model

		model.addAttribute("norms", serviceNorm.loadAll());
		return "knowledgebase/standard/norm/norms";
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json")
	public String section(Model model) throws Exception {

		// call default
		return displayAll(model);
	}

	/**
	 * loadSingleNorm: <br>
	 * Description
	 * 
	 * @param normId
	 * @param session
	 * @param model
	 * @param redirectAttributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{normId}")
	public String loadSingleNorm(@PathVariable("normId") String normId, Map<String, Object> model, RedirectAttributes redirectAttributes, Locale locale) throws Exception {

		// load norm object
		Norm norm = serviceNorm.loadSingleNormByName(normId);
		if (norm == null) {

			// retrun error if norm does not exist
			String msg = messageSource.getMessage("errors.norm.notexist", null, "Norm does not exist", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
			return "redirect:/KnowLedgeBase/Norm";
		}

		// load norm to model
		model.put("norm", norm);

		return "knowledgebase/standard/norm/showNorm";
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param value
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	List<String[]> save(@RequestBody String value, Locale locale) {

		// init errors list
		List<String[]> errors = new LinkedList<>();

		try {

			// create new empty object
			Norm norm = new Norm();

			// build norm object
			buildNorm(errors, norm, value, locale);

			// check if norm has to be create (new) or updated
			if (norm.getId() < 1) {

				// save
				serviceNorm.save(norm);
			} else {

				// update
				serviceNorm.saveOrUpdate(norm);
			}

			// errors
			return errors;
		} catch (Exception e) {

			// return errors
			errors.add(new String[] { "norm", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
			return errors;
		}
	}

	/**
	 * deleteNorm: <br>
	 * Description
	 * 
	 * @param normId
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Delete/{normId}", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String deleteNorm(@PathVariable("normId") Integer normId, Locale locale) throws Exception {

		try {

			// try to delete the norm
			customDelete.deleteNorm(serviceNorm.getNormByID(normId));

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.norm.delete.successfully", null, "Norm was deleted successfully", locale));
		} catch (Exception e) {

			// return error message
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.norm.delete.successfully", null, "Norm was not deleted. Make sure it is not used in an analysis", locale));
		}
	}

	/**
	 * 
	 * Upload new norm file
	 * 
	 * */
	@RequestMapping(value = "/Upload", method = RequestMethod.GET, headers = "Accept=application/json")
	public String UploadNorm() throws Exception {
		return "knowledgebase/standard/norm/uploadForm";
	}

	/**
	 * importNewNorm: <br>
	 * Description
	 */
	@RequestMapping(value = "/Import", headers = "Accept=application/json")
	public String importNewNorm(@RequestParam(value = "file") MultipartFile file, Principal principal, HttpServletRequest request, RedirectAttributes attributes, Locale locale)
			throws Exception {
		File importFile = new File(request.getServletContext().getRealPath("/WEB-INF/tmp") + "/" + principal.getName() + "_" + System.nanoTime() + "");
		file.transferTo(importFile);
		Worker worker = new WorkerImportNorm(serviceTaskFeedback, sessionFactory, workersPoolManager, importFile);
		if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId())) {
			executor.execute(worker);
			return "redirect:/Task/Status/" + worker.getId();
		}
		attributes.addFlashAttribute("errors", messageSource.getMessage("failed.start.export.analysis", null, "Analysis export was failed", locale));
		return "redirect:/KnowledgeBase/Norm/Upload";
	}

	/**
	 * buildNorm: <br>
	 * Description
	 * 
	 * @param errors
	 * @param norm
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildNorm(List<String[]> errors, Norm norm, String source, Locale locale) {

		try {

			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			// load norm id
			int id = jsonNode.get("id").asInt();

			// check if norm has to be updated
			if (id > 0)

				// init id
				norm.setId(jsonNode.get("id").asInt());

			// set data
			norm.setLabel(jsonNode.get("label").asText());

			norm.setDescription(jsonNode.get("description").asText());
			norm.setVersion(jsonNode.get("version").asInt());

			// set computable flag
			if (jsonNode.get("computable").asText().equals("on")) {
				norm.setComputable(true);
			} else {
				norm.setComputable(false);
			}

			// return success
			return true;

		} catch (Exception e) {

			// return error
			errors.add(new String[] { "norm", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
			return false;
		}
	}
}