package lu.itrust.business.TS.controller;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.asset.Asset;
import lu.itrust.business.TS.data.asset.AssetType;
import lu.itrust.business.TS.data.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.data.general.AssetTypeValue;
import lu.itrust.business.TS.data.general.Language;
import lu.itrust.business.TS.data.general.Phase;
import lu.itrust.business.TS.data.general.SecurityCriteria;
import lu.itrust.business.TS.data.parameter.Parameter;
import lu.itrust.business.TS.data.rrf.ImportRRFForm;
import lu.itrust.business.TS.data.standard.AnalysisStandard;
import lu.itrust.business.TS.data.standard.AssetStandard;
import lu.itrust.business.TS.data.standard.MaturityStandard;
import lu.itrust.business.TS.data.standard.NormalStandard;
import lu.itrust.business.TS.data.standard.Standard;
import lu.itrust.business.TS.data.standard.StandardType;
import lu.itrust.business.TS.data.standard.measure.AssetMeasure;
import lu.itrust.business.TS.data.standard.measure.MaturityMeasure;
import lu.itrust.business.TS.data.standard.measure.Measure;
import lu.itrust.business.TS.data.standard.measure.MeasureAssetValue;
import lu.itrust.business.TS.data.standard.measure.MeasureProperties;
import lu.itrust.business.TS.data.standard.measure.NormalMeasure;
import lu.itrust.business.TS.data.standard.measure.helper.MeasureManager;
import lu.itrust.business.TS.data.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.data.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.database.service.ServiceActionPlanSummary;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceMeasureAssetValue;
import lu.itrust.business.TS.database.service.ServiceMeasureDescription;
import lu.itrust.business.TS.database.service.ServiceMeasureDescriptionText;
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.database.service.ServiceStandard;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.validator.MeasureDescriptionTextValidator;
import lu.itrust.business.TS.validator.MeasureDescriptionValidator;
import lu.itrust.business.TS.validator.StandardValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	private ServiceMeasure serviceMeasure;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceParameter serviceParameter;

	@Autowired
	private ServiceActionPlanSummary serviceActionPlanSummary;

	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private MeasureManager measureManager;

	@Autowired
	private ServiceStandard serviceStandard;

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

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private ServiceMeasureAssetValue serviceMeasureAssetValue;

	@Autowired
	private ServicePhase servicePhase;

	/**
	 * selected analysis actions (reload section. single measure, load soa, get compliances)
	 */

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param session
	 * @param model
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Section")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).READ)")
	public String section(HttpSession session, Model model, Principal principal) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;

		List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);

		List<Standard> standards = new ArrayList<Standard>();

		for (AnalysisStandard astandard : analysisStandards)
			standards.add(astandard.getStandard());

		model.addAttribute("standards", standards);

		Map<String, List<Measure>> measures = mapMeasures(null, analysisStandards);

		model.addAttribute("measures", measures);

		// add language of the analysis
		model.addAttribute("language", serviceLanguage.getFromAnalysis(idAnalysis).getAlpha3());

		return "analyses/singleAnalysis/components/standards/standard/standards";
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param session
	 * @param model
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Section/{standardid}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).READ)")
	public String sectionByStandard(@PathVariable Integer standardid, HttpSession session, Model model, Principal principal) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;

		List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);

		Integer realstandardid = null;

		String standardlabel = null;

		List<Standard> standards = new ArrayList<Standard>();

		for (AnalysisStandard standard : analysisStandards) {
			standards.add(standard.getStandard());
			if (standard.getStandard().getId() == standardid) {
				realstandardid = standardid;
				standardlabel = standard.getStandard().getLabel();
			}
		}

		if (realstandardid == null)
			return null;

		model.addAttribute("standards", standards);

		// add measures of the analysis

		Map<String, List<Measure>> measures = mapMeasures(standardlabel, analysisStandards);

		model.addAttribute("measures", measures);

		// add language of the analysis
		model.addAttribute("language", serviceLanguage.getFromAnalysis(idAnalysis).getAlpha3());

		return "analyses/singleAnalysis/components/standards/standard/standards";
	}

	/**
	 * mapMeasures: <br>
	 * Description
	 * 
	 * @param standardlabel
	 * @param standards
	 * @return
	 */
	private Map<String, List<Measure>> mapMeasures(String standardlabel, List<AnalysisStandard> standards) {

		Map<String, List<Measure>> measuresmap = new LinkedHashMap<String, List<Measure>>();

		for (AnalysisStandard standard : standards) {

			if (standardlabel == null) {
				List<Measure> measures = standard.getMeasures();
				Comparator<Measure> cmp = new Comparator<Measure>() {
					public int compare(Measure o1, Measure o2) {
						return Measure.compare(o1.getMeasureDescription().getReference(), o2.getMeasureDescription().getReference());
					}
				};
				Collections.sort(measures, cmp);
				measuresmap.put(standard.getStandard().getLabel(), measures);
			} else {
				if (standard.getStandard().getLabel().equals(standardlabel)) {
					List<Measure> measures = standard.getMeasures();
					Comparator<Measure> cmp = new Comparator<Measure>() {
						public int compare(Measure o1, Measure o2) {
							return Measure.compare(o1.getMeasureDescription().getReference(), o2.getMeasureDescription().getReference());
						}
					};
					Collections.sort(measures, cmp);
					measuresmap.put(standard.getStandard().getLabel(), measures);

				}
			}

		}

		return measuresmap;
	}

	/**
	 * getSingleMeasure: <br>
	 * Description
	 * 
	 * @param elementID
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{idStandard}/SingleMeasure/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Measure', #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).READ)")
	public String getSingleMeasure(@PathVariable int elementID, Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);
		model.addAttribute("language", serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3());
		model.addAttribute("measure", measure);
		model.addAttribute("analysisOnly",measure.getAnalysisStandard().getStandard().isAnalysisOnly());
		model.addAttribute("standard", measure.getAnalysisStandard().getStandard().getLabel());
		model.addAttribute("standardType", measure.getAnalysisStandard().getStandard().getType());
		model.addAttribute("standardid", measure.getAnalysisStandard().getStandard().getId());

		return "analyses/singleAnalysis/components/standards/measure/singleMeasure";
	}

	/**
	 * compliance: <br>
	 * Description
	 * 
	 * @param standard
	 * @param session
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "/{standardid}/Compliance", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).READ)")
	@ResponseBody
	public String compliance(@PathVariable Integer standardid, HttpSession session, Principal principal, Locale locale) {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		try {

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));

			List<AnalysisStandard> standards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);
			for (AnalysisStandard standard : standards)
				if (standard.getStandard().getId() == standardid)
					return chartGenerator.compliance(idAnalysis, standard.getStandard().getLabel(), customLocale != null ? customLocale : locale);
			// return chart of either standard 27001 or 27002 or null

			return null;

		} catch (Exception e) {

			// retrun error
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * compliances: <br>
	 * Description
	 * 
	 * @param session
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "/Compliances", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).READ)")
	@ResponseBody
	public String compliances(HttpSession session, Principal principal, Locale locale) {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		try {

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));

			List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);

			String value = "{\"standards\":{";

			for (AnalysisStandard analysisStandard : analysisStandards) {

				value += "\"" + analysisStandard.getStandard().getId() + "\":[";

				value += chartGenerator.compliance(idAnalysis, analysisStandard.getStandard().getLabel(), customLocale != null ? customLocale : locale);

				value += "],";
			}

			value = value.substring(0, value.length() - 1);

			value += "}}";

			// System.out.println(value);

			// return chart of either standard 27001 or 27002 or null

			return value;

		} catch (Exception e) {

			// retrun error
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * getSOA: <br>
	 * Description
	 * 
	 * @param session
	 * @param principal
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/SOA", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).READ)")
	public String getSOA(HttpSession session, Principal principal, Model model) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		model.addAttribute("measures", serviceMeasure.getSOAMeasuresFromAnalysis(idAnalysis));

		return "analyses/singleAnalysis/components/soa";
	}

	/**
	 * manage analysis standards (manage menu)
	 */

	/**
	 * manageForm: <br>
	 * Description
	 * 
	 * @param session
	 * @param principal
	 * @param model
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Manage", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public String manageForm(HttpSession session, Principal principal, Model model, RedirectAttributes attributes, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		model.addAttribute("currentStandards", serviceStandard.getAllFromAnalysis(idAnalysis));
		return "analyses/singleAnalysis/components/standards/standard/manageForm";
	}

	/**
	 * createStandardForm: <br>
	 * Description
	 * 
	 * @param value
	 * @param session
	 * @param principal
	 * @param model
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Create", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
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
			Standard standard = buildStandard(errors, value, customLocale != null ? customLocale : locale, analysis);

			if (!errors.isEmpty())
				// return error on failure
				return errors;

			List<AnalysisStandard> astandards = analysis.getAnalysisOnlyStandards();

			for (AnalysisStandard astandard : astandards)
				if ((astandard.getStandard().getLabel().equals(standard.getLabel())) && (astandard.getStandard().getType().equals(standard.getType()))) {
					errors.put("standard", messageSource.getMessage("error.analysis.standard_exist_in_analysis", null, "The standard already exists in this analysis!", locale));
					break;
				}

			if (!errors.isEmpty())
				// return error on failure
				return errors;

			Integer version = serviceStandard.getBiggestVersionFromStandardByNameAndType(standard.getLabel(), standard.getType());

			if (version == null)
				version = 0;

			standard.setVersion(version + 1);

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
			e.printStackTrace();
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
	 * updateStandard: <br>
	 * Description
	 * 
	 * @param value
	 * @param session
	 * @param principal
	 * @param model
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, String> updateStandard(@RequestBody String value, HttpSession session, Principal principal, Model model, RedirectAttributes attributes, Locale locale)
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
			Standard standard = buildStandard(errors, value, customLocale != null ? customLocale : locale, analysis);

			// build standard

			if (!errors.isEmpty())
				// return error on failure
				return errors;

			serviceStandard.saveOrUpdate(standard);

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
			e.printStackTrace();
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
	 * getAvailableStandards: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Available", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Map<Integer, String> getAvailableStandards(HttpSession session, Principal principal, Locale locale) throws Exception {

		Map<Integer, String> availableStandards = new LinkedHashMap<Integer, String>();

		try {

			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

			List<Standard> standards = serviceStandard.getAllNotInAnalysis(idAnalysis);

			for (Standard standard : standards)
				availableStandards.put(standard.getId(), standard.getLabel() + " - " + standard.getVersion());

			return availableStandards;

		} catch (Exception e) {
			e.printStackTrace();
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
			availableStandards.clear();
			availableStandards.put(0, messageSource.getMessage("error.analysis.add.standard", null, "An unknown error occurred during analysis saving", customLocale != null ? customLocale : locale));
			return availableStandards;
		}
	}

	/**
	 * addStandard: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Add/{idStandard}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
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
				List<AssetType> assetTypes = serviceAssetType.getAll();
				List<AssetTypeValue> assetTypeValues = ((NormalMeasure) measure).getAssetTypeValues();
				for (AssetType assetType : assetTypes)
					assetTypeValues.add(new AssetTypeValue(assetType, 0));
				((NormalMeasure) measure).setMeasurePropertyList(new MeasureProperties());
				implementationRate = new Double(0);
			}
			Phase phase = analysis.findPhaseByNumber(Constant.PHASE_DEFAULT);
			if (phase == null) {
				phase = new Phase(Constant.PHASE_DEFAULT);
				phase.setAnalysis(analysis);
				analysis.addPhase(phase);
			}
			analysisStandard.setStandard(standard);
			measure.setStatus(Constant.MEASURE_STATUS_APPLICABLE);
			measure.setImplementationRate(implementationRate);
			for (MeasureDescription measureDescription : measureDescriptions) {
				Measure measure2 = measure.duplicate(analysisStandard, phase);
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

	/**
	 * removeStandard: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Delete/{idStandard}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String removeStandard(@PathVariable int idStandard, HttpSession session, Principal principal, Locale locale) throws Exception {

		try {

			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));

			measureManager.removeStandardFromAnalysis(idAnalysis, idStandard);
			return JsonMessage.Success(messageSource.getMessage("success.analysis.norm.delete", null, "Standard was successfully removed from your analysis", customLocale != null ? customLocale
				: locale));
		} catch (Exception e) {
			e.printStackTrace();
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
			return JsonMessage.Error(messageSource.getMessage("error.analysis.norm.delete", null, "Standard could not be deleted!", customLocale != null ? customLocale : locale));
		}
	}

	/**
	 * manage measures of standard
	 */

	@RequestMapping(value = "/{idStandard}/Measure/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
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

			Measure measure = serviceMeasure.get(id);

			// create new empty measuredescription object
			MeasureDescription measureDescription = measure != null ? measure.getMeasureDescription() : null;
			Standard standard = serviceStandard.get(idStandard);

			if (standard == null) {
				errors.put("norm", messageSource.getMessage("error.norm.not_found", null, "Standard does not exist", locale));
				return errors;
			}

			if (!standard.isAnalysisOnly()) {
				errors.put("norm", messageSource.getMessage("error.measure.manage_knowledgebase_measure", null, "This Measure can only be managed from the knowledge base", locale));
				return errors;
			}

			if (measureDescription == null) {
				// retrieve standard
				measureDescription = new MeasureDescription();

				measureDescription.setStandard(standard);

				if (buildMeasureDescription(errors, measureDescription, value, locale, idAnalysis)) {
					serviceMeasureDescription.save(measureDescription);

					measureManager.createNewMeasureForAllAnalyses(measureDescription);
				}

			} else {
				if (measureDescription.getStandard().getId() != idStandard) {
					errors.put("norm", messageSource.getMessage("error.measure_description.norm.not_matching", null, "Measure description does not belong to given standard", locale));

				} else {

					if (buildMeasureDescription(errors, measureDescription, value, locale, idAnalysis)) {
						serviceMeasureDescription.saveOrUpdate(measureDescription);
						measure.setMeasureDescription(measureDescription);
						serviceMeasure.saveOrUpdate(measure);
					}
				}
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
	 * deleteMeasureDescription: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param measureid
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{idStandard}/Measure/Delete/{idMeasure}", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #idMeasure, 'Measure', #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).DELETE)")
	public @ResponseBody String deleteMeasureDescription(@PathVariable("idStandard") int idStandard, @PathVariable("idMeasure") int idMeasure, Locale locale, Principal principal, HttpSession session) {
		try {
			// try to delete measure
			MeasureDescription measureDescription = serviceMeasure.get(idMeasure).getMeasureDescription();

			if (measureDescription == null || measureDescription.getStandard().getId() != idStandard)
				return JsonMessage.Error(messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", locale));

			if (!measureDescription.getStandard().isAnalysisOnly())
				return JsonMessage.Error(messageSource.getMessage("error.measure.manage_knowledgebase_measure", null, "This measure can only be managed from the knowledge base", locale));

			customDelete.deleteAnalysisMeasure(measureDescription);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.measure.delete.successfully", null, "Measure was deleted successfully", locale));
		} catch (Exception e) {
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.measure.delete.failed", null, "Measure deleting was failed: Standard might be in use", locale));
		}
	}

	@RequestMapping(value = "/{idStandard}/AssetMeasure/New", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public String newAssetMeasure(@PathVariable("idStandard") int idStandard, Locale locale, Model model, Principal principal, HttpSession session) {
		try {

			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

			Standard standard = serviceStandard.get(idStandard);

			if (standard == null) {
				model.addAttribute("error", messageSource.getMessage("error.standard.not_found", null, "Standard could not be found!", locale));
				return "analyses/singleAnalysis/components/standards/measure/assetMeasure";
			}
			if (!standard.isAnalysisOnly() || standard.getType() != StandardType.ASSET) {
				model.addAttribute("error", messageSource.getMessage("error.standard.not_asset_type", null, "Standard is not a asset standard type!", locale));
				return "analyses/singleAnalysis/components/standards/measure/assetMeasure";
			}

			boolean found = false;

			for (AnalysisStandard astandard : serviceAnalysisStandard.getAllFromAnalysis(idAnalysis))
				if (astandard.getStandard().equals(standard)) {
					found = true;
					break;
				}
			if (!found) {
				model.addAttribute("error", messageSource.getMessage("error.standard.not_in_analysis", null, "Standard does not beloong to analysis!", locale));
				return "analyses/singleAnalysis/components/standards/measure/assetMeasure";
			}

			List<Asset> availableAssets = serviceAsset.getAllFromAnalysis(idAnalysis);

			List<Asset> measureAssets = new ArrayList<Asset>();

			model.addAttribute("idStandard", idStandard);

			model.addAttribute("idMeasure", -1);

			model.addAttribute("availableAssets", availableAssets);

			model.addAttribute("measureAssets", measureAssets);

			AssetMeasure measure = new AssetMeasure();

			measure.setMeasureDescription(new MeasureDescription());

			measure.setMeasurePropertyList(new MeasureProperties());

			model.addAttribute("desc", measure.getMeasureDescription());

			MeasureDescriptionText text = new MeasureDescriptionText();

			model.addAttribute("desctext", text);

			model.addAttribute("props", measure.getMeasurePropertyList());

			LinkedHashMap<String, Integer> result = new LinkedHashMap<String, Integer>();

			if (serviceAnalysis.isAnalysisCssf(idAnalysis)) {
				for (String category : CategoryConverter.JAVAKEYS)
					result.put(category, 0);
				model.addAttribute("categories", result);
			} else {
				for (String category : CategoryConverter.TYPE_CIA_KEYS)
					result.put(category, 0);
				model.addAttribute("categories", result);
			}

			model.addAttribute("typeValue", false);
			model.addAttribute("assets", new ArrayList<MeasureAssetValue>());

			// return success message
			return "analyses/singleAnalysis/components/standards/measure/assetMeasure";
		} catch (Exception e) {
			// return error
			e.printStackTrace();
			model.addAttribute("error", messageSource.getMessage("error.measure.delete.failed", null, "Measure deleting was failed: Standard might be in use", locale));
			return "analyses/singleAnalysis/components/standards/measure/assetMeasure";
		}
	}

	@RequestMapping(value = "/{idStandard}/AssetMeasure/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, String> measuresave(@RequestBody String value, @PathVariable("idStandard") int idStandard, Locale locale, Model model, Principal principal, HttpSession session) {

		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {

			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

			Standard standard = serviceStandard.get(idStandard);

			if (standard == null) {

				errors.put("standard", messageSource.getMessage("error.standard.not_found", null, "Standard could not be found!", locale));
				return errors;
			}
			if (!standard.isAnalysisOnly() || standard.getType() != StandardType.ASSET) {
				errors.put("standard", messageSource.getMessage("error.standard.not_asset_type", null, "Standard is not a asset standard type!", locale));
				return errors;
			}

			AnalysisStandard astandard = null;

			for (AnalysisStandard tmpastandard : serviceAnalysisStandard.getAllFromAnalysis(idAnalysis))
				if (tmpastandard.getStandard().equals(standard)) {
					astandard = tmpastandard;
					break;
				}
			if (astandard == null) {
				errors.put("standard", messageSource.getMessage("error.standard.not_in_analysis", null, "Standard does not belong to analysis!", locale));
				return errors;
			}

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(value);

			int id = jsonNode.get("idMeasure").asInt();

			Measure measure = null;

			if (id != -1) {
				for (Measure tmpmeasure : astandard.getMeasures())
					if (tmpmeasure.getId() == id) {
						measure = tmpmeasure;
						break;
					}
				if (measure == null) {
					errors.put("measure", messageSource.getMessage("error.measure.not_found", null, "Measure could not be found!", locale));
					return errors;
				}
			} else {
				measure = new AssetMeasure();
				measure.setAnalysisStandard(astandard);
				((AssetMeasure) measure).setMeasureAssetValues(new ArrayList<MeasureAssetValue>());
				measure.setMeasureDescription(new MeasureDescription());
				measure.getMeasureDescription().setStandard(standard);
				((AssetMeasure) measure).setMeasurePropertyList(new MeasureProperties());
				measure.setImplementationRate(new Double(0));
				measure.setStatus("AP");
				astandard.getMeasures().add(measure);
				Phase phase = servicePhase.getFromAnalysisByPhaseNumber(idAnalysis, Constant.PHASE_DEFAULT);
				if (phase == null) {
					phase = new Phase(Constant.PHASE_DEFAULT);
					phase.setAnalysis(serviceAnalysis.get(idAnalysis));
				}
				measure.setPhase(phase);
			}

			buildAssetMeasure(errors, ((AssetMeasure) measure), value, locale, idAnalysis);

			if (!errors.isEmpty())
				return errors;

			if (measure.getId() > 0)
				serviceAnalysisStandard.saveOrUpdate(astandard);
			else
				serviceAnalysisStandard.save(astandard);

			errors.put("success", messageSource.getMessage("success.measure.assetmeasure_update_assets", null, "Assets successfully updated", locale));

			// return success message
			return errors;
		} catch (Exception e) {
			// return error
			e.printStackTrace();
			errors.put("error", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			return errors;
		}
	}

	@RequestMapping(value = "/{idStandard}/AssetMeasure/{idMeasure}/Edit", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #idMeasure, 'Measure', #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public String editAssetMeasure(@PathVariable("idStandard") int idStandard, @PathVariable("idMeasure") int idMeasure, Locale locale, Model model, Principal principal, HttpSession session) {
		try {

			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

			Standard standard = serviceStandard.get(idStandard);

			if (standard == null) {
				model.addAttribute("error", messageSource.getMessage("error.standard.not_found", null, "Standard could not be found!", locale));
				return "analyses/singleAnalysis/components/standards/measure/assetMeasure";
			}
			if (!standard.isAnalysisOnly() || standard.getType() != StandardType.ASSET) {
				model.addAttribute("error", messageSource.getMessage("error.standard.not_asset_type", null, "Standard is not a asset standard type!", locale));
				return "analyses/singleAnalysis/components/standards/measure/assetMeasure";
			}

			boolean standardfound = false;

			boolean measurefound = false;

			AssetMeasure measure = null;

			for (AnalysisStandard astandard : serviceAnalysisStandard.getAllFromAnalysis(idAnalysis))
				if (astandard.getStandard().equals(standard)) {
					standardfound = true;
					for (Measure tmpmeasure : astandard.getMeasures())
						if (tmpmeasure.getId() == idMeasure) {
							measurefound = true;
							measure = (AssetMeasure) tmpmeasure;
							break;
						}
					break;
				}
			if (!standardfound) {
				model.addAttribute("error", messageSource.getMessage("error.standard.not_in_analysis", null, "Standard not found in analysis!", locale));
				return "analyses/singleAnalysis/components/standards/measure/assetMeasure";
			}

			if (!measurefound) {
				model.addAttribute("error", messageSource.getMessage("error.measure.not_found", null, "Measure could not be found!", locale));
				return "analyses/singleAnalysis/components/standards/measure/assetMeasure";
			}

			List<Asset> availableAssets = serviceAsset.getAllFromAnalysis(idAnalysis);

			List<Asset> measureAssets = new ArrayList<Asset>();

			for (MeasureAssetValue mav : measure.getMeasureAssetValues()) {
				availableAssets.remove(mav.getAsset());
				measureAssets.add(mav.getAsset());
			}

			model.addAttribute("idStandard", idStandard);

			model.addAttribute("idMeasure", idMeasure);

			model.addAttribute("availableAssets", availableAssets);

			model.addAttribute("measureAssets", measureAssets);

			model.addAttribute("measure", measure);

			model.addAttribute("desc", measure.getMeasureDescription());

			MeasureDescriptionText text = measure.getMeasureDescription().getMeasureDescriptionText(serviceAnalysis.getLanguageOfAnalysis(idAnalysis));

			if (text == null) {
				text = new MeasureDescriptionText();

			}

			model.addAttribute("desctext", text);

			model.addAttribute("props", measure.getMeasurePropertyList());

			if (serviceAnalysis.isAnalysisCssf(idAnalysis)) {
				model.addAttribute("categories", measure.getMeasurePropertyList().getAllCategories());
			} else {
				model.addAttribute("categories", measure.getMeasurePropertyList().getCIACategories());
			}

			double typeValue =
				measure.getMeasurePropertyList().getPreventive() + measure.getMeasurePropertyList().getDetective() + measure.getMeasurePropertyList().getLimitative()
					+ measure.getMeasurePropertyList().getCorrective();
			model.addAttribute("typeValue", round(typeValue, 1) == 1 ? true : false);
			model.addAttribute("assets", measure.getMeasureAssetValues());

			// return success message
			return "analyses/singleAnalysis/components/standards/measure/assetMeasure";
		} catch (Exception e) {
			// return error
			e.printStackTrace();
			model.addAttribute("error", messageSource.getMessage("error.measure.new", null, "Error retrieving measure info", locale));
			return "analyses/singleAnalysis/components/standards/measure/assetMeasure";
		}
	}

	private static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	/**
	 * importRRF: <br>
	 * Description
	 * 
	 * @param session
	 * @param principal
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	@RequestMapping(value = "/Import/RRF", headers = "Accept=application/json;charset=UTF-8")
	public String importRRF(HttpSession session, Principal principal, Model model) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		List<Standard> standards = serviceStandard.getAllFromAnalysis(idAnalysis);
		List<Integer> idStandards = new ArrayList<Integer>(standards.size());
		for (Standard standard : standards) {
			if (!Constant.STANDARD_MATURITY.equalsIgnoreCase(standard.getLabel()))
				idStandards.add(standard.getId());
		}
		List<Analysis> profiles = serviceAnalysis.getAllProfileContainsStandard(standards);
		model.addAttribute("idStandards", idStandards);
		model.addAttribute("profiles", profiles);
		return "analyses/singleAnalysis/components/forms/importMeasureCharacteristics";

	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	@RequestMapping(value = "/Import/RRF/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody Object importRRFSave(@ModelAttribute ImportRRFForm rrfForm, HttpSession session, Principal principal, Locale locale) {
		try {
			if (rrfForm.getProfile() < 1)
				return JsonMessage.Error(messageSource.getMessage("error.import_rrf.no_profile", null, "No profile", locale));
			else if (rrfForm.getStandards() == null || rrfForm.getStandards().isEmpty())
				return JsonMessage.Error(messageSource.getMessage("error.import_rrf.norm", null, "No standard", locale));
			measureManager.importStandard((Integer) session.getAttribute("selectedAnalysis"), rrfForm);

			return JsonMessage.Success(messageSource.getMessage("success.import_rrf", null, "Measure characteristics has been successfully imported", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}

	}

	/**
	 * buildAssetMeasure: <br>
	 * Description
	 * 
	 * @param errors
	 * @param measure
	 * @param source
	 * @param locale
	 * @param analysisId
	 * @return
	 */
	private boolean buildAssetMeasure(Map<String, String> errors, AssetMeasure measure, String source, Locale locale, Integer analysisId) {
		try {
			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			if (!serviceDataValidation.isRegistred(MeasureDescription.class))
				serviceDataValidation.register(new MeasureDescriptionValidator());

			if (!serviceDataValidation.isRegistred(MeasureDescriptionText.class))
				serviceDataValidation.register(new MeasureDescriptionTextValidator());

			String reference = jsonNode.get("reference").asText();

			boolean computable = jsonNode.get("computable").asText().equals("on") ? true : false;

			int level = jsonNode.get("level").asInt();

			MeasureProperties mesProps = measure.getMeasurePropertyList();

			MeasureDescription mesdesc = measure.getMeasureDescription();

			String error = serviceDataValidation.validate(mesdesc, "reference", reference);

			if (error != null)
				errors.put("reference", serviceDataValidation.ParseError(error, messageSource, locale));
			else {
				if (mesdesc.getId() < 1 && serviceMeasureDescription.existsForMeasureByReferenceAndStandard(reference, mesdesc.getStandard()))
					errors.put("reference", messageSource.getMessage("error.measuredescription.reference.duplicate", null, "Reference already exists in this standard", locale));
				else
					mesdesc.setReference(reference);
			}

			error = serviceDataValidation.validate(mesdesc, "level", level);

			if (error != null)
				errors.put("level", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				mesdesc.setLevel(level);

			error = serviceDataValidation.validate(mesdesc, "computable", computable);

			if (error != null)
				errors.put("computable", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				mesdesc.setComputable(computable);

			ValidatorField validator = serviceDataValidation.findByClass(MeasureDescriptionText.class);

			// get language
			Language language = serviceAnalysis.getLanguageOfAnalysis(analysisId);

			// get domain in this language
			String domain = jsonNode.get("domain").asText().trim();

			// get description in this language
			String description = jsonNode.get("description").asText().trim();

			// init measdesctext object
			MeasureDescriptionText mesDescText = mesdesc.findByLanguage(language);

			// if new measure or text for this language does not exist:
			// create new text and save
			if (mesDescText == null) {
				// create new and add data
				mesDescText = new MeasureDescriptionText();
				mesDescText.setLanguage(language);
				mesdesc.addMeasureDescriptionText(mesDescText);
			}

			error = validator.validate(mesDescText, "domain", domain);

			if (error != null)
				errors.put("domain", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				mesDescText.setDomain(domain);

			if (level == 3)
				error = validator.validate(mesDescText, "description", description);
			else
				error = null;
			if (error != null)
				errors.put("description", serviceDataValidation.ParseError(error, messageSource, locale));
			else if (!errors.containsKey("level"))
				mesDescText.setDescription(description);

			if (mesdesc.isComputable()) {

				Iterator<String> assetnodes = jsonNode.get("measureassetvalues").getFieldNames();

				Map<Asset, Integer> assets = new LinkedHashMap<Asset, Integer>();

				while (assetnodes.hasNext()) {

					String asset = assetnodes.next();
					Integer value = jsonNode.get("measureassetvalues").get(asset).asInt();

					Asset tmpasset = serviceAsset.getFromAnalysisByName(analysisId, asset);

					if (tmpasset == null)
						errors.put("asset", "asset could not be found");
					else {
						assets.put(tmpasset, value);
					}
				}

				if (errors.get("asset") == null) {

					for (Asset asset : assets.keySet()) {

						boolean found = false;

						for (MeasureAssetValue mav : measure.getMeasureAssetValues()) {
							if (mav.getAsset().equals(asset)) {
								mav.setValue(assets.get(asset));
								found = true;
								break;
							}
						}
						if (!found)
							measure.addAnMeasureAssetValue(new MeasureAssetValue(asset, assets.get(asset)));
					}

					List<MeasureAssetValue> assetvalues = new ArrayList<MeasureAssetValue>();

					for (MeasureAssetValue mav : measure.getMeasureAssetValues()) {
						boolean found = false;
						for (Asset asset : assets.keySet()) {
							if (mav.getAsset().equals(asset)) {
								found = true;
								break;
							}

						}
						if (!found)
							assetvalues.add(mav);
					}

					for (MeasureAssetValue assetValue : assetvalues) {
						measure.getMeasureAssetValues().remove(assetValue);
						serviceMeasureAssetValue.delete(assetValue);
					}

				}
			} else {
				for (MeasureAssetValue mav : measure.getMeasureAssetValues()) {
					serviceMeasureAssetValue.delete(mav);
				}
				measure.getMeasureAssetValues().clear();
			}
			Iterator<String> properties = jsonNode.get("properties").getFieldNames();

			while (properties.hasNext()) {

				String property = properties.next();

				if (property.equals("categories")) {

					Iterator<String> categories = jsonNode.get("properties").get("categories").getFieldNames();

					while (categories.hasNext()) {
						String category = categories.next();

						int value = jsonNode.get("properties").get("categories").get(category).asInt();

						if (MeasureProperties.isCategoryKey(category))
							mesProps.setCategoryValue(category, value);
						else
							errors.put("category", "One or more categories are not valid");

					}

				} else {
					if (!property.equals("fsectoral") && !property.equals("fmeasure")) {
						if (property.equals("preventive") || property.equals("detective") || property.equals("limitative") || property.equals("corrective")) {
							double val = jsonNode.get("properties").get(property).asDouble();

							if (val < 0.0 || val > 1.0)
								errors.put(property, "value is not valid");
							else {
								Field field = SecurityCriteria.class.getDeclaredField(property);

								field.setAccessible(true);

								field.set(mesProps, val);
							}
						} else {
							int val = jsonNode.get("properties").get(property).asInt();
							if (val < 0 || val > 4)
								errors.put(property, "value is not valid");
							else {
								Field field = SecurityCriteria.class.getDeclaredField(property);

								field.setAccessible(true);

								field.set(mesProps, val);
							}
						}
					} else {
						int val = jsonNode.get("properties").get(property).asInt();
						if (property.equals("fmeasure")) {
							if (val < 0 || val > 10)
								errors.put(property, "value is not valid");
						} else {
							if (val < 0 || val > 4)
								errors.put(property, "value is not valid");
						}

						if (errors.get(property) == null) {
							Field field = MeasureProperties.class.getDeclaredField(property);

							field.setAccessible(true);

							field.set(mesProps, val);
						}

					}

				}

			}

			// return success message
			return errors.isEmpty();

		} catch (Exception e) {

			// return error message
			errors.put("measure", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return false;
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
			else if (!errors.containsKey("level"))
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
	private Standard buildStandard(Map<String, String> errors, String source, Locale locale, Analysis analysis) {

		try {

			Standard standard = null;

			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			ValidatorField validator = serviceDataValidation.findByClass(Standard.class);

			if (validator == null)
				serviceDataValidation.register(validator = new StandardValidator());

			Integer id = jsonNode.get("id").asInt();

			if (id > 0)
				standard = serviceStandard.get(id);
			else {
				standard = new Standard();
				standard.setAnalysisOnly(true);
			}

			String prevlabel = standard.getLabel();

			StandardType prevtype = standard.getType();

			String label = jsonNode.get("label").asText();

			String description = jsonNode.get("description").asText();

			StandardType type = StandardType.getByName(jsonNode.get("type").asText());

			// set data
			String error = validator.validate(standard, "label", label);
			if (error != null)
				errors.put("label", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				standard.setLabel(label);

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

			if (label != prevlabel || type != prevtype) {

				if (serviceStandard.existsByNameVersionType(label, 1, type)) {

					Integer version = serviceStandard.getBiggestVersionFromStandardByNameAndType(label, type);
					if (version == null)
						version = 0;

					standard.setVersion(version + 1);
				} else
					standard.setVersion(1);

			}
			// return success
			return standard;

		} catch (Exception e) {
			// return error
			errors.put("standard", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return null;
		}
	}

}
