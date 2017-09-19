package lu.itrust.business.TS.exportation.word.impl.docx4j;

import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getRow;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.docx4j.dml.CTRegularTextRun;
import org.docx4j.dml.chart.CTBarChart;
import org.docx4j.dml.chart.CTBarSer;
import org.docx4j.dml.chart.CTBoolean;
import org.docx4j.dml.chart.CTNumFmt;
import org.docx4j.dml.chart.CTNumVal;
import org.docx4j.dml.chart.CTStrVal;
import org.docx4j.dml.chart.CTValAx;
import org.docx4j.dml.chart.STDispBlanksAs;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.DrawingML.Chart;
import org.docx4j.openpackaging.parts.WordprocessingML.EmbeddedPackagePart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.P;
import org.docx4j.wml.PPrBase.TextAlignment;
import org.docx4j.wml.R;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.springframework.context.MessageSource;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager;
import lu.itrust.business.TS.model.analysis.AnalysisType;
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
public class Docx4jQuantitativeReportExporter extends Docx4jWordExporter {

	private static final String NUMBER_FORMAT = "[>9.99]#\\ ###\\ ###\\ ###\\ ##0\\k\\€;[>0.509]#\\k\\€;#,##0\\k\\€";

	public Docx4jQuantitativeReportExporter() {
	}

	public Docx4jQuantitativeReportExporter(MessageSource messageSource, ServiceTaskFeedback serviceTaskFeedback, String contextPath) {
		setMessageSource(messageSource);
		setContextPath(contextPath);
		setServiceTaskFeedback(serviceTaskFeedback);
	}

	@Override
	protected void generateActionPlan() throws Exception {
		P paragraph = findTableAnchor("ActionPlan");
		List<ActionPlanEntry> actionplan = analysis.getActionPlan(ActionPlanMode.APPN);
		if (!(paragraph == null || actionplan.isEmpty())) {
			setCurrentParagraphId(TS_TAB_TEXT_2);
			TextAlignment alignment = createAlignment("left"), alignmentCenter = createAlignment("center");
			Tbl table = createTable("TableTSActionPlan", actionplan.size() + 1, 13);
			Tr row = (Tr) table.getContent().get(0);
			setCellText((Tc) row.getContent().get(0), getMessage("report.action_plan.row_number", null, "Nr", locale));
			setCellText((Tc) row.getContent().get(1), getMessage("report.action_plan.norm", null, "Stds", locale));
			setCellText((Tc) row.getContent().get(2), getMessage("report.action_plan.reference", null, "Ref.", locale));
			setCellText((Tc) row.getContent().get(3), getMessage("report.action_plan.description", null, "Description", locale));
			setCellText((Tc) row.getContent().get(4), getMessage("report.action_plan.ale", null, "ALE", locale));
			setCellText((Tc) row.getContent().get(5), getMessage("report.action_plan.delta_ale", null, "Δ ALE", locale));
			setCellText((Tc) row.getContent().get(6), getMessage("report.action_plan.cost", null, "CS", locale));
			setCellText((Tc) row.getContent().get(7), getMessage("report.action_plan.rosi", null, "ROSI", locale));
			setCellText((Tc) row.getContent().get(8), getMessage("report.action_plan.internal.workload", null, "IS", locale));
			setCellText((Tc) row.getContent().get(9), getMessage("report.action_plan.external.workload", null, "ES", locale));
			setCellText((Tc) row.getContent().get(10), getMessage("report.action_plan.investment", null, "INV", locale));
			setCellText((Tc) row.getContent().get(11), getMessage("report.measure.phase", null, "P", locale));
			setCellText((Tc) row.getContent().get(12), getMessage("report.measure.responsable", null, "Resp.", locale));
			setRepeatHeader(row);
			int nr = 1;
			// set data
			for (ActionPlanEntry entry : actionplan) {
				row = (Tr) table.getContent().get(nr);
				setCellText((Tc) row.getContent().get(0), "" + (nr++), alignmentCenter);
				setCellText((Tc) row.getContent().get(1), entry.getMeasure().getAnalysisStandard().getStandard().getLabel());
				setCellText((Tc) row.getContent().get(2), entry.getMeasure().getMeasureDescription().getReference());
				MeasureDescriptionText descriptionText = entry.getMeasure().getMeasureDescription().findByLanguage(analysis.getLanguage());
				addCellParagraph((Tc) row.getContent().get(3), descriptionText == null ? "" : descriptionText.getDomain() + (locale == Locale.FRENCH ? "\u00A0:" : ":"));
				((Tc) row.getContent().get(3)).getContent().parallelStream().flatMap(p -> ((P) p).getContent().parallelStream()).map(r -> (R) r).forEach(r -> {
					if (r.getRPr() == null)
						r.setRPr(factory.createRPr());
					r.getRPr().setB(factory.createBooleanDefaultTrue());
				});
				addCellParagraph((Tc) row.getContent().get(3), entry.getMeasure().getToDo(), true);
				setAlignment((Tc) row.getContent().get(3), alignment);
				addCellNumber((Tc) row.getContent().get(4), numberFormat.format(entry.getTotalALE() * 0.001));
				addCellNumber((Tc) row.getContent().get(5), numberFormat.format(entry.getDeltaALE() * 0.001));
				addCellNumber((Tc) row.getContent().get(6), numberFormat.format(entry.getMeasure().getCost() * 0.001));
				addCellNumber((Tc) row.getContent().get(7), numberFormat.format(entry.getROI() * 0.001));
				numberFormat.setMaximumFractionDigits(1);
				addCellNumber((Tc) row.getContent().get(8), numberFormat.format(entry.getMeasure().getInternalWL()));
				addCellNumber((Tc) row.getContent().get(9), numberFormat.format(entry.getMeasure().getExternalWL()));
				numberFormat.setMaximumFractionDigits(0);
				addCellNumber((Tc) row.getContent().get(10), numberFormat.format(entry.getMeasure().getInvestment() * 0.001));
				addCellNumber((Tc) row.getContent().get(11), entry.getMeasure().getPhase().getNumber() + "");
				addCellNumber((Tc) row.getContent().get(12), entry.getMeasure().getResponsible());
			}
			insertBofore(paragraph, table);
		}

	}

