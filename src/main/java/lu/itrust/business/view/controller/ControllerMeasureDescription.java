package lu.itrust.business.view.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.TS.Standard;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.ComparatorMeasureDescription;
import lu.itrust.business.component.CustomDelete;
import lu.itrust.business.component.MeasureManager;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasureDescription;
import lu.itrust.business.service.ServiceMeasureDescriptionText;
import lu.itrust.business.service.ServiceStandard;
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
	private ServiceStandard serviceStandard;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceDataValidation serviceDataValidation;
	
	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	MeasureManager measureManager;

	/**
	 * displayAll: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param idLanguage
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("KnowledgeBase/Standard/{idStandard}/Language/{idLanguage}/Measures")
	public String displayAll(@PathVariable int idStandard, @PathVariable int idLanguage, HttpServletRequest request, Model model) throws Exception {

		// load all measuredescriptions of a standard
		List<MeasureDescription> mesDescs = serviceMeasureDescription.getAllByStandard(idStandard);

		// chekc if measuredescriptions are not null
		if (mesDescs != null) {

			// set language
			Language lang = null;
			if (idLanguage != 0) {
				lang = serviceLanguage.get(idLanguage);
			} else {
				lang = serviceLanguage.getByAlpha3("ENG");
			}

			// parse measuredescriptions and remove texts to add only selected
			// language text
			for (MeasureDescription mesDesc : mesDescs) {

				mesDesc.setMeasureDescriptionTexts(new ArrayList<MeasureDescriptionText>());
				
				// load only from language
				MeasureDescriptionText mesDescText = serviceMeasureDescriptionText.getForMeasureDescriptionAndLanguage(mesDesc.getId(), lang.getId());

				// check if not null
				if (mesDescText == null) {

					// create new empty descriptiontext with language
					mesDescText = new MeasureDescriptionText();
					mesDescText.setLanguage(lang);
				}

				mesDesc.addMeasureDescriptionText(mesDescText);
				
				//System.out.println(mesDescText.getDomain() + "::" + mesDescText.getDescription());
				
			}
			Collections.sort(mesDescs, new ComparatorMeasureDescription());
			// put data to model
			model.addAttribute("selectedLanguage", lang);
			model.addAttribute("languages", serviceLanguage.getAll());
			model.addAttribute("standard", serviceStandard.get(idStandard));
			model.addAttribute("measureDescriptions", mesDescs);
		}
		return "knowledgebase/standards/measure/measures";
	}

	/**
	 * displayAll: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param value
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("KnowledgeBase/Standard/{idStandard}/Language/{idLanguage}/Measures/{idMeasure}")
	public String displaySingle(@PathVariable int idStandard, @PathVariable int idLanguage, @PathVariable int idMeasure, HttpServletRequest request, HttpServletResponse response,
			Model model, Locale locale) throws Exception {

		// load all measuredescriptions of a standard
		MeasureDescription mesDesc = serviceMeasureDescription.get(idMeasure);

		// chekc if measuredescriptions are not null
		if (mesDesc != null) {

			// set language
			Language lang = null;
			if (idLanguage != 0) {
				lang = serviceLanguage.get(idLanguage);
			} else {
				lang = serviceLanguage.getByAlpha3("ENG");
			}

			// load only from language
			MeasureDescriptionText mesDescText = serviceMeasureDescriptionText.getForMeasureDescriptionAndLanguage(mesDesc.getId(), lang.getId());

			// put data to model
			model.addAttribute("standard", serviceStandard.get(idStandard));
			model.addAttribute("measureDescription", mesDesc);
			model.addAttribute("measureDescriptionText", mesDescText);
		}

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		return "knowledgebase/standards/measure/measure";
	}

	/**
	 * displayAddForm: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param value
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("KnowledgeBase/Standard/{idStandard}/Measures/Add")
	public String displayAddForm(@PathVariable("idStandard") Integer idStandard, HttpServletRequest request, Model model) throws Exception {

		// load all languages
		List<Language> languages = serviceLanguage.getAll();

		// add languages to model
		model.addAttribute("languages", languages);

		// select first language
		if (languages != null) {
			model.addAttribute("selectedLanguage", languages.get(0));
		}

		return "knowledgebase/standards/measure/measuredescriptionform";
	}

	/**
	 * displayEditForm: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param value
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("KnowledgeBase/Standard/{idStandard}/Measures/{idMeasure}/Edit")
	public String displayEditForm(@PathVariable("idStandard") Integer idStandard, @PathVariable int idMeasure, HttpServletRequest request, Model model) throws Exception {

		// load all languages
		List<Language> languages = serviceLanguage.getAll();

		// load measuredescriptiontexts of measuredescription
		List<MeasureDescriptionText> mesDesc = serviceMeasureDescription.get(idMeasure).getMeasureDescriptionTexts();

		MeasureDescription md = null;

		if (mesDesc.isEmpty())
			return "knowledgebase/standards/measure/measuredescriptioneditform";

		md = mesDesc.get(0).getMeasureDescription();

		// add languages to model
		model.addAttribute("languages", languages);

		for (Language lang : languages) {
			boolean found = false;
			for (MeasureDescriptionText mdt : mesDesc) {
				if (mdt.getLanguage().getId() == lang.getId()) {
					found = true;
					break;
				}
			}
			if (!found) {
				MeasureDescriptionText mdt = new MeasureDescriptionText();
				mdt.setLanguage(lang);
				mdt.setMeasureDescription(md);
				mesDesc.add(mdt);
			}
		}

		// add texts to model
		model.addAttribute("measuredescriptionTexts", mesDesc);

		// add to model: first language as selected
		if (languages != null) {
			model.addAttribute("selectedLanguage", languages.get(0));
		}

		return "knowledgebase/standards/measure/measuredescriptioneditform";
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param value
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "KnowledgeBase/Standard/{idStandard}/Measures/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody Map<String, String> save(@PathVariable("idStandard") int idStandard, @RequestBody String value, Locale locale) {
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
				// retrieve standard
				measureDescription = new MeasureDescription();
				Standard standard = serviceStandard.get(idStandard);
				if (standard == null)
					errors.put("measureDescription.norm", messageSource.getMessage("error.norm.not_found", null, "Standard is not exist", locale));
				measureDescription.setStandard(standard);
			} else if (measureDescription.getStandard().getId() != idStandard)
				errors.put("measureDescription.norm",
						messageSource.getMessage("error.measure_description.norm.not_matching", null, "Measure description or standard is not exist", locale));

			if (errors.isEmpty() && buildMeasureDescription(errors, measureDescription, value, locale)) {
				serviceMeasureDescription.saveOrUpdate(measureDescription);

				measureManager.createNewMeasureForAllAnalyses(measureDescription);
			}

			// System.out.println(measureDescription.isComputable()==true?"TRUE":"FALSE");

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
	 * @param idStandard
	 * @param measureid
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "KnowledgeBase/Standard/{idStandard}/Measures/Delete/{idMeasure}", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody String deleteMeasureDescription(@PathVariable("idStandard") int idStandard, @PathVariable("idMeasure") int idMeasure, Locale locale) {
		try {
			// try to delete measure
			MeasureDescription measureDescription = serviceMeasureDescription.get(idMeasure);
			if (measureDescription == null || measureDescription.getStandard().getId() != idStandard)
				return JsonMessage.Error(messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", locale));
			customDelete.delete(measureDescription);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.measure.delete.successfully", null, "Measure was deleted successfully", locale));
		} catch (Exception e) {
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.measure.delete.failed", null, "Measure deleting was failed: Standard might be in used", locale));
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
			Boolean computable = jsonNode.get("computable").asText().equals("on") ? true : false;
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
				if (measuredescription.getId() < 1 && serviceMeasureDescription.existsForMeasureByReferenceAndStandard(reference, measuredescription.getStandard()))
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
					// int count = 1;
					// for (int i = 0; i < reference.length(); i++)
					// if (reference.charAt(i) == '.')
					// count++;
					// if (count != level)
					// errors.put("measuredescription.level",
					// messageSource.getMessage("error.measuredescription.level.reference.not_meet",
					// null, "Level and reference do not match", locale));
					// else
					measuredescription.setLevel(level);
				}
			}

			error = serviceDataValidation.validate(measuredescription, "computable", computable);

			if (error != null)
				errors.put("measuredescription.computable", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				measuredescription.setComputable(computable);

			// load languages
			List<Language> languages = serviceLanguage.getAll();

			ValidatorField validator = serviceDataValidation.findByClass(MeasureDescriptionText.class);

			// parse all languages
			for (int i = 0; i < languages.size(); i++) {

				// get language
				Language language = languages.get(i);

				// get domain in this language
				String domain = jsonNode.get("domain_" + language.getId()).asText().trim();

				// get description in this language
				String description = jsonNode.get("description_" + language.getId()).asText().trim();

				if (domain.equals(Constant.EMPTY_STRING) && description.equals(Constant.EMPTY_STRING))
					continue;

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

				if (level == 3 && !(measuredescription.getStandard().getLabel().equals("27001") && measuredescription.getStandard().getVersion() == 2013))
					error = validator.validate(mesDescText, "description", description);
				else
					error = null;
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