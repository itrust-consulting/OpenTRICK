/**
 * 
 */
package lu.itrust.business.TS.exportation;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.context.MessageSource;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.chartJS.Chart;
import lu.itrust.business.TS.component.chartJS.Dataset;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.exportation.helper.ReportExcelSheet;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.RiskAcceptanceParameter;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.model.scale.Translation;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;

/**
 * @author eomar
 *
 */
public class ExportQualitativeReport extends AbstractWordExporter {

	public ExportQualitativeReport(MessageSource messageSource, ServiceTaskFeedback serviceTaskFeedback, String realPath) {
		setMessageSource(messageSource);
		setServiceTaskFeedback(serviceTaskFeedback);
		setContextPath(realPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.AbstractWordExporter#generateActionPlan
	 * ()
	 */
	@Override
	protected void generateActionPlan() throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findTableAnchor("<ActionPlan>");

		// run = paragraph.getRuns().get(0);

		List<ActionPlanEntry> actionplan = analysis.getActionPlan(ActionPlanMode.APPN);

		if (paragraph != null && actionplan != null && actionplan.size() > 0) {

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSActionPlan");

			row = table.getRow(0);

			for (int i = 1; i < 11; i++)
				row.addNewTableCell();

			row.getCell(0).setText(getMessage("report.action_plan.row_number", null, "Nr", locale));
			row.getCell(1).setText(getMessage("report.action_plan.norm", null, "Stds", locale));
			row.getCell(2).setText(getMessage("report.action_plan.reference", null, "Ref.", locale));
			row.getCell(3).setText(getMessage("report.action_plan.description", null, "Description", locale));
			row.getCell(4).setText(getMessage("report.action_plan.risk_count", null, "NR", locale));
			row.getCell(5).setText(getMessage("report.action_plan.cost", null, "CS", locale));
			row.getCell(6).setText(getMessage("report.action_plan.internal.workload", null, "IS", locale));
			row.getCell(7).setText(getMessage("report.action_plan.external.workload", null, "ES", locale));
			row.getCell(8).setText(getMessage("report.action_plan.investment", null, "INV", locale));
			row.getCell(9).setText(getMessage("report.measure.phase", null, "P", locale));
			row.getCell(10).setText(getMessage("report.measure.responsable", null, "Resp.", locale));
			int nr = 0;
			// set data
			for (ActionPlanEntry entry : actionplan) {
				row = table.createRow();
				row.getCell(0).setText("" + (++nr));
				row.getCell(1).setText(entry.getMeasure().getAnalysisStandard().getStandard().getLabel());
				row.getCell(2).setText(entry.getMeasure().getMeasureDescription().getReference());
				MeasureDescriptionText descriptionText = entry.getMeasure().getMeasureDescription().findByLanguage(analysis.getLanguage());
				addCellParagraph(row.getCell(3), descriptionText == null ? "" : descriptionText.getDomain() + (locale == Locale.FRENCH ? "\u00A0:" : ":"));
				for (XWPFParagraph paragraph2 : row.getCell(3).getParagraphs()) {
					for (XWPFRun run : paragraph2.getRuns())
						run.setBold(true);
				}
				addCellParagraph(row.getCell(3), entry.getMeasure().getToDo(), true);
				addCellNumber(row.getCell(4), numberFormat.format(entry.getRiskCount()));
				addCellNumber(row.getCell(5), numberFormat.format(entry.getMeasure().getCost() * 0.001));
				numberFormat.setMaximumFractionDigits(1);
				addCellNumber(row.getCell(6), numberFormat.format(entry.getMeasure().getInternalWL()));
				addCellNumber(row.getCell(7), numberFormat.format(entry.getMeasure().getExternalWL()));
				numberFormat.setMaximumFractionDigits(0);
				addCellNumber(row.getCell(8), numberFormat.format(entry.getMeasure().getInvestment() * 0.001));
				addCellNumber(row.getCell(9), entry.getMeasure().getPhase().getNumber() + "");
				addCellNumber(row.getCell(10), entry.getMeasure().getResponsible());
			}
		}

		if (paragraph != null)
			paragraphsToDelete.add(paragraph);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.AbstractWordExporter#
	 * generateActionPlanSummary()
	 */
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

		// set header

		row = table.getRow(0);

		for (int i = 1; i < 3; i++)
			row.addNewTableCell();

		int rownumber = 0;

		while (rownumber < 24) {

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

			case 7: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(
						"2.3	" + getMessage("report.characteristic.count.not_compliant_measure", new Object[] { "27001" }, "Non-compliant measures of the 27001", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), stage.getNotCompliantMeasure27001Count() + "");
				break;
			}

			case 8: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText(
						"2.4	" + getMessage("report.characteristic.count.not_compliant_measure", new Object[] { "27002" }, "Non-compliant measures of the 27002", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), stage.getNotCompliantMeasure27002Count() + "");
				break;
			}

			case 9:
				MergeCell(row, 0, summary.size() + 1, null);
				row.getCell(0).setText("3	" + getMessage("report.summary_stage.evolution_of_implemented_measure", null, "Evolution of implemented measures", locale));
				break;
			case 10: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("3.1	" + getMessage("report.summary_stage.number_of_measure_for_phase", null, "Number of measures for phase", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), "" + stage.getMeasureCount());
				break;
			}
			case 11: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("3.2	" + getMessage("report.summary_stage.implementted_measures", null, "Implemented measures (number)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), "" + stage.getImplementedMeasuresCount());
				break;
			}
			case 12: {
				MergeCell(row, 0, summary.size() + 1, null);
				row.getCell(0).setText("5	" + getMessage("report.summary_stage.resource.planning", null, "Resource planning", locale));
				// mrege columns
				break;
			}

			case 13: {
				MergeCell(row, 0, summary.size() + 1, null);
				row.getCell(0).setText("5.1	" + getMessage("report.summary_stage.implementation.cost", null, "Implementation costs", locale));
				// mrege columns
				break;
			}
			case 14: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.1.1	" + getMessage("report.summary_stage.workload.internal", null, "Internal workload (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getInternalWorkload()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 15: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.1.2	" + getMessage("report.summary_stage.workload.external", null, "External workload (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getExternalWorkload()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 16: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.1.3	" + getMessage("report.summary_stage.investment", null, "Investment (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(Math.floor(stage.getInvestment() * 0.001)));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}

			case 17: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.1.4	" + getMessage("report.summary_stage.total.implement.phase.cost", null, "Total implement cost of phase (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getImplementCostOfPhase() * 0.001), true);
				numberFormat.setMaximumFractionDigits(0);
				break;
			}

