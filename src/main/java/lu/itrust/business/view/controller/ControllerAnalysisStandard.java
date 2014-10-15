package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisStandard;
import lu.itrust.business.TS.AssetStandard;
import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MaturityMeasure;
import lu.itrust.business.TS.MaturityStandard;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.TS.MeasureProperties;
import lu.itrust.business.TS.NormalMeasure;
import lu.itrust.business.TS.NormalStandard;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.Phase;
import lu.itrust.business.TS.Standard;
import lu.itrust.business.TS.StandardType;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.ComparatorMeasureDescription;
import lu.itrust.business.component.MeasureManager;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.exception.TrickException;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAnalysisStandard;
import lu.itrust.business.service.ServiceAsset;
import lu.itrust.business.service.ServiceAssetType;
import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.service.ServiceMeasureDescription;
import lu.itrust.business.service.ServiceMeasureDescriptionText;
import lu.itrust.business.service.ServiceStandard;
import lu.itrust.business.validator.MeasureDescriptionTextValidator;
import lu.itrust.business.validator.MeasureDescriptionValidator;
import lu.itrust.business.validator.StandardValidator;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ControllerAnalysisStandard.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since Oct 13, 2014
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/Analysis/Standard")
public class ControllerAnalysisStandard {

	@Autowired
	private MeasureManager measureManager;

	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceStandard serviceStandard;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceMeasureDescription serviceMeasureDescription;

	@Autowired
	private ServiceMeasureDescriptionText serviceMeasureDescriptionText;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@RequestMapping(value = "/Manage", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public String addStandardForm(HttpSession session, Principal principal, Model model, RedirectAttributes attributes, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		List<Standard> standards = serviceStandard.getAllNotInAnalysis(idAnalysis);
		model.addAttribute("standards", standards);
		model.addAttribute("currentStandards", serviceStandard.getAllFromAnalysisNotBound(idAnalysis));
		model.addAttribute("currentAnalysisStandards", serviceStandard.getAllAnalysisOnlyStandardsFromAnalysis(idAnalysis));
		model.addAttribute("idAnalysis", idAnalysis);
		return "analysis/components/forms/standard";
	}

	@RequestMapping(value = "/Delete/{idStandard}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody String removeStandard(@PathVariable int idStandard, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		measureManager.removeStandardFromAnalysis(idAnalysis, idStandard);
		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
		return JsonMessage
				.Success(messageSource.getMessage("success.analysis.norm.delete", null, "Standard was successfully removed from your analysis", customLocale != null ? customLocale : locale));
	}

	@RequestMapping(value = "/Create", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, String> createStandardForm(@RequestBody String value, HttpSession session, Principal principal, Model model, RedirectAttributes attributes, Locale locale)
			throws Exception {

		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {

			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis == null) {
				errors.put("standard", messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale));
				return errors;
			}

			Analysis analysis = serviceAnalysis.get(idAnalysis);

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));

			// create new standard object
			Standard standard = new Standard();

			// build standard
			buildStandard(errors, standard, value, customLocale != null ? customLocale : locale, analysis);

			if (!errors.isEmpty())
				// return error on failure
				return errors;

			serviceStandard.save(standard);

			AnalysisStandard astandard = null;

			switch (standard.getType()) {
				case ASSET:
					astandard = new AssetStandard(standard);
					break;
				case MATURITY:
					astandard = new MaturityStandard(standard);
					break;
				case NORMAL:
				default:
					astandard = new NormalStandard(standard);
					break;
			}

			if (astandard != null) {
				analysis.addAnalysisStandard(astandard);
				serviceAnalysis.saveOrUpdate(analysis);
			}

