package lu.itrust.business.view.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.TrickService;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.TS.actionplan.SummaryStandardConformance;
import lu.itrust.business.TS.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.settings.AnalysisSetting;
import lu.itrust.business.TS.settings.AppSettingEntry;
import lu.itrust.business.TS.settings.ApplicationSetting;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.component.GeneralComperator;
import lu.itrust.business.component.MeasureManager;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.exception.TrickException;
import lu.itrust.business.service.ServiceActionPlan;
import lu.itrust.business.service.ServiceActionPlanSummary;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAnalysisNorm;
import lu.itrust.business.service.ServiceAppSettingEntry;
import lu.itrust.business.service.ServiceMeasure;
import lu.itrust.business.service.ServiceParameter;
import lu.itrust.business.service.ServiceScenario;
import lu.itrust.business.service.ServiceTrickService;
import lu.itrust.business.service.ServiceUser;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ControllerPatch.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since May 9, 2014
 */
@Controller
@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
@RequestMapping("/Patch")
public class ControllerPatch {

	@Autowired
	private ServiceTrickService serviceTrickService;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceParameter serviceParameter;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceActionPlanSummary serviceActionPlanSummary;

	@Autowired
	private ServiceMeasure serviceMeasure;

	@Autowired
	private AssessmentManager assessmentManager;

	@Autowired
	private MeasureManager measureManager;

	@Autowired
	private ServiceActionPlan serviceActionPlan;

	@Autowired
	private ServiceAnalysisNorm serviceAnalysisNorm;
	
	@Autowired
	private ServiceUser serviceUser;
	
