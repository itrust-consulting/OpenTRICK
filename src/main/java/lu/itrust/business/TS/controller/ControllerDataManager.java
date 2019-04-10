package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.RI_SHEET_MAPPERS;
import static lu.itrust.business.TS.constants.Constant.ROLE_MIN_USER;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.createHeader;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.createRow;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.createWorkSheetPart;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.findTable;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getRow;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.setFormula;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.SpreadsheetML.TablePart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.sml.CTTable;
import org.xlsx4j.sml.ObjectFactory;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerAnalysisImport;
import lu.itrust.business.TS.asynchronousWorkers.WorkerExportAnalysis;
import lu.itrust.business.TS.asynchronousWorkers.WorkerExportRiskRegister;
import lu.itrust.business.TS.asynchronousWorkers.WorkerExportRiskSheet;
import lu.itrust.business.TS.asynchronousWorkers.WorkerExportWordReport;
import lu.itrust.business.TS.asynchronousWorkers.WorkerImportEstimation;
import lu.itrust.business.TS.asynchronousWorkers.WorkerImportMeasureData;
import lu.itrust.business.TS.asynchronousWorkers.WorkerImportRiskInformation;
import lu.itrust.business.TS.asynchronousWorkers.WorkerSOAExport;
import lu.itrust.business.TS.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.component.DefaultReportTemplateLoader;
import lu.itrust.business.TS.component.MeasureManager;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.controller.form.CSSFExportForm;
import lu.itrust.business.TS.controller.form.DataManagerItem;
import lu.itrust.business.TS.controller.form.ExportWordReportForm;
import lu.itrust.business.TS.controller.form.ImportAnalysisForm;
import lu.itrust.business.TS.controller.form.ImportRRFForm;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceImpactParameter;
import lu.itrust.business.TS.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.TS.database.service.ServiceReportTemplate;
import lu.itrust.business.TS.database.service.ServiceRiskAcceptanceParameter;
import lu.itrust.business.TS.database.service.ServiceScaleType;
import lu.itrust.business.TS.database.service.ServiceSimpleParameter;
import lu.itrust.business.TS.database.service.ServiceStandard;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.word.ExportReportData;
import lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.CellRef;
import lu.itrust.business.TS.helper.Column;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.helper.RRFExportImport;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.analysis.helper.AnalysisComparator;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.cssf.helper.CSSFFilter;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.document.impl.ReportTemplate;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.IImpactParameter;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.IProbabilityParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.riskinformation.helper.RiskInformationComparator;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.scenario.ScenarioType;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.MaturityStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;

@PreAuthorize(ROLE_MIN_USER)
@Controller
@RequestMapping("/Analysis/Data-manager")
public class ControllerDataManager {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	@Autowired
	private MeasureManager measureManager;

	@Autowired
	private ServiceStandard serviceStandard;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	@Autowired
	private TaskExecutor executor;

	@Autowired
	private ServiceSimpleParameter serviceSimpleParameter;

	@Autowired
	private ServiceImpactParameter serviceImpactParameter;

	@Autowired
	private ServiceLikelihoodParameter serviceLikelihoodParameter;

	@Autowired
	private ServiceAssessment serviceAssessment;

	@Autowired
	private ServiceScaleType serviceScaleType;

	@Autowired
	private ServiceRiskAcceptanceParameter serviceRiskAcceptanceParameter;

	@Autowired
	private DefaultReportTemplateLoader defaultReportTemplateLoader;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceReportTemplate serviceReportTemplate;

	@Value("${app.settings.report.refurbish.max.size}")
	private long maxRefurbishReportSize;

	@Value("${app.settings.upload.file.max.size}")
	private Long maxUploadFileSize;

	@Value("${app.settings.risk.information.template.path}")
	private String brainstormingTemplate;

