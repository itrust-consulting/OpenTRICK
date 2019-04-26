/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain;

import static lu.itrust.business.TS.exportation.word.ExportReport.TS_TAB_TEXT_2;

import java.util.List;
import java.util.Locale;

import org.docx4j.wml.P;
import org.docx4j.wml.PPrBase.TextAlignment;
import org.docx4j.wml.R;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

import lu.itrust.business.TS.exportation.word.IDocxBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.TS.exportation.word.impl.docx4j.DocxChainFactory;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jData;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;

/**
 * @author eomar
 *
 */
public class Docx4jActionPlanBuilder extends Docx4jBuilder {

	private static final String TS_QT_ACTIONPLAN = "ts_qt_actionplan";
	private static final String TS_QL_ACTIONPLAN = "ts_ql_actionplan";

	/**
	 * @param next
	 * @param supports
	 */
	public Docx4jActionPlanBuilder(IDocxBuilder next) {
		super(next, TS_QL_ACTIONPLAN, TS_QT_ACTIONPLAN);
	}

	@Override
	protected boolean internalBuild(Docx4jData data) {
		switch (data.getAnchor()) {
		case TS_QL_ACTIONPLAN:
			return buildQualitative(data);
		case TS_QT_ACTIONPLAN:
			return buildQuantitative(data);
		default:
			return true;
		}
	}

