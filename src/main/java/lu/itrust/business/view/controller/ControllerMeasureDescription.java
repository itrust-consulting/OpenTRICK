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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	public String displayAll(@PathVariable("normId") Integer normId,HttpServletRequest request, Map<String, Object> model) throws Exception {
		List<MeasureDescription> mesDescs = serviceMeasureDescription.getAllByNorm(normId);
		Language lang = null;
		if(request.getParameter("languageId")!=null) {
			lang = serviceLanguage.get(Integer.valueOf(request.getParameter("languageId")));
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
		model.put("norm", serviceNorm.getNormByID(normId).getLabel());
		model.put("measureDescriptions", mesDescs);
		return "knowledgebase/standard/measure/measures";
	}

	/**
	 * 
	 * Edit a Measure
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Standard/Norm/{normLabel}/Measures/Edit/{measureid}")
	public String editMeasureDescription(@PathVariable("normLabel") String normLabel, @PathVariable("measureid") Integer measureid, HttpSession session, Map<String, Object> model,
			RedirectAttributes redirectAttributes, Locale locale) throws Exception {
		MeasureDescription measureDescription = (MeasureDescription) session.getAttribute("measure");
		if (measureDescription == null || !measureDescription.getNorm().getLabel().equals(normLabel) || measureDescription.getId() != measureid)
			measureDescription = serviceMeasureDescription.get(measureid);
		if (measureDescription == null) {
			String msg = messageSource.getMessage("errors.measure.notexist", null, "Measure does not exist", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
			return "redirect:/KnowLedgeBase/Standard/Norm/" + normLabel + "/Measures/Display";
		}
		model.put("measureDescription", measureDescription);
		return "standard/editMeasureDescription";
	}

	/**
	 * 
	 * Perform edit Measure
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Standard/Norm/{normLabel}/Measures/Update/{measureid}")
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

	/**
	 * 
	 * Request add new Norm
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Standard/Norm/{normLabel}/Measures/Add")
	public String addMEasureDescription(Map<String, Object> model) {
		model.put("measureDescription", new MeasureDescription());
		return "standard/addMeasureDescription";
	}

	/**
	 * 
	 * Perform add new Measure
	 * 
	 * */
	@RequestMapping("KnowLedgeBase/Standard/Norm/{normLabel}/Measures/Create")
	public String createMeasure(@PathVariable("normLabel") String normLabel, @ModelAttribute("measureDescription") @Valid MeasureDescription measureDescription,
			BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Norm norm = serviceNorm.loadSingleNormByName(normLabel);

		measureDescription.setNorm(norm);

		HashMap<String, MeasureDescriptionText> measureDescriptionTexts = new HashMap<>();
		
		List<Language> languages = serviceLanguage.loadAll();
		
		for (int i = 0; i < languages.size();i++) {
			measureDescriptionTexts.put(languages.get(i).getAlpha3(), new MeasureDescriptionText());
		}
		
		for (Entry<String, String[]> parameter : request.getParameterMap().entrySet()) {

			// get the parameter name
			String parametername = parameter.getKey();
			
			// get the paramter value (or values if the same name was used)
			String[] parameterValues = parameter.getValue();
		
			// check if the paramter was a domain value field
			if (parametername.startsWith("domain_")) {
									
				String langLabel = parametername.substring(parametername.indexOf("_")+1, parametername.length());
				Language language = serviceLanguage.loadFromAlpha3(langLabel);
						
				if (language != null) {
					
					MeasureDescriptionText mesDescText = measureDescriptionTexts.get(language.getAlpha3());
					
					if (mesDescText.getLanguage() == null) {
						mesDescText.setLanguage(language);
					}
					
					if (parameterValues[0] == null) {
						mesDescText.setDomain("");
					} else {
						mesDescText.setDomain(parameterValues[0]);
					}
				}
							 
			}
			
			// check if the paramter was a description value field
			if (parametername.startsWith("description_")) {
									
				String langLabel = parametername.substring(parametername.indexOf("_")+1, parametername.length());
				Language language = serviceLanguage.loadFromAlpha3(langLabel);
						
				if (language != null) {
					
					MeasureDescriptionText mesDescText = measureDescriptionTexts.get(language.getAlpha3());
					
					if (mesDescText.getLanguage() == null) {
						mesDescText.setLanguage(language);
					}

					if (parameterValues[0] == null) {
						mesDescText.setDescription("");
					} else {
						mesDescText.setDescription(parameterValues[0]);
					}
				}
							 
			}

		}

		for (MeasureDescriptionText mesDescText: measureDescriptionTexts.values()) {
			measureDescription.addMeasureDescriptionText(mesDescText);
		}
			
		this.serviceMeasureDescription.save(measureDescription);
		return "redirect:./Display";
	}

}
