/**
 * 
 */
package lu.itrust.business.ts.exportation.word.impl.docx4j.builder.chain;

import static lu.itrust.business.ts.constants.Constant.MEASURE_STATUS_NOT_APPLICABLE;
import static lu.itrust.business.ts.constants.Constant.STANDARD_27001;
import static lu.itrust.business.ts.constants.Constant.STANDARD_27002;
import static lu.itrust.business.ts.exportation.word.ExportReport.TS_TAB_TEXT_3;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.Docx4jReportImpl.mergeCell;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.Docx4jReportImpl.setColor;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.docx4j.dml.CTRegularTextRun;
import org.docx4j.dml.chart.CTNumVal;
import org.docx4j.dml.chart.CTRadarChart;
import org.docx4j.dml.chart.CTRadarSer;
import org.docx4j.dml.chart.CTStrVal;
import org.docx4j.dml.chart.CTValAx;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.DrawingML.Chart;
import org.docx4j.openpackaging.parts.WordprocessingML.EmbeddedPackagePart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.P;
import org.docx4j.wml.PPrBase.TextAlignment;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import com.atlassian.util.concurrent.atomic.AtomicInteger;

import jakarta.xml.bind.JAXBException;
import lu.itrust.business.ts.component.ChartGenerator;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.exportation.word.IDocxBuilder;
import lu.itrust.business.ts.exportation.word.impl.docx4j.ClonePartResult;
import lu.itrust.business.ts.exportation.word.impl.docx4j.Docx4jExcelSheet;
import lu.itrust.business.ts.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.ts.exportation.word.impl.docx4j.DocxChainFactory;
import lu.itrust.business.ts.exportation.word.impl.docx4j.builder.Docx4jBuilder;
import lu.itrust.business.ts.exportation.word.impl.docx4j.builder.Docx4jData;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.CTRadarSerProxy;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.actionplan.ActionPlanMode;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.general.Phase;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.helper.MeasureComparator;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;

/**
 * @author eomar
 *
 */
public class Docx4jStandardBuilder extends Docx4jBuilder {

	private static final String TS_CURRENTSECURITYLEVEL = "ts_currentsecuritylevel";
	private static final String TS_MEASURESCOLLECTION = "ts_measurescollection";
	private static final String TS_LISTCOLLECTION = "ts_listcollection";
	private static final String TS_QL_CHARTCOMPLIANCE27002 = "ts_ql_chartcompliance27002";
	private static final String TS_QL_CHARTCOMPLIANCE27001 = "ts_ql_chartcompliance27001";
	private static final String TS_QT_CHARTCOMPLIANCE27002 = "ts_qt_chartcompliance27002";
	private static final String TS_QT_CHARTCOMPLIANCE27001 = "ts_qt_chartcompliance27001";
	private static final String TS_HY_ADDITIONALCOLLECTION = "ts_hy_additionalcollection";
	private static final String TS_QL_ADDITIONALCOLLECTION = "ts_ql_additionalcollection";
	private static final String TS_QT_ADDITIONALCOLLECTION = "ts_qt_additionalcollection";
	private static final String[] TS_2700X_CHART_IDS = { "chartcompliance27001", "chartcompliance27002",
			TS_QL_CHARTCOMPLIANCE27001, TS_QT_CHARTCOMPLIANCE27001,
			TS_QL_CHARTCOMPLIANCE27002, TS_QT_CHARTCOMPLIANCE27002 };

	public Docx4jStandardBuilder(IDocxBuilder next) {
		super(next, TS_QT_ADDITIONALCOLLECTION, TS_QL_ADDITIONALCOLLECTION, TS_HY_ADDITIONALCOLLECTION,
				TS_QT_CHARTCOMPLIANCE27001, TS_QT_CHARTCOMPLIANCE27002,
				TS_QL_CHARTCOMPLIANCE27001, TS_QL_CHARTCOMPLIANCE27002, TS_LISTCOLLECTION, TS_MEASURESCOLLECTION,
				TS_CURRENTSECURITYLEVEL);
	}

