package lu.itrust.business.view.controller;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.MeasureProperties;
import lu.itrust.business.TS.NormMeasure;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.component.helper.RRFFieldEditor;
import lu.itrust.business.component.helper.RRFFilter;
import lu.itrust.business.dao.hbm.DAOHibernate;
import lu.itrust.business.service.ServiceActionPlanSummary;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasure;
import lu.itrust.business.service.ServiceParameter;

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
	@RequestMapping(value = "/{idMeasure}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
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

	@RequestMapping(value = "/SingleMeasure/{idMeasure}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String getSingleMeasure(@PathVariable int idMeasure, Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Measure measure = serviceMeasure.findByIdAndAnalysis(idMeasure, idAnalysis);
		if (measure instanceof NormMeasure) {
			Hibernate.initialize(((NormMeasure) measure).getAssetTypeValues());
			Hibernate.initialize(measure);
			for (AssetTypeValue assetTypeValue : ((NormMeasure) measure).getAssetTypeValues())
				assetTypeValue.setAssetType(DAOHibernate.Initialise(assetTypeValue.getAssetType()));
			((NormMeasure) measure).setMeasurePropertyList(DAOHibernate.Initialise(((NormMeasure) measure).getMeasurePropertyList()));
		}

		model.addAttribute("measure", measure);
		model.addAttribute("norm", measure.getAnalysisNorm().getNorm().getLabel());

		return "analysis/components/singleMeasure";
	}

	@RequestMapping(value = "/RRF/Update", method = RequestMethod.POST, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String updateRRF(@RequestBody RRFFieldEditor fieldEditor, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		NormMeasure measure = (NormMeasure) serviceMeasure.findByIdAndAnalysis(fieldEditor.getId(), idAnalysis);
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
		return chartGenerator.rrfByMeasure(measure, idAnalysis, locale, fieldEditor.getFilter());
	}

	@RequestMapping(value = "/RRF/{idMeasure}/Load", method = RequestMethod.POST, headers = "Accept=application/json; charset=UTF-8")
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
	@RequestMapping(value = "/Compliance/{norm}", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@ResponseBody
	public String compliance(@PathVariable String norm, HttpSession session, Locale locale, Principal principal) {

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

	@RequestMapping(value = "/SOA", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String getSOA(HttpSession session, Principal principal, Model model) {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		model.addAttribute("measures", serviceMeasure.loadSOA(idAnalysis));

		return "analysis/components/soa";
	}

	@RequestMapping(value = "/Update/Maintenance", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody
	String updateMaintenance(Locale locale) {

		try {

			System.out.println("Load all analyses...");

			List<Analysis> analyses = serviceAnalysis.loadAll();
			for (Analysis analysis : analyses) {
				System.out.println("Update maintenance...");
				if (analysis == null)
					return JsonMessage.Error(messageSource.getMessage("error.maintenance.update.all", null, "Analysis not found!", locale));
				for (AnalysisNorm norm : analysis.getAnalysisNorms()) {
					for (Measure measure : norm.getMeasures()) {

						double maintenance = measure.getMaintenance();

						double internalmaintenance = 0;

						double externalmaintenance = 0;

						double recurrentInvestment = 0;

						double investment = 0;

						investment = measure.getInvestment();

						internalmaintenance = (measure.getInternalWL() * (maintenance / 100.));

						externalmaintenance = (measure.getExternalWL() * (maintenance / 100.));

						recurrentInvestment = (investment * (maintenance / 100.));

						measure.setInternalMaintenance(internalmaintenance);

						measure.setExternalMaintenance(externalmaintenance);

						measure.setRecurrentInvestment(recurrentInvestment);

						Measure.ComputeCost(measure, analysis);

						serviceMeasure.saveOrUpdate(measure);

					}
				}

				System.out.println("set new recurrent investment field in action plan summary...");

				for (SummaryStage summaryStage : analysis.getSummaries()) {
					summaryStage.setRecurrentInvestment(0);
					serviceActionPlanSummary.saveOrUpdate(summaryStage);
				}

				System.out.println("remove default maintenance param...");

				Parameter maintenanceDefaultParam = null;

				for (Parameter parameter : analysis.getParameters()) {
					if (parameter.getDescription().equals(Constant.PARAMETER_MAINTENANCE_DEFAULT)) {
						maintenanceDefaultParam = parameter;
						break;
					}
				}

				if (maintenanceDefaultParam != null) {
					analysis.getParameters().remove(maintenanceDefaultParam);
					serviceParameter.delete(maintenanceDefaultParam);

				}

				serviceAnalysis.saveOrUpdate(analysis);

			}

			System.out.println("Done...");

			return JsonMessage.Success(messageSource.getMessage("success.maintenance.update.all", null, "Measures were successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(e.getMessage());
		}
	}
}