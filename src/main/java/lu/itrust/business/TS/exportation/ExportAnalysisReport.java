package lu.itrust.business.TS.exportation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.context.MessageSource;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.exportation.helper.ReportExcelSheet;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.ALE;
import lu.itrust.business.TS.model.assessment.helper.AssessmentComparator;
import lu.itrust.business.TS.model.assessment.helper.AssetComparatorByALE;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;
import lu.itrust.business.TS.model.iteminformation.helper.ComparatorItemInformation;
import lu.itrust.business.TS.model.parameter.AcronymParameter;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.riskinformation.helper.RiskInformationManager;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.helper.MeasureComparator;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;

/**
 * ExportReport.java: <br>
 * Detailed description...
 * 
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since May 27, 2014
 */
public class ExportAnalysisReport {

	private static final String MAX_IMPL = "MAX_IMPL";

	private static final String DEFAULT_CELL_COLOR = "f5f9f0";

	private static final String HEADER_COLOR = "B8CCE4";

	private static final String SUB_HEADER_COLOR = "dbe5f1";

	private static final String SUPER_HEAD_COLOR = "95b3d7";

	private static final String ZERO_COST_COLOR = "e6b8b7";

	private Analysis analysis = null;

	private String contextPath;

	private XWPFDocument document = null;

	private String idTask;

