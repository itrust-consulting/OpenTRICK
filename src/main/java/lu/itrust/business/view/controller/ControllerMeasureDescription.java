package lu.itrust.business.view.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasureDescription;
import lu.itrust.business.service.ServiceMeasureDescriptionText;
import lu.itrust.business.service.ServiceNorm;

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
	@RequestMapping("KnowledgeBase/Norm/{normId}/Measures")
	public String displayAll(@PathVariable("normId") Integer normId, @RequestBody String value, HttpServletRequest request, Model model) throws Exception {
		int id = 0;

		// load all measuredescriptions of a norm
		List<MeasureDescription> mesDescs = serviceMeasureDescription.getAllByNorm(normId);

		// chekc if measuredescriptions are not null
		if (mesDescs != null) {

			// parse data sent
			if (!value.equals("")) {

				// retrieve language id
				ObjectMapper mapper = new ObjectMapper();
				JsonNode jsonNode = mapper.readTree(value);
				id = jsonNode.get("languageId").asInt();
			}

			// set language
			Language lang = null;
			if (id != 0) {
				lang = serviceLanguage.get(id);
			} else {
				lang = serviceLanguage.loadFromAlpha3("ENG");
			}

			// parse measuredescriptions and remove texts to add only selected language text
			for (MeasureDescription mesDesc : mesDescs) {

				// remove all descriptiontexts
				mesDesc.getMeasureDescriptionTexts().clear();

				// load only from language
				MeasureDescriptionText mesDescText = serviceMeasureDescriptionText.getByLanguage(mesDesc, lang);

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
	@RequestMapping("KnowledgeBase/Norm/{normId}/Measures/AddForm")
	public String displayAddForm(@PathVariable("normId") Integer normId, @RequestBody String value, HttpServletRequest request, Model model) throws Exception {

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
	@RequestMapping("KnowledgeBase/Norm/{normId}/Measures/EditForm")
	public String displayEditForm(@PathVariable("normId") Integer normId, @RequestBody String value, HttpServletRequest request, Model model) throws Exception {

		// create json parser
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(value);

		// retrieve measure id
		int measureId = jsonNode.get("measureId").asInt();

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
	List<String[]> save(@PathVariable("normId") Integer normId, @RequestBody String value, Locale locale) {

		// create error list
		List<String[]> errors = new LinkedList<>();

		try {

			// retrieve norm
			Norm norm = serviceNorm.getNormByID(normId);

			// create new empty measuredescription object
			MeasureDescription measureDescription = new MeasureDescription();

			// add measure to norm
			measureDescription.setNorm(norm);

			// try to build the object with json data
			buildMeasureDescription(errors, measureDescription, value, locale);

			// return errors
			return errors;
		}

		catch (Exception e) {

			// return errors
			errors.add(new String[] { "measuredescription", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
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
	String[] deleteMeasureDescription(@PathVariable("normId") Integer normId, @PathVariable("measureid") Integer measureid, Locale locale) throws Exception {

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
	private boolean buildMeasureDescription(List<String[]> errors, MeasureDescription measuredescription, String source, Locale locale) {

		try {

			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			// retrieve measure id
			int id = jsonNode.get("id").asInt();

			// set measure reference
			measuredescription.setReference(jsonNode.get("reference").asText());

			// set measure level
			measuredescription.setLevel(jsonNode.get("level").asInt());

			// check if measure is new or to update
			if (id > 0) {

				// set id
				measuredescription.setId(jsonNode.get("id").asInt());
			} else {

				// create measure
				serviceMeasureDescription.save(measuredescription);
			}

			// load languages
			List<Language> languages = serviceLanguage.loadAll();

			// parse all languages
			for (int i = 0; i < languages.size(); i++) {

				// get language
				Language language = languages.get(i);

				// get domain in this language
				String domain = jsonNode.get("domain_" + language.getId()).asText();

				// get description in this language
				String description = jsonNode.get("description_" + language.getId()).asText();

				// init measdesctext object
				MeasureDescriptionText mesDescText = null;

				// try to load existing texts of this language
				mesDescText = serviceMeasureDescriptionText.getByLanguage(measuredescription, language);

				// if new measure or text for this language does not exist: create new text and save
				if (id < 1 || mesDescText == null) {

					// create new and add data
					mesDescText = new MeasureDescriptionText();
					mesDescText.setMeasureDescription(measuredescription);
					mesDescText.setLanguage(language);
					mesDescText.setDomain(domain);
					mesDescText.setDescription(description);

					// save
					serviceMeasureDescriptionText.save(mesDescText);

				} else {

					// update existing text
					mesDescText.setDomain(domain);
					mesDescText.setDescription(description);

					// update object
					serviceMeasureDescriptionText.saveOrUpdate(mesDescText);
				}

				// add text to object
				measuredescription.addMeasureDescriptionText(mesDescText);
			}

			// return success message
			return true;

		} catch (Exception e) {

			// return error message
			errors.add(new String[] { "buildMeasureDescription", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
			return false;
		}
	}
}