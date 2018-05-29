package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.OPEN_MODE;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.createHeader;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.createWorkSheetPart;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getRow;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceRiskProfile;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.helper.Column;
import lu.itrust.business.TS.helper.FieldValue;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.helper.chartJS.item.ColorBound;
import lu.itrust.business.TS.helper.chartJS.model.Chart;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.ALE;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.parameter.IImpactParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.AssetStandard;
import lu.itrust.business.TS.model.standard.NormalStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;

/**
 * @author eom
 * 
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/Assessment")
@Controller
public class ControllerAssessment {

	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceRiskProfile serviceRiskProfile;

	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private ServiceMeasure serviceMeasure;

	@Value("${app.settings.excel.default.template.path}")
	private String defaultExcelTemplate;

	@RequestMapping(value = "/Asset/{idAsset}/Load", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idAsset, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadAssetAssessment(@PathVariable int idAsset, @RequestParam(value = "idScenario", defaultValue = "-1") int idScenario, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		Asset asset = analysis.findAsset(idAsset);
		loadAssessmentData(model, locale, analysis);
		if (idScenario < 1) {
			List<Assessment> assessments = analysis.findSelectedAssessmentByAsset(idAsset);
			if (analysis.isQuantitative()) {
				ALE ale = new ALE(asset.getName(), 0);
				ALE aleo = new ALE(asset.getName(), 0);
				ALE alep = new ALE(asset.getName(), 0);
				model.addAttribute("ale", ale);
				model.addAttribute("aleo", aleo);
				model.addAttribute("alep", alep);
				AssessmentAndRiskProfileManager.ComputeALE(assessments, ale, alep, aleo);
			}
			assessments.sort(assessmentScenarioComparator().reversed());
			model.addAttribute("assessments", assessments);
		} else {
			Scenario scenario = analysis.findScenario(idScenario);
			if (scenario != null) {
				Assessment assessment = analysis.findAssessmentByAssetAndScenario(idAsset, idScenario);
				if (assessment != null && assessment.isSelected())
					loadAssessmentFormData(idScenario, idAsset, model, analysis, assessment);
				model.addAttribute("scenario", scenario);
			}
		}

		model.addAttribute("asset", asset);
		loadAnalysisSettings(model, analysis);
		model.addAttribute("isEditable", !OpenMode.isReadOnly((OpenMode) session.getAttribute(OPEN_MODE)));
		return "analyses/single/components/risk-estimation/asset/home";
	}

	@RequestMapping(value = "/Scenario/{idScenario}/Load", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idScenario, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadSceanrioAssessment(@PathVariable int idScenario, @RequestParam(value = "idAsset", defaultValue = "-1") int idAsset, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		Scenario scenario = analysis.findScenario(idScenario);
		loadAssessmentData(model, locale, analysis);
		if (idAsset < 1) {
			List<Assessment> assessments = analysis.findSelectedAssessmentByScenario(idScenario);
			if (analysis.isQuantitative()) {
				ALE ale = new ALE(scenario.getName(), 0);
				ALE aleo = new ALE(scenario.getName(), 0);
				ALE alep = new ALE(scenario.getName(), 0);
				model.addAttribute("ale", ale);
				model.addAttribute("aleo", aleo);
				model.addAttribute("alep", alep);
				AssessmentAndRiskProfileManager.ComputeALE(assessments, ale, alep, aleo);
			}
			assessments.sort(assessmentAssetComparator().reversed());
			model.addAttribute("assessments", assessments);
		} else {
			Asset asset = analysis.findAsset(idAsset);
			if (asset != null) {
				Assessment assessment = analysis.findAssessmentByAssetAndScenario(idAsset, idScenario);
				if (assessment != null && assessment.isSelected())
					loadAssessmentFormData(idScenario, idAsset, model, analysis, assessment);
				model.addAttribute("asset", asset);
			}
		}
		loadAnalysisSettings(model, analysis);
		model.addAttribute("scenario", scenario);
		model.addAttribute("isEditable", !OpenMode.isReadOnly((OpenMode) session.getAttribute(OPEN_MODE)));
		return "analyses/single/components/risk-estimation/scenario/home";

	}

	@RequestMapping(value = "/RiskProfile/Manage-measure", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idScenario, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY) and "
			+ "@permissionEvaluator.userIsAuthorized(#session, #idAsset, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String manageRiskProfileMeasure(@RequestParam(name = "idAsset") Integer idAsset, @RequestParam(name = "idScenario") Integer idScenario, Model model, HttpSession session,
			Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		RiskProfile riskProfile = serviceRiskProfile.getByAssetAndScanrio(idAsset, idScenario);
		if (riskProfile == null)
			return null;
		model.addAttribute("riskProfile", riskProfile);
		model.addAttribute("valueFactory", new ValueFactory(Collections.emptyList()));
		model.addAttribute("standards", serviceAnalysisStandard.findStandardByAnalysisIdAndTypeIn(idAnalysis, NormalStandard.class, AssetStandard.class));
		return "analyses/single/components/risk-estimation/form/measure";
	}

	/**
	 * updateAssessment: <br>
	 * Description
	 * 
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Refresh", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String refreshAssessment(HttpSession session, Locale locale, Principal principal) throws Exception {
		try {
			// retrieve analysis id
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis is not null
			if (integer == null)
				return new String("{\"error\":\"" + messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale) + "\" }");

			// load analysis object
			Analysis analysis = serviceAnalysis.get(integer);

			// check if analysis object is not null
			if (analysis == null)
				return new String("{\"error\":\"" + messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale) + "\" }");
			// update assessments of analysis
			assessmentAndRiskProfileManager.WipeAssessment(analysis);
			assessmentAndRiskProfileManager.updateAssessment(analysis, null);
			// update
			serviceAnalysis.saveOrUpdate(analysis);
			// return success message
			return new String("{\"success\":\"" + messageSource.getMessage("success.assessment.refresh", null, "Assessments were successfully refreshed", locale) + "\"}");
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return new String(
					"{\"error\":\"" + messageSource.getMessage("error.internal.message.assessment.generation", null, "An error occurred during the generation", locale) + "\"}");
		}
	}

	@RequestMapping(value = "/Chart/Risk-evolution-heat-map", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Chart riskEvolutionHeatMapChart(HttpSession session, Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return chartGenerator.generateRiskEvolutionHeatMap(idAnalysis);
	}

	@RequestMapping(value = "/Chart/Risk-heat-map", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Chart riskHeatMapChart(HttpSession session, Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return chartGenerator.generateRiskHeatMap(idAnalysis);
	}

	@RequestMapping(value = "/RiskProfile/Update/Measure", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idScenario, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY) and "
			+ "@permissionEvaluator.userIsAuthorized(#session, #idAsset, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String saveRiskProfileMeasure(@RequestBody List<Integer> measureIds, @RequestParam(name = "idAsset") Integer idAsset,
			@RequestParam(name = "idScenario") Integer idScenario, HttpSession session, Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		RiskProfile riskProfile = serviceRiskProfile.getByAssetAndScanrio(idAsset, idScenario);
		if (riskProfile == null)
			return JsonMessage.Error(messageSource.getMessage("error.risk_profile.not_found", null, "Risk profile cannot be found", locale));
		Map<Integer, Measure> measures = serviceMeasure.getByIdAnalysisAndIds(idAnalysis, measureIds).stream().collect(Collectors.toMap(Measure::getId, Function.identity()));
		riskProfile.getMeasures().removeIf(measure -> !measures.containsKey(measure.getId()));
		riskProfile.getMeasures().forEach(measure -> measures.remove(measure.getId()));
		riskProfile.getMeasures().addAll(measures.values());
		serviceRiskProfile.saveOrUpdate(riskProfile);
		return JsonMessage.Success(messageSource.getMessage("success.save.risk_profile", null, "Risk profile has been successfully save", locale));
	}

	@RequestMapping(value = "/Update/ALE", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String updateAle(HttpSession session, Locale locale, Principal principal) throws Exception {
		try {
			// retrieve analysis id
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// check if analysis is not null
			// load analysis object
			Analysis analysis = serviceAnalysis.get(integer);
			// update assessments of analysis
			AssessmentAndRiskProfileManager.UpdateAssetALE(analysis, null);
			// update
			serviceAnalysis.saveOrUpdate(analysis);
			// return success message
			return new String("{\"success\":\"" + messageSource.getMessage("success.assessment.ale.update", null, "Assessments ale were successfully updated", locale) + "\"}");
		} catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return new String(
					"{\"error\":\"" + messageSource.getMessage("error.internal.assessment.ale.update", null, "Assessment ale update failed: an error occurred", locale) + "\"}");
		}
	}

	/**
	 * updateAssessment: <br>
	 * Description
	 * 
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String updateAssessment(HttpSession session, Locale locale, Principal principal) throws Exception {
		try {
			// retrieve analysis id
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// load analysis object
			Analysis analysis = serviceAnalysis.get(integer);
			// update assessments of analysis
			assessmentAndRiskProfileManager.updateAssessment(analysis, null);
			// update
			serviceAnalysis.saveOrUpdate(analysis);
			// return success message
			return new String("{\"success\":\"" + messageSource.getMessage("success.assessment.update", null, "Assessments were successfully updated", locale) + "\"}");
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return new String(
					"{\"error\":\"" + messageSource.getMessage("error.internal.message.assessment.generation", null, "An error occurred during the generation", locale) + "\"}");
		}
	}

	@GetMapping(value = "/Export", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportEstimationProcess(HttpServletRequest request, HttpServletResponse response, HttpSession session, Locale locale, Principal principal) throws Exception {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Analysis analysis = serviceAnalysis.get(idAnalysis);
		final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.createPackage();
		final WorkbookPart workbook = mlPackage.getWorkbookPart();
		final WorksheetPart worksheetPart = createWorkSheetPart(mlPackage, "Risk estimation");
		final SheetData sheetData = worksheetPart.getContents().getSheetData();
		final boolean hiddenComment = analysis.getSetting(AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT);
		final boolean qualitative = analysis.isHybrid() || analysis.isQualitative();
		final boolean rowColumn = analysis.getSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN);
		final boolean uncertainty = analysis.isUncertainty();
		AssessmentAndRiskProfileManager.UpdateRiskDendencies(analysis, new ValueFactory(analysis.getParameters()));
		final List<ScaleType> scales = analysis.getImpacts();
		final Map<String, RiskProfile> riskProfiles = analysis.getRiskProfiles().stream().collect(Collectors.toMap(RiskProfile::getKey, Function.identity()));
		final String[] columns = createTableHeader(scales, workbook, qualitative, hiddenComment, rowColumn, uncertainty);
		createHeader(worksheetPart, "Risk_estimation", columns, analysis.getAssessments().size());
		analysis.getAssessments().sort((a1, a2) -> {
			int v = NaturalOrderComparator.compareTo(a1.getAsset().getName(), a2.getAsset().getName());
			if (v == 0) {
				v = NaturalOrderComparator.compareTo(a1.getScenario().getName(), a2.getScenario().getName());
				if (v == 0) {
					v = a1.getScenario().getType().compareTo(a2.getScenario().getType());
					if (v == 0)
						v = Double.compare(a2.getALE(), a1.getALE());
				}
			}
			return v;
		});

		int rowIndex = 1;
		for (Assessment assessment : analysis.getAssessments()) {
			int cellIndex = 0;
			Row row = getRow(sheetData, rowIndex++, columns.length);
			RiskProfile profile = riskProfiles.get(RiskProfile.key(assessment.getAsset(), assessment.getScenario()));
			if (qualitative)
				setValue(row, cellIndex++, profile.getIdentifier());
			setValue(row, cellIndex++, assessment.getAsset().getName());
			setValue(row, cellIndex++, assessment.getScenario().getName());
			setValue(row, cellIndex++, assessment.getScenario().getType());
			if (qualitative) {
				if (rowColumn)
					cellIndex += writeProbaImpact(row, cellIndex++, profile.getRawProbaImpact(), scales);
				cellIndex += writeProbaImpact(row, cellIndex++, assessment, scales, analysis.getType());
				cellIndex += writeProbaImpact(row, cellIndex++, profile.getExpProbaImpact(), scales);
			} else {
				setValue(row, cellIndex++, assessment.getLikelihood());
				setValue(row, cellIndex++, assessment.getImpactValue(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME) * 0.001);
			}

			if (uncertainty)
				setValue(row, cellIndex++, assessment.getUncertainty());
			setValue(row, cellIndex++, assessment.getOwner());
			setValue(row, cellIndex++, assessment.getComment());
			if (hiddenComment)
				setValue(row, cellIndex++, assessment.getHiddenComment());
			if (qualitative) {
				setValue(row, cellIndex++, profile.getRiskTreatment());
				Map<String, String> measures = profile.getMeasures().stream().map(Measure::getMeasureDescription)
						.sorted((m1, m2) -> NaturalOrderComparator.compareTo(m1.getReference(), m2.getReference()))
						.collect(Collectors.groupingBy(m -> m.getStandard().getLabel(), Collectors.mapping(MeasureDescription::getReference, Collectors.joining(";"))));
				String value = measures.entrySet().stream().sorted((e1, e2) -> NaturalOrderComparator.compareTo(e1.getKey(), e2.getKey()))
						.map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.joining("\n"));
				setValue(row, cellIndex++, value);
				setValue(row, cellIndex++, profile.getActionPlan());
			}

		}

		response.setContentType("xlsx");
		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\"" + String.format("Risk estimation for %s_v%s.xlsx", analysis.getLabel(), analysis.getVersion()) + "\"");
		mlPackage.save(response.getOutputStream());
		serviceAnalysis.saveOrUpdate(analysis);
		// Log
		TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.risk.estimation",
				String.format("Analysis: %s, version: %s, type: Risk estimation", analysis.getIdentifier(), analysis.getVersion()), principal.getName(), LogAction.EXPORT,
				analysis.getIdentifier(), analysis.getVersion());

	}

	private int writeProbaImpact(Row row, int colIndex, Assessment assessment, List<ScaleType> scales, AnalysisType analysisType) {
		setValue(row, colIndex++, assessment.getLikelihood());
		for (ScaleType type : scales) {
			IValue value = assessment.getImpact(type.getName());
			if (value == null)
				setValue(row, colIndex++, 0);
			else if (type.getName().equals(Constant.DEFAULT_IMPACT_NAME))
				setValue(row, colIndex++, value.getReal() * 0.001);
			else
				setValue(row, colIndex++, value.getLevel());
		}
		return 1 + scales.size();
	}

	private int writeProbaImpact(Row row, int colIndex, RiskProbaImpact probaImpact, List<ScaleType> scales) {
		if (probaImpact == null) {
			for (int i = 0; i <= scales.size(); i++)
				setValue(row, colIndex++, 0);
		} else {
			setValue(row, colIndex++, probaImpact.getProbability() == null ? 0 : probaImpact.getProbability().getAcronym());
			for (ScaleType type : scales) {
				IImpactParameter parameter = probaImpact.get(type.getName());
				if (parameter == null)
					setValue(row, colIndex++, 0);
				else if (type.getName().equals(Constant.DEFAULT_IMPACT_NAME))
					setValue(row, colIndex++, "NA");
				else
					setValue(row, colIndex++, parameter.getLevel());
			}
		}
		return 1 + scales.size();
	}

	private String[] createTableHeader(List<ScaleType> scales, WorkbookPart workbook, boolean qualitative, boolean hiddenComment, boolean rowColumn, boolean uncertainty) {
		List<Column> columns = new ArrayList<>();
		if (qualitative)
			columns.add(new Column("Risk ID"));
		columns.add(new Column("Asset"));
		columns.add(new Column("Scenario"));
		columns.add(new Column("Category"));
		if (qualitative) {
			if (rowColumn) {
				columns.add(new Column("RAW Probability"));
				for (ScaleType type : scales)
					columns.add(new Column("RAW " + type.getDisplayName()));
			}
			columns.add(new Column("Probability"));
			for (ScaleType type : scales)
				columns.add(new Column(type.getDisplayName()));
			columns.add(new Column("EXP Probability"));
			for (ScaleType type : scales)
				columns.add(new Column("EXP " + type.getDisplayName()));
		} else {
			columns.add(new Column("Probability"));
			columns.add(new Column("Impact"));
		}
		if (uncertainty)
			columns.add(new Column("Uncertainty"));
		columns.add(new Column("Owner"));
		columns.add(new Column("Comment"));
		if (hiddenComment)
			columns.add(new Column("Hidden comment"));
		if (qualitative) {
			columns.add(new Column("Security measures"));
			columns.add(new Column("Measures"));
			columns.add(new Column("Action plan"));
		}

		String[] result = new String[columns.size()];
		for (int i = 0; i < columns.size(); i++)
			result[i] = columns.get(i).getName();

		return result;
	}

	@GetMapping(value = "/Import/form", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	private String importEstimationForm() {
		return null;
	}

	@PostMapping(value = "/Import", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	private String importEstimationProcess() {
		return null;
	}

	private Comparator<? super Assessment> assessmentAssetComparator() {
		return (a1, a2) -> {
			int compare = Double.compare(a1.getALE(), a2.getALE());
			if (compare == 0) {
				compare = Double.compare(a1.getAsset().getValue(), a2.getAsset().getValue());
				if (compare == 0) {
					compare = a1.getAsset().getAssetType().getName().compareTo(a2.getAsset().getAssetType().getName());
					if (compare == 0)
						compare = a1.getAsset().getName().compareTo(a2.getAsset().getName());
				}
			}
			return compare;
		};
	}

	private Comparator<? super Assessment> assessmentScenarioComparator() {
		return (a1, a2) -> {
			int compare = Double.compare(a1.getALE(), a2.getALE());
			if (compare == 0) {
				compare = a1.getScenario().getType().getName().compareTo(a2.getScenario().getType().getName());
				if (compare == 0)
					compare = a1.getScenario().getName().compareTo(a2.getScenario().getName());
			}
			return compare;
		};
	}

	private void loadAnalysisSettings(Model model, Analysis analysis) {
		AnalysisSetting rawSetting = AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN, hiddenCommentSetting = AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT;
		model.addAttribute("showHiddenComment", analysis.getSetting(hiddenCommentSetting));
		model.addAttribute("showRawColumn", analysis.getSetting(rawSetting));
		model.addAttribute("showDynamicAnalysis", analysis.getSetting(AnalysisSetting.ALLOW_DYNAMIC_ANALYSIS));
	}

	private void loadAssessmentData(Model model, Locale locale, Analysis analysis) {
		model.addAttribute("valueFactory", new ValueFactory(analysis.getParameters()));
		model.addAttribute("impactTypes", analysis.getImpacts());
		model.addAttribute("type", analysis.getType());
		model.addAttribute("language", locale.getISO3Country());
		model.addAttribute("show_uncertainty", analysis.isUncertainty());
		model.addAttribute("langue", locale.getLanguage().toUpperCase());
	}

	private void loadAssessmentFormData(int idScenario, int idAsset, Model model, Analysis analysis, Assessment assessment) {
		ValueFactory factory = (ValueFactory) model.asMap().get("valueFactory");
		model.addAttribute("impacts", factory.getImpacts());
		model.addAttribute("assessment", assessment);
		model.addAttribute("probabilities", analysis.getLikelihoodParameters());
		model.addAttribute("dynamics", analysis.getDynamicParameters());
		if (analysis.isQualitative()) {

			RiskProfile riskProfile = analysis.findRiskProfileByAssetAndScenario(idAsset, idScenario);
			model.addAttribute("strategies", RiskStrategy.values());
			model.addAttribute("riskProfile", riskProfile);
			List<ColorBound> colorBounds = ChartGenerator.GenerateColorBounds(analysis.getRiskAcceptanceParameters());

			Integer netImportance = factory.findImportance(assessment);
			model.addAttribute("computedNetImportance", colorBounds.stream().filter(v -> v.isAccepted(netImportance))
					.map(v -> new FieldValue("importance", netImportance, v.getLabel(), null, v.getColor())).findAny().orElse(new FieldValue("importance", netImportance)));

			Integer expImportance = riskProfile.getComputedExpImportance();

			model.addAttribute("computedExpImportance", colorBounds.stream().filter(v -> v.isAccepted(expImportance))
					.map(v -> new FieldValue("importance", expImportance, v.getLabel(), null, v.getColor())).findAny().orElse(new FieldValue("importance", expImportance)));

			if (analysis.getSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN).equals(true)) {
				Integer rawImportance = riskProfile.getComputedRawImportance();
				model.addAttribute("computedRawImportance", colorBounds.stream().filter(v -> v.isAccepted(rawImportance))
						.map(v -> new FieldValue("importance", rawImportance, v.getLabel(), null, v.getColor())).findAny().orElse(new FieldValue("importance", rawImportance)));
			}

			if (analysis.isQuantitative())
				model.addAttribute("riskRegister", analysis.findRiskRegisterByAssetAndScenario(idAsset, idScenario));
		}
	}

}