	@Override
	protected boolean internalBuild(Docx4jData data) {
		try {
			switch (data.getAnchor()) {
				case TS_QT_ADDITIONALCOLLECTION:
					return buildAdditionalCollection(data, ActionPlanMode.APPN);
				case TS_QL_ADDITIONALCOLLECTION:
					return buildAdditionalCollection(data, ActionPlanMode.APQ);
				case TS_HY_ADDITIONALCOLLECTION:
					return buildAdditionalCollection(data, ActionPlanMode.APPN, ActionPlanMode.APQ);
				case TS_QT_CHARTCOMPLIANCE27001:
					return buildCompliance(data, STANDARD_27001, ActionPlanMode.APPN);
				case TS_QL_CHARTCOMPLIANCE27001:
					return buildCompliance(data, STANDARD_27001, ActionPlanMode.APQ);
				case TS_QT_CHARTCOMPLIANCE27002:
					return buildCompliance(data, STANDARD_27002, ActionPlanMode.APPN);
				case TS_QL_CHARTCOMPLIANCE27002:
					return buildCompliance(data, STANDARD_27002, ActionPlanMode.APQ);
				case TS_LISTCOLLECTION:
					return buildCollectionList(data);
				case TS_MEASURESCOLLECTION:
					return buildMeasures(data);
				case TS_CURRENTSECURITYLEVEL:
					return buildCurrentSecurityLevel(data);
				default:
					return false;
			}
		} catch (TrickException e) {
			throw e;
		} catch (Exception e) {
			throw new TrickException("error.internal.report", null, e);
		}
	}

	private boolean buildAdditionalCollection(Docx4jData data, ActionPlanMode... planModes) throws Exception {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph =  exporter.findP(data.getSource());
		if (paragraph != null) {
			final List<AnalysisStandard> analysisStandards = exporter.getAnalysis().getAnalysisStandards().values()
					.stream()
					.filter(analysisStandard -> !(analysisStandard.getStandard().is(STANDARD_27001)
							|| analysisStandard.getStandard().is(STANDARD_27002)))
					.sorted(standardComparator()).collect(Collectors.toList());
			if (!analysisStandards.isEmpty()) {
				final Chart chart2700x = find27001Part(exporter);
				if (chart2700x != null) {

					final List<Object> contents = new LinkedList<>();
					final List<Object[]> charts = new LinkedList<>();
					final List<Phase> phases = exporter.getAnalysis().findUsablePhase();

					for (AnalysisStandard analysisStandard : analysisStandards) {

						contents.add(exporter.setText(exporter.setStyle(exporter.getFactory().createP(), "Heading3"),
								exporter.getMessage("report.additional.collection.title",
										new Object[] { analysisStandard.getStandard().getName() },
										analysisStandard.getStandard().getName())));

						contents.add(exporter.setText(exporter.setStyle(exporter.getFactory().createP(), "BodyOfText"),
								exporter.getMessage(
										"report.additional.collection.description",
										new Object[] { analysisStandard.getStandard().getName() },
										analysisStandard.getStandard().getName())));
						for (ActionPlanMode planMode : planModes) {

							ClonePartResult result = exporter.cloneChart(chart2700x,
									analysisStandard.getStandard().getName(),
									"Compliance"
											+ (planModes.length == 1 ? ""
													: (planMode == ActionPlanMode.APPN ? "QT" : "QL"))
											+ analysisStandard.getStandard().getName());

							contents.add(result.getP());

							contents.add(exporter.addFigureCaption(exporter.getMessage(
									"report.additional.collection.caption",
									new Object[] { analysisStandard.getStandard().getName(),
											(planModes.length == 1 ? 0 : (planMode == ActionPlanMode.APPN ? 1 : 2)) },
									analysisStandard.getStandard().getName())));
							charts.add(new Object[] { result.getPart(), planMode,
									analysisStandard.getStandard().getName(), });
						}

					}
					exporter.insertAllBefore(paragraph, contents);

					for (Object[] objects : charts)
						buildComplianceChart((Chart) objects[0], (ActionPlanMode) objects[1], (String) objects[2],
								exporter, phases);
				}
			}
		}
		return true;
	}

