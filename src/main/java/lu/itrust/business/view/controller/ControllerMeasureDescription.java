package lu.itrust.business.view.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasureDescription;
import lu.itrust.business.service.ServiceMeasureDescriptionText;
import lu.itrust.business.service.ServiceNorm;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ControllerMeasureDescription.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since Oct 15, 2013
 */
@Secured("ROLE_USER")
@Controller
public class ControllerMeasureDescription {

	@Autowired
	private ServiceMeasureDescription serviceMeasureDescription;
	
	@Autowired
	private ServiceMeasureDescriptionText serviceMeasureDescriptionText;

	@Autowired
	private ServiceNorm serviceNorm;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private MessageSource messageSource;

	/**
	 * setServiceMeasureDescription: <br>
	 * Description
	 * 
	 * @param serviceMeasureDescription
	 */
	public void setServiceMeasureDescription(ServiceMeasureDescription serviceMeasureDescription) {
		this.serviceMeasureDescription = serviceMeasureDescription;
	}
	
	/**
	 * setServiceMeasureDescriptionText: <br>
	 * Description
	 * 
	 * @param serviceMeasureDescriptionText
	 */
	public void setServiceMeasureDescriptionText(ServiceMeasureDescriptionText serviceMeasureDescriptionText) {
		this.serviceMeasureDescriptionText = serviceMeasureDescriptionText;
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

	/**
	 * setServiceLanguage: <br>
	 * Description
	 * 
	 * @param serviceLanguage
	 */
	public void setServiceLanguage(ServiceLanguage serviceLanguage) {
		this.serviceLanguage = serviceLanguage;
	}

	/**
	 * 
	 * Display all MeasureDescriptions of a given Norm
	 * 
	 * */
	@RequestMapping("KnowledgeBase/Norm/{normId}/Measures")
	public String displayAll(@PathVariable("normId") Integer normId, @RequestBody String value,HttpServletRequest request, Model model) throws Exception {
		int id = 0;
		
		List<MeasureDescription> mesDescs = serviceMeasureDescription.getAllByNorm(normId);
		
		if (mesDescs != null) {
			
			if (!value.equals("")){
				ObjectMapper mapper = new ObjectMapper();
				JsonNode jsonNode = mapper.readTree(value);
				id = jsonNode.get("languageId").asInt();
				//System.out.println(id);
			}
			Language lang = null;
			if(id!=0) {
				lang = serviceLanguage.get(id);
			} else {
				lang = serviceLanguage.loadFromAlpha3("ENG");
			}
			for (MeasureDescription mesDesc : mesDescs) {
				mesDesc.getMeasureDescriptionTexts().clear();
				MeasureDescriptionText mesDescText = serviceMeasureDescriptionText.getByLanguage(mesDesc, lang);
				if (mesDescText == null) {
					mesDescText = new MeasureDescriptionText();
					mesDescText.setLanguage(lang);
				}
				mesDesc.addMeasureDescriptionText(mesDescText);
			}
			model.addAttribute("selectedLanguage", lang);	
			model.addAttribute("languages", serviceLanguage.loadAll());
			model.addAttribute("norm", serviceNorm.getNormByID(normId));
			model.addAttribute("measureDescriptions", mesDescs);
		}
		return "knowledgebase/standard/measure/measures";
	}

	/**
	 * 
	 * Display all MeasureDescriptions of a given Norm
	 * 
	 * */
	@RequestMapping("KnowledgeBase/Norm/{normId}/Measures/AddForm")
	public String displayAddForm(@PathVariable("normId") Integer normId, @RequestBody String value,HttpServletRequest request, Model model) throws Exception {
		
		List<Language> languages = serviceLanguage.loadAll();
		
		model.addAttribute("languages", languages);
		if (languages != null) {
			model.addAttribute("selectedLanguage", languages.get(0));
		}
			
		return "knowledgebase/standard/measure/measuredescriptionform";
	}
	
	/**
	 * 
	 * Perform edit Measure
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Standard/Norm/{normLabel}/Measures/Save")
	public String updateMeasureDescription(@PathVariable("measureid") Integer measureid, @PathVariable("normLabel") String normLabel,
			@ModelAttribute("measureDescription") @Valid MeasureDescription measureDescription, BindingResult result, RedirectAttributes redirectAttributes, Locale locale)
			throws Exception {
		if (measureDescription == null || !measureDescription.getNorm().getLabel().equals(normLabel) || measureDescription.getId() != measureid) {
			String msg = messageSource.getMessage("errors.measure.update.notrecognized", null, "Measure not recognized", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
		} else {
			try {
				serviceMeasureDescription.saveOrUpdate(measureDescription);
				String msg = messageSource.getMessage("success.measure.update.success", null, "Measure had been updated!", locale);
				redirectAttributes.addFlashAttribute("success", msg);
			} catch (Exception e) {
				String msg = messageSource.getMessage("errors.measure.update.fail", null, "Measure update failed!", locale);
				redirectAttributes.addFlashAttribute("errors", msg);
			}
		}
		return "redirect:/KnowLedgeBase/Standard/Norm/" + normLabel + "/Measures/Display";
	}

	/**
	 * 
	 * Delete single Norm
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Standard/Norm/{normLabel}/Measures/Delete/{measureid}")
	public String deleteMeasureDescription(@PathVariable("normLabel") String normLabel, @PathVariable("measureid") Integer measureid) throws Exception {
		if (serviceMeasureDescription.get(measureid).getNorm().getLabel().equals(normLabel)) {
			serviceMeasureDescription.remove(serviceMeasureDescription.get(measureid));
		}
		return "redirect:../Display";
	}

}
