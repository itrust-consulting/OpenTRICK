package lu.itrust.business.view.controller;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.MeasureProperties;
import lu.itrust.business.TS.NormalMeasure;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.component.MeasureManager;
import lu.itrust.business.component.ScenarioManager;
import lu.itrust.business.component.helper.RRFFieldEditor;
import lu.itrust.business.component.helper.RRFFilter;
import lu.itrust.business.dao.hbm.DAOHibernate;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAssetType;
import lu.itrust.business.service.ServiceMeasure;
import lu.itrust.business.service.ServiceScenario;

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

	@RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String rrf(Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		List<NormalMeasure> normalMeasures = serviceMeasure.getAllNormalMeasuresFromAnalysisAndComputable(idAnalysis);
		List<Scenario> scenarios = serviceScenario.getAllFromAnalysis(idAnalysis);
		model.addAttribute("measures", MeasureManager.SplitByChapter(normalMeasures));
		model.addAttribute("categories", CategoryConverter.JAVAKEYS);
		model.addAttribute("scenarios", ScenarioManager.SplitByType(scenarios));
		model.addAttribute("assetTypes", serviceAssetType.getAll());
		Language language = serviceAnalysis.getLanguageOfAnalysis(idAnalysis);
		model.addAttribute("language", language.getAlpha3());
		return "analyses/singleAnalysis/components/forms/rrfEditor";
	}

	@RequestMapping(value = "/Scenario/Update", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody String updateScenarioRRF(@RequestBody RRFFieldEditor fieldEditor, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
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

	@RequestMapping(value = "/Scenario/{elementID}/Load", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID,'Scenario', #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody String loadScenarioRRF(@RequestBody RRFFilter filter, @PathVariable int elementID, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Scenario scenario = serviceScenario.getFromAnalysisById(idAnalysis, elementID);
		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
		return chartGenerator.rrfByScenario(scenario, idAnalysis, customLocale != null ? customLocale : locale, filter);
	}

	@RequestMapping(value = "/Measure/Update", method = RequestMethod.POST, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody String updateStandardRRF(@RequestBody RRFFieldEditor fieldEditor, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
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
		return chartGenerator.rrfByMeasure(measure, idAnalysis, customLocale != null ? customLocale : locale, fieldEditor.getFilter());
	}

	@RequestMapping(value = "/Measure/{elementID}/Load", method = RequestMethod.POST, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Measure', #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody String loadMeasureRRF(@RequestBody RRFFilter filter, @PathVariable int elementID, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);
		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
		return chartGenerator.rrfByMeasure((NormalMeasure) measure, idAnalysis, customLocale != null ? customLocale : locale, filter);
	}

}
