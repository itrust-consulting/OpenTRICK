/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain;

import static lu.itrust.business.TS.exportation.word.ExportReport.NUMBER_FORMAT;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getRow;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.docx4j.dml.CTRegularTextRun;
import org.docx4j.dml.chart.CTBarChart;
import org.docx4j.dml.chart.CTBarSer;
import org.docx4j.dml.chart.CTBoolean;
import org.docx4j.dml.chart.CTNumFmt;
import org.docx4j.dml.chart.CTNumVal;
import org.docx4j.dml.chart.CTStrVal;
import org.docx4j.dml.chart.CTValAx;
import org.docx4j.dml.chart.STDispBlanksAs;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.DrawingML.Chart;
import org.docx4j.openpackaging.parts.WordprocessingML.EmbeddedPackagePart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.P;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.word.IDocxBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jExcelSheet;
import lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jData;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.CTBarSerProxy;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.ALE;
import lu.itrust.business.TS.model.assessment.helper.AssetComparatorByALE;

/**
 * @author eomar
 *
 */
public class Docx4jALEChartBuilder extends Docx4jBuilder {

	private static final String TS_CHARTALEBYSCENARIOTYPE = "ts_chartalebyscenariotype";
	private static final String TS_CHARTALEBYSCENARIO = "ts_chartalebyscenario";
	private static final String TS_CHARTALEBYASSETTYPE = "ts_chartalebyassettype";
	private static final String TS_CHARTALEBYASSET = "ts_chartalebyasset";

	public Docx4jALEChartBuilder(IDocxBuilder next) {
		super(next, TS_CHARTALEBYASSET, TS_CHARTALEBYASSETTYPE, TS_CHARTALEBYSCENARIO, TS_CHARTALEBYSCENARIOTYPE);
	}

	@Override
	protected boolean internalBuild(Docx4jData data) {
		try {
			switch (data.getAnchor()) {
			case TS_CHARTALEBYASSET:
				return buildAleByAssetChart(data);
			case TS_CHARTALEBYASSETTYPE:
				return buildAleByAssetTypeChart(data);
			case TS_CHARTALEBYSCENARIO:
				return buildAleByScenarioChart(data);
			case TS_CHARTALEBYSCENARIOTYPE:
				return buildAleByScenarioTypeChart(data);
			default:
				return true;
			}
		} catch (TrickException e) {
			throw e;
		} catch (Exception e) {
			throw new TrickException("error.internal.report", null, e);
		}

	}

	private boolean buildAleByAssetChart(Docx4jData data) throws Exception {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = exporter.findP(data.getSource());
		if (paragraph != null) {
			final List<Assessment> assessments = data.getExportor().getAnalysis().findSelectedAssessments();
			final Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
			for (Assessment assessment : assessments) {
				ALE ale = ales.get(assessment.getAsset().getId());
				if (ale == null)
					ales.put(assessment.getAsset().getId(), ale = new ALE(assessment.getAsset().getName(), 0));
				ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
			}
			buildALEChart(exporter, paragraph, ales, data.getExportor().getMessage("report.chart.ale.title.asset", null, "ALE By Asset"),
					data.getExportor().getMessage("report.chart.asset", null, "Asset"), "AleByAsset", "report.chart.ale.title.asset.index");
		}
		return true;
	}

	private boolean buildAleByAssetTypeChart(Docx4jData data) throws Exception {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = exporter.findP(data.getSource());
		if (paragraph != null) {
			final List<Assessment> assessments = data.getExportor().getAnalysis().findSelectedAssessments();
			final Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
			for (Assessment assessment : assessments) {
				ALE ale = ales.get(assessment.getAsset().getAssetType().getId());
				if (ale == null)
					ales.put(assessment.getAsset().getAssetType().getId(), ale = new ALE(assessment.getAsset().getAssetType().getName(), 0));
				ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
			}

			buildALEChart(exporter, paragraph, ales, data.getExportor().getMessage("report.chart.ale.title.asset.type", null, "Asset type"),
					data.getExportor().getMessage("report.chart.asset.type", null, "Asset type"), "AleByAssetType", "report.chart.ale.title.asset.type.index");
		}
		return true;
	}

	private boolean buildAleByScenarioChart(Docx4jData data) throws Exception {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = exporter.findP(data.getSource());
		if (paragraph != null) {
			final List<Assessment> assessments = data.getExportor().getAnalysis().findSelectedAssessments();
			final Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
			for (Assessment assessment : assessments) {
				ALE ale = ales.get(assessment.getScenario().getId());
				if (ale == null)
					ales.put(assessment.getScenario().getId(), ale = new ALE(assessment.getScenario().getName(), 0));
				ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
			}
			buildALEChart(exporter, paragraph, ales, data.getExportor().getMessage("report.chart.ale.title.scenario", null, "Scenario"),
					data.getExportor().getMessage("report.chart.scenario", null, "Scenario"), "AleBySceanrio", "report.chart.ale.title.scenario.index");
		}
		return true;
	}

