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
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.TablePart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.xlsx4j.sml.CTTable;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerImportEstimation;
import lu.itrust.business.TS.asynchronousWorkers.WorkerImportMeasureData;
import lu.itrust.business.TS.asynchronousWorkers.WorkerImportRiskInformation;
import lu.itrust.business.TS.asynchronousWorkers.WorkerSOAExport;
import lu.itrust.business.TS.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.component.MeasureManager;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.controller.form.DataManagerItem;
import lu.itrust.business.TS.controller.form.ImportRRFForm;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceStandard;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.CellRef;
import lu.itrust.business.TS.helper.Column;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.helper.RRFExportImport;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.analysis.helper.AnalysisComparator;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.parameter.IImpactParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.riskinformation.helper.RiskInformationComparator;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.MaturityStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;

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

	@Value("${app.settings.upload.file.max.size}")
	private Long maxUploadFileSize;

	@Value("${app.settings.risk.information.template.path}")
	private String brainstormingTemplate;

	@GetMapping(value = "/Export", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public String exportation(@RequestParam(name = "analysisId") Integer idAnalysis, Model model, HttpSession session, Principal principal, Locale locale) {
		List<DataManagerItem> items = new LinkedList<>();
		items.add(new DataManagerItem("action-plan-raw", "/Analysis/Data-manager/Action-plan-raw/Export-process"));
		items.add(new DataManagerItem("asset", "/Analysis/Data-manager/Asset/Export-process"));
		items.add(new DataManagerItem("risk-information", "/Analysis/Data-manager/Risk-information/Export-process"));
		items.add(new DataManagerItem("measure", "/Analysis/Data-manager/Measure/Import-form", "/Analysis/Data-manager/Measure/Export-process", null, null, null));
		items.add(new DataManagerItem("report", "/Analysis/Data-manager/Report/Import-form", "/Analysis/Data-manager/Report/Export-process"));
		items.add(new DataManagerItem("risk-estimation", "/Analysis/Data-manager/Risk-estimation/Export-process"));
		items.add(new DataManagerItem("rrf-raw", "/Analysis/Data-manager/RRF-RAW/Export-process"));
		items.add(new DataManagerItem("risk-register", "/Analysis/Data-manager/Risk-register/Export-form", "/Analysis/Data-manager/Risk-register/Export-process", null));
		items.add(new DataManagerItem("risk-sheet", "/Analysis/Data-manager/Risk-sheet/Export-form", "/Analysis/Data-manager/Risk-sheet/Export-process", null));
		items.add(new DataManagerItem("risk-sheet-raw", "/Analysis/Data-manager/Risk-sheet-raw/Import-form", "/Analysis/Data-manager/Risk-sheet-raw/Export-process", null));
		items.add(new DataManagerItem("scenario", "/Analysis/Data-manager/Scenario/Export-process", null));
		items.add(new DataManagerItem("soa", "/Analysis/Data-manager/SOA/Export-process", null));
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
		final boolean hiddenComment = analysis.getSetting(AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT);
		final boolean qualitative = analysis.isHybrid() || analysis.isQualitative();
		final boolean rowColumn = analysis.getSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN);
		final boolean uncertainty = analysis.isUncertainty();
		assessmentAndRiskProfileManager.updateAssessment(analysis, new ValueFactory(analysis.getParameters()));
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

	@GetMapping("/Measure/Export-process")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session,#principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportMeasureProcess(HttpServletRequest request, HttpServletResponse response, HttpSession session, Principal principal,
			Locale locale) throws Exception {
		Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.createPackage();
		final ValueFactory factory = new ValueFactory(analysis.getParameters());
		for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards()) {
			WorksheetPart worksheetPart = createWorkSheetPart(mlPackage, analysisStandard.getStandard().getLabel());
			exportMeasureStandard(factory, analysisStandard, mlPackage, worksheetPart);
		}
		response.setContentType("xlsx");
		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\"" + String.format("%s_v%s.xlsx", analysis.getLabel(), analysis.getVersion()) + "\"");
		mlPackage.save(response.getOutputStream());
		// Log
		TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.measure",
				String.format("Analysis: %s, version: %s, type: Measure data", analysis.getIdentifier(), analysis.getVersion()), principal.getName(), LogAction.EXPORT,
				analysis.getIdentifier(), analysis.getVersion());

	}

	@GetMapping("/Risk-information/Export-proccess")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public String exportRiskInformationProcess(HttpServletRequest request, HttpServletResponse response, HttpSession session, Principal principal, Locale locale) throws Exception {
		Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		Locale analysisLocale = new Locale(analysis.getLanguage().getAlpha2());
		Map<String, List<RiskInformation>> riskInformationMap = analysis.getRiskInformations().stream().map(riskInformation -> {
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

		File workFile = new File(request.getServletContext().getRealPath(String.format("/WEB-INF/tmp/TMP_Risk-information_%d_%d.xlsx", analysis.getId(), System.nanoTime())));

		try {
			FileUtils.copyFile(new File(request.getServletContext().getRealPath(brainstormingTemplate)), workFile);
			SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(workFile);
			WorkbookPart workbook = mlPackage.getWorkbookPart();
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

	///
	/// Import part.
	///

	@RequestMapping(value = "/RRF-Raw/Export-process", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportRRFRawProcess(Model model, HttpSession session, HttpServletResponse response, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		new RRFExportImport(serviceAssetType, serviceAnalysis, messageSource).exportRawRRF(serviceAnalysis.get(idAnalysis), response, principal.getName(), locale);
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
	@RequestMapping(value = "/SOA/Export-process", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody String exportSOAProcess(@RequestParam("idAnalysis") Integer idAnalysis, Principal principal, HttpServletRequest request, Model model, Locale locale)
			throws Exception {
		Worker worker = new WorkerSOAExport(principal.getName(), request.getServletContext().getRealPath("/WEB-INF"), idAnalysis, messageSource, serviceTaskFeedback,
				workersPoolManager, sessionFactory);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
		// execute task
		executor.execute(worker);
		return JsonMessage.Success(messageSource.getMessage("success.start.exporting.soa", null, "SOA exporting was successfully started", locale));
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
		List<DataManagerItem> items = new LinkedList<>();
		items.add(new DataManagerItem("asset", "/Analysis/Data-manager/Asset/Import-process", ".xls,.xlsx,.xlsm"));
		items.add(new DataManagerItem("risk-information", "/Analysis/Data-manager/Risk-information/Import-process", ".xls,.xlsx,.xlsm"));
		items.add(new DataManagerItem("measure", "/Analysis/Data-manager/Measure/Import-process", ".xls,.xlsx,.xlsm"));
		items.add(new DataManagerItem("risk-estimation", "/Analysis/Data-manager/Risk-estimation/Import-process", ".xls,.xlsx,.xlsm"));
		items.add(new DataManagerItem("scenario", "/Analysis/Data-manager/Scenario/Import-process", ".xls,.xlsx,.xlsm"));
		items.add(new DataManagerItem("rrf", "/Analysis/Data-manager/RRF/Import-form",null, null));
		model.addAttribute("items", items);
		model.addAttribute("maxFileSize", maxUploadFileSize);
		return "analyses/single/components/data-manager/import";
	}

	@PostMapping(value = "/Risk-estimation/Import-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String importEstimationProcess(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		if (file.isEmpty())
			return JsonMessage.Error(messageSource.getMessage("error.file.empty", null, "File cannot be empty", locale));
		if (file.getSize() > maxUploadFileSize)
			return JsonMessage.Error(messageSource.getMessage("error.file.too.large", new Object[] { maxUploadFileSize }, "File is to large", locale));
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		File workFile = new File(request.getServletContext().getRealPath("/WEB-INF/tmp") + "/" + principal.getName() + "_" + System.nanoTime() + ".xslx");
		Worker worker = new WorkerImportEstimation(idAnalysis, principal.getName(), workFile, serviceTaskFeedback, workersPoolManager, sessionFactory);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
		file.transferTo(workFile);
		executor.execute(worker);
		return JsonMessage.Success(messageSource.getMessage("success.start.risk.estimation", null, "Importing of risk estimation data", locale));
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

	private String[] createTableHeader(List<ScaleType> scales, WorkbookPart workbook, boolean qualitative, boolean hiddenComment, boolean rowColumn, boolean uncertainty) {
		List<Column> columns = WorkerImportEstimation.generateColumns(scales, qualitative, hiddenComment, rowColumn, uncertainty);
		String[] result = new String[columns.size()];
		for (int i = 0; i < columns.size(); i++)
			result[i] = columns.get(i).getName();
		return result;
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

	private String[] getColumns(AnalysisStandard analysisStandard) {
		return analysisStandard instanceof MaturityStandard ? Constant.MATURITY_MEASURE_COLUMNS : Constant.NORMAL_MEASURE_COLUMNS;
	}

	private void prepareTableHeader(AnalysisStandard analysisStandard, WorksheetPart worksheetPart, String[] columns) throws Exception {
		createHeader(worksheetPart, "Measures" + analysisStandard.getStandard().getId(), columns, analysisStandard.getMeasures().size());
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
