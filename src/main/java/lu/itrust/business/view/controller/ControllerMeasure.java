package lu.itrust.business.view.controller;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.AnalysisStandard;
import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.MeasureProperties;
import lu.itrust.business.TS.NormalMeasure;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.component.helper.RRFFieldEditor;
import lu.itrust.business.component.helper.RRFFilter;
import lu.itrust.business.dao.hbm.DAOHibernate;
import lu.itrust.business.service.ServiceActionPlanSummary;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAnalysisStandard;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasure;
import lu.itrust.business.service.ServiceParameter;
import lu.itrust.business.service.ServiceUser;

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
 * ControllerMeasure.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl. :
 * @version
 * @since Feb 4, 2014
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Measure")
@Controller
public class ControllerMeasure {

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String section(HttpSession session, Model model, Principal principal) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;

		// add measures of the analysis
		model.addAttribute("measures", serviceMeasure.getAllFromAnalysis(idAnalysis));

		// add language of the analysis
		model.addAttribute("language", serviceLanguage.getFromAnalysis(idAnalysis).getAlpha3());

		return "analysis/components/measure";
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param idMeasure
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Measure', #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public @ResponseBody Measure get(@PathVariable int elementID, Model model, HttpSession session, Principal principal) throws Exception {
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
		}
		return measure;
	}

	@RequestMapping(value = "/SingleMeasure/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Measure', #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String getSingleMeasure(@PathVariable int elementID, Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);
		if (measure instanceof NormalMeasure) {
			Hibernate.initialize(((NormalMeasure) measure).getAssetTypeValues());
			Hibernate.initialize(measure);
			for (AssetTypeValue assetTypeValue : ((NormalMeasure) measure).getAssetTypeValues())
				assetTypeValue.setAssetType(DAOHibernate.Initialise(assetTypeValue.getAssetType()));
			((NormalMeasure) measure).setMeasurePropertyList(DAOHibernate.Initialise(((NormalMeasure) measure).getMeasurePropertyList()));
		}
		model.addAttribute("language", serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3());
		model.addAttribute("measure", measure);
		model.addAttribute("standards", measure.getAnalysisStandard().getStandard().getLabel());

		return "analysis/components/singleMeasure";
	}

	@RequestMapping(value = "/RRF/Update", method = RequestMethod.POST, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody String updateRRF(@RequestBody RRFFieldEditor fieldEditor, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
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

	@RequestMapping(value = "/RRF/{elementID}/Load", method = RequestMethod.POST, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Measure', #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody String load(@RequestBody RRFFilter filter, @PathVariable int elementID, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);
		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
		return chartGenerator.rrfByMeasure((NormalMeasure) measure, idAnalysis, customLocale != null ? customLocale : locale, filter);
	}

	/**
	 * sectionStandard: <br>
	 * Description
	 * 
	 * @param standard
	 * @param session
	 * @param model
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String sectionStandard(@PathVariable String standard, HttpSession session, Model model, Principal principal) throws Exception {

		// get analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;

		// add measures of a standard
		model.addAttribute("measures", serviceMeasure.getAllFromAnalysisAndStandard(idAnalysis, standard));

		// add language of analysis
		model.addAttribute("language", serviceLanguage.getFromAnalysis(idAnalysis));

		return "analysis/components/measure";
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
	@RequestMapping(value = "/Compliance/{standard}", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@ResponseBody
	public String compliance(@PathVariable String standard, HttpSession session, Principal principal, Locale locale) {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		try {

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
			// return chart of either standard 27001 or 27002 or null
			return chartGenerator.compliance(idAnalysis, standard, customLocale != null ? customLocale : locale);

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@ResponseBody
	public String compliances(HttpSession session, Principal principal, Locale locale) {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		try {

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));

			List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);

			String value = "{\"standards\":{";

			for (AnalysisStandard analysisStandard : analysisStandards) {

				value += "\"" + analysisStandard.getStandard().getLabel() + "\":[";

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

	@RequestMapping(value = "/SOA", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String getSOA(HttpSession session, Principal principal, Model model) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		model.addAttribute("measures", serviceMeasure.getSOAMeasuresFromAnalysis(idAnalysis));

		return "analysis/components/soa";
	}

}