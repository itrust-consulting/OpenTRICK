/**
 * 
 */
package lu.itrust.business.ts.exportation.word.impl.docx4j.builder.chain;

import static lu.itrust.business.ts.exportation.word.ExportReport.NUMBER_FORMAT;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getRow;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.ts.component.ChartGenerator;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.exportation.word.IDocxBuilder;
import lu.itrust.business.ts.exportation.word.impl.docx4j.Docx4jExcelSheet;
import lu.itrust.business.ts.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.ts.exportation.word.impl.docx4j.builder.Docx4jBuilder;
import lu.itrust.business.ts.exportation.word.impl.docx4j.builder.Docx4jData;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.CTBarSerProxy;
import lu.itrust.business.ts.model.actionplan.ActionPlanMode;
import lu.itrust.business.ts.model.actionplan.summary.SummaryStage;
import lu.itrust.business.ts.model.actionplan.summary.helper.ActionPlanSummaryManager;
import lu.itrust.business.ts.model.general.Phase;

/**
 * @author eomar
 *
 */
public class Docx4jRentabilityChartBuilder extends Docx4jBuilder {

	private static final String TS_CHARTRENTABILITY = "ts_chartrentability";

	/**
	 * @param next
	 */
	public Docx4jRentabilityChartBuilder(IDocxBuilder next) {
		super(next, TS_CHARTRENTABILITY);
	}

	@Override
	protected boolean internalBuild(Docx4jData data) {
		try {
			final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
			final Chart chart = (Chart) exporter.findChart(data.getSource());
			if (chart != null) {
				final List<Phase> phases = exporter.getAnalysis().getPhases();
				final List<SummaryStage> summaryStages = exporter.getAnalysis().findSummary(ActionPlanMode.APPN);
				final Map<String, List<Object>> summaries = ActionPlanSummaryManager.buildChartData(summaryStages, phases);
				final String path = chart.getRelationshipsPart().getRelationships().getRelationship().parallelStream().filter(r -> r.getTarget().endsWith(".xlsx"))
						.map(Relationship::getTarget).findAny().orElse(null);
				if (path != null) {
					final Part excel = exporter.getWordMLPackage().getParts().get(new PartName("/word" + path.replace("..", "")));
					if (excel != null) {

						final CTBarChart barChart = (CTBarChart) chart.getContents().getChart().getPlotArea().getAreaChartOrArea3DChartOrLineChart().parallelStream()
								.filter(c -> c instanceof CTBarChart).findAny().orElse(null);
						if (barChart != null) {

							final Docx4jExcelSheet docx4jExcelSheet = new Docx4jExcelSheet((EmbeddedPackagePart) excel);

							final String[] dataName = { "ALE", "COST", "ROSI", "LOST" };

							final int[] colorIndex = { 1, 3, 5, 7 };

							final Map<String, CTBarSer> profiltabilityDatasets = new LinkedHashMap<>(dataName.length);

							final SheetData sheet = docx4jExcelSheet.getWorkbook().getWorksheet(0).getContents().getSheetData();

							final Map<String, Phase> usesPhases = ActionPlanSummaryManager.buildPhase(phases, ActionPlanSummaryManager.extractPhaseRow(summaryStages));

							chart.getContents().getChart().getPlotArea().getValAxOrCatAxOrDateAx().parallelStream().filter(valAx -> valAx instanceof CTValAx)
									.map(valAx -> (CTValAx) valAx).forEach(valAx -> {
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

							for (int i = 0; i < dataName.length; i++) {
								CTBarSer ser = exporter.createChart(String.format("%s!$A$%s", docx4jExcelSheet.getName(), i + 2), i,
										exporter.getMessage("label.title.chart.evolution_profitability." + dataName[i].toLowerCase()), new CTBarSerProxy()).getProxy();
								profiltabilityDatasets.put(dataName[i], ser);
								ser.getVal().getNumRef().getNumCache().setFormatCode(NUMBER_FORMAT);
								barChart.getSer().add(ser);
								exporter.setColor(ser, ChartGenerator.getStaticColor(colorIndex[i]));
								setValue(getRow(sheet, i + 1, usesPhases.size() + 1), 0, dataName[i]);
							}

							final CTBarSer barSer = profiltabilityDatasets.get(dataName[0]);

							final Row rowPhase = sheet.getRow().get(0);

							for (Phase phase : usesPhases.values()) {
								final CTStrVal catName = new CTStrVal();
								catName.setV("P" + phase.getNumber());
								catName.setIdx(barSer.getCat().getStrRef().getStrCache().getPt().size());
								barSer.getCat().getStrRef().getStrCache().getPt().add(catName);
								setValue(rowPhase, barSer.getCat().getStrRef().getStrCache().getPt().size(), catName.getV());
							}

							for (int i = 1; i < dataName.length; i++)
								profiltabilityDatasets.get(dataName[i]).setCat(barSer.getCat());
							
							if (!(summaries == null || summaries.isEmpty())) {
								for (int i = 0; i < usesPhases.size(); i++) {
									for (int j = 0; j < dataName.length; j++) {
										final CTNumVal numVal = new CTNumVal();
										final CTBarSer ser = profiltabilityDatasets.get(dataName[j]);
										final Double rosi = (Double) summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI).get(i);
										double value = 0d;
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
											exporter.setColor(ser, ChartGenerator.getStaticColor(1));
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

										numVal.setIdx(i);
										if (value > 0) {
											numVal.setV(value + "");
											setValue(sheet.getRow().get(j + 1), i + 1, value);
										}
										ser.getVal().getNumRef().getNumCache().getPt().add(numVal);
									}
								}
							}

							barSer.getCat().getStrRef()
									.setF(String.format("%s!$B$1:$%s$1", docx4jExcelSheet.getName(), (char) ('B' + barSer.getCat().getStrRef().getStrCache().getPt().size())));

							docx4jExcelSheet.save();
						}
					}
				}
			}
			return true;
		} catch (Exception e) {
			throw new TrickException("error.internal.report", null, e);
		}

	}

}
