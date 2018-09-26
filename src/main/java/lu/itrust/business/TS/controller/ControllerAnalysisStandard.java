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
import org.springframework.beans.factory.annotation.Value;
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
import lu.itrust.business.TS.asynchronousWorkers.WorkerSOAExport;
import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.MeasureManager;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.controller.form.LinkForm;
import lu.itrust.business.TS.controller.form.MeasureAssetValueForm;
import lu.itrust.business.TS.controller.form.MeasureForm;
import lu.itrust.business.TS.controller.form.SOAForm;
import lu.itrust.business.TS.controller.form.TicketingForm;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceDynamicParameter;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceMaturityParameter;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceMeasureAssetValue;
import lu.itrust.business.TS.database.service.ServiceMeasureDescription;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.database.service.ServiceSimpleParameter;
import lu.itrust.business.TS.database.service.ServiceStandard;
import lu.itrust.business.TS.database.service.ServiceTSSetting;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.ResourceNotFoundException;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.helper.chartJS.model.Chart;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.analysis.AnalysisType;
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
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.AssetStandard;
import lu.itrust.business.TS.model.standard.MaturityStandard;
import lu.itrust.business.TS.model.standard.NormalStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.StandardType;
import lu.itrust.business.TS.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.helper.MeasureComparator;
import lu.itrust.business.TS.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.impl.MaturityMeasure;
import lu.itrust.business.TS.model.standard.measure.impl.MeasureAssetValue;
import lu.itrust.business.TS.model.standard.measure.impl.MeasureProperties;
import lu.itrust.business.TS.model.standard.measure.impl.NormalMeasure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.model.ticketing.TicketingTask;
import lu.itrust.business.TS.model.ticketing.builder.Client;
import lu.itrust.business.TS.model.ticketing.builder.ClientBuilder;
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
	private ServiceDynamicParameter serviceDynamicParameter;

	@Autowired
	private ServiceSimpleParameter serviceSimpleParameter;

	@Autowired
	private ServiceMaturityParameter serviceMaturityParameter;

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
			if(analysis.getAnalysisStandards().stream().anyMatch(a-> a.getStandard().getLabel().equalsIgnoreCase(standard.getLabel())))
				return JsonMessage.Error(messageSource.getMessage("error.analysis.add.standard.duplicate", null, "Your analysis already has another version of the standard!", locale));
			Measure measure = null;
			AnalysisStandard analysisStandard = null;
			List<MeasureDescription> measureDescriptions = serviceMeasureDescription.getAllByStandard(standard);
			Object implementationRate = null;
			if (standard.getType() == StandardType.MATURITY && analysis.isQuantitative()) {
				analysisStandard = new MaturityStandard();
				measure = new MaturityMeasure();
				implementationRate = analysis.getSimpleParameters().stream().filter(parameter -> parameter.isMatch(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME)
						&& (parameter.getValue().doubleValue() == 0 || parameter.getDescription().equals(Constant.IS_NOT_ACHIEVED))).findAny().orElse(null);
			} else if (standard.getType() == StandardType.NORMAL) {
				analysisStandard = new NormalStandard();
				measure = new NormalMeasure();
				List<AssetType> assetTypes = serviceAssetType.getAll();
				List<AssetTypeValue> assetTypeValues = ((NormalMeasure) measure).getAssetTypeValues();
				for (AssetType assetType : assetTypes)
					assetTypeValues.add(new AssetTypeValue(assetType, 0));
				((AbstractNormalMeasure) measure).setMeasurePropertyList(new MeasureProperties());
				implementationRate = new Double(0);
			} else
				throw new TrickException("error.action.not_authorise", "Action does not authorised");

			Phase phase = analysis.findPhaseByNumber(Constant.PHASE_DEFAULT);
			if (phase == null) {
				phase = new Phase(Constant.PHASE_DEFAULT);
				phase.setAnalysis(analysis);
				analysis.add(phase);
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

			analysis.add(analysisStandard);

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
	 * compliance: <br>
	 * Description
	 * 
	 * @param standard
	 * @param session
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "/{standardId}/Compliance/{type}", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Chart compliance(@PathVariable Integer standardId, @PathVariable ActionPlanMode type, HttpSession session, Principal principal, Locale locale) {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return serviceAnalysisStandard.getAllFromAnalysis(idAnalysis).stream().filter(analysisStandard -> analysisStandard.getStandard().getId() == standardId)
				.map(analysisStandard -> chartGenerator.compliance(idAnalysis, analysisStandard, type, locale)).findAny().orElse(new Chart());
	}

	/**
	 * compliances: <br>
	 * Description
	 * 
	 * @param session
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "/Compliances/{type}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody List<Chart> compliances(@PathVariable ActionPlanMode type, HttpSession session, Principal principal, Locale locale) {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return serviceAnalysisStandard.getAllFromAnalysis(idAnalysis).stream().map(analysisStandard -> chartGenerator.compliance(idAnalysis, analysisStandard, type, locale))
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/Compute-efficiency", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object computeEfficience(@RequestBody List<String> chapters, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		List<Measure> measures = serviceMeasure.getByAnalysisIdStandardAndChapters(idAnalysis, Constant.STANDARD_27002, chapters);
		List<Measure> maturities = serviceMeasure.getByAnalysisIdStandardAndChapters(idAnalysis, Constant.STANDARD_MATURITY,
				chapters.stream().map(reference -> "M." + reference).collect(Collectors.toList()));
		return MeasureManager.ComputeMaturiyEfficiencyRate(measures, maturities, loadMaturityParameters(idAnalysis), false,
				new ValueFactory(serviceDynamicParameter.findByAnalysisId(idAnalysis)));
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
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object createStandardForm(@RequestBody String value, HttpSession session, Principal principal, Model model, RedirectAttributes attributes, Locale locale)
			throws Exception {
		Map<String, String> errors = new LinkedHashMap<String, String>();
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		try {
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			Standard standard = buildStandard(errors, value, locale, analysis);
			if (!errors.isEmpty())
				return errors;
			if (standard.getId() < 1) {
				if (analysis.getAnalysisStandards().stream().anyMatch(analysisStandard -> analysisStandard.getStandard().hasSameName(standard)))
					return JsonMessage.Field("label",
							messageSource.getMessage("error.analysis.standard_exist_in_analysis", null, "The standard already exists in this analysis!", locale));
				standard.setVersion(serviceStandard.getNextVersionByNameAndType(standard.getLabel(), standard.getType()));
				switch (standard.getType()) {
				case ASSET:
					analysis.add(new AssetStandard(standard));
					break;
				case MATURITY:
					analysis.add(new MaturityStandard(standard));
					break;
				case NORMAL:
				default:
					analysis.add(new NormalStandard(standard));
					break;
				}
				serviceAnalysis.saveOrUpdate(analysis);
				return JsonMessage.Success(messageSource.getMessage("success.analysis.create.standard", null, "The standard was successfully created", locale));
			} else {
				serviceStandard.saveOrUpdate(standard);
				return JsonMessage.Success(messageSource.getMessage("success.analysis.update.standard", null, "The standard was successfully updated", locale));
			}
		} catch (TrickException e) {
			errors.put("standard", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return errors;
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			errors.put("standard", messageSource.getMessage(e.getMessage(), null, locale));
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
	@RequestMapping(value = "/{idStandard}/Measure/Delete/{idMeasure}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idMeasure, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String deleteMeasureDescription(@PathVariable("idStandard") int idStandard, @PathVariable("idMeasure") int idMeasure, Locale locale, Principal principal,
			HttpSession session) {
		try {
			customDelete.deleteAnalysisMeasure((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), idStandard, idMeasure);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.measure.delete.successfully", null, "Measure was deleted successfully", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			if (e instanceof TrickException)
				return JsonMessage.Error(messageSource.getMessage(((TrickException) e).getCode(), ((TrickException) e).getParameters(), e.getMessage(), locale));
			return JsonMessage.Error(messageSource.getMessage("error.measure.delete.failed", null, "Measure deleting was failed: Standard might be in use", locale));
		}
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

			AnalysisType type = serviceAnalysis.getAnalysisTypeById(idAnalysis);

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
						assetTypesMapping.put(assetType.getName(), false);
					if (normalMeasure.getAssetTypeValueByAssetType(assetType) == null)
						normalMeasure.addAnAssetTypeValue(new AssetTypeValue(assetType, 0));
				}
				model.addAttribute("hiddenAssetTypes", assetTypesMapping);
			}

			if (type == AnalysisType.QUANTITATIVE) {
				Map<String, Boolean> excludes = new HashMap<>();
				for (String category : CategoryConverter.TYPE_CSSF_KEYS)
					excludes.put(category, true);
				model.addAttribute("cssfExcludes", excludes);
			}

			model.addAttribute("type", type);

			model.addAttribute("isComputable", measure.getAnalysisStandard().getStandard().isComputable());

			model.addAttribute("isAnalysisOnly", measure.getAnalysisStandard().getStandard().isAnalysisOnly());

			model.addAttribute("measureForm", MeasureForm.Build(measure, type, serviceLanguage.getFromAnalysis(idAnalysis).getAlpha3()));

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

	@RequestMapping(value = "/Ticketing/Generate", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String generateTickets(@RequestBody TicketingForm form, Principal principal, HttpSession session, Locale locale) {
		if (!loadUserSettings(principal, null, null))
			throw new ResourceNotFoundException();
		Worker worker = new WorkerGenerateTickets((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), null, form, serviceTaskFeedback, workersPoolManager, sessionFactory);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale)) {
			worker.cancel();
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
		} else {
			((WorkerGenerateTickets) worker).setClient(buildClient(principal.getName()));
			executor.execute(worker);
			return JsonMessage.Success(messageSource.getMessage("success.starting.creating.tickets", null, "Please wait while creating tickets", locale));
		}
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
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		model.addAttribute("availableStandards", AnalysisType.isQuantitative(serviceAnalysis.getAnalysisTypeById(idAnalysis)) ? serviceStandard.getAllNotInAnalysis(idAnalysis)
				: serviceStandard.getAllNotInAnalysisAndNotMaturity(idAnalysis));
		return "analyses/single/components/standards/standard/form/import";
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
		model.addAttribute("measure", measure);
		loadAnalysisSettings(model, idAnalysis);
		loadEfficience(model, idAnalysis, measure);
		model.addAttribute("isAnalysisOnly", measure.getAnalysisStandard().getStandard().isAnalysisOnly());
		model.addAttribute("isEditable", !OpenMode.isReadOnly(mode) && serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.MODIFY));
		model.addAttribute("standard", measure.getAnalysisStandard().getStandard().getLabel());
		model.addAttribute("selectedStandard", measure.getAnalysisStandard().getStandard());
		model.addAttribute("standardType", measure.getAnalysisStandard().getStandard().getType());
		model.addAttribute("standardid", measure.getAnalysisStandard().getStandard().getId());
		model.addAttribute("isLinkedToProject", serviceAnalysis.hasProject(idAnalysis) && loadUserSettings(principal, model, null));
		model.addAttribute("valueFactory", new ValueFactory(serviceDynamicParameter.findByAnalysisId(idAnalysis)));
		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));
		return "analyses/single/components/standards/measure/singleMeasure";
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
			}
			return String.format("analyses/single/components/ticketing/%s/forms/link", model.asMap().get(TICKETING_NAME).toString().toLowerCase());
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (TrickException e) {
			attributes.addAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} catch (Exception e) {
			attributes.addAttribute("error", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
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

	@RequestMapping(value = "/Measure/{idMeasure}/Description/{langue}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idMeasure, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody String loadDescription(@PathVariable("idMeasure") int idMeasure, @PathVariable("langue") String langue, Principal principal, HttpSession session,
			Locale locale) {
		Measure measure = serviceMeasure.get(idMeasure);
		MeasureDescriptionText measureDescriptionText = measure.getMeasureDescription().findByAlph2(langue);
		if (measureDescriptionText == null)
			return JsonMessage.Error(messageSource.getMessage("error.measure.description.empty", null, "There is no other description for this security measure", locale));
		else
			return JsonMessage.Field("description", measureDescriptionText.getDescription());
	}

	@RequestMapping(value = "/Measures", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object loadMeasureByStandard(@RequestParam("idStandard") Integer idStandard, Principal principal, HttpSession session, Locale locale) {
		return serviceMeasure.getAllFromAnalysisAndStandard((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), idStandard).stream()
				.filter(measure -> !measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE) && measure.getImplementationRateValue(Collections.emptyList()) < 100)
				.map(measure -> {
					MeasureForm measureForm = new MeasureForm();
					MeasureDescriptionText descriptionText = measure.getMeasureDescription().getMeasureDescriptionTextByAlpha2(locale.getLanguage());
					measureForm.setId(measure.getId());
					measureForm.setIdStandard(idStandard);
					measureForm.setReference(measure.getMeasureDescription().getReference());
					// measureForm.setLevel(measure.getMeasureDescription().getLevel());
					measureForm.setComputable(measure.getMeasureDescription().isComputable());
					measureForm.setImplementationRate((int) measure.getImplementationRateValue(Collections.emptyList()));
					measureForm.setStatus(measure.getStatus());
					measureForm.setPhase(measure.getPhase().getNumber());
					measureForm.setResponsible(measure.getResponsible());
					if (descriptionText != null) {
						measureForm.setDomain(descriptionText.getDomain());
						measureForm.setDescription(descriptionText.getDescription());
					}
					return measureForm;
				}).collect(Collectors.toList());
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
	public String loadSOA(HttpSession session, Principal principal, Model model) throws Exception {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		IParameter parameter = serviceSimpleParameter.findByAnalysisIdAndDescription(idAnalysis, Constant.SOA_THRESHOLD);

		model.addAttribute("soaThreshold", parameter == null ? 100.0 : parameter.getValue().doubleValue());

		Comparator<Measure> comparator = new MeasureComparator();

		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));

		model.addAttribute("valueFactory", new ValueFactory(serviceDynamicParameter.findByAnalysisId(idAnalysis)));

		model.addAttribute("soas", serviceAnalysisStandard.findBySOAEnabledAndAnalysisId(true, idAnalysis).stream()
				.sorted((e1, e2) -> NaturalOrderComparator.compareTo(e1.getStandard().getLabel(), e2.getStandard().getLabel())).map(analysisStandard -> {
					analysisStandard.getMeasures().sort(comparator);
					return analysisStandard;
				}).collect(Collectors.toMap(AnalysisStandard::getStandard, AnalysisStandard::getMeasures, (e1, e2) -> e1, LinkedHashMap::new)));

		return "analyses/single/components/soa/home";
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
			}
			return String.format("analyses/single/components/ticketing/%s/forms/link", model.asMap().get(TICKETING_NAME).toString().toLowerCase());
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (TrickException e) {
			attributes.addAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} catch (Exception e) {
			attributes.addAttribute("error", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
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
	@RequestMapping(value = "/SOA/Manage", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String manageSOA(HttpSession session, Principal principal, Model model, RedirectAttributes attributes, Locale locale) throws Exception {
		model.addAttribute("analysisStandards",
				serviceAnalysisStandard.findByAndAnalysisIdAndTypeIn((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), NormalStandard.class, AssetStandard.class));
		return "analyses/single/components/soa/form";
	}

	@RequestMapping(value = "/Measure/{idMeasure}/Load", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idMeasure, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String measureForm(@PathVariable("idMeasure") int idMeasure, Locale locale, Model model, Principal principal, HttpSession session) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		try {
			Measure measure = serviceMeasure.get(idMeasure);
			List<Phase> phases = servicePhase.getAllFromAnalysis(idAnalysis);
			MeasureDescription measureDescription = measure.getMeasureDescription();
			MeasureDescriptionText measureDescriptionText = measureDescription.getMeasureDescriptionTextByAlpha2(locale.getLanguage());
			model.addAttribute("measureDescriptionText", measureDescriptionText);
			model.addAttribute("measureDescription", measureDescription);
			if (measureDescriptionText != null) {
				model.addAttribute("countLine", measureDescriptionText.getDescription().trim().split("\r\n|\r|\n").length);
				MeasureDescriptionText otherMeasureDescriptionText = measureDescriptionText.getLanguage().getAlpha3().equalsIgnoreCase("fra") ? measureDescription.findByAlph2("en")
						: measureDescription.findByAlph2("fr");
				if (!(otherMeasureDescriptionText == null || StringUtils.isEmpty(otherMeasureDescriptionText.getDescription())))
					model.addAttribute("otherMeasureDescriptionText", true);
			}
			model.addAttribute("measureDescription", measureDescription);
			boolean isMaturity = measure instanceof MaturityMeasure;
			model.addAttribute("isMaturity", isMaturity);
			if (isMaturity)
				model.addAttribute("impscales", serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME, idAnalysis));
			model.addAttribute("isLinkedToProject", serviceAnalysis.hasProject(idAnalysis) && loadUserSettings(principal, model, null));
			model.addAttribute("showTodo", measureDescription.isComputable());
			model.addAttribute("selectedMeasure", measure);
			model.addAttribute("phases", phases);
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
		return "analyses/single/components/standards/edition/measure";

	}

	@RequestMapping(value = "/Measure/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, Object> measuresave(@RequestBody MeasureForm measureForm, Model model, Principal principal, HttpSession session, Locale locale)
			throws Exception {
		Map<String, Object> result = new HashMap<>(), errors = new LinkedHashMap<>();
		try {
			result.put("errors", errors);
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			AnalysisStandard analysisStandard = serviceAnalysisStandard.getFromAnalysisIdAndStandardId(idAnalysis, measureForm.getIdStandard());
			AnalysisType type = serviceAnalysis.getAnalysisTypeById(idAnalysis);

			if (analysisStandard == null) {
				errors.put("standard", messageSource.getMessage("error.standard.not_in_analysis", null, "Standard does not belong to analysis!", locale));
				return result;
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
					return result;
			} else {
				switch (measureForm.getType()) {
				case ASSET:
					measure = new AssetMeasure();
					if (measureForm.isComputable() && measureForm.getAssetValues().isEmpty()) {
						errors.put("asset", messageSource.getMessage("error.asset.empty", null, "Asset cannot be empty", locale));
						return result;
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
					return result;
				}

				measure.setPhase(phase);

				measure.setStatus(Constant.MEASURE_STATUS_APPLICABLE);

				measure.setImplementationRate(0.0);

				// measure.setAnalysisStandard(analysisStandard);
				analysisStandard.add(measure);
			}

			if (AnalysisType.isQuantitative(type) && measureForm.getProperties() == null) {
				errors.put("properties", messageSource.getMessage("error.properties.empty", null, "Properties cannot be empty", locale));
				return result;
			}

			if (analysisStandard.getStandard().isAnalysisOnly()) {
				validate(measureForm, errors, locale);
				if (!errors.isEmpty())
					return result;
				if (!update(measure, measureForm, idAnalysis, type, serviceLanguage.getFromAnalysis(idAnalysis), locale, errors).isEmpty())
					return result;
			} else if (StandardType.NORMAL.equals(analysisStandard.getStandard().getType())) {
				if (measure.getId() < 1)
					throw new TrickException("error.measure.not_found", "Measure cannot be found");
				measureForm.getProperties().copyTo(((AbstractNormalMeasure) measure).getMeasurePropertyList());
				if (!updateAssetTypeValues((NormalMeasure) measure, measureForm.getAssetValues(), errors, locale).isEmpty())
					return result;
			} else
				throw new TrickException("error.action.not_authorise", "Action does not authorised");

			serviceMeasure.saveOrUpdate(measure);

			result.put("id", measure.getId());

		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			errors.put("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			errors.put("error", messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
		return result;
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

			AnalysisType type = serviceAnalysis.getAnalysisTypeById(idAnalysis);

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
						assetTypesMapping.put(assetType.getName(), false);
					normalMeasure.addAnAssetTypeValue(new AssetTypeValue(assetType, 0));
				}
				model.addAttribute("hiddenAssetTypes", assetTypesMapping);
			}

			measure.setMeasureDescription(new MeasureDescription(new MeasureDescriptionText(language)));

			if (type == AnalysisType.QUANTITATIVE) {
				Map<String, Boolean> excludes = new HashMap<>();
				for (String category : CategoryConverter.TYPE_CSSF_KEYS)
					excludes.put(category, true);
				model.addAttribute("cssfExcludes", excludes);
			}

			model.addAttribute("isComputable", measure.getAnalysisStandard().getStandard().isComputable());

			model.addAttribute("type", type);

			model.addAttribute("isAnalysisOnly", measure.getAnalysisStandard().getStandard().isAnalysisOnly());

			model.addAttribute("measureForm", MeasureForm.Build(measure, type, language.getAlpha3()));

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
			attributes.addAttribute("error", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
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

	@RequestMapping(value = "/SOA/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object saveSOA(@RequestBody List<SOAForm> soaForms, HttpSession session, Principal principal, Model model, RedirectAttributes attributes, Locale locale)
			throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		soaForms.forEach(form -> {
			AnalysisStandard analysisStandard = serviceAnalysisStandard.findOne(form.getId(), idAnalysis);
			if (analysisStandard != null) {
				analysisStandard.setSoaEnabled(form.isEnabled());
				serviceAnalysisStandard.saveOrUpdate(analysisStandard);
			}
		});
		return JsonMessage.Success(messageSource.getMessage("success.update.soa", null, "SOA has been successfully updated", locale));
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
	@RequestMapping("/Section")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String section(HttpSession session, Model model, Principal principal) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		OpenMode mode = (OpenMode) session.getAttribute(Constant.OPEN_MODE);

		List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);

		List<Standard> standards = new ArrayList<>(analysisStandards.size());

		Map<String, List<Measure>> measuresByStandard = new LinkedHashMap<>(analysisStandards.size());

		ValueFactory factory = new ValueFactory(serviceDynamicParameter.findByAnalysisId(idAnalysis));

		analysisStandards.forEach(analysisStandard -> {
			standards.add(analysisStandard.getStandard());
			measuresByStandard.put(analysisStandard.getStandard().getLabel(), analysisStandard.getMeasures());
		});

		boolean hasMaturity = measuresByStandard.containsKey(Constant.STANDARD_MATURITY);

		if (hasMaturity)
			model.addAttribute("effectImpl27002", MeasureManager.ComputeMaturiyEfficiencyRate(measuresByStandard.get(Constant.STANDARD_27002),
					measuresByStandard.get(Constant.STANDARD_MATURITY), loadMaturityParameters(idAnalysis), true, factory));

		model.addAttribute("hasMaturity", hasMaturity);

		model.addAttribute("standards", standards);

		model.addAttribute("measuresByStandard", measuresByStandard);

		model.addAttribute("isLinkedToProject", serviceAnalysis.hasProject(idAnalysis) && loadUserSettings(principal, model, null));

		model.addAttribute("isEditable", !OpenMode.isReadOnly(mode) && serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.MODIFY));

		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));

		model.addAttribute("valueFactory", factory);

		loadAnalysisSettings(model, idAnalysis);

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
		AnalysisStandard analysisStandard = serviceAnalysisStandard.getFromAnalysisIdAndStandardId(idAnalysis, standardid);
		if (analysisStandard == null)
			return null;
		ValueFactory factory = new ValueFactory(serviceDynamicParameter.findByAnalysisId(idAnalysis));
		List<Standard> standards = new ArrayList<Standard>(1);
		Map<String, List<Measure>> measuresByStandard = new HashMap<>(1);
		if (analysisStandard.getStandard().is(Constant.STANDARD_27002)) {
			AnalysisStandard maturityStandard = serviceAnalysisStandard.getFromAnalysisIdAndStandardName(idAnalysis, Constant.STANDARD_MATURITY);
			if (maturityStandard != null) {
				if (maturityStandard.getStandard().isComputable())
					model.addAttribute("effectImpl27002", MeasureManager.ComputeMaturiyEfficiencyRate(analysisStandard.getMeasures(), maturityStandard.getMeasures(),
							loadMaturityParameters(idAnalysis), true, factory));
				model.addAttribute("hasMaturity", true);
			} else
				model.addAttribute("hasMaturity", false);
		}

		standards.add(analysisStandard.getStandard());

		measuresByStandard.put(analysisStandard.getStandard().getLabel(), analysisStandard.getMeasures());

		model.addAttribute("standards", standards);

		model.addAttribute("measuresByStandard", measuresByStandard);

		model.addAttribute("isLinkedToProject", serviceAnalysis.hasProject(idAnalysis) && loadUserSettings(principal, model, null));

		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));

		model.addAttribute("isEditable", !OpenMode.isReadOnly(mode) && serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.MODIFY));

		model.addAttribute("valueFactory", factory);

		loadAnalysisSettings(model, idAnalysis);

		return "analyses/single/components/standards/standard/standards";
	}

	@Value("${app.settings.soa.english.template.name}")
	public void setSoaEnglishTemplate(String template) {
		WorkerSOAExport.ENG_TEMPLATE = template;
	}

	@Value("${app.settings.soa.french.template.name}")
	public void setSoaFrenchTemplate(String template) {
		WorkerSOAExport.FR_TEMPLATE = template;
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
				List<? extends IParameter> parameters = analysis.findParametersByType(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME);
				model.addAttribute("measures", measures);
				model.addAttribute("parameters", parameters);
				model.addAttribute("tasks", tasks);
			}
			return String.format("analyses/single/components/ticketing/%s/forms/synchronise", model.asMap().get(TICKETING_NAME).toString().toLowerCase());
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (TrickException e) {
			attributes.addAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} catch (Exception e) {
			attributes.addAttribute("error", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
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

	@RequestMapping(value = "/Update/Cost", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String updateCost(HttpSession session, Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		double externalSetupValue = -1, internalSetupValue = -1, lifetimeDefault = -1;
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		Iterator<SimpleParameter> iterator = analysis.getSimpleParameters().stream().filter(parameter -> parameter.isMatch(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME)).iterator();
		while (iterator.hasNext()) {
			IParameter parameter = iterator.next();
			switch (parameter.getDescription()) {
			case Constant.PARAMETER_INTERNAL_SETUP_RATE:
				internalSetupValue = parameter.getValue().doubleValue();
				break;
			case Constant.PARAMETER_EXTERNAL_SETUP_RATE:
				externalSetupValue = parameter.getValue().doubleValue();
				break;
			case Constant.PARAMETER_LIFETIME_DEFAULT:
				lifetimeDefault = parameter.getValue().doubleValue();
				break;
			}
		}
		updateMeasureCost(externalSetupValue, internalSetupValue, lifetimeDefault, analysis);
		serviceAnalysis.saveOrUpdate(analysis);
		return JsonMessage.Success(messageSource.getMessage("success.measure.cost.update", null, "Measure cost has been successfully updated", locale));
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

	private void loadAnalysisSettings(Model model, Integer integer) {
		Map<String, String> settings = serviceAnalysis.getSettingsByIdAnalysis(integer);
		AnalysisSetting rawSetting = AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN, hiddenCommentSetting = AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT,
				dynamicAnalysis = AnalysisSetting.ALLOW_DYNAMIC_ANALYSIS;
		model.addAttribute("showHiddenComment", Analysis.findSetting(hiddenCommentSetting, settings.get(hiddenCommentSetting.name())));
		model.addAttribute("showRawColumn", Analysis.findSetting(rawSetting, settings.get(rawSetting.name())));
		model.addAttribute("showDynamicAnalysis", Analysis.findSetting(dynamicAnalysis, settings.get(dynamicAnalysis.name())));
	}

	/**
	 * Compute Maturity-based Effectiveness Rate
	 * 
	 * @param model
	 * @param idAnalysis
	 * @param measure
	 */
	private void loadEfficience(Model model, Integer idAnalysis, Measure measure) {
		try {
			if (!measure.getAnalysisStandard().getStandard().is(Constant.STANDARD_27002))
				return;
			if (measure.getMeasureDescription().isComputable()) {
				ValueFactory factory = new ValueFactory(serviceDynamicParameter.findByAnalysisId(idAnalysis));
				String chapter = measure.getMeasureDescription().getReference().split("[.]", 2)[0];
				List<Measure> measures = serviceMeasure.getReferenceStartWith(idAnalysis, Constant.STANDARD_MATURITY, "M." + chapter);
				if (measures.isEmpty())
					model.addAttribute("hasMaturity", serviceAnalysisStandard.hasStandard(idAnalysis, Constant.STANDARD_MATURITY));
				else {
					model.addAttribute("hasMaturity", true);
					Double maturity = MeasureManager.ComputeMaturityByChapter(measures, loadMaturityParameters(idAnalysis), factory).get(chapter);
					model.addAttribute("effectImpl27002", maturity == null ? 0 : measure.getImplementationRateValue(factory) * maturity * 0.01);
				}
			} else
				model.addAttribute("hasMaturity", serviceAnalysisStandard.hasStandard(idAnalysis, Constant.STANDARD_MATURITY));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}

	}

	private List<IParameter> loadMaturityParameters(Integer idAnalysis) {
		List<IParameter> parameters = new LinkedList<>(serviceMaturityParameter.findByAnalysisId(idAnalysis));
		parameters.addAll(serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME, idAnalysis));
		return parameters;
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

	private Map<String, Object> update(Measure measure, MeasureForm measureForm, Integer idAnalysis, AnalysisType type, Language language, Locale locale,
			Map<String, Object> errors) throws Exception {
		if (errors == null)
			errors = new LinkedHashMap<String, Object>();
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
				description = new MeasureDescription(measureForm.getReference(), measure.getAnalysisStandard().getStandard(), measureForm.isComputable());
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
			// description.setLevel(measureForm.getLevel());
		}
		if (description.getId() > 0) {
			MeasureDescriptionText descriptionText = description.findByLanguage(language);
			if (descriptionText == null)
				description.addMeasureDescriptionText(new MeasureDescriptionText(description, measureForm.getDomain(), measureForm.getDescription(), language));
			else
				descriptionText.update(measureForm.getDomain(), measureForm.getDescription());
		}

		description.setComputable(measureForm.isComputable());

		if (AnalysisType.isQuantitative(type) && measureForm.getProperties() == null) {
			errors.put("properties", messageSource.getMessage("error.properties.empty", null, "Properties cannot be empty", locale));
			return errors;
		}

		if (measure instanceof AssetMeasure) {
			AssetMeasure assetMeasure = (AssetMeasure) measure;
			if (assetMeasure.getMeasurePropertyList() == null)
				assetMeasure.setMeasurePropertyList(new MeasureProperties());

			if (AnalysisType.isQuantitative(type))
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
			if (AnalysisType.isQuantitative(type))
				measureForm.getProperties().copyTo(normalMeasure.getMeasurePropertyList());
			updateAssetTypeValues(normalMeasure, measureForm.getAssetValues(), errors, locale);
		}

		return errors;
	}

	private Map<String, Object> updateAssetTypeValues(NormalMeasure measure, List<MeasureAssetValueForm> assetValueForms, final Map<String, Object> errors, Locale locale)
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

	private void updateMeasureCost(double externalSetupValue, double internalSetupValue, double lifetimeDefault, Analysis analysis) {
		analysis.getAnalysisStandards().stream().flatMap(analysisStandard -> analysisStandard.getMeasures().parallelStream()).forEach(measure -> {
			double cost = Analysis.computeCost(internalSetupValue, externalSetupValue, lifetimeDefault, measure.getInternalMaintenance(), measure.getExternalMaintenance(),
					measure.getRecurrentInvestment(), measure.getInternalWL(), measure.getExternalWL(), measure.getInvestment(), measure.getLifetime());
			measure.setCost(cost >= 0D ? cost : 0D);
		});
	}

	private void validate(MeasureForm measureForm, Map<String, Object> errors, Locale locale) throws Exception {
		ValidatorField validator = serviceDataValidation.findByClass(MeasureDescriptionValidator.class);
		if (validator == null)
			serviceDataValidation.register(validator = new MeasureDescriptionValidator());
		String error = validator.validate("reference", measureForm.getReference());
		if (error != null)
			errors.put("reference", serviceDataValidation.ParseError(error, messageSource, locale));
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

}