			case 18: {
				MergeCell(row, 0, summary.size() + 1, null);
				row.getCell(0).setText("5.2	" + getMessage("report.summary_stage.cost.recurrent", null, "Recurrent costs", locale));
				break;
			}

			case 19: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.2.1	" + getMessage("report.summary_stage.maintenance.internal", null, "Internal maintenance (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getInternalMaintenance()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 20: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.2.2	" + getMessage("report.summary_stage.maintenance.external", null, "External maintenance (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getExternalMaintenance()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 21: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.2.3	" + getMessage("report.summary_stage.investment.recurrent", null, "Recurrent investment (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getRecurrentInvestment() * 0.001));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}

			case 22: {
				int cellnumber = 0;
				row.getCell(cellnumber).setText("5.2.4	" + getMessage("report.summary_stage.total.cost.recurrent", null, "Total recurrent costs (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber(row.getCell(++cellnumber), numberFormat.format(stage.getRecurrentCost() * 0.001), true);
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 23: {
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
		paragraphsToDelete.add(paragraph);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.AbstractWordExporter#
	 * generateAssessements()
	 */
	@Override
	protected void generateAssessements() {
		XWPFParagraph paragraph = null;
		XWPFParagraph paragraphOrigin = null;
		XWPFTable table = null;
		XWPFTableRow row = null;
		String language = locale.getLanguage().toUpperCase();
		paragraphOrigin = findTableAnchor("<Assessment>");
		Map<Asset, List<Assessment>> assessementsByAsset = analysis.findSelectedAssessmentByAsset();
		if (paragraphOrigin != null && assessementsByAsset.size() > 0) {
			while (!paragraphOrigin.getRuns().isEmpty())
				paragraphOrigin.removeRun(0);
			List<ScaleType> scaleTypes = analysis.getImpacts();
			int colLength = 4 + scaleTypes.size(), colIndex = 0;
			for (Asset asset : assessementsByAsset.keySet()) {
				paragraph = document.insertNewParagraph(paragraphOrigin.getCTP().newCursor());
				paragraph.createRun().setText(asset.getName());
				paragraph.setStyle("TSEstimationTitle");
				paragraph = document.insertNewParagraph(paragraphOrigin.getCTP().newCursor());
				table = document.insertNewTbl(paragraph.getCTP().newCursor());
				table.setStyleID("TableTSAssessment");
				row = table.getRow(0);
				while (row.getTableCells().size() < colLength)
					row.addNewTableCell();
				row.getCell(colIndex++).setText(getMessage("report.assessment.scenarios", null, "Scenarios", locale));
				for (ScaleType scaleType : scaleTypes)
					setCellText(row.getCell(colIndex++), scaleType.getShortName(language), ParagraphAlignment.CENTER);
				setCellText(row.getCell(colIndex++), getMessage("report.assessment.probability", null, "P.", locale), ParagraphAlignment.CENTER);
				row.getCell(colIndex++).setText(getMessage("report.assessment.owner", null, "Owner", locale));
				row.getCell(colIndex++).setText(getMessage("report.assessment.comment", null, "Comment", locale));

				for (Assessment assessment : assessementsByAsset.get(asset)) {
					row = table.createRow();
					while (row.getTableCells().size() < colLength)
						row.addNewTableCell();
					colIndex = 0;
					row.getCell(colIndex++).setText(assessment.getScenario().getName());
					for (ScaleType scaleType : scaleTypes) {
						IValue impact = assessment.getImpact(scaleType.getName());
						setCellText(row.getCell(colIndex++), impact == null || impact.getLevel() == 0 ? getMessage("label.status.na", null, "na", locale) : impact.getLevel() + "",
								ParagraphAlignment.CENTER);
					}
					int probaLevel = valueFactory.findProbLevel(assessment.getLikelihood());
					setCellText(row.getCell(colIndex++), probaLevel == 0 ? getMessage("label.status.na", null, "na", locale) : probaLevel + "", ParagraphAlignment.CENTER);
					addCellParagraph(row.getCell(colIndex++), assessment.getOwner());
					addCellParagraph(row.getCell(colIndex++), assessment.getComment());
				}
				paragraph.createRun().setText(getMessage("report.assessment.table.caption", new Object[] { asset.getName() },
						String.format("Risk estimation for the asset %s", asset.getName()), locale));
				paragraph.setStyle("Caption");
				colIndex = 0;
			}
			assessementsByAsset.clear();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.AbstractWordExporter#generateAssets(
	 * java.lang.String, java.util.List)
	 */
	@Override
	protected void generateAssets(String name, List<Asset> assets) {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findTableAnchor(name);

		if (paragraph != null) {

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSAsset");

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 5; i++)
				row.addNewTableCell();

			// set header
			table.getRow(0).getCell(0).setText(getMessage("report.asset.title.number.row", null, "Nr", locale));
			table.getRow(0).getCell(1).setText(getMessage("report.asset.title.name", null, "Name", locale));
			table.getRow(0).getCell(2).setText(getMessage("report.asset.title.type", null, "Type", locale));
			table.getRow(0).getCell(3).setText(getMessage("report.asset.title.value", null, "Value(k€)", locale));
			table.getRow(0).getCell(4).setText(getMessage("report.asset.title.comment", null, "Comment", locale));

			int number = 0;

			// set data
			for (Asset asset : assets) {
				row = table.createRow();
				row.getCell(0).setText("" + ++number);
				row.getCell(1).setText(asset.getName());
				row.getCell(2).setText(asset.getAssetType().getType());
				addCellNumber(row.getCell(3), kEuroFormat.format(asset.getValue() * 0.001));
				addCellParagraph(row.getCell(4), asset.getComment());
			}
		}

		if (paragraph != null)
			paragraphsToDelete.add(paragraph);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.AbstractWordExporter#
	 * generateExtendedParameters(java.lang.String)
	 */
	@Override
	protected void generateExtendedParameters(String type) throws Exception {
		XWPFParagraph paragraph = null;
		String parmetertype = "", languuage = locale.getLanguage().toUpperCase();
		if (type.equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
			parmetertype = "Impact";
		else if (type.equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME))
			parmetertype = "Proba";

		paragraph = findTableAnchor("<" + parmetertype + ">");

		if (paragraph != null) {
			if (parmetertype == "Proba")
				buildImpactProbabilityTable(paragraph, getMessage("report.parameter.title." + type.toLowerCase(), null, type, locale), parmetertype,
						analysis.getLikelihoodParameters());
			else {
				Map<ScaleType, List<ImpactParameter>> impacts = analysis.getImpactParameters().stream().collect(Collectors.groupingBy(ImpactParameter::getType));
				for (ScaleType scaleType : impacts.keySet()) {
					Translation title = scaleType.get(languuage);
					buildImpactProbabilityTable(paragraph, title == null ? scaleType.getDisplayName() : title.getName(), parmetertype, impacts.get(scaleType));
				}
			}
			paragraphsToDelete.add(paragraph);
		}

	}

	private void buildImpactProbabilityTable(XWPFParagraph paragraph, String title, String type, List<? extends IBoundedParameter> parameters) {
		XWPFParagraph titleParagraph = document.insertNewParagraph(paragraph.getCTP().newCursor());
		titleParagraph.createRun().setText(title);
		titleParagraph.setStyle("TSEstimationTitle");
		XWPFTable table = document.insertNewTbl(paragraph.getCTP().newCursor());
		table.setStyleID("TableTS" + type);
		// set header
		XWPFTableRow row = table.getRow(0);
		for (int i = 1; i < 3; i++) {
			XWPFTableCell cell = row.getCell(i);
			if (cell != null)
				cell.setColor(HEADER_COLOR);
			else
				row.addNewTableCell().setColor(HEADER_COLOR);
		}
		row.getCell(0).setText(getMessage("report.parameter.level", null, "Level", locale));
		row.getCell(1).setText(getMessage("report.parameter.label", null, "Label", locale));
		row.getCell(2).setText(getMessage("report.parameter.qualification", null, "Qualification", locale));
		// set data
		for (IBoundedParameter parameter : parameters) {
			row = table.createRow();
			while (row.getTableCells().size() < 3)
				row.addNewTableCell();
			row.getCell(0).setText("" + parameter.getLevel());
			row.getCell(1).setText(parameter.getLabel());
			row.getCell(2).setText(parameter.getDescription());

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.AbstractWordExporter#writeChart(lu.
	 * itrust.business.TS.exportation.helper.ReportExcelSheet)
	 */
	@Override
	protected void writeChart(ReportExcelSheet reportExcelSheet) throws Exception {
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
			case "RiskByScenarioType":
				serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.ale.by.scenario.type", "Printing ALE by scenario type excel sheet", increase(3)));// 77%
				generateRiskByScenarioTypeGraphic(reportExcelSheet);
				break;
			case "RiskByScenario":
				serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.ale.by.scenario", "Printing ALE by scenario excel sheet", increase(5)));// 82%
				generateRiskByScenarioGraphic(reportExcelSheet);
				break;
			case "RiskByAssetType":
				serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.ale.by.asset.type", "Printing ALE by asset type excel sheet", increase(2)));// 84%
				generateRiskByAssetTypeGraphic(reportExcelSheet);
				break;
			case "RiskByAsset":
				serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.ale.by.asset", "Printing ALE by asset excel sheet", increase(5)));// 89%
				generateRiskByAssetGraphic(reportExcelSheet);
				break;
			}
		} finally {
			reportExcelSheet.save();
		}
	}

	private void generateRiskByAssetGraphic(ReportExcelSheet reportExcelSheet) {
	}

	private void generateRiskByAssetTypeGraphic(ReportExcelSheet reportExcelSheet) {
	}

	private void generateRiskByScenarioGraphic(ReportExcelSheet reportExcelSheet) {

	}

	private void generateRiskByScenarioTypeGraphic(ReportExcelSheet reportExcelSheet) {

	}

	@Override
	protected void generateOtherData() {
		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.risk_heat.map", "Printing risk heat map", increase(3)));
		generateRiskHeatMap();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.risk_acceptance", "Printing risk acceptance table", increase(2)));
		generateRiskAcceptance();
	}

	private void generateRiskAcceptance() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;
		paragraph = findTableAnchor("<risk-acceptance>");
		if (paragraph != null) {
			table = document.insertNewTbl(paragraph.getCTP().newCursor());
			table.setStyleID("TableTSRiskAcceptance");
			// set header
			row = table.getRow(0);
			for (int i = 1; i < 2; i++)
				row.addNewTableCell();
			// set header
			table.getRow(0).getCell(0).setText(getMessage("report.risk_acceptance.title.level", null, "Risk level", locale));
			table.getRow(0).getCell(1).setText(getMessage("report.risk_acceptance.title.acceptance.criteria", null, "Risk acceptance criteria", locale));
			// set data
			for (RiskAcceptanceParameter parameter : analysis.getRiskAcceptanceParameters()) {
				row = table.createRow();
				XWPFTableCell cell = row.getCell(0);
				addCellParagraph(cell, parameter.getLabel()).setAlignment(ParagraphAlignment.CENTER);
				addCellParagraph(cell, getMessage("report.risk_acceptance.importance_threshold.value", new Object[] { parameter.getValue().intValue() },
						"Importance threshold: " + parameter.getValue().intValue(), locale), true);
				addCellParagraph(row.getCell(1), parameter.getDescription());
				if (!parameter.getColor().isEmpty())
					cell.setColor(parameter.getColor().substring(1));
			}
		}

		if (paragraph != null)
			paragraphsToDelete.add(paragraph);
	}

	private void generateRiskHeatMap() {
		Chart chart = ChartGenerator.generateRiskHeatMap(analysis, valueFactory);
		generateRiskHeatMap(chart, "<risk-heat-map-summary>");
		generateRiskHeatMap(chart, "<risk-heat-map>");
	}

	private void generateRiskHeatMap(Chart chart, String anchor) {
		XWPFParagraph paragraphOriginal = null;
		XWPFTable table = null;
		XWPFTableRow row = null;
		int rowIndex = 0;
		paragraphOriginal = findTableAnchor(anchor);
		if (paragraphOriginal != null) {
			XWPFParagraph paragraph = document.insertNewParagraph(paragraphOriginal.getCTP().newCursor());
			paragraph.setStyle("TabText1");
			chart.getLegends().forEach(legend -> {
				XWPFRun run = paragraph.createRun();
				run.setText(legend.getLabel());
				run.setColor(legend.getColor().substring(1));
				paragraph.createRun().addTab();
			});
			table = document.insertNewTbl(paragraphOriginal.getCTP().newCursor());
			table.setStyleID("TableTSRiskHeatMap");
			// set header
			row = table.getRow(rowIndex);
			for (int i = 1; i < chart.getLabels().size() + 2; i++)
				row.addNewTableCell();
			row.getCell(0).setText(getMessage("report.risk_heat_map.title.probability", null, "Probability", locale));
			for (int i = 0; i < chart.getDatasets().size(); i++) {
				Dataset dataset = chart.getDatasets().get(i);
				if (i > 0) {
					row = table.getRow(rowIndex++);
					if (row == null)
						row = table.createRow();
				}
				XWPFTableCell cell = row.getCell(1);
				cell.setVerticalAlignment(XWPFVertAlign.CENTER);
				addCellParagraph(cell, dataset.getLabel()).setAlignment(ParagraphAlignment.CENTER);
				for (int j = 0; j < dataset.getData().size(); j++) {
					cell = row.getCell(j + 2);
					cell.setVerticalAlignment(XWPFVertAlign.CENTER);
					Object data = dataset.getData().get(i);
					if (data instanceof Integer)
						addCellParagraph(cell, data.toString()).setAlignment(ParagraphAlignment.CENTER);
					cell.setColor(dataset.getBackgroundColor().get(j).substring(1));
				}
			}
			row = table.getRow(rowIndex++);
			if (row == null)
				row = table.createRow();
			for (int i = 0; i < chart.getLabels().size(); i++) {
				XWPFTableCell cell = row.getCell(i + 1);
				cell.setVerticalAlignment(XWPFVertAlign.CENTER);
				addCellParagraph(cell, chart.getLabels().get(i)).setAlignment(ParagraphAlignment.CENTER);
			}
			row = table.getRow(rowIndex);
			if (row == null)
				row = table.createRow();
			row.getCell(0).setText(getMessage("report.risk_heat_map.title.impact", null, "Impact", locale));
		}

		if (paragraphOriginal != null)
			paragraphsToDelete.add(paragraphOriginal);

	}

}
