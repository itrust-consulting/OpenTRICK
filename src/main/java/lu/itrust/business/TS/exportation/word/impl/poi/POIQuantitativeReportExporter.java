package lu.itrust.business.TS.exportation.word.impl.poi;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.context.MessageSource;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.helper.POIExcelSheet;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.ALE;
import lu.itrust.business.TS.model.assessment.helper.AssessmentComparator;
import lu.itrust.business.TS.model.assessment.helper.AssetComparatorByALE;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;

/**
 * ExportReport.java: <br>
 * Detailed description...
 * 
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since May 27, 2014
 */
public class POIQuantitativeReportExporter extends POIWordExporter {

	public POIQuantitativeReportExporter() {
	}

	public POIQuantitativeReportExporter(MessageSource messageSource, ServiceTaskFeedback serviceTaskFeedback, String contextPath) {
		setMessageSource(messageSource);
		setContextPath(contextPath);
		setServiceTaskFeedback(serviceTaskFeedback);
	}

	@Override
	protected void generateActionPlan() throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findTableAnchor("<ActionPlan>");

		setCurrentParagraphId(TS_TAB_TEXT_2);

		// run = paragraph.getRuns().get(0);

		List<ActionPlanEntry> actionplan = analysis.getActionPlan(ActionPlanMode.APPN);

		if (paragraph != null && actionplan != null && actionplan.size() > 0) {

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSActionPlan");

			row = table.getRow(0);

			for (int i = 1; i < 13; i++)
				row.addNewTableCell();

			setCellText(row.getCell(0), getMessage("report.action_plan.row_number", null, "Nr", locale));
			setCellText(row.getCell(1), getMessage("report.action_plan.norm", null, "Stds", locale));
			setCellText(row.getCell(2), getMessage("report.action_plan.reference", null, "Ref.", locale));
			setCellText(row.getCell(3), getMessage("report.action_plan.description", null, "Description", locale));
			setCellText(row.getCell(4), getMessage("report.action_plan.ale", null, "ALE", locale));
			setCellText(row.getCell(5), getMessage("report.action_plan.delta_ale", null, "Δ ALE", locale));
			setCellText(row.getCell(6), getMessage("report.action_plan.cost", null, "CS", locale));
			setCellText(row.getCell(7), getMessage("report.action_plan.rosi", null, "ROSI", locale));
			setCellText(row.getCell(8), getMessage("report.action_plan.internal.workload", null, "IS", locale));
			setCellText(row.getCell(9), getMessage("report.action_plan.external.workload", null, "ES", locale));
			setCellText(row.getCell(10), getMessage("report.action_plan.investment", null, "INV", locale));
			setCellText(row.getCell(11), getMessage("report.measure.phase", null, "P", locale));
			setCellText(row.getCell(12), getMessage("report.measure.responsable", null, "Resp.", locale));
			int nr = 1;
			// set data
			for (ActionPlanEntry entry : actionplan) {
				row = table.createRow();
				setCellText(row.getCell(0), "" + (nr++));
				setCellText(row.getCell(1), entry.getMeasure().getAnalysisStandard().getStandard().getLabel());
				setCellText(row.getCell(2), entry.getMeasure().getMeasureDescription().getReference());
				MeasureDescriptionText descriptionText = entry.getMeasure().getMeasureDescription().findByLanguage(analysis.getLanguage());
				addCellParagraph(row.getCell(3), descriptionText == null ? "" : descriptionText.getDomain() + (locale == Locale.FRENCH ? "\u00A0:" : ":"));
				for (XWPFParagraph paragraph2 : row.getCell(3).getParagraphs()) {
					for (XWPFRun run : paragraph2.getRuns())
						run.setBold(true);
					paragraph2.setAlignment(ParagraphAlignment.LEFT);
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
			}
		}

		if (paragraph != null)
			paragraphsToDelete.add(paragraph);

	}

	@Override
	protected void generateActionPlanSummary() throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findTableAnchor("<Summary>");

