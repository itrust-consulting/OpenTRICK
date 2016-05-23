package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.ALLOWED_TICKETING;
import static lu.itrust.business.TS.constants.Constant.TICKETING_NAME;
import static lu.itrust.business.TS.constants.Constant.TICKETING_URL;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerGenerateTickets;
import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceMeasureAssetValue;
import lu.itrust.business.TS.database.service.ServiceMeasureDescription;
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.database.service.ServiceStandard;
import lu.itrust.business.TS.database.service.ServiceTSSetting;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.ResourceNotFoundException;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.general.TSSetting;
import lu.itrust.business.TS.model.general.TSSettingName;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.AssetStandard;
import lu.itrust.business.TS.model.standard.MaturityStandard;
import lu.itrust.business.TS.model.standard.NormalStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.StandardType;
import lu.itrust.business.TS.model.standard.measure.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.MaturityMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.MeasureAssetValue;
import lu.itrust.business.TS.model.standard.measure.MeasureProperties;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;
import lu.itrust.business.TS.model.standard.measure.helper.MeasureAssetValueForm;
import lu.itrust.business.TS.model.standard.measure.helper.MeasureForm;
import lu.itrust.business.TS.model.standard.measure.helper.MeasureManager;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.model.ticketing.TicketingTask;
import lu.itrust.business.TS.model.ticketing.builder.Client;
import lu.itrust.business.TS.model.ticketing.builder.ClientBuilder;
import lu.itrust.business.TS.model.ticketing.helper.LinkForm;
import lu.itrust.business.TS.model.ticketing.helper.TicketingForm;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.validator.MeasureDescriptionTextValidator;
import lu.itrust.business.TS.validator.MeasureDescriptionValidator;
import lu.itrust.business.TS.validator.StandardValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

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
	private ServiceAnalysisStandard serviceAnalysisStandard;

	@Autowired
	private MeasureManager measureManager;

	@Autowired
	private ServiceStandard serviceStandard;

	@Autowired
	private ServiceMeasureDescription serviceMeasureDescription;

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

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceTSSetting serviceTSSetting;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private TaskExecutor executor;

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String section(HttpSession session, Model model, Principal principal) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		OpenMode mode = (OpenMode) session.getAttribute(Constant.OPEN_MODE);

		List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);

		List<Standard> standards = new ArrayList<Standard>();

		for (AnalysisStandard astandard : analysisStandards)
			standards.add(astandard.getStandard());

		model.addAttribute("standards", standards);

		Map<String, List<Measure>> measures = mapMeasures(null, analysisStandards);

		model.addAttribute("measures", measures);

		model.addAttribute("isLinkedToProject", serviceAnalysis.hasProject(idAnalysis) && loadUserSettings(principal, model, null));

		model.addAttribute("isEditable", !OpenMode.isReadOnly(mode) && serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.MODIFY));

		// add language of the analysis
		model.addAttribute("language", serviceLanguage.getFromAnalysis(idAnalysis).getAlpha2());

		return "analyses/single/components/standards/standard/standards";
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String sectionByStandard(@PathVariable Integer standardid, HttpSession session, Model model, Principal principal) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (idAnalysis == null)
			return null;

		OpenMode mode = (OpenMode) session.getAttribute(Constant.OPEN_MODE);

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

		model.addAttribute("isLinkedToProject", serviceAnalysis.hasProject(idAnalysis) && loadUserSettings(principal, model, null));

		// add language of the analysis
		model.addAttribute("language", serviceLanguage.getFromAnalysis(idAnalysis).getAlpha2());

		model.addAttribute("isEditable", !OpenMode.isReadOnly(mode) && serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.MODIFY));

		return "analyses/single/components/standards/standard/standards";
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
	@RequestMapping(value = "/{idStandard}/SingleMeasure/{elementID}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String getSingleMeasure(@PathVariable int elementID, Model model, HttpSession session, Principal principal) throws Exception {
		OpenMode mode = (OpenMode) session.getAttribute(Constant.OPEN_MODE);
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);
		model.addAttribute("language", serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
		model.addAttribute("measure", measure);
		model.addAttribute("isAnalysisOnly", measure.getAnalysisStandard().getStandard().isAnalysisOnly());
		model.addAttribute("isEditable", !OpenMode.isReadOnly(mode) && serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.MODIFY));
		model.addAttribute("standard", measure.getAnalysisStandard().getStandard().getLabel());
		model.addAttribute("selectedStandard", measure.getAnalysisStandard().getStandard());
		model.addAttribute("standardType", measure.getAnalysisStandard().getStandard().getType());
		model.addAttribute("standardid", measure.getAnalysisStandard().getStandard().getId());
		model.addAttribute("isLinkedToProject", serviceAnalysis.hasProject(idAnalysis) && loadUserSettings(principal, model, null));
		return "analyses/single/components/standards/measure/singleMeasure";
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody String compliance(@PathVariable Integer standardid, HttpSession session, Principal principal, Locale locale) {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		try {
			List<AnalysisStandard> standards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);
			for (AnalysisStandard standard : standards)
				if (standard.getStandard().getId() == standardid)
					return chartGenerator.compliance(idAnalysis, standard.getStandard().getLabel(), locale);
			// return chart of either standard 27001 or 27002 or null
			return null;
		} catch (Exception e) {
			// retrun error
			TrickLogManager.Persist(e);
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
	@RequestMapping(value = "/Compliances", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody String compliances(HttpSession session, Principal principal, Locale locale) {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		try {

			List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);

			String value = "{\"standards\":{";

			for (AnalysisStandard analysisStandard : analysisStandards) {

				value += "\"" + analysisStandard.getStandard().getId() + "\":[";

				value += chartGenerator.compliance(idAnalysis, analysisStandard.getStandard().getLabel(), locale);

				value += "],";
			}

			value = value.substring(0, value.length() - 1);

			value += "}}";

			// System.out.println(value);

			// return chart of either standard 27001 or 27002 or null

			return value;

		} catch (Exception e) {

			// retrun error
			TrickLogManager.Persist(e);
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
	@RequestMapping(value = "/SOA", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String getSOA(HttpSession session, Principal principal, Model model) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		Parameter parameter = serviceParameter.getByAnalysisIdAndDescription(idAnalysis, Constant.SOA_THRESHOLD);

		model.addAttribute("soaThreshold", parameter == null ? 100.0 : parameter.getValue());

		model.addAttribute("soa", serviceMeasure.getSOAMeasuresFromAnalysis(idAnalysis));

		return "analyses/single/components/soa";
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
	@RequestMapping(value = "/Manage", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String manageForm(HttpSession session, Principal principal, Model model, RedirectAttributes attributes, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		model.addAttribute("currentStandards", serviceStandard.getAllFromAnalysis(idAnalysis));
		return "analyses/single/components/standards/standard/form/manage";
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
	@RequestMapping(value = "/Create", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, String> createStandardForm(@RequestBody String value, HttpSession session, Principal principal, Model model, RedirectAttributes attributes,
			Locale locale) throws Exception {

		Map<String, String> errors = new LinkedHashMap<String, String>();

		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		try {
			// retrieve analysis id
			Analysis analysis = serviceAnalysis.get(idAnalysis);

			// create new standard object
			Standard standard = buildStandard(errors, value, locale, analysis);

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

			standard.setVersion(serviceStandard.getNextVersionByNameAndType(standard.getLabel(), standard.getType()));

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

			errors.put("success", messageSource.getMessage("success.analysis.create.standard", null, "The standard was successfully created", locale));

		} catch (TrickException e) {
			errors.put("standard", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return errors;
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			errors.put("standard", messageSource.getMessage(e.getMessage(), null, locale));
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
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, String> updateStandard(@RequestBody String value, HttpSession session, Principal principal, Model model, RedirectAttributes attributes,
			Locale locale) throws Exception {
		Map<String, String> errors = new LinkedHashMap<String, String>();

		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		try {
			// retrieve analysis id

			Analysis analysis = serviceAnalysis.get(idAnalysis);

			// create new standard object
			Standard standard = buildStandard(errors, value, locale, analysis);

			// build standard

			if (!errors.isEmpty())
				// return error on failure
				return errors;

			serviceStandard.saveOrUpdate(standard);

		} catch (TrickException e) {
			errors.put("standard", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return errors;
		} catch (Exception e) {
			errors.put("standard", messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
			TrickLogManager.Persist(e);
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
	@RequestMapping(value = "/Available", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String getAvailableStandards(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {
		model.addAttribute("availableStandards", serviceStandard.getAllNotInAnalysis((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS)));
		return "analyses/single/components/standards/standard/form/import";
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
	@RequestMapping(value = "/Add/{idStandard}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String addStandard(@PathVariable int idStandard, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		try {

			Standard standard = serviceStandard.get(idStandard);
			if (standard == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.add.standard.not_found", null, "Unfortunately, selected standard does not exist", locale));

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
			} else
				throw new TrickException("error.action.not_authorise", "Action does not authorised");

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

			return JsonMessage.Success(messageSource.getMessage("success.analysis.add.standard", null, "The standard was successfully added", locale));
		} catch (TrickException e) {
			return JsonMessage.Success(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.analysis.add.standard", null, "An unknown error occurred during analysis saving", locale));
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
	@RequestMapping(value = "/Delete/{idStandard}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String removeStandard(@PathVariable int idStandard, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		try {
			measureManager.removeStandardFromAnalysis(idAnalysis, idStandard);
			return JsonMessage.Success(messageSource.getMessage("success.analysis.norm.delete", null, "Standard was successfully removed from your analysis", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.analysis.norm.delete", null, "Standard could not be deleted!", locale));
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
	@RequestMapping(value = "/{idStandard}/Measure/Delete/{idMeasure}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idMeasure, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String deleteMeasureDescription(@PathVariable("idStandard") int idStandard, @PathVariable("idMeasure") int idMeasure, Locale locale, Principal principal,
			HttpSession session) {
		try {
			// try to delete measure
			MeasureDescription measureDescription = serviceMeasure.get(idMeasure).getMeasureDescription();

			Integer analysisID = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			if (measureDescription == null || measureDescription.getStandard().getId() != idStandard)
				return JsonMessage.Error(messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", locale));

			if (!measureDescription.getStandard().isAnalysisOnly())
				return JsonMessage
						.Error(messageSource.getMessage("error.measure.manage_knowledgebase_measure", null, "This measure can only be managed from the knowledge base", locale));

			customDelete.deleteAnalysisMeasure(analysisID, measureDescription);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.measure.delete.successfully", null, "Measure was deleted successfully", locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.measure.delete.failed", null, "Measure deleting was failed: Standard might be in use", locale));
		}
	}

	@RequestMapping(value = "/{idStandard}/Measure/New", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String newAssetMeasure(@PathVariable("idStandard") int idStandard, Model model, HttpSession session, Principal principal, RedirectAttributes attributes, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		try {

			Language language = serviceLanguage.getFromAnalysis(idAnalysis);

			locale = new Locale(language.getAlpha2());

			AnalysisStandard analysisStandard = serviceAnalysisStandard.getFromAnalysisIdAndStandardId(idAnalysis, idStandard);

			if (analysisStandard == null)
				throw new TrickException("error.standard.not_in_analysis", "Standard does not beloong to analysis!");

			if (!analysisStandard.getStandard().isAnalysisOnly())
				throw new TrickException("error.action.not_authorise", "Action does not authorised");

			boolean isCSSF = serviceAnalysis.isAnalysisCssf(idAnalysis);

			Measure measure = MeasureManager.Create(analysisStandard);

			List<AssetType> analysisAssetTypes = serviceAssetType.getAllFromAnalysis(idAnalysis);

			if (measure instanceof AssetMeasure) {

				List<Asset> availableAssets = serviceAsset.getAllFromAnalysisIdAndSelected(idAnalysis);

				model.addAttribute("availableAssets", availableAssets);

				model.addAttribute("assetTypes", analysisAssetTypes);

				((AssetMeasure) measure).setMeasurePropertyList(new MeasureProperties());

			} else if (measure instanceof NormalMeasure) {
				NormalMeasure normalMeasure = (NormalMeasure) measure;
				normalMeasure.setMeasurePropertyList(new MeasureProperties());
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

			if (!isCSSF) {
				Map<String, Boolean> excludes = new HashMap<>();
				for (String category : CategoryConverter.TYPE_CSSF_KEYS)
					excludes.put(category, true);
				model.addAttribute("cssfExcludes", excludes);
			}

			model.addAttribute("isComputable", measure.getAnalysisStandard().getStandard().isComputable());

			model.addAttribute("isAnalysisOnly", measure.getAnalysisStandard().getStandard().isAnalysisOnly());

			model.addAttribute("measureForm", MeasureForm.Build(measure, language.getAlpha3()));

			// return success message
			return "analyses/single/components/standards/measure/form";
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			attributes.addFlashAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			attributes.addFlashAttribute("error", messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
		return "redirect:/Error";
	}

	@RequestMapping(value = "/Measure/{idMeasure}/Edit", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idMeasure, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String editAssetMeasure(@PathVariable("idMeasure") int idMeasure, Locale locale, Model model, Principal principal, HttpSession session, RedirectAttributes attributes) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		try {

			Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, idMeasure);
			if (measure == null)
				throw new TrickException("error.measure.not_found", "Measure cannot be found");
			else if (!(measure.getAnalysisStandard().getStandard().isComputable() || measure.getAnalysisStandard().getStandard().isAnalysisOnly()))
				throw new TrickException("error.action.not_authorise", "Action does not authorised");

			boolean isCSSF = serviceAnalysis.isAnalysisCssf(idAnalysis);

			List<AssetType> analysisAssetTypes = serviceAssetType.getAllFromAnalysis(idAnalysis);

			if (measure instanceof AssetMeasure) {

				AssetMeasure assetMeasure = (AssetMeasure) measure;

				List<Asset> availableAssets = serviceAsset.getAllFromAnalysisIdAndSelected(idAnalysis);

				model.addAttribute("availableAssets", availableAssets);

				model.addAttribute("assetTypes", analysisAssetTypes);

				if (!(availableAssets.isEmpty() || assetMeasure.getMeasureAssetValues().isEmpty())) {
					for (MeasureAssetValue assetValue : assetMeasure.getMeasureAssetValues())
						availableAssets.remove(assetValue.getAsset());
				}

			} else if (measure instanceof NormalMeasure) {
				NormalMeasure normalMeasure = (NormalMeasure) measure;
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

			if (!isCSSF) {
				Map<String, Boolean> excludes = new HashMap<>();
				for (String category : CategoryConverter.TYPE_CSSF_KEYS)
					excludes.put(category, true);
				model.addAttribute("cssfExcludes", excludes);
			}

			model.addAttribute("isComputable", measure.getAnalysisStandard().getStandard().isComputable());

			model.addAttribute("isAnalysisOnly", measure.getAnalysisStandard().getStandard().isAnalysisOnly());

			model.addAttribute("measureForm", MeasureForm.Build(measure, serviceLanguage.getFromAnalysis(idAnalysis).getAlpha3()));

			// return success message
			return "analyses/single/components/standards/measure/form";
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			attributes.addFlashAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			attributes.addFlashAttribute("error", messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
		return "redirect:/Error";
	}

	@RequestMapping(value = "/Measure/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, String> measuresave(@RequestBody MeasureForm measureForm, Model model, Principal principal, HttpSession session, Locale locale)
			throws Exception {
		Map<String, String> errors = new LinkedHashMap<String, String>();
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		try {

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
				else if (measure instanceof AssetMeasure && measureForm.isComputable() && measureForm.getAssetValues().isEmpty())
					errors.put("asset", messageSource.getMessage("error.asset.empty", null, "Asset cannot be empty", locale));
				if (!errors.isEmpty())
					return errors;
			} else {
				switch (measureForm.getType()) {
				case ASSET:
					measure = new AssetMeasure();
					if (measureForm.isComputable() && measureForm.getAssetValues().isEmpty()) {
						errors.put("asset", messageSource.getMessage("error.asset.empty", null, "Asset cannot be empty", locale));
						return errors;
					}
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
				if (!update(measure, measureForm, idAnalysis, serviceLanguage.getFromAnalysis(idAnalysis), locale, errors).isEmpty())
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
			TrickLogManager.Persist(e);
			errors.put("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			errors.put("error", messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
		return errors;
	}

	@RequestMapping(value = "/Measure/{idMeasure}/Form", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idMeasure, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String measureForm(@PathVariable("idMeasure") int idMeasure, Locale locale, Model model, Principal principal, HttpSession session) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		try {
			Measure measure = serviceMeasure.get(idMeasure);
			List<Phase> phases = servicePhase.getAllFromAnalysis(idAnalysis);
			Language language = serviceLanguage.getFromAnalysis(idAnalysis);
			MeasureDescription measureDescription = measure.getMeasureDescription();
			MeasureDescriptionText measureDescriptionText = measureDescription.findByLanguage(language);
			model.addAttribute("measureDescriptionText", measureDescriptionText);
			model.addAttribute("measureDescription", measureDescription);
			if (measureDescriptionText != null)
				model.addAttribute("countLine", measureDescriptionText.getDescription().trim().split("\r\n|\r|\n").length);
			model.addAttribute("measureDescription", measureDescription);
			boolean isMaturity = measure instanceof MaturityMeasure;
			model.addAttribute("isMaturity", isMaturity);
			if (isMaturity)
				model.addAttribute("impscales", serviceParameter.getAllFromAnalysisByType(idAnalysis, Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME));
			model.addAttribute("isLinkedToProject", serviceAnalysis.hasProject(idAnalysis) && loadUserSettings(principal, model, null));
			model.addAttribute("showTodo", measureDescription.isComputable());
			model.addAttribute("language", language.getAlpha2());
			model.addAttribute("selectedMeasure", measure);
			model.addAttribute("phases", phases);
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
		return "analyses/single/components/standards/measure";

	}

	@RequestMapping(value = "/Ticketing/Generate", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String generateTickets(@RequestBody TicketingForm form, Principal principal, HttpSession session, Locale locale) {
		if (!loadUserSettings(principal, null, null))
			throw new ResourceNotFoundException();
		Worker worker = new WorkerGenerateTickets((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), null, form, serviceTaskFeedback, workersPoolManager, sessionFactory);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId())) {
			worker.cancel();
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
		} else {
			((WorkerGenerateTickets) worker).setClient(buildClient(principal.getName()));
			executor.execute(worker);
			return JsonMessage.Success(messageSource.getMessage("success.starting.creating.tickets", null, "Please wait while creating tickets", locale));
		}
	}

	@RequestMapping(value = "/Ticketing/Open", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String openTickets(@RequestBody List<Integer> measures, Model model, Principal principal, HttpSession session, RedirectAttributes attributes, Locale locale) {
		Client client = null;
		try {
			if (!loadUserSettings(principal, model, null))
				throw new ResourceNotFoundException();
			Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
			if (analysis.hasProject()) {
				client = buildClient(principal.getName());
				Map<Integer, String> keyIssues;
				if (measures.size() > 5) {
					Map<Integer, Integer> contains = measures.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
					keyIssues = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
							.filter(measure -> contains.containsKey(measure.getId()) && !StringUtils.isEmpty(measure.getTicket()))
							.collect(Collectors.toMap(Measure::getId, Measure::getTicket));
				} else {
					keyIssues = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
							.filter(measure -> !StringUtils.isEmpty(measure.getTicket()) && measures.contains(measure.getId()))
							.collect(Collectors.toMap(Measure::getId, Measure::getTicket));
				}
				Map<String, TicketingTask> taskMap = client.findByIdsAndProjectId(analysis.getProject(), keyIssues.values()).stream()
						.collect(Collectors.toMap(TicketingTask::getId, Function.identity()));
				if (!taskMap.isEmpty()) {
					List<TicketingTask> tasks = new LinkedList<>();
					measures.stream().filter(id -> taskMap.containsKey(keyIssues.get(id))).forEach(id -> tasks.add(taskMap.get(keyIssues.get(id))));
					model.addAttribute("first", tasks.get(0));
					model.addAttribute("tasks", tasks);
				}
			}
			return String.format("analyses/single/components/ticketing/%s/home", model.asMap().get(TICKETING_NAME).toString().toLowerCase());
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (TrickException e) {
			attributes.addAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} catch (Exception e) {
			attributes.addAttribute("error", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@RequestMapping(value = "/Ticketing/UnLink", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String unlinkTickets(@RequestBody List<Integer> measureIds, Principal principal, HttpSession session, Locale locale) {
		if (!loadUserSettings(principal, null, null))
			throw new ResourceNotFoundException();
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		serviceMeasure.getByIdAnalysisAndIds(idAnalysis, measureIds).forEach(measure -> {
			if (!StringUtils.isEmpty(measure.getTicket())) {
				measure.setTicket(null);
				serviceMeasure.saveOrUpdate(measure);
			}
		});
		if (measureIds.size() > 1)
			return JsonMessage.Success(messageSource.getMessage("success.unlinked.measures.from.tickets", null, "Measures has been successfully unlinked from tickets", locale));
		return JsonMessage.Success(messageSource.getMessage("success.unlinked.measure.from.ticket", null, "Measure has been successfully unlinked from a ticket", locale));

	}

	@RequestMapping(value = "/Ticketing/Synchronise", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String synchroniseWithTicketingSystem(@RequestBody List<Integer> ids, Model model, Principal principal, HttpSession session, RedirectAttributes attributes,
			Locale locale) {
		Client client = null;
		try {
			if (!loadUserSettings(principal, model, null))
				throw new ResourceNotFoundException();
			Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
			if (analysis.hasProject()) {
				client = buildClient(principal.getName());
				Map<Integer, Measure> measuresMap;
				if (ids.size() > 5) {
					Map<Integer, Integer> contains = ids.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
					measuresMap = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
							.filter(measure -> contains.containsKey(measure.getId()) && !StringUtils.isEmpty(measure.getTicket()))
							.collect(Collectors.toMap(Measure::getId, Function.identity()));
				} else {
					measuresMap = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
							.filter(measure -> !StringUtils.isEmpty(measure.getTicket()) && ids.contains(measure.getId()))
							.collect(Collectors.toMap(Measure::getId, Function.identity()));
				}

				List<Measure> measures = new LinkedList<>();

				List<String> keyIssues = new LinkedList<>();

				ids.stream().filter(id -> measuresMap.containsKey(id)).forEach(id -> {
					Measure measure = measuresMap.get(id);
					measures.add(measure);
					keyIssues.add(measure.getTicket());
				});

				Map<String, TicketingTask> tasks = client.findByIdsAndProjectId(analysis.getProject(), keyIssues).stream()
						.collect(Collectors.toMap(task -> task.getId(), Function.identity()));
				List<Parameter> parameters = analysis.findParametersByType(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME);
				model.addAttribute("measures", measures);
				model.addAttribute("parameters", parameters);
				model.addAttribute("tasks", tasks);
				model.addAttribute("language", analysis.getLanguage().getAlpha2());
			}
			return String.format("analyses/single/components/ticketing/%s/forms/synchronise", model.asMap().get(TICKETING_NAME).toString().toLowerCase());
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (TrickException e) {
			attributes.addAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} catch (Exception e) {
			attributes.addAttribute("error", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@RequestMapping(value = "/Ticketing/Link", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String linkTickets(@RequestBody List<Integer> measureIds, Model model, Principal principal, HttpSession session, RedirectAttributes attributes, Locale locale) {
		Client client = null;
		try {
			if (!loadUserSettings(principal, model, null))
				throw new ResourceNotFoundException();
			Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
			if (analysis.hasProject()) {
				client = buildClient(principal.getName());
				List<Measure> measures;
				List<String> excludes = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
						.filter(measure -> !StringUtils.isEmpty(measure.getTicket())).map(Measure::getTicket).collect(Collectors.toList());

				if (measureIds.size() > 5) {
					Map<Integer, Integer> contains = measureIds.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
					measures = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
							.filter(measure -> contains.containsKey(measure.getId()) && StringUtils.isEmpty(measure.getTicket())).collect(Collectors.toList());
				} else {
					measures = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
							.filter(measure -> StringUtils.isEmpty(measure.getTicket()) && measureIds.contains(measure.getId())).collect(Collectors.toList());
				}
				model.addAttribute("tasks", client.findOtherTasksByProjectId(analysis.getProject(), excludes, 20, 0));
				model.addAttribute("measures", measures);
				model.addAttribute("language", analysis.getLanguage().getAlpha2());
			}
			return String.format("analyses/single/components/ticketing/%s/forms/link", model.asMap().get(TICKETING_NAME).toString().toLowerCase());
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (TrickException e) {
			attributes.addAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} catch (Exception e) {
			attributes.addAttribute("error", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@RequestMapping(value = "/Ticketing/Load", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String loadTickets(@RequestBody List<Integer> measureIds, @RequestParam(name = "startIndex") int startIndex, Model model, Principal principal, HttpSession session,
			RedirectAttributes attributes, Locale locale) {
		Client client = null;
		try {
			if (!loadUserSettings(principal, model, null))
				throw new ResourceNotFoundException();
			Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
			if (analysis.hasProject()) {
				client = buildClient(principal.getName());
				List<String> excludes = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
						.filter(measure -> !StringUtils.isEmpty(measure.getTicket())).map(Measure::getTicket).collect(Collectors.toList());
				model.addAttribute("tasks", client.findOtherTasksByProjectId(analysis.getProject(), excludes, 20, startIndex));
				model.addAttribute("language", analysis.getLanguage().getAlpha2());
			}
			return String.format("analyses/single/components/ticketing/%s/forms/link", model.asMap().get(TICKETING_NAME).toString().toLowerCase());
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (TrickException e) {
			attributes.addAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} catch (Exception e) {
			attributes.addAttribute("error", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@RequestMapping(value = "/Ticketing/Link/Measure", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String linkTicket(@RequestBody LinkForm form, Principal principal, HttpSession session, Locale locale) {
		if (!loadUserSettings(principal, null, null))
			throw new ResourceNotFoundException();
		if (StringUtils.isEmpty(form.getIdTicket()))
			return JsonMessage.Error(messageSource.getMessage("error.ticket.not_found", null, "Ticket cannot be found", locale));

		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Analysis analysis = serviceAnalysis.get(idAnalysis);

		if (!analysis.hasProject())
			return JsonMessage.Error(messageSource.getMessage("error.analysis.no_project", null, "Please link your analysis to a project and try again", locale));
		Measure measure = analysis.findMeasureById(form.getIdMeasure());
		if (measure == null)
			return JsonMessage.Error(messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", locale));
		if (!StringUtils.isEmpty(measure.getTicket())) {
			return measure.getTicket().equals(form.getIdTicket())
					? JsonMessage.Success(messageSource.getMessage("info.measure.already.link", null, "Measure has been already linked to this ticket", locale))
					: JsonMessage.Error(messageSource.getMessage("error.measure.already.link", null, "Measure is already linked to another ticket", locale));
		}
		if (analysis.hasTicket(form.getIdTicket()))
			return JsonMessage.Error(messageSource.getMessage("error.ticket.already.linked", null, "Ticket is already linked to another measure", locale));

		measure.setTicket(form.getIdTicket());
		serviceMeasure.saveOrUpdate(measure);
		return JsonMessage.Success(messageSource.getMessage("success.link.measure.to.ticket", null, "Measure has been successfully linked to a ticket", locale));

	}

	private Client buildClient(String username) {
		User user = serviceUser.get(username);
		TSSetting urlSetting = serviceTSSetting.get(TSSettingName.TICKETING_SYSTEM_URL);
		TSSetting nameSetting = serviceTSSetting.get(TSSettingName.TICKETING_SYSTEM_NAME);
		if (urlSetting == null || nameSetting == null)
			throw new TrickException("error.load.setting", "Setting cannot be loaded");
		Map<String, Object> settings = new HashMap<>(3);
		settings.put("username", user.getSetting(Constant.USER_TICKETING_SYSTEM_USERNAME));
		settings.put("password", user.getSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD));
		settings.put("url", urlSetting.getValue());
		Client client = null;
		boolean isConnected = false;
		try {
			isConnected = (client = ClientBuilder.Build(nameSetting.getString())).connect(settings);
		} catch (TrickException e) {
			throw e;
		} catch (Exception e) {
			throw new TrickException("error.ticket_system.connexion.failed", "Unable to connect to your ticketing system", e);
		} finally {
			if (!(client == null || isConnected)) {
				try {
					client.close();
				} catch (IOException e) {
					TrickLogManager.Persist(e);
				}
			}
		}
		return client;
	}

	private boolean loadUserSettings(@Nonnull Principal principal, @Nullable Model model, @Nullable User user) {
		boolean allowedTicketing = false;
		try {
			if (user == null)
				user = serviceUser.get(principal.getName());
			TSSetting name = serviceTSSetting.get(TSSettingName.TICKETING_SYSTEM_NAME), url = serviceTSSetting.get(TSSettingName.TICKETING_SYSTEM_URL);
			String username = user.getSetting(Constant.USER_TICKETING_SYSTEM_USERNAME), password = user.getSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD);
			allowedTicketing = !(name == null || url == null || StringUtils.isEmpty(name.getValue()) || StringUtils.isEmpty(url.getValue()) || StringUtils.isEmpty(username)
					|| StringUtils.isEmpty(password)) && serviceTSSetting.isAllowed(TSSettingName.SETTING_ALLOWED_TICKETING_SYSTEM_LINK);
			if (model != null && allowedTicketing) {
				model.addAttribute(TICKETING_NAME, StringUtils.capitalize(name.getValue()));
				model.addAttribute(TICKETING_URL, url.getString());
			}
		} catch (Exception e) {
			TrickLogManager.Persist(e);

		} finally {
			if (model != null)
				model.addAttribute(ALLOWED_TICKETING, allowedTicketing);
		}
		return allowedTicketing;
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
		else if (!errors.containsKey("reference") && measureForm.getReference().split(Constant.REGEX_SPLIT_REFERENCE).length != measureForm.getLevel())
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

	private Map<String, String> update(Measure measure, MeasureForm measureForm, Integer idAnalysis, Language language, Locale locale, Map<String, String> errors)
			throws Exception {
		if (errors == null)
			errors = new LinkedHashMap<String, String>();
		MeasureDescription description = measure.getMeasureDescription();
		if (description == null) {
			if (serviceMeasureDescription.existsForMeasureByReferenceAndAnalysisStandardId(measureForm.getReference(), measure.getAnalysisStandard().getId())) {
				errors.put("reference",
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
		} else if (!description.getReference().equals(measureForm.getReference())) {
			if (serviceMeasureDescription.existsForMeasureByReferenceAndAnalysisStandardId(measureForm.getReference(), measure.getAnalysisStandard().getId())) {
				errors.put("reference",
						messageSource.getMessage("error.measure_description.reference.duplicated", new String[] { measure.getAnalysisStandard().getStandard().getLabel() },
								String.format("The reference already exists for %s", measure.getAnalysisStandard().getStandard().getLabel()), locale));
				return errors;
			}
			description.setReference(measureForm.getReference());
			description.setLevel(measureForm.getLevel());
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
				MeasureAssetValue assetValue = serviceMeasureAssetValue.getByMeasureIdAndAssetId(measure.getId(), asset.getId());
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

			if (id > 0) {
				standard = analysis.findStandardByAndAnalysisOnly(id);
				if (standard == null) {
					errors.put("standard", messageSource.getMessage("error.standard.not.belong.selected.analysis", null, "Standard does not belong to selected analysis", locale));
					return null;
				}
			} else {
				standard = new Standard();
				standard.setAnalysisOnly(true);
			}

			String prevlabel = standard.getLabel();

			String label = jsonNode.get("label").asText();

			String description = jsonNode.get("description").asText();

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

			if (standard.getId() < 1) {

				StandardType type = StandardType.getByName(jsonNode.get("type").asText());

				error = validator.validate(standard, "type", type);
				if (error != null)
					errors.put("type", serviceDataValidation.ParseError(error, messageSource, locale));
				else
					standard.setType(type);
			}

			// set computable flag
			standard.setComputable(jsonNode.get("computable").asText().equals("on"));

			if (!label.equals(prevlabel) || standard.getId() < 1)
				standard.setVersion(serviceStandard.getNextVersionByNameAndType(label, standard.getType()));
			// return success
			return standard;

		} catch (TrickException e) {
			errors.put("standard", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
		} catch (Exception e) {
			// return error
			errors.put("standard", messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
			TrickLogManager.Persist(e);
		}
		return null;
	}

}