	@Override
	protected void generateActionPlanSummary() throws Exception {
		P paragraph = findTableAnchor("Summary");
		if (paragraph == null)
			return;
		List<SummaryStage> summary = getSummaryStage();
		setCurrentParagraphId(TS_TAB_TEXT_2);
		Tbl table = createTable("TableTSSummary", 30, summary.size() + 1);

		// set header
		int rownumber = 0;

		while (rownumber < 30) {
			Tr row = (Tr) table.getContent().get(rownumber);
			switch (rownumber) {
			case 0: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), getMessage("report.summary_stage.phase.characteristics", null, "Phase characteristics", locale));
				for (SummaryStage stage : summary) {
					setCellText((Tc) row.getContent().get(++cellnumber),
							stage.getStage().equalsIgnoreCase("Start(P0)") ? getMessage("report.summary_stage.phase.start", null, stage.getStage(), locale)
									: getMessage("report.summary_stage.phase", stage.getStage().split(" "), stage.getStage(), locale));
				}
				setRepeatHeader(row);
				break;
			}
			case 1:
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText((Tc) row.getContent().get(0), "1	" + getMessage("report.summary_stage.phase_duration", null, "Phase duration", locale));
				break;
			case 2: {
				setCellText((Tc) row.getContent().get(0), "1.1	" + getMessage("report.summary_stage.date.beginning", null, "Beginning date", locale));
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
				for (int i = 1; i < summary.size(); i++) {
					addCellParagraph((Tc) row.getContent().get(i + 1), dateFormat.format(analysis.findPhaseByNumber(i).getBeginDate()));
				}
				break;
			}
			case 3: {
				setCellText((Tc) row.getContent().get(0), "1.2	" + getMessage("report.summary_stage.date.end", null, "End date", locale));
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
				for (int i = 1; i < summary.size(); i++)
					addCellParagraph((Tc) row.getContent().get(i + 1), dateFormat.format(analysis.findPhaseByNumber(i).getEndDate()));
				break;
			}

