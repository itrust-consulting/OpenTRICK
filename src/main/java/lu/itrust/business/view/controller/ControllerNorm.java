package lu.itrust.business.view.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.service.ServiceNorm;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ControllerNorm.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version 0.1
 * @since Oct 14, 2013
 */
@Controller
@RequestMapping("/KnowledgeBase/Norm")
public class ControllerNorm {

	@Autowired
	private ServiceNorm serviceNorm;

	@Autowired
	private MessageSource messageSource;

	/**
	 * 
	 * Display all Norms
	 * 
	 * */
	@RequestMapping
	public String displayAll(Map<String, Object> model) throws Exception {
		model.put("norms", serviceNorm.loadAll());
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
		model.addAttribute("norms", serviceNorm.loadAll());
		return "knowledgebase/standard/norm/norms";
	}

	/**
	 * 
	 * Display single Norm
	 * 
	 * */
	@RequestMapping("/{normId}")
	public String loadSingleNorm(@PathVariable("normId") String normId, HttpSession session, Map<String, Object> model, RedirectAttributes redirectAttributes, Locale locale)
			throws Exception {
		Norm norm = (Norm) session.getAttribute("norm");
		if (norm == null || norm.getLabel() != normId)
			norm = serviceNorm.loadSingleNormByName(normId);
		if (norm == null) {
			String msg = messageSource.getMessage("errors.norm.notexist", null, "Norm does not exist", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
			return "redirect:/KnowLedgeBase/Norm";
		}
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
		List<String[]> errors = new LinkedList<>();
		try {

			Norm norm = new Norm();
			if (!buildNorm(errors, norm, value, locale))
				return errors;
			if (norm.getId() < 1) {
				serviceNorm.save(norm);
			} else {
				serviceNorm.saveOrUpdate(norm);
			}
		} catch (Exception e) {
			errors.add(new String[] { "norm", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}
		return errors;
	}

	/**
	 * 
	 * Delete single language
	 * 
	 * */
	@RequestMapping(value = "/Delete/{normId}", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String[] deleteLanguage(@PathVariable("normId") Integer normId, Locale locale) throws Exception {
		serviceNorm.remove(serviceNorm.getNormByID(normId));
		return new String[] { "error", messageSource.getMessage("success.norm.delete.successfully", null, "Norm was deleted successfully", locale) };

	}

	/**
	 * buildLanguage: <br>
	 * Description
	 * 
	 * @param errors
	 * @param language
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildNorm(List<String[]> errors, Norm norm, String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			int id = jsonNode.get("id").asInt();
			if (id > 0)
				norm.setId(jsonNode.get("id").asInt());

			norm.setLabel(jsonNode.get("label").asText());

			return true;

		} catch (Exception e) {

			errors.add(new String[] { "norm", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * setServiceNorm: <br>
	 * Description
	 * 
	 * @param serviceNorm
	 */
	public void setServiceNorm(ServiceNorm serviceNorm) {
		this.serviceNorm = serviceNorm;
	}

}