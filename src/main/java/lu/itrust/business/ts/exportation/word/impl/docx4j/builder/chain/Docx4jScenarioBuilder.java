/**
 * 
 */
package lu.itrust.business.ts.exportation.word.impl.docx4j.builder.chain;

import java.util.List;

import org.docx4j.wml.P;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.docx4j.wml.PPrBase.TextAlignment;

import lu.itrust.business.ts.exportation.word.ExportReport;
import lu.itrust.business.ts.exportation.word.IDocxBuilder;
import lu.itrust.business.ts.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.ts.exportation.word.impl.docx4j.DocxChainFactory;
import lu.itrust.business.ts.exportation.word.impl.docx4j.builder.Docx4jBuilder;
import lu.itrust.business.ts.exportation.word.impl.docx4j.builder.Docx4jData;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.scenario.Scenario;

/**
 * @author eomar
 *
 */
public class Docx4jScenarioBuilder extends Docx4jBuilder {

	public Docx4jScenarioBuilder(IDocxBuilder next) {
		super(next, "ts_scenario");
	}

	@Override
	protected boolean internalBuild(Docx4jData data) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = (P) exporter.findP(data.getSource());
		if (paragraph != null) {
			final TextAlignment alignmentLeft = exporter.createAlignment("left");
			final TextAlignment alignmentCenter = exporter.createAlignment("center");
			final List<Scenario> scenarios = exporter.getAnalysis().findSelectedScenarios();
			final Tbl table = exporter.createTable("TableTSScenario", scenarios.size() + 1, 3);
			Tr row = (Tr) table.getContent().get(0);
			exporter.setCurrentParagraphId(ExportReport.TS_TAB_TEXT_2);
			exporter.setCellText((Tc) row.getContent().get(0), exporter.getMessage("report.scenario.title.number.row", null, "Nr"));
			exporter.setCellText((Tc) row.getContent().get(1), exporter.getMessage("report.scenario.title.name", null, "Name"));
			exporter.setCellText((Tc) row.getContent().get(2), exporter.getMessage("report.scenario.title.description", null, "Description"));
			exporter.setRepeatHeader(row);
			int number = 1;
			for (Scenario scenario : scenarios) {
				row = (Tr) table.getContent().get(number);
				exporter.setCellText((Tc) row.getContent().get(0), "" + (number++), alignmentCenter);
				exporter.setCellText((Tc) row.getContent().get(1), scenario.getName(), alignmentLeft);
				exporter.addCellParagraph((Tc) row.getContent().get(2), scenario.getDescription());
			}
			if (exporter.insertBefore(paragraph, table))
				DocxChainFactory.format(table, exporter.getDefaultTableStyle(), AnalysisType.HYBRID, exporter.getColors());
		}
		return true;
	}

}
