package lu.itrust.business.TS.controller.analysis;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

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
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import lu.itrust.business.TS.asynchronousWorkers.WorkerSOAExport;
import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.MeasureManager;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
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
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.form.MeasureAssetValueForm;
import lu.itrust.business.TS.form.MeasureForm;
import lu.itrust.business.TS.form.SOAForm;
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
public class ControllerStandard extends AbstractController {

	@Autowired
	private ServiceMeasure serviceMeasure;

	@Autowired
	private ChartGenerator chartGenerator;

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
	private ServiceDynamicParameter serviceDynamicParameter;

	@Autowired
	private ServiceSimpleParameter serviceSimpleParameter;

	@Autowired
	private ServiceMaturityParameter serviceMaturityParameter;

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
			final Standard standard = serviceStandard.get(idStandard);
			if (standard == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.add.standard.not_found", null, "Unfortunately, selected standard does not exist", locale));
			final Analysis analysis = serviceAnalysis.get(idAnalysis);
			if (analysis.getAnalysisStandards().values().stream()
					.anyMatch(a -> a.getStandard().getName().equalsIgnoreCase(standard.getName()) || a.getStandard().getLabel().equalsIgnoreCase(standard.getLabel())))
				return JsonMessage
						.Error(messageSource.getMessage("error.analysis.add.standard.duplicate", null, "Your analysis already has another version of the standard!", locale));
			Measure measure = null;
			AnalysisStandard analysisStandard = null;
			final List<MeasureDescription> measureDescriptions = serviceMeasureDescription.getAllByStandard(standard);
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
			final Analysis analysis = serviceAnalysis.get(idAnalysis);
			final Standard standard = buildStandard(errors, value, locale, analysis);
			if (!errors.isEmpty())
				return errors;
			if (standard.getId() < 1) {
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
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
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
		final OpenMode mode = (OpenMode) session.getAttribute(Constant.OPEN_MODE);
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Analysis analysis = serviceAnalysis.get(idAnalysis);
		final Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);
		model.addAttribute("measure", measure);
		loadAnalysisSettings(model, idAnalysis);
		loadEfficience(model, idAnalysis, measure);
		model.addAttribute("isAnalysisOnly", measure.getAnalysisStandard().getStandard().isAnalysisOnly());
		model.addAttribute("isEditable", !OpenMode.isReadOnly(mode) && serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.MODIFY));
		model.addAttribute("standard", measure.getAnalysisStandard().getStandard().getName());
		model.addAttribute("selectedStandard", measure.getAnalysisStandard().getStandard());
		model.addAttribute("standardType", measure.getAnalysisStandard().getStandard().getType());
		model.addAttribute("standardid", measure.getAnalysisStandard().getStandard().getId());
		model.addAttribute("isLinkedToProject", serviceAnalysis.hasProject(idAnalysis) && loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(), model, null));
		model.addAttribute("valueFactory", new ValueFactory(serviceDynamicParameter.findByAnalysisId(idAnalysis)));
		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));
		return "analyses/single/components/standards/measure/singleMeasure";
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
		final ValueFactory factory = new ValueFactory(serviceDynamicParameter.findByAnalysisId((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS)));
		return serviceMeasure.getAllFromAnalysisAndStandard((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), idStandard).stream()
				.filter(measure -> !measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE) && measure.getImplementationRateValue(Collections.emptyList()) < 100)
				.sorted(new MeasureComparator()).map(measure -> {
					MeasureForm measureForm = new MeasureForm();
					MeasureDescriptionText descriptionText = measure.getMeasureDescription().getMeasureDescriptionTextByAlpha2(locale.getLanguage());
					measureForm.setId(measure.getId());
					measureForm.setIdStandard(idStandard);
					measureForm.setReference(measure.getMeasureDescription().getReference());
					measureForm.setComputable(measure.getMeasureDescription().isComputable());
					measureForm.setImplementationRate((int) measure.getImplementationRateValue(factory));
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
				.sorted((e1, e2) -> NaturalOrderComparator.compareTo(e1.getStandard().getName(), e2.getStandard().getName())).map(analysisStandard -> {
					analysisStandard.getMeasures().sort(comparator);
					return analysisStandard;
				}).collect(Collectors.toMap(AnalysisStandard::getStandard, AnalysisStandard::getMeasures, (e1, e2) -> e1, LinkedHashMap::new)));

		return "analyses/single/components/soa/home";
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
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		try {
			final Measure measure = serviceMeasure.get(idMeasure);
			final Analysis analysis = serviceAnalysis.get(idAnalysis);
			final boolean isMaturity = measure instanceof MaturityMeasure;
			final List<Phase> phases = servicePhase.getAllFromAnalysis(idAnalysis);
			final MeasureDescription measureDescription = measure.getMeasureDescription();
			final MeasureDescriptionText measureDescriptionText = measureDescription.getMeasureDescriptionTextByAlpha2(locale.getLanguage());
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

			model.addAttribute("isMaturity", isMaturity);
			if (isMaturity)
				model.addAttribute("impscales", serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME, idAnalysis));
			model.addAttribute("isLinkedToProject", analysis.hasProject() && loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(), model, null));
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
		final OpenMode mode = (OpenMode) session.getAttribute(Constant.OPEN_MODE);

		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		final Analysis analysis = serviceAnalysis.get(idAnalysis);

		final List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);

		final List<Standard> standards = new ArrayList<>(analysisStandards.size());

		final Map<String, List<Measure>> measuresByStandard = new LinkedHashMap<>(analysisStandards.size());

		final ValueFactory factory = new ValueFactory(serviceDynamicParameter.findByAnalysisId(idAnalysis));

		final boolean hasMaturity = measuresByStandard.containsKey(Constant.STANDARD_MATURITY);

		analysisStandards.forEach(analysisStandard -> {
			standards.add(analysisStandard.getStandard());
			measuresByStandard.put(analysisStandard.getStandard().getName(), analysisStandard.getMeasures());
		});

		if (hasMaturity)
			model.addAttribute("effectImpl27002", MeasureManager.ComputeMaturiyEfficiencyRate(measuresByStandard.get(Constant.STANDARD_27002),
					measuresByStandard.get(Constant.STANDARD_MATURITY), loadMaturityParameters(idAnalysis), true, factory));

		model.addAttribute("hasMaturity", hasMaturity);

		model.addAttribute("standards", standards);

		model.addAttribute("measuresByStandard", measuresByStandard);

		model.addAttribute("isLinkedToProject", analysis.hasProject() && loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(), model, null));

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
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (idAnalysis == null)
			return null;
		final OpenMode mode = (OpenMode) session.getAttribute(Constant.OPEN_MODE);
		final AnalysisStandard analysisStandard = serviceAnalysisStandard.getFromAnalysisIdAndStandardId(idAnalysis, standardid);
		if (analysisStandard == null)
			return null;
		final Analysis analysis = serviceAnalysis.get(idAnalysis);
		final List<Standard> standards = new ArrayList<Standard>(1);
		final Map<String, List<Measure>> measuresByStandard = new HashMap<>(1);
		final ValueFactory factory = new ValueFactory(serviceDynamicParameter.findByAnalysisId(idAnalysis));
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

		measuresByStandard.put(analysisStandard.getStandard().getName(), analysisStandard.getMeasures());

		model.addAttribute("standards", standards);

		model.addAttribute("measuresByStandard", measuresByStandard);

		model.addAttribute("isLinkedToProject", analysis.hasProject() && loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(), model, null));

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
			final ObjectMapper mapper = new ObjectMapper();
			final JsonNode jsonNode = mapper.readTree(source);

			ValidatorField validator = serviceDataValidation.findByClass(Standard.class);

			if (validator == null)
				serviceDataValidation.register(validator = new StandardValidator());

			final Integer id = jsonNode.get("id").asInt();

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

			final String prevName = standard.getName();

			final String prevLabel = standard.getLabel();

			final String name = jsonNode.get("name").asText("").trim();

			final String label = jsonNode.get("label").asText("").trim();

			final String description = jsonNode.get("description").asText();

			// set data
			String error = validator.validate(standard, "label", label);

			if (error != null)
				errors.put("label", serviceDataValidation.ParseError(error, messageSource, locale));
			else if ((standard.getId() < 1 || !prevLabel.equalsIgnoreCase(label)) && analysis.findStandardByLabel(label) != null)
				errors.put("label", messageSource.getMessage("error.analysis.standard_exist_in_analysis", null, "The standard already exists in this analysis!", locale));
			else
				standard.setLabel(label);

			error = validator.validate(standard, "name", label);
			if (error != null)
				errors.put("name", serviceDataValidation.ParseError(error, messageSource, locale));
			else if ((standard.getId() < 1 || !prevName.equalsIgnoreCase(name)) && analysis.findStandardByName(name) != null)
				errors.put("name", messageSource.getMessage("error.analysis.standard_exist_in_analysis", null, "The standard already exists in this analysis!", locale));
			else
				standard.setName(name);

			error = validator.validate(standard, "description", description);

			if (error != null)
				errors.put("description", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				standard.setDescription(description);

			if (standard.getId() < 1) {

				final StandardType type = StandardType.getByName(jsonNode.get("type").asText());

				error = validator.validate(standard, "type", type);
				if (error != null)
					errors.put("type", serviceDataValidation.ParseError(error, messageSource, locale));
				else
					standard.setType(type);
			}

			// set computable flag
			standard.setComputable(jsonNode.get("computable").asText().equals("on"));

			if (!label.equals(prevLabel) || standard.getId() < 1)
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

	private Map<String, Object> update(Measure measure, MeasureForm measureForm, Integer idAnalysis, AnalysisType type, Language language, Locale locale,
			Map<String, Object> errors) throws Exception {
		if (errors == null)
			errors = new LinkedHashMap<String, Object>();
		MeasureDescription description = measure.getMeasureDescription();
		if (description == null) {
			if (serviceMeasureDescription.existsForMeasureByReferenceAndAnalysisStandardId(measureForm.getReference(), measure.getAnalysisStandard().getId())) {
				errors.put("reference",
						messageSource.getMessage("error.measure_description.reference.duplicated", new String[] { measure.getAnalysisStandard().getStandard().getName() },
								String.format("The reference already exists for %s", measure.getAnalysisStandard().getStandard().getName()), locale));
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
						messageSource.getMessage("error.measure_description.reference.duplicated", new String[] { measure.getAnalysisStandard().getStandard().getName() },
								String.format("The reference already exists for %s", measure.getAnalysisStandard().getStandard().getName()), locale));
				return errors;
			}
			description.setReference(measureForm.getReference());
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
		analysis.getAnalysisStandards().values().stream().flatMap(analysisStandard -> analysisStandard.getMeasures().parallelStream()).forEach(measure -> {
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
