/**
 *
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain;

import static lu.itrust.business.TS.exportation.word.ExportReport.TS_TAB_TEXT_2;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.docx4j.wml.P;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exportation.word.IDocxBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.TS.exportation.word.impl.docx4j.DocxChainFactory;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jData;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;

/**
 * o
 *
 * @author eomar
 *
 */
public class Docx4jSummaryBuilder extends Docx4jBuilder {

	private static final String TS_QT_SUMMARY = "ts_qt_summary";
	private static final String TS_QL_SUMMARY = "ts_ql_summary";

	/**
	 * @param next
	 * @param supports
	 */
	public Docx4jSummaryBuilder(IDocxBuilder next) {
		super(next, TS_QL_SUMMARY, TS_QT_SUMMARY);
	}

	@Override
	protected boolean internalBuild(Docx4jData data) {
		switch (data.getAnchor()) {
		case TS_QL_SUMMARY:
			return buildQualitative(data);
		case TS_QT_SUMMARY:
			return buildQuantitative(data);
		default:
			return true;
		}

	}

	private boolean buildQuantitative(Docx4jData data) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = exporter.findP(data.getSource());
		if (paragraph != null) {
			final Analysis analysis = exporter.getAnalysis();
			final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
			final List<SummaryStage> summary = analysis.findSummary(ActionPlanMode.APPN);
			final List<String> collectionNames = exporter.getStandardNames();
			final Tbl table = exporter.createTable("TableTSSummary", 23 + exporter.computeSommuryLength(collectionNames), summary.size() + 1);
			exporter.setCurrentParagraphId(TS_TAB_TEXT_2);
			buildSummaryCosts(exporter, summary, table, "5", buildSummaryProfitabilities(exporter, summary, table,
					buildSummaryCompliance(exporter, summary, collectionNames, table, buildSummaryHeaders(exporter, dateFormat, summary, table, 0))));
			if (exporter.insertBefore(paragraph, table))
				DocxChainFactory.format(table, exporter.getDefaultTableStyle(), AnalysisType.QUANTITATIVE, exporter.getColors());
		}
		return false;
	}

	private boolean buildQualitative(Docx4jData data) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = exporter.findP(data.getSource());
		if (paragraph != null) {
			exporter.setCurrentParagraphId(TS_TAB_TEXT_2);
			final Analysis analysis = exporter.getAnalysis();
			final List<String> collectionNames = exporter.getStandardNames();
			final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
			final List<SummaryStage> summaries = analysis.findSummary(ActionPlanMode.APQ);
			final Tbl table = exporter.createTable("TableTSSummary", 17 + exporter.computeSommuryLength(collectionNames), summaries.size() + 1);
			buildSummaryCosts(exporter, summaries, table, "4",
					buildSummaryCompliance(exporter, summaries, collectionNames, table, buildSummaryHeaders(exporter, dateFormat, summaries, table, 0)));
			if (exporter.insertBefore(paragraph, table))
				DocxChainFactory.format(table, exporter.getDefaultTableStyle(), AnalysisType.QUALITATIVE, exporter.getColors());
		}
		return true;
	}

	private int buildSummaryCompliance(final Docx4jReportImpl exporter, final List<SummaryStage> summary, final List<String> collectionNames, final Tbl table, int rownumber) {
		Tr row = (Tr) table.getContent().get(rownumber++);
		mergeCell(row, 0, summary.size() + 1, exporter.getColors().getDark());
		exporter.setCellText((Tc) row.getContent().get(0), "2	" + exporter.getMessage("report.summary_stage.compliance", null, "Compliance"));
		int complianceIndex = 1;
		for (String standard : collectionNames) {
			int cellnumber = 0;
			row = (Tr) table.getContent().get(rownumber++);
			exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()), "2." + (complianceIndex++) + "	"
					+ exporter.getMessage("report.summary_stage.compliance.level", new Object[] { standard }, "Compliance level " + standard + " (%)..."));
			for (SummaryStage stage : summary)
				exporter.addCellNumber((Tc) row.getContent().get(++cellnumber),
						exporter.getNumberFormat().format(stage.getSingleConformance(standard) == null ? 0 : stage.getSingleConformance(standard) * 100));
		}

		if (collectionNames.contains(Constant.STANDARD_27001)) {
			int cellnumber = 0;
			row = (Tr) table.getContent().get(rownumber++);
			exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()), "2." + (complianceIndex++) + "	"
					+ exporter.getMessage("report.characteristic.count.not_compliant_measure", new Object[] { "27001" }, "Non-compliant measures of the 27001"));
			for (SummaryStage stage : summary)
				exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), stage.getNotCompliantMeasure27001Count() + "");
		}

		if (collectionNames.contains(Constant.STANDARD_27002)) {
			int cellnumber = 0;
			row = (Tr) table.getContent().get(rownumber++);
			exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()), "2." + (complianceIndex++) + "	"
					+ exporter.getMessage("report.characteristic.count.not_compliant_measure", new Object[] { "27002" }, "Non-compliant measures of the 27002"));
			for (SummaryStage stage : summary)
				exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), stage.getNotCompliantMeasure27002Count() + "");
		}

		row = (Tr) table.getContent().get(rownumber++);
		mergeCell(row, 0, summary.size() + 1, exporter.getColors().getDark());
		exporter.setCellText((Tc) row.getContent().get(0),
				"3	" + exporter.getMessage("report.summary_stage.evolution_of_implemented_measure", null, "Evolution of implemented measures"));

		int cellnumber = 0;
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				"3.1	" + exporter.getMessage("report.summary_stage.number_of_measure_for_phase", null, "Number of measures for phase"));
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), "" + stage.getMeasureCount());

		cellnumber = 0;
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				"3.2	" + exporter.getMessage("report.summary_stage.implementted_measures", null, "Implemented measures (number)..."));
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), "" + stage.getImplementedMeasuresCount());
		return rownumber;
	}

	private int buildSummaryHeaders(final Docx4jReportImpl exporter, final SimpleDateFormat dateFormat, final List<SummaryStage> summary, final Tbl table, int rownumber) {
		Tr row = (Tr) table.getContent().get(rownumber++);
		int cellnumber = 0;
		exporter.setCellText((Tc) row.getContent().get(cellnumber), exporter.getMessage("report.summary_stage.phase.characteristics", null, "Phase characteristics"));
		for (SummaryStage stage : summary) {
			exporter.setCellText((Tc) row.getContent().get(++cellnumber),
					stage.getStage().equalsIgnoreCase("Start(P0)") ? exporter.getMessage("report.summary_stage.phase.start", null, stage.getStage())
							: exporter.getMessage("report.summary_stage.phase", stage.getStage().split(" "), stage.getStage()));
		}
		exporter.setRepeatHeader(row);
		row = (Tr) table.getContent().get(rownumber++);
		mergeCell(row, 0, summary.size() + 1, exporter.getColors().getDark());
		exporter.setCellText((Tc) row.getContent().get(0), "1	" + exporter.getMessage("report.summary_stage.phase_duration", null, "Phase duration"));
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				"1.1	" + exporter.getMessage("report.summary_stage.date.beginning", null, "Beginning date"));
		for (int i = 1; i < summary.size(); i++)
			exporter.addCellParagraph((Tc) row.getContent().get(i + 1), dateFormat.format(exporter.getAnalysis().findPhaseByNumber(i).getBeginDate()));
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				"1.2	" + exporter.getMessage("report.summary_stage.date.end", null, "End date"));
		for (int i = 1; i < summary.size(); i++)
			exporter.addCellParagraph((Tc) row.getContent().get(i + 1), dateFormat.format(exporter.getAnalysis().findPhaseByNumber(i).getEndDate()));
		return rownumber;
	}

	private int buildSummaryProfitabilities(final Docx4jReportImpl exporter, final List<SummaryStage> summary, final Tbl table, int rownumber) {
		Tr row = (Tr) table.getContent().get(rownumber++);
		mergeCell(row, 0, summary.size() + 1, exporter.getColors().getDark());
		exporter.setCellText((Tc) row.getContent().get(0), "4	" + exporter.getMessage("report.summary_stage.profitability", null, "Profitability"));

		int cellnumber = 0;
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				"4.1	" + exporter.getMessage("report.summary_stage.ale_at_end", null, "ALE (k€/y)... at end"));
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), exporter.getNumberFormat().format(stage.getTotalALE() * 0.001));

		cellnumber = 0;
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				"4.2	" + exporter.getMessage("report.summary_stage.risk_reduction", null, "Risk reduction (k€/y)"));
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), exporter.getNumberFormat().format(stage.getDeltaALE() * 0.001));

		cellnumber = 0;
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				"4.3	" + exporter.getMessage("report.summary_stage.average_yearly_cost_of_phase", null, "Average yearly cost of phase (k€/y)"));
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), exporter.getNumberFormat().format(stage.getCostOfMeasures() * 0.001));

		cellnumber = 0;
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				"4.3	" + exporter.getMessage("report.summary_stage.rosi", null, "ROSI (k€/y)"));
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), exporter.getNumberFormat().format(stage.getROSI() * 0.001));

		cellnumber = 0;
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				"4.4	" + exporter.getMessage("report.summary_stage.rosi.relative", null, "Relative ROSI"));
		DecimalFormat format = (DecimalFormat) exporter.getNumberFormat().clone();
		format.setMaximumFractionDigits(2);
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), format.format(stage.getRelativeROSI()));
		return rownumber;
	}

	private void buildSummaryCosts(final Docx4jReportImpl exporter, final List<SummaryStage> summary, final Tbl table, final String numbering, int rownumber) {
		exporter.getNumberFormat().setMaximumFractionDigits(1);// modify decimal

		int cellnumber = 0;
		Tr row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText((Tc) row.getContent().get(0),
				numbering + "	" + exporter.getMessage("report.summary_stage.resource.planning", null, "Resource planning	Total cost of phase (k€)"));
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), exporter.getNumberFormat().format(stage.getTotalCostofStage() * 0.001), true);

		setColor(row, exporter.getColors().getDark());

		cellnumber = 0;
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				numbering + ".1	" + exporter.getMessage("report.summary_stage.implementation.cost", null, "Implementation costs"));
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), exporter.getNumberFormat().format(stage.getImplementCostOfPhase() * 0.001), true);

		cellnumber = 0;
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				numbering + ".1.1	" + exporter.getMessage("report.summary_stage.workload.internal", null, "Internal workload (md)"));
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), exporter.getNumberFormat().format(stage.getInternalWorkload()));

		cellnumber = 0;
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				numbering + ".1.2	" + exporter.getMessage("report.summary_stage.workload.external", null, "External workload (md)"));
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), exporter.getNumberFormat().format(stage.getExternalWorkload()));

		cellnumber = 0;
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				numbering + ".1.3	" + exporter.getMessage("report.summary_stage.investment", null, "Investment (k€)"));
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), exporter.getNumberFormat().format(Math.floor(stage.getInvestment() * 0.001)));

		cellnumber = 0;
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				numbering + ".2	" + exporter.getMessage("report.summary_stage.cost.recurrent", null, "Recurrent costs"));
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), exporter.getNumberFormat().format(stage.getRecurrentCost() * 0.001), true);

		cellnumber = 0;
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				numbering + ".2.1	" + exporter.getMessage("report.summary_stage.maintenance.internal", null, "Internal maintenance (md)"));
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), exporter.getNumberFormat().format(stage.getInternalMaintenance()));

		cellnumber = 0;
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				numbering + ".2.2	" + exporter.getMessage("report.summary_stage.maintenance.external", null, "External maintenance (md)"));
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), exporter.getNumberFormat().format(stage.getExternalMaintenance()));

		cellnumber = 0;
		row = (Tr) table.getContent().get(rownumber++);
		exporter.setCellText(setColor((Tc) row.getContent().get(0), exporter.getColors().getNormal()),
				numbering + ".2.3	" + exporter.getMessage("report.summary_stage.investment.recurrent", null, "Recurrent investment (k€)"));
		for (SummaryStage stage : summary)
			exporter.addCellNumber((Tc) row.getContent().get(++cellnumber), exporter.getNumberFormat().format(stage.getRecurrentInvestment() * 0.001));

		exporter.getNumberFormat().setMaximumFractionDigits(0);// restore decimal
	}

}
