package lu.itrust.business.TS.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.Phase;
import lu.itrust.business.TS.RiskInformation;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.ActionPlanMode;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.ActionPlanSummaryManager;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.AssetComparatorByALE;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.component.ComparatorItemInformation;
import lu.itrust.business.component.RiskInformationManager;
import lu.itrust.business.component.helper.ALE;
import lu.itrust.business.component.helper.ReportExcelSheet;
import lu.itrust.business.exception.TrickException;
import lu.itrust.business.service.ServiceAnalysis;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.hibernate.Hibernate;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.springframework.context.MessageSource;

/**
 * ExportReport.java: <br>
 * Detailed description...
 * 
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since May 27, 2014
 */
public class ExportAnalysisReport {

	private static final String SUPER_HEAD_COLOR = "95b3d7";

	private static final String DEFAULT_CELL_COLOR = "f5f9f0";

	private static final String ZERO_COST_COLOR = "e6b8b7";

	private static final String SUB_HEADER_COLOR = "dbe5f1";

	private static final String HEADER_COLOR = "B8CCE4";

	private XWPFDocument document = null;

	private ServiceAnalysis serviceAnalysis = null;

	private ServletContext context = null;

	private Analysis analysis = null;

	private Locale locale = null;

	private MessageSource messageSource;

	private String reportName;

	private String reportVersion;

