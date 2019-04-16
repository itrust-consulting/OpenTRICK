package lu.itrust.business.TS.controller.analysis;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.MeasureManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.helper.JSTLFunctions;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.helper.RFFMeasureFilter;
import lu.itrust.business.TS.helper.RRFScenarioFilter;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.asset.helper.AssetTypeValueComparator;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.scenario.ScenarioType;
import lu.itrust.business.TS.model.scenario.helper.ScenarioManager;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.StandardType;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.helper.Chapter;
import lu.itrust.business.TS.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.impl.MeasureAssetValue;
import lu.itrust.business.TS.model.standard.measure.impl.NormalMeasure;

/**
 * ControllerRRF.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since Oct 16, 2014
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/RRF")
public class ControllerRRF {

	private static final String SCENARIOS = "scenarios";

	private static final String EXTERNAL_THREAT = "externalThreat";

	private static final String INTERNAL_THREAT = "internalThreat";

	private static final String ENVIRONMENTAL = "environmental";

	private static final String ACCIDENTAL = "accidental";

	private static final String INTENTIONAL = "intentional";

	private static final String CORRECTIVE = "corrective";

	private static final String LIMITATIVE = "limitative";

	private static final String DETECTIVE = "detective";

	private static final String PREVENTIVE = "preventive";

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private ServiceMeasure serviceMeasure;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;

	/**
	 * loadRRFMeasure: <br>
	 * Description
	 * 
	 * @param standardID
	 * @param elementID
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Standard/{standardID}/Measure/{elementID}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadRRFMeasure(@PathVariable int standardID, @PathVariable int elementID, Model model, HttpSession session, Principal principal) throws Exception {

		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);

		if (measure.getAnalysisStandard().getStandard().getId() != standardID || measure.getAnalysisStandard().getStandard().getType() == StandardType.MATURITY)
			return null;

		AnalysisType type = serviceAnalysis.getAnalysisTypeById(idAnalysis);

		if (measure instanceof NormalMeasure) {

			NormalMeasure normalMeasure = (NormalMeasure) measure;
			model.addAttribute("strength_measure", normalMeasure.getMeasurePropertyList().getFMeasure());
			model.addAttribute("strength_sectorial", normalMeasure.getMeasurePropertyList().getFSectoral());
			if (AnalysisType.isQualitative(type))
				model.addAttribute("categories", normalMeasure.getMeasurePropertyList().getAllCategories());
			else
				model.addAttribute("categories", normalMeasure.getMeasurePropertyList().getCIACategories());
			model.addAttribute(PREVENTIVE, normalMeasure.getMeasurePropertyList().getPreventive());
			model.addAttribute(DETECTIVE, normalMeasure.getMeasurePropertyList().getDetective());
			model.addAttribute(LIMITATIVE, normalMeasure.getMeasurePropertyList().getLimitative());
			model.addAttribute(CORRECTIVE, normalMeasure.getMeasurePropertyList().getCorrective());
			model.addAttribute(INTENTIONAL, normalMeasure.getMeasurePropertyList().getIntentional());
			model.addAttribute(ACCIDENTAL, normalMeasure.getMeasurePropertyList().getAccidental());
			model.addAttribute(ENVIRONMENTAL, normalMeasure.getMeasurePropertyList().getEnvironmental());
			model.addAttribute(INTERNAL_THREAT, normalMeasure.getMeasurePropertyList().getInternalThreat());
			model.addAttribute(EXTERNAL_THREAT, normalMeasure.getMeasurePropertyList().getExternalThreat());
			List<AssetType> assetTypes = serviceAssetType.getAllFromAnalysis(idAnalysis);
			List<AssetTypeValue> assetTypeValues = normalMeasure.getAssetTypeValues();
			int size = assetTypeValues.size();
			for (AssetType assetType : assetTypes) {
				if (normalMeasure.getAssetTypeValueByAssetType(assetType) == null)
					normalMeasure.addAnAssetTypeValue(new AssetTypeValue(assetType, 0));
			}
			if (assetTypeValues.size() != size)
				serviceMeasure.saveOrUpdate(measure);
			model.addAttribute("assetTypeValues", assetTypeValues);
		} else if (measure instanceof AssetMeasure) {
			AssetMeasure assetMeasure = (AssetMeasure) measure;
			model.addAttribute("standardid", assetMeasure.getAnalysisStandard().getStandard().getId());
			model.addAttribute("measureid", assetMeasure.getId());
			model.addAttribute("strength_measure", assetMeasure.getMeasurePropertyList().getFMeasure());
			model.addAttribute("strength_sectorial", assetMeasure.getMeasurePropertyList().getFSectoral());
			if (AnalysisType.isQualitative(type))
				model.addAttribute("categories", assetMeasure.getMeasurePropertyList().getAllCategories());
			else
				model.addAttribute("categories", assetMeasure.getMeasurePropertyList().getCIACategories());
			model.addAttribute(PREVENTIVE, assetMeasure.getMeasurePropertyList().getPreventive());
			model.addAttribute(DETECTIVE, assetMeasure.getMeasurePropertyList().getDetective());
			model.addAttribute(LIMITATIVE, assetMeasure.getMeasurePropertyList().getLimitative());
			model.addAttribute(CORRECTIVE, assetMeasure.getMeasurePropertyList().getCorrective());
			model.addAttribute(INTENTIONAL, assetMeasure.getMeasurePropertyList().getIntentional());
			model.addAttribute(ACCIDENTAL, assetMeasure.getMeasurePropertyList().getAccidental());
			model.addAttribute(ENVIRONMENTAL, assetMeasure.getMeasurePropertyList().getEnvironmental());
			model.addAttribute(INTERNAL_THREAT, assetMeasure.getMeasurePropertyList().getInternalThreat());
			model.addAttribute(EXTERNAL_THREAT, assetMeasure.getMeasurePropertyList().getExternalThreat());
			model.addAttribute("assets", assetMeasure.getMeasureAssetValues());
		}

		Language language = serviceAnalysis.getLanguageOfAnalysis(idAnalysis);
		model.addAttribute("language", language.getAlpha2());

		return "analyses/single/components/rrf/editor/measure";
	}

	/**
	 * loadRRFScenario: <br>
	 * Description
	 * 
	 * @param elementID
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Scenario/{elementID}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadRRFScenario(@PathVariable int elementID, Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Scenario scenario = serviceScenario.getFromAnalysisById(idAnalysis, elementID);
		scenario.getAssetTypeValues().sort(new AssetTypeValueComparator());
		model.addAttribute("selectedScenario", scenario);
		double typeValue = scenario.getCorrective() + scenario.getDetective() + scenario.getPreventive() + scenario.getLimitative();
		model.addAttribute("typeValue", JSTLFunctions.round(typeValue, 1) == 1 ? true : false);
		return "analyses/single/components/rrf/editor/scenario";
	}

	/**
	 * loadRRFScenarioChart: <br>
	 * Description
	 * 
	 * @param filter
	 * @param elementID
	 * @param model
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Scenario/{elementID}/Chart", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID,'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object loadRRFScenarioChart(@RequestBody RFFMeasureFilter measureFilter, @PathVariable int elementID, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (measureFilter.getIdStandard() < 1 || StringUtils.isEmpty(measureFilter.getChapter()))
			return null;
		Scenario scenario = serviceScenario.getFromAnalysisById(idAnalysis, elementID);
		scenario.getAssetTypeValues().sort(new AssetTypeValueComparator());
		List<AnalysisStandard> standards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);
		List<Measure> measures = new ArrayList<Measure>();
		for (AnalysisStandard standard : standards) {
			if (standard.getStandard().getId() == measureFilter.getIdStandard() && standard.getStandard().getType() != StandardType.MATURITY) {
				if (measureFilter.getIdMeasure() < 1 && standard.getStandard().getType() == StandardType.ASSET)
					return JsonMessage.Error(messageSource.getMessage("error.rrf.standard.standardtype_invalid", null,
							"This standard type permits only to see RRF by single measure (Select a single measure of this standard)", locale));

				for (Measure measure : standard.getMeasures()) {
					if (measureFilter.getIdMeasure() > 0) {
						if (measure.getId() == measureFilter.getIdMeasure()) {
							measures.add(measure);
							break;
						}
					} else if (!measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
							&& measure.getMeasureDescription().getReference().startsWith(measureFilter.getChapter() + ".") && measure.getMeasureDescription().isComputable())
						measures.add(measure);
				}
			}
		}
		return chartGenerator.rrfByScenario(scenario, idAnalysis, measures, locale);
	}

	/**
	 * loadRRFStandardChart: <br>
	 * Description
	 * 
	 * @param filter
	 * @param elementID
	 * @param model
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Measure/{elementID}/Chart", method = RequestMethod.POST, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object loadRRFStandardChart(@RequestBody RRFScenarioFilter scenarioFilter, @PathVariable int elementID, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);

		// retrieve analysis id to compute
		if (scenarioFilter.getIdScenarioType() < 1)
			return null;

		ScenarioType scenariotype = ScenarioType.valueOf(scenarioFilter.getIdScenarioType());

		List<Scenario> scenarios = null;

		if (scenarioFilter.getIdScenario() > 0) {
			scenarios = new ArrayList<Scenario>();
			scenarios.add(serviceScenario.getFromAnalysisById(idAnalysis, scenarioFilter.getIdScenario()));
		} else
			scenarios = serviceScenario.getAllSelectedFromAnalysisByType(idAnalysis, scenariotype);

		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
		return chartGenerator.rrfByMeasure(measure, idAnalysis, scenarios, customLocale != null ? customLocale : locale);
	}

	/**
	 * rrf: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String rrf(Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		List<Measure> measures = serviceMeasure.getAllNotMaturityMeasuresFromAnalysisAndComputable(idAnalysis);
		List<Scenario> scenarios = serviceScenario.getAllSelectedFromAnalysis(idAnalysis);
		Map<Chapter, List<Measure>> splittedmeasures = MeasureManager.SplitByChapter(measures);
		if (!splittedmeasures.isEmpty() && splittedmeasures.entrySet().iterator().next().getValue().get(0) != null) {

			model.addAttribute("measures", splittedmeasures);
			model.addAttribute(SCENARIOS, ScenarioManager.SplitByType(scenarios));
			Measure measure = splittedmeasures.entrySet().iterator().next().getValue().get(0);
			AnalysisType type = serviceAnalysis.getAnalysisTypeById(idAnalysis);
			if (measure instanceof NormalMeasure) {
				NormalMeasure normalMeasure = (NormalMeasure) measure;
				model.addAttribute("standardid", normalMeasure.getAnalysisStandard().getStandard().getId());
				model.addAttribute("measureid", normalMeasure.getId());
				model.addAttribute("strength_measure", normalMeasure.getMeasurePropertyList().getFMeasure());
				model.addAttribute("strength_sectorial", normalMeasure.getMeasurePropertyList().getFSectoral());
				if (AnalysisType.isQualitative(type))
					model.addAttribute("categories", normalMeasure.getMeasurePropertyList().getAllCategories());
				else
					model.addAttribute("categories", normalMeasure.getMeasurePropertyList().getCIACategories());
				model.addAttribute(PREVENTIVE, normalMeasure.getMeasurePropertyList().getPreventive());
				model.addAttribute(DETECTIVE, normalMeasure.getMeasurePropertyList().getDetective());
				model.addAttribute(LIMITATIVE, normalMeasure.getMeasurePropertyList().getLimitative());
				model.addAttribute(CORRECTIVE, normalMeasure.getMeasurePropertyList().getCorrective());
				model.addAttribute(INTENTIONAL, normalMeasure.getMeasurePropertyList().getIntentional());
				model.addAttribute(ACCIDENTAL, normalMeasure.getMeasurePropertyList().getAccidental());
				model.addAttribute(ENVIRONMENTAL, normalMeasure.getMeasurePropertyList().getEnvironmental());
				model.addAttribute(INTERNAL_THREAT, normalMeasure.getMeasurePropertyList().getInternalThreat());
				model.addAttribute(EXTERNAL_THREAT, normalMeasure.getMeasurePropertyList().getExternalThreat());
				List<AssetType> assetTypes = serviceAssetType.getAllFromAnalysis(idAnalysis);
				List<AssetTypeValue> assetTypeValues = normalMeasure.getAssetTypeValues();
				int size = assetTypeValues.size();
				for (AssetType assetType : assetTypes) {
					if (normalMeasure.getAssetTypeValueByAssetType(assetType) == null)
						normalMeasure.addAnAssetTypeValue(new AssetTypeValue(assetType, 0));
				}

				if (assetTypeValues.size() != size)
					serviceMeasure.saveOrUpdate(measure);

				model.addAttribute("assetTypeValues", assetTypeValues);
			}
			if (measure instanceof AssetMeasure) {
				AssetMeasure assetMeasure = (AssetMeasure) measure;
				model.addAttribute("standardid", assetMeasure.getAnalysisStandard().getStandard().getId());
				model.addAttribute("measureid", assetMeasure.getId());
				model.addAttribute("strength_measure", assetMeasure.getMeasurePropertyList().getFMeasure());
				model.addAttribute("strength_sectorial", assetMeasure.getMeasurePropertyList().getFSectoral());
				if (AnalysisType.isQualitative(type))
					model.addAttribute("categories", assetMeasure.getMeasurePropertyList().getAllCategories());
				else
					model.addAttribute("categories", assetMeasure.getMeasurePropertyList().getCIACategories());
				model.addAttribute(PREVENTIVE, assetMeasure.getMeasurePropertyList().getPreventive());
				model.addAttribute(DETECTIVE, assetMeasure.getMeasurePropertyList().getDetective());
				model.addAttribute(LIMITATIVE, assetMeasure.getMeasurePropertyList().getLimitative());
				model.addAttribute(CORRECTIVE, assetMeasure.getMeasurePropertyList().getCorrective());
				model.addAttribute(INTENTIONAL, assetMeasure.getMeasurePropertyList().getIntentional());
				model.addAttribute(ACCIDENTAL, assetMeasure.getMeasurePropertyList().getAccidental());
				model.addAttribute(ENVIRONMENTAL, assetMeasure.getMeasurePropertyList().getEnvironmental());
				model.addAttribute(INTERNAL_THREAT, assetMeasure.getMeasurePropertyList().getInternalThreat());
				model.addAttribute(EXTERNAL_THREAT, assetMeasure.getMeasurePropertyList().getExternalThreat());
				double typeValue = assetMeasure.getMeasurePropertyList().getPreventive() + assetMeasure.getMeasurePropertyList().getDetective()
						+ assetMeasure.getMeasurePropertyList().getLimitative() + assetMeasure.getMeasurePropertyList().getCorrective();
				model.addAttribute("typeValue", JSTLFunctions.round(typeValue, 1) == 1 ? true : false);
				model.addAttribute("assets", assetMeasure.getMeasureAssetValues());
			}

			Language language = serviceAnalysis.getLanguageOfAnalysis(idAnalysis);
			model.addAttribute("language", language.getAlpha3());
			model.addAttribute("notenoughdata", false);
		} else {
			model.addAttribute("notenoughdata", true);
		}

		return "analyses/single/components/rrf/editor/home";
	}

	@RequestMapping(value = "/Measure/{idMeasure}/Update-child", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idMeasure, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String updateChildRRF(@PathVariable int idMeasure, @RequestBody LinkedList<Integer> idMeasureChilds, HttpSession session, Principal principal,
			Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, idMeasure);
		List<Measure> measures = new ArrayList<Measure>(idMeasureChilds.size());
		for (Integer idChild : idMeasureChilds) {
			Measure child = serviceMeasure.getFromAnalysisById(idAnalysis, idChild);
			if (child == null)
				return JsonMessage.Error(messageSource.getMessage("error.action.not_authorise", null, "Action does not authorised", locale));
			else {
				measures.add(measure);
				if (measure instanceof NormalMeasure) {
					NormalMeasure parentMeasure = (NormalMeasure) measure, childMeasure = (NormalMeasure) child;
					parentMeasure.getMeasurePropertyList().copyTo(childMeasure.getMeasurePropertyList());
					Map<AssetType, AssetTypeValue> assetTypeValues = new LinkedHashMap<AssetType, AssetTypeValue>(childMeasure.getAssetTypeValues().size());
					childMeasure.getAssetTypeValues().forEach(assetValue -> assetTypeValues.put(assetValue.getAssetType(), assetValue));
					for (AssetTypeValue assetTypeValue : parentMeasure.getAssetTypeValues()) {
						AssetTypeValue typeValue = assetTypeValues.get(assetTypeValue.getAssetType());
						if (typeValue == null)
							childMeasure.addAnAssetTypeValue(new AssetTypeValue(assetTypeValue.getAssetType(), assetTypeValue.getValue()));
						else
							typeValue.setValue(assetTypeValue.getValue());
					}
				} else if (measure instanceof AssetMeasure) {
					AssetMeasure parentMeasure = (AssetMeasure) measure, childMeasure = (AssetMeasure) child;
					parentMeasure.getMeasurePropertyList().copyTo(childMeasure.getMeasurePropertyList());
					Map<Asset, MeasureAssetValue> measureAssetValues = new LinkedHashMap<Asset, MeasureAssetValue>(childMeasure.getMeasureAssetValues().size());
					childMeasure.getMeasureAssetValues().forEach(assetValue -> measureAssetValues.put(assetValue.getAsset(), assetValue));
					for (MeasureAssetValue assetValue : parentMeasure.getMeasureAssetValues()) {
						MeasureAssetValue measureAssetValue = measureAssetValues.get(assetValue.getAsset());
						if (measureAssetValue == null)
							childMeasure.addAnMeasureAssetValue(new MeasureAssetValue(assetValue.getAsset(), assetValue.getValue()));
						else
							measureAssetValue.setValue(assetValue.getValue());
					}
				} else
					continue;
				serviceMeasure.saveOrUpdate(child);
			}
		}
		return JsonMessage.Success(messageSource.getMessage("success.import_rrf", null, "Measure characteristics has been successfully imported", locale));
	}

}