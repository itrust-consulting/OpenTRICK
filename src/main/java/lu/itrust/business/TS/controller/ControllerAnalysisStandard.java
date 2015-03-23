package lu.itrust.business.TS.controller;

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
import lu.itrust.business.TS.data.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.data.asset.Asset;
import lu.itrust.business.TS.data.asset.AssetType;
import lu.itrust.business.TS.data.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.data.general.AssetTypeValue;
import lu.itrust.business.TS.data.general.Language;
import lu.itrust.business.TS.data.general.Phase;
import lu.itrust.business.TS.data.parameter.Parameter;
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
import lu.itrust.business.TS.data.standard.measure.helper.MeasureAssetValueForm;
import lu.itrust.business.TS.data.standard.measure.helper.MeasureForm;
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
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.validator.MeasureDescriptionTextValidator;
import lu.itrust.business.TS.validator.MeasureDescriptionValidator;
import lu.itrust.business.TS.validator.StandardValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	/**
	 * selected analysis actions (reload section. single measure, load soa, get
	 * compliances)
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

		model.addAttribute("isEditable", serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.MODIFY));

		// add language of the analysis
		model.addAttribute("language", serviceLanguage.getFromAnalysis(idAnalysis).getAlpha2());

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
		model.addAttribute("language", serviceLanguage.getFromAnalysis(idAnalysis).getAlpha2());

		model.addAttribute("isEditable", serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.MODIFY));

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
		model.addAttribute("language", serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
		model.addAttribute("measure", measure);
		model.addAttribute("analysisOnly", measure.getAnalysisStandard().getStandard().isAnalysisOnly());
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

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());

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

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());

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
	public @ResponseBody Map<String, String> createStandardForm(@RequestBody String value, HttpSession session, Principal principal, Model model, RedirectAttributes attributes,
			Locale locale) throws Exception {

		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {

			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis == null) {
				errors.put("standard", messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale));
				return errors;
			}

			Analysis analysis = serviceAnalysis.get(idAnalysis);

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());

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

			errors.put("success",
					messageSource.getMessage("success.analysis.create.standard", null, "The standard was successfully created", customLocale != null ? customLocale : locale));

		} catch (TrickException e) {
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis != null) {
				Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
				errors.put("standard", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), customLocale != null ? customLocale : locale));
			} else
				errors.put("standard", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			e.printStackTrace();
			return errors;
		} catch (Exception e) {
			e.printStackTrace();
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis != null) {
				Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
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
	public @ResponseBody Map<String, String> updateStandard(@RequestBody String value, HttpSession session, Principal principal, Model model, RedirectAttributes attributes,
			Locale locale) throws Exception {
		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {

			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis == null) {
				errors.put("standard", messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale));
				return errors;
			}

			Analysis analysis = serviceAnalysis.get(idAnalysis);

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());

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
				Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
				errors.put("standard", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), customLocale != null ? customLocale : locale));
			} else
				errors.put("standard", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			e.printStackTrace();
			return errors;
		} catch (Exception e) {
			e.printStackTrace();
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis != null) {
				Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
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
			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
			availableStandards.clear();
			availableStandards
					.put(0, messageSource.getMessage("error.analysis.add.standard", null, "An unknown error occurred during analysis saving", customLocale != null ? customLocale
							: locale));
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

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());

			Standard standard = serviceStandard.get(idStandard);
			if (standard == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.add.standard.not_found", null, "Unfortunately, selected standard does not exist",
						customLocale != null ? customLocale : locale));

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

			return JsonMessage.Success(messageSource.getMessage("success.analysis.add.standard", null, "The standard was successfully added", customLocale != null ? customLocale
					: locale));
		} catch (TrickException e) {
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
			return JsonMessage.Success(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), customLocale != null ? customLocale : locale));
		} catch (Exception e) {
			e.printStackTrace();
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
			return JsonMessage.Error(messageSource.getMessage("error.analysis.add.standard", null, "An unknown error occurred during analysis saving",
					customLocale != null ? customLocale : locale));
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
			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
			measureManager.removeStandardFromAnalysis(idAnalysis, idStandard);
			return JsonMessage.Success(messageSource.getMessage("success.analysis.norm.delete", null, "Standard was successfully removed from your analysis",
					customLocale != null ? customLocale : locale));
		} catch (Exception e) {
			e.printStackTrace();
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
			return JsonMessage.Error(messageSource.getMessage("error.analysis.norm.delete", null, "Standard could not be deleted!", customLocale != null ? customLocale : locale));
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
	public @ResponseBody String deleteMeasureDescription(@PathVariable("idStandard") int idStandard, @PathVariable("idMeasure") int idMeasure, Locale locale, Principal principal,
			HttpSession session) {
		try {
			// try to delete measure
			MeasureDescription measureDescription = serviceMeasure.get(idMeasure).getMeasureDescription();

			Integer analysisID = (Integer) session.getAttribute("selectedAnalysis");

			if (measureDescription == null || measureDescription.getStandard().getId() != idStandard)
				return JsonMessage.Error(messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", locale));

			if (!measureDescription.getStandard().isAnalysisOnly())
				return JsonMessage.Error(messageSource.getMessage("error.measure.manage_knowledgebase_measure", null, "This measure can only be managed from the knowledge base",
						locale));

			customDelete.deleteAnalysisMeasure(analysisID, measureDescription);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.measure.delete.successfully", null, "Measure was deleted successfully", locale));
		} catch (Exception e) {
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.measure.delete.failed", null, "Measure deleting was failed: Standard might be in use", locale));
		}
	}

	@RequestMapping(value = "/{idStandard}/Measure/New", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public String newAssetMeasure(@PathVariable("idStandard") int idStandard, Model model, HttpSession session, Principal principal, RedirectAttributes attributes, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		try {

			Language language = serviceLanguage.getFromAnalysis(idAnalysis);

			locale = new Locale(language.getAlpha2());

			AnalysisStandard analysisStandard = serviceAnalysisStandard.getFromAnalysisIdAndStandardId(idAnalysis, idStandard);

			if (analysisStandard == null)
				throw new TrickException("error.standard.not_in_analysis", "Standard does not beloong to analysis!");

			if (!analysisStandard.getStandard().isAnalysisOnly())
				throw new TrickException("error.action.not_authorise", "Action does not authorised");

			Measure measure = MeasureManager.Create(analysisStandard);

			MeasureProperties properties = null;

			List<AssetType> analysisAssetTypes = serviceAssetType.getAllFromAnalysis(idAnalysis);

			if (measure instanceof AssetMeasure) {

				List<Asset> availableAssets = serviceAsset.getAllFromAnalysis(idAnalysis);

				model.addAttribute("availableAssets", availableAssets);

				model.addAttribute("assetTypes", analysisAssetTypes);

				((AssetMeasure) measure).setMeasurePropertyList(properties = new MeasureProperties());

			} else if (measure instanceof NormalMeasure) {
				NormalMeasure normalMeasure = (NormalMeasure) measure;
				normalMeasure.setMeasurePropertyList(properties = new MeasureProperties());
				List<AssetType> assetTypes = serviceAssetType.getAll();

				Map<String, Boolean> assetTypesMapping = new LinkedHashMap<String, Boolean>();
				for (AssetType assetType : assetTypes) {
					if (!analysisAssetTypes.contains(assetType))
						assetTypesMapping.put(assetType.getType(), false);
					normalMeasure.addAnAssetTypeValue(new AssetTypeValue(assetType, 0));
				}
				model.addAttribute("hiddenAssetTypes", assetTypesMapping);
			}

			measure.setMeasureDescription(new MeasureDescription(new MeasureDescriptionText(language)));

			if (properties != null) {
				boolean isCSSF = serviceAnalysis.isAnalysisCssf(idAnalysis);
				for (String category : isCSSF ? CategoryConverter.JAVAKEYS : CategoryConverter.TYPE_CIA_KEYS)
					properties.setCategoryValue(category, 0);
			}

			model.addAttribute("isAnalysisOnly", measure.getAnalysisStandard().getStandard().isAnalysisOnly());

			model.addAttribute("measureForm", MeasureForm.Build(measure, language.getAlpha3()));

			// return success message
			return "analyses/singleAnalysis/components/standards/measure/form";
		} catch (TrickException e) {
			e.printStackTrace();
			attributes.addFlashAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			e.printStackTrace();
			attributes.addFlashAttribute("error", messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
		return "redirect:/Error";
	}

	@RequestMapping(value = "/Measure/{idMeasure}/Edit", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #idMeasure, 'Measure', #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public String editAssetMeasure(@PathVariable("idMeasure") int idMeasure, Locale locale, Model model, Principal principal, HttpSession session, RedirectAttributes attributes) {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		try {

			Language language = serviceLanguage.getFromAnalysis(idAnalysis);
			locale = new Locale(language.getAlpha2());

			Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, idMeasure);
			if (measure == null)
				throw new TrickException("error.measure.not_found", "Measure cannot be found");
			else if (!measure.getAnalysisStandard().getStandard().isComputable())
				throw new TrickException("error.action.not_authorise", "Action does not authorised");

			MeasureProperties properties = null;

			List<AssetType> analysisAssetTypes = serviceAssetType.getAllFromAnalysis(idAnalysis);

			if (measure instanceof AssetMeasure) {

				AssetMeasure assetMeasure = (AssetMeasure) measure;

				List<Asset> availableAssets = serviceAsset.getAllFromAnalysis(idAnalysis);

				model.addAttribute("availableAssets", availableAssets);

				model.addAttribute("assetTypes", analysisAssetTypes);

				if (!(availableAssets.isEmpty() || assetMeasure.getMeasureAssetValues().isEmpty())) {
					for (MeasureAssetValue assetValue : assetMeasure.getMeasureAssetValues())
						availableAssets.remove(assetValue.getAsset());
				}

				properties = ((AssetMeasure) measure).getMeasurePropertyList();

			} else if (measure instanceof NormalMeasure) {
				NormalMeasure normalMeasure = (NormalMeasure) measure;
				properties = normalMeasure.getMeasurePropertyList();
				List<AssetType> assetTypes = serviceAssetType.getAll();
				Map<String, Boolean> assetTypesMapping = new LinkedHashMap<String, Boolean>();
				for (AssetType assetType : assetTypes) {
					if (!analysisAssetTypes.contains(assetType))
						assetTypesMapping.put(assetType.getType(), false);
					if (normalMeasure.getAssetTypeValueByAssetType(assetType) == null)
						normalMeasure.addAnAssetTypeValue(new AssetTypeValue(assetType, 0));
				}
				model.addAttribute("hiddenAssetTypes", assetTypesMapping);
			}

			if (properties != null) {
				boolean isCSSF = serviceAnalysis.isAnalysisCssf(idAnalysis);
				for (String category : isCSSF ? CategoryConverter.JAVAKEYS : CategoryConverter.TYPE_CIA_KEYS)
					properties.getCategoryValue(category);
			}

			model.addAttribute("isAnalysisOnly", measure.getAnalysisStandard().getStandard().isAnalysisOnly());

			model.addAttribute("measureForm", MeasureForm.Build(measure, language.getAlpha3()));

			// return success message
			return "analyses/singleAnalysis/components/standards/measure/form";
		} catch (TrickException e) {
			e.printStackTrace();
			attributes.addFlashAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			e.printStackTrace();
			attributes.addFlashAttribute("error", messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
		return "redirect:/Error";
	}

	@RequestMapping(value = "/Measure/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, String> measuresave(@RequestBody MeasureForm measureForm, Model model, Principal principal, HttpSession session, Locale locale)
			throws Exception {
		Map<String, String> errors = new LinkedHashMap<String, String>();
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		try {

			Language language = serviceLanguage.getFromAnalysis(idAnalysis);

			locale = new Locale(language.getAlpha2());

			AnalysisStandard analysisStandard = serviceAnalysisStandard.getFromAnalysisIdAndStandardId(idAnalysis, measureForm.getIdStandard());

			if (analysisStandard == null) {
				errors.put("standard", messageSource.getMessage("error.standard.not_in_analysis", null, "Standard does not belong to analysis!", locale));
				return errors;
			}

			Measure measure = null;
			if (measureForm.getId() > 0) {
				measure = serviceMeasure.getFromAnalysisById(idAnalysis, measureForm.getId());
				if (measure == null)
					errors.put("measure", messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", locale));
				else if (measure.getAnalysisStandard().getId() != analysisStandard.getId())
					errors.put("measure", messageSource.getMessage("error.measure.belong.standard", null, "Measure does not belong to standard", locale));
				if (!errors.isEmpty())
					return errors;
			} else {
				switch (measureForm.getType()) {
				case ASSET:
					measure = new AssetMeasure();
					break;
				case NORMAL:
					measure = new NormalMeasure();
					break;
				default:
					throw new TrickException("error.measure.cannot.be.created", "Measure cannot be created");
				}

				Phase phase = servicePhase.getFromAnalysisByPhaseNumber(idAnalysis, Constant.PHASE_DEFAULT);
				if (phase == null) {
					errors.put("phase", messageSource.getMessage("error.measure.default.pahse.not_found", null, "Default phase cannot be found", locale));
					return errors;
				}

				measure.setPhase(phase);

				measure.setStatus(Constant.MEASURE_STATUS_APPLICABLE);

				measure.setImplementationRate(0.0);

				measure.setAnalysisStandard(analysisStandard);
			}

			if (measureForm.getProperties() == null) {
				errors.put("properties", messageSource.getMessage("error.properties.empty", null, "Properties cannot be empty", locale));
				return errors;
			}

			if (analysisStandard.getStandard().isAnalysisOnly()) {
				validate(measureForm, errors, locale);
				if (!errors.isEmpty())
					return errors;
				if (!update(measure, measureForm, idAnalysis, language, locale, errors).isEmpty())
					return errors;
			} else if (StandardType.NORMAL.equals(analysisStandard.getStandard().getType())) {
				if (measure.getId() < 1)
					throw new TrickException("error.measure.not_found", "Measure cannot be found");
				measureForm.getProperties().copyTo(((NormalMeasure) measure).getMeasurePropertyList());
				if (!updateAssetTypeValues((NormalMeasure) measure, measureForm.getAssetValues(), errors, locale).isEmpty())
					return errors;
			} else
				throw new TrickException("error.action.not_authorise", "Action does not authorised");

			serviceMeasure.saveOrUpdate(measure);
		} catch (TrickException e) {
			e.printStackTrace();
			errors.put("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			e.printStackTrace();
			errors.put("error", messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
		return errors;
	}

	private Map<String, String> updateAssetTypeValues(NormalMeasure measure, List<MeasureAssetValueForm> assetValueForms, final Map<String, String> errors, Locale locale)
			throws Exception {
		Map<Integer, AssetType> assetTypes = new LinkedHashMap<Integer, AssetType>();
		serviceAssetType.getAll().stream().forEach(assetType -> assetTypes.put(assetType.getId(), assetType));
		assetValueForms.stream().forEach(assetTypeValueForm -> {
			try {
				AssetType assetType = assetTypes.get(assetTypeValueForm.getId());
				if (assetType == null) {
					errors.put("assetType", messageSource.getMessage("error.asset_type.not_found", null, "Asset type cannot be found", locale));
					return;
				}
				AssetTypeValue assetTypeValue = measure.getAssetTypeValueByAssetType(assetType);
				if (assetTypeValue == null)
					measure.addAnAssetTypeValue(assetTypeValue = new AssetTypeValue(assetType, assetTypeValueForm.getValue()));
				else
					assetTypeValue.setValue(assetTypeValueForm.getValue());
			} catch (TrickException e) {
				errors.put("assetTypeValue", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
				return;
			}
		});
		return errors;

	}

	private void validate(MeasureForm measureForm, Map<String, String> errors, Locale locale) throws Exception {
		ValidatorField validator = serviceDataValidation.findByClass(MeasureDescriptionValidator.class);
		if (validator == null)
			serviceDataValidation.register(validator = new MeasureDescriptionValidator());
		String error = validator.validate("reference", measureForm.getReference());
		if (error != null)
			errors.put("reference", serviceDataValidation.ParseError(error, messageSource, locale));
		error = validator.validate("level", measureForm.getLevel());
		if (error != null)
			errors.put("level", serviceDataValidation.ParseError(error, messageSource, locale));
		else if (!errors.containsKey("reference") && measureForm.getReference().split("\\.").length != measureForm.getLevel())
			errors.put("level", messageSource.getMessage("error.measure_description.level.not.match.reference", null, "The level and the reference do not match.", locale));
		validator = serviceDataValidation.findByClass(MeasureDescriptionTextValidator.class);
		if (validator == null)
			serviceDataValidation.register(validator = new MeasureDescriptionTextValidator());

		error = validator.validate("domain", measureForm.getDomain());
		if (error != null)
			errors.put("domain", serviceDataValidation.ParseError(error, messageSource, locale));
		error = validator.validate("description", measureForm.getDescription());
		if (error != null)
			errors.put("description", serviceDataValidation.ParseError(error, messageSource, locale));
	}

	private Map<String, String> update(Measure measure, MeasureForm measureForm, Integer idAnalysis, Language language, Locale locale, Map<String, String> errors) throws Exception {
		if (errors == null)
			errors = new LinkedHashMap<String, String>();
		MeasureDescription description = measure.getMeasureDescription();
		if (description == null) {
			if (serviceMeasureDescription.existsForMeasureByReferenceAndAnalysisStandardId(measureForm.getReference(), measure.getAnalysisStandard().getId())) {
				errors.put(
						"reference",
						messageSource.getMessage("error.measure_description.reference.duplicated", new String[] { measure.getAnalysisStandard().getStandard().getLabel() },
								String.format("The reference already exists for %s", measure.getAnalysisStandard().getStandard().getLabel()), locale));
				return errors;
			}
			description = serviceMeasureDescription.getByReferenceAndStandard(measureForm.getReference(), measure.getAnalysisStandard().getStandard());
			if (description == null) {
				description = new MeasureDescription(measureForm.getReference(), measure.getAnalysisStandard().getStandard(), measureForm.getLevel(), measureForm.isComputable());
				description.addMeasureDescriptionText(new MeasureDescriptionText(description, measureForm.getDomain(), measureForm.getDescription(), language));
			}
			measure.setMeasureDescription(description);
		}
		if (description.getId() > 0) {
			MeasureDescriptionText descriptionText = description.findByLanguage(language);
			if (descriptionText == null)
				description.addMeasureDescriptionText(new MeasureDescriptionText(description, measureForm.getDomain(), measureForm.getDescription(), language));
			else
				descriptionText.update(measureForm.getDomain(), measureForm.getDescription());
		}

		description.setComputable(measureForm.isComputable());

		if (measureForm.getProperties() == null) {
			errors.put("properties", messageSource.getMessage("error.properties.empty", null, "Properties cannot be empty", locale));
			return errors;
		}

		if (measure instanceof AssetMeasure) {
			AssetMeasure assetMeasure = (AssetMeasure) measure;
			if (assetMeasure.getMeasurePropertyList() == null)
				assetMeasure.setMeasurePropertyList(new MeasureProperties());

			measureForm.getProperties().copyTo(assetMeasure.getMeasurePropertyList());

			List<MeasureAssetValue> assetValues = new ArrayList<MeasureAssetValue>(measureForm.getAssetValues().size());
			for (MeasureAssetValueForm assetValueForm : measureForm.getAssetValues()) {
				Asset asset = serviceAsset.getFromAnalysisById(idAnalysis, assetValueForm.getId());
				if (asset == null)
					throw new TrickException("error.asset.not_found", "Asset does not found");
				MeasureAssetValue assetValue = serviceMeasureAssetValue.getByAssetId(asset.getId());
				if (assetValue == null)
					assetValue = new MeasureAssetValue(asset, assetValueForm.getValue());
				else
					assetValue.setValue(assetValueForm.getValue());
				assetValues.add(assetValue);
			}

			Iterator<MeasureAssetValue> iterator = assetMeasure.getMeasureAssetValues().iterator();
			while (iterator.hasNext()) {
				MeasureAssetValue assetValue = iterator.next();
				if (!assetValues.contains(assetValue)) {
					iterator.remove();
					serviceMeasureAssetValue.delete(assetValue);
				} else
					assetValues.remove(assetValue);
			}
			assetMeasure.getMeasureAssetValues().addAll(assetValues);
		} else if (measure instanceof NormalMeasure) {
			NormalMeasure normalMeasure = (NormalMeasure) measure;
			if (normalMeasure.getMeasurePropertyList() == null)
				normalMeasure.setMeasurePropertyList(new MeasureProperties());
			measureForm.getProperties().copyTo(normalMeasure.getMeasurePropertyList());
			updateAssetTypeValues(normalMeasure, measureForm.getAssetValues(), errors, locale);
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
			errors.put("standard", messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
			e.printStackTrace();
			return null;
		}
	}

}
