/**
 * 
 */
package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.OPEN_MODE;
import static lu.itrust.business.TS.constants.Constant.RI_SHEET_MAPPERS;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.findTable;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getRow;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.io.File;
import java.security.Principal;
import java.util.Collections;
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
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
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
import org.springframework.web.multipart.MultipartFile;
import org.xlsx4j.sml.CTTable;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerImportRiskInformation;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.NaturalOrderComparator;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceRiskInformation;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.riskinformation.helper.RiskInformationComparator;
import lu.itrust.business.TS.model.riskinformation.helper.RiskInformationManager;
import lu.itrust.business.permissionevaluator.PermissionEvaluator;

/**
 * @author eomar
 *
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/Risk-information")
public class ControllerRiskInformation {

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceRiskInformation serviceRiskInformation;

	@Autowired
	private PermissionEvaluator permissionEvaluator;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private TaskExecutor executor;

	@Autowired
	private MessageSource messageSource;

	@Value("${app.settings.risk.information.template.path}")
	private String template;

	@RequestMapping(value = "/Manage/{category}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String manage(@PathVariable String category, Model model, HttpSession session, Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Locale analysisLocale = new Locale(serviceLanguage.getFromAnalysis(idAnalysis).getAlpha2());
		Map<String, List<RiskInformation>> riskInformationMap = serviceRiskInformation
				.getAllByIdAnalysisAndCategory(idAnalysis, category.equals("Risk") ? new String[] { "Risk_TBA", "Risk_TBS" } : new String[] { category }).stream()
				.map(riskInformation -> {
					if (!riskInformation.isCustom()) {
						switch (riskInformation.getCategory()) {
						case "Risk_TBA":
							riskInformation.setLabel(messageSource.getMessage(String.format("label.risk_information.risk_tba.", riskInformation.getChapter().replace(".", "_")),
									null, riskInformation.getLabel(), analysisLocale));
							break;
						case "Risk_TBS":
							riskInformation.setLabel(messageSource.getMessage(String.format("label.risk_information.risk_tbs.", riskInformation.getChapter().replace(".", "_")),
									null, riskInformation.getLabel(), analysisLocale));
							break;
						default:
							riskInformation.setLabel(messageSource.getMessage(
									String.format("label.risk_information.%s.", riskInformation.getCategory().toLowerCase(), riskInformation.getChapter().replace(".", "_")), null,
									riskInformation.getLabel(), analysisLocale));
							break;
						}
					}
					return riskInformation;
				}).collect(Collectors.groupingBy(riskInformation -> riskInformation.getCategory().startsWith("Risk_TB") ? "Risk" : riskInformation.getCategory()));
		riskInformationMap.values().forEach(riskInformation -> riskInformation.sort((o1, o2) -> NaturalOrderComparator.compareTo(o1.getChapter(), o2.getChapter())));
		if (!riskInformationMap.containsKey(category))
			riskInformationMap.put(category, Collections.emptyList());
		model.addAttribute("type", category);
		model.addAttribute("riskInformationMap", riskInformationMap);
		return "analyses/single/components/risk-information/manage";
	}

	@RequestMapping(value = "/Manage/{category}/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object save(@PathVariable String category, @RequestBody List<RiskInformation> riskInformations, HttpSession session, Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		Map<String, RiskInformation> riskInformationMap = analysis.getRiskInformations().stream().filter(riskInformation -> riskInformation.isMatch(category))
				.collect(Collectors.toMap(RiskInformation::getKey, Function.identity()));
		Map<Integer, String> riskInformationIDMap = riskInformationMap.values().parallelStream().collect(Collectors.toMap(RiskInformation::getId, RiskInformation::getKey));
		riskInformations.forEach(riskInformation -> {

			RiskInformation persisted = riskInformationMap.remove(riskInformation.getKey());
			if (persisted == null && riskInformation.getId() > 0) {
				String key = riskInformationIDMap.remove(riskInformation.getId());
				if (key != null)
					persisted = riskInformationMap.remove(key);
			}

			if (riskInformation.isCustom()) {
				if (persisted == null) {
					riskInformation.setId(-1);
					analysis.getRiskInformations().add(riskInformation);
				} else {
					persisted.setChapter(riskInformation.getChapter());
					persisted.setLabel(riskInformation.getLabel());
					persisted.setCustom(true);
				}
			}
		});

		analysis.getRiskInformations().removeAll(riskInformationMap.values());
		serviceRiskInformation.delete(riskInformationMap.values());
		analysis.getRiskInformations().sort(new RiskInformationComparator());
		serviceAnalysis.saveOrUpdate(analysis);
		return JsonMessage.Success(messageSource.getMessage("success.update.risk_information", null, "Risk information has been successfully updated", locale));
	}

	@RequestMapping(value = "/Section/{type}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String section(@PathVariable String type, Model model, HttpSession session, Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Map<String, List<RiskInformation>> riskInformations = RiskInformationManager.Split(serviceRiskInformation.findByIdAnalysisAndCategory(idAnalysis, type));
		if (!riskInformations.containsKey(type))
			riskInformations.put(type, Collections.emptyList());
		OpenMode mode = (OpenMode) session.getAttribute(OPEN_MODE);
		model.addAttribute("riskInformationSplited", riskInformations);
		model.addAttribute("isEditable", !OpenMode.isReadOnly(mode));
		model.addAttribute("canExport", permissionEvaluator.userOrOwnerIsAuthorized(idAnalysis, principal, AnalysisRight.EXPORT));
		model.addAttribute("showHiddenComment", serviceAnalysis.findSetting(idAnalysis, AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT));
		return "analyses/single/components/risk-information/home";
	}

	@GetMapping("/Export")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public String export(HttpServletRequest request, HttpServletResponse response, HttpSession session, Principal principal, Locale locale) throws Exception {
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
			FileUtils.copyFile(new File(request.getServletContext().getRealPath(template)), workFile);
			SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(workFile);
			WorkbookPart workbook = mlPackage.getWorkbookPart();
			for (String[] mapper : RI_SHEET_MAPPERS) {
				List<RiskInformation> riskInformations = riskInformationMap.get(mapper[0]);
				SheetData sheet = findSheet(workbook, mapper[1]);
				if (sheet == null)
					throw new TrickException("error.risk.information.template.sheet.not.found",
							String.format("Something wrong with template: Sheet `%s` cannot be found", mapper[1]), mapper[1]);
				TablePart tablePart = findTable(sheet.getWorksheetPart(), mapper[0] + "Table");
				if (tablePart == null)
					throw new TrickException("error.risk.information.template.table.not.found",
							String.format("Something wrong with sheet `%s` : Table `%s` cannot be found", mapper[1], mapper[0] + "Table"), mapper[1], mapper[0] + "Table");
				AddressRef address = AddressRef.parse(tablePart.getContents().getRef());
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

	@GetMapping("/Import-form")
	public String importRiskModal() {
		return "analyses/single/components/risk-information/import-modal";
	}

	@PostMapping(value = "/Import", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String importRisk(@RequestParam(value = "file") MultipartFile file, HttpSession session, Principal principal, HttpServletRequest request, Locale locale)
			throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		File workFile = new File(request.getServletContext().getRealPath("/WEB-INF/tmp") + "/" + principal.getName() + "_" + System.nanoTime());
		Worker worker = new WorkerImportRiskInformation(idAnalysis, principal.getName(), workFile, messageSource, workersPoolManager, sessionFactory, serviceTaskFeedback);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
		file.transferTo(workFile);
		executor.execute(worker);
		return JsonMessage.Success(messageSource.getMessage("success.start.import.risk.information", null, "Importing of risk information", locale));
	}

}