			errors.put("success", messageSource.getMessage("success.analysis.create.standard", null, "The standard was successfully created", customLocale != null ? customLocale : locale));

		} catch (TrickException e) {
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis != null) {
				Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
				errors.put("standard", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), customLocale != null ? customLocale : locale));
			} else
				errors.put("standard", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			e.printStackTrace();
			return errors;
		} catch (Exception e) {
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis != null) {
				Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
				errors.put("standard", messageSource.getMessage(e.getMessage(), null, customLocale != null ? customLocale : locale));
			} else
				errors.put("standard", messageSource.getMessage(e.getMessage(), null, locale));
			e.printStackTrace();
			return errors;
		}
		return errors;
	}

	/**
	 * buildStandard: <br>
	 * Description
	 * 
	 * @param errors
	 * @param standard
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildStandard(Map<String, String> errors, Standard standard, String source, Locale locale, Analysis analysis) {

		try {

			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			ValidatorField validator = serviceDataValidation.findByClass(Standard.class);

			if (validator == null)
				serviceDataValidation.register(validator = new StandardValidator());

			String label = jsonNode.get("label").asText();

			String description = jsonNode.get("description").asText();

			StandardType type = StandardType.getByName(jsonNode.get("type").asText());

			Integer version = null;

			try {
				version = jsonNode.get("version").asInt();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			// set data
			String error = validator.validate(standard, "label", label);
			if (error != null)
				errors.put("label", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				standard.setLabel(label);

			error = validator.validate(standard, "version", version);

			if (error != null)
				errors.put("version", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				standard.setVersion(version);

			error = validator.validate(standard, "description", description);

			if (error != null)
				errors.put("description", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				standard.setDescription(description);

			error = validator.validate(standard, "type", type);

			if (error != null)
				errors.put("type", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				standard.setType(type);

			// set computable flag
			standard.setComputable(jsonNode.get("computable").asText().equals("on"));

			standard.setAnalysis(analysis);

			// return success
			return errors.isEmpty();

		} catch (Exception e) {
			// return error
			errors.put("standard", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return false;
		}
	}

	@RequestMapping(value = "/Save/{idStandard}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody String addStandard(@PathVariable int idStandard, HttpSession session, Principal principal, Locale locale) throws Exception {
		try {

			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));

			Standard standard = serviceStandard.get(idStandard);
			if (standard == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.add.standard.not_found", null, "Unfortunately, selected standard does not exist", customLocale != null ? customLocale
					: locale));

			Analysis analysis = serviceAnalysis.get(idAnalysis);
			Measure measure = null;
			AnalysisStandard analysisStandard = null;
			List<MeasureDescription> measureDescriptions = serviceMeasureDescription.getAllByStandard(standard);
			Object implementationRate = null;

			if (standard.getType() == StandardType.MATURITY) {
				analysisStandard = new MaturityStandard();
				measure = new MaturityMeasure();
				for (Parameter parameter : analysis.getParameters()) {
					if (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME) && parameter.getValue() == 0) {
						implementationRate = parameter;
						break;
					}
				}
			} else if (standard.getType() == StandardType.NORMAL) {
				analysisStandard = new NormalStandard();
				measure = new NormalMeasure();
				List<AssetType> assetTypes = serviceAssetType.getAllFromAnalysis(idAnalysis);
				List<AssetTypeValue> assetTypeValues = ((NormalMeasure) measure).getAssetTypeValues();
				for (AssetType assetType : assetTypes)
					assetTypeValues.add(new AssetTypeValue(assetType, 0));
				((NormalMeasure) measure).setMeasurePropertyList(new MeasureProperties());
				implementationRate = new Double(0);
			}
			Phase phase = analysis.findPhaseByNumber(Constant.PHASE_DEFAULT);
			if (phase == null)
				analysis.addUsedPhase(phase = new Phase(Constant.PHASE_DEFAULT));
			measure.setPhase(phase);
			analysisStandard.setAnalysis(analysis);
			analysisStandard.setStandard(standard);
			measure.setStatus(Constant.MEASURE_STATUS_APPLICABLE);
			measure.setImplementationRate(implementationRate);
			for (MeasureDescription measureDescription : measureDescriptions) {
				Measure measure2 = measure.duplicate();
				measure2.setMeasureDescription(measureDescription);
				measure2.setAnalysisStandard(analysisStandard);
				analysisStandard.getMeasures().add(measure2);
			}
			analysis.addAnalysisStandard(analysisStandard);

			serviceAnalysis.saveOrUpdate(analysis);

			return JsonMessage.Success(messageSource.getMessage("success.analysis.add.standard", null, "The standard was successfully added", customLocale != null ? customLocale : locale));
		} catch (TrickException e) {
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
			return JsonMessage.Success(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), customLocale != null ? customLocale : locale));
		} catch (Exception e) {
			e.printStackTrace();
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
			return JsonMessage.Error(messageSource.getMessage("error.analysis.add.standard", null, "An unknown error occurred during analysis saving", customLocale != null ? customLocale : locale));
		}
	}

	@RequestMapping("/Show/{idStandard}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public String showMeasures(@PathVariable int idStandard, HttpSession session, Principal principal, Locale locale, Model model) throws Exception {

		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		// load all measuredescriptions of a standard
		List<MeasureDescription> mesDescs = serviceMeasureDescription.getAllByStandard(idStandard);

		// chekc if measuredescriptions are not null
		if (mesDescs != null) {

			// set language
			Language lang = serviceAnalysis.getLanguageOfAnalysis(idAnalysis);

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

				// System.out.println(mesDescText.getDomain() + "::" +
				// mesDescText.getDescription());

			}
			Collections.sort(mesDescs, new ComparatorMeasureDescription());
			// put data to model
			model.addAttribute("languages", new ArrayList<Language>());
			model.addAttribute("standard", serviceStandard.get(idStandard));
			model.addAttribute("measureDescriptions", mesDescs);
		}
		return "knowledgebase/standards/measure/measures";
	}

	@RequestMapping(value = "/{idStandard}/Measure/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, String> saveMeasure(@PathVariable("idStandard") int idStandard, HttpSession session, Principal principal, @RequestBody String value, Locale locale) {
		// create error list
		Map<String, String> errors = new LinkedHashMap<String, String>();
		try {
			
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			
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
					errors.put("norm", messageSource.getMessage("error.norm.not_found", null, "Standard does not exist", locale));
				measureDescription.setStandard(standard);
			} else if (measureDescription.getStandard().getId() != idStandard)
				errors.put("norm", messageSource.getMessage("error.measure_description.norm.not_matching", null, "Measure description does not belong to given standard", locale));

			if (errors.isEmpty() && buildMeasureDescription(errors, measureDescription, value, locale, idAnalysis)) {
				serviceMeasureDescription.saveOrUpdate(measureDescription);

				measureManager.createNewMeasureForAllAnalyses(measureDescription);
			}

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
	 * buildMeasureDescription: <br>
	 * Description
	 * 
	 * @param errors
	 * @param measuredescription
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildMeasureDescription(Map<String, String> errors, MeasureDescription measuredescription, String source, Locale locale, Integer analysisId) {
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
				errors.put("reference", serviceDataValidation.ParseError(error, messageSource, locale));
			else {
				if (measuredescription.getId() < 1 && serviceMeasureDescription.existsForMeasureByReferenceAndStandard(reference, measuredescription.getStandard()))
					errors.put("reference", messageSource.getMessage("error.measuredescription.reference.duplicate", null, "Reference already exists in this standard", locale));
				else
					measuredescription.setReference(reference);
			}

			error = serviceDataValidation.validate(measuredescription, "level", level);

			if (error != null)
				errors.put("level", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				measuredescription.setLevel(level);

			error = serviceDataValidation.validate(measuredescription, "computable", computable);

			if (error != null)
				errors.put("computable", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				measuredescription.setComputable(computable);

			ValidatorField validator = serviceDataValidation.findByClass(MeasureDescriptionText.class);

			
				// get language
				Language language = serviceAnalysis.getLanguageOfAnalysis(analysisId);

				// get domain in this language
				String domain = jsonNode.get("domain").asText().trim();

				// get description in this language
				String description = jsonNode.get("description").asText().trim();

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
					errors.put("domain", serviceDataValidation.ParseError(error, messageSource, locale));
				else
					mesDescText.setDomain(domain);

				if (level == 3 && !(measuredescription.getStandard().getLabel().equals("27001") && measuredescription.getStandard().getVersion() == 2013))
					error = validator.validate(mesDescText, "description", description);
				else
					error = null;
				if (error != null)
					errors.put("description", serviceDataValidation.ParseError(error, messageSource, locale));
				else
					if(!errors.containsKey("level"))
						mesDescText.setDescription(description);
			
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