	private DecimalFormat kEuroFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);

	private DecimalFormat numberFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);

	private Locale locale = null;

	private MessageSource messageSource;

	private String reportName;

	private ServiceTaskFeedback serviceTaskFeedback;

	private File workFile;

	private int progress;

	private int minProgress;

	private int maxProgress;

	public ExportAnalysisReport() {
	}

	public ExportAnalysisReport(MessageSource messageSource, ServiceTaskFeedback serviceTaskFeedback, String contextPath) {
		setMessageSource(messageSource);
		setContextPath(contextPath);
		setServiceTaskFeedback(serviceTaskFeedback);
	}

	private XWPFRun addCellNumber(XWPFTableCell cell, String number) {
		return addCellNumber(cell, number, false);
	}

	private XWPFRun addCellNumber(XWPFTableCell cell, String number, boolean isBold) {
		XWPFParagraph paragraph = cell.getParagraphs().size() == 1 ? cell.getParagraphs().get(0) : cell.addParagraph();
		paragraph.setStyle("TableParagraphTS");
		paragraph.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun run = paragraph.createRun();
		run.setBold(isBold);
		run.setText(number);
		return run;
	}

	private XWPFParagraph addCellParagraph(XWPFTableCell cell, String text) {
		return addCellParagraph(cell, text, false);
	}

	private XWPFParagraph addCellParagraph(XWPFTableCell cell, String text, boolean add) {
		XWPFParagraph paragraph = !add && cell.getParagraphs().size() == 1 ? cell.getParagraphs().get(0) : cell.addParagraph();
		if (text == null)
			text = "";
		String[] texts = text.split("(\r\n|\n\r|\r|\n)");
		for (int i = 0; i < texts.length; i++) {
			if (i > 0)
				paragraph = cell.addParagraph();
			paragraph.setStyle("TableParagraphTS");
			paragraph.createRun().setText(texts[i]);
		}
		return paragraph;
	}

	/**
	 * exportToWordDocument: <br>
	 * Description
	 * @param analysisId
	 * @param context
	 * @param serviceAnalysis
	 * 
	 * @return
	 * @throws Exception
	 */
	public void exportToWordDocument(Analysis analysis) throws Exception {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			setAnalysis(analysis);
			switch (analysis.getLanguage().getAlpha3().toLowerCase()) {
			case "fra":
				locale = Locale.FRENCH;
				break;
			case "eng":
			default:
				locale = Locale.ENGLISH;
			}

			kEuroFormat.setMaximumFractionDigits(1);
			numberFormat.setMaximumFractionDigits(0);

			serviceTaskFeedback.send(idTask, new MessageHandler("info.create.temporary.word.file", "Create temporary word file", increase(1)));// 1%
			workFile = new File(
					String.format("%s/WEB-INF/tmp/STA_%d_%s_V%s.docm", contextPath, System.nanoTime(), analysis.getLabel().replaceAll("/|-|:|.|&", "_"), analysis.getVersion()));
			if (!workFile.exists())
				workFile.createNewFile();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.load.word.template", "Loading word template", increase(2)));// 3%
			
			File doctemplate = new File(String.format("%s/WEB-INF/data/%s.dotm", contextPath, reportName));
			OPCPackage pkg = OPCPackage.open(doctemplate.getAbsoluteFile());
			pkg.replaceContentType("application/vnd.ms-word.template.macroEnabledTemplate.main+xml", "application/vnd.ms-word.document.macroEnabled.main+xml");
			pkg.save(workFile);
			
			document = new XWPFDocument(inputStream = new FileInputStream(workFile));
		
			serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.data", "Printing data", increase(2)));// 5%

			generatePlaceholders();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.item.information", "Printing item information table", increase(5)));// 10%

			generateItemInformation();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.asset", "Printing asset table", increase(5)));// 15%

			generateAssets();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.scenario", "Printing scenario table", increase(5)));// 20%

			generateScenarios();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.estimation", "Printing estimation table", increase(5)));// 25%

			generateAssessements();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.threat", "Printing threat table", increase(5)));// 30%

			generateThreats();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.impact", "Printing impact table", increase(5)));// 35%

			generateExtendedParameters(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME);

			serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.probabilty", "Printing probabilty table", increase(5)));// 40%

			generateExtendedParameters(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME);

			serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.action.plan", "Printing action plan table", increase(5)));// 45%

			generateActionPlan();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.summary", "Printing summary table", increase(10)));// 55%

			generateActionPlanSummary();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.measure", "Printing measure table", increase(5)));// 60%

			generateMeasures();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart", "Printing chart", increase(5)));// 70%

			generateGraphics();

			updateProperties();

			document.write(outputStream = new FileOutputStream(workFile));
			
			outputStream.flush();
			
		} finally {
			if (inputStream != null)
				inputStream.close();
			if(outputStream!=null)
				outputStream.close();
		}
	}

	private void updateProperties() {
		Optional<Parameter> maxImplParameter = analysis.getParameters().stream().filter(parameter -> parameter.getDescription().equals(Constant.SOA_THRESHOLD)).findAny();
		if (maxImplParameter.isPresent()) {
			CTProperty soaThresholdProperty = document.getProperties().getCustomProperties().getProperty(MAX_IMPL);
			if (soaThresholdProperty == null)
				document.getProperties().getCustomProperties().addProperty(MAX_IMPL, (int) maxImplParameter.get().getValue());
			else
				soaThresholdProperty.setLpwstr(String.valueOf((int) maxImplParameter.get().getValue()));
		}
		document.getProperties().getCoreProperties().setCategory(analysis.getCustomer().getOrganisation());
		document.getProperties().getCoreProperties().setCreator(String.format("%s %s", analysis.getOwner().getFirstName(), analysis.getOwner().getLastName()));
		document.enforceUpdateFields();
	}

	private XWPFParagraph findParagraphByText(String text) {
		List<XWPFParagraph> paragraphs = document.getParagraphs();
		for (XWPFParagraph paragraph : paragraphs) {
			if (paragraph.getParagraphText().equals(text))
				return paragraph;
		}
		return null;
	}

	private String formatedImpact(String impactFin) {
		try {
			return kEuroFormat.format(Double.parseDouble(impactFin) * 0.001);
		} catch (Exception e) {
			return impactFin;
		}
	}

	private String formatLikelihood(String likelihood) {
		try {
			return kEuroFormat.format(Double.parseDouble(likelihood));
		} catch (Exception e) {
			return likelihood;
		}
	}

	private void generateActionPlan() throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<ActionPlan>");

		// run = paragraph.getRuns().get(0);

		List<ActionPlanEntry> actionplan = analysis.getActionPlan(ActionPlanMode.APPN);

		if (paragraph != null && actionplan != null && actionplan.size() > 0) {

			while (!paragraph.getRuns().isEmpty())
				paragraph.removeRun(0);

			// initialise table with 1 row and 1 column after the paragraph
			// cursor

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSActionPlan");

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 13; i++)
				row.addNewTableCell();

			row.getCell(0).setText(getMessage("report.action_plan.row_number", null, "Nr", locale));
			row.getCell(1).setText(getMessage("report.action_plan.norm", null, "Stds", locale));
			row.getCell(2).setText(getMessage("report.action_plan.reference", null, "Ref.", locale));
			row.getCell(3).setText(getMessage("report.action_plan.description", null, "Description", locale));
			row.getCell(4).setText(getMessage("report.action_plan.ale", null, "ALE", locale));
			row.getCell(5).setText(getMessage("report.action_plan.delta_ale", null, "Δ ALE", locale));
			row.getCell(6).setText(getMessage("report.action_plan.cost", null, "CS", locale));
			row.getCell(7).setText(getMessage("report.action_plan.rosi", null, "ROSI", locale));
			row.getCell(8).setText(getMessage("report.action_plan.internal.workload", null, "IS", locale));
			row.getCell(9).setText(getMessage("report.action_plan.external.workload", null, "ES", locale));
			row.getCell(10).setText(getMessage("report.action_plan.investment", null, "INV", locale));
			row.getCell(11).setText(getMessage("report.measure.phase", null, "P", locale));
			row.getCell(12).setText(getMessage("report.measure.responsable", null, "Resp.", locale));
			int nr = 0;
			// set data
			for (ActionPlanEntry entry : actionplan) {
				row = table.createRow();
				row.getCell(0).setText("" + (++nr));
				row.getCell(1).setText(entry.getMeasure().getAnalysisStandard().getStandard().getLabel());
				row.getCell(2).setText(entry.getMeasure().getMeasureDescription().getReference());
				MeasureDescriptionText descriptionText = entry.getMeasure().getMeasureDescription().findByLanguage(analysis.getLanguage());
				addCellParagraph(row.getCell(3), descriptionText == null ? "" : descriptionText.getDomain() + (locale == Locale.FRENCH ? " : " : ": "));
				for (XWPFParagraph paragraph2 : row.getCell(3).getParagraphs()) {
					for (XWPFRun run : paragraph2.getRuns())
						run.setBold(true);
				}
				addCellParagraph(row.getCell(3), entry.getMeasure().getToDo(), true);
				addCellNumber(row.getCell(4), numberFormat.format(entry.getTotalALE() * 0.001));
				addCellNumber(row.getCell(5), numberFormat.format(entry.getDeltaALE() * 0.001));
				addCellNumber(row.getCell(6), numberFormat.format(entry.getMeasure().getCost() * 0.001));
				addCellNumber(row.getCell(7), numberFormat.format(entry.getROI() * 0.001));
				numberFormat.setMaximumFractionDigits(1);
				addCellNumber(row.getCell(8), numberFormat.format(entry.getMeasure().getInternalWL()));
				addCellNumber(row.getCell(9), numberFormat.format(entry.getMeasure().getExternalWL()));
				numberFormat.setMaximumFractionDigits(0);
				addCellNumber(row.getCell(10), numberFormat.format(entry.getMeasure().getInvestment() * 0.001));
				addCellNumber(row.getCell(11), entry.getMeasure().getPhase().getNumber() + "");
				addCellNumber(row.getCell(12), entry.getMeasure().getResponsible());
				for (int i = 0; i < 13; i++)
					row.getCell(i).setColor(SUB_HEADER_COLOR);
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

		// set header

		row = table.getRow(0);

		for (int i = 1; i < 3; i++)
			row.addNewTableCell();

		int rownumber = 0;

		while (rownumber < 28) {

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
					cell.setText(stage.getStage().equalsIgnoreCase("Start(P0)") ? getMessage("report.summary_stage.phase.start", null, stage.getStage(), locale)
							: getMessage("report.summary_stage.phase", stage.getStage().split(" "), stage.getStage(), locale));
				}
				break;
			}

			case 1:
				MergeCell(row, 0, summary.size() + 1, null);
				row.getCell(0).setText("1	" + getMessage("report.summary_stage.phase_duration", null, "Phase duration", locale));
				break;
			case 2: {
				row.getCell(0).setText("1.1	" + getMessage("report.summary_stage.date.beginning", null, "Beginning date", locale));
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
				for (int i = 1; i < summary.size(); i++) {
					addCellParagraph(row.getCell(i + 1), dateFormat.format(analysis.findPhaseByNumber(i).getBeginDate()));
				}
				break;
			}
			case 3: {
				row.getCell(0).setText("1.2	" + getMessage("report.summary_stage.date.end", null, "End date", locale));
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
				for (int i = 1; i < summary.size(); i++)
					addCellParagraph(row.getCell(i + 1), dateFormat.format(analysis.findPhaseByNumber(i).getEndDate()));
				break;
			}

			case 4:
				MergeCell(row, 0, summary.size() + 1, null);
				row.getCell(0).setText("2	" + getMessage("report.summary_stage.compliance", null, "Compliance", locale));
				break;
			case 5: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("2.1	" + getMessage("report.summary_stage.compliance.level", new Object[] { "27001" }, "Compliance level 27001 (%)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getSingleConformance("27001") == null ? 0 : stage.getSingleConformance("27001") * 100));
				break;
			}
			case 6: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("2.2	" + getMessage("report.summary_stage.compliance.level", new Object[] { "27002" }, "Compliance level 27002 (%)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getSingleConformance("27002") == null ? 0 : stage.getSingleConformance("27002") * 100));
				break;
			}

			case 7:
				MergeCell(row, 0, summary.size() + 1, null);
				row.getCell(0).setText("3	" + getMessage("report.summary_stage.evolution_of_implemented_measure", null, "Evolution of implemented measures", locale));
				break;
			case 8: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("3.1	" + getMessage("report.summary_stage.number_of_measure_for_phase", null, "Number of measures for phase", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), "" + stage.getMeasureCount());
				break;
			}
			case 9: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("3.2	" + getMessage("report.summary_stage.implementted_measures", null, "Implemented measures (number)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), "" + stage.getImplementedMeasuresCount());
				break;
			}
			case 10: {
				MergeCell(row, 0, summary.size() + 1, null);
				row.getCell(0).setText("4	" + getMessage("report.summary_stage.profitability", null, "Profitability", locale));
				break;
			}
			case 11: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("4.1	" + getMessage("report.summary_stage.ale_at_end", null, "ALE (k€/y)... at end", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getTotalALE() * 0.001));
				break;
			}
			case 12: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("4.2	" + getMessage("report.summary_stage.risk_reduction", null, "Risk reduction (k€/y)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getDeltaALE() * 0.001));
				break;
			}
			case 13: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("4.3	" + getMessage("report.summary_stage.average_yearly_cost_of_phase", null, "Average yearly cost of phase (k€/y)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getCostOfMeasures() * 0.001));
				break;
			}
			case 14: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("4.3	" + getMessage("report.summary_stage.rosi", null, "ROSI (k€/y)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getROSI() * 0.001));
				break;
			}
			case 15: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("4.4	" + getMessage("report.summary_stage.rosi.relative", null, "Relative ROSI", locale));
				DecimalFormat format = (DecimalFormat) numberFormat.clone();
				format.setMaximumFractionDigits(2);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), format.format(stage.getRelativeROSI()));
				break;
			}
			case 16: {
				MergeCell(row, 0, summary.size() + 1, null);
				row.getCell(0).setText("5	" + getMessage("report.summary_stage.resource.planning", null, "Resource planning", locale));
				// mrege columns
				break;
			}

			case 17: {
				MergeCell(row, 0, summary.size() + 1, null);
				row.getCell(0).setText("5.1	" + getMessage("report.summary_stage.implementation.cost", null, "Implementation costs", locale));
				// mrege columns
				break;
			}
			case 18: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.1.1	" + getMessage("report.summary_stage.workload.internal", null, "Internal workload (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getInternalWorkload()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 19: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.1.2	" + getMessage("report.summary_stage.workload.external", null, "External workload (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getExternalWorkload()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 20: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.1.3	" + getMessage("report.summary_stage.investment", null, "Investment (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(Math.floor(stage.getInvestment() * 0.001)));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}

			case 21: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.1.4	" + getMessage("report.summary_stage.total.implement.phase.cost", null, "Total implement cost of phase (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getImplementCostOfPhase() * 0.001), true);
				numberFormat.setMaximumFractionDigits(0);
				break;
			}

			case 22: {
				MergeCell(row, 0, summary.size() + 1, null);
				row.getCell(0).setText("5.2	" + getMessage("report.summary_stage.cost.recurrent", null, "Recurrent costs", locale));
				break;
			}

			case 23: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.2.1	" + getMessage("report.summary_stage.maintenance.internal", null, "Internal maintenance (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getInternalMaintenance()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 24: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.2.2	" + getMessage("report.summary_stage.maintenance.external", null, "External maintenance (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getExternalMaintenance()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 25: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.2.3	" + getMessage("report.summary_stage.investment.recurrent", null, "Recurrent investment (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getRecurrentInvestment() * 0.001));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}

			case 26: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.2.4	" + getMessage("report.summary_stage.total.cost.recurrent", null, "Total recurrent costs (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getRecurrentCost() * 0.001), true);
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 27: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.3	" + getMessage("report.summary_stage.cost.total_of_phase", null, "Total cost of phase (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getTotalCostofStage() * 0.001), true);
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			}
			rownumber++;
		}

	}

	private void generateALEByAssetGraphic(ReportExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
		if (reportExcelSheet == null)
			return;
		List<Assessment> assessments = analysis.getSelectedAssessments();
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales2 = new LinkedList<ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getAsset().getId());
			if (ale == null) {
				ales.put(assessment.getAsset().getId(), ale = new ALE(assessment.getAsset().getName(), 0));
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

	}

	private void generateALEByScenarioTypeGraphic(ReportExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
		if (reportExcelSheet == null)
			return;
		List<Assessment> assessments = analysis.getSelectedAssessments();
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales2 = new LinkedList<ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getScenario().getType().getValue());
			if (ale == null) {
				ales.put(assessment.getScenario().getType().getValue(), ale = new ALE(assessment.getScenario().getType().getName(), 0));
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
	}

	private void generateAssessements() {
		XWPFParagraph paragraph = null;
		XWPFParagraph paragraphOrigin = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraphOrigin = findParagraphByText("<Assessment>");

		List<Assessment> assessments = analysis.getSelectedAssessments();

		Collections.sort(assessments, new AssessmentComparator());

		double totalale = 0;

		for (Assessment assessment : assessments)
			totalale += assessment.getALE();

		if (paragraphOrigin != null && assessments.size() > 0) {

			while (!paragraphOrigin.getRuns().isEmpty())
				paragraphOrigin.removeRun(0);

			paragraph = document.insertNewParagraph(paragraphOrigin.getCTP().newCursor());

			Map<String, ALE> alesmap = new LinkedHashMap<String, ALE>();
			Map<String, List<Assessment>> assessementsmap = new LinkedHashMap<String, List<Assessment>>();

			AssessmentAndRiskProfileManager.SplitAssessment(assessments, alesmap, assessementsmap);

			List<ALE> ales = new ArrayList<ALE>(alesmap.size());

			for (ALE ale : alesmap.values())
				ales.add(ale);

			alesmap.clear();

			Collections.sort(ales, new AssetComparatorByALE());

			XWPFRun run = paragraph.createRun();

			run.setText(getMessage("report.assessment.total_ale.assets", null, "Total ALE for all assets", locale));

			paragraph.createRun().addTab();

			run = paragraph.createRun();

			run.setText(String.format("%s k€", kEuroFormat.format(totalale * 0.001)));

			paragraph.setStyle("TSAssessmentTotalALE");

			for (ALE ale : ales) {
				paragraph = document.insertNewParagraph(paragraphOrigin.getCTP().newCursor());
				paragraph.createRun().setText(ale.getAssetName());
				paragraph.setStyle("TSEstimationTitle");

				paragraph = document.insertNewParagraph(paragraphOrigin.getCTP().newCursor());

				run = paragraph.createRun();

				run.setText(getMessage("report.assessment.total.ale.for.asset", null, "Total ALE of asset", locale));

				paragraph.createRun().addTab();

				run = paragraph.createRun();

				run.setText(String.format("%s k€", kEuroFormat.format(ale.getValue() * 0.001)));

				run.setBold(true);

				paragraph.setStyle("TSAssessmentTotalALEByAsset");

				document.insertNewParagraph(paragraphOrigin.getCTP().newCursor());

				table = document.insertNewTbl(paragraphOrigin.getCTP().newCursor());

				table.setStyleID("TableTSAssessment");

				row = table.getRow(0);

				while (row.getTableCells().size() < 6)
					row.addNewTableCell();

				row.getCell(0).setText(getMessage("report.assessment.scenarios", null, "Scenarios", locale));
				row.getCell(1).setText(getMessage("report.assessment.impact.financial", null, "Fin.", locale));
				row.getCell(1).setColor("c6d9f1");
				row.getCell(2).setText(getMessage("report.assessment.probability", null, "P.", locale));
				row.getCell(2).setColor("c6d9f1");
				row.getCell(3).setText(getMessage("report.assessment.ale", null, "ALE(k€/y)", locale));
				row.getCell(3).setColor("c6d9f1");
				row.getCell(4).setText(getMessage("report.assessment.owner", null, "Owner", locale));
				row.getCell(4).setColor("c6d9f1");
				row.getCell(5).setText(getMessage("report.assessment.comment", null, "Comment", locale));
				row.getCell(5).setColor("c6d9f1");
				


				List<Assessment> assessmentsofasset = assessementsmap.get(ale.getAssetName());
				for (Assessment assessment : assessmentsofasset) {
					row = table.createRow();
					while (row.getTableCells().size() < 6)
						row.addNewTableCell();
					row.getCell(0).setText(assessment.getScenario().getName());
					addCellNumber(row.getCell(1), formatedImpact(assessment.getImpactFin()));
					addCellNumber(row.getCell(2), formatLikelihood(assessment.getLikelihood()));
					addCellNumber(row.getCell(3), kEuroFormat.format(assessment.getALE() * 0.001));
					addCellParagraph(row.getCell(4), assessment.getOwner());
					addCellParagraph(row.getCell(5), assessment.getComment());
				}
			}
			assessementsmap.clear();
			ales.clear();
		}
	}

	private void generateAssets() {
		generateAssets("<Asset>", analysis.findSelectedAssets());
		generateAssets("<Asset-no-selected>", analysis.findNoAssetSelected());
	}

	private void generateAssets(String name, List<Asset> assets) {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText(name);

		if (paragraph != null) {

			while (!paragraph.getRuns().isEmpty())
				paragraph.removeRun(0);

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSAsset");

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

	private void generateBudgetGraphic(ReportExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {

		if (analysis.getSummaries() == null || analysis.getSummaries().isEmpty())
			return;

		List<SummaryStage> summaryStages = analysis.getSummary(ActionPlanMode.APPN);
		Map<String, List<String>> summaries = ActionPlanSummaryManager.buildTable(summaryStages, analysis.getPhases());
		Map<String, Phase> usesPhases = ActionPlanSummaryManager.buildPhase(analysis.getPhases(), ActionPlanSummaryManager.extractPhaseRow(summaryStages));
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

		List<String> dataImplementPHaseCost = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_IMPLEMENT_PHASE_COST);

		List<String> dataCurrentCost = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_RECURRENT_COST);

		List<String> dataTotalPhaseCost = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST);

		for (int j = 1; j < 9; j++) {
			if (xssfSheet.getRow(0) == null)
				xssfSheet.createRow(0);
			if (xssfSheet.getRow(0).getCell(j) == null)
				xssfSheet.getRow(0).createCell(j);
		}
		xssfSheet.getRow(0).getCell(1).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD, null, "Internal workload", locale));
		xssfSheet.getRow(0).getCell(2).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD, null, "External workload", locale));
		xssfSheet.getRow(0).getCell(3).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE, null, "Internal maintenance", locale));
		xssfSheet.getRow(0).getCell(4).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE, null, "External maintenance", locale));
		xssfSheet.getRow(0).getCell(5).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INVESTMENT, null, "Investment", locale));
		xssfSheet.getRow(0).getCell(6)
				.setCellValue(getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_IMPLEMENT_PHASE_COST, null, "Total implement cost of phase", locale));
		xssfSheet.getRow(0).getCell(7).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_RECURRENT_COST, null, "Current cost", locale));
		xssfSheet.getRow(0).getCell(8).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST, null, "Total phase cost", locale));

		rowIndex = 1;
		for (int i = 0; i < dataInternalWorkload.size(); i++) {
			for (int j = 1; j < 9; j++) {
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
			xssfSheet.getRow(rowIndex).getCell(6).setCellValue(Double.parseDouble(dataImplementPHaseCost.get(i)));
			xssfSheet.getRow(rowIndex).getCell(7).setCellValue(Double.parseDouble(dataCurrentCost.get(i)));
			xssfSheet.getRow(rowIndex++).getCell(8).setCellValue(Double.parseDouble(dataTotalPhaseCost.get(i)));
		}
	}

	@SuppressWarnings("unchecked")
	private void generateComplianceGraphic(ReportExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
		if (reportExcelSheet == null)
			return;
		String standard = reportExcelSheet.getName().endsWith("27001") ? "27001" : "27002";
		List<Measure> measures = (List<Measure>) analysis.findMeasureByStandard(standard);
		List<AcronymParameter> expressionParameters = analysis.getExpressionParameters();
		if (measures == null)
			return;
		XSSFSheet xssfSheet = reportExcelSheet.getXssfWorkbook().getSheetAt(0);
		Map<String, Object[]> compliances = ChartGenerator.ComputeComplianceBefore(measures, expressionParameters);
		int rowCount = 0;
		String phaseLabel = getMessage("label.chart.series.current_level", null, "Current Level", locale);
		if (xssfSheet.getRow(rowCount) == null)
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

		if (!actionPlanMeasures.isEmpty()) {
			List<Phase> phases = analysis.findUsablePhase();
			int columnIndex = 2;
			for (Phase phase : phases) {
				compliances = ChartGenerator.ComputeCompliance(measures, phase, actionPlanMeasures, compliances, expressionParameters);
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
		}
	}

	private void generateEvolutionOfProfitabilityGraphic(ReportExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
		if (reportExcelSheet == null || analysis.getSummaries() == null || analysis.getSummaries().isEmpty())
			return;
		List<SummaryStage> summaryStages = analysis.getSummary(ActionPlanMode.APPN);
		Map<String, List<String>> summaries = ActionPlanSummaryManager.buildTable(summaryStages, analysis.getPhases());
		Map<String, Phase> usesPhases = ActionPlanSummaryManager.buildPhase(analysis.getPhases(), ActionPlanSummaryManager.extractPhaseRow(summaryStages));
		XSSFSheet xssfSheet = reportExcelSheet.getXssfWorkbook().getSheetAt(0);
		int rowIndex = 1;
		for (Phase phase : usesPhases.values()) {
			if (xssfSheet.getRow(rowIndex) == null)
				xssfSheet.createRow(rowIndex);
			if (xssfSheet.getRow(rowIndex).getCell(0) == null)
				xssfSheet.getRow(rowIndex).createCell(0);
			xssfSheet.getRow(rowIndex++).getCell(0).setCellValue(String.format("P%d", phase.getNumber()));
		}

		List<String> dataCompliance27001s = summaries.get(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE + "27001");
		List<String> dataCompliance27002s = summaries.get(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE + "27002");
		List<String> dataALEs = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ALE_UNTIL_END);
		List<String> dataRiskReductions = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_RISK_REDUCTION);
		List<String> dataCostOfMeasures = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_AVERAGE_YEARLY_COST_OF_PHASE);
		List<String> dataROSIs = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI);
		List<String> dataRelatifROSIs = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI_RELATIF);

		for (int j = 1; j < 8; j++) {
			if (xssfSheet.getRow(0) == null)
				xssfSheet.createRow(0);
			if (xssfSheet.getRow(0).getCell(j) == null)
				xssfSheet.getRow(0).createCell(j);
		}

		xssfSheet.getRow(0).getCell(1).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE, null, "Compliance", locale) + " 27001");
		xssfSheet.getRow(0).getCell(2).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE, null, "Compliance", locale) + " 27002");
		xssfSheet.getRow(0).getCell(3).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_ALE_UNTIL_END, null, "ALE (k€)... at end", locale));
		xssfSheet.getRow(0).getCell(4).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_RISK_REDUCTION, null, "Risk reduction", locale));
		xssfSheet.getRow(0).getCell(5)
				.setCellValue(getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_AVERAGE_YEARLY_COST_OF_PHASE, null, "Average yearly cost of phase (k€/y)", locale));
		xssfSheet.getRow(0).getCell(6).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI, null, "ROSI", locale));
		xssfSheet.getRow(0).getCell(7).setCellValue(getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI_RELATIF, null, "ROSI relatif", locale));
		rowIndex = 1;

		int size = 0;

		if (dataCompliance27001s != null)
			size = dataCompliance27001s.size();
		else if (dataCompliance27002s != null)
			size = dataCompliance27002s.size();

		for (int i = 0; i < size; i++) {
			for (int j = 1; j < 8; j++) {
				if (xssfSheet.getRow(rowIndex) == null)
					xssfSheet.createRow(rowIndex);
				if (xssfSheet.getRow(rowIndex).getCell(j) == null)
					xssfSheet.getRow(rowIndex).createCell(j);
			}
			xssfSheet.getRow(rowIndex).getCell(1).setCellValue(Double.parseDouble((dataCompliance27001s == null ? "0" : dataCompliance27001s.get(i))) * 0.01);
			xssfSheet.getRow(rowIndex).getCell(2).setCellValue(Double.parseDouble((dataCompliance27002s == null ? "0" : dataCompliance27002s.get(i))) * 0.01);
			xssfSheet.getRow(rowIndex).getCell(3).setCellValue(Double.parseDouble(dataALEs.get(i)));
			xssfSheet.getRow(rowIndex).getCell(4).setCellValue(Double.parseDouble(dataRiskReductions.get(i)));
			xssfSheet.getRow(rowIndex).getCell(5).setCellValue(Double.parseDouble(dataCostOfMeasures.get(i)));
			xssfSheet.getRow(rowIndex).getCell(6).setCellValue(Double.parseDouble(dataROSIs.get(i)));
			xssfSheet.getRow(rowIndex++).getCell(7).setCellValue(Double.parseDouble(dataRelatifROSIs.get(i)));
		}
	}

	private void generateExtendedParameters(String type) throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;
		String parmetertype = "";
		if (type.equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
			parmetertype = "Impact";
		else if (type.equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME))
			parmetertype = "Proba";

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

			// set header

			row = table.getRow(0);
			MergeCell(row, 0, 6, null);
			row.getCell(0).setText(getMessage("report.parameter.title." + parmetertype.toLowerCase(), null, parmetertype, locale));
			row = table.createRow();

			for (int i = 1; i < 6; i++) {
				XWPFTableCell cell = row.getCell(i);
				if (cell != null)
					cell.setColor(HEADER_COLOR);
				else
					row.addNewTableCell().setColor(HEADER_COLOR);
			}

			row.getCell(0).setText(getMessage("report.parameter.level", null, "Level", locale));
			row.getCell(1).setText(getMessage("report.parameter.acronym", null, "Acro", locale));
			row.getCell(2).setText(getMessage("report.parameter.qualification", null, "Qualification", locale));

			if (parmetertype.equals("Proba"))
				row.getCell(3).setText(getMessage("report.parameter.proba.value", null, "Value (/y)", locale));
			else
				row.getCell(3).setText(getMessage("report.parameter.value", null, "Value (k€/y)", locale));

			row.getCell(4).setText(getMessage("report.parameter.value.from", null, "Value From", locale));
			row.getCell(5).setText(getMessage("report.parameter.value.to", null, "Value To", locale));

			int countrow = 0;
			// set data
			for (ExtendedParameter extendedParameter : extendedParameters) {
				row = table.createRow();

				while (row.getTableCells().size() < 6)
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
		}
	}

	private void generateGraphics() throws OpenXML4JException, IOException {
		for (PackagePart packagePart : this.document.getPackage().getParts())
			if (packagePart.getPartName().getExtension().contains("xls")) {
				ReportExcelSheet reportExcelSheet = new ReportExcelSheet(packagePart, String.format("%s/WEB-INF/tmp/", contextPath));
				try {
					switch (reportExcelSheet.getName()) {
					case "Compliance27001":
					case "Compliance27002":
						if (reportExcelSheet.getName().equalsIgnoreCase("Compliance27001"))
							serviceTaskFeedback.send(idTask,
									new MessageHandler("info.printing.chart.data.compliance.27001", "Printing compliance 27001 excel sheet", increase(2)));// 72%
						else
							serviceTaskFeedback.send(idTask,
									new MessageHandler("info.printing.chart.data.compliance.27002", "Printing compliance 27002 excel sheet", increase(2)));// 74%
						generateComplianceGraphic(reportExcelSheet);
						break;
					case "ALEByScenarioType":
						serviceTaskFeedback.send(idTask,
								new MessageHandler("info.printing.chart.data.ale.by.scenario.type", "Printing ALE by scenario type excel sheet", increase(3)));// 77%
						generateALEByScenarioTypeGraphic(reportExcelSheet);
						break;
					case "ALEByScenario":
						serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.ale.by.scenario", "Printing ALE by scenario excel sheet", increase(5)));// 82%
						generateALEByScenarioGraphic(reportExcelSheet);
						break;
					case "ALEByAssetType":
						serviceTaskFeedback.send(idTask,
								new MessageHandler("info.printing.chart.data.ale.by.asset.type", "Printing ALE by asset type excel sheet", increase(2)));// 84%
						generateALEByAssetTypeGraphic(reportExcelSheet);
						break;
					case "ALEByAsset":
						serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.ale.by.asset", "Printing ALE by asset excel sheet", increase(5)));// 89%
						generateALEByAssetGraphic(reportExcelSheet);
						break;
					case "EvolutionOfProfitability":
						serviceTaskFeedback.send(idTask,
								new MessageHandler("info.printing.chart.data.evolution.of.profitability", "Printing evolution of profitability  excel sheet", increase(7)));// 96%
						generateEvolutionOfProfitabilityGraphic(reportExcelSheet);
						break;
					case "Budget":
						serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.budget", "Printing budget excel sheet", increase(2)));// 98%
						generateBudgetGraphic(reportExcelSheet);
						break;
					}
				} finally {
					reportExcelSheet.save();
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

	private void generateMeasures() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Measures>");

		// run = paragraph.getRuns().get(0);

		List<AnalysisStandard> analysisStandards = analysis.getAnalysisStandards();
		
		List<AcronymParameter> expressionParameters = this.analysis.getExpressionParameters();

		if (paragraph != null && analysisStandards.size() > 0) {

			while (!paragraph.getRuns().isEmpty())
				paragraph.removeRun(0);

			boolean isFirst = true;

			Comparator<Measure> comparator = new MeasureComparator();

			for (AnalysisStandard analysisStandard : analysisStandards) {

				// initialise table with 1 row and 1 column after the paragraph
				// cursor
				if (analysisStandard.getMeasures().isEmpty())
					continue;

				if (isFirst)
					isFirst = false;
				else
					paragraph = document.createParagraph();

				paragraph.setStyle("TSMeasureTitle");

				paragraph.createRun().setText(analysisStandard.getStandard().getLabel());

				paragraph = document.createParagraph();

				paragraph.setIndentationLeft(0);

				paragraph.setAlignment(ParagraphAlignment.CENTER);

				table = document.insertNewTbl(paragraph.getCTP().newCursor());

				table.setStyleID("TableTSMeasure");
				// set header
				row = table.getRow(0);

				if (!row.getTableCells().isEmpty())
					row.getCell(0).setColor(SUPER_HEAD_COLOR);

				while (row.getTableCells().size() < 16)
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
				row.getCell(12).setText(getMessage("report.measure.phase", null, "P", locale));
				row.getCell(13).setText(getMessage("report.measure.responsible", null, "Resp.", locale));
				row.getCell(14).setText(getMessage("report.measure.to_do", null, "To Do", locale));
				row.getCell(15).setText(getMessage("report.measure.comment", null, "Comment", locale));
				// set data
				Collections.sort(analysisStandard.getMeasures(), comparator);

				for (Measure measure : analysisStandard.getMeasures()) {
					row = table.createRow();
					while (row.getTableCells().size() < 2)
						row.createCell();
					row.getCell(0).setText(measure.getMeasureDescription().getReference());
					MeasureDescriptionText description = measure.getMeasureDescription().findByLanguage(analysis.getLanguage());
					row.getCell(1).setText(description == null ? "" : description.getDomain());
					if (!measure.getMeasureDescription().isComputable()) {
						String color = measure.getMeasureDescription().getLevel() < 2 ? SUPER_HEAD_COLOR : HEADER_COLOR;
						for (int i = 0; i < 16; i++)
							row.getCell(i).setColor(i == 15 ? DEFAULT_CELL_COLOR : color);
					} else {
						while (row.getTableCells().size() < 16)
							row.createCell();
						row.getCell(2).setText(getMessage("label.measure.status." + measure.getStatus().toLowerCase(), null, measure.getStatus(), locale));
						addCellNumber(row.getCell(3), numberFormat.format(measure.getImplementationRateValue(expressionParameters)));
						addCellNumber(row.getCell(4), kEuroFormat.format(measure.getInternalWL()));
						addCellNumber(row.getCell(5), kEuroFormat.format(measure.getExternalWL()));
						addCellNumber(row.getCell(6), numberFormat.format(measure.getInvestment() * 0.001));
						addCellNumber(row.getCell(7), numberFormat.format(measure.getLifetime()));
						addCellNumber(row.getCell(8), kEuroFormat.format(measure.getInternalMaintenance()));
						addCellNumber(row.getCell(9), kEuroFormat.format(measure.getExternalMaintenance()));
						addCellNumber(row.getCell(10), numberFormat.format(measure.getRecurrentInvestment() * 0.001));
						addCellNumber(row.getCell(11), numberFormat.format(measure.getCost() * 0.001));
						addCellParagraph(row.getCell(12), measure.getPhase().getNumber() + "");
						addCellParagraph(row.getCell(13), measure.getResponsible());
						addCellParagraph(row.getCell(14), measure.getToDo());
						if (Constant.MEASURE_STATUS_NOT_APPLICABLE.equalsIgnoreCase(measure.getStatus()) || measure.getImplementationRateValue(expressionParameters) >= 100) {
							for (int i = 0; i < 16; i++)
								row.getCell(i).setColor(DEFAULT_CELL_COLOR);
						} else {
							row.getCell(0).setColor(SUB_HEADER_COLOR);
							row.getCell(1).setColor(SUB_HEADER_COLOR);
							row.getCell(11).setColor(measure.getCost() == 0 ? ZERO_COST_COLOR : SUB_HEADER_COLOR);
						}
					}
					addCellParagraph(row.getCell(15), measure.getComment());
				}
			}
		}
	}

	public static void MergeCell(XWPFTableRow row, int begin, int size, String color) {
		int length = begin + size;
		for (int i = 0; i < length; i++) {
			XWPFTableCell cell = row.getCell(i);
			if (cell == null)
				cell = row.addNewTableCell();
			if (color != null)
				cell.setColor(color);
			if (i < begin)
				continue;
			else if (i == begin)
				cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
			else
				cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
		}
	}

	private void generatePlaceholders() {
		document.createParagraph().createRun().setText("<Measures>");
	}

	private void generateScenarios() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Scenario>");

		List<Scenario> scenarios = analysis.findSelectedScenarios();

		if (paragraph != null && scenarios.size() > 0) {

			while (!paragraph.getRuns().isEmpty())
				paragraph.removeRun(0);

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSScenario");

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 3; i++)
				row.addNewTableCell();

			// set header
			table.getRow(0).getCell(0).setText(getMessage("report.scenario.title.number.row", null, "Nr", locale));
			table.getRow(0).getCell(1).setText(getMessage("report.scenario.title.name", null, "Name", locale));
			table.getRow(0).getCell(2).setText(getMessage("report.scenario.title.description", null, "Description", locale));

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
							row.getCell(4).setText(getMessage(String.format("report.risk_information.title.%s", "owner"), null, "Owner", locale));
							row.addNewTableCell();
							row.getCell(5).setText(getMessage(String.format("report.risk_information.title.%s", "comment"), null, "Comment", locale));
						} else {
							row.addNewTableCell();
							row.getCell(2).setText(getMessage(String.format("report.risk_information.title.%s", "expo"), null, "Expo.", locale));
							row.addNewTableCell();
							row.getCell(3).setText(getMessage(String.format("report.risk_information.title.%s", "owner"), null, "Owner", locale));
							row.addNewTableCell();
							row.getCell(4).setText(getMessage(String.format("report.risk_information.title.%s", "comment"), null, "Comment", locale));
						}
					}

					previouselement = riskinfo;
					row = table.createRow();
					row.getCell(0).setText(riskinfo.getChapter());
					row.getCell(1).setText(getMessage(String.format("label.risk_information.%s.%s", riskinfo.getCategory().toLowerCase(), riskinfo.getChapter().replace(".", "_")),
							null, riskinfo.getLabel(), locale));
					chapter = riskinfo.getChapter().matches("\\d(\\.0){2}");
					if (riskinfo.getCategory().equals("Threat")) {
						for (int i = 0; i < 3; i++)
							row.getCell(i).setColor(chapter ? HEADER_COLOR : SUB_HEADER_COLOR);
						row.getCell(2).setText(riskinfo.getAcronym());
						row.getCell(3).setText("" + riskinfo.getExposed());
						row.getCell(4).setText(getValueOrEmpty(riskinfo.getOwner()));
						addCellParagraph(row.getCell(5), riskinfo.getComment());
					} else {
						for (int i = 0; i < 2; i++)
							row.getCell(i).setColor(chapter ? HEADER_COLOR : SUB_HEADER_COLOR);
						row.getCell(2).setText("" + riskinfo.getExposed());
						row.getCell(3).setText(getValueOrEmpty(riskinfo.getOwner()));
						addCellParagraph(row.getCell(4), riskinfo.getComment());
					}
				}
			}
		}
	}

	private String getValueOrEmpty(String value) {
		return value == null ? "" : value;
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

	public String getContextPath() {
		return contextPath;
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

	public String getIdTask() {
		return idTask;
	}

	public Locale getLocale() {
		return locale;
	}

	private String getMessage(String code, Object[] parameters, String defaultMessage, Locale locale) {
		return messageSource.getMessage(code, parameters, defaultMessage, locale);
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public String getReportName() {
		return reportName;
	}

	public ServiceTaskFeedback getServiceTaskFeedback() {
		return serviceTaskFeedback;
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

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
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

	public void setIdTask(String idTask) {
		this.idTask = idTask;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public void setServiceTaskFeedback(ServiceTaskFeedback serviceTaskFeedback) {
		this.serviceTaskFeedback = serviceTaskFeedback;
	}

	public File getWorkFile() {
		return workFile;
	}

	public void setWorkFile(File workFile) {
		this.workFile = workFile;
	}

	public int getProgress() {
		return progress;
	}

	private void setProgress(int progress) {
		this.progress = progress;
	}

	public int increase(int value) {
		if (!(value < 0 || value > 100)) {
			progress += value;
			if (progress > 100)
				setProgress(100);
		}
		return (int) (minProgress + (maxProgress - minProgress) * 0.01 * progress);
	}

	public int getMinProgress() {
		return minProgress;
	}

	public void setMinProgress(int minProgress) {
		this.minProgress = minProgress;
	}

	public int getMaxProgress() {
		if (maxProgress <= 0)
			return 100;
		return maxProgress;
	}

	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}
}