	@RequestMapping(value = "/Action-plan-raw/Export-process", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportActionPlanRawProcess(HttpServletRequest request, HttpServletResponse response, HttpSession session, Principal principal, Locale locale) throws Exception {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final SpreadsheetMLPackage spreadsheetMLPackage = SpreadsheetMLPackage.createPackage();
		exportRawActionPlan(analysis, spreadsheetMLPackage, new Locale(analysis.getLanguage().getAlpha2()));
		response.setContentType("xlsx");
		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\"" + String.format("STA_%s_v%s.xlsx", analysis.getLabel(), analysis.getVersion()) + "\"");
		updateTokenCookie(request, response);
		spreadsheetMLPackage.save(response.getOutputStream());
		// Log
		TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.raw.action_plan",
				String.format("Analysis: %s, version: %s, type: Raw action plan", analysis.getIdentifier(), analysis.getVersion()), principal.getName(), LogAction.EXPORT,
				analysis.getIdentifier(), analysis.getVersion());

	}

	@RequestMapping(value = "/Asset/Export-process", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportAssetProcess(HttpServletRequest request, HttpServletResponse response, HttpSession session, Principal principal, Locale locale) throws Exception {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final SpreadsheetMLPackage spreadsheetMLPackage = SpreadsheetMLPackage.createPackage();
		exportAsset(analysis, spreadsheetMLPackage);
		response.setContentType("xlsx");
		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\"" + String.format("Assets of %s_v%s.xlsx", analysis.getLabel(), analysis.getVersion()) + "\"");
		updateTokenCookie(request, response);
		spreadsheetMLPackage.save(response.getOutputStream());
		// Log
		TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.asset",
				String.format("Analysis: %s, version: %s, type: Asset", analysis.getIdentifier(), analysis.getVersion()), principal.getName(), LogAction.EXPORT,
				analysis.getIdentifier(), analysis.getVersion());
	}

	@GetMapping(value = "/Export", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public String exportation(@RequestParam(name = "analysisId") Integer idAnalysis, Model model, HttpSession session, Principal principal, Locale locale) {
		final Analysis analysis = serviceAnalysis.findByIdAndEager(idAnalysis);
		final List<DataManagerItem> items = new LinkedList<>();
		if (!analysis.getActionPlans().isEmpty())
			items.add(new DataManagerItem("action-plan-raw", "/Analysis/Data-manager/Action-plan-raw/Export-process"));
		items.add(new DataManagerItem("asset", "/Analysis/Data-manager/Asset/Export-process"));
		items.add(new DataManagerItem("risk-information", "/Analysis/Data-manager/Risk-information/Export-process"));
		if (!analysis.getAnalysisStandards().isEmpty())
			items.add(new DataManagerItem("measure", "/Analysis/Data-manager/Measure/Export-form", null, null));
		items.add(new DataManagerItem("word-report", "/Analysis/Data-manager/Report/Export-form", "/Analysis/Data-manager/Report/Export-process", null));
		if (!analysis.getAssessments().isEmpty())
			items.add(new DataManagerItem("risk-estimation", "/Analysis/Data-manager/Risk-estimation/Export-process"));
		if (analysis.isQualitative()) {
			if (!analysis.getRiskRegisters().isEmpty())
				items.add(new DataManagerItem("risk-register", "/Analysis/Data-manager/Risk-register/Export-process", true));
			if (!analysis.getRiskProfiles().isEmpty())
				items.add(new DataManagerItem("risk-sheet", "/Analysis/Data-manager/Risk-sheet/Export-form", "/Analysis/Data-manager/Risk-sheet/Export-process", null));
		}

		if (analysis.isQuantitative())
			items.add(new DataManagerItem("rrf-raw", "/Analysis/Data-manager/RRF-Raw/Export-process"));
		items.add(new DataManagerItem("scenario", "/Analysis/Data-manager/Scenario/Export-process"));

		if (analysis.getAnalysisStandards().stream().anyMatch(AnalysisStandard::isSoaEnabled))
			items.add(new DataManagerItem("soa", "/Analysis/Data-manager/SOA/Export-process", true));
		items.sort((i1, i2) -> NaturalOrderComparator.compareTo(messageSource.getMessage("label.menu.data_manager.export." + i1.getName().replaceAll("-", "_"), null, locale),
				messageSource.getMessage("label.menu.data_manager.export." + i2.getName().replaceAll("-", "_"), null, locale)));
		model.addAttribute("items", items);
		model.addAttribute("maxFileSize", maxUploadFileSize);
		return "analyses/single/components/data-manager/export";
	}

	@GetMapping(value = "/Risk-estimation/Export-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportEstimationProcess(HttpServletRequest request, HttpServletResponse response, HttpSession session, Locale locale, Principal principal) throws Exception {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Analysis analysis = serviceAnalysis.get(idAnalysis);
		final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.createPackage();
		final WorkbookPart workbook = mlPackage.getWorkbookPart();
		final WorksheetPart worksheetPart = createWorkSheetPart(mlPackage, "Risk estimation");
		final SheetData sheetData = worksheetPart.getContents().getSheetData();
		final boolean hiddenComment = analysis.findSetting(AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT);
		final boolean qualitative = analysis.isHybrid() || analysis.isQualitative();
		final boolean rowColumn = analysis.findSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN);
		final boolean uncertainty = analysis.isUncertainty();
		final ValueFactory factory = new ValueFactory(analysis.getParameters());
		assessmentAndRiskProfileManager.updateAssessment(analysis, factory);
		final List<ScaleType> scales = analysis.findImpacts();
		final Map<String, RiskProfile> riskProfiles = analysis.getRiskProfiles().stream().collect(Collectors.toMap(RiskProfile::getKey, Function.identity()));
		final Map<AssetType, String> assetTypes = serviceAssetType.getAll().stream()
				.collect(Collectors.toMap(Function.identity(), e -> messageSource.getMessage("label.asset_type." + e.getName().toLowerCase(), null, e.getName(), locale)));
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
			if (qualitative) {
				setValue(row, cellIndex++, (profile.getRiskStrategy() == null ? RiskStrategy.ACCEPT : profile.getRiskStrategy()).getNameToLower());
				if (rowColumn)
					cellIndex += writeProbaImpact(row, cellIndex++, profile.getRawProbaImpact(), scales);
				cellIndex += writeProbaImpact(row, cellIndex++, assessment, scales, analysis.getType());
				cellIndex += writeProbaImpact(row, cellIndex++, profile.getExpProbaImpact(), scales);
			} else {
				setValue(row, cellIndex++, assessment.getLikelihood());
				writeQuantitativeImpact(row, cellIndex++, assessment.getImpact(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME));
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
			setFormula(setValue(row, cellIndex++, assetTypes.get(assessment.getAsset().getAssetType())),
					"VLOOKUP(Risk_estimation[[#This Row],[Asset]],Assets[[#All],[Name]:[Value]],2,FALSE)");
			setFormula(setValue(row, cellIndex++, assessment.getAsset().isSelected()), "VLOOKUP(Risk_estimation[[#This Row],[Asset]],Assets[[#All],[Name]:[Value]],3,FALSE)");
		}
		exportAsset(analysis, mlPackage, hiddenComment);
		exportScenario(analysis, mlPackage);
		response.setContentType("xlsx");
		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\"" + String.format("Risk estimation for %s_v%s.xlsx", analysis.getLabel(), analysis.getVersion()) + "\"");
		updateTokenCookie(request, response);
		mlPackage.save(response.getOutputStream());
		serviceAnalysis.saveOrUpdate(analysis);
		// Log
		TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.risk.estimation",
				String.format("Analysis: %s, version: %s, type: Risk estimation", analysis.getIdentifier(), analysis.getVersion()), principal.getName(), LogAction.EXPORT,
				analysis.getIdentifier(), analysis.getVersion());

	}

	@GetMapping("/Measure/Export-form")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session,#principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public String exportMeasureForm(Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		model.addAttribute("standards", analysis.findStandards());
		model.addAttribute("item", new DataManagerItem("measure", "/Analysis/Data-manager/Measure/Export-process"));
		return "analyses/single/components/data-manager/export/measure";
	}

	@PostMapping(value = "/Measure/Export-process")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session,#principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportMeasureProcess(@RequestParam(name = "standards") List<Integer> standards, HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.createPackage();
		final ValueFactory factory = new ValueFactory(analysis.getParameters());
		for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards()) {
			if (standards.contains(analysisStandard.getStandard().getId())) {
				WorksheetPart worksheetPart = createWorkSheetPart(mlPackage, analysisStandard.getStandard().getLabel());
				exportMeasureStandard(factory, analysisStandard, mlPackage, worksheetPart);
			}
		}
		response.setContentType("xlsx");
		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\"" + String.format("%s_v%s.xlsx", analysis.getLabel(), analysis.getVersion()) + "\"");
		updateTokenCookie(request, response);
		mlPackage.save(response.getOutputStream());
		// Log
		TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.measure",
				String.format("Analysis: %s, version: %s, type: Measure data", analysis.getIdentifier(), analysis.getVersion()), principal.getName(), LogAction.EXPORT,
				analysis.getIdentifier(), analysis.getVersion());
	}

	@GetMapping(value = "/Report/Export-form/{analysisId}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public String exportReportForm(@PathVariable Integer analysisId, Principal principal, Model model, Locale locale) {
		final Analysis analysis = serviceAnalysis.get(analysisId);
		final List<ReportTemplate> reportTemplates = defaultReportTemplateLoader.findByTypeAndLanguage(analysis.getType(), analysis.getLanguage().getAlpha3());
		final Map<String, String> versions = reportTemplates.stream().collect(Collectors.toMap(ReportTemplate::getKey, ReportTemplate::getVersion));
		analysis.getCustomer().getTemplates().stream()
				.filter(p -> p.getLanguage().equals(analysis.getLanguage()) && analysis.getType().isHybrid() ? true : analysis.getType() == p.getType())
				.sorted((p1, p2) -> NaturalOrderComparator.compareTo(p1.getVersion(), p2.getVersion())).forEach(p -> {
					reportTemplates.add(p);
					if (!p.getVersion().equalsIgnoreCase(versions.get(p.getKey())))
						p.setOutToDate(true);
				});

		if (analysis.isHybrid())
			model.addAttribute("types", new AnalysisType[] { AnalysisType.QUANTITATIVE, AnalysisType.QUALITATIVE });
		model.addAttribute("analysis", analysis);
		model.addAttribute("templates", reportTemplates);
		model.addAttribute("maxFileSize", Math.min(maxUploadFileSize, maxRefurbishReportSize));
		return "analyses/single/components/data-manager/export/report/modal";
	}

	@GetMapping(value = "/Report/Export-form", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public String exportReportForm(Model model, HttpSession session, Principal principal, Locale locale) {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final List<ReportTemplate> reportTemplates = defaultReportTemplateLoader.findByTypeAndLanguage(analysis.getType(), analysis.getLanguage().getAlpha3());
		final Map<String, String> versions = reportTemplates.stream().collect(Collectors.toMap(ReportTemplate::getKey, ReportTemplate::getVersion));
		analysis.getCustomer().getTemplates().stream()
				.filter(p -> p.getLanguage().equals(analysis.getLanguage()) && analysis.getType().isHybrid() ? true : analysis.getType() == p.getType())
				.sorted((p1, p2) -> NaturalOrderComparator.compareTo(p1.getVersion(), p2.getVersion())).forEach(p -> {
					reportTemplates.add(p);
					if (!p.getVersion().equalsIgnoreCase(versions.get(p.getKey())))
						p.setOutToDate(true);
				});

		if (analysis.isHybrid())
			model.addAttribute("items", new DataManagerItem[] { new DataManagerItem(AnalysisType.QUANTITATIVE.name(), "/Analysis/Data-manager/Report/Export-process", ".docx"),
					new DataManagerItem(AnalysisType.QUALITATIVE.name(), "/Analysis/Data-manager/Report/Export-process", ".docx") });
		else
			model.addAttribute("item", new DataManagerItem(analysis.getType().name(), "/Analysis/Data-manager/Report/Export-process", ".docx"));

		model.addAttribute("analysis", analysis);
		model.addAttribute("templates", reportTemplates);
		model.addAttribute("maxFileSize", Math.min(maxUploadFileSize, maxRefurbishReportSize));
		return "analyses/single/components/data-manager/export/report/home";
	}

	// *****************************************************************
	// * reload customer section by pageindex
	// *****************************************************************
	/**
	 * computeRiskRegister: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param attributes
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/Report/Export-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody String exportReportProcess(@RequestParam(name = "analysis") Integer analysisId, @ModelAttribute ExportWordReportForm form, HttpServletRequest request,
			Principal principal, Locale locale) throws Exception {
		try {
			Integer customerId = serviceAnalysis.getCustomerIdByIdAnalysis(analysisId);
			if (form.isInternal()) {
				if (!serviceReportTemplate.isUseAuthorised(form.getTemplate(), customerId))
					throw new AccessDeniedException(messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));
			} else if (form.getFile() == null)
				return JsonMessage.Error(messageSource.getMessage("error.export.report.file.empty", null, "No file selected", locale));

			if (AnalysisType.isQualitative(serviceAnalysis.getAnalysisTypeById(analysisId)) && !serviceRiskAcceptanceParameter.existsByAnalysisId(analysisId))
				throw new TrickException("error.export.risk.acceptance.empty", "Please update risk acception settings: Analysis -> Parameter -> Risk acceptance");

			if (!form.isInternal()) {
				long maxSize = Math.min(maxUploadFileSize, maxRefurbishReportSize);
				if (form.getFile().getSize() > maxSize)
					return JsonMessage.Error(messageSource.getMessage("error.file.too.large", new Object[] { maxSize }, "File is to large", locale));
				if (!DefaultReportTemplateLoader.isDocx(form.getFile().getInputStream()))
					return JsonMessage.Error(messageSource.getMessage("error.file.no.docx", null, "Docx file is excepted", locale));
			}

			ExportReportData exportAnalysisReport = new Docx4jReportImpl(request.getServletContext().getRealPath(""),messageSource);
			Worker worker = new WorkerExportWordReport(analysisId, form.getTemplate(), principal.getName(), sessionFactory, workersPoolManager, exportAnalysisReport, serviceTaskFeedback);
			if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
				return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
			if (!form.isInternal()) {
				((Docx4jReportImpl) exportAnalysisReport).setFile(new File(
						request.getServletContext().getRealPath("/WEB-INF/tmp") + "/Report-template-" + principal.getName() + "-" + UUID.randomUUID().toString() + ".docx"));
				form.getFile().transferTo(exportAnalysisReport.getFile());
				exportAnalysisReport.getFile().deleteOnExit();
			}
			executor.execute(worker);
			return JsonMessage.Success(messageSource.getMessage("success.analysis.report.exporting", null, "Exporting report", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			if (e instanceof AccessDeniedException)
				throw e;
			return JsonMessage.Error(messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
	}

	@GetMapping("/Risk-information/Export-process")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public String exportRiskInformationProcess(HttpServletRequest request, HttpServletResponse response, HttpSession session, Principal principal, Locale locale) throws Exception {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final Locale analysisLocale = new Locale(analysis.getLanguage().getAlpha2());
		final Map<String, List<RiskInformation>> riskInformationMap = analysis.getRiskInformations().stream().map(riskInformation -> {
			if (!riskInformation.isCustom()) {
				switch (riskInformation.getCategory()) {
				case "Risk_TBA":
					riskInformation.setLabel(messageSource.getMessage(String.format("label.risk_information.risk_tba.", riskInformation.getChapter().replace(".", "_")), null,
							riskInformation.getLabel(), analysisLocale));
					break;
				case "Risk_TBS":
					riskInformation.setLabel(messageSource.getMessage(String.format("label.risk_information.risk_tbs.", riskInformation.getChapter().replace(".", "_")), null,
							riskInformation.getLabel(), analysisLocale));
					break;
				default:
					riskInformation.setLabel(messageSource.getMessage(
							String.format("label.risk_information.%s.", riskInformation.getCategory().toLowerCase(), riskInformation.getChapter().replace(".", "_")), null,
							riskInformation.getLabel(), analysisLocale));
					break;
				}
			}
			return riskInformation;
		}).sorted(new RiskInformationComparator())
				.collect(Collectors.groupingBy(riskInformation -> riskInformation.getCategory().startsWith("Risk_TB") ? "Risk" : riskInformation.getCategory()));

		final File workFile = new File(request.getServletContext().getRealPath(String.format("/WEB-INF/tmp/TMP_Risk-information_%d_%d.xlsx", analysis.getId(), System.nanoTime())));

		try {
			FileUtils.copyFile(new File(request.getServletContext().getRealPath(brainstormingTemplate)), workFile);
			final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(workFile);
			final WorkbookPart workbook = mlPackage.getWorkbookPart();
			for (Object[] mapper : RI_SHEET_MAPPERS) {
				List<RiskInformation> riskInformations = riskInformationMap.get(mapper[0]);
				if (riskInformations == null)
					continue;
				SheetData sheet = findSheet(workbook, mapper[1].toString());
				if (sheet == null)
					throw new TrickException("error.risk.information.template.sheet.not.found",
							String.format("Something wrong with template: Sheet `%s` cannot be found", mapper[1].toString()), mapper[1].toString());
				TablePart tablePart = findTable(sheet.getWorksheetPart(), mapper[0] + "Table");
				if (tablePart == null)
					throw new TrickException("error.risk.information.template.table.not.found",
							String.format("Something wrong with sheet `%s` : Table `%s` cannot be found", mapper[1].toString(), mapper[0] + "Table"), mapper[1].toString(),
							mapper[0] + "Table");
				AddressRef address = AddressRef.parse(tablePart.getContents().getRef());
				if (address.getEnd() == null)
					address.setEnd(new CellRef(riskInformations.size(), (int) mapper[2] - 1));
				else
					address.getEnd().setRow(riskInformations.size());

				CTTable table = tablePart.getContents();
				table.setRef(address.toString());

				if (table.getAutoFilter() != null)
					table.getAutoFilter().setRef(table.getRef());

				if (sheet.getWorksheetPart().getContents().getDimension() != null)
					sheet.getWorksheetPart().getContents().getDimension().setRef(table.getRef());
				int rowIndex = 1, colSize = address.getEnd().getCol();
				for (RiskInformation riskInformation : riskInformations) {
					int colIndex = 0;
					Row row = getRow(sheet, rowIndex++, colSize);
					setValue(row.getC().get(colIndex++), riskInformation.getChapter());
					setValue(row.getC().get(colIndex++), riskInformation.getLabel());
					if (riskInformation.getCategory().equals(Constant.RI_TYPE_THREAT))
						setValue(row.getC().get(colIndex++), riskInformation.getAcronym());
					setValue(row.getC().get(colIndex++), riskInformation.getExposed());
					setValue(row.getC().get(colIndex++), riskInformation.getOwner());
					setValue(row.getC().get(colIndex++), riskInformation.getComment());
					setValue(row.getC().get(colIndex++), riskInformation.getHiddenComment());
				}
			}

			String identifierName = "TS_Brainstorming_" + analysis.getIdentifier() + "_Version_" + analysis.getVersion();
			response.setContentType("xlsx");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + (identifierName.trim().replaceAll(":|-|[ ]", "_")) + ".xlsx\"");
			updateTokenCookie(request, response);
			mlPackage.save(response.getOutputStream());
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.export.risk.information",
					String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()), principal.getName(), LogAction.EXPORT, analysis.getIdentifier(),
					analysis.getVersion());

			return null;
		} finally {
			if (workFile.exists() && !workFile.delete())
				workFile.deleteOnExit();
		}
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	@RequestMapping(value = "/Risk-register/Export-process", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object exportRiskRegisterProcess(HttpSession session, HttpServletRequest request, Principal principal, Locale locale) {
		Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Locale analysisLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha2());
		Worker worker = new WorkerExportRiskRegister(analysisId, principal.getName(), request.getServletContext().getRealPath("/WEB-INF"), sessionFactory, workersPoolManager,
				serviceTaskFeedback, messageSource);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", analysisLocale));
		// execute task
		executor.execute(worker);
		return JsonMessage.Success(messageSource.getMessage("success.start.export.risk_register", null, "Start to export risk register", analysisLocale));
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	@RequestMapping(value = "/Risk-sheet/Export-form", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String exportRiskSheetForm(HttpSession session, Model model, HttpServletRequest request, Principal principal) {
		Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		ScaleType scaleType = serviceScaleType.findOneByAnalysisId(analysisId);
		List<? extends IBoundedParameter> impacts = serviceImpactParameter.findByTypeAndAnalysisId(scaleType, analysisId),
				probabilities = serviceLikelihoodParameter.findByAnalysisId(analysisId);
		impacts.removeIf(parameter -> parameter.getLevel() == 0);
		probabilities.removeIf(parameter -> parameter.getLevel() == 0);
		model.addAttribute("parameters", serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_CSSF_NAME, analysisId).stream()
				.collect(Collectors.toMap(IParameter::getDescription, Function.identity())));
		model.addAttribute("owners", serviceAssessment.getDistinctOwnerByIdAnalysis(analysisId));
		model.addAttribute("impacts", impacts);
		model.addAttribute("probabilities", probabilities);
		model.addAttribute("reportRiskSheetItem", new DataManagerItem("risk-sheet-report", "/Analysis/Data-manager/Risk-sheet/Export-process"));
		model.addAttribute("rawRiskSheetItem", new DataManagerItem("risk-sheet-raw", "/Analysis/Data-manager/Risk-sheet/Export-process"));
		return "analyses/single/components/data-manager/export/risk-sheet/home";
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	@RequestMapping(value = "/Risk-sheet/Export-process", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object exportRiskSheetProcess(@RequestBody CSSFExportForm cssfExportForm, HttpSession session, HttpServletRequest request, Principal principal,
			Locale locale) {
		Map<String, String> errors = new HashMap<>();
		if (cssfExportForm.getFilter() == null)
			errors.put("filter", messageSource.getMessage("error.invalid.filter", null, "Filter cannot be load", locale));
		else {
			CSSFFilter cssfFilter = cssfExportForm.getFilter();
			if (cssfFilter.getImpact() < 0 || cssfFilter.getImpact() > Constant.DOUBLE_MAX_VALUE)
				errors.put("filter.impact", messageSource.getMessage("error.invalid.value", null, "Invalid value", locale));
			if (cssfFilter.getProbability() < 0 || cssfFilter.getProbability() > Constant.DOUBLE_MAX_VALUE)
				errors.put("filter.probability", messageSource.getMessage("error.invalid.value", null, "Invalid value", locale));
			if (cssfFilter.getDirect() < -2)
				errors.put("filter.direct", messageSource.getMessage("error.invalid.value", null, "Invalid value", locale));
			if (cssfFilter.getIndirect() < -2)
				errors.put("filter.indirect", messageSource.getMessage("error.invalid.value", null, "Invalid value", locale));
			if (cssfFilter.getCia() < -2)
				errors.put("filter.cia", messageSource.getMessage("error.invalid.value", null, "Invalid value", locale));
		}

		if (!errors.isEmpty())
			return errors;

		Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Locale analysisLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha2());
		Worker worker = new WorkerExportRiskSheet(cssfExportForm, workersPoolManager, sessionFactory, serviceTaskFeedback, request.getServletContext().getRealPath("/WEB-INF"),
				analysisId, principal.getName(), messageSource);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", analysisLocale));
		// execute task
		executor.execute(worker);
		return JsonMessage.Success(messageSource.getMessage("success.start.export.risk_sheet", null, "Start to export risk sheet", analysisLocale));
	}

	@RequestMapping(value = "/RRF-Raw/Export-process", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportRRFRawProcess(Model model, HttpServletRequest request, HttpSession session, HttpServletResponse response, Principal principal, Locale locale)
			throws Exception {

		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final Consumer<SpreadsheetMLPackage> callback = (m) -> {
			try {
				response.setContentType("xlsx");
				// set response header with location of the filename
				response.setHeader("Content-Disposition", "attachment; filename=\"" + String.format("RAW RRF %s_V%s.xlsx", analysis.getLabel(), analysis.getVersion()) + "\"");
				updateTokenCookie(request, response);
				m.save(response.getOutputStream());
				// log
				TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.raw.rrf",
						String.format("Analysis: %s, version: %s, type: Raw RRF", analysis.getIdentifier(), analysis.getVersion()), principal.getName(), LogAction.EXPORT,
						analysis.getIdentifier(), analysis.getVersion());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};

		new RRFExportImport(serviceAssetType, serviceAnalysis, messageSource).exportRawRRF(analysis, response, locale, callback);
	}

	@RequestMapping(value = "/Scenario/Export-process", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportScenarioProcess(HttpServletRequest request, HttpServletResponse response, HttpSession session, Principal principal, Locale locale) throws Exception {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final SpreadsheetMLPackage spreadsheetMLPackage = SpreadsheetMLPackage.createPackage();
		exportScenario(analysis, spreadsheetMLPackage);
		response.setContentType("xlsx");
		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\"" + String.format("Scenarios of %s_v%s.xlsx", analysis.getLabel(), analysis.getVersion()) + "\"");
		updateTokenCookie(request, response);
		spreadsheetMLPackage.save(response.getOutputStream());
		// Log
		TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.asset",
				String.format("Analysis: %s, version: %s, type: Asset", analysis.getIdentifier(), analysis.getVersion()), principal.getName(), LogAction.EXPORT,
				analysis.getIdentifier(), analysis.getVersion());
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
	@RequestMapping(value = "/SOA/Export-process", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody String exportSOAProcess(HttpSession session, Principal principal, HttpServletRequest request, Model model, Locale locale) throws Exception {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Worker worker = new WorkerSOAExport(principal.getName(), request.getServletContext().getRealPath("/WEB-INF"), idAnalysis, messageSource, serviceTaskFeedback,
				workersPoolManager, sessionFactory);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
		// execute task
		executor.execute(worker);
		return JsonMessage.Success(messageSource.getMessage("success.start.exporting.soa", null, "SOA exporting was successfully started", locale));
	}

	/**
	 * exportAnalysis: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param principal
	 * @param request
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Sqlite/Export-process", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody String exportSqliteProcess(@RequestParam(name = "idAnalysis") int analysisId, Principal principal, HttpServletRequest request, Locale locale)
			throws Exception {
		// create worker
		Worker worker = new WorkerExportAnalysis(serviceTaskFeedback, sessionFactory, principal, request.getServletContext(), workersPoolManager, analysisId);
		// register worker
		if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale)) {
			executor.execute(worker);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.start.export.analysis", null, "Analysis export was started successfully", locale));
		} else
			// return error message
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
	}

	/**
	 * importAnalysis: <br>
	 * Description
	 * 
	 * @param principal
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/Sqlite/Import-form", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String importAnalysis(@RequestParam(name = "idCustomer") Integer idCustomer, Principal principal, Model model) throws Exception {
		Customer customer = serviceCustomer.getFromUsernameAndId(principal.getName(), idCustomer);
		if (customer == null)
			throw new AccessDeniedException("access denied");
		model.addAttribute("maxFileSize", maxUploadFileSize);
		model.addAttribute("customer", customer);
		return "analyses/single/components/data-manager/import/sqlite";
	}

	/**
	 * importAnalysisSave: <br>
	 * Description
	 * 
	 * @param principal
	 * @param customerId
	 * @param request
	 * @param file
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/Sqlite/Import-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object importAnalysisSave(@RequestParam(name = "customer") Integer idCustomer, @ModelAttribute ImportAnalysisForm form, Principal principal,
			HttpServletRequest request, Locale locale) throws Exception {
		// retrieve the customer
		if (!serviceCustomer.hasAccess(principal.getName(), idCustomer))
			throw new AccessDeniedException("access denied");
		if (form.getFile().isEmpty())
			return JsonMessage.Error(messageSource.getMessage("error.customer_or_file.import.analysis", null, "Customer or file are not set or empty!", locale));
		else if (form.getFile().getSize() > maxUploadFileSize)
			return JsonMessage.Error(messageSource.getMessage("error.file.too.large", new Object[] { maxUploadFileSize }, "File is to large", locale));
		// the file to import
		File importFile = new File(request.getServletContext().getRealPath("/WEB-INF/tmp") + "/" + principal.getName() + "_" + System.nanoTime() + ".tsdb");
		// create worker
		Worker worker = new WorkerAnalysisImport(workersPoolManager, sessionFactory, serviceTaskFeedback, importFile, idCustomer, principal.getName());
		// register worker to tasklist
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
		// transfer form file to java file
		form.getFile().transferTo(importFile);
		// execute task
		executor.execute(worker);
		return JsonMessage.Success(messageSource.getMessage("sucess.analysis.importing", null, "Please wait while importing your analysis", locale));
	}

	@PostMapping(value = "/Asset/Import-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String importAssetProcess(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		return importRisEstimation(true, false, file, request, model, session, principal, locale);
	}

	/**
	 * 
	 * @param idAnalysis
	 * @param model
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 */
	@GetMapping(value = "/Import", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String importation(@RequestParam(name = "analysisId") Integer idAnalysis, Model model, HttpSession session, Principal principal, Locale locale) {
		final Analysis analysis = serviceAnalysis.findByIdAndEager(idAnalysis);
		final List<DataManagerItem> items = new LinkedList<>();
		items.add(new DataManagerItem("asset", "/Analysis/Data-manager/Asset/Import-process", ".xls,.xlsx,.xlsm"));
		items.add(new DataManagerItem("risk-information", "/Analysis/Data-manager/Risk-information/Import-process", ".xls,.xlsx,.xlsm"));
		if (!analysis.getAnalysisStandards().isEmpty())
			items.add(new DataManagerItem("measure", "/Analysis/Data-manager/Measure/Import-process", ".xls,.xlsx,.xlsm"));
		items.add(new DataManagerItem("risk-estimation", "/Analysis/Data-manager/Risk-estimation/Import-process", ".xls,.xlsx,.xlsm"));
		items.add(new DataManagerItem("scenario", "/Analysis/Data-manager/Scenario/Import-process", ".xls,.xlsx,.xlsm"));
		if (analysis.isQuantitative())
			items.add(new DataManagerItem("rrf", "/Analysis/Data-manager/RRF/Import-form", null, null));
		items.sort((i1, i2) -> NaturalOrderComparator.compareTo(messageSource.getMessage("label.menu.data_manager.import." + i1.getName().replaceAll("-", "_"), null, locale),
				messageSource.getMessage("label.menu.data_manager.import." + i2.getName().replaceAll("-", "_"), null, locale)));
		model.addAttribute("items", items);
		model.addAttribute("maxFileSize", maxUploadFileSize);
		return "analyses/single/components/data-manager/import";
	}

	@PostMapping(value = "/Risk-estimation/Import-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String importEstimationProcess(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		return importRisEstimation(false, false, file, request, model, session, principal, locale);
	}

	/**
	 * manage analysis standards (manage menu)
	 */
	@PostMapping(value = "/Measure/Import-process", produces = APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object importMeasureProcess(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpSession session, Principal principal, Locale locale)
			throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		File workFile = new File(request.getServletContext().getRealPath("/WEB-INF/tmp") + "/" + principal.getName() + "_" + System.nanoTime());
		Worker worker = new WorkerImportMeasureData(principal.getName(), idAnalysis, workFile, serviceTaskFeedback, workersPoolManager, sessionFactory);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
		file.transferTo(workFile);
		executor.execute(worker);
		return JsonMessage.Success(messageSource.getMessage("success.start.import.measure.data", null, "Importing of security measures data", locale));
	}

	@PostMapping(value = "/Risk-information/Import-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String importRiskInformationProcess(@RequestParam(value = "file") MultipartFile file, HttpSession session, Principal principal, HttpServletRequest request,
			Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		File workFile = new File(request.getServletContext().getRealPath("/WEB-INF/tmp") + "/" + principal.getName() + "_" + System.nanoTime());
		Worker worker = new WorkerImportRiskInformation(idAnalysis, principal.getName(), workFile, messageSource, workersPoolManager, sessionFactory, serviceTaskFeedback);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
		file.transferTo(workFile);
		executor.execute(worker);
		return JsonMessage.Success(messageSource.getMessage("success.start.import.risk.information", null, "Importing of risk information", locale));
	}

	/**
	 * importRRF: <br>
	 * Description
	 * 
	 * @param session
	 * @param principal
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	@RequestMapping(value = "/RRF/Import-form", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String importRRFForm(HttpSession session, Principal principal, Model model) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		List<Standard> standards = serviceStandard.getAllFromAnalysis(idAnalysis);
		standards.removeIf(standard -> Constant.STANDARD_MATURITY.equalsIgnoreCase(standard.getLabel()));
		List<Analysis> analyses = serviceAnalysis.getAllProfileContainsStandard(standards, AnalysisType.QUANTITATIVE, AnalysisType.HYBRID);
		analyses.addAll(serviceAnalysis.getAllHasRightsAndContainsStandard(principal.getName(), AnalysisRight.highRightFrom(AnalysisRight.MODIFY), standards,
				AnalysisType.QUANTITATIVE, AnalysisType.HYBRID));
		analyses.removeIf(analysis -> analysis.getId() == idAnalysis);
		Collections.sort(analyses, new AnalysisComparator());
		List<Customer> customers = new ArrayList<Customer>();
		analyses.stream().map(analysis -> analysis.getCustomer()).distinct().forEach(customer -> customers.add(customer));
		model.addAttribute("standards", standards);
		model.addAttribute("customers", customers);
		model.addAttribute("analyses", analyses);
		model.addAttribute("rawRRFItem", new DataManagerItem("raw-rrf", "/Analysis/Data-manager/RRF-RAW/Import-process", ".xls,.xlsx,.xlsm"));
		model.addAttribute("rrfItem", new DataManagerItem("rrf-knowledge-base", null, "/Analysis/Data-manager/RRF/Import-process", null));
		return "analyses/single/components/data-manager/import/rrf";
	}

	/**
	 * 
	 * @param rrfForm
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	@RequestMapping(value = "/RRF/Import-process", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object importRRFProcess(@ModelAttribute ImportRRFForm rrfForm, HttpSession session, Principal principal, Locale locale) {
		try {
			if (rrfForm.getAnalysis() < 1)
				return JsonMessage.Error(messageSource.getMessage("error.import_rrf.no_analysis", null, "No analysis selected", locale));
			else if (rrfForm.getStandards() == null || rrfForm.getStandards().isEmpty())
				return JsonMessage.Error(messageSource.getMessage("error.import_rrf.norm", null, "No standard", locale));
			if (!(serviceAnalysis.isProfile(rrfForm.getAnalysis())
					|| serviceUserAnalysisRight.isUserAuthorized(rrfForm.getAnalysis(), principal.getName(), AnalysisRight.highRightFrom(AnalysisRight.MODIFY))))
				return JsonMessage.Error(messageSource.getMessage("error.action.not_authorise", null, "Action does not authorised", locale));
			else if (!rrfForm.getStandards().stream().allMatch(idStandard -> serviceStandard.belongsToAnalysis(rrfForm.getAnalysis(), idStandard)))
				return JsonMessage.Error(messageSource.getMessage("error.action.not_authorise", null, "Action does not authorised", locale));
			measureManager.importStandard((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), rrfForm);
			return JsonMessage.Success(messageSource.getMessage("success.import_rrf", null, "Measure characteristics has been successfully imported", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}

	}

	@RequestMapping(value = "/RRF-RAW/Import-process", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object importRRFRawProcess(@RequestParam(value = "file") MultipartFile file, HttpSession session, Principal principal, HttpServletRequest request,
			Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return new RRFExportImport(serviceAssetType, serviceAnalysis, messageSource).importRawRRF(request.getServletContext().getRealPath("/WEB-INF/tmp/"), idAnalysis, file,
				principal.getName(), locale);
	}

	///
	/// Import part.
	///

	@PostMapping(value = "/Scenario/Import-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String importScenarioProcess(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		return importRisEstimation(false, true, file, request, model, session, principal, locale);
	}

	private String[] createTableHeader(List<ScaleType> scales, WorkbookPart workbook, boolean qualitative, boolean hiddenComment, boolean rowColumn, boolean uncertainty) {
		List<Column> columns = WorkerImportEstimation.generateColumns(scales, qualitative, hiddenComment, rowColumn, uncertainty);
		columns.add(new Column("Asset type"));
		columns.add(new Column("Asset selected"));
		String[] result = new String[columns.size()];
		for (int i = 0; i < columns.size(); i++)
			result[i] = columns.get(i).getName();
		return result;
	}

	private void exportAsset(Analysis analysis, SpreadsheetMLPackage spreadsheetMLPackage) throws Exception, JAXBException {
		final boolean hidenComment = analysis.findSetting(AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT);
		exportAsset(analysis, spreadsheetMLPackage, hidenComment);
	}

	private void exportAsset(Analysis analysis, SpreadsheetMLPackage spreadsheetMLPackage, final boolean hidenComment)
			throws InvalidFormatException, JAXBException, Docx4JException, Exception {
		final String name = "Assets";
		final ObjectFactory factory = Context.getsmlObjectFactory();
		final WorksheetPart worksheetPart = createWorkSheetPart(spreadsheetMLPackage, name);
		final SheetData sheet = worksheetPart.getContents().getSheetData();
		final String[] columns = hidenComment ? new String[] { "Name", "Type", "Selected", "Value", "Comment", "Hidden comment" }
				: new String[] { "Name", "Type", "Selected", "Value", "Comment" };
		createHeader(worksheetPart, name, columns, analysis.getAssets().size());
		final Map<String, String> assetTypes = exportAssetType(spreadsheetMLPackage, new Locale(analysis.getLanguage().getAlpha2()), factory);
		for (Asset asset : analysis.getAssets()) {
			Row row = factory.createRow();
			for (int i = 0; i <= columns.length; i++) {
				if (row.getC().size() < i)
					row.getC().add(Context.smlObjectFactory.createCell());
			}
			setValue(row.getC().get(0), asset.getName());
			setValue(row.getC().get(1), assetTypes.getOrDefault(asset.getAssetType().getName(), asset.getAssetType().getName()));
			setValue(row.getC().get(2), asset.isSelected());
			setValue(row.getC().get(3), asset.getValue() * 0.001);
			setValue(row.getC().get(4), asset.getComment());
			if (hidenComment)
				setValue(row.getC().get(5), asset.getHiddenComment());
			sheet.getRow().add(row);
		}
	}

	private Map<String, String> exportAssetType(SpreadsheetMLPackage spreadsheetMLPackage, Locale locale, ObjectFactory factory) throws JAXBException, Exception {
		final String name = "AssetTypes";
		final WorksheetPart worksheetPart = createWorkSheetPart(spreadsheetMLPackage, name);
		final SheetData sheet = worksheetPart.getContents().getSheetData();
		final List<AssetType> assetTypes = serviceAssetType.getAll();
		createHeader(worksheetPart, name, new String[] { "Name", "Display name" }, assetTypes.size());
		final Map<String, String> names = new LinkedHashMap<>(assetTypes.size());
		for (AssetType assetType : assetTypes) {
			String value = messageSource.getMessage("label.asset_type." + assetType.getName().toLowerCase(), null, assetType.getName(), locale);
			names.put(assetType.getName(), value);
			final Row row = factory.createRow();
			setValue(row, 0, assetType.getName());
			setValue(row, 1, value);
			sheet.getRow().add(row);
		}
		return names;
	}

	private void exportMeasureStandard(ValueFactory valueFactory, AnalysisStandard analysisStandard, SpreadsheetMLPackage mlPackage, WorksheetPart worksheetPart) throws Exception {
		String[] columns = getColumns(analysisStandard);
		prepareTableHeader(analysisStandard, worksheetPart, columns);
		SheetData sheetData = worksheetPart.getContents().getSheetData();
		analysisStandard.getMeasures().sort((m1, m2) -> NaturalOrderComparator.compareTo(m1.getMeasureDescription().getReference(), m2.getMeasureDescription().getReference()));
		for (Measure measure : analysisStandard.getMeasures()) {
			Row row = createRow(sheetData);
			for (int i = 0; i < columns.length; i++) {
				switch (columns[i]) {
				case "Reference":
					setValue(row, i, measure.getMeasureDescription().getReference());
					break;
				case "Status":
					setValue(row, i, measure.getStatus());
					break;
				case "Implemention":
					setValue(row, i, measure.getImplementationRateValue(valueFactory) * 0.01);
					break;
				case "Internal Workload":
					setValue(row, i, measure.getInternalWL());
					break;
				case "External Workload":
					setValue(row, i, measure.getExternalWL());
					break;
				case "Investment":
					setValue(row, i, measure.getInvestment() * 0.001);
					break;
				case "Life time":
					setValue(row, i, measure.getLifetime());
					break;
				case "Internal Maintenance":
					setValue(row, i, measure.getInternalMaintenance());
					break;
				case "External Maintenance":
					setValue(row, i, measure.getExternalMaintenance());
					break;
				case "Recurrent Maintenance":
					setValue(row, i, measure.getRecurrentInvestment() * 0.001);
					break;
				case "Phase":
					setValue(row, i, measure.getPhase().getNumber());
					break;
				case "Responsible":
					setValue(row, i, measure.getResponsible());
					break;
				case "To check":
					if (measure instanceof AbstractNormalMeasure)
						setValue(row, i, ((AbstractNormalMeasure) measure).getToCheck());
					break;
				case "Comment":
					setValue(row, i, measure.getComment());
					break;
				case "To do":
					setValue(row, i, measure.getToDo());
					break;
				default:
					break;
				}
			}
		}
	}

	// *****************************************************************
	// * select or deselect analysis
	// *****************************************************************

	private void exportRawActionPlan(Analysis analysis, SpreadsheetMLPackage spreadsheetMLPackage, Locale locale) throws Exception {
		ObjectFactory factory = Context.getsmlObjectFactory();
		List<IProbabilityParameter> expressionParameters = analysis.getExpressionParameters();
		ActionPlanMode[] types = analysis.isHybrid() ? new ActionPlanMode[] { ActionPlanMode.APPN, ActionPlanMode.APQ }
				: analysis.isQualitative() ? new ActionPlanMode[] { ActionPlanMode.APQ } : new ActionPlanMode[] { ActionPlanMode.APPN };

		for (int i = 0; i < types.length; i++) {
			final ActionPlanMode type = types[i];
			final int colCount = type == ActionPlanMode.APPN ? 21 : 19;
			final List<ActionPlanEntry> actionPlanEntries = analysis.findActionPlan(type);
			if (actionPlanEntries.isEmpty())
				continue;
			final String name = messageSource.getMessage("label.title.plan_type." + type.getName().toLowerCase(), null, type.getName(), locale);
			final WorksheetPart worksheetPart = spreadsheetMLPackage.createWorksheetPart(new PartName("/xl/worksheets/sheet" + (i + 1) + ".xml"), name, i + 1);
			final SheetData sheet = worksheetPart.getContents().getSheetData();
			createHeader(worksheetPart, name, generateActionPlanColumns(colCount - 1, type, locale), actionPlanEntries.size());
			for (ActionPlanEntry actionPlanEntry : actionPlanEntries)
				sheet.getRow().add(writeActionPLanData(factory.createRow(), colCount, actionPlanEntry, expressionParameters, locale));
		}
	}

	private void exportScenario(Analysis analysis, SpreadsheetMLPackage spreadsheetMLPackage) throws Exception {
		final String name = "Scenarios";
		final ObjectFactory factory = Context.getsmlObjectFactory();
		final String[] columns = { "Name", "Type", "Apply to", "Selected", "Description" };
		final WorksheetPart worksheetPart = createWorkSheetPart(spreadsheetMLPackage, name);
		final SheetData sheet = worksheetPart.getContents().getSheetData();
		createHeader(worksheetPart, name, columns, analysis.getScenarios().size());
		final Map<String, String> scenarioTypes = exportScenarioType(spreadsheetMLPackage, new Locale(analysis.getLanguage().getAlpha2()), factory);
		for (Scenario scenario : analysis.getScenarios()) {
			Row row = factory.createRow();
			for (int i = 0; i <= columns.length; i++) {
				if (row.getC().size() < i)
					row.getC().add(Context.smlObjectFactory.createCell());
			}
			setValue(row.getC().get(0), scenario.getName());
			setValue(row.getC().get(1), scenarioTypes.getOrDefault(scenario.getType().getName(), scenario.getType().getName()));
			setValue(row.getC().get(2), scenario.isAssetLinked() ? "Asset" : "Asset type");
			setValue(row.getC().get(3), scenario.isSelected());
			setValue(row.getC().get(4), scenario.getDescription());
			sheet.getRow().add(row);
		}
	}

	private Map<String, String> exportScenarioType(SpreadsheetMLPackage spreadsheetMLPackage, Locale locale, ObjectFactory factory) throws Exception {
		final String name = "ScenarioTypes";
		final ScenarioType[] scenarioTypes = ScenarioType.values();
		final WorksheetPart worksheetPart = createWorkSheetPart(spreadsheetMLPackage, name);
		final SheetData sheet = worksheetPart.getContents().getSheetData();
		createHeader(worksheetPart, name, new String[] { "Name", "Display name" }, scenarioTypes.length);
		final Map<String, String> names = new LinkedHashMap<>(scenarioTypes.length);
		for (ScenarioType scenarioType : ScenarioType.values()) {
			String value = messageSource.getMessage("label.scenario.type." + scenarioType.getName().replaceAll("-", "_").toLowerCase(), null, scenarioType.getName(), locale);
			names.put(scenarioType.getName(), value);
			final Row row = factory.createRow();
			setValue(row, 0, scenarioType.getName());
			setValue(row, 1, value);
			sheet.getRow().add(row);
		}
		return names;
	}

	private String[] generateActionPlanColumns(final int count, ActionPlanMode type, Locale locale) {
		int colIndex = 0;
		final String[] columns = new String[count];
		columns[colIndex++] = messageSource.getMessage("report.action_plan.norm", null, "Stds", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.reference", null, "Ref.", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.domain", null, "Domain", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.status", null, "ST", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.comment", null, "Comment", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.to_do", null, "To Do", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.responsible", null, "Resp.", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.implementation_rate", null, "IR(%)", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.internal.workload", null, "IS(md)", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.external.workload", null, "ES(md)", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.investment", null, "INV(k€)", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.life_time", null, "LT(y)", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.internal.maintenance", null, "IM(md)", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.external.maintenance", null, "EM(md)", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.recurrent.investment", null, "RINV(k€)", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.cost", null, "CS(k€)", locale);
		columns[colIndex++] = messageSource.getMessage("label.measure.phase", null, "Phase", locale);
		if (type == ActionPlanMode.APQ)
			columns[colIndex++] = messageSource.getMessage("report.action_plan.risk_count", null, "NR", locale);
		else if (type == ActionPlanMode.APPN) {
			columns[colIndex++] = messageSource.getMessage("report.action_plan.ale", null, "ALE", locale);
			columns[colIndex++] = messageSource.getMessage("report.action_plan.delta_ale", null, "Δ ALE", locale);
			columns[colIndex++] = messageSource.getMessage("report.action_plan.rosi", null, "ROSI", locale);
		}
		return columns;
	}

	private String[] getColumns(AnalysisStandard analysisStandard) {
		return analysisStandard instanceof MaturityStandard ? Constant.MATURITY_MEASURE_COLUMNS : Constant.NORMAL_MEASURE_COLUMNS;
	}

	/**
	 * Import full risk estimation: asset, risk scenario, risk estimation
	 * 
	 * @param asset
	 * @param scenario
	 * @param file
	 * @param request
	 * @param model
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	private String importRisEstimation(boolean asset, boolean scenario, MultipartFile file, HttpServletRequest request, Model model, HttpSession session, Principal principal,
			Locale locale) throws Exception {
		if (file.isEmpty())
			return JsonMessage.Error(messageSource.getMessage("error.file.empty", null, "File cannot be empty", locale));
		if (file.getSize() > maxUploadFileSize)
			return JsonMessage.Error(messageSource.getMessage("error.file.too.large", new Object[] { maxUploadFileSize }, "File is to large", locale));
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		File workFile = new File(request.getServletContext().getRealPath("/WEB-INF/tmp") + "/" + principal.getName() + "_" + System.nanoTime() + ".xslx");
		Worker worker = new WorkerImportEstimation(idAnalysis, principal.getName(), workFile, serviceTaskFeedback, workersPoolManager, sessionFactory, asset, scenario);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
		file.transferTo(workFile);
		executor.execute(worker);
		return asset ? JsonMessage.Success(messageSource.getMessage("success.start.import.asset", null, "Importing of assets data", locale))
				: scenario ? JsonMessage.Success(messageSource.getMessage("success.start.import.risk.scenario", null, "Importing of risk scenarios data", locale))
						: JsonMessage.Success(messageSource.getMessage("success.start.risk.estimation", null, "Importing of risk estimations data", locale));
	}

	private void prepareTableHeader(AnalysisStandard analysisStandard, WorksheetPart worksheetPart, String[] columns) throws Exception {
		createHeader(worksheetPart, "Measures" + analysisStandard.getStandard().getId(), columns, analysisStandard.getMeasures().size());
	}

	private void updateTokenCookie(HttpServletRequest request, HttpServletResponse response) {
		String token = request.getParameter("token");
		if (token != null) {
			Cookie cookie = new Cookie(token, "1");
			cookie.setMaxAge(120);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
	}

	private Row writeActionPLanData(Row row, int colCount, ActionPlanEntry actionPlanEntry, List<IProbabilityParameter> expressionParameters, Locale locale) {
		for (int i = 0; i < colCount; i++) {
			if (row.getC().size() < i)
				row.getC().add(Context.smlObjectFactory.createCell());
		}
		int colIndex = 0;
		Measure measure = actionPlanEntry.getMeasure();
		MeasureDescriptionText descriptionText = measure.getMeasureDescription().getMeasureDescriptionTextByAlpha3(locale.getISO3Language());
		setValue(row.getC().get(colIndex), measure.getAnalysisStandard().getStandard().getLabel());
		setValue(row.getC().get(++colIndex), measure.getMeasureDescription().getReference());
		setValue(row.getC().get(++colIndex), descriptionText.getDomain());
		setValue(row.getC().get(++colIndex), measure.getStatus());
		setValue(row.getC().get(++colIndex), measure.getComment());
		setValue(row.getC().get(++colIndex), measure.getToDo());
		setValue(row.getC().get(++colIndex), measure.getResponsible());
		setValue(row.getC().get(++colIndex), measure.getImplementationRateValue(expressionParameters));
		setValue(row.getC().get(++colIndex), measure.getInternalWL());
		setValue(row.getC().get(++colIndex), measure.getExternalWL());
		setValue(row.getC().get(++colIndex), measure.getInvestment() * 0.001);
		setValue(row.getC().get(++colIndex), measure.getLifetime());
		setValue(row.getC().get(++colIndex), measure.getInternalMaintenance());
		setValue(row.getC().get(++colIndex), measure.getExternalMaintenance());
		setValue(row.getC().get(++colIndex), measure.getRecurrentInvestment() * 0.001);
		setValue(row.getC().get(++colIndex), measure.getCost() * 0.001);
		setValue(row.getC().get(++colIndex), measure.getPhase().getNumber());
		if (actionPlanEntry.getActionPlanType().getActionPlanMode() == ActionPlanMode.APQ)
			setValue(row.getC().get(++colIndex), actionPlanEntry.getRiskCount());
		else {
			setValue(row.getC().get(++colIndex), actionPlanEntry.getTotalALE() * 0.001);
			setValue(row.getC().get(++colIndex), actionPlanEntry.getDeltaALE() * 0.001);
			setValue(row.getC().get(++colIndex), actionPlanEntry.getROI() * 0.001);
		}
		return row;
	}

	private int writeProbaImpact(Row row, int colIndex, Assessment assessment, List<ScaleType> scales, AnalysisType analysisType) {
		setValue(row, colIndex++, assessment.getLikelihood());
		for (ScaleType type : scales) {
			IValue value = assessment.getImpact(type.getName());
			if (value == null)
				setValue(row, colIndex++, 0);
			else if (type.getName().equals(Constant.DEFAULT_IMPACT_NAME))
				writeQuantitativeImpact(row, colIndex++, value);
			else
				setValue(row, colIndex++, "i" + value.getLevel());
		}
		return 1 + scales.size();
	}

	private int writeProbaImpact(Row row, int colIndex, RiskProbaImpact probaImpact, List<ScaleType> scales) {
		int columnCount = 1;
		if (probaImpact == null) {
			for (ScaleType type : scales) {
				if (!type.getName().equals(Constant.DEFAULT_IMPACT_NAME)) {
					setValue(row, colIndex++, 0);
					columnCount++;
				}
			}
		} else {
			setValue(row, colIndex++, probaImpact.getProbability() == null ? 0 : probaImpact.getProbability().getAcronym());
			for (ScaleType type : scales) {
				if (!type.getName().equals(Constant.DEFAULT_IMPACT_NAME)) {
					IImpactParameter parameter = probaImpact.get(type.getName());
					if (parameter == null)
						setValue(row, colIndex++, 0);
					else
						setValue(row, colIndex++, "i" + parameter.getLevel());
					columnCount++;
				}
			}
		}
		return columnCount;
	}

	private void writeQuantitativeImpact(Row row, int cellIndex, IValue impact) {
		if (impact == null)
			setValue(row, cellIndex, 0);
		else if (impact.getRaw() instanceof Double)
			setValue(row, cellIndex, impact.getReal() * 0.001);
		else
			setValue(row, cellIndex, impact.getVariable());
	}

}
