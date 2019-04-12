/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain;

import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getRow;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.docx4j.dml.CTRegularTextRun;
import org.docx4j.dml.CTTextParagraph;
import org.docx4j.dml.chart.CTBarChart;
import org.docx4j.dml.chart.CTBarSer;
import org.docx4j.dml.chart.CTNumVal;
import org.docx4j.dml.chart.CTStrVal;
import org.docx4j.dml.chart.STDispBlanksAs;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.EmbeddedPackagePart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.P;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.word.IDocxBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jExcelSheet;
import lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jData;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.helper.chartJS.item.ColorBound;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.parameter.impl.RiskAcceptanceParameter;

/**
 * @author eomar
 *
 */
public class Docx4jRiskChartBuilder extends Docx4jBuilder {

	private static final String TS_CHARTRISKBYSCENARIOTYPE = "ts_chartriskbyscenariotype";
	private static final String TS_CHARTRISKBYSCENARIO = "ts_chartriskbyscenario";
	private static final String TS_CHARTRISKBYASSETTYPE = "ts_chartriskbyassettype";
	private static final String TS_CHARTRISKBYASSET = "ts_chartriskbyasset";

	public Docx4jRiskChartBuilder(IDocxBuilder next) {
		super(next, TS_CHARTRISKBYASSET, TS_CHARTRISKBYASSETTYPE, TS_CHARTRISKBYSCENARIO, TS_CHARTRISKBYSCENARIOTYPE);
	}

	@Override
	protected boolean internalBuild(Docx4jData data) {
		try {
			switch (data.getAnchor()) {
			case TS_CHARTRISKBYASSET:
				return buildRiskByAsset(data);
			case TS_CHARTRISKBYASSETTYPE:
				return buildRiskByAssetType(data);
			case TS_CHARTRISKBYSCENARIO:
				return buildRiskByScenario(data);
			case TS_CHARTRISKBYSCENARIOTYPE:
				return buildRiskByScenarioType(data);
			default:
				return true;
			}
		} catch (TrickException e) {
			throw e;
		} catch (Exception e) {
			throw new TrickException("error.internal.report", null, e);
		}

	}

	private List<ColorBound> buildColorBounds(Docx4jReportImpl exporter) {
		if (exporter.getAnalysis().getRiskAcceptanceParameters().isEmpty())
			throw new TrickException("error.export.risk.acceptance.empty", "Please update risk acception settings: Analysis -> Parameter -> Risk acceptance");
		final List<RiskAcceptanceParameter> riskAcceptanceParameters = exporter.getAnalysis().getRiskAcceptanceParameters();
		final List<ColorBound> colorBounds = new ArrayList<>(riskAcceptanceParameters.size());
		for (int i = 0; i < riskAcceptanceParameters.size(); i++) {
			RiskAcceptanceParameter parameter = riskAcceptanceParameters.get(i);
			if (colorBounds.isEmpty())
				colorBounds.add(new ColorBound(parameter.getColor(), parameter.getLabel(), 0, parameter.getValue().intValue()));
			else if (riskAcceptanceParameters.size() == (i + 1))
				colorBounds.add(new ColorBound(parameter.getColor(), parameter.getLabel(), riskAcceptanceParameters.get(i - 1).getValue().intValue(), Integer.MAX_VALUE));
			else
				colorBounds.add(
						new ColorBound(parameter.getColor(), parameter.getLabel(), riskAcceptanceParameters.get(i - 1).getValue().intValue(), parameter.getValue().intValue()));
		}
		return colorBounds;

	}

	private boolean buildRiskByAsset(Docx4jData data) throws Exception {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = (P) exporter.findP(data.getSource());
		if (paragraph != null) {
			final Map<String, List<Assessment>> assessmentByAsset = exporter.getAnalysis().getAssessments().parallelStream().filter(Assessment::isSelected).sorted((a1, a2) -> {
				return NaturalOrderComparator.compareTo(a1.getAsset().getName(), a2.getAsset().getName());
			}).collect(Collectors.groupingBy(assessment -> assessment.getAsset().getName(), LinkedHashMap::new, Collectors.toList()));
			buildRiskChart(exporter, paragraph, assessmentByAsset, "report.chart.risk.title.asset", "Risk par asset", "report.chart.risk.title.asset.index");
		}
		return true;
	}

