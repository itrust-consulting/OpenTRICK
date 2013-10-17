package lu.itrust.business.controller;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.service.ServiceMeasureDescription;
import lu.itrust.business.service.ServiceNorm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ControllerStandard.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version 0.1
 * @since Oct 14, 2013
 */
@Controller
public class ControllerNorm {

	@Autowired
	private ServiceNorm serviceNorm;

	@Autowired
	private MessageSource messageSource;

	/**
	 * setServiceNorm: <br>
	 * Description
	 * 
	 * @param serviceNorm
	 */
	public void setServiceNorm(ServiceNorm serviceNorm) {
		this.serviceNorm = serviceNorm;
	}

	/**
	 * 
	 * Display all Norms
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Standard/Norm/Display")
	public String displayAll(Map<String, Object> model) throws Exception {
		model.put("norms", serviceNorm.loadAll());
		return "standard/norms";
	}

	/**
	 * 
	 * Edit Norm
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Standard/Norm/Edit/{normLabel}")
	public String editStandard(@PathVariable("normLabel") String normLabel, HttpSession session, Map<String, Object> model, RedirectAttributes redirectAttributes, Locale locale)
			throws Exception {
		Norm norm = (Norm) session.getAttribute("norm");
		if (norm == null || !norm.getLabel().equals(normLabel))
			norm = serviceNorm.loadSingleNormByName(normLabel);
		if (norm == null) {
			String msg = messageSource.getMessage("errors.norm.notexist", null, "Norm does not exist", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
			return "redirect:/KnowLedgeBase/Standard/Norm/Display";
		}
		model.put("norm", norm);
		return "standard/editNorm";
	}

	/**
	 * 
	 * Perform edit Norm
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Standard/Norm/Update/{normLabel}")
	public String updateLanguage(@PathVariable("normLabel") String normLabel, @ModelAttribute("norm") @Valid Norm norm, BindingResult result,
			RedirectAttributes redirectAttributes, Locale locale) throws Exception {
		if (norm == null || !norm.getLabel().equals(normLabel)) {
			String msg = messageSource.getMessage("errors.norm.update.notrecognized", null, "Norm not recognized", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
		} else {
			try {
				serviceNorm.saveOrUpdate(norm);
				String msg = messageSource.getMessage("success.norm.update.success", null, "Norm had been updated!", locale);
				redirectAttributes.addFlashAttribute("success", msg);
			} catch (Exception e) {
				String msg = messageSource.getMessage("errors.norm.update.fail", null, "Norm update failed!", locale);
				redirectAttributes.addFlashAttribute("errors", msg);
			}
		}
		return "redirect:/KnowLedgeBase/Standard/Norm/Display";
	}

	/**
	 * 
	 * Delete single Norm
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Standard/Norm/Delete/{normLabel}")
	public String deleteLanguage(@PathVariable("normLabel") String normLabel) throws Exception {
		
		serviceNorm.remove(serviceNorm.loadSingleNormByName(normLabel));
		return "redirect:../Display";
	}

	/**
	 * 
	 * Request add new Norm
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Standard/Norm/Add")
	public String addNorm(Map<String, Object> model) {
		model.put("norm", new Norm());
		return "standard/addNorm";
	}

	/**
	 * 
	 * Perform add new Norm
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Standard/Norm/Create")
	public String createLanguage(@ModelAttribute("norm") @Valid Norm norm, BindingResult result) throws Exception {
		this.serviceNorm.save(norm);
		return "redirect:./Display";
	}

}