	private boolean buildQualitative(Docx4jData data) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = exporter.findP(data.getSource());
		if (paragraph != null) {
			final List<ActionPlanEntry> actionplan = exporter.getAnalysis().findActionPlan(ActionPlanMode.APQ);
			final Tbl table = exporter.createTable("TableTSActionPlan", actionplan.size() + 1, 11);
			final TextAlignment alignment = exporter.createAlignment("left");
			final TextAlignment alignmentCenter = exporter.createAlignment("center");
			final Tr header = (Tr) table.getContent().get(0);
			exporter.setCurrentParagraphId(TS_TAB_TEXT_2);
			exporter.setCellText((Tc) header.getContent().get(0), exporter.getMessage("report.action_plan.row_number", null, "Nr"));
			exporter.setCellText((Tc) header.getContent().get(1), exporter.getMessage("report.action_plan.norm", null, "Stds"));
			exporter.setCellText((Tc) header.getContent().get(2), exporter.getMessage("report.action_plan.reference", null, "Ref."));
			exporter.setCellText((Tc) header.getContent().get(3), exporter.getMessage("report.action_plan.description", null, "Description"));
			exporter.setCellText((Tc) header.getContent().get(4), exporter.getMessage("report.action_plan.risk_count", null, "NR"));
			exporter.setCellText((Tc) header.getContent().get(5), exporter.getMessage("report.action_plan.cost", null, "CS"));
			exporter.setCellText((Tc) header.getContent().get(6), exporter.getMessage("report.action_plan.internal.workload", null, "IS"));
			exporter.setCellText((Tc) header.getContent().get(7), exporter.getMessage("report.action_plan.external.workload", null, "ES"));
			exporter.setCellText((Tc) header.getContent().get(8), exporter.getMessage("report.action_plan.investment", null, "INV"));
			exporter.setCellText((Tc) header.getContent().get(9), exporter.getMessage("report.measure.phase", null, "P"));
			exporter.setCellText((Tc) header.getContent().get(10), exporter.getMessage("report.measure.responsable", null, "Resp."));
			exporter.setRepeatHeader(header);

			int nr = 1;
			// set data
			for (ActionPlanEntry entry : actionplan) {
				final Tr row = (Tr) table.getContent().get(nr);
				exporter.setCellText((Tc) row.getContent().get(0), "" + (nr++), alignmentCenter);
				exporter.setCellText((Tc) row.getContent().get(1), entry.getMeasure().getAnalysisStandard().getStandard().getLabel());
				exporter.setCellText((Tc) row.getContent().get(2), entry.getMeasure().getMeasureDescription().getReference());
				MeasureDescriptionText descriptionText = entry.getMeasure().getMeasureDescription().findByLanguage(exporter.getAnalysis().getLanguage());
				exporter.addCellParagraph((Tc) row.getContent().get(3),
						descriptionText == null ? "" : descriptionText.getDomain() + (exporter.getLocale().equals(Locale.FRENCH) ? "\u00A0:" : ":"));
				((Tc) row.getContent().get(3)).getContent().parallelStream().flatMap(p -> ((P) p).getContent().parallelStream()).map(r -> (R) r).forEach(r -> {
					if (r.getRPr() == null)
						r.setRPr(exporter.getFactory().createRPr());
					r.getRPr().setB(exporter.getFactory().createBooleanDefaultTrue());
				});
				exporter.addCellParagraph((Tc) row.getContent().get(3), entry.getMeasure().getToDo(), true);
				exporter.setAlignment((Tc) row.getContent().get(3), alignment);
				exporter.addCellNumber((Tc) row.getContent().get(4), exporter.getNumberFormat().format(entry.getRiskCount()));
				exporter.addCellNumber((Tc) row.getContent().get(5), exporter.getNumberFormat().format(entry.getMeasure().getCost() * 0.001));
				exporter.getNumberFormat().setMaximumFractionDigits(1);
				exporter.addCellNumber((Tc) row.getContent().get(6), exporter.getNumberFormat().format(entry.getMeasure().getInternalWL()));
				exporter.addCellNumber((Tc) row.getContent().get(7), exporter.getNumberFormat().format(entry.getMeasure().getExternalWL()));
				exporter.getNumberFormat().setMaximumFractionDigits(0);
				exporter.addCellNumber((Tc) row.getContent().get(8), exporter.getNumberFormat().format(entry.getMeasure().getInvestment() * 0.001));
				exporter.addCellNumber((Tc) row.getContent().get(9), entry.getMeasure().getPhase().getNumber() + "");
				exporter.addCellNumber((Tc) row.getContent().get(10), entry.getMeasure().getResponsible());
			}
			if (exporter.insertBefore(paragraph, table))
				DocxChainFactory.format(table, exporter.getDefaultTableStyle(), AnalysisType.QUALITATIVE, exporter.getColors());
		}
		return true;
	}

	private boolean buildQuantitative(Docx4jData data) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = exporter.findP(data.getSource());
		if (paragraph != null) {
			final List<ActionPlanEntry> actionplan = exporter.getAnalysis().findActionPlan(ActionPlanMode.APPN);
			final TextAlignment alignment = exporter.createAlignment("left");
			final TextAlignment alignmentCenter = exporter.createAlignment("center");
			final Tbl table = exporter.createTable("TableTSActionPlan", actionplan.size() + 1, 13);
			final Tr header = (Tr) table.getContent().get(0);

			exporter.setCurrentParagraphId(TS_TAB_TEXT_2);
			exporter.setCellText((Tc) header.getContent().get(0), exporter.getMessage("report.action_plan.row_number", null, "Nr"));
			exporter.setCellText((Tc) header.getContent().get(1), exporter.getMessage("report.action_plan.norm", null, "Stds"));
			exporter.setCellText((Tc) header.getContent().get(2), exporter.getMessage("report.action_plan.reference", null, "Ref."));
			exporter.setCellText((Tc) header.getContent().get(3), exporter.getMessage("report.action_plan.description", null, "Description"));
			exporter.setCellText((Tc) header.getContent().get(4), exporter.getMessage("report.action_plan.ale", null, "ALE"));
			exporter.setCellText((Tc) header.getContent().get(5), exporter.getMessage("report.action_plan.delta_ale", null, "Δ ALE"));
			exporter.setCellText((Tc) header.getContent().get(6), exporter.getMessage("report.action_plan.cost", null, "CS"));
			exporter.setCellText((Tc) header.getContent().get(7), exporter.getMessage("report.action_plan.rosi", null, "ROSI"));
			exporter.setCellText((Tc) header.getContent().get(8), exporter.getMessage("report.action_plan.internal.workload", null, "IS"));
			exporter.setCellText((Tc) header.getContent().get(9), exporter.getMessage("report.action_plan.external.workload", null, "ES"));
			exporter.setCellText((Tc) header.getContent().get(10), exporter.getMessage("report.action_plan.investment", null, "INV"));
			exporter.setCellText((Tc) header.getContent().get(11), exporter.getMessage("report.measure.phase", null, "P"));
			exporter.setCellText((Tc) header.getContent().get(12), exporter.getMessage("report.measure.responsable", null, "Resp."));
			exporter.setRepeatHeader(header);

			int nr = 1;
			// set data
			for (ActionPlanEntry entry : actionplan) {
				final Tr row = (Tr) table.getContent().get(nr);
				exporter.setCellText((Tc) row.getContent().get(0), "" + (nr++), alignmentCenter);
				exporter.setCellText((Tc) row.getContent().get(1), entry.getMeasure().getAnalysisStandard().getStandard().getLabel());
				exporter.setCellText((Tc) row.getContent().get(2), entry.getMeasure().getMeasureDescription().getReference());
				final MeasureDescriptionText descriptionText = entry.getMeasure().getMeasureDescription().findByLanguage(exporter.getAnalysis().getLanguage());
				exporter.addCellParagraph((Tc) row.getContent().get(3),
						descriptionText == null ? "" : descriptionText.getDomain() + (exporter.getLocale().equals(Locale.FRENCH) ? "\u00A0:" : ":"));
				((Tc) row.getContent().get(3)).getContent().parallelStream().flatMap(p -> ((P) p).getContent().parallelStream()).map(r -> (R) r).forEach(r -> {
					if (r.getRPr() == null)
						r.setRPr(exporter.getFactory().createRPr());
					r.getRPr().setB(exporter.getFactory().createBooleanDefaultTrue());
				});
				exporter.addCellParagraph((Tc) row.getContent().get(3), entry.getMeasure().getToDo(), true);
				exporter.setAlignment((Tc) row.getContent().get(3), alignment);
				exporter.addCellNumber((Tc) row.getContent().get(4), exporter.getNumberFormat().format(entry.getTotalALE() * 0.001));
				exporter.addCellNumber((Tc) row.getContent().get(5), exporter.getNumberFormat().format(entry.getDeltaALE() * 0.001));
				exporter.addCellNumber((Tc) row.getContent().get(6), exporter.getNumberFormat().format(entry.getMeasure().getCost() * 0.001));
				exporter.addCellNumber((Tc) row.getContent().get(7), exporter.getNumberFormat().format(entry.getROI() * 0.001));
				exporter.getNumberFormat().setMaximumFractionDigits(1);
				exporter.addCellNumber((Tc) row.getContent().get(8), exporter.getNumberFormat().format(entry.getMeasure().getInternalWL()));
				exporter.addCellNumber((Tc) row.getContent().get(9), exporter.getNumberFormat().format(entry.getMeasure().getExternalWL()));
				exporter.getNumberFormat().setMaximumFractionDigits(0);
				exporter.addCellNumber((Tc) row.getContent().get(10), exporter.getNumberFormat().format(entry.getMeasure().getInvestment() * 0.001));
				exporter.addCellNumber((Tc) row.getContent().get(11), entry.getMeasure().getPhase().getNumber() + "");
				exporter.addCellNumber((Tc) row.getContent().get(12), entry.getMeasure().getResponsible());
			}
			if (exporter.insertBefore(paragraph, table))
				DocxChainFactory.format(table, exporter.getDefaultTableStyle(), AnalysisType.QUANTITATIVE, exporter.getColors());
		}
		return true;
	}

}
