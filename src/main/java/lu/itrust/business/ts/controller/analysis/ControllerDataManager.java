package lu.itrust.business.ts.controller.analysis;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.ts.constants.Constant.APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.ts.constants.Constant.RI_SHEET_MAPPERS;
import static lu.itrust.business.ts.constants.Constant.ROLE_MIN_USER;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.createHeader;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.createRow;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.createWorkSheetPart;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findTable;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getExtension;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getOrCreateCell;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getOrCreateRow;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getWorksheetPart;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.setFormula;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;
import static lu.itrust.business.ts.helper.InstanceManager.loadTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.TablePart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.unit.DataSize;
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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.xml.bind.JAXBException;
import lu.itrust.business.ts.asynchronousWorkers.Worker;
import lu.itrust.business.ts.asynchronousWorkers.WorkerAnalysisImport;
import lu.itrust.business.ts.asynchronousWorkers.WorkerExportAnalysis;
import lu.itrust.business.ts.asynchronousWorkers.WorkerExportRiskRegister;
import lu.itrust.business.ts.asynchronousWorkers.WorkerExportRiskSheet;
import lu.itrust.business.ts.asynchronousWorkers.WorkerExportWordReport;
import lu.itrust.business.ts.asynchronousWorkers.WorkerImportEstimation;
import lu.itrust.business.ts.asynchronousWorkers.WorkerImportItemInformation;
import lu.itrust.business.ts.asynchronousWorkers.WorkerImportMeasureData;
import lu.itrust.business.ts.asynchronousWorkers.WorkerImportRiskInformation;
import lu.itrust.business.ts.asynchronousWorkers.WorkerSOAExport;
import lu.itrust.business.ts.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.ts.component.DefaultTemplateLoader;
import lu.itrust.business.ts.component.MeasureManager;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceAnalysis;
import lu.itrust.business.ts.database.service.ServiceAssessment;
import lu.itrust.business.ts.database.service.ServiceAssetType;
import lu.itrust.business.ts.database.service.ServiceAssetTypeValue;
import lu.itrust.business.ts.database.service.ServiceCustomer;
import lu.itrust.business.ts.database.service.ServiceImpactParameter;
import lu.itrust.business.ts.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.ts.database.service.ServiceRiskAcceptanceParameter;
import lu.itrust.business.ts.database.service.ServiceScaleType;
import lu.itrust.business.ts.database.service.ServiceSimpleParameter;
import lu.itrust.business.ts.database.service.ServiceStandard;
import lu.itrust.business.ts.database.service.ServiceStorage;
import lu.itrust.business.ts.database.service.ServiceTaskFeedback;
import lu.itrust.business.ts.database.service.ServiceTrickTemplate;
import lu.itrust.business.ts.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.exportation.word.ExportReport;
import lu.itrust.business.ts.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.CellRef;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper;
import lu.itrust.business.ts.form.CSSFExportForm;
import lu.itrust.business.ts.form.DataManagerItem;
import lu.itrust.business.ts.form.ExportWordReportForm;
import lu.itrust.business.ts.form.ImportAnalysisForm;
import lu.itrust.business.ts.form.ImportRRFForm;
import lu.itrust.business.ts.helper.Column;
import lu.itrust.business.ts.helper.DependencyGraphManager;
import lu.itrust.business.ts.helper.ILRExport;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.helper.RRFExportImport;
import lu.itrust.business.ts.model.actionplan.ActionPlanEntry;
import lu.itrust.business.ts.model.actionplan.ActionPlanMode;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisSetting;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.analysis.ExportFileName;
import lu.itrust.business.ts.model.analysis.helper.AnalysisComparator;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.asset.AssetType;
import lu.itrust.business.ts.model.cssf.RiskProbaImpact;
import lu.itrust.business.ts.model.cssf.RiskProfile;
import lu.itrust.business.ts.model.cssf.RiskRegisterItem;
import lu.itrust.business.ts.model.cssf.RiskStrategy;
import lu.itrust.business.ts.model.cssf.helper.CSSFFilter;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplate;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplateType;
import lu.itrust.business.ts.model.general.helper.Utils;
import lu.itrust.business.ts.model.ilr.AssetImpact;
import lu.itrust.business.ts.model.ilr.AssetNode;
import lu.itrust.business.ts.model.ilr.ILRImpact;
import lu.itrust.business.ts.model.iteminformation.helper.ComparatorItemInformation;
import lu.itrust.business.ts.model.parameter.IAcronymParameter;
import lu.itrust.business.ts.model.parameter.IBoundedParameter;
import lu.itrust.business.ts.model.parameter.IImpactParameter;
import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.ts.model.parameter.value.IValue;
import lu.itrust.business.ts.model.riskinformation.RiskInformation;
import lu.itrust.business.ts.model.riskinformation.helper.RiskInformationComparator;
import lu.itrust.business.ts.model.scale.ScaleType;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.scenario.ScenarioType;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.MaturityStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;

/**
 * This class is a controller that handles data management operations for the
 * Analysis module.
 * It provides endpoints for exporting various types of data related to an
 * analysis, such as action plans, assets, risk information, measures, reports,
 * etc.
 * The exported data is in Excel format.
 * This controller requires the user to have the necessary authorization to
 * perform the export operations.
 */
@PreAuthorize(ROLE_MIN_USER)
@Controller
@RequestMapping("/Analysis/Data-manager")
public class ControllerDataManager {

	/**
	 *
	 */
	private static final String ANALYSIS_DATA_MANAGER_RISK_ESTIMATION_EXPORT_PROCESS = "/Analysis/Data-manager/Risk-estimation/Export-process";

	/**
	 *
	 */
	private static final String ANALYSIS_DATA_MANAGER_REPORT_EXPORT_PROCESS = "/Analysis/Data-manager/Report/Export-process";

	/**
	 *
	 */
	private static final String ANALYSIS_DATA_MANAGER_RISK_SHEET_EXPORT_FORM = "/Analysis/Data-manager/Risk-sheet/Export-form";

	/**
	 *
	 */
	private static final String ANALYSIS_DATA_MANAGER_RISK_SHEET_EXPORT_PROCESS = "/Analysis/Data-manager/Risk-sheet/Export-process";

	private static int ASSET_HIDDEN_COMMENT_CELL_INDEX = 5;

	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	@Value("${app.settings.excel.default.table.style}")
	private String defaultExcelTableStyle;

	@Value("${app.settings.excel.header.footer.sheet.name}")
	private String headerFooterSheetName;

	@Autowired
	private DefaultTemplateLoader defaultTemplateLoader;

	@Autowired
	private TaskExecutor executor;

	private long maxRefurbishReportSize;

	private Long maxUploadFileSize;

	@Autowired
	private MeasureManager measureManager;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAssessment serviceAssessment;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceImpactParameter serviceImpactParameter;

	@Autowired
	private ServiceLikelihoodParameter serviceLikelihoodParameter;

	@Autowired
	private ServiceTrickTemplate serviceTrickTemplate;

	@Autowired
	private ServiceRiskAcceptanceParameter serviceRiskAcceptanceParameter;

	@Autowired
	private ServiceScaleType serviceScaleType;

	@Autowired
	private ServiceSimpleParameter serviceSimpleParameter;

	@Autowired
	private ServiceStandard serviceStandard;

	@Autowired
	private ServiceStorage serviceStorage;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	@Autowired
	private ServiceAssetTypeValue serviceAssetTypeValue;

