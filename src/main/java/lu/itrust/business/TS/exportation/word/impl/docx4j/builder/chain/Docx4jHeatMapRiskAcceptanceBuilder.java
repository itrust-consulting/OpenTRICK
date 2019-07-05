/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain;

import static lu.itrust.business.TS.exportation.word.ExportReport.TS_TAB_TEXT_2;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl.setColor;

import java.util.LinkedList;
import java.util.List;

import org.docx4j.wml.CTVerticalJc;
import org.docx4j.wml.P;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.docx4j.wml.PPrBase.TextAlignment;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.exportation.word.IDocxBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.TS.exportation.word.impl.docx4j.DocxChainFactory;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jData;
import lu.itrust.business.TS.helper.chartJS.model.Chart;
import lu.itrust.business.TS.helper.chartJS.model.Dataset;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.parameter.impl.RiskAcceptanceParameter;

/**
 * @author eomar
 *
 */
public class Docx4jHeatMapRiskAcceptanceBuilder extends Docx4jBuilder {

	private static final String TS_RISKHEATMAP = "ts_riskheatmap";
	private static final String TS_RISKACCEPTANCE = "ts_riskacceptance";
	private static final String TS_RISKHEATMAPSUMMARY = "ts_riskheatmapsummary";

	/**
	 * @param next
	 * @param supports
	 */
	public Docx4jHeatMapRiskAcceptanceBuilder(IDocxBuilder next) {
		super(next, TS_RISKHEATMAP, TS_RISKHEATMAPSUMMARY, TS_RISKACCEPTANCE);
	}

	@Override
	protected boolean internalBuild(Docx4jData data) {
		switch (data.getAnchor()) {
		case TS_RISKACCEPTANCE:
			return buildRiskAcceptance(data);
		case TS_RISKHEATMAP:
		case TS_RISKHEATMAPSUMMARY:
			return builHeatMap(data);
		default:
			return true;
		}
	}

	private boolean buildRiskAcceptance(Docx4jData data) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = exporter.findP(data.getSource());
		if (paragraph != null) {
			final Tbl table = exporter.createTable("TableTSRiskAcceptance", exporter.getAnalysis().getRiskAcceptanceParameters().size() + 1, 2);
			final Tr header = (Tr) table.getContent().get(0);
			final TextAlignment alignmentCenter = exporter.createAlignment("center");
			// set header
			exporter.setCurrentParagraphId(TS_TAB_TEXT_2);
			exporter.setCellText((Tc) header.getContent().get(0), exporter.getMessage("report.risk_acceptance.title.level", null, "Risk level"));
			exporter.setCellText((Tc) header.getContent().get(1), exporter.getMessage("report.risk_acceptance.title.acceptance.criteria", null, "Risk acceptance criteria"));
			int index = 1;
			for (RiskAcceptanceParameter parameter : exporter.getAnalysis().getRiskAcceptanceParameters()) {
				final Tr row = (Tr) table.getContent().get(index++);
				final Tc cell = (Tc) row.getContent().get(0);
				exporter.addCellParagraph(cell, parameter.getLabel());
				exporter.setAlignment(cell, alignmentCenter);
				exporter.addCellParagraph(cell, exporter.getMessage("report.risk_acceptance.importance_threshold.value", new Object[] { parameter.getValue().intValue() },
						"Importance threshold: " + parameter.getValue().intValue()), true);
				exporter.addCellParagraph((Tc) row.getContent().get(1), parameter.getDescription());
				if (!parameter.getColor().isEmpty())
					setColor(cell, parameter.getColor().substring(1));
			}
			if (exporter.insertBefore(paragraph, table))
				DocxChainFactory.format(table, exporter.getDefaultTableStyle(), AnalysisType.QUALITATIVE, exporter.getColors());
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean builHeatMap(Docx4jData docx4jData) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) docx4jData.getExportor();
		final P paragraph = exporter.findP(docx4jData.getSource());
		if (paragraph != null) {
			final int index[] = { 0 };
			final List<Object> contents = new LinkedList<>();
			final TextAlignment alignmentCenter = exporter.createAlignment("center");
			final CTVerticalJc verticalJc = exporter.createVerticalAlignment(STVerticalJc.CENTER);
			final Chart chart = ChartGenerator.generateRiskHeatMap(exporter.getAnalysis(), exporter.getValueFactory(), exporter.getMessageSource(), exporter.getLocale());
			final Tbl table = exporter.createTable("TableTSRiskHeatMap", chart.getLabels().size() + 2, chart.getLabels().size() + 2);
			final Tbl legends = exporter.createTable("TableTSHeatMapLegend", 1, chart.getLegends().size());
			final Tr legendRow = (Tr) legends.getContent().get(0);

			exporter.setCurrentParagraphId(TS_TAB_TEXT_2);

			chart.getLegends().forEach(legend -> {
				final Tc column = (Tc) legendRow.getContent().get(index[0]++);
				exporter.setCellText(column, legend.getLabel(), alignmentCenter);
				setColor(column, legend.getColor().substring(1));
			});

			int rowIndex = 1;
			Tr row = (Tr) table.getContent().get(0);
			exporter.setCellText((Tc) row.getContent().get(0), exporter.getMessage("report.risk_heat_map.title.impact", null, "Impact"));

			for (int i = 0; i < chart.getDatasets().size(); i++) {
				final Dataset<List<String>> dataset = (Dataset<List<String>>) chart.getDatasets().get(i);
				if (i > 0)
					row = (Tr) table.getContent().get(rowIndex++);
				Tc cell = (Tc) row.getContent().get(1);
				exporter.setVerticalAlignment(cell, verticalJc);
				exporter.setAlignment(exporter.addCellParagraph(cell, dataset.getLabel()), alignmentCenter);
				setColor(cell, exporter.getLightColor());
				for (int j = 0; j < dataset.getData().size(); j++) {
					cell = (Tc) row.getContent().get(j + 2);
					exporter.setVerticalAlignment(cell, verticalJc);
					Object data = dataset.getData().get(j);
					if (data instanceof Integer)
						exporter.setAlignment(exporter.addCellParagraph(cell, data.toString()), alignmentCenter);
					else
						exporter.setAlignment(exporter.addCellParagraph(cell, ""), alignmentCenter);
					setColor(cell, dataset.getBackgroundColor().get(j).substring(1));
				}
			}

			row = (Tr) table.getContent().get(rowIndex++);

			for (int i = 0; i < chart.getLabels().size(); i++) {
				final Tc cell = (Tc) row.getContent().get(i + 2);
				exporter.setVerticalAlignment(cell, verticalJc);
				exporter.setAlignment(exporter.addCellParagraph(cell, chart.getLabels().get(i)), alignmentCenter);
				setColor(cell, exporter.getLightColor());
			}

			row = (Tr) table.getContent().get(rowIndex);

			exporter.setCellText((Tc) row.getContent().get(0), exporter.getMessage("report.risk_heat_map.title.probability", null, "Probability"), alignmentCenter);
			contents.add(legends);
			contents.add(exporter.setStyle(exporter.getFactory().createP(), "Endlist"));
			contents.add(table);
			if (exporter.insertBefore(paragraph, table))
				DocxChainFactory.format(table, exporter.getDefaultTableStyle(), AnalysisType.QUALITATIVE, exporter.getColors());
		}
		return true;
	}

}
