package lu.itrust.business.view.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasureDescription;
import lu.itrust.business.service.ServiceMeasureDescriptionText;
import lu.itrust.business.service.ServiceNorm;
import lu.itrust.business.validator.MeasureDescriptionTextValidator;
import lu.itrust.business.validator.MeasureDescriptionValidator;
import lu.itrust.business.validator.field.ValidatorField;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize(Constant.ROLE_MIN_CONSULTANT)
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
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private MessageSource messageSource;

	/**
	 * displayAll: <br>
	 * Description
	 * 
	 * @param normId
	 * @param value
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("KnowledgeBase/Norm/{normId}/language/{idLanguage}/Measures")
	public String displayAll(@PathVariable int normId, @PathVariable int idLanguage, HttpServletRequest request, Model model) throws Exception {

		// load all measuredescriptions of a norm
		List<MeasureDescription> mesDescs = serviceMeasureDescription.getAllByNorm(normId);

		// chekc if measuredescriptions are not null
		if (mesDescs != null) {

			// set language
			Language lang = null;
			if (idLanguage != 0) {
				lang = serviceLanguage.get(idLanguage);
			} else {
				lang = serviceLanguage.loadFromAlpha3("ENG");
			}

			// parse measuredescriptions and remove texts to add only selected
			// language text
			for (MeasureDescription mesDesc : mesDescs) {

				// remove all descriptiontexts
				mesDesc.getMeasureDescriptionTexts().clear();

				// load only from language
				MeasureDescriptionText mesDescText = serviceMeasureDescriptionText.getByLanguage(mesDesc.getId(), lang.getId());

				// check if not null
				if (mesDescText == null) {

					// create new empty descriptiontext with language
					mesDescText = new MeasureDescriptionText();
					mesDescText.setLanguage(lang);
				}

				// add to measuredescription
				mesDesc.addMeasureDescriptionText(mesDescText);
			}

			// put data to model
			model.addAttribute("selectedLanguage", lang);
			model.addAttribute("languages", serviceLanguage.loadAll());
			model.addAttribute("norm", serviceNorm.getNormByID(normId));
			model.addAttribute("measureDescriptions", mesDescs);
		}
		return "knowledgebase/standard/measure/measures";
	}

	/**
	 * displayAll: <br>
	 * Description
	 * 
	 * @param normId
	 * @param value
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("KnowledgeBase/Norm/{normId}/language/{idLanguage}/Measures/{idMeasure}")
	public String displaySingle(@PathVariable int normId, @PathVariable int idLanguage, @PathVariable int idMeasure, HttpServletRequest request, Model model) throws Exception {

		// load all measuredescriptions of a norm
		MeasureDescription mesDesc = serviceMeasureDescription.get(idMeasure);

		// chekc if measuredescriptions are not null
		if (mesDesc != null) {

			// set language
			Language lang = null;
			if (idLanguage != 0) {
				lang = serviceLanguage.get(idLanguage);
			} else {
				lang = serviceLanguage.loadFromAlpha3("ENG");
			}

			// load only from language
			MeasureDescriptionText mesDescText = serviceMeasureDescriptionText.getByLanguage(mesDesc.getId(), lang.getId());

			

			// put data to model
			model.addAttribute("norm", serviceNorm.getNormByID(normId));
			model.addAttribute("measureDescription", mesDesc);
			model.addAttribute("measureDescriptionText", mesDescText);
		}
		return "knowledgebase/standard/measure/measure";
	}
	
	/**
	 * displayAddForm: <br>
	 * Description
	 * 
	 * @param normId
	 * @param value
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("KnowledgeBase/Norm/{normId}/Measures/Add")
	public String displayAddForm(@PathVariable("normId") Integer normId, HttpServletRequest request, Model model) throws Exception {

		// load all languages
		List<Language> languages = serviceLanguage.loadAll();

		// add languages to model
		model.addAttribute("languages", languages);

		// select first language
		if (languages != null) {
			model.addAttribute("selectedLanguage", languages.get(0));
		}

		return "knowledgebase/standard/measure/measuredescriptionform";
	}

	/**
	 * displayEditForm: <br>
	 * Description
	 * 
	 * @param normId
	 * @param value
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("KnowledgeBase/Norm/{normId}/Measures/{measureId}/Edit")
	public String displayEditForm(@PathVariable("normId") Integer normId, @PathVariable int measureId, HttpServletRequest request, Model model) throws Exception {

		// load all languages
		List<Language> languages = serviceLanguage.loadAll();

		// load measuredescriptiontexts of measuredescription
		List<MeasureDescriptionText> mesDesc = serviceMeasureDescription.get(measureId).getMeasureDescriptionTexts();

		// add texts to model
		model.addAttribute("measuredescriptionTexts", mesDesc);

		// add languages to model
		model.addAttribute("languages", languages);

		// add to model: first language as selected
		if (languages != null) {
			model.addAttribute("selectedLanguage", languages.get(0));
		}

		return "knowledgebase/standard/measure/measuredescriptioneditform";
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param normId
	 * @param value
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "KnowledgeBase/Norm/{normId}/Measures/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	Map<String, String> save(@PathVariable("normId") int normId, @RequestBody String value, Locale locale) {
		// create error list
		Map<String, String> errors = new LinkedHashMap<String, String>();
		try {
			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(value);

			// retrieve measure id
			int id = jsonNode.get("id").asInt();

			// create new empty measuredescription object
			MeasureDescription measureDescription = serviceMeasureDescription.get(id);

			if (measureDescription == null) {
				// retrieve norm
				measureDescription = new MeasureDescription();
				Norm norm = serviceNorm.getNormByID(normId);
				if (norm == null)
					errors.put("measureDescription.norm", messageSource.getMessage("error.norm.not_found", null, "Standard is not exist", locale));
				measureDescription.setNorm(norm);
			} else if (measureDescription.getNorm().getId() != normId)
				errors.put("measureDescription.norm",
						messageSource.getMessage("error.measure_description.norm.not_matching", null, "Measure description or standard is not exist", locale));

			if (errors.isEmpty() && buildMeasureDescription(errors, measureDescription, value, locale))
				serviceMeasureDescription.saveOrUpdate(measureDescription);
			
			//System.out.println(measureDescription.isComputable()==true?"TRUE":"FALSE");
			
			// return errors
			return errors;
		}

		catch (Exception e) {

			// return errors
			errors.put("measuredescription", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return errors;
		}

	}

	/**
	 * deleteMeasureDescription: <br>
	 * Description
	 * 
	 * @param normId
	 * @param measureid
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "KnowledgeBase/Norm/{normId}/Measures/Delete/{measureid}", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String[] deleteMeasureDescription(@PathVariable("normId") int normId, @PathVariable("measureid") int measureid, Locale locale) throws Exception {

		try {

			// try to delete measure
			if (serviceMeasureDescription.get(measureid).getNorm().getId() == normId)
				serviceMeasureDescription.remove(serviceMeasureDescription.get(measureid));

			// return success message
			return new String[] { "success", messageSource.getMessage("success.measure.delete.successfully", null, "Measure was deleted successfully", locale) };

		} catch (Exception e) {

			// return error
			e.printStackTrace();
			return new String[] { "error", messageSource.getMessage("error.measure.delete.failed", null, "Measure deletion failed", locale) };
		}
	}

	/**
	 * buildMeasureDescription: <br>
	 * Description
	 * 
	 * @param errors
	 * @param measuredescription
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildMeasureDescription(Map<String, String> errors, MeasureDescription measuredescription, String source, Locale locale) {
		try {
			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			String reference = jsonNode.get("reference").asText();
			Integer level = null;
			Boolean computable = jsonNode.get("computable").asText().equals("on")?true:false;
			try {
				level = jsonNode.get("level").asInt();
			} catch (Exception e) {
			}
			if (!serviceDataValidation.isRegistred(MeasureDescription.class))
				serviceDataValidation.register(new MeasureDescriptionValidator());

			if (!serviceDataValidation.isRegistred(MeasureDescriptionText.class))
				serviceDataValidation.register(new MeasureDescriptionTextValidator());

			String error = serviceDataValidation.validate(measuredescription, "reference", reference);

			if (error != null)
				errors.put("measuredescription.reference", serviceDataValidation.ParseError(error, messageSource, locale));
			else {
				if (measuredescription.getId() < 1 && serviceMeasureDescription.exists(reference, measuredescription.getNorm()))
					errors.put("measuredescription.reference",
							messageSource.getMessage("error.measuredescription.reference.duplicate", null, "Reference already exists in this standard", locale));
				else
					measuredescription.setReference(reference);
			}

			error = serviceDataValidation.validate(measuredescription, "level", level);

			if (error != null)
				errors.put("measuredescription.level", serviceDataValidation.ParseError(error, messageSource, locale));
			else {
				if (!errors.containsKey("measuredescription.reference")) {
//					int count = 1;
//					for (int i = 0; i < reference.length(); i++)
//						if (reference.charAt(i) == '.')
//							count++;
//					if (count != level)
//						errors.put("measuredescription.level", messageSource.getMessage("error.measuredescription.level.reference.not_meet", null, "Level and reference do not match", locale));
//					else
						measuredescription.setLevel(level);
				}
			}

			error = serviceDataValidation.validate(measuredescription, "computable", computable);

			if (error != null)
				errors.put("measuredescription.computable", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				measuredescription.setComputable(computable);
			
			
			// load languages
			List<Language> languages = serviceLanguage.loadAll();

			ValidatorField validator = serviceDataValidation.findByClass(MeasureDescriptionText.class);

			// parse all languages
			for (int i = 0; i < languages.size(); i++) {

				// get language
				Language language = languages.get(i);

				// get domain in this language
				String domain = jsonNode.get("domain_" + language.getId()).asText();

				// get description in this language
				String description = jsonNode.get("description_" + language.getId()).asText();

				// init measdesctext object
				MeasureDescriptionText mesDescText = measuredescription.findByLanguage(language);

				// if new measure or text for this language does not exist:
				// create new text and save
				if (mesDescText == null) {
					// create new and add data
					mesDescText = new MeasureDescriptionText();
					mesDescText.setLanguage(language);
					measuredescription.addMeasureDescriptionText(mesDescText);
				}
				
				error = validator.validate(mesDescText, "domain", domain);

				if (error != null)
					errors.put("measureDescriptionText.domain_" + language.getId(), serviceDataValidation.ParseError(error, messageSource, locale));
				else
					mesDescText.setDomain(domain);
				
				if (level==3 && !(measuredescription.getNorm().getLabel().equals("27001") && measuredescription.getNorm().getVersion()==2013))
					error = validator.validate(mesDescText, "description", description);
				else
					error=null;
				if (error != null)
					errors.put("measureDescriptionText.description_" + language.getId(), serviceDataValidation.ParseError(error, messageSource, locale));
				else
					mesDescText.setDescription(description);
			}
			// return success message
			return errors.isEmpty();

		} catch (Exception e) {

			// return error message
			errors.put("measureDescription", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return false;
		}
	}
}