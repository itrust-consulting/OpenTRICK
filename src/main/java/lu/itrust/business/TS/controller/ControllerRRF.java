package lu.itrust.business.TS.controller;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.data.general.AssetTypeValue;
import lu.itrust.business.TS.data.general.Language;
import lu.itrust.business.TS.data.rrf.RRFFieldEditor;
import lu.itrust.business.TS.data.rrf.RRFFilter;
import lu.itrust.business.TS.data.scenario.Scenario;
import lu.itrust.business.TS.data.scenario.ScenarioType;
import lu.itrust.business.TS.data.scenario.helper.ScenarioManager;
import lu.itrust.business.TS.data.standard.measure.AssetMeasure;
import lu.itrust.business.TS.data.standard.measure.Measure;
import lu.itrust.business.TS.data.standard.measure.MeasureProperties;
import lu.itrust.business.TS.data.standard.measure.NormalMeasure;
import lu.itrust.business.TS.data.standard.measure.helper.Chapter;
import lu.itrust.business.TS.data.standard.measure.helper.MeasureManager;
import lu.itrust.business.TS.database.dao.hbm.DAOHibernate;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceScenario;

import org.hibernate.Hibernate;
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
	@RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).READ)")
	public String rrf(Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		List<Measure> measures = serviceMeasure.getAllNotMaturityMeasuresFromAnalysisAndComputable(idAnalysis);
		List<Scenario> scenarios = serviceScenario.getAllFromAnalysis(idAnalysis);
		Map<Chapter, List<Measure>> splittedmeasures = MeasureManager.SplitByChapter(measures);
		model.addAttribute("measures", splittedmeasures);
		model.addAttribute("scenarios", ScenarioManager.SplitByType(scenarios));
		Measure measure = null;
		measure = splittedmeasures.entrySet().iterator().next().getValue().get(0);
		if(measure != null) {
			if(measure instanceof NormalMeasure) {
				NormalMeasure normalMeasure = (NormalMeasure) measure;
				model.addAttribute("standardid", normalMeasure.getAnalysisStandard().getStandard().getId());
				model.addAttribute("measureid", normalMeasure.getId());
				model.addAttribute("strength_measure", normalMeasure.getMeasurePropertyList().getFMeasure());
				model.addAttribute("strength_sectorial", normalMeasure.getMeasurePropertyList().getFSectoral());
				if (serviceAnalysis.isAnalysisCssf(idAnalysis)) {
					model.addAttribute("categories", normalMeasure.getMeasurePropertyList().getCSSFCategories());
				} else {
					model.addAttribute("categories", normalMeasure.getMeasurePropertyList().getCIACategories());
				}
				model.addAttribute("preventive", normalMeasure.getMeasurePropertyList().getPreventive());
				model.addAttribute("detective", normalMeasure.getMeasurePropertyList().getDetective());
				model.addAttribute("limitative", normalMeasure.getMeasurePropertyList().getLimitative());
				model.addAttribute("corrective", normalMeasure.getMeasurePropertyList().getCorrective());
				model.addAttribute("intentional", normalMeasure.getMeasurePropertyList().getIntentional());
				model.addAttribute("accidental", normalMeasure.getMeasurePropertyList().getAccidental());
				model.addAttribute("environmental", normalMeasure.getMeasurePropertyList().getEnvironmental());
				model.addAttribute("internalThreat", normalMeasure.getMeasurePropertyList().getInternalThreat());
				model.addAttribute("externalThreat", normalMeasure.getMeasurePropertyList().getExternalThreat());
				model.addAttribute("assetTypes", normalMeasure.getAssetTypeValues());
			}
			if(measure instanceof AssetMeasure) {
				AssetMeasure assetMeasure = (AssetMeasure) measure;
			model.addAttribute("strength_measure", assetMeasure.getMeasurePropertyList().getFMeasure());
			model.addAttribute("strength_sectorial", assetMeasure.getMeasurePropertyList().getFSectoral());
			if (serviceAnalysis.isAnalysisCssf(idAnalysis)) {
				model.addAttribute("categories", assetMeasure.getMeasurePropertyList().getCSSFCategories());
			} else {
				model.addAttribute("categories", assetMeasure.getMeasurePropertyList().getCIACategories());
			}
			model.addAttribute("preventive", assetMeasure.getMeasurePropertyList().getPreventive());
			model.addAttribute("detective", assetMeasure.getMeasurePropertyList().getDetective());
			model.addAttribute("limitative", assetMeasure.getMeasurePropertyList().getLimitative());
			model.addAttribute("corrective", assetMeasure.getMeasurePropertyList().getCorrective());
			model.addAttribute("intentional", assetMeasure.getMeasurePropertyList().getIntentional());
			model.addAttribute("accidental", assetMeasure.getMeasurePropertyList().getAccidental());
			model.addAttribute("environemental", assetMeasure.getMeasurePropertyList().getEnvironmental());
			model.addAttribute("internalThreat", assetMeasure.getMeasurePropertyList().getInternalThreat());
			model.addAttribute("externalThreat", assetMeasure.getMeasurePropertyList().getExternalThreat());
				model.addAttribute("assets", assetMeasure.getMeasureAssetValues());
			}
		}
		
		Language language = serviceAnalysis.getLanguageOfAnalysis(idAnalysis);
		model.addAttribute("language", language.getAlpha3());
		return "analyses/singleAnalysis/components/forms/rrf/rrfEditor";
	}

	/***********************
	 * Scenarios
	 **********************/

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
	@RequestMapping(value = "/Scenario/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public String loadRRFScenario(@PathVariable int elementID, Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Scenario scenario = DAOHibernate.Initialise(serviceScenario.getFromAnalysisById(idAnalysis, elementID));
		scenario.setScenarioType(DAOHibernate.Initialise(scenario.getScenarioType()));
		for (AssetTypeValue assetTypeValue : scenario.getAssetTypeValues())
			assetTypeValue.setAssetType(DAOHibernate.Initialise(assetTypeValue.getAssetType()));
		model.addAttribute("selectedScenario", scenario);
		model.addAttribute("assetTypes", serviceAssetType.getAll());
		return "analyses/singleAnalysis/components/forms/rrf/scenarioRRF";
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
	@RequestMapping(value = "/Scenario/{elementID}/Chart", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID,'Scenario', #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String loadRRFScenarioChart(@RequestBody RRFFilter filter, @PathVariable int elementID, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Scenario scenario = serviceScenario.getFromAnalysisById(idAnalysis, elementID);
		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
		return chartGenerator.rrfByScenario(scenario, idAnalysis, customLocale != null ? customLocale : locale, filter);
	}

	/**
	 * updateRRFScenarioChart: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param model
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Scenario/Chart/Update", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String updateRRFScenarioChart(@RequestBody RRFFieldEditor fieldEditor, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Scenario scenario = serviceScenario.getFromAnalysisById(idAnalysis, fieldEditor.getId());
		Field field = ControllerEditField.FindField(Scenario.class, fieldEditor.getFieldName());
		if (field == null) {
			AssetTypeValue assetData = null;
			for (AssetTypeValue assetTypeValue : scenario.getAssetTypeValues()) {
				if (fieldEditor.getFieldName().equals(assetTypeValue.getAssetType().getType())) {
					assetData = assetTypeValue;
					break;
				}
			}
			if (assetData != null)
				assetData.setValue((Integer) fieldEditor.getValue());
			else
				return null;
		} else {
			field.setAccessible(true);
			field.set(scenario, fieldEditor.getValue());
		}
		serviceScenario.saveOrUpdate(scenario);

		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));

		return chartGenerator.rrfByScenario(scenario, idAnalysis, customLocale != null ? customLocale : locale, fieldEditor.getFilter());
	}

	/***************
	 * Measures
	 ***************/

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
	@RequestMapping(value = "/{standardID}/Measure/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Measure', #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Measure loadRRFMeasure(@PathVariable int standardID, @PathVariable int elementID, Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);
		measure.setAnalysisStandard(null);
		measure.setMeasureDescription(null);
		if (measure instanceof NormalMeasure) {
			((NormalMeasure) measure).setPhase(null);
			Hibernate.initialize(((NormalMeasure) measure).getAssetTypeValues());
			Hibernate.initialize(measure);
			for (AssetTypeValue assetTypeValue : ((NormalMeasure) measure).getAssetTypeValues())
				assetTypeValue.setAssetType(DAOHibernate.Initialise(assetTypeValue.getAssetType()));
			((NormalMeasure) measure).setMeasurePropertyList(DAOHibernate.Initialise(((NormalMeasure) measure).getMeasurePropertyList()));
		} else if (measure instanceof AssetMeasure) {
			((AssetMeasure) measure).setPhase(null);
			Hibernate.initialize(measure);
			((AssetMeasure) measure).setMeasurePropertyList(DAOHibernate.Initialise(((AssetMeasure) measure).getMeasurePropertyList()));
			Hibernate.initialize(((AssetMeasure) measure).getMeasureAssetValues());
		}
		return measure;
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
	@RequestMapping(value = "/Measure/{elementID}/Chart", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Measure', #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody String loadRRFStandardChart(@PathVariable int elementID, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);
		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
		return chartGenerator.rrfByMeasure(measure, idAnalysis, customLocale != null ? customLocale : locale);
	}

	/**
	 * updateRRFStandardChart: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param model
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Measure/Chart/Update", method = RequestMethod.POST, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String updateRRFStandardChart(@RequestBody RRFFieldEditor fieldEditor, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		NormalMeasure measure = (NormalMeasure) serviceMeasure.getFromAnalysisById(idAnalysis, fieldEditor.getId());
		Field field = ControllerEditField.FindField(MeasureProperties.class, fieldEditor.getFieldName());
		if (field == null) {
			if (MeasureProperties.isCategoryKey(fieldEditor.getFieldName()))
				measure.getMeasurePropertyList().setCategoryValue(fieldEditor.getFieldName(), (Integer) fieldEditor.getValue());
			else {
				AssetTypeValue assetData = null;
				for (AssetTypeValue assetTypeValue : measure.getAssetTypeValues()) {
					if (fieldEditor.getFieldName().equals(assetTypeValue.getAssetType().getType())) {
						assetData = assetTypeValue;
						break;
					}
				}
				if (assetData != null)
					assetData.setValue((Integer) fieldEditor.getValue());
				else
					return null;
			}
		} else {
			field.setAccessible(true);
			MeasureProperties properties = DAOHibernate.Initialise(measure.getMeasurePropertyList());
			field.set(properties, fieldEditor.getValue());
			measure.setMeasurePropertyList(properties);
		}
		serviceMeasure.saveOrUpdate(measure);
		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
		return chartGenerator.rrfByMeasure(measure, idAnalysis, customLocale != null ? customLocale : locale);
	}

}