	private boolean buildRiskByAssetType(Docx4jData data) throws Exception {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = (P) exporter.findP(data.getSource());
		if (paragraph != null) {
			final Map<String, List<Assessment>> assessments = exporter.getAnalysis().getAssessments().parallelStream().filter(Assessment::isSelected).sorted((a1, a2) -> {
				return NaturalOrderComparator.compareTo(exporter.getDisplayName(a1.getAsset().getAssetType()), exporter.getDisplayName(a2.getAsset().getAssetType()));
			}).collect(Collectors.groupingBy(assessment -> exporter.getDisplayName(assessment.getAsset().getAssetType()), LinkedHashMap::new, Collectors.toList()));
			buildRiskChart(exporter, paragraph, assessments, "report.chart.risk.title.asset.type", "Risk par asset type", "report.chart.risk.title.asset.type.index");
		}
		return true;
	}

	private boolean buildRiskByScenario(Docx4jData data) throws Exception {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = (P) exporter.findP(data.getSource());
		if (paragraph != null) {
			final Map<String, List<Assessment>> assessments = exporter.getAnalysis().getAssessments().parallelStream().filter(Assessment::isSelected).sorted((a1, a2) -> {
				return NaturalOrderComparator.compareTo(a1.getScenario().getName(), a2.getScenario().getName());
			}).collect(Collectors.groupingBy(assessment -> assessment.getScenario().getName(), LinkedHashMap::new, Collectors.toList()));
			buildRiskChart(exporter, paragraph, assessments, "report.chart.risk.title.scenario", "Risk par scenario", "report.chart.risk.title.scenario.index");
		}
		return true;
	}

	private boolean buildRiskByScenarioType(Docx4jData data) throws Exception {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = (P) exporter.findP(data.getSource());
		if (paragraph != null) {
			Map<String, List<Assessment>> assessments = exporter.getAnalysis().getAssessments().parallelStream().filter(Assessment::isSelected).sorted((a1, a2) -> {
				return NaturalOrderComparator.compareTo(exporter.getDisplayName(a1.getScenario().getType()), exporter.getDisplayName(a2.getScenario().getType()));
			}).collect(Collectors.groupingBy(assessment -> exporter.getDisplayName(assessment.getScenario().getType()), LinkedHashMap::new, Collectors.toList()));
			buildRiskChart(exporter, paragraph, assessments, "report.chart.risk.title.scenario.type", "Risk par scenario type", "report.chart.risk.title.scenario.type.index");
		}
		return true;
	}

	private void buildRiskChart(final Docx4jReportImpl exporter, P anchor, final Map<String, List<Assessment>> assessmentMapper, String title, String name, String multiTitleCode)
			throws Exception {
		final List<ColorBound> colorBounds = buildColorBounds(exporter);
		final List<Entry<String, List<Assessment>>> assessments = assessmentMapper.entrySet().stream().filter(entry -> {
			entry.getValue().forEach(assessment -> {
				final int importance = exporter.getValueFactory().findImportance(assessment);
				colorBounds.parallelStream().filter(colorBound -> colorBound.isAccepted(importance)).findAny()
						.ifPresent(colorBound -> colorBound.setCount(colorBound.getCount() + 1));
			});
			return colorBounds.parallelStream().anyMatch(colorBound -> colorBound.getCount() > 0);
		}).collect(Collectors.toList());
		if (assessments.size() <= Constant.CHAR_SINGLE_CONTENT_MAX_SIZE)
			buildRiskGraphic(exporter, colorBounds, exporter.findChart(anchor), exporter.getMessage(title, null, null), assessments);
		else {
			final List<Part> parts = exporter.duplicateChart(assessments.size(), anchor, name);
			final int count = parts.size();
			final double divisor = (double) assessments.size() / (double) count;
			for (int i = 0; i < count; i++)
				buildRiskGraphic(exporter, colorBounds, parts.get(i), exporter.getMessage(multiTitleCode, new Object[] { i + 1, count }, null),
						assessments.subList((int) Math.round(i * divisor), i == (count - 1) ? assessments.size() : (int) Math.round((i + 1) * divisor)));
		}
	}