		if (paragraph == null)
			return;

		List<SummaryStage> summary = analysis.getSummary(ActionPlanMode.APPN);

		if (summary.isEmpty()) {
			paragraphsToDelete.add(paragraph);
			return;
		}

		// initialise table with 1 row and 1 column after the paragraph
		// cursor

		table = document.insertNewTbl(paragraph.getCTP().newCursor());

		table.setStyleID("TableTSSummary");

		setCurrentParagraphId(TS_TAB_TEXT_2);

		// set header

		row = table.getRow(0);

		for (int i = 1; i < 3; i++)
			row.addNewTableCell();

		int rownumber = 0;

		while (rownumber < 30) {

			if (rownumber == 0)
				row = table.getRow(rownumber);
			else
				row = table.createRow();

			switch (rownumber) {
			case 0: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), getMessage("report.summary_stage.phase.characteristics", null, "Phase characteristics", locale));
				for (SummaryStage stage : summary) {
					XWPFTableCell cell = row.getCell(++cellnumber);
					if (cell == null)
						cell = row.addNewTableCell();
					setCellText(cell, stage.getStage().equalsIgnoreCase("Start(P0)") ? getMessage("report.summary_stage.phase.start", null, stage.getStage(), locale)
							: getMessage("report.summary_stage.phase", stage.getStage().split(" "), stage.getStage(), locale));
				}
				break;
			}

			case 1:
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText(row.getCell(0), "1	" + getMessage("report.summary_stage.phase_duration", null, "Phase duration", locale));
				break;
			case 2: {
				setCellText(row.getCell(0), "1.1	" + getMessage("report.summary_stage.date.beginning", null, "Beginning date", locale));
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
				for (int i = 1; i < summary.size(); i++) {
					addCellParagraph(row.getCell(i + 1), dateFormat.format(analysis.findPhaseByNumber(i).getBeginDate()));
				}
				break;
			}
			case 3: {
				setCellText(row.getCell(0), "1.2	" + getMessage("report.summary_stage.date.end", null, "End date", locale));
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
				for (int i = 1; i < summary.size(); i++)
					addCellParagraph(row.getCell(i + 1), dateFormat.format(analysis.findPhaseByNumber(i).getEndDate()));
				break;
			}

			case 4:
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText(row.getCell(0), "2	" + getMessage("report.summary_stage.compliance", null, "Compliance", locale));
				break;
			case 5: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber),
						"2.1	" + getMessage("report.summary_stage.compliance.level", new Object[] { "27001" }, "Compliance level 27001 (%)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getSingleConformance("27001") == null ? 0 : stage.getSingleConformance("27001") * 100));
				break;
			}
			case 6: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber),
						"2.2	" + getMessage("report.summary_stage.compliance.level", new Object[] { "27002" }, "Compliance level 27002 (%)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getSingleConformance("27002") == null ? 0 : stage.getSingleConformance("27002") * 100));
				break;
			}

			case 7: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber),
						"2.3	" + getMessage("report.characteristic.count.not_compliant_measure", new Object[] { "27001" }, "Non-compliant measures of the 27001", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), stage.getNotCompliantMeasure27001Count() + "");
				break;
			}

			case 8: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber),
						"2.4	" + getMessage("report.characteristic.count.not_compliant_measure", new Object[] { "27002" }, "Non-compliant measures of the 27002", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), stage.getNotCompliantMeasure27002Count() + "");
				break;
			}

			case 9:
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText(row.getCell(0), "3	" + getMessage("report.summary_stage.evolution_of_implemented_measure", null, "Evolution of implemented measures", locale));
				break;
			case 10: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), "3.1	" + getMessage("report.summary_stage.number_of_measure_for_phase", null, "Number of measures for phase", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), "" + stage.getMeasureCount());
				break;
			}
			case 11: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), "3.2	" + getMessage("report.summary_stage.implementted_measures", null, "Implemented measures (number)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), "" + stage.getImplementedMeasuresCount());
				break;
			}
			case 12: {
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText(row.getCell(0), "4	" + getMessage("report.summary_stage.profitability", null, "Profitability", locale));
				break;
			}
			case 13: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), "4.1	" + getMessage("report.summary_stage.ale_at_end", null, "ALE (k€/y)... at end", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getTotalALE() * 0.001));
				break;
			}
			case 14: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), "4.2	" + getMessage("report.summary_stage.risk_reduction", null, "Risk reduction (k€/y)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getDeltaALE() * 0.001));
				break;
			}
			case 15: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber),
						"4.3	" + getMessage("report.summary_stage.average_yearly_cost_of_phase", null, "Average yearly cost of phase (k€/y)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getCostOfMeasures() * 0.001));
				break;
			}
			case 16: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), "4.3	" + getMessage("report.summary_stage.rosi", null, "ROSI (k€/y)", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getROSI() * 0.001));
				break;
			}
			case 17: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), "4.4	" + getMessage("report.summary_stage.rosi.relative", null, "Relative ROSI", locale));
				DecimalFormat format = (DecimalFormat) numberFormat.clone();
				format.setMaximumFractionDigits(2);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), format.format(stage.getRelativeROSI()));
				break;
			}
			case 18: {
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText(row.getCell(0), "5	" + getMessage("report.summary_stage.resource.planning", null, "Resource planning", locale));
				// mrege columns
				break;
			}

			case 19: {
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText(row.getCell(0), "5.1	" + getMessage("report.summary_stage.implementation.cost", null, "Implementation costs", locale));
				// mrege columns
				break;
			}
			case 20: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), "5.1.1	" + getMessage("report.summary_stage.workload.internal", null, "Internal workload (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getInternalWorkload()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 21: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), "5.1.2	" + getMessage("report.summary_stage.workload.external", null, "External workload (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getExternalWorkload()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 22: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), "5.1.3	" + getMessage("report.summary_stage.investment", null, "Investment (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(Math.floor(stage.getInvestment() * 0.001)));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}

			case 23: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), "5.1.4	" + getMessage("report.summary_stage.total.implement.phase.cost", null, "Total implement cost of phase (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getImplementCostOfPhase() * 0.001), true);
				numberFormat.setMaximumFractionDigits(0);
				break;
			}

			case 24: {
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText(row.getCell(0), "5.2	" + getMessage("report.summary_stage.cost.recurrent", null, "Recurrent costs", locale));
				break;
			}

			case 25: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), "5.2.1	" + getMessage("report.summary_stage.maintenance.internal", null, "Internal maintenance (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getInternalMaintenance()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 26: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), "5.2.2	" + getMessage("report.summary_stage.maintenance.external", null, "External maintenance (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getExternalMaintenance()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 27: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), "5.2.3	" + getMessage("report.summary_stage.investment.recurrent", null, "Recurrent investment (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getRecurrentInvestment() * 0.001));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}

			case 28: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), "5.2.4	" + getMessage("report.summary_stage.total.cost.recurrent", null, "Total recurrent costs (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getRecurrentCost() * 0.001), true);
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 29: {
				int cellnumber = 0;
				setCellText(row.getCell(cellnumber), "5.3	" + getMessage("report.summary_stage.cost.total_of_phase", null, "Total cost of phase (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getTotalCostofStage() * 0.001), true);
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			}
			rownumber++;
		}
		paragraphsToDelete.add(paragraph);

	}

	@Override
	protected void generateAssessements() {
		XWPFParagraph paragraph = null;
		XWPFParagraph paragraphOrigin = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraphOrigin = findTableAnchor("<Assessment>");

		List<Assessment> assessments = analysis.getSelectedAssessments();

		Collections.sort(assessments, new AssessmentComparator());

		DecimalFormat assessmentFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);
		assessmentFormat.setMinimumFractionDigits(1);
		assessmentFormat.setMaximumFractionDigits(1);

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

			setCurrentParagraphId(TS_TAB_TEXT_2);

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
				paragraph.setStyle("TSAssessmentTotalALE");
				paragraph = document.insertNewParagraph(paragraphOrigin.getCTP().newCursor());
				table = document.insertNewTbl(paragraph.getCTP().newCursor());
				table.setStyleID("TableTSAssessment");
				row = table.getRow(0);
				while (row.getTableCells().size() < 6)
					row.addNewTableCell();
				setCellText(row.getCell(0), getMessage("report.assessment.scenarios", null, "Scenarios", locale), ParagraphAlignment.LEFT);
				setCellText(row.getCell(1), getMessage("report.assessment.impact.financial", null, "Fin.", locale), ParagraphAlignment.CENTER);
				setCellText(row.getCell(2), getMessage("report.assessment.probability", null, "P.", locale), ParagraphAlignment.CENTER);
				setCellText(row.getCell(3), getMessage("report.assessment.ale", null, "ALE(k€/y)", locale));
				setCellText(row.getCell(4), getMessage("report.assessment.owner", null, "Owner", locale));
				setCellText(row.getCell(5), getMessage("report.assessment.comment", null, "Comment", locale));
				List<Assessment> assessmentsofasset = assessementsmap.get(ale.getAssetName());
				for (Assessment assessment : assessmentsofasset) {
					row = table.createRow();
					while (row.getTableCells().size() < 6)
						row.addNewTableCell();
					setCellText(row.getCell(0), assessment.getScenario().getName(), ParagraphAlignment.LEFT);
					IValue impact = assessment.getImpact(Constant.DEFAULT_IMPACT_NAME);
					if (impact == null)
						throw new TrickException("error.analysis.repport.unsupported", "Analysis cannot export repport");
					setCellText(row.getCell(1), kEuroFormat.format(impact.getReal() * 0.001), ParagraphAlignment.CENTER);
					setCellText(row.getCell(2), formatLikelihood(assessment.getLikelihood()), ParagraphAlignment.CENTER);
					addCellNumber(row.getCell(3),
							assessment.getALE() == 0 ? kEuroFormat.format(assessment.getALE() * 0.001) : assessmentFormat.format(assessment.getALE() * 0.001));
					addCellParagraph(row.getCell(4), assessment.getOwner());
					addCellParagraph(row.getCell(5), assessment.getComment());
				}
				paragraph.createRun().setText(getMessage("report.assessment.table.caption", new Object[] { ale.getAssetName() },
						String.format("Risk estimation for the asset %s", ale.getAssetName()), locale));
				paragraph.setStyle("Caption");
			}
			assessementsmap.clear();
			ales.clear();
		}

	}

	@Override
	protected void generateAssets(String name, List<Asset> assets) {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;
		paragraph = findTableAnchor(name);
		if (paragraph != null) {
			table = document.insertNewTbl(paragraph.getCTP().newCursor());
			table.setStyleID("TableTSAsset");
			setCurrentParagraphId(TS_TAB_TEXT_2);
			// set header
			row = table.getRow(0);
			for (int i = 1; i < 6; i++)
				row.addNewTableCell();
			// set header
			setCellText(row.getCell(0), getMessage("report.asset.title.number.row", null, "Nr", locale));
			setCellText(row.getCell(1), getMessage("report.asset.title.name", null, "Name", locale));
			setCellText(row.getCell(2), getMessage("report.asset.title.type", null, "Type", locale));
			setCellText(row.getCell(3), getMessage("report.asset.title.value", null, "Value(k€)", locale));
			setCellText(row.getCell(4), getMessage("report.asset.title.ale", null, "ALE(k€)", locale));
			setCellText(row.getCell(5), getMessage("report.asset.title.comment", null, "Comment", locale));
			int number = 1;
			// set data
			for (Asset asset : assets) {
				row = table.createRow();
				setCellText(row.getCell(0), "" + (number++));
				setCellText(row.getCell(1), asset.getName(), ParagraphAlignment.LEFT);
				setCellText(row.getCell(2), getDisplayName(asset.getAssetType()));
				addCellNumber(row.getCell(3), kEuroFormat.format(asset.getValue() * 0.001));
				row.getCell(4).setColor(LIGHT_CELL_COLOR);
				addCellNumber(row.getCell(4), kEuroFormat.format(asset.getALE() * 0.001));
				addCellParagraph(row.getCell(5), asset.getComment());
			}
		}
		if (paragraph != null)
			paragraphsToDelete.add(paragraph);
	}

	protected void generateEvolutionOfProfitabilityGraphic(POIExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
		if (reportExcelSheet == null || analysis.getSummaries() == null || analysis.getSummaries().isEmpty())
			return;
		List<SummaryStage> summaryStages = analysis.getSummary(ActionPlanMode.APPN);
		Map<String, List<String>> summaries = ActionPlanSummaryManager.buildTable(summaryStages, analysis.getPhases());
		Map<String, Phase> usesPhases = ActionPlanSummaryManager.buildPhase(analysis.getPhases(), ActionPlanSummaryManager.extractPhaseRow(summaryStages));
		XSSFSheet xssfSheet = reportExcelSheet.getWorkbook().getSheetAt(0);
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

	@SuppressWarnings("unchecked")
	@Override
	protected void generateExtendedParameters(String type) throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;
		String parmetertype = "";
		if (type.equals(Constant.DEFAULT_IMPACT_NAME))
			parmetertype = "Impact";
		else if (type.equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME))
			parmetertype = "Proba";

		paragraph = findTableAnchor("<" + parmetertype + ">");

		setCurrentParagraphId(TS_TAB_TEXT_2);

		List<IBoundedParameter> parameters = (List<IBoundedParameter>) analysis.findParametersByType(type);

		if (paragraph != null && parameters.size() > 0) {
			XWPFParagraph title = document.insertNewParagraph(paragraph.getCTP().newCursor());
			title.createRun().setText(getMessage("report.parameter.title." + parmetertype.toLowerCase(), null, parmetertype, locale));
			title.setStyle("TSEstimationTitle");
			table = document.insertNewTbl(paragraph.getCTP().newCursor());
			table.setStyleID("TableTS" + parmetertype);
			// set header
			row = table.getRow(0);
			for (int i = 1; i < 6; i++) {
				XWPFTableCell cell = row.getCell(i);
				if (cell != null)
					cell.setColor(HEADER_COLOR);
				else
					row.addNewTableCell().setColor(HEADER_COLOR);
			}
			setCellText(row.getCell(0), getMessage("report.parameter.level", null, "Level", locale));
			setCellText(row.getCell(1), getMessage("report.parameter.acronym", null, "Acro", locale));
			setCellText(row.getCell(2), getMessage("report.parameter.qualification", null, "Qualification", locale));

			if (parmetertype.equals("Proba"))
				setCellText(row.getCell(3), getMessage("report.parameter.proba.value", null, "Value (/y)", locale));
			else
				setCellText(row.getCell(3), getMessage("report.parameter.value", null, "Value (k€/y)", locale));

			setCellText(row.getCell(4), getMessage("report.parameter.value.from", null, "Value From", locale));
			setCellText(row.getCell(5), getMessage("report.parameter.value.to", null, "Value To", locale));

			int countrow = 0, length = parameters.size() - 1;
			// set data
			for (IBoundedParameter parameter : parameters) {
				row = table.createRow();

				while (row.getTableCells().size() < 6)
					row.addNewTableCell();
				setCellText(row.getCell(0), "" + parameter.getLevel());
				setCellText(row.getCell(1), parameter.getAcronym());
				setCellText(row.getCell(2), parameter.getDescription());
				Double value = 0.;
				value = parameter.getValue();
				if (type.equals(Constant.DEFAULT_IMPACT_NAME))
					value *= 0.001;
				addCellNumber(row.getCell(3), kEuroFormat.format(value));
				if (countrow % 2 != 0)
					row.getCell(3).setColor(SUB_HEADER_COLOR);
				value = parameter.getBounds().getFrom();
				if (type.equals(Constant.DEFAULT_IMPACT_NAME))
					value *= 0.001;
				addCellNumber(row.getCell(4), kEuroFormat.format(value));
				if (parameter.getLevel() == length)
					addCellNumber(row.getCell(5), "+∞");
				else {
					value = parameter.getBounds().getTo();
					if (type.equals(Constant.DEFAULT_IMPACT_NAME))
						value *= 0.001;
					addCellNumber(row.getCell(5), kEuroFormat.format(value));
				}
				for (int i = 4; i < 6; i++)
					row.getCell(i).setColor(SUB_HEADER_COLOR);
				countrow++;
			}
		}
		if (paragraph != null)
			paragraphsToDelete.add(paragraph);

	}

	@Override
	protected void writeChart(POIExcelSheet reportExcelSheet) throws Exception {
		try {
			switch (reportExcelSheet.getName()) {
			case "Compliance27001":
			case "Compliance27002":
				if (reportExcelSheet.getName().equalsIgnoreCase("Compliance27001"))
					serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.compliance.27001", "Printing compliance 27001 excel sheet", increase(2)));// 72%
				else
					serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.compliance.27002", "Printing compliance 27002 excel sheet", increase(2)));// 74%
				generateComplianceGraphic(reportExcelSheet);
				break;
			case "ALEByScenarioType":
				serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.ale.by.scenario.type", "Printing ALE by scenario type excel sheet", increase(3)));// 77%
				generateALEByScenarioTypeGraphic(reportExcelSheet);
				break;
			case "ALEByScenario":
				serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.ale.by.scenario", "Printing ALE by scenario excel sheet", increase(5)));// 82%
				generateALEByScenarioGraphic(reportExcelSheet);
				break;
			case "ALEByAssetType":
				serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.ale.by.asset.type", "Printing ALE by asset type excel sheet", increase(2)));// 84%
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

	private void generateALEByAssetGraphic(POIExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
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
		XSSFSheet xssfSheet = reportExcelSheet.getWorkbook().getSheetAt(0);
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

	private void generateALEByAssetTypeGraphic(POIExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
		if (reportExcelSheet == null)
			return;
		List<Assessment> assessments = analysis.getSelectedAssessments();
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales2 = new LinkedList<ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getAsset().getAssetType().getId());
			if (ale == null) {
				ales.put(assessment.getAsset().getAssetType().getId(), ale = new ALE(assessment.getAsset().getAssetType().getName(), 0));
				ales2.add(ale);
			}
			ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
		}
		Collections.sort(ales2, new AssetComparatorByALE());
		XSSFSheet xssfSheet = reportExcelSheet.getWorkbook().getSheetAt(0);
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

	private void generateALEByScenarioGraphic(POIExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
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

		XSSFSheet xssfSheet = reportExcelSheet.getWorkbook().getSheetAt(0);
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

	private void generateALEByScenarioTypeGraphic(POIExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
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
		XSSFSheet xssfSheet = reportExcelSheet.getWorkbook().getSheetAt(0);
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

	private void generateBudgetGraphic(POIExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {

		if (analysis.getSummaries() == null || analysis.getSummaries().isEmpty())
			return;

		List<SummaryStage> summaryStages = analysis.getSummary(ActionPlanMode.APPN);
		Map<String, List<String>> summaries = ActionPlanSummaryManager.buildTable(summaryStages, analysis.getPhases());
		Map<String, Phase> usesPhases = ActionPlanSummaryManager.buildPhase(analysis.getPhases(), ActionPlanSummaryManager.extractPhaseRow(summaryStages));
		XSSFSheet xssfSheet = reportExcelSheet.getWorkbook().getSheetAt(0);
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
					xssfSheet.getRow(rowIndex).createCell(j, CellType.NUMERIC);
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

	@Override
	protected void generateOtherData() {
	}

}
