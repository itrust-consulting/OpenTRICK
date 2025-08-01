package lu.itrust.business.ts.controller.analysis;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

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
import java.util.stream.Collectors;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lu.itrust.business.ts.component.ChartGenerator;
import lu.itrust.business.ts.component.CustomDelete;
import lu.itrust.business.ts.component.ImportCustomStandard;
import lu.itrust.business.ts.component.MeasureManager;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceAnalysisStandard;
import lu.itrust.business.ts.database.service.ServiceAsset;
import lu.itrust.business.ts.database.service.ServiceAssetType;
import lu.itrust.business.ts.database.service.ServiceDataValidation;
import lu.itrust.business.ts.database.service.ServiceDynamicParameter;
import lu.itrust.business.ts.database.service.ServiceLanguage;
import lu.itrust.business.ts.database.service.ServiceMaturityParameter;
import lu.itrust.business.ts.database.service.ServiceMeasure;
import lu.itrust.business.ts.database.service.ServiceMeasureAssetValue;
import lu.itrust.business.ts.database.service.ServiceMeasureDescription;
import lu.itrust.business.ts.database.service.ServicePhase;
import lu.itrust.business.ts.database.service.ServiceSimpleParameter;
import lu.itrust.business.ts.database.service.ServiceStandard;
import lu.itrust.business.ts.database.service.ServiceStorage;
import lu.itrust.business.ts.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.form.MeasureAssetValueForm;
import lu.itrust.business.ts.form.MeasureForm;
import lu.itrust.business.ts.form.SOAForm;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.helper.chartJS.model.Chart;
import lu.itrust.business.ts.model.actionplan.ActionPlanMode;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisSetting;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.asset.AssetType;
import lu.itrust.business.ts.model.cssf.tools.CategoryConverter;
import lu.itrust.business.ts.model.general.AssetTypeValue;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.general.OpenMode;
import lu.itrust.business.ts.model.general.Phase;
import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.parameter.impl.SimpleParameter;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.AssetStandard;
import lu.itrust.business.ts.model.standard.MaturityStandard;
import lu.itrust.business.ts.model.standard.NormalStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.StandardType;
import lu.itrust.business.ts.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.helper.MeasureComparator;
import lu.itrust.business.ts.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.ts.model.standard.measure.impl.MaturityMeasure;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureAssetValue;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureProperties;
import lu.itrust.business.ts.model.standard.measure.impl.NormalMeasure;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.ts.validator.MeasureDescriptionTextValidator;
import lu.itrust.business.ts.validator.MeasureDescriptionValidator;
import lu.itrust.business.ts.validator.StandardValidator;
import lu.itrust.business.ts.validator.field.ValidatorField;

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

	@Autowired
	private ServiceStorage serviceStorage;

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
	 * @
	 */
	@RequestMapping(value = "/Add/{idStandard}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String addStandard(@PathVariable int idStandard, HttpSession session, Principal principal,
			Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		try {
			final Standard standard = serviceStandard.get(idStandard);
			if (standard == null)
				return JsonMessage.error(messageSource.getMessage("error.analysis.add.standard.not_found", null,
						"Unfortunately, selected standard does not exist", locale));
			final Analysis analysis = serviceAnalysis.get(idAnalysis);
			if (analysis.getAnalysisStandards().values().stream()
					.anyMatch(a -> a.getStandard().getName().equalsIgnoreCase(standard.getName())
							|| a.getStandard().getLabel().equalsIgnoreCase(standard.getLabel())))
				return JsonMessage
						.error(messageSource.getMessage("error.analysis.add.standard.duplicate", null,
								"Your analysis already has another version of the standard!", locale));
			Measure measure = null;
			AnalysisStandard analysisStandard = null;
			final List<MeasureDescription> measureDescriptions = serviceMeasureDescription.getAllByStandard(standard);
			Object implementationRate = null;
			if (standard.getType() == StandardType.MATURITY && analysis.isQuantitative()) {
				analysisStandard = new MaturityStandard();
				measure = new MaturityMeasure();
				implementationRate = analysis.getSimpleParameters().stream()
						.filter(parameter -> parameter.isMatch(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME)
								&& (parameter.getValue().doubleValue() == 0
										|| parameter.getDescription().equals(Constant.IS_NOT_ACHIEVED)))
						.findAny().orElse(null);
			} else if (standard.getType() == StandardType.NORMAL) {
				analysisStandard = new NormalStandard();
				measure = new NormalMeasure();
				List<AssetType> assetTypes = serviceAssetType.getAll();
				List<AssetTypeValue> assetTypeValues = ((NormalMeasure) measure).getAssetTypeValues();
				for (AssetType assetType : assetTypes)
					assetTypeValues.add(new AssetTypeValue(assetType, 0));
				((AbstractNormalMeasure) measure).setMeasurePropertyList(new MeasureProperties());
				implementationRate = Double.valueOf(0d);
			} else
				throw new TrickException("error.action.not_authorise", "Action does not authorised");

			Phase phase = analysis.findPhaseByNumber(Constant.PHASE_DEFAULT);
			if (phase == null) {
				phase = new Phase(Constant.PHASE_DEFAULT);
				phase.setAnalysis(analysis);
				analysis.add(phase);
			}

			analysisStandard.setStandard(standard);
			measure.setImplementationRate(implementationRate);
			measure.setStatus(Constant.MEASURE_STATUS_APPLICABLE);

			for (MeasureDescription measureDescription : measureDescriptions) {
				Measure measure2 = measure.duplicate(analysisStandard, phase);
				measure2.setMeasureDescription(measureDescription);
				analysisStandard.getMeasures().add(measure2);
			}

			analysis.add(analysisStandard);

			serviceAnalysis.saveOrUpdate(analysis);

			return JsonMessage.success(messageSource.getMessage("success.analysis.add.standard", null,
					"The standard was successfully added", locale));
		} catch (TrickException e) {
			return JsonMessage
					.success(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.persist(e);
			return JsonMessage.error(messageSource.getMessage("error.analysis.add.standard", null,
					"An unknown error occurred during analysis saving", locale));
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Chart compliance(@PathVariable Integer standardId, @PathVariable ActionPlanMode type,
			HttpSession session, Principal principal, Locale locale) {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return serviceAnalysisStandard.getAllFromAnalysis(idAnalysis).stream()
				.filter(analysisStandard -> analysisStandard.getStandard().getId() == standardId)
				.map(analysisStandard -> chartGenerator.compliance(idAnalysis, analysisStandard, type, locale))
				.findAny().orElse(new Chart());
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody List<Chart> compliances(@PathVariable ActionPlanMode type, HttpSession session,
			Principal principal, Locale locale) {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return serviceAnalysisStandard.getAllFromAnalysis(idAnalysis).stream()
				.map(analysisStandard -> chartGenerator.compliance(idAnalysis, analysisStandard, type, locale))
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/Compute-efficiency", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object computeEfficience(@RequestBody List<String> chapters, HttpSession session,
			Principal principal) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		List<Measure> measures = serviceMeasure.getByAnalysisIdStandardAndChapters(idAnalysis, Constant.STANDARD_27002,
				chapters);
		List<Measure> maturities = serviceMeasure.getByAnalysisIdStandardAndChapters(idAnalysis,
				Constant.STANDARD_MATURITY,
				chapters.stream().map(reference -> "M." + reference).collect(Collectors.toList()));
		return MeasureManager.computeMaturiyEfficiencyRate(measures, maturities, loadMaturityParameters(idAnalysis),
				false,
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
	 * @
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object createStandardForm(@RequestBody String value, HttpSession session, Principal principal,
			Model model, RedirectAttributes attributes, Locale locale) {
		Map<String, String> errors = new LinkedHashMap<>();
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		try {
			final Analysis analysis = serviceAnalysis.get(idAnalysis);
			final Standard standard = buildStandard(errors, value, locale, analysis);
			if (!errors.isEmpty())
				return errors;
			if (standard.getId() < 1) {
				standard.setVersion(
						serviceStandard.getNextVersionByLabelAndType(standard.getLabel(), standard.getType()));
				switch (standard.getType()) {
					case ASSET:
						analysis.add(new AssetStandard(standard));
						break;
					case MATURITY:
						throw new TrickException("error.custom.maturity.standard",
								"Custom Maturity standard is not supported");
					case NORMAL:
					default:
						analysis.add(new NormalStandard(standard));
						break;
				}
				serviceAnalysis.saveOrUpdate(analysis);
				return JsonMessage.success(messageSource.getMessage("success.analysis.create.standard", null,
						"The standard was successfully created", locale));
			} else {
				serviceStandard.saveOrUpdate(standard);
				return JsonMessage.success(messageSource.getMessage("success.analysis.update.standard", null,
						"The standard was successfully updated", locale));
			}
		} catch (TrickException e) {
			errors.put("standard", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.persist(e);
			return errors;
		} catch (Exception e) {
			TrickLogManager.persist(e);
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
	 * @
	 */
	@DeleteMapping(value = "/{idStandard}/Measure/Delete/{idMeasure}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idMeasure, 'Measure', #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String deleteMeasureDescription(@PathVariable("idStandard") int idStandard,
			@PathVariable("idMeasure") int idMeasure, Locale locale, Principal principal,
			HttpSession session) {
		try {
			customDelete.deleteMeasure((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), idStandard,
					idMeasure);
			// return success message
			return JsonMessage.success(messageSource.getMessage("success.measure.delete.successfully", null,
					"Measure was deleted successfully", locale));
		} catch (Exception e) {
			TrickLogManager.persist(e);
			if (e instanceof TrickException)
				return JsonMessage.error(messageSource.getMessage(((TrickException) e).getCode(),
						((TrickException) e).getParameters(), e.getMessage(), locale));
			return JsonMessage.error(messageSource.getMessage("error.measure.delete.failed", null,
					"Measure deleting was failed: Standard might be in use", locale));
		}
	}

	@DeleteMapping(value = "/{idStandard}/Measure/Delete", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, Object> deleteMeasure(@PathVariable int idStandard, @RequestBody List<Integer> ids,
			HttpSession session,
			Principal principal, Locale locale) {
		final Map<String, Object> results = new LinkedHashMap<>();
		try {
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			final List<Integer> deletedIds = customDelete.deleteMeasures(ids, idStandard, idAnalysis);
			if (deletedIds.isEmpty()) {
				JsonMessage.error(results, messageSource.getMessage("error.measure.delete.failed", null,
						"Measure deleting was failed: Standard might be in use", locale));
			} else {
				JsonMessage.success(results,
						messageSource.getMessage("success.measure.delete.successfully", null,
								"Measure was deleted successfully", locale));
				results.put("ids", deletedIds);
			}
		} catch (TrickException e) {
			JsonMessage.error(results,
					messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.persist(e);
		} catch (Exception e) {
			JsonMessage.error(results, messageSource.getMessage("error.measure.delete.failed", null,
					"Measure deleting was failed: Standard might be in use", locale));
			TrickLogManager.persist(e);
		}
		return results;
	}

	/**
	 * editAssetMeasure: <br>
	 * Description
	 * 
	 * @param idMeasure
	 * @param locale
	 * @param model
	 * @param principal
	 * @param session
	 * @param attributes
	 * @return
	 */
	@RequestMapping(value = "/Measure/{idMeasure}/Edit", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idMeasure, 'Measure', #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public String editAssetMeasure(@PathVariable("idMeasure") int idMeasure, Locale locale, Model model,
			Principal principal, HttpSession session, RedirectAttributes attributes) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		try {

			Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, idMeasure);
			if (measure == null)
				throw new TrickException("error.measure.not_found", "Measure cannot be found");
			else if (!(measure.getMeasureDescription().getStandard().isComputable()
					|| measure.getMeasureDescription().getStandard().isAnalysisOnly()))
				throw new TrickException("error.action.not_authorise", "Action does not authorised");

			AnalysisType type = serviceAnalysis.getAnalysisTypeById(idAnalysis);

			List<AssetType> analysisAssetTypes = serviceAssetType.getAllFromAnalysis(idAnalysis);

			if (measure instanceof AssetMeasure assetMeasure) {

				List<Asset> availableAssets = serviceAsset.getAllFromAnalysisIdAndSelected(idAnalysis);

				model.addAttribute("availableAssets", availableAssets);

				model.addAttribute("assetTypes", analysisAssetTypes);

				if (!(availableAssets.isEmpty() || assetMeasure.getMeasureAssetValues().isEmpty())) {
					for (MeasureAssetValue assetValue : assetMeasure.getMeasureAssetValues())
						availableAssets.remove(assetValue.getAsset());
				}

			} else if (measure instanceof NormalMeasure normalMeasure) {

				List<AssetType> assetTypes = serviceAssetType.getAll();
				Map<String, Boolean> assetTypesMapping = new LinkedHashMap<>();
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

			model.addAttribute("isComputable", measure.getMeasureDescription().getStandard().isComputable());

			model.addAttribute("isAnalysisOnly", measure.getMeasureDescription().getStandard().isAnalysisOnly());

			model.addAttribute("measureForm",
					MeasureForm.build(measure, type, serviceLanguage.getFromAnalysis(idAnalysis).getAlpha3()));

			// return success message
			return "jsp/analyses/single/components/standards/measure/form";
		} catch (TrickException e) {
			TrickLogManager.persist(e);
			attributes.addFlashAttribute("error",
					messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.persist(e);
			attributes.addFlashAttribute("error",
					messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
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
	 * @
	 */
	@RequestMapping(value = "/Available", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public String getAvailableStandards(HttpSession session, Model model, Principal principal, Locale locale) {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		model.addAttribute("availableStandards",
				AnalysisType.isQuantitative(serviceAnalysis.getAnalysisTypeById(idAnalysis))
						? serviceStandard.getAllNotInAnalysis(idAnalysis)
						: serviceStandard.getAllNotInAnalysisAndNotMaturity(idAnalysis));
		return "jsp/analyses/single/components/standards/standard/form/importFromKb";
	}

	/****
	 * 
	 *
	 */
	@GetMapping(value = "/Suggest/{type}/{name}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody List<String> listOfExistenceStandardName(@PathVariable StandardType type,
			@PathVariable String name, HttpSession session,
			Principal principal) {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return serviceAnalysisStandard.findByAnalysisAndNameLikeAndTypeAndCustom(idAnalysis, name, type, true);
	}

	@PostMapping(value = "/Import-from-file", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object importFromFile(@RequestParam(value = "file") MultipartFile file,
			@RequestParam(value = "id") int id,
			@RequestParam(value = "type", required = false) StandardType type,
			@RequestParam(value = "name", required = false) String name,
			HttpSession session, Principal principal, Locale locale) {
		if (id > 0) {
			final Standard standard = serviceStandard.get(id);
			if (standard != null) {
				name = standard.getName();
				type = standard.getType();
			} else if (type == null || !StringUtils.hasLength(name)) {
				return JsonMessage.error(messageSource.getMessage("error.standard.not_found", null, locale));
			}
		}
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final String filename = ServiceStorage.RandoomFilename("xlsx");
		serviceStorage.store(file, filename);
		return new ImportCustomStandard(type, name, filename).importStandard(idAnalysis, principal.getName(), locale);
	}

	@GetMapping(value = "/Export/{idStandard}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public void exportStandard(@PathVariable int idStandard, HttpSession session, Principal principal,
			HttpServletResponse response, Locale locale) throws TrickException, Docx4JException, IOException {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (idAnalysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis not found");
		if (!serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.READ))
			throw new AccessDeniedException(
					messageSource.getMessage("error.action.not_authorise", null, "Action does not authorised", locale));

		final Standard standard = serviceStandard.get(idStandard);
		if (standard == null)
			throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,
					messageSource.getMessage("error.standard.not_found", null, "Standard not found", locale));

		if (!standard.isAnalysisOnly())
			throw new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN,
					messageSource.getMessage("error.export.standard.analysis_only", null,
							"This standard can only be exported from the knowledge base.", locale));

		if (!measureManager.exportStandard(idStandard, response, principal.getName()))
			throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,
					messageSource.getMessage("error.standard.not_found", null, "Standard not found", locale));
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
	 * @
	 */
	@RequestMapping(value = "/{idStandard}/SingleMeasure/{elementID}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public String loadSingleMeasure(@PathVariable int elementID, Model model, HttpSession session,
			Principal principal) {
		final OpenMode mode = (OpenMode) session.getAttribute(Constant.OPEN_MODE);
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Analysis analysis = serviceAnalysis.get(idAnalysis);
		final Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);
		final boolean allowedTicketing = loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(), model,
				null);
		final boolean isNoClientTicketing = (boolean) model.asMap().getOrDefault("isNoClientTicketing", false);
		model.addAttribute("measure", measure);
		loadAnalysisSettings(model, idAnalysis);
		loadEfficience(model, idAnalysis, measure);
		model.addAttribute("isAnalysisOnly", measure.getMeasureDescription().getStandard().isAnalysisOnly());
		model.addAttribute("isEditable", !OpenMode.isReadOnly(mode)
				&& serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.MODIFY));
		model.addAttribute("standard", measure.getMeasureDescription().getStandard().getName());
		model.addAttribute("selectedStandard", measure.getMeasureDescription().getStandard());
		model.addAttribute("standardType", measure.getMeasureDescription().getStandard().getType());
		model.addAttribute("standardid", measure.getMeasureDescription().getStandard().getId());
		model.addAttribute("isLinkedToProject",
				allowedTicketing && (isNoClientTicketing || serviceAnalysis.hasProject(idAnalysis)));
		model.addAttribute("valueFactory", new ValueFactory(serviceDynamicParameter.findByAnalysisId(idAnalysis)));
		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));
		return "jsp/analyses/single/components/standards/measure/singleMeasure";
	}

	@RequestMapping(value = "/Measure/{idMeasure}/Description/{langue}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idMeasure, 'Measure', #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody String loadDescription(@PathVariable("idMeasure") int idMeasure,
			@PathVariable("langue") String langue, Principal principal, HttpSession session,
			Locale locale) {
		Measure measure = serviceMeasure.get(idMeasure);
		MeasureDescriptionText measureDescriptionText = measure.getMeasureDescription().findByAlph2(langue);
		if (measureDescriptionText == null)
			return JsonMessage.error(messageSource.getMessage("error.measure.description.empty", null,
					"There is no other description for this security measure", locale));
		else
			return JsonMessage.field("description", measureDescriptionText.getDescription());
	}

	@RequestMapping(value = "/Measures", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object loadMeasureByStandard(@RequestParam("idStandard") Integer idStandard,
			Principal principal, HttpSession session, Locale locale) {
		final ValueFactory factory = new ValueFactory(
				serviceDynamicParameter.findByAnalysisId((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS)));
		return serviceMeasure
				.getAllFromAnalysisAndStandard((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), idStandard)
				.stream()
				.filter(measure -> !measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
						&& measure.getImplementationRateValue(Collections.emptyList()) < 100)
				.sorted(new MeasureComparator()).map(measure -> {
					MeasureForm measureForm = new MeasureForm();
					MeasureDescriptionText descriptionText = measure.getMeasureDescription()
							.getMeasureDescriptionTextByAlpha2(locale.getLanguage());
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
	 * @
	 */
	@RequestMapping(value = "/SOA", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public String loadSOA(HttpSession session, Principal principal, Model model) {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		IParameter parameter = serviceSimpleParameter.findByAnalysisIdAndDescription(idAnalysis,
				Constant.SOA_THRESHOLD);

		model.addAttribute("soaThreshold", parameter == null ? 100.0 : parameter.getValue().doubleValue());

		Comparator<Measure> comparator = new MeasureComparator();

		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));

		model.addAttribute("valueFactory", new ValueFactory(serviceDynamicParameter.findByAnalysisId(idAnalysis)));

		model.addAttribute("soas", serviceAnalysisStandard.findBySOAEnabledAndAnalysisId(true, idAnalysis).stream()
				.sorted((e1, e2) -> NaturalOrderComparator.compareTo(e1.getStandard().getName(),
						e2.getStandard().getName()))
				.map(analysisStandard -> {
					analysisStandard.getMeasures().sort(comparator);
					return analysisStandard;
				})
				.collect(Collectors.toMap(AnalysisStandard::getStandard,
						e -> e.getMeasures().stream()
								.filter(b -> !(b.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
										|| b.getStatus().equals(Constant.MEASURE_STATUS_OPTIONAL)))
								.toList(),
						(e1, e2) -> e1, LinkedHashMap::new)));

		return "jsp/analyses/single/components/soa/home";
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
	 * @
	 */
	@RequestMapping(value = "/Manage", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public String manageForm(HttpSession session, Principal principal, Model model, RedirectAttributes attributes,
			Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		model.addAttribute("canExport",
				serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.EXPORT));

		model.addAttribute("currentStandards", serviceStandard.getAllFromAnalysis(idAnalysis));
		return "jsp/analyses/single/components/standards/standard/form/manage";
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
	 * @
	 */
	@RequestMapping(value = "/SOA/Manage", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public String manageSOA(HttpSession session, Principal principal, Model model, RedirectAttributes attributes,
			Locale locale) {
		model.addAttribute("analysisStandards",
				serviceAnalysisStandard.findByAndAnalysisIdAndTypeIn(
						(Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), NormalStandard.class,
						AssetStandard.class));
		return "jsp/analyses/single/components/soa/form";
	}

	@RequestMapping(value = "/Measure/{idMeasure}/Load", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idMeasure, 'Measure', #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public String measureForm(@PathVariable("idMeasure") int idMeasure, Locale locale, Model model, Principal principal,
			HttpSession session) {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		try {
			final Measure measure = serviceMeasure.get(idMeasure);
			final Analysis analysis = serviceAnalysis.get(idAnalysis);
			final boolean isMaturity = measure instanceof MaturityMeasure;
			final List<Phase> phases = servicePhase.getAllFromAnalysis(idAnalysis);
			final MeasureDescription measureDescription = measure.getMeasureDescription();
			final MeasureDescriptionText measureDescriptionText = measureDescription
					.getMeasureDescriptionTextByAlpha2(locale.getLanguage());
			final boolean allowedTicketing = loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(),
					model,
					null);
			final boolean isNoClientTicketing = (boolean) model.asMap().getOrDefault("isNoClientTicketing", false);

			model.addAttribute("measureDescriptionText", measureDescriptionText);
			model.addAttribute("measureDescription", measureDescription);
			if (measureDescriptionText != null) {
				model.addAttribute("countLine",
						measureDescriptionText.getDescription().trim().split("\r\n|\r|\n").length);
				MeasureDescriptionText otherMeasureDescriptionText = measureDescriptionText.getLanguage().getAlpha3()
						.equalsIgnoreCase("fra") ? measureDescription.findByAlph2("en")
								: measureDescription.findByAlph2("fr");
				if (otherMeasureDescriptionText != null
						&& StringUtils.hasText(otherMeasureDescriptionText.getDescription()))
					model.addAttribute("otherMeasureDescriptionText", true);
			}
			model.addAttribute("measureDescription", measureDescription);

			model.addAttribute("isMaturity", isMaturity);
			if (isMaturity)
				model.addAttribute("impscales", serviceSimpleParameter
						.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME, idAnalysis));

			model.addAttribute("isLinkedToProject",
					allowedTicketing && (isNoClientTicketing || serviceAnalysis.hasProject(idAnalysis)));

			model.addAttribute("showTodo", measureDescription.isComputable());
			model.addAttribute("selectedMeasure", measure);
			model.addAttribute("phases", phases);
		} catch (Exception e) {
			TrickLogManager.persist(e);
		}
		return "jsp/analyses/single/components/standards/edition/measure";

	}

	@RequestMapping(value = "/Measure/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, Object> measuresave(@RequestBody MeasureForm measureForm, Model model,
			Principal principal, HttpSession session, Locale locale) {
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> errors = new LinkedHashMap<>();
		try {
			result.put("errors", errors);
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			AnalysisStandard analysisStandard = analysis.getAnalysisStandards().values().stream()
					.filter(e -> e.getStandard().getId() == measureForm.getIdStandard()).findAny().orElse(null);
			AnalysisType type = analysis.getType();

			if (analysisStandard == null) {
				errors.put("standard", messageSource.getMessage("error.standard.not_in_analysis", null,
						"Standard does not belong to analysis!", locale));
				return result;
			}

			Measure measure = null;
			if (measureForm.getId() > 0) {
				measure = analysisStandard.getMeasures().stream().filter(e -> e.getId() == measureForm.getId())
						.findAny().orElse(null);
				if (measure == null)
					errors.put("measure", messageSource.getMessage("error.measure.belong.standard", null,
							"Measure does not belong to standard", locale));
				else if (measure instanceof AssetMeasure && measureForm.isComputable()
						&& measureForm.getAssetValues().isEmpty())
					errors.put("asset",
							messageSource.getMessage("error.asset.empty", null, "Asset cannot be empty", locale));
				if (!errors.isEmpty())
					return result;
			} else {
				switch (measureForm.getType()) {
					case ASSET:
						measure = new AssetMeasure();
						if (measureForm.isComputable() && measureForm.getAssetValues().isEmpty()) {
							errors.put("asset", messageSource.getMessage("error.asset.empty", null,
									"Asset cannot be empty", locale));
							return result;
						}
						break;
					case NORMAL:
						measure = new NormalMeasure();
						break;
					default:
						throw new TrickException("error.measure.cannot.be.created", "Measure cannot be created");
				}

				Phase phase = analysis.findPhaseByNumber(Constant.PHASE_DEFAULT);
				if (phase == null) {
					errors.put("phase", messageSource.getMessage("error.measure.default.pahse.not_found", null,
							"Default phase cannot be found", locale));
					return result;
				}

				measure.setPhase(phase);

				measure.setStatus(Constant.MEASURE_STATUS_APPLICABLE);

				measure.setImplementationRate(0.0);

				analysisStandard.add(measure);
			}

			if (AnalysisType.isQuantitative(type) && measureForm.getProperties() == null) {
				errors.put("properties",
						messageSource.getMessage("error.properties.empty", null, "Properties cannot be empty", locale));
				return result;
			}

			if (analysisStandard.getStandard().isAnalysisOnly()) {
				validate(measureForm, errors, locale);
				if (!errors.isEmpty())
					return result;
				if (!update(measure, measureForm, analysis, analysisStandard, locale,
						errors).isEmpty())
					return result;
			} else if (StandardType.NORMAL.equals(analysisStandard.getStandard().getType())) {
				if (measure.getId() < 1)
					throw new TrickException("error.measure.not_found", "Measure cannot be found");
				measureForm.getProperties().copyTo(((AbstractNormalMeasure) measure).getMeasurePropertyList());
				if (!updateAssetTypeValues((NormalMeasure) measure, measureForm.getAssetValues(), errors, locale)
						.isEmpty())
					return result;
			} else
				throw new TrickException("error.action.not_authorise", "Action does not authorised");

			serviceAnalysis.saveOrUpdate(analysis);

			result.put("id", measure.getId());

		} catch (TrickException e) {
			TrickLogManager.persist(e);
			errors.put("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.persist(e);
			errors.put("error",
					messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
		return result;
	}

	@GetMapping(value = "/{idStandard}/Measure/New", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public String newAssetMeasure(@PathVariable("idStandard") int idStandard, Model model, HttpSession session,
			Principal principal, RedirectAttributes attributes, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		try {

			Language language = serviceLanguage.getFromAnalysis(idAnalysis);

			locale = new Locale(language.getAlpha2());

			AnalysisStandard analysisStandard = serviceAnalysisStandard.getFromAnalysisIdAndStandardId(idAnalysis,
					idStandard);

			if (analysisStandard == null)
				throw new TrickException("error.standard.not_in_analysis", "Standard does not beloong to analysis!");

			if (!analysisStandard.getStandard().isAnalysisOnly())
				throw new TrickException("error.action.not_authorise", "Action does not authorised");

			AnalysisType type = serviceAnalysis.getAnalysisTypeById(idAnalysis);

			Measure measure = MeasureManager.create(analysisStandard.getStandard().getType());

			List<AssetType> analysisAssetTypes = serviceAssetType.getAllFromAnalysis(idAnalysis);

			if (measure instanceof AssetMeasure assetMeasure) {

				List<Asset> availableAssets = serviceAsset.getAllFromAnalysisIdAndSelected(idAnalysis);

				model.addAttribute("availableAssets", availableAssets);

				model.addAttribute("assetTypes", analysisAssetTypes);

				assetMeasure.setMeasurePropertyList(new MeasureProperties());

			} else if (measure instanceof NormalMeasure normalMeasure) {
				normalMeasure.setMeasurePropertyList(new MeasureProperties());
				List<AssetType> assetTypes = serviceAssetType.getAll();

				Map<String, Boolean> assetTypesMapping = new LinkedHashMap<>();
				for (AssetType assetType : assetTypes) {
					if (!analysisAssetTypes.contains(assetType))
						assetTypesMapping.put(assetType.getName(), false);
					normalMeasure.addAnAssetTypeValue(new AssetTypeValue(assetType, 0));
				}
				model.addAttribute("hiddenAssetTypes", assetTypesMapping);
			}

			measure.setMeasureDescription(
					new MeasureDescription(analysisStandard.getStandard(), new MeasureDescriptionText(language)));

			if (type == AnalysisType.QUANTITATIVE) {
				Map<String, Boolean> excludes = new HashMap<>();
				for (String category : CategoryConverter.TYPE_CSSF_KEYS)
					excludes.put(category, true);
				model.addAttribute("cssfExcludes", excludes);
			}

			model.addAttribute("isComputable", analysisStandard.getStandard().isComputable());

			model.addAttribute("type", type);

			model.addAttribute("isAnalysisOnly", analysisStandard.getStandard().isAnalysisOnly());

			model.addAttribute("measureForm", MeasureForm.build(measure, type, language.getAlpha3()));

			// return success message
			return "jsp/analyses/single/components/standards/measure/form";
		} catch (TrickException e) {
			TrickLogManager.persist(e);
			attributes.addFlashAttribute("error",
					messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.persist(e);
			attributes.addFlashAttribute("error",
					messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
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
	 * @
	 */
	@RequestMapping(value = "/Delete/{idStandard}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String removeStandard(@PathVariable int idStandard, HttpSession session, Principal principal,
			Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		try {
			measureManager.removeStandardFromAnalysis(idAnalysis, idStandard);
			return JsonMessage.success(messageSource.getMessage("success.analysis.norm.delete", null,
					"Standard was successfully removed from your analysis", locale));
		} catch (Exception e) {
			TrickLogManager.persist(e);
			return JsonMessage.error(messageSource.getMessage("error.analysis.norm.delete", null,
					"Standard could not be deleted!", locale));
		}
	}

	@RequestMapping(value = "/SOA/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object saveSOA(@RequestBody List<SOAForm> soaForms, HttpSession session, Principal principal,
			Model model, RedirectAttributes attributes, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		soaForms.forEach(form -> {
			AnalysisStandard analysisStandard = serviceAnalysisStandard.findOne(form.getId(), idAnalysis);
			if (analysisStandard != null) {
				analysisStandard.setSoaEnabled(form.isEnabled());
				serviceAnalysisStandard.saveOrUpdate(analysisStandard);
			}
		});
		return JsonMessage.success(
				messageSource.getMessage("success.update.soa", null, "SOA has been successfully updated", locale));
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param session
	 * @param model
	 * @param principal
	 * @return
	 * @
	 */
	@RequestMapping("/Section")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public String section(HttpSession session, Model model, Principal principal) {

		// retrieve analysis id
		final OpenMode mode = (OpenMode) session.getAttribute(Constant.OPEN_MODE);

		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		final Analysis analysis = serviceAnalysis.get(idAnalysis);

		final List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);

		final List<Standard> standards = new ArrayList<>(analysisStandards.size());

		final Map<String, List<Measure>> measuresByStandard = new LinkedHashMap<>(analysisStandards.size());

		final ValueFactory factory = new ValueFactory(serviceDynamicParameter.findByAnalysisId(idAnalysis));

		final boolean hasMaturity = measuresByStandard.containsKey(Constant.STANDARD_MATURITY);

		final boolean allowedTicketing = loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(),
				model,
				null);
		final boolean isNoClientTicketing = (boolean) model.asMap().getOrDefault("isNoClientTicketing", false);

		analysisStandards.forEach(analysisStandard -> {
			standards.add(analysisStandard.getStandard());
			measuresByStandard.put(analysisStandard.getStandard().getName(), analysisStandard.getMeasures());
		});

		if (hasMaturity)
			model.addAttribute("effectImpl27002",
					MeasureManager.computeMaturiyEfficiencyRate(measuresByStandard.get(Constant.STANDARD_27002),
							measuresByStandard.get(Constant.STANDARD_MATURITY), loadMaturityParameters(idAnalysis),
							true, factory));

		model.addAttribute("hasMaturity", hasMaturity);

		model.addAttribute("standards", standards);

		model.addAttribute("measuresByStandard", measuresByStandard);

		model.addAttribute("isLinkedToProject",
				allowedTicketing && (isNoClientTicketing || serviceAnalysis.hasProject(idAnalysis)));

		model.addAttribute("isEditable", !OpenMode.isReadOnly(mode)
				&& serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.MODIFY));

		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));

		model.addAttribute("valueFactory", factory);

		loadAnalysisSettings(model, idAnalysis);

		return "jsp/analyses/single/components/standards/standard/standards";
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param session
	 * @param model
	 * @param principal
	 * @return
	 * @
	 */
	@RequestMapping("/Section/{standardid}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public String sectionByStandard(@PathVariable Integer standardid, HttpSession session, Model model,
			Principal principal) {

		// retrieve analysis id
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (idAnalysis == null)
			return null;
		final OpenMode mode = (OpenMode) session.getAttribute(Constant.OPEN_MODE);
		final AnalysisStandard analysisStandard = serviceAnalysisStandard.getFromAnalysisIdAndStandardId(idAnalysis,
				standardid);
		if (analysisStandard == null)
			return null;
		final Analysis analysis = serviceAnalysis.get(idAnalysis);
		final List<Standard> standards = new ArrayList<>(1);
		final Map<String, List<Measure>> measuresByStandard = new HashMap<>(1);
		final ValueFactory factory = new ValueFactory(serviceDynamicParameter.findByAnalysisId(idAnalysis));
		final boolean allowedTicketing = loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(),
				model,
				null);
		final boolean isNoClientTicketing = (boolean) model.asMap().getOrDefault("isNoClientTicketing", false);

		if (analysisStandard.getStandard().is(Constant.STANDARD_27002)) {
			AnalysisStandard maturityStandard = serviceAnalysisStandard.getFromAnalysisIdAndStandardName(idAnalysis,
					Constant.STANDARD_MATURITY);
			if (maturityStandard != null) {
				if (maturityStandard.getStandard().isComputable())
					model.addAttribute("effectImpl27002",
							MeasureManager.computeMaturiyEfficiencyRate(analysisStandard.getMeasures(),
									maturityStandard.getMeasures(),
									loadMaturityParameters(idAnalysis), true, factory));
				model.addAttribute("hasMaturity", true);
			} else
				model.addAttribute("hasMaturity", false);
		}

		standards.add(analysisStandard.getStandard());

		measuresByStandard.put(analysisStandard.getStandard().getName(), analysisStandard.getMeasures());

		model.addAttribute("standards", standards);

		model.addAttribute("measuresByStandard", measuresByStandard);

		model.addAttribute("isLinkedToProject",
				allowedTicketing && (isNoClientTicketing || serviceAnalysis.hasProject(idAnalysis)));

		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));

		model.addAttribute("isEditable", !OpenMode.isReadOnly(mode)
				&& serviceUserAnalysisRight.isUserAuthorized(idAnalysis, principal.getName(), AnalysisRight.MODIFY));

		model.addAttribute("valueFactory", factory);

		loadAnalysisSettings(model, idAnalysis);

		return "jsp/analyses/single/components/standards/standard/standards";
	}

	@RequestMapping(value = "/Update/Cost", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String updateCost(HttpSession session, Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		double externalSetupValue = -1, internalSetupValue = -1, lifetimeDefault = -1;
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		Iterator<SimpleParameter> iterator = analysis.getSimpleParameters().stream()
				.filter(parameter -> parameter.isMatch(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME)).iterator();
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
		return JsonMessage.success(messageSource.getMessage("success.measure.cost.update", null,
				"Measure cost has been successfully updated", locale));
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
					errors.put("standard", messageSource.getMessage("error.standard.not.belong.selected.analysis", null,
							"Standard does not belong to selected analysis", locale));
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
			else if ((standard.getId() < 1 || !prevLabel.equalsIgnoreCase(label))
					&& analysis.findStandardByLabel(label) != null)
				errors.put("label", messageSource.getMessage("error.analysis.standard_exist_in_analysis", null,
						"The standard already exists in this analysis!", locale));
			else
				standard.setLabel(label);

			error = validator.validate(standard, "name", label);
			if (error != null)
				errors.put("name", serviceDataValidation.ParseError(error, messageSource, locale));
			else if ((standard.getId() < 1 || !prevName.equalsIgnoreCase(name))
					&& analysis.findStandardByName(name) != null)
				errors.put("name", messageSource.getMessage("error.analysis.standard_exist_in_analysis", null,
						"The standard already exists in this analysis!", locale));
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
				standard.setVersion(serviceStandard.getNextVersionByLabelAndType(label, standard.getType()));
			// return success
			return standard;

		} catch (TrickException e) {
			errors.put("standard", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.persist(e);
		} catch (Exception e) {
			// return error
			errors.put("standard",
					messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
			TrickLogManager.persist(e);
		}
		return null;
	}

	private void loadAnalysisSettings(Model model, Integer integer) {
		Map<String, String> settings = serviceAnalysis.getSettingsByIdAnalysis(integer);
		AnalysisSetting rawSetting = AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN,
				hiddenCommentSetting = AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT,
				dynamicAnalysis = AnalysisSetting.ALLOW_DYNAMIC_ANALYSIS;
		model.addAttribute("showHiddenComment",
				Analysis.findSetting(hiddenCommentSetting, settings.get(hiddenCommentSetting.name())));
		model.addAttribute("showRawColumn", Analysis.findSetting(rawSetting, settings.get(rawSetting.name())));
		model.addAttribute("showDynamicAnalysis",
				Analysis.findSetting(dynamicAnalysis, settings.get(dynamicAnalysis.name())));
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
			if (!measure.getMeasureDescription().getStandard().is(Constant.STANDARD_27002))
				return;
			if (measure.getMeasureDescription().isComputable()) {
				ValueFactory factory = new ValueFactory(serviceDynamicParameter.findByAnalysisId(idAnalysis));
				String chapter = measure.getMeasureDescription().getReference().split("[.]", 2)[0];
				List<Measure> measures = serviceMeasure.getReferenceStartWith(idAnalysis, Constant.STANDARD_MATURITY,
						"M." + chapter);
				if (measures.isEmpty())
					model.addAttribute("hasMaturity",
							serviceAnalysisStandard.hasStandard(idAnalysis, Constant.STANDARD_MATURITY));
				else {
					model.addAttribute("hasMaturity", true);
					Double maturity = MeasureManager
							.computeMaturityByChapter(measures, loadMaturityParameters(idAnalysis), factory)
							.get(chapter);
					model.addAttribute("effectImpl27002",
							maturity == null ? 0 : measure.getImplementationRateValue(factory) * maturity * 0.01);
				}
			} else
				model.addAttribute("hasMaturity",
						serviceAnalysisStandard.hasStandard(idAnalysis, Constant.STANDARD_MATURITY));
		} catch (Exception e) {
			TrickLogManager.persist(e);
		}

	}

	private List<IParameter> loadMaturityParameters(Integer idAnalysis) {
		List<IParameter> parameters = new LinkedList<>(serviceMaturityParameter.findByAnalysisId(idAnalysis));
		parameters.addAll(
				serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME, idAnalysis));
		return parameters;
	}

	private Map<String, Object> update(Measure measure, MeasureForm measureForm, Analysis analysis,
			AnalysisStandard analysisStandard, Locale locale,
			Map<String, Object> errors) {
		if (errors == null)
			errors = new LinkedHashMap<>();
		MeasureDescription description = measure.getMeasureDescription();
		if (description == null) {
			if (analysisStandard.getMeasures().stream().filter(e -> e != measure).anyMatch(
					e -> e.getMeasureDescription().getReference().equalsIgnoreCase(measureForm.getReference()))) {
				errors.put("reference",
						messageSource.getMessage("error.measure_description.reference.duplicated",
								new String[] { measure.getMeasureDescription().getStandard().getName() },
								String.format("The reference already exists for %s",
										measure.getMeasureDescription().getStandard().getName()),
								locale));
				return errors;
			}
			description = serviceMeasureDescription.getByReferenceAndStandard(measureForm.getReference(),
					analysisStandard.getStandard());
			if (description == null) {
				description = new MeasureDescription(measureForm.getReference(),
						analysisStandard.getStandard(), measureForm.isComputable());
				description.addMeasureDescriptionText(new MeasureDescriptionText(description, measureForm.getDomain(),
						measureForm.getDescription(), analysis.getLanguage()));
			}
			measure.setMeasureDescription(description);
		} else if (!description.getReference().equals(measureForm.getReference())) {
			if (analysisStandard.getMeasures().stream().allMatch(
					e -> e.getMeasureDescription().getReference().equalsIgnoreCase(measureForm.getReference()))) {
				errors.put("reference",
						messageSource.getMessage("error.measure_description.reference.duplicated",
								new String[] { measure.getMeasureDescription().getStandard().getName() },
								String.format("The reference already exists for %s",
										measure.getMeasureDescription().getStandard().getName()),
								locale));
				return errors;
			}
			description.setReference(measureForm.getReference());
		}
		if (description.getId() > 0) {
			MeasureDescriptionText descriptionText = description.findByLanguage(analysis.getLanguage());
			if (descriptionText == null)
				description.addMeasureDescriptionText(new MeasureDescriptionText(description, measureForm.getDomain(),
						measureForm.getDescription(), analysis.getLanguage()));
			else
				descriptionText.update(measureForm.getDomain(), measureForm.getDescription());
		}

		description.setComputable(measureForm.isComputable());

		if (AnalysisType.isQuantitative(analysis.getType()) && measureForm.getProperties() == null) {
			errors.put("properties",
					messageSource.getMessage("error.properties.empty", null, "Properties cannot be empty", locale));
			return errors;
		}

		if (measure instanceof AssetMeasure assetMeasure) {
			if (assetMeasure.getMeasurePropertyList() == null)
				assetMeasure.setMeasurePropertyList(new MeasureProperties());

			if (AnalysisType.isQuantitative(analysis.getType()))
				measureForm.getProperties().copyTo(assetMeasure.getMeasurePropertyList());

			List<MeasureAssetValue> assetValues = new ArrayList<>(measureForm.getAssetValues().size());
			for (MeasureAssetValueForm assetValueForm : measureForm.getAssetValues()) {
				Asset asset = analysis.getAssets().stream().filter(e -> e.getId() == assetValueForm.getId()).findAny()
						.orElse(null);
				if (asset == null)
					throw new TrickException("error.asset.not_found", "Asset does not found");
				MeasureAssetValue assetValue = serviceMeasureAssetValue.getByMeasureIdAndAssetId(measure.getId(),
						asset.getId());
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
		} else if (measure instanceof NormalMeasure normalMeasure) {
			if (normalMeasure.getMeasurePropertyList() == null)
				normalMeasure.setMeasurePropertyList(new MeasureProperties());
			if (AnalysisType.isQuantitative(analysis.getType()))
				measureForm.getProperties().copyTo(normalMeasure.getMeasurePropertyList());
			updateAssetTypeValues(normalMeasure, measureForm.getAssetValues(), errors, locale);
		}

		return errors;
	}

	private Map<String, Object> updateAssetTypeValues(NormalMeasure measure,
			List<MeasureAssetValueForm> assetValueForms, final Map<String, Object> errors, Locale locale) {
		Map<Integer, AssetType> assetTypes = new LinkedHashMap<>();
		serviceAssetType.getAll().stream().forEach(assetType -> assetTypes.put(assetType.getId(), assetType));
		assetValueForms.stream().forEach(assetTypeValueForm -> {
			try {
				AssetType assetType = assetTypes.get(assetTypeValueForm.getId());
				if (assetType == null) {
					errors.put("assetType", messageSource.getMessage("error.asset_type.not_found", null,
							"Asset type cannot be found", locale));
					return;
				}
				AssetTypeValue assetTypeValue = measure.getAssetTypeValueByAssetType(assetType);
				if (assetTypeValue == null)
					measure.addAnAssetTypeValue(
							assetTypeValue = new AssetTypeValue(assetType, assetTypeValueForm.getValue()));
				else
					assetTypeValue.setValue(assetTypeValueForm.getValue());
			} catch (TrickException e) {
				errors.put("assetTypeValue",
						messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
				return;
			}
		});
		return errors;

	}

	private void updateMeasureCost(double externalSetupValue, double internalSetupValue, double lifetimeDefault,
			Analysis analysis) {
		final boolean isFullRelatedCost = analysis.findSetting(AnalysisSetting.ALLOW_FULL_COST_RELATED_TO_MEASURE);
		final ValueFactory factory = new ValueFactory(analysis.getParameters());
		analysis.getAnalysisStandards().values().stream()
				.flatMap(analysisStandard -> analysisStandard.getMeasures().parallelStream()).forEach(measure -> {
					final double implementationRate = measure.getImplementationRateValue(factory) * 0.01;
					final double cost = Analysis.computeCost(internalSetupValue, externalSetupValue, lifetimeDefault,
							measure.getInternalMaintenance(), measure.getExternalMaintenance(),
							measure.getRecurrentInvestment(), measure.getInternalWL(), measure.getExternalWL(),
							measure.getInvestment(), measure.getLifetime(), implementationRate, isFullRelatedCost);
					measure.setCost(cost >= 0D ? cost : 0D);
				});
	}

	private void validate(MeasureForm measureForm, Map<String, Object> errors, Locale locale) {
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