			case 4:
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText((Tc) row.getContent().get(0), "2	" + getMessage("report.summary_stage.compliance", null, "Compliance", locale));
				break;
			case 5: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"2.1	" + getMessage("report.summary_stage.compliance.level", new Object[] { "27001" }, "Compliance level 27001 (%)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber),
							numberFormat.format(stage.getSingleConformance("27001") == null ? 0 : stage.getSingleConformance("27001") * 100));
				break;
			}
			case 6: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"2.2	" + getMessage("report.summary_stage.compliance.level", new Object[] { "27002" }, "Compliance level 27002 (%)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber),
							numberFormat.format(stage.getSingleConformance("27002") == null ? 0 : stage.getSingleConformance("27002") * 100));
				break;
			}

			case 7: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"2.3	" + getMessage("report.characteristic.count.not_compliant_measure", new Object[] { "27001" }, "Non-compliant measures of the 27001", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), stage.getNotCompliantMeasure27001Count() + "");
				break;
			}

			case 8: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"2.4	" + getMessage("report.characteristic.count.not_compliant_measure", new Object[] { "27002" }, "Non-compliant measures of the 27002", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), stage.getNotCompliantMeasure27002Count() + "");
				break;
			}

			case 9:
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText((Tc) row.getContent().get(0),
						"3	" + getMessage("report.summary_stage.evolution_of_implemented_measure", null, "Evolution of implemented measures", locale));
				break;
			case 10: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"3.1	" + getMessage("report.summary_stage.number_of_measure_for_phase", null, "Number of measures for phase", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), "" + stage.getMeasureCount());
				break;
			}
			case 11: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"3.2	" + getMessage("report.summary_stage.implementted_measures", null, "Implemented measures (number)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), "" + stage.getImplementedMeasuresCount());
				break;
			}
			case 12: {
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText((Tc) row.getContent().get(0), "4	" + getMessage("report.summary_stage.profitability", null, "Profitability", locale));
				break;
			}
			case 13: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "4.1	" + getMessage("report.summary_stage.ale_at_end", null, "ALE (k€/y)... at end", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getTotalALE() * 0.001));
				break;
			}
			case 14: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "4.2	" + getMessage("report.summary_stage.risk_reduction", null, "Risk reduction (k€/y)", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getDeltaALE() * 0.001));
				break;
			}
			case 15: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"4.3	" + getMessage("report.summary_stage.average_yearly_cost_of_phase", null, "Average yearly cost of phase (k€/y)", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getCostOfMeasures() * 0.001));
				break;
			}
			case 16: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "4.3	" + getMessage("report.summary_stage.rosi", null, "ROSI (k€/y)", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getROSI() * 0.001));
				break;
			}
			case 17: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "4.4	" + getMessage("report.summary_stage.rosi.relative", null, "Relative ROSI", locale));
				DecimalFormat format = (DecimalFormat) numberFormat.clone();
				format.setMaximumFractionDigits(2);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), format.format(stage.getRelativeROSI()));
				break;
			}
			case 18: {
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText((Tc) row.getContent().get(0), "5	" + getMessage("report.summary_stage.resource.planning", null, "Resource planning", locale));
				break;
			}

			case 19: {
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText((Tc) row.getContent().get(0), "5.1	" + getMessage("report.summary_stage.implementation.cost", null, "Implementation costs", locale));
				break;
			}
			case 20: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "5.1.1	" + getMessage("report.summary_stage.workload.internal", null, "Internal workload (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getInternalWorkload()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 21: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "5.1.2	" + getMessage("report.summary_stage.workload.external", null, "External workload (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getExternalWorkload()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 22: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "5.1.3	" + getMessage("report.summary_stage.investment", null, "Investment (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(Math.floor(stage.getInvestment() * 0.001)));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 23: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"5.1.4	" + getMessage("report.summary_stage.total.implement.phase.cost", null, "Total implement cost of phase (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getImplementCostOfPhase() * 0.001), true);
				numberFormat.setMaximumFractionDigits(0);
				break;
			}

			case 24: {
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText((Tc) row.getContent().get(0), "5.2	" + getMessage("report.summary_stage.cost.recurrent", null, "Recurrent costs", locale));
				break;
			}

			case 25: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "5.2.1	" + getMessage("report.summary_stage.maintenance.internal", null, "Internal maintenance (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getInternalMaintenance()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 26: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "5.2.2	" + getMessage("report.summary_stage.maintenance.external", null, "External maintenance (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getExternalMaintenance()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 27: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "5.2.3	" + getMessage("report.summary_stage.investment.recurrent", null, "Recurrent investment (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getRecurrentInvestment() * 0.001));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}

			case 28: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"5.2.4	" + getMessage("report.summary_stage.total.cost.recurrent", null, "Total recurrent costs (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getRecurrentCost() * 0.001), true);
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 29: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "5.3	" + getMessage("report.summary_stage.cost.total_of_phase", null, "Total cost of phase (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getTotalCostofStage() * 0.001), true);
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			}
			rownumber++;
		}

		insertBofore(paragraph, table);

		if (!summary.isEmpty()) {
			setCustomProperty("FINAL_ALE_VAL", (long) (summary.get(summary.size() - 1).getTotalALE() * 0.001));
		}
	}

	@Override
	protected void generateAssessements() throws Exception {

		P paragraphOrigin = findTableAnchor("Assessment");

		List<Assessment> assessments = analysis.getSelectedAssessments();

		Collections.sort(assessments, new AssessmentComparator());

		Map<String, Double> aleByAssetTypes = new LinkedHashMap<>();

		DecimalFormat assessmentFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);
		assessmentFormat.setMinimumFractionDigits(1);
		assessmentFormat.setMaximumFractionDigits(1);

		double totalale = 0;

		for (Assessment assessment : assessments) {
			totalale += assessment.getALE();
			String assetType = assessment.getAsset().getAssetType().getName().toUpperCase();
			aleByAssetTypes.put(assetType, aleByAssetTypes.getOrDefault(assetType, 0D) + assessment.getALE());
		}

		for (Entry<String, Double> entry : aleByAssetTypes.entrySet())
			setCustomProperty(entry.getKey() + "_Rsk", (long) (entry.getValue() * 0.001));

		setCustomProperty("TOTAL_ALE_VAL", (long) (totalale * 0.001));

		if (paragraphOrigin != null && assessments.size() > 0) {

			List<Object> contents = new LinkedList<>();

			Map<String, ALE> alesmap = new LinkedHashMap<String, ALE>();
			Map<String, List<Assessment>> assessementsmap = new LinkedHashMap<String, List<Assessment>>();
			AssessmentAndRiskProfileManager.SplitAssessment(assessments, alesmap, assessementsmap);
			List<ALE> ales = new ArrayList<ALE>(alesmap.size());
			for (ALE ale : alesmap.values())
				ales.add(ale);
			alesmap.clear();
			setCurrentParagraphId(TS_TAB_TEXT_2);
			Collections.sort(ales, new AssetComparatorByALE());
			P paragraph = setStyle(factory.createP(), "TSAssessmentTotalALE");
			setText(paragraph, getMessage("report.assessment.total_ale.assets", null, "Total ALE for all assets", locale));
			addTab(paragraph);
			addText(paragraph, String.format("%s k€", kEuroFormat.format(totalale * 0.001)));

			contents.add(paragraph);

			TextAlignment alignmentLeft = createAlignment("left"), alignmentCenter = createAlignment("center");
			for (ALE ale : ales) {
				paragraph = factory.createP();
				setText(paragraph, ale.getAssetName());
				setStyle(paragraph, "TSEstimationTitle");

				contents.add(paragraph);

				paragraph = factory.createP();
				setText(paragraph, getMessage("report.assessment.total.ale.for.asset", null, "Total ALE of asset", locale));
				addTab(paragraph);
				setText(paragraph, String.format("%s k€", kEuroFormat.format(ale.getValue() * 0.001)), true);
				setStyle(paragraph, "TSAssessmentTotalALE");

				contents.add(paragraph);

				List<Assessment> assessmentsofasset = assessementsmap.get(ale.getAssetName());
				Tbl table = createTable("TableTSAssessment", assessmentsofasset.size() + 1, 6);
				Tr row = (Tr) table.getContent().get(0);
				setCellText((Tc) row.getContent().get(0), getMessage("report.assessment.scenarios", null, "Scenarios", locale), alignmentLeft);
				setCellText((Tc) row.getContent().get(1), getMessage("report.assessment.impact.financial", null, "Fin.", locale), alignmentCenter);
				setCellText((Tc) row.getContent().get(2), getMessage("report.assessment.probability", null, "P.", locale), alignmentCenter);
				setCellText((Tc) row.getContent().get(3), getMessage("report.assessment.ale", null, "ALE(k€/y)", locale));
				setCellText((Tc) row.getContent().get(4), getMessage("report.assessment.owner", null, "Owner", locale));
				setCellText((Tc) row.getContent().get(5), getMessage("report.assessment.comment", null, "Comment", locale));
				int index = 1;
				for (Assessment assessment : assessmentsofasset) {
					row = (Tr) table.getContent().get(index++);
					setCellText((Tc) row.getContent().get(0), assessment.getScenario().getName(), alignmentLeft);
					IValue impact = assessment.getImpact(Constant.DEFAULT_IMPACT_NAME);
					if (impact == null)
						throw new TrickException("error.analysis.repport.unsupported", "Analysis cannot export repport");
					addCellNumber((Tc) row.getContent().get(1), kEuroFormat.format(impact.getReal() * 0.001));
					setCellText((Tc) row.getContent().get(2), formatLikelihood(assessment.getLikelihood()), alignmentCenter);
					addCellNumber((Tc) row.getContent().get(3),
							assessment.getALE() == 0 ? kEuroFormat.format(assessment.getALE() * 0.001) : assessmentFormat.format(assessment.getALE() * 0.001));
					addCellParagraph((Tc) row.getContent().get(4), assessment.getOwner());
					addCellParagraph((Tc) row.getContent().get(5), assessment.getComment());
				}

				contents.add(table);
				contents.add(addTableCaption(getMessage("report.assessment.table.caption", new Object[] { ale.getAssetName() },
						String.format("Risk estimation for the asset %s", ale.getAssetName()), locale)));
			}
			insertAllAfter(paragraphOrigin, contents);
			assessementsmap.clear();
			ales.clear();
			contents.clear();
		}

	}

	@Override
	protected void generateAssets(String name, List<Asset> assets) throws XPathBinderAssociationIsPartialException, JAXBException {
		P paragraph = findTableAnchor(name);
		if (paragraph != null) {
			setCurrentParagraphId(TS_TAB_TEXT_2);
			Tbl table = createTable("TableTSAsset", assets.size() + 1, 6);
			Tr row = (Tr) table.getContent().get(0);
			TextAlignment alignment = createAlignment("left");
			// set header
			setCellText((Tc) row.getContent().get(0), getMessage("report.asset.title.number.row", null, "Nr", locale));
			setCellText((Tc) row.getContent().get(1), getMessage("report.asset.title.name", null, "Name", locale));
			setCellText((Tc) row.getContent().get(2), getMessage("report.asset.title.type", null, "Type", locale));
			setCellText((Tc) row.getContent().get(3), getMessage("report.asset.title.value", null, "Value(k€)", locale));
			setCellText((Tc) row.getContent().get(4), getMessage("report.asset.title.ale", null, "ALE(k€)", locale));
			setCellText((Tc) row.getContent().get(5), getMessage("report.asset.title.comment", null, "Comment", locale));
			setRepeatHeader(row);
			int number = 1;
			// set data
			for (Asset asset : assets) {
				row = (Tr) table.getContent().get(number);
				setCellText((Tc) row.getContent().get(0), "" + (number++));
				setCellText((Tc) row.getContent().get(1), asset.getName(), alignment);
				setCellText((Tc) row.getContent().get(2), getDisplayName(asset.getAssetType()));
				addCellNumber((Tc) row.getContent().get(3), kEuroFormat.format(asset.getValue() * 0.001));
				setColor(((Tc) row.getContent().get(4)), LIGHT_CELL_COLOR);
				addCellNumber((Tc) row.getContent().get(4), kEuroFormat.format(asset.getALE() * 0.001));
				addCellParagraph((Tc) row.getContent().get(5), asset.getComment());
			}
			insertBofore(paragraph, table);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void generateExtendedParameters(String type) throws Exception {
		String parmetertype = "";
		if (type.equals(Constant.DEFAULT_IMPACT_NAME))
			parmetertype = "Impact";
		else if (type.equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME))
			parmetertype = "Proba";

		P paragraph = findTableAnchor(parmetertype);

		setCurrentParagraphId(TS_TAB_TEXT_2);

		List<IBoundedParameter> parameters = (List<IBoundedParameter>) analysis.findParametersByType(type);

		if (paragraph != null && parameters.size() > 0) {
			P title = setText(setStyle(factory.createP(), "TSEstimationTitle"), getMessage("report.parameter.title." + parmetertype.toLowerCase(), null, parmetertype, locale));
			insertBofore(paragraph, title);
			Tbl table = createTable("TableTS" + parmetertype, parameters.size() + 1, 6);
			Tr row = (Tr) table.getContent().get(0);
			for (int i = 1; i < 6; i++)
				setColor((Tc) row.getContent().get(i), HEADER_COLOR);
			setCellText((Tc) row.getContent().get(0), getMessage("report.parameter.level", null, "Level", locale));
			setCellText((Tc) row.getContent().get(1), getMessage("report.parameter.acronym", null, "Acro", locale));
			setCellText((Tc) row.getContent().get(2), getMessage("report.parameter.qualification", null, "Qualification", locale));

			if (parmetertype.equals("Proba"))
				setCellText((Tc) row.getContent().get(3), getMessage("report.parameter.proba.value", null, "Value (/y)", locale));
			else
				setCellText((Tc) row.getContent().get(3), getMessage("report.parameter.value", null, "Value (k€/y)", locale));

			setCellText((Tc) row.getContent().get(4), getMessage("report.parameter.value.from", null, "Value From", locale));
			setCellText((Tc) row.getContent().get(5), getMessage("report.parameter.value.to", null, "Value To", locale));

			setRepeatHeader(row);

			TextAlignment alignmentCenter = createAlignment("center");

			int countrow = 0, length = parameters.size() - 1;
			// set data
			for (IBoundedParameter parameter : parameters) {
				row = (Tr) table.getContent().get(countrow + 1);
				setCellText((Tc) row.getContent().get(0), "" + parameter.getLevel(), alignmentCenter);
				setCellText((Tc) row.getContent().get(1), parameter.getAcronym(), alignmentCenter);
				setCellText((Tc) row.getContent().get(2), parameter.getDescription());
				Double value = 0.;
				value = parameter.getValue();
				if (type.equals(Constant.DEFAULT_IMPACT_NAME))
					value *= 0.001;
				addCellNumber((Tc) row.getContent().get(3), kEuroFormat.format(value));
				if (countrow % 2 != 0)
					setColor((Tc) row.getContent().get(3), SUB_HEADER_COLOR);
				value = parameter.getBounds().getFrom();
				if (type.equals(Constant.DEFAULT_IMPACT_NAME))
					value *= 0.001;
				addCellNumber((Tc) row.getContent().get(4), kEuroFormat.format(value));
				if (parameter.getLevel() == length)
					addCellNumber((Tc) row.getContent().get(5), "+∞");
				else {
					value = parameter.getBounds().getTo();
					if (type.equals(Constant.DEFAULT_IMPACT_NAME))
						value *= 0.001;
					addCellNumber((Tc) row.getContent().get(5), kEuroFormat.format(value));
				}
				for (int i = 4; i < 6; i++)
					setColor((Tc) row.getContent().get(i), SUB_HEADER_COLOR);
				countrow++;
			}
			insertBofore(paragraph, table);
		}
	}

	@Override
	protected void generateOtherData() throws Exception {
		Map<String, Double> assetTypeValues = analysis.findSelectedAssets().stream()
				.collect(Collectors.groupingBy(asset -> asset.getAssetType().getName(), Collectors.summingDouble(Asset::getValue)));
		double assetTotalValue = 0;
		for (Entry<String, Double> entry : assetTypeValues.entrySet()) {
			assetTotalValue += entry.getValue();
			setCustomProperty(entry.getKey().toUpperCase() + "_Val", (long) (entry.getValue() * 0.001));
		}

		DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);
		decimalFormat.setMaximumFractionDigits(1);

		setCustomProperty("TOTAL_ASSET_VAL", decimalFormat.format(assetTotalValue * 0.001));

		Double compliance = analysis.getAnalysisStandards().stream().mapToDouble(analysisStandard -> ChartGenerator.ComputeCompliance(analysisStandard, valueFactory)).average()
				.orElse(0);

		setCustomProperty("CURRENT_COMPLIANCE", compliance.longValue());

		List<Phase> phases = analysis.findUsablePhase();

		double time = 0, sumRosi = 0, sumDRosi = 0, sumCost = 0;
		for (SummaryStage stage : getSummaryStage()) {
			Phase phase = phases.stream().filter(p -> stage.getStage().equals("Phase " + p.getNumber())).findAny().orElse(null);
			if (phase == null)
				continue;
			time += phase.getTime();
			sumRosi += stage.getROSI();
			sumDRosi += stage.getRelativeROSI() * stage.getCostOfMeasures();
			sumCost += stage.getCostOfMeasures();
		}

		if (time == 0)
			time = 1;
		if (sumCost == 0)
			sumCost = 1;

		double avRosi = sumRosi / (time * 1000), avDRosi = sumDRosi / (time * sumCost);

		setCustomProperty("AV_ROSI_VAL", decimalFormat.format(avRosi));

		setCustomProperty("AV_DROSI_VAL", (long) avDRosi);

		decimalFormat.setMaximumFractionDigits(2);

		setCustomProperty("GAIN_VAL", decimalFormat.format(1 + avDRosi * 0.01));

	}

	@Override
	protected void writeChart(Docx4jExcelSheet reportExcelSheet) throws Exception {
	}

	private void generateEvolutionOfProfitabilityGraphic() throws Exception {
		Map<String, List<Object>> summaries = ActionPlanSummaryManager.buildChartData(getSummaryStage(), analysis.getPhases());
		if (summaries.isEmpty())
			return;
		Chart chart = (Chart) findChart("ChartRentability");
		if (chart == null)
			return;
		String path = chart.getRelationshipsPart().getRelationships().getRelationship().parallelStream().filter(r -> r.getTarget().endsWith(".xlsx")).map(Relationship::getTarget)
				.findAny().orElse(null);
		if (path == null)
			return;
		Part excel = wordMLPackage.getParts().get(new PartName("/word" + path.replace("..", "")));
		if (excel == null)
			return;
		Docx4jExcelSheet docx4jExcelSheet = null;
		try {
			CTBarChart barChart = (CTBarChart) chart.getContents().getChart().getPlotArea().getAreaChartOrArea3DChartOrLineChart().parallelStream()
					.filter(c -> c instanceof CTBarChart).findAny().orElse(null);
			if (barChart == null)
				return;

			chart.getContents().getChart().getPlotArea().getValAxOrCatAxOrDateAx().parallelStream().filter(valAx -> valAx instanceof CTValAx).map(valAx -> (CTValAx) valAx)
					.forEach(valAx -> {
						valAx.getNumFmt().setSourceLinked(false);
						valAx.getNumFmt().setFormatCode(NUMBER_FORMAT);
					});

			chart.getContents().getChart().getDispBlanksAs().setVal(STDispBlanksAs.GAP);

			barChart.getSer().clear();

			if (barChart.getDLbls().getShowVal() == null)
				barChart.getDLbls().setShowVal(new CTBoolean());

			barChart.getDLbls().getShowVal().setVal(true);

			if (barChart.getDLbls().getNumFmt() == null)
				barChart.getDLbls().setNumFmt(new CTNumFmt());

			barChart.getDLbls().getNumFmt().setFormatCode(NUMBER_FORMAT);

			docx4jExcelSheet = new Docx4jExcelSheet((EmbeddedPackagePart) excel, String.format("%s/WEB-INF/tmp/", contextPath));
			String[] dataName = { "ALE", "COST", "ROSI", "LOST" };
			int[] colorIndex = { 1, 3, 5, 7 };

			Map<String, CTBarSer> profiltabilityDatasets = new LinkedHashMap<>(dataName.length);

			SheetData sheet = docx4jExcelSheet.getWorkbook().getWorksheet(0).getContents().getSheetData();

			Map<String, Phase> usesPhases = ActionPlanSummaryManager.buildPhase(analysis.getPhases(), ActionPlanSummaryManager.extractPhaseRow(getSummaryStage()));

			for (int i = 0; i < dataName.length; i++) {
				CTBarSer ser = createChart(String.format("%s!$A$%s", docx4jExcelSheet.getName(), i + 2), i,
						getMessage("label.title.chart.evolution_profitability." + dataName[i].toLowerCase(), null, null, locale), new CTBarSer());
				profiltabilityDatasets.put(dataName[i], ser);
				ser.getVal().getNumRef().getNumCache().setFormatCode(NUMBER_FORMAT);
				barChart.getSer().add(ser);
				setColor(ser, ChartGenerator.getStaticColor(colorIndex[i]));

				setValue(getRow(sheet, i + 1, usesPhases.size() + 1), 0, dataName[i]);
			}

			CTBarSer barSer = profiltabilityDatasets.get(dataName[0]);

			Row rowPhase = sheet.getRow().get(0);

			for (Phase phase : usesPhases.values()) {
				CTStrVal catName = new CTStrVal();
				catName.setV("P" + phase.getNumber());
				catName.setIdx(barSer.getCat().getStrRef().getStrCache().getPt().size());
				barSer.getCat().getStrRef().getStrCache().getPt().add(catName);
				setValue(rowPhase, barSer.getCat().getStrRef().getStrCache().getPt().size(), catName.getV());
			}

			barSer.getCat().getStrRef().setF(String.format("%s!$B$1:$%s$1", docx4jExcelSheet.getName(), (char) ('B' + barSer.getCat().getStrRef().getStrCache().getPt().size())));

			for (int i = 1; i < dataName.length; i++)
				profiltabilityDatasets.get(dataName[i]).setCat(barSer.getCat());
			for (int i = 0; i < usesPhases.size(); i++) {
				for (int j = 0; j < dataName.length; j++) {
					CTBarSer ser = profiltabilityDatasets.get(dataName[j]);
					Double rosi = (double) summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI).get(i), value = 0d;
					switch (dataName[j]) {
					case "ALE":
						value = (Double) summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ALE_UNTIL_END).get(i);
						break;
					case "COST":
						if (rosi >= 0)
							value = (Double) summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_AVERAGE_YEARLY_COST_OF_PHASE).get(i);
						else {
							List<Object> ales = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ALE_UNTIL_END);
							value = ((Number) ales.get(i - 1)).doubleValue() - ((Number) ales.get(i)).doubleValue();
						}
						setColor(ser, ChartGenerator.getStaticColor(1));
						break;
					case "ROSI":
						if (rosi >= 0)
							value = rosi;
						break;
					case "LOST":
						if (rosi < 0)
							value = (rosi * -1);
						break;
					}
					CTNumVal numVal = new CTNumVal();
					numVal.setIdx(i);
					if (value > 0) {
						numVal.setV(value + "");
						setValue(sheet.getRow().get(j + 1), i + 1, value);
					}
					ser.getVal().getNumRef().getNumCache().getPt().add(numVal);
				}
			}

		} finally {
			if (docx4jExcelSheet != null)
				docx4jExcelSheet.save();
		}

	}

	private void generateALEByAssetTypeGraphic() throws Exception {
		List<Assessment> assessments = analysis.getSelectedAssessments();
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getAsset().getAssetType().getId());
			if (ale == null)
				ales.put(assessment.getAsset().getAssetType().getId(), ale = new ALE(assessment.getAsset().getAssetType().getName(), 0));
			ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
		}

		generateALEChart(ales, "ChartALEByAssetType", getMessage("report.chart.ale.title.asset.type", null, "Asset type", locale),
				getMessage("report.chart.asset.type", null, "Asset type", locale), "AleByAssetType", "report.chart.ale.title.asset.type.index");

	}

	private void generateALEByScenarioGraphic() throws Exception {
		List<Assessment> assessments = analysis.getSelectedAssessments();
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getScenario().getId());
			if (ale == null)
				ales.put(assessment.getScenario().getId(), ale = new ALE(assessment.getScenario().getName(), 0));
			ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
		}
		generateALEChart(ales, "ChartALEByScenario", getMessage("report.chart.ale.title.scenario", null, "Scenario", locale),
				getMessage("report.chart.scenario", null, "Scenario", locale), "AleBySceanrio", "report.chart.ale.title.scenario.index");
	}

	private void generateALEByScenarioTypeGraphic() throws Exception {

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

		generateALEChart(ales, "ChartALEByScenarioType", getMessage("report.chart.ale.title.scenario.type", null, "Scenario type", locale),
				getMessage("report.chart.scenario.type", null, "Scenario type", locale), "AleBySceanrioType", "report.chart.ale.title.scenario.type.index");

	}

	@Override
	protected AnalysisType getType() {
		return AnalysisType.QUANTITATIVE;
	}

	@Override
	protected ActionPlanMode getActionPlanType() {
		return ActionPlanMode.APPN;
	}

	@Override
	protected void updateGraphics() throws Exception {
		generateALEByAssetGraphic();
		generateALEByAssetTypeGraphic();
		generateALEByScenarioGraphic();
		generateALEByScenarioTypeGraphic();
		generateEvolutionOfProfitabilityGraphic();
	}

	private void generateALEByAssetGraphic() throws Exception {

		List<Assessment> assessments = analysis.getSelectedAssessments();
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getAsset().getId());
			if (ale == null)
				ales.put(assessment.getAsset().getId(), ale = new ALE(assessment.getAsset().getName(), 0));
			ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
		}
		generateALEChart(ales, "ChartALEByAsset", getMessage("report.chart.ale.title.asset", null, "ALE By Asset", locale), getMessage("report.chart.asset", null, "Asset", locale),
				"AleByAsset", "report.chart.ale.title.asset.index");
	}

	private void generateALEChart(Map<Integer, ALE> ales, String chartName, String title, String column, String name, String multiTitleCode) throws Exception {
		List<ALE> ales2 = ales.values().parallelStream().filter(ale -> ale.getValue() > 0).sorted(new AssetComparatorByALE()).collect(Collectors.toList());
		if (ales2.size() <= Constant.CHAR_SINGLE_CONTENT_MAX_SIZE)
			generateALEChart(ales2, (Chart) findChart(chartName), title, column);
		else {
			List<Part> parts = duplicateChart(ales2.size(), chartName, name);
			int count = parts.size(), divisor = Math.floorDiv(ales2.size(), count);
			for (int i = 0; i < count; i++)
				generateALEChart(ales2.subList(i * divisor, i == (count - 1) ? ales2.size() : (i + 1) * divisor), (Chart) parts.get(i),
						getMessage(multiTitleCode, new Object[] { i + 1, count }, null, locale), column);
		}
	}

	private void generateALEChart(List<ALE> ales, Chart chart, String title, String name) throws Exception {
		if (chart == null)
			return;
		String path = chart.getRelationshipsPart().getRelationships().getRelationship().parallelStream().filter(r -> r.getTarget().endsWith(".xlsx")).map(Relationship::getTarget)
				.findAny().orElse(null);
		if (path == null)
			return;
		Part excel = wordMLPackage.getParts().get(new PartName("/word" + path.replace("..", "")));
		if (excel == null)
			return;
		Docx4jExcelSheet reportExcelSheet = null;
		try {
			reportExcelSheet = new Docx4jExcelSheet((EmbeddedPackagePart) excel, String.format("%s/WEB-INF/tmp/", contextPath));
			CTBarChart barChart = (CTBarChart) chart.getContents().getChart().getPlotArea().getAreaChartOrArea3DChartOrLineChart().parallelStream()
					.filter(c -> c instanceof CTBarChart).findAny().orElse(null);
			if (barChart == null)
				return;

			SheetData sheet = reportExcelSheet.getWorkbook().getWorksheet(0).getContents().getSheetData();

			CTRegularTextRun r = (CTRegularTextRun) chart.getContents().getChart().getTitle().getTx().getRich().getP().get(0).getEGTextRun().get(0);
			r.setT(title);

			chart.getContents().getChart().getPlotArea().getValAxOrCatAxOrDateAx().parallelStream().filter(valAx -> valAx instanceof CTValAx).map(valAx -> (CTValAx) valAx)
					.forEach(valAx -> {
						valAx.getNumFmt().setSourceLinked(false);
						valAx.getNumFmt().setFormatCode(NUMBER_FORMAT);
					});

			chart.getContents().getChart().getDispBlanksAs().setVal(STDispBlanksAs.GAP);

			barChart.getSer().clear();

			CTBarSer ser = createChart(String.format("%s!$B$1", reportExcelSheet.getName()), 0, name, new CTBarSer());

			ser.getVal().getNumRef().getNumCache().setFormatCode(NUMBER_FORMAT);

			if (barChart.getDLbls().getShowVal() == null)
				barChart.getDLbls().setShowVal(new CTBoolean());

			barChart.getDLbls().getShowVal().setVal(true);

			if (barChart.getDLbls().getNumFmt() == null)
				barChart.getDLbls().setNumFmt(new CTNumFmt());

			barChart.getDLbls().getNumFmt().setFormatCode(NUMBER_FORMAT);

			int rowCount = 0, colSzie = 2;
			setValue(getRow(sheet, rowCount++, colSzie), 0, name);
			for (ALE ale : ales) {
				CTStrVal catName = new CTStrVal();
				catName.setV(ale.getAssetName());
				catName.setIdx(rowCount - 1);
				ser.getCat().getStrRef().getStrCache().getPt().add(catName);
				CTNumVal numVal = new CTNumVal();

				numVal.setIdx(rowCount - 1);
				if (ale.getValue() > 0) {
					numVal.setV(ale.getValue() + "");
					setValue(getRow(sheet, rowCount, colSzie), 1, ale.getValue());
				}

				ser.getVal().getNumRef().getNumCache().getPt().add(numVal);
				setValue(getRow(sheet, rowCount++, colSzie), 0, ale.getAssetName());

			}

			ser.getCat().getStrRef().setF(String.format("%s!$A$2:$A$%d", reportExcelSheet.getName(), ser.getCat().getStrRef().getStrCache().getPt().size() + 1));
			ser.getVal().getNumRef().setF(String.format("%s!$B$2:$B$%d", reportExcelSheet.getName(), ser.getCat().getStrRef().getStrCache().getPt().size() + 1));

			barChart.getSer().add(ser);

		} finally {
			if (reportExcelSheet != null)
				reportExcelSheet.save();
		}

	}

}
