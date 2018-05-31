package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.ROLE_MIN_USER;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.createHeader;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.createWorkSheetPart;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getRow;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.io.File;
import java.security.Principal;
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
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerImportEstimation;
import lu.itrust.business.TS.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.helper.Column;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.parameter.IImpactParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.scale.ScaleType;
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
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	@Autowired
	private TaskExecutor executor;

	@Value("${app.settings.upload.file.max.size}")
	private Long maxUploadFileSize;

	@GetMapping(value = "/Import", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String importManager(Model model, HttpSession session, Locale locale) {
		return "analyses/single/components/data-manager/import";
	}

	@GetMapping(value = "/Export", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public String exportManager(HttpSession session, Locale locale) {
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

	@GetMapping(value = "/Risk-estimation/Import-form", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String importEstimationForm(Model model, HttpSession session, Principal principal, Locale locale) {
		return "analyses/single/components/data-manager/import/risk-estimation";
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
		return JsonMessage.Success(messageSource.getMessage("success.start.risk.estimation.data", null, "Importing of risk estimation data", locale));
	}

	private String[] createTableHeader(List<ScaleType> scales, WorkbookPart workbook, boolean qualitative, boolean hiddenComment, boolean rowColumn, boolean uncertainty) {
		List<Column> columns = WorkerImportEstimation.generateColumns(scales, qualitative, hiddenComment, rowColumn, uncertainty);
		String[] result = new String[columns.size()];
		for (int i = 0; i < columns.size(); i++)
			result[i] = columns.get(i).getName();
		return result;
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
				setValue(row, colIndex++, "i" + value.getLevel());
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
				if (type.getName().equals(Constant.DEFAULT_IMPACT_NAME))
					setValue(row, colIndex++, Constant.MEASURE_STATUS_NOT_APPLICABLE);
				else {
					IImpactParameter parameter = probaImpact.get(type.getName());
					if (parameter == null)
						setValue(row, colIndex++, 0);
					else
						setValue(row, colIndex++, "i" + parameter.getLevel());
				}
			}
		}
		return 1 + scales.size();
	}

}