	private void buildRiskGraphic(final Docx4jReportImpl exporter, List<ColorBound> colorBounds, Part part, String title, List<Entry<String, List<Assessment>>> assessmentEntries)
			throws Exception {
		if (part == null || colorBounds.isEmpty() || assessmentEntries.isEmpty())
			return;
		final String path = part.getRelationshipsPart().getRelationships().getRelationship().parallelStream().filter(r -> r.getTarget().endsWith(".xlsx"))
				.map(Relationship::getTarget).findAny().orElse(null);
		if (path == null)
			return;
		final Part excel = exporter.getWordMLPackage().getParts().get(new PartName("/word" + path.replace("..", "")));
		if (excel == null)
			return;

		final org.docx4j.openpackaging.parts.DrawingML.Chart chart = (org.docx4j.openpackaging.parts.DrawingML.Chart) part;

		final Docx4jExcelSheet reportExcelSheet = new Docx4jExcelSheet((EmbeddedPackagePart) excel, String.format("%s/WEB-INF/tmp/", exporter.getPath()));

		final CTBarChart barChart = (CTBarChart) chart.getContents().getChart().getPlotArea().getAreaChartOrArea3DChartOrLineChart().parallelStream()
				.filter(c -> c instanceof CTBarChart).findAny().orElse(null);

		final SheetData sheet = reportExcelSheet.getWorkbook().getWorksheet(0).getContents().getSheetData();

		final CTTextParagraph p = chart.getContents().getChart().getTitle().getTx().getRich().getP().get(0);

		if (barChart == null)
			return;

		barChart.getSer().clear();

		while (p.getEGTextRun().size() > 1)
			p.getEGTextRun().remove(1);

		Row row = getRow(sheet, 0, colorBounds.size() + 1);

		((CTRegularTextRun) p.getEGTextRun().get(0)).setT(title);

		chart.getContents().getChart().getDispBlanksAs().setVal(STDispBlanksAs.GAP);

		for (int i = 0; i < colorBounds.size(); i++) {
			final CTBarSer ser = exporter.createChart(String.format("%s!$%s$1", reportExcelSheet.getName(), (char) ('B' + i)), i, colorBounds.get(i).getLabel(), new CTBarSer());
			exporter.setColor(ser, colorBounds.get(i).getColor());
			barChart.getSer().add(ser);
			setValue(row, i + 1, colorBounds.get(i).getLabel());
		}

		final CTBarSer barSer = barChart.getSer().get(0);
		for (Entry<String, List<Assessment>> entry : assessmentEntries) {
			final CTStrVal catName = new CTStrVal();
			catName.setV(entry.getKey());
			catName.setIdx(barSer.getCat().getStrRef().getStrCache().getPt().size());
			barSer.getCat().getStrRef().getStrCache().getPt().add(catName);
			setValue(getRow(sheet, barSer.getCat().getStrRef().getStrCache().getPt().size(), colorBounds.size() + 1), 0, catName.getV());
		}

		barSer.getCat().getStrRef().setF(String.format("%s!$A$2:$A$%d", reportExcelSheet.getName(), assessmentEntries.size() + 1));

		for (int i = 1; i < colorBounds.size(); i++)
			barChart.getSer().get(i).setCat(barSer.getCat());

		int rowIndex = 1;

		for (Entry<String, List<Assessment>> entry : assessmentEntries) {
			colorBounds.parallelStream().forEach(color -> color.setCount(0));
			entry.getValue().forEach(assessment -> {
				final int importance = exporter.getValueFactory().findImportance(assessment);
				colorBounds.parallelStream().filter(colorBound -> colorBound.isAccepted(importance)).findAny()
						.ifPresent(colorBound -> colorBound.setCount(colorBound.getCount() + 1));
			});
			setValue(row = sheet.getRow().get(rowIndex++), 0, entry.getKey());

			for (int i = 0; i < colorBounds.size(); i++) {
				final CTBarSer ser = barChart.getSer().get(i);
				final CTNumVal numVal = new CTNumVal();

				numVal.setIdx(rowIndex - 2);
				if (colorBounds.get(i).getCount() > 0) {
					setValue(row, i + 1, colorBounds.get(i).getCount());
					numVal.setV(colorBounds.get(i).getCount() + "");
				}
				ser.getVal().getNumRef().getNumCache().getPt().add(numVal);
			}
		}

		for (int i = 0; i < colorBounds.size(); i++) {
			final char col = (char) ('B' + i);
			final CTBarSer ser = barChart.getSer().get(i);
			ser.getVal().getNumRef().setF(String.format("%s!$%s$2:$%s$%d", reportExcelSheet.getName(), col, col, ser.getCat().getStrRef().getStrCache().getPt().size() + 1));
		}
		reportExcelSheet.save();

	}

}
