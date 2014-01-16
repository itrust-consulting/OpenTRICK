package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ControllerMeasureDescription.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since Oct 15, 2013
 */
@Secured("ROLE_CONSULTANT")
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
	public String displayAll(@PathVariable("normId") Integer normId, @RequestBody String value, HttpServletRequest request, Model model) throws Exception {
		int id = 0;

		List<MeasureDescription> mesDescs = serviceMeasureDescription.getAllByNorm(normId);

		if (mesDescs != null) {

			if (!value.equals("")) {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode jsonNode = mapper.readTree(value);
				id = jsonNode.get("languageId").asInt();
			}
			Language lang = null;
			if (id != 0) {
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
	public String displayAddForm(@PathVariable("normId") Integer normId, @RequestBody String value, HttpServletRequest request, Model model) throws Exception {

		List<Language> languages = serviceLanguage.loadAll();

		model.addAttribute("languages", languages);
		if (languages != null) {
			model.addAttribute("selectedLanguage", languages.get(0));
		}

		return "knowledgebase/standard/measure/measuredescriptionform";
	}

	/**
	 * 
	 * Display all MeasureDescriptions of a given Norm
	 * 
	 * */
	@RequestMapping("KnowledgeBase/Norm/{normId}/Measures/EditForm")
	public String displayEditForm(@PathVariable("normId") Integer normId, @RequestBody String value, HttpServletRequest request, Model model) throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(value);
		int measureId = jsonNode.get("measureId").asInt();
		
		List<Language> languages = serviceLanguage.loadAll();
		
		List<MeasureDescriptionText> mesDesc = serviceMeasureDescription.get(measureId).getMeasureDescriptionTexts();

		model.addAttribute("measuredescriptionTexts", mesDesc);
		model.addAttribute("languages", languages);
		if (languages != null) {
			model.addAttribute("selectedLanguage", languages.get(0));
		}

		return "knowledgebase/standard/measure/measuredescriptioneditform";
	}
	
	/**
	 * 
	 * Perform edit Measure
	 * 
	 * */
	@RequestMapping(value = "KnowledgeBase/Norm/{normId}/Measures/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	List<String[]> save(@PathVariable("normId") Integer normId, @RequestBody String value, HttpSession session, Principal principal, Locale locale) {
		List<String[]> errors = new LinkedList<>();
		try {

			Norm norm = serviceNorm.getNormByID(normId);
			
			MeasureDescription measureDescription = new MeasureDescription();
			
			measureDescription.setNorm(norm);
			
			if (!buildMeasureDescription(errors, measureDescription, value, locale))
				return errors;
		}

		catch (Exception e) {
			errors.add(new String[] { "measuredescription", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}
		return errors;
	}

	private boolean buildMeasureDescription(List<String[]> errors, MeasureDescription measuredescription, String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			int id = jsonNode.get("id").asInt();
			
			measuredescription.setReference(jsonNode.get("reference").asText());
			measuredescription.setLevel(jsonNode.get("level").asInt());
			
			if (id > 0) {
				measuredescription.setId(jsonNode.get("id").asInt());
			} else {
				serviceMeasureDescription.save(measuredescription);
			}
			

			List<Language> languages = serviceLanguage.loadAll();
			
			for (int i=0;i<languages.size();i++){
				
				Language language = languages.get(i);
				
				String domain = jsonNode.get("domain_"+language.getId()).asText();
				String description = jsonNode.get("description_"+language.getId()).asText();
				
				MeasureDescriptionText mesDescText = null;
				
				mesDescText = serviceMeasureDescriptionText.getByLanguage(measuredescription, language);
				
				if (id<1 || mesDescText == null){
				
					mesDescText = new MeasureDescriptionText();
									
					mesDescText.setMeasureDescription(measuredescription);
					
					mesDescText.setLanguage(language);
					
					mesDescText.setDomain(domain);
					
					mesDescText.setDescription(description);
					
					serviceMeasureDescriptionText.save(mesDescText);
					
				} else {
					mesDescText.setDomain(domain);
					mesDescText.setDescription(description);
					serviceMeasureDescriptionText.saveOrUpdate(mesDescText);
				}
							
				measuredescription.addMeasureDescriptionText(mesDescText);
								
			}
					
			return true;

		} catch (Exception e) {

			errors.add(new String[] { "buildMeasureDescription", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 
	 * Delete single language
	 * 
	 * */
	@RequestMapping(value = "KnowledgeBase/Norm/{normId}/Measures/Delete/{measureid}", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String[] deleteMeasureDescription(@PathVariable("normId") Integer normId, @PathVariable("measureid") Integer measureid, Locale locale) throws Exception {
		if (serviceMeasureDescription.get(measureid).getNorm().getId()==normId) {
			serviceMeasureDescription.remove(serviceMeasureDescription.get(measureid));
		}
		return new String[] { "error", messageSource.getMessage("success.measure.delete.successfully", null, "Measure was deleted successfully", locale) };

	}

}