	private boolean buildAleByScenarioTypeChart(Docx4jData data) throws Exception {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = exporter.findP(data.getSource());
		if (paragraph != null) {
			final List<Assessment> assessments = data.getExportor().getAnalysis().findSelectedAssessments();
			final Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
			final List<ALE> ales2 = new LinkedList<ALE>();
			for (Assessment assessment : assessments) {
				ALE ale = ales.get(assessment.getScenario().getType().getValue());
				if (ale == null) {
					ales.put(assessment.getScenario().getType().getValue(), ale = new ALE(assessment.getScenario().getType().getName(), 0));
					ales2.add(ale);
				}
				ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
			}
			buildALEChart(exporter, paragraph, ales, data.getExportor().getMessage("report.chart.ale.title.scenario.type", null, "Scenario type"),
					data.getExportor().getMessage("report.chart.scenario.type", null, "Scenario type"), "AleBySceanrioType", "report.chart.ale.title.scenario.type.index");
		}
		return true;
	}

	private void buildALEChart(final Docx4jReportImpl exporter, final List<ALE> ales, final Chart chart, final String title, final String name, Double max) throws Exception {
		if (chart == null)
			return;
		final String path = chart.getRelationshipsPart().getRelationships().getRelationship().parallelStream().filter(r -> r.getTarget().endsWith(".xlsx"))
				.map(Relationship::getTarget).findAny().orElse(null);
		if (path == null)
			return;
		Part excel = exporter.getWordMLPackage().getParts().get(new PartName("/word" + path.replace("..", "")));
		if (excel == null)
			return;
		Docx4jExcelSheet reportExcelSheet = new Docx4jExcelSheet((EmbeddedPackagePart) excel);
		final CTBarChart barChart = (CTBarChart) chart.getContents().getChart().getPlotArea().getAreaChartOrArea3DChartOrLineChart().parallelStream()
				.filter(c -> c instanceof CTBarChart).findAny().orElse(null);
		if (barChart == null)
			return;

		final SheetData sheet = reportExcelSheet.getWorkbook().getWorksheet(0).getContents().getSheetData();

		final CTBarSer ser = exporter.createChart(String.format("%s!$B$1", reportExcelSheet.getName()), 0, name, new CTBarSerProxy()).getProxy();

		final CTRegularTextRun r = (CTRegularTextRun) chart.getContents().getChart().getTitle().getTx().getRich().getP().get(0).getEGTextRun().get(0);

		r.setT(title);

		chart.getContents().getChart().getPlotArea().getValAxOrCatAxOrDateAx().parallelStream().filter(valAx -> valAx instanceof CTValAx).map(valAx -> (CTValAx) valAx)
				.forEach(valAx -> {
					valAx.getNumFmt().setSourceLinked(false);
					valAx.getNumFmt().setFormatCode(NUMBER_FORMAT);
					if (max != null) {
						if (valAx.getScaling() == null)
							valAx.setScaling(exporter.getChartFactory().createCTScaling());
						if (valAx.getScaling().getMax() == null)
							valAx.getScaling().setMax(exporter.getChartFactory().createCTDouble());
						if (valAx.getScaling().getMin() == null)
							valAx.getScaling().setMin(exporter.getChartFactory().createCTDouble());
						valAx.getScaling().getMax().setVal(max);
						valAx.getScaling().getMin().setVal(0d);
					}
				});

		chart.getContents().getChart().getDispBlanksAs().setVal(STDispBlanksAs.GAP);

		barChart.getSer().clear();

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
			final CTStrVal catName = new CTStrVal();
			final CTNumVal numVal = new CTNumVal();
			catName.setV(ale.getAssetName());
			catName.setIdx(rowCount - 1);
			ser.getCat().getStrRef().getStrCache().getPt().add(catName);
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
		reportExcelSheet.save();

	}

	private void buildALEChart(final Docx4jReportImpl exporter, P paragraph, Map<Integer, ALE> ales, String title, String column, String name, String multiTitleCode)
			throws Exception {
		final List<ALE> ales2 = ales.values().parallelStream().filter(ale -> ale.getValue() > 0).sorted(new AssetComparatorByALE()).collect(Collectors.toList());
		if (ales2.size() <= Constant.CHAR_SINGLE_CONTENT_MAX_SIZE)
			buildALEChart(exporter, ales2, (Chart) exporter.findChart(paragraph), title, column, null);
		else {
			final List<Part> parts = exporter.duplicateChart(ales2.size(), paragraph, name);
			final double maxValue = ales2.parallelStream().mapToDouble(e -> (Double) e.getValue()).max().orElse(-1);
			int count = parts.size();
			double divisor = (double) ales2.size() / (double) count;
			for (int i = 0; i < count; i++)
				buildALEChart(exporter, ales2.subList((int) Math.round(i * divisor), i == (count - 1) ? ales2.size() : (int) Math.round((i + 1) * divisor)), (Chart) parts.get(i),
						exporter.getMessage(multiTitleCode, new Object[] { i + 1, count }, null), column, i == 0 || maxValue < 0 ? null : maxValue);
		}
	}

}