	/**
	 * Exports the raw action plan process as an Excel file.
	 *
	 * @param request   the HttpServletRequest object
	 * @param response  the HttpServletResponse object
	 * @param session   the HttpSession object
	 * @param principal the Principal object
	 * @param locale    the Locale object
	 * @throws Exception if an error occurs during the export process
	 */
	@GetMapping(value = "/Action-plan-raw/Export-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportActionPlanRawProcess(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Principal principal, Locale locale) throws Exception {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final File file = loadTemplate(analysis.getCustomer(), TrickTemplateType.DEFAULT_EXCEL, analysis.getLanguage());
		try {
			final SpreadsheetMLPackage spreadsheetMLPackage = SpreadsheetMLPackage.load(file);
			exportRawActionPlan(analysis, spreadsheetMLPackage, new Locale(analysis.getLanguage().getAlpha2()));
			final String extension = getExtension(spreadsheetMLPackage);
			response.setContentType(extension);
			// set response header with location of the filename
			final String filename = String.format(Constant.ITR_FILE_NAMING,
					Utils.cleanUpFileName(analysis.findSetting(ExportFileName.ACTION_PLAN)),
					Utils.cleanUpFileName(analysis.getCustomer().getOrganisation()),
					Utils.cleanUpFileName(analysis.getLabel()), "ActionPlan", analysis.getVersion(),
					extension);
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ filename + "\"");
			updateTokenCookie(request, response);
			spreadsheetMLPackage.save(response.getOutputStream());
			// Log
			TrickLogManager.persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.raw.action_plan",
					String.format("Analysis: %s, version: %s, type: Raw action plan", analysis.getIdentifier(),
							analysis.getVersion()),
					principal.getName(), LogAction.EXPORT,
					analysis.getIdentifier(), analysis.getVersion());
		} finally {
			serviceStorage.delete(file.getAbsolutePath());
		}

	}

	/**
	 * Exports the asset process for a given analysis.
	 *
	 * @param request   the HTTP servlet request
	 * @param response  the HTTP servlet response
	 * @param session   the HTTP session
	 * @param principal the principal object representing the user
	 * @param locale    the locale of the analysis
	 * @throws Exception if an error occurs during the export process
	 */
	@GetMapping(value = "/Asset/Export-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportAssetProcess(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final File file = loadTemplate(analysis.getCustomer(), TrickTemplateType.DEFAULT_EXCEL, analysis.getLanguage());
		try {
			final SpreadsheetMLPackage spreadsheetMLPackage = SpreadsheetMLPackage.load(file);
			final String extension = getExtension(spreadsheetMLPackage);
			exportAsset(analysis, spreadsheetMLPackage);
			response.setContentType(extension);
			final String filename = String.format(Constant.ITR_FILE_NAMING,
					Utils.cleanUpFileName(analysis.findSetting(ExportFileName.ASSET)),
					Utils.cleanUpFileName(analysis.getCustomer().getOrganisation()),
					Utils.cleanUpFileName(analysis.getLabel()), "Asset", analysis.getVersion(),
					extension);
			// set response header with location of the filename
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ filename + "\"");
			updateTokenCookie(request, response);
			spreadsheetMLPackage.save(response.getOutputStream());
			// Log
			TrickLogManager.persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.asset",
					String.format("Analysis: %s, version: %s, type: Asset", analysis.getIdentifier(),
							analysis.getVersion()),
					principal.getName(), LogAction.EXPORT,
					analysis.getIdentifier(), analysis.getVersion());
		} finally {
			serviceStorage.delete(file.getAbsolutePath());
		}
	}

	/**
	 * Exports the asset process for a given analysis.
	 *
	 * @param request   the HTTP servlet request
	 * @param response  the HTTP servlet response
	 * @param session   the HTTP session
	 * @param principal the principal object representing the user
	 * @param locale    the locale of the analysis
	 * @throws Docx4JException
	 * @throws JAXBException
	 * @throws IOException
	 * @throws Exception       if an error occurs during the export process
	 */
	@GetMapping(value = "/Item-information/Export-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportItemInformationProcess(HttpServletRequest request, HttpServletResponse response,
			HttpSession session,
			Principal principal, Locale locale) throws Docx4JException, JAXBException, IOException {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final File file = loadTemplate(analysis.getCustomer(), TrickTemplateType.DEFAULT_EXCEL, analysis.getLanguage());
		try {
			final SpreadsheetMLPackage spreadsheetMLPackage = SpreadsheetMLPackage.load(file);
			final String extension = getExtension(spreadsheetMLPackage);
			exportScope(analysis, spreadsheetMLPackage);
			response.setContentType(extension);
			final String filename = String.format(Constant.ITR_FILE_NAMING,
					Utils.cleanUpFileName(analysis.findSetting(ExportFileName.SCOPE)),
					Utils.cleanUpFileName(analysis.getCustomer().getOrganisation()),
					Utils.cleanUpFileName(analysis.getLabel()), "Scope", analysis.getVersion(),
					extension);
			// set response header with location of the filename
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ filename + "\"");
			updateTokenCookie(request, response);
			spreadsheetMLPackage.save(response.getOutputStream());
			// Log
			TrickLogManager.persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.scope",
					String.format("Analysis: %s, version: %s, type: Scope", analysis.getIdentifier(),
							analysis.getVersion()),
					principal.getName(), LogAction.EXPORT,
					analysis.getIdentifier(), analysis.getVersion());
		} finally {
			serviceStorage.delete(file.getAbsolutePath());
		}
	}

	/**
	 * Exportation method that returns a String.
	 * This method exports data related to the analysis identified by the given
	 * analysisId.
	 * The exported data includes action plans, assets, risk information, measures,
	 * word reports,
	 * risk estimations, risk registers, risk sheets, RRF raw data, scenarios, SOA
	 * data, and ILR data.
	 * The exported data is added to the model and the maximum file size for upload
	 * is also added to the model.
	 * The method returns the name of the JSP file that will be used to render the
	 * export view.
	 *
	 * @param idAnalysis The ID of the analysis to export.
	 * @param model      The model to add the exported data and maximum file size.
	 * @param session    The HttpSession object.
	 * @param principal  The Principal object representing the currently
	 *                   authenticated user.
	 * @param locale     The Locale object representing the user's preferred
	 *                   language.
	 * @return The name of the JSP file that will be used to render the export view.
	 */
	@GetMapping(value = "/Export", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public String exportation(@RequestParam(name = "analysisId") Integer idAnalysis, Model model, HttpSession session,
			Principal principal, Locale locale) {
		final Analysis analysis = serviceAnalysis.findByIdAndEager(idAnalysis);
		final boolean isILR = Analysis.isILR(analysis);
		final List<DataManagerItem> items = new LinkedList<>();
		if (!analysis.getActionPlans().isEmpty())
			items.add(new DataManagerItem("action-plan-raw", "/Analysis/Data-manager/Action-plan-raw/Export-process"));
		items.add(new DataManagerItem("asset", "/Analysis/Data-manager/Asset/Export-process"));
		items.add(new DataManagerItem("item-information", "/Analysis/Data-manager/Item-information/Export-process"));
		items.add(new DataManagerItem("risk-information", "/Analysis/Data-manager/Risk-information/Export-process"));
		if (!analysis.getAnalysisStandards().isEmpty())
			items.add(new DataManagerItem("measure", "/Analysis/Data-manager/Measure/Export-form", null, null));
		items.add(new DataManagerItem("word-report", "/Analysis/Data-manager/Report/Export-form",
				ANALYSIS_DATA_MANAGER_REPORT_EXPORT_PROCESS, null));
		if (!analysis.getAssessments().isEmpty()) {
			items.add(new DataManagerItem("risk-estimation",
					"/Analysis/Data-manager/Risk-estimation/Export-form",
					ANALYSIS_DATA_MANAGER_RISK_ESTIMATION_EXPORT_PROCESS, ".csv"));
		}
		if (analysis.isQualitative()) {
			if (!analysis.getRiskRegisters().isEmpty())
				items.add(new DataManagerItem("risk-register", "/Analysis/Data-manager/Risk-register/Export-process",
						true));
			if (!analysis.getRiskProfiles().isEmpty())
				items.add(new DataManagerItem("risk-sheet", ANALYSIS_DATA_MANAGER_RISK_SHEET_EXPORT_FORM,
						ANALYSIS_DATA_MANAGER_RISK_SHEET_EXPORT_PROCESS, null));
		}

		if (analysis.isQuantitative())
			items.add(new DataManagerItem("rrf-raw", "/Analysis/Data-manager/RRF-Raw/Export-process"));
		items.add(new DataManagerItem("scenario", "/Analysis/Data-manager/Scenario/Export-process"));

		if (analysis.getAnalysisStandards().values().stream().anyMatch(AnalysisStandard::isSoaEnabled))
			items.add(new DataManagerItem("soa", "/Analysis/Data-manager/SOA/Export-process", true));

		if (isILR)
			items.add(new DataManagerItem("ilr", "/Analysis/Data-manager/ILR/Export-form",
					"/Analysis/Data-manager/ILR/Export-process", ".json;.csv"));

		items.sort((i1, i2) -> NaturalOrderComparator.compareTo(
				messageSource.getMessage("label.menu.data_manager.export." + i1.getName().replaceAll("-", "_"), null,
						locale),
				messageSource.getMessage("label.menu.data_manager.export." + i2.getName().replaceAll("-", "_"), null,
						locale)));
		model.addAttribute("items", items);
		model.addAttribute("maxFileSize", maxUploadFileSize);
		return "jsp/analyses/single/components/data-manager/export";
	}

	/**
	 * Export the ILR form.
	 *
	 * @param model     the model object
	 * @param session   the HttpSession object
	 * @param principal the Principal object
	 * @param locale    the Locale object
	 * @return the view name for the ILR export form
	 */
	@GetMapping(value = "/ILR/Export-form", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public String exportILRForm(Model model, HttpSession session, Principal principal, Locale locale) {
		model.addAttribute("item", new DataManagerItem("ilr", "/Analysis/Data-manager/ILR/Export-process"));
		model.addAttribute("maxFileSize", maxUploadFileSize);
		return "jsp/analyses/single/components/data-manager/export/ilr";
	}

	/**
	 * Retrieves the export risk estimation form.
	 *
	 * @param model     the model object to be populated with data
	 * @param session   the HttpSession object
	 * @param principal the Principal object representing the currently
	 *                  authenticated user
	 * @param locale    the Locale object representing the user's preferred language
	 * @return the name of the JSP file to be rendered
	 */
	@GetMapping(value = "/Risk-estimation/Export-form", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public String exportRiskEstimationForm(Model model, HttpSession session, Principal principal, Locale locale) {
		model.addAttribute("item",
				new DataManagerItem("risk-estimation", ANALYSIS_DATA_MANAGER_RISK_ESTIMATION_EXPORT_PROCESS));
		model.addAttribute("maxFileSize", maxUploadFileSize);
		return "jsp/analyses/single/components/data-manager/export/risk-estimation";
	}

	/**
	 * Exports the ILR process data.
	 *
	 * @param ilrData     The ILR data file to be exported.
	 * @param mappingFile The mapping file (optional) used for exporting the ILR
	 *                    data.
	 * @param request     The HTTP servlet request.
	 * @param response    The HTTP servlet response.
	 * @param session     The HTTP session.
	 * @param principal   The principal object representing the authenticated user.
	 * @param locale      The locale for the export operation.
	 * @throws Exception If an error occurs during the export process.
	 */
	@PostMapping(value = "/ILR/Export-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal,T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportILRProcess(@RequestParam(value = "ilrData") MultipartFile ilrData,
			@RequestParam(value = "mappingFile", required = false) MultipartFile mappingFile,
			HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Principal principal, Locale locale) throws Exception {

		final File data = serviceStorage.createTmpFile();
		final File mapping = serviceStorage.createTmpFile();
		try {
			data.deleteOnExit();
			mapping.deleteOnExit();

			serviceStorage.store(ilrData, data.getName());
			if (mappingFile != null && mappingFile.getSize() > 0)
				serviceStorage.store(mappingFile, mapping.getName());

			final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			final Analysis analysis = serviceAnalysis.findByIdAndEager(idAnalysis);
			final List<ScaleType> scales = serviceScaleType.findAll();
			new ILRExport().exportILRData(analysis, scales, data, mapping);

			response.setContentType("json");
			final String filename = String.format(Constant.ITR_FILE_NAMING,
					Utils.cleanUpFileName(analysis.findSetting(ExportFileName.ILR_DATA)),
					Utils.cleanUpFileName(analysis.getCustomer().getOrganisation()),
					Utils.cleanUpFileName(analysis.getLabel()), "ILR-Data", analysis.getVersion(),
					"json");
			// set response header with location of the filename
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + filename + "\"");
			updateTokenCookie(request, response);

			Files.copy(data.toPath(), response.getOutputStream());

			// Log
			TrickLogManager.persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.ilr",
					String.format("Analysis: %s, version: %s, type: ILR", analysis.getIdentifier(),
							analysis.getVersion()),
					principal.getName(), LogAction.EXPORT,
					analysis.getIdentifier(), analysis.getVersion());

		} finally {
			serviceStorage.delete(data.getAbsolutePath());
			serviceStorage.delete(mapping.getAbsolutePath());
		}

	}

	/**
	 * Exports the estimation process for risk analysis.
	 * This method handles the export of the risk estimation process in Excel
	 * format.
	 *
	 * @param extrasFormula The extras formula file (optional).
	 * @param request       The HTTP servlet request.
	 * @param response      The HTTP servlet response.
	 * @param session       The HTTP session.
	 * @param locale        The locale.
	 * @param principal     The principal.
	 * @throws Exception If an error occurs during the export process.
	 */
	@RequestMapping(value = "/Risk-estimation/Export-process", method = { RequestMethod.GET,
			RequestMethod.POST }, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportEstimationProcess(
			@RequestParam(value = "extrasFormula", required = false) MultipartFile extrasFormula,
			HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Locale locale, Principal principal) throws Exception {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Analysis analysis = serviceAnalysis.get(idAnalysis);
		final File file = loadTemplate(analysis.getCustomer(), TrickTemplateType.DEFAULT_EXCEL, analysis.getLanguage());
		try {
			final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(file);
			final WorksheetPart worksheetPart = createWorkSheetPart(mlPackage, "Risk estimation");
			final SheetData sheetData = worksheetPart.getContents().getSheetData();
			final boolean isILR = Analysis.isILR(analysis);
			final boolean hiddenComment = analysis.findSetting(AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT);
			final boolean rowColumn = analysis.findSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN);
			final boolean uncertainty = analysis.isUncertainty();
			final ValueFactory factory = new ValueFactory(analysis.getParameters());

			assessmentAndRiskProfileManager.updateAssessment(analysis, factory);

			serviceAnalysis.saveOrUpdate(analysis);

			final Map<String, String> extrasColumns = loadExtrasColumns(extrasFormula);

			System.out.println(extrasColumns);

			final List<ScaleType> scales = analysis.findImpacts();

			final String[] columns = createTableHeader(scales, extrasColumns.keySet(), analysis.getType(), isILR,
					hiddenComment,
					rowColumn,
					uncertainty);

			createHeader(worksheetPart, "Risk_estimation", defaultExcelTableStyle, columns,
					analysis.getAssessments().size());

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

			generateRiskEstimation(analysis, sheetData, hiddenComment, isILR, rowColumn, uncertainty,
					extrasColumns, scales, columns, locale);

			ExcelHelper.applyHeaderAndFooter(headerFooterSheetName, "Risk estimation", mlPackage);

			exportAsset(analysis, mlPackage, hiddenComment, isILR);

			exportScenario(analysis, mlPackage);

			// Dependancy
			if (isILR) {
				exportDependancy(analysis, mlPackage, locale);
				exportProbability(analysis, mlPackage);
			}

			final String extension = getExtension(mlPackage);

			response.setContentType(extension);

			final String filename = String.format(Constant.ITR_FILE_NAMING,
					Utils.cleanUpFileName(analysis.findSetting(ExportFileName.RISK_ESTIMATION)),
					Utils.cleanUpFileName(analysis.getCustomer().getOrganisation()),
					Utils.cleanUpFileName(analysis.getLabel()), "RiskEstimation", analysis.getVersion(),
					extension);
			// set response header with location of the filename
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + filename + "\"");
			updateTokenCookie(request, response);
			mlPackage.save(response.getOutputStream());

			// Log
			TrickLogManager.persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.risk.estimation",
					String.format("Analysis: %s, version: %s, type: Risk estimation", analysis.getIdentifier(),
							analysis.getVersion()),
					principal.getName(), LogAction.EXPORT,
					analysis.getIdentifier(), analysis.getVersion());
		} finally {
			serviceStorage.delete(file.getAbsolutePath());
		}

	}

	@GetMapping("/Measure/Export-form")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session,#principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public String exportMeasureForm(Model model, HttpSession session, Principal principal, Locale locale)
			throws Exception {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		model.addAttribute("standards", analysis.findStandards());
		model.addAttribute("item", new DataManagerItem("measure", "/Analysis/Data-manager/Measure/Export-process"));
		return "jsp/analyses/single/components/data-manager/export/measure";
	}

	@PostMapping(value = "/Measure/Export-process")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session,#principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportMeasureProcess(@RequestParam(name = "standards") List<Integer> standards,
			HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final File file = loadTemplate(analysis.getCustomer(), TrickTemplateType.DEFAULT_EXCEL, analysis.getLanguage());
		try {
			final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(file);
			final ValueFactory factory = new ValueFactory(analysis.getParameters());
			for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards().values()) {
				if (standards.contains(analysisStandard.getStandard().getId())) {
					final WorksheetPart worksheetPart = createWorkSheetPart(mlPackage,
							analysisStandard.getStandard().getName());
					exportMeasureStandard(factory, analysisStandard, worksheetPart);
					ExcelHelper.applyHeaderAndFooter(headerFooterSheetName, analysisStandard.getStandard().getName(),
							mlPackage);
				}
			}
			final String extension = getExtension(mlPackage);
			response.setContentType(extension);

			final String filename = String.format(Constant.ITR_FILE_NAMING,
					Utils.cleanUpFileName(analysis.findSetting(ExportFileName.MEASURE_COLLECTION)),
					Utils.cleanUpFileName(analysis.getCustomer().getOrganisation()),
					Utils.cleanUpFileName(analysis.getLabel()), "MeasureCollection", analysis.getVersion(),
					extension);
			// set response header with location of the filename
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ filename + "\"");
			updateTokenCookie(request, response);
			mlPackage.save(response.getOutputStream());
			// Log
			TrickLogManager.persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.measure",
					String.format("Analysis: %s, version: %s, type: Measure data", analysis.getIdentifier(),
							analysis.getVersion()),
					principal.getName(), LogAction.EXPORT,
					analysis.getIdentifier(), analysis.getVersion());
		} finally {
			serviceStorage.delete(file.getAbsolutePath());
		}
	}

	@GetMapping(value = "/Report/Export-form/{analysisId}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public String exportReportForm(@PathVariable Integer analysisId, Principal principal, Model model, Locale locale) {
		final Analysis analysis = serviceAnalysis.get(analysisId);
		final List<TrickTemplate> templates = defaultTemplateLoader
				.findReportByTypeAndLanguage(analysis.getType(), analysis.getLanguage().getAlpha3());
		final Map<String, String> versions = templates.stream()
				.collect(Collectors.toMap(TrickTemplate::getKey, TrickTemplate::getVersion));
		analysis.getCustomer().getTemplates().stream()
				.filter(p -> p.getType() == TrickTemplateType.REPORT && analysis.getLanguage().equals(p.getLanguage())
						&& (analysis.getType().isHybrid() || analysis.getType() == p.getAnalysisType()))
				.sorted((p1, p2) -> NaturalOrderComparator.compareTo(p1.getVersion(), p2.getVersion())).forEach(p -> {
					templates.add(p);
					if (!p.getVersion().equalsIgnoreCase(versions.get(p.getKey())))
						p.setOutToDate(true);
				});

		if (analysis.isHybrid())
			model.addAttribute("types", AnalysisType.values());
		model.addAttribute("analysis", analysis);
		model.addAttribute("templates", templates);
		model.addAttribute("maxFileSize", Math.min(maxUploadFileSize, maxRefurbishReportSize));
		return "jsp/analyses/single/components/data-manager/export/report/modal";
	}

	@GetMapping(value = "/Report/Export-form", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public String exportReportForm(Model model, HttpSession session, Principal principal, Locale locale) {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final List<TrickTemplate> templates = new ArrayList<>(defaultTemplateLoader
				.findReportByTypeAndLanguage(analysis.getType(), analysis.getLanguage().getAlpha3()));

		final Map<String, String> versions = templates.stream()
				.collect(Collectors.toMap(TrickTemplate::getKey, TrickTemplate::getVersion));

		analysis.getCustomer().getTemplates().stream()
				.filter(p -> p.getType() == TrickTemplateType.REPORT && analysis.getLanguage().equals(p.getLanguage())
						&& (analysis.getType().isHybrid() || analysis.getType() == p.getAnalysisType()))
				.sorted((p1, p2) -> NaturalOrderComparator.compareTo(p1.getVersion(), p2.getVersion())).forEach(p -> {
					templates.add(p);
					if (!p.getVersion().equalsIgnoreCase(versions.get(p.getKey())))
						p.setOutToDate(true);
				});

		if (analysis.isHybrid())
			model.addAttribute("items",
					new DataManagerItem[] {
							new DataManagerItem(AnalysisType.QUANTITATIVE.name(),
									ANALYSIS_DATA_MANAGER_REPORT_EXPORT_PROCESS, ".docx"),
							new DataManagerItem(AnalysisType.HYBRID.name(),
									ANALYSIS_DATA_MANAGER_REPORT_EXPORT_PROCESS, ".docx"),
							new DataManagerItem(AnalysisType.QUALITATIVE.name(),
									ANALYSIS_DATA_MANAGER_REPORT_EXPORT_PROCESS, ".docx") });
		else
			model.addAttribute("item", new DataManagerItem(analysis.getType().name(),
					ANALYSIS_DATA_MANAGER_REPORT_EXPORT_PROCESS, ".docx"));

		model.addAttribute("analysis", analysis);
		model.addAttribute("templates", templates);
		model.addAttribute("maxFileSize", Math.min(maxUploadFileSize, maxRefurbishReportSize));
		return "jsp/analyses/single/components/data-manager/export/report/home";
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody String exportReportProcess(@RequestParam(name = "analysis") Integer analysisId,
			@ModelAttribute ExportWordReportForm form, HttpServletRequest request,
			Principal principal, Locale locale) throws Exception {
		try {
			final Integer customerId = serviceAnalysis.getCustomerIdByIdAnalysis(analysisId);
			if (form.isInternal()) {
				if (!serviceTrickTemplate.isUseAuthorised(form.getTemplate(), customerId))
					throw new AccessDeniedException(
							messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));
			} else if (form.getFile() == null)
				return JsonMessage.error(
						messageSource.getMessage("error.export.report.file.empty", null, "No file selected", locale));

			if (AnalysisType.isQualitative(serviceAnalysis.getAnalysisTypeById(analysisId))
					&& !serviceRiskAcceptanceParameter.existsByAnalysisId(analysisId))
				throw new TrickException("error.export.risk.acceptance.empty",
						"Please update risk acception settings: Analysis -> Parameter -> Risk acceptance");

			if (!form.isInternal()) {
				final long maxSize = Math.min(maxUploadFileSize, maxRefurbishReportSize);
				if (form.getFile().getSize() > maxSize)
					return JsonMessage.error(messageSource.getMessage("error.file.too.large", new Object[] { maxSize },
							"File is to large", locale));
				if (!DefaultTemplateLoader.isDocx(form.getFile().getInputStream()))
					return JsonMessage.error(
							messageSource.getMessage("error.file.no.docx", null, "Docx file is excepted", locale));
			}

			final ExportReport exportAnalysisReport = new Docx4jReportImpl();
			final Worker worker = new WorkerExportWordReport(analysisId, form.getTemplate(), principal.getName(),
					exportAnalysisReport);
			if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
				return JsonMessage.error(messageSource.getMessage("error.task_manager.too.many", null,
						"Too many tasks running in background", locale));
			if (!form.isInternal()) {
				((Docx4jReportImpl) exportAnalysisReport).setFile(serviceStorage.createTmpFile());
				serviceStorage.store(form.getFile(), exportAnalysisReport.getFile().getName());
				exportAnalysisReport.getFile().deleteOnExit();
			}
			executor.execute(worker);
			return JsonMessage.success(
					messageSource.getMessage("success.analysis.report.exporting", null, "Exporting report", locale));
		} catch (TrickException e) {
			TrickLogManager.persist(e);
			return JsonMessage.error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.persist(e);
			if (e instanceof AccessDeniedException)
				throw e;
			return JsonMessage.error(
					messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
	}

	@GetMapping("/Risk-information/Export-process")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public String exportRiskInformationProcess(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Principal principal, Locale locale) throws Exception {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final Locale analysisLocale = new Locale(analysis.getLanguage().getAlpha2());
		final Map<String, List<RiskInformation>> riskInformationMap = analysis.getRiskInformations().stream()
				.map(riskInformation -> {
					if (!riskInformation.isCustom()) {
						switch (riskInformation.getCategory()) {
							case "Risk_TBA":
								riskInformation.setLabel(messageSource.getMessage(
										String.format("label.risk_information.risk_tba.%s",
												riskInformation.getChapter().replace(".", "_")),
										null,
										riskInformation.getLabel(), analysisLocale));
								break;
							case "Risk_TBS":
								riskInformation.setLabel(messageSource.getMessage(
										String.format("label.risk_information.risk_tbs.%s",
												riskInformation.getChapter().replace(".", "_")),
										null,
										riskInformation.getLabel(), analysisLocale));

								break;
							default:
								riskInformation.setLabel(messageSource.getMessage(
										String.format("label.risk_information.%s.%s",
												riskInformation.getCategory().toLowerCase(),
												riskInformation.getChapter().replace(".", "_")),
										null,
										riskInformation.getLabel(), analysisLocale));
								break;
						}
					}
					return riskInformation;
				}).sorted(new RiskInformationComparator())
				.collect(Collectors
						.groupingBy(riskInformation -> riskInformation.getCategory().startsWith("Risk_TB") ? "Risk"
								: riskInformation.getCategory()));

		final File workFile = InstanceManager.loadTemplate(analysis.getCustomer(), TrickTemplateType.RISK_INFORMATION,
				analysis.getLanguage());
		try {
			final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(workFile);
			final WorkbookPart workbook = mlPackage.getWorkbookPart();
			for (Object[] mapper : RI_SHEET_MAPPERS) {
				List<RiskInformation> riskInformations = riskInformationMap.get(mapper[0]);
				if (riskInformations == null)
					continue;
				final SheetData sheet = findSheet(workbook, mapper[1].toString());
				if (sheet == null)
					throw new TrickException("error.risk.information.template.sheet.not.found",
							String.format("Something wrong with template: Sheet `%s` cannot be found",
									mapper[1].toString()),
							mapper[1].toString());
				final WorksheetPart worksheetPart = getWorksheetPart(sheet);
				final TablePart tablePart = findTable(worksheetPart, mapper[0] + "Table");
				if (tablePart == null)
					throw new TrickException("error.risk.information.template.table.not.found",
							String.format("Something wrong with sheet `%s` : Table `%s` cannot be found",
									mapper[1].toString(), mapper[0] + "Table"),
							mapper[1].toString(),
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

				if (worksheetPart.getContents().getDimension() != null)
					worksheetPart.getContents().getDimension().setRef(table.getRef());

				int rowIndex = 1;
				int colSize = address.getEnd().getCol() + 1;

				for (RiskInformation riskInformation : riskInformations) {
					int colIndex = 0;
					Row row = getOrCreateRow(sheet, rowIndex++, colSize);
					setValue(row.getC().get(colIndex++), riskInformation.getChapter());
					setValue(row.getC().get(colIndex++), riskInformation.getLabel());
					if (riskInformation.getCategory().equals(Constant.RI_TYPE_THREAT))
						setValue(row.getC().get(colIndex++), riskInformation.getAcronym());
					setValue(row.getC().get(colIndex++), riskInformation.getExposed());
					setValue(row.getC().get(colIndex++), riskInformation.getOwner());
					setValue(row.getC().get(colIndex++), riskInformation.getComment());
					setValue(row.getC().get(colIndex++), riskInformation.getHiddenComment());
				}

				ExcelHelper.applyHeaderAndFooter(headerFooterSheetName,
						mapper[1].toString(), mlPackage);
			}

			final String extension = getExtension(mlPackage);

			final String filename = String.format(Constant.ITR_FILE_NAMING,
					Utils.cleanUpFileName(analysis.findSetting(ExportFileName.BRAINSTORMING)),
					Utils.cleanUpFileName(analysis.getCustomer().getOrganisation()),
					Utils.cleanUpFileName(analysis.getLabel()), "Brainstorming", analysis.getVersion(),
					extension);

			response.setContentType(extension);
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + filename + "\"");
			updateTokenCookie(request, response);
			mlPackage.save(response.getOutputStream());
			/**
			 * Log
			 */
			TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.export.risk.information",
					String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()),
					principal.getName(), LogAction.EXPORT, analysis.getIdentifier(),
					analysis.getVersion());

			return null;
		} finally {
			serviceStorage.delete(workFile.getName());
		}
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	@RequestMapping(value = "/Risk-register/Export-process", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object exportRiskRegisterProcess(HttpSession session, Principal principal, Locale locale) {
		final Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Locale analysisLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha2());
		final Worker worker = new WorkerExportRiskRegister(analysisId, principal.getName());
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.error(messageSource.getMessage("error.task_manager.too.many", null,
					"Too many tasks running in background", analysisLocale));
		// execute task
		executor.execute(worker);
		return JsonMessage.success(messageSource.getMessage("success.start.export.risk_register", null,
				"Start to export risk register", analysisLocale));
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	@RequestMapping(value = "/Risk-sheet/Export-form", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String exportRiskSheetForm(HttpSession session, Model model, HttpServletRequest request,
			Principal principal) {
		Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		ScaleType scaleType = serviceScaleType.findOneByAnalysisId(analysisId);
		List<? extends IBoundedParameter> impacts = serviceImpactParameter.findByTypeAndAnalysisId(scaleType,
				analysisId),
				probabilities = serviceLikelihoodParameter.findByAnalysisId(analysisId);
		impacts.removeIf(parameter -> parameter.getLevel() == 0);
		probabilities.removeIf(parameter -> parameter.getLevel() == 0);
		model.addAttribute("parameters",
				serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_CSSF_NAME, analysisId)
						.stream()
						.collect(Collectors.toMap(IParameter::getDescription, Function.identity())));
		model.addAttribute("owners", serviceAssessment.getDistinctOwnerByIdAnalysis(analysisId));
		model.addAttribute("impacts", impacts);
		model.addAttribute("probabilities", probabilities);
		model.addAttribute("reportRiskSheetItem",
				new DataManagerItem("risk-sheet-report", ANALYSIS_DATA_MANAGER_RISK_SHEET_EXPORT_PROCESS));
		model.addAttribute("rawRiskSheetItem",
				new DataManagerItem("risk-sheet-raw", ANALYSIS_DATA_MANAGER_RISK_SHEET_EXPORT_PROCESS));
		return "jsp/analyses/single/components/data-manager/export/risk-sheet/home";
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	@RequestMapping(value = "/Risk-sheet/Export-process", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object exportRiskSheetProcess(@RequestBody CSSFExportForm cssfExportForm, HttpSession session,
			HttpServletRequest request, Principal principal,
			Locale locale) {
		Map<String, String> errors = new HashMap<>();
		if (cssfExportForm.getFilter() == null)
			errors.put("filter",
					messageSource.getMessage("error.invalid.filter", null, "Filter cannot be load", locale));
		else {
			CSSFFilter cssfFilter = cssfExportForm.getFilter();
			if (cssfFilter.getImpact() < 0 || cssfFilter.getImpact() > Constant.DOUBLE_MAX_VALUE)
				errors.put("filter.impact",
						messageSource.getMessage("error.invalid.value", null, "Invalid value", locale));
			if (cssfFilter.getProbability() < 0 || cssfFilter.getProbability() > Constant.DOUBLE_MAX_VALUE)
				errors.put("filter.probability",
						messageSource.getMessage("error.invalid.value", null, "Invalid value", locale));
			if (cssfFilter.getDirect() < -2)
				errors.put("filter.direct",
						messageSource.getMessage("error.invalid.value", null, "Invalid value", locale));
			if (cssfFilter.getIndirect() < -2)
				errors.put("filter.indirect",
						messageSource.getMessage("error.invalid.value", null, "Invalid value", locale));
			if (cssfFilter.getCia() < -2)
				errors.put("filter.cia",
						messageSource.getMessage("error.invalid.value", null, "Invalid value", locale));
		}

		if (!errors.isEmpty())
			return errors;

		Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Locale analysisLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha2());
		Worker worker = new WorkerExportRiskSheet(cssfExportForm, analysisId, principal.getName());
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.error(messageSource.getMessage("error.task_manager.too.many", null,
					"Too many tasks running in background", analysisLocale));
		// execute task
		executor.execute(worker);
		return JsonMessage.success(messageSource.getMessage("success.start.export.risk_sheet", null,
				"Start to export risk sheet", analysisLocale));
	}

	@RequestMapping(value = "/RRF-Raw/Export-process", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportRRFRawProcess(Model model, HttpServletRequest request, HttpSession session,
			HttpServletResponse response, Principal principal, Locale locale)
			throws Exception {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final File file = InstanceManager.loadTemplate(analysis.getCustomer(), TrickTemplateType.DEFAULT_EXCEL,
				analysis.getLanguage());
		try {

			final Consumer<SpreadsheetMLPackage> callback = (m) -> {
				try {
					final String extension = getExtension(m);
					response.setContentType(extension);
					final String filename = String.format(Constant.ITR_FILE_NAMING,
							Utils.cleanUpFileName(analysis.findSetting(ExportFileName.RRF)),
							Utils.cleanUpFileName(analysis.getCustomer().getOrganisation()),
							Utils.cleanUpFileName(analysis.getLabel()), "RRF", analysis.getVersion(),
							extension);
					// set response header with location of the filename
					response.setHeader("Content-Disposition", "attachment; filename=\""
							+ filename + "\"");
					updateTokenCookie(request, response);
					m.save(response.getOutputStream());
					// log
					TrickLogManager.persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.raw.rrf",
							String.format("Analysis: %s, version: %s, type: Raw RRF", analysis.getIdentifier(),
									analysis.getVersion()),
							principal.getName(), LogAction.EXPORT,
							analysis.getIdentifier(), analysis.getVersion());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			};
			new RRFExportImport(serviceAssetType, serviceAnalysis, serviceAssetTypeValue, messageSource)
					.exportRawRRF(analysis, file, callback);
		} finally {
			serviceStorage.delete(file.getAbsolutePath());
		}
	}

	@RequestMapping(value = "/Scenario/Export-process", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportScenarioProcess(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final File file = InstanceManager.loadTemplate(analysis.getCustomer(), TrickTemplateType.DEFAULT_EXCEL,
				analysis.getLanguage());
		try {
			final SpreadsheetMLPackage spreadsheetMLPackage = SpreadsheetMLPackage.load(file);
			exportScenario(analysis, spreadsheetMLPackage);
			final String extension = getExtension(spreadsheetMLPackage);
			response.setContentType(extension);
			final String filename = String.format(Constant.ITR_FILE_NAMING,
					Utils.cleanUpFileName(analysis.findSetting(ExportFileName.SCENARIO)),
					Utils.cleanUpFileName(analysis.getCustomer().getOrganisation()),
					Utils.cleanUpFileName(analysis.getLabel()), "Scenario", analysis.getVersion(),
					extension);
			// set response header with location of the filename
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ filename + "\"");
			updateTokenCookie(request, response);
			spreadsheetMLPackage.save(response.getOutputStream());
			// Log
			TrickLogManager.persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.asset",
					String.format("Analysis: %s, version: %s, type: Asset", analysis.getIdentifier(),
							analysis.getVersion()),
					principal.getName(), LogAction.EXPORT,
					analysis.getIdentifier(), analysis.getVersion());
		} finally {
			serviceStorage.delete(file.getAbsolutePath());
		}
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody String exportSOAProcess(HttpSession session, Principal principal, HttpServletRequest request,
			Model model, Locale locale) throws Exception {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Worker worker = new WorkerSOAExport(principal.getName(), idAnalysis);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.error(messageSource.getMessage("error.task_manager.too.many", null,
					"Too many tasks running in background", locale));
		// execute task
		executor.execute(worker);
		return JsonMessage.success(messageSource.getMessage("success.start.exporting.soa", null,
				"SOA exporting was successfully started", locale));
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody String exportSqliteProcess(@RequestParam(name = "idAnalysis") int analysisId,
			Principal principal, HttpServletRequest request, Locale locale)
			throws Exception {
		// create worker
		final Worker worker = new WorkerExportAnalysis(principal.getName(), analysisId);
		// register worker
		if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale)) {
			executor.execute(worker);
			// return success message
			return JsonMessage.success(messageSource.getMessage("success.start.export.analysis", null,
					"Analysis export was started successfully", locale));
		} else
			// return error message
			return JsonMessage.error(messageSource.getMessage("error.task_manager.too.many", null,
					"Too many tasks running in background", locale));
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
	public String importAnalysis(@RequestParam(name = "idCustomer") Integer idCustomer, Principal principal,
			Model model) throws Exception {
		Customer customer = serviceCustomer.getFromUsernameAndId(principal.getName(), idCustomer);
		if (customer == null)
			throw new AccessDeniedException("access denied");
		model.addAttribute("maxFileSize", maxUploadFileSize);
		model.addAttribute("customer", customer);
		return "jsp/analyses/single/components/data-manager/import/sqlite";
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
	public @ResponseBody Object importAnalysisSave(@RequestParam(name = "customer") Integer idCustomer,
			@ModelAttribute ImportAnalysisForm form, Principal principal,
			HttpServletRequest request, Locale locale) throws Exception {
		// retrieve the customer
		if (!serviceCustomer.hasAccess(principal.getName(), idCustomer))
			throw new AccessDeniedException("access denied");
		if (form.getFile().isEmpty())
			return JsonMessage.error(messageSource.getMessage("error.customer_or_file.import.analysis", null,
					"Customer or file are not set or empty!", locale));
		else if (form.getFile().getSize() > maxUploadFileSize)
			return JsonMessage.error(messageSource.getMessage("error.file.too.large",
					new Object[] { maxUploadFileSize }, "File is to large", locale));
		// the file to import
		final String filename = principal.getName() + "_" + System.nanoTime() + ".tsdb";
		// create worker
		Worker worker = new WorkerAnalysisImport(filename, idCustomer, principal.getName());
		// register worker to tasklist
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.error(messageSource.getMessage("error.task_manager.too.many", null,
					"Too many tasks running in background", locale));
		// transfer form file to java file
		serviceStorage.store(form.getFile(), filename);
		// execute task
		executor.execute(worker);
		return JsonMessage.success(messageSource.getMessage("sucess.analysis.importing", null,
				"Please wait while importing your analysis", locale));
	}

	@PostMapping(value = "/Asset/Import-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String importAssetProcess(@RequestParam(value = "file") MultipartFile file,
			HttpServletRequest request, Model model, HttpSession session,
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public String importation(@RequestParam(name = "analysisId") Integer idAnalysis, Model model, HttpSession session,
			Principal principal, Locale locale) {
		final Analysis analysis = serviceAnalysis.findByIdAndEager(idAnalysis);
		final List<DataManagerItem> items = new LinkedList<>();
		items.add(new DataManagerItem("asset", "/Analysis/Data-manager/Asset/Import-process", ".xls,.xlsx,.xlsm"));
		items.add(new DataManagerItem("item-information", "/Analysis/Data-manager/Item-information/Import-process",
				".xls,.xlsx,.xlsm"));
		items.add(new DataManagerItem("risk-information", "/Analysis/Data-manager/Risk-information/Import-process",
				".xls,.xlsx,.xlsm"));
		if (!analysis.getAnalysisStandards().isEmpty())
			items.add(new DataManagerItem("measure", "/Analysis/Data-manager/Measure/Import-process",
					".xls,.xlsx,.xlsm"));
		items.add(new DataManagerItem("risk-estimation", "/Analysis/Data-manager/Risk-estimation/Import-process",
				".xls,.xlsx,.xlsm"));
		items.add(
				new DataManagerItem("scenario", "/Analysis/Data-manager/Scenario/Import-process", ".xls,.xlsx,.xlsm"));
		if (analysis.isQuantitative())
			items.add(new DataManagerItem("rrf", "/Analysis/Data-manager/RRF/Import-form", null, null));
		items.sort((i1, i2) -> NaturalOrderComparator.compareTo(
				messageSource.getMessage("label.menu.data_manager.import." + i1.getName().replaceAll("-", "_"), null,
						locale),
				messageSource.getMessage("label.menu.data_manager.import." + i2.getName().replaceAll("-", "_"), null,
						locale)));
		model.addAttribute("items", items);
		model.addAttribute("maxFileSize", maxUploadFileSize);
		return "jsp/analyses/single/components/data-manager/import";
	}

	@PostMapping(value = "/Risk-estimation/Import-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String importEstimationProcess(@RequestParam(value = "file") MultipartFile file,
			HttpServletRequest request, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		return importRisEstimation(false, false, file, request, model, session, principal, locale);
	}

	/**
	 * manage analysis standards (manage menu)
	 */
	@PostMapping(value = "/Measure/Import-process", produces = APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object importMeasureProcess(@RequestParam("file") MultipartFile file,
			HttpServletRequest request, HttpSession session, Principal principal, Locale locale)
			throws Exception {
		final String filename = ServiceStorage.RandoomFilename();
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Worker worker = new WorkerImportMeasureData(principal.getName(), idAnalysis, filename);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.error(messageSource.getMessage("error.task_manager.too.many", null,
					"Too many tasks running in background", locale));
		serviceStorage.store(file, filename);
		executor.execute(worker);
		return JsonMessage.success(messageSource.getMessage("success.start.import.measure.data", null,
				"Importing of security measures data", locale));
	}

	@PostMapping(value = "/Risk-information/Import-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String importRiskInformationProcess(@RequestParam(value = "file") MultipartFile file,
			@RequestParam(value = "overwrite", defaultValue = "true") boolean overwrite, HttpSession session,
			Principal principal, HttpServletRequest request, Locale locale) {
		final String filename = ServiceStorage.RandoomFilename();
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Worker worker = new WorkerImportRiskInformation(idAnalysis, principal.getName(), filename, overwrite);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.error(messageSource.getMessage("error.task_manager.too.many", null,
					"Too many tasks running in background", locale));
		serviceStorage.store(file, filename);
		executor.execute(worker);
		return JsonMessage.success(messageSource.getMessage("success.start.import.risk.information", null,
				"Importing of risk information", locale));
	}

	@PostMapping(value = "/Item-information/Import-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String importItemInformationProcess(@RequestParam(value = "file") MultipartFile file,
			@RequestParam(value = "overwrite", defaultValue = "true") boolean overwrite, HttpSession session,
			Principal principal, HttpServletRequest request, Locale locale) {
		final String filename = ServiceStorage.RandoomFilename();
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Worker worker = new WorkerImportItemInformation(idAnalysis, principal.getName(), filename, overwrite);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.error(messageSource.getMessage("error.task_manager.too.many", null,
					"Too many tasks running in background", locale));
		serviceStorage.store(file, filename);
		executor.execute(worker);
		return JsonMessage.success(messageSource.getMessage("success.start.import.item.information", null,
				"Importing of item information", locale));
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	@RequestMapping(value = "/RRF/Import-form", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String importRRFForm(HttpSession session, Principal principal, Model model) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		List<Standard> standards = serviceStandard.getAllFromAnalysis(idAnalysis);
		standards.removeIf(standard -> Constant.STANDARD_MATURITY.equalsIgnoreCase(standard.getName()));
		List<Analysis> analyses = serviceAnalysis.getAllProfileContainsStandard(standards, AnalysisType.QUANTITATIVE,
				AnalysisType.HYBRID);
		analyses.addAll(serviceAnalysis.getAllHasRightsAndContainsStandard(principal.getName(),
				AnalysisRight.highRightFrom(AnalysisRight.MODIFY), standards,
				AnalysisType.QUANTITATIVE, AnalysisType.HYBRID));
		analyses.removeIf(analysis -> analysis.getId() == idAnalysis);
		Collections.sort(analyses, new AnalysisComparator());
		List<Customer> customers = new ArrayList<>();
		analyses.stream().map(Analysis::getCustomer).distinct()
				.forEach(customers::add);
		model.addAttribute("standards", standards);
		model.addAttribute("customers", customers);
		model.addAttribute("analyses", analyses);
		model.addAttribute("rawRRFItem",
				new DataManagerItem("raw-rrf", "/Analysis/Data-manager/RRF-RAW/Import-process", ".xls,.xlsx,.xlsm"));
		model.addAttribute("rrfItem",
				new DataManagerItem("rrf-knowledge-base", null, "/Analysis/Data-manager/RRF/Import-process", null));
		return "jsp/analyses/single/components/data-manager/import/rrf";
	}

	/**
	 * 
	 * @param rrfForm
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	@RequestMapping(value = "/RRF/Import-process", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object importRRFProcess(@ModelAttribute ImportRRFForm rrfForm, HttpSession session,
			Principal principal, Locale locale) {
		try {
			if (rrfForm.getAnalysis() < 1)
				return JsonMessage.error(
						messageSource.getMessage("error.import_rrf.no_analysis", null, "No analysis selected", locale));
			else if (rrfForm.getStandards() == null || rrfForm.getStandards().isEmpty())
				return JsonMessage
						.error(messageSource.getMessage("error.import_rrf.norm", null, "No standard", locale));
			if (!(serviceAnalysis.isProfile(rrfForm.getAnalysis())
					|| serviceUserAnalysisRight.isUserAuthorized(rrfForm.getAnalysis(), principal.getName(),
							AnalysisRight.highRightFrom(AnalysisRight.MODIFY))
					|| rrfForm.getStandards().stream()
							.allMatch(idStandard -> serviceStandard.belongsToAnalysis(rrfForm.getAnalysis(),
									idStandard))))
				return JsonMessage.error(messageSource.getMessage("error.action.not_authorise", null,
						"Action does not authorised", locale));
			measureManager.importRRFFromStandard((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), rrfForm);
			return JsonMessage.success(messageSource.getMessage("success.import_rrf", null,
					"Measure characteristics has been successfully imported", locale));
		} catch (TrickException e) {
			TrickLogManager.persist(e);
			return JsonMessage.error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.persist(e);
			return JsonMessage
					.error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}

	}

	@RequestMapping(value = "/RRF-RAW/Import-process", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object importRRFRawProcess(@RequestParam(value = "file") MultipartFile file,
			HttpSession session, Principal principal, HttpServletRequest request,
			Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return new RRFExportImport(serviceAssetType, serviceAnalysis, serviceAssetTypeValue, messageSource)
				.importRawRRF(idAnalysis, file,
						principal.getName(), locale);
	}

	@PostMapping(value = "/Scenario/Import-process", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String importScenarioProcess(@RequestParam(value = "file") MultipartFile file,
			HttpServletRequest request, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		return importRisEstimation(false, true, file, request, model, session, principal, locale);
	}

	@Value("${app.settings.report.refurbish.max.size}")
	public void setMaxRefurbishReportSize(String value) {
		this.maxRefurbishReportSize = DataSize.parse(value).toBytes();
	}

	@Value("${spring.servlet.multipart.max-file-size}")
	public void setMaxUploadFileSize(String value) {
		this.maxUploadFileSize = DataSize.parse(value).toBytes();
	}

	private void exportProbability(Analysis analysis, SpreadsheetMLPackage mlPackage) throws Exception {
		final String name = "Probability";
		final ObjectFactory factory = Context.getsmlObjectFactory();
		final String[] columns = new String[] { "Level", "Label", "Value", "ILR" };
		final WorksheetPart worksheetPart = createWorkSheetPart(mlPackage, name);
		final SheetData sheet = worksheetPart.getContents().getSheetData();
		final List<LikelihoodParameter> parameters = analysis.getLikelihoodParameters();
		parameters.sort((e1, e2) -> Integer.compare(e1.getLevel(), e2.getLevel()));
		if (!parameters.isEmpty()) {
			final LikelihoodParameter firstEntry = parameters.get(0).clone();
			firstEntry.setAcronym("0");
			parameters.add(0, firstEntry.clone());
			firstEntry.setAcronym("na");
			parameters.add(0, firstEntry);
		}

		createHeader(worksheetPart, name, defaultExcelTableStyle, columns, parameters.size());

		for (LikelihoodParameter parameter : parameters) {
			Row row = factory.createRow();
			for (int i = 0; i <= columns.length; i++) {
				if (row.getC().size() < i)
					row.getC().add(Context.smlObjectFactory.createCell());
			}
			setValue(row.getC().get(0), parameter.getAcronym());
			setValue(row.getC().get(1),
					parameter.getLabel());
			setValue(row.getC().get(2), parameter.getValue());
			setValue(row.getC().get(3), parameter.getIlrLevel());
			sheet.getRow().add(row);
		}
		ExcelHelper.applyHeaderAndFooter(headerFooterSheetName, name, mlPackage);
	}

	private void generateRiskEstimation(final Analysis analysis, final SheetData sheetData, final boolean hiddenComment,
			boolean isILR, final boolean rowColumn, final boolean uncertainty,
			final Map<String, String> extrasColumns, final List<ScaleType> scales,
			final String[] columns, Locale locale) {
		final boolean qualitative = analysis.isQualitative();

		final Map<String, RiskProfile> riskProfiles = analysis.getRiskProfiles().stream()
				.collect(Collectors.toMap(RiskProfile::getKey, Function.identity()));

		final Map<String, RiskRegisterItem> riskRegisters = analysis.getRiskRegisters().stream()
				.collect(Collectors.toMap(RiskRegisterItem::getKey, Function.identity()));

		final Map<AssetType, String> assetTypes = serviceAssetType.getAll().stream()
				.collect(Collectors.toMap(Function.identity(), e -> messageSource
						.getMessage("label.asset_type." + e.getName().toLowerCase(), null, e.getName(), locale)));

		if (isILR)
			DependencyGraphManager.computeImpact(analysis.getAssetNodes());

		int rowIndex = 1;

		for (Assessment assessment : analysis.getAssessments()) {
			int cellIndex = 0;
			Row row = getOrCreateRow(sheetData, rowIndex++, columns.length);
			RiskProfile profile = riskProfiles
					.get(RiskProfile.key(assessment.getAsset(), assessment.getScenario()));
			if (qualitative)
				setValue(row, cellIndex++, profile.getIdentifier());
			setValue(row, cellIndex++, assessment.getAsset().getName());
			setValue(row, cellIndex++, assessment.getScenario().getName());
			if (qualitative) {
				setValue(row, cellIndex++,
						(profile.getRiskStrategy() == null ? RiskStrategy.ACCEPT : profile.getRiskStrategy())
								.getNameToLower());
				if (rowColumn)
					cellIndex += writeProbaImpact(row, cellIndex++, profile.getRawProbaImpact(), scales, false);
				cellIndex += writeProbaImpact(row, cellIndex++, assessment, scales, isILR);
				cellIndex += writeProbaImpact(row, cellIndex++, profile.getExpProbaImpact(), scales, isILR);
			} else {
				writeLikelihood(row, cellIndex++, assessment.getLikelihood());
				if (isILR)
					setValue(row, cellIndex++, assessment.getVulnerability());
				writeQuantitativeImpact(row, cellIndex++,
						assessment.getImpact(Constant.PARAMETER_TYPE_IMPACT_NAME));
			}

			if (uncertainty)
				setValue(row, cellIndex++, assessment.getUncertainty());
			if (isILR) {
				final int[] ilrRisks = ILRExport.computeIlrRisk(analysis, assessment, profile);
				for (int i = 0; i < ilrRisks.length; i++)
					setValue(row, cellIndex++, ilrRisks[i] == -1 ? null : ilrRisks[i]);
			}
			setValue(row, cellIndex++, assessment.getOwner());
			setValue(row, cellIndex++, assessment.getComment());
			if (hiddenComment)
				setValue(row, cellIndex++, assessment.getHiddenComment());

			setValue(row, cellIndex++, assessment.getCockpit());

			if (qualitative) {
				setValue(row, cellIndex++, profile.getRiskTreatment());
				Map<String, String> measures = profile.getMeasures().stream().map(Measure::getMeasureDescription)
						.sorted((m1, m2) -> NaturalOrderComparator.compareTo(m1.getReference(), m2.getReference()))
						.collect(Collectors.groupingBy(m -> m.getStandard().getName(),
								Collectors.mapping(MeasureDescription::getReference, Collectors.joining(";"))));
				String value = measures.entrySet().stream()
						.sorted((e1, e2) -> NaturalOrderComparator.compareTo(e1.getKey(), e2.getKey()))
						.map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.joining("\n"));
				setValue(row, cellIndex++, value);
				setValue(row, cellIndex++, profile.getActionPlan());
			}

			if (analysis.isHybrid()) {
				var riskRegister = riskRegisters
						.get(RiskRegisterItem.key(assessment.getAsset(), assessment.getScenario()));

				if (rowColumn) {
					setValue(row, cellIndex++,
							riskRegister == null || riskRegister.getRawEvaluation() == null ? null
									: riskRegister.getRawEvaluation().getImportance() * 0.001);
				}

				setValue(row, cellIndex++, assessment.getALE() * 0.001);
				setValue(row, cellIndex++,
						riskRegister == null || riskRegister.getExpectedEvaluation() == null ? null
								: riskRegister.getExpectedEvaluation().getImportance() * 0.001);

			}

			setFormula(setValue(row, cellIndex++, assetTypes.get(assessment.getAsset().getAssetType())),
					"VLOOKUP(Risk_estimation[[#This Row],[Asset]],Assets[[#All],[Name]:[Value]],2,FALSE)");
			setFormula(setValue(row, cellIndex++, assessment.getAsset().isSelected()),
					"VLOOKUP(Risk_estimation[[#This Row],[Asset]],Assets[[#All],[Name]:[Value]],3,FALSE)");

			setFormula(setValue(row, cellIndex++, assessment.getAsset().isSelected()),
					"VLOOKUP(Risk_estimation[[#This Row],[Scenario]],Scenarios[[Name]:[Selected]],4,FALSE)");

			for (String formula : extrasColumns.values()) {
				if (formula.contains("VLOOKUP"))
					setFormula(setValue(row, cellIndex++, ""), formula);
				else
					setFormula(getOrCreateCell(row, cellIndex++), formula);
			}
		}
	}

	/**
	 * Parse the extras columns from the file.
	 * The file should contain lines in the format: column_name;[@[column]] or [@column]
	 * @param multipartFile
	 * @return
	 */

	private Map<String, String> loadExtrasColumns(MultipartFile multipartFile) {
		if (multipartFile == null || multipartFile.isEmpty())
			return Collections.emptyMap();
		final File file = InstanceManager.getServiceStorage().createTmpFile();
		try {
			InstanceManager.getServiceStorage().store(multipartFile, file.getAbsolutePath());
			final Pattern p2 = Pattern.compile("\\[@\\[([^\\]]+)\\]\\]"); // Handle [@[column]] first
			final Pattern p1 = Pattern.compile("\\[@([^\\]]+)\\]"); // Then handle [@column]

			try (Stream<String> stream = Files.lines(file.toPath())) {
				return stream.map(String::trim)
						.map(e -> e.split(";", 2))
						.filter(e -> e.length == 2)
						.collect(Collectors.toMap(
								e -> e[0].trim(),
								e -> {
									String valueStr = e[1].replace(";", ",").trim();
									// Process structured references
									valueStr = p2.matcher(valueStr)
											.replaceAll(m -> "[[#This Row],[" + m.group(1) + "]]");
									valueStr = p1.matcher(valueStr)
											.replaceAll(m -> "[[#This Row],[" + m.group(1) + "]]");

									return valueStr;
								},
								(e1, e2) -> e1,
								LinkedHashMap::new));
			}

		} catch (Exception e) {
			TrickLogManager.persist(e);
			return Collections.emptyMap();
		} finally {
			serviceStorage.delete(file.getAbsolutePath());
		}

	}

	private void exportDependancy(Analysis analysis, SpreadsheetMLPackage mlPackage, Locale locale) throws Exception {
		final Set<AssetNode> rootSetNodes = new HashSet<>();
		final Set<AssetNode> branchSetNodes = new HashSet<>();
		final Set<AssetNode> leafSetNodes = new HashSet<>();

		analysis.getAssetNodes().stream().flatMap(e -> e.getEdges().values().stream()).forEach(e -> {
			leafSetNodes.add(e.getParent());
			if (e.getChild().isLeaf())
				rootSetNodes.add(e.getChild());
			else
				branchSetNodes.add(e.getChild());
		});

		analysis.getAssetNodes().stream()
				.filter(e -> !(rootSetNodes.contains(e) || branchSetNodes.contains(e) || leafSetNodes.contains(e)))
				.forEach(leafSetNodes::add);

		leafSetNodes.removeAll(branchSetNodes);

		final List<String> columns = rootSetNodes.stream().map(e -> e.getAsset().getName())
				.sorted(NaturalOrderComparator::compareTo).distinct().collect(Collectors.toList());

		final List<String> bStrings = branchSetNodes.stream().map(e -> e.getAsset().getName())
				.sorted(NaturalOrderComparator::compareTo).distinct().collect(Collectors.toList());

		columns.removeAll(bStrings);
		columns.addAll(bStrings);

		final List<String> rowNames = new ArrayList<>(columns);

		final List<String> leafs = leafSetNodes.stream().map(e -> e.getAsset().getName())
				.sorted(NaturalOrderComparator::compareTo).distinct().collect(Collectors.toList());

		rowNames.removeAll(leafs);

		rowNames.addAll(leafs);

		final Map<String, String> assetTypes = serviceAssetType.getAll().stream()
				.collect(Collectors.toMap(AssetType::getName,
						e -> messageSource.getMessage("label.asset_type." + e.getName().toLowerCase(), null,
								e.getName(), locale)));

		final Map<String, Asset> assetMap = new HashMap<>(analysis.getAssets().size());

		analysis.getAssets().forEach(asset -> {
			assetMap.put(asset.getName(), asset);
			if (!rowNames.contains(asset.getName()))
				rowNames.add(asset.getName());
		});

		final Map<String, Map<String, Double>> dependancies = analysis.getAssetNodes().stream()
				.flatMap(e -> e.getEdges().values().stream())
				.collect(Collectors.groupingBy(e -> e.getParent().getAsset().getName(),
						Collectors.toMap(e -> e.getChild().getAsset().getName(),
								e -> (Math.abs(e.getWeight() - 0) < 1E-9 ? 1 : e.getWeight()))));

		final WorksheetPart worksheetPart = createWorkSheetPart(mlPackage, "Dependency");
		final SheetData sheetData = worksheetPart.getContents().getSheetData();
		final int startRwoIndex = 2;

		columns.sort(NaturalOrderComparator::compareTo);
		rowNames.sort(NaturalOrderComparator::compareTo);

		columns.add(0, "AssetList");
		columns.add(1, "AssetType");
		columns.add(2, "AssetComment");

		createHeader(worksheetPart, "Table_dep", defaultExcelTableStyle, columns.toArray(new String[columns.size()]),
				startRwoIndex,
				analysis.getAssets().size());

		for (String assetName : rowNames) {
			final Row row = getOrCreateRow(sheetData, startRwoIndex + sheetData.getRow().size(), columns.size());
			final Map<String, Double> myDependancies = dependancies.getOrDefault(assetName, Collections.emptyMap());
			for (int i = 0; i < columns.size(); i++) {
				switch (i) {
					case 0:
						setValue(row, i, assetName);
						break;
					case 1:
						setValue(row, i, assetTypes.get(assetMap.get(assetName).getAssetType().getName()));
						break;
					case 2:
						setValue(row, i, assetMap.get(assetName).getComment());
						break;
					default:
						setValue(row, i, myDependancies.getOrDefault(columns.get(i), 0D));

				}
			}
		}
		ExcelHelper.applyHeaderAndFooter(headerFooterSheetName, "Dependency", mlPackage);
	}

	private String[] createTableHeader(List<ScaleType> scales, Collection<String> extrasColumns, AnalysisType type,
			boolean isILR,
			boolean hiddenComment, boolean rowColumn, boolean uncertainty) {
		List<Column> columns = WorkerImportEstimation.generateColumns(scales, type.isQualitative() || type.isHybrid(),
				isILR,
				hiddenComment,
				rowColumn,
				uncertainty);

		if (type.isHybrid()) {
			if (rowColumn)
				columns.add(new Column("RAW ALE"));
			columns.add(new Column("ALE"));
			columns.add(new Column("Residual ALE"));
		}

		columns.add(new Column("Asset type"));
		columns.add(new Column("Asset selected"));
		columns.add(new Column("Scenario selected"));
		extrasColumns.forEach(e -> columns.add(new Column(e)));

		String[] result = new String[columns.size()];
		for (int i = 0; i < columns.size(); i++)
			result[i] = columns.get(i).getName();
		return result;
	}

	private void exportAsset(Analysis analysis, SpreadsheetMLPackage spreadsheetMLPackage)
			throws Exception, JAXBException {
		final boolean hiddenComment = analysis.findSetting(AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT);
		final boolean isILR = Analysis.isILR(analysis);
		exportAsset(analysis, spreadsheetMLPackage, hiddenComment, isILR);
	}

	private void exportAsset(Analysis analysis, SpreadsheetMLPackage spreadsheetMLPackage, final boolean hiddenComment,
			boolean isILR)
			throws InvalidFormatException, JAXBException, Docx4JException, Exception {
		final String name = "Assets";
		final ObjectFactory factory = Context.getsmlObjectFactory();
		final WorksheetPart worksheetPart = createWorkSheetPart(spreadsheetMLPackage, name);
		final SheetData sheet = worksheetPart.getContents().getSheetData();
		final List<String> ilrImpactHeaders = (isILR ? new ArrayList<>() : Collections.emptyList());
		final List<String> myColumns = new ArrayList<>(Arrays.asList("Name", "Type", "Selected", "Value", "Comment"));
		final Map<String, Map<String, Integer>> ilrAssetImpacts = (isILR ? new HashMap<>() : Collections.emptyMap());
		final Map<String, String> maxFormulas;

		if (hiddenComment)
			myColumns.add("Hidden comment");

		myColumns.add("Related name");

		if (isILR) {
			writeAssetILRImpacts(analysis, ilrImpactHeaders, myColumns, ilrAssetImpacts);
			maxFormulas = ilrImpactHeaders.stream()
					.collect(Collectors.groupingBy(e -> e.substring(0, 1),
							Collectors.reducing("", e -> String.format("Assets[[#This Row],[%s]]", e),
									(e1, e2) -> e1 + (e1.isEmpty() ? "" : ",") + e2)));
		} else
			maxFormulas = Collections.emptyMap();

		final String[] columns = myColumns.toArray(new String[myColumns.size()]);

		createHeader(worksheetPart, name, defaultExcelTableStyle, columns, analysis.getAssets().size());
		final Map<String, String> assetTypes = exportAssetType(spreadsheetMLPackage,
				new Locale(analysis.getLanguage().getAlpha2()), factory);

		for (Asset asset : analysis.getAssets()) {

			final Row row = factory.createRow();

			for (int i = 0; i <= columns.length; i++) {
				if (row.getC().size() < i)
					row.getC().add(Context.smlObjectFactory.createCell());
			}

			writeAssetDefaultCells(assetTypes, asset, row);

			if (hiddenComment) {
				setValue(row.getC().get(ASSET_HIDDEN_COMMENT_CELL_INDEX), asset.getHiddenComment());
				setValue(row.getC().get(ASSET_HIDDEN_COMMENT_CELL_INDEX + 1), asset.getRelatedName());
			} else {
				setValue(row.getC().get(ASSET_HIDDEN_COMMENT_CELL_INDEX), asset.getRelatedName());
			}

			if (isILR)
				writeAssetILRCells(hiddenComment, ilrImpactHeaders, ilrAssetImpacts, asset, row);
			if (!maxFormulas.isEmpty()) {
				for (int i = columns.length - 3; i < columns.length; i++)
					setFormula(getOrCreateCell(row, i), String.format("MAX(%s)", maxFormulas.get(columns[i])));
			}

			sheet.getRow().add(row);
		}
		ExcelHelper.applyHeaderAndFooter(headerFooterSheetName, name, spreadsheetMLPackage);
	}

	private void exportScope(Analysis analysis, SpreadsheetMLPackage spreadsheetMLPackage)
			throws JAXBException, Docx4JException {
		final String name = "Scope";
		final ObjectFactory factory = Context.getsmlObjectFactory();
		final WorksheetPart worksheetPart = createWorkSheetPart(spreadsheetMLPackage, name);
		final SheetData sheet = worksheetPart.getContents().getSheetData();
		final List<String> myColumns = new ArrayList<>(Arrays.asList("Description", "Value"));
		final String[] columns = myColumns.toArray(new String[myColumns.size()]);
		final Locale locale = Locale.forLanguageTag(analysis.getLanguage().getAlpha2());
		createHeader(worksheetPart, name, defaultExcelTableStyle, columns, analysis.getItemInformations().size());
		Collections.sort(analysis.getItemInformations(), new ComparatorItemInformation());
		for (var item : analysis.getItemInformations()) {
			final Row row = factory.createRow();
			for (int i = 0; i <= columns.length; i++) {
				if (row.getC().size() < i)
					row.getC().add(Context.smlObjectFactory.createCell());
			}
			setValue(row.getC().get(0),
					messageSource.getMessage("label.item_information." + item.getDescription().trim(), null,
							item.getDescription(), locale));
			setValue(row.getC().get(1), item.getValue());

			sheet.getRow().add(row);
		}
		ExcelHelper.applyHeaderAndFooter(headerFooterSheetName, name, spreadsheetMLPackage);
	}

	private void writeAssetDefaultCells(final Map<String, String> assetTypes, Asset asset, final Row row) {
		setValue(row.getC().get(0), asset.getName());
		setValue(row.getC().get(1),
				assetTypes.getOrDefault(asset.getAssetType().getName(), asset.getAssetType().getName()));
		setValue(row.getC().get(2), asset.isSelected());
		setValue(row.getC().get(3), asset.getValue() * 0.001);
		setValue(row.getC().get(4), asset.getComment());
	}

	private void writeAssetILRCells(final boolean hiddenComment, final List<String> ilrImpactHeaders,
			final Map<String, Map<String, Integer>> ilrAssetImpacts, Asset asset, final Row row) {
		final int currentColum = ASSET_HIDDEN_COMMENT_CELL_INDEX + (hiddenComment ? 2 : 1);
		final Map<String, Integer> assetImpacts = ilrAssetImpacts.get(asset.getName());

		if (assetImpacts == null) {
			for (int j = 0; j < ilrImpactHeaders.size(); j++)
				setValue(row.getC().get(currentColum + j), -1);
		} else {
			for (int j = 0; j < ilrImpactHeaders.size(); j++)
				setValue(row.getC().get(currentColum + j),
						assetImpacts.getOrDefault(ilrImpactHeaders.get(j), -1));
		}

	}

	private void writeAssetILRImpacts(Analysis analysis, final List<String> ilrImpactHeaders,
			final List<String> myColumns,
			final Map<String, Map<String, Integer>> ilrAssetImpacts) {
		final String[] cias = { "C", "I", "A" };

		analysis.getIlrImpactTypes()
				.sort((e1, e2) -> NaturalOrderComparator.compareTo(e1.getShortName(), e2.getShortName()));

		analysis.getIlrImpactTypes().forEach(s -> {
			final String name = s.getShortName().replace(".", "");
			for (String cia : cias)
				ilrImpactHeaders.add(cia + "-" + name);
		});

		myColumns.addAll(ilrImpactHeaders);

		if (!ilrImpactHeaders.isEmpty())
			Collections.addAll(myColumns, cias);

		analysis.getAssets().forEach(a -> ilrAssetImpacts.put(a.getName(),
				ilrImpactHeaders.stream().collect(Collectors.toMap(Function.identity(), r -> -1))));

		analysis.getAssetNodes().forEach(n -> {
			final AssetImpact assetImpact = n.getImpact();
			if (assetImpact == null || assetImpact.getAsset() == null)
				return;
			final Map<String, Integer> assetImpacts = ilrAssetImpacts.get(assetImpact.getAsset().getName());
			writeILRImpact(assetImpact.getConfidentialityImpacts(), "C-", assetImpacts);
			writeILRImpact(assetImpact.getIntegrityImpacts(), "I-", assetImpacts);
			writeILRImpact(assetImpact.getAvailabilityImpacts(), "A-", assetImpacts);
		});
	}

	private void writeILRImpact(final Map<ScaleType, ILRImpact> impacts, String prefix,
			final Map<String, Integer> assetImpacts) {
		impacts.forEach((type, impact) -> {
			final String acronym = type.getShortName().replace(".", "");
			assetImpacts.put(prefix + acronym, impact.getValue());
		});
	}

	// *****************************************************************
	// * select or deselect analysis
	// *****************************************************************

	private Map<String, String> exportAssetType(SpreadsheetMLPackage spreadsheetMLPackage, Locale locale,
			ObjectFactory factory) throws Exception {
		final String name = "AssetTypes";
		final WorksheetPart worksheetPart = createWorkSheetPart(spreadsheetMLPackage, name);
		final SheetData sheet = worksheetPart.getContents().getSheetData();
		final List<AssetType> assetTypes = serviceAssetType.getAll();
		createHeader(worksheetPart, name, defaultExcelTableStyle, new String[] { "Name", "Display name" },
				assetTypes.size());
		final Map<String, String> names = new LinkedHashMap<>(assetTypes.size());
		for (AssetType assetType : assetTypes) {
			String value = messageSource.getMessage("label.asset_type." + assetType.getName().toLowerCase(), null,
					assetType.getName(), locale);
			names.put(assetType.getName(), value);
			final Row row = factory.createRow();
			setValue(row, 0, assetType.getName());
			setValue(row, 1, value);
			sheet.getRow().add(row);
		}
		ExcelHelper.applyHeaderAndFooter(headerFooterSheetName, name, spreadsheetMLPackage);
		return names;
	}

	private void exportMeasureStandard(ValueFactory valueFactory, AnalysisStandard analysisStandard,
			WorksheetPart worksheetPart) throws Exception {
		String[] columns = getColumns(analysisStandard);
		prepareTableHeader(analysisStandard, worksheetPart, columns);
		SheetData sheetData = worksheetPart.getContents().getSheetData();
		analysisStandard.getMeasures().sort((m1, m2) -> NaturalOrderComparator
				.compareTo(m1.getMeasureDescription().getReference(), m2.getMeasureDescription().getReference()));
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
					case "Importance":
						switch (measure.getImportance()) {
							case 1:
								setValue(row, i, "L");
								break;
							case 3:
								setValue(row, i, "H");
								break;
							default:
								setValue(row, i, "M");
						}
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
					case "SOA Mitigated risk":
						if (measure instanceof AbstractNormalMeasure)
							setValue(row, i, ((AbstractNormalMeasure) measure).getSoaRisk());
						break;
					case "SOA Justification":
						if (measure instanceof AbstractNormalMeasure)
							setValue(row, i, ((AbstractNormalMeasure) measure).getSoaComment());
						break;
					case "SOA Reference":
						if (measure instanceof AbstractNormalMeasure)
							setValue(row, i, ((AbstractNormalMeasure) measure).getSoaReference());
						break;
					default:
						break;
				}
			}
		}
	}

	private void exportRawActionPlan(Analysis analysis, SpreadsheetMLPackage spreadsheetMLPackage, Locale locale)
			throws Exception {
		ObjectFactory factory = Context.getsmlObjectFactory();
		final List<IAcronymParameter> expressionParameters = analysis.getExpressionParameters();
		final ActionPlanMode[] types = analysis.isHybrid()
				? new ActionPlanMode[] { ActionPlanMode.APPN, ActionPlanMode.APQ }
				: analysis.isQualitative() ? new ActionPlanMode[] { ActionPlanMode.APQ }
						: new ActionPlanMode[] { ActionPlanMode.APPN };
		for (int i = 0; i < types.length; i++) {
			final ActionPlanMode type = types[i];
			final int colCount = type == ActionPlanMode.APPN ? 23 : 21;
			final List<ActionPlanEntry> actionPlanEntries = analysis.findActionPlan(type);
			if (actionPlanEntries.isEmpty())
				continue;
			final String name = messageSource.getMessage("label.title.plan_type." + type.getName().toLowerCase(), null,
					type.getName(), locale);
			final WorksheetPart worksheetPart = createWorkSheetPart(spreadsheetMLPackage, name);
			final SheetData sheet = worksheetPart.getContents().getSheetData();
			final String title = messageSource.getMessage(
					"label.title.export.plan_type." + type.getName().toLowerCase(), null, type.getName(), locale);
			createHeader(worksheetPart, title, defaultExcelTableStyle,
					generateActionPlanColumns(colCount - 1, type, locale), actionPlanEntries.size());
			for (ActionPlanEntry actionPlanEntry : actionPlanEntries)
				sheet.getRow().add(writeActionPLanData(factory.createRow(), colCount, actionPlanEntry,
						expressionParameters, locale));
			ExcelHelper.applyHeaderAndFooter(headerFooterSheetName, name, spreadsheetMLPackage);
		}
	}

	private void exportScenario(Analysis analysis, SpreadsheetMLPackage spreadsheetMLPackage) throws Exception {
		final String name = "Scenarios";
		final ObjectFactory factory = Context.getsmlObjectFactory();
		final boolean isILR = Analysis.isILR(analysis);
		final String[] columns = isILR
				? new String[] { "Name", "Type", "Apply to", "Selected", "Description", "Threat", "Vulnerability" }
				: new String[] { "Name", "Type", "Apply to", "Selected", "Description" };
		final WorksheetPart worksheetPart = createWorkSheetPart(spreadsheetMLPackage, name);
		final SheetData sheet = worksheetPart.getContents().getSheetData();
		createHeader(worksheetPart, name, defaultExcelTableStyle, columns, analysis.getScenarios().size());
		final Map<String, String> scenarioTypes = exportScenarioType(spreadsheetMLPackage,
				new Locale(analysis.getLanguage().getAlpha2()), factory);

		for (Scenario scenario : analysis.getScenarios()) {
			Row row = factory.createRow();
			for (int i = 0; i <= columns.length; i++) {
				if (row.getC().size() < i)
					row.getC().add(Context.smlObjectFactory.createCell());
			}
			setValue(row.getC().get(0), scenario.getName());
			setValue(row.getC().get(1),
					scenarioTypes.getOrDefault(scenario.getType().getName(), scenario.getType().getName()));
			setValue(row.getC().get(2), scenario.isAssetLinked() ? "Asset" : "Asset type");
			setValue(row.getC().get(3), scenario.isSelected());
			setValue(row.getC().get(4), scenario.getDescription());

			if (isILR) {
				setValue(row.getC().get(5), scenario.getThreat());
				setValue(row.getC().get(6), scenario.getVulnerability());
			}

			sheet.getRow().add(row);
		}
		ExcelHelper.applyHeaderAndFooter(headerFooterSheetName, name, spreadsheetMLPackage);
	}

	private Map<String, String> exportScenarioType(SpreadsheetMLPackage spreadsheetMLPackage, Locale locale,
			ObjectFactory factory) throws Exception {
		final String name = "ScenarioTypes";
		final ScenarioType[] scenarioTypes = ScenarioType.values();
		final WorksheetPart worksheetPart = createWorkSheetPart(spreadsheetMLPackage, name);
		final SheetData sheet = worksheetPart.getContents().getSheetData();
		createHeader(worksheetPart, name, defaultExcelTableStyle, new String[] { "Name", "Display name" },
				scenarioTypes.length);
		final Map<String, String> names = new LinkedHashMap<>(scenarioTypes.length);
		for (ScenarioType scenarioType : ScenarioType.values()) {
			String value = messageSource.getMessage(
					"label.scenario.type." + scenarioType.getName().replaceAll("-", "_").toLowerCase(), null,
					scenarioType.getName(), locale);
			names.put(scenarioType.getName(), value);
			final Row row = factory.createRow();
			setValue(row, 0, scenarioType.getName());
			setValue(row, 1, value);
			sheet.getRow().add(row);
		}
		ExcelHelper.applyHeaderAndFooter(headerFooterSheetName, name, spreadsheetMLPackage);
		return names;
	}

	private String[] generateActionPlanColumns(final int count, ActionPlanMode type, Locale locale) {
		int colIndex = 0;
		final String[] columns = new String[count];
		columns[colIndex++] = messageSource.getMessage("report.action_plan.norm", null, "Stds", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.reference", null, "Ref.", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.domain", null, "Domain", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.description", null, "Description", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.status", null, "ST", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.comment", null, "Comment", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.to_do", null, "To Do", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.responsible", null, "Resp.", locale);
		columns[colIndex++] = messageSource.getMessage("report.measure.ticket", null, "ticket", locale);
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
		return analysisStandard instanceof MaturityStandard ? Constant.MATURITY_MEASURE_COLUMNS
				: Constant.NORMAL_MEASURE_COLUMNS;
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
	private String importRisEstimation(boolean asset, boolean scenario, MultipartFile file, HttpServletRequest request,
			Model model, HttpSession session, Principal principal,
			Locale locale) throws Exception {
		if (file.isEmpty())
			return JsonMessage
					.error(messageSource.getMessage("error.file.empty", null, "File cannot be empty", locale));
		if (file.getSize() > maxUploadFileSize)
			return JsonMessage.error(messageSource.getMessage("error.file.too.large",
					new Object[] { maxUploadFileSize }, "File is to large", locale));
		final String filename = ServiceStorage.RandoomFilename();
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Worker worker = new WorkerImportEstimation(idAnalysis, principal.getName(), filename, asset, scenario);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.error(messageSource.getMessage("error.task_manager.too.many", null,
					"Too many tasks running in background", locale));
		serviceStorage.store(file, filename);
		executor.execute(worker);
		return asset
				? JsonMessage.success(messageSource.getMessage("success.start.import.asset", null,
						"Importing of assets data", locale))
				: scenario
						? JsonMessage.success(messageSource.getMessage("success.start.import.risk.scenario", null,
								"Importing of risk scenarios data", locale))
						: JsonMessage.success(messageSource.getMessage("success.start.risk.estimation", null,
								"Importing of risk estimations data", locale));
	}

	private void prepareTableHeader(AnalysisStandard analysisStandard, WorksheetPart worksheetPart, String[] columns)
			throws Exception {
		createHeader(worksheetPart, "Measures" + analysisStandard.getStandard().getId(), defaultExcelTableStyle,
				columns, analysisStandard.getMeasures().size());
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

	private Row writeActionPLanData(Row row, int colCount, ActionPlanEntry actionPlanEntry,
			List<IAcronymParameter> expressionParameters, Locale locale) {
		for (int i = 0; i < colCount; i++) {
			if (row.getC().size() < i)
				getOrCreateCell(row, i);
		}
		int colIndex = 0;
		Measure measure = actionPlanEntry.getMeasure();
		MeasureDescriptionText descriptionText = measure.getMeasureDescription()
				.getMeasureDescriptionTextByAlpha3(locale.getISO3Language());
		setValue(row.getC().get(colIndex), measure.getMeasureDescription().getStandard().getName());
		setValue(row.getC().get(++colIndex), measure.getMeasureDescription().getReference());
		setValue(row.getC().get(++colIndex), descriptionText.getDomain());
		setValue(row.getC().get(++colIndex), descriptionText.getDescription());
		setValue(row.getC().get(++colIndex), measure.getStatus());
		setValue(row.getC().get(++colIndex), measure.getComment());
		setValue(row.getC().get(++colIndex), measure.getToDo());
		setValue(row.getC().get(++colIndex), measure.getResponsible());
		setValue(row.getC().get(++colIndex), measure.getTicket());
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

	private int writeProbaImpact(Row row, int colIndex, Assessment assessment, List<ScaleType> scales,
			boolean hasVulnerability) {
		writeLikelihood(row, colIndex++, assessment.getLikelihood());
		if (hasVulnerability)
			setValue(row, colIndex++, "v" + assessment.getVulnerability());
		for (ScaleType type : scales) {
			IValue value = assessment.getImpact(type.getName());
			if (value == null)
				setValue(row, colIndex++, 0);
			else if (type.getName().equals(Constant.DEFAULT_IMPACT_NAME))
				writeQuantitativeImpact(row, colIndex++, value);
			else
				setValue(row, colIndex++, "i" + value.getLevel());
		}
		return (hasVulnerability ? 2 : 1) + scales.size();
	}

	private int writeProbaImpact(Row row, int colIndex, RiskProbaImpact probaImpact, List<ScaleType> scales,
			boolean hasVulnerability) {
		int columnCount = hasVulnerability ? 2 : 1;
		if (probaImpact == null) {
			setValue(row, colIndex++, 0);
			if (hasVulnerability)
				setValue(row, colIndex++, "v1");

			for (ScaleType type : scales) {
				if (!type.getName().equals(Constant.DEFAULT_IMPACT_NAME)) {
					setValue(row, colIndex++, 0);
					columnCount++;
				}
			}
		} else {
			setValue(row, colIndex++,
					probaImpact.getProbability() == null ? 0 : probaImpact.getProbability().getAcronym());

			if (hasVulnerability)
				setValue(row, colIndex++, "v" + probaImpact.getVulnerability());

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

	private void writeLikelihood(Row row, int cellIndex, IValue impact) {
		if (impact == null)
			setValue(row, cellIndex, "na");
		else if (impact.getRaw() instanceof Double) {
			if (impact.getRaw().equals(0d))
				setValue(row, cellIndex, "na");
			else
				setValue(row, cellIndex, impact.getReal());
		} else
			setValue(row, cellIndex, impact.getVariable());
	}

}