	private boolean buildCollectionList(Docx4jData data) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = exporter.findP(data.getSource());
		if (paragraph != null) {
			final List<Object> objects = exporter.getAnalysis().getAnalysisStandards().values().stream()
					.filter(a -> !(a.getStandard().is(STANDARD_27001) || a.getStandard().is(STANDARD_27002)))
					.sorted(standardComparator())
					.map(a -> exporter.setText(exporter.setStyle(exporter.getFactory().createP(), "ListParagraph"),
							exporter.getMessage("report.format.bullet.list.iteam",
									new Object[] { a.getStandard().getName() }, a.getStandard().getName())))
					.collect(Collectors.toList());
			objects.parallelStream().forEach(p -> ((P) p).setPPr(paragraph.getPPr()));
			exporter.insertAllAfter(paragraph, objects);
		}
		return true;
	}

	private boolean buildCompliance(Docx4jData data, String standard, ActionPlanMode planMode) throws Exception {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final Part part = exporter.findChart(data.getSource());
		if (part != null)
			buildComplianceChart(part, planMode, standard, exporter, exporter.getAnalysis().findUsablePhase());
		return true;
	}

	private void buildComplianceChart(Part part, ActionPlanMode mode, String standard, Docx4jReportImpl exporter,
			final List<Phase> phases) throws Exception {
		final String path = part.getRelationshipsPart().getRelationships().getRelationship().parallelStream()
				.filter(r -> r.getTarget().endsWith(".xlsx"))
				.map(Relationship::getTarget).findAny().orElse(null);
		if (path == null)
			return;
		final Part excel = exporter.getWordMLPackage().getParts().get(new PartName("/word" + path.replace("..", "")));
		if (excel == null)
			return;
		final Docx4jExcelSheet reportExcelSheet = new Docx4jExcelSheet((EmbeddedPackagePart) excel);
		final Chart chart = (Chart) part;
		final CTRadarChart radarChart = (CTRadarChart) chart.getContents().getChart().getPlotArea()
				.getAreaChartOrArea3DChartOrLineChart().parallelStream()
				.filter(CTRadarChart.class::isInstance).findAny().orElse(null);
		if (radarChart == null)
			return;
		radarChart.getSer().clear();

		CTRegularTextRun r = (CTRegularTextRun) chart.getContents().getChart().getTitle().getTx().getRich().getP()
				.get(0).getEGTextRun().get(0);

		if (standard.equals(STANDARD_27001) || standard.equals(STANDARD_27002))
			r.setT(exporter.getMessage("report.compliance.iso", new Object[] { standard },
					"Compliance ISO " + standard));
		else {
			r.setT(exporter.getMessage("report.compliance.custom", new Object[] { standard },
					"Compliance " + standard));
			reportExcelSheet.setName("Compliance" + standard.trim().replaceAll(":|-|[ ]|!|\\$|€", "_"));
			reportExcelSheet.getWorkbook().getContents().getSheets().getSheet().get(0)
					.setName(reportExcelSheet.getName());
		}

		chart.getContents().getChart().getPlotArea().getValAxOrCatAxOrDateAx().parallelStream()
				.filter(CTValAx.class::isInstance).map(CTValAx.class::cast)
				.forEach(valAx -> {
					valAx.getNumFmt().setSourceLinked(false);
					valAx.getNumFmt().setFormatCode("0%");
				});

		final List<Measure> measures = exporter.getAnalysis().getAnalysisStandards().values().stream()
				.filter(analysisStandard -> analysisStandard.getStandard().is(standard))
				.map(AnalysisStandard::getMeasures).findAny().orElse(Collections.emptyList());

		final SheetData sheet = reportExcelSheet.getWorkbook().getWorksheet(0).getContents().getSheetData();

		final Map<Integer, Boolean> actionPlanMeasures = exporter.getAnalysis()
				.findIdMeasuresImplementedByActionPlanType(mode);

		String phaseLabel = exporter.getMessage("label.chart.series.current_level", null, "Current Level");

		CTRadarSer ser = exporter
				.createChart(String.format("%s!$B$1", reportExcelSheet.getName()), 0, phaseLabel, new CTRadarSerProxy())
				.getProxy();

		Map<String, Object[]> compliances = ChartGenerator.ComputeComplianceBefore(measures,
				exporter.getValueFactory());

		int rowCount = 0;

		Row row = ExcelHelper.getOrCreateRow(sheet, rowCount++, compliances.size() + 1);
		setValue(row, 0, exporter.getMessage("report.compliance.chapter", null, "Chapter"));
		setValue(row, 1, phaseLabel);

		ser.getVal().getNumRef().getNumCache().setFormatCode("0%");

		for (String key : compliances.keySet()) {
			final CTNumVal numVal = new CTNumVal();
			final CTStrVal catName = new CTStrVal();
			final Object[] compliance = compliances.get(key);
			double value = (((Double) compliance[1]).doubleValue() / ((Integer) compliance[0]).doubleValue()) * 0.01;

			catName.setV(key);
			catName.setIdx(ser.getCat().getStrRef().getStrCache().getPt().size());
			ser.getCat().getStrRef().getStrCache().getPt().add(catName);

			numVal.setIdx(ser.getVal().getNumRef().getNumCache().getPt().size());
			ser.getVal().getNumRef().getNumCache().getPt().add(numVal);

			if (Double.isNaN(value))
				value = 0;
			numVal.setV(value + "");

			row = ExcelHelper.getOrCreateRow(sheet, rowCount++, compliances.size() + 1);
			setValue(row, 0, key);
			setValue(row, 1, value);
		}

		ser.getCat().getStrRef().setF(String.format("%s!$A$2:$A$%d", reportExcelSheet.getName(),
				ser.getCat().getStrRef().getStrCache().getPt().size() + 1));
		ser.getVal().getNumRef().setF(String.format("%s!$B$2:$B$%d", reportExcelSheet.getName(),
				ser.getCat().getStrRef().getStrCache().getPt().size() + 1));

		radarChart.getSer().add(ser);

		if (!actionPlanMeasures.isEmpty()) {

			int columnIndex = 2;
			for (Phase phase : phases) {
				final char col = (char) ( 'A' + columnIndex);

				phaseLabel = exporter.getMessage("label.chart.phase", new Object[] { phase.getNumber() },
						"Phase " + phase.getNumber());

				compliances = ChartGenerator.ComputeCompliance(measures, phase, actionPlanMeasures, compliances,
						exporter.getValueFactory());

				ser = exporter.createChart(ser.getCat(), String.format("%s!$%s$1", reportExcelSheet.getName(), col),
						columnIndex - 1L, phaseLabel, new CTRadarSerProxy()).getProxy();
						
				ser.getVal().getNumRef().getNumCache().setFormatCode("0%");

				setValue(sheet.getRow().get(rowCount = 0), columnIndex, phaseLabel);

				for (String key : compliances.keySet()) {
					final CTNumVal numVal = new CTNumVal();
					final Object[] compliance = compliances.get(key);
					double value = (((Double) compliance[1]).doubleValue() / ((Integer) compliance[0]).doubleValue())
							* 0.01;
					numVal.setIdx(ser.getVal().getNumRef().getNumCache().getPt().size());
					ser.getVal().getNumRef().getNumCache().getPt().add(numVal);
					if (Double.isNaN(value))
						value = 0;
					numVal.setV(value + "");
					setValue(sheet.getRow().get(++rowCount), columnIndex, value);
				}

				ser.getVal().getNumRef().setF(String.format("%s!$%s$2:$%s$%d", reportExcelSheet.getName(), col, col,
						ser.getCat().getStrRef().getStrCache().getPt().size() + 1));

				radarChart.getSer().add(ser);

				columnIndex++;
			}
		}

		reportExcelSheet.save();

	}

	private boolean buildCurrentSecurityLevel(Docx4jData data) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraphOriginal = exporter.findP(data.getSource());
		if (paragraphOriginal != null) {
			final List<Object> contents = new LinkedList<>();
			final AtomicInteger index = new AtomicInteger(0);
			final int count = exporter.getAnalysis().getAnalysisStandards().size();
			exporter.getAnalysis().getAnalysisStandards().values().stream().sorted(standardComparator())
					.forEach(analysisStandard -> {
						final double complaince = ChartGenerator.ComputeCompliance(analysisStandard,
								exporter.getValueFactory());
						final String name = analysisStandard.getStandard().is(Constant.STANDARD_27001)
								? Constant.STANDARD_27001
								: analysisStandard.getStandard().is(Constant.STANDARD_27002) ? Constant.STANDARD_27002
										: analysisStandard.getStandard().getName();
						final P paragraph = exporter.setStyle(exporter.getFactory().createP(), "BulletL1");
						if (name.equals(Constant.STANDARD_27001) || name.equals(Constant.STANDARD_27002))
							exporter.setText(paragraph, exporter.getMessage("report.current.security.level.iso",
									new Object[] { name, Math.round(complaince),
											index.incrementAndGet() == count ? 1 : 0 },
									null));
						else
							exporter.setText(paragraph,
									exporter.getMessage(
											"report.current.security.level", new Object[] { name,
													Math.round(complaince), index.incrementAndGet() == count ? 1 : 0 },
											null));
						contents.add(paragraph);
					});
			exporter.insertAllBefore(paragraphOriginal, contents);
		}
		return true;
	}

	private boolean buildMeasures(Docx4jData data) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P reference = (P) exporter.findP(data.getSource());
		if (reference != null) {

			final TextAlignment alignmentLeft = exporter.createAlignment("left");

			final Comparator<Measure> comparator = new MeasureComparator();

			final List<Object> contents = new LinkedList<>();

			exporter.setCurrentParagraphId(TS_TAB_TEXT_3);

			exporter.getAnalysis().getAnalysisStandards().values().stream().sorted(standardComparator())
					.forEach(analysisStandard -> {
						final Tbl table = exporter.createTable("TableTSMeasure",
								analysisStandard.getMeasures().size() + 1, 17);
						final Tr header = (Tr) table.getContent().get(0);
						table.getTblPr().getTblW().setType("dxa");
						table.getTblPr().getTblW().setW(BigInteger.valueOf(16157));
						Collections.sort(analysisStandard.getMeasures(), comparator);

						exporter.setCellText((Tc) header.getContent().get(0),
								exporter.getMessage("report.measure.reference", null, "Ref."));
						exporter.setCellText((Tc) header.getContent().get(1),
								exporter.getMessage("report.measure.domain", null, "Domain"));
						exporter.setCellText((Tc) header.getContent().get(2),
								exporter.getMessage("report.measure.status", null, "ST"));
						exporter.setCellText((Tc) header.getContent().get(3),
								exporter.getMessage("report.measure.implementation_rate", null, "IR(%)"));
						exporter.setCellText((Tc) header.getContent().get(4),
								exporter.getMessage("report.measure.internal.workload", null, "IS(md)"));
						exporter.setCellText((Tc) header.getContent().get(5),
								exporter.getMessage("report.measure.external.workload", null, "ES(md)"));
						exporter.setCellText((Tc) header.getContent().get(6),
								exporter.getMessage("report.measure.investment", null, "INV(k€)"));
						exporter.setCellText((Tc) header.getContent().get(7),
								exporter.getMessage("report.measure.life_time", null, "LT(y)"));
						exporter.setCellText((Tc) header.getContent().get(8),
								exporter.getMessage("report.measure.internal.maintenance", null, "IM(md)"));
						exporter.setCellText((Tc) header.getContent().get(9),
								exporter.getMessage("report.measure.external.maintenance", null, "EM(md)"));
						exporter.setCellText((Tc) header.getContent().get(10),
								exporter.getMessage("report.measure.recurrent.investment", null, "RINV(k€)"));
						exporter.setCellText((Tc) header.getContent().get(11),
								exporter.getMessage("report.measure.cost", null, "CS(k€)"));
						exporter.setCellText((Tc) header.getContent().get(12),
								exporter.getMessage("report.measure.phase", null, "P"));
						exporter.setCellText((Tc) header.getContent().get(13),
								exporter.getMessage("report.measure.importance", null, "Imp"));
						exporter.setCellText((Tc) header.getContent().get(14),
								exporter.getMessage("report.measure.responsible", null, "Resp."));
						exporter.setCellText((Tc) header.getContent().get(15),
								exporter.getMessage("report.measure.to_do", null, "To Do"));
						exporter.setCellText((Tc) header.getContent().get(16),
								exporter.getMessage("report.measure.comment", null, "Comment"));
						exporter.setRepeatHeader(header);

						int index = 1;

						for (Measure measure : analysisStandard.getMeasures()) {
							final MeasureDescriptionText description = measure.getMeasureDescription()
									.findByLanguage(exporter.getAnalysis().getLanguage());
							final Tr row = (Tr) table.getContent().get(index++);
							exporter.setCellText((Tc) row.getContent().get(0),
									measure.getMeasureDescription().getReference());
							exporter.setCellText((Tc) row.getContent().get(1),
									description == null ? "" : description.getDomain(), alignmentLeft);
							if (!measure.getMeasureDescription().isComputable()) {
								for (int i = 0; i < 16; i++)
									setColor((Tc) row.getContent().get(i), exporter.getDarkColor());
								mergeCell(row, 1, 14, exporter.getDarkColor());
							} else {
								exporter.setCellText((Tc) row.getContent().get(2),
										exporter.getMessage("label.measure.status." + measure.getStatus().toLowerCase(),
												null, measure.getStatus()));
								exporter.addCellNumber((Tc) row.getContent().get(3), exporter.getNumberFormat()
										.format(measure.getImplementationRateValue(exporter.getValueFactory())));
								exporter.addCellNumber((Tc) row.getContent().get(4),
										exporter.getKiloNumberFormat().format(measure.getInternalWL()));
								exporter.addCellNumber((Tc) row.getContent().get(5),
										exporter.getKiloNumberFormat().format(measure.getExternalWL()));
								exporter.addCellNumber((Tc) row.getContent().get(6),
										exporter.getKiloNumberFormat().format(measure.getInvestment() * 0.001));
								exporter.addCellNumber((Tc) row.getContent().get(7),
										exporter.getNumberFormat().format(measure.getLifetime()));
								exporter.addCellNumber((Tc) row.getContent().get(8),
										exporter.getKiloNumberFormat().format(measure.getInternalMaintenance()));
								exporter.addCellNumber((Tc) row.getContent().get(9),
										exporter.getKiloNumberFormat().format(measure.getExternalMaintenance()));
								exporter.addCellNumber((Tc) row.getContent().get(10),
										exporter.getNumberFormat().format(measure.getRecurrentInvestment() * 0.001));
								exporter.addCellNumber((Tc) row.getContent().get(11),
										exporter.getNumberFormat().format(measure.getCost() * 0.001));
								exporter.addCellParagraph((Tc) row.getContent().get(12),
										measure.getPhase().getNumber() + "");
								exporter.addCellParagraph((Tc) row.getContent().get(13),
										exporter.getMessage("report.measure.importance.value",
												new Object[] { measure.getImportance() }, null));
								exporter.addCellParagraph((Tc) row.getContent().get(14), measure.getResponsible());
								exporter.addCellParagraph((Tc) row.getContent().get(15), measure.getToDo());
								if (MEASURE_STATUS_NOT_APPLICABLE.equalsIgnoreCase(measure.getStatus())
										|| measure.getImplementationRateValue(exporter.getValueFactory()) >= 100) {
									for (Object object : row.getContent())
										setColor((Tc) object, "ffffff");
								} else {
									setColor((Tc) row.getContent().get(0), exporter.getDefaultColor());
									setColor((Tc) row.getContent().get(1), exporter.getDefaultColor());
									setColor((Tc) row.getContent().get(11),
											measure.getCost() == 0 ? exporter.getZeroCostColor()
													: exporter.getDefaultColor());
								}
							}
							exporter.addCellParagraph((Tc) row.getContent().get(16), measure.getComment());
						}
						// contents.add(exporter.addBreak(exporter.getFactory().createP(),
						// STBrType.PAGE));
						contents.add(
								exporter.setText(exporter.setStyle(exporter.getFactory().createP(), "TSMeasureTitle"),
										analysisStandard.getStandard().getName()));
						if (contents.add(table))
							DocxChainFactory.format(table, exporter.getDefaultTableStyle(), AnalysisType.HYBRID,
									exporter.getColors());
					});
			if (!contents.isEmpty())
				exporter.insertAllAfter(reference, contents);
		}

		return true;
	}

	private Chart find27001Part(Docx4jReportImpl exporter)
			throws InvalidFormatException, XPathBinderAssociationIsPartialException, JAXBException {
		for (String name : TS_2700X_CHART_IDS) {
			Chart chart = (Chart) exporter.findChart(name);
			if (chart != null)
				return chart;
		}
		return null;
	}

	private Comparator<? super AnalysisStandard> standardComparator() {
		return (a1, a2) -> NaturalOrderComparator.compareTo(a1.getStandard().getName(), a2.getStandard().getName());
	}

}
