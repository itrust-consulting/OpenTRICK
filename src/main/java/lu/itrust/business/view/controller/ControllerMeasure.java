package lu.itrust.business.view.controller;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.MeasureProperties;
import lu.itrust.business.TS.NormMeasure;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.component.helper.RRFFieldEditor;
import lu.itrust.business.component.helper.RRFFilter;
import lu.itrust.business.dao.hbm.DAOHibernate;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasure;

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

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param session
	 * @param model
	 * @param principal
	 * @return
	 */
	@RequestMapping("/Section")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String section(HttpSession session, Model model, Principal principal) {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;

		// add measures of the analysis
		model.addAttribute("measures", serviceMeasure.findByAnalysis(idAnalysis));

		// add language of the analysis
		model.addAttribute("language", serviceLanguage.findByAnalysis(idAnalysis).getAlpha3());

		return "analysis/components/measure";
	}

	@RequestMapping(value = "/{idMeasure}", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	Measure get(@PathVariable int idMeasure, Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Measure measure = serviceMeasure.findByIdAndAnalysis(idMeasure, idAnalysis);
		measure.setAnalysisNorm(null);
		measure.setMeasureDescription(null);
		if (measure instanceof NormMeasure) {
			((NormMeasure) measure).setPhase(null);
			Hibernate.initialize(((NormMeasure) measure).getAssetTypeValues());
			Hibernate.initialize(measure);
			for (AssetTypeValue assetTypeValue : ((NormMeasure) measure).getAssetTypeValues())
				assetTypeValue.setAssetType(DAOHibernate.Initialise(assetTypeValue.getAssetType()));
			((NormMeasure) measure).setMeasurePropertyList(DAOHibernate.Initialise(((NormMeasure) measure).getMeasurePropertyList()));
		}
		return measure;
	}

	@RequestMapping(value = "/RRF/Update", method = RequestMethod.POST, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String updateRRF(@RequestBody RRFFieldEditor fieldEditor, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		NormMeasure measure = (NormMeasure) serviceMeasure.findByIdAndAnalysis(fieldEditor.getId(), idAnalysis);
		Field field = ControllerEditField.FindField(MeasureProperties.class, fieldEditor.getFieldName());
		if (field == null) {
			if (MeasureProperties.isCategoryKey(fieldEditor.getFieldName()))
				measure.getMeasurePropertyList().setCategoryValue(fieldEditor.getFieldName(), (Integer)fieldEditor.getValue());
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
		return chartGenerator.rrfByMeasure(measure, idAnalysis, locale, fieldEditor.getFilter());
	}

	@RequestMapping(value = "/RRF/{idMeasure}/Load", method = RequestMethod.POST, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String load(@RequestBody RRFFilter filter, @PathVariable int idMeasure, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Measure measure = serviceMeasure.findByIdAndAnalysis(idMeasure, idAnalysis);
		return chartGenerator.rrfByMeasure((NormMeasure) measure, idAnalysis, locale, filter);
	}

	/**
	 * sectionNorm: <br>
	 * Description
	 * 
	 * @param norm
	 * @param session
	 * @param model
	 * @param attributes
	 * @return
	 */
	@RequestMapping("/Section/{norm}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String sectionNorm(@PathVariable String norm, HttpSession session, Model model, Principal principal) {

		// get analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;

		// add measures of a norm
		model.addAttribute("measures", serviceMeasure.findByAnalysisAndNorm(idAnalysis, norm));

		// add language of analysis
		model.addAttribute("language", serviceLanguage.findByAnalysis(idAnalysis));

		return "analysis/components/measure";
	}

	/**
	 * compliance: <br>
	 * Description
	 * 
	 * @param norm
	 * @param session
	 * @param locale
	 * @return
	 */
	@RequestMapping("/Compliance/{norm}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@ResponseBody
	String compliance(@PathVariable String norm, HttpSession session, Locale locale, Principal principal) {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		try {

			// return chart of either norm 27001 or 27002 or null
			return (norm.equals(Constant.NORM_27001) || norm.equals(Constant.NORM_27002)) ? chartGenerator.compliance(idAnalysis, norm, locale) : null;

		} catch (Exception e) {

			// retrun error
			e.printStackTrace();
			return null;
		}
	}
}