	@Autowired
	private ServiceAppSettingEntry serviceAppSettingEntry;
	
		
	@RequestMapping(value = "/Update/ScenarioCategoryValue", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody
	String updateAllScenario(Locale locale) {
		try {

			List<Scenario> scenarios = serviceScenario.getAll();
			for (Scenario scenario : scenarios) {
				for (String key : CategoryConverter.JAVAKEYS)
					scenario.setCategoryValue(key, 0);
				scenario.setCategoryValue(CategoryConverter.getTypeFromScenario(scenario), 4);
				serviceScenario.saveOrUpdate(scenario);
			}

			System.out.println("Done...");

			return JsonMessage.Success(messageSource.getMessage("success.scenario.update.all", null, "Scenarios were successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(e.getMessage());
		}
	}

	@RequestMapping(value = "/Update/Measure/AssettypeValue", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody
	String removeMeasureAssetTypeValueDuplicate(Locale locale) throws Exception {
		try {
			measureManager.patchMeasureAssetypeValueDuplicated();
		} catch (TrickException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		}
		return JsonMessage.Success(messageSource.getMessage("success.remove.measure.asset_type_value.duplication", null,
				"The duplications of measure characteristics for the assets were successfully removed", locale));
	}

	@RequestMapping(value = "/Update/Assessments", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody
	Map<String, String> updateAssessments(Locale locale) {

		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {

			List<Analysis> analyses = serviceAnalysis.getAll();

			for (Analysis analysis : analyses) {
				Hibernate.initialize(analysis.getAssets());
				Hibernate.initialize(analysis.getScenarios());
				Hibernate.initialize(analysis.getAssessments());
				Hibernate.initialize(analysis.getParameters());
				assessmentManager.UpdateAssessment(analysis);
			}

			System.out.println("Done...");

			errors.put("success", messageSource.getMessage("success.assessments.update.all", null, "All assessments were successfully updated", locale));

			return errors;
		} catch (Exception e) {
			errors.put("error", e.getMessage());
			e.printStackTrace();
			return errors;
		}
	}

	@RequestMapping(value = "/Update/ParameterImplementationScale", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody
	Map<String, String> updateImplementationScaleNames(Locale locale) {

		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {
			List<Parameter> parameters = serviceParameter.getAll();
			for (Parameter parameter : parameters) {
				if (!parameter.getDescription().startsWith("ImpScale"))
					continue;
				else {
					Integer line = Integer.valueOf(parameter.getDescription().substring(8));
					String desc = "";
					switch (line) {
						case 1:
							desc = Constant.IS_NOT_ACHIEVED;
							break;
						case 2:
							desc = Constant.IS_RUDIMENTARY_ACHIEVED;
							break;
						case 3:
							desc = Constant.IS_PARTIALLY_ACHIEVED;
							break;
						case 4:
							desc = Constant.IS_LARGELY_ACHIEVED;
							break;
						case 5:
							desc = Constant.IS_FULLY_ACHIEVED;
							break;
						default:
							desc = "ImpScale" + String.valueOf(line);
							break;
					}

					parameter.setDescription(desc);
					serviceParameter.saveOrUpdate(parameter);
				}
			}

			System.out.println("Done...");

			errors.put("success", messageSource.getMessage("success.impscale.update.all", null, "Implmentation Scale Parameter descriptions were successfully updated", locale));

			return errors;
		} catch (Exception e) {
			errors.put("error", e.getMessage());
			e.printStackTrace();
			return errors;
		}
	}

	@RequestMapping(value = "/Update/UpdateCompliances", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody
	String updateCompliances(Locale locale) {

		String patchversion = "0.6.3b";

		try {

			System.out.println("Patching compliance (version 0.6.3b)");

			if(serviceTrickService.getStatus().getVersion().equals(patchversion)) {
				System.out.println("Patch already installed!");
				return JsonMessage.Error(messageSource.getMessage("error.patch.installed", null, "The Patch is already installed!", locale));
			}
			
			List<Integer> analyses = serviceAnalysis.getAllAnalysisIDs();

			for (Integer idAnalysis : analyses) {

				System.out.println("analysis " + (analyses.indexOf(idAnalysis) + 1) + " of " + analyses.size());

				List<AnalysisNorm> norms = serviceAnalysisNorm.getAllFromAnalysis(idAnalysis);

				for (AnalysisNorm norm : norms) {

					List<SummaryStage> summaries = serviceActionPlanSummary.getAllFromAnalysis(idAnalysis);
					
					for (SummaryStage summary : summaries) {
						
						if(norm.getNorm().getLabel().equals(Constant.NORM_27001)) {
							
							boolean found = false;
							
							for(SummaryStandardConformance conformance : summary.getConformances())
								if(conformance.getAnalysisNorm().getNorm().getLabel().equals(norm.getNorm().getLabel())) {
									found = true;
									break;
								}
							
							if(!found)						
								summary.addConformance(norm, summary.getConformance27001());
						} else if(norm.getNorm().getLabel().equals(Constant.NORM_27002)) {
							
							boolean found = false;
							
							for(SummaryStandardConformance conformance : summary.getConformances())
								if(conformance.getAnalysisNorm().getNorm().getLabel().equals(norm.getNorm().getLabel())) {
									found = true;
									break;
								}
							
							if(!found)						
								summary.addConformance(norm, summary.getConformance27002());
						} else {
							
							boolean found = false;
							
							for(SummaryStandardConformance conformance : summary.getConformances())
								if(conformance.getAnalysisNorm().getNorm().getLabel().equals(norm.getNorm().getLabel())) {
									found = true;
									break;
								}
							
							if(!found) {
							
								Map<String, Object[]> previouscompliances = ChartGenerator.ComputeComplianceBefore(norm.getMeasures());
								
								int compliancevalue = 0;
								
								for (String key : previouscompliances.keySet()) {
									Object[] compliance = previouscompliances.get(key);
									compliancevalue += (int) Math.floor(((Double) compliance[1]) / (Integer) compliance[0]);
								}
								
								compliancevalue = compliancevalue / previouscompliances.size();
								
								summary.addConformance(norm, compliancevalue*0.01);
								
							}
								
						}
							
						
					}
				
					for (SummaryStage summary : summaries)
						serviceActionPlanSummary.saveOrUpdate(summary);
				}
			}

			TrickService ts = serviceTrickService.getStatus();
			if (GeneralComperator.VersionComparator(ts.getVersion(), patchversion) == -1) {

				ts.setVersion(patchversion);
				serviceTrickService.saveOrUpdate(ts);

			}
			System.out.println("Done...");

			return JsonMessage.Success(messageSource.getMessage("success.maintenance.update.all", null, "Measures were successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/Update/ApplicationSettings", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody String createDefaultSettingsForUsers(Locale locale){
		
		System.out.println("Patching compliance (version 0.6.3)");

		String patchversion = "0.6.3d";

		try{
		
		if(serviceTrickService.getStatus().getVersion().equals(patchversion)) {
			System.out.println("Patch already installed!");
			return JsonMessage.Error(messageSource.getMessage("error.patch.installed", null, "The Patch is already installed!", locale));
		}
		
		List<User> users = serviceUser.getAll();
		
		for(User user: users) {

			if(!user.applicationSettingExists(Constant.SETTING_DEFAULT_UI_LANGUAGE)){
				ApplicationSetting setting = new ApplicationSetting(Constant.SETTING_DEFAULT_UI_LANGUAGE,"en");
				user.addApplicationSetting(setting);
			}
			
			if(!user.applicationSettingExists(Constant.SETTING_DEFAULT_SHOW_UNCERTAINTY)){
				ApplicationSetting setting = new ApplicationSetting(Constant.SETTING_DEFAULT_SHOW_UNCERTAINTY,"true");
				user.addApplicationSetting(setting);
			}
				
			if(!user.applicationSettingExists(Constant.SETTING_DEFAULT_SHOW_CSSF)){
				ApplicationSetting setting = new ApplicationSetting(Constant.SETTING_DEFAULT_SHOW_CSSF,"false");
				user.addApplicationSetting(setting);
			}
			
			List<Analysis> analyses = serviceAnalysis.getAllFromUser(user);
			
			for(Analysis analysis : analyses){
				
				AppSettingEntry settings = serviceAppSettingEntry.getByUsernameAndGroupAndName(user.getLogin(), "analysis", String.valueOf(analysis.getId()));
				
				String uncertainty = settings.findByKey("show_uncertainty");
				
				String cssf = settings.findByKey("show_cssf");
				
				String defaultuncertainty = "true";
				
				String defaultcssf = "false";
				
				if(uncertainty==null){
					if(!analysis.analysisSettingExists(Constant.SETTING_SHOW_UNCERTAINTY)){
						AnalysisSetting analysisSetting = new AnalysisSetting(Constant.SETTING_SHOW_UNCERTAINTY,defaultuncertainty, user);
						analysis.addAnalysisSetting(analysisSetting);
					}
				}
				
				if(cssf==null){
					if(!analysis.analysisSettingExists(Constant.SETTING_SHOW_CSSF)){
						AnalysisSetting analysisSetting = new AnalysisSetting(Constant.SETTING_SHOW_CSSF,defaultcssf, user);
						analysis.addAnalysisSetting(analysisSetting);
					}
				}
				
				AnalysisSetting analysisSetting = new AnalysisSetting(Constant.SETTING_LANGUAGE,analysis.getLanguage().getAlpha3(), user);
				analysis.addAnalysisSetting(analysisSetting);
				
				serviceAnalysis.saveOrUpdate(analysis);
				
			}
			
			serviceUser.saveOrUpdate(user);
			
		}
		
		TrickService ts = serviceTrickService.getStatus();
		if (GeneralComperator.VersionComparator(ts.getVersion(), patchversion) == -1) {

			ts.setVersion(patchversion);
			serviceTrickService.saveOrUpdate(ts);

		}
		System.out.println("Done...");
		return JsonMessage.Success(messageSource.getMessage("success.maintenance.update.all", null, "Measures were successfully updated", locale));
	} catch (Exception e) {
		e.printStackTrace();
		return JsonMessage.Error(e.getMessage());
	}
		
	}
	

}