/**
 * 
 */
package lu.itrust.business.ts.exportation.word.impl.docx4j.builder.chain;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.docx4j.wml.P;

import lu.itrust.business.ts.exportation.word.IDocxBuilder;
import lu.itrust.business.ts.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.ts.exportation.word.impl.docx4j.builder.Docx4jBuilder;
import lu.itrust.business.ts.exportation.word.impl.docx4j.builder.Docx4jData;
import lu.itrust.business.ts.model.actionplan.ActionPlanMode;
import lu.itrust.business.ts.model.actionplan.summary.SummaryStage;

/**
 * @author eomar
 *
 */
public class Docx4jPhaseBuilder extends Docx4jBuilder {

	private static final String TS_QT_PHASE = "ts_qt_phase";
	private static final String TS_QL_PHASE = "ts_ql_phase";

	/**
	 * @param next
	 * @param supports
	 */
	public Docx4jPhaseBuilder(IDocxBuilder next) {
		super(next, TS_QL_PHASE, TS_QT_PHASE);
	}

	@Override
	protected boolean internalBuild(Docx4jData data) {
		switch (data.getAnchor()) {
		case TS_QL_PHASE:
			return buildPhase(data, ActionPlanMode.APQ);
		case TS_QT_PHASE:
			return buildPhase(data, ActionPlanMode.APPN);
		default:
			return true;
		}
	}

	private boolean buildPhase(Docx4jData data, ActionPlanMode mode) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraphOriginal = (P) exporter.findP(data.getSource());
		if (paragraphOriginal != null) {
			final List<Object> contents = new LinkedList<>();
			final List<SummaryStage> summaryStages = exporter.getAnalysis().findSummary(mode);
			exporter.getAnalysis().getPhases().stream()
					.filter(phase -> phase.getNumber() > 0 && summaryStages.stream().anyMatch(stage -> stage.getStage().equals("Phase " + phase.getNumber()))).forEach(phase -> {
						final SummaryStage summaryStage = summaryStages.stream().filter(stage -> stage.getStage().equals("Phase " + phase.getNumber())).findAny().orElse(null);
						final Calendar begin = Calendar.getInstance();
						final Calendar end = Calendar.getInstance();
						begin.setTime(phase.getBeginDate());
						end.setTime(phase.getEndDate());
						final int monthBegin = begin.get(Calendar.MONTH) + 1;
						final int monthEnd = end.get(Calendar.MONTH) + 1;
						final P paragraph = exporter.setStyle(exporter.getFactory().createP(), "BulletL1");
						exporter.setText(paragraph,
								exporter.getMessage("report.risk.treatment.plan.summary", new Object[] { phase.getNumber(), (monthBegin < 10 ? "0" : "") + monthBegin,
										begin.get(Calendar.YEAR) + "", (monthEnd < 10 ? "0" : "") + monthEnd, end.get(Calendar.YEAR) + "", summaryStage.getMeasureCount() + "" },
										null));
						contents.add(paragraph);
					});
			exporter.insertAllBefore(paragraphOriginal, contents);
		}
		return true;
	}

}