	private DecimalFormat kEuroFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);

	private DecimalFormat numberFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);

	public ExportAnalysisReport() {
		kEuroFormat.setMaximumFractionDigits(1);
		numberFormat.setMaximumFractionDigits(0);
	}

	/**
	 * exportToWordDocument: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param context
	 * @param serviceAnalysis
	 * @return
	 * @throws Exception
	 */
	public File exportToWordDocument(Integer analysisId, ServletContext context, ServiceAnalysis serviceAnalysis, boolean template) throws Exception {
		InputStream inputStream = null;
		try {
			if (!serviceAnalysis.exists(analysisId)) {
				throw new TrickException("error.analysis.not_exist", "Analysis not found");
			} else if (serviceAnalysis.isProfile(analysisId)) {
				throw new TrickException("error.analysis.is_profile", "Profile cannot be exported as report");
			} else if (!serviceAnalysis.hasData(analysisId)) {
				throw new TrickException("error.analysis.no_data", "Empty analysis cannot be exported");
			}

			Analysis analysis = serviceAnalysis.get(analysisId);

			if (analysis.getLanguage() == null || !analysis.getLanguage().getAlpha3().equalsIgnoreCase("fra"))
				locale = Locale.ENGLISH;
			else
				locale = Locale.FRENCH;

			this.analysis = analysis;

			this.context = context;

			this.serviceAnalysis = serviceAnalysis;

			XWPFDocument document = null;

			File doctemp = new File(this.getContext().getRealPath(String.format("/WEB-INF/tmp/STA_%s_V%s.docm", analysis.getLabel(), analysis.getVersion())));

			if (!doctemp.exists())
				doctemp.createNewFile();

			if (template) {
				File doctemplate = new File(this.getContext().getRealPath(
						String.format("/WEB-INF/data/%s-%s_V%s.dotm", reportName, locale == Locale.FRENCH ? "FR" : "EN", reportVersion)));
				OPCPackage pkg = OPCPackage.open(doctemplate.getAbsoluteFile());
				pkg.replaceContentType("application/vnd.ms-word.template.macroEnabledTemplate.main+xml", "application/vnd.ms-word.document.macroEnabled.main+xml");
				pkg.save(doctemp);
				document = new XWPFDocument(inputStream = new FileInputStream(doctemp));
			} else {
				XWPFDocument templateDocx = new XWPFDocument(inputStream = new FileInputStream(new File(this.getContext().getRealPath(
						String.format("/WEB-INF/data/%s-%s_V%s.dotm", reportName, locale == Locale.FRENCH ? "FR" : "EN", reportVersion)))));
				document = new XWPFDocument();
				XWPFStyles xwpfStyles = document.createStyles();
				xwpfStyles.setStyles(templateDocx.getStyle());
			}

			this.document = document;

			generatePlaceholders();

			generateItemInformation();

			generateAssets();

			generateScenarios();

			generateAssessements();

			generateThreats();

			generateExtendedParameters(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME);

			generateExtendedParameters(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME);

			generateActionPlan();

			generateActionPlanSummary();

			generateMeasures();

			generateGraphics();

			document.write(new FileOutputStream(doctemp));

			return doctemp;
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
	}

	private void generateGraphics() throws OpenXML4JException, IOException {
		System.out.println(this.document.getAllEmbedds());
		for (PackagePart packagePart : this.document.getPackage().getParts())
			if (packagePart.getPartName().getExtension().contains("xls")) {
				ReportExcelSheet reportExcelSheet = new ReportExcelSheet(packagePart, context.getRealPath("/WEB-INF/tmp/"));
				switch (reportExcelSheet.getName()) {
				case "Compliance27001":
				case "Compliance27002":
					generateComplianceGraphic(reportExcelSheet);
					break;
				case "ALEByScenarioType":
					generateALEByScenarioTypeGraphic(reportExcelSheet);
					break;
				case "ALEByScenario":
					generateALEByScenarioGraphic(reportExcelSheet);
					break;
				case "ALEByAssetType":
					generateALEByAssetTypeGraphic(reportExcelSheet);
					break;
				case "ALEByAsset":
					generateALEByAssetGraphic(reportExcelSheet);
					break;
				case "EvolutionOfProfitability":
					generateEvolutionOfProfitabilityGraphic(reportExcelSheet);
					break;
				case "Budget":
					generateBudgetGraphic(reportExcelSheet);
					break;
				}
				//reportExcelSheet.getPackagePart().
			}
		
	}

	private void generateEvolutionOfProfitabilityGraphic(ReportExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
		if (reportExcelSheet == null)
			return;
		Map<String, List<String>> summaries = ActionPlanSummaryManager.buildTable(analysis.getSummaries(), analysis.getUsedPhases());
		Map<String, Phase> usesPhases = ActionPlanSummaryManager.buildPhase(analysis.getUsedPhases(), ActionPlanSummaryManager.extractPhaseRow(analysis.getSummaries()));
		XSSFSheet xssfSheet = reportExcelSheet.getXssfWorkbook().getSheetAt(0);
		int rowIndex = 1;
		for (Phase phase : usesPhases.values()) {
			if (xssfSheet.getRow(rowIndex) == null)
				xssfSheet.createRow(rowIndex);
			if (xssfSheet.getRow(rowIndex).getCell(0) == null)
				xssfSheet.getRow(rowIndex).createCell(0);
			xssfSheet.getRow(rowIndex++).getCell(0).setCellValue(String.format("P%d", phase.getNumber()));
		}

		List<String> dataCompliance27001s = summaries.get(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE_27001);
		List<String> dataCompliance27002s = summaries.get(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE_27002);
		List<String> dataALEs = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ALE_UNTIL_END);
		List<String> dataRiskReductions = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_RISK_REDUCTION);
		List<String> dataROSIs = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI);
		List<String> dataRelatifROSIs = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI_RELATIF);
		List<String> dataPhaseAnnualCosts = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_PHASE_ANNUAL_COST);

		for (int j = 1; j < 8; j++) {
			if (xssfSheet.getRow(0) == null)
				xssfSheet.createRow(0);
			if (xssfSheet.getRow(0).getCell(j) == null)
				xssfSheet.getRow(0).createCell(j);
		}
		xssfSheet.getRow(0).getCell(1).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE_27001, null, "Compliance 27001", locale));
		xssfSheet.getRow(0).getCell(2).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE_27002, null, "Compliance 27002", locale));
		xssfSheet.getRow(0).getCell(3).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_ALE_UNTIL_END, null, "ALE (k€)... at end", locale));
		xssfSheet.getRow(0).getCell(4).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_RISK_REDUCTION, null, "Risk reduction", locale));
		xssfSheet.getRow(0).getCell(5).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_PHASE_ANNUAL_COST, null, "Phase annual cost", locale));
		xssfSheet.getRow(0).getCell(6).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI, null, "ROSI", locale));
		xssfSheet.getRow(0).getCell(7).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI_RELATIF, null, "ROSI relatif", locale));
		rowIndex = 1;
		for (int i = 0; i < dataCompliance27001s.size(); i++) {
			for (int j = 1; j < 8; j++) {
				if (xssfSheet.getRow(rowIndex) == null)
					xssfSheet.createRow(rowIndex);
				if (xssfSheet.getRow(rowIndex).getCell(j) == null)
					xssfSheet.getRow(rowIndex).createCell(j);
			}
			xssfSheet.getRow(rowIndex).getCell(1).setCellValue(Double.parseDouble(dataCompliance27001s.get(i)) * 0.01);
			xssfSheet.getRow(rowIndex).getCell(2).setCellValue(Double.parseDouble(dataCompliance27002s.get(i)) * 0.01);
			xssfSheet.getRow(rowIndex).getCell(3).setCellValue(Double.parseDouble(dataALEs.get(i)));
			xssfSheet.getRow(rowIndex).getCell(4).setCellValue(Double.parseDouble(dataRiskReductions.get(i)));
			xssfSheet.getRow(rowIndex).getCell(5).setCellValue(Double.parseDouble(dataROSIs.get(i)));
			xssfSheet.getRow(rowIndex).getCell(6).setCellValue(Double.parseDouble(dataRelatifROSIs.get(i)));
			xssfSheet.getRow(rowIndex++).getCell(7).setCellValue(Double.parseDouble(dataPhaseAnnualCosts.get(i)));
		}
		reportExcelSheet.save();
	}

	private void generateBudgetGraphic(ReportExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
		Map<String, List<String>> summaries = ActionPlanSummaryManager.buildTable(analysis.getSummaries(), analysis.getUsedPhases());
		Map<String, Phase> usesPhases = ActionPlanSummaryManager.buildPhase(analysis.getUsedPhases(), ActionPlanSummaryManager.extractPhaseRow(analysis.getSummaries()));
		XSSFSheet xssfSheet = reportExcelSheet.getXssfWorkbook().getSheetAt(0);
		int rowIndex = 1;
		for (Phase phase : usesPhases.values()) {
			if (xssfSheet.getRow(rowIndex) == null)
				xssfSheet.createRow(rowIndex);
			if (xssfSheet.getRow(rowIndex).getCell(0) == null)
				xssfSheet.getRow(rowIndex).createCell(0);
			xssfSheet.getRow(rowIndex++).getCell(0).setCellValue(String.format("P%d", phase.getNumber()));
		}

		List<String> dataInternalWorkload = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD);

		List<String> dataExternalWorkload = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD);

		List<String> dataInternalMaintenace = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE);

		List<String> dataExternalMaintenance = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE);

		List<String> dataInvestment = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INVESTMENT);

		List<String> dataCurrentCost = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_CURRENT_COST);

		List<String> dataTotalPhaseCost = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST);

		for (int j = 1; j < 8; j++) {
			if (xssfSheet.getRow(0) == null)
				xssfSheet.createRow(0);
			if (xssfSheet.getRow(0).getCell(j) == null)
				xssfSheet.getRow(0).createCell(j);
		}
		xssfSheet.getRow(0).getCell(1).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD, null, "Internal workload", locale));
		xssfSheet.getRow(0).getCell(2).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD, null, "External workload", locale));
		xssfSheet.getRow(0).getCell(3).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE, null, "Internal maintenance", locale));
		xssfSheet.getRow(0).getCell(4).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE, null, "External maintenance", locale));
		xssfSheet.getRow(0).getCell(5).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST, null, "Total phase cost", locale));
		xssfSheet.getRow(0).getCell(6).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INVESTMENT, null, "Investment", locale));
		xssfSheet.getRow(0).getCell(7).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_CURRENT_COST, null, "Current cost", locale));
		rowIndex = 1;
		for (int i = 0; i < dataInternalWorkload.size(); i++) {
			for (int j = 1; j < 8; j++) {
				if (xssfSheet.getRow(rowIndex) == null)
					xssfSheet.createRow(rowIndex);
				if (xssfSheet.getRow(rowIndex).getCell(j) == null)
					xssfSheet.getRow(rowIndex).createCell(j, Cell.CELL_TYPE_NUMERIC);
			}
			xssfSheet.getRow(rowIndex).getCell(1).setCellValue(Double.parseDouble(dataInternalWorkload.get(i)));
			xssfSheet.getRow(rowIndex).getCell(2).setCellValue(Double.parseDouble(dataExternalWorkload.get(i)));
			xssfSheet.getRow(rowIndex).getCell(3).setCellValue(Double.parseDouble(dataInternalMaintenace.get(i)));
			xssfSheet.getRow(rowIndex).getCell(4).setCellValue(Double.parseDouble(dataExternalMaintenance.get(i)));
			xssfSheet.getRow(rowIndex).getCell(5).setCellValue(Double.parseDouble(dataInvestment.get(i)));
			xssfSheet.getRow(rowIndex).getCell(6).setCellValue(Double.parseDouble(dataCurrentCost.get(i)));
			xssfSheet.getRow(rowIndex++).getCell(7).setCellValue(Double.parseDouble(dataTotalPhaseCost.get(i)));
		}
		reportExcelSheet.save();
	}

	private void generateALEByAssetTypeGraphic(ReportExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
		if (reportExcelSheet == null)
			return;
		List<Assessment> assessments = analysis.getSelectedAssessments();
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales2 = new LinkedList<ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getAsset().getAssetType().getId());
			if (ale == null) {
				ales.put(assessment.getAsset().getAssetType().getId(), ale = new ALE(assessment.getAsset().getAssetType().getType(), 0));
				ales2.add(ale);
			}
			ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
		}
		Collections.sort(ales2, new AssetComparatorByALE());
		XSSFSheet xssfSheet = reportExcelSheet.getXssfWorkbook().getSheetAt(0);
		int rowCount = 0;
		if (xssfSheet.getRow(rowCount) == null)
			xssfSheet.createRow(rowCount);
		if (xssfSheet.getRow(rowCount).getCell(1) == null)
			xssfSheet.getRow(rowCount).createCell(1);
		xssfSheet.getRow(rowCount++).getCell(1).setCellValue(getMessage("report.chart.asset_type", null, "Asset type", locale));
		for (ALE ale : ales2) {
			if (xssfSheet.getRow(rowCount) == null)
				xssfSheet.createRow(rowCount);
			if (xssfSheet.getRow(rowCount).getCell(0) == null)
				xssfSheet.getRow(rowCount).createCell(0);
			if (xssfSheet.getRow(rowCount).getCell(1) == null)
				xssfSheet.getRow(rowCount).createCell(1);
			xssfSheet.getRow(rowCount).getCell(0).setCellValue(ale.getAssetName());
			xssfSheet.getRow(rowCount++).getCell(1).setCellValue(ale.getValue());
		}
		reportExcelSheet.save();
	}

	private void generateALEByAssetGraphic(ReportExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
		if (reportExcelSheet == null)
			return;
		List<Assessment> assessments = analysis.getSelectedAssessments();
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales2 = new LinkedList<ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getAsset().getAssetType().getId());
			if (ale == null) {
				ales.put(assessment.getAsset().getAssetType().getId(), ale = new ALE(assessment.getAsset().getName(), 0));
				ales2.add(ale);
			}
			ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
		}
		Collections.sort(ales2, new AssetComparatorByALE());
		XSSFSheet xssfSheet = reportExcelSheet.getXssfWorkbook().getSheetAt(0);
		int rowCount = 0;
		if (xssfSheet.getRow(rowCount) == null)
			xssfSheet.createRow(rowCount);
		if (xssfSheet.getRow(rowCount).getCell(1) == null)
			xssfSheet.getRow(rowCount).createCell(1);
		xssfSheet.getRow(rowCount++).getCell(1).setCellValue(getMessage("report.chart.asset", null, "Asset", locale));
		for (ALE ale : ales2) {
			if (xssfSheet.getRow(rowCount) == null)
				xssfSheet.createRow(rowCount);
			if (xssfSheet.getRow(rowCount).getCell(0) == null)
				xssfSheet.getRow(rowCount).createCell(0);
			if (xssfSheet.getRow(rowCount).getCell(1) == null)
				xssfSheet.getRow(rowCount).createCell(1);
			xssfSheet.getRow(rowCount).getCell(0).setCellValue(ale.getAssetName());
			xssfSheet.getRow(rowCount++).getCell(1).setCellValue(ale.getValue());
		}
		reportExcelSheet.save();
	}

	private void generateALEByScenarioGraphic(ReportExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
		if (reportExcelSheet == null)
			return;
		List<Assessment> assessments = analysis.getSelectedAssessments();
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales2 = new LinkedList<ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getScenario().getId());
			if (ale == null) {
				ales.put(assessment.getScenario().getId(), ale = new ALE(assessment.getScenario().getName(), 0));
				ales2.add(ale);
			}
			ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
		}
		Collections.sort(ales2, new AssetComparatorByALE());

		XSSFSheet xssfSheet = reportExcelSheet.getXssfWorkbook().getSheetAt(0);
		int rowCount = 0;
		if (xssfSheet.getRow(rowCount) == null)
			xssfSheet.createRow(rowCount);
		if (xssfSheet.getRow(rowCount).getCell(1) == null)
			xssfSheet.getRow(rowCount).createCell(1);
		xssfSheet.getRow(rowCount++).getCell(1).setCellValue(getMessage("report.chart.scenario", null, "Scenario", locale));
		for (ALE ale : ales2) {
			if (xssfSheet.getRow(rowCount) == null)
				xssfSheet.createRow(rowCount);
			if (xssfSheet.getRow(rowCount).getCell(0) == null)
				xssfSheet.getRow(rowCount).createCell(0);
			if (xssfSheet.getRow(rowCount).getCell(1) == null)
				xssfSheet.getRow(rowCount).createCell(1);
			xssfSheet.getRow(rowCount).getCell(0).setCellValue(ale.getAssetName());
			xssfSheet.getRow(rowCount++).getCell(1).setCellValue(ale.getValue());
		}
		reportExcelSheet.save();

	}

	private void generateALEByScenarioTypeGraphic(ReportExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
		if (reportExcelSheet == null)
			return;
		List<Assessment> assessments = analysis.getSelectedAssessments();
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales2 = new LinkedList<ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getScenario().getScenarioType().getId());
			if (ale == null) {
				ales.put(assessment.getScenario().getScenarioType().getId(), ale = new ALE(assessment.getScenario().getScenarioType().getName(), 0));
				ales2.add(ale);
			}
			ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
		}
		Collections.sort(ales2, new AssetComparatorByALE());
		XSSFSheet xssfSheet = reportExcelSheet.getXssfWorkbook().getSheetAt(0);
		int rowCount = 0;
		if (xssfSheet.getRow(rowCount) == null)
			xssfSheet.createRow(rowCount);
		if (xssfSheet.getRow(rowCount).getCell(1) == null)
			xssfSheet.getRow(rowCount).createCell(1);
		xssfSheet.getRow(rowCount++).getCell(1).setCellValue(getMessage("report.chart.scenario_type", null, "Scenario type", locale));
		for (ALE ale : ales2) {
			if (xssfSheet.getRow(rowCount) == null)
				xssfSheet.createRow(rowCount);
			if (xssfSheet.getRow(rowCount).getCell(0) == null)
				xssfSheet.getRow(rowCount).createCell(0);
			if (xssfSheet.getRow(rowCount).getCell(1) == null)
				xssfSheet.getRow(rowCount).createCell(1);
			xssfSheet.getRow(rowCount).getCell(0).setCellValue(ale.getAssetName());
			xssfSheet.getRow(rowCount++).getCell(1).setCellValue(ale.getValue());
		}
		reportExcelSheet.save();

	}

	@SuppressWarnings("unchecked")
	private void generateComplianceGraphic(ReportExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
		if (reportExcelSheet == null)
			return;
		String norm = reportExcelSheet.getName().endsWith("27001") ? "27001" : "27002";
		List<Measure> measures = (List<Measure>) analysis.findMeasureByNorm(norm);
		XSSFSheet xssfSheet = reportExcelSheet.getXssfWorkbook().getSheetAt(0);
		Map<String, Object[]> compliances = ChartGenerator.ComputeComplianceBefore(measures);
		int rowCount = 0;
		String phaseLabel = getMessage("label.chart.series.current_level", null, "Current Level", locale);
		if(xssfSheet.getRow(rowCount) == null)
			xssfSheet.createRow(rowCount);
		xssfSheet.getRow(rowCount).createCell(0);
		xssfSheet.getRow(rowCount).createCell(1);
		xssfSheet.getRow(rowCount).getCell(0).setCellValue(getMessage("report.compliance.chapter", null, "Chapter", locale));
		xssfSheet.getRow(rowCount++).getCell(1).setCellValue(phaseLabel);
		for (String key : compliances.keySet()) {
			Object[] compliance = compliances.get(key);
			if (xssfSheet.getRow(rowCount) == null)
				xssfSheet.createRow(rowCount);
			if (xssfSheet.getRow(rowCount).getCell(0) == null)
				xssfSheet.getRow(rowCount).createCell(0);
			if (xssfSheet.getRow(rowCount).getCell(1) == null)
				xssfSheet.getRow(rowCount).createCell(1, Cell.CELL_TYPE_NUMERIC);
			xssfSheet.getRow(rowCount).getCell(0).setCellValue(key);
			xssfSheet.getRow(rowCount++).getCell(1).setCellValue((((Double) compliance[1]).doubleValue() / ((Integer) compliance[0]).doubleValue()) * 0.01);
		}

		Map<Integer, Boolean> actionPlanMeasures = analysis.findIdMeasuresImplementedByActionPlanType(ActionPlanMode.APPN);
		List<Phase> phases = analysis.findUsablePhase();
		int columnIndex = 2;
		for (Phase phase : phases) {
			compliances = ChartGenerator.ComputeCompliance(measures, phase, actionPlanMeasures, compliances);
			if (xssfSheet.getRow(rowCount = 0) == null)
				xssfSheet.createRow(rowCount);
			if (xssfSheet.getRow(rowCount).getCell(columnIndex) == null)
				xssfSheet.getRow(rowCount).createCell(columnIndex);
			xssfSheet.getRow(rowCount++).getCell(columnIndex).setCellValue(getMessage("label.chart.phase", null, "Phase", locale) + " " + phase.getNumber());
			for (String key : compliances.keySet()) {
				Object[] compliance = compliances.get(key);
				if (xssfSheet.getRow(rowCount) == null)
					xssfSheet.createRow(rowCount);
				if (xssfSheet.getRow(rowCount).getCell(columnIndex) == null)
					xssfSheet.getRow(rowCount).createCell(columnIndex, Cell.CELL_TYPE_NUMERIC);
				xssfSheet.getRow(rowCount++).getCell(columnIndex).setCellValue((((Double) compliance[1]).doubleValue() / ((Integer) compliance[0]).doubleValue()) * 0.01);
			}
			columnIndex++;
		}
		reportExcelSheet.save();
	}

	private void generatePlaceholders() {
		/*
		 * document.createParagraph().createRun().setText("<Scope>");
		 * document.createParagraph().createRun().setText("<Asset>");
		 * document.createParagraph().createRun().setText("<Scenario>");
		 * document.createParagraph().createRun().setText("<Assessment>");
		 * document.createParagraph().createRun().setText("<Threat>");
		 * document.createParagraph().createRun().setText("<Vul>");
		 * document.createParagraph().createRun().setText("<Risk>");
		 * document.createParagraph().createRun().setText("<Impact>");
		 * document.createParagraph().createRun().setText("<Proba>");
		 * document.createParagraph().createRun().setText("<ActionPlan>");
		 * document.createParagraph().createRun().setText("<Summary>");
		 */
		document.createParagraph().createRun().setText("<Measures>");
	}

	private XWPFRun addCellNumber(XWPFTableCell cell, String number) {
		XWPFParagraph paragraph = cell.getParagraphs().size() == 1 ? cell.getParagraphs().get(0) : cell.addParagraph();
		paragraph.setStyle("TableParagraphTS");
		paragraph.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun run = paragraph.createRun();
		run.setText(number);
		return run;
	}

	private XWPFParagraph addCellParagraph(XWPFTableCell cell, String text) {
		XWPFParagraph paragraph = cell.getParagraphs().size() == 1 ? cell.getParagraphs().get(0) : cell.addParagraph();
		paragraph.setStyle("TableParagraphTS");
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		String[] texts = text.split("(\r\n|\n\r|\r|\n)");
		for (int i = 0; i < texts.length; i++) {
			if (i > 0)
				paragraph.createRun().addBreak();
			paragraph.createRun().setText(texts[i]);
		}
		return paragraph;
	}

	private void generateMeasures() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Measures>");

		// run = paragraph.getRuns().get(0);

		List<AnalysisNorm> anorms = analysis.getAnalysisNorms();

		if (paragraph != null && anorms.size() > 0) {

			while (!paragraph.getRuns().isEmpty())
				paragraph.removeRun(0);

			boolean isFirst = true;

			for (AnalysisNorm anorm : anorms) {

				// initialise table with 1 row and 1 column after the paragraph
				// cursor

				if (isFirst)
					isFirst = false;
				else {
					paragraph = document.createParagraph();
					paragraph.createRun().addCarriageReturn();
				}

				table = document.insertNewTbl(paragraph.getCTP().newCursor());

				table.setStyleID("TableTSMeasure");

				CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
				width.setW(BigInteger.valueOf(10000));

				// set header

				row = table.getRow(0);

				if (row.getCtRow().getTcList().isEmpty())
					row.addNewTableCell();
				if (row.getCell(0).getCTTc().getTcPr() == null)
					row.getCell(0).getCTTc().addNewTcPr();

				row.getCell(0).getCTTc().getTcPr().addNewGridSpan().setVal(BigInteger.valueOf(14));

				row.getCell(0).setText(anorm.getNorm().getLabel());

				row = table.createRow();

				if (!row.getTableCells().isEmpty())
					row.getCell(0).setColor(SUPER_HEAD_COLOR);

				while (row.getTableCells().size() < 14)
					row.createCell().setColor(SUPER_HEAD_COLOR);

				row.getCell(0).setText(getMessage("report.measure.reference", null, "Ref.", locale));
				row.getCell(1).setText(getMessage("report.measure.domain", null, "Domain", locale));
				row.getCell(2).setText(getMessage("report.measure.status", null, "ST", locale));
				row.getCell(3).setText(getMessage("report.measure.implementation_rate", null, "IR(%)", locale));
				row.getCell(4).setText(getMessage("report.measure.internal.workload", null, "IS(md)", locale));
				row.getCell(5).setText(getMessage("report.measure.external.workload", null, "ES(md)", locale));
				row.getCell(6).setText(getMessage("report.measure.investment", null, "INV(k€)", locale));
				row.getCell(7).setText(getMessage("report.measure.life_time", null, "LT(y)", locale));
				row.getCell(8).setText(getMessage("report.measure.internal.maintenance", null, "IM(md)", locale));
				row.getCell(9).setText(getMessage("report.measure.external.maintenance", null, "EM(md)", locale));
				row.getCell(10).setText(getMessage("report.measure.recurrent.investment", null, "RINV(k€)", locale));
				row.getCell(11).setText(getMessage("report.measure.cost", null, "CS(k€)", locale));
				row.getCell(12).setText(getMessage("report.measure.comment", null, "Comment", locale));
				row.getCell(13).setText(getMessage("report.measure.to_do", null, "To Do", locale));
				// set data
				for (Measure measure : anorm.getMeasures()) {

					row = table.createRow();
					while (row.getTableCells().size() < 14)
						row.createCell();

					row.getCell(0).setText(measure.getMeasureDescription().getReference());
					MeasureDescriptionText description = measure.getMeasureDescription().findByLanguage(analysis.getLanguage());
					row.getCell(1).setText(description == null ? "" : description.getDomain());
					if (measure.getMeasureDescription().getLevel() < 3) {
						while (row.getCtRow().getTcList().size() > 2)
							row.getCtRow().getTcList().remove(2);
						if (row.getCell(1).getCTTc().getTcPr() == null)
							row.getCell(1).getCTTc().addNewTcPr();
						row.getCell(1).getCTTc().getTcPr().addNewGridSpan().setVal(BigInteger.valueOf(13));
						for (int i = 0; i < 2; i++)
							row.getCell(i).setColor(measure.getMeasureDescription().getLevel() < 2 ? SUPER_HEAD_COLOR : HEADER_COLOR);
					} else {

						row.getCell(2).setText(measure.getStatus());
						addCellNumber(row.getCell(3), numberFormat.format(measure.getImplementationRateValue()));
						addCellNumber(row.getCell(4), kEuroFormat.format(measure.getInternalWL()));
						addCellNumber(row.getCell(5), kEuroFormat.format(measure.getExternalWL()));
						addCellNumber(row.getCell(6), numberFormat.format(measure.getInvestment() * 0.001));
						addCellNumber(row.getCell(7), numberFormat.format(measure.getLifetime()));
						addCellNumber(row.getCell(8), kEuroFormat.format(measure.getInternalMaintenance()));
						addCellNumber(row.getCell(9), kEuroFormat.format(measure.getExternalMaintenance()));
						addCellNumber(row.getCell(10), numberFormat.format(measure.getRecurrentInvestment() * 0.001));
						addCellNumber(row.getCell(11), numberFormat.format(measure.getCost() * 0.001));
						addCellParagraph(row.getCell(12), measure.getComment());
						addCellParagraph(row.getCell(13), measure.getToDo());

						if (Constant.MEASURE_STATUS_NOT_APPLICABLE.equalsIgnoreCase(measure.getStatus()) || measure.getImplementationRateValue() >= 100) {
							for (int i = 0; i < 14; i++)
								row.getCell(i).setColor(DEFAULT_CELL_COLOR);
						} else {
							row.getCell(0).setColor(SUB_HEADER_COLOR);
							row.getCell(1).setColor(SUB_HEADER_COLOR);
							row.getCell(11).setColor(measure.getCost() == 0 ? ZERO_COST_COLOR : SUB_HEADER_COLOR);
						}

					}
				}
			}
		}
	}

	private void generateActionPlanSummary() throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Summary>");

		if (paragraph == null)
			return;

		// run = paragraph.getRuns().get(0);

		List<SummaryStage> summary = analysis.getSummary(ActionPlanMode.APPN);

		while (!paragraph.getRuns().isEmpty())
			paragraph.removeRun(0);

		if (summary.isEmpty())
			return;

		// initialise table with 1 row and 1 column after the paragraph
		// cursor

		table = document.insertNewTbl(paragraph.getCTP().newCursor());

		table.setStyleID("TableTSSummary");

		CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
		width.setW(BigInteger.valueOf(10000));

		// set header

		row = table.getRow(0);

		for (int i = 1; i < 3; i++)
			row.addNewTableCell();

		int rownumber = 0;

		while (rownumber < 22) {

			if (rownumber == 0)
				row = table.getRow(rownumber);
			else
				row = table.createRow();

			switch (rownumber) {
			case 0: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.phase.characteristics", null, "Phase characteristics", locale));
				for (SummaryStage stage : summary) {
					XWPFTableCell cell = row.getCell(++cellnumber);
					if (cell == null)
						cell = row.addNewTableCell();
					cell.setText(stage.getStage().equalsIgnoreCase("Start(P0)") ? getMessage("report.summary_stage.phase.start", null, stage.getStage(), locale) : getMessage(
							"report.summary_stage.phase", stage.getStage().split(" "), stage.getStage(), locale));
				}
				break;
			}
			case 1: {
				row.getCell(0).setText(getMessage("report.summary_stage.date.beginning", null, "Beginning date", locale));
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				for (int i = 1; i < summary.size(); i++) {
					addCellParagraph(row.getCell(i + 1), dateFormat.format(analysis.findPhaseByNumber(i).getBeginDate()));
				}
				break;
			}
			case 2: {
				row.getCell(0).setText(getMessage("report.summary_stage.date.end", null, "End date", locale));
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				for (int i = 1; i < summary.size(); i++)
					addCellParagraph(row.getCell(i + 1), dateFormat.format(analysis.findPhaseByNumber(i).getEndDate()));
				break;
			}
			case 3: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.compliance.level", new Object[] { "27001" }, "Compliance level 27001 (%)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), String.valueOf((int) (stage.getConformance27001() * 100)));
				break;
			}
			case 4: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.compliance.level", new Object[] { "27002" }, "Compliance level 27002 (%)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), String.valueOf((int) (stage.getConformance27002() * 100)));
				break;
			}
			case 5: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.number_of_measure_for_phase", null, "Number of measures for phase", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), "" + stage.getMeasureCount());
				break;
			}
			case 6: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.implementted_measures", null, "Implemented measures (number)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), "" + stage.getImplementedMeasuresCount());
				break;
			}
			case 7: {
				while (row.getCtRow().getTcList().size() > 1)
					row.getCtRow().removeTc(1);
				if (row.getCell(0).getCTTc().getTcPr() == null)
					row.getCell(0).getCTTc().addNewTcPr();
				row.getCell(0).getCTTc().getTcPr().addNewGridSpan().setVal(BigInteger.valueOf(summary.size() + 1));
				row.getCell(0).setText(getMessage("report.summary_stage.profitability", null, "Profitability", locale));

				break;
			}
			case 8: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.ale_at_end", null, "ALE (k€/y)... at end", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(Math.floor(stage.getTotalALE() * 0.001)));
				break;
			}
			case 9: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.risk_reduction", null, "Risk reduction (k€/y)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(Math.floor(stage.getDeltaALE() * 0.001)));
				break;
			}
			case 10: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.average_yearly_cost_of_phase", null, "Average yearly cost of phase (k€/y)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(Math.floor(stage.getCostOfMeasures() * 0.001)));
				break;
			}
			case 11: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.rosi", null, "ROSI (k€/y)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(Math.floor(stage.getROSI() * 0.001)));
				break;
			}
			case 12: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.rosi.relative", null, "Relative ROSI", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(Math.floor(stage.getRelativeROSI() * 0.001)));
				break;
			}
			case 13: {
				while (row.getCtRow().getTcList().size() > 1)
					row.getCtRow().removeTc(1);
				if (row.getCell(0).getCTTc().getTcPr() == null)
					row.getCell(0).getCTTc().addNewTcPr();
				row.getCell(0).getCTTc().getTcPr().addNewGridSpan().setVal(BigInteger.valueOf(summary.size() + 1));
				row.getCell(0).setText(getMessage("report.summary_stage.resource.planning", null, "Resource planning", locale));

				// mrege columns

				break;
			}
			case 14: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.workload.internal", null, "Internal workload (md)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getInternalWorkload()));
				break;
			}
			case 15: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.workload.external", null, "External workload (md)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getExternalWorkload()));
				break;
			}
			case 16: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.investment", null, "Investment (k€)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(Math.floor(stage.getInvestment() * 0.001)));
				break;
			}
			case 17: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.investment", null, "Internal maintenance (md)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getInternalMaintenance()));
				break;
			}
			case 18: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.maintenance.external", null, "External maintenance (md)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getExternalMaintenance()));
				break;
			}
			case 19: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.investment.recurrent", null, "Recurrent investment (k€)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(Math.floor(stage.getRecurrentInvestment() * 0.001)));
				break;
			}
			case 20: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.cost.recurrent", null, "Recurrent costs (k€)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(Math.floor(stage.getRecurrentCost() * 0.001)));
				break;
			}
			case 21: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(getMessage("report.summary_stage.cost.total_of_phase", null, "Total cost of phase (k€)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(Math.floor(stage.getTotalCostofStage() * 0.001)));
				break;
			}
			}
			rownumber++;
		}

	}

	private void generateActionPlan() throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<ActionPlan>");

		// run = paragraph.getRuns().get(0);

		List<ActionPlanEntry> actionplan = analysis.getActionPlan(ActionPlanMode.APPN);

		if (paragraph != null && actionplan.size() > 0) {

			while (!paragraph.getRuns().isEmpty())
				paragraph.removeRun(0);

			// initialise table with 1 row and 1 column after the paragraph
			// cursor

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSActionPlan");

			CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
			width.setW(BigInteger.valueOf(10000));

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 12; i++)
				row.addNewTableCell();

			row.getCell(0).setText(getMessage("report.action_plan.row_number", null, "Nr", locale));
			row.getCell(1).setText(getMessage("report.action_plan.norm", null, "Norm", locale));
			row.getCell(2).setText(getMessage("report.action_plan.reference", null, "Ref.", locale));
			row.getCell(3).setText(getMessage("report.action_plan.description", null, "Description", locale));
			row.getCell(4).setText(getMessage("report.action_plan.ale", null, "ALE (k€/y)", locale));
			row.getCell(5).setText(getMessage("report.action_plan.delta_row_number", null, "ΔNr", locale));
			row.getCell(6).setText(getMessage("report.action_plan.cost", null, "CS (k€/y)", locale));
			row.getCell(7).setText(getMessage("report.action_plan.rosi", null, "ROSI (k€/y)", locale));
			row.getCell(8).setText(getMessage("report.action_plan.internal.workload", null, "IS", locale));
			row.getCell(9).setText(getMessage("report.action_plan.external.workload", null, "ES", locale));
			row.getCell(10).setText(getMessage("report.action_plan.investment", null, "INV (k€)", locale));
			row.getCell(11).setText(getMessage("report.action_plan.probability", null, "P", locale));
			int nr = 0;
			// set data
			for (ActionPlanEntry entry : actionplan) {
				row = table.createRow();
				nr++;
				Hibernate.initialize(entry);
				Hibernate.initialize(entry.getMeasure());
				Hibernate.initialize(entry.getActionPlanAssets());
				row.getCell(0).setText("" + nr);
				row.getCell(1).setText(entry.getMeasure().getAnalysisNorm().getNorm().getLabel());
				row.getCell(2).setText(entry.getMeasure().getMeasureDescription().getReference());
				paragraph = addCellParagraph(row.getCell(3), entry.getMeasure().getMeasureDescription().findByLanguage(analysis.getLanguage()).getDomain() + ":");
				for (XWPFRun run : paragraph.getRuns())
					run.setBold(true);
				paragraph.createRun().addBreak();
				addCellParagraph(row.getCell(3), entry.getMeasure().getToDo());
				addCellNumber(row.getCell(4), numberFormat.format(entry.getTotalALE() * 0.001));
				addCellNumber(row.getCell(5), entry.getPosition());
				addCellNumber(row.getCell(6), numberFormat.format(entry.getMeasure().getCost() * 0.001));
				addCellNumber(row.getCell(7), numberFormat.format(entry.getROI() * 0.001));
				addCellNumber(row.getCell(8), "" + entry.getMeasure().getInternalWL());
				addCellNumber(row.getCell(9), "" + entry.getMeasure().getExternalWL());
				addCellNumber(row.getCell(10), numberFormat.format(entry.getMeasure().getInvestment() * 0.001));
				addCellNumber(row.getCell(11), "" + entry.getMeasure().getPhase().getNumber());
				for (int i = 0; i < 11; i++)
					row.getCell(i).setColor(SUB_HEADER_COLOR);

			}

			// Set the table style. If the style is not defined, the table style
			// will become
			// "Normal".
			// table.getCTTbl().getTblPr().addNewTblStyle().setVal("TableTS");

			// table.setStyleID("TableTS");

		}
	}

	private void generateExtendedParameters(String type) throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;
		String parmetertype = "";
		if (type.equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
			parmetertype = "Proba";
		else if (type.equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME))
			parmetertype = "Impact";

		paragraph = findParagraphByText("<" + parmetertype + ">");

		List<Parameter> parameters = analysis.getParameters();

		List<ExtendedParameter> extendedParameters = new ArrayList<ExtendedParameter>();

		for (Parameter parameter : parameters) {
			if (parameter.getType().getLabel().equals(type))
				extendedParameters.add((ExtendedParameter) parameter);
		}

		if (paragraph != null && extendedParameters.size() > 0) {

			while (!paragraph.getRuns().isEmpty())
				paragraph.removeRun(0);

			// initialise table with 1 row and 1 column after the paragraph
			// cursor

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTS" + parmetertype);

			CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
			width.setW(BigInteger.valueOf(10000));

			// set header

			row = table.getRow(0);

			if (row.getTableCells().isEmpty())
				row.createCell();
			if (row.getCell(0).getCTTc().getTcPr() == null)
				row.getCell(0).getCTTc().addNewTcPr();
			row.getCell(0).getCTTc().getTcPr().addNewGridSpan().setVal(BigInteger.valueOf(6));
			row.getCell(0).setText(getMessage("report.parameter.title." + parmetertype.toLowerCase(), null, parmetertype, locale));

			row = table.createRow();

			for (int i = 1; i < 6; i++)
				row.addNewTableCell().setColor(HEADER_COLOR);

			row.getCell(0).setText(getMessage("report.parameter.level", null, "Level", locale));
			row.getCell(1).setText(getMessage("report.parameter.acronym", null, "Acro", locale));
			row.getCell(2).setText(getMessage("report.parameter.qualification", null, "Qualification", locale));
			row.getCell(3).setText(getMessage("report.parameter.value", null, "Value", locale));
			row.getCell(4).setText(getMessage("report.parameter.value.from", null, "Value From [", locale));
			row.getCell(5).setText(getMessage("report.parameter.value.to", null, "Value To [", locale));

			int countrow = 0;
			// set data
			for (ExtendedParameter extendedParameter : extendedParameters) {
				row = table.createRow();

				while (row.getCtRow().getTcList().size() < 6)
					row.addNewTableCell();
				row.getCell(0).setText("" + extendedParameter.getLevel());
				row.getCell(1).setText(extendedParameter.getAcronym());
				row.getCell(2).setText(extendedParameter.getDescription());
				Double value = 0.;
				value = extendedParameter.getValue();
				if (type.equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
					value *= 0.001;
				addCellNumber(row.getCell(3), kEuroFormat.format(value));
				if (countrow % 2 != 0)
					row.getCell(3).setColor(SUB_HEADER_COLOR);
				value = extendedParameter.getBounds().getFrom();
				if (type.equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
					value *= 0.001;
				addCellNumber(row.getCell(4), kEuroFormat.format(value));
				if (extendedParameter.getLevel() == 10)
					addCellNumber(row.getCell(5), "+∞");
				else {
					value = extendedParameter.getBounds().getTo();
					if (type.equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
						value *= 0.001;
					addCellNumber(row.getCell(5), kEuroFormat.format(value));
				}
				for (int i = 4; i < 6; i++)
					row.getCell(i).setColor(SUB_HEADER_COLOR);

				countrow++;
			}

			// Set the table style. If the style is not defined, the table style
			// will become
			// "Normal".
			// table.getCTTbl().getTblPr().addNewTblStyle().setVal("TableTS");

			// table.setStyleID("TableTS");

		}
	}

	private void generateThreats() {
		XWPFParagraph paragraph = null;
		XWPFTableRow row = null;
		XWPFTable table = null;

		List<RiskInformation> riskInformations = analysis.getRiskInformations();

		Map<String, List<RiskInformation>> riskmapping = RiskInformationManager.Split(riskInformations);
		boolean chapter = false;

		for (String key : riskmapping.keySet()) {

			paragraph = findParagraphByText("<" + key + ">");

			List<RiskInformation> elements = riskmapping.get(key);

			if (paragraph != null && elements.size() > 0) {

				while (!paragraph.getRuns().isEmpty())
					paragraph.removeRun(0);

				RiskInformation previouselement = null;

				// set data

				for (RiskInformation riskinfo : elements) {

					if ((previouselement == null) || (!riskinfo.getCategory().equals(previouselement.getCategory()))) {

						if (previouselement != null)
							document.insertNewParagraph(paragraph.getCTP().newCursor());

						table = document.insertNewTbl(paragraph.getCTP().newCursor());

						table.setStyleID("TableTS" + key);

						CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
						width.setW(BigInteger.valueOf(10000));

						// set header
						row = table.getRow(0);
						row.getCell(0).setText(getMessage(String.format("report.risk_information.title.%s", "id"), null, "Id", locale));
						row.addNewTableCell();
						table.getRow(0).getCell(1).setText(getMessage(String.format("report.risk_information.title.%s", key.toLowerCase()), null, key.toLowerCase(), locale));
						if (riskinfo.getCategory().equals("Threat")) {
							row.addNewTableCell();
							row.getCell(2).setText(getMessage(String.format("report.risk_information.title.%s", "acro"), null, "Acro", locale));
							row.addNewTableCell();
							row.getCell(3).setText(getMessage(String.format("report.risk_information.title.%s", "expo"), null, "Expo.", locale));
							row.addNewTableCell();
							row.getCell(4).setText(getMessage(String.format("report.risk_information.title.%s", "comment"), null, "Comment", locale));
						} else {
							row.addNewTableCell();
							row.getCell(2).setText(getMessage(String.format("report.risk_information.title.%s", "expo"), null, "Expo.", locale));
							row.addNewTableCell();
							row.getCell(3).setText(getMessage(String.format("report.risk_information.title.%s", "comment"), null, "Comment", locale));
						}
					}

					previouselement = riskinfo;
					row = table.createRow();
					chapter = riskinfo.getChapter().matches("\\d(\\.0){2}");
					row.getCell(0).setText(riskinfo.getChapter());
					row.getCell(1).setText(
							getMessage(String.format("label.risk_information.%s.%s", riskinfo.getCategory().toLowerCase(), riskinfo.getChapter().replace(".", "_")), null,
									riskinfo.getLabel(), locale));
					if (riskinfo.getCategory().equals("Threat")) {
						for (int i = 0; i < 3; i++)
							row.getCell(i).setColor(chapter ? HEADER_COLOR : SUB_HEADER_COLOR);
						row.getCell(2).setText(riskinfo.getAcronym());
						row.getCell(3).setText("" + riskinfo.getExposed());
						addCellParagraph(row.getCell(4), riskinfo.getComment());
					} else {
						for (int i = 0; i < 2; i++)
							row.getCell(i).setColor(chapter ? HEADER_COLOR : SUB_HEADER_COLOR);
						row.getCell(2).setText("" + riskinfo.getExposed());
						addCellParagraph(row.getCell(3), riskinfo.getComment());
					}
				}
			}
		}
	}

	private void generateAssessements() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Assessment>");

		List<Assessment> assessments = analysis.getSelectedAssessments();

		double totalale = 0;

		for (Assessment assessment : assessments)
			totalale += assessment.getALE();

		if (paragraph != null && assessments.size() > 0) {

			while (!paragraph.getRuns().isEmpty())
				paragraph.removeRun(0);

			Map<String, ALE> alesmap = new LinkedHashMap<String, ALE>();
			Map<String, List<Assessment>> assessementsmap = new LinkedHashMap<String, List<Assessment>>();

			AssessmentManager.SplitAssessment(assessments, alesmap, assessementsmap);

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSAssessment");

			CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
			width.setW(BigInteger.valueOf(10000));

			// set header

			row = table.getRow(0);

			// set header
			row.getCell(0).setText(getMessage("report.assessment.assets", null, "Assets", locale));

			row.getCell(0).getCTTc().addNewTcPr();

			if (row.getCell(0).getCTTc().getTcPr().getGridSpan() == null)
				row.getCell(0).getCTTc().getTcPr().addNewGridSpan();
			row.getCell(0).getCTTc().getTcPr().getGridSpan().setVal(BigInteger.valueOf(5));

			row = table.createRow();

			for (int i = 1; i < 5; i++)
				row.addNewTableCell();

			row.getCell(0).setText(getMessage("report.assessment.scenarios", null, "Scenarios", locale));
			row.getCell(1).setText(getMessage("report.assessment.impact.financial", null, "Fin.", locale));
			row.getCell(1).setColor("c6d9f1");
			row.getCell(2).setText(getMessage("report.assessment.probability", null, "P.", locale));
			row.getCell(2).setColor("c6d9f1");
			row.getCell(3).setText(getMessage("report.assessment.ale", null, "ALE(k€/y", locale));
			row.getCell(3).setColor("c6d9f1");
			row.getCell(4).setText(getMessage("report.assessment.comment", null, "Comment", locale));
			row.getCell(4).setColor("c6d9f1");

			row = table.createRow();
			for (int i = 1; i < 5; i++)
				row.addNewTableCell().setColor("c6d9f1");

			row.getCell(0).setText(getMessage("report.assessment.total_ale.assets", null, "Total ALE of Assets", locale));
			addCellNumber(row.getCell(3), numberFormat.format(totalale * 0.001));

			for (String assetname : assessementsmap.keySet()) {
				List<Assessment> assessmentsofasset = assessementsmap.get(assetname);
				ALE ale = alesmap.get(assetname);
				row = table.createRow();
				for (int i = 1; i < 5; i++)
					row.addNewTableCell().setColor("c6d9f1");
				row.getCell(0).setText(ale.getAssetName());
				addCellNumber(row.getCell(3), numberFormat.format(ale.getValue() * 0.001));
				for (Assessment assessment : assessmentsofasset) {
					row = table.createRow();
					for (int i = 1; i < 5; i++)
						row.addNewTableCell();
					row.getCell(0).setText(assessment.getScenario().getName());
					addCellNumber(row.getCell(1), formatedImpact(assessment.getImpactFin()));
					addCellNumber(row.getCell(2), formatLikelihood(assessment.getLikelihood()));
					addCellNumber(row.getCell(3), numberFormat.format(assessment.getALE() * 0.001));
					addCellParagraph(row.getCell(4), assessment.getComment());
				}
			}

		}
	}

	private String formatLikelihood(String likelihood) {
		try {
			return kEuroFormat.format(Double.parseDouble(likelihood));
		} catch (Exception e) {
			return likelihood;
		}
	}

	private String formatedImpact(String impactFin) {
		try {
			return kEuroFormat.format(Double.parseDouble(impactFin) * 0.001);
		} catch (Exception e) {
			return impactFin;
		}
	}

	private void generateScenarios() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Scenario>");

		List<Scenario> scenarios = analysis.getSelectedScenarios();

		if (paragraph != null && scenarios.size() > 0) {

			while (!paragraph.getRuns().isEmpty())
				paragraph.removeRun(0);

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSScenario");

			CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
			width.setW(BigInteger.valueOf(10000));

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 3; i++)
				row.addNewTableCell();

			// set header
			table.getRow(0).getCell(0).setText(getMessage("report.scenario.title.number.row", null, "Nr", locale));
			table.getRow(0).getCell(1).setText(getMessage("report.scenario.title.number.name", null, "Name", locale));
			table.getRow(0).getCell(2).setText(getMessage("report.scenario.title.number.description", null, "Description", locale));

			int number = 0;

			// set data
			for (Scenario scenario : scenarios) {
				row = table.createRow();
				number++;
				row.getCell(0).setText("" + number);
				addCellParagraph(row.getCell(1), scenario.getName());
				addCellParagraph(row.getCell(2), scenario.getDescription());
			}
		}
	}

	private void generateAssets() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Asset>");

		List<Asset> assets = analysis.getSelectedAssets();

		if (paragraph != null && assets.size() > 0) {

			while (!paragraph.getRuns().isEmpty())
				paragraph.removeRun(0);

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSAsset");

			CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
			width.setW(BigInteger.valueOf(10000));

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 6; i++)
				row.addNewTableCell();

			// set header
			table.getRow(0).getCell(0).setText(getMessage("report.asset.title.number.row", null, "Nr", locale));
			table.getRow(0).getCell(1).setText(getMessage("report.asset.title.name", null, "Name", locale));
			table.getRow(0).getCell(2).setText(getMessage("report.asset.title.type", null, "Type", locale));
			table.getRow(0).getCell(3).setText(getMessage("report.asset.title.value", null, "Value(k€)", locale));
			table.getRow(0).getCell(4).setText(getMessage("report.asset.title.ale", null, "ALE(k€)", locale));
			table.getRow(0).getCell(5).setText(getMessage("report.asset.title.comment", null, "Comment", locale));

			int number = 0;

			// set data
			for (Asset asset : assets) {
				row = table.createRow();
				number++;
				row.getCell(0).setText("" + (number));
				row.getCell(1).setText(asset.getName());
				row.getCell(2).setText(asset.getAssetType().getType());
				addCellNumber(row.getCell(3), kEuroFormat.format(asset.getValue() * 0.001));
				row.getCell(4).setColor("c6d9f1");
				addCellNumber(row.getCell(4), kEuroFormat.format(asset.getALE() * 0.001));
				addCellParagraph(row.getCell(5), asset.getComment());
			}
		}
	}

	private void generateItemInformation() throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Scope>");

		List<ItemInformation> iteminformations = analysis.getItemInformations();

		Collections.sort(iteminformations, new ComparatorItemInformation());

		if (paragraph != null && iteminformations.size() > 0) {

			while (!paragraph.getRuns().isEmpty())
				paragraph.removeRun(0);

			// initialise table with 1 row and 1 column after the paragraph
			// cursor

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSScope");

			CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
			width.setW(BigInteger.valueOf(10000));

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 2; i++)
				row.addNewTableCell();

			row.getCell(0).setText(getMessage("report.scope.title.description", null, "Description", locale));
			row.getCell(1).setText(getMessage("report.scope.title.value", null, "Value", locale));

			// set data
			for (ItemInformation iteminfo : iteminformations) {
				row = table.createRow();
				row.getCell(0).setText(getMessage("report.scope.name." + iteminfo.getDescription().toLowerCase(), null, iteminfo.getDescription(), locale));
				addCellParagraph(row.getCell(1), iteminfo.getValue());
			}
		}
	}

	private String getMessage(String code, Object[] parameters, String defaultMessage, Locale locale) {
		// System.out.println(String.format("%s=%s", code, defaultMessage));
		return messageSource.getMessage(code, parameters, defaultMessage, locale);
	}

	private XWPFParagraph findParagraphByText(String text) {
		List<XWPFParagraph> paragraphs = document.getParagraphs();
		for (XWPFParagraph paragraph : paragraphs) {
			if (paragraph.getParagraphText().equals(text))
				return paragraph;
		}
		return null;
	}

	/**
	 * getAnalysis: <br>
	 * Returns the analysis field value.
	 * 
	 * @return The value of the analysis field
	 */
	public Analysis getAnalysis() {
		return analysis;
	}

	/**
	 * setAnalysis: <br>
	 * Sets the Field "analysis" with a value.
	 * 
	 * @param analysis
	 *            The Value to set the analysis field
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	/**
	 * getContext: <br>
	 * Returns the context field value.
	 * 
	 * @return The value of the context field
	 */
	public ServletContext getContext() {
		return context;
	}

	/**
	 * setContext: <br>
	 * Sets the Field "context" with a value.
	 * 
	 * @param context
	 *            The Value to set the context field
	 */
	public void setContext(ServletContext context) {
		this.context = context;
	}

	/**
	 * getServiceAnalysis: <br>
	 * Returns the serviceAnalysis field value.
	 * 
	 * @return The value of the serviceAnalysis field
	 */
	public ServiceAnalysis getServiceAnalysis() {
		return serviceAnalysis;
	}

	/**
	 * setServiceAnalysis: <br>
	 * Sets the Field "serviceAnalysis" with a value.
	 * 
	 * @param serviceAnalysis
	 *            The Value to set the serviceAnalysis field
	 */
	public void setServiceAnalysis(ServiceAnalysis serviceAnalysis) {
		this.serviceAnalysis = serviceAnalysis;
	}

	/**
	 * getDocument: <br>
	 * Returns the document field value.
	 * 
	 * @return The value of the document field
	 */
	public XWPFDocument getDocument() {
		return document;
	}

	/**
	 * setDocument: <br>
	 * Sets the Field "document" with a value.
	 * 
	 * @param document
	 *            The Value to set the document field
	 */
	public void setDocument(XWPFDocument document) {
		this.document = document;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportVersion() {
		return reportVersion;
	}

	public void setReportVersion(String reportVersion) {
		this.reportVersion = reportVersion;
	}